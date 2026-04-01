package com.drv.assetmanagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<T> list;
}