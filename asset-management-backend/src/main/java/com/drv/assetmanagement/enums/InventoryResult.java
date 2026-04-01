package com.drv.assetmanagement.enums;

import lombok.Getter;

@Getter
public enum InventoryResult {
    NORMAL("NORMAL", "正常"),
    LOCATION_CHANGE("LOCATION_CHANGE", "位置变更"),
    USER_CHANGE("USER_CHANGE", "责任人变更"),
    MISSING("MISSING", "盘亏"),
    SURPLUS("SURPLUS", "盘盈");

    private final String code;
    private final String name;

    InventoryResult(String code, String name) {
        this.code = code;
        this.name = name;
    }
}