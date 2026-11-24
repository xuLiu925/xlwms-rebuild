package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.BasicProduct;
import com.xlwms.rebuild.mapper.BasicProductMapper;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基础数据-产品资料页（/basicData/product）接口，只读。数据来自本地 MySQL 表 basic_product（虚构示例数据），
 * 经 MyBatis-Plus 查询。接口路径 /gateway/wms/basicProduct、请求体字段与状态编码（0 停用 / 1 启用）均为本项目占位，真实接口未抓取。
 */
@RestController
@RequestMapping("/gateway/wms/basicProduct")
public class BasicProductController {

    private final BasicProductMapper mapper;

    public BasicProductController(BasicProductMapper mapper) {
        this.mapper = mapper;
    }

    private LambdaQueryWrapper<BasicProduct> wrapper(Map<String, Object> body) {
        String keyword = PageUtil.strParam(body, "keyword");
        String customer = PageUtil.strParam(body, "customerCode");
        Object status = body == null ? null : body.get("status");
        LambdaQueryWrapper<BasicProduct> w = new LambdaQueryWrapper<>();
        w.eq(StringUtils.hasText(customer), BasicProduct::getCustomerCode, customer);
        w.eq(status != null && StringUtils.hasText(status.toString()), BasicProduct::getStatus, status);
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(BasicProduct::getSku, keyword)
                    .or().like(BasicProduct::getBarcode, keyword)
                    .or().like(BasicProduct::getProductName, keyword));
        }
        return w;
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        Page<BasicProduct> page = new Page<>(PageUtil.longParam(body, "current", 1), PageUtil.longParam(body, "size", 20));
        return R.ok(PageUtil.of(mapper.selectPage(page, wrapper(body))));
    }
}
