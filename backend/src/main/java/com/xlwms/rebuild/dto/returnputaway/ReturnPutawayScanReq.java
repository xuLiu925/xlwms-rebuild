package com.xlwms.rebuild.dto.returnputaway;

/**
 * 退货上架扫描请求体。用于 POST /gateway/wms/pda/return/putaway/{scanBarcode,scanOrderNo}。
 * 字段来自 APK 反编译的 com.lingxing.wms.viewmodel.request.ReturnPutawayScanReq。
 */
public class ReturnPutawayScanReq {
    /** 扫描内容（条码或单号） */
    private String scanContent;
    /** 仓库编码 */
    private String whCode;

    public String getScanContent() {
        return scanContent;
    }

    public void setScanContent(String scanContent) {
        this.scanContent = scanContent;
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }
}
