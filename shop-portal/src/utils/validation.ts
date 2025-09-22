/**
 * 表单验证工具函数库
 * 功能描述：提供表单验证相关的工具函数，包括实时验证、密码强度检查、可用性验证等
 * 采用技术：函数式编程，防抖优化，异步验证，TypeScript类型安全
 * 技术优势：高性能的验证机制，用户体验友好，可复用性强
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */

import { 
  REGEX_PATTERNS, 
  FIELD_LIMITS, 
  VALIDATION_MESSAGES, 
  PASSWORD_STRENGTH, 
  COMMON_PASSWORDS,
  ValidationResult,
  PasswordStrengthResult,
  ValidationFunction,
  AsyncValidationFunction
} from '../constants/validation';
import { AUTH_ENDPOINTS } from '../constants/auth';

/**
 * 防抖函数
 * 功能描述：创建防抖函数，用于优化频繁的验证请求
 * 采用技术：闭包，定时器，函数柯里化
 * 技术优势：减少不必要的API调用，提升性能和用户体验
 * 
 * @param func 要防抖的函数
 * @param delay 延迟时间（毫秒）
 * @returns 防抖后的函数
 */
export const debounce = <T extends (...args: any[]) => any>(
  func: T,
  delay: number
): ((...args: Parameters<T>) => void) => {
  let timeoutId: NodeJS.Timeout;
  
  return (...args: Parameters<T>) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func(...args), delay);
  };
};

/**
 * 验证用户名格式
 * 功能描述：验证用户名是否符合格式要求
 * 采用技术：正则表达式验证，长度检查，字符集验证
 * 技术优势：全面的用户名格式验证，防止无效输入
 * 
 * @param username 用户名
 * @returns 验证结果
 */
export const validateUsername = (username: string): ValidationResult => {
  const errors: string[] = [];
  const warnings: string[] = [];
  
  // 检查是否为空
  if (!username || username.trim().length === 0) {
    errors.push(VALIDATION_MESSAGES.REQUIRED.USERNAME);
    return { isValid: false, errors, warnings };
  }
  
  const trimmedUsername = username.trim();
  
  // 检查长度
  if (trimmedUsername.length < FIELD_LIMITS.USERNAME.MIN) {
    errors.push(VALIDATION_MESSAGES.LENGTH.USERNAME_MIN);
  }
  
  if (trimmedUsername.length > FIELD_LIMITS.USERNAME.MAX) {
    errors.push(VALIDATION_MESSAGES.LENGTH.USERNAME_MAX);
  }
  
  // 检查格式
  if (!REGEX_PATTERNS.USERNAME.test(trimmedUsername)) {
    errors.push(VALIDATION_MESSAGES.FORMAT.USERNAME);
  }
  
  // 检查是否以数字开头
  if (/^\d/.test(trimmedUsername)) {
    errors.push('用户名不能以数字开头');
  }
  
  // 检查是否包含连续下划线
  if (/__/.test(trimmedUsername)) {
    warnings.push('建议避免使用连续的下划线');
  }
  
  return {
    isValid: errors.length === 0,
    errors,
    warnings
  };
};

/**
 * 验证邮箱格式
 * 功能描述：验证邮箱地址是否符合标准格式
 * 采用技术：正则表达式验证，长度检查，域名验证
 * 技术优势：准确的邮箱格式验证，支持国际化域名
 * 
 * @param email 邮箱地址
 * @returns 验证结果
 */
export const validateEmail = (email: string): ValidationResult => {
  const errors: string[] = [];
  const warnings: string[] = [];
  
  // 检查是否为空
  if (!email || email.trim().length === 0) {
    errors.push(VALIDATION_MESSAGES.REQUIRED.EMAIL);
    return { isValid: false, errors, warnings };
  }
  
  const trimmedEmail = email.trim().toLowerCase();
  
  // 检查长度
  if (trimmedEmail.length > FIELD_LIMITS.EMAIL.MAX) {
    errors.push(VALIDATION_MESSAGES.LENGTH.EMAIL_MAX);
  }
  
  // 检查格式
  if (!REGEX_PATTERNS.EMAIL.test(trimmedEmail)) {
    errors.push(VALIDATION_MESSAGES.FORMAT.EMAIL);
  }
  
  // 检查常见的临时邮箱域名
  const tempEmailDomains = [
    '10minutemail.com',
    'tempmail.org',
    'guerrillamail.com',
    'mailinator.com'
  ];
  
  const domain = trimmedEmail.split('@')[1];
  if (domain && tempEmailDomains.includes(domain)) {
    warnings.push('检测到临时邮箱，建议使用常用邮箱');
  }
  
  return {
    isValid: errors.length === 0,
    errors,
    warnings
  };
};

