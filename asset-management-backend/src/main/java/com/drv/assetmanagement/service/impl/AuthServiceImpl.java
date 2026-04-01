package com.drv.assetmanagement.service.impl;

import com.drv.assetmanagement.dto.LoginRequestDTO;
import com.drv.assetmanagement.dto.LoginResponseDTO;
import com.drv.assetmanagement.dto.Result;
import com.drv.assetmanagement.entity.User;
import com.drv.assetmanagement.repository.UserMapper;
import com.drv.assetmanagement.service.AuthService;
import com.drv.assetmanagement.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Result<LoginResponseDTO> login(LoginRequestDTO request) {
        // 1. 查找用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            log.warn("登录失败：用户不存在 - {}", request.getUsername());
            return Result.error("用户名或密码错误");
        }

        // 2. 检查测试账户（生产环境禁用）
        if (Boolean.TRUE.equals(isTestAccountDisabled(user))) {
            log.warn("登录失败：测试账户已在生产环境禁用 - {}", request.getUsername());
            return Result.error("测试账户已禁用");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("登录失败：密码错误 - {}", request.getUsername());
            return Result.error("用户名或密码错误");
        }

        // 4. 检查用户状态
        if (!"在职".equals(user.getStatus()) && !"合作中".equals(user.getStatus())) {
            log.warn("登录失败：用户状态异常 - {}, status={}", request.getUsername(), user.getStatus());
            return Result.error("账户已停用");
        }

        // 5. 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleCode());

        // 6. 获取权限列表
        List<String> permissions = getPermissions(user.getRoleCode());

        // 7. 构建响应
        LoginResponseDTO response = LoginResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(86400L) // 24小时
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roleCode(user.getRoleCode())
                .roleName(getRoleName(user.getRoleCode()))
                .userType(user.getUserType())
                .permissions(permissions)
                .build();

        log.info("登录成功：{}", request.getUsername());
        return Result.success(response);
    }

    @Override
    public Result<Void> logout(String token) {
        // TODO: 将token加入黑名单（如果使用Redis）
        log.info("用户登出");
        return Result.success(null);
    }

    @Override
    public Result<LoginResponseDTO> refreshToken(String refreshToken) {
        // TODO: 实现token刷新逻辑
        return Result.error("功能暂未实现");
    }

    private Boolean isTestAccountDisabled(User user) {
        // 检查是否为测试账户且生产环境禁用
        if (user.getIsTestAccount() != null && user.getIsTestAccount() == 1) {
            // 从配置中读取是否禁用测试账户
            String testAccountsEnabled = System.getProperty("app.test-accounts.enabled", "true");
            return "false".equals(testAccountsEnabled);
        }
        return false;
    }

    private String getRoleName(String roleCode) {
        return switch (roleCode) {
            case "MGR_SYSTEM" -> "系统管理员";
            case "MGR_ASSET_ADMIN" -> "资产管理员";
            case "MGR_TRANSFER" -> "调拨员";
            case "MGR_INVENTORY" -> "盘点员";
            case "MGR_FINANCE" -> "财务专员";
            case "USER_STORE_MANAGER" -> "店长";
            case "USER_DEPT_MANAGER" -> "部门经理";
            case "USER_EMPLOYEE" -> "员工";
            case "SUPPLIER_SELLER" -> "出售方";
            case "SUPPLIER_RENTER" -> "出租方";
            case "SUPPLIER_MAINTAINER" -> "维修方";
            default -> "未知角色";
        };
    }

    private List<String> getPermissions(String roleCode) {
        // 根据角色返回权限列表
        return switch (roleCode) {
            case "MGR_SYSTEM" -> Arrays.asList("*:*");
            case "MGR_ASSET_ADMIN" -> Arrays.asList("asset:*", "process:*", "supplier:*", "inventory:*");
            case "MGR_TRANSFER" -> Arrays.asList("asset:read", "transfer:execute");
            case "MGR_INVENTORY" -> Arrays.asList("asset:read", "inventory:*");
            case "MGR_FINANCE" -> Arrays.asList("asset:read", "finance:*", "process:approve:scrap");
            case "USER_STORE_MANAGER" -> Arrays.asList("asset:read:store", "process:apply", "process:approve:store");
            case "USER_DEPT_MANAGER" -> Arrays.asList("asset:read:dept", "process:apply", "process:approve:dept");
            case "USER_EMPLOYEE" -> Arrays.asList("asset:read:self", "process:apply");
            case "SUPPLIER_SELLER", "SUPPLIER_RENTER", "SUPPLIER_MAINTAINER" -> Arrays.asList("order:read", "document:upload");
            default -> Arrays.asList("asset:read:self");
        };
    }
}
