<template>
  <div class="blog-list-page">
    <!-- 搜索区域 -->
    <section class="search-section">
      <div class="container">
        <div class="search-header">
          <h1 class="section-title">探索精彩内容</h1>
          <p class="section-subtitle">发现有趣的文章和思考</p>
        </div>
        
        <!-- 搜索框 -->
        <div class="search-box" ref="searchBoxRef">
          <el-input
            v-model="searchParams.keyword"
            size="large"
            placeholder="搜索文章标题、内容..."
            prefix-icon="Search"
            clearable
            @input="debounceSearch"
            @clear="handleSearch"
            class="search-input"
          />
          <AppleButton 
            type="primary" 
            size="large" 
            @click="handleSearch"
            class="search-btn"
          >
            搜索
          </AppleButton>
        </div>
      </div>
    </section>

    <!-- 筛选区域 -->
    <section class="filter-section">
    <div class="container">
        <div class="filter-container" ref="filterRef">
          <!-- 分类筛选 -->
          <div class="filter-group">
            <h3 class="filter-title">分类</h3>
            <div class="category-tags">
              <div 
                v-for="category in categories" 
                :key="category.id"
                :class="['category-tag', { active: selectedCategory === category.id }]"
                @click="selectCategory(category.id)"
              >
                <span class="category-name">{{ category.name }}</span>
                <span class="category-count">{{ category.articleCount || 0 }}</span>
              </div>
              <div 
                :class="['category-tag', { active: selectedCategory === null }]"
                @click="selectCategory(null)"
              >
                <span class="category-name">全部</span>
                <span class="category-count">{{ totalArticles }}</span>
              </div>
            </div>
          </div>

          <!-- 标签筛选 -->
          <div class="filter-group">
            <h3 class="filter-title">热门标签</h3>
            <div class="tag-cloud">
              <div
                v-for="tag in popularTags"
                :key="tag.id"
                :class="['tag-item', { active: selectedTags.includes(tag.id) }]"
                :style="{ fontSize: `${Math.min(1.2, 0.8 + tag.weight * 0.01)}rem` }"
                @click="toggleTag(tag.id)"
              >
                {{ tag.name }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 文章列表区域 -->
    <section class="articles-section">
      <div class="container">
        <!-- 结果统计 -->
        <div class="results-info" v-if="!loading">
          <span class="results-count">
            找到 <strong>{{ pagination.total }}</strong> 篇文章
          </span>
          <div class="sort-options">
            <el-select v-model="searchParams.sortBy" @change="handleSearch" size="small">
              <el-option label="最新发布" value="createTime" />
              <el-option label="最多浏览" value="viewCount" />
              <el-option label="最多点赞" value="likeCount" />
            </el-select>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>

        <!-- 文章网格 -->
        <div v-else class="articles-grid" ref="articlesGridRef">
          <article
            v-for="(article, index) in articles"
            :key="article.id"
            class="article-card"
            :style="{ animationDelay: `${index * 0.1}s` }"
            @click="goToDetail(article.id)"
          >
            <!-- 文章封面 -->
            <div class="article-cover">
              <img 
                v-if="article.coverImage" 
                :src="article.coverImage" 
                :alt="article.title"
                loading="lazy"
              />
              <div v-else class="default-cover">
                <el-icon><Document /></el-icon>
              </div>
              
              <!-- 覆盖层信息 -->
              <div class="cover-overlay">
                <div class="article-stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ formatNumber(article.viewCount) }}
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ formatNumber(article.likeCount) }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 文章信息 -->
            <div class="article-info">
              <!-- 分类标签 -->
              <div class="article-category" v-if="article.category">
                <span class="category-label">{{ article.category.name }}</span>
              </div>

              <!-- 标题和摘要 -->
              <h3 class="article-title">{{ article.title }}</h3>
              <p class="article-summary">{{ article.summary || '暂无摘要...' }}</p>

              <!-- 标签 -->
              <div class="article-tags" v-if="article.tags && article.tags.length">
                <span 
                  v-for="tag in article.tags.slice(0, 3)" 
                  :key="tag.id" 
                  class="tag-label"
                >
                  {{ tag.name }}
                </span>
              </div>

              <!-- 文章元信息 -->
              <div class="article-meta">
                <span class="publish-time">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(article.createTime) }}
                </span>
                <span class="read-time">
                  <el-icon><Reading /></el-icon>
                  {{ estimateReadTime(article.content) }}分钟阅读
                </span>
              </div>
            </div>
          </article>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && articles.length === 0" class="empty-state">
          <el-empty 
            description="暂无文章"
            image-size="120"
          >
            <AppleButton type="primary" @click="resetFilters">
              清除筛选条件
            </AppleButton>
          </el-empty>
        </div>

        <!-- 分页组件 -->
        <div v-if="!loading && articles.length > 0" class="pagination-container">
          <el-pagination
            v-model:current-page="searchParams.pageNum"
            v-model:page-size="searchParams.pageSize"
            :total="pagination.total"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
            class="pagination"
          />
      </div>
    </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document, View, Star, Clock, Reading, Search } from '@element-plus/icons-vue'
