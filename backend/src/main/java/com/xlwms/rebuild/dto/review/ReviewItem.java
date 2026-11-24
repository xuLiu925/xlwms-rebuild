package com.xlwms.rebuild.dto.review;

import java.util.List;

/**
 * 复核扫描明细，APK 中 Review4packageReq.Review 与 ReviewMorePieceItem 字段相同。
 *
 * @param barcode         产品条码
 * @param productSnNoList 扫到的序列号
 * @param qty             数量
 */
public record ReviewItem(String barcode, List<String> productSnNoList, int qty) {
}
