package com.drv.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 资产流程申请DTO
 */
@Data
public class AssetProcessApplyDTO {
    
    /**
     * 资产ID
     */
    @NotNull(message = "资产ID不能为空")
    private Long assetId;
    
    /**
     * 流程类型 - ASSIGN/TRANSFER/REPAIR/SCRAP/RETURN
     */
    @NotBlank(message = "流程类型不能为空")
    private String processType;
    
    /**
     * 申请原因
     */
    @NotBlank(message = "申请原因不能为空")
    @Size(max = 500, message = "申请原因不能超过500字")
    private String reason;
    
    /**
     * 目标用户ID（调拨/领用）
     */
    private Long toUserId;
    
    /**
     * 目标用户姓名
     */
    private String toUserName;
    
    /**
     * 目标部门
     */
    private String toDepartment;
    
    /**
     * 目标位置
     */
    private String toLocation;
    
    /**
     * 期望日期
     */
    private String expectedDate;
    
    /**
     * 附件列表
     */
    private List<String> attachments;
    
    /**
     * OA回调URL
     */
    private String callbackUrl;
    
    /**
     * 来源 - OA/INTERNAL/SUPPLIER
     */
    private String source;
}