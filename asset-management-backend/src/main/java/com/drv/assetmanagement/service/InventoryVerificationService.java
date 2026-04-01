package com.drv.assetmanagement.service;

import com.drv.assetmanagement.dto.*;

import java.util.List;

/**
 * 盘点核实服务 - 支持多轮沟通
 */
public interface InventoryVerificationService {
    
    /**
     * 提交核实（被管理方）
     */
    Result<VerificationRecordDTO> submitVerification(VerificationSubmitDTO submitDTO);
    
    /**
     * 管理方审核
     */
    Result<VerificationRecordDTO> reviewVerification(VerificationSubmitDTO reviewDTO);
    
    /**
     * 驳回修改（管理方→被管理方）
     */
    Result<VerificationRecordDTO> returnForModification(VerificationSubmitDTO returnDTO);
    
    /**
     * 结案
     */
    Result<Void> closeVerification(Long itemId, String comment);
    
    /**
     * 获取核实记录列表
     */
    Result<List<VerificationRecordDTO>> getVerificationRecords(Long itemId);
    
    /**
     * 获取当前轮次
     */
    Result<Integer> getCurrentRound(Long itemId);
    
    /**
     * 处理OA回调
     */
    Result<Void> handleOACallback(OACallbackDTO callbackDTO);
}