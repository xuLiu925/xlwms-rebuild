package com.xlwms.rebuild.dto.review;

/**
 * 复核后确认出库。对应 PDA POST /gateway/wms/pda/review/confirmDelivery。
 * 字段来自 APK 反编译的 ReviewConfirmDeliveryReq。
 */
public record ReviewConfirmDeliveryReq(String customerCode, String deliveryNo, String whCode) {
}
