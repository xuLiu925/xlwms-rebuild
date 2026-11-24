package com.xlwms.rebuild.dto.review;

import java.util.List;

/**
 * 按包裹复核（一票一件 / 一票多包裹）。对应 PDA POST /gateway/wms/pda/review/review4packageV2。
 * 字段来自 APK 反编译的 Review4packageReq（含内部类 Review）。
 *
 * @param customerCode 客户编码
 * @param deliveryNo   出库单号
 * @param oneClickPass 一键通过（不逐件扫描）
 * @param packNo       包裹号
 * @param reviewList   扫描明细
 * @param whCode       仓库编码
 */
public record Review4packageReq(String customerCode, String deliveryNo, boolean oneClickPass, String packNo,
                                List<ReviewItem> reviewList, String whCode) {
}
