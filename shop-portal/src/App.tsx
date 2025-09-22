import { Routes, Route } from 'react-router-dom'
import { Suspense, useEffect } from 'react'
import HomePage from './pages/HomePage'
import AuthPage from './pages/AuthPage'
import AdminDashboard from './pages/Dashboard'
import UserCenter from './pages/UserCenter'
import ProfileSettingsPage from './pages/ProfileSettingsPage'
import Layout from './components/Layout'
import LoadingSpinner from './components/LoadingSpinner'
import RoleBasedNavigation from './components/RoleBasedNavigation'
import { AdminGuard, UserGuard } from './components/auth/RoleGuard'
import { useAuthStore } from './store/authStore'


function App() {
  // 获取认证store的初始化方法和加载状态
  const { initialize, isLoading } = useAuthStore();

  // 在应用启动时初始化认证状态
  useEffect(() => {
    initialize().catch(() => {
      // 静默处理初始化失败
    });
  }, [initialize]);

  // 在初始化完成前显示加载界面
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-indigo-600 mx-auto"></div>
          <h2 className="mt-4 text-xl font-semibold text-gray-900">正在加载应用...</h2>
          <p className="mt-2 text-gray-600">请稍候，正在初始化用户认证状态</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Layout>
        <Suspense fallback={<LoadingSpinner />}>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/auth" element={<AuthPage />} />
            <Route path="/login" element={<AuthPage />} />
            <Route path="/register" element={<AuthPage />} />
            
            {/* 角色导航路由 - 登录成功后根据角色跳转 */}
            <Route path="/role-navigation" element={<RoleBasedNavigation />} />
            
            {/* 管理员路由 */}
            <Route path="/admin/dashboard" element={
              <AdminGuard>
                <AdminDashboard />
              </AdminGuard>
            } />
            
            {/* 普通用户路由 */}
            <Route path="/user/center" element={
              <UserGuard>
                <UserCenter />
              </UserGuard>
            } />
            
            {/* 个人设置页面 */}
            <Route path="/profile" element={
              <UserGuard>
                <ProfileSettingsPage />
              </UserGuard>
            } />
          </Routes>
        </Suspense>
      </Layout>

    </div>
  )
}

export default App