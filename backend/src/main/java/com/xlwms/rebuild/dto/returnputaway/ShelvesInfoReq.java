package com.xlwms.rebuild.dto.returnputaway;

import java.util.List;

/**
 * 退货上架提交请求体。对应 PDA 接口 POST /gateway/wms/pda/return/putaway/submit。
 * 字段来自 APK 反编译的 com.lingxing.wms.data.model.bean.shalves.ShelvesInfoReq。
 */
public class ShelvesInfoReq {
    /** 条码 */
    private String barcode;
    /** 库位 */
    private String cellNo;
    /** 客户编码 */
    private String customerCode;
    /** 预报数量 */
    private Integer forecastQty;
    /** 处理附件（凭证图片等） */
    private List<Appendix> handleAppendixList;
    /** 处理备注 */
    private String handleRemark;
    /** 商品名称 */
    private String productName;
    /** 品质（良品/次品等，枚举值由后端定义） */
    private Integer productQuality;
    /** 按批次（生产日期/到期日）拆分的上架明细 */
    private List<ShelvesSubmit> putawayExpirationSubmitDTOList;
    /** 本次上架数量 */
    private int qty;
    /** 退货单号 */
    private String returnNo;
    /** SKU ID */
    private String skuId;
    /** 待上架数量 */
    private int waitPutawayQty;
    /** 仓库编码 */
    private String whCode;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCellNo() {
        return cellNo;
    }

    public void setCellNo(String cellNo) {
        this.cellNo = cellNo;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public Integer getForecastQty() {
        return forecastQty;
    }

    public void setForecastQty(Integer forecastQty) {
        this.forecastQty = forecastQty;
    }

    public List<Appendix> getHandleAppendixList() {
        return handleAppendixList;
    }

    public void setHandleAppendixList(List<Appendix> handleAppendixList) {
        this.handleAppendixList = handleAppendixList;
    }

    public String getHandleRemark() {
        return handleRemark;
    }

    public void setHandleRemark(String handleRemark) {
        this.handleRemark = handleRemark;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductQuality() {
        return productQuality;
    }

    public void setProductQuality(Integer productQuality) {
        this.productQuality = productQuality;
    }

    public List<ShelvesSubmit> getPutawayExpirationSubmitDTOList() {
        return putawayExpirationSubmitDTOList;
    }

    public void setPutawayExpirationSubmitDTOList(List<ShelvesSubmit> putawayExpirationSubmitDTOList) {
        this.putawayExpirationSubmitDTOList = putawayExpirationSubmitDTOList;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getReturnNo() {
        return returnNo;
    }

    public void setReturnNo(String returnNo) {
        this.returnNo = returnNo;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public int getWaitPutawayQty() {
        return waitPutawayQty;
    }

    public void setWaitPutawayQty(int waitPutawayQty) {
        this.waitPutawayQty = waitPutawayQty;
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }

    /** 按批次拆分的上架明细（生产日期 / 到期日 / 数量 / 库位）。 */
    public static class ShelvesSubmit {
        private String cellNo;
        private String productionTime;
        private String expiryTime;
        private int qty;

        public String getCellNo() {
            return cellNo;
        }

        public void setCellNo(String cellNo) {
            this.cellNo = cellNo;
        }

        public String getProductionTime() {
            return productionTime;
        }

        public void setProductionTime(String productionTime) {
            this.productionTime = productionTime;
        }

        public String getExpiryTime() {
            return expiryTime;
        }

        public void setExpiryTime(String expiryTime) {
            this.expiryTime = expiryTime;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }
    }
}
