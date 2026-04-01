package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.HrEmployeeSync;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface HrEmployeeSyncMapper extends BaseMapper<HrEmployeeSync> {

    @Select("SELECT * FROM hr_employee_sync WHERE sync_status = 'PENDING' LIMIT 100")
    List<HrEmployeeSync> findPendingSync();

    @Select("SELECT * FROM hr_employee_sync WHERE hr_employee_id = #{employeeId}")
    HrEmployeeSync findByHrEmployeeId(@Param("employeeId") String employeeId);

    @Update("UPDATE hr_employee_sync SET sync_status = #{status}, sync_time = NOW(), local_user_id = #{localId} WHERE id = #{id}")
    int updateSyncStatus(@Param("id") Long id, @Param("status") String status, @Param("localId") Long localId);
}