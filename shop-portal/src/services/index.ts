/**
 * API 服务入口文件
 */

// 导出所有服务
export { UserService } from './userService';
export { get, post, put, patch, del as delete, uploadFile, downloadFile } from '../utils/http';

// 重新导出常用服务方法
export {
  UserService as default
} from './userService';

// 导出服务类型
export type {
  // 用户相关类型已在 types/index.ts 中导出
} from '../types';