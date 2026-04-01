package com.drv.assetmanagement.dto;

import lombok.Data;

/**
 * 流程实例DTO
 */
@Data
public class WorkflowInstanceDTO {
    
    private Long id;
    private String bizType;
    private Long bizId;
    private String bizCode;
    private String status;
    private String source;
    private Integer currentRound;
    private Integer maxRounds;
    private Boolean isClosed;
    private String createdByName;
    private String createTime;
}