import { gsap } from 'gsap'
import AppleButton from '@/components/common/AppleButton.vue'
import { articleApi, categoryApi, tagApi } from '@/api'

// 路由
const router = useRouter()

// 响应式数据
const loading = ref(false)
const articles = ref([])
const categories = ref([])
const popularTags = ref([])
const selectedCategory = ref(null)
const selectedTags = ref([])
const totalArticles = ref(0)

// 搜索参数
const searchParams = reactive({
  keyword: '',
  pageNum: 1,
  pageSize: 12,
  categoryId: null,
  tagIds: [],
  sortBy: 'createTime'
})

// 分页信息
const pagination = reactive({
  total: 0,
  pages: 0,
  current: 1,
  size: 12
})

// DOM引用
const searchBoxRef = ref(null)
const filterRef = ref(null)
const articlesGridRef = ref(null)

// 计算属性
const searchKeyword = computed(() => searchParams.keyword?.trim())

// 防抖搜索
let searchTimer = null
const debounceSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    handleSearch()
  }, 500)
}

// 获取文章列表
const fetchArticles = async () => {
  try {
    loading.value = true
    
    const params = {
      pageNum: searchParams.pageNum,
      pageSize: searchParams.pageSize,
      keyword: searchParams.keyword,
      categoryId: searchParams.categoryId,
      tagIds: searchParams.tagIds.length > 0 ? searchParams.tagIds.join(',') : null,
      sortBy: searchParams.sortBy,
      status: 'published' // 只显示已发布的文章
    }

    const response = await articleApi.getArticleList(params)
    
    if (response.code === 200) {
      articles.value = response.data.records || []
      pagination.total = response.data.total || 0
      pagination.pages = response.data.pages || 0
      pagination.current = response.data.current || 1
      pagination.size = response.data.size || 12
      
      // 动画效果
      await nextTick()
      animateArticleCards()
    }
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await categoryApi.getAllCategories()
    if (response.code === 200) {
      categories.value = response.data || []
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取热门标签
const fetchPopularTags = async () => {
  try {
    const response = await tagApi.getPopularTags(20)
    if (response.code === 200) {
      popularTags.value = response.data || []
    }
  } catch (error) {
    console.error('获取热门标签失败:', error)
  }
}

// 选择分类
const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  searchParams.categoryId = categoryId
  searchParams.pageNum = 1
  handleSearch()
}

// 切换标签
const toggleTag = (tagId) => {
  const index = selectedTags.value.indexOf(tagId)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tagId)
  }
  searchParams.tagIds = [...selectedTags.value]
  searchParams.pageNum = 1
  handleSearch()
}

// 搜索处理
const handleSearch = () => {
  searchParams.pageNum = 1
  fetchArticles()
}

