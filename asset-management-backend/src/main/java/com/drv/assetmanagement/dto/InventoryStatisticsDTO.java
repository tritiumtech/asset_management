package com.drv.assetmanagement.dto;

import lombok.Data;

@Data
public class InventoryStatisticsDTO {
    private Long taskId;
    private Integer totalCount;
    private Integer checkedCount;
    private Integer uncheckedCount;
    private Integer normalCount;
    private Integer missingCount;
    private Integer surplusCount;
    private Integer locationChangeCount;
    private Integer userChangeCount;
    private Double progressPercentage;
}