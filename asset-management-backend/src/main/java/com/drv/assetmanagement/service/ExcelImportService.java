package com.drv.assetmanagement.service;

import com.drv.assetmanagement.dto.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Excel导入服务
 */
public interface ExcelImportService {
    
    /**
     * 导入资产Excel
     * @param file Excel文件
     * @param requestDTO 导入请求参数
     * @return 导入结果
     */
    Result<ExcelImportResultDTO> importAssets(MultipartFile file, ExcelImportRequestDTO requestDTO);
    
    /**
     * 预览Excel内容（不实际导入）
     * @param file Excel文件
     * @return 预览结果
     */
    Result<ExcelImportResultDTO> previewAssets(MultipartFile file);
    
    /**
     * 下载导入模板
     * @return 模板文件字节数组
     */
    Result<byte[]> downloadTemplate();
    
    /**
     * 获取导入历史
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 导入历史列表
     */
    Result<PageResult<ExcelImportResultDTO>> getImportHistory(Integer pageNum, Integer pageSize);
}