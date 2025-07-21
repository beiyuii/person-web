// 主要JavaScript功能
import { ParticleSystem } from "./ParticleSystem"
import { AnimationController } from "./AnimationController"
import gsap from "gsap"

class BlogWebsite {
    constructor() {
        this.isLogin = true
        this.particleSystem = null
        this.animationController = null

        this.init()
    }

    init() {
        this.initParticles()
        this.initAnimations()
        this.initNavigation()
        this.initTypewriter()
        this.bindEvents()
    }

    initParticles() {
        const particlesContainer = document.getElementById("particles-container")
        if (particlesContainer) {
            this.particleSystem = new ParticleSystem(particlesContainer)
        }
    }

    initAnimations() {
        this.animationController = new AnimationController()
    }

    initNavigation() {
        const navToggle = document.getElementById("nav-toggle")
        const navMenu = document.getElementById("nav-menu")

        if (navToggle && navMenu) {
            navToggle.addEventListener("click", () => {
                navToggle.classList.toggle("active")
                navMenu.classList.toggle("active")
            })
        }

        // 平滑滚动
        document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
            anchor.addEventListener("click", (e) => {
                e.preventDefault()
                const target = document.querySelector(anchor.getAttribute("href"))
                if (target) {
                    target.scrollIntoView({
                        behavior: "smooth",
                        block: "start",
                    })
                }
            })
        })
    }

    initTypewriter() {
        const text = "创造数字世界的无限可能"
        const element = document.getElementById("typing-text")
        let index = 0

        const typeInterval = setInterval(() => {
            if (index < text.length) {
                element.textContent += text.charAt(index)
                index++
            } else {
                clearInterval(typeInterval)
            }
        }, 100)
    }

    bindEvents() {
        // 技能卡片悬浮效果
        document.querySelectorAll(".skill-card").forEach((card) => {
            card.addEventListener("mouseenter", () => {
                gsap.to(card, {
                    duration: 0.3,
                    scale: 1.05,
                    rotationY: 10,
                    ease: "power2.out",
                })
            })

            card.addEventListener("mouseleave", () => {
                gsap.to(card, {
                    duration: 0.3,
                    scale: 1,
                    rotationY: 0,
                    ease: "power2.out",
                })
            })
        })

        // 作品项目悬浮效果
        document.querySelectorAll(".work-item").forEach((item) => {
            item.addEventListener("mouseenter", () => {
                gsap.to(item, {
                    duration: 0.3,
                    y: -10,
                    ease: "power2.out",
                })
            })

            item.addEventListener("mouseleave", () => {
                gsap.to(item, {
                    duration: 0.3,
                    y: 0,
                    ease: "power2.out",
                })
            })
        })
    }
}

// 全局函数
function scrollToSection(sectionId) {
    const section = document.getElementById(sectionId)
    if (section) {
        section.scrollIntoView({
            behavior: "smooth",
            block: "start",
        })
    }
}

function openWorkDetail(workId) {
    const modal = document.getElementById("work-modal")
    const modalBody = document.getElementById("modal-body")

    // 模拟作品数据
    const works = {
        1: {
            title: "智能博客系统",
            description: "基于现代化技术栈的博客平台，支持文章管理、用户认证、评论系统等功能。",
            image: "/placeholder.svg?height=400&width=800",
            technologies: ["HTML5", "CSS3", "JavaScript", "Spring Boot", "MySQL"],
            features: ["响应式设计", "用户认证", "文章管理", "评论系统", "SEO优化"],
            github: "https://github.com/example/blog-system",
            demo: "https://demo.example.com",
        },
        2: {
            title: "3D作品展示",
            description: "交互式3D模型展示平台，支持模型上传、在线预览、材质编辑等功能。",
            image: "/placeholder.svg?height=400&width=800",
            technologies: ["Three.js", "WebGL", "Blender", "Node.js"],
            features: ["3D模型加载", "实时渲染", "材质编辑", "动画控制", "场景管理"],
            github: "https://github.com/example/3d-viewer",
            demo: "https://3d-demo.example.com",
        },
        3: {
            title: "移动端应用",
            description: "跨平台移动应用，提供原生体验的用户界面和流畅的交互效果。",
            image: "/placeholder.svg?height=400&width=800",
            technologies: ["React Native", "Flutter", "Firebase"],
            features: ["跨平台兼容", "原生性能", "离线支持", "推送通知", "数据同步"],
            github: "https://github.com/example/mobile-app",
            demo: "https://app.example.com",
        },
        4: {
            title: "数据可视化",
            description: "企业级数据分析平台，提供丰富的图表类型和交互式数据探索功能。",
            image: "/placeholder.svg?height=400&width=800",
            technologies: ["D3.js", "Chart.js", "Python", "Django"],
            features: ["多种图表", "实时数据", "交互式探索", "数据导出", "权限管理"],
            github: "https://github.com/example/data-viz",
            demo: "https://viz-demo.example.com",
        },
    }

    const work = works[workId]
    if (work) {
        modalBody.innerHTML = `
            <div class="work-detail">
                <img src="${work.image}" alt="${work.title}" class="work-detail-image">
                <div class="work-detail-content">
                    <h2>${work.title}</h2>
                    <p class="work-description">${work.description}</p>
                    
                    <div class="work-technologies">
                        <h3>技术栈</h3>
                        <div class="tech-tags">
                            ${work.technologies.map((tech) => `<span class="tech-tag">${tech}</span>`).join("")}
                        </div>
                    </div>
                    
                    <div class="work-features">
                        <h3>主要功能</h3>
                        <ul>
                            ${work.features.map((feature) => `<li>${feature}</li>`).join("")}
                        </ul>
                    </div>
                    
                    <div class="work-links">
                        <a href="${work.github}" target="_blank" class="work-link github-link">
                            <i class="fab fa-github"></i>
                            查看源码
                        </a>
                        <a href="${work.demo}" target="_blank" class="work-link demo-link">
                            <i class="fas fa-external-link-alt"></i>
                            在线演示
                        </a>
                    </div>
                </div>
            </div>
        `

        modal.style.display = "block"
        gsap.from(".modal-content", {
            duration: 0.3,
            scale: 0.8,
            opacity: 0,
            ease: "power2.out",
        })
    }
}

function closeWorkModal() {
    const modal = document.getElementById("work-modal")
    gsap.to(".modal-content", {
        duration: 0.2,
        scale: 0.8,
        opacity: 0,
        ease: "power2.in",
        onComplete: () => {
            modal.style.display = "none"
        },
    })
}

// 点击模态框外部关闭
window.addEventListener("click", (e) => {
    const modal = document.getElementById("work-modal")
    if (e.target === modal) {
        closeWorkModal()
    }
})

// 初始化网站
document.addEventListener("DOMContentLoaded", () => {
    new BlogWebsite()
})
