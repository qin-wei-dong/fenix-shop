import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { hasPermission, isAdmin, UserRole } from '../../types/user';

interface RoleGuardProps {
  children: React.ReactNode;
  requiredRole?: UserRole | UserRole[];
  requiredPermission?: string;
  fallbackPath?: string;
  showError?: boolean;
}

/**
 * 角色守卫组件
 * 功能描述：基于用户角色和权限的路由访问控制组件
 * 采用技术：React Router导航控制、角色权限验证、条件渲染
 * 技术优势：细粒度的权限控制，安全的路由保护，用户友好的错误提示
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
const RoleGuard: React.FC<RoleGuardProps> = ({
  children,
  requiredRole,
  requiredPermission,
  fallbackPath = '/',
  showError = true
}) => {
  const { user, isAuthenticated, isLoading } = useAuthStore();

  // 如果认证状态还在加载中，显示加载界面而不是重定向
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-indigo-600 mx-auto"></div>
          <h2 className="mt-4 text-xl font-semibold text-gray-900">验证权限中...</h2>
          <p className="mt-2 text-gray-600">请稍候，正在验证您的访问权限</p>
        </div>
      </div>
    );
  }

  // 只有在初始化完成后才判断是否需要重定向
  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace />;
  }

  const userRoles = user.roles || [];

  // 检查角色权限
  if (requiredRole) {
    const requiredRoles = Array.isArray(requiredRole) ? requiredRole : [requiredRole];
    const hasRequiredRole = requiredRoles.some(role => userRoles.includes(role));
    
    if (!hasRequiredRole) {
      if (showError) {
        return (
          <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="max-w-md w-full bg-white shadow-lg rounded-lg p-8 text-center">
              <div className="mb-4">
                <svg className="mx-auto h-16 w-16 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.854-.833-2.598 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
                </svg>
              </div>
              <h3 className="text-lg font-medium text-gray-900 mb-2">访问受限</h3>
              <p className="text-gray-600 mb-6">
                抱歉，您没有权限访问此页面。请联系管理员获取相应权限。
              </p>
              <button
                onClick={() => window.history.back()}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                返回上一页
              </button>
            </div>
          </div>
        );
      }
      return <Navigate to={fallbackPath} replace />;
    }
  }

  // 检查具体权限
  if (requiredPermission && !hasPermission(userRoles, requiredPermission)) {
    if (showError) {
      return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
          <div className="max-w-md w-full bg-white shadow-lg rounded-lg p-8 text-center">
            <div className="mb-4">
              <svg className="mx-auto h-16 w-16 text-orange-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">权限不足</h3>
            <p className="text-gray-600 mb-6">
              您没有执行此操作的权限。请联系管理员获取相应权限。
            </p>
            <button
              onClick={() => window.history.back()}
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-orange-600 hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
            >
              返回上一页
            </button>
          </div>
        </div>
      );
    }
    return <Navigate to={fallbackPath} replace />;
  }

  // 权限验证通过，渲染子组件
  return <>{children}</>;
};

/**
 * 管理员守卫组件
 * 只允许管理员角色访问
 */
export const AdminGuard: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <RoleGuard requiredRole={[UserRole.SUPER_ADMIN, UserRole.ADMIN]}>
      {children}
    </RoleGuard>
  );
};

/**
 * 普通用户守卫组件  
 * 只允许普通用户访问（排除管理员）
 */
export const UserGuard: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, isLoading } = useAuthStore();
  
  // 如果还在加载中，等待初始化完成
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-indigo-600 mx-auto"></div>
          <h2 className="mt-4 text-xl font-semibold text-gray-900">验证用户权限...</h2>
          <p className="mt-2 text-gray-600">请稍候，正在验证您的访问权限</p>
        </div>
      </div>
    );
  }
  
  const userRoles = user?.roles || [];
  
  // 如果是管理员，重定向到管理后台
  if (isAdmin(userRoles)) {
    return <Navigate to="/admin/dashboard" replace />;
  }
  
  return (
    <RoleGuard requiredRole={UserRole.USER}>
      {children}
    </RoleGuard>
  );
};

export default RoleGuard;