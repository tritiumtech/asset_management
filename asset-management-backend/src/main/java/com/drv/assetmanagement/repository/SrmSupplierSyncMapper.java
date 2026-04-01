package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.SrmSupplierSync;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SrmSupplierSyncMapper extends BaseMapper<SrmSupplierSync> {

    @Select("SELECT * FROM srm_supplier_sync WHERE sync_status = 'PENDING' LIMIT 100")
    List<SrmSupplierSync> findPendingSync();

    @Select("SELECT * FROM srm_supplier_sync WHERE srm_supplier_code = #{code}")
    SrmSupplierSync findBySrmCode(@Param("code") String code);

    @Update("UPDATE srm_supplier_sync SET sync_status = #{status}, sync_time = NOW(), local_supplier_id = #{localId} WHERE id = #{id}")
    int updateSyncStatus(@Param("id") Long id, @Param("status") String status, @Param("localId") Long localId);
}