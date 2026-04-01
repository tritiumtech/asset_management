package com.drv.assetmanagement.enums;

import lombok.Getter;

@Getter
public enum InventoryStatus {
    PLANNED("PLANNED", "计划中"),
    IN_PROGRESS("IN_PROGRESS", "进行中"),
    FIRST_VERIFY("FIRST_VERIFY", "第一轮核实"),
    SECOND_VERIFY("SECOND_VERIFY", "第二轮确认"),
    COMPLETED("COMPLETED", "已完成");

    private final String code;
    private final String name;

    InventoryStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }
}