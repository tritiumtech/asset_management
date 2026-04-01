package com.drv.assetmanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 核实记录响应DTO
 */
@Data
public class VerificationRecordDTO {
    
    private Long id;
    private Long inventoryItemId;
    private Integer round;
    private String verifierName;
    private String verifierType;
    private String action;
    private String result;
    private String comment;
    private List<String> attachments;
    private LocalDateTime createTime;
}