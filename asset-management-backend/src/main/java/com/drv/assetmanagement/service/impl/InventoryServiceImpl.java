package com.drv.assetmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drv.assetmanagement.dto.*;
import com.drv.assetmanagement.entity.*;
import com.drv.assetmanagement.enums.AssetStatus;
import com.drv.assetmanagement.enums.InventoryResult;
import com.drv.assetmanagement.enums.InventoryStatus;
import com.drv.assetmanagement.repository.*;
import com.drv.assetmanagement.service.InventoryService;
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
public class InventoryServiceImpl implements InventoryService {

    private final InventoryTaskMapper taskMapper;
    private final InventoryItemMapper itemMapper;
    private final AssetMapper assetMapper;

    @Override
    public Result<PageResult<InventoryTaskDTO>> listTasks(Integer pageNum, Integer pageSize) {
        Page<InventoryTask> page = new Page<>(pageNum, pageSize);
        Page<InventoryTask> result = taskMapper.selectPage(page, new LambdaQueryWrapper<>()
                .orderByDesc(InventoryTask::getCreateTime));
        
        PageResult<InventoryTaskDTO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPageNum((int) result.getCurrent());
        pageResult.setPageSize((int) result.getSize());
        pageResult.setList(result.getRecords().stream()
                .map(this::convertTaskToDTO)
                .collect(Collectors.toList()));
        
        return Result.success(pageResult);
    }

    @Override
    public Result<InventoryTaskDTO> getTaskById(Long id) {
        InventoryTask task = taskMapper.selectById(id);
        if (task == null) {
            return Result.error("盘点任务不存在");
        }
        return Result.success(convertTaskToDTO(task));
    }

    @Override
    @Transactional
    public Result<Void> createTask(InventoryTaskDTO taskDTO) {
        InventoryTask task = new InventoryTask();
        BeanUtils.copyProperties(taskDTO, task);
        task.setStatus(InventoryStatus.PLANNED.getCode());
        
        // 根据盘点范围统计应盘数量
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Asset::getDeleted, 0);
        if (!"全部".equals(task.getScope())) {
            wrapper.eq(Asset::getDepartment, task.getScopeDetail());
        }
        Long count = assetMapper.selectCount(wrapper);
        task.setTotalCount(count.intValue());
        
        taskMapper.insert(task);
        
        // 生成盘点明细
        List<Asset> assets = assetMapper.selectList(wrapper);
        for (Asset asset : assets) {
            InventoryItem item = new InventoryItem();
            item.setTaskId(task.getId());
            item.setAssetId(asset.getId());
            item.setAssetCode(asset.getAssetCode());
            item.setPlannedDate(task.getPlannedStartDate());
            itemMapper.insert(item);
        }
        
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> startTask(Long taskId) {
        InventoryTask task = taskMapper.selectById(taskId);
        if (task == null) {
            return Result.error("盘点任务不存在");
        }
        task.setStatus(InventoryStatus.IN_PROGRESS.getCode());
        task.setActualStartDate(LocalDate.now());
        taskMapper.updateById(task);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> completeTask(Long taskId) {
        InventoryTask task = taskMapper.selectById(taskId);
        if (task == null) {
            return Result.error("盘点任务不存在");
        }
        task.setStatus(InventoryStatus.COMPLETED.getCode());
        task.setActualEndDate(LocalDate.now());
        taskMapper.updateById(task);
        return Result.success();
    }

    @Override
    public Result<PageResult<InventoryItemDTO>> listItems(Long taskId, Integer pageNum, Integer pageSize) {
        Page<InventoryItem> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InventoryItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryItem::getTaskId, taskId);
        Page<InventoryItem> result = itemMapper.selectPage(page, wrapper);
        
        PageResult<InventoryItemDTO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPageNum((int) result.getCurrent());
        pageResult.setPageSize((int) result.getSize());
        pageResult.setList(result.getRecords().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList()));
        
