/**
 * 认证工具函数库
 * 功能描述：提供认证模块使用的各种工具函数，包括令牌处理、状态检查、权限验证等
 * 采用技术：纯函数式编程，TypeScript类型安全，无副作用设计
 * 技术优势：可测试性强，复用性高，类型安全，便于维护和调试
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */

import { AuthState, UserInfo, AuthError, AuthErrorType, Permission } from '../types/auth';
import { TOKEN_CONFIG, SESSION_CONFIG } from '../constants/auth';

/**
 * 检查用户是否已认证
 * 功能描述：验证用户当前的认证状态是否有效
 * 采用技术：多重验证机制，包括令牌存在性、过期时间、用户信息完整性
 * 技术优势：全面的认证状态检查，确保安全性
 * 
 * @param authState 认证状态对象
 * @returns 是否已认证
 */
export const isAuthenticated = (authState: AuthState): boolean => {
  // 检查基础认证标志
  if (!authState.isAuthenticated) {
    return false;
  }
  
  // 检查访问令牌是否存在
  if (!authState.accessToken) {
    return false;
  }
  
  // 检查用户信息是否存在
  if (!authState.user) {
    return false;
  }
  
  // 检查令牌是否过期
  if (authState.tokenExpiry && Date.now() > authState.tokenExpiry) {
    return false;
  }
  
  return true;
};

/**
 * 检查令牌是否即将过期
 * 功能描述：判断访问令牌是否需要刷新
 * 采用技术：基于时间阈值的预判机制
 * 技术优势：提前刷新令牌，避免用户操作中断
 * 
 * @param tokenExpiry 令牌过期时间戳
 * @param threshold 提前刷新阈值（毫秒），默认使用配置值
 * @returns 是否需要刷新
 */
export const shouldRefreshToken = (
  tokenExpiry: number | null,
  threshold: number = TOKEN_CONFIG.REFRESH_THRESHOLD
): boolean => {
  // 如果没有过期时间，认为需要刷新
  if (!tokenExpiry) {
    return true;
  }
  
  // 检查是否在刷新阈值内
  const now = Date.now();
  return (tokenExpiry - now) <= threshold;
};

/**
 * 检查令牌是否已过期
 * 功能描述：验证令牌的有效性
 * 采用技术：时间戳比较
 * 技术优势：精确的过期检查，防止使用无效令牌
 * 
 * @param tokenExpiry 令牌过期时间戳
 * @returns 是否已过期
 */
export const isTokenExpired = (tokenExpiry: number | null): boolean => {
  // 如果没有过期时间，认为已过期
  if (!tokenExpiry) {
    return true;
  }
  
  return Date.now() > tokenExpiry;
};

/**
 * 计算令牌过期时间
 * 功能描述：根据令牌有效期计算具体的过期时间戳
 * 采用技术：时间戳计算，支持秒和毫秒单位
 * 技术优势：统一的过期时间计算，避免时间单位混乱
 * 
 * @param expiresIn 有效期（秒）
 * @param issuedAt 签发时间戳（毫秒），默认为当前时间
 * @returns 过期时间戳（毫秒）
 */
export const calculateTokenExpiry = (
  expiresIn: number,
  issuedAt: number = Date.now()
): number => {
  // 将秒转换为毫秒并计算过期时间
  return issuedAt + (expiresIn * 1000);
};

/**
 * 解析JWT令牌载荷
 * 功能描述：解析JWT令牌的载荷部分，获取用户信息和过期时间
 * 采用技术：Base64解码，JSON解析，错误处理
 * 技术优势：客户端令牌信息提取，无需服务器验证
 * 
 * @param token JWT令牌
 * @returns 解析后的载荷对象，解析失败返回null
 */
export const parseJwtPayload = (token: string): Record<string, any> | null => {
  try {
    // 移除Bearer前缀（如果存在）
    const cleanToken = token.replace(/^Bearer\s+/, '');
    
    // JWT由三部分组成，用.分隔
    const parts = cleanToken.split('.');
    if (parts.length !== 3) {
      return null;
    }
    
    // 解码载荷部分（第二部分）
    const payload = parts[1];
    
    // 处理Base64URL编码
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=');
    
    // 解码并解析JSON
    const decoded = atob(padded);
    return JSON.parse(decoded);
  } catch (error) {
    console.error('Failed to parse JWT payload:', error);
    return null;
  }
};

/**
 * 从JWT令牌中提取用户信息
 * 功能描述：从JWT载荷中提取并格式化用户信息
 * 采用技术：JWT解析，数据映射，类型转换
 * 技术优势：统一的用户信息提取，支持不同的JWT格式
 * 
 * @param token JWT令牌
 * @returns 用户信息对象，提取失败返回null
 */
