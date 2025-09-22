import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useAuthStore } from '../store/authStore';
import { UserService, UserProfile, UpdateProfileRequest, ChangePasswordRequest } from '../services/userService';
import { EyeIcon, EyeSlashIcon, UserCircleIcon, KeyIcon, CogIcon, BellIcon } from '@heroicons/react/24/outline';
import { Link } from 'react-router-dom';

/**
 * 个人设置页面组件
 * 功能描述：用户个人设置页面，包含基本信息编辑、密码修改、头像上传等功能
 * 采用技术：React Hooks、React Hook Form、Tailwind CSS、状态管理
 * 技术优势：完整的个人信息管理，用户体验友好，表单验证完善
 */
const ProfileSettingsPage: React.FC = () => {
  const { user } = useAuthStore();
  const [activeTab, setActiveTab] = useState<'profile' | 'password' | 'notifications'>('profile');
  const [isLoading, setIsLoading] = useState(false);
  const [userProfile, setUserProfile] = useState<UserProfile | null>(null);
  const [showPassword, setShowPassword] = useState({
    current: false,
    new: false,
    confirm: false
  });
  const [avatarPreview, setAvatarPreview] = useState<string>('');

  // 基本信息表单
  const {
    register: registerProfile,
    handleSubmit: handleProfileSubmit,
    formState: { errors: profileErrors, isSubmitting: isProfileSubmitting },
    setValue: setProfileValue
  } = useForm<UpdateProfileRequest>();

  // 密码修改表单
  const {
    register: registerPassword,
    handleSubmit: handlePasswordSubmit,
    formState: { errors: passwordErrors, isSubmitting: isPasswordSubmitting },
    reset: resetPasswordForm
  } = useForm<ChangePasswordRequest>();

  // 加载用户信息
  useEffect(() => {
    const loadUserProfile = async () => {
      try {
        setIsLoading(true);
        const profile = await UserService.getProfile();
        setUserProfile(profile);
        
        // 设置表单默认值
        setProfileValue('email', profile.email || '');
        setProfileValue('mobile', profile.mobile || '');
        
        // 设置头像预览
        if (profile.avatarUrl) {
          setAvatarPreview(profile.avatarUrl);
        }
      } catch (error) {
        console.error('Failed to load user profile:', error);
      } finally {
        setIsLoading(false);
      }
    };

    loadUserProfile();
  }, [setProfileValue]);

  // 处理基本信息更新
  const onProfileSubmit = async (data: UpdateProfileRequest) => {
    try {
      await UserService.updateProfile(data);
      // 更新本地状态
      if (userProfile) {
        setUserProfile({
          ...userProfile,
          ...data
        });
      }
      alert('个人信息更新成功！');
    } catch (error) {
      console.error('Failed to update profile:', error);
      alert('个人信息更新失败，请重试');
    }
  };

  // 处理密码修改
  const onPasswordSubmit = async (data: ChangePasswordRequest) => {
    try {
      await UserService.changePassword(data);
      resetPasswordForm();
      alert('密码修改成功！');
    } catch (error) {
      console.error('Failed to change password:', error);
      alert('密码修改失败，请检查当前密码是否正确');
    }
  };

  // 处理头像上传
  const handleAvatarUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    // 检查文件大小（限制2MB）
    if (file.size > 2 * 1024 * 1024) {
      alert('图片大小不能超过2MB');
      return;
    }

    // 检查文件类型
    if (!file.type.startsWith('image/')) {
      alert('请选择图片文件');
      return;
    }

    try {
      setIsLoading(true);
      
      // 生成预览
      const reader = new FileReader();
      reader.onload = (e) => {
        setAvatarPreview(e.target?.result as string);
      };
      reader.readAsDataURL(file);

      // 上传头像
      const avatarUrl = await UserService.uploadAvatar(file);
      
      // 更新个人信息
      await UserService.updateProfile({ avatarUrl });
      
      if (userProfile) {
        setUserProfile({
          ...userProfile,
          avatarUrl
        });
      }
      
      alert('头像更新成功！');
    } catch (error) {
      console.error('Failed to upload avatar:', error);
      alert('头像上传失败，请重试');
    } finally {
      setIsLoading(false);
    }
  };

  const tabs = [
    { id: 'profile', name: '基本信息', icon: UserCircleIcon },
    { id: 'password', name: '修改密码', icon: KeyIcon },
    { id: 'notifications', name: '通知设置', icon: BellIcon },
  ];

  if (isLoading && !userProfile) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500 mx-auto"></div>
          <p className="mt-4 text-gray-600">加载中...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
        {/* 面包屑导航 */}
        <nav className="mb-4 text-sm text-gray-500" aria-label="Breadcrumb">
          <ol className="flex items-center gap-2">
            <li>
              <Link to="/" className="hover:text-gray-700">首页</Link>
            </li>
            <li className="text-gray-400">/</li>
            <li>
              <Link to="/user/center" className="hover:text-gray-700">用户中心</Link>
            </li>
            <li className="text-gray-400">/</li>
            <li aria-current="page" className="text-gray-700">个人设置</li>
          </ol>
        </nav>

        {/* 顶部品牌横幅（对齐用户中心） */}
        <div className="mb-4">
          <div className="bg-gradient-to-r from-indigo-500 to-purple-600 overflow-hidden shadow rounded-lg">
            <div className="px-6 py-5 sm:p-6 text-white flex items-center justify-between gap-6">
              <div>
                <h1 className="text-2xl sm:text-3xl font-bold">个人设置</h1>
                <p className="mt-1 text-indigo-100">完善资料，享受更顺畅的购物体验</p>
              </div>
              <div className="hidden sm:flex items-center gap-3">
                <span className="text-sm text-indigo-100">资料完善度</span>
                <div className="w-40 h-2 bg-white/30 rounded-full shadow-inner">
                  <div className="h-2 rounded-full bg-gradient-to-r from-indigo-300 to-purple-300" style={{ width: `${Math.min(100, [userProfile?.email?25:0, userProfile?.mobile?25:0, userProfile?.avatarUrl?25:0, 25].reduce((a,b)=>a+b,0))}%` }}></div>
                </div>
              </div>
            </div>
          </div>
        </div>
        {/* 账户概览卡片 + 完成项清单 */}
        <div className="mb-8 bg-white overflow-hidden shadow rounded-lg p-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 items-center">
            {/* 头像与基础信息 */}
            <div className="flex items-center gap-4">
              {avatarPreview || userProfile?.avatarUrl ? (
                <img
                  src={avatarPreview || userProfile?.avatarUrl}
                  alt="头像"
                  className="h-14 w-14 rounded-full object-cover border"
                />
              ) : (
                <div className="h-14 w-14 rounded-full bg-gray-200 flex items-center justify-center">
                  <UserCircleIcon className="h-8 w-8 text-gray-400" />
                </div>
              )}
              <div>
                <div className="text-lg font-semibold text-gray-900">{userProfile?.username || user?.username || '未登录'}</div>
                <div className="text-sm text-gray-500">
                  {userProfile?.registerTime ? `注册于 ${new Date(userProfile.registerTime).toLocaleDateString()}` : '欢迎使用 Fenix Shop'}
                </div>
              </div>
            </div>

            {/* 会员等级与积分 */}
            <div className="flex md:justify-center gap-4">
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-50 text-indigo-700 border border-indigo-200">
                <CogIcon className="h-4 w-4 text-indigo-600" />
                <span>{userProfile?.userLevel ? `会员等级 Lv.${userProfile.userLevel}` : '普通会员'}</span>
              </div>
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-amber-50 text-amber-700 border border-amber-200">
                <span className="text-xs">积分</span>
                <span className="font-semibold">{userProfile?.points ?? 0}</span>
              </div>
            </div>

            {/* 快捷入口 */}
            <div className="flex md:justify-end flex-wrap gap-2">
              <Link
                to="/orders"
                className="px-3 py-2 text-sm border border-gray-300 rounded-lg bg-white hover:bg-gray-50"
              >
                我的订单
              </Link>
              <Link
                to="/addresses"
                className="px-3 py-2 text-sm border border-gray-300 rounded-lg bg-white hover:bg-gray-50"
              >
                地址管理
              </Link>
              <Link
                to="/payments"
                className="px-3 py-2 text-sm border border-gray-300 rounded-lg bg-white hover:bg-gray-50"
              >
                支付方式
              </Link>
            </div>
          </div>

          {/* 完成项清单（卡片化，贴合用户中心卡片风格） */}
          <div className="mt-6 grid grid-cols-1 md:grid-cols-3 gap-6 text-sm">
            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="p-4 flex items-center">
                <svg className={`h-6 w-6 ${userProfile?.email ? 'text-emerald-600':'text-gray-400'}`} fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 12H8m8 4H8m8-8H8m12 8V8a2 2 0 00-2-2H6a2 2 0 00-2 2v8a2 2 0 002 2h12z" />
                </svg>
                <div className="ml-3">
                  <div className="text-gray-900 font-medium">绑定邮箱</div>
                  <div className="text-gray-500">{userProfile?.email || '未绑定'}</div>
                </div>
              </div>
            </div>
            <div className="bg白 overflow-hidden shadow rounded-lg">
              <div className="p-4 flex items-center">
                <svg className={`h-6 w-6 ${userProfile?.mobile ? 'text-emerald-600':'text-gray-400'}`} fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5h18M7 5v14a2 2 0 002 2h6a2 2 0 002-2V5" />
                </svg>
                <div className="ml-3">
                  <div className="text-gray-900 font-medium">绑定手机</div>
                  <div className="text-gray-500">{userProfile?.mobile || '未绑定'}</div>
                </div>
              </div>
            </div>
            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="p-4 flex items-center">
                <svg className={`h-6 w-6 ${(userProfile?.avatarUrl||avatarPreview) ? 'text-emerald-600':'text-gray-400'}`} fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                <div className="ml-3">
                  <div className="text-gray-900 font-medium">设置头像</div>
                  <div className="text-gray-500">{(userProfile?.avatarUrl||avatarPreview) ? '已设置' : '未设置'}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="flex flex-col lg:flex-row gap-8">
          {/* 侧边栏导航 */}
          <div className="lg:w-1/4 lg:sticky lg:top-16 self-start">
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
              <nav className="space-y-1">
                {tabs.map((tab) => {
                  const Icon = tab.icon;
                  return (
                    <button
                      key={tab.id}
                      onClick={() => setActiveTab(tab.id as any)}
                      className={`w-full flex items-center space-x-3 px-3 py-2 rounded-lg text-left font-medium transition-colors ${
                        activeTab === tab.id
                          ? 'bg-primary-50 text-primary-700 border border-primary-200'
                          : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                      }`}
                    >
                      <Icon className="h-5 w-5" />
                      <span>{tab.name}</span>
                    </button>
                  );
                })}
              </nav>
            </div>
          </div>

          {/* 主要内容区域 */}
          <div className="lg:w-3/4">
            <div className="bg-white rounded-lg shadow-sm border border-gray-200">
              {/* 基本信息标签页 */}
              {activeTab === 'profile' && (
                <div className="p-6">
                  <h2 className="text-xl font-semibold text-gray-900 mb-6">基本信息</h2>
                  
                  {/* 头像上传 */}
                  <div className="mb-8">
                    <label className="block text-sm font-medium text-gray-700 mb-4">
                      头像
                    </label>
                    <div className="flex items-center space-x-6">
                      <div className="relative">
                        {avatarPreview || userProfile?.avatarUrl ? (
                          <img
                            src={avatarPreview || userProfile?.avatarUrl}
                            alt="头像"
                            className="h-20 w-20 rounded-full object-cover border-2 border-gray-200"
                          />
                        ) : (
                          <div className="h-20 w-20 rounded-full bg-gray-200 flex items-center justify-center">
                            <UserCircleIcon className="h-12 w-12 text-gray-400" />
                          </div>
                        )}
                        {isLoading && (
                          <div className="absolute inset-0 bg-black bg-opacity-50 rounded-full flex items-center justify-center">
                            <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-white"></div>
                          </div>
                        )}
                      </div>
                      <div>
                        <input
                          type="file"
                          accept="image/*"
                          onChange={handleAvatarUpload}
                          className="hidden"
                          id="avatar-upload"
                          disabled={isLoading}
                        />
                        <label
                          htmlFor="avatar-upload"
                          className="cursor-pointer inline-flex items-center px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          更换头像
                        </label>
                        <p className="mt-2 text-xs text-gray-500">
                          支持 JPG、PNG 格式，文件大小不超过 2MB
                        </p>
                      </div>
                    </div>
                  </div>

                  {/* 基本信息表单 */}
                  <form onSubmit={handleProfileSubmit(onProfileSubmit)} className="space-y-6">
                    {/* 用户名（只读） */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        用户名
                      </label>
                      <input
                        type="text"
                        value={userProfile?.username || ''}
                        disabled
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-500 cursor-not-allowed"
                      />
                      <p className="mt-1 text-xs text-gray-500">用户名不可修改</p>
                    </div>

                    {/* 邮箱 / 手机 - 响应式两列 */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          邮箱地址
                        </label>
                        <input
                          {...registerProfile('email')}
                          type="email"
                          className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 ${
                            profileErrors.email ? 'border-red-300' : 'border-gray-300'
                          }`}
                          placeholder="请输入邮箱地址"
                        />
                        {profileErrors.email && (
                          <p className="mt-1 text-sm text-red-600">{profileErrors.email.message}</p>
                        )}
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          手机号码
                        </label>
                        <input
                          {...registerProfile('mobile')}
                          type="tel"
                          className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 ${
                            profileErrors.mobile ? 'border-red-300' : 'border-gray-300'
                          }`}
                          placeholder="请输入手机号码"
                        />
                        {profileErrors.mobile && (
                          <p className="mt-1 text-sm text-red-600">{profileErrors.mobile.message}</p>
                        )}
                      </div>
                    </div>

                    {/* 提交按钮 */}
                    <div className="flex justify-end">
                      <button
                        type="submit"
                        disabled={isProfileSubmitting}
                        className="px-6 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        {isProfileSubmitting ? '保存中...' : '保存更改'}
                      </button>
                    </div>
                  </form>
                </div>
              )}

              {/* 密码修改标签页 */}
              {activeTab === 'password' && (
                <div className="p-6">
                  <h2 className="text-xl font-semibold text-gray-900 mb-6">修改密码</h2>
                  
                  <form onSubmit={handlePasswordSubmit(onPasswordSubmit)} className="space-y-6">
                    {/* 当前密码 */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        当前密码
                      </label>
                      <div className="relative">
                        <input
                          {...registerPassword('currentPassword')}
                          type={showPassword.current ? 'text' : 'password'}
                          className={`w-full px-3 py-2 pr-10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 ${
                            passwordErrors.currentPassword ? 'border-red-300' : 'border-gray-300'
                          }`}
                          placeholder="请输入当前密码"
                        />
                        <button
                          type="button"
                          onClick={() => setShowPassword({ ...showPassword, current: !showPassword.current })}
                          className="absolute inset-y-0 right-0 pr-3 flex items-center"
                        >
                          {showPassword.current ? (
                            <EyeSlashIcon className="h-5 w-5 text-gray-400" />
                          ) : (
                            <EyeIcon className="h-5 w-5 text-gray-400" />
                          )}
                        </button>
                      </div>
                      {passwordErrors.currentPassword && (
                        <p className="mt-1 text-sm text-red-600">{passwordErrors.currentPassword.message}</p>
                      )}
                    </div>

                    {/* 新密码 */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        新密码
                      </label>
                      <div className="relative">
                        <input
                          {...registerPassword('newPassword')}
                          type={showPassword.new ? 'text' : 'password'}
                          className={`w-full px-3 py-2 pr-10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 ${
                            passwordErrors.newPassword ? 'border-red-300' : 'border-gray-300'
                          }`}
                          placeholder="请输入新密码"
                        />
                        <button
                          type="button"
                          onClick={() => setShowPassword({ ...showPassword, new: !showPassword.new })}
                          className="absolute inset-y-0 right-0 pr-3 flex items-center"
                        >
                          {showPassword.new ? (
                            <EyeSlashIcon className="h-5 w-5 text-gray-400" />
                          ) : (
                            <EyeIcon className="h-5 w-5 text-gray-400" />
                          )}
                        </button>
                      </div>
                      {passwordErrors.newPassword && (
                        <p className="mt-1 text-sm text-red-600">{passwordErrors.newPassword.message}</p>
                      )}
                      <p className="mt-1 text-xs text-gray-500">
                        密码必须包含大小写字母和数字，至少6个字符
                      </p>
                    </div>

                    {/* 确认新密码 */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        确认新密码
                      </label>
                      <div className="relative">
                        <input
                          {...registerPassword('confirmNewPassword')}
                          type={showPassword.confirm ? 'text' : 'password'}
                          className={`w-full px-3 py-2 pr-10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 ${
                            passwordErrors.confirmNewPassword ? 'border-red-300' : 'border-gray-300'
                          }`}
                          placeholder="请再次输入新密码"
                        />
                        <button
                          type="button"
                          onClick={() => setShowPassword({ ...showPassword, confirm: !showPassword.confirm })}
                          className="absolute inset-y-0 right-0 pr-3 flex items-center"
                        >
                          {showPassword.confirm ? (
                            <EyeSlashIcon className="h-5 w-5 text-gray-400" />
                          ) : (
                            <EyeIcon className="h-5 w-5 text-gray-400" />
                          )}
                        </button>
                      </div>
                      {passwordErrors.confirmNewPassword && (
                        <p className="mt-1 text-sm text-red-600">{passwordErrors.confirmNewPassword.message}</p>
                      )}
                    </div>

                    {/* 提交按钮 */}
                    <div className="flex justify-end space-x-4">
                      <button
                        type="button"
                        onClick={() => resetPasswordForm()}
                        className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
                      >
                        重置
                      </button>
                      <button
                        type="submit"
                        disabled={isPasswordSubmitting}
                        className="px-6 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        {isPasswordSubmitting ? '修改中...' : '修改密码'}
                      </button>
                    </div>
                  </form>
                </div>
              )}

              {/* 通知设置标签页 */}
              {activeTab === 'notifications' && (
                <div className="p-6">
                  <h2 className="text-xl font-semibold text-gray-900 mb-6">通知设置</h2>
                  
                  <div className="space-y-6">
                    <div className="text-center py-12">
                      <BellIcon className="mx-auto h-12 w-12 text-gray-400" />
                      <h3 className="mt-2 text-lg font-medium text-gray-900">通知设置功能开发中</h3>
                      <p className="mt-1 text-gray-500">敬请期待更多通知管理功能</p>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
        </div>
      </main>
    </div>
  );
};

export default ProfileSettingsPage;