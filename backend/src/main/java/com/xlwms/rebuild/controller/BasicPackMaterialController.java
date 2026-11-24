package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.BasicPackMaterial;
import com.xlwms.rebuild.mapper.BasicPackMaterialMapper;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基础数据-包材页（/basicData/packMaterial）接口，只读。数据来自本地 MySQL 表 basic_pack_material（虚构示例数据），
 * 经 MyBatis-Plus 查询。接口路径 /gateway/wms/basicPackMaterial、请求体字段、包材类型与状态编码
 * （0 停用 / 1 启用）均为本项目占位，真实接口未抓取。
 */
@RestController
@RequestMapping("/gateway/wms/basicPackMaterial")
public class BasicPackMaterialController {

    private final BasicPackMaterialMapper mapper;

    public BasicPackMaterialController(BasicPackMaterialMapper mapper) {
        this.mapper = mapper;
    }

    private LambdaQueryWrapper<BasicPackMaterial> wrapper(Map<String, Object> body) {
        String keyword = PageUtil.strParam(body, "keyword");
        String materialType = PageUtil.strParam(body, "materialType");
        Object status = body == null ? null : body.get("status");
        LambdaQueryWrapper<BasicPackMaterial> w = new LambdaQueryWrapper<>();
        w.eq(StringUtils.hasText(materialType), BasicPackMaterial::getMaterialType, materialType);
        w.eq(status != null && StringUtils.hasText(status.toString()), BasicPackMaterial::getStatus, status);
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(BasicPackMaterial::getMaterialCode, keyword).or().like(BasicPackMaterial::getMaterialName, keyword));
        }
        return w;
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        Page<BasicPackMaterial> page = new Page<>(PageUtil.longParam(body, "current", 1), PageUtil.longParam(body, "size", 20));
        return R.ok(PageUtil.of(mapper.selectPage(page, wrapper(body))));
    }
}
