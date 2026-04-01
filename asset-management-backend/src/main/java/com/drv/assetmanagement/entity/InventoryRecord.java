package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_task")
public class InventoryTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 盘点批次号 - 如"2026H1"
     */
    private String batchNo;

    /**
     * 盘点名称
     */
    private String taskName;

    /**
     * 盘点范围 - 全部/指定部门/指定门店
     */
    private String scope;

    /**
     * 盘点范围详情
     */
    private String scopeDetail;

    /**
     * 计划开始日期
     */
    private LocalDate plannedStartDate;

    /**
     * 计划结束日期
     */
    private LocalDate plannedEndDate;

    /**
     * 实际开始日期
     */
    private LocalDate actualStartDate;

    /**
     * 实际结束日期
     */
    private LocalDate actualEndDate;

    /**
     * 盘点状态 - 计划中/进行中/第一轮核实/第二轮确认/已完成
     */
    private String status;

    /**
     * 应盘数量
     */
    private Integer totalCount;

    /**
     * 实盘数量
     */
    private Integer checkedCount;

    /**
     * 正常数量
     */
    private Integer normalCount;

    /**
     * 盘亏数量
     */
    private Integer missingCount;

    /**
     * 盘盈数量
     */
    private Integer surplusCount;

    /**
     * 位置变更数量
     */
    private Integer locationChangeCount;

    /**
     * 责任人变更数量
     */
    private Integer userChangeCount;

    /**
     * 负责人ID
     */
    private Long managerId;

    /**
     * 负责人姓名
     */
    private String managerName;

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
}