package com.drv.assetmanagement.enums;

import lombok.Getter;

@Getter
public enum OperationType {
    INBOUND("INBOUND", "入库"),
    ASSIGN("ASSIGN", "领用"),
    RETURN("RETURN", "归还"),
    TRANSFER("TRANSFER", "调拨"),
    REPAIR("REPAIR", "维修"),
    SCRAP("SCRAP", "报废"),
    INVENTORY_ADJUST("INVENTORY_ADJUST", "盘点调整");

    private final String code;
    private final String name;

    OperationType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}