/**
 * 验证手机号格式
 * 功能描述：验证手机号是否符合中国大陆格式
 * 采用技术：正则表达式验证，号段验证
 * 技术优势：准确的手机号验证，支持最新号段
 * 
 * @param phone 手机号
 * @returns 验证结果
 */
export const validatePhone = (phone: string): ValidationResult => {
  const errors: string[] = [];
  const warnings: string[] = [];
  
  // 检查是否为空
  if (!phone || phone.trim().length === 0) {
    errors.push(VALIDATION_MESSAGES.REQUIRED.PHONE);
    return { isValid: false, errors, warnings };
  }
  
  const trimmedPhone = phone.trim().replace(/\s+/g, '');
  
  // 检查长度
  if (trimmedPhone.length !== FIELD_LIMITS.PHONE.LENGTH) {
    errors.push(VALIDATION_MESSAGES.LENGTH.PHONE_EXACT);
  }
  
  // 检查格式
  if (!REGEX_PATTERNS.PHONE.CN.test(trimmedPhone)) {
    errors.push(VALIDATION_MESSAGES.FORMAT.PHONE);
  }
  
  // 检查号段有效性
  const validPrefixes = ['13', '14', '15', '16', '17', '18', '19'];
  const prefix = trimmedPhone.substring(0, 2);
  if (!validPrefixes.includes(prefix)) {
    errors.push('手机号段不正确');
  }
  
  return {
    isValid: errors.length === 0,
    errors,
    warnings
  };
};

/**
 * 计算密码强度
 * 功能描述：计算密码的安全强度等级和评分
 * 采用技术：多维度评分算法，模式识别，字典检查
 * 技术优势：全面的密码强度评估，引导用户设置安全密码
 * 
 * @param password 密码
 * @returns 密码强度结果
 */
export const calculatePasswordStrength = (password: string): PasswordStrengthResult => {
  let score = 0;
  const feedback: string[] = [];
  
  // 检查密码长度
  const lengthScore = Math.min(
    (password.length / PASSWORD_STRENGTH.SCORING.LENGTH.OPTIMAL_LENGTH) * PASSWORD_STRENGTH.SCORING.LENGTH.MAX_SCORE,
    PASSWORD_STRENGTH.SCORING.LENGTH.MAX_SCORE
  );
  score += lengthScore;
  
  if (password.length < FIELD_LIMITS.PASSWORD.MIN) {
    feedback.push(`密码至少需要${FIELD_LIMITS.PASSWORD.MIN}位`);
  } else if (password.length < PASSWORD_STRENGTH.SCORING.LENGTH.OPTIMAL_LENGTH) {
    feedback.push('建议密码长度至少12位');
  }
  
  // 检查字符类型复杂度
  if (/[a-z]/.test(password)) {
    score += PASSWORD_STRENGTH.SCORING.COMPLEXITY.LOWERCASE;
  } else {
    feedback.push('建议包含小写字母');
  }
  
  if (/[A-Z]/.test(password)) {
    score += PASSWORD_STRENGTH.SCORING.COMPLEXITY.UPPERCASE;
  } else {
    feedback.push('建议包含大写字母');
  }
  
  if (/\d/.test(password)) {
    score += PASSWORD_STRENGTH.SCORING.COMPLEXITY.NUMBERS;
  } else {
    feedback.push('建议包含数字');
  }
  
  if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
    score += PASSWORD_STRENGTH.SCORING.COMPLEXITY.SYMBOLS;
  } else {
    feedback.push('建议包含特殊字符');
  }
  
  // 检查重复字符
  if (!/(..).*\1/.test(password)) {
    score += PASSWORD_STRENGTH.SCORING.PATTERNS.NO_REPEAT;
  } else {
    feedback.push('避免使用重复的字符组合');
  }
  
  // 检查连续字符
  const hasSequence = /(?:abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz|123|234|345|456|567|678|789)/i.test(password);
  if (!hasSequence) {
    score += PASSWORD_STRENGTH.SCORING.PATTERNS.NO_SEQUENCE;
  } else {
    feedback.push('避免使用连续的字符序列');
  }
  
  // 检查常见密码
  const isCommonPassword = COMMON_PASSWORDS.some(common => 
    password.toLowerCase().includes(common.toLowerCase())
  );
  if (!isCommonPassword) {
    score += PASSWORD_STRENGTH.SCORING.PATTERNS.NO_COMMON;
  } else {
    feedback.push('避免使用常见密码');
  }
  
  // 确定强度等级
  let level: string;
  if (score >= PASSWORD_STRENGTH.THRESHOLDS.VERY_STRONG) {
    level = PASSWORD_STRENGTH.LEVELS.VERY_STRONG;
  } else if (score >= PASSWORD_STRENGTH.THRESHOLDS.STRONG) {
    level = PASSWORD_STRENGTH.LEVELS.STRONG;
  } else if (score >= PASSWORD_STRENGTH.THRESHOLDS.MEDIUM) {
    level = PASSWORD_STRENGTH.LEVELS.MEDIUM;
  } else {
    level = PASSWORD_STRENGTH.LEVELS.WEAK;
  }
  
  return {
    score: Math.min(score, 100),
    level,
    feedback
  };
};

