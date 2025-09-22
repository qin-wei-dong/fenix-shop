import { useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useAuthStore } from '../store/authStore'

const Header = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const { isAuthenticated, user, logout } = useAuthStore()

  // 处理登出
  const handleLogout = async () => {
    try {
      await logout()
    } catch (error) {
      console.error('Logout failed:', error)
    }
  }

  const navItems = [
    { name: '首页', path: '/' },
    { name: '商品', path: '/products' },
    { name: '分类', path: '/categories' },
    { name: '品牌', path: '/brands' },
  ]

  return (
    <header className="bg-white shadow-lg sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-gradient-to-r from-primary-500 to-secondary-500 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-lg">凤</span>
            </div>
            <span className="text-xl font-bold text-gray-900">凤凰商城</span>
          </Link>

          {/* Navigation */}
          <nav className="hidden md:flex space-x-8">
            {navItems.map((item) => (
              <Link
                key={item.name}
                to={item.path}
                className="text-gray-700 hover:text-primary-600 px-3 py-2 rounded-md text-sm font-medium transition-colors"
              >
                {item.name}
              </Link>
            ))}
          </nav>

          {/* Search Bar */}
          <div className="hidden md:flex flex-1 max-w-lg mx-8">
            <div className="relative w-full">
              <input
                type="text"
                placeholder="搜索商品..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
            </div>
          </div>

          {/* User Actions */}
          <div className="flex items-center space-x-4">
            {/* 根据用户状态显示不同内容 */}
            {isAuthenticated && user ? (
              // 已登录状态
              <div className="flex items-center space-x-3">
                {/* 用户信息 - 高端设计 */}
                <Link 
                  to="/user/center" 
                  className="group relative flex items-center space-x-3 px-3 h-[42px] bg-gradient-to-r from-white via-gray-50 to-white hover:from-primary-50 hover:via-primary-100 hover:to-primary-50 border border-gray-200 hover:border-primary-300 rounded-lg shadow-sm hover:shadow-md transition-all duration-300 ease-in-out overflow-hidden"
                >
                  {/* 背景光效 */}
                  <div className="absolute inset-0 bg-gradient-to-r from-transparent via-primary-100 to-transparent opacity-0 group-hover:opacity-20 transform -skew-x-12 -translate-x-full group-hover:translate-x-full transition-all duration-500 ease-out"></div>
                  
                  {/* 头像容器 */}
                  <div className="relative">
                    <img
                      className="h-7 w-7 rounded-full ring-1 ring-gray-200 group-hover:ring-primary-300 transition-all duration-300 shadow-sm"
                      src={user.avatar || '/default-avatar.png'}
                      alt={user.username || '用户头像'}
                    />
                    {/* 在线状态指示器 */}
                    <div className="absolute -bottom-0.5 -right-0.5 h-2.5 w-2.5 bg-green-400 border border-white rounded-full"></div>
                  </div>
                  
                  {/* 用户信息文字 */}
                  <div className="hidden sm:flex flex-col relative z-10">
                    <span className="text-sm font-medium text-gray-800 group-hover:text-primary-700 transition-colors duration-300">
                      {user.nickname || user.username}
                    </span>
                    <span className="text-xs text-gray-500 group-hover:text-primary-500 transition-colors duration-300">
                      {user.phone ? user.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '手机号未绑定'}
                    </span>
                  </div>
                  
                  {/* 箭头图标 */}
                  <svg 
                    className="h-3.5 w-3.5 text-gray-400 group-hover:text-primary-500 group-hover:translate-x-0.5 transition-all duration-300" 
                    fill="none" 
                    stroke="currentColor" 
                    viewBox="0 0 24 24"
                  >
                    <path 
                      strokeLinecap="round" 
                      strokeLinejoin="round" 
                      strokeWidth={2} 
                      d="M9 5l7 7-7 7" 
                    />
                  </svg>
                </Link>
                
                {/* 登出按钮 - 高端设计 */}
                <button
                  onClick={handleLogout}
                  className="group relative flex items-center justify-center space-x-2 px-3 h-[42px] bg-gradient-to-r from-slate-600 via-slate-700 to-slate-800 hover:from-slate-700 hover:via-slate-800 hover:to-slate-900 text-white font-medium rounded-lg shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-300 ease-in-out overflow-hidden border border-slate-500 hover:border-slate-400"
                >
                  {/* 动态背景光泽效果 */}
                  <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white to-transparent opacity-0 group-hover:opacity-20 transform -skew-x-12 -translate-x-full group-hover:translate-x-full transition-all duration-700 ease-out"></div>
                  
                  {/* 图标 */}
                  <svg 
                    className="h-4 w-4 transform group-hover:rotate-12 transition-transform duration-300" 
                    fill="none" 
                    stroke="currentColor" 
                    viewBox="0 0 24 24"
                  >
                    <path 
                      strokeLinecap="round" 
                      strokeLinejoin="round" 
                      strokeWidth={2.5} 
                      d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" 
                    />
                  </svg>
                  
                  {/* 文字 */}
                  <span className="text-sm font-semibold hidden sm:block relative z-10 tracking-wide">
                    登出
                  </span>
                  
                  {/* 边框光效 */}
                  <div className="absolute inset-0 rounded-lg border border-primary-300 opacity-0 group-hover:opacity-30 transition-opacity duration-300"></div>
                </button>
              </div>
            ) : (
              // 未登录状态
              <Link to="/login" className="flex items-center space-x-1 px-3 py-2 text-gray-700 hover:text-primary-600 transition-colors rounded-md">
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                <span className="text-sm font-medium">登录</span>
              </Link>
            )}
          </div>

          {/* Mobile menu button */}
          <button
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            className="md:hidden p-2 text-gray-700 hover:text-primary-600 transition-colors"
          >
            <svg className="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={isMenuOpen ? "M6 18L18 6M6 6l12 12" : "M4 6h16M4 12h16M4 18h16"} />
            </svg>
          </button>
        </div>
      </div>

      {/* Mobile menu */}
      {isMenuOpen && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -10 }}
          className="md:hidden bg-white border-t border-gray-200"
        >
          <div className="px-2 pt-2 pb-3 space-y-1">
            {navItems.map((item) => (
              <Link
                key={item.name}
                to={item.path}
                className="block px-3 py-2 text-gray-700 hover:text-primary-600 hover:bg-gray-50 rounded-md text-base font-medium transition-colors"
                onClick={() => setIsMenuOpen(false)}
              >
                {item.name}
              </Link>
            ))}
          </div>
          <div className="px-4 py-3 border-t border-gray-200">
            <input
              type="text"
              placeholder="搜索商品..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
        </motion.div>
      )}
    </header>
  )
}

export default Header