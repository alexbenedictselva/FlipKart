(() => {
    const auth = readJson("flipkartAuth", null);
    if (!auth || auth.role !== "DELIVERY_PERSON" || !auth.token) {
        window.location.replace("index.html");
        return;
    }

    const deliveries = document.querySelector("#deliveries");
    const toastElement = document.querySelector("#toast");
    let toastTimer;
    const statuses = [
        { value: "OUT_FOR_DELIVERY", label: "Out for delivery" },
        { value: "DELIVERED", label: "Delivered" },
        { value: "FAILED", label: "Failed" }
    ];

    function readJson(key, fallback) {
        try { return JSON.parse(localStorage.getItem(key)) || fallback; } catch (_) { return fallback; }
    }
    function escape(value) {
        const node = document.createElement("span");
        node.textContent = value ?? "";
        return node.innerHTML;
    }
    function loading() { deliveries.innerHTML = '<div class="empty-state">Loading deliveries...</div>'; }
    function empty(message) { deliveries.innerHTML = `<div class="empty-state">${escape(message)}</div>`; }
    function toast(message, type = "success") {
        clearTimeout(toastTimer);
        toastElement.textContent = message;
        toastElement.className = `toast ${type}`;
        toastElement.hidden = false;
        toastTimer = setTimeout(() => { toastElement.hidden = true; }, 3600);
    }
    function setLoading(button, state, label) {
        button.disabled = state;
        button.textContent = state ? "Updating..." : label;
    }
    function displayStatus(status) {
        return String(status || "ASSIGNED").replaceAll("_", " ").toLowerCase()
            .replace(/\b\w/g, (letter) => letter.toUpperCase());
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

    function render(deliveryList) {
        if (!deliveryList?.length) return empty("No active deliveries have been assigned to you.");
        deliveries.innerHTML = deliveryList.map((delivery) => {
            const current = String(delivery.status || "ASSIGNED");
            const isFinal = current === "DELIVERED" || current === "FAILED";
            return `
                <article class="delivery-card">
                    <div class="card-heading">
                        <div><p class="eyebrow">DELIVERY #${delivery.deliveryId}</p><h2>${escape(delivery.name || "Customer")}</h2><p class="muted">${escape(delivery.phNo || "No phone number")}</p></div>
                        <span class="status-badge status-${current.toLowerCase()}">${escape(displayStatus(current))}</span>
                    </div>
                    <div class="products">${(delivery.productNames || []).map((product) => `<span>${escape(product.productName)}</span>`).join("")}</div>
                    <div class="update-row">
                        <label>Update status
                            <select class="status-select" data-delivery-id="${delivery.deliveryId}" ${isFinal ? "disabled" : ""}>
                                ${statuses.map((status) => `<option value="${status.value}" ${current === status.value ? "selected" : ""}>${status.label}</option>`).join("")}
                            </select>
                        </label>
                        <button class="primary-button update-status" type="button" data-delivery-id="${delivery.deliveryId}" ${isFinal ? "disabled" : ""}>Update status</button>
                    </div>
                </article>`;
        }).join("");
    }

    async function loadDeliveries() {
        loading();
        try { render(await api("deliveryPartner")); }
        catch (error) { empty(error.message); }
    }

    async function updateStatus(button) {
        const deliveryId = Number(button.dataset.deliveryId);
        const select = document.querySelector(`.status-select[data-delivery-id="${deliveryId}"]`);
        const status = select?.value;
        if (!statuses.some((item) => item.value === status)) {
            toast("Choose a valid delivery status.", "error");
            return;
        }
        setLoading(button, true, "Update status");
        try {
            await api("deliveryPartner", {
                method: "PUT",
                body: JSON.stringify({ deliveryId, status })
            });
            toast("Delivery status updated.");
            await loadDeliveries();
        } catch (error) {
            toast(error.message, "error");
            setLoading(button, false, "Update status");
        }
    }

    deliveries.addEventListener("click", (event) => {
        const button = event.target.closest(".update-status");
        if (button) updateStatus(button);
    });
    document.querySelector("#refresh").addEventListener("click", loadDeliveries);
    document.querySelector("#logout").addEventListener("click", () => {
        localStorage.removeItem("flipkartAuth");
        window.location.replace("index.html");
    });
    loadDeliveries();
})();
