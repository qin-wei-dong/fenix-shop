/**
 * 认证相关类型定义
 * 功能描述：定义前端认证模块所需的所有TypeScript类型
 * 采用技术：TypeScript 5.0+ 严格类型系统
 * 技术优势：提供完整的类型安全，减少运行时错误，提升开发体验
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */

/**
 * 登录请求参数类型
 * 功能描述：用户登录时提交的表单数据结构
 * 采用技术：严格的接口定义确保数据完整性
 * 技术优势：编译时类型检查，防止参数错误
 */
export interface LoginRequest {
  /** 用户名（用户名/邮箱/手机号） */
  username: string;
  /** 用户密码 */
  password: string;
  /** 是否记住登录状态 */
  rememberMe?: boolean;
  /** 验证码（如果需要） */
  captcha?: string;
}

/**
 * 注册请求参数类型
 * 功能描述：用户注册时提交的表单数据结构
 * 采用技术：扩展性设计，支持未来字段扩展
 * 技术优势：类型安全的注册流程，减少数据错误
 */
export interface RegisterRequest {
  /** 用户名 */
  username: string;
  /** 邮箱地址 */
  email: string;
  /** 密码 */
  password: string;
  /** 确认密码 */
  confirmPassword: string;
  /** 昵称 */
  nickname: string;
  /** 手机号码 */
  mobile: string;
  /** 推荐码（可选） */
  referralCode?: string;
  /** 验证码（可选） */
  captcha?: string;
  /** 同意服务条款 */
  agreeTerms: boolean;
}

/**
 * 认证响应数据类型
 * 功能描述：后端认证接口返回的数据结构
 * 采用技术：JWT令牌标准，符合OAuth2规范
 * 技术优势：标准化的认证响应，便于集成和维护
 */
export interface AuthResponse {
  /** 访问令牌 */
  accessToken: string;
  /** 刷新令牌 */
  refreshToken: string;
  /** 令牌类型，通常为 'Bearer' */
  tokenType: string;
  /** 访问令牌过期时间（秒） */
  expiresIn: number;
  /** 用户基本信息 */
  user: UserInfo;
}

/**
 * 用户基本信息类型
 * 功能描述：认证成功后返回的用户信息
 * 采用技术：最小化信息原则，只包含必要字段
 * 技术优势：减少数据传输，保护用户隐私
 */
export interface UserInfo {
  /** 用户ID */
  id: string;
  /** 用户名 */
  username: string;
  /** 邮箱地址 */
  email: string;
  /** 用户角色 */
  roles: string[];
  /** 头像URL */
  avatar?: string;
  /** 昵称 */
  nickname?: string;
  /** 手机号码 */
  phone?: string;
  /** 账户状态 */
  status: UserStatus;
  /** 创建时间 */
  createdAt: string;
  /** 最后登录时间 */
  lastLoginAt?: string;
}

/**
 * 用户状态枚举
 * 功能描述：定义用户账户的各种状态
 * 采用技术：TypeScript枚举类型
 * 技术优势：类型安全的状态管理，避免魔法字符串
 */
export enum UserStatus {
  /** 正常状态 */
  ACTIVE = 'ACTIVE',
  /** 未激活 */
  INACTIVE = 'INACTIVE',
  /** 已禁用 */
  DISABLED = 'DISABLED',
  /** 已锁定 */
  LOCKED = 'LOCKED'
}

/**
 * 认证状态类型
 * 功能描述：前端认证状态的完整描述
 * 采用技术：状态机模式，确保状态转换的正确性
 * 技术优势：清晰的状态管理，便于调试和维护
 */
export interface AuthState {
  /** 是否已认证 */
  isAuthenticated: boolean;
  /** 是否正在加载 */
  isLoading: boolean;
  /** 当前用户信息 */
  user: UserInfo | null;
  /** 访问令牌 */
  accessToken: string | null;
  /** 令牌过期时间 */
  tokenExpiry: number | null;
  /** 认证错误信息 */
  error: string | null;
  /** 最后活动时间 */
  lastActivity: number;
}

/**
 * 令牌刷新请求类型
 * 功能描述：刷新访问令牌时的请求参数
 * 采用技术：OAuth2 Refresh Token标准
 * 技术优势：安全的令牌续期机制
 */
export interface RefreshTokenRequest {
  /** 刷新令牌 */
  refreshToken: string;
}

/**
 * 密码修改请求类型
 * 功能描述：用户修改密码时的请求参数
 * 采用技术：安全的密码变更流程
 * 技术优势：确保密码修改的安全性和完整性
 */
