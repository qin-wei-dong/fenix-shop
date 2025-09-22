package com.fenix.shop.common.model.dto;

import com.fenix.shop.common.constants.ValidationConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 基础分页DTO
 *
 * @author fenix
 * @date 2025-06-28
 */
@Data
public class BasePageDTO {
    
    /**
     * 页码，从1开始
     */
    @Min(value = ValidationConstants.PAGE_NUM_MIN, message = "页码不能小于1")
    @Max(value = ValidationConstants.PAGE_NUM_MAX, message = "页码不能大于10000")
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    @Min(value = ValidationConstants.PAGE_SIZE_MIN, message = "每页大小不能小于1")
    @Max(value = ValidationConstants.PAGE_SIZE_MAX, message = "每页大小不能大于100")
    private Integer pageSize = 10;
}