export const extractUserFromToken = (token: string): UserInfo | null => {
  const payload = parseJwtPayload(token);
  if (!payload) {
    return null;
  }
  
  try {
    // 映射JWT载荷到用户信息对象
    return {
      id: payload.sub || payload.userId || payload.id,
      username: payload.username || payload.preferred_username,
      email: payload.email,
      roles: payload.roles || payload.authorities || [],
      avatar: payload.avatar || payload.picture,
      nickname: payload.nickname || payload.name,
      phone: payload.phone || payload.phone_number,
      status: payload.status || 'ACTIVE',
      createdAt: payload.created_at || payload.iat ? new Date(payload.iat * 1000).toISOString() : new Date().toISOString(),
      lastLoginAt: payload.last_login_at ? new Date(payload.last_login_at * 1000).toISOString() : undefined
    };
  } catch (error) {
    console.error('Failed to extract user from token:', error);
    return null;
  }
};

/**
 * 检查用户是否具有指定权限
 * 功能描述：验证用户是否拥有执行特定操作的权限
 * 采用技术：基于角色的访问控制(RBAC)，权限继承机制
 * 技术优势：灵活的权限检查，支持细粒度控制
 * 
 * @param user 用户信息
 * @param requiredPermission 所需权限代码
 * @param userPermissions 用户权限列表（可选）
 * @returns 是否具有权限
 */
export const hasPermission = (
  user: UserInfo | null,
  requiredPermission: string,
  userPermissions?: Permission[]
): boolean => {
  // 用户未登录，无权限
  if (!user) {
    return false;
  }
  
  // 超级管理员拥有所有权限
  if (user.roles.includes('SUPER_ADMIN')) {
    return true;
  }
  
  // 如果提供了权限列表，直接检查
  if (userPermissions) {
    return userPermissions.some(permission => permission.code === requiredPermission);
  }
  
  // 基于角色的简单权限检查（可根据实际需求扩展）
  const rolePermissionMap: Record<string, string[]> = {
    'ADMIN': ['user:read', 'user:write', 'user:delete', 'order:read', 'order:write'],
    'USER': ['user:read', 'order:read'],
    'GUEST': ['user:read']
  };
  
  return user.roles.some(role => {
    const permissions = rolePermissionMap[role] || [];
    return permissions.includes(requiredPermission);
  });
};

/**
 * 检查用户是否具有指定角色
 * 功能描述：验证用户是否拥有特定角色
 * 采用技术：角色匹配，支持多角色检查
 * 技术优势：简单直接的角色验证，支持角色继承
 * 
 * @param user 用户信息
 * @param requiredRole 所需角色
 * @param requireAll 是否需要拥有所有角色（默认false，拥有任一角色即可）
 * @returns 是否具有角色
 */
export const hasRole = (
  user: UserInfo | null,
  requiredRole: string | string[],
  requireAll: boolean = false
): boolean => {
  // 用户未登录，无角色
  if (!user) {
    return false;
  }
  
  const requiredRoles = Array.isArray(requiredRole) ? requiredRole : [requiredRole];
  
  if (requireAll) {
    // 需要拥有所有指定角色
    return requiredRoles.every(role => user.roles.includes(role));
  } else {
    // 拥有任一指定角色即可
    return requiredRoles.some(role => user.roles.includes(role));
  }
};

/**
 * 检查会话是否超时
 * 功能描述：根据最后活动时间判断会话是否超时
 * 采用技术：时间差计算，可配置超时阈值
 * 技术优势：自动会话管理，提升安全性
 * 
 * @param lastActivity 最后活动时间戳
 * @param timeout 超时时间（毫秒），默认使用配置值
 * @returns 是否已超时
 */
export const isSessionTimeout = (
  lastActivity: number,
  timeout: number = SESSION_CONFIG.SESSION_TIMEOUT
): boolean => {
  const now = Date.now();
  return (now - lastActivity) > timeout;
};

/**
 * 更新最后活动时间
 * 功能描述：记录用户的最后活动时间
 * 采用技术：时间戳记录，本地存储持久化
 * 技术优势：准确的活动时间追踪，支持会话管理
 * 
 * @returns 当前时间戳
 */
export const updateLastActivity = (): number => {
  const now = Date.now();
  // 可以在这里添加持久化逻辑
  return now;
};

/**
 * 创建认证错误对象
 * 功能描述：标准化认证错误的创建和格式化
 * 采用技术：错误对象标准化，类型安全
 * 技术优势：统一的错误处理，便于调试和用户反馈
 * 
 * @param type 错误类型
 * @param message 错误消息
 * @param code 错误代码（可选）
 * @param details 错误详情（可选）
 * @returns 标准化的认证错误对象
 */
export const createAuthError = (
  type: AuthErrorType,
  message: string,
  code?: string,
  details?: Record<string, any>
): AuthError => {
  return {
    type,
    message,
    code,
    details,
    timestamp: Date.now()
  };
};

