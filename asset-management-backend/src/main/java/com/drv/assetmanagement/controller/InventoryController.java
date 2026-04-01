package com.drv.assetmanagement.controller;

import com.drv.assetmanagement.dto.*;
import com.drv.assetmanagement.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/tasks")
    public Result<PageResult<InventoryTaskDTO>> listTasks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return inventoryService.listTasks(pageNum, pageSize);
    }

    @GetMapping("/tasks/{id}")
    public Result<InventoryTaskDTO> getTaskById(@PathVariable Long id) {
        return inventoryService.getTaskById(id);
    }

    @PostMapping("/tasks")
    public Result<Void> createTask(@RequestBody InventoryTaskDTO taskDTO) {
        return inventoryService.createTask(taskDTO);
    }

    @PostMapping("/tasks/{id}/start")
    public Result<Void> startTask(@PathVariable Long id) {
        return inventoryService.startTask(id);
    }

    @PostMapping("/tasks/{id}/complete")
    public Result<Void> completeTask(@PathVariable Long id) {
        return inventoryService.completeTask(id);
    }

    @GetMapping("/tasks/{taskId}/items")
    public Result<PageResult<InventoryItemDTO>> listItems(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return inventoryService.listItems(taskId, pageNum, pageSize);
    }

    @PostMapping("/scan")
    public Result<InventoryItemDTO> scanAsset(@RequestBody ScanInventoryDTO scanDTO) {
        return inventoryService.scanAsset(scanDTO);
    }

    @PostMapping("/verify")
    public Result<Void> verifyItem(@RequestBody VerifyInventoryDTO verifyDTO) {
        return inventoryService.verifyItem(verifyDTO);
    }

    @GetMapping("/tasks/{taskId}/statistics")
    public Result<InventoryStatisticsDTO> getTaskStatistics(@PathVariable Long taskId) {
        return inventoryService.getTaskStatistics(taskId);
    }
}