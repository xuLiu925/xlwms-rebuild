package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.BlDelivery;
import com.xlwms.rebuild.mapper.BlDeliveryMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 一件代发（/outbound/parcel）列表接口，只读。数据来自本地 MySQL 表 bl_delivery（虚构示例数据），
 * 通过 MyBatis-Plus 查询。status 编码和字段名为本项目占位，真实编码未知。
 */
@RestController
@RequestMapping("/gateway/wms")
public class DeliveryController {

    /** 页签状态：编码为占位，真实编码未知。 */
    static final Map<Integer, String> STATUS = new LinkedHashMap<>();

    static {
        STATUS.put(10, "待处理");
        STATUS.put(20, "待拣货");
        STATUS.put(30, "待复核");
        STATUS.put(40, "待出库");
        STATUS.put(50, "已出库");
        STATUS.put(80, "异常");
        STATUS.put(90, "已取消");
    }

    private final BlDeliveryMapper mapper;

    public DeliveryController(BlDeliveryMapper mapper) {
        this.mapper = mapper;
    }

    /** 基础筛选（客户、关键字）；byStatus 为 true 时再按 status 过滤。 */
    private LambdaQueryWrapper<BlDelivery> wrapper(Map<String, Object> body, boolean byStatus) {
        String status = PageUtil.strParam(body, "status");
        String keyword = PageUtil.strParam(body, "keyword");
        String customer = PageUtil.strParam(body, "customerCode");
        LambdaQueryWrapper<BlDelivery> w = new LambdaQueryWrapper<>();
        w.eq(byStatus && StringUtils.hasText(status), BlDelivery::getStatus, status);
        w.eq(StringUtils.hasText(customer), BlDelivery::getCustomerCode, customer);
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(BlDelivery::getDeliveryNo, keyword)
                    .or().like(BlDelivery::getTrackingNo, keyword)
                    .or().like(BlDelivery::getWaveNo, keyword));
        }
        return w;
    }

    /** 各页签计数（按当前筛选条件，不含状态本身）。 */
    @PostMapping("/blDelivery/queryInTotal")
    public R<List<Map<String, Object>>> queryInTotal(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> out = new ArrayList<>();
        STATUS.forEach((code, name) -> {
            LambdaQueryWrapper<BlDelivery> w = wrapper(body, false).eq(BlDelivery::getStatus, code);
            out.add(Map.of("status", code, "statusName", name, "statusSum", mapper.selectCount(w)));
        });
        return R.ok(out);
    }

    @PostMapping("/blDelivery/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        Page<BlDelivery> page = new Page<>(PageUtil.longParam(body, "current", 1), PageUtil.longParam(body, "size", 20));
        return R.ok(PageUtil.of(mapper.selectPage(page, wrapper(body, true))));
    }
}
