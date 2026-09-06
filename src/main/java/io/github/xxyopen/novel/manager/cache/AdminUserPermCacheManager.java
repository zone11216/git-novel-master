package io.github.xxyopen.novel.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.xxyopen.novel.core.constant.CacheConsts;
import io.github.xxyopen.novel.core.constant.DatabaseConsts;
import io.github.xxyopen.novel.dao.entity.SysMenu;
import io.github.xxyopen.novel.dao.entity.SysRole;
import io.github.xxyopen.novel.dao.entity.SysRoleMenu;
import io.github.xxyopen.novel.dao.entity.SysUserRole;
import io.github.xxyopen.novel.dao.mapper.SysMenuMapper;
import io.github.xxyopen.novel.dao.mapper.SysRoleMapper;
import io.github.xxyopen.novel.dao.mapper.SysRoleMenuMapper;
import io.github.xxyopen.novel.dao.mapper.SysUserRoleMapper;
import io.github.xxyopen.novel.dto.AdminUserPermDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理员权限信息 缓存管理类
 */
@Component
@RequiredArgsConstructor
public class AdminUserPermCacheManager {

    /**
     * 菜单类型：菜单
     */
    private static final int MENU_TYPE_MENU = 1;

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysMenuMapper sysMenuMapper;

    /**
     * 查询管理员的权限信息（角色标识 + 菜单权限），并放入 Redis 缓存
     */
    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
        value = CacheConsts.ADMIN_USER_PERM_CACHE_NAME)
    public AdminUserPermDto getPerm(Long userId) {
        // 1. 查询用户关联的角色 ID
        List<Long> roleIds = sysUserRoleMapper.selectList(new QueryWrapper<SysUserRole>()
                .eq(DatabaseConsts.SysUserRoleTable.COLUMN_USER_ID, userId))
            .stream().map(SysUserRole::getRoleId).toList();
        if (roleIds.isEmpty()) {
            return AdminUserPermDto.builder()
                .roleSigns(Set.of()).menuIds(Set.of()).menuUrls(Set.of()).build();
        }

        // 2. 查询角色标识
        Set<String> roleSigns = sysRoleMapper.selectBatchIds(roleIds).stream()
            .map(SysRole::getRoleSign).filter(StringUtils::hasText).collect(Collectors.toSet());

        // 3. 查询角色关联的菜单 ID
        List<Long> menuIds = sysRoleMenuMapper.selectList(new QueryWrapper<SysRoleMenu>()
                .in(DatabaseConsts.SysRoleMenuTable.COLUMN_ROLE_ID, roleIds))
            .stream().map(SysRoleMenu::getMenuId).distinct().toList();
        if (menuIds.isEmpty()) {
            return AdminUserPermDto.builder()
                .roleSigns(roleSigns).menuIds(Set.of()).menuUrls(Set.of()).build();
        }

        // 4. 查询菜单类型节点的权限 URI（目录节点无 url）
        Set<Long> menuIdSet = Set.copyOf(menuIds);
        Set<String> menuUrls = sysMenuMapper.selectBatchIds(menuIds).stream()
            .filter(m -> Objects.equals(m.getType(), MENU_TYPE_MENU)
                && StringUtils.hasText(m.getUrl()))
            .map(SysMenu::getUrl).collect(Collectors.toSet());

        return AdminUserPermDto.builder()
            .roleSigns(roleSigns).menuIds(menuIdSet).menuUrls(menuUrls).build();
    }

    /**
     * 权限数据变更时，清除对应管理员的权限缓存
     */
    @CacheEvict(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
        value = CacheConsts.ADMIN_USER_PERM_CACHE_NAME)
    public void evict(Long userId) {
    }

}