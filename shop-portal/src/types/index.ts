/**
 * 类型定义入口文件
 */

// 导出所有类型定义
export * from './api';
export * from './user';
export * from './common';

// 重新导出常用类型
export type {
  ApiResponse,
  PaginatedResponse,
  ApiError,
  ApiRequestConfig
} from './api';

export type {
  User,
  UserProfileDTO,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  UpdateUserSettingsRequest,
  ChangePasswordRequest
} from './user';

export type {
  ID,
  Optional,
  RequiredFields,
  DeepPartial,
  Option,
  QueryParams,
  ResponseStatus,
  LoadingState
} from './common';