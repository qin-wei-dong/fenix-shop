/**
 * 认证服务模块
 * 功能描述：提供用户认证相关的API服务，包括登录、注册、登出、密码管理等功能
 * 采用技术：HTTP客户端封装，Promise异步处理，TypeScript类型安全，错误处理机制
 * 技术优势：统一的认证API接口，完善的错误处理，类型安全的数据传输
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */

import { 
  LoginRequest, 
  RegisterRequest, 
  AuthResponse, 
  UserInfo, 
  ChangePasswordRequest, 
  ForgotPasswordRequest, 
  ResetPasswordRequest,
  SessionInfo
} from '../types/auth';
import { AUTH_ENDPOINTS } from '../constants/auth';
import { post, get, put, del } from '../utils/http';

/**
 * 用户登录
 * 功能描述：用户登录认证，获取访问令牌和用户信息
 * 采用技术：HTTP POST请求，JWT令牌认证，表单数据验证
 * 技术优势：安全的登录流程，自动令牌管理，错误处理
 * 
 * @param loginData 登录请求数据
 * @returns Promise<认证响应>
 */
export const login = async (loginData: LoginRequest): Promise<AuthResponse> => {
  try {
    // 发送登录请求
    const response = await post<AuthResponse>(
      AUTH_ENDPOINTS.LOGIN,
      loginData,
      { skipAuth: true } // 登录请求不需要认证
    );
    
    return response.data;
  } catch (error) {
    console.error('Login failed:', error);
    throw error;
  }
};

/**
 * 用户注册
 * 功能描述：新用户注册账户，创建用户信息
 * 采用技术：HTTP POST请求，数据验证，密码加密传输
 * 技术优势：安全的注册流程，完整的数据验证，用户友好的错误提示
 * 
 * @param registerData 注册请求数据
 * @returns Promise<认证响应>
 */
export const register = async (registerData: RegisterRequest): Promise<AuthResponse> => {
  try {
    // 发送注册请求
    const response = await post<AuthResponse>(
      AUTH_ENDPOINTS.REGISTER,
      registerData,
      { skipAuth: true } // 注册请求不需要认证
    );
    
    return response.data;
  } catch (error) {
    console.error('Registration failed:', error);
    throw error;
  }
};

/**
 * 用户登出
 * 功能描述：用户安全登出，清除服务器端会话
 * 采用技术：HTTP POST请求，会话管理，令牌失效
 * 技术优势：安全的登出流程，服务器端会话清理，防止令牌泄露
 * 
 * @returns Promise<void>
 */
export const logout = async (): Promise<void> => {
  try {
    // 发送登出请求
    await post(AUTH_ENDPOINTS.LOGOUT);
  } catch (error) {
    console.error('Logout failed:', error);
    // 即使登出请求失败，也应该清除本地状态
    throw error;
  }
};

/**
 * 刷新访问令牌
 * 功能描述：使用刷新令牌获取新的访问令牌
 * 采用技术：HTTP POST请求，令牌刷新机制，自动重试
 * 技术优势：无感知的令牌刷新，延长用户会话，提升用户体验
 * 
 * @param refreshToken 刷新令牌
 * @returns Promise<认证响应>
 */
export const refreshToken = async (refreshToken: string): Promise<AuthResponse> => {
  try {
    // 发送令牌刷新请求
    const response = await post<AuthResponse>(
      AUTH_ENDPOINTS.REFRESH_TOKEN,
      { refreshToken },
      { skipAuth: true } // 刷新请求使用刷新令牌，不需要访问令牌
    );
    
    return response.data;
  } catch (error) {
    console.error('Token refresh failed:', error);
    throw error;
  }
};

/**
 * 获取当前用户信息
 * 功能描述：获取当前登录用户的详细信息
 * 采用技术：HTTP GET请求，令牌认证，用户信息缓存
 * 技术优势：实时的用户信息获取，支持信息更新同步
 * 
 * @returns Promise<用户信息>
 */
export const getCurrentUser = async (): Promise<UserInfo> => {
  try {
    // 发送获取用户信息请求
    const response = await get<UserInfo>(AUTH_ENDPOINTS.USER_INFO);
    
    return response.data;
  } catch (error) {
    console.error('Get current user failed:', error);
    throw error;
  }
};

