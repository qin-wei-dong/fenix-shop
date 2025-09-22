/**
 * 令牌存储工具函数库
 * 功能描述：提供访问令牌和刷新令牌的本地存储管理功能
 * 采用技术：localStorage API，TypeScript类型安全，错误处理机制
 * 技术优势：持久化存储，类型安全，异常处理，易于测试和维护
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */

import { STORAGE_KEYS, TOKEN_CONFIG } from '../constants/auth';

/**
 * 设置访问令牌到本地存储
 * 功能描述：将访问令牌安全地存储到localStorage中
 * 采用技术：localStorage API，异常处理
 * 技术优势：持久化存储，错误处理，类型安全
 * 
 * @param token 访问令牌字符串
 */
export const setToken = (token: string): void => {
  try {
    // 将访问令牌存储到localStorage
    localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, token);
  } catch (error) {
    // 处理存储异常（如存储空间不足）
    console.error('Failed to set access token:', error);
    throw new Error('无法保存访问令牌');
  }
};

/**
 * 从本地存储获取访问令牌
 * 功能描述：从localStorage中安全地读取访问令牌
 * 采用技术：localStorage API，null检查
 * 技术优势：安全读取，null处理，异常捕获
 * 
 * @returns 访问令牌字符串或null
 */
export const getToken = (): string | null => {
  try {
    // 从localStorage读取访问令牌
    return localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
  } catch (error) {
    // 处理读取异常
    console.error('Failed to get access token:', error);
    return null;
  }
};

/**
 * 从本地存储移除访问令牌
 * 功能描述：安全地从localStorage中删除访问令牌
 * 采用技术：localStorage API，异常处理
 * 技术优势：安全删除，错误处理，清理完整
 */
export const removeToken = (): void => {
  try {
    // 从localStorage移除访问令牌
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
  } catch (error) {
    // 处理删除异常
    console.error('Failed to remove access token:', error);
  }
};

/**
 * 设置刷新令牌到本地存储
 * 功能描述：将刷新令牌安全地存储到localStorage中
 * 采用技术：localStorage API，异常处理
 * 技术优势：持久化存储，错误处理，类型安全
 * 
 * @param refreshToken 刷新令牌字符串
 */
export const setRefreshToken = (refreshToken: string): void => {
  try {
    // 将刷新令牌存储到localStorage
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
  } catch (error) {
    // 处理存储异常
    console.error('Failed to set refresh token:', error);
    throw new Error('无法保存刷新令牌');
  }
};

/**
 * 从本地存储获取刷新令牌
 * 功能描述：从localStorage中安全地读取刷新令牌
 * 采用技术：localStorage API，null检查
 * 技术优势：安全读取，null处理，异常捕获
 * 
 * @returns 刷新令牌字符串或null
 */
export const getRefreshToken = (): string | null => {
  try {
    // 从localStorage读取刷新令牌
    return localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
  } catch (error) {
    // 处理读取异常
    console.error('Failed to get refresh token:', error);
    return null;
  }
};

/**
 * 从本地存储移除刷新令牌
 * 功能描述：安全地从localStorage中删除刷新令牌
 * 采用技术：localStorage API，异常处理
 * 技术优势：安全删除，错误处理，清理完整
 */
export const removeRefreshToken = (): void => {
  try {
    // 从localStorage移除刷新令牌
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
  } catch (error) {
    // 处理删除异常
    console.error('Failed to remove refresh token:', error);
  }
};

/**
 * 清除所有认证相关的存储数据
 * 功能描述：一次性清除所有认证令牌和相关数据
 * 采用技术：localStorage API，批量操作
 * 技术优势：完整清理，防止数据残留，操作原子性
 */
export const clearAuthStorage = (): void => {
  try {
    // 移除访问令牌
    removeToken();
    // 移除刷新令牌
    removeRefreshToken();
    // 移除其他认证相关数据
    localStorage.removeItem(STORAGE_KEYS.USER_INFO);
    localStorage.removeItem(STORAGE_KEYS.AUTH_STATE);
  } catch (error) {
    // 处理清理异常
    console.error('Failed to clear auth storage:', error);
  }
};

/**
 * 检查令牌是否需要刷新
 * 功能描述：根据令牌过期时间和刷新阈值判断是否需要刷新
 * 采用技术：时间计算，配置化阈值
 * 技术优势：自动化判断，可配置阈值，提前刷新避免过期
 * 
 * @param tokenExpiry 令牌过期时间戳
 * @returns 是否需要刷新令牌
 */
export const refreshTokenIfNeeded = async (tokenExpiry: number | null): Promise<boolean> => {
  // 检查令牌过期时间是否存在
  if (!tokenExpiry) {
    return false;
  }
  
  // 计算当前时间与过期时间的差值
  const timeUntilExpiry = tokenExpiry - Date.now();
  
  // 如果剩余时间小于刷新阈值，则需要刷新
  return timeUntilExpiry < TOKEN_CONFIG.REFRESH_THRESHOLD;
};

/**
 * 验证令牌格式是否有效
 * 功能描述：检查令牌字符串格式是否符合JWT标准
 * 采用技术：正则表达式，字符串分割验证
 * 技术优势：快速验证，格式检查，避免无效请求
 * 
 * @param token 令牌字符串
 * @returns 令牌格式是否有效
 */
export const isValidTokenFormat = (token: string | null): boolean => {
  // 检查令牌是否存在
  if (!token || typeof token !== 'string') {
    return false;
  }
  
  // 检查JWT格式（三个部分用.分隔）
  const parts = token.split('.');
  if (parts.length !== 3) {
    return false;
  }
  
  // 检查每个部分是否为有效的base64字符串
  return parts.every(part => {
    try {
      // 尝试解码base64
      atob(part.replace(/-/g, '+').replace(/_/g, '/'));
      return true;
    } catch {
      return false;
    }
  });
};