package com.fenix.shop.product.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 统一商品查询条件DTO
 *
 * @author fenix
 * @date 2025/5/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class ProductQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键词（商品名称、描述）
     */
    @Size(max = 100, message = "搜索关键词长度不能超过100个字符")
    private String keyword;

    /**
     * 分类ID（支持任意级别分类）
     */
    @Positive(message = "分类ID必须是正数")
    private Long categoryId;

    /**
     * 分类ID列表（用于包含子分类查询）
     */
    private List<Long> categoryIds;

    /**
     * 是否包含子分类，默认false
     */
    private Boolean includeSubCategories = false;

    /**
     * 品牌ID列表
     */
    private List<Long> brandIds;

    /**
     * 最低价格
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "最低价格不能小于0")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确，最多8位整数2位小数")
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "最高价格不能小于0")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确，最多8位整数2位小数")
    private BigDecimal maxPrice;

    /**
     * 商品标签筛选（支持多标签）
     */
    @Size(max = 10, message = "标签数量不能超过10个")
    private List<@Size(max = 20, message = "标签长度不能超过20个字符") String> tags;

    /**
     * 商品状态筛选
     */
    @Pattern(regexp = "^(ON_SALE|OFF_SHELF|DELETED)$", message = "商品状态只能是 ON_SALE、OFF_SHELF 或 DELETED")
    private String status;

    /**
     * 商品属性
     */
    private Map<String, List<String>> attributes;

    /**
     * 是否只显示推荐商品
     */
    private Boolean isFeatured;

    /**
     * 是否只显示有库存商品
     */
    private Boolean hasStock;

    /**
     * 是否只显示促销商品
     */
    private Boolean onPromotion;

    /**
     * 排序字段：price, sales, rating, createTime, updateTime
     */
    @Pattern(regexp = "^(price|sales|rating|createTime|updateTime)$",
            message = "排序字段只能是 price、sales、rating、createTime 或 updateTime")
    private String sortBy = "createTime";

    /**
     * 排序方式：ASC, DESC
     */
    @Pattern(regexp = "^(ASC|DESC)$", message = "排序方式只能是 ASC 或 DESC")
    private String sortOrder = "DESC";

    /**
     * 页码，默认1 (for DB)
     */
    @Min(value = 1, message = "页码不能小于1")
    @Max(value = 10000, message = "页码不能大于10000")
    private Integer pageNum = 1;

    /**
     * 每页数量，默认20，最大100 (for DB)
     */
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 20;

    /**
     * 设置默认值和校验参数
     */
    public void setDefaults() {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
        }
        if (pageSize > 100) {
            pageSize = 100;
        }
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "createTime";
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "DESC";
        } else {
            sortOrder = sortOrder.toUpperCase();
            if (!sortOrder.equals("ASC") && !sortOrder.equals("DESC")) {
                sortOrder = "DESC";
            }
        }
        if (includeSubCategories == null) {
            includeSubCategories = false;
        }
    }

    /**
     * 校验价格区间合理性
     */
    @AssertTrue(message = "最低价格不能大于最高价格")
    public boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) {
            return true;
        }
        return minPrice.compareTo(maxPrice) <= 0;
    }

    /**
     * 转换为Elasticsearch搜索参数
     *
     * @return 转换后的搜索参数Map
     */
    public Map<String, Object> toElasticsearchParams() {
        setDefaults(); 
        Map<String, Object> params = new java.util.HashMap<>();

        params.put("keyword", this.keyword);
        if (this.categoryId != null) {
            params.put("categoryIds", java.util.Collections.singletonList(this.categoryId));
        } else {
            params.put("categoryIds", this.categoryIds);
        }
        params.put("brandIds", this.brandIds);
        params.put("minPrice", this.minPrice);
        params.put("maxPrice", this.maxPrice);
        params.put("tags", this.tags);
        params.put("attributes", this.attributes);
        params.put("hasStock", this.hasStock);
        params.put("onPromotion", this.onPromotion);
        params.put("status", this.status);
        params.put("isFeatured", this.isFeatured);

        if (this.sortBy != null && !this.sortBy.isEmpty() && this.sortOrder != null && !this.sortOrder.isEmpty()) {
            params.put("sortField", this.sortBy.toLowerCase() + "_" + this.sortOrder.toLowerCase());
        }

        params.put("page", this.pageNum - 1);
        params.put("size", this.pageSize);
        params.put("includeSubCategories", this.includeSubCategories);

        return params;
    }

    /**
     * 为保持旧代码兼容性，提供brandId的getter/setter
     */
    public Long getBrandId() {
        if (this.brandIds != null && !this.brandIds.isEmpty()) {
            return this.brandIds.get(0);
        }
        return null;
    }

    public void setBrandId(Long brandId) {
        if (brandId != null) {
            this.brandIds = java.util.Collections.singletonList(brandId);
        }
    }
} 