package com.drv.assetmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Excel导入请求DTO
 */
@Data
public class ExcelImportRequestDTO {
    
    /**
     * 导入批次号
     */
    private String batchNo;
    
    /**
     * 导入说明
     */
    private String description;
    
    /**
     * 是否跳过错误行（true:跳过错误行继续导入，false:全部回滚）
     */
    private Boolean skipErrorRows = true;
    
    /**
     * 预览模式（true:只预览不实际导入）
     */
    private Boolean previewMode = false;
}