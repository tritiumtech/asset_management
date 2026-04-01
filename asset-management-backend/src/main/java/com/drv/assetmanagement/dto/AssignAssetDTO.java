package com.drv.assetmanagement.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignAssetDTO {
    private Long assetId;
    private Long toUserId;
    private String toUserName;
    private String toDepartment;
    private String description;
    private LocalDate expectedReturnDate;
}