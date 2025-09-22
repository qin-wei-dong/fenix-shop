/**
 * 注册表单组件
 * 功能描述：提供用户注册界面，包含表单验证、密码强度检查、用户名可用性验证等功能
 * 采用技术：React Hook Form表单管理，Yup验证，实时验证，TypeScript类型安全
 * 技术优势：完整的注册流程，实时反馈，用户体验优化，安全性保障
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
import { object, boolean, string, ref } from 'yup';
import { RegisterRequest } from '../../types/auth';
import { useAuthStore } from '../../store/authStore';
import { AUTH_ROUTES } from '../../constants/auth';
import { validateUsername, validateEmail, calculatePasswordStrength } from '../../utils/validation';

/**
 * 注册表单数据接口
 * 功能描述：定义注册表单的数据结构
 * 采用技术：TypeScript接口定义
 * 技术优势：类型安全，IDE支持，编译时检查
 */
interface RegisterFormData {
  /** 用户名 */
  username: string;
  /** 邮箱地址 */
  email: string;
  /** 密码 */
  password: string;
  /** 确认密码 */
  confirmPassword: string;
  /** 昵称 */
  nickname: string;
  /** 手机号 */
  mobile: string;
  /** 同意服务条款 */
  agreeTerms: boolean;
}

/**
 * 注册表单组件属性接口
 * 功能描述：定义组件的可配置属性
 * 采用技术：TypeScript接口定义，可选属性
 * 技术优势：组件复用性，配置灵活性，类型安全
 */
interface RegisterFormProps {
  /** 注册成功后的重定向路径 */
  redirectTo?: string;
  /** 是否显示登录链接 */
  showLoginLink?: boolean;
  /** 自定义样式类名 */
  className?: string;
  /** 注册成功回调函数 */
  onRegisterSuccess?: () => void;
}

/**
 * 密码强度指示器组件
 * 功能描述：显示密码强度等级和建议
 * 采用技术：React组件，动态样式，实时计算
 * 技术优势：用户体验优化，安全性提升
 */
interface PasswordStrengthProps {
  password: string;
}

const PasswordStrength: React.FC<PasswordStrengthProps> = ({ password }) => {
  const strength = calculatePasswordStrength(password);
  
  const getStrengthColor = (level: string) => {
    switch (level) {
      case 'weak': return 'bg-red-500';
      case 'medium': return 'bg-yellow-500';
      case 'strong': return 'bg-green-500';
      case 'very_strong': return 'bg-green-600';
      default: return 'bg-gray-300';
    }
  };
  
  const getStrengthText = (level: string) => {
    switch (level) {
      case 'weak': return '弱';
      case 'medium': return '中等';
      case 'strong': return '强';
      case 'very_strong': return '很强';
      default: return '';
    }
  };
  
  if (!password) return null;
  
  return (
    <div className="mt-2">
      <div className="flex items-center space-x-2">
        <div className="flex-1 bg-gray-200 rounded-full h-2">
          <div 
            className={`h-2 rounded-full transition-all duration-300 ${getStrengthColor(strength.level)}`}
            style={{ width: `${(strength.score / 100) * 100}%` }}
          />
        </div>
        <span className="text-sm text-gray-600">
          密码强度: {getStrengthText(strength.level)}
        </span>
      </div>
      {strength.feedback.length > 0 && (
        <ul className="mt-1 text-xs text-gray-500">
          {strength.feedback.map((feedback, index) => (
            <li key={index}>• {feedback}</li>
          ))}
        </ul>
      )}
    </div>
  );
};

/**
 * 注册表单组件
 * 功能描述：渲染注册表单界面，处理用户注册逻辑
 * 采用技术：React函数组件，Hook Form，实时验证
 * 技术优势：声明式UI，高性能表单，用户体验优化
 * 
 * @param props 组件属性
 * @returns JSX元素
 */
