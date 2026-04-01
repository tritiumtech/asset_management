package com.drv.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 资产流程审批DTO
 */
@Data
public class AssetProcessApproveDTO {
    
    /**
     * 工作流实例ID
     */
    @NotNull(message = "工作流ID不能为空")
    private Long workflowId;
    
    /**
     * 审批动作 - APPROVE/REJECT/RETURN
     */
    @NotBlank(message = "审批动作不能为空")
    private String action;
    
    /**
     * 审批意见
     */
    @Size(max = 1000, message = "审批意见不能超过1000字")
    private String comment;
    
    /**
     * 附件列表
     */
    private List<String> attachments;
    
    /**
     * 下一审批人ID（多级审批）
     */
    private Long nextApproverId;
}