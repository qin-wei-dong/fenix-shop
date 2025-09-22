/**
 * 认证模块常量配置
 * 功能描述：定义认证模块使用的所有常量，包括API端点、存储键、配置参数等
 * 采用技术：TypeScript常量定义，确保类型安全和代码可维护性
 * 技术优势：集中管理配置，便于修改和维护，避免魔法字符串
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */

/**
 * API端点常量
 * 功能描述：定义所有认证相关的API接口地址
 * 采用技术：RESTful API设计规范
 * 技术优势：统一的API地址管理，便于接口变更和维护
 */
export const AUTH_ENDPOINTS = {
  /** 用户登录接口 */
  LOGIN: '/api/user/login',
  /** 用户注册接口 */
  REGISTER: '/api/user/register',
  /** 用户登出接口 */
  LOGOUT: '/api/user/logout',
  /** 刷新令牌接口 */
  REFRESH_TOKEN: '/api/user/refresh',
  /** 获取用户信息接口 */
  USER_INFO: '/api/user/me',
  /** 修改密码接口 */
  CHANGE_PASSWORD: '/api/user/password',
  /** 忘记密码接口 */
  FORGOT_PASSWORD: '/api/user/forgot-password',
  /** 重置密码接口 */
  RESET_PASSWORD: '/api/user/reset-password',
  /** 验证令牌接口 */
  VERIFY_TOKEN: '/api/user/verify',
  /** 获取用户会话列表 */
  USER_SESSIONS: '/api/user/sessions',
  /** 终止指定会话 */
  TERMINATE_SESSION: '/api/user/sessions',
  /** 检查用户名可用性 */
  CHECK_USERNAME: '/api/user/check-username',
  /** 检查邮箱可用性 */
  CHECK_EMAIL: '/api/user/check-email'
} as const;

/**
 * 本地存储键名常量
 * 功能描述：定义本地存储使用的键名，避免键名冲突
 * 采用技术：命名空间前缀，确保键名唯一性
 * 技术优势：统一的存储键管理，防止键名冲突和拼写错误
 */
export const STORAGE_KEYS = {
  /** 访问令牌存储键 */
  ACCESS_TOKEN: 'token',
  /** 刷新令牌存储键 */
  REFRESH_TOKEN: 'refreshToken',
  /** 用户信息存储键 */
  USER_INFO: 'user',
  /** 认证状态存储键 */
  AUTH_STATE: 'fenix_auth_state',
  /** 记住登录状态存储键 */
  REMEMBER_ME: 'fenix_auth_remember_me',
  /** 最后活动时间存储键 */
  LAST_ACTIVITY: 'fenix_auth_last_activity',
  /** 登录重定向URL存储键 */
  REDIRECT_URL: 'fenix_auth_redirect_url',
  /** 主题设置存储键 */
  THEME_PREFERENCE: 'fenix_auth_theme',
  /** 语言设置存储键 */
  LANGUAGE_PREFERENCE: 'fenix_auth_language'
} as const;

/**
 * 令牌配置常量
 * 功能描述：定义JWT令牌相关的配置参数
 * 采用技术：基于时间的令牌管理策略
 * 技术优势：灵活的令牌生命周期管理，平衡安全性和用户体验
 */
export const TOKEN_CONFIG = {
  /** 访问令牌默认过期时间（毫秒）- 15分钟 */
  ACCESS_TOKEN_EXPIRY: 15 * 60 * 1000,
  /** 刷新令牌默认过期时间（毫秒）- 7天 */
  REFRESH_TOKEN_EXPIRY: 7 * 24 * 60 * 60 * 1000,
  /** 令牌刷新提前时间（毫秒）- 提前5分钟刷新 */
  REFRESH_THRESHOLD: 5 * 60 * 1000,
  /** 令牌类型 */
  TOKEN_TYPE: 'Bearer',
  /** JWT令牌前缀 */
  TOKEN_PREFIX: 'Bearer ',
  /** 令牌刷新重试次数 */
  REFRESH_RETRY_COUNT: 3,
  /** 令牌刷新重试间隔（毫秒） */
  REFRESH_RETRY_DELAY: 1000
} as const;

/**
 * 会话配置常量
 * 功能描述：定义用户会话相关的配置参数
 * 采用技术：基于活动时间的会话管理
 * 技术优势：自动会话管理，提升安全性和用户体验
 */
export const SESSION_CONFIG = {
  /** 会话超时时间（毫秒）- 30分钟无活动自动登出 */
  SESSION_TIMEOUT: 30 * 60 * 1000,
  /** 活动检查间隔（毫秒）- 每分钟检查一次 */
  ACTIVITY_CHECK_INTERVAL: 60 * 1000,
  /** 会话警告提前时间（毫秒）- 提前5分钟警告 */
  SESSION_WARNING_TIME: 5 * 60 * 1000,
  /** 最大并发会话数 */
  MAX_CONCURRENT_SESSIONS: 5,
  /** 记住登录状态的有效期（毫秒）- 30天 */
  REMEMBER_ME_DURATION: 30 * 24 * 60 * 60 * 1000
} as const;

