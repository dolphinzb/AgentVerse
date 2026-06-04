package com.agentverse.runtime.service;

import com.agentverse.common.dto.auth.*;
import com.agentverse.common.entity.SysUser;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.security.JwtUtils;
import com.agentverse.common.security.PasswordUtils;
import com.agentverse.runtime.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 用户认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserMapper sysUserMapper;
    private final RolePermissionService rolePermissionService;

    /**
     * 用户注册
     */
    @Transactional(rollbackFor = Exception.class)
    public UserInfoResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.getUsername());

        // 检查用户名是否存在
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, request.getUsername());
        if (sysUserMapper.selectCount(queryWrapper) > 0) {
            throw new BizException(ErrorCode.USERNAME_EXISTS);
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtils.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRoleCode("developer"); // 默认注册为 developer
        user.setStatus(1);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        user.setDeleted(0);

        sysUserMapper.insert(user);
        log.info("User registered successfully: id={}, username={}", user.getId(), user.getUsername());

        return buildUserInfoResponse(user);
    }

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsername());

        // 查询用户
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser user = sysUserMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BizException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException(ErrorCode.USER_DISABLED);
        }

        // 校验密码
        if (!PasswordUtils.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 获取用户权限
        List<String> permissions = rolePermissionService.getPermissionsByRoleCode(user.getRoleCode());

        // 生成 Token
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getRoleCode());

        log.info("User logged in successfully: {}", user.getUsername());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(JwtUtils.getExpiresInSeconds());

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setRoleCode(user.getRoleCode());
        userInfo.setPermissions(permissions);

        response.setUser(userInfo);
        return response;
    }

    /**
     * 根据 ID 获取用户
     */
    public SysUser getUserById(Long userId) {
        return sysUserMapper.selectById(userId);
    }

    /**
     * 构建用户信息响应
     */
    public UserInfoResponse buildUserInfoResponse(SysUser user) {
        List<String> permissions = rolePermissionService.getPermissionsByRoleCode(user.getRoleCode());

        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRoleCode(user.getRoleCode());
        response.setPermissions(permissions);
        return response;
    }
}