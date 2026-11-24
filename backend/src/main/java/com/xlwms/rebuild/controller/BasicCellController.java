package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.BasicCell;
import com.xlwms.rebuild.mapper.BasicCellMapper;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基础数据-库位页（/basicData/cell）接口，只读。数据来自本地 MySQL 表 basic_cell（虚构示例数据），
 * 经 MyBatis-Plus 查询。接口路径 /gateway/wms/basicCell、请求体字段、库位类型与占用状态
 * （0 空闲 / 1 占用 / 2 锁定）均为本项目占位，真实接口未抓取。
 */
@RestController
@RequestMapping("/gateway/wms/basicCell")
public class BasicCellController {

    private final BasicCellMapper mapper;

    public BasicCellController(BasicCellMapper mapper) {
        this.mapper = mapper;
    }

    private LambdaQueryWrapper<BasicCell> wrapper(Map<String, Object> body) {
        String keyword = PageUtil.strParam(body, "keyword");
        String areaCode = PageUtil.strParam(body, "areaCode");
        Object usageStatus = body == null ? null : body.get("usageStatus");
        LambdaQueryWrapper<BasicCell> w = new LambdaQueryWrapper<>();
        w.eq(StringUtils.hasText(areaCode), BasicCell::getAreaCode, areaCode);
        w.eq(usageStatus != null && StringUtils.hasText(usageStatus.toString()), BasicCell::getUsageStatus, usageStatus);
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(BasicCell::getCellCode, keyword).or().like(BasicCell::getAreaName, keyword));
        }
        return w;
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        Page<BasicCell> page = new Page<>(PageUtil.longParam(body, "current", 1), PageUtil.longParam(body, "size", 20));
        return R.ok(PageUtil.of(mapper.selectPage(page, wrapper(body))));
    }
}
