package com.drv.assetmanagement.enums;

import lombok.Getter;

@Getter
public enum AssetStatus {
    IN_STOCK("IN_STOCK", "库存"),
    IN_USE("IN_USE", "在用"),
    IN_TRANSFER("IN_TRANSFER", "调拨中"),
    IN_REPAIR("IN_REPAIR", "维修中"),
    SCRAPPED("SCRAPPED", "报废"),
    MISSING("MISSING", "盘亏");

    private final String code;
    private final String name;

    AssetStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }
}