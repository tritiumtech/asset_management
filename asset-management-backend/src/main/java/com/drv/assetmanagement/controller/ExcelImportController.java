package com.drv.assetmanagement.controller;

import com.drv.assetmanagement.dto.*;
import com.drv.assetmanagement.service.ExcelImportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Excel导入Controller
 */
@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExcelImportController {

    private final ExcelImportService excelImportService;

    /**
     * 导入资产Excel
     */
    @PostMapping(value = "/assets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ExcelImportResultDTO> importAssets(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "true") Boolean skipErrorRows) {
        
        // 检查文件类型
        if (file.isEmpty()) {
            return Result.error("上传文件为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || 
            (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            return Result.error("请上传Excel文件(.xlsx或.xls)");
        }
        
        ExcelImportRequestDTO requestDTO = new ExcelImportRequestDTO();
        requestDTO.setSkipErrorRows(skipErrorRows);
        requestDTO.setPreviewMode(false);
        
        return excelImportService.importAssets(file, requestDTO);
    }

    /**
     * 预览资产Excel（不实际导入）
     */
    @PostMapping(value = "/assets/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ExcelImportResultDTO> previewAssets(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件为空");
        }
        
        return excelImportService.previewAssets(file);
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        Result<byte[]> result = excelImportService.downloadTemplate();
        
        if (result.getCode() != 200 || result.getData() == null) {
            response.sendError(500, result.getMessage());
            return;
        }
        
        byte[] templateBytes = result.getData();
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setContentLength(templateBytes.length);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=" + URLEncoder.encode("资产导入模板.xlsx", StandardCharsets.UTF_8));
        
        response.getOutputStream().write(templateBytes);
        response.getOutputStream().flush();
    }

    /**
     * 获取导入历史
     */
    @GetMapping("/history")
    public Result<PageResult<ExcelImportResultDTO>> getImportHistory(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return excelImportService.getImportHistory(pageNum, pageSize);
    }
}