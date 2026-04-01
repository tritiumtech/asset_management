package com.drv.assetmanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 登录响应DTO
 */
@Data
@Builder
public class LoginResponseDTO {
    
    private String token;
    private String tokenType;
    private Long expiresIn;
    
    private Long userId;
    private String username;
    private String realName;
    private String roleCode;
    private String roleName;
    private String userType;
    private List<String> permissions;
}
