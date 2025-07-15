-- ================================================
-- 个人博客系统数据库创建脚本
-- ================================================
-- 数据库版本: MySQL 8.0+
-- 字符集: utf8mb4
-- 创建时间: 2024年
-- 文档版本: v1.0
-- ================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS person_blog 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE person_blog;

-- ================================================
-- 1. 用户管理模块
-- ================================================

-- 用户表
CREATE TABLE tb_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    nickname VARCHAR(100) NOT NULL COMMENT '昵称',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像URL',
    signature VARCHAR(255) COMMENT '个性签名',
    website VARCHAR(255) COMMENT '个人网站',
    github VARCHAR(255) COMMENT 'GitHub地址',
    weibo VARCHAR(255) COMMENT '微博地址',
    qq VARCHAR(20) COMMENT 'QQ号',
    wechat VARCHAR(50) COMMENT '微信号',
    profession VARCHAR(100) COMMENT '职业',
    introduction TEXT COMMENT '个人介绍',
    skills JSON COMMENT '技能标签（JSON格式）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_is_delete (is_delete)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ================================================
-- 2. 内容管理模块
-- ================================================

-- 分类表
CREATE TABLE tb_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    slug VARCHAR(100) UNIQUE COMMENT '分类别名（URL友好）',
    description TEXT COMMENT '分类描述',
    icon VARCHAR(100) COMMENT '分类图标',
    cover_image VARCHAR(500) COMMENT '分类封面图',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    level TINYINT DEFAULT 1 COMMENT '分类层级',
    path VARCHAR(500) DEFAULT '0,' COMMENT '分类路径，如：0,1,2,',
    sort_order INT DEFAULT 0 COMMENT '排序权重，数值越大越靠前',
    article_count INT DEFAULT 0 COMMENT '文章数量',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    seo_title VARCHAR(255) COMMENT 'SEO标题',
    seo_description VARCHAR(500) COMMENT 'SEO描述',
    seo_keywords VARCHAR(300) COMMENT 'SEO关键词',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    
    INDEX idx_parent_id (parent_id),
    INDEX idx_slug (slug),
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order),
    INDEX idx_article_count (article_count),
    INDEX idx_is_delete (is_delete)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 标签表
CREATE TABLE tb_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    slug VARCHAR(50) UNIQUE COMMENT '标签别名（URL友好）',
    color VARCHAR(20) DEFAULT '#409eff' COMMENT '标签颜色',
    description VARCHAR(200) COMMENT '标签描述',
    article_count INT DEFAULT 0 COMMENT '关联文章数量',
    click_count INT DEFAULT 0 COMMENT '点击次数',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    
    INDEX idx_name (name),
    INDEX idx_slug (slug),
    INDEX idx_article_count (article_count),
    INDEX idx_click_count (click_count),
    INDEX idx_status (status),
    INDEX idx_is_delete (is_delete)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 文章表
