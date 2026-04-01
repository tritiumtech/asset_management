package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.PermissionAudit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionAuditMapper extends BaseMapper<PermissionAudit> {

    @Select("SELECT * FROM permission_audit WHERE target_user_id = #{userId} ORDER BY create_time DESC")
    List<PermissionAudit> findByTargetUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM permission_audit WHERE operator_id = #{operatorId} ORDER BY create_time DESC LIMIT 100")
    List<PermissionAudit> findByOperatorId(@Param("operatorId") Long operatorId);
}