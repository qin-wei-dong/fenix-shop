import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { isAdmin } from '../types/user';
import LoadingSpinner from '../components/LoadingSpinner';

/**
 * 角色路由导航组件
 * 功能描述：根据用户角色自动导航到相应的页面
 * 采用技术：React Router导航、角色判断、条件渲染
 * 技术优势：智能的角色路由分发，提升用户体验
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
const RoleBasedNavigation: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated, user, isLoading } = useAuthStore();

  useEffect(() => {
    // 如果还在加载中，等待加载完成
    if (isLoading) {
      return;
    }

    // 如果未登录，跳转到登录页
    if (!isAuthenticated || !user) {
      navigate('/login', { replace: true });
      return;
    }

    // 获取用户角色
    const userRoles = user.roles || [];

    // 根据角色跳转到相应页面
    if (isAdmin(userRoles)) {
      // 管理员跳转到管理后台
      navigate('/admin/dashboard', { replace: true });
    } else {
      // 普通用户和其他角色都跳转到商城首页
      navigate('/', { replace: true });
    }
  }, [navigate, isAuthenticated, user, isLoading]);

  // 显示加载状态
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <LoadingSpinner />
        <p className="mt-4 text-gray-600">正在为您准备页面...</p>
      </div>
    </div>
  );
};

export default RoleBasedNavigation;