package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.WorkflowApproval;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkflowApprovalMapper extends BaseMapper<WorkflowApproval> {

    @Select("SELECT * FROM workflow_approval WHERE workflow_id = #{workflowId} ORDER BY round ASC, create_time ASC")
    List<WorkflowApproval> findByWorkflowId(@Param("workflowId") Long workflowId);

    @Select("SELECT * FROM workflow_approval WHERE workflow_id = #{workflowId} AND round = #{round} ORDER BY create_time DESC")
    List<WorkflowApproval> findByWorkflowIdAndRound(@Param("workflowId") Long workflowId, @Param("round") Integer round);
}