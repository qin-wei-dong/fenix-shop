package com.fenix.shop.feign;

import java.util.Map;

import com.fenix.shop.common.model.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fenix.shop.product.model.dto.ProductQueryDTO;

/**
 * 搜索服务Feign客户端
 * 用于微服务间调用搜索服务
 *
 * @author fenix
 * @date 2025/6/15
 */
@FeignClient(name = "shopping-search", path = "/api/search", fallback = SearchServiceFeignFallback.class)
public interface SearchServiceFeignClient {

    /**
     * 调用搜索服务进行高级搜索
     *
     * @param searchDTO 搜索条件
     * @return 搜索结果
     */
    @PostMapping("/products/advanced")
    Result<Map<String, Object>> searchProducts(@RequestBody ProductQueryDTO searchDTO);
} 