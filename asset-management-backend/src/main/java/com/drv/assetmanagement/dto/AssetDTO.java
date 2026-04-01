package com.drv.assetmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssetDTO {
    private Long id;
    private String assetCode;
    private String assetName;
    private String category;
    private String categoryName;
    private String subCategory;
    private String brand;
    private String model;
    private String serialNumber;
    private LocalDate purchaseDate;
    private BigDecimal purchaseAmount;
    private LocalDate warrantyDate;
    private String supplier;
    private String locationType;
    private String locationDetail;
    private String status;
    private String statusName;
    private Long currentUserId;
    private String currentUserName;
    private String department;
    private String employeeType;
    private LocalDate assignDate;
    private LocalDate expectedReturnDate;
    private String assetPhotoUrl;
    private String purchaseDocUrl;
    private String remark;
}