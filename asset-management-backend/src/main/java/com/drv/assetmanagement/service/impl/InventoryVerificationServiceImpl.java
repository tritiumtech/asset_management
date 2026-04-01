package com.drv.assetmanagement.service.impl;

import com.alibaba.fastjson.JSON;
import com.drv.assetmanagement.dto.*;
import com.drv.assetmanagement.entity.*;
import com.drv.assetmanagement.repository.*;
import com.drv.assetmanagement.service.InventoryVerificationService;
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
public class InventoryVerificationServiceImpl implements InventoryVerificationService {

    private final InventoryItemMapper itemMapper;
    private final InventoryVerificationMapper verificationMapper;
    private final WorkflowInstanceMapper workflowMapper;
    private final WorkflowApprovalMapper approvalMapper;

    @Override
    @Transactional
    public Result<VerificationRecordDTO> submitVerification(VerificationSubmitDTO submitDTO) {
        Long itemId = submitDTO.getItemId();
        
        // 查询盘点明细
        InventoryItem item = itemMapper.selectById(itemId);
        if (item == null) {
            return Result.error("盘点记录不存在");
        }
        
        // 检查是否已结案
        if (Boolean.TRUE.equals(item.getIsClosed())) {
            return Result.error("该盘点记录已结案，无法提交");
        }
        
        // 检查盘点结果是否已录入
        if (item.getResult() == null) {
            return Result.error("请先完成盘点扫描，再提交核实");
        }
        
        // 获取当前轮次
        Integer currentRound = verificationMapper.findMaxRoundByItemId(itemId);
        if (currentRound == null) {
            currentRound = 0;
        }
        Integer nextRound = currentRound + 1;
        
        // 检查最大轮次限制（防止无限循环）
        if (nextRound > 10) {
            return Result.error("已超过最大核实轮次限制(10轮)，请联系管理员处理");
        }
        
        // 创建核实记录
        InventoryVerification verification = new InventoryVerification();
        verification.setInventoryItemId(itemId);
        verification.setTaskId(item.getTaskId());
        verification.setRound(nextRound);
        verification.setAction("SUBMIT");
        verification.setResult(submitDTO.getResult());
        verification.setComment(submitDTO.getComment());
        
        // 设置提交人信息（应该从SecurityContext获取）
        verification.setVerifierId(1L); // TODO: 从上下文获取
        verification.setVerifierName("提交人"); // TODO: 从上下文获取
        verification.setVerifierType("USER");
        
        // 处理附件
        if (submitDTO.getAttachments() != null && !submitDTO.getAttachments().isEmpty()) {
            verification.setAttachments(JSON.toJSONString(submitDTO.getAttachments()));
        }
        
        verificationMapper.insert(verification);
        
        // 更新盘点明细状态
        item.setCurrentStatus("UNDER_REVIEW");
        item.setCurrentRound(nextRound);
        itemMapper.updateById(item);
        
        // 更新或创建工作流实例
        updateWorkflowInstance(item, submitDTO, nextRound);
        
        log.info("盘点核实提交成功: itemId={}, round={}", itemId, nextRound);
        
        return Result.success(convertToDTO(verification));
    }

    @Override
    @Transactional
    public Result<VerificationRecordDTO> reviewVerification(VerificationSubmitDTO reviewDTO) {
        Long itemId = reviewDTO.getItemId();
        
        InventoryItem item = itemMapper.selectById(itemId);
        if (item == null) {
            return Result.error("盘点记录不存在");
        }
        
        // 获取当前轮次
        Integer currentRound = item.getCurrentRound();
        if (currentRound == null || currentRound == 0) {
            return Result.error("该记录尚未提交核实");
        }
        
        // 创建审核记录
        InventoryVerification verification = new InventoryVerification();
        verification.setInventoryItemId(itemId);
        verification.setTaskId(item.getTaskId());
        verification.setRound(currentRound);
        verification.setAction("REVIEW");
        verification.setResult(reviewDTO.getResult());
        verification.setComment(reviewDTO.getComment());
        
        // 设置审核人信息（管理方）
        verification.setVerifierId(2L); // TODO: 从上下文获取
        verification.setVerifierName("管理员"); // TODO: 从上下文获取
        verification.setVerifierType("MANAGER");
        
        if (reviewDTO.getAttachments() != null && !reviewDTO.getAttachments().isEmpty()) {
            verification.setAttachments(JSON.toJSONString(reviewDTO.getAttachments()));
        }
        
        verificationMapper.insert(verification);
        
        // 根据审核结果更新状态
        if ("PASS".equals(reviewDTO.getResult())) {
            item.setCurrentStatus("RESOLVED");
            // 如果是通过，可以设置为待结案状态
        } else if ("REJECT".equals(reviewDTO.getResult())) {
            item.setCurrentStatus("REJECTED");
        } else if ("NEED_INFO".equals(reviewDTO.getResult())) {
            item.setCurrentStatus("NEED_INFO");
        }
        
        itemMapper.updateById(item);
        
        log.info("盘点核实审核完成: itemId={}, round={}, result={}", 
                itemId, currentRound, reviewDTO.getResult());
        
        return Result.success(convertToDTO(verification));
    }