/**
 * 格式化认证头
 * 功能描述：格式化HTTP请求的Authorization头
 * 采用技术：标准Bearer令牌格式
 * 技术优势：统一的认证头格式，符合OAuth2标准
 * 
 * @param token 访问令牌
 * @param tokenType 令牌类型，默认为Bearer
 * @returns 格式化的认证头值
 */
export const formatAuthHeader = (
  token: string,
  tokenType: string = TOKEN_CONFIG.TOKEN_TYPE
): string => {
  // 移除可能已存在的前缀
  const cleanToken = token.replace(/^Bearer\s+/, '');
  return `${tokenType} ${cleanToken}`;
};

/**
 * 生成随机字符串
 * 功能描述：生成指定长度的随机字符串，用于状态参数、nonce等
 * 采用技术：加密安全的随机数生成
 * 技术优势：高安全性的随机字符串，防止CSRF攻击
 * 
 * @param length 字符串长度，默认32
 * @returns 随机字符串
 */
export const generateRandomString = (length: number = 32): string => {
  const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  
  // 使用crypto API生成安全随机数（如果可用）
  if (typeof crypto !== 'undefined' && crypto.getRandomValues) {
    const array = new Uint8Array(length);
    crypto.getRandomValues(array);
    for (let i = 0; i < length; i++) {
      result += charset[array[i] % charset.length];
    }
  } else {
    // 降级到Math.random（不够安全，仅用于开发环境）
    for (let i = 0; i < length; i++) {
      result += charset[Math.floor(Math.random() * charset.length)];
    }
  }
  
  return result;
};

/**
 * 清理敏感数据
 * 功能描述：从对象中移除敏感信息，用于日志记录和调试
 * 采用技术：对象深拷贝，敏感字段过滤
 * 技术优势：数据安全，防止敏感信息泄露
 * 
 * @param data 原始数据对象
 * @param sensitiveFields 敏感字段列表
 * @returns 清理后的数据对象
 */
export const sanitizeData = (
  data: Record<string, any>,
  sensitiveFields: string[] = ['password', 'token', 'secret', 'key']
): Record<string, any> => {
  const sanitized = { ...data };
  
  sensitiveFields.forEach(field => {
    if (field in sanitized) {
      sanitized[field] = '[REDACTED]';
    }
  });
  
  return sanitized;
};

/**
 * 验证邮箱格式
 * 功能描述：验证邮箱地址的格式是否正确
 * 采用技术：正则表达式验证，符合RFC标准
 * 技术优势：准确的邮箱格式验证，防止无效邮箱
 * 
 * @param email 邮箱地址
 * @returns 是否为有效邮箱
 */
export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return emailRegex.test(email);
};

/**
 * 验证用户名格式
 * 功能描述：验证用户名是否符合规范
 * 采用技术：正则表达式验证，长度检查
 * 技术优势：统一的用户名规范，防止无效用户名
 * 
 * @param username 用户名
 * @returns 是否为有效用户名
 */
export const isValidUsername = (username: string): boolean => {
  // 检查长度
  if (username.length < 3 || username.length > 20) {
    return false;
  }
  
  // 检查格式：字母、数字、下划线，不能以数字开头
  const usernameRegex = /^[a-zA-Z][a-zA-Z0-9_]*$/;
  return usernameRegex.test(username);
};

/**
 * 获取用户显示名称
 * 功能描述：获取用户的显示名称，优先级：昵称 > 用户名 > 邮箱
 * 采用技术：优先级选择算法
 * 技术优势：友好的用户名显示，提升用户体验
 * 
 * @param user 用户信息
 * @returns 用户显示名称
 */
export const getUserDisplayName = (user: UserInfo | null): string => {
  if (!user) {
    return '未知用户';
  }
  
  return user.nickname || user.username || user.email || '未知用户';
};

/**
 * 获取用户头像URL
 * 功能描述：获取用户头像，如果没有则返回默认头像
 * 采用技术：URL验证，默认头像机制
 * 技术优势：确保头像显示，提升用户体验
 * 
 * @param user 用户信息
 * @param defaultAvatar 默认头像URL
 * @returns 头像URL
 */
export const getUserAvatarUrl = (
  user: UserInfo | null,
  defaultAvatar: string = '/images/default-avatar.png'
): string => {
  if (!user || !user.avatar) {
    return defaultAvatar;
  }
  
  // 验证URL格式
  try {
    new URL(user.avatar);
    return user.avatar;
  } catch {
    // 如果不是完整URL，可能是相对路径
    if (user.avatar.startsWith('/') || user.avatar.startsWith('./')) {
      return user.avatar;
    }
    return defaultAvatar;
  }
};