/**
 * 验证密码格式和强度
 * 功能描述：综合验证密码的格式要求和安全强度
 * 采用技术：多层验证机制，强度评估，安全建议
 * 技术优势：全面的密码验证，确保账户安全
 * 
 * @param password 密码
 * @param requireStrong 是否要求强密码
 * @returns 验证结果
 */
export const validatePassword = (
  password: string, 
  requireStrong: boolean = false
): ValidationResult => {
  const errors: string[] = [];
  const warnings: string[] = [];
  
  // 检查是否为空
  if (!password || password.length === 0) {
    errors.push(VALIDATION_MESSAGES.REQUIRED.PASSWORD);
    return { isValid: false, errors, warnings };
  }
  
  // 检查长度
  if (password.length < FIELD_LIMITS.PASSWORD.MIN) {
    errors.push(VALIDATION_MESSAGES.LENGTH.PASSWORD_MIN);
  }
  
  if (password.length > FIELD_LIMITS.PASSWORD.MAX) {
    errors.push(VALIDATION_MESSAGES.LENGTH.PASSWORD_MAX);
  }
  
  // 检查基本格式
  if (!REGEX_PATTERNS.PASSWORD.BASIC.test(password)) {
    errors.push(VALIDATION_MESSAGES.FORMAT.PASSWORD);
  }
  
  // 计算密码强度
  const strengthResult = calculatePasswordStrength(password);
  
  // 如果要求强密码，检查强度等级
  if (requireStrong && strengthResult.level === PASSWORD_STRENGTH.LEVELS.WEAK) {
    errors.push(VALIDATION_MESSAGES.SECURITY.WEAK_PASSWORD);
  }
  
  // 添加强度建议到警告
  if (strengthResult.feedback.length > 0) {
    warnings.push(...strengthResult.feedback);
  }
  
  return {
    isValid: errors.length === 0,
    errors,
    warnings,
    metadata: {
      strength: strengthResult
    }
  };
};

/**
 * 验证确认密码
 * 功能描述：验证确认密码是否与原密码一致
 * 采用技术：字符串比较，空值检查
 * 技术优势：确保密码输入的一致性，防止用户输入错误
 * 
 * @param password 原密码
 * @param confirmPassword 确认密码
 * @returns 验证结果
 */
