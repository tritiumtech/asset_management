package com.drv.assetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 员工编号
     */
    private String employeeNo;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 部门
     */
    private String department;

    /**
     * 员工类型 - 总部员工/门店员工
     */
    private String employeeType;

    /**
     * 门店/区域（如果是门店员工）
     */
    private String storeName;

    /**
     * 用户类型 - SUPPLIER/EMPLOYEE/MANAGER（新增）
     */
    private String userType;

    /**
     * 角色编码（新增）
     */
    private String roleCode;

    /**
     * 子角色 - SELLER/RENTER/MAINTAINER/TRANSFER/INVENTORY（新增）
     */
    private String subRole;

    /**
     * 数据范围 - ALL/DEPT/STORE/SELF/ASSIGNED（新增）
     */
    private String dataScope;

    /**
     * 关联供应商ID（外部用户）（新增）
     */
    private Long supplierId;

    /**
     * 管理的部门列表（JSON）（新增）
     */
    private String managedDepts;

    /**
     * 管理的门店列表（JSON）（新增）
     */
    private String managedStores;

    /**
     * 是否测试账户（新增）
     */
    private Integer isTestAccount;

    /**
     * 角色（兼容旧版）
     */
    private String role;

    /**
     * 状态 - 在职/离职
     */
    private String status;

    /**
     * 入职日期
     */
    private LocalDate joinDate;

    /**
     * 离职日期
     */
    private LocalDate leaveDate;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

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