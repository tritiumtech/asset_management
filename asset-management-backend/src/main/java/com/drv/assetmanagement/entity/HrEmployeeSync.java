package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 北森HR员工同步中间表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("hr_employee_sync")
public class HrEmployeeSync {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String hrEmployeeId;
    private String hrEmployeeNo;
    private String name;
    private String email;
    private String phone;
    private String departmentCode;
    private String departmentName;
    private String storeCode;
    private String storeName;
    private String position;
    private String employeeType;
    private String status;
    private LocalDate joinDate;
    private LocalDate leaveDate;
    private String managerId;

    // 同步控制字段
    private String syncStatus;
    private LocalDateTime syncTime;
    private String syncError;
    private Long localUserId;

    // 数据变更追踪
    private LocalDateTime hrCreateTime;
    private LocalDateTime hrUpdateTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}