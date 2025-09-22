package com.fenix.shop.common.config;// package com.fenix.shopping.common.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// /**
//  * Web MVC 全局配置
//  * 包括CORS跨域、拦截器、资源处理等配置
//  */
// @Configuration
// public class WebMvcConfig implements WebMvcConfigurer {

//     /**
//      * 配置CORS跨域支持
//      * 允许前端开发服务器和生产环境访问后端API
//      */
//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/**")
//                 .allowedOrigins("http://localhost:3000", "http://localhost:5173")  // 明确指定允许的来源
//                 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                 .allowedHeaders("*")  // 允许所有请求头
//                 .allowCredentials(true)  // 允许发送Cookie
//                 .maxAge(3600);  // 预检请求的有效期，单位为秒
//     }
// } 