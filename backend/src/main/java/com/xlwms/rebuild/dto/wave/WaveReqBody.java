package com.xlwms.rebuild.dto.wave;

/**
 * 波次扫库位 / 扫 SKU 请求体。对应 PDA POST /gateway/wms/pda/wave/{scanCell,scanSku}。
 * 字段来自 APK 反编译的 com.lingxing.wms.data.model.bean.WaveReqBody。
 *
 * @param barcode      扫到的条码（扫 SKU 时）
 * @param cellNo       库位号
 * @param customerCode 客户编码
 * @param sku          产品 SKU（APK 里字段名就是 sku，不是 productSku）
 * @param waveNo       波次号
 * @param whCode       仓库编码
 */
public record WaveReqBody(String barcode, String cellNo, String customerCode, String sku, String waveNo, String whCode) {
}
