package com.xlwms.rebuild.dto.receipt;

/**
 * 扫描托盘请求体。对应 PDA 接口 POST /gateway/wms/pda/receipt/scanPallet。
 * 字段来自 APK 反编译的 com.lingxing.wms.data.model.bean.PalletReceiptScanReq。
 */
public class PalletReceiptScanReq {
    /** 托盘编码 */
    private String palletCode;
    /** 仓库编码 */
    private String whCode;

    public String getPalletCode() {
        return palletCode;
    }

    public void setPalletCode(String palletCode) {
        this.palletCode = palletCode;
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }
}
