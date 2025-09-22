/**
 * 表单验证规则常量配置
 * 功能描述：定义所有表单验证使用的规则、模式和错误消息
 * 采用技术：Yup验证库 + 正则表达式，提供完整的客户端验证
 * 技术优势：统一的验证标准，确保数据质量，提升用户体验和安全性
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */

import { string, ref } from 'yup';

/**
 * 字段长度限制常量
 * 功能描述：定义各个字段的长度限制
 * 采用技术：常量定义，便于统一管理和修改
 * 技术优势：集中管理长度限制，确保前后端一致性
 */
export const FIELD_LIMITS = {
  /** 用户名长度限制 */
  USERNAME: {
    MIN: 3,
    MAX: 20
  },
  /** 密码长度限制 */
  PASSWORD: {
    MIN: 8,
    MAX: 128
  },
  /** 邮箱长度限制 */
  EMAIL: {
    MAX: 254
  },
  /** 昵称长度限制 */
  NICKNAME: {
    MIN: 2,
    MAX: 50
  },
  /** 手机号长度 */
  PHONE: {
    LENGTH: 11
  },
  /** 验证码长度 */
  CAPTCHA: {
    LENGTH: 6
  },
  /** 重置令牌长度 */
  RESET_TOKEN: {
    MIN: 32,
    MAX: 128
  }
} as const;

/**
 * 正则表达式模式常量
 * 功能描述：定义各种字段的验证正则表达式
 * 采用技术：正则表达式，提供精确的格式验证
 * 技术优势：标准化的格式验证，防止无效数据输入
 */
export const REGEX_PATTERNS = {
  /** 用户名格式：字母、数字、下划线，不能以数字开头 */
  USERNAME: /^[a-zA-Z][a-zA-Z0-9_]*$/,
  
  /** 密码强度：至少包含字母和数字，可包含特殊字符 */
  PASSWORD: {
    /** 基础密码：至少包含字母和数字 */
    BASIC: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/,
    /** 中等强度：包含大小写字母和数字 */
    MEDIUM: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/,
    /** 高强度：包含大小写字母、数字和特殊字符 */
    STRONG: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/
  },
  
  /** 邮箱格式：标准邮箱格式 */
  EMAIL: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
  
  /** 手机号格式：中国大陆手机号 */
  PHONE: {
    /** 中国大陆手机号 */
    CN: /^1[3-9]\d{9}$/,
    /** 国际手机号格式 */
    INTERNATIONAL: /^\+?[1-9]\d{1,14}$/
  },
  
  /** 昵称格式：中文、英文、数字，不包含特殊字符 */
  NICKNAME: /^[\u4e00-\u9fa5a-zA-Z0-9\s]+$/,
  
  /** 验证码格式：6位数字或字母 */
  CAPTCHA: /^[A-Za-z0-9]{6}$/,
  
  /** URL格式验证 */
  URL: /^https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)$/,
  
  /** 身份证号格式（中国大陆） */
  ID_CARD: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
} as const;

/**
 * 错误消息常量
 * 功能描述：定义各种验证错误的提示消息
 * 采用技术：国际化友好的错误消息设计
 * 技术优势：用户友好的错误提示，提升用户体验
 */
export const VALIDATION_MESSAGES = {
  /** 必填字段错误消息 */
  REQUIRED: {
    USERNAME: '请输入用户名',
    PASSWORD: '请输入密码',
    CONFIRM_PASSWORD: '请确认密码',
    EMAIL: '请输入邮箱地址',
    PHONE: '请输入手机号码',
    NICKNAME: '请输入昵称',
    CAPTCHA: '请输入验证码',
    CURRENT_PASSWORD: '请输入当前密码',
    NEW_PASSWORD: '请输入新密码',
    RESET_TOKEN: '重置令牌不能为空'
  },
  
  /** 格式错误消息 */
  FORMAT: {
    USERNAME: '用户名只能包含字母、数字和下划线，且不能以数字开头',
    PASSWORD: '密码至少8位，必须包含字母和数字',
    EMAIL: '请输入有效的邮箱地址',
    PHONE: '请输入有效的手机号码',
    NICKNAME: '昵称只能包含中文、英文和数字',
    CAPTCHA: '验证码格式不正确',
    URL: '请输入有效的网址',
    ID_CARD: '请输入有效的身份证号码'
  },
  
  /** 长度错误消息 */
  LENGTH: {
    USERNAME_MIN: `用户名至少${FIELD_LIMITS.USERNAME.MIN}位`,
    USERNAME_MAX: `用户名最多${FIELD_LIMITS.USERNAME.MAX}位`,
    PASSWORD_MIN: `密码至少${FIELD_LIMITS.PASSWORD.MIN}位`,
    PASSWORD_MAX: `密码最多${FIELD_LIMITS.PASSWORD.MAX}位`,
    EMAIL_MAX: `邮箱地址最多${FIELD_LIMITS.EMAIL.MAX}位`,
    NICKNAME_MIN: `昵称至少${FIELD_LIMITS.NICKNAME.MIN}位`,
    NICKNAME_MAX: `昵称最多${FIELD_LIMITS.NICKNAME.MAX}位`,
    PHONE_EXACT: `手机号必须是${FIELD_LIMITS.PHONE.LENGTH}位`,
    CAPTCHA_EXACT: `验证码必须是${FIELD_LIMITS.CAPTCHA.LENGTH}位`
  },
  
  /** 匹配错误消息 */
  MATCH: {
    PASSWORD_CONFIRM: '两次输入的密码不一致',
    EMAIL_CONFIRM: '两次输入的邮箱不一致'
  },
  
  /** 可用性错误消息 */
  AVAILABILITY: {
    USERNAME_EXISTS: '用户名已存在，请选择其他用户名',
    EMAIL_EXISTS: '邮箱已被注册，请使用其他邮箱',
    PHONE_EXISTS: '手机号已被注册，请使用其他手机号'
  },
  
  /** 安全性错误消息 */
  SECURITY: {
    WEAK_PASSWORD: '密码强度不足，建议包含大小写字母、数字和特殊字符',
    COMMON_PASSWORD: '密码过于简单，请设置更复杂的密码',
    CURRENT_PASSWORD_WRONG: '当前密码错误',
    NEW_PASSWORD_SAME: '新密码不能与当前密码相同'
  }
} as const;

