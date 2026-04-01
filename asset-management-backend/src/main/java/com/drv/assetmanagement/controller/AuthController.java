package com.drv.assetmanagement.controller;

import com.drv.assetmanagement.dto.LoginRequestDTO;
import com.drv.assetmanagement.dto.LoginResponseDTO;
import com.drv.assetmanagement.dto.Result;
import com.drv.assetmanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return authService.logout(token);
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public Result<LoginResponseDTO> refreshToken(@RequestParam String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
