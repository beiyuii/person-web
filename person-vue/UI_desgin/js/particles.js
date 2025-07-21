// 粒子系统
class ParticleSystem {
    constructor(container) {
        this.container = container
        this.canvas = document.createElement("canvas")
        this.ctx = this.canvas.getContext("2d")
        this.particles = []
        this.mouse = { x: 0, y: 0 }

        this.init()
        this.bindEvents()
        this.animate()
    }

    init() {
        this.canvas.style.position = "absolute"
        this.canvas.style.top = "0"
        this.canvas.style.left = "0"
        this.canvas.style.pointerEvents = "none"
        this.container.appendChild(this.canvas)

        this.resize()
        this.createParticles()
    }

    resize() {
        this.canvas.width = this.container.offsetWidth
        this.canvas.height = this.container.offsetHeight
    }

    createParticles() {
        const particleCount = Math.floor((this.canvas.width * this.canvas.height) / 15000)

        for (let i = 0; i < particleCount; i++) {
            this.particles.push({
                x: Math.random() * this.canvas.width,
                y: Math.random() * this.canvas.height,
                vx: (Math.random() - 0.5) * 2,
                vy: (Math.random() - 0.5) * 2,
                size: Math.random() * 3 + 1,
                opacity: Math.random() * 0.5 + 0.2,
                color: `hsl(${220 + Math.random() * 60}, 70%, 60%)`,
            })
        }
    }

    bindEvents() {
        window.addEventListener("resize", () => this.resize())

        this.container.addEventListener("mousemove", (e) => {
            const rect = this.container.getBoundingClientRect()
            this.mouse.x = e.clientX - rect.left
            this.mouse.y = e.clientY - rect.top
        })
    }

    animate() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)

        this.particles.forEach((particle) => {
            // 更新粒子位置
            particle.x += particle.vx
            particle.y += particle.vy

            // 边界检测
            if (particle.x < 0 || particle.x > this.canvas.width) particle.vx *= -1
            if (particle.y < 0 || particle.y > this.canvas.height) particle.vy *= -1

            // 鼠标交互
            const dx = this.mouse.x - particle.x
            const dy = this.mouse.y - particle.y
            const distance = Math.sqrt(dx * dx + dy * dy)

            if (distance < 100) {
                const force = (100 - distance) / 100
                particle.vx -= dx * force * 0.01
                particle.vy -= dy * force * 0.01
            }

            // 绘制粒子
            this.ctx.save()
            this.ctx.globalAlpha = particle.opacity
            this.ctx.fillStyle = particle.color
            this.ctx.beginPath()
            this.ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2)
            this.ctx.fill()
            this.ctx.restore()
        })

        // 绘制连接线
        this.drawConnections()

        requestAnimationFrame(() => this.animate())
    }

    drawConnections() {
        this.particles.forEach((particle, i) => {
            for (let j = i + 1; j < this.particles.length; j++) {
                const other = this.particles[j]
                const dx = particle.x - other.x
                const dy = particle.y - other.y
                const distance = Math.sqrt(dx * dx + dy * dy)

                if (distance < 120) {
                    this.ctx.save()
                    this.ctx.globalAlpha = ((120 - distance) / 120) * 0.3
                    this.ctx.strokeStyle = "#667eea"
                    this.ctx.lineWidth = 1
                    this.ctx.beginPath()
                    this.ctx.moveTo(particle.x, particle.y)
                    this.ctx.lineTo(other.x, other.y)
                    this.ctx.stroke()
                    this.ctx.restore()
                }
            }
        })
    }
}
