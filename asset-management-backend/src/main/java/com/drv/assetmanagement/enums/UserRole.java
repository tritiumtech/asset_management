package com.drv.assetmanagement.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN", "系统管理员"),
    ASSET_MANAGER("ASSET_MANAGER", "资产管理员"),
    FINANCE("FINANCE", "财务专员"),
    REGION_MANAGER("REGION_MANAGER", "区域经理"),
    DEPT_MANAGER("DEPT_MANAGER", "部门负责人"),
    STORE_MANAGER("STORE_MANAGER", "店长"),
    USER("USER", "普通员工");

    private final String code;
    private final String name;

    UserRole(String code, String name) {
        this.code = code;
        this.name = name;
    }
}