/**
 * 修改密码
 * 功能描述：用户修改登录密码
 * 采用技术：HTTP PUT请求，密码验证，安全传输
 * 技术优势：安全的密码修改流程，旧密码验证，强密码要求
 * 
 * @param changePasswordData 修改密码请求数据
 * @returns Promise<void>
 */
export const changePassword = async (changePasswordData: ChangePasswordRequest): Promise<void> => {
  try {
    // 发送修改密码请求
    await put(
      AUTH_ENDPOINTS.CHANGE_PASSWORD,
      changePasswordData
    );
  } catch (error) {
    console.error('Change password failed:', error);
    throw error;
  }
};

/**
 * 忘记密码
 * 功能描述：用户忘记密码时发送重置邮件
 * 采用技术：HTTP POST请求，邮件发送，安全令牌生成
 * 技术优势：安全的密码重置流程，邮件验证，防止恶意重置
 * 
 * @param forgotPasswordData 忘记密码请求数据
 * @returns Promise<void>
 */
export const forgotPassword = async (forgotPasswordData: ForgotPasswordRequest): Promise<void> => {
  try {
    // 发送忘记密码请求
    await post(
      AUTH_ENDPOINTS.FORGOT_PASSWORD,
      forgotPasswordData,
      { skipAuth: true } // 忘记密码请求不需要认证
    );
  } catch (error) {
    console.error('Forgot password failed:', error);
    throw error;
  }
};

/**
 * 重置密码
 * 功能描述：通过重置令牌设置新密码
 * 采用技术：HTTP POST请求，令牌验证，密码重置
 * 技术优势：安全的密码重置，令牌有效期控制，防止重放攻击
 * 
 * @param resetPasswordData 重置密码请求数据
 * @returns Promise<void>
 */
export const resetPassword = async (resetPasswordData: ResetPasswordRequest): Promise<void> => {
  try {
    // 发送重置密码请求
    await post(
      AUTH_ENDPOINTS.RESET_PASSWORD,
      resetPasswordData,
      { skipAuth: true } // 重置密码请求使用重置令牌
    );
  } catch (error) {
    console.error('Reset password failed:', error);
    throw error;
  }
};

/**
 * 验证令牌有效性
 * 功能描述：验证当前访问令牌是否有效
 * 采用技术：HTTP GET请求，令牌验证，状态检查
 * 技术优势：实时的令牌状态检查，支持自动登出
 * 
 * @returns Promise<boolean>
 */
export const verifyToken = async (): Promise<boolean> => {
  try {
    // 发送令牌验证请求
    await get(AUTH_ENDPOINTS.VERIFY_TOKEN);
    return true;
  } catch (error) {
    console.error('Token verification failed:', error);
    return false;
  }
};

/**
 * 获取用户会话列表
 * 功能描述：获取用户当前所有活跃会话
 * 采用技术：HTTP GET请求，会话管理，设备识别
 * 技术优势：多设备会话管理，安全监控，异常登录检测
 * 
 * @returns Promise<会话信息数组>
 */
export const getUserSessions = async (): Promise<SessionInfo[]> => {
  try {
    // 发送获取会话列表请求
    const response = await get<SessionInfo[]>(AUTH_ENDPOINTS.USER_SESSIONS);
    
    return response.data;
  } catch (error) {
    console.error('Get user sessions failed:', error);
    throw error;
  }
};

/**
 * 终止指定会话
 * 功能描述：终止用户指定的会话
 * 采用技术：HTTP DELETE请求，会话管理，安全控制
 * 技术优势：精确的会话控制，安全的远程登出，防止未授权访问
 * 
 * @param sessionId 会话ID
 * @returns Promise<void>
 */
export const terminateSession = async (sessionId: string): Promise<void> => {
  try {
    // 发送终止会话请求
    await del(`${AUTH_ENDPOINTS.TERMINATE_SESSION}/${sessionId}`);
  } catch (error) {
    console.error('Terminate session failed:', error);
    throw error;
  }
};

/**
 * 检查用户名可用性
 * 功能描述：检查用户名是否已被使用
 * 采用技术：HTTP GET请求，实时检查，防抖优化
 * 技术优势：实时可用性检查，提升用户体验，减少注册失败
 * 
 * @param username 用户名
 * @returns Promise<boolean>
 */
