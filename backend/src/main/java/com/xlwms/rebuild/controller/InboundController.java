package com.xlwms.rebuild.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlwms.rebuild.common.PageUtil;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.entity.BlAsn;
import com.xlwms.rebuild.mapper.BlAsnMapper;
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
 * 入库管理（ASN）列表相关接口，只读。数据来自本地 MySQL 表 bl_asn（虚构示例数据），
 * 通过 MyBatis-Plus 查询。status：10 在途/11 收货中/15 已收货/30 已上架/90 已取消。
 */
@RestController
@RequestMapping("/gateway/wms/blAsn")
public class InboundController {

    static final Map<Integer, String> STATUS = new LinkedHashMap<>();

    static {
        STATUS.put(10, "在途");
        STATUS.put(11, "收货中");
        STATUS.put(15, "已收货");
        STATUS.put(30, "已上架");
        STATUS.put(90, "已取消");
    }

    private final BlAsnMapper mapper;

    public InboundController(BlAsnMapper mapper) {
        this.mapper = mapper;
    }

    private LambdaQueryWrapper<BlAsn> wrapper(Map<String, Object> body, boolean byStatus) {
        String status = PageUtil.strParam(body, "status");
        String keyword = PageUtil.strParam(body, "keyword");
        String customer = PageUtil.strParam(body, "customerCode");
        LambdaQueryWrapper<BlAsn> w = new LambdaQueryWrapper<>();
        w.eq(byStatus && StringUtils.hasText(status), BlAsn::getStatus, status);
        w.eq(StringUtils.hasText(customer), BlAsn::getCustomerCode, customer);
        if (StringUtils.hasText(keyword)) {
            w.and(q -> q.like(BlAsn::getAsnNo, keyword).or().like(BlAsn::getSourceNo, keyword));
        }
        return w;
    }

    @PostMapping("/status/statistics")
    public R<List<Map<String, Object>>> statistics(@RequestBody(required = false) Map<String, Object> body) {
        List<Map<String, Object>> out = new ArrayList<>();
        STATUS.forEach((code, name) -> {
            LambdaQueryWrapper<BlAsn> w = wrapper(body, false).eq(BlAsn::getStatus, code);
            out.add(Map.of("status", code, "statusName", name, "statusSum", mapper.selectCount(w)));
        });
        return R.ok(out);
    }

    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        Page<BlAsn> page = new Page<>(PageUtil.longParam(body, "current", 1), PageUtil.longParam(body, "size", 20));
        return R.ok(PageUtil.of(mapper.selectPage(page, wrapper(body, true))));
    }
}
