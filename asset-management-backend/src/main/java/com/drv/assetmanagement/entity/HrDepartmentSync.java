package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 北森HR部门同步中间表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("hr_department_sync")
public class HrDepartmentSync {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String hrDeptCode;
    private String hrDeptName;
    private String parentCode;
    private Integer deptLevel;
    private String deptType;
    private String status;

    // 同步控制字段
    private String syncStatus;
    private LocalDateTime syncTime;
    private Long localDeptId;

    // 数据变更追踪
    private LocalDateTime hrCreateTime;
    private LocalDateTime hrUpdateTime;
    private LocalDateTime createTime;
}