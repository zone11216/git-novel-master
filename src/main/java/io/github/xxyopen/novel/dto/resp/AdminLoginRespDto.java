package io.github.xxyopen.novel.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 管理员登录 响应DTO
 *
 * @author xiongxiaoyang
 * @date 2026/04/17
 */
@Data
@Builder
public class AdminLoginRespDto {

    @Schema(description = "管理员ID")
    private Long uid;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String name;

    @Schema(description = "用户token")
    private String token;

}
