package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.InventoryVerification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InventoryVerificationMapper extends BaseMapper<InventoryVerification> {

    @Select("SELECT * FROM inventory_verification WHERE inventory_item_id = #{itemId} ORDER BY round ASC, create_time ASC")
    List<InventoryVerification> findByItemId(@Param("itemId") Long itemId);

    @Select("SELECT * FROM inventory_verification WHERE task_id = #{taskId} ORDER BY create_time DESC")
    List<InventoryVerification> findByTaskId(@Param("taskId") Long taskId);

    @Select("SELECT MAX(round) FROM inventory_verification WHERE inventory_item_id = #{itemId}")
    Integer findMaxRoundByItemId(@Param("itemId") Long itemId);
}