package com.drv.assetmanagement.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class InventoryTaskDTO {
    private Long id;
    private String batchNo;
    private String taskName;
    private String scope;
    private String scopeDetail;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private String status;
    private String statusName;
    private Integer totalCount;
    private Integer checkedCount;
    private Integer normalCount;
    private Integer missingCount;
    private Integer surplusCount;
    private Integer locationChangeCount;
    private Integer userChangeCount;
    private Long managerId;
    private String managerName;
    private String remark;
}