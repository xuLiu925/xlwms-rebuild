package com.xlwms.rebuild.dto.wave;

/**
 * 查询订单是否被拦截。对应 PDA POST /gateway/wms/pda/wave/queryIntercepting（返回 Integer）。
 * 字段来自 APK 反编译的 QueryInterceptingReq。
 */
public record QueryInterceptingReq(String orderNo, String tenantCode, String whCode) {
}
