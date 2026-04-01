package com.drv.assetmanagement.dto;

import lombok.Data;

@Data
public class VerifyInventoryDTO {
    private Long itemId;
    private String verifyType; // FIRST or SECOND
    private String status;
    private String note;
}