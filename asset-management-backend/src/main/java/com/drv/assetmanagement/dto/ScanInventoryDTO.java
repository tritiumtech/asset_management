package com.drv.assetmanagement.dto;

import lombok.Data;

@Data
public class ScanInventoryDTO {
    private Long taskId;
    private Long assetId;
    private String assetCode;
    private String result;
    private String checkedLocation;
    private Long checkedUserId;
    private String checkedUserName;
    private String remark;
}