package com.drv.assetmanagement.service.impl;

import com.drv.assetmanagement.dto.*;
import com.drv.assetmanagement.entity.Asset;
import com.drv.assetmanagement.enums.AssetStatus;
import com.drv.assetmanagement.repository.AssetMapper;
import com.drv.assetmanagement.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelImportServiceImpl implements ExcelImportService {

    private final AssetMapper assetMapper;
    
    // 批次大小（每批插入1000条，避免内存问题）
    private static final int BATCH_SIZE = 1000;
    
    // 最大支持行数
    private static final int MAX_ROWS = 100000;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ExcelImportResultDTO> importAssets(MultipartFile file, ExcelImportRequestDTO requestDTO) {
        String batchNo = generateBatchNo();
        log.info("开始导入资产Excel: batchNo={}, fileName={}", batchNo, file.getOriginalFilename());
        
        ExcelImportResultDTO result = new ExcelImportResultDTO();
        result.setBatchNo(batchNo);
        result.setPreviewMode(requestDTO.getPreviewMode());
        result.setDetails(new ArrayList<>());
        
        List<Asset> assetList = new ArrayList<>();
        int totalRows = 0;
        int successCount = 0;
        int failCount = 0;
        
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            totalRows = sheet.getLastRowNum(); // 不包含表头
            
            // 检查行数限制
            if (totalRows > MAX_ROWS) {
                return Result.error("Excel行数超过最大限制(" + MAX_ROWS + "行)");
            }
            
            result.setTotalRows(totalRows);
            
            // 解析每一行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                ExcelImportResultDTO.ImportDetailDTO detail = new ExcelImportResultDTO.ImportDetailDTO();
                detail.setRowNum(i + 1);
                
                try {
                    Asset asset = parseRow(row);
                    
                    // 数据校验
                    String validateError = validateAsset(asset);
                    if (validateError != null) {
                        throw new IllegalArgumentException(validateError);
                    }
                    
                    // 检查资产编号是否已存在
                    if (isAssetCodeExists(asset.getAssetCode())) {
                        throw new IllegalArgumentException("资产编号已存在: " + asset.getAssetCode());
                    }
                    
                    detail.setAssetCode(asset.getAssetCode());
                    detail.setAssetName(asset.getAssetName());
                    detail.setStatus("SUCCESS");
                    
                    assetList.add(asset);
                    successCount++;
                    
                } catch (Exception e) {
                    detail.setStatus("FAIL");
                    detail.setErrorMessage(e.getMessage());
                    failCount++;
                    
                    if (!requestDTO.getSkipErrorRows()) {
                        // 不跳过错误，抛出异常回滚
                        throw new RuntimeException("第" + (i + 1) + "行导入失败: " + e.getMessage(), e);
                    }
                }
                
                result.getDetails().add(detail);
            }
            
            // 非预览模式，执行实际导入
            if (!requestDTO.getPreviewMode()) {
                batchInsert(assetList);
                log.info("资产导入完成: batchNo={}, success={}, fail={}", batchNo, successCount, failCount);
            } else {
                // 预览模式，转换为DTO返回
                List<AssetDTO> previewList = new ArrayList<>();
                for (Asset asset : assetList) {
                    AssetDTO dto = new AssetDTO();
                    dto.setAssetCode(asset.getAssetCode());
                    dto.setAssetName(asset.getAssetName());
                    dto.setCategory(asset.getCategory());
                    dto.setBrand(asset.getBrand());
                    dto.setModel(asset.getModel());
                    dto.setSerialNumber(asset.getSerialNumber());
                    dto.setLocationDetail(asset.getLocationDetail());
                    previewList.add(dto);
                }
                result.setPreviewAssets(previewList);
                log.info("资产预览完成: batchNo={}, previewCount={}", batchNo, previewList.size());
            }
            
            result.setSuccessCount(successCount);
            result.setFailCount(failCount);
            
            return Result.success(result);
            
        } catch (IOException e) {
            log.error("Excel文件读取失败", e);
            return Result.error("Excel文件读取失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ExcelImportResultDTO> previewAssets(MultipartFile file) {
        ExcelImportRequestDTO requestDTO = new ExcelImportRequestDTO();
        requestDTO.setPreviewMode(true);
        requestDTO.setSkipErrorRows(true);
        return importAssets(file, requestDTO);
    }

    @Override
    public Result<byte[]> downloadTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("资产导入模板");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "资产编号*", "资产名称*", "资产类别*", "二级分类", "品牌", 
                "型号", "SN码*", "购置日期", "购置金额", "保修期至",
                "供应商", "存放位置类型*", "存放位置详情*", "使用状态", "备注"
            };
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 添加示例行
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("NB-26-0001");
            exampleRow.createCell(1).setCellValue("MacBook Pro 16寸");
            exampleRow.createCell(2).setCellValue("NB");
            exampleRow.createCell(3).setCellValue("笔记本");
            exampleRow.createCell(4).setCellValue("Apple");
            exampleRow.createCell(5).setCellValue("MacBook Pro 16\" M3 Max");
            exampleRow.createCell(6).setCellValue("C02ZW1YJMD6T");
            exampleRow.createCell(7).setCellValue("2026-01-15");
            exampleRow.createCell(8).setCellValue(29999.00);
            exampleRow.createCell(9).setCellValue("2029-01-15");
            exampleRow.createCell(10).setCellValue("Apple官方");
            exampleRow.createCell(11).setCellValue("总部");
            exampleRow.createCell(12).setCellValue("总部-1F-IT部");
            exampleRow.createCell(13).setCellValue("库存");
            exampleRow.createCell(14).setCellValue("示例数据，导入时请删除");
            
            // 设置列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int width = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.max(width, 15 * 256));
            }
            
            // 添加说明Sheet
            Sheet descSheet = workbook.createSheet("填写说明");
            createDescriptionSheet(descSheet);
            
            workbook.write(baos);
            return Result.success(baos.toByteArray());
            
        } catch (IOException e) {
            log.error("生成模板失败", e);
            return Result.error("生成模板失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResult<ExcelImportResultDTO>> getImportHistory(Integer pageNum, Integer pageSize) {
        // TODO: 实现导入历史记录查询（需要创建导入历史表）
        return Result.success(new PageResult<>());
    }
    
    // ========== 私有方法 ==========
    
    private Asset parseRow(Row row) {
        Asset asset = new Asset();
        
        asset.setAssetCode(getStringCellValue(row.getCell(0)));
        asset.setAssetName(getStringCellValue(row.getCell(1)));
        asset.setCategory(getStringCellValue(row.getCell(2)));
        asset.setSubCategory(getStringCellValue(row.getCell(3)));
        asset.setBrand(getStringCellValue(row.getCell(4)));
        asset.setModel(getStringCellValue(row.getCell(5)));
        asset.setSerialNumber(getStringCellValue(row.getCell(6)));
        
        // 日期处理
        asset.setPurchaseDate(parseDate(row.getCell(7)));
        
        // 金额处理
        asset.setPurchaseAmount(parseAmount(row.getCell(8)));
        
        asset.setWarrantyDate(parseDate(row.getCell(9)));
        asset.setSupplier(getStringCellValue(row.getCell(10)));
        asset.setLocationType(getStringCellValue(row.getCell(11)));
        asset.setLocationDetail(getStringCellValue(row.getCell(12)));
        
        // 状态处理，默认为库存
        String status = getStringCellValue(row.getCell(13));
        asset.setStatus(status != null ? status : AssetStatus.IN_STOCK.getCode());
        
        asset.setRemark(getStringCellValue(row.getCell(14)));
        
        return asset;
    }
    
    private String validateAsset(Asset asset) {
        if (asset.getAssetCode() == null || asset.getAssetCode().trim().isEmpty()) {
            return "资产编号不能为空";
        }
        if (asset.getAssetName() == null || asset.getAssetName().trim().isEmpty()) {
            return "资产名称不能为空";
        }
        if (asset.getCategory() == null || asset.getCategory().trim().isEmpty()) {
            return "资产类别不能为空";
        }
        if (asset.getLocationType() == null || asset.getLocationType().trim().isEmpty()) {
            return "存放位置类型不能为空";
        }
        if (asset.getLocationDetail() == null || asset.getLocationDetail().trim().isEmpty()) {
            return "存放位置详情不能为空";
        }
        return null;
    }
    
    private boolean isAssetCodeExists(String assetCode) {
        return assetMapper.findByAssetCode(assetCode) != null;
    }
    
    private void batchInsert(List<Asset> assetList) {
        int total = assetList.size();
        for (int i = 0; i < total; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, total);
            List<Asset> batch = assetList.subList(i, end);
            
            for (Asset asset : batch) {
                assetMapper.insert(asset);
            }
            
            log.info("批量插入进度: {}/{}", end, total);
        }
    }
    
    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    private LocalDate parseDate(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
            } else {
                String dateStr = getStringCellValue(cell);
                if (dateStr == null || dateStr.trim().isEmpty()) return null;
                
                // 尝试多种日期格式
                DateTimeFormatter[] formatters = {
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                    DateTimeFormatter.ofPattern("yyyy.MM.dd")
                };
                
                for (DateTimeFormatter formatter : formatters) {
                    try {
                        return LocalDate.parse(dateStr, formatter);
                    } catch (DateTimeParseException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            log.warn("日期解析失败: {}", cell);
        }
        return null;
    }
    
    private BigDecimal parseAmount(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            } else {
                String amountStr = getStringCellValue(cell);
                if (amountStr == null || amountStr.trim().isEmpty()) return null;
                return new BigDecimal(amountStr);
            }
        } catch (Exception e) {
            log.warn("金额解析失败: {}", cell);
            return null;
        }
    }
    
    private String generateBatchNo() {
        return "IMP" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
               UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private void createDescriptionSheet(Sheet sheet) {
        String[] descriptions = {
            "资产导入模板填写说明",
            "",
            "1. 带*号的字段为必填项",
            "",
            "2. 资产类别代码对照：",
            "   PC = 台式主机",
            "   NB = 笔记本",
            "   IP = iPad/平板",
            "   DS = 显示器",
            "   PJ = 投影仪",
            "   PE = 外设",
            "   NT = 网络设备",
            "   PO = 收银设备",
            "   MAT = 物料",
            "",
            "3. 日期格式：yyyy-MM-dd（如：2026-01-15）",
            "",
            "4. 金额格式：数字即可，不需要添加单位",
            "",
            "5. 存放位置类型：总部/门店/仓库",
            "",
            "6. 使用状态：默认为'库存'，可填写：库存/在用/维修中/报废",
            "",
            "7. 资产编号规则：类别代码-年份-流水号（如：NB-26-0001）",
            "",
            "8. 请勿修改表头，否则会导致导入失败"
        };
        
        for (int i = 0; i < descriptions.length; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(descriptions[i]);
        }
        
        sheet.setColumnWidth(0, 80 * 256);
    }
}