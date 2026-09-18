(() => {
    const API_BASE = "user";
    const loginForm = document.querySelector("#login-form");
    const registerForm = document.querySelector("#register-form");
    const loginTab = document.querySelector("#login-tab");
    const registerTab = document.querySelector("#register-tab");
    const notice = document.querySelector("#notice");

    function showForm(name) {
        const isLogin = name === "login";
        loginForm.hidden = !isLogin;
        registerForm.hidden = isLogin;
        loginTab.classList.toggle("is-active", isLogin);
        registerTab.classList.toggle("is-active", !isLogin);
        loginTab.setAttribute("aria-selected", String(isLogin));
        registerTab.setAttribute("aria-selected", String(!isLogin));
        hideNotice();
        (isLogin ? loginForm : registerForm).querySelector("input").focus();
    }

    function showNotice(message, type) {
        notice.textContent = message;
        notice.className = `notice ${type}`;
        notice.hidden = false;
    }
    function hideNotice() { notice.hidden = true; }

    async function request(path, body) {
        const response = await fetch(`${API_BASE}/${path}`, {
            method: "POST",
            headers: { "Content-Type": "application/json", "Accept": "application/json" },
            body: JSON.stringify(body)
        });
        let data = {};
        try { data = await response.json(); } catch (_) { /* Response body is optional. */ }
        if (!response.ok) throw new Error(data.error || data.message || "Something went wrong. Please try again.");
        return data;
    }

    function setLoading(form, loading) {
        const button = form.querySelector(".submit-button");
        button.disabled = loading;
        button.dataset.label ||= button.innerHTML;
        button.innerHTML = loading ? "Please wait…" : button.dataset.label;
    }

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault(); hideNotice();
        if (!loginForm.reportValidity()) return;
        const data = Object.fromEntries(new FormData(loginForm));
        setLoading(loginForm, true);
        try {
            const result = await request("login", data);
            localStorage.setItem("flipkartAuth", JSON.stringify(result));
            if (result.role === "CUSTOMER") {
                window.location.replace("customer.html");
                return;
            }
            showNotice("Login successful. A dashboard for this role will be available soon.", "success");
        } catch (error) { showNotice(error.message, "error"); }
        finally { setLoading(loginForm, false); }
    });

    registerForm.addEventListener("submit", async (event) => {
        event.preventDefault(); hideNotice();
        if (!registerForm.reportValidity()) return;
        const data = Object.fromEntries(new FormData(registerForm));
        setLoading(registerForm, true);
        try {
            const result = await request("register", data);
            showNotice(result.message || "Account created. You can log in now.", "success");
            loginForm.querySelector("[name=phNo]").value = data.phNo;
            registerForm.reset();
            window.setTimeout(() => showForm("login"), 900);
        } catch (error) { showNotice(error.message, "error"); }
        finally { setLoading(registerForm, false); }
    });

    document.querySelectorAll("[data-show-form]").forEach((button) => button.addEventListener("click", () => showForm(button.dataset.showForm)));
    loginTab.addEventListener("click", () => showForm("login"));
    registerTab.addEventListener("click", () => showForm("register"));
    document.querySelectorAll("[data-toggle-password]").forEach((button) => button.addEventListener("click", () => {
        const input = document.getElementById(button.dataset.togglePassword);
        const visible = input.type === "text";
        input.type = visible ? "password" : "text";
        button.textContent = visible ? "Show" : "Hide";
        button.setAttribute("aria-label", visible ? "Show password" : "Hide password");
    }));
})();
