package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.WorkflowInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface WorkflowInstanceMapper extends BaseMapper<WorkflowInstance> {

    @Select("SELECT * FROM workflow_instance WHERE biz_type = #{bizType} AND biz_id = #{bizId} AND is_closed = 0 LIMIT 1")
    WorkflowInstance findActiveByBiz(@Param("bizType") String bizType, @Param("bizId") Long bizId);

    @Update("UPDATE workflow_instance SET current_round = current_round + 1, update_time = NOW() WHERE id = #{id}")
    int incrementRound(@Param("id") Long id);

    @Update("UPDATE workflow_instance SET is_closed = 1, closed_time = NOW(), update_time = NOW() WHERE id = #{id}")
    int closeWorkflow(@Param("id") Long id);
}