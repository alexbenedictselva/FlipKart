(() => {
    const auth = readAuth();
    if (!auth || auth.role !== "CUSTOMER" || !auth.token) {
        window.location.replace("index.html");
        return;
    }

    const elements = {
        categories: document.querySelector("#categories"), listings: document.querySelector("#listings"),
        listingsSection: document.querySelector("#listings-section"), selectedCategory: document.querySelector("#selected-category"),
        cartItems: document.querySelector("#cart-items"), cartCount: document.querySelector("#cart-count"),
        cartHelp: document.querySelector("#cart-help"), placeOrder: document.querySelector("#place-order"),
        currentOrders: document.querySelector("#current-orders"), historyOrders: document.querySelector("#history-orders"), toast: document.querySelector("#toast")
    };
    const listingMetadata = readJson("flipkartListingMetadata", {});
    let toastTimer;

    function readAuth() { return readJson("flipkartAuth", null); }
    function readJson(key, fallback) { try { return JSON.parse(localStorage.getItem(key)) || fallback; } catch (_) { return fallback; } }
    function saveMetadata() { localStorage.setItem("flipkartListingMetadata", JSON.stringify(listingMetadata)); }
    function escape(value) { const node = document.createElement("span"); node.textContent = value ?? ""; return node.innerHTML; }
    function currency(value) { return new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 2 }).format(value); }

    async function api(path, options = {}) {
        const response = await fetch(path, {
            ...options,
            headers: { "Accept": "application/json", "Authorization": `Bearer ${auth.token}`, ...(options.body ? { "Content-Type": "application/json" } : {}), ...options.headers }
        });
        let data = null;
        try { data = await response.json(); } catch (_) { /* No response body. */ }
        if (!response.ok) {
            if (response.status === 401) { localStorage.removeItem("flipkartAuth"); window.location.replace("index.html"); }
            throw new Error(data?.error || data?.message || "Unable to complete this request.");
        }
        return data;
    }

    function toast(message, type = "success") {
        clearTimeout(toastTimer); elements.toast.textContent = message; elements.toast.className = `toast ${type}`; elements.toast.hidden = false;
        toastTimer = setTimeout(() => { elements.toast.hidden = true; }, 3600);
    }
    function loading(container) { container.innerHTML = '<div class="empty-state">Loading…</div>'; }
    function empty(container, message) { container.innerHTML = `<div class="empty-state">${escape(message)}</div>`; }
    function setButtonLoading(button, loadingState, label) { button.disabled = loadingState; button.textContent = loadingState ? "Please wait…" : label; }

    async function loadCategories() {
        loading(elements.categories);
        try {
            const products = await api("user/getAllProductCategory");
            if (!products?.length) return empty(elements.categories, "No product categories are available yet.");
            elements.categories.innerHTML = products.map((product, index) => `
                <button class="category-card" type="button" data-product-id="${product.productId}" data-product-name="${escape(product.name)}">
                    <span class="category-icon">${String(index + 1).padStart(2, "0")}</span><h3>${escape(product.name)}</h3><p>${escape(product.description || "Browse offerings from sellers.")}</p>
                </button>`).join("");
        } catch (error) { empty(elements.categories, error.message); }
    }

    async function loadListings(productId, name) {
        elements.selectedCategory.textContent = name;
        elements.listingsSection.hidden = false;
        loading(elements.listings);
        elements.listingsSection.scrollIntoView({ behavior: "smooth", block: "start" });
        try {
            const listings = await api(`user/products?productId=${encodeURIComponent(productId)}`);
            if (!listings?.length) return empty(elements.listings, "No seller listings are available for this category.");
            listings.forEach((item) => { listingMetadata[item.productInDisplayId] = item; }); saveMetadata();
            elements.listings.innerHTML = listings.map((item) => `
                <article class="listing-card"><h3>${escape(item.productName)}</h3><p class="vendor">Sold by ${escape(item.vendorName)}</p>
                    <p class="price">${currency(item.price)}</p><p class="stock">${item.quantity} available</p>
                    <button class="primary-button full add-cart" type="button" data-listing-id="${item.productInDisplayId}">Add to cart</button></article>`).join("");
        } catch (error) { empty(elements.listings, error.message); }
    }

    async function addToCart(button) {
        const listingId = Number(button.dataset.listingId); setButtonLoading(button, true, "Add to cart");
        try { await api("user/cart", { method: "POST", body: JSON.stringify({ productInDisplayId: listingId }) }); toast("Added to your cart."); await loadCart(); }
        catch (error) { toast(error.message, "error"); }
        finally { setButtonLoading(button, false, "Add to cart"); }
    }

    async function loadCart() {
        loading(elements.cartItems);
        try {
            const items = await api("user/cart");
            elements.cartCount.textContent = items?.length || 0;
            elements.placeOrder.disabled = !items?.length;
            if (!items?.length) { elements.cartHelp.hidden = true; return empty(elements.cartItems, "Your cart is empty. Add a product to get started."); }
            const unknownListing = items.some((item) => !listingMetadata[item.productInDisplayId]?.vendorId);
            elements.cartHelp.hidden = !unknownListing;
            elements.cartItems.innerHTML = items.map((item) => `<article class="cart-card"><div><h3>${escape(item.name)}</h3><p>Saved for your next order</p></div><span class="item-id">Listing #${item.productInDisplayId}</span></article>`).join("");
            return items;
        } catch (error) { elements.placeOrder.disabled = true; empty(elements.cartItems, error.message); return []; }
    }

    async function placeOrder() {
        setButtonLoading(elements.placeOrder, true, "Place order");
        try {
            const items = await api("user/cart");
            const missing = items.filter((item) => !listingMetadata[item.productInDisplayId]?.vendorId);
            if (missing.length) throw new Error("Please open the category for each cart item once before ordering, so its seller can be confirmed.");
            const orderItems = items.map((item) => ({ productInDisplayId: item.productInDisplayId, quantity: 1, vendorId: listingMetadata[item.productInDisplayId].vendorId }));
            const result = await api("customer/order", { method: "POST", body: JSON.stringify(orderItems) });
            toast(result?.message || "Order created successfully."); await loadCart(); showView("current");
        } catch (error) { toast(error.message, "error"); }
        finally { setButtonLoading(elements.placeOrder, false, "Place order"); }
    }

    function orderCards(orders, container, emptyMessage) {
        if (!orders?.length) return empty(container, emptyMessage);
        container.innerHTML = orders.map((order) => `<article class="order-card"><div class="order-top"><h3>Order #${order.orderId}</h3><span class="order-date">${escape(order.purchasedAt || "")}</span></div><div class="order-items">${(order.customerOrderItemViewings || []).map((item) => `<div class="order-item"><div><strong>${escape(item.productName)}</strong> <span>· ${escape(item.vendorName)}</span><br><span>${currency(item.price)} × ${item.quantity}</span></div><span class="status">${escape(item.status || "PROCESSING")}</span></div>`).join("")}</div></article>`).join("");
    }
    async function loadOrders(type) {
        const current = type === "current"; const container = current ? elements.currentOrders : elements.historyOrders;
        loading(container);
        try { orderCards(await api(current ? "user/currentOrder" : "user/orderHistory"), container, current ? "You have no current orders." : "No delivered orders yet."); }
        catch (error) { empty(container, error.message); }
    }

    function showView(name) {
        document.querySelectorAll(".view").forEach((view) => { view.hidden = view.id !== `${name}-view`; });
        document.querySelectorAll(".nav-link").forEach((button) => button.classList.toggle("active", button.dataset.view === name));
        if (name === "cart") loadCart(); if (name === "current" || name === "history") loadOrders(name);
    }

    document.querySelector("#categories").addEventListener("click", (event) => { const card = event.target.closest("[data-product-id]"); if (card) loadListings(card.dataset.productId, card.dataset.productName); });
    document.querySelector("#listings").addEventListener("click", (event) => { const button = event.target.closest(".add-cart"); if (button) addToCart(button); });
    document.querySelector("#back-to-categories").addEventListener("click", () => { elements.listingsSection.hidden = true; window.scrollTo({ top: 0, behavior: "smooth" }); });
    document.querySelectorAll(".nav-link").forEach((button) => button.addEventListener("click", () => showView(button.dataset.view)));
    elements.placeOrder.addEventListener("click", placeOrder);
    document.querySelector("#logout").addEventListener("click", () => { localStorage.removeItem("flipkartAuth"); window.location.replace("index.html"); });
    loadCategories(); loadCart();
})();
