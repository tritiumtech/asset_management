package com.drv.assetmanagement.controller;

import com.drv.assetmanagement.dto.*;
import com.drv.assetmanagement.service.AssetProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 资产流程Controller - 领用/调拨/报修/报废/归还
 */
@RestController
@RequestMapping("/api/asset-process")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssetProcessController {

    private final AssetProcessService processService;

    /**
     * 申请流程
     */
    @PostMapping("/apply")
    public Result<WorkflowInstanceDTO> apply(@Valid @RequestBody AssetProcessApplyDTO applyDTO) {
        return processService.apply(applyDTO);
    }

    /**
     * 审批流程
     */
    @PostMapping("/approve")
    public Result<AssetProcessRecordDTO> approve(@Valid @RequestBody AssetProcessApproveDTO approveDTO) {
        return processService.approve(approveDTO);
    }

    /**
     * 重新申请
     */
    @PostMapping("/{workflowId}/reapply")
    public Result<WorkflowInstanceDTO> reapply(
            @PathVariable Long workflowId,
            @Valid @RequestBody AssetProcessApplyDTO applyDTO) {
        return processService.reapply(workflowId, applyDTO);
    }

    /**
     * 结案流程
     */
    @PostMapping("/{workflowId}/close")
    public Result<Void> close(
            @PathVariable Long workflowId,
            @RequestParam(required = false) String comment) {
        return processService.close(workflowId, comment);
    }

    /**
     * 获取流程详情
     */
    @GetMapping("/{workflowId}")
    public Result<AssetProcessRecordDTO> getDetail(@PathVariable Long workflowId) {
        return processService.getProcessDetail(workflowId);
    }

    /**
     * 获取资产流程列表
     */
    @GetMapping("/asset/{assetId}")
    public Result<PageResult<AssetProcessRecordDTO>> listByAsset(
            @PathVariable Long assetId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return processService.listAssetProcesses(assetId, pageNum, pageSize);
    }

    /**
     * 获取我的待办
     */
    @GetMapping("/my-pending")
    public Result<PageResult<AssetProcessRecordDTO>> getMyPending(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return processService.getMyPendingTasks(userId, pageNum, pageSize);
    }

    /**
     * OA回调端点
     */
    @PostMapping("/callback")
    public Result<Void> handleOACallback(@Valid @RequestBody OACallbackDTO callbackDTO) {
        return processService.handleOACallback(callbackDTO);
    }
}