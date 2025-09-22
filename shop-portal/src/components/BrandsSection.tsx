import { motion } from 'framer-motion'
import { useEffect, useState } from 'react'

interface Brand {
  id: number
  name: string
  logo: string
  description: string
}

const BrandsSection = () => {
  const [currentIndex, setCurrentIndex] = useState(0)

  const brands: Brand[] = [
    {
      id: 1,
      name: 'Apple',
      logo: 'https://upload.wikimedia.org/wikipedia/commons/f/fa/Apple_logo_black.svg',
      description: '创新科技，改变世界'
    },
    {
      id: 2,
      name: 'Nike',
      logo: 'https://upload.wikimedia.org/wikipedia/commons/a/a6/Logo_NIKE.svg',
      description: 'Just Do It'
    },
    {
      id: 3,
      name: 'Samsung',
      logo: 'https://upload.wikimedia.org/wikipedia/commons/2/24/Samsung_Logo.svg',
      description: '科技创造可能'
    },
    {
      id: 4,
      name: 'Sony',
      logo: 'https://upload.wikimedia.org/wikipedia/commons/c/ca/Sony_logo.svg',
      description: '索尼大法好'
    },
    {
      id: 5,
      name: 'Adidas',
      logo: 'https://upload.wikimedia.org/wikipedia/commons/2/20/Adidas_Logo.svg',
      description: 'Impossible is Nothing'
    },
    {
      id: 6,
      name: 'Microsoft',
      logo: 'https://upload.wikimedia.org/wikipedia/commons/9/96/Microsoft_logo_%282012%29.svg',
      description: '赋能每个人和组织'
    },
    {
      id: 7,
      name: 'Google',
      logo: 'https://upload.wikimedia.org/wikipedia/commons/2/2f/Google_2015_logo.svg',
      description: '整理全球信息'
    },
    {
      id: 8,
      name: 'Tesla',
      logo: 'https://upload.wikimedia.org/wikipedia/commons/b/bb/Tesla_T_symbol.svg',
      description: '加速世界向可持续能源转变'
    }
  ]

  // 自动轮播
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % Math.ceil(brands.length / 4))
    }, 3000)

    return () => clearInterval(timer)
  }, [])

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  }

  const itemVariants = {
    hidden: { y: 30, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: {
        duration: 0.5,
        ease: "easeOut"
      }
    }
  }

  return (
    <section className="py-20 bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true }}
          className="text-center mb-16"
        >
          <h2 className="text-4xl font-bold text-gray-900 mb-4">
            合作品牌
          </h2>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            与全球知名品牌深度合作，为您提供正品保障和优质服务
          </p>
        </motion.div>

        {/* 品牌展示区域 */}
        <div className="relative overflow-hidden">
          <motion.div
            variants={containerVariants}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
            className="grid grid-cols-2 md:grid-cols-4 gap-8"
          >
            {brands.slice(currentIndex * 4, currentIndex * 4 + 8).map((brand) => (
              <motion.div
                key={brand.id}
                variants={itemVariants}
                whileHover={{ 
                  scale: 1.05,
                  transition: { duration: 0.3 }
                }}
                className="group cursor-pointer"
              >
                <div className="bg-white rounded-2xl p-8 shadow-lg hover:shadow-2xl transition-all duration-300 border border-gray-100 h-40 flex flex-col items-center justify-center">
                  <div className="w-16 h-16 mb-4 flex items-center justify-center">
                    <img
                      src={brand.logo}
                      alt={brand.name}
                      className="max-w-full max-h-full object-contain filter grayscale group-hover:grayscale-0 transition-all duration-300"
                      onError={(e) => {
                        // 如果图片加载失败，显示品牌名称
                        const target = e.target as HTMLImageElement
                        target.style.display = 'none'
                        const parent = target.parentElement
                        if (parent) {
                          parent.innerHTML = `<div class="text-2xl font-bold text-gray-600">${brand.name}</div>`
                        }
                      }}
                    />
                  </div>
                  <h3 className="text-lg font-bold text-gray-900 mb-2">
                    {brand.name}
                  </h3>
                  <p className="text-sm text-gray-500 text-center opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                    {brand.description}
                  </p>
                </div>
              </motion.div>
            ))}
          </motion.div>
        </div>

        {/* 轮播指示器 */}
        <div className="flex justify-center mt-8 space-x-2">
          {Array.from({ length: Math.ceil(brands.length / 4) }, (_, i) => (
            <button
              key={i}
              onClick={() => setCurrentIndex(i)}
              className={`w-3 h-3 rounded-full transition-all duration-300 ${
                i === currentIndex
                  ? 'bg-primary-500 w-8'
                  : 'bg-gray-300 hover:bg-gray-400'
              }`}
            />
          ))}
        </div>

        {/* 统计数据 */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.3 }}
          viewport={{ once: true }}
          className="mt-16 grid grid-cols-2 md:grid-cols-4 gap-8"
        >
          {[
            { label: '合作品牌', value: '500+', icon: '🏢' },
            { label: '全球用户', value: '1000万+', icon: '👥' },
            { label: '商品种类', value: '50万+', icon: '📦' },
            { label: '服务国家', value: '100+', icon: '🌍' }
          ].map((stat, index) => (
            <motion.div
              key={index}
              whileHover={{ scale: 1.05 }}
              className="text-center p-6 bg-white rounded-2xl shadow-lg"
            >
              <div className="text-3xl mb-2">{stat.icon}</div>
              <div className="text-3xl font-bold text-primary-600 mb-1">
                {stat.value}
              </div>
              <div className="text-gray-600">{stat.label}</div>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  )
}

export default BrandsSection