package com.drv.assetmanagement.dto;

import lombok.Data;

import java.util.List;

/**
 * 核实提交请求DTO
 */
@Data
public class VerificationSubmitDTO {
    
    /**
     * 盘点明细ID
     */
    private Long itemId;
    
    /**
     * 操作类型 - SUBMIT/REVIEW/RETURN/CLOSE
     */
    private String action;
    
    /**
     * 结果 - PASS/REJECT/NEED_INFO/MODIFIED
     */
    private String result;
    
    /**
     * 核实意见
     */
    private String comment;
    
    /**
     * 附件列表
     */
    private List<String> attachments;
    
    /**
     * OA回调URL（如果是OA发起的）
     */
    private String callbackUrl;
    
    /**
     * 来源 - OA/INTERNAL/SUPPLIER
     */
    private String source;
}