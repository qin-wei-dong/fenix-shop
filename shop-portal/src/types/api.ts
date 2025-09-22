/**
 * API相关类型定义
 */

// HTTP方法
export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';

// API请求配置
export interface ApiRequestConfig {
  url: string;
  method: HttpMethod;
  headers?: Record<string, string>;
  params?: Record<string, any>;
  data?: any;
  timeout?: number;
  withCredentials?: boolean;
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
  path?: string;
}

// 分页请求参数
export interface PaginationParams {
  page: number;
  size: number;
  sort?: string;
  direction?: 'asc' | 'desc';
}

// 分页响应数据
export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

// API错误类型
export interface ApiError {
  code: string;
  message: string;
  details?: any;
  timestamp: string;
  path: string;
  status: number;
}

// 请求状态
export enum RequestStatus {
  IDLE = 'idle',
  LOADING = 'loading',
  SUCCESS = 'success',
  ERROR = 'error'
}

// 异步操作状态
export interface AsyncState<T = any> {
  data: T | null;
  status: RequestStatus;
  error: string | null;
  lastUpdated?: number;
}

// API端点配置
export interface ApiEndpoint {
  path: string;
  method: HttpMethod;
  requiresAuth?: boolean;
  timeout?: number;
}

// API端点集合
export interface ApiEndpoints {
  [key: string]: ApiEndpoint;
}

// 请求拦截器
export type RequestInterceptor = (config: ApiRequestConfig) => ApiRequestConfig | Promise<ApiRequestConfig>;

// 响应拦截器
export type ResponseInterceptor<T = any> = (response: ApiResponse<T>) => ApiResponse<T> | Promise<ApiResponse<T>>;

// 错误拦截器
export type ErrorInterceptor = (error: ApiError) => ApiError | Promise<ApiError>;

// API客户端配置
export interface ApiClientConfig {
  baseURL: string;
  timeout: number;
  headers: Record<string, string>;
  requestInterceptors: RequestInterceptor[];
  responseInterceptors: ResponseInterceptor[];
  errorInterceptors: ErrorInterceptor[];
}

// 文件上传配置
export interface FileUploadConfig {
  url: string;
  field: string;
  maxSize: number;
  allowedTypes: string[];
  multiple?: boolean;
}

// 文件上传进度
export interface UploadProgress {
  loaded: number;
  total: number;
  percentage: number;
}

// 文件上传响应
export interface FileUploadResponse {
  url: string;
  filename: string;
  size: number;
  type: string;
  thumbnailUrl?: string;
}

// 缓存配置
export interface CacheConfig {
  ttl: number; // 生存时间（毫秒）
  maxSize: number; // 最大缓存条目数
  strategy: 'lru' | 'fifo' | 'ttl';
}

// 缓存条目
export interface CacheEntry<T> {
  data: T;
  timestamp: number;
  ttl: number;
  key: string;
}

// 重试配置
export interface RetryConfig {
  maxAttempts: number;
  delay: number;
  backoff: 'linear' | 'exponential';
  retryCondition: (error: ApiError) => boolean;
}

// 请求队列项
export interface QueueItem {
  id: string;
  config: ApiRequestConfig;
  resolve: (value: any) => void;
  reject: (reason: any) => void;
  retryCount: number;
  timestamp: number;
}

// API统计信息
export interface ApiStats {
  totalRequests: number;
  successfulRequests: number;
  failedRequests: number;
  averageResponseTime: number;
  cacheHitRate: number;
  lastRequestTime?: number;
}

// WebSocket消息类型
export interface WebSocketMessage<T = any> {
  type: string;
  data: T;
  timestamp: number;
  id?: string;
}

// WebSocket连接状态
export enum WebSocketStatus {
  CONNECTING = 'connecting',
  CONNECTED = 'connected',
  DISCONNECTED = 'disconnected',
  ERROR = 'error',
  RECONNECTING = 'reconnecting'
}

// WebSocket配置
export interface WebSocketConfig {
  url: string;
  protocols?: string[];
  reconnect: boolean;
  reconnectInterval: number;
  maxReconnectAttempts: number;
  heartbeatInterval?: number;
}

// 实时通知
export interface RealtimeNotification {
  id: string;
  type: 'info' | 'success' | 'warning' | 'error';
  title: string;
  message: string;
  timestamp: number;
  read: boolean;
  actions?: NotificationAction[];
}

// 通知操作
export interface NotificationAction {
  label: string;
  action: string;
  style?: 'primary' | 'secondary' | 'danger';
}

// 服务器发送事件
export interface ServerSentEvent<T = any> {
  id?: string;
  event?: string;
  data: T;
  retry?: number;
}

// 健康检查响应
export interface HealthCheckResponse {
  status: 'healthy' | 'unhealthy' | 'degraded';
  timestamp: number;
  services: ServiceHealth[];
  version: string;
  uptime: number;
}

// 服务健康状态
export interface ServiceHealth {
  name: string;
  status: 'healthy' | 'unhealthy';
  responseTime?: number;
  lastCheck: number;
  details?: Record<string, any>;
}

// API版本信息
export interface ApiVersion {
  version: string;
  buildTime: string;
  gitCommit: string;
  environment: string;
  features: string[];
}

// 限流信息
export interface RateLimitInfo {
  limit: number;
  remaining: number;
  reset: number;
  retryAfter?: number;
}

// 地理位置信息
export interface GeoLocation {
  latitude: number;
  longitude: number;
  accuracy?: number;
  altitude?: number;
  altitudeAccuracy?: number;
  heading?: number;
  speed?: number;
}

// IP信息
export interface IpInfo {
  ip: string;
  country: string;
  region: string;
  city: string;
  timezone: string;
  isp: string;
  location: GeoLocation;
}