(() => {
    const auth = readAuth();
    if (!auth || auth.role !== "CUSTOMER" || !auth.token) {
        window.location.replace("index.html");
        return;
    }

    const el = {
        categories: document.querySelector("#categories"),
        listings: document.querySelector("#listings"),
        listingsSection: document.querySelector("#listings-section"),
        selectedCategory: document.querySelector("#selected-category"),
        cartItems: document.querySelector("#cart-items"),
        cartCount: document.querySelector("#cart-count"),
        cartHelp: document.querySelector("#cart-help"),
        placeOrder: document.querySelector("#place-order"),
        currentOrders: document.querySelector("#current-orders"),
        historyOrders: document.querySelector("#history-orders"),
        toast: document.querySelector("#toast")
    };
    const listingMetadata = readJson("flipkartListingMetadata", {});
    const selectedCartIds = new Set();
    const cartQuantities = new Map();
    let toastTimer;

    function readAuth() { return readJson("flipkartAuth", null); }
    function readJson(key, fallback) {
        try { return JSON.parse(localStorage.getItem(key)) || fallback; } catch (_) { return fallback; }
    }
    function escape(value) {
        const node = document.createElement("span");
        node.textContent = value ?? "";
        return node.innerHTML;
    }
    function currency(value) {
        return new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 2 }).format(value);
    }
    function loading(container) { container.innerHTML = '<div class="empty-state">Loading...</div>'; }
    function empty(container, message) { container.innerHTML = `<div class="empty-state">${escape(message)}</div>`; }
    function saveMetadata() { localStorage.setItem("flipkartListingMetadata", JSON.stringify(listingMetadata)); }
    function setButtonLoading(button, state, label) {
        button.disabled = state;
        button.textContent = state ? "Please wait..." : label;
    }

    async function api(path, options = {}) {
        const currentAuth = readAuth();
        const headers = new Headers(options.headers);
        headers.set("Accept", "application/json");
        headers.set(["Author", "ization"].join(""), ["Bearer", currentAuth.token].join(" "));
        if (options.body) headers.set("Content-Type", "application/json");
        const response = await fetch(path, { ...options, headers });
        let data = null;
        try { data = await response.json(); } catch (_) { /* Response body is optional. */ }
        if (!response.ok) {
            if (response.status === 401) {
                localStorage.removeItem("flipkartAuth");
                window.location.replace("index.html");
            }
            throw new Error(data?.error || data?.message || "Unable to complete this request.");
        }
        return data;
    }

    function toast(message, type = "success") {
        clearTimeout(toastTimer);
        el.toast.textContent = message;
        el.toast.className = `toast ${type}`;
        el.toast.hidden = false;
        toastTimer = setTimeout(() => { el.toast.hidden = true; }, 3600);
    }

    async function loadCategories() {
        loading(el.categories);
        try {
            const products = await api("user/getAllProductCategory");
            if (!products?.length) return empty(el.categories, "No product categories are available yet.");
            el.categories.innerHTML = products.map((product, index) => `
                <button class="category-card" type="button" data-product-id="${product.productId}" data-product-name="${escape(product.name)}">
                    <span class="category-icon">${String(index + 1).padStart(2, "0")}</span>
                    <h3>${escape(product.name)}</h3>
                    <p>${escape(product.description || "Browse offerings from sellers.")}</p>
                </button>`).join("");
        } catch (error) { empty(el.categories, error.message); }
    }

    async function loadListings(productId, name) {
        el.selectedCategory.textContent = name;
        el.listingsSection.hidden = false;
        loading(el.listings);
        el.listingsSection.scrollIntoView({ behavior: "smooth", block: "start" });
        try {
            const listings = await api(`user/products?productId=${encodeURIComponent(productId)}`);
            if (!listings?.length) return empty(el.listings, "No seller listings are available for this category.");
            listings.forEach((item) => { listingMetadata[item.productInDisplayId] = item; });
            saveMetadata();
            el.listings.innerHTML = listings.map((item) => `
                <article class="listing-card">
                    <h3>${escape(item.productName)}</h3>
                    <p class="vendor">Sold by ${escape(item.vendorName)}</p>
                    <p class="price">${currency(item.price)}</p>
                    <p class="stock">${item.quantity} available</p>
                    <button class="primary-button full add-cart" type="button" data-listing-id="${item.productInDisplayId}">Add to cart</button>
                </article>`).join("");
        } catch (error) { empty(el.listings, error.message); }
    }

    async function addToCart(button) {
        setButtonLoading(button, true, "Add to cart");
        try {
            await api("user/cart", { method: "POST", body: JSON.stringify({ productInDisplayId: Number(button.dataset.listingId) }) });
            toast("Added to your cart.");
            await loadCart();
        } catch (error) { toast(error.message, "error"); }
        finally { setButtonLoading(button, false, "Add to cart"); }
    }

    async function deleteCartItem(button) {
        const cartItemId = Number(button.dataset.cartItemId);
        if (!cartItemId) {
            toast("This cart item cannot be removed.", "error");
            return;
        }
        setButtonLoading(button, true, "Delete");
        try {
            await api(`user/cart?CartItemId=${encodeURIComponent(cartItemId)}`, { method: "DELETE" });
            selectedCartIds.delete(Number(button.dataset.productId));
            cartQuantities.delete(Number(button.dataset.productId));
            toast("Item removed from your cart.");
            await loadCart();
        } catch (error) {
            toast(error.message, "error");
            setButtonLoading(button, false, "Delete");
        }
    }

    async function loadCart() {
        loading(el.cartItems);
        try {
            const items = await api("user/cart");
            el.cartCount.textContent = items?.length || 0;
            const itemIds = new Set((items || []).map((item) => item.productInDisplayId));
            selectedCartIds.forEach((id) => { if (!itemIds.has(id)) selectedCartIds.delete(id); });
            cartQuantities.forEach((quantity, id) => {
                if (!itemIds.has(id)) {
                    cartQuantities.delete(id);
                    return;
                }
                const available = Number(listingMetadata[id]?.quantity);
                if (Number.isFinite(available) && available > 0) {
                    cartQuantities.set(id, Math.min(quantity, available));
                }
            });
            el.placeOrder.disabled = selectedCartIds.size === 0;
            if (!items?.length) {
                el.cartHelp.hidden = true;
                return empty(el.cartItems, "Your cart is empty. Add a product to get started.");
            }
            el.cartHelp.hidden = !items.some((item) =>
                selectedCartIds.has(item.productInDisplayId) &&
                !listingMetadata[item.productInDisplayId]?.vendorId
            );
            el.cartItems.innerHTML = items.map((item) => `
                <article class="cart-card">
                    <label>
                        <input class="cart-select" type="checkbox" data-cart-id="${item.productInDisplayId}"
                            ${selectedCartIds.has(item.productInDisplayId) ? "checked" : ""} aria-label="Select ${escape(item.name)}">
                    </label>
                    <div><h3>${escape(item.name)}</h3><p>Saved for your next order</p></div>
                    <div class="quantity-controls" ${selectedCartIds.has(item.productInDisplayId) ? "" : "hidden"}>
                        <button class="quantity-button decrease-quantity" type="button" data-cart-id="${item.productInDisplayId}" aria-label="Decrease quantity">−</button>
                        <output class="quantity-value">${cartQuantities.get(item.productInDisplayId) || 1}</output>
                        <button class="quantity-button increase-quantity" type="button" data-cart-id="${item.productInDisplayId}"
                            ${Number(listingMetadata[item.productInDisplayId]?.quantity) <= (cartQuantities.get(item.productInDisplayId) || 1) ? "disabled" : ""} aria-label="Increase quantity">+</button>
                    </div>
                    <span class="item-id">Listing #${item.productInDisplayId}</span>
                    <button class="delete-cart-button" type="button" data-cart-item-id="${item.cartItemId}"
                        data-product-id="${item.productInDisplayId}" aria-label="Remove ${escape(item.name)} from cart">Delete</button>
                </article>`).join("");
            return items;
        } catch (error) {
            el.placeOrder.disabled = true;
            empty(el.cartItems, error.message);
            return [];
        }
    }

    async function placeOrder() {
        setButtonLoading(el.placeOrder, true, "Place selected items");
        try {
            const items = await api("user/cart");
            const selectedItems = items.filter((item) => selectedCartIds.has(item.productInDisplayId));
            if (!selectedItems.length) {
                throw new Error("Select at least one cart item before placing the order.");
            }
            if (selectedItems.some((item) => !listingMetadata[item.productInDisplayId]?.vendorId)) {
                throw new Error("Please open the category for each cart item once before ordering, so its seller can be confirmed.");
            }
            const orderItems = selectedItems.map((item) => ({
                productInDisplayId: item.productInDisplayId,
                quantity: cartQuantities.get(item.productInDisplayId) || 1,
                vendorId: listingMetadata[item.productInDisplayId].vendorId
            }));
            const result = await api("customer/order", { method: "POST", body: JSON.stringify(orderItems) });
            toast(result?.message || "Order created successfully.");
            selectedItems.forEach((item) => selectedCartIds.delete(item.productInDisplayId));
            await loadCart();
            showView("current");
        } catch (error) { toast(error.message, "error"); }
        finally { setButtonLoading(el.placeOrder, false, "Place selected items"); }
    }

    function renderOrders(orders, container, message) {
        if (!orders?.length) return empty(container, message);
        container.innerHTML = orders.map((order) => `
            <article class="order-card">
                <div class="order-top"><h3>Order #${order.orderId}</h3><span class="order-date">${escape(order.purchasedAt || "")}</span></div>
                <div class="order-items">${(order.customerOrderItemViewings || []).map((item) => `
                    <div class="order-item">
                        <div><strong>${escape(item.productName)}</strong> <span>· ${escape(item.vendorName)}</span><br><span>${currency(item.price)} × ${item.quantity}</span></div>
                        <span class="status">${escape(item.status || "PROCESSING")}</span>
                    </div>`).join("")}</div>
            </article>`).join("");
    }

    async function loadOrders(type) {
        const current = type === "current";
        const container = current ? el.currentOrders : el.historyOrders;
        loading(container);
        try {
            renderOrders(await api(current ? "user/currentOrder" : "user/orderHistory"), container,
                current ? "You have no current orders." : "No delivered orders yet.");
        } catch (error) { empty(container, error.message); }
    }

    function showView(name) {
        document.querySelectorAll(".view").forEach((view) => { view.hidden = view.id !== `${name}-view`; });
        document.querySelectorAll(".nav-link").forEach((button) => { button.classList.toggle("active", button.dataset.view === name); });
        if (name === "cart") loadCart();
        if (name === "current" || name === "history") loadOrders(name);
    }

    el.categories.addEventListener("click", (event) => {
        const card = event.target.closest("[data-product-id]");
        if (card) loadListings(card.dataset.productId, card.dataset.productName);
    });
    el.listings.addEventListener("click", (event) => {
        const button = event.target.closest(".add-cart");
        if (button) addToCart(button);
    });
    el.cartItems.addEventListener("change", (event) => {
        const checkbox = event.target.closest(".cart-select");
        if (!checkbox) return;
        const cartId = Number(checkbox.dataset.cartId);
        if (checkbox.checked) {
            selectedCartIds.add(cartId);
            if (!cartQuantities.has(cartId)) cartQuantities.set(cartId, 1);
        } else {
            selectedCartIds.delete(cartId);
        }
        el.placeOrder.disabled = selectedCartIds.size === 0;
        const controls = checkbox.closest(".cart-card").querySelector(".quantity-controls");
        controls.hidden = !checkbox.checked;
    });
    el.cartItems.addEventListener("click", (event) => {
        const deleteButton = event.target.closest(".delete-cart-button");
        if (deleteButton) {
            deleteCartItem(deleteButton);
            return;
        }
        const button = event.target.closest(".quantity-button");
        if (!button) return;
        const cartId = Number(button.dataset.cartId);
        const current = cartQuantities.get(cartId) || 1;
        const available = Number(listingMetadata[cartId]?.quantity);
        const maximum = Number.isFinite(available) && available > 0 ? available : Number.MAX_SAFE_INTEGER;
        const next = button.classList.contains("increase-quantity")
            ? Math.min(current + 1, maximum)
            : Math.max(current - 1, 1);
        cartQuantities.set(cartId, next);
        const card = button.closest(".cart-card");
        card.querySelector(".quantity-value").textContent = next;
        card.querySelector(".increase-quantity").disabled = next >= maximum;
    });
    document.querySelector("#back-to-categories").addEventListener("click", () => {
        el.listingsSection.hidden = true;
        window.scrollTo({ top: 0, behavior: "smooth" });
    });
    document.querySelectorAll(".nav-link").forEach((button) => button.addEventListener("click", () => showView(button.dataset.view)));
    el.placeOrder.addEventListener("click", placeOrder);
    document.querySelector("#logout").addEventListener("click", () => {
        localStorage.removeItem("flipkartAuth");
        window.location.replace("index.html");
    });

    loadCategories();
    loadCart();
})();
