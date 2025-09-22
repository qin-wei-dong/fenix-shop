/**
 * HTTP客户端工具函数库
 * 功能描述：提供统一的HTTP请求处理，包括请求拦截、响应处理、错误处理、认证管理等
 * 采用技术：Axios HTTP客户端，拦截器模式，Promise异步处理，TypeScript类型安全
 * 技术优势：统一的API调用接口，自动化的认证处理，完善的错误处理机制
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */

import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { 
  SESSION_CONFIG, 
  AUTH_ENDPOINTS, 
  HTTP_STATUS, 
  ERROR_MESSAGES 
} from '../constants/auth';
import { 
  getToken, 
  removeToken, 
  refreshTokenIfNeeded,
  getRefreshToken,
  setToken,
  setRefreshToken,
  removeRefreshToken
} from './tokenStorage';
import type { AuthResponse } from '../types/auth';
import { 
  shouldRefreshToken,
  parseJwtPayload 
} from './auth';

/**
 * HTTP响应数据接口
 * 功能描述：定义标准的API响应数据结构
 * 采用技术：TypeScript泛型，接口定义
 * 技术优势：类型安全的响应数据处理
 */
export interface ApiResponse<T = any> {
  /** 响应状态码 */
  code: number;
  /** 响应消息 */
  message: string;
  /** 响应数据 */
  data: T;
  /** 响应时间戳 */
  timestamp: number;
  /** 请求追踪ID */
  traceId?: string;
}

/**
 * HTTP错误响应接口
 * 功能描述：定义标准的API错误响应结构
 * 采用技术：TypeScript接口，错误类型定义
 * 技术优势：统一的错误处理格式
 */
export interface ApiError {
  /** 错误状态码 */
  code: number;
  /** 错误消息 */
  message: string;
  /** 详细错误信息 */
  details?: any;
  /** 错误时间戳 */
  timestamp: number;
  /** 请求路径 */
  path?: string;
}

/**
 * 请求配置接口
 * 功能描述：扩展Axios请求配置，添加自定义选项
 * 采用技术：TypeScript接口继承，可选属性
 * 技术优势：灵活的请求配置，支持自定义行为
 */
export interface RequestConfig extends AxiosRequestConfig {
  /** 是否跳过认证 */
  skipAuth?: boolean;
  /** 是否跳过错误处理 */
  skipErrorHandling?: boolean;
  /** 是否显示加载状态 */
  showLoading?: boolean;
  /** 重试次数 */
  retryCount?: number;
  /** 重试延迟（毫秒） */
  retryDelay?: number;
}

/**
 * 创建Axios实例
 * 功能描述：创建配置好的HTTP客户端实例
 * 采用技术：Axios实例化，基础配置，超时设置
 * 技术优势：统一的请求配置，便于全局管理
 */
const createHttpClient = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8090',
    timeout: SESSION_CONFIG.SESSION_TIMEOUT,
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  });

  return instance;
};

/**
 * HTTP客户端实例
 * 功能描述：全局HTTP客户端实例
 * 采用技术：单例模式，Axios实例
 * 技术优势：统一的HTTP客户端，避免重复创建
 */
export const httpClient = createHttpClient();

/**
 * 请求拦截器
 * 功能描述：在请求发送前进行预处理，添加认证头、请求ID等
 * 采用技术：Axios请求拦截器，Promise处理，认证管理
 * 技术优势：自动化的请求预处理，统一的认证机制
 */
