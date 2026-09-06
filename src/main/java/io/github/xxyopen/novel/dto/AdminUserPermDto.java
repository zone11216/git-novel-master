package io.github.xxyopen.novel.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 管理员权限信息 DTO
 */
@Data
@Builder
public class AdminUserPermDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色标识集合
     */
    private Set<String> roleSigns;

    /**
     * 菜单 ID 集合
     */
    private Set<Long> menuIds;

    /**
     * 菜单权限 URI 集合
     */
    private Set<String> menuUrls;

}