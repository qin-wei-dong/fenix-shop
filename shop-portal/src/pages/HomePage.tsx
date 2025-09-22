import { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import HeroSection from '../components/HeroSection'
import FeaturesSection from '../components/FeaturesSection'
import CategoriesSection from '../components/CategoriesSection'
import ProductsSection from '../components/ProductsSection'
import BrandsSection from '../components/BrandsSection'

const HomePage = () => {
  const [isLoaded, setIsLoaded] = useState(false)

  useEffect(() => {
    setIsLoaded(true)
  }, [])

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: isLoaded ? 1 : 0 }}
      transition={{ duration: 0.5 }}
      className="min-h-screen"
    >
      <HeroSection />
      <FeaturesSection />
      <CategoriesSection />
      <ProductsSection />
      <BrandsSection />
    </motion.div>
  )
}

export default HomePage