/**
 * 路由配置常量
 * 功能描述：定义认证相关的路由路径
 * 采用技术：集中式路由管理
 * 技术优势：统一的路由配置，便于路由变更和维护
 */
export const AUTH_ROUTES = {
  /** 登录页面路径 */
  LOGIN: '/login',
  /** 注册页面路径 */
  REGISTER: '/register',
  /** 忘记密码页面路径 */
  FORGOT_PASSWORD: '/forgot-password',
  /** 重置密码页面路径 */
  RESET_PASSWORD: '/reset-password',
  /** 个人中心页面路径 */
  PROFILE: '/profile',
  /** 账户设置页面路径 */
  ACCOUNT_SETTINGS: '/account/settings',
  /** 安全设置页面路径 */
  SECURITY_SETTINGS: '/account/security',
  /** 默认登录后重定向路径 */
  DEFAULT_REDIRECT: '/',
  /** 默认登出后重定向路径 */
  LOGOUT_REDIRECT: '/login'
} as const;

/**
 * 白名单路由常量
 * 功能描述：定义不需要认证的路由路径
 * 采用技术：路径匹配模式，支持通配符
 * 技术优势：灵活的路由保护机制，避免过度拦截
 */
export const PUBLIC_ROUTES = [
  '/login',
  '/register',
  '/forgot-password',
  '/reset-password',
  '/about',
  '/contact',
  '/privacy',
  '/terms',
  '/help',
  '/api/health',
  '/api/public/*'
] as const;

/**
 * 验证规则常量
 * 功能描述：定义表单验证使用的规则参数
 * 采用技术：正则表达式和长度限制
 * 技术优势：统一的验证标准，确保数据质量和安全性
 */
export const VALIDATION_RULES = {
  /** 用户名验证规则 */
  USERNAME: {
    /** 最小长度 */
    MIN_LENGTH: 3,
    /** 最大长度 */
    MAX_LENGTH: 20,
    /** 用户名格式正则表达式 - 字母数字下划线 */
    PATTERN: /^[a-zA-Z0-9_]+$/,
    /** 错误消息 */
    ERROR_MESSAGE: '用户名只能包含字母、数字和下划线，长度3-20位'
  },
  /** 密码验证规则 */
  PASSWORD: {
    /** 最小长度 */
    MIN_LENGTH: 8,
    /** 最大长度 */
    MAX_LENGTH: 128,
    /** 密码强度正则表达式 - 至少包含字母和数字 */
    PATTERN: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/,
    /** 错误消息 */
    ERROR_MESSAGE: '密码至少8位，必须包含字母和数字'
  },
  /** 邮箱验证规则 */
  EMAIL: {
    /** 邮箱格式正则表达式 */
    PATTERN: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    /** 最大长度 */
    MAX_LENGTH: 254,
    /** 错误消息 */
    ERROR_MESSAGE: '请输入有效的邮箱地址'
  },
  /** 手机号验证规则 */
  PHONE: {
    /** 手机号格式正则表达式 - 中国大陆手机号 */
    PATTERN: /^1[3-9]\d{9}$/,
    /** 错误消息 */
    ERROR_MESSAGE: '请输入有效的手机号码'
  }
} as const;

/**
 * HTTP状态码常量
 * 功能描述：定义认证相关的HTTP状态码
 * 采用技术：标准HTTP状态码
 * 技术优势：标准化的状态码处理，便于错误分类和处理
 */
export const HTTP_STATUS = {
  /** 成功 */
  OK: 200,
  /** 创建成功 */
  CREATED: 201,
  /** 无内容 */
  NO_CONTENT: 204,
  /** 请求错误 */
  BAD_REQUEST: 400,
  /** 未授权 */
  UNAUTHORIZED: 401,
  /** 禁止访问 */
  FORBIDDEN: 403,
  /** 资源未找到 */
  NOT_FOUND: 404,
  /** 方法不允许 */
  METHOD_NOT_ALLOWED: 405,
  /** 冲突 */
  CONFLICT: 409,
  /** 请求实体过大 */
  PAYLOAD_TOO_LARGE: 413,
  /** 请求过于频繁 */
  TOO_MANY_REQUESTS: 429,
  /** 服务器内部错误 */
  INTERNAL_SERVER_ERROR: 500,
  /** 服务不可用 */
  SERVICE_UNAVAILABLE: 503
} as const;

/**
 * 错误消息常量
 * 功能描述：定义用户友好的错误提示消息
 * 采用技术：国际化支持的错误消息管理
 * 技术优势：统一的错误提示，提升用户体验
 */
