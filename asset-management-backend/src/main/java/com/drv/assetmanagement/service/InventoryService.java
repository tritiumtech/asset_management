package com.drv.assetmanagement.service;

import com.drv.assetmanagement.dto.*;

public interface InventoryService {
    
    Result<PageResult<InventoryTaskDTO>> listTasks(Integer pageNum, Integer pageSize);
    
    Result<InventoryTaskDTO> getTaskById(Long id);
    
    Result<Void> createTask(InventoryTaskDTO taskDTO);
    
    Result<Void> startTask(Long taskId);
    
    Result<Void> completeTask(Long taskId);
    
    Result<PageResult<InventoryItemDTO>> listItems(Long taskId, Integer pageNum, Integer pageSize);
    
    Result<InventoryItemDTO> scanAsset(ScanInventoryDTO scanDTO);
    
    Result<Void> verifyItem(VerifyInventoryDTO verifyDTO);
    
    Result<InventoryStatisticsDTO> getTaskStatistics(Long taskId);
}