export const validateConfirmPassword = (
  password: string, 
  confirmPassword: string
): ValidationResult => {
  const errors: string[] = [];
  
  // 检查是否为空
  if (!confirmPassword || confirmPassword.length === 0) {
    errors.push(VALIDATION_MESSAGES.REQUIRED.CONFIRM_PASSWORD);
    return { isValid: false, errors };
  }
  
  // 检查是否一致
  if (password !== confirmPassword) {
    errors.push(VALIDATION_MESSAGES.MATCH.PASSWORD_CONFIRM);
  }
  
  return {
    isValid: errors.length === 0,
    errors
  };
};

/**
 * 验证验证码格式
 * 功能描述：验证验证码是否符合格式要求
 * 采用技术：正则表达式验证，长度检查
 * 技术优势：确保验证码格式正确，提升验证成功率
 * 
 * @param captcha 验证码
 * @returns 验证结果
 */
export const validateCaptcha = (captcha: string): ValidationResult => {
  const errors: string[] = [];
  
  // 检查是否为空
  if (!captcha || captcha.trim().length === 0) {
    errors.push(VALIDATION_MESSAGES.REQUIRED.CAPTCHA);
    return { isValid: false, errors };
  }
  
  const trimmedCaptcha = captcha.trim();
  
  // 检查长度
  if (trimmedCaptcha.length !== FIELD_LIMITS.CAPTCHA.LENGTH) {
    errors.push(VALIDATION_MESSAGES.LENGTH.CAPTCHA_EXACT);
  }
  
  // 检查格式
  if (!REGEX_PATTERNS.CAPTCHA.test(trimmedCaptcha)) {
    errors.push(VALIDATION_MESSAGES.FORMAT.CAPTCHA);
  }
  
  return {
    isValid: errors.length === 0,
    errors
  };
};

/**
 * 异步检查用户名可用性
 * 功能描述：向服务器检查用户名是否已被使用
 * 采用技术：异步HTTP请求，防抖优化，错误处理
 * 技术优势：实时可用性检查，提升用户体验
 * 
 * @param username 用户名
 * @returns Promise<验证结果>
 */
export const checkUsernameAvailability: AsyncValidationFunction<string> = async (
  username: string
): Promise<boolean | string> => {
  try {
    // 首先进行格式验证
    const formatResult = validateUsername(username);
    if (!formatResult.isValid) {
      return formatResult.errors[0];
    }
    
    // 发送可用性检查请求
    const response = await fetch(`${AUTH_ENDPOINTS.CHECK_USERNAME}?username=${encodeURIComponent(username)}`);
    
    if (!response.ok) {
      throw new Error('网络请求失败');
    }
    
    const data = await response.json();
    
    if (data.available === false) {
      return VALIDATION_MESSAGES.AVAILABILITY.USERNAME_EXISTS;
    }
    
    return true;
  } catch (error) {
    console.error('Username availability check failed:', error);
    return '无法检查用户名可用性，请稍后重试';
  }
};

/**
 * 异步检查邮箱可用性
 * 功能描述：向服务器检查邮箱是否已被注册
 * 采用技术：异步HTTP请求，防抖优化，错误处理
 * 技术优势：实时可用性检查，防止重复注册
 * 
 * @param email 邮箱地址
 * @returns Promise<验证结果>
 */
export const checkEmailAvailability: AsyncValidationFunction<string> = async (
  email: string
): Promise<boolean | string> => {
  try {
    // 首先进行格式验证
    const formatResult = validateEmail(email);
    if (!formatResult.isValid) {
      return formatResult.errors[0];
    }
    
    // 发送可用性检查请求
    const response = await fetch(`${AUTH_ENDPOINTS.CHECK_EMAIL}?email=${encodeURIComponent(email)}`);
    
    if (!response.ok) {
      throw new Error('网络请求失败');
    }
    
    const data = await response.json();
    
    if (data.available === false) {
      return VALIDATION_MESSAGES.AVAILABILITY.EMAIL_EXISTS;
    }
    
    return true;
  } catch (error) {
    console.error('Email availability check failed:', error);
    return '无法检查邮箱可用性，请稍后重试';
  }
};

