import { motion } from 'framer-motion'
import { useState } from 'react'

interface Order {
  id: string
  date: string
  status: 'pending' | 'shipped' | 'delivered' | 'cancelled'
  total: number
  items: {
    name: string
    image: string
    quantity: number
    price: number
  }[]
}

interface UserInfo {
  name: string
  email: string
  phone: string
  avatar: string
  memberLevel: string
  points: number
  joinDate: string
}

const ProfilePage = () => {
  const [activeTab, setActiveTab] = useState('overview')
  const [userInfo] = useState<UserInfo>({
    name: '张三',
    email: 'zhangsan@example.com',
    phone: '138****8888',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
    memberLevel: '黄金会员',
    points: 2580,
    joinDate: '2023-01-15'
  })

  const [orders] = useState<Order[]>([
    {
      id: 'ORD-2024-001',
      date: '2024-01-15',
      status: 'delivered',
      total: 9999,
      items: [
        {
          name: 'iPhone 15 Pro Max 256GB',
          image: 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=100&h=100&fit=crop',
          quantity: 1,
          price: 9999
        }
      ]
    },
    {
      id: 'ORD-2024-002',
      date: '2024-01-10',
      status: 'shipped',
      total: 3798,
      items: [
        {
          name: 'AirPods Pro 第三代',
          image: 'https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?w=100&h=100&fit=crop',
          quantity: 2,
          price: 1899
        }
      ]
    },
    {
      id: 'ORD-2024-003',
      date: '2024-01-05',
      status: 'pending',
      total: 899,
      items: [
        {
          name: 'Nike Air Max 270',
          image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=100&h=100&fit=crop',
          quantity: 1,
          price: 899
        }
      ]
    }
  ])

  const tabs = [
    { id: 'overview', name: '概览', icon: '📊' },
    { id: 'orders', name: '我的订单', icon: '📦' },
    { id: 'favorites', name: '我的收藏', icon: '❤️' },
    { id: 'addresses', name: '收货地址', icon: '📍' },
    { id: 'settings', name: '账户设置', icon: '⚙️' }
  ]

  const getStatusColor = (status: Order['status']) => {
    switch (status) {
      case 'pending':
        return 'bg-yellow-100 text-yellow-800'
      case 'shipped':
        return 'bg-blue-100 text-blue-800'
      case 'delivered':
        return 'bg-green-100 text-green-800'
      case 'cancelled':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  const getStatusText = (status: Order['status']) => {
    switch (status) {
      case 'pending':
        return '待发货'
      case 'shipped':
        return '已发货'
      case 'delivered':
        return '已送达'
      case 'cancelled':
        return '已取消'
      default:
        return '未知状态'
    }
  }

  const formatPrice = (price: number) => {
    return `¥${price.toLocaleString()}`
  }

  const renderOverview = () => (
    <div className="space-y-6">
      {/* 用户统计 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        {[
          { label: '累计订单', value: '28', icon: '📦', color: 'from-blue-500 to-cyan-500' },
          { label: '积分余额', value: userInfo.points.toLocaleString(), icon: '⭐', color: 'from-yellow-500 to-orange-500' },
          { label: '优惠券', value: '12', icon: '🎫', color: 'from-purple-500 to-pink-500' },
          { label: '收藏商品', value: '45', icon: '❤️', color: 'from-red-500 to-rose-500' }
        ].map((stat, index) => (
          <motion.div
            key={index}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: index * 0.1 }}
            whileHover={{ scale: 1.05 }}
            className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100"
          >
            <div className={`w-12 h-12 bg-gradient-to-r ${stat.color} rounded-xl flex items-center justify-center text-white text-xl mb-4`}>
              {stat.icon}
            </div>
            <div className="text-2xl font-bold text-gray-900 mb-1">
              {stat.value}
            </div>
            <div className="text-gray-600">{stat.label}</div>
          </motion.div>
        ))}
      </div>

      {/* 最近订单 */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, delay: 0.4 }}
        className="bg-white rounded-2xl shadow-lg border border-gray-100"
      >
        <div className="p-6 border-b border-gray-200">
          <h3 className="text-xl font-bold text-gray-900">最近订单</h3>
        </div>
        <div className="p-6">
          <div className="space-y-4">
            {orders.slice(0, 3).map((order) => (
              <div key={order.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
                <div className="flex items-center space-x-4">
                  <img
                    src={order.items[0].image}
                    alt={order.items[0].name}
                    className="w-12 h-12 object-cover rounded-lg"
                  />
                  <div>
                    <div className="font-medium text-gray-900">{order.id}</div>
                    <div className="text-sm text-gray-500">{order.date}</div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="font-bold text-gray-900">{formatPrice(order.total)}</div>
                  <span className={`inline-block px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(order.status)}`}>
                    {getStatusText(order.status)}
                  </span>
                </div>
              </div>
            ))}
          </div>
          <button className="w-full mt-4 text-primary-600 hover:text-primary-700 font-medium transition-colors">
            查看全部订单 →
          </button>
        </div>
      </motion.div>
    </div>
  )

  const renderOrders = () => (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.6 }}
      className="bg-white rounded-2xl shadow-lg border border-gray-100"
    >
      <div className="p-6 border-b border-gray-200">
        <h3 className="text-xl font-bold text-gray-900">我的订单</h3>
      </div>
      <div className="divide-y divide-gray-200">
        {orders.map((order) => (
          <div key={order.id} className="p-6">
            <div className="flex items-center justify-between mb-4">
              <div>
                <div className="font-bold text-gray-900">{order.id}</div>
                <div className="text-sm text-gray-500">下单时间: {order.date}</div>
              </div>
              <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(order.status)}`}>
                {getStatusText(order.status)}
              </span>
            </div>
            
            <div className="space-y-3">
              {order.items.map((item, index) => (
                <div key={index} className="flex items-center space-x-4">
                  <img
                    src={item.image}
                    alt={item.name}
                    className="w-16 h-16 object-cover rounded-lg"
                  />
                  <div className="flex-1">
                    <div className="font-medium text-gray-900">{item.name}</div>
                    <div className="text-sm text-gray-500">数量: {item.quantity}</div>
                  </div>
                  <div className="text-right">
                    <div className="font-bold text-gray-900">{formatPrice(item.price)}</div>
                  </div>
                </div>
              ))}
            </div>
            
            <div className="flex items-center justify-between mt-4 pt-4 border-t border-gray-200">
              <div className="text-lg font-bold text-gray-900">
                订单总计: {formatPrice(order.total)}
              </div>
              <div className="space-x-3">
                {order.status === 'pending' && (
                  <button className="px-4 py-2 border border-red-300 text-red-600 rounded-lg hover:bg-red-50 transition-colors">
                    取消订单
                  </button>
                )}
                {order.status === 'shipped' && (
                  <button className="px-4 py-2 border border-blue-300 text-blue-600 rounded-lg hover:bg-blue-50 transition-colors">
                    查看物流
                  </button>
                )}
                <button className="px-4 py-2 bg-primary-500 text-white rounded-lg hover:bg-primary-600 transition-colors">
                  查看详情
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </motion.div>
  )

  const renderContent = () => {
    switch (activeTab) {
      case 'overview':
        return renderOverview()
      case 'orders':
        return renderOrders()
      case 'favorites':
        return (
          <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8 text-center">
            <div className="text-6xl mb-4">❤️</div>
            <h3 className="text-xl font-bold text-gray-900 mb-2">收藏功能开发中</h3>
            <p className="text-gray-600">敬请期待更多精彩功能</p>
          </div>
        )
      case 'addresses':
        return (
          <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8 text-center">
            <div className="text-6xl mb-4">📍</div>
            <h3 className="text-xl font-bold text-gray-900 mb-2">地址管理功能开发中</h3>
            <p className="text-gray-600">敬请期待更多精彩功能</p>
          </div>
        )
      case 'settings':
        return (
          <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8 text-center">
            <div className="text-6xl mb-4">⚙️</div>
            <h3 className="text-xl font-bold text-gray-900 mb-2">设置功能开发中</h3>
            <p className="text-gray-600">敬请期待更多精彩功能</p>
          </div>
        )
      default:
        return renderOverview()
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 pt-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* 用户信息卡片 */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="bg-gradient-to-r from-primary-500 to-secondary-500 rounded-2xl shadow-lg p-8 mb-8 text-white"
        >
          <div className="flex items-center space-x-6">
            <img
              src={userInfo.avatar}
              alt={userInfo.name}
              className="w-20 h-20 rounded-full border-4 border-white/20"
            />
            <div className="flex-1">
              <h1 className="text-3xl font-bold mb-2">{userInfo.name}</h1>
              <div className="flex items-center space-x-4 text-white/80">
                <span>{userInfo.memberLevel}</span>
                <span>•</span>
                <span>积分: {userInfo.points.toLocaleString()}</span>
                <span>•</span>
                <span>加入时间: {userInfo.joinDate}</span>
              </div>
            </div>
            <button className="bg-white/20 hover:bg-white/30 px-6 py-3 rounded-xl font-medium transition-colors">
              编辑资料
            </button>
          </div>
        </motion.div>

        <div className="flex flex-col lg:flex-row gap-8">
          {/* 侧边栏导航 */}
          <motion.div
            initial={{ opacity: 0, x: -30 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.6 }}
            className="lg:w-1/4"
          >
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6 sticky top-24">
              <nav className="space-y-2">
                {tabs.map((tab) => (
                  <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id)}
                    className={`w-full flex items-center space-x-3 px-4 py-3 rounded-xl font-medium transition-all duration-300 ${
                      activeTab === tab.id
                        ? 'bg-gradient-to-r from-primary-500 to-secondary-500 text-white shadow-lg'
                        : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                    }`}
                  >
                    <span className="text-xl">{tab.icon}</span>
                    <span>{tab.name}</span>
                  </button>
                ))}
              </nav>
            </div>
          </motion.div>

          {/* 主要内容区域 */}
          <motion.div
            key={activeTab}
            initial={{ opacity: 0, x: 30 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.6 }}
            className="lg:w-3/4"
          >
            {renderContent()}
          </motion.div>
        </div>
      </div>
    </div>
  )
}

export default ProfilePage