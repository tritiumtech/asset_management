package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.InventoryItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface InventoryItemMapper extends BaseMapper<InventoryItem> {

    @Select("SELECT * FROM inventory_item WHERE task_id = #{taskId}")
    List<InventoryItem> findByTaskId(@Param("taskId") Long taskId);

    @Select("SELECT * FROM inventory_item WHERE task_id = #{taskId} AND result IS NULL")
    List<InventoryItem> findUncheckedByTaskId(@Param("taskId") Long taskId);

    @Select("SELECT COUNT(*) FROM inventory_item WHERE task_id = #{taskId} AND result = #{result}")
    Long countByTaskIdAndResult(@Param("taskId") Long taskId, @Param("result") String result);

    @Update("UPDATE inventory_item SET checked_count = checked_count + 1 WHERE task_id = #{taskId}")
    void incrementCheckedCount(@Param("taskId") Long taskId);
}