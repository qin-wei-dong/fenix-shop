import React, { useState, useEffect } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import LoginForm from '../components/auth/LoginForm';
import RegisterForm from '../components/auth/RegisterForm';
import { useAuthStore } from '../store/authStore';

/**
 * 认证页面组件
 * 功能描述：用户认证的主页面，包含登录和注册表单的切换功能
 * 采用技术：React Hooks、条件渲染、状态管理、响应式设计
 * 技术优势：用户体验优化，界面简洁，功能集中管理
 * 
 * @author fenix
 * @date 2024-01-20
 * @version 1.0
 */
const AuthPage: React.FC = () => {
  // 获取当前路由位置
  const location = useLocation();
  
  // 根据路径确定初始模式
  const getInitialMode = (): 'login' | 'register' => {
    return location.pathname === '/register' ? 'register' : 'login';
  };
  
  // 表单类型状态：'login' | 'register'
  const [authMode, setAuthMode] = useState<'login' | 'register'>(getInitialMode);
  
  // 获取认证状态
  const { isAuthenticated } = useAuthStore();
  
  // 监听路径变化，更新认证模式
  useEffect(() => {
    const newMode = getInitialMode();
    if (newMode !== authMode) {
      setAuthMode(newMode);
    }
  }, [location.pathname]);

  // 如果已认证，重定向到角色导航页面
  if (isAuthenticated) {
    return <Navigate to="/role-navigation" replace />;
  }

  // 切换认证模式（不再直接暴露，入口通过链接导航）
  // const toggleAuthMode = () => { /* removed unused */ };


  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        {/* 页面标题区域 */}
        <div className="text-center">
          <div className="mx-auto h-12 w-12 flex items-center justify-center rounded-full bg-indigo-100">
            <svg 
              className="h-8 w-8 text-indigo-600" 
              fill="none" 
              viewBox="0 0 24 24" 
              stroke="currentColor"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" 
              />
            </svg>
          </div>
          
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            {authMode === 'login' ? '登录您的账户' : '创建新账户'}
          </h2>
        </div>

        {/* 表单容器 */}
        <div className="bg-white py-8 px-6 shadow-xl rounded-lg sm:px-10">
          {authMode === 'login' ? (
            <LoginForm />
          ) : (
            <RegisterForm />
          )}
        </div>

        {/* 页脚信息 */}
        <div className="text-center">
          <p className="text-xs text-gray-500">
            登录即表示您同意我们的
            <a href="/terms" className="text-indigo-600 hover:text-indigo-500 mx-1">
              服务条款
            </a>
            和
            <a href="/privacy" className="text-indigo-600 hover:text-indigo-500 mx-1">
              隐私政策
            </a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;