export const ERROR_MESSAGES = {
  /** 网络错误 */
  NETWORK_ERROR: '网络连接失败，请检查网络设置',
  /** 服务器错误 */
  SERVER_ERROR: '服务器暂时不可用，请稍后重试',
  /** 认证失败 */
  AUTH_FAILED: '用户名或密码错误',
  /** 账户被锁定 */
  ACCOUNT_LOCKED: '账户已被锁定，请联系管理员',
  /** 账户未激活 */
  ACCOUNT_INACTIVE: '账户未激活，请检查邮箱激活链接',
  /** 令牌过期 */
  TOKEN_EXPIRED: '登录已过期，请重新登录',
  /** 令牌无效 */
  TOKEN_INVALID: '登录状态无效，请重新登录',
  /** 权限不足 */
  INSUFFICIENT_PERMISSIONS: '权限不足，无法访问该资源',
  /** 验证码错误 */
  CAPTCHA_ERROR: '验证码错误，请重新输入',
  /** 用户名已存在 */
  USERNAME_EXISTS: '用户名已存在，请选择其他用户名',
  /** 邮箱已存在 */
  EMAIL_EXISTS: '邮箱已被注册，请使用其他邮箱',
  /** 密码强度不足 */
  WEAK_PASSWORD: '密码强度不足，请设置更复杂的密码',
  /** 会话超时 */
  SESSION_TIMEOUT: '会话已超时，请重新登录',
  /** 未知错误 */
  UNKNOWN_ERROR: '发生未知错误，请稍后重试'
} as const;

/**
 * 成功消息常量
 * 功能描述：定义操作成功时的提示消息
 * 采用技术：用户友好的成功反馈
 * 技术优势：积极的用户反馈，提升用户体验
 */
export const SUCCESS_MESSAGES = {
  /** 登录成功 */
  LOGIN_SUCCESS: '登录成功，欢迎回来！',
  /** 注册成功 */
  REGISTER_SUCCESS: '注册成功，请查收激活邮件',
  /** 登出成功 */
  LOGOUT_SUCCESS: '已安全退出登录',
  /** 密码修改成功 */
  PASSWORD_CHANGED: '密码修改成功',
  /** 密码重置邮件发送成功 */
  RESET_EMAIL_SENT: '密码重置邮件已发送，请查收',
  /** 密码重置成功 */
  PASSWORD_RESET: '密码重置成功，请使用新密码登录',
  /** 个人信息更新成功 */
  PROFILE_UPDATED: '个人信息更新成功',
  /** 会话终止成功 */
  SESSION_TERMINATED: '会话已成功终止'
} as const;

/**
 * 加载状态常量
 * 功能描述：定义各种加载状态的标识
 * 采用技术：状态机模式
 * 技术优势：清晰的加载状态管理，提升用户体验
 */
export const LOADING_STATES = {
  /** 空闲状态 */
  IDLE: 'idle',
  /** 加载中 */
  LOADING: 'loading',
  /** 成功 */
  SUCCESS: 'success',
  /** 错误 */
  ERROR: 'error'
} as const;

/**
 * 事件名称常量
 * 功能描述：定义认证模块触发的自定义事件名称
 * 采用技术：事件驱动架构
 * 技术优势：解耦的事件通信，便于模块间协作
 */
export const AUTH_EVENTS = {
  /** 登录成功事件 */
  LOGIN_SUCCESS: 'auth:login:success',
  /** 登录失败事件 */
  LOGIN_FAILED: 'auth:login:failed',
  /** 登出事件 */
  LOGOUT: 'auth:logout',
  /** 令牌刷新事件 */
  TOKEN_REFRESHED: 'auth:token:refreshed',
  /** 令牌过期事件 */
  TOKEN_EXPIRED: 'auth:token:expired',
  /** 会话超时事件 */
  SESSION_TIMEOUT: 'auth:session:timeout',
  /** 用户信息更新事件 */
  USER_UPDATED: 'auth:user:updated',
  /** 权限变更事件 */
  PERMISSIONS_CHANGED: 'auth:permissions:changed'
} as const;

/**
 * 默认配置常量
 * 功能描述：定义认证模块的默认配置参数
 * 采用技术：可配置的默认值
 * 技术优势：灵活的配置管理，便于环境适配
 */
export const DEFAULT_CONFIG = {
  /** 是否启用自动刷新令牌 */
  AUTO_REFRESH_TOKEN: true,
  /** 是否启用会话超时检查 */
  SESSION_TIMEOUT_CHECK: true,
  /** 是否启用记住登录功能 */
  REMEMBER_ME_ENABLED: true,
  /** 是否启用多设备登录 */
  MULTI_DEVICE_LOGIN: true,
  /** 是否启用登录验证码 */
  LOGIN_CAPTCHA_ENABLED: false,
  /** 是否启用注册验证码 */
  REGISTER_CAPTCHA_ENABLED: true,
  /** 是否启用密码强度检查 */
  PASSWORD_STRENGTH_CHECK: true,
  /** 是否启用实时可用性检查 */
  REAL_TIME_AVAILABILITY_CHECK: true
} as const;