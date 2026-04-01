package com.drv.assetmanagement.service;

import com.drv.assetmanagement.dto.LoginRequestDTO;
import com.drv.assetmanagement.dto.LoginResponseDTO;
import com.drv.assetmanagement.dto.Result;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    Result<LoginResponseDTO> login(LoginRequestDTO request);
    
    /**
     * 用户登出
     */
    Result<Void> logout(String token);
    
    /**
     * 刷新token
     */
    Result<LoginResponseDTO> refreshToken(String refreshToken);
}