export interface ChangePasswordRequest {
  /** 当前密码 */
  currentPassword: string;
  /** 新密码 */
  newPassword: string;
  /** 确认新密码 */
  confirmNewPassword: string;
}

/**
 * 忘记密码请求类型
 * 功能描述：用户忘记密码时的重置请求
 * 采用技术：邮箱验证码重置机制
 * 技术优势：安全的密码重置流程
 */
export interface ForgotPasswordRequest {
  /** 邮箱地址 */
  email: string;
  /** 验证码（可选，某些流程需要） */
  captcha?: string;
}

/**
 * 重置密码请求类型
 * 功能描述：通过重置令牌设置新密码
 * 采用技术：临时令牌验证机制
 * 技术优势：安全的密码重置确认流程
 */
export interface ResetPasswordRequest {
  /** 重置令牌 */
  resetToken: string;
  /** 新密码 */
  newPassword: string;
  /** 确认新密码 */
  confirmNewPassword: string;
}

/**
 * 认证错误类型
 * 功能描述：定义认证过程中可能出现的错误类型
 * 采用技术：分类错误处理，便于用户理解和系统处理
 * 技术优势：精确的错误分类，提升用户体验
 */
export enum AuthErrorType {
  /** 无效凭据 */
  INVALID_CREDENTIALS = 'INVALID_CREDENTIALS',
  /** 账户被锁定 */
  ACCOUNT_LOCKED = 'ACCOUNT_LOCKED',
  /** 账户未激活 */
  ACCOUNT_INACTIVE = 'ACCOUNT_INACTIVE',
  /** 令牌过期 */
  TOKEN_EXPIRED = 'TOKEN_EXPIRED',
  /** 令牌无效 */
  TOKEN_INVALID = 'TOKEN_INVALID',
  /** 网络错误 */
  NETWORK_ERROR = 'NETWORK_ERROR',
  /** 服务器错误 */
  SERVER_ERROR = 'SERVER_ERROR',
  /** 验证码错误 */
  CAPTCHA_ERROR = 'CAPTCHA_ERROR',
  /** 用户名已存在 */
  USERNAME_EXISTS = 'USERNAME_EXISTS',
  /** 邮箱已存在 */
  EMAIL_EXISTS = 'EMAIL_EXISTS',
  /** 密码强度不足 */
  WEAK_PASSWORD = 'WEAK_PASSWORD',
  /** 未知错误 */
  UNKNOWN_ERROR = 'UNKNOWN_ERROR'
}

/**
 * 认证错误详情类型
 * 功能描述：包含错误类型和详细信息的错误对象
 * 采用技术：结构化错误信息，便于错误处理和用户提示
 * 技术优势：统一的错误处理机制，提升开发效率
 */
export interface AuthError {
  /** 错误类型 */
  type: AuthErrorType;
  /** 错误消息 */
  message: string;
  /** 错误代码 */
  code?: string;
  /** 错误详情 */
  details?: Record<string, any>;
  /** 错误时间戳 */
  timestamp: number;
}

/**
 * 会话信息类型
 * 功能描述：用户会话的详细信息
 * 采用技术：会话管理机制，支持多设备登录追踪
 * 技术优势：完整的会话生命周期管理
 */
export interface SessionInfo {
  /** 会话ID */
  sessionId: string;
  /** 设备信息 */
  deviceInfo: string;
  /** IP地址 */
  ipAddress: string;
  /** 登录时间 */
  loginTime: string;
  /** 最后活动时间 */
  lastActivity: string;
  /** 是否为当前会话 */
  isCurrent: boolean;
}

/**
 * 权限类型
 * 功能描述：用户权限的详细定义
 * 采用技术：基于角色的访问控制(RBAC)
 * 技术优势：灵活的权限管理，支持细粒度控制
 */
export interface Permission {
  /** 权限ID */
  id: string;
  /** 权限名称 */
  name: string;
  /** 权限代码 */
  code: string;
  /** 权限描述 */
  description?: string;
  /** 资源类型 */
  resource: string;
  /** 操作类型 */
  action: string;
}

/**
 * 角色类型
 * 功能描述：用户角色的完整定义
 * 采用技术：角色权限映射，支持权限继承
 * 技术优势：简化权限管理，提升安全性
 */
export interface Role {
  /** 角色ID */
  id: string;
  /** 角色名称 */
  name: string;
  /** 角色代码 */
  code: string;
  /** 角色描述 */
  description?: string;
  /** 角色权限列表 */
  permissions: Permission[];
  /** 是否为默认角色 */
  isDefault: boolean;
}