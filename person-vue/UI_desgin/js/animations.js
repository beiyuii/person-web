// GSAP动画控制器
import gsap from "gsap"
import { ScrollTrigger } from "gsap/ScrollTrigger"

class AnimationController {
    constructor() {
        this.init()
    }

    init() {
        gsap.registerPlugin(ScrollTrigger)
        this.initHeroAnimations()
        this.initScrollAnimations()
        this.initSkillAnimations()
        this.initWorksAnimations()
        this.initAboutAnimations()
    }

    initHeroAnimations() {
        const tl = gsap.timeline()

        tl.from(".hero-title", {
            duration: 1,
            y: 100,
            opacity: 0,
            ease: "power3.out",
        })
            .from(
                ".hero-subtitle",
                {
                    duration: 1,
                    y: 50,
                    opacity: 0,
                    ease: "power3.out",
                },
                "-=0.5",
            )
            .from(
                ".hero-buttons .btn",
                {
                    duration: 0.8,
                    y: 30,
                    opacity: 0,
                    stagger: 0.2,
                    ease: "power3.out",
                },
                "-=0.3",
            )
            .from(
                ".scroll-indicator",
                {
                    duration: 0.5,
                    opacity: 0,
                    ease: "power2.out",
                },
                "-=0.2",
            )
    }

    initScrollAnimations() {
        // 导航栏滚动效果
        ScrollTrigger.create({
            start: "top -80",
            end: 99999,
            toggleClass: {
                className: "scrolled",
                targets: ".navbar",
            },
        })

        // 通用滚动显示动画
        gsap.utils.toArray(".section-title").forEach((title) => {
            gsap.from(title, {
                scrollTrigger: {
                    trigger: title,
                    start: "top 80%",
                    end: "bottom 20%",
                    toggleActions: "play none none reverse",
                },
                duration: 1,
                y: 50,
                opacity: 0,
                ease: "power3.out",
            })
        })
    }

    initSkillAnimations() {
        ScrollTrigger.create({
            trigger: ".skills-section",
            start: "top 70%",
            onEnter: () => {
                gsap.from(".skill-card", {
                    duration: 0.8,
                    y: 100,
                    opacity: 0,
                    stagger: 0.2,
                    ease: "power3.out",
                })

                // 进度条动画
                setTimeout(() => {
                    this.animateProgressBars()
                }, 500)
            },
        })
    }

    animateProgressBars() {
        document.querySelectorAll(".progress-bar").forEach((bar) => {
            const progress = bar.dataset.progress
            gsap.to(bar, {
                duration: 1.5,
                width: `${progress}%`,
                ease: "power2.out",
            })
        })
    }

    initWorksAnimations() {
        ScrollTrigger.create({
            trigger: ".works-section",
            start: "top 70%",
            onEnter: () => {
                gsap.from(".work-item", {
                    duration: 1,
                    scale: 0.8,
                    opacity: 0,
                    stagger: 0.3,
                    ease: "back.out(1.7)",
                })
            },
        })
    }

    initAboutAnimations() {
        ScrollTrigger.create({
            trigger: ".about-section",
            start: "top 70%",
            onEnter: () => {
                gsap.from(".about-text", {
                    duration: 1,
                    x: -100,
                    opacity: 0,
                    ease: "power3.out",
                })

                gsap.from(".avatar-container", {
                    duration: 1.5,
                    scale: 0.5,
                    rotation: 360,
                    opacity: 0,
                    ease: "elastic.out(1, 0.5)",
                })

                // 统计数字动画
                setTimeout(() => {
                    this.animateStats()
                }, 800)

                // 打字机效果
                setTimeout(() => {
                    this.typeAboutText()
                }, 1000)
            },
        })
    }

    animateStats() {
        document.querySelectorAll(".stat-number").forEach((stat) => {
            const target = Number.parseInt(stat.dataset.target)
            const isPercentage = stat.textContent.includes("%")

            gsap.to(
                { value: 0 },
                {
                    duration: 2,
                    value: target,
                    ease: "power2.out",
                    onUpdate: function () {
                        const current = Math.round(this.targets()[0].value)
                        stat.textContent = isPercentage ? `${current}%` : `${current}+`
                    },
                },
            )
        })
    }

    typeAboutText() {
        const text =
            "我是一名充满激情的全栈开发者，专注于创造优雅而强大的数字体验。从前端的精美界面到后端的稳定架构，我致力于将创意转化为现实，用代码构建更美好的数字世界。"
        const element = document.getElementById("about-typing-text")
        let index = 0

        const typeInterval = setInterval(() => {
            if (index < text.length) {
                element.textContent += text.charAt(index)
                index++
            } else {
                clearInterval(typeInterval)
            }
        }, 50)
    }
}