export const checkUsernameAvailability = async (username: string): Promise<boolean> => {
  try {
    // 发送用户名可用性检查请求
    const response = await get<{ available: boolean }>(
      `${AUTH_ENDPOINTS.CHECK_USERNAME}?username=${encodeURIComponent(username)}`,
      { skipAuth: true } // 可用性检查不需要认证
    );
    
    return response.data.available;
  } catch (error) {
    console.error('Check username availability failed:', error);
    // 如果检查失败，假设用户名不可用（保守策略）
    return false;
  }
};

/**
 * 检查邮箱可用性
 * 功能描述：检查邮箱是否已被注册
 * 采用技术：HTTP GET请求，实时检查，防抖优化
 * 技术优势：实时可用性检查，防止重复注册，提升用户体验
 * 
 * @param email 邮箱地址
 * @returns Promise<boolean>
 */
export const checkEmailAvailability = async (email: string): Promise<boolean> => {
  try {
    // 发送邮箱可用性检查请求
    const response = await get<{ available: boolean }>(
      `${AUTH_ENDPOINTS.CHECK_EMAIL}?email=${encodeURIComponent(email)}`,
      { skipAuth: true } // 可用性检查不需要认证
    );
    
    return response.data.available;
  } catch (error) {
    console.error('Check email availability failed:', error);
    // 如果检查失败，假设邮箱不可用（保守策略）
    return false;
  }
};

/**
 * 更新用户信息
 * 功能描述：更新用户的个人信息
 * 采用技术：HTTP PUT请求，数据验证，部分更新支持
 * 技术优势：灵活的信息更新，数据验证，实时同步
 * 
 * @param userInfo 用户信息
 * @returns Promise<用户信息>
 */
export const updateUserInfo = async (userInfo: Partial<UserInfo>): Promise<UserInfo> => {
  try {
    // 发送更新用户信息请求
    const response = await put<UserInfo>(
      AUTH_ENDPOINTS.USER_INFO,
      userInfo
    );
    
    return response.data;
  } catch (error) {
    console.error('Update user info failed:', error);
    throw error;
  }
};

/**
 * 批量终止会话
 * 功能描述：批量终止多个会话
 * 采用技术：并发请求，错误聚合，部分成功处理
 * 技术优势：高效的批量操作，错误隔离，操作结果反馈
 * 
 * @param sessionIds 会话ID数组
 * @returns Promise<终止结果>
 */
export const terminateMultipleSessions = async (
  sessionIds: string[]
): Promise<{ success: string[]; failed: string[] }> => {
  const results = {
    success: [] as string[],
    failed: [] as string[]
  };
  
  // 并发终止所有会话
  const promises = sessionIds.map(async (sessionId) => {
    try {
      await terminateSession(sessionId);
      results.success.push(sessionId);
    } catch (error) {
      console.error(`Failed to terminate session ${sessionId}:`, error);
      results.failed.push(sessionId);
    }
  });
  
  await Promise.all(promises);
  
  return results;
};

/**
 * 终止所有其他会话
 * 功能描述：终止除当前会话外的所有其他会话
 * 采用技术：会话管理，批量操作，当前会话保护
 * 技术优势：安全的会话清理，保护当前会话，防止意外登出
 * 
 * @returns Promise<void>
 */
export const terminateAllOtherSessions = async (): Promise<void> => {
  try {
    // 获取所有会话
    const sessions = await getUserSessions();
    
    // 过滤出其他会话（非当前会话）
    const otherSessions = sessions.filter(session => !session.isCurrent);
    
    if (otherSessions.length > 0) {
      // 批量终止其他会话
      const sessionIds = otherSessions.map(session => session.sessionId);
      await terminateMultipleSessions(sessionIds);
    }
  } catch (error) {
    console.error('Terminate all other sessions failed:', error);
    throw error;
  }
};

/**
 * 认证服务默认导出
 * 功能描述：提供统一的认证服务接口
 * 采用技术：对象导出，方法聚合
 * 技术优势：便于使用的API接口，支持按需导入
 */
export default {
  login,
  register,
  logout,
  refreshToken,
  getCurrentUser,
  changePassword,
  forgotPassword,
  resetPassword,
  verifyToken,
  getUserSessions,
  terminateSession,
  checkUsernameAvailability,
  checkEmailAvailability,
  updateUserInfo,
  terminateMultipleSessions,
  terminateAllOtherSessions
};