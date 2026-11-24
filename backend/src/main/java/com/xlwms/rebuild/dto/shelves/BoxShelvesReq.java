package com.xlwms.rebuild.dto.shelves;

import java.util.List;

/**
 * 整箱上架请求体。对应 PDA 接口 POST /gateway/wms/pda/shelf/v2/box/submitShelf。
 * 字段来自 APK 反编译的 com.lingxing.wms.data.model.bean.shalves.BoxShelvesReq。
 */
public class BoxShelvesReq {
    /** 客户编码 */
    private String customerCode;
    /** 上架单号 */
    private String putawayNo;
    /** 仓库编码 */
    private String whCode;
    /** 整箱上架明细（箱码 + 目标库位） */
    private List<BoxShelvesItem> boxBarcodeList;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getPutawayNo() {
        return putawayNo;
    }

    public void setPutawayNo(String putawayNo) {
        this.putawayNo = putawayNo;
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }

    public List<BoxShelvesItem> getBoxBarcodeList() {
        return boxBarcodeList;
    }

    public void setBoxBarcodeList(List<BoxShelvesItem> boxBarcodeList) {
        this.boxBarcodeList = boxBarcodeList;
    }

    /** 单条整箱上架明细。 */
    public static class BoxShelvesItem {
        /** 箱码 */
        private String boxBarcode;
        /** 目标库位 */
        private String cellNo;

        public String getBoxBarcode() {
            return boxBarcode;
        }

        public void setBoxBarcode(String boxBarcode) {
            this.boxBarcode = boxBarcode;
        }

        public String getCellNo() {
            return cellNo;
        }

        public void setCellNo(String cellNo) {
            this.cellNo = cellNo;
        }
    }
}
