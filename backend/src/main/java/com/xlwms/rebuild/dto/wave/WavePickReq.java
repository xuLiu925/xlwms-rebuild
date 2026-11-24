package com.xlwms.rebuild.dto.wave;

import java.util.List;

/**
 * 波次拣货提交请求体。对应 PDA POST /gateway/wms/pda/wave/pick。
 * 字段来自 APK 反编译的 WavePickReqBean（含 WaveGridPickItem）。
 *
 * @param cellNo       拣货库位
 * @param creator      操作人
 * @param customerCode 客户编码
 * @param gridPickList 边拣边分时各格口的拣货数量
 * @param pickQty      本次拣货数量
 * @param productSku   产品 SKU
 * @param sourceNo     来源单号
 * @param waveNo       波次号
 * @param whCode       仓库编码
 */
public record WavePickReq(String cellNo, String creator, String customerCode, List<GridPickItem> gridPickList,
                          int pickQty, String productSku, String sourceNo, String waveNo, String whCode) {

    /** @param gridSerial 格口序号  @param pickQty 该格口数量 */
    public record GridPickItem(int gridSerial, int pickQty) {
    }
}
