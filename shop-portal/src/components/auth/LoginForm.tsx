/**
 * 登录表单组件
 * 功能描述：提供用户登录界面，包含表单验证、错误处理、加载状态等功能
 * 采用技术：React Hook Form表单管理，Yup验证，Tailwind CSS样式，TypeScript类型安全
 * 技术优势：高性能表单处理，完整的验证体系，响应式设计，优秀的用户体验
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */

import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { Link, useNavigate } from 'react-router-dom';
import { EyeIcon, EyeSlashIcon } from '@heroicons/react/24/outline';
import { LoginRequest } from '../../types/auth';
import { YUP_SCHEMAS } from '../../constants/validation';
import { object, boolean } from 'yup';
import { useAuthStore } from '../../store/authStore';
import { AUTH_ROUTES } from '../../constants/auth';

interface LoginFormData {
  username: string;
  password: string;
  rememberMe: boolean;
}

interface LoginFormProps {
  showRegisterLink?: boolean;
  className?: string;
  onLoginSuccess?: () => void;
}

export const LoginForm: React.FC<LoginFormProps> = ({
  showRegisterLink = true,
  className = '',
  onLoginSuccess
}) => {
  const navigate = useNavigate();
  
  const { login, isLoading, error, clearError } = useAuthStore();
  
  const [showPassword, setShowPassword] = useState(false);
  
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    setFocus
  } = useForm<LoginFormData>({
    resolver: yupResolver(object({
      username: YUP_SCHEMAS.username,
      password: YUP_SCHEMAS.password,
      rememberMe: boolean().default(false)
    })),
    defaultValues: {
      username: '',
      password: '',
      rememberMe: false
    }
  });
  
  useEffect(() => {
    setFocus('username');
  }, [setFocus]);
  
  useEffect(() => {
    return () => {
      clearError();
    };
  }, [clearError]);
  
  const onSubmit = async (data: LoginFormData) => {
    try {
      clearError();
      const loginRequest: LoginRequest = {
        username: data.username.trim(),
        password: data.password,
        rememberMe: data.rememberMe
      };
      await login(loginRequest);
      if (onLoginSuccess) onLoginSuccess();
      navigate('/role-navigation', { replace: true });
    } catch (error) {
      console.error('Login failed:', error);
    }
  };
  
  const togglePasswordVisibility = () => {
    setShowPassword(prev => !prev);
  };
  
  const handleInputChange = () => {
    if (error) clearError();
  };
  
  return (
    <div className={`w-full max-w-md mx-auto ${className}`}>
      <div className="text-center mb-8">
        <h2 className="text-3xl font-bold text-gray-900 mb-2">登录账户</h2>
        <p className="text-gray-600">欢迎回来，请输入您的登录信息</p>
      </div>
      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
            </div>
            <div className="ml-3">
              <p className="text-sm text-red-800">{error}</p>
            </div>
          </div>
        </div>
      )}
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div>
          <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-2">用户名或邮箱</label>
          <input
            {...register('username', { onChange: handleInputChange })}
            type="text"
            id="username"
            autoComplete="username"
            placeholder="请输入用户名或邮箱"
            className={`w-full px-3 py-2 border rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.username ? 'border-red-300' : 'border-gray-300'}`}
          />
          {errors.username && (<p className="mt-1 text-sm text-red-600">{errors.username.message}</p>)}
        </div>
        <div>
          <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">密码</label>
          <div className="relative">
            <input
              {...register('password', { onChange: handleInputChange })}
              type={showPassword ? 'text' : 'password'}
              id="password"
              autoComplete="current-password"
              placeholder="请输入密码"
              className={`w-full px-3 py-2 pr-10 border rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.password ? 'border-red-300' : 'border-gray-300'}`}
            />
            <button type="button" onClick={togglePasswordVisibility} className="absolute inset-y-0 right-0 pr-3 flex items-center">
              {showPassword ? (<EyeSlashIcon className="h-5 w-5 text-gray-400 hover:text-gray-600" />) : (<EyeIcon className="h-5 w-5 text-gray-400 hover:text-gray-600" />)}
            </button>
          </div>
          {errors.password && (<p className="mt-1 text-sm text-red-600">{errors.password.message}</p>)}
        </div>
        <div className="flex items-center justify-between">
          <div className="flex items-center">
            <input {...register('rememberMe')} id="rememberMe" type="checkbox" className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded" />
            <label htmlFor="rememberMe" className="ml-2 block text-sm text-gray-700">记住我</label>
          </div>
          <Link to={AUTH_ROUTES.FORGOT_PASSWORD} className="text-sm text-blue-600 hover:text-blue-500 hover:underline">忘记密码？</Link>
        </div>
        <button type="submit" disabled={isSubmitting || isLoading} className="w-full flex justify-center py-2 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200">
          {isSubmitting || isLoading ? (
            <>
              <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              登录中...
            </>
          ) : (
            '登录'
          )}
        </button>
        {showRegisterLink && (
          <div className="text-center">
            <p className="text-sm text-gray-600">
              还没有账户？{' '}
              <button type="button" onClick={() => navigate(AUTH_ROUTES.REGISTER)} className="text-blue-600 hover:text-blue-500 hover:underline font-medium cursor-pointer bg-transparent border-none p-0">
                立即注册
              </button>
            </p>
          </div>
        )}
      </form>
    </div>
  );
};

export default LoginForm;