/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.service;

import com.agentverse.common.dto.auth.LoginRequest;
import com.agentverse.common.dto.auth.LoginResponse;
import com.agentverse.common.dto.auth.RegisterRequest;
import com.agentverse.common.dto.auth.UserInfoResponse;
import com.agentverse.common.entity.SysUser;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.security.JwtUtils;
import com.agentverse.common.security.PasswordUtils;
import com.agentverse.runtime.mapper.SysUserMapper;
import com.agentverse.runtime.service.RolePermissionService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final SysUserMapper sysUserMapper;
    private final RolePermissionService rolePermissionService;

    @Transactional(rollbackFor={Exception.class})
    public UserInfoResponse register(RegisterRequest request) {
        log.info("Registering user: {}", (Object)request.getUsername());
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysUser::getUsername, (Object)request.getUsername());
        if (this.sysUserMapper.selectCount((Wrapper)queryWrapper) > 0L) {
            throw new BizException(ErrorCode.USERNAME_EXISTS);
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtils.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRoleCode("developer");
        user.setStatus(1);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        user.setDeleted(0);
        this.sysUserMapper.insert(user);
        log.info("User registered successfully: id={}, username={}", (Object)user.getId(), (Object)user.getUsername());
        return this.buildUserInfoResponse(user);
    }

    public LoginResponse login(LoginRequest request) {
        log.info("User login attempt: {}", (Object)request.getUsername());
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysUser::getUsername, (Object)request.getUsername());
        SysUser user = (SysUser)this.sysUserMapper.selectOne((Wrapper)queryWrapper);
        if (user == null) {
            throw new BizException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException(ErrorCode.USER_DISABLED);
        }
        if (!PasswordUtils.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.INVALID_CREDENTIALS);
        }
        List<String> permissions = this.rolePermissionService.getPermissionsByRoleCode(user.getRoleCode());
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getRoleCode());
        log.info("User logged in successfully: {}", (Object)user.getUsername());
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

    public SysUser getUserById(Long userId) {
        return (SysUser)this.sysUserMapper.selectById(userId);
    }

    public UserInfoResponse buildUserInfoResponse(SysUser user) {
        List<String> permissions = this.rolePermissionService.getPermissionsByRoleCode(user.getRoleCode());
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRoleCode(user.getRoleCode());
        response.setPermissions(permissions);
        return response;
    }

    @Generated
    public UserService(SysUserMapper sysUserMapper, RolePermissionService rolePermissionService) {
        this.sysUserMapper = sysUserMapper;
        this.rolePermissionService = rolePermissionService;
    }
}

