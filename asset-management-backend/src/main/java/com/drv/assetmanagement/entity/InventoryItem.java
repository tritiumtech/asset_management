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
     * 第一轮核实状态 - 待核实/已核实/待确认
     */
    private String firstVerifyStatus;

    /**
     * 第一轮核实说明
     */
    private String firstVerifyNote;

    /**
     * 第一轮核实人
     */
    private String firstVerifyBy;

    /**
     * 第一轮核实时间
     */
    private LocalDateTime firstVerifyTime;

    /**
     * 第二轮确认状态 - 待确认/已确认
     */
    private String secondVerifyStatus;

    /**
     * 第二轮确认说明
     */
    private String secondVerifyNote;

    /**
     * 第二轮确认人
     */
    private String secondVerifyBy;

    /**
     * 第二轮确认时间
     */
    private LocalDateTime secondVerifyTime;

    /**
     * 最终处理结果 - 无差异/已调整/报损/补录
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