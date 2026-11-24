package com.xlwms.rebuild.dto.review;

import java.util.List;

/**
 * 一票多件复核。对应 PDA POST /gateway/wms/pda/review/morePieceV2。
 * 字段来自 APK 反编译的 ReviewMorePieceReq。
 */
public record ReviewMorePieceReq(String customerCode, String deliveryNo, boolean oneClickPass,
                                 List<ReviewItem> reviewList, String whCode) {
}