/**
 * 密码强度等级常量
 * 功能描述：定义密码强度的等级和评分标准
 * 采用技术：多维度密码强度评估
 * 技术优势：引导用户设置安全密码，提升账户安全性
 */
export const PASSWORD_STRENGTH = {
  /** 强度等级 */
  LEVELS: {
    WEAK: 'weak',
    MEDIUM: 'medium',
    STRONG: 'strong',
    VERY_STRONG: 'very_strong'
  },
  
  /** 强度评分标准 */
  SCORING: {
    /** 长度评分 */
    LENGTH: {
      MIN_SCORE: 0,
      MAX_SCORE: 25,
      OPTIMAL_LENGTH: 12
    },
    /** 字符类型评分 */
    COMPLEXITY: {
      LOWERCASE: 5,
      UPPERCASE: 5,
      NUMBERS: 5,
      SYMBOLS: 10
    },
    /** 模式评分 */
    PATTERNS: {
      NO_REPEAT: 10,
      NO_SEQUENCE: 10,
      NO_COMMON: 15
    }
  },
  
  /** 强度阈值 */
  THRESHOLDS: {
    WEAK: 30,
    MEDIUM: 60,
    STRONG: 80,
    VERY_STRONG: 90
  }
} as const;

/**
 * Yup验证模式定义
 * 功能描述：使用Yup库定义可复用的验证模式
 * 采用技术：Yup验证库，提供强大的验证功能
 * 技术优势：类型安全的验证，支持异步验证和自定义规则
 */
export const YUP_SCHEMAS = {
  /** 用户名验证模式 */
  username: string()
    .required(VALIDATION_MESSAGES.REQUIRED.USERNAME)
    .min(FIELD_LIMITS.USERNAME.MIN, VALIDATION_MESSAGES.LENGTH.USERNAME_MIN)
    .max(FIELD_LIMITS.USERNAME.MAX, VALIDATION_MESSAGES.LENGTH.USERNAME_MAX)
    .matches(REGEX_PATTERNS.USERNAME, VALIDATION_MESSAGES.FORMAT.USERNAME),
  
  /** 密码验证模式 */
  password: string()
    .required(VALIDATION_MESSAGES.REQUIRED.PASSWORD)
    .min(FIELD_LIMITS.PASSWORD.MIN, VALIDATION_MESSAGES.LENGTH.PASSWORD_MIN)
    .max(FIELD_LIMITS.PASSWORD.MAX, VALIDATION_MESSAGES.LENGTH.PASSWORD_MAX)
    .matches(REGEX_PATTERNS.PASSWORD.BASIC, VALIDATION_MESSAGES.FORMAT.PASSWORD),
  
  /** 确认密码验证模式 */
  confirmPassword: string()
    .required(VALIDATION_MESSAGES.REQUIRED.CONFIRM_PASSWORD)
    .oneOf([ref('password')], VALIDATION_MESSAGES.MATCH.PASSWORD_CONFIRM),
  
  /** 邮箱验证模式 */
  email: string()
    .required(VALIDATION_MESSAGES.REQUIRED.EMAIL)
    .max(FIELD_LIMITS.EMAIL.MAX, VALIDATION_MESSAGES.LENGTH.EMAIL_MAX)
    .matches(REGEX_PATTERNS.EMAIL, VALIDATION_MESSAGES.FORMAT.EMAIL),
  
  /** 手机号验证模式 */
  phone: string()
    .required(VALIDATION_MESSAGES.REQUIRED.PHONE)
    .length(FIELD_LIMITS.PHONE.LENGTH, VALIDATION_MESSAGES.LENGTH.PHONE_EXACT)
    .matches(REGEX_PATTERNS.PHONE.CN, VALIDATION_MESSAGES.FORMAT.PHONE),
  
  /** 昵称验证模式 */
  nickname: string()
    .required(VALIDATION_MESSAGES.REQUIRED.NICKNAME)
    .min(FIELD_LIMITS.NICKNAME.MIN, VALIDATION_MESSAGES.LENGTH.NICKNAME_MIN)
    .max(FIELD_LIMITS.NICKNAME.MAX, VALIDATION_MESSAGES.LENGTH.NICKNAME_MAX)
    .matches(REGEX_PATTERNS.NICKNAME, VALIDATION_MESSAGES.FORMAT.NICKNAME),
  
  /** 验证码验证模式 */
  captcha: string()
    .required(VALIDATION_MESSAGES.REQUIRED.CAPTCHA)
    .length(FIELD_LIMITS.CAPTCHA.LENGTH, VALIDATION_MESSAGES.LENGTH.CAPTCHA_EXACT)
    .matches(REGEX_PATTERNS.CAPTCHA, VALIDATION_MESSAGES.FORMAT.CAPTCHA)
} as const;

