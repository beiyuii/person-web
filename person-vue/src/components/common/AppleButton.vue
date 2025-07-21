<template>
  <button
    :class="buttonClasses"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <!-- 加载状态图标 -->
    <div v-if="loading" class="apple-btn-loading">
      <div class="apple-spinner"></div>
    </div>
    
    <!-- 左侧图标 -->
    <component 
      v-if="prefixIcon && !loading" 
      :is="prefixIcon" 
      class="apple-btn-icon apple-btn-icon--prefix"
    />
    
    <!-- 按钮文本 -->
    <span v-if="$slots.default" class="apple-btn-text">
      <slot></slot>
    </span>
    
    <!-- 右侧图标 -->
    <component 
      v-if="suffixIcon && !loading" 
      :is="suffixIcon" 
      class="apple-btn-icon apple-btn-icon--suffix"
    />
  </button>
</template>

<script setup>
import { computed } from 'vue'

/**
 * 苹果风格按钮组件
 * 支持多种类型、尺寸和状态
 */
const props = defineProps({
  // 按钮类型
  type: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary', 'outline', 'ghost', 'danger'].includes(value)
  },
  
  // 按钮尺寸
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  
  // 是否加载中
  loading: {
    type: Boolean,
    default: false
  },
  
  // 是否圆角
  rounded: {
    type: Boolean,
    default: false
  },
  
  // 前缀图标
  prefixIcon: {
    type: [String, Object],
    default: null
  },
  
  // 后缀图标
  suffixIcon: {
    type: [String, Object],
    default: null
  },
  
  // 是否为块级按钮
  block: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

// 计算按钮样式类
const buttonClasses = computed(() => {
  return [
    'apple-btn',
    `apple-btn--${props.type}`,
    `apple-btn--${props.size}`,
    {
      'apple-btn--disabled': props.disabled,
      'apple-btn--loading': props.loading,
      'apple-btn--rounded': props.rounded,
      'apple-btn--block': props.block
    }
  ]
})

// 处理点击事件
const handleClick = (event) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style lang="scss" scoped>
.apple-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--apple-space-sm);
  border: none;
  border-radius: var(--apple-radius-md);
  font-family: var(--apple-font-family);
  font-weight: 500;
  text-align: center;
  cursor: pointer;
  transition: all var(--apple-transition-fast);
  position: relative;
  overflow: hidden;
  user-select: none;
  white-space: nowrap;
  
  // 按下效果
  &:active {
    transform: scale(0.95);
  }
  
  // 聚焦样式
  &:focus {
    outline: none;
    box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.3);
  }
  
  // 类型样式
  &--primary {
    background: var(--apple-blue);
    color: white;
    
    &:hover:not(.apple-btn--disabled) {
      background: #E55A2B;
      box-shadow: var(--apple-shadow-md);
    }
  }
  
  &--secondary {
    background: var(--apple-gray-5);
    color: var(--apple-text-primary);
    
    &:hover:not(.apple-btn--disabled) {
      background: var(--apple-gray-4);
      box-shadow: var(--apple-shadow-sm);
    }
  }
  
  &--outline {
    background: transparent;
    color: var(--apple-blue);
    border: 1px solid var(--apple-blue);
    
    &:hover:not(.apple-btn--disabled) {
      background: rgba(255, 107, 53, 0.1);
    }
  }
  
  &--ghost {
    background: transparent;
    color: var(--apple-blue);
    
    &:hover:not(.apple-btn--disabled) {
      background: rgba(255, 107, 53, 0.1);
    }
  }
  
  &--danger {
    background: var(--apple-red);
    color: white;
    
    &:hover:not(.apple-btn--disabled) {
      background: #D70015;
      box-shadow: var(--apple-shadow-md);
    }
  }
  
  // 尺寸样式
  &--small {
    padding: 8px 16px;
    font-size: var(--apple-text-sm);
    min-height: 32px;
  }
  
  &--medium {
    padding: 12px 24px;
    font-size: var(--apple-text-md);
    min-height: 44px;
  }
  
  &--large {
    padding: 16px 32px;
    font-size: var(--apple-text-lg);
    min-height: 56px;
  }
  
  // 状态样式
  &--disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none !important;
  }
  
  &--loading {
    cursor: wait;
  }
  
  &--rounded {
    border-radius: 50px;
  }
  
  &--block {
    width: 100%;
  }
}

// 按钮图标
.apple-btn-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  
  &--prefix {
    margin-right: 4px;
  }
  
  &--suffix {
    margin-left: 4px;
  }
}

// 加载动画
.apple-btn-loading {
  display: flex;
  align-items: center;
  justify-content: center;
}

.apple-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

// 按钮文本
.apple-btn-text {
  flex: 1;
  text-align: center;
}
</style> 