package com.drv.assetmanagement.enums;

import lombok.Getter;

@Getter
public enum AssetCategory {
    PC("PC", "台式主机"),
    NB("NB", "笔记本"),
    IP("IP", "iPad/平板"),
    DS("DS", "显示器"),
    PJ("PJ", "投影仪"),
    PE("PE", "外设"),
    NT("NT", "网络设备"),
    PO("PO", "收银设备"),
    MAT("MAT", "消耗物料");

    private final String code;
    private final String name;

    AssetCategory(String code, String name) {
        this.code = code;
        this.name = name;
    }
}