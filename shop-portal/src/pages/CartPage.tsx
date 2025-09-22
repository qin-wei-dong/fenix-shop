import { motion } from 'framer-motion'
import { useState } from 'react'

interface CartItem {
  id: number
  name: string
  price: number
  originalPrice?: number
  image: string
  quantity: number
  selected: boolean
  inStock: boolean
  brand: string
}

const CartPage = () => {
  const [cartItems, setCartItems] = useState<CartItem[]>([
    {
      id: 1,
      name: 'iPhone 15 Pro Max 256GB',
      price: 9999,
      originalPrice: 10999,
      image: 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400&h=400&fit=crop',
      quantity: 1,
      selected: true,
      inStock: true,
      brand: 'Apple'
    },
    {
      id: 2,
      name: 'AirPods Pro 第三代',
      price: 1899,
      originalPrice: 2199,
      image: 'https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?w=400&h=400&fit=crop',
      quantity: 2,
      selected: true,
      inStock: true,
      brand: 'Apple'
    },
    {
      id: 3,
      name: 'Nike Air Max 270',
      price: 899,
      originalPrice: 1299,
      image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop',
      quantity: 1,
      selected: false,
      inStock: false,
      brand: 'Nike'
    }
  ])

  const updateQuantity = (id: number, newQuantity: number) => {
    if (newQuantity < 1) return
    setCartItems(items =>
      items.map(item =>
        item.id === id ? { ...item, quantity: newQuantity } : item
      )
    )
  }

  const toggleSelection = (id: number) => {
    setCartItems(items =>
      items.map(item =>
        item.id === id ? { ...item, selected: !item.selected } : item
      )
    )
  }

  const removeItem = (id: number) => {
    setCartItems(items => items.filter(item => item.id !== id))
  }

  const selectAll = () => {
    const allSelected = cartItems.every(item => item.selected)
    setCartItems(items =>
      items.map(item => ({ ...item, selected: !allSelected }))
    )
  }

  const selectedItems = cartItems.filter(item => item.selected && item.inStock)
  const totalPrice = selectedItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const totalOriginalPrice = selectedItems.reduce((sum, item) => {
    const originalPrice = item.originalPrice || item.price
    return sum + originalPrice * item.quantity
  }, 0)
  const totalSavings = totalOriginalPrice - totalPrice

  const formatPrice = (price: number) => {
    return `¥${price.toLocaleString()}`
  }

  return (
    <div className="min-h-screen bg-gray-50 pt-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* 页面标题 */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="text-center mb-8"
        >
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            购物车
          </h1>
          <p className="text-xl text-gray-600">
            {cartItems.length > 0 ? `您的购物车中有 ${cartItems.length} 件商品` : '您的购物车是空的'}
          </p>
        </motion.div>

        {cartItems.length === 0 ? (
          /* 空购物车状态 */
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="text-center py-16"
          >
            <div className="text-6xl mb-8">🛒</div>
            <h2 className="text-2xl font-bold text-gray-900 mb-4">
              购物车是空的
            </h2>
            <p className="text-gray-600 mb-8">
              快去挑选您喜欢的商品吧！
            </p>
            <button className="bg-gradient-to-r from-primary-500 to-secondary-500 text-white px-8 py-3 rounded-full font-semibold hover:shadow-lg transform hover:scale-105 transition-all duration-300">
              去购物
            </button>
          </motion.div>
        ) : (
          <div className="flex flex-col lg:flex-row gap-8">
            {/* 购物车商品列表 */}
            <div className="lg:w-2/3">
              <motion.div
                initial={{ opacity: 0, x: -30 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.6 }}
                className="bg-white rounded-2xl shadow-lg overflow-hidden"
              >
                {/* 表头 */}
                <div className="bg-gray-50 px-6 py-4 border-b border-gray-200">
                  <div className="flex items-center">
                    <input
                      type="checkbox"
                      checked={cartItems.every(item => item.selected)}
                      onChange={selectAll}
                      className="w-5 h-5 text-primary-600 rounded focus:ring-primary-500"
                    />
                    <span className="ml-3 text-gray-700 font-medium">全选</span>
                    <span className="ml-auto text-gray-500">
                      已选择 {selectedItems.length} 件商品
                    </span>
                  </div>
                </div>

                {/* 商品列表 */}
                <div className="divide-y divide-gray-200">
                  {cartItems.map((item, index) => (
                    <motion.div
                      key={item.id}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ duration: 0.5, delay: index * 0.1 }}
                      className={`p-6 ${!item.inStock ? 'bg-gray-50' : ''}`}
                    >
                      <div className="flex items-center space-x-4">
                        {/* 选择框 */}
                        <input
                          type="checkbox"
                          checked={item.selected}
                          onChange={() => toggleSelection(item.id)}
                          disabled={!item.inStock}
                          className="w-5 h-5 text-primary-600 rounded focus:ring-primary-500 disabled:opacity-50"
                        />

                        {/* 商品图片 */}
                        <div className="relative">
                          <img
                            src={item.image}
                            alt={item.name}
                            className={`w-20 h-20 object-cover rounded-lg ${!item.inStock ? 'opacity-50' : ''}`}
                          />
                          {!item.inStock && (
                            <div className="absolute inset-0 flex items-center justify-center">
                              <span className="bg-red-500 text-white px-2 py-1 rounded text-xs">
                                缺货
                              </span>
                            </div>
                          )}
                        </div>

                        {/* 商品信息 */}
                        <div className="flex-1 min-w-0">
                          <div className="flex items-start justify-between">
                            <div>
                              <h3 className={`text-lg font-medium ${!item.inStock ? 'text-gray-500' : 'text-gray-900'}`}>
                                {item.name}
                              </h3>
                              <p className="text-sm text-gray-500 mt-1">
                                品牌: {item.brand}
                              </p>
                              {!item.inStock && (
                                <p className="text-sm text-red-500 mt-1">
                                  商品暂时缺货
                                </p>
                              )}
                            </div>
                            <button
                              onClick={() => removeItem(item.id)}
                              className="text-gray-400 hover:text-red-500 transition-colors"
                            >
                              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                              </svg>
                            </button>
                          </div>

                          <div className="flex items-center justify-between mt-4">
                            {/* 价格 */}
                            <div className="flex items-center space-x-2">
                              <span className={`text-xl font-bold ${!item.inStock ? 'text-gray-500' : 'text-red-600'}`}>
                                {formatPrice(item.price)}
                              </span>
                              {item.originalPrice && (
                                <span className="text-sm text-gray-500 line-through">
                                  {formatPrice(item.originalPrice)}
                                </span>
                              )}
                            </div>

                            {/* 数量控制 */}
                            <div className="flex items-center space-x-3">
                              <button
                                onClick={() => updateQuantity(item.id, item.quantity - 1)}
                                disabled={!item.inStock || item.quantity <= 1}
                                className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                              >
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 12H4" />
                                </svg>
                              </button>
                              <span className={`w-12 text-center font-medium ${!item.inStock ? 'text-gray-500' : 'text-gray-900'}`}>
                                {item.quantity}
                              </span>
                              <button
                                onClick={() => updateQuantity(item.id, item.quantity + 1)}
                                disabled={!item.inStock}
                                className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                              >
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                </svg>
                              </button>
                            </div>
                          </div>
                        </div>
                      </div>
                    </motion.div>
                  ))}
                </div>
              </motion.div>
            </div>

            {/* 订单摘要 */}
            <div className="lg:w-1/3">
              <motion.div
                initial={{ opacity: 0, x: 30 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.6 }}
                className="bg-white rounded-2xl shadow-lg p-6 sticky top-24"
              >
                <h2 className="text-xl font-bold text-gray-900 mb-6">
                  订单摘要
                </h2>

                <div className="space-y-4 mb-6">
                  <div className="flex justify-between">
                    <span className="text-gray-600">商品总价</span>
                    <span className="font-medium">{formatPrice(totalOriginalPrice)}</span>
                  </div>
                  {totalSavings > 0 && (
                    <div className="flex justify-between text-green-600">
                      <span>优惠金额</span>
                      <span>-{formatPrice(totalSavings)}</span>
                    </div>
                  )}
                  <div className="flex justify-between">
                    <span className="text-gray-600">运费</span>
                    <span className="text-green-600">免费</span>
                  </div>
                  <div className="border-t border-gray-200 pt-4">
                    <div className="flex justify-between items-center">
                      <span className="text-lg font-bold text-gray-900">总计</span>
                      <span className="text-2xl font-bold text-red-600">
                        {formatPrice(totalPrice)}
                      </span>
                    </div>
                  </div>
                </div>

                <div className="space-y-3">
                  <button
                    disabled={selectedItems.length === 0}
                    className="w-full bg-gradient-to-r from-primary-500 to-secondary-500 text-white py-4 rounded-xl font-semibold hover:shadow-lg transform hover:scale-105 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
                  >
                    立即结算 ({selectedItems.length})
                  </button>
                  <button className="w-full border border-gray-300 text-gray-700 py-3 rounded-xl font-medium hover:bg-gray-50 transition-colors">
                    继续购物
                  </button>
                </div>

                {/* 优惠券 */}
                <div className="mt-6 p-4 bg-gradient-to-r from-yellow-50 to-orange-50 rounded-xl border border-yellow-200">
                  <div className="flex items-center mb-2">
                    <svg className="w-5 h-5 text-yellow-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M5 2a1 1 0 011 1v1h1a1 1 0 010 2H6v1a1 1 0 01-2 0V6H3a1 1 0 010-2h1V3a1 1 0 011-1zm0 10a1 1 0 011 1v1h1a1 1 0 110 2H6v1a1 1 0 11-2 0v-1H3a1 1 0 110-2h1v-1a1 1 0 011-1zM12 2a1 1 0 01.967.744L14.146 7.2 17.5 9.134a1 1 0 010 1.732L14.146 12.8l-1.179 4.456a1 1 0 01-1.934 0L9.854 12.8 6.5 10.866a1 1 0 010-1.732L9.854 7.2l1.179-4.456A1 1 0 0112 2z" clipRule="evenodd" />
                    </svg>
                    <span className="font-medium text-yellow-800">优惠券</span>
                  </div>
                  <p className="text-sm text-yellow-700 mb-3">
                    满 ¥500 减 ¥50，满 ¥1000 减 ¥120
                  </p>
                  <button className="text-sm text-yellow-800 font-medium hover:underline">
                    查看可用优惠券 →
                  </button>
                </div>
              </motion.div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default CartPage