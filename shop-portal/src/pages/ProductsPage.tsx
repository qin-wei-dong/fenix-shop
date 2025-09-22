import { motion } from 'framer-motion'
import { useState } from 'react'

interface Product {
  id: number
  name: string
  price: number
  originalPrice?: number
  image: string
  rating: number
  reviews: number
  category: string
  brand: string
  inStock: boolean
}

const ProductsPage = () => {
  const [selectedCategory, setSelectedCategory] = useState('all')
  const [sortBy, setSortBy] = useState('default')
  const [priceRange, setPriceRange] = useState([0, 10000])

  const categories = [
    { id: 'all', name: '全部商品' },
    { id: 'electronics', name: '数码电子' },
    { id: 'fashion', name: '时尚服装' },
    { id: 'home', name: '家居生活' },
    { id: 'beauty', name: '美妆护肤' },
    { id: 'sports', name: '运动户外' }
  ]

  const products: Product[] = [
    {
      id: 1,
      name: 'iPhone 15 Pro Max 256GB',
      price: 9999,
      originalPrice: 10999,
      image: 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400&h=400&fit=crop',
      rating: 4.8,
      reviews: 1250,
      category: 'electronics',
      brand: 'Apple',
      inStock: true
    },
    {
      id: 2,
      name: 'MacBook Air M3 芯片',
      price: 8999,
      image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop',
      rating: 4.9,
      reviews: 890,
      category: 'electronics',
      brand: 'Apple',
      inStock: true
    },
    {
      id: 3,
      name: 'Nike Air Max 270',
      price: 899,
      originalPrice: 1299,
      image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop',
      rating: 4.6,
      reviews: 650,
      category: 'fashion',
      brand: 'Nike',
      inStock: true
    },
    {
      id: 4,
      name: '现代简约沙发',
      price: 3999,
      image: 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400&h=400&fit=crop',
      rating: 4.5,
      reviews: 320,
      category: 'home',
      brand: '宜家',
      inStock: false
    },
    {
      id: 5,
      name: 'Dior 口红套装',
      price: 599,
      image: 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400&h=400&fit=crop',
      rating: 4.7,
      reviews: 890,
      category: 'beauty',
      brand: 'Dior',
      inStock: true
    },
    {
      id: 6,
      name: 'Adidas 运动套装',
      price: 799,
      originalPrice: 999,
      image: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=400&fit=crop',
      rating: 4.4,
      reviews: 450,
      category: 'sports',
      brand: 'Adidas',
      inStock: true
    }
  ]

  const filteredProducts = products.filter(product => {
    if (selectedCategory !== 'all' && product.category !== selectedCategory) {
      return false
    }
    if (product.price < priceRange[0] || product.price > priceRange[1]) {
      return false
    }
    return true
  })

  const sortedProducts = [...filteredProducts].sort((a, b) => {
    switch (sortBy) {
      case 'price-low':
        return a.price - b.price
      case 'price-high':
        return b.price - a.price
      case 'rating':
        return b.rating - a.rating
      case 'reviews':
        return b.reviews - a.reviews
      default:
        return 0
    }
  })

  const formatPrice = (price: number) => {
    return `¥${price.toLocaleString()}`
  }

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, i) => (
      <svg
        key={i}
        className={`w-4 h-4 ${
          i < Math.floor(rating) ? 'text-yellow-400' : 'text-gray-300'
        }`}
        fill="currentColor"
        viewBox="0 0 20 20"
      >
        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
      </svg>
    ))
  }

  return (
    <div className="min-h-screen bg-gray-50 pt-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* 页面标题 */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="text-center mb-12"
        >
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            商品中心
          </h1>
          <p className="text-xl text-gray-600">
            发现更多优质商品，享受购物乐趣
          </p>
        </motion.div>

        <div className="flex flex-col lg:flex-row gap-8">
          {/* 侧边栏筛选 */}
          <motion.div
            initial={{ opacity: 0, x: -30 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.6 }}
            className="lg:w-1/4"
          >
            <div className="bg-white rounded-2xl shadow-lg p-6 sticky top-24">
              {/* 分类筛选 */}
              <div className="mb-8">
                <h3 className="text-lg font-bold text-gray-900 mb-4">商品分类</h3>
                <div className="space-y-2">
                  {categories.map((category) => (
                    <button
                      key={category.id}
                      onClick={() => setSelectedCategory(category.id)}
                      className={`w-full text-left px-4 py-2 rounded-lg transition-colors ${
                        selectedCategory === category.id
                          ? 'bg-primary-500 text-white'
                          : 'text-gray-600 hover:bg-gray-100'
                      }`}
                    >
                      {category.name}
                    </button>
                  ))}
                </div>
              </div>

              {/* 价格筛选 */}
              <div className="mb-8">
                <h3 className="text-lg font-bold text-gray-900 mb-4">价格区间</h3>
                <div className="space-y-4">
                  <div className="flex items-center space-x-4">
                    <input
                      type="number"
                      value={priceRange[0]}
                      onChange={(e) => setPriceRange([parseInt(e.target.value) || 0, priceRange[1]])}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      placeholder="最低价"
                    />
                    <span className="text-gray-500">-</span>
                    <input
                      type="number"
                      value={priceRange[1]}
                      onChange={(e) => setPriceRange([priceRange[0], parseInt(e.target.value) || 10000])}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      placeholder="最高价"
                    />
                  </div>
                </div>
              </div>
            </div>
          </motion.div>

          {/* 主要内容区域 */}
          <div className="lg:w-3/4">
            {/* 排序和结果统计 */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: 0.2 }}
              className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 bg-white rounded-2xl shadow-lg p-6"
            >
              <div className="mb-4 sm:mb-0">
                <p className="text-gray-600">
                  共找到 <span className="font-bold text-primary-600">{sortedProducts.length}</span> 件商品
                </p>
              </div>
              <div className="flex items-center space-x-4">
                <label className="text-gray-600">排序方式:</label>
                <select
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value)}
                  className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                >
                  <option value="default">默认排序</option>
                  <option value="price-low">价格从低到高</option>
                  <option value="price-high">价格从高到低</option>
                  <option value="rating">评分最高</option>
                  <option value="reviews">评价最多</option>
                </select>
              </div>
            </motion.div>

            {/* 商品网格 */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.6, delay: 0.3 }}
              className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6"
            >
              {sortedProducts.map((product, index) => (
                <motion.div
                  key={product.id}
                  initial={{ opacity: 0, y: 30 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.5, delay: index * 0.1 }}
                  whileHover={{ y: -5 }}
                  className="group cursor-pointer"
                >
                  <div className="bg-white rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 overflow-hidden border border-gray-100">
                    <div className="relative overflow-hidden">
                      <img
                        src={product.image}
                        alt={product.name}
                        className="w-full h-64 object-cover group-hover:scale-110 transition-transform duration-500"
                      />
                      
                      {!product.inStock && (
                        <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
                          <span className="bg-red-500 text-white px-4 py-2 rounded-full font-medium">
                            暂时缺货
                          </span>
                        </div>
                      )}
                      
                      {product.originalPrice && (
                        <div className="absolute top-4 right-4">
                          <span className="bg-red-500 text-white px-2 py-1 rounded text-xs font-bold">
                            -{Math.round((1 - product.price / product.originalPrice) * 100)}%
                          </span>
                        </div>
                      )}
                    </div>
                    
                    <div className="p-6">
                      <div className="flex items-center justify-between mb-2">
                        <span className="text-sm text-gray-500">{product.brand}</span>
                        <button className="text-gray-400 hover:text-red-500 transition-colors">
                          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                          </svg>
                        </button>
                      </div>
                      
                      <h3 className="font-bold text-lg text-gray-900 mb-2 line-clamp-2">
                        {product.name}
                      </h3>
                      
                      <div className="flex items-center mb-3">
                        <div className="flex items-center mr-2">
                          {renderStars(product.rating)}
                        </div>
                        <span className="text-sm text-gray-500">
                          {product.rating} ({product.reviews})
                        </span>
                      </div>
                      
                      <div className="flex items-center justify-between mb-4">
                        <div className="flex items-center space-x-2">
                          <span className="text-2xl font-bold text-red-600">
                            {formatPrice(product.price)}
                          </span>
                          {product.originalPrice && (
                            <span className="text-sm text-gray-500 line-through">
                              {formatPrice(product.originalPrice)}
                            </span>
                          )}
                        </div>
                      </div>
                      
                      <button 
                        disabled={!product.inStock}
                        className={`w-full py-3 rounded-lg font-medium transition-all duration-300 ${
                          product.inStock
                            ? 'bg-gradient-to-r from-primary-500 to-secondary-500 text-white hover:shadow-lg transform hover:scale-105'
                            : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                        }`}
                      >
                        {product.inStock ? '立即购买' : '暂时缺货'}
                      </button>
                    </div>
                  </div>
                </motion.div>
              ))}
            </motion.div>

            {/* 分页 */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: 0.5 }}
              className="flex justify-center mt-12"
            >
              <div className="flex items-center space-x-2">
                <button className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
                  上一页
                </button>
                {[1, 2, 3, 4, 5].map((page) => (
                  <button
                    key={page}
                    className={`px-4 py-2 rounded-lg transition-colors ${
                      page === 1
                        ? 'bg-primary-500 text-white'
                        : 'border border-gray-300 hover:bg-gray-50'
                    }`}
                  >
                    {page}
                  </button>
                ))}
                <button className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
                  下一页
                </button>
              </div>
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ProductsPage