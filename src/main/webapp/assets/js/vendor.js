(() => {
    const auth = readJson("flipkartAuth", null);
    if (!auth || auth.role !== "VENDOR" || !auth.token) {
        window.location.replace("index.html");
        return;
    }

    const products = document.querySelector("#products");
    const orders = document.querySelector("#orders");
    const toastElement = document.querySelector("#toast");
    const newProductForm = document.querySelector("#new-product-form");
    const productCategory = document.querySelector("#product-category");
    let toastTimer;

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
    function toast(message, type = "success") {
        clearTimeout(toastTimer);
        toastElement.textContent = message;
        toastElement.className = `toast ${type}`;
        toastElement.hidden = false;
        toastTimer = setTimeout(() => { toastElement.hidden = true; }, 3600);
    }
    function setLoading(button, loadingState, label) {
        button.disabled = loadingState;
        button.textContent = loadingState ? "Please wait..." : label;
    }

    async function api(path, options = {}) {
        const currentAuth = readJson("flipkartAuth", null);
        const headers = new Headers(options.headers);
        headers.set("Accept", "application/json");
        headers.set("Authorization", `Bearer ${currentAuth.token}`);
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

    function renderProducts(items) {
        if (!items?.length) return empty(products, "You have not posted any products yet.");
        products.innerHTML = items.map((item) => `
            <article class="product-card" data-product-id="${item.productInDisplayId}">
                <div class="card-heading">
                    <div><p class="eyebrow">LISTING #${item.productInDisplayId}</p><h2>${escape(item.productName)}</h2><p class="vendor-name">Sold by ${escape(item.vendorName)}</p></div>
                    <button class="danger-button delete-product" type="button" data-product-id="${item.productInDisplayId}">Delete</button>
                </div>
                <div class="edit-grid">
                    <label>Price<input class="edit-price" type="number" min="0.01" step="0.01" value="${item.price}"></label>
                    <label>Quantity<input class="edit-quantity" type="number" min="1" value="${item.quantity}"></label>
                    <button class="primary-button update-product" type="button" data-product-id="${item.productInDisplayId}">Save changes</button>
                </div>
            </article>`).join("");
    }

    function renderOrders(items) {
        if (!items?.length) return empty(orders, "No orders include your products yet.");
        orders.innerHTML = items.map((item) => `
            <article class="order-card">
                <div class="order-top"><h2>${escape(item.productName)}</h2><span>${escape(item.orderAt || "")}</span></div>
                <div class="order-details"><span>Customer: <strong>${escape(item.CustomerName || item.customerName)}</strong></span><span>Quantity: <strong>${item.quantity}</strong></span><span>Total: <strong>${currency(Number(item.price) * Number(item.quantity))}</strong></span></div>
            </article>`).join("");
    }

    async function loadProducts() {
        loading(products);
        try { renderProducts(await api("vendor/products/")); }
        catch (error) { empty(products, error.message); }
    }

    async function loadProductCategories() {
        try {
            const categories = await api("vendor/products/getAllProductCategories");
            if (!categories?.length) {
                productCategory.innerHTML = '<option value="">No products available</option>';
                productCategory.disabled = true;
                return;
            }
            productCategory.innerHTML = '<option value="">Select a product</option>' +
                categories.map((product) =>
                    `<option value="${product.productId}">${escape(product.name)} (#${product.productId})</option>`
                ).join("");
            productCategory.disabled = false;
        } catch (error) {
            productCategory.innerHTML = `<option value="">${escape(error.message)}</option>`;
            productCategory.disabled = true;
        }
    }

    async function loadOrders() {
        loading(orders);
        try { renderOrders(await api("vendor/products/GetAllVendorOrders")); }
        catch (error) { empty(orders, error.message); }
    }

    async function postProduct(event) {
        event.preventDefault();
        if (!newProductForm.reportValidity()) return;
        const button = newProductForm.querySelector("button");
        setLoading(button, true, "Post product");
        try {
            const data = Object.fromEntries(new FormData(newProductForm));
            await api("vendor/products", {
                method: "POST",
                body: JSON.stringify({
                    productId: Number(data.productId),
                    price: Number(data.price),
                    quantity: Number(data.quantity)
                })
            });
            newProductForm.reset();
            toast("Product posted successfully.");
            await loadProducts();
        } catch (error) { toast(error.message, "error"); }
        finally { setLoading(button, false, "Post product"); }
    }

    async function updateProduct(button) {
        const card = button.closest(".product-card");
        const price = Number(card.querySelector(".edit-price").value);
        const quantity = Number(card.querySelector(".edit-quantity").value);
        if (!Number.isFinite(price) || price <= 0 || !Number.isInteger(quantity) || quantity <= 0) {
            toast("Enter a valid positive price and quantity.", "error");
            return;
        }
        setLoading(button, true, "Save changes");
        try {
            await api(`vendor/products?productInDisplayId=${encodeURIComponent(button.dataset.productId)}`, {
                method: "PATCH", body: JSON.stringify({ price, quantity })
            });
            toast("Product updated successfully.");
            await loadProducts();
        } catch (error) { toast(error.message, "error"); }
        finally { setLoading(button, false, "Save changes"); }
    }

    async function deleteProduct(button) {
        setLoading(button, true, "Delete");
        try {
            await api(`vendor/products?productInDisplayId=${encodeURIComponent(button.dataset.productId)}`, { method: "DELETE" });
            toast("Product deleted successfully.");
            await loadProducts();
        } catch (error) { toast(error.message, "error"); }
        finally { setLoading(button, false, "Delete"); }
    }

    function showView(name) {
        document.querySelectorAll(".view").forEach((view) => { view.hidden = view.id !== `${name}-view`; });
        document.querySelectorAll(".nav-link").forEach((button) => { button.classList.toggle("active", button.dataset.view === name); });
        if (name === "orders") loadOrders();
    }

    newProductForm.addEventListener("submit", postProduct);
    products.addEventListener("click", (event) => {
        const updateButton = event.target.closest(".update-product");
        const deleteButton = event.target.closest(".delete-product");
        if (updateButton) updateProduct(updateButton);
        if (deleteButton) deleteProduct(deleteButton);
    });
    document.querySelectorAll(".nav-link").forEach((button) => button.addEventListener("click", () => showView(button.dataset.view)));
    document.querySelector("#logout").addEventListener("click", () => {
        localStorage.removeItem("flipkartAuth");
        window.location.replace("index.html");
    });

    loadProducts();
    loadProductCategories();
})();
