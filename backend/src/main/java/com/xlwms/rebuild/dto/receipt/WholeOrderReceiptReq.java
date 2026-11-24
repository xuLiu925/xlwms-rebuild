package com.xlwms.rebuild.dto.receipt;

/**
 * 整单收货请求体。对应 PDA 接口 POST /gateway/wms/pda/receipt/wholeOrderReceipt。
 * 字段来自 APK 反编译的 com.lingxing.wms.data.model.bean.WholeOrderReceiptReq（见 docs/PDA_REQUEST_BODIES.md）。
 */
public class WholeOrderReceiptReq {
    /** 仓库编码 */
    private String whCode;
    /** 客户编码 */
    private String customerCode;
    /** 来源单号 */
    private String sourceNo;
    /** ASN 单号 */
    private String asnNo;
    /** 乐观锁版本号，提交时回传 */
    private String rowVersion;

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public String getAsnNo() {
        return asnNo;
    }

    public void setAsnNo(String asnNo) {
        this.asnNo = asnNo;
    }

    public String getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(String rowVersion) {
        this.rowVersion = rowVersion;
    }
}
