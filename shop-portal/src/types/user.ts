/**
 * 用户相关类型定义
 */

// 用户基本信息
export interface User {
  userId: string;
  username: string;
  nickname?: string;
  email: string;
  mobile: string;
  avatarUrl?: string;
  userLevel: number;
  points: number;
  registeredAt: string;
  lastLoginTime?: string;
  isActive: boolean;
  permissions?: string[];
  roles?: string[];
  notificationSettings?: NotificationSettings;
  userPreferences?: UserPreferences;
}

// 通知设置
export interface NotificationSettings {
  emailNotifications: boolean;
  smsNotifications: boolean;
  pushNotifications: boolean;
  marketingEmails: boolean;
}

// 用户偏好设置
export interface UserPreferences {
  language: string;
  timezone: string;
  currency: string;
  theme: 'light' | 'dark' | 'auto';
}

// 登录请求
export interface LoginRequest {
  username: string; // 用户名、邮箱或手机号
  password: string;
  rememberMe?: boolean;
}

// 注册请求
export interface RegisterRequest {
  username: string;
  password: string;
  confirmPassword: string;
  mobile: string;
  email: string;
  nickname: string;
  referralCode?: string;
  agreeTerms: boolean;
}

// 登录响应
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  refreshToken?: string;
  userInfo: UserInfo;
}

// 用户信息（登录后返回）
export interface UserInfo {
  userId: string;
  username: string;
  email: string;
  mobile: string; // 已脱敏
  avatarUrl?: string;
  userLevel: number;
  points: number;
  lastLoginTime?: string;
  isActive: boolean;
  roles?: string[]; // 用户角色编码列表
  permissions?: string[]; // 用户权限列表
}

// 用户个人资料DTO
export interface UserProfileDTO {
  userId?: string;
  username: string;
  email: string;
  mobile: string;
  avatarUrl?: string;
  userLevel?: number;
  points?: number;
  registeredAt?: string;
  lastLoginTime?: string;
  isActive?: boolean;
  notificationSettings?: NotificationSettings;
  userPreferences?: UserPreferences;
}

// 修改密码请求
export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmNewPassword: string;
}

// 用户状态
export interface UserState {
  currentUser: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  token: string | null;
}

// 用户操作类型
export enum UserActionType {
  LOGIN_START = 'LOGIN_START',
  LOGIN_SUCCESS = 'LOGIN_SUCCESS',
  LOGIN_FAILURE = 'LOGIN_FAILURE',
  LOGOUT = 'LOGOUT',
  REGISTER_START = 'REGISTER_START',
  REGISTER_SUCCESS = 'REGISTER_SUCCESS',
  REGISTER_FAILURE = 'REGISTER_FAILURE',
  UPDATE_PROFILE_START = 'UPDATE_PROFILE_START',
  UPDATE_PROFILE_SUCCESS = 'UPDATE_PROFILE_SUCCESS',
  UPDATE_PROFILE_FAILURE = 'UPDATE_PROFILE_FAILURE',
  CHANGE_PASSWORD_START = 'CHANGE_PASSWORD_START',
  CHANGE_PASSWORD_SUCCESS = 'CHANGE_PASSWORD_SUCCESS',
  CHANGE_PASSWORD_FAILURE = 'CHANGE_PASSWORD_FAILURE',
  CLEAR_ERROR = 'CLEAR_ERROR',
  SET_LOADING = 'SET_LOADING'
}

// 用户操作
export interface UserAction {
  type: UserActionType;
  payload?: any;
}

// API响应基础类型
export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
  timestamp?: string;
  status?: number;
  error?: string;
  fieldErrors?: Record<string, string>;
}

// 可用性检查响应
export interface AvailabilityResponse {
  available: boolean;
  message: string;
}

// 用户验证规则
export interface UserValidationRules {
  username: {
    minLength: number;
    maxLength: number;
    pattern: RegExp;
  };
  password: {
    minLength: number;
    maxLength: number;
    requireUppercase: boolean;
    requireLowercase: boolean;
    requireNumbers: boolean;
    requireSpecialChars: boolean;
  };
  mobile: {
    pattern: RegExp;
  };
  email: {
    pattern: RegExp;
  };
}

// 表单验证错误
export interface FormValidationError {
  field: string;
  message: string;
}

// 用户等级信息
export interface UserLevel {
  level: number;
  name: string;
  minPoints: number;
  maxPoints: number;
  benefits: string[];
  color: string;
  icon: string;
}

// 积分记录
export interface PointsRecord {
  id: string;
  userId: string;
  points: number;
  type: 'earn' | 'spend';
  reason: string;
  createdAt: string;
  orderId?: string;
}

// 登录历史
export interface LoginHistory {
  id: string;
  userId: string;
  loginTime: string;
  ipAddress: string;
  userAgent: string;
  location?: string;
  success: boolean;
}

// 用户设置更新请求
export interface UpdateUserSettingsRequest {
  notificationSettings?: Partial<NotificationSettings>;
  userPreferences?: Partial<UserPreferences>;
}

// 头像上传响应
export interface AvatarUploadResponse {
  avatarUrl: string;
  thumbnailUrl?: string;
}

// 用户统计信息
export interface UserStats {
  totalOrders: number;
  totalSpent: number;
  totalPoints: number;
  memberSince: string;
  favoriteCategories: string[];
  recentActivity: UserActivity[];
}

// 用户活动记录
export interface UserActivity {
  id: string;
  type: 'login' | 'order' | 'review' | 'favorite' | 'share';
  description: string;
  timestamp: string;
  metadata?: Record<string, any>;
}

// 密码强度
export enum PasswordStrength {
  WEAK = 1,
  MEDIUM = 2,
  STRONG = 3
}

// 密码强度检查结果
export interface PasswordStrengthResult {
  strength: PasswordStrength;
  score: number;
  feedback: string[];
  suggestions: string[];
}

// 用户角色枚举
export enum UserRole {
  SUPER_ADMIN = 'SUPER_ADMIN',
  ADMIN = 'ADMIN', 
  USER = 'USER'
}

// 角色权限映射
export const ROLE_PERMISSIONS: Record<UserRole, string[]> = {
  [UserRole.SUPER_ADMIN]: ['*'], // 超级管理员拥有所有权限
  [UserRole.ADMIN]: [
    'user.view', 'user.edit', 'user.delete',
    'product.view', 'product.edit', 'product.delete',
    'order.view', 'order.edit', 'order.delete',
    'system.config'
  ],
  [UserRole.USER]: [
    'profile.view', 'profile.edit',
    'order.view', 'cart.manage',
    'product.view'
  ]
};

// 检查用户是否有指定权限
export const hasPermission = (userRoles: string[], permission: string): boolean => {
  if (!userRoles || userRoles.length === 0) return false;
  
  // 超级管理员拥有所有权限
  if (userRoles.includes(UserRole.SUPER_ADMIN)) return true;
  
  // 检查其他角色权限
  return userRoles.some(role => {
    const rolePermissions = ROLE_PERMISSIONS[role as UserRole];
    return rolePermissions && (rolePermissions.includes('*') || rolePermissions.includes(permission));
  });
};

// 检查用户是否为管理员
export const isAdmin = (userRoles: string[]): boolean => {
  return userRoles && (userRoles.includes(UserRole.SUPER_ADMIN) || userRoles.includes(UserRole.ADMIN));
};

// 检查用户是否为普通用户
export const isRegularUser = (userRoles: string[]): boolean => {
  return userRoles && userRoles.includes(UserRole.USER) && !isAdmin(userRoles);
};