    @Override
    @Transactional
    public Result<VerificationRecordDTO> returnForModification(VerificationSubmitDTO returnDTO) {
        Long itemId = returnDTO.getItemId();
        
        InventoryItem item = itemMapper.selectById(itemId);
        if (item == null) {
            return Result.error("盘点记录不存在");
        }
        
        Integer currentRound = item.getCurrentRound();
        
        // 创建退回记录
        InventoryVerification verification = new InventoryVerification();
        verification.setInventoryItemId(itemId);
        verification.setTaskId(item.getTaskId());
        verification.setRound(currentRound);
        verification.setAction("RETURN");
        verification.setResult("NEED_INFO");
        verification.setComment(returnDTO.getComment());
        verification.setVerifierId(2L);
        verification.setVerifierName("管理员");
        verification.setVerifierType("MANAGER");
        
        if (returnDTO.getAttachments() != null && !returnDTO.getAttachments().isEmpty()) {
            verification.setAttachments(JSON.toJSONString(returnDTO.getAttachments()));
        }
        
        verificationMapper.insert(verification);
        
        // 更新状态为需要补充信息
        item.setCurrentStatus("NEED_INFO");
        itemMapper.updateById(item);
        
        log.info("盘点核实退回修改: itemId={}, round={}", itemId, currentRound);
        
        return Result.success(convertToDTO(verification));
    }

    @Override
    @Transactional
    public Result<Void> closeVerification(Long itemId, String comment) {
        InventoryItem item = itemMapper.selectById(itemId);
        if (item == null) {
            return Result.error("盘点记录不存在");
        }
        
        Integer currentRound = item.getCurrentRound();
        
        // 创建结案记录
        InventoryVerification verification = new InventoryVerification();
        verification.setInventoryItemId(itemId);
        verification.setTaskId(item.getTaskId());
        verification.setRound(currentRound);
        verification.setAction("CLOSE");
        verification.setResult("CLOSED");
        verification.setComment(comment);
        verification.setVerifierId(2L);
        verification.setVerifierName("管理员");
        verification.setVerifierType("MANAGER");
        
        verificationMapper.insert(verification);
        
        // 更新盘点明细为结案状态
        item.setIsClosed(true);
        item.setCurrentStatus("CLOSED");
        item.setFinalResult(item.getResult()); // 设置最终结果
        itemMapper.updateById(item);
        
        // 关闭工作流
        closeWorkflowInstance(item.getTaskId(), itemId);
        
        log.info("盘点核实结案: itemId={}", itemId);
        
        return Result.success();
    }

    @Override
    public Result<List<VerificationRecordDTO>> getVerificationRecords(Long itemId) {
        List<InventoryVerification> records = verificationMapper.findByItemId(itemId);
        
        List<VerificationRecordDTO> dtoList = records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return Result.success(dtoList);
    }

    @Override
    public Result<Integer> getCurrentRound(Long itemId) {
        Integer maxRound = verificationMapper.findMaxRoundByItemId(itemId);
        return Result.success(maxRound != null ? maxRound : 0);
    }