export const RegisterForm: React.FC<RegisterFormProps> = ({
  redirectTo,
  showLoginLink = true,
  className = '',
  onRegisterSuccess
}) => {
  // 获取路由相关hooks
  const navigate = useNavigate();
  
  // 获取认证store状态和方法
  const { register: registerUser, isLoading, error, clearError } = useAuthStore();
  
  // 组件状态
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [usernameAvailable, setUsernameAvailable] = useState<boolean | null>(null);
  const [emailAvailable, setEmailAvailable] = useState<boolean | null>(null);
  const [isCheckingUsername, setIsCheckingUsername] = useState(false);
  const [isCheckingEmail, setIsCheckingEmail] = useState(false);
  
  // 初始化表单
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    watch,
    setFocus,
  } = useForm<RegisterFormData>({
    resolver: yupResolver(object({
      username: string().required('请输入用户名').min(3, '用户名至少3个字符').max(20, '用户名最多20个字符'),
      email: string().required('请输入邮箱地址').email('请输入有效的邮箱地址'),
      password: string().required('请输入密码').min(8, '密码至少8个字符'),
      confirmPassword: string().required('请确认密码').oneOf([ref('password')], '两次输入的密码不一致'),
      nickname: string().required('请输入昵称').min(2, '昵称至少2个字符').max(20, '昵称最多20个字符'),
      mobile: string().required('请输入手机号').matches(/^1[3-9]\d{9}$/, '手机号格式不正确'),
      agreeTerms: boolean().required('请同意服务条款和隐私政策').oneOf([true], '请同意服务条款和隐私政策')
    })),
    defaultValues: {
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      nickname: '',
      mobile: '',
      agreeTerms: false as boolean
    },
    mode: 'onChange'
  });
  
  // 监听表单字段变化
  const watchedUsername = watch('username');
  const watchedEmail = watch('email');
  const watchedPassword = watch('password');
  
  // 组件挂载时聚焦用户名输入框
  useEffect(() => {
    setFocus('username');
  }, [setFocus]);
  
  // 清除错误信息当组件卸载时
  useEffect(() => {
    return () => {
      clearError();
    };
  }, [clearError]);
  
  // 用户名可用性检查
  useEffect(() => {
    const checkUsername = async () => {
      if (watchedUsername && watchedUsername.length >= 3) {
        setIsCheckingUsername(true);
        try {
          const result = await validateUsername(watchedUsername);
          setUsernameAvailable(result.isValid);
        } catch (error) {
          setUsernameAvailable(null);
        } finally {
          setIsCheckingUsername(false);
        }
      } else {
        setUsernameAvailable(null);
      }
    };
    
    const timeoutId = setTimeout(checkUsername, 500);
    return () => clearTimeout(timeoutId);
  }, [watchedUsername]);
  
  // 邮箱可用性检查
  useEffect(() => {
    const checkEmail = async () => {
      if (watchedEmail && watchedEmail.includes('@')) {
        setIsCheckingEmail(true);
        try {
          const result = await validateEmail(watchedEmail);
          setEmailAvailable(result.isValid);
        } catch (error) {
          setEmailAvailable(null);
        } finally {
          setIsCheckingEmail(false);
        }
      } else {
        setEmailAvailable(null);
      }
    };
    
    const timeoutId = setTimeout(checkEmail, 500);
    return () => clearTimeout(timeoutId);
  }, [watchedEmail]);
  
  /**
   * 处理表单提交
   * 功能描述：验证表单数据并调用注册API
   * 采用技术：异步处理，错误捕获，状态更新
   * 技术优势：用户体验优化，错误处理完善
   * 
   * @param data 表单数据
   */
  const onSubmit = async (data: RegisterFormData) => {
    try {
      // 清除之前的错误信息
      clearError();
      
      // 构造注册请求数据
      const registerRequest: RegisterRequest = {
        username: data.username.trim(),
        email: data.email.trim().toLowerCase(),
        password: data.password,
        confirmPassword: data.confirmPassword,
        nickname: data.nickname.trim(),
        mobile: data.mobile.trim(),
        agreeTerms: data.agreeTerms
      };
      
      // 调用注册API
      await registerUser(registerRequest);
      
      // 注册成功处理
      if (onRegisterSuccess) {
        onRegisterSuccess();
      }
      
      // 重定向到登录页面或指定页面
      const targetPath = redirectTo || AUTH_ROUTES.LOGIN;
      navigate(targetPath, { 
        replace: true,
        state: { 
          message: '注册成功！请使用您的账户信息登录。',
          username: data.username 
        }
      });
      
    } catch (error) {
      // 错误已经在store中处理，这里不需要额外处理
      console.error('Registration failed:', error);
    }
  };
  
  /**
   * 切换密码显示状态
   * 功能描述：控制密码输入框的显示/隐藏
   * 采用技术：React状态管理
   * 技术优势：用户体验优化，交互友好
   */
  const togglePasswordVisibility = () => {
    setShowPassword(prev => !prev);
  };
  
  const toggleConfirmPasswordVisibility = () => {
    setShowConfirmPassword(prev => !prev);
  };
  
  /**
   * 处理输入框变化
   * 功能描述：输入时清除错误信息
   * 采用技术：事件处理，状态清理
   * 技术优势：实时反馈，用户体验优化
   */
  const handleInputChange = () => {
    if (error) {
      clearError();
    }
  };
  
  return (
    <div className={`w-full max-w-md mx-auto ${className}`}>
      {/* 表单标题 */}
      <div className="text-center mb-8">
        <h2 className="text-3xl font-bold text-gray-900 mb-2">
          创建账户
        </h2>
        <p className="text-gray-600">
          加入我们，开始您的购物之旅
        </p>
      </div>
      
      {/* 错误信息显示 */}
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
      
      {/* 注册表单 */}
      <form onSubmit={handleSubmit(onSubmit as any)} className="space-y-6">
        {/* 用户名输入框 */}
        <div>
          <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-2">
            用户名
          </label>
          <div className="relative">
            <input
              {...register('username')}
              type="text"
              id="username"
              autoComplete="username"
              placeholder="请输入用户名"
              onChange={handleInputChange}
              className={`w-full px-3 py-2 pr-10 border rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
                errors.username ? 'border-red-300' : 
                usernameAvailable === false ? 'border-red-300' :
                usernameAvailable === true ? 'border-green-300' : 'border-gray-300'
              }`}
            />
            <div className="absolute inset-y-0 right-0 pr-3 flex items-center">
              {isCheckingUsername ? (
                <svg className="animate-spin h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              ) : usernameAvailable === true ? (
                <svg className="h-5 w-5 text-green-500" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414L8.414 15l-4.121-4.121a1 1 0 011.414-1.414L8.414 12.172l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
              ) : usernameAvailable === false ? (
                <svg className="h-5 w-5 text-red-500" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                </svg>
              ) : null}
            </div>
          </div>
          {errors.username && (
            <p className="mt-1 text-sm text-red-600">{errors.username.message}</p>
          )}
          {!errors.username && usernameAvailable === false && (
            <p className="mt-1 text-sm text-red-600">用户名已存在，请选择其他用户名</p>
          )}
          {!errors.username && usernameAvailable === true && (
            <p className="mt-1 text-sm text-green-600">用户名可用</p>
          )}
        </div>
        
        {/* 邮箱输入框 */}
        <div>
          <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
            邮箱地址
          </label>
          <div className="relative">
            <input
              {...register('email')}
              type="email"
              id="email"
              autoComplete="email"
              placeholder="请输入邮箱地址"
              onChange={handleInputChange}
              className={`w-full px-3 py-2 pr-10 border rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
                errors.email ? 'border-red-300' : 
                emailAvailable === false ? 'border-red-300' :
                emailAvailable === true ? 'border-green-300' : 'border-gray-300'
              }`}
            />
            <div className="absolute inset-y-0 right-0 pr-3 flex items-center">
              {isCheckingEmail ? (
                <svg className="animate-spin h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              ) : emailAvailable === true ? (
                <svg className="h-5 w-5 text-green-500" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414L8.414 15l-4.121-4.121a1 1 0 011.414-1.414L8.414 12.172l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
              ) : emailAvailable === false ? (
                <svg className="h-5 w-5 text-red-500" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                </svg>
              ) : null}
            </div>
          </div>
          {errors.email && (
            <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
          )}
          {!errors.email && emailAvailable === false && (
            <p className="mt-1 text-sm text-red-600">邮箱已被注册，请使用其他邮箱</p>
          )}
          {!errors.email && emailAvailable === true && (
            <p className="mt-1 text-sm text-green-600">邮箱可用</p>
          )}
        </div>
        
        {/* 昵称输入框 */}
        <div>
          <label htmlFor="nickname" className="block text-sm font-medium text-gray-700 mb-2">
            昵称
          </label>
          <input
            {...register('nickname')}
            type="text"
            id="nickname"
            autoComplete="nickname"
            placeholder="请输入昵称"
            onChange={handleInputChange}
            className={`w-full px-3 py-2 border rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
              errors.nickname ? 'border-red-300' : 'border-gray-300'
            }`}
          />
          {errors.nickname && (
            <p className="mt-1 text-sm text-red-600">{errors.nickname.message}</p>
          )}
        </div>
        
        {/* 手机号输入框 */}
        <div>
          <label htmlFor="mobile" className="block text-sm font-medium text-gray-700 mb-2">
            手机号
          </label>
          <input
            {...register('mobile')}
            type="tel"
            id="mobile"
            autoComplete="tel"
            placeholder="请输入手机号"
            onChange={handleInputChange}
            className={`w-full px-3 py-2 border rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
              errors.mobile ? 'border-red-300' : 'border-gray-300'
            }`}
          />
          {errors.mobile && (
            <p className="mt-1 text-sm text-red-600">{errors.mobile.message}</p>
          )}
        </div>
        
        {/* 密码输入框 */}
        <div>
          <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
            密码
          </label>
          <div className="relative">
            <input
              {...register('password')}
              type={showPassword ? 'text' : 'password'}
              id="password"
              autoComplete="new-password"
              placeholder="请输入密码"
              onChange={handleInputChange}
              className={`w-full px-3 py-2 pr-10 border rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
                errors.password ? 'border-red-300' : 'border-gray-300'
              }`}
            />
            <button
              type="button"
              onClick={togglePasswordVisibility}
              className="absolute inset-y-0 right-0 pr-3 flex items-center"
            >
              {showPassword ? (
                <EyeSlashIcon className="h-5 w-5 text-gray-400 hover:text-gray-600" />
              ) : (
                <EyeIcon className="h-5 w-5 text-gray-400 hover:text-gray-600" />
              )}
            </button>
          </div>
          {errors.password && (
            <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>
          )}
          <PasswordStrength password={watchedPassword} />
        </div>
        
        {/* 确认密码输入框 */}
        <div>
          <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-2">
            确认密码
          </label>
          <div className="relative">
            <input
              {...register('confirmPassword')}
              type={showConfirmPassword ? 'text' : 'password'}
              id="confirmPassword"
              autoComplete="new-password"
              placeholder="请再次输入密码"
              onChange={handleInputChange}
              className={`w-full px-3 py-2 pr-10 border rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
                errors.confirmPassword ? 'border-red-300' : 'border-gray-300'
              }`}
            />
            <button
              type="button"
              onClick={toggleConfirmPasswordVisibility}
              className="absolute inset-y-0 right-0 pr-3 flex items-center"
            >
              {showConfirmPassword ? (
                <EyeSlashIcon className="h-5 w-5 text-gray-400 hover:text-gray-600" />
              ) : (
                <EyeIcon className="h-5 w-5 text-gray-400 hover:text-gray-600" />
              )}
            </button>
          </div>
          {errors.confirmPassword && (
            <p className="mt-1 text-sm text-red-600">{errors.confirmPassword.message}</p>
          )}
        </div>
        
        {/* 服务条款同意 */}
        <div>
          <div className="flex items-start">
            <input
              {...register('agreeTerms')}
              id="agreeTerms"
              type="checkbox"
              className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded mt-1"
            />
            <label htmlFor="agreeTerms" className="ml-2 block text-sm text-gray-700">
              我已阅读并同意{' '}
              <Link to="/terms" className="text-blue-600 hover:text-blue-500 hover:underline">
                服务条款
              </Link>
              {' '}和{' '}
              <Link to="/privacy" className="text-blue-600 hover:text-blue-500 hover:underline">
                隐私政策
              </Link>
            </label>
          </div>
          {errors.agreeTerms && (
            <p className="mt-1 text-sm text-red-600">{errors.agreeTerms.message}</p>
          )}
        </div>
        
        {/* 注册按钮 */}
        <button
          type="submit"
          disabled={isSubmitting || isLoading || usernameAvailable === false || emailAvailable === false}
          className="w-full flex justify-center py-2 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
        >
          {isSubmitting || isLoading ? (
            <>
              <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              注册中...
            </>
          ) : (
            '创建账户'
          )}
        </button>
        
        {/* 登录链接 */}
        {showLoginLink && (
          <div className="text-center">
            <p className="text-sm text-gray-600">
              已有账户？{' '}
              <Link
                to={AUTH_ROUTES.LOGIN}
                className="text-blue-600 hover:text-blue-500 hover:underline font-medium"
              >
                立即登录
              </Link>
            </p>
          </div>
        )}
      </form>
    </div>
  );
};

export default RegisterForm;