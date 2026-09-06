package io.github.xxyopen.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.xxyopen.novel.core.common.constant.ErrorCodeEnum;
import io.github.xxyopen.novel.core.common.exception.BusinessException;
import io.github.xxyopen.novel.core.common.resp.RestResp;
import io.github.xxyopen.novel.core.constant.DatabaseConsts;
import io.github.xxyopen.novel.core.constant.SystemConfigConsts;
import io.github.xxyopen.novel.core.util.JwtUtils;
import io.github.xxyopen.novel.dao.entity.SysUser;
import io.github.xxyopen.novel.dao.mapper.SysUserMapper;
import io.github.xxyopen.novel.dto.req.AdminLoginReqDto;
import io.github.xxyopen.novel.dto.resp.AdminLoginRespDto;
import io.github.xxyopen.novel.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.util.DigestUtils;

/**
 * 后台管理模块 服务实现类
 *
 * @author xiongxiaoyang
 * @date 2026/04/17
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SysUserMapper sysUserMapper;

    private final JwtUtils jwtUtils;

    @Override
    public RestResp<AdminLoginRespDto> login(AdminLoginReqDto dto) {
        // 1. 根据用户名查询管理员信息
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DatabaseConsts.SysUserTable.COLUMN_USERNAME, dto.getUsername())
            .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);

        // 2. 校验用户是否存在
        if (Objects.isNull(sysUser)) {
            throw new BusinessException(ErrorCodeEnum.USER_ACCOUNT_NOT_EXIST);
        }

        // 3. 校验密码是否正确（使用 MD5 加密比对，与前台用户保持一致）
        String encryptedPassword = DigestUtils.md5DigestAsHex(
            dto.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!Objects.equals(sysUser.getPassword(), encryptedPassword)) {
            throw new BusinessException(ErrorCodeEnum.USER_PASSWORD_ERROR);
        }

        // 4. 校验账号是否启用（0-禁用 1-正常）
        if (Objects.nonNull(sysUser.getStatus()) && sysUser.getStatus() == 0) {
            throw new BusinessException(ErrorCodeEnum.USER_ACCOUNT_DISABLED);
        }

        // 5. 更新最后登录时间（可选）
        SysUser updateUser = new SysUser();
        updateUser.setId(sysUser.getId());
        updateUser.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(updateUser);

        // 6. 生成 JWT 并返回
        return RestResp.ok(AdminLoginRespDto.builder()
            .token(jwtUtils.generateToken(sysUser.getId(), SystemConfigConsts.NOVEL_ADMIN_KEY))
            .uid(sysUser.getId())
            .username(sysUser.getUsername())
            .name(sysUser.getName())
            .build());
    }

}