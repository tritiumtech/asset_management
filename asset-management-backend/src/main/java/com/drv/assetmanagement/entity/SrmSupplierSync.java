package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * SRM供应商同步中间表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("srm_supplier_sync")
public class SrmSupplierSync {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String srmSupplierCode;
    private String srmSupplierName;
    private String supplierType;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String businessLicense;
    private String bankAccount;
    private String status;

    // 同步控制字段
    private String syncStatus;
    private LocalDateTime syncTime;
    private String syncError;
    private Long localSupplierId;

    // 数据变更追踪
    private LocalDateTime srmCreateTime;
    private LocalDateTime srmUpdateTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}