package io.github.xxyopen.novel.core.auth;

import io.github.xxyopen.novel.core.common.constant.ErrorCodeEnum;
import io.github.xxyopen.novel.core.common.exception.BusinessException;
import io.github.xxyopen.novel.core.constant.ApiRouterConsts;
import io.github.xxyopen.novel.core.constant.SystemConfigConsts;
import io.github.xxyopen.novel.core.util.JwtUtils;
import io.github.xxyopen.novel.dao.entity.SysUser;
import io.github.xxyopen.novel.dao.mapper.SysUserMapper;
import io.github.xxyopen.novel.dto.AdminUserPermDto;
import io.github.xxyopen.novel.manager.cache.AdminUserPermCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AdminAuthStrategy implements AuthStrategy {

    /**
     * 超级管理员角色标识，拥有全部接口权限
     */
    public static final String SUPER_ROLE_SIGN = "super";

    /**
     * 免接口鉴权的 URI：查询当前登录管理员自身菜单信息，仅认证不鉴权（参照 RuoYi getRouters 设计）
     */
    private static final String SELF_MENU_URI = ApiRouterConsts.API_ADMIN_MENU_URL_PREFIX + "/current";

    private final JwtUtils jwtUtils;
    private final SysUserMapper sysUserMapper;
    private final AdminUserPermCacheManager adminUserPermCacheManager;

    @Override
    public void auth(String token, String requestUri) throws BusinessException {
        // 1. 校验 token 非空
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCodeEnum.USER_LOGIN_EXPIRED);
        }

        // 2. 解析 token 获取管理员 ID
        Long adminId = jwtUtils.parseToken(token, SystemConfigConsts.NOVEL_ADMIN_KEY);
        if (Objects.isNull(adminId)) {
            throw new BusinessException(ErrorCodeEnum.USER_LOGIN_EXPIRED);
        }

        // 3. 查询管理员信息
        SysUser sysUser = sysUserMapper.selectById(adminId);
        if (Objects.isNull(sysUser)) {
            throw new BusinessException(ErrorCodeEnum.USER_ACCOUNT_NOT_EXIST);
        }

        // 4. RBAC 接口鉴权：自身菜单信息接口豁免；其余接口需拥有 super 角色，
        //    或请求 URI 与所分配菜单的 url（API 前缀）匹配
        if (!SELF_MENU_URI.equals(requestUri)) {
            AdminUserPermDto perm = adminUserPermCacheManager.getPerm(adminId);
            boolean permitted = perm.getRoleSigns().contains(SUPER_ROLE_SIGN)
                || perm.getMenuUrls().stream().anyMatch(url -> StringUtils.hasText(url)
                    && (requestUri.equals(url) || requestUri.startsWith(url + "/")));
            if (!permitted) {
                throw new BusinessException(ErrorCodeEnum.USER_UN_AUTH);
            }
        }

        // 5. 设置 adminId 到当前线程（暂用 setUserId，复用现有字段）
        UserHolder.setUserId(adminId);
    }
}