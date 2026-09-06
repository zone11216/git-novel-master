package io.github.xxyopen.novel.service;

import io.github.xxyopen.novel.core.common.resp.RestResp;
import io.github.xxyopen.novel.dto.req.AdminLoginReqDto;
import io.github.xxyopen.novel.dto.resp.AdminLoginRespDto;

/**
 * 后台管理模块 服务类
 *
 * @author xiongxiaoyang
 * @date 2026/04/17
 */
public interface AdminService {

    /**
     * 管理员登录
     *
     * @param dto 登录参数
     * @return token + 用户信息
     */
    RestResp<AdminLoginRespDto> login(AdminLoginReqDto dto);

}