httpClient.interceptors.request.use(
  async (config: any) => {
    // 生成请求ID用于追踪
    const requestId = `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    config.headers['X-Request-ID'] = requestId;
    
    // 添加时间戳
    config.headers['X-Timestamp'] = Date.now().toString();
    
    // 如果不跳过认证，添加认证头
    if (!config.skipAuth) {
      const token = getToken();
      
      if (token) {
        // 检查token是否即将过期，如果是则尝试刷新
        const payload = parseJwtPayload(token);
        const tokenExpiry = payload?.exp ? payload.exp * 1000 : null;
        if (shouldRefreshToken(tokenExpiry)) {
          try {
            // 判断是否需要刷新并执行真正的刷新流程
            const needRefresh = await refreshTokenIfNeeded(tokenExpiry);
            if (needRefresh) {
              const refreshToken = getRefreshToken();
              if (refreshToken) {
                // 使用当前模块的post方法并设置 skipAuth 避免递归拦截
                const refreshResp = await post<AuthResponse>(
                  AUTH_ENDPOINTS.REFRESH_TOKEN,
                  { refreshToken },
                  { skipAuth: true }
                );
                const auth = refreshResp.data;
                if (auth?.accessToken) {
                  setToken(auth.accessToken);
                  if (auth.refreshToken) {
                    setRefreshToken(auth.refreshToken);
                  }
                  config.headers.Authorization = `Bearer ${auth.accessToken}`;
                } else {
                  config.headers.Authorization = `Bearer ${token}`;
                }
              } else {
                // 无刷新令牌，降级为使用现有token
                config.headers.Authorization = `Bearer ${token}`;
              }
            } else {
              // 不需要刷新，直接使用现有token
              config.headers.Authorization = `Bearer ${token}`;
            }
          } catch (error) {
            console.warn('Token refresh failed:', error);
            // 刷新失败，清理本地状态并跳转登录
            removeToken();
            removeRefreshToken();
            config.headers.Authorization = undefined;
            if (typeof window !== 'undefined') {
              window.location.href = '/login';
            }
          }
        } else {
          config.headers.Authorization = `Bearer ${token}`;
        }
      }
    }
    
    // 记录请求日志（开发环境）
    if (process.env.NODE_ENV === 'development') {
      console.log(`[HTTP Request] ${config.method?.toUpperCase()} ${config.url}`, {
        requestId,
        headers: config.headers,
        data: config.data
      });
    }
    
    return config;
  },
  (error: AxiosError) => {
    console.error('[HTTP Request Error]', error);
    return Promise.reject(error);
  }
);

/**
 * 响应拦截器
 * 功能描述：在响应返回后进行后处理，统一错误处理、数据格式化等
 * 采用技术：Axios响应拦截器，错误分类处理，状态码判断
 * 技术优势：统一的响应处理，自动化的错误处理
 */
httpClient.interceptors.response.use(
  (response: AxiosResponse) => {
    const requestId = response.config.headers['X-Request-ID'];
    
    // 记录响应日志（开发环境）
    if (process.env.NODE_ENV === 'development') {
      console.log(`[HTTP Response] ${response.status} ${response.config.url}`, {
        requestId,
        data: response.data
      });
    }
    
    // 检查业务状态码
    if (response.data && typeof response.data === 'object') {
      const apiResponse = response.data as ApiResponse;
      
      // 如果业务状态码表示失败
        if (apiResponse.code && apiResponse.code !== HTTP_STATUS.OK) {
        const error: ApiError = {
          code: apiResponse.code,
          message: apiResponse.message || '请求失败',
          timestamp: Date.now(),
          path: response.config.url
        };
        
        return Promise.reject(error);
      }
    }
    
    return response;
  },
  async (error: AxiosError) => {
    const requestId = error.config?.headers?.['X-Request-ID'];
    
    // 记录错误日志
    console.error(`[HTTP Response Error] ${error.response?.status || 'Network'} ${error.config?.url}`, {
      requestId,
      error: error.message,
      response: error.response?.data
    });
    
    // 处理不同类型的错误
    if (error.response) {
      const status = error.response.status;
      const responseData = error.response.data as any;
      
      // 构造标准错误对象
      const apiError: ApiError = {
        code: responseData?.code || status,
        message: responseData?.message || getErrorMessage(status),
        details: responseData?.details,
        timestamp: Date.now(),
        path: error.config?.url
      };
      
      // 处理认证相关错误
      if (status === HTTP_STATUS.UNAUTHORIZED) {
        // 清除无效token
        removeToken();
        
        // 如果不是登录请求，重定向到登录页
        if (!error.config?.url?.includes(AUTH_ENDPOINTS.LOGIN)) {
          window.location.href = '/login';
        }
      }
      
      // 处理权限错误
      if (status === HTTP_STATUS.FORBIDDEN) {
        // 可以显示权限不足的提示
        console.warn('Access forbidden:', apiError.message);
      }
      
      // 处理服务器错误
      if (status >= HTTP_STATUS.INTERNAL_SERVER_ERROR) {
        // 可以显示服务器错误的提示
        console.error('Server error:', apiError.message);
      }
      
      return Promise.reject(apiError);
    }
    
    // 处理网络错误
    if (error.request) {
      const networkError: ApiError = {
        code: 0,
        message: ERROR_MESSAGES.NETWORK_ERROR,
        timestamp: Date.now(),
        path: error.config?.url
      };
      
      return Promise.reject(networkError);
    }
    
    // 处理其他错误
    const unknownError: ApiError = {
      code: -1,
      message: error.message || ERROR_MESSAGES.UNKNOWN_ERROR,
      timestamp: Date.now()
    };
    
    return Promise.reject(unknownError);
  }
);

/**
 * 根据HTTP状态码获取错误消息
 * 功能描述：将HTTP状态码转换为用户友好的错误消息
 * 采用技术：状态码映射，消息本地化
 * 技术优势：用户友好的错误提示，支持国际化
 * 
 * @param status HTTP状态码
 * @returns 错误消息
 */
const getErrorMessage = (status: number): string => {
  switch (status) {
    case HTTP_STATUS.BAD_REQUEST:
      return '请求参数错误';
    case HTTP_STATUS.UNAUTHORIZED:
      return ERROR_MESSAGES.AUTH_FAILED;
    case HTTP_STATUS.FORBIDDEN:
      return ERROR_MESSAGES.INSUFFICIENT_PERMISSIONS;
    case HTTP_STATUS.NOT_FOUND:
      return '请求的资源不存在';
    case HTTP_STATUS.CONFLICT:
      return '数据冲突，请刷新后重试';
    case HTTP_STATUS.INTERNAL_SERVER_ERROR:
      return ERROR_MESSAGES.SERVER_ERROR;
    case HTTP_STATUS.SERVICE_UNAVAILABLE:
      return ERROR_MESSAGES.SERVER_ERROR;
    default:
      return ERROR_MESSAGES.UNKNOWN_ERROR;
  }
};

/**
 * 发送GET请求
 * 功能描述：发送HTTP GET请求的便捷方法
 * 采用技术：Axios GET请求，泛型支持，配置合并
 * 技术优势：类型安全的GET请求，简化的调用接口
 * 
 * @param url 请求URL
 * @param config 请求配置
 * @returns Promise<响应数据>
 */
export const get = <T = any>(
  url: string, 
  config?: RequestConfig
): Promise<ApiResponse<T>> => {
  return httpClient.get(url, config).then(response => response.data);
};

/**
 * 发送POST请求
 * 功能描述：发送HTTP POST请求的便捷方法
 * 采用技术：Axios POST请求，泛型支持，数据序列化
 * 技术优势：类型安全的POST请求，自动数据处理
 * 
 * @param url 请求URL
 * @param data 请求数据
 * @param config 请求配置
 * @returns Promise<响应数据>
 */
export const post = <T = any>(
  url: string, 
  data?: any, 
  config?: RequestConfig
): Promise<ApiResponse<T>> => {
  return httpClient.post(url, data, config).then(response => response.data);
};

/**
 * 发送PUT请求
 * 功能描述：发送HTTP PUT请求的便捷方法
 * 采用技术：Axios PUT请求，泛型支持，数据更新
 * 技术优势：类型安全的PUT请求，支持资源更新
 * 
 * @param url 请求URL
 * @param data 请求数据
 * @param config 请求配置
 * @returns Promise<响应数据>
 */
export const put = <T = any>(
  url: string, 
  data?: any, 
  config?: RequestConfig
): Promise<ApiResponse<T>> => {
  return httpClient.put(url, data, config).then(response => response.data);
};

/**
 * 发送PATCH请求
 * 功能描述：发送HTTP PATCH请求的便捷方法
 * 采用技术：Axios PATCH请求，泛型支持，部分更新
 * 技术优势：类型安全的PATCH请求，支持部分资源更新
 * 
 * @param url 请求URL
 * @param data 请求数据
 * @param config 请求配置
 * @returns Promise<响应数据>
 */
export const patch = <T = any>(
  url: string, 
  data?: any, 
  config?: RequestConfig
): Promise<ApiResponse<T>> => {
  return httpClient.patch(url, data, config).then(response => response.data);
};

/**
 * 发送DELETE请求
 * 功能描述：发送HTTP DELETE请求的便捷方法
 * 采用技术：Axios DELETE请求，泛型支持，资源删除
 * 技术优势：类型安全的DELETE请求，支持资源删除
 * 
 * @param url 请求URL
 * @param config 请求配置
 * @returns Promise<响应数据>
 */
export const del = <T = any>(
  url: string, 
  config?: RequestConfig
): Promise<ApiResponse<T>> => {
  return httpClient.delete(url, config).then(response => response.data);
};

/**
 * 上传文件
 * 功能描述：上传文件的便捷方法
 * 采用技术：FormData，文件上传，进度监控
 * 技术优势：支持文件上传，可监控上传进度
 * 
 * @param url 上传URL
 * @param file 文件对象
 * @param onProgress 进度回调
 * @param config 请求配置
 * @returns Promise<响应数据>
 */
export const uploadFile = <T = any>(
  url: string,
  file: File,
  onProgress?: (progress: number) => void,
  config?: RequestConfig
): Promise<ApiResponse<T>> => {
  const formData = new FormData();
  formData.append('file', file);
  
  const uploadConfig: RequestConfig = {
    ...config,
    headers: {
      ...config?.headers,
      'Content-Type': 'multipart/form-data',
    },
    onUploadProgress: (progressEvent) => {
      if (onProgress && progressEvent.total) {
        const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        onProgress(progress);
      }
    },
  };
  
  return httpClient.post(url, formData, uploadConfig).then(response => response.data);
};

/**
 * 下载文件
 * 功能描述：下载文件的便捷方法
 * 采用技术：Blob响应，文件下载，浏览器API
 * 技术优势：支持文件下载，自动处理文件类型
 * 
 * @param url 下载URL
 * @param filename 文件名
 * @param config 请求配置
 * @returns Promise<void>
 */
export const downloadFile = async (
  url: string,
  filename?: string,
  config?: RequestConfig
): Promise<void> => {
  const downloadConfig: RequestConfig = {
    ...config,
    responseType: 'blob',
  };
  
  const response = await httpClient.get(url, downloadConfig);
  
  // 创建下载链接
  const blob = new Blob([response.data]);
  const downloadUrl = window.URL.createObjectURL(blob);
  
  // 创建临时链接并触发下载
  const link = document.createElement('a');
  link.href = downloadUrl;
  link.download = filename || 'download';
  document.body.appendChild(link);
  link.click();
  
  // 清理
  document.body.removeChild(link);
  window.URL.revokeObjectURL(downloadUrl);
};

/**
 * 重试请求
 * 功能描述：带重试机制的请求方法
 * 采用技术：递归重试，指数退避，错误处理
 * 技术优势：提高请求成功率，处理网络不稳定情况
 * 
 * @param requestFn 请求函数
 * @param maxRetries 最大重试次数
 * @param delay 重试延迟
 * @returns Promise<响应数据>
 */
export const retryRequest = async <T>(
  requestFn: () => Promise<T>,
  maxRetries: number = 3,
  delay: number = 1000
): Promise<T> => {
  let lastError: any;
  
  for (let i = 0; i <= maxRetries; i++) {
    try {
      return await requestFn();
    } catch (error) {
      lastError = error;
      
      // 如果是最后一次重试，直接抛出错误
      if (i === maxRetries) {
        throw error;
      }
      
      // 等待后重试（指数退避）
      await new Promise(resolve => setTimeout(resolve, delay * Math.pow(2, i)));
    }
  }
  
  throw lastError;
};

/**
 * 并发请求
 * 功能描述：并发执行多个请求
 * 采用技术：Promise.all，并发控制，错误聚合
 * 技术优势：提高请求效率，支持批量操作
 * 
 * @param requests 请求数组
 * @returns Promise<响应数组>
 */
export const concurrentRequests = async <T>(
  requests: (() => Promise<T>)[]
): Promise<T[]> => {
  return Promise.all(requests.map(request => request()));
};

/**
 * 取消请求的控制器
 * 功能描述：创建可取消的请求控制器
 * 采用技术：AbortController，请求取消，资源管理
 * 技术优势：支持请求取消，避免内存泄漏
 */
export class RequestController {
  private abortController: AbortController;
  
  constructor() {
    this.abortController = new AbortController();
  }
  
  /**
   * 获取取消信号
   * 功能描述：获取用于取消请求的信号
   * 采用技术：AbortController API
   * 技术优势：标准的请求取消机制
   * 
   * @returns 取消信号
   */
  getSignal(): AbortSignal {
    return this.abortController.signal;
  }
  
  /**
   * 取消请求
   * 功能描述：取消所有使用此控制器的请求
   * 采用技术：AbortController.abort()
   * 技术优势：统一的请求取消管理
   */
  cancel(): void {
    this.abortController.abort();
  }
  
  /**
   * 重置控制器
   * 功能描述：重置控制器以便重新使用
   * 采用技术：重新创建AbortController
   * 技术优势：支持控制器复用
   */
  reset(): void {
    this.abortController = new AbortController();
  }
}

/**
 * 默认导出HTTP工具对象
 * 功能描述：提供统一的HTTP工具接口
 * 采用技术：对象导出，方法聚合
 * 技术优势：便于使用的API接口，支持按需导入
 */
export default {
  get,
  post,
  put,
  patch,
  delete: del,
  uploadFile,
  downloadFile,
  retryRequest,
  concurrentRequests,
  RequestController,
  httpClient
};