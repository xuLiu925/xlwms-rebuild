package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.BasicArea;
import com.xlwms.rebuild.mapper.BasicAreaMapper;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基础数据-库区页（/basicData/area）接口，只读。数据来自本地 MySQL 表 basic_area（虚构示例数据），
 * 经 MyBatis-Plus 查询。接口路径 /gateway/wms/basicArea、请求体字段与库区类型均为本项目占位，真实接口未抓取。
 */
@RestController
@RequestMapping("/gateway/wms/basicArea")
public class BasicAreaController {

    private final BasicAreaMapper mapper;

    public BasicAreaController(BasicAreaMapper mapper) {
        this.mapper = mapper;
    }

    private LambdaQueryWrapper<BasicArea> wrapper(Map<String, Object> body) {
        String keyword = PageUtil.strParam(body, "keyword");
        String whCode = PageUtil.strParam(body, "whCode");
        String areaType = PageUtil.strParam(body, "areaType");
        LambdaQueryWrapper<BasicArea> w = new LambdaQueryWrapper<>();
        w.eq(StringUtils.hasText(whCode), BasicArea::getWhCode, whCode);
        w.eq(StringUtils.hasText(areaType), BasicArea::getAreaType, areaType);
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(BasicArea::getAreaCode, keyword).or().like(BasicArea::getAreaName, keyword));
        }
        return w;
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        Page<BasicArea> page = new Page<>(PageUtil.longParam(body, "current", 1), PageUtil.longParam(body, "size", 20));
        return R.ok(PageUtil.of(mapper.selectPage(page, wrapper(body))));
    }
}
