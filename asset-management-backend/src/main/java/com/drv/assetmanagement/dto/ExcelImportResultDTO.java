package com.drv.assetmanagement.dto;

import lombok.Data;

import java.util.List;

/**
 * Excel导入结果DTO
 */
@Data
public class ExcelImportResultDTO {
    
    /**
     * 导入批次号
     */
    private String batchNo;
    
    /**
     * 总行数
     */
    private Integer totalRows;
    
    /**
     * 成功导入数
     */
    private Integer successCount;
    
    /**
     * 失败数
     */
    private Integer failCount;
    
    /**
     * 预览模式
     */
    private Boolean previewMode;
    
    /**
     * 导入详情
     */
    private List<ImportDetailDTO> details;
    
    /**
     * 导入的资产列表（预览模式）
     */
    private List<AssetDTO> previewAssets;
    
    @Data
    public static class ImportDetailDTO {
        /**
         * 行号
         */
        private Integer rowNum;
        
        /**
         * 资产编号
         */
        private String assetCode;
        
        /**
         * 资产名称
         */
        private String assetName;
        
        /**
         * 导入状态 - SUCCESS/FAIL
         */
        private String status;
        
        /**
         * 错误信息
         */
        private String errorMessage;
    }
}