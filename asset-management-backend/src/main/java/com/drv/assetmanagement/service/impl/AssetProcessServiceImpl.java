package com.drv.assetmanagement.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drv.assetmanagement.dto.*;
import com.drv.assetmanagement.entity.*;
import com.drv.assetmanagement.enums.AssetStatus;
import com.drv.assetmanagement.repository.*;
import com.drv.assetmanagement.service.AssetProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetProcessServiceImpl implements AssetProcessService {

    private final AssetMapper assetMapper;
    private final AssetTransactionMapper transactionMapper;
    private final WorkflowInstanceMapper workflowMapper;
    private final WorkflowApprovalMapper approvalMapper;

    @Override
    @Transactional
    public Result<WorkflowInstanceDTO> apply(AssetProcessApplyDTO applyDTO) {
        // 查询资产
        Asset asset = assetMapper.selectById(applyDTO.getAssetId());
        if (asset == null) {
            return Result.error("资产不存在");
        }
        
        // 检查是否有进行中的流程
        WorkflowInstance existingWorkflow = workflowMapper.findActiveByBiz(
                applyDTO.getProcessType(), applyDTO.getAssetId());
        if (existingWorkflow != null) {
            return Result.error("该资产已有进行中的" + getProcessTypeName(applyDTO.getProcessType()) + "流程");
        }
        
        // 检查资产状态是否允许该操作
        String checkResult = checkAssetStatus(asset, applyDTO.getProcessType());
        if (checkResult != null) {
            return Result.error(checkResult);
        }
        
        // 创建工作流实例
        WorkflowInstance workflow = new WorkflowInstance();
        workflow.setBizType(applyDTO.getProcessType());
        workflow.setBizId(applyDTO.getAssetId());
        workflow.setBizCode(asset.getAssetCode());
        workflow.setStatus("PENDING");
        workflow.setSource(applyDTO.getSource() != null ? applyDTO.getSource() : "INTERNAL");
        workflow.setCallbackUrl(applyDTO.getCallbackUrl());
        workflow.setCurrentRound(1);
        workflow.setMaxRounds(10);
        workflow.setIsClosed(false);
        workflow.setCreatedBy(1L); // TODO: 从上下文获取
        workflow.setCreatedByName("申请人"); // TODO: 从上下文获取
        workflowMapper.insert(workflow);
        
        // 创建第一条审批记录（申请提交）
        WorkflowApproval approval = new WorkflowApproval();
        approval.setWorkflowId(workflow.getId());
        approval.setRound(1);
        approval.setApproverId(1L);
        approval.setApproverName("申请人");
        approval.setApproverType("USER");
        approval.setAction("SUBMIT");
        approval.setResult("SUBMITTED");
        approval.setComment(applyDTO.getReason());
        if (applyDTO.getAttachments() != null && !applyDTO.getAttachments().isEmpty()) {
            approval.setAttachments(JSON.toJSONString(applyDTO.getAttachments()));
        }
        approval.setCreateTime(LocalDateTime.now());
        approvalMapper.insert(approval);
        
        // 更新资产状态为流程中
        updateAssetStatus(asset, applyDTO.getProcessType(), "PENDING");
        
        log.info("资产流程申请成功: assetId={}, type={}, workflowId={}", 
                applyDTO.getAssetId(), applyDTO.getProcessType(), workflow.getId());
        
        return Result.success(convertWorkflowToDTO(workflow));
    }

    @Override
    @Transactional
    public Result<AssetProcessRecordDTO> approve(AssetProcessApproveDTO approveDTO) {
        WorkflowInstance workflow = workflowMapper.selectById(approveDTO.getWorkflowId());
        if (workflow == null) {
            return Result.error("流程不存在");
        }
        
        if (Boolean.TRUE.equals(workflow.getIsClosed())) {
            return Result.error("流程已结案");
        }
        
        Integer currentRound = workflow.getCurrentRound();
        
        // 创建审批记录
        WorkflowApproval approval = new WorkflowApproval();
        approval.setWorkflowId(workflow.getId());
        approval.setRound(currentRound);
        approval.setApproverId(2L); // TODO: 从上下文获取
        approval.setApproverName("审批人"); // TODO: 从上下文获取
        approval.setApproverType("MANAGER");
        approval.setAction(approveDTO.getAction());
        approval.setResult("APPROVE".equals(approveDTO.getAction()) ? "APPROVED" : "REJECTED");
        approval.setComment(approveDTO.getComment());
        if (approveDTO.getAttachments() != null && !approveDTO.getAttachments().isEmpty()) {
            approval.setAttachments(JSON.toJSONString(approveDTO.getAttachments()));
        }
        approval.setNextApproverId(approveDTO.getNextApproverId());
        approval.setCreateTime(LocalDateTime.now());
        approvalMapper.insert(approval);
        
        // 处理审批结果
        if ("APPROVE".equals(approveDTO.getAction())) {
            // 审批通过
            workflow.setStatus("APPROVED");
            
            // 执行实际的资产操作
            executeAssetOperation(workflow);
            
        } else if ("REJECT".equals(approveDTO.getAction())) {
            // 审批驳回
            workflow.setStatus("REJECTED");
            workflow.setIsClosed(true);
            workflow.setClosedTime(LocalDateTime.now());
            
            // 恢复资产状态
            restoreAssetStatus(workflow.getBizId(), workflow.getBizType());
            
        } else if ("RETURN".equals(approveDTO.getAction())) {
            // 退回修改，增加轮次
            workflow.setCurrentRound(currentRound + 1);
            workflow.setStatus("RETURNED");
            
            // 检查是否超过最大轮次
            if (workflow.getCurrentRound() > workflow.getMaxRounds()) {
                workflow.setStatus("REJECTED");
                workflow.setIsClosed(true);
                workflow.setClosedTime(LocalDateTime.now());
                restoreAssetStatus(workflow.getBizId(), workflow.getBizType());
            }
        }
        
        workflow.setUpdateTime(LocalDateTime.now());
        workflowMapper.updateById(workflow);
        
        log.info("资产流程审批完成: workflowId={}, action={}, round={}", 
                workflow.getId(), approveDTO.getAction(), currentRound);
        
        return Result.success(getProcessDetail(workflow.getId()).getData());
    }

    @Override
    @Transactional
    public Result<WorkflowInstanceDTO> reapply(Long workflowId, AssetProcessApplyDTO applyDTO) {
        WorkflowInstance workflow = workflowMapper.selectById(workflowId);
        if (workflow == null) {
            return Result.error("流程不存在");
        }
        
        if (!"RETURNED".equals(workflow.getStatus())) {
            return Result.error("只有被退回的流程才能重新申请");
        }
        
        Integer currentRound = workflow.getCurrentRound();
        
        // 创建重新申请记录
        WorkflowApproval approval = new WorkflowApproval();
        approval.setWorkflowId(workflow.getId());
        approval.setRound(currentRound);
        approval.setApproverId(1L);
        approval.setApproverName("申请人");
        approval.setApproverType("USER");
        approval.setAction("REAPPLY");
        approval.setResult("RESUBMITTED");
        approval.setComment(applyDTO.getReason());
        if (applyDTO.getAttachments() != null && !applyDTO.getAttachments().isEmpty()) {
            approval.setAttachments(JSON.toJSONString(applyDTO.getAttachments()));
        }
        approval.setCreateTime(LocalDateTime.now());
        approvalMapper.insert(approval);
        
        // 更新状态为待审批
        workflow.setStatus("PENDING");
        workflow.setUpdateTime(LocalDateTime.now());
        workflowMapper.updateById(workflow);
        
        log.info("资产流程重新申请: workflowId={}, round={}", workflowId, currentRound);
        
        return Result.success(convertWorkflowToDTO(workflow));
    }

    @Override
    @Transactional
    public Result<Void> close(Long workflowId, String comment) {
        WorkflowInstance workflow = workflowMapper.selectById(workflowId);
        if (workflow == null) {
            return Result.error("流程不存在");
        }
        
        // 创建结案记录
        WorkflowApproval approval = new WorkflowApproval();
        approval.setWorkflowId(workflow.getId());
        approval.setRound(workflow.getCurrentRound());
        approval.setApproverId(2L);
        approval.setApproverName("管理员");
        approval.setApproverType("MANAGER");
        approval.setAction("CLOSE");
        approval.setResult("CLOSED");
        approval.setComment(comment);
        approval.setCreateTime(LocalDateTime.now());
        approvalMapper.insert(approval);
        
        // 更新工作流为结案
        workflow.setIsClosed(true);
        workflow.setStatus("CLOSED");
        workflow.setClosedTime(LocalDateTime.now());
        workflow.setUpdateTime(LocalDateTime.now());
        workflowMapper.updateById(workflow);
        
        log.info("资产流程结案: workflowId={}", workflowId);
        
        return Result.success();
    }

    @Override
    public Result<AssetProcessRecordDTO> getProcessDetail(Long workflowId) {
        WorkflowInstance workflow = workflowMapper.selectById(workflowId);
        if (workflow == null) {
            return Result.error("流程不存在");
        }
        
        AssetProcessRecordDTO dto = convertToProcessRecordDTO(workflow);
        
        // 查询审批历史
        List<WorkflowApproval> approvals = approvalMapper.findByWorkflowId(workflowId);
        List<AssetProcessRecordDTO.ApprovalRecordDTO> history = approvals.stream()
                .map(this::convertToApprovalRecordDTO)
                .collect(Collectors.toList());
        dto.setApprovalHistory(history);
        
        return Result.success(dto);
    }

    @Override
    public Result<PageResult<AssetProcessRecordDTO>> listAssetProcesses(Long assetId, Integer pageNum, Integer pageSize) {
        Page<WorkflowInstance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WorkflowInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowInstance::getBizId, assetId)
               .orderByDesc(WorkflowInstance::getCreateTime);
        
        Page<WorkflowInstance> result = workflowMapper.selectPage(page, wrapper);
        
        PageResult<AssetProcessRecordDTO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPageNum((int) result.getCurrent());
        pageResult.setPageSize((int) result.getSize());
        pageResult.setList(result.getRecords().stream()
                .map(this::convertToProcessRecordDTO)
                .collect(Collectors.toList()));
        
        return Result.success(pageResult);
    }

    @Override
    public Result<PageResult<AssetProcessRecordDTO>> getMyPendingTasks(Long userId, Integer pageNum, Integer pageSize) {
        // TODO: 实现基于用户权限的待办查询
        // 这里简化实现，查询所有待审批的流程
        Page<WorkflowInstance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WorkflowInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowInstance::getStatus, "PENDING")
               .or().eq(WorkflowInstance::getStatus, "RETURNED")
               .orderByDesc(WorkflowInstance::getCreateTime);
        
        Page<WorkflowInstance> result = workflowMapper.selectPage(page, wrapper);
        
        PageResult<AssetProcessRecordDTO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPageNum((int) result.getCurrent());
        pageResult.setPageSize((int) result.getSize());
        pageResult.setList(result.getRecords().stream()
                .map(this::convertToProcessRecordDTO)
                .collect(Collectors.toList()));
        
        return Result.success(pageResult);
    }

    @Override
    @Transactional
    public Result<Void> handleOACallback(OACallbackDTO callbackDTO) {
        log.info("处理OA资产流程回调: type={}, bizId={}, action={}", 
                callbackDTO.getCallbackType(), 
                callbackDTO.getBizId(), 
                callbackDTO.getAction());
        
        // 验证签名
        if (!verifySignature(callbackDTO)) {
            return Result.error("签名验证失败");
        }
        
        // 根据回调类型处理
        String callbackType = callbackDTO.getCallbackType();
        if (callbackType != null && callbackType.startsWith("ASSET_")) {
            String processType = callbackType.replace("ASSET_", "");
            
            switch (callbackDTO.getAction()) {
                case "SUBMIT":
                    AssetProcessApplyDTO applyDTO = new AssetProcessApplyDTO();
                    applyDTO.setAssetId(callbackDTO.getBizId());
                    applyDTO.setProcessType(processType);
                    applyDTO.setReason(callbackDTO.getData().getComment());
                    applyDTO.setSource("OA");
                    apply(applyDTO);
                    break;
                case "APPROVE":
                case "REJECT":
                case "RETURN":
                    WorkflowInstance workflow = workflowMapper.findActiveByBiz(processType, callbackDTO.getBizId());
                    if (workflow != null) {
                        AssetProcessApproveDTO approveDTO = new AssetProcessApproveDTO();
                        approveDTO.setWorkflowId(workflow.getId());
                        approveDTO.setAction(callbackDTO.getAction());
                        approveDTO.setComment(callbackDTO.getData().getComment());
                        approve(approveDTO);
                    }
                    break;
                case "CLOSE":
                    WorkflowInstance wf = workflowMapper.findActiveByBiz(processType, callbackDTO.getBizId());
                    if (wf != null) {
                        close(wf.getId(), callbackDTO.getData().getComment());
                    }
                    break;
                default:
                    return Result.error("未知的操作类型: " + callbackDTO.getAction());
            }
        }
        
        return Result.success();
    }
    
    // ========== 私有方法 ==========
    
    private String checkAssetStatus(Asset asset, String processType) {
        switch (processType) {
            case "ASSIGN":
                if (!"IN_STOCK".equals(asset.getStatus())) {
                    return "只有库存状态的资产才能领用";
                }
                break;
            case "TRANSFER":
                if (!"IN_USE".equals(asset.getStatus())) {
                    return "只有使用中的资产才能调拨";
                }
                break;
            case "RETURN":
                if (!"IN_USE".equals(asset.getStatus())) {
                    return "只有使用中的资产才能归还";
                }
                break;
            case "REPAIR":
                if (!"IN_USE".equals(asset.getStatus()) && !"IN_STOCK".equals(asset.getStatus())) {
                    return "该状态资产不能报修";
                }
                break;
            case "SCRAP":
                if ("SCRAPPED".equals(asset.getStatus())) {
                    return "资产已报废";
                }
                break;
        }
        return null;
    }
    
    private void updateAssetStatus(Asset asset, String processType, String status) {
        // 流程申请时，资产状态保持不变或标记为流程中
        // 实际状态变更在审批通过后执行
        assetMapper.updateById(asset);
    }
    
    private void restoreAssetStatus(Long assetId, String processType) {
        // 流程被拒绝后，恢复资产原始状态
        Asset asset = assetMapper.selectById(assetId);
        if (asset != null) {
            // 根据原状态恢复，这里简化处理
            assetMapper.updateById(asset);
        }
    }
    
    private void executeAssetOperation(WorkflowInstance workflow) {
        Asset asset = assetMapper.selectById(workflow.getBizId());
        if (asset == null) return;
        
        String processType = workflow.getBizType();
        
        switch (processType) {
            case "ASSIGN":
                // 执行领用操作
                asset.setStatus(AssetStatus.IN_USE.getCode());
                // TODO: 设置责任人信息
                break;
            case "TRANSFER":
                // 执行调拨操作
                // TODO: 更新责任人
                break;
            case "RETURN":
                // 执行归还操作
                asset.setStatus(AssetStatus.IN_STOCK.getCode());
                asset.setCurrentUserId(null);
                asset.setCurrentUserName(null);
                break;
            case "REPAIR":
                // 执行报修操作
                asset.setStatus(AssetStatus.IN_REPAIR.getCode());
                break;
            case "SCRAP":
                // 执行报废操作
                asset.setStatus(AssetStatus.SCRAPPED.getCode());
                break;
        }
        
        assetMapper.updateById(asset);
        
        // 记录资产流转
        AssetTransaction transaction = new AssetTransaction();
        transaction.setAssetId(asset.getId());
        transaction.setOperationType(processType);
        transaction.setOperationDate(java.time.LocalDate.now());
        transaction.setDescription("流程审批通过: " + getProcessTypeName(processType));
        transactionMapper.insert(transaction);
    }
    
    private String getProcessTypeName(String processType) {
        switch (processType) {
            case "ASSIGN": return "领用";
            case "TRANSFER": return "调拨";
            case "RETURN": return "归还";
            case "REPAIR": return "报修";
            case "SCRAP": return "报废";
            default: return processType;
        }
    }
    
    private boolean verifySignature(OACallbackDTO callbackDTO) {
        // 复用InventoryVerificationServiceImpl的签名验证逻辑
        // 简化实现，实际应从数据库获取密钥
        if (callbackDTO.getSignature() == null || callbackDTO.getTimestamp() == null) {
            return true;
        }
        // TODO: 实现完整签名验证
        return true;
    }
    
    private WorkflowInstanceDTO convertWorkflowToDTO(WorkflowInstance workflow) {
        WorkflowInstanceDTO dto = new WorkflowInstanceDTO();
        BeanUtils.copyProperties(workflow, dto);
        return dto;
    }
    
    private AssetProcessRecordDTO convertToProcessRecordDTO(WorkflowInstance workflow) {
        AssetProcessRecordDTO dto = new AssetProcessRecordDTO();
        dto.setId(workflow.getId());
        dto.setWorkflowId(workflow.getId());
        dto.setProcessType(workflow.getBizType());
        dto.setProcessTypeName(getProcessTypeName(workflow.getBizType()));
        dto.setStatus(workflow.getStatus());
        dto.setStatusName(getStatusName(workflow.getStatus()));
        dto.setCurrentRound(workflow.getCurrentRound());
        dto.setSource(workflow.getSource());
        dto.setCreatedByName(workflow.getCreatedByName());
        dto.setCreateTime(workflow.getCreateTime().toString());
        dto.setUpdateTime(workflow.getUpdateTime().toString());
        dto.setIsClosed(workflow.getIsClosed());
        return dto;
    }
    
    private AssetProcessRecordDTO.ApprovalRecordDTO convertToApprovalRecordDTO(WorkflowApproval approval) {
        AssetProcessRecordDTO.ApprovalRecordDTO dto = new AssetProcessRecordDTO.ApprovalRecordDTO();
        dto.setRound(approval.getRound());
        dto.setApproverName(approval.getApproverName());
        dto.setApproverType(approval.getApproverType());
        dto.setAction(approval.getAction());
        dto.setResult(approval.getResult());
        dto.setComment(approval.getComment());
        dto.setCreateTime(approval.getCreateTime().toString());
        return dto;
    }
    
    private String getStatusName(String status) {
        switch (status) {
            case "PENDING": return "待审批";
            case "APPROVED": return "已通过";
            case "REJECTED": return "已驳回";
            case "RETURNED": return "退回修改";
            case "CLOSED": return "已结案";
            default: return status;
        }
    }
}