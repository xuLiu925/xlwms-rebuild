package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
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
 * 本地演示登录。路径 /auth/login、/auth/getUserInfo 取自网页端观察；请求体字段与返回结构是本项目占位。
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
        if (account.isEmpty() || PageUtil.strParam(body, "password").isEmpty()) {
            throw new BizException("请输入账号和密码");
        }
        String token = "local-" + UUID.randomUUID();
        tokens.put(token, account);
        return R.ok(Map.of("token", token, "account", account));
    }

    @GetMapping("/getUserInfo")
    public R<Map<String, Object>> userInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        String account = token == null ? null : tokens.get(token);
        if (account == null) {
            throw new BizException("未登录或登录已失效");
        }
        return R.ok(Map.of("account", account, "realName", "本地演示用户", "whCodes", List.of("WH01", "WH02")));
    }

    @PostMapping("/logout")
    public R<Boolean> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null) {
            tokens.remove(token);
        }
        return R.ok(true);
    }
}
