/**
 * 通用类型定义
 */

// 基础ID类型
export type ID = string | number;

// 时间戳类型
export type Timestamp = number;

// 日期字符串类型
export type DateString = string;

// 可选字段类型
export type Optional<T, K extends keyof T> = Omit<T, K> & Partial<Pick<T, K>>;

// 必需字段类型
export type RequiredFields<T, K extends keyof T> = T & Required<Pick<T, K>>;

// 深度可选类型
export type DeepPartial<T> = {
  [P in keyof T]?: T[P] extends object ? DeepPartial<T[P]> : T[P];
};

// 深度必需类型
export type DeepRequired<T> = {
  [P in keyof T]-?: T[P] extends object ? DeepRequired<T[P]> : T[P];
};

// 键值对类型
export type KeyValuePair<K = string, V = any> = {
  key: K;
  value: V;
};

// 选项类型
export interface Option<T = any> {
  label: string;
  value: T;
  disabled?: boolean;
  description?: string;
  icon?: string;
}

// 选择器选项组
export interface OptionGroup<T = any> {
  label: string;
  options: Option<T>[];
  disabled?: boolean;
}

// 排序方向
export type SortDirection = 'asc' | 'desc';

// 排序配置
export interface SortConfig {
  field: string;
  direction: SortDirection;
}

// 过滤操作符
export type FilterOperator = 'eq' | 'ne' | 'gt' | 'gte' | 'lt' | 'lte' | 'like' | 'in' | 'nin' | 'between';

// 过滤条件
export interface FilterCondition {
  field: string;
  operator: FilterOperator;
  value: any;
  values?: any[]; // 用于 in, nin, between 操作符
}

// 查询参数
export interface QueryParams {
  page?: number;
  size?: number;
  sort?: SortConfig[];
  filters?: FilterCondition[];
  search?: string;
  [key: string]: any;
}

// 响应状态
export enum ResponseStatus {
  SUCCESS = 'success',
  ERROR = 'error',
  WARNING = 'warning',
  INFO = 'info'
}

// 加载状态
export enum LoadingState {
  IDLE = 'idle',
  LOADING = 'loading',
  SUCCESS = 'success',
  ERROR = 'error'
}

// 主题类型
export type Theme = 'light' | 'dark' | 'auto';

// 语言类型
export type Language = 'zh-CN' | 'en-US' | 'ja-JP' | 'ko-KR';

// 货币类型
export type Currency = 'CNY' | 'USD' | 'EUR' | 'JPY' | 'KRW';

// 尺寸类型
export type Size = 'xs' | 'sm' | 'md' | 'lg' | 'xl';

// 颜色类型
export type Color = 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info';

// 位置类型
export type Position = 'top' | 'bottom' | 'left' | 'right' | 'center';

// 对齐方式
export type Alignment = 'start' | 'center' | 'end' | 'stretch';

// 方向类型
export type Direction = 'horizontal' | 'vertical';

// 设备类型
export type DeviceType = 'mobile' | 'tablet' | 'desktop';

// 浏览器类型
export type BrowserType = 'chrome' | 'firefox' | 'safari' | 'edge' | 'ie' | 'opera' | 'unknown';

// 操作系统类型
export type OSType = 'windows' | 'macos' | 'linux' | 'ios' | 'android' | 'unknown';

// 环境类型
export type Environment = 'development' | 'staging' | 'production';

// 日志级别
export enum LogLevel {
  DEBUG = 'debug',
  INFO = 'info',
  WARN = 'warn',
  ERROR = 'error'
}

// 日志条目
export interface LogEntry {
  level: LogLevel;
  message: string;
  timestamp: number;
  source?: string;
  data?: any;
  stack?: string;
}

// 错误类型
export interface ErrorInfo {
  code: string;
  message: string;
  details?: any;
  timestamp: number;
  stack?: string;
  source?: string;
}

// 成功响应
export interface SuccessResponse<T = any> {
  success: true;
  data: T;
  message?: string;
  timestamp: number;
}

// 错误响应
export interface ErrorResponse {
  success: false;
  error: ErrorInfo;
  message: string;
  timestamp: number;
}

// 统一响应类型
export type UnifiedResponse<T = any> = SuccessResponse<T> | ErrorResponse;

// 事件类型
export interface EventInfo<T = any> {
  type: string;
  data: T;
  timestamp: number;
  source?: string;
  target?: string;
}

// 回调函数类型
export type Callback<T = void> = () => T;
export type CallbackWithParam<P, T = void> = (param: P) => T;
export type AsyncCallback<T = void> = () => Promise<T>;
export type AsyncCallbackWithParam<P, T = void> = (param: P) => Promise<T>;

// 事件处理器类型
export type EventHandler<T = any> = (event: EventInfo<T>) => void;
export type AsyncEventHandler<T = any> = (event: EventInfo<T>) => Promise<void>;

