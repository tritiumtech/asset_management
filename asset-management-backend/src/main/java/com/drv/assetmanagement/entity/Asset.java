package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("asset")
public class Asset {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资产编号 - 唯一标识，条码/二维码内容
     */
    private String assetCode;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 资产类别
     */
    private String category;

    /**
     * 二级分类
     */
    private String subCategory;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 型号
     */
    private String model;

    /**
     * SN码 - 设备唯一序列号
     */
    private String serialNumber;

    /**
     * 购置日期
     */
    private LocalDate purchaseDate;

    /**
     * 购置金额
     */
    private BigDecimal purchaseAmount;

    /**
     * 保修期至
     */
    private LocalDate warrantyDate;

    /**
     * 供应商
     */
    private String supplier;

    /**
     * 存放位置类型 - 总部/门店/仓库
     */
    private String locationType;

    /**
     * 存放位置详情
     */
    private String locationDetail;

    /**
     * 使用状态 - 库存/在用/调拨中/维修中/报废/盘亏
     */
    private String status;

    /**
     * 当前责任人ID
     */
    private Long currentUserId;

    /**
     * 当前责任人姓名
     */
    private String currentUserName;

    /**
     * 责任部门
     */
    private String department;

    /**
     * 员工类型 - 总部员工/门店员工
     */
    private String employeeType;

    /**
     * 领用日期
     */
    private LocalDate assignDate;

    /**
     * 预计归还日期
     */
    private LocalDate expectedReturnDate;

    /**
     * 资产照片URL
     */
    private String assetPhotoUrl;

    /**
     * 购置凭证URL
     */
     private String purchaseDocUrl;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 关联的流转记录
     */
    @TableField(exist = false)
    private List<AssetTransaction> transactions;
}