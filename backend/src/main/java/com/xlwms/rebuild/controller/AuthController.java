package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 本地演示登录，网页端与 PDA 共用。路径 /auth/login、/auth/getUserInfo 网页端与 APK 一致；
 * PDA 的请求体 LoginReqBean（loginAccount/password/businessType/version/autoLogin）、响应 LoginResp.token 与 UserInfo 字段取自 APK，
 * 网页端的 account/realName/whCodes 是本项目占位，两套字段同时返回。PDA 发的 "Authorization: Bearer xxx" 会去掉前缀再查。
 * <p>只用于本地演示：<b>不校验、不保存任何密码</b>，账号和密码非空即签发一个随机本地令牌（内存保存，重启失效）。
 * 其他接口不做鉴权拦截。真实系统还有腾讯云验证码，这里未实现。
 */
@RestController
@RequestMapping("/gateway/wms/auth")
public class AuthController {

    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody(required = false) Map<String, Object> body) {
        String account = PageUtil.strParam(body, "account");
        if (account.isEmpty()) {
            account = PageUtil.strParam(body, "loginAccount");
        }
        if (account.isEmpty() || PageUtil.strParam(body, "password").isEmpty()) {
            throw new BizException("请输入账号和密码");
        }
        String token = "local-" + UUID.randomUUID();
        tokens.put(token, account);
        return R.ok(Map.of("token", token, "account", account));
    }

    @GetMapping("/getUserInfo")
    public R<Map<String, Object>> userInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        String account = token == null ? null : tokens.get(token.startsWith("Bearer ") ? token.substring(7).trim() : token);
        if (account == null) {
            throw new BizException("未登录或登录已失效");
        }
        Map<String, Object> u = new LinkedHashMap<>();
        u.put("account", account);
        u.put("realName", "本地演示用户");
        u.put("whCodes", List.of("WH01", "WH02"));
        // UserInfo（APK）
        u.put("avatar", "");
        u.put("createBy", "system");
        u.put("createTime", "2026-11-24 00:00:00");
        u.put("customerCode", "");
        u.put("email", "");
        u.put("loginAccount", account);
        u.put("phone", "");
        u.put("roleNameList", List.of("本地演示角色"));
        u.put("sex", 0);
        u.put("status", 1);
        u.put("tenantCode", "T-DEMO");
        u.put("updateBy", "system");
        u.put("updateTime", "2026-11-24 00:00:00");
        u.put("userNameCn", "本地演示用户");
        u.put("userNameEn", "Local Demo");
        return R.ok(u);
    }

    @PostMapping("/logout")
    public R<Boolean> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null) {
            tokens.remove(token.startsWith("Bearer ") ? token.substring(7).trim() : token);
        }
        return R.ok(true);
    }
}