// 验证规则
export interface ValidationRule {
  required?: boolean;
  min?: number;
  max?: number;
  minLength?: number;
  maxLength?: number;
  pattern?: RegExp;
  custom?: (value: any) => boolean | string;
  message?: string;
}

// 验证结果
export interface ValidationResult {
  valid: boolean;
  errors: string[];
}

// 表单字段
export interface FormField<T = any> {
  name: string;
  value: T;
  label: string;
  type: string;
  placeholder?: string;
  disabled?: boolean;
  readonly?: boolean;
  required?: boolean;
  rules?: ValidationRule[];
  options?: Option<T>[];
  description?: string;
  error?: string;
}

// 表单配置
export interface FormConfig {
  fields: FormField[];
  layout?: 'horizontal' | 'vertical' | 'inline';
  labelWidth?: string;
  size?: Size;
  disabled?: boolean;
  readonly?: boolean;
}

// 表格列配置
export interface TableColumn<T = any> {
  key: string;
  title: string;
  dataIndex?: string;
  width?: number | string;
  align?: Alignment;
  sortable?: boolean;
  filterable?: boolean;
  render?: (value: any, record: T, index: number) => React.ReactNode;
  fixed?: 'left' | 'right';
  ellipsis?: boolean;
}

// 表格配置
export interface TableConfig<T = any> {
  columns: TableColumn<T>[];
  rowKey: string;
  size?: Size;
  bordered?: boolean;
  striped?: boolean;
  hoverable?: boolean;
  loading?: boolean;
  pagination?: boolean | PaginationConfig;
  selection?: SelectionConfig<T>;
}

// 分页配置
export interface PaginationConfig {
  current: number;
  pageSize: number;
  total: number;
  showSizeChanger?: boolean;
  showQuickJumper?: boolean;
  showTotal?: boolean;
  pageSizeOptions?: number[];
}

// 选择配置
export interface SelectionConfig<T = any> {
  type: 'checkbox' | 'radio';
  selectedRowKeys: string[];
  onChange: (selectedRowKeys: string[], selectedRows: T[]) => void;
  getCheckboxProps?: (record: T) => { disabled?: boolean };
}

// 菜单项
export interface MenuItem {
  key: string;
  label: string;
  icon?: string;
  path?: string;
  children?: MenuItem[];
  disabled?: boolean;
  hidden?: boolean;
  badge?: string | number;
  permissions?: string[];
}

// 面包屑项
export interface BreadcrumbItem {
  title: string;
  path?: string;
  icon?: string;
}

// 标签页
export interface TabItem {
  key: string;
  label: string;
  content: React.ReactNode;
  disabled?: boolean;
  closable?: boolean;
  icon?: string;
}

// 步骤项
export interface StepItem {
  title: string;
  description?: string;
  icon?: string;
  status?: 'wait' | 'process' | 'finish' | 'error';
}

// 时间范围
export interface TimeRange {
  start: Date | string;
  end: Date | string;
}

// 坐标点
export interface Point {
  x: number;
  y: number;
}

// 矩形区域
export interface Rectangle {
  x: number;
  y: number;
  width: number;
  height: number;
}

// 文件信息
export interface FileInfo {
  name: string;
  size: number;
  type: string;
  lastModified: number;
  url?: string;
  thumbnailUrl?: string;
}

// 媒体查询断点
export interface Breakpoints {
  xs: number;
  sm: number;
  md: number;
  lg: number;
  xl: number;
  xxl: number;
}

// 动画配置
export interface AnimationConfig {
  duration: number;
  easing: string;
  delay?: number;
  iterations?: number;
  direction?: 'normal' | 'reverse' | 'alternate' | 'alternate-reverse';
  fillMode?: 'none' | 'forwards' | 'backwards' | 'both';
}

// 拖拽数据
export interface DragData<T = any> {
  type: string;
  data: T;
  source: string;
}

// 拖拽事件
export interface DragEvent<T = any> {
  dragData: DragData<T>;
  target: string;
  position: Point;
}

// 键盘快捷键
export interface Shortcut {
  key: string;
  ctrl?: boolean;
  alt?: boolean;
  shift?: boolean;
  meta?: boolean;
  action: string;
  description?: string;
}

// 权限信息
export interface Permission {
  code: string;
  name: string;
  description?: string;
  resource?: string;
  action?: string;
}

// 角色信息
export interface Role {
  code: string;
  name: string;
  description?: string;
  permissions: Permission[];
}

// 配置项
export interface ConfigItem<T = any> {
  key: string;
  value: T;
  type: 'string' | 'number' | 'boolean' | 'object' | 'array';
  description?: string;
  defaultValue?: T;
  required?: boolean;
  validation?: ValidationRule;
}