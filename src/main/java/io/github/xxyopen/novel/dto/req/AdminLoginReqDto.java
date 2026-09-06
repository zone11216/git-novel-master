package io.github.xxyopen.novel.dto.req;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
/**
 * 管理员登录 请求DTO
 *
 * @author xiongxiaoyang
 * @date 2026/04/17
 */
@Data
public class AdminLoginReqDto {
    /**
     * 管理员账号
     */
    @Schema(description = "管理员账号")
    @NotNull(message = "管理员账号不能为空")
    @Size(min = 1, max = 20, message = "管理员账号长度必须在1-20之间")
    private String username;
    /**
     * 管理员密码
     */
    @Schema(description = "管理员密码")
    @NotNull(message = "管理员密码不能为空")
    @Size(min = 1, max = 20, message = "管理员密码长度必须在1-20之间")
    private String password;
    
}
