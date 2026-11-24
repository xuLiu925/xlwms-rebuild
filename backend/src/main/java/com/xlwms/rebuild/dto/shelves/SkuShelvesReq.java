package com.xlwms.rebuild.dto.shelves;

import java.util.List;

/**
 * 按 SKU 上架确认请求体。对应 PDA 接口 POST /gateway/wms/pda/shelf/v2/confirm。
 * 字段来自 APK 反编译的 com.lingxing.wms.data.model.bean.shalves.SkuShelvesReq。
 */
public class SkuShelvesReq {
    /** 客户编码 */
    private String customerCode;
    /** 上架单号 */
    private String putawayNo;
    /** 仓库编码 */
    private String whCode;
    /** 上架 SKU 明细 */
    private List<SkuShelvesItem> skuList;

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

    public List<SkuShelvesItem> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<SkuShelvesItem> skuList) {
        this.skuList = skuList;
    }

    /** 单条上架 SKU 明细。 */
    public static class SkuShelvesItem {
        /** 目标库位 */
        private String cellNo;
        /** 产品 SKU */
        private String productSku;
        /** 上架数量 */
        private int putawayQty;
        /** 生产日期 */
        private String productionTime;
        /** 到期日期 */
        private String expiryTime;

        public String getCellNo() {
            return cellNo;
        }

        public void setCellNo(String cellNo) {
            this.cellNo = cellNo;
        }

        public String getProductSku() {
            return productSku;
        }

        public void setProductSku(String productSku) {
            this.productSku = productSku;
        }

        public int getPutawayQty() {
            return putawayQty;
        }

        public void setPutawayQty(int putawayQty) {
            this.putawayQty = putawayQty;
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
    }
}