        return Result.success(pageResult);
    }

    @Override
    @Transactional
    public Result<InventoryItemDTO> scanAsset(ScanInventoryDTO scanDTO) {
        // 查找盘点明细
        LambdaQueryWrapper<InventoryItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryItem::getTaskId, scanDTO.getTaskId())
               .eq(InventoryItem::getAssetId, scanDTO.getAssetId());
        InventoryItem item = itemMapper.selectOne(wrapper);
        
        // 如果不在盘点清单中，记录为盘盈
        if (item == null) {
            item = new InventoryItem();
            item.setTaskId(scanDTO.getTaskId());
            item.setAssetId(scanDTO.getAssetId());
            item.setAssetCode(scanDTO.getAssetCode());
            item.setResult(InventoryResult.SURPLUS.getCode());
        } else {
            // 判断盘点结果
            Asset asset = assetMapper.selectById(scanDTO.getAssetId());
            String result = determineInventoryResult(asset, scanDTO);
            item.setResult(result);
        }
        
        item.setActualDate(LocalDate.now());
        item.setCheckedLocation(scanDTO.getCheckedLocation());
        item.setCheckedUserId(scanDTO.getCheckedUserId());
        item.setCheckedUserName(scanDTO.getCheckedUserName());
        item.setRemark(scanDTO.getRemark());
        
        if (item.getId() == null) {
            itemMapper.insert(item);
        } else {
            itemMapper.updateById(item);
        }
        
        // 更新任务统计
        updateTaskStatistics(scanDTO.getTaskId());
        
        return Result.success(convertItemToDTO(item));
    }

    @Override
    @Transactional
    public Result<Void> verifyItem(VerifyInventoryDTO verifyDTO) {
        InventoryItem item = itemMapper.selectById(verifyDTO.getItemId());
        if (item == null) {
            return Result.error("盘点记录不存在");
        }
        
        if ("FIRST".equals(verifyDTO.getVerifyType())) {
            item.setFirstVerifyStatus(verifyDTO.getStatus());
            item.setFirstVerifyNote(verifyDTO.getNote());
            item.setFirstVerifyBy("当前用户"); // TODO: 从SecurityContext获取
            item.setFirstVerifyTime(LocalDateTime.now());
        } else if ("SECOND".equals(verifyDTO.getVerifyType())) {
            item.setSecondVerifyStatus(verifyDTO.getStatus());
            item.setSecondVerifyNote(verifyDTO.getNote());
            item.setSecondVerifyBy("当前用户"); // TODO: 从SecurityContext获取
            item.setSecondVerifyTime(LocalDateTime.now());
        }
        
        itemMapper.updateById(item);
        return Result.success();
    }

    @Override
    public Result<InventoryStatisticsDTO> getTaskStatistics(Long taskId) {
        InventoryTask task = taskMapper.selectById(taskId);
        if (task == null) {
            return Result.error("盘点任务不存在");
        }
        
        InventoryStatisticsDTO stats = new InventoryStatisticsDTO();
        stats.setTaskId(taskId);
        stats.setTotalCount(task.getTotalCount());
        stats.setCheckedCount(task.getCheckedCount());
        stats.setUncheckedCount(task.getTotalCount() - task.getCheckedCount());
        stats.setNormalCount(task.getNormalCount());
        stats.setMissingCount(task.getMissingCount());
        stats.setSurplusCount(task.getSurplusCount());
        stats.setLocationChangeCount(task.getLocationChangeCount());
        stats.setUserChangeCount(task.getUserChangeCount());
        
        if (task.getTotalCount() > 0) {
            stats.setProgressPercentage((double) task.getCheckedCount() / task.getTotalCount() * 100);
        } else {
            stats.setProgressPercentage(0.0);
        }
        
        return Result.success(stats);
    }

    private String determineInventoryResult(Asset asset, ScanInventoryDTO scanDTO) {
        // 默认正常
        if (!asset.getLocationDetail().equals(scanDTO.getCheckedLocation())) {
            return InventoryResult.LOCATION_CHANGE.getCode();
        }
        if (asset.getCurrentUserId() != null && !asset.getCurrentUserId().equals(scanDTO.getCheckedUserId())) {
            return InventoryResult.USER_CHANGE.getCode();
        }
        return InventoryResult.NORMAL.getCode();
    }

    private void updateTaskStatistics(Long taskId) {
        InventoryTask task = taskMapper.selectById(taskId);
        if (task == null) return;
        
        // 统计各状态数量
        Long checkedCount = itemMapper.selectCount(
            new LambdaQueryWrapper<InventoryItem>()
                .eq(InventoryItem::getTaskId, taskId)
                .isNotNull(InventoryItem::getResult)
        );
        
        Long normalCount = itemMapper.countByTaskIdAndResult(taskId, InventoryResult.NORMAL.getCode());
        Long missingCount = itemMapper.countByTaskIdAndResult(taskId, InventoryResult.MISSING.getCode());
        Long surplusCount = itemMapper.countByTaskIdAndResult(taskId, InventoryResult.SURPLUS.getCode());
        Long locationChangeCount = itemMapper.countByTaskIdAndResult(taskId, InventoryResult.LOCATION_CHANGE.getCode());
        Long userChangeCount = itemMapper.countByTaskIdAndResult(taskId, InventoryResult.USER_CHANGE.getCode());
        
        task.setCheckedCount(checkedCount.intValue());
        task.setNormalCount(normalCount.intValue());
        task.setMissingCount(missingCount.intValue());
        task.setSurplusCount(surplusCount.intValue());
        task.setLocationChangeCount(locationChangeCount.intValue());
        task.setUserChangeCount(userChangeCount.intValue());
        
        taskMapper.updateById(task);
    }

    private InventoryTaskDTO convertTaskToDTO(InventoryTask task) {
        InventoryTaskDTO dto = new InventoryTaskDTO();
        BeanUtils.copyProperties(task, dto);
        return dto;
    }

    private InventoryItemDTO convertItemToDTO(InventoryItem item) {
        InventoryItemDTO dto = new InventoryItemDTO();
        BeanUtils.copyProperties(item, dto);
        
        // 查询资产名称
        Asset asset = assetMapper.selectById(item.getAssetId());
        if (asset != null) {
            dto.setAssetName(asset.getAssetName());
            dto.setCategory(asset.getCategory());
        }
        
        return dto;
    }
}