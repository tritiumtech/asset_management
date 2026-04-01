package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_item")
public class InventoryItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 盘点任务ID
     */
    private Long taskId;

    /**
     * 关联资产ID
     */
    private Long assetId;

    /**
     * 资产编号
     */
    private String assetCode;

    /**
     * 计划盘点日期
     */
    private LocalDate plannedDate;

    /**
     * 实际盘点日期
     */
    private LocalDate actualDate;

    /**
     * 盘点人ID
     */
    private Long checkerId;

    /**
     * 盘点人姓名
     */
    private String checkerName;

    /**
     * 盘点结果 - 正常/位置变更/责任人变更/盘亏/盘盈
     */
    private String result;

    /**
     * 盘点时位置
     */
    private String checkedLocation;

    /**
     * 盘点时责任人ID
     */
    private Long checkedUserId;

    /**
     * 盘点时责任人姓名
     */
    private String checkedUserName;

    /**
     * 当前状态 - PENDING/SUBMITTED/UNDER_REVIEW/RESOLVED/CLOSED
     */
    private String currentStatus;

    /**
     * 当前轮次
     */
    private Integer currentRound;

    /**
     * 是否已结案
     */
    private Boolean isClosed;

    /**
     * 最终处理结果
     */
    private String finalResult;

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
}