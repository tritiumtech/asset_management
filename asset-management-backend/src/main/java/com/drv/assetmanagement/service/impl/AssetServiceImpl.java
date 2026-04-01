package com.drv.assetmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drv.assetmanagement.dto.*;
import com.drv.assetmanagement.entity.Asset;
import com.drv.assetmanagement.entity.AssetTransaction;
import com.drv.assetmanagement.enums.AssetStatus;
import com.drv.assetmanagement.enums.OperationType;
import com.drv.assetmanagement.repository.AssetMapper;
import com.drv.assetmanagement.repository.AssetTransactionMapper;
import com.drv.assetmanagement.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetMapper assetMapper;
    private final AssetTransactionMapper transactionMapper;

    @Override
    public Result<PageResult<AssetDTO>> listAssets(AssetQueryDTO queryDTO) {
        Page<Asset> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.like(Asset::getAssetName, queryDTO.getKeyword())
                   .or().like(Asset::getAssetCode, queryDTO.getKeyword())
                   .or().like(Asset::getSerialNumber, queryDTO.getKeyword());
        }
        if (queryDTO.getCategory() != null) {
            wrapper.eq(Asset::getCategory, queryDTO.getCategory());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Asset::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getCurrentUserId() != null) {
            wrapper.eq(Asset::getCurrentUserId, queryDTO.getCurrentUserId());
        }
        if (queryDTO.getDepartment() != null) {
            wrapper.eq(Asset::getDepartment, queryDTO.getDepartment());
        }
        if (queryDTO.getLocationType() != null) {
            wrapper.eq(Asset::getLocationType, queryDTO.getLocationType());
        }
        
        wrapper.orderByDesc(Asset::getCreateTime);
        Page<Asset> result = assetMapper.selectPage(page, wrapper);
        
        PageResult<AssetDTO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPageNum((int) result.getCurrent());
        pageResult.setPageSize((int) result.getSize());
        pageResult.setList(result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        
        return Result.success(pageResult);
    }

    @Override
    public Result<AssetDTO> getAssetById(Long id) {
        Asset asset = assetMapper.selectById(id);
        if (asset == null) {
            return Result.error("资产不存在");
        }
        return Result.success(convertToDTO(asset));
    }

    @Override
    public Result<AssetDTO> getAssetByCode(String assetCode) {
        Asset asset = assetMapper.findByAssetCode(assetCode);
        if (asset == null) {
            return Result.error("资产不存在");
        }
        return Result.success(convertToDTO(asset));
    }

    @Override
    @Transactional
    public Result<Void> createAsset(AssetDTO assetDTO) {
        Asset asset = new Asset();
        BeanUtils.copyProperties(assetDTO, asset);
        asset.setStatus(AssetStatus.IN_STOCK.getCode());
        assetMapper.insert(asset);
        
        // 记录入库流水
        AssetTransaction transaction = new AssetTransaction();
        transaction.setAssetId(asset.getId());
        transaction.setOperationType(OperationType.INBOUND.getCode());
        transaction.setOperationDate(LocalDate.now());
        transaction.setToLocation(asset.getLocationDetail());
        transaction.setDescription("资产入库");
        transactionMapper.insert(transaction);
        
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> updateAsset(AssetDTO assetDTO) {
        if (assetDTO.getId() == null) {
            return Result.error("资产ID不能为空");
        }
        Asset asset = new Asset();
        BeanUtils.copyProperties(assetDTO, asset);
        assetMapper.updateById(asset);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> deleteAsset(Long id) {
        assetMapper.deleteById(id);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> assignAsset(AssignAssetDTO assignDTO) {
        Asset asset = assetMapper.selectById(assignDTO.getAssetId());
        if (asset == null) {
            return Result.error("资产不存在");
        }
        if (!AssetStatus.IN_STOCK.getCode().equals(asset.getStatus())) {
            return Result.error("资产不在库存状态，无法领用");
        }
        
        // 更新资产状态
        asset.setStatus(AssetStatus.IN_USE.getCode());
        asset.setCurrentUserId(assignDTO.getToUserId());
        asset.setCurrentUserName(assignDTO.getToUserName());
        asset.setDepartment(assignDTO.getToDepartment());
        asset.setAssignDate(LocalDate.now());
        asset.setExpectedReturnDate(assignDTO.getExpectedReturnDate());
        assetMapper.updateById(asset);
        
        // 记录领用流水
        AssetTransaction transaction = new AssetTransaction();
        transaction.setAssetId(asset.getId());
        transaction.setOperationType(OperationType.ASSIGN.getCode());
        transaction.setOperationDate(LocalDate.now());
        transaction.setToUserId(assignDTO.getToUserId());
        transaction.setToUserName(assignDTO.getToUserName());
        transaction.setDescription(assignDTO.getDescription());
        transactionMapper.insert(transaction);
        
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> returnAsset(Long assetId, String description) {
        Asset asset = assetMapper.selectById(assetId);
        if (asset == null) {
            return Result.error("资产不存在");
        }
        
        Long fromUserId = asset.getCurrentUserId();
        String fromUserName = asset.getCurrentUserName();
        
        // 更新资产状态
        asset.setStatus(AssetStatus.IN_STOCK.getCode());
        asset.setCurrentUserId(null);
        asset.setCurrentUserName(null);
        asset.setDepartment(null);
        asset.setAssignDate(null);
        asset.setExpectedReturnDate(null);
        assetMapper.updateById(asset);
        
        // 记录归还流水
        AssetTransaction transaction = new AssetTransaction();
        transaction.setAssetId(asset.getId());
        transaction.setOperationType(OperationType.RETURN.getCode());
        transaction.setOperationDate(LocalDate.now());
        transaction.setFromUserId(fromUserId);
        transaction.setFromUserName(fromUserName);
        transaction.setDescription(description);
        transactionMapper.insert(transaction);
        
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> transferAsset(AssignAssetDTO transferDTO) {
        Asset asset = assetMapper.selectById(transferDTO.getAssetId());
        if (asset == null) {
            return Result.error("资产不存在");
        }
        
        Long fromUserId = asset.getCurrentUserId();
        String fromUserName = asset.getCurrentUserName();
        
        // 更新资产状态
        asset.setCurrentUserId(transferDTO.getToUserId());
        asset.setCurrentUserName(transferDTO.getToUserName());
        asset.setDepartment(transferDTO.getToDepartment());
        assetMapper.updateById(asset);
        
        // 记录调拨流水
        AssetTransaction transaction = new AssetTransaction();
        transaction.setAssetId(asset.getId());
        transaction.setOperationType(OperationType.TRANSFER.getCode());
        transaction.setOperationDate(LocalDate.now());
        transaction.setFromUserId(fromUserId);
        transaction.setFromUserName(fromUserName);
        transaction.setToUserId(transferDTO.getToUserId());
        transaction.setToUserName(transferDTO.getToUserName());
        transaction.setDescription(transferDTO.getDescription());
        transactionMapper.insert(transaction);
        
        return Result.success();
    }

    private AssetDTO convertToDTO(Asset asset) {
        AssetDTO dto = new AssetDTO();
        BeanUtils.copyProperties(asset, dto);
        return dto;
    }
}