package com.drv.assetmanagement.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventoryItemDTO {
    private Long id;
    private Long taskId;
    private Long assetId;
    private String assetCode;
    private String assetName;
    private String category;
    private LocalDate plannedDate;
    private LocalDate actualDate;
    private Long checkerId;
    private String checkerName;
    private String result;
    private String resultName;
    private String checkedLocation;
    private Long checkedUserId;
    private String checkedUserName;
    private String firstVerifyStatus;
    private String firstVerifyNote;
    private String firstVerifyBy;
    private LocalDateTime firstVerifyTime;
    private String secondVerifyStatus;
    private String secondVerifyNote;
    private String secondVerifyBy;
    private LocalDateTime secondVerifyTime;
    private String finalResult;
}