    @Override
    @Transactional
    public Result<Void> handleOACallback(OACallbackDTO callbackDTO) {
        log.info("处理OA回调: type={}, bizId={}, action={}", 
                callbackDTO.getCallbackType(), 
                callbackDTO.getBizId(), 
                callbackDTO.getAction());
        
        // 验证签名
        if (!verifySignature(callbackDTO)) {
            return Result.error("签名验证失败");
        }
        
        // 根据回调类型处理
        if ("INVENTORY_VERIFICATION".equals(callbackDTO.getCallbackType())) {
            // 构造提交DTO
            VerificationSubmitDTO submitDTO = new VerificationSubmitDTO();
            submitDTO.setItemId(callbackDTO.getBizId());
            submitDTO.setAction(callbackDTO.getAction());
            submitDTO.setResult(callbackDTO.getData().getResult());
            submitDTO.setComment(callbackDTO.getData().getComment());
            submitDTO.setSource("OA");
            
            // 根据action分发
            switch (callbackDTO.getAction()) {
                case "SUBMIT":
                    submitVerification(submitDTO);
                    break;
                case "REVIEW":
                    reviewVerification(submitDTO);
                    break;
                case "RETURN":
                    returnForModification(submitDTO);
                    break;
                case "CLOSE":
                    closeVerification(callbackDTO.getBizId(), callbackDTO.getData().getComment());
                    break;
                default:
                    return Result.error("未知的操作类型: " + callbackDTO.getAction());
            }
        }
        
        return Result.success();
    }
    
    private void updateWorkflowInstance(InventoryItem item, VerificationSubmitDTO submitDTO, Integer round) {
        // 查询是否已有工作流实例
        WorkflowInstance workflow = workflowMapper.findActiveByBiz("INVENTORY", item.getId());
        
        if (workflow == null) {
            // 创建工作流实例
            workflow = new WorkflowInstance();
            workflow.setBizType("INVENTORY");
            workflow.setBizId(item.getId());
            workflow.setBizCode(item.getAssetCode());
            workflow.setStatus("UNDER_REVIEW");
            workflow.setSource(submitDTO.getSource() != null ? submitDTO.getSource() : "INTERNAL");
            workflow.setCallbackUrl(submitDTO.getCallbackUrl());
            workflow.setCurrentRound(round);
            workflow.setMaxRounds(10);
            workflow.setIsClosed(false);
            workflow.setCreatedBy(1L);
            workflow.setCreatedByName("系统");
            workflowMapper.insert(workflow);
        } else {
            // 更新轮次
            workflow.setCurrentRound(round);
            workflow.setUpdateTime(LocalDateTime.now());
            workflowMapper.updateById(workflow);
        }
    }
    
    private void closeWorkflowInstance(Long taskId, Long itemId) {
        WorkflowInstance workflow = workflowMapper.findActiveByBiz("INVENTORY", itemId);
        if (workflow != null) {
            workflowMapper.closeWorkflow(workflow.getId());
        }
    }
    
    private boolean verifySignature(OACallbackDTO callbackDTO) {
        // 如果未配置签名，直接通过（开发环境）
        if (callbackDTO.getSignature() == null || callbackDTO.getTimestamp() == null) {
            log.warn("OA回调缺少签名或时间戳，开发环境直接通过");
            return true;
        }
        
        try {
            // 1. 拼接签名字符串
            String signContent = callbackDTO.getCallbackType() + 
                    callbackDTO.getBizId() + 
                    callbackDTO.getAction() + 
                    callbackDTO.getTimestamp();
            
            // 2. 从数据库获取该工作流的签名密钥
            WorkflowInstance workflow = workflowMapper.findActiveByBiz(
                    callbackDTO.getCallbackType(), 
                    callbackDTO.getBizId()
            );
            
            String secretKey = (workflow != null && workflow.getCallbackToken() != null) 
                    ? workflow.getCallbackToken() 
                    : "default-secret-key"; // 默认密钥
            
            // 3. 计算HMAC-SHA256
            String calculatedSign = calculateHmacSHA256(signContent, secretKey);
            
            // 4. 对比签名（忽略大小写）
            boolean valid = calculatedSign.equalsIgnoreCase(callbackDTO.getSignature());
            
            if (!valid) {
                log.error("签名验证失败: content={}, expected={}, actual={}", 
                        signContent, calculatedSign, callbackDTO.getSignature());
            }
            
            return valid;
            
        } catch (Exception e) {
            log.error("签名验证异常", e);
            return false;
        }
    }
    
    private String calculateHmacSHA256(String content, String secret) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] bytes = mac.doFinal(content.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC计算失败", e);
        }
    }
    
    private VerificationRecordDTO convertToDTO(InventoryVerification verification) {
        VerificationRecordDTO dto = new VerificationRecordDTO();
        BeanUtils.copyProperties(verification, dto);
        
        // 解析附件JSON
        if (verification.getAttachments() != null) {
            try {
                List<String> attachments = JSON.parseArray(verification.getAttachments(), String.class);
                dto.setAttachments(attachments);
            } catch (Exception e) {
                log.warn("解析附件失败: {}", verification.getAttachments());
            }
        }
        
        return dto;
    }
}