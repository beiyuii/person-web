// 认证页面JavaScript
import gsap from "gsap" // Import gsap

class AuthManager {
    constructor() {
        this.isLogin = true
        this.apiBase = "http://localhost:8080/api" // 后端API地址

        this.init()
    }

    init() {
        this.bindEvents()
        this.initAnimations()
    }

    bindEvents() {
        const form = document.getElementById("auth-form")
        if (form) {
            form.addEventListener("submit", (e) => this.handleSubmit(e))
        }

        // 输入框焦点效果
        document.querySelectorAll(".form-input").forEach((input) => {
            input.addEventListener("focus", () => {
                input.parentElement.classList.add("focused")
            })

            input.addEventListener("blur", () => {
                if (!input.value) {
                    input.parentElement.classList.remove("focused")
                }
            })

            // 实时验证
            input.addEventListener("input", () => {
                this.validateField(input)
            })
        })
    }

    initAnimations() {
        gsap.from(".auth-container", {
            duration: 1,
            y: 50,
            opacity: 0,
            ease: "power3.out",
        })

        gsap.from(".circle", {
            duration: 2,
            scale: 0,
            opacity: 0,
            stagger: 0.3,
            ease: "elastic.out(1, 0.5)",
        })
    }

    async handleSubmit(e) {
        e.preventDefault()

        if (!this.validateForm()) {
            return
        }

        const submitBtn = document.getElementById("submit-btn")
        const btnText = document.getElementById("btn-text")
        const btnLoading = document.getElementById("btn-loading")

        // 显示加载状态
        submitBtn.classList.add("loading")
        submitBtn.disabled = true

        try {
            const formData = this.getFormData()
            const endpoint = this.isLogin ? "/auth/login" : "/auth/register"

            const response = await fetch(`${this.apiBase}${endpoint}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(formData),
            })

            const result = await response.json()

            if (result.success) {
                this.showMessage("success", this.isLogin ? "登录成功！" : "注册成功！")

                if (this.isLogin) {
                    // 保存token和用户信息
                    localStorage.setItem("token", result.data.token)
                    localStorage.setItem("userInfo", JSON.stringify(result.data.userInfo))

                    // 延迟跳转
                    setTimeout(() => {
                        window.location.href = "index.html"
                    }, 1500)
                } else {
                    // 注册成功后切换到登录模式
                    setTimeout(() => {
                        this.toggleAuthMode()
                        this.showMessage("info", "请使用新账户登录")
                    }, 1500)
                }
            } else {
                this.showMessage("error", result.message || "操作失败")
            }
        } catch (error) {
            console.error("Auth error:", error)
            this.showMessage("error", "网络错误，请稍后重试")
        } finally {
            // 恢复按钮状态
            submitBtn.classList.remove("loading")
            submitBtn.disabled = false
        }
    }

    getFormData() {
        const username = document.getElementById("username").value
        const password = document.getElementById("password").value

        if (this.isLogin) {
            return {
                username,
                password,
                remember: document.getElementById("remember-me").checked,
            }
        } else {
            return {
                username,
                email: document.getElementById("email").value,
                password,
            }
        }
    }

    validateForm() {
        let isValid = true
        const inputs = document.querySelectorAll(".form-input")

        inputs.forEach((input) => {
            if (!this.validateField(input)) {
                isValid = false
            }
        })

        // 注册时验证密码确认
        if (!this.isLogin) {
            const password = document.getElementById("password").value
            const confirmPassword = document.getElementById("confirm-password").value

            if (password !== confirmPassword) {
                this.showFieldError("confirm-password", "两次输入密码不一致")
                isValid = false
            }
        }

        return isValid
    }

    validateField(input) {
        const value = input.value.trim()
        const fieldName = input.name
        let isValid = true

        // 清除之前的错误
        this.clearFieldError(fieldName)

        switch (fieldName) {
            case "username":
                if (!value) {
                    this.showFieldError(fieldName, "请输入用户名")
                    isValid = false
                } else if (value.length < 3) {
                    this.showFieldError(fieldName, "用户名至少3个字符")
                    isValid = false
                }
                break

            case "email":
                if (!this.isLogin && !value) {
                    this.showFieldError(fieldName, "请输入邮箱地址")
                    isValid = false
                } else if (!this.isLogin && !this.isValidEmail(value)) {
                    this.showFieldError(fieldName, "请输入正确的邮箱地址")
                    isValid = false
                }
                break

            case "password":
                if (!value) {
                    this.showFieldError(fieldName, "请输入密码")
                    isValid = false
                } else if (value.length < 6) {
                    this.showFieldError(fieldName, "密码至少6个字符")
                    isValid = false
                }
                break
        }

        return isValid
    }

    showFieldError(fieldName, message) {
        const errorElement = document.getElementById(`${fieldName}-error`)
        if (errorElement) {
            errorElement.textContent = message
            errorElement.classList.add("show")
        }
    }

    clearFieldError(fieldName) {
        const errorElement = document.getElementById(`${fieldName}-error`)
        if (errorElement) {
            errorElement.textContent = ""
            errorElement.classList.remove("show")
        }
    }

    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        return emailRegex.test(email)
    }

    showMessage(type, message) {
        const container = document.getElementById("message-container")
        const messageEl = document.createElement("div")
        messageEl.className = `message ${type}`

        const icon =
            type === "success" ? "fas fa-check-circle" : type === "error" ? "fas fa-exclamation-circle" : "fas fa-info-circle"

        messageEl.innerHTML = `
            <i class="${icon}"></i>
            <span>${message}</span>
        `

        container.appendChild(messageEl)

        // 自动移除消息
        setTimeout(() => {
            messageEl.style.animation = "slideOutRight 0.3s ease forwards"
            setTimeout(() => {
                if (messageEl.parentNode) {
                    messageEl.parentNode.removeChild(messageEl)
                }
            }, 300)
        }, 3000)
    }
}

// 全局函数
function toggleAuthMode() {
    const authManager = window.authManager
    authManager.isLogin = !authManager.isLogin

    const formTitle = document.getElementById("form-title")
    const formSubtitle = document.getElementById("form-subtitle")
    const btnText = document.getElementById("btn-text")
    const footerText = document.getElementById("footer-text")
    const switchBtn = document.getElementById("switch-btn")
    const emailGroup = document.getElementById("email-group")
    const confirmPasswordGroup = document.getElementById("confirm-password-group")
    const loginOptions = document.getElementById("login-options")

    // 清空表单
    document.querySelectorAll(".form-input").forEach((input) => {
        if (input.name !== "remember-me") {
            input.value = ""
        }
    })

    // 清除错误信息
    document.querySelectorAll(".error-message").forEach((error) => {
        error.classList.remove("show")
    })

    if (authManager.isLogin) {
        // 切换到登录模式
        formTitle.textContent = "欢迎回来"
        formSubtitle.textContent = "登录您的账户继续精彩旅程"
        btnText.textContent = "登录"
        footerText.textContent = "还没有账户？"
        switchBtn.textContent = "立即注册"
        emailGroup.style.display = "none"
        confirmPasswordGroup.style.display = "none"
        loginOptions.style.display = "flex"
        document.getElementById("username").placeholder = "用户名或邮箱"
        document.getElementById("password").placeholder = "密码"
    } else {
        // 切换到注册模式
        formTitle.textContent = "加入我们"
        formSubtitle.textContent = "创建账户开始您的创作之旅"
        btnText.textContent = "注册"
        footerText.textContent = "已有账户？"
        switchBtn.textContent = "立即登录"
        emailGroup.style.display = "block"
        confirmPasswordGroup.style.display = "block"
        loginOptions.style.display = "none"
        document.getElementById("username").placeholder = "用户名"
        document.getElementById("password").placeholder = "设置密码"
    }

    // 动画效果
    gsap.fromTo(
        ".auth-container",
        { x: authManager.isLogin ? 50 : -50, opacity: 0.8 },
        { x: 0, opacity: 1, duration: 0.5, ease: "power2.out" },
    )
}

function togglePassword(fieldId) {
    const input = document.getElementById(fieldId)
    const icon = document.getElementById(`${fieldId}-eye`)

    if (input.type === "password") {
        input.type = "text"
        icon.classList.remove("fa-eye")
        icon.classList.add("fa-eye-slash")
    } else {
        input.type = "password"
        icon.classList.remove("fa-eye-slash")
        icon.classList.add("fa-eye")
    }
}

function handleForgotPassword() {
    window.authManager.showMessage("info", "忘记密码功能开发中...")
}

function handleSocialLogin(provider) {
    window.authManager.showMessage("info", `${provider} 登录功能开发中...`)
}

// 初始化认证管理器
document.addEventListener("DOMContentLoaded", () => {
    window.authManager = new AuthManager()
})
