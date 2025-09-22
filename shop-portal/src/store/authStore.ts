/**
 * 认证状态管理Store
 * 功能描述：使用Zustand管理用户认证状态，包括登录状态、用户信息、令牌管理等
 * 采用技术：Zustand状态管理，TypeScript类型安全，持久化存储，中间件模式
 * 技术优势：轻量级状态管理，优秀的TypeScript支持，自动持久化，开发工具集成
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */

import { create } from 'zustand';
import { devtools, persist, subscribeWithSelector } from 'zustand/middleware';
import { immer } from 'zustand/middleware/immer';
import { 
  AuthState, 
  UserInfo, 
  LoginRequest, 
  RegisterRequest, 
  AuthResponse,
  ChangePasswordRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
  SessionInfo
} from '../types/auth';
import { 
  STORAGE_KEYS, 
  SESSION_CONFIG, 
  AUTH_EVENTS
} from '../constants/auth';
import {
  login as loginApi,
  register as registerApi,
  logout as logoutApi,
  refreshToken as refreshTokenApi,
  getCurrentUser,
  changePassword as changePasswordApi,
  forgotPassword as forgotPasswordApi,
  resetPassword as resetPasswordApi,
  verifyToken,
  getUserSessions,
  terminateSession,
  terminateAllOtherSessions
} from '../services/authService';
import {
  isTokenExpired,
  parseJwtPayload,
  extractUserFromToken,
} from '../utils/auth';
import {
  setToken,
  getToken,
  removeToken,
  setRefreshToken,
  getRefreshToken,
  removeRefreshToken
} from '../utils/tokenStorage';

/**
 * 认证Store状态接口
 * 功能描述：定义认证Store的完整状态结构
 * 采用技术：TypeScript接口，状态分离，操作方法定义
 * 技术优势：类型安全的状态管理，清晰的状态结构
 */
