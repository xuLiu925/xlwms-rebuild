package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.StockBox;
import com.xlwms.rebuild.mapper.StockBoxMapper;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 按箱库存页（/stock/box）接口，只读。数据来自本地 MySQL 表 stock_box（虚构示例数据），
 * 经 MyBatis-Plus 查询。接口路径 /gateway/wms/stockBox 与请求体字段均为本项目占位，真实接口未抓取。
 */
@RestController
@RequestMapping("/gateway/wms/stockBox")
public class StockBoxController {

    private final StockBoxMapper mapper;

    public StockBoxController(StockBoxMapper mapper) {
        this.mapper = mapper;
    }

    private LambdaQueryWrapper<StockBox> wrapper(Map<String, Object> body) {
        String keyword = PageUtil.strParam(body, "keyword");
        String customer = PageUtil.strParam(body, "customerCode");
        String attr = PageUtil.strParam(body, "stockAttr");
        LambdaQueryWrapper<StockBox> w = new LambdaQueryWrapper<>();
        w.eq(StringUtils.hasText(customer), StockBox::getCustomerCode, customer);
        w.eq(StringUtils.hasText(attr), StockBox::getStockAttr, attr);
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(StockBox::getBoxNo, keyword)
                    .or().like(StockBox::getSku, keyword)
                    .or().like(StockBox::getProductName, keyword)
                    .or().like(StockBox::getCellCode, keyword));
        }
        return w;
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        Page<StockBox> page = new Page<>(PageUtil.longParam(body, "current", 1), PageUtil.longParam(body, "size", 20));
        return R.ok(PageUtil.of(mapper.selectPage(page, wrapper(body))));
    }

    /** 按当前筛选条件汇总：箱数与总数量。 */
    @PostMapping("/boxTotal")
    public R<Map<String, Object>> boxTotal(@RequestBody(required = false) Map<String, Object> body) {
        List<StockBox> rows = mapper.selectList(wrapper(body));
        return R.ok(Map.of(
                "boxCount", rows.size(),
                "totalQty", rows.stream().mapToInt(StockBox::getQty).sum()));
    }
}