/**
 * 表单验证配置常量
 * 功能描述：定义表单验证的配置参数
 * 采用技术：可配置的验证行为
 * 技术优势：灵活的验证配置，适应不同场景需求
 */
export const VALIDATION_CONFIG = {
  /** 验证触发时机 */
  TRIGGER: {
    /** 输入时验证 */
    ON_CHANGE: 'onChange',
    /** 失焦时验证 */
    ON_BLUR: 'onBlur',
    /** 提交时验证 */
    ON_SUBMIT: 'onSubmit'
  },
  
  /** 防抖延迟时间（毫秒） */
  DEBOUNCE_DELAY: {
    /** 输入验证防抖 */
    INPUT: 300,
    /** 可用性检查防抖 */
    AVAILABILITY: 500,
    /** 密码强度检查防抖 */
    PASSWORD_STRENGTH: 200
  },
  
  /** 验证模式 */
  MODE: {
    /** 严格模式：所有规则都必须通过 */
    STRICT: 'strict',
    /** 宽松模式：允许部分规则失败 */
    LOOSE: 'loose',
    /** 渐进模式：根据用户输入逐步验证 */
    PROGRESSIVE: 'progressive'
  },
  
  /** 错误显示配置 */
  ERROR_DISPLAY: {
    /** 显示第一个错误 */
    FIRST_ERROR: 'first',
    /** 显示所有错误 */
    ALL_ERRORS: 'all',
    /** 按优先级显示错误 */
    PRIORITY: 'priority'
  }
} as const;

/**
 * 常用密码黑名单
 * 功能描述：定义常用的弱密码列表，用于密码强度检查
 * 采用技术：黑名单过滤机制
 * 技术优势：防止用户使用常见弱密码，提升账户安全性
 */
export const COMMON_PASSWORDS = [
  '12345678',
  'password',
  '123456789',
  '12345678',
  'qwerty',
  'abc123',
  'password123',
  'admin',
  'letmein',
  'welcome',
  '123123',
  'password1',
  'qwerty123',
  '111111',
  '123321',
  'dragon',
  'sunshine',
  'master',
  '666666',
  '888888',
  'abcdef',
  '123abc',
  'qazwsx',
  'zxcvbn'
] as const;

/**
 * 验证工具函数类型
 * 功能描述：定义验证相关的工具函数类型
 * 采用技术：TypeScript函数类型定义
 * 技术优势：类型安全的验证函数，便于开发和维护
 */
export type ValidationFunction<T = any> = (value: T) => boolean | string;
export type AsyncValidationFunction<T = any> = (value: T) => Promise<boolean | string>;
export type PasswordStrengthResult = {
  score: number;
  level: string;
  feedback: string[];
};

/**
 * 验证结果类型
 * 功能描述：定义验证结果的数据结构
 * 采用技术：结构化的验证结果
 * 技术优势：统一的验证结果格式，便于处理和显示
 */
export interface ValidationResult {
  /** 是否验证通过 */
  isValid: boolean;
  /** 错误消息列表 */
  errors: string[];
  /** 警告消息列表 */
  warnings?: string[];
  /** 验证元数据 */
  metadata?: Record<string, any>;
}

/**
 * 字段验证状态类型
 * 功能描述：定义单个字段的验证状态
 * 采用技术：状态机模式
 * 技术优势：清晰的字段状态管理，便于UI反馈
 */
export interface FieldValidationState {
  /** 字段值 */
  value: any;
  /** 是否已验证 */
  isValidated: boolean;
  /** 是否验证通过 */
  isValid: boolean;
  /** 是否正在验证 */
  isValidating: boolean;
  /** 错误消息 */
  error: string | null;
  /** 警告消息 */
  warning: string | null;
  /** 是否已触摸（用户已交互） */
  isTouched: boolean;
  /** 是否已修改 */
  isDirty: boolean;
}