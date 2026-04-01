package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 盘点核实记录表 - 支持多轮沟通
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_verification")
public class InventoryVerification {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联盘点明细ID
     */
    private Long inventoryItemId;

    /**
     * 关联盘点任务ID
     */
    private Long taskId;

    /**
     * 第几轮沟通
     */
    private Integer round;

    /**
     * 核实人ID
     */
    private Long verifierId;

    /**
     * 核实人姓名
     */
    private String verifierName;

    /**
     * 核实人类型 - MANAGER/USER/SYSTEM
     */
    private String verifierType;

    /**
     * 操作类型 - SUBMIT/REVIEW/RETURN/CLOSE
     */
    private String action;

    /**
     * 结果 - PASS/REJECT/NEED_INFO/MODIFIED
     */
    private String result;

    /**
     * 核实意见
     */
    private String comment;

    /**
     * 附件列表（JSON格式）
     */
    private String attachments;

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