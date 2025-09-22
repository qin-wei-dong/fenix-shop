/**
 * 用户相关API服务
 */

import { get, put } from '../utils/http';
import { uploadFile } from '../utils/http';

/**
 * 用户个人信息接口
 */
export interface UserProfile {
  userId: string;
  username: string;
  email?: string;
  mobile?: string;
  avatarUrl?: string;
  userLevel?: number;
  points?: number;
  registerTime?: string;
  lastLoginTime?: string;
  isActive?: boolean;
  notificationSettings?: string;
  preferences?: string;
}

/**
 * 更新个人信息请求接口
 */
export interface UpdateProfileRequest {
  email?: string;
  mobile?: string;
  avatarUrl?: string;
  notificationSettings?: string;
  preferences?: string;
}

/**
 * 修改密码请求接口
 */
export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmNewPassword: string;
}

/**
 * 用户服务API
 */
export class UserService {
  /**
   * 获取当前用户个人信息
   */
  static async getProfile(): Promise<UserProfile> {
    const response = await get<UserProfile>('/api/user/profile');
    return response.data;
  }

  /**
   * 更新个人信息
   */
  static async updateProfile(data: UpdateProfileRequest): Promise<void> {
    await put('/api/user/profile', data);
  }

  /**
   * 修改密码
   */
  static async changePassword(data: ChangePasswordRequest): Promise<void> {
    await put('/api/user/password', data);
  }

  /**
   * 上传头像
   */
  static async uploadAvatar(file: File): Promise<string> {
    const resp = await uploadFile<{ url: string }>('/api/user/upload/avatar', file);
    return resp.data.url;
  }
}

// 导出默认实例
export default UserService;