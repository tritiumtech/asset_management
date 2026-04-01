package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 流程实例表 - 支持OA集成
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("workflow_instance")
public class WorkflowInstance {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务类型 - INVENTORY/ASSIGN/TRANSFER/REPAIR/SCRAP
     */
    private String bizType;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 业务编号（展示用）
     */
    private String bizCode;

    /**
     * 流程状态
     */
    private String status;

    /**
     * 来源 - OA/INTERNAL/SUPPLIER
     */
    private String source;

    /**
     * OA回调地址
     */
    private String callbackUrl;

    /**
     * 回调签名密钥
     */
    private String callbackToken;

    /**
     * 当前轮次
     */
    private Integer currentRound;

    /**
     * 最大轮次限制
     */
    private Integer maxRounds;

    /**
     * 是否已结案
     */
    private Boolean isClosed;

    /**
     * 结案时间
     */
    private LocalDateTime closedTime;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建人姓名
     */
    private String createdByName;

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
}