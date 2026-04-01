package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {

    @Select("SELECT * FROM supplier WHERE supplier_type = #{type} AND status = '合作中'")
    List<Supplier> findActiveByType(@Param("type") String type);

    @Select("SELECT * FROM supplier WHERE supplier_code = #{code}")
    Supplier findByCode(@Param("code") String code);
}