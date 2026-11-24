package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.BasicTray;
import com.xlwms.rebuild.mapper.BasicTrayMapper;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基础数据-托盘页（/basicData/tray）接口，只读。数据来自本地 MySQL 表 basic_tray（虚构示例数据），
 * 经 MyBatis-Plus 查询。接口路径 /gateway/wms/basicTray、请求体字段与占用状态
 * （0 空闲 / 1 在用）均为本项目占位，真实接口未抓取。
 */
@RestController
@RequestMapping("/gateway/wms/basicTray")
public class BasicTrayController {

    private final BasicTrayMapper mapper;

    public BasicTrayController(BasicTrayMapper mapper) {
        this.mapper = mapper;
    }

    private LambdaQueryWrapper<BasicTray> wrapper(Map<String, Object> body) {
        String keyword = PageUtil.strParam(body, "keyword");
        String whCode = PageUtil.strParam(body, "whCode");
        Object status = body == null ? null : body.get("status");
        LambdaQueryWrapper<BasicTray> w = new LambdaQueryWrapper<>();
        w.eq(StringUtils.hasText(whCode), BasicTray::getWhCode, whCode);
        w.eq(status != null && StringUtils.hasText(status.toString()), BasicTray::getStatus, status);
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(BasicTray::getTrayNo, keyword).or().like(BasicTray::getCellCode, keyword));
        }
        return w;
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        Page<BasicTray> page = new Page<>(PageUtil.longParam(body, "current", 1), PageUtil.longParam(body, "size", 20));
        return R.ok(PageUtil.of(mapper.selectPage(page, wrapper(body))));
    }
}