// 分页处理
const handlePageChange = (page) => {
  searchParams.pageNum = page
  fetchArticles()
  
  // 滚动到顶部
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

// 页面大小改变
const handleSizeChange = (size) => {
  searchParams.pageSize = size
  searchParams.pageNum = 1
  fetchArticles()
}

// 重置筛选条件
const resetFilters = () => {
  searchParams.keyword = ''
  searchParams.categoryId = null
  searchParams.tagIds = []
  selectedCategory.value = null
  selectedTags.value = []
  searchParams.pageNum = 1
  fetchArticles()
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/blog/${id}`)
}

// 格式化数字
const formatNumber = (num) => {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  
  if (diff < 24 * 60 * 60 * 1000) {
    return '今天'
  } else if (diff < 2 * 24 * 60 * 60 * 1000) {
    return '昨天'
  } else if (diff < 7 * 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

// 估算阅读时间
const estimateReadTime = (content) => {
  if (!content) return 1
  const wordsPerMinute = 200
  const wordCount = content.length
  return Math.max(1, Math.ceil(wordCount / wordsPerMinute))
}

// 动画效果
const animateSearchBox = () => {
  gsap.fromTo(searchBoxRef.value, 
    { y: 30, opacity: 0 },
    { y: 0, opacity: 1, duration: 0.6, ease: "power2.out" }
  )
}

const animateFilters = () => {
  gsap.fromTo(filterRef.value,
    { y: 20, opacity: 0 },
    { y: 0, opacity: 1, duration: 0.5, delay: 0.2, ease: "power2.out" }
  )
}

const animateArticleCards = () => {
  const cards = articlesGridRef.value?.querySelectorAll('.article-card')
  if (cards) {
    gsap.fromTo(cards,
      { y: 40, opacity: 0, scale: 0.95 },
      { 
        y: 0, 
        opacity: 1, 
        scale: 1,
        duration: 0.6,
        stagger: 0.1,
        ease: "power2.out"
      }
    )
  }
}

// 页面初始化
onMounted(async () => {
  // 并行获取数据
  await Promise.all([
    fetchCategories(),
    fetchPopularTags(),
    fetchArticles()
  ])
  
  // 页面动画
  await nextTick()
  animateSearchBox()
  animateFilters()
})
</script>

<style lang="scss" scoped>
.blog-list-page {
  min-height: 100vh;
  background: var(--apple-bg-primary);
}

// 搜索区域
.search-section {
  padding: var(--apple-space-2xl) 0;
  background: linear-gradient(135deg, var(--apple-bg-secondary) 0%, var(--apple-bg-primary) 100%);
  
  .search-header {
    text-align: center;
    margin-bottom: var(--apple-space-xl);
    
    .section-title {
      font-size: 3rem;
      font-weight: 700;
      color: var(--apple-text-primary);
      margin-bottom: var(--apple-space-sm);
      background: linear-gradient(135deg, var(--apple-blue) 0%, var(--apple-purple) 100%);
      -webkit-background-clip: text;
      background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    
    .section-subtitle {
      font-size: var(--apple-text-lg);
      color: var(--apple-text-secondary);
    }
  }
  
  .search-box {
    display: flex;
    gap: var(--apple-space-md);
    max-width: 600px;
    margin: 0 auto;
    
    .search-input {
      flex: 1;
      
      :deep(.el-input__wrapper) {
        border-radius: var(--apple-radius-lg);
        box-shadow: var(--apple-shadow-sm);
        border: 1px solid var(--apple-border-primary);
        transition: all 0.3s ease;
        
        &:hover {
          box-shadow: var(--apple-shadow-md);
        }
        
        &.is-focus {
          box-shadow: 0 0 0 3px rgba(var(--apple-blue-rgb), 0.1);
        }
      }
    }
    
    .search-btn {
      min-width: 120px;
    }
  }
}

// 筛选区域
.filter-section {
  padding: var(--apple-space-xl) 0;
  border-bottom: 1px solid var(--apple-border-primary);
  
  .filter-container {
    display: flex;
    gap: var(--apple-space-2xl);
    
    @media (max-width: 768px) {
      flex-direction: column;
      gap: var(--apple-space-lg);
    }
  }
  
  .filter-group {
    flex: 1;
    
    .filter-title {
      font-size: var(--apple-text-lg);
      font-weight: 600;
      color: var(--apple-text-primary);
      margin-bottom: var(--apple-space-md);
    }
  }
  
  .category-tags {
    display: flex;
    flex-wrap: wrap;
    gap: var(--apple-space-sm);
    
    .category-tag {
      display: flex;
      align-items: center;
      gap: var(--apple-space-xs);
      padding: var(--apple-space-sm) var(--apple-space-md);
      background: var(--apple-bg-secondary);
      border: 1px solid var(--apple-border-primary);
      border-radius: var(--apple-radius-full);
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        background: var(--apple-bg-hover);
        transform: translateY(-2px);
        box-shadow: var(--apple-shadow-sm);
      }
      
      &.active {
        background: var(--apple-blue);
        color: white;
        border-color: var(--apple-blue);
        box-shadow: var(--apple-shadow-md);
      }
      
      .category-name {
        font-weight: 500;
      }
      
      .category-count {
        background: rgba(255, 255, 255, 0.2);
        padding: 2px 6px;
        border-radius: 8px;
        font-size: var(--apple-text-xs);
      }
    }
  }
  
  .tag-cloud {
    display: flex;
    flex-wrap: wrap;
    gap: var(--apple-space-sm);
    
    .tag-item {
      padding: var(--apple-space-xs) var(--apple-space-sm);
      background: var(--apple-bg-secondary);
      border: 1px solid var(--apple-border-primary);
      border-radius: var(--apple-radius-md);
      cursor: pointer;
      transition: all 0.3s ease;
      font-weight: 500;
      
      &:hover {
        background: var(--apple-bg-hover);
        transform: scale(1.05);
      }
      
      &.active {
        background: var(--apple-green);
        color: white;
        border-color: var(--apple-green);
      }
    }
  }
}

// 文章区域
.articles-section {
  padding: var(--apple-space-xl) 0;
  
  .results-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: var(--apple-space-xl);
    
    .results-count {
      color: var(--apple-text-secondary);
      
      strong {
        color: var(--apple-text-primary);
        font-weight: 600;
      }
    }
    
    .sort-options {
      .el-select {
        width: 120px;
      }
    }
  }
  
  .loading-container {
    padding: var(--apple-space-xl);
  }
  
  .articles-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: var(--apple-space-xl);
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: var(--apple-space-lg);
    }
  }
  
  .article-card {
    background: var(--apple-bg-secondary);
    border-radius: var(--apple-radius-lg);
    overflow: hidden;
    box-shadow: var(--apple-shadow-sm);
    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    cursor: pointer;
    border: 1px solid var(--apple-border-primary);
    
    &:hover {
      transform: translateY(-8px) scale(1.02);
      box-shadow: var(--apple-shadow-lg);
      border-color: var(--apple-border-hover);
      
      .cover-overlay {
        opacity: 1;
      }
      
      .article-title {
        color: var(--apple-blue);
      }
    }
    
    .article-cover {
      position: relative;
      height: 200px;
      overflow: hidden;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.4s ease;
      }
      
      .default-cover {
        width: 100%;
        height: 100%;
        background: linear-gradient(135deg, var(--apple-blue-light) 0%, var(--apple-purple-light) 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        
        .el-icon {
          font-size: 3rem;
          color: var(--apple-blue);
        }
      }
      
      .cover-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(to bottom, transparent, rgba(0, 0, 0, 0.7));
        display: flex;
        align-items: flex-end;
        padding: var(--apple-space-md);
        opacity: 0;
        transition: opacity 0.3s ease;
        
        .article-stats {
          display: flex;
          gap: var(--apple-space-md);
          color: white;
          
          .stat-item {
            display: flex;
            align-items: center;
            gap: var(--apple-space-xs);
            font-size: var(--apple-text-sm);
          }
        }
      }
      
      &:hover img {
        transform: scale(1.1);
      }
    }
    
    .article-info {
      padding: var(--apple-space-lg);
      
      .article-category {
        margin-bottom: var(--apple-space-sm);
        
        .category-label {
          display: inline-block;
          padding: var(--apple-space-xs) var(--apple-space-sm);
          background: var(--apple-blue-light);
          color: var(--apple-blue);
          border-radius: var(--apple-radius-sm);
          font-size: var(--apple-text-xs);
          font-weight: 600;
        }
      }
      
      .article-title {
        font-size: var(--apple-text-xl);
        font-weight: 600;
        color: var(--apple-text-primary);
        margin-bottom: var(--apple-space-sm);
        line-height: 1.4;
        transition: color 0.3s ease;
        
        // 限制行数
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
      
      .article-summary {
        color: var(--apple-text-secondary);
        margin-bottom: var(--apple-space-md);
        line-height: 1.6;
        
        // 限制行数
        display: -webkit-box;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
      
      .article-tags {
        display: flex;
        flex-wrap: wrap;
        gap: var(--apple-space-xs);
        margin-bottom: var(--apple-space-md);
        
        .tag-label {
          padding: 2px var(--apple-space-xs);
          background: var(--apple-bg-primary);
          color: var(--apple-text-secondary);
          border-radius: var(--apple-radius-xs);
          font-size: var(--apple-text-xs);
          border: 1px solid var(--apple-border-primary);
        }
      }
      
      .article-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        color: var(--apple-text-tertiary);
        font-size: var(--apple-text-sm);
        
        .publish-time,
        .read-time {
          display: flex;
          align-items: center;
          gap: var(--apple-space-xs);
        }
      }
    }
  }
  
  .empty-state {
    padding: var(--apple-space-2xl);
    text-align: center;
  }
  
  .pagination-container {
    margin-top: var(--apple-space-2xl);
    display: flex;
    justify-content: center;
    
    .pagination {
      :deep(.el-pagination) {
        gap: var(--apple-space-sm);
      }
      
      :deep(.el-pager li) {
        border-radius: var(--apple-radius-sm);
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .search-section {
    padding: var(--apple-space-lg) 0;
    
    .search-header .section-title {
      font-size: 2rem;
    }
    
    .search-box {
      flex-direction: column;
      
      .search-btn {
        min-width: auto;
      }
    }
  }
  
  .articles-section .results-info {
    flex-direction: column;
    gap: var(--apple-space-md);
    align-items: stretch;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .search-section .search-header .section-title {
    font-size: 1.75rem;
  }
  
  .filter-section .filter-container {
    gap: var(--apple-space-md);
  }
  
     .articles-grid {
     grid-template-columns: 1fr;
  }
}
</style> 