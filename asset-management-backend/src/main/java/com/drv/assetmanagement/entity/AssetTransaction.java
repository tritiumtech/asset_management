package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("asset_transaction")
public class AssetTransaction {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联资产ID
     */
    private Long assetId;

    /**
     * 操作类型 - 入库/领用/归还/调拨/维修/报废/盘点调整
     */
    private String operationType;

    /**
     * 操作日期
     */
    private LocalDate operationDate;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 原责任人ID
     */
    private Long fromUserId;

    /**
     * 原责任人姓名
     */
    private String fromUserName;

    /**
     * 目标责任人ID
     */
    private Long toUserId;

    /**
     * 目标责任人姓名
     */
    private String toUserName;

    /**
     * 原位置
     */
    private String fromLocation;

    /**
     * 目标位置
     */
    private String toLocation;

    /**
     * 审批单号
     */
    private String approvalNo;

    /**
     * 操作说明
     */
    private String description;

    /**
     * 附件URL
     */
    private String attachmentUrl;

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
}