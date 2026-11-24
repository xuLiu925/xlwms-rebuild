package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.StockReturn;
import com.xlwms.rebuild.mapper.StockReturnMapper;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 退货库存页（/stock/return）接口，只读。数据来自本地 MySQL 表 stock_return（虚构示例数据），
 * 经 MyBatis-Plus 查询。接口路径 /gateway/wms/stockReturn、请求体字段与状态编码
 * （10 待上架 / 20 已上架 / 30 待销毁 / 40 已销毁）均为本项目占位，真实接口未抓取。
 */
@RestController
@RequestMapping("/gateway/wms/stockReturn")
public class StockReturnController {

    private final StockReturnMapper mapper;

    public StockReturnController(StockReturnMapper mapper) {
        this.mapper = mapper;
    }

    private LambdaQueryWrapper<StockReturn> wrapper(Map<String, Object> body) {
        String keyword = PageUtil.strParam(body, "keyword");
        String customer = PageUtil.strParam(body, "customerCode");
        Object status = body == null ? null : body.get("status");
        LambdaQueryWrapper<StockReturn> w = new LambdaQueryWrapper<>();
        w.eq(StringUtils.hasText(customer), StockReturn::getCustomerCode, customer);
        w.eq(status != null && StringUtils.hasText(status.toString()), StockReturn::getStatus, status);
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(StockReturn::getReturnNo, keyword)
                    .or().like(StockReturn::getTrackingNo, keyword)
                    .or().like(StockReturn::getSku, keyword)
                    .or().like(StockReturn::getProductName, keyword));
        }
        return w;
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        Page<StockReturn> page = new Page<>(PageUtil.longParam(body, "current", 1), PageUtil.longParam(body, "size", 20));
        return R.ok(PageUtil.of(mapper.selectPage(page, wrapper(body))));
    }

    /** 按当前筛选条件汇总：退货单数与总数量。 */
    @PostMapping("/returnTotal")
    public R<Map<String, Object>> returnTotal(@RequestBody(required = false) Map<String, Object> body) {
        List<StockReturn> rows = mapper.selectList(wrapper(body));
        return R.ok(Map.of(
                "returnCount", rows.size(),
                "totalQty", rows.stream().mapToInt(StockReturn::getQty).sum()));
    }
}
