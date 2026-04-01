package com.drv.assetmanagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssetQueryDTO {
    private String keyword;
    private String category;
    private String status;
    private Long currentUserId;
    private String department;
    private String locationType;
    private String locationDetail;
    private String employeeType;
    private List<String> assetCodes;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}