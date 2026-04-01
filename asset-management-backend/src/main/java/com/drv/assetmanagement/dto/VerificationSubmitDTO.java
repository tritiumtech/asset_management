package com.drv.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "盘点明细ID不能为空")
    private Long itemId;
    
    /**
     * 操作类型 - SUBMIT/REVIEW/RETURN/CLOSE
     */
    @NotBlank(message = "操作类型不能为空")
    private String action;
    
    /**
     * 结果 - PASS/REJECT/NEED_INFO/MODIFIED
     */
    @NotBlank(message = "结果不能为空")
    private String result;
    
    /**
     * 核实意见
     */
    @Size(max = 1000, message = "核实意见不能超过1000字")
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