interface AuthStore extends AuthState {
  // 基础认证操作
  login: (loginData: LoginRequest) => Promise<void>;
  register: (registerData: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  
  // 令牌管理
  refreshToken: () => Promise<void>;
  checkTokenValidity: () => Promise<boolean>;
  
  // 用户信息管理
  fetchUserInfo: () => Promise<void>;
  updateUserInfo: (userInfo: Partial<UserInfo>) => void;
  
  // 密码管理
  changePassword: (changePasswordData: ChangePasswordRequest) => Promise<void>;
  forgotPassword: (forgotPasswordData: ForgotPasswordRequest) => Promise<void>;
  resetPassword: (resetPasswordData: ResetPasswordRequest) => Promise<void>;
  
  // 会话管理
  fetchSessions: () => Promise<SessionInfo[]>;
  terminateSession: (sessionId: string) => Promise<void>;
  terminateAllOtherSessions: () => Promise<void>;
  
  // 状态管理
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  clearError: () => void;
  updateLastActivity: () => void;
  
  // 初始化和清理
  initialize: () => Promise<void>;
  reset: () => void;
}

/**
 * 初始状态
 * 功能描述：定义认证Store的初始状态
 * 采用技术：状态初始化，默认值设置
 * 技术优势：一致的初始状态，便于状态重置
 */
const initialState: AuthState = {
  isAuthenticated: false,
  isLoading: false,
  user: null,
  accessToken: null,
  tokenExpiry: null,
  error: null,
  lastActivity: Date.now()
};

/**
 * 创建认证Store
 * 功能描述：使用Zustand创建认证状态管理Store
 * 采用技术：Zustand create，中间件组合，状态持久化
 * 技术优势：强大的状态管理能力，开发工具支持，自动持久化
 */
export const useAuthStore = create<AuthStore>()(
  devtools(
    persist(
      subscribeWithSelector(
        immer<AuthStore>((set, get) => ({
            ...initialState,

            /**
             * 用户登录
             * 功能描述：处理用户登录流程，更新认证状态
             * 采用技术：异步状态更新，错误处理，令牌存储
             * 技术优势：完整的登录流程，自动状态同步
             */
            login: async (loginData: LoginRequest) => {
              set((state) => {
                state.isLoading = true;
                state.error = null;
              });

              try {
                // 调用登录API
                const authResponse: AuthResponse = await loginApi(loginData);
                
                // 存储令牌
                setToken(authResponse.accessToken);
                setRefreshToken(authResponse.refreshToken);
                
                // 计算令牌过期时间
                const tokenExpiry = Date.now() + (authResponse.expiresIn * 1000);
                
                // 更新状态
                set((state) => {
                  state.isAuthenticated = true;
                  state.user = authResponse.user;
                  state.accessToken = authResponse.accessToken;
                  state.tokenExpiry = tokenExpiry;
                  state.lastActivity = Date.now();
                  state.isLoading = false;
                  state.error = null;
                });
                
                // 发送登录成功事件
                window.dispatchEvent(new CustomEvent(AUTH_EVENTS.LOGIN_SUCCESS, {
                  detail: { user: authResponse.user }
                }));
                
              } catch (error: any) {
                set((state) => {
                  state.isLoading = false;
                  state.error = error.message || '登录失败';
                });
                
                // 发送登录失败事件
                window.dispatchEvent(new CustomEvent(AUTH_EVENTS.LOGIN_FAILED, {
                  detail: { error: error.message }
                }));
                
                throw error;
              }
            },

            /**
             * 用户注册
             * 功能描述：处理用户注册流程
             * 采用技术：异步状态更新，错误处理，自动登录
             * 技术优势：完整的注册流程，注册后自动登录
             */
            register: async (registerData: RegisterRequest) => {
              set((state) => {
                state.isLoading = true;
                state.error = null;
              });

              try {
                // 调用注册API
                const authResponse: AuthResponse = await registerApi(registerData);
                
                // 存储令牌
                setToken(authResponse.accessToken);
                setRefreshToken(authResponse.refreshToken);
                
                // 计算令牌过期时间
                const tokenExpiry = Date.now() + (authResponse.expiresIn * 1000);
                
                // 更新状态
                set((state) => {
                  state.isAuthenticated = true;
                  state.user = authResponse.user;
                  state.accessToken = authResponse.accessToken;
                  state.tokenExpiry = tokenExpiry;
                  state.lastActivity = Date.now();
                  state.isLoading = false;
                  state.error = null;
                });
                
              } catch (error: any) {
                set((state) => {
                  state.isLoading = false;
                  state.error = error.message || '注册失败';
                });
                
                throw error;
              }
            },

            /**
             * 用户登出
             * 功能描述：处理用户登出流程，清理认证状态并跳转到登录页
             * 采用技术：状态清理，令牌移除，事件通知，页面导航
             * 技术优势：完整的登出流程，状态清理彻底，用户体验优化
             */
            logout: async () => {
              set((state) => {
                state.isLoading = true;
              });

              try {
                // 调用登出API
                await logoutApi();
              } catch (error) {
                console.error('Logout API failed:', error);
                // 即使API失败，也要清理本地状态
              } finally {
                // 清理令牌
                removeToken();
                removeRefreshToken();
                
                // 重置状态
                set((state) => {
                  Object.assign(state, initialState);
                });
                
                // 发送登出事件
                window.dispatchEvent(new CustomEvent(AUTH_EVENTS.LOGOUT));
                
                // 跳转到登录页面
                window.location.href = '/login';
              }
            },

            /**
             * 刷新访问令牌
             * 功能描述：刷新访问令牌，延长用户会话
             * 采用技术：令牌刷新，状态更新，错误处理
             * 技术优势：无感知的令牌刷新，会话延长
             */
            refreshToken: async () => {
              const refreshToken = getRefreshToken();
              
              if (!refreshToken) {
                throw new Error('No refresh token available');
              }

              try {
                // 调用刷新令牌API
                const authResponse: AuthResponse = await refreshTokenApi(refreshToken);
                
                // 存储新令牌
                setToken(authResponse.accessToken);
                setRefreshToken(authResponse.refreshToken);
                
                // 计算令牌过期时间
                const tokenExpiry = Date.now() + (authResponse.expiresIn * 1000);
                
                // 更新状态
                set((state) => {
                  state.accessToken = authResponse.accessToken;
                  state.tokenExpiry = tokenExpiry;
                  state.lastActivity = Date.now();
                });
                
                // 发送令牌刷新事件
                window.dispatchEvent(new CustomEvent(AUTH_EVENTS.TOKEN_REFRESHED));
                
              } catch (error: any) {
                // 刷新失败，清理状态
                removeToken();
                removeRefreshToken();
                
                set((state) => {
                  Object.assign(state, initialState);
                });
                
                // 发送令牌过期事件
                window.dispatchEvent(new CustomEvent(AUTH_EVENTS.TOKEN_EXPIRED));
                
                throw error;
              }
            },

            /**
             * 检查令牌有效性
             * 功能描述：验证当前令牌是否有效
             * 采用技术：令牌验证，状态同步
             * 技术优势：实时的令牌状态检查
             */
            checkTokenValidity: async (): Promise<boolean> => {
              const token = getToken();
              
              if (!token) {
                return false;
              }
              
              // 检查令牌是否过期
              const state = get();
              if (isTokenExpired(state.tokenExpiry)) {
                try {
                  // 尝试刷新令牌
                  await get().refreshToken();
                  return true;
                } catch (error) {
                  return false;
                }
              }
              
              // 验证令牌有效性
              try {
                const isValid = await verifyToken();
                
                if (!isValid) {
                  // 令牌无效，清理状态
                  get().reset();
                }
                
                return isValid;
              } catch (error) {
                return false;
              }
            },

            /**
             * 获取用户信息
             * 功能描述：从服务器获取最新的用户信息
             * 采用技术：API调用，状态更新，错误处理
             * 技术优势：实时的用户信息同步
             */
            fetchUserInfo: async () => {
              try {
                const userInfo = await getCurrentUser();
                
                set((state) => {
                  state.user = userInfo;
                });
                
                // 发送用户信息更新事件
                window.dispatchEvent(new CustomEvent(AUTH_EVENTS.USER_UPDATED, {
                  detail: { user: userInfo }
                }));
                
              } catch (error: any) {
                set((state) => {
                  state.error = error.message || '获取用户信息失败';
                });
                
                throw error;
              }
            },

            /**
             * 更新用户信息
             * 功能描述：更新本地用户信息状态
             * 采用技术：状态更新，部分更新支持
             * 技术优势：灵活的用户信息更新
             */
            updateUserInfo: (userInfo: Partial<UserInfo>) => {
              set((state) => {
                if (state.user) {
                  Object.assign(state.user, userInfo);
                }
              });
            },

            /**
             * 修改密码
             * 功能描述：处理用户密码修改
             * 采用技术：API调用，状态管理，错误处理
             * 技术优势：安全的密码修改流程
             */
            changePassword: async (changePasswordData: ChangePasswordRequest) => {
              set((state) => {
                state.isLoading = true;
                state.error = null;
              });

              try {
                await changePasswordApi(changePasswordData);
                
                set((state) => {
                  state.isLoading = false;
                });
                
              } catch (error: any) {
                set((state) => {
                  state.isLoading = false;
                  state.error = error.message || '密码修改失败';
                });
                
                throw error;
              }
            },

            /**
             * 忘记密码
             * 功能描述：处理忘记密码请求
             * 采用技术：API调用，状态管理，错误处理
             * 技术优势：安全的密码重置流程
             */
            forgotPassword: async (forgotPasswordData: ForgotPasswordRequest) => {
              set((state) => {
                state.isLoading = true;
                state.error = null;
              });

              try {
                await forgotPasswordApi(forgotPasswordData);
                
                set((state) => {
                  state.isLoading = false;
                });
                
              } catch (error: any) {
                set((state) => {
                  state.isLoading = false;
                  state.error = error.message || '发送重置邮件失败';
                });
                
                throw error;
              }
            },

            /**
             * 重置密码
             * 功能描述：处理密码重置
             * 采用技术：API调用，状态管理，错误处理
             * 技术优势：安全的密码重置流程
             */
            resetPassword: async (resetPasswordData: ResetPasswordRequest) => {
              set((state) => {
                state.isLoading = true;
                state.error = null;
              });

              try {
                await resetPasswordApi(resetPasswordData);
                
                set((state) => {
                  state.isLoading = false;
                });
                
              } catch (error: any) {
                set((state) => {
                  state.isLoading = false;
                  state.error = error.message || '密码重置失败';
                });
                
                throw error;
              }
            },

            /**
             * 获取用户会话列表
             * 功能描述：获取用户所有活跃会话
             * 采用技术：API调用，会话管理
             * 技术优势：多设备会话管理
             */
            fetchSessions: async (): Promise<SessionInfo[]> => {
              try {
                return await getUserSessions();
              } catch (error: any) {
                set((state) => {
                  state.error = error.message || '获取会话列表失败';
                });
                
                throw error;
              }
            },

            /**
             * 终止指定会话
             * 功能描述：终止用户指定的会话
             * 采用技术：API调用，会话管理
             * 技术优势：精确的会话控制
             */
            terminateSession: async (sessionId: string) => {
              try {
                await terminateSession(sessionId);
              } catch (error: any) {
                set((state) => {
                  state.error = error.message || '终止会话失败';
                });
                
                throw error;
              }
            },

            /**
             * 终止所有其他会话
             * 功能描述：终止除当前会话外的所有其他会话
             * 采用技术：批量会话管理，当前会话保护
             * 技术优势：安全的会话清理
             */
            terminateAllOtherSessions: async () => {
              try {
                await terminateAllOtherSessions();
              } catch (error: any) {
                set((state) => {
                  state.error = error.message || '终止其他会话失败';
                });
                
                throw error;
              }
            },

            /**
             * 设置加载状态
             * 功能描述：更新加载状态
             * 采用技术：状态更新
             * 技术优势：统一的加载状态管理
             */
            setLoading: (loading: boolean) => {
              set((state) => {
                state.isLoading = loading;
              });
            },

            /**
             * 设置错误信息
             * 功能描述：更新错误状态
             * 采用技术：状态更新
             * 技术优势：统一的错误状态管理
             */
            setError: (error: string | null) => {
              set((state) => {
                state.error = error;
              });
            },

            /**
             * 清除错误信息
             * 功能描述：清除当前错误状态
             * 采用技术：状态重置
             * 技术优势：便捷的错误清理
             */
            clearError: () => {
              set((state) => {
                state.error = null;
              });
            },

            /**
             * 更新最后活动时间
             * 功能描述：更新用户最后活动时间
             * 采用技术：时间戳更新
             * 技术优势：会话超时管理
             */
            updateLastActivity: () => {
              set((state) => {
                state.lastActivity = Date.now();
              });
            },

            /**
             * 初始化认证状态
             * 功能描述：应用启动时初始化认证状态，确保刷新页面后认证状态正确恢复
             * 采用技术：令牌检查，状态恢复，健壮错误处理，降级缓存机制
             * 技术优势：自动的状态恢复，无感知的会话延续，完善的降级策略
             */
            initialize: async () => {
              try {
                const token = getToken();
                
                if (!token) {
                  set((state) => {
                    state.isLoading = false;
                    state.isAuthenticated = false;
                  });
                  return;
                }
                
                // 从本地存储恢复用户信息
                const storedUser = localStorage.getItem('user');
                let userFromStorage = null;
                if (storedUser) {
                  try {
                    userFromStorage = JSON.parse(storedUser);
                  } catch (e) {
                    // 忽略JSON解析错误
                  }
                }
                
                // 从token中尝试解析用户信息作为后备
                let userFromToken = null;
                try {
                  const tokenPayload = parseJwtPayload(token);
                  if (tokenPayload) {
                    userFromToken = extractUserFromToken(token); // 传入完整token
                  }
                } catch (e) {
                  // 忽略token解析错误
                }
                
                // 以当前持久化状态为主；仅在缺失时用本地缓存或token信息填充，避免刷新时覆盖已有用户信息
                const currentUser = get().user;
                const initialUser = currentUser || userFromStorage || userFromToken;

                set((state) => {
                  state.isAuthenticated = true;
                  state.accessToken = token;
                  if (!state.user && initialUser) {
                    state.user = initialUser;
                  }
                  state.lastActivity = Date.now();
                  state.isLoading = true;
                  state.error = null;
                });
                
                // 尝试验证token并获取最新用户信息
                try {
                  const userInfo = await getCurrentUser();
                  
                  // 更新用户信息并缓存
                  const existingUser = get().user as any;
                  const sanitizedServer = Object.fromEntries(Object.entries(userInfo as any).filter(([_, v]) => v !== undefined && v !== null));
                  const mergedUser = existingUser ? { ...existingUser, ...sanitizedServer } : userInfo;

                  set((state) => {
                    state.user = mergedUser as any;
                    state.isLoading = false;
                    state.error = null;
                  });
                  
                  localStorage.setItem('user', JSON.stringify(mergedUser));
                  
                } catch (userInfoError: any) {
                  
                  // 根据错误类型决定处理策略
                  if (userInfoError.status === 401 || userInfoError.message?.includes('未授权')) {
                    get().reset();
                  } else {
                    set((state) => {
                      state.isLoading = false;
                      state.error = `${userInfoError.message || '网络错误'}，正在使用缓存数据`;
                    });
                    window.dispatchEvent(new CustomEvent('auth:fallback:mode', {
                      detail: { error: userInfoError.message }
                    }));
                  }
                }
                
              } catch (error) {
                get().reset();
              }
            },

            /**
             * 重置认证状态
             * 功能描述：重置所有认证状态到初始值
             * 采用技术：状态重置，令牌清理
             * 技术优势：彻底的状态清理
             */
            reset: () => {
              removeToken();
              removeRefreshToken();
              
              set((state) => {
                Object.assign(state, initialState);
              });
            }
          }))
        ),
        {
          name: STORAGE_KEYS.AUTH_STATE,
          partialize: (state) => ({
            isAuthenticated: state.isAuthenticated,
            user: state.user,
            lastActivity: state.lastActivity,
            tokenExpiry: state.tokenExpiry
          })
        }
      ),
      {
        name: 'auth-store'
      }
    )
  );

/**
 * 会话超时检查Hook
 * 功能描述：定期检查会话是否超时
 * 采用技术：定时器，状态监听，自动登出
 * 技术优势：自动的会话管理，防止无效会话
 */
export const useSessionTimeout = () => {
  const { isAuthenticated, lastActivity, logout, updateLastActivity } = useAuthStore();
  
  React.useEffect(() => {
    if (!isAuthenticated) {
      return;
    }
    
    const checkSessionTimeout = () => {
      const now = Date.now();
      const timeSinceLastActivity = now - lastActivity;
      
      if (timeSinceLastActivity > SESSION_CONFIG.SESSION_TIMEOUT) {
        // 会话超时，自动登出
        logout();
        
        // 发送会话超时事件
        window.dispatchEvent(new CustomEvent(AUTH_EVENTS.SESSION_TIMEOUT));
      }
    };
    
    // 定期检查会话超时
    const interval = setInterval(checkSessionTimeout, SESSION_CONFIG.ACTIVITY_CHECK_INTERVAL);
    
    // 监听用户活动
    const handleUserActivity = () => {
      updateLastActivity();
    };
    
    // 添加活动监听器
    const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart'];
    events.forEach(event => {
      document.addEventListener(event, handleUserActivity, true);
    });
    
    return () => {
      clearInterval(interval);
      events.forEach(event => {
        document.removeEventListener(event, handleUserActivity, true);
      });
    };
  }, [isAuthenticated, lastActivity, logout, updateLastActivity]);
};

/**
 * 令牌自动刷新Hook
 * 功能描述：自动刷新即将过期的令牌
 * 采用技术：定时器，令牌检查，自动刷新
 * 技术优势：无感知的令牌刷新，会话延续
 */
export const useTokenRefresh = () => {
  const { isAuthenticated, tokenExpiry, refreshToken } = useAuthStore();
  
  React.useEffect(() => {
    if (!isAuthenticated || !tokenExpiry) {
      return;
    }
    
    const checkTokenExpiry = async () => {
      const now = Date.now();
      const timeUntilExpiry = tokenExpiry - now;
      
      // 如果令牌即将在阈值时间内过期，刷新令牌
      if (timeUntilExpiry <= 60000 && timeUntilExpiry > 0) { // 每分钟检查一次
        try {
          await refreshToken();
        } catch (error) {
          // 自动令牌刷新失败
        }
      }
    };
    
    // 定期检查令牌过期时间
    const interval = setInterval(checkTokenExpiry, 60000); // 每分钟检查一次
    
    return () => {
      clearInterval(interval);
    };
  }, [isAuthenticated, tokenExpiry, refreshToken]);
};

/**
 * 认证初始化Hook
 * 功能描述：应用启动时初始化认证状态
 * 采用技术：Effect Hook，状态初始化
 * 技术优势：自动的认证状态恢复
 */
export const useAuthInitialization = () => {
  const initialize = useAuthStore(state => state.initialize);
  
  React.useEffect(() => {
    initialize();
  }, [initialize]);
};

// 导入React用于Hook
import React from 'react';

export default useAuthStore;