/**
 * 创建防抖的可用性检查函数
 * 功能描述：为可用性检查函数添加防抖功能
 * 采用技术：函数柯里化，防抖机制，Promise处理
 * 技术优势：减少不必要的API调用，提升性能
 * 
 * @param checkFunction 原始检查函数
 * @param delay 防抖延迟时间
 * @returns 防抖后的检查函数
 */
export const createDebouncedAvailabilityCheck = (
  checkFunction: AsyncValidationFunction<string>,
  delay: number = 500
) => {
  let timeoutId: NodeJS.Timeout;
  let currentPromise: Promise<boolean | string> | null = null;
  
  return (value: string): Promise<boolean | string> => {
    // 如果有正在进行的检查，取消它
    if (timeoutId) {
      clearTimeout(timeoutId);
    }
    
    // 创建新的Promise
    currentPromise = new Promise((resolve) => {
      timeoutId = setTimeout(async () => {
        try {
          const result = await checkFunction(value);
          resolve(result);
        } catch (error) {
          resolve('检查失败，请稍后重试');
        }
      }, delay);
    });
    
    return currentPromise;
  };
};

/**
 * 验证表单字段
 * 功能描述：通用的表单字段验证函数
 * 采用技术：策略模式，类型安全，可扩展设计
 * 技术优势：统一的验证接口，支持自定义验证规则
 * 
 * @param fieldName 字段名
 * @param value 字段值
 * @param customValidators 自定义验证器
 * @returns 验证结果
 */
export const validateField = (
  fieldName: string,
  value: any,
  customValidators?: ValidationFunction[]
): ValidationResult => {
  let result: ValidationResult;
  
  // 根据字段名选择验证器
  switch (fieldName) {
    case 'username':
      result = validateUsername(value);
      break;
    case 'email':
      result = validateEmail(value);
      break;
    case 'phone':
      result = validatePhone(value);
      break;
    case 'password':
      result = validatePassword(value);
      break;
    case 'captcha':
      result = validateCaptcha(value);
      break;
    default:
      result = { isValid: true, errors: [] };
  }
  
  // 执行自定义验证器
  if (customValidators && customValidators.length > 0) {
    for (const validator of customValidators) {
      const customResult = validator(value);
      if (customResult !== true) {
        result.isValid = false;
        result.errors.push(typeof customResult === 'string' ? customResult : '验证失败');
      }
    }
  }
  
  return result;
};

/**
 * 批量验证表单
 * 功能描述：一次性验证多个表单字段
 * 采用技术：并行验证，结果聚合，错误收集
 * 技术优势：高效的批量验证，完整的错误信息
 * 
 * @param formData 表单数据
 * @param fieldValidators 字段验证器映射
 * @returns 验证结果映射
 */
export const validateForm = (
  formData: Record<string, any>,
  fieldValidators?: Record<string, ValidationFunction[]>
): Record<string, ValidationResult> => {
  const results: Record<string, ValidationResult> = {};
  
  // 验证每个字段
  Object.keys(formData).forEach(fieldName => {
    const value = formData[fieldName];
    const customValidators = fieldValidators?.[fieldName];
    
    results[fieldName] = validateField(fieldName, value, customValidators);
  });
  
  return results;
};

/**
 * 检查表单是否有效
 * 功能描述：检查整个表单的验证状态
 * 采用技术：结果聚合，布尔逻辑
 * 技术优势：快速的表单状态检查，支持提交控制
 * 
 * @param validationResults 验证结果映射
 * @returns 表单是否有效
 */
export const isFormValid = (validationResults: Record<string, ValidationResult>): boolean => {
  return Object.values(validationResults).every(result => result.isValid);
};

/**
 * 获取表单错误摘要
 * 功能描述：收集表单中所有的错误信息
 * 采用技术：数组扁平化，错误聚合
 * 技术优势：完整的错误信息收集，便于用户反馈
 * 
 * @param validationResults 验证结果映射
 * @returns 错误信息数组
 */
export const getFormErrors = (validationResults: Record<string, ValidationResult>): string[] => {
  const errors: string[] = [];
  
  Object.entries(validationResults).forEach(([_, result]) => {
    if (!result.isValid && result.errors.length > 0) {
      errors.push(...result.errors);
    }
  });
  
  return errors;
};