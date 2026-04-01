package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.AssetTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AssetTransactionMapper extends BaseMapper<AssetTransaction> {

    @Select("SELECT * FROM asset_transaction WHERE asset_id = #{assetId} AND deleted = 0 ORDER BY create_time DESC")
    List<AssetTransaction> findByAssetId(@Param("assetId") Long assetId);
}