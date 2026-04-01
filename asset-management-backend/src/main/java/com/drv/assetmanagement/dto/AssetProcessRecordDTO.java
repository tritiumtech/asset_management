package com.drv.assetmanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资产流程记录DTO
 */
@Data
public class AssetProcessRecordDTO {
    
    private Long id;
    private Long workflowId;
    private String processType;
    private String processTypeName;
    private String status;
    private String statusName;
    private Integer currentRound;
    private String source;
    private String createdByName;
    private String createTime;
    private String updateTime;
    private Boolean isClosed;
    
    /**
     * 审批历史
     */
    private List<ApprovalRecordDTO> approvalHistory;
    
    @Data
    public static class ApprovalRecordDTO {
        private Integer round;
        private String approverName;
        private String approverType;
        private String action;
        private String result;
        private String comment;
        private String createTime;
    }
}