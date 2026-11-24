package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.StockSku;
import com.xlwms.rebuild.mapper.StockSkuMapper;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 产品库存页（/stock/sku，按产品）接口，只读。数据来自本地 MySQL 表 stock_sku（虚构示例数据），
 * 通过 MyBatis-Plus 查询；请求体字段名为本项目占位。
 */
@RestController
@RequestMapping("/gateway/wms/stockSku")
public class StockController {

    private final StockSkuMapper mapper;

    public StockController(StockSkuMapper mapper) {
        this.mapper = mapper;
    }

    private LambdaQueryWrapper<StockSku> wrapper(Map<String, Object> body) {
        String keyword = PageUtil.strParam(body, "keyword");
        String customer = PageUtil.strParam(body, "customerCode");
        String attr = PageUtil.strParam(body, "stockAttr");
        LambdaQueryWrapper<StockSku> w = new LambdaQueryWrapper<>();
        w.eq(StringUtils.hasText(customer), StockSku::getCustomerCode, customer);
        w.eq(StringUtils.hasText(attr), StockSku::getStockAttr, attr);
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(StockSku::getSku, keyword)
                    .or().like(StockSku::getBarcode, keyword)
                    .or().like(StockSku::getProductName, keyword));
        }
        return w;
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        Page<StockSku> page = new Page<>(PageUtil.longParam(body, "current", 1), PageUtil.longParam(body, "size", 20));
        return R.ok(PageUtil.of(mapper.selectPage(page, wrapper(body))));
    }

    /** 按当前筛选条件汇总。 */
    @PostMapping("/skuTotal")
    public R<Map<String, Object>> skuTotal(@RequestBody(required = false) Map<String, Object> body) {
        List<StockSku> rows = mapper.selectList(wrapper(body));
        return R.ok(Map.of(
                "skuCount", rows.size(),
                "totalQty", rows.stream().mapToInt(StockSku::getTotalQty).sum(),
                "availableQty", rows.stream().mapToInt(StockSku::getAvailableQty).sum(),
                "lockedQty", rows.stream().mapToInt(StockSku::getLockedQty).sum()));
    }
}