CREATE TABLE tb_article (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章ID',
    title VARCHAR(255) NOT NULL COMMENT '文章标题',
    slug VARCHAR(255) UNIQUE COMMENT '文章别名（URL友好）',
    summary TEXT COMMENT '文章摘要',
    content LONGTEXT NOT NULL COMMENT '文章内容（Markdown格式）',
    content_html LONGTEXT COMMENT '文章HTML内容',
    cover_image VARCHAR(500) COMMENT '封面图片URL',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-草稿，1-发布，2-下线',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    is_recommend TINYINT DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
    is_original TINYINT DEFAULT 1 COMMENT '是否原创：0-转载，1-原创',
    password VARCHAR(255) COMMENT '文章密码（加密文章）',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    comment_count INT DEFAULT 0 COMMENT '评论次数',
    collect_count INT DEFAULT 0 COMMENT '收藏次数',
    word_count INT DEFAULT 0 COMMENT '字数统计',
    reading_time INT DEFAULT 0 COMMENT '预计阅读时间（分钟）',
    published_time DATETIME COMMENT '发布时间',
    seo_title VARCHAR(255) COMMENT 'SEO标题',
    seo_description VARCHAR(500) COMMENT 'SEO描述',
    seo_keywords VARCHAR(300) COMMENT 'SEO关键词',
    original_url VARCHAR(500) COMMENT '原文链接（转载文章）',
    editor_type TINYINT DEFAULT 1 COMMENT '编辑器类型：1-Markdown，2-富文本',
    allow_comment TINYINT DEFAULT 1 COMMENT '是否允许评论：0-否，1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    
    INDEX idx_author_id (author_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_is_top (is_top),
    INDEX idx_is_recommend (is_recommend),
    INDEX idx_published_time (published_time),
    INDEX idx_view_count (view_count),
    INDEX idx_like_count (like_count),
    INDEX idx_create_time (create_time),
    INDEX idx_is_delete (is_delete),
    INDEX idx_slug (slug),
    FULLTEXT KEY ft_title_content (title, content),
    
    FOREIGN KEY (author_id) REFERENCES tb_user(id) ON DELETE RESTRICT,
    FOREIGN KEY (category_id) REFERENCES tb_category(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 文章标签关联表
CREATE TABLE tb_article_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    
    UNIQUE KEY uk_article_tag (article_id, tag_id),
    INDEX idx_article_id (article_id),
    INDEX idx_tag_id (tag_id),
    INDEX idx_is_delete (is_delete),
    
    FOREIGN KEY (article_id) REFERENCES tb_article(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tb_tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- ================================================
-- 3. 互动模块
-- ================================================

-- 评论表
CREATE TABLE tb_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID，0表示顶级评论',
    reply_to_id BIGINT DEFAULT 0 COMMENT '回复的评论ID',
    level TINYINT DEFAULT 1 COMMENT '评论层级',
    path VARCHAR(1000) DEFAULT '0,' COMMENT '评论路径',
    author_name VARCHAR(50) NOT NULL COMMENT '评论者姓名',
    author_email VARCHAR(100) NOT NULL COMMENT '评论者邮箱',
    author_website VARCHAR(200) COMMENT '评论者网站',
    author_avatar VARCHAR(500) COMMENT '评论者头像',
    author_ip VARCHAR(50) COMMENT '评论者IP',
    author_location VARCHAR(100) COMMENT '评论者地理位置',
    author_agent TEXT COMMENT '用户代理信息',
    content TEXT NOT NULL COMMENT '评论内容',
    content_html TEXT COMMENT 'HTML格式内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    reply_count INT DEFAULT 0 COMMENT '回复数',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝，3-垃圾评论',
    is_admin TINYINT DEFAULT 0 COMMENT '是否管理员评论：0-否，1-是',
    is_sticky TINYINT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    notify_email TINYINT DEFAULT 1 COMMENT '邮件通知：0-否，1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    
    INDEX idx_article_id (article_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_reply_to_id (reply_to_id),
    INDEX idx_status (status),
    INDEX idx_author_email (author_email),
    INDEX idx_author_ip (author_ip),
    INDEX idx_create_time (create_time),
    INDEX idx_is_delete (is_delete),
    
    FOREIGN KEY (article_id) REFERENCES tb_article(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ================================================
-- 4. 文件管理模块
-- ================================================

-- 文件表
CREATE TABLE tb_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_url VARCHAR(500) COMMENT '访问URL',
    file_type VARCHAR(100) COMMENT '文件类型',
    file_extension VARCHAR(20) COMMENT '文件扩展名',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小（字节）',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    upload_user_id BIGINT COMMENT '上传用户ID',
    upload_ip VARCHAR(50) COMMENT '上传IP',
    storage_type TINYINT DEFAULT 1 COMMENT '存储类型：1-本地，2-阿里云OSS，3-七牛云，4-双存储',
    bucket_name VARCHAR(100) COMMENT '存储桶名称',
    is_public TINYINT DEFAULT 1 COMMENT '是否公开：0-私有，1-公开',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    remark VARCHAR(255) COMMENT '备注信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    
    INDEX idx_file_name (file_name),
    INDEX idx_file_type (file_type),
    INDEX idx_upload_user_id (upload_user_id),
    INDEX idx_storage_type (storage_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_is_delete (is_delete),
    
    FOREIGN KEY (upload_user_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';

-- ================================================
-- 5. 系统配置模块
-- ================================================

-- 系统配置表
CREATE TABLE tb_system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_group VARCHAR(100) NOT NULL COMMENT '配置分组',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_name VARCHAR(200) COMMENT '配置名称',
    config_desc VARCHAR(500) COMMENT '配置描述',
    config_type TINYINT DEFAULT 1 COMMENT '配置类型：1-字符串，2-数字，3-布尔，4-JSON',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统配置：0-否，1-是',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    
    UNIQUE KEY uk_group_key (config_group, config_key),
    INDEX idx_config_group (config_group),
    INDEX idx_config_key (config_key),
    INDEX idx_status (status),
    INDEX idx_is_delete (is_delete)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ================================================
-- 6. 统计分析模块
-- ================================================

-- 访问日志表
CREATE TABLE tb_visit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    article_id BIGINT COMMENT '文章ID',
    visitor_ip VARCHAR(50) NOT NULL COMMENT '访问者IP',
    visitor_location VARCHAR(100) COMMENT '访问者地理位置',
    user_agent TEXT COMMENT '用户代理信息',
    browser VARCHAR(50) COMMENT '浏览器',
    browser_version VARCHAR(20) COMMENT '浏览器版本',
    os VARCHAR(50) COMMENT '操作系统',
    device VARCHAR(50) COMMENT '设备类型',
    referer VARCHAR(500) COMMENT '来源页面',
    request_url VARCHAR(500) COMMENT '请求URL',
    visit_time DATETIME NOT NULL COMMENT '访问时间',
    stay_time INT DEFAULT 0 COMMENT '停留时间（秒）',
    is_mobile TINYINT DEFAULT 0 COMMENT '是否移动端：0-否，1-是',
    is_spider TINYINT DEFAULT 0 COMMENT '是否爬虫：0-否，1-是',
    spider_name VARCHAR(50) COMMENT '爬虫名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    
    INDEX idx_article_id (article_id),
    INDEX idx_visitor_ip (visitor_ip),
    INDEX idx_visit_time (visit_time),
    INDEX idx_is_mobile (is_mobile),
    INDEX idx_is_spider (is_spider),
    INDEX idx_create_time (create_time),
    INDEX idx_is_delete (is_delete),
    
    FOREIGN KEY (article_id) REFERENCES tb_article(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问日志表';

-- ================================================
-- 7. 插入初始化数据
-- ================================================

-- 插入默认管理员用户（密码为：admin123）
INSERT INTO tb_user (username, password, nickname, email, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaYNuTBh6zda6', '管理员', 'admin@example.com', 1);

-- 插入默认分类
INSERT INTO tb_category (name, slug, description, status) VALUES
('技术分享', 'tech', '技术相关文章分享', 1),
('生活随笔', 'life', '生活感悟和随笔记录', 1),
('项目作品', 'projects', '个人项目作品展示', 1),
('学习笔记', 'study', '学习过程中的笔记整理', 1);

-- 插入默认标签
INSERT INTO tb_tag (name, slug, color) VALUES
('Vue.js', 'vuejs', '#4FC08D'),
('Spring Boot', 'springboot', '#6DB33F'),
('JavaScript', 'javascript', '#F7DF1E'),
('Java', 'java', '#ED8B00'),
('MySQL', 'mysql', '#4479A1'),
('Redis', 'redis', '#DC382D'),
('Docker', 'docker', '#2496ED'),
('Linux', 'linux', '#FCC624');

-- 插入系统配置
INSERT INTO tb_system_config (config_group, config_key, config_value, config_name, config_desc, config_type) VALUES
-- 网站基本配置
('site', 'site_name', '我的个人博客', '网站名称', '网站标题显示', 1),
('site', 'site_description', '记录技术与生活的个人博客', '网站描述', '网站描述信息', 1),
('site', 'site_keywords', '博客,技术,编程,生活,分享', '网站关键词', 'SEO关键词设置', 1),
('site', 'site_author', '博主', '网站作者', '网站作者信息', 1),
('site', 'site_logo', '', '网站Logo', '网站Logo图片URL', 1),
('site', 'site_favicon', '', '网站图标', '网站Favicon图标URL', 1),
('site', 'site_copyright', '© 2024 我的个人博客. All rights reserved.', '版权信息', '网站底部版权信息', 1),
('site', 'icp_beian', '', 'ICP备案号', '网站ICP备案号', 1),

-- 评论系统配置
('comment', 'comment_audit', '1', '评论审核', '是否开启评论审核', 3),
('comment', 'comment_guest', '1', '游客评论', '是否允许游客评论', 3),
('comment', 'comment_email_notify', '1', '邮件通知', '新评论邮件通知', 3),
('comment', 'comment_max_depth', '3', '最大回复层级', '评论最大嵌套层级', 2),

-- 文章配置
('article', 'article_page_size', '10', '文章分页大小', '文章列表每页显示数量', 2),
('article', 'article_excerpt_length', '200', '摘要长度', '文章摘要最大字符数', 2),
('article', 'article_auto_excerpt', '1', '自动生成摘要', '无摘要时自动截取内容', 3),

-- 文件上传配置
('upload', 'upload_max_size', '10485760', '文件大小限制', '上传文件最大大小（字节）', 2),
('upload', 'upload_allowed_types', 'jpg,jpeg,png,gif,webp,pdf,doc,docx', '允许上传类型', '允许上传的文件类型', 1),
('upload', 'upload_storage_type', '1', '存储类型', '文件存储方式：1-本地，2-阿里云OSS', 2);

-- ================================================
-- 脚本执行完成
-- ================================================

SELECT '数据库创建完成！' AS message;
SELECT 'Tables created:' AS info, COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema = 'person_blog'; 