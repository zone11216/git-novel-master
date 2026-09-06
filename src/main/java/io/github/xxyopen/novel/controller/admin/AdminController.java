package io.github.xxyopen.novel.controller.admin;

import io.github.xxyopen.novel.core.common.resp.RestResp;
import io.github.xxyopen.novel.core.constant.ApiRouterConsts;
import io.github.xxyopen.novel.core.constant.SystemConfigConsts;
import io.github.xxyopen.novel.dto.req.AdminLoginReqDto;
import io.github.xxyopen.novel.dto.resp.AdminLoginRespDto;
import io.github.xxyopen.novel.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台后台-管理模块 API 控制器
 *
 * @author xiongxiaoyang
 * @date 2026/04/17
 */
@Tag(name = "AdminController", description = "平台后台-管理模块")
@SecurityRequirement(name = SystemConfigConsts.HTTP_AUTH_HEADER_NAME)
@RestController
@RequestMapping(ApiRouterConsts.API_ADMIN_URL_PREFIX)
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 管理员登录接口
     */
    @Operation(summary = "管理员登录接口")
    @PostMapping("login")
    public RestResp<AdminLoginRespDto> login(@Valid @RequestBody AdminLoginReqDto dto) {
        return adminService.login(dto);
    }

}