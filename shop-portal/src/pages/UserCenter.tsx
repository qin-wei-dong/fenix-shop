import React from 'react';
import { useAuthStore } from '../store/authStore';
import { Link } from 'react-router-dom';

/**
 * 用户中心页面组件
 * 功能描述：普通用户的个人中心页面，提供用户基础功能和信息展示
 * 采用技术：React Hooks、状态管理、响应式设计、用户体验优化
 * 技术优势：用户友好的界面设计，功能清晰分类，符合电商用户使用习惯
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
const UserCenter: React.FC = () => {
  const { user } = useAuthStore();

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 主要内容区域 */}
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          {/* 欢迎信息 */}
          <div className="bg-gradient-to-r from-indigo-500 to-purple-600 overflow-hidden shadow rounded-lg mb-6">
            <div className="px-4 py-5 sm:p-6 text-white">
              <h2 className="text-2xl font-bold mb-2">
                欢迎回来，{user?.nickname || user?.username}！
              </h2>
              <p className="text-indigo-100">
                感谢您选择 Fenix Shop，祝您购物愉快！
              </p>
              <div className="mt-4 flex items-center space-x-6">
                <div className="text-center">
                  <div className="text-2xl font-bold">{1}</div>
                  <div className="text-xs text-indigo-100">用户等级</div>
                </div>
                <div className="text-center">
                  <div className="text-2xl font-bold">{0}</div>
                  <div className="text-xs text-indigo-100">积分余额</div>
                </div>
              </div>
            </div>
          </div>

          {/* 功能卡片网格 */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
            {/* 我的订单 */}
            <Link
              to="/orders"
              className="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow duration-200"
            >
              <div className="p-6">
                <div className="flex items-center">
                  <div className="flex-shrink-0">
                    <svg className="h-8 w-8 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                  </div>
                  <div className="ml-4">
                    <h3 className="text-lg font-medium text-gray-900">我的订单</h3>
                    <p className="text-gray-600">查看订单状态和物流信息</p>
                  </div>
                </div>
              </div>
            </Link>

            {/* 购物车 */}
            <Link
              to="/cart"
              className="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow duration-200"
            >
              <div className="p-6">
                <div className="flex items-center">
                  <div className="flex-shrink-0">
                    <svg className="h-8 w-8 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4m0 0L7 13m0 0l-1.5 6M7 13l-1.5 6M20 13v6a2 2 0 01-2 2H6a2 2 0 01-2-2v-6" />
                    </svg>
                  </div>
                  <div className="ml-4">
                    <h3 className="text-lg font-medium text-gray-900">购物车</h3>
                    <p className="text-gray-600">管理您的购物清单</p>
                  </div>
                </div>
              </div>
            </Link>

            {/* 我的收藏 */}
            <Link
              to="/favorites"
              className="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow duration-200"
            >
              <div className="p-6">
                <div className="flex items-center">
                  <div className="flex-shrink-0">
                    <svg className="h-8 w-8 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                    </svg>
                  </div>
                  <div className="ml-4">
                    <h3 className="text-lg font-medium text-gray-900">我的收藏</h3>
                    <p className="text-gray-600">管理您收藏的商品</p>
                  </div>
                </div>
              </div>
            </Link>

            {/* 个人设置 */}
            <Link
              to="/profile"
              className="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow duration-200"
            >
              <div className="p-6">
                <div className="flex items-center">
                  <div className="flex-shrink-0">
                    <svg className="h-8 w-8 text-purple-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                  </div>
                  <div className="ml-4">
                    <h3 className="text-lg font-medium text-gray-900">个人设置</h3>
                    <p className="text-gray-600">修改个人信息和密码</p>
                  </div>
                </div>
              </div>
            </Link>
          </div>

          {/* 快速访问区域 */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* 最近订单 */}
            <div className="bg-white shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                  最近订单
                </h3>
                <div className="text-center py-8 text-gray-500">
                  <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <p className="mt-2">暂无订单记录</p>
                  <Link
                    to="/products"
                    className="mt-4 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
                  >
                    去购物
                  </Link>
                </div>
              </div>
            </div>

            {/* 推荐商品 */}
            <div className="bg-white shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                  为您推荐
                </h3>
                <div className="text-center py-8 text-gray-500">
                  <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                  </svg>
                  <p className="mt-2">根据您的喜好推荐商品</p>
                  <Link
                    to="/products"
                    className="mt-4 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700"
                  >
                    发现商品
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default UserCenter;