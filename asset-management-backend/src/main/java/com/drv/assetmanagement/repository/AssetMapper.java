package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.Asset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AssetMapper extends BaseMapper<Asset> {

    @Select("SELECT * FROM asset WHERE asset_code = #{assetCode} AND deleted = 0")
    Asset findByAssetCode(@Param("assetCode") String assetCode);

    @Select("SELECT * FROM asset WHERE current_user_id = #{userId} AND deleted = 0")
    List<Asset> findByCurrentUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM asset WHERE status = #{status} AND deleted = 0")
    Long countByStatus(@Param("status") String status);
}