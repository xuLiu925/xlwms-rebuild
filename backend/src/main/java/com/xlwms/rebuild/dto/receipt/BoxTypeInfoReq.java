package com.xlwms.rebuild.dto.receipt;

import java.util.List;

/**
 * 获取箱型信息请求体。对应 PDA 接口 POST /gateway/wms/pda/receipt/getBoxTypeInfo。
 * 字段来自 APK 反编译的 com.lingxing.wms.data.model.bean.BoxTypeInfoReq。
 * 原 boxTypeList 在 APK 中为 List&lt;? extends Object&gt;，此处用 List&lt;String&gt; 占位。
 */
public class BoxTypeInfoReq {
    /** ASN 单号 */
    private String asnNo;
    /** 箱型编码列表 */
    private List<String> boxTypeList;
    /** 客户编码 */
    private String customerCode;
    /** 仓库编码 */
    private String whCode;

    public String getAsnNo() {
        return asnNo;
    }

    public void setAsnNo(String asnNo) {
        this.asnNo = asnNo;
    }

    public List<String> getBoxTypeList() {
        return boxTypeList;
    }

    public void setBoxTypeList(List<String> boxTypeList) {
        this.boxTypeList = boxTypeList;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }
}
