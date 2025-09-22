package com.fenix.shop.feign;

import com.fenix.shop.common.model.vo.Result;
import com.fenix.shop.product.model.dto.ProductQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 搜索服务Feign客户端降级处理
 *
 * @author fenix
 * @date 2025/6/15
 */
@Slf4j
@Component
public class SearchServiceFeignFallback implements SearchServiceFeignClient {
    @Override
    public Result<Map<String, Object>> searchProducts(ProductQueryDTO searchDTO) {
        log.error("Search service is not available. searchDTO: {}", searchDTO);
        return Result.fail("Search service is busy, please try again later.");
    }
} 