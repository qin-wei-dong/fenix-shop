package com.fenix.shop.common.utils;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 分页工具类
 * 统一前后端分页参数处理，确保一致性
 *
 * @author fenix
 * @date 2025-06-28
 */
public class PageUtils {

    /**
     * 默认页码（从1开始）
     */
    public static final int DEFAULT_PAGE_NUM = 1;
    
    /**
     * 默认每页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最小每页大小
     */
    public static final int MIN_PAGE_SIZE = 1;
    
    /**
     * 最大每页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 最大页码
     */
    public static final int MAX_PAGE_NUM = 10000;

    /**
     * 标准化分页参数
     * 确保分页参数在合理范围内
     * 
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 标准化后的分页参数
     */
    public static PageRequest normalizePage(Integer pageNum, Integer pageSize) {
        // 处理页码
        int normalizedPageNum = pageNum != null ? pageNum : DEFAULT_PAGE_NUM;
        if (normalizedPageNum < 1) {
            normalizedPageNum = DEFAULT_PAGE_NUM;
        }
        if (normalizedPageNum > MAX_PAGE_NUM) {
            normalizedPageNum = MAX_PAGE_NUM;
        }
        
        // 处理每页大小
        int normalizedPageSize = pageSize != null ? pageSize : DEFAULT_PAGE_SIZE;
        if (normalizedPageSize < MIN_PAGE_SIZE) {
            normalizedPageSize = DEFAULT_PAGE_SIZE;
        }
        if (normalizedPageSize > MAX_PAGE_SIZE) {
            normalizedPageSize = MAX_PAGE_SIZE;
        }
        
        return PageRequest.builder()
                .pageNum(normalizedPageNum)
                .pageSize(normalizedPageSize)
                .build();
    }

    /**
     * 转换为MyBatis-Plus的Page对象（从0开始）
     * 
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return MyBatis-Plus的Page对象
     */
    public static com.baomidou.mybatisplus.extension.plugins.pagination.Page<Object> toMybatisPlusPage(Integer pageNum, Integer pageSize) {
        PageRequest normalized = normalizePage(pageNum, pageSize);
        // MyBatis-Plus的Page从1开始计数，与我们的标准一致
        return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                normalized.getPageNum(), 
                normalized.getPageSize()
        );
    }

    /**
     * 转换为Spring Data的Pageable对象（从0开始）
     * 
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return Spring Data的Pageable对象
     */
    public static org.springframework.data.domain.Pageable toSpringPageable(Integer pageNum, Integer pageSize) {
        PageRequest normalized = normalizePage(pageNum, pageSize);
        // Spring Data的Pageable从0开始，需要减1
        return org.springframework.data.domain.PageRequest.of(
                normalized.getPageNum() - 1, 
                normalized.getPageSize()
        );
    }

    /**
     * 转换为Spring Data的Pageable对象（带排序）
     * 
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @param sort 排序对象
     * @return Spring Data的Pageable对象
     */
    public static org.springframework.data.domain.Pageable toSpringPageable(Integer pageNum, Integer pageSize, org.springframework.data.domain.Sort sort) {
        PageRequest normalized = normalizePage(pageNum, pageSize);
        // Spring Data的Pageable从0开始，需要减1
        return org.springframework.data.domain.PageRequest.of(
                normalized.getPageNum() - 1, 
                normalized.getPageSize(),
                sort
        );
    }

    /**
     * 计算总页数
     * 
     * @param total 总记录数
     * @param pageSize 每页大小
     * @return 总页数
     */
    public static int calculateTotalPages(long total, int pageSize) {
        if (total <= 0 || pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 计算偏移量（用于SQL LIMIT）
     * 
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 偏移量
     */
    public static long calculateOffset(int pageNum, int pageSize) {
        return (long) (pageNum - 1) * pageSize;
    }

    /**
     * 验证分页参数是否有效
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 是否有效
     */
    public static boolean isValidPage(Integer pageNum, Integer pageSize) {
        return pageNum != null && pageNum >= 1 && pageNum <= MAX_PAGE_NUM
                && pageSize != null && pageSize >= MIN_PAGE_SIZE && pageSize <= MAX_PAGE_SIZE;
    }

    /**
     * 标准分页请求对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageRequest {
        /**
         * 页码（从1开始）
         */
        private Integer pageNum;
        
        /**
         * 每页大小
         */
        private Integer pageSize;
        
        /**
         * 获取偏移量
         */
        public long getOffset() {
            return calculateOffset(pageNum, pageSize);
        }
    }

    /**
     * 标准分页响应对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageResponse<T> {
        /**
         * 当前页码（从1开始）
         */
        private Integer pageNum;
        
        /**
         * 每页大小
         */
        private Integer pageSize;
        
        /**
         * 总记录数
         */
        private Long total;
        
        /**
         * 总页数
         */
        private Integer totalPages;
        
        /**
         * 是否有下一页
         */
        private Boolean hasNext;
        
        /**
         * 是否有上一页
         */
        private Boolean hasPrevious;
        
        /**
         * 数据列表
         */
        private java.util.List<T> records;
        
        /**
         * 从Spring Data Page对象创建
         */
        public static <T> PageResponse<T> fromSpringPage(org.springframework.data.domain.Page<T> page) {
            return PageResponse.<T>builder()
                    .pageNum(page.getNumber() + 1) // Spring Data从0开始，转换为从1开始
                    .pageSize(page.getSize())
                    .total(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .hasNext(page.hasNext())
                    .hasPrevious(page.hasPrevious())
                    .records(page.getContent())
                    .build();
        }
        
        /**
         * 从MyBatis-Plus IPage对象创建
         */
        public static <T> PageResponse<T> fromMybatisPage(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
            return PageResponse.<T>builder()
                    .pageNum((int) page.getCurrent()) // MyBatis-Plus从1开始，直接使用
                    .pageSize((int) page.getSize())
                    .total(page.getTotal())
                    .totalPages((int) page.getPages())
                    .hasNext(page.getCurrent() < page.getPages())
                    .hasPrevious(page.getCurrent() > 1)
                    .records(page.getRecords())
                    .build();
        }
    }
}
