package com.drv.assetmanagement.controller;

import com.drv.assetmanagement.dto.*;
import com.drv.assetmanagement.service.InventoryVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 盘点核实Controller - Atomic API设计，支持OA回调
 */
@RestController
@RequestMapping("/api/inventory/verification")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryVerificationController {

    private final InventoryVerificationService verificationService;

    /**
     * 提交核实（被管理方）
     */
    @PostMapping("/{itemId}/submit")
    public Result<VerificationRecordDTO> submit(
            @PathVariable Long itemId,
            @Valid @RequestBody VerificationSubmitDTO submitDTO) {
        submitDTO.setItemId(itemId);
        submitDTO.setAction("SUBMIT");
        return verificationService.submitVerification(submitDTO);
    }

    /**
     * 管理方审核
     */
    @PostMapping("/{itemId}/review")
    public Result<VerificationRecordDTO> review(
            @PathVariable Long itemId,
            @Valid @RequestBody VerificationSubmitDTO reviewDTO) {
        reviewDTO.setItemId(itemId);
        reviewDTO.setAction("REVIEW");
        return verificationService.reviewVerification(reviewDTO);
    }

    /**
     * 驳回修改（管理方→被管理方）
     */
    @PostMapping("/{itemId}/return")
    public Result<VerificationRecordDTO> returnForModification(
            @PathVariable Long itemId,
            @Valid @RequestBody VerificationSubmitDTO returnDTO) {
        returnDTO.setItemId(itemId);
        returnDTO.setAction("RETURN");
        return verificationService.returnForModification(returnDTO);
    }

    /**
     * 结案
     */
    @PostMapping("/{itemId}/close")
    public Result<Void> close(
            @PathVariable Long itemId,
            @RequestParam(required = false) String comment) {
        return verificationService.closeVerification(itemId, comment);
    }

    /**
     * 获取核实记录列表
     */
    @GetMapping("/{itemId}/records")
    public Result<List<VerificationRecordDTO>> getRecords(@PathVariable Long itemId) {
        return verificationService.getVerificationRecords(itemId);
    }

    /**
     * 获取当前轮次
     */
    @GetMapping("/{itemId}/round")
    public Result<Integer> getCurrentRound(@PathVariable Long itemId) {
        return verificationService.getCurrentRound(itemId);
    }

    /**
     * OA回调端点 - 支持泛微OA集成
     */
    @PostMapping("/callback")
    public Result<Void> handleOACallback(@Valid @RequestBody OACallbackDTO callbackDTO) {
        return verificationService.handleOACallback(callbackDTO);
    }
}