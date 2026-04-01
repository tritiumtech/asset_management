package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.HrDepartmentSync;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HrDepartmentSyncMapper extends BaseMapper<HrDepartmentSync> {

    @Select("SELECT * FROM hr_department_sync WHERE sync_status = 'PENDING'")
    List<HrDepartmentSync> findPendingSync();

    @Select("SELECT * FROM hr_department_sync WHERE hr_dept_code = #{code}")
    HrDepartmentSync findByHrDeptCode(@Param("code") String code);
}