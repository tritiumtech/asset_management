package com.drv.assetmanagement.service;

import com.drv.assetmanagement.dto.*;

import java.util.List;

/**
 * 资产流程服务 - 领用/调拨/报修/报废/归还
 */
public interface AssetProcessService {
    
    /**
     * 申请流程
     */
    Result<WorkflowInstanceDTO> apply(AssetProcessApplyDTO applyDTO);
    
    /**
     * 审批流程
     */
    Result<AssetProcessRecordDTO> approve(AssetProcessApproveDTO approveDTO);
    
    /**
     * 重新申请（被驳回后）
     */
    Result<WorkflowInstanceDTO> reapply(Long workflowId, AssetProcessApplyDTO applyDTO);
    
    /**
     * 结案流程
     */
    Result<Void> close(Long workflowId, String comment);
    
    /**
     * 获取流程详情
     */
    Result<AssetProcessRecordDTO> getProcessDetail(Long workflowId);
    
    /**
     * 获取资产流程列表
     */
    Result<PageResult<AssetProcessRecordDTO>> listAssetProcesses(Long assetId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取我的待办流程
     */
    Result<PageResult<AssetProcessRecordDTO>> getMyPendingTasks(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 处理OA回调
     */
    Result<Void> handleOACallback(OACallbackDTO callbackDTO);
}