package com.xlwms.rebuild.dto.receipt;

import java.util.List;

/**
 * 按 SKU 预报收货请求体。对应 PDA 接口 POST /gateway/wms/pda/receipt/skuReceipt。
 * 结构来自 APK 反编译的 com.lingxing.wms.data.model.bean.SkuForecastReceiptReq
 * （含内部类 Sku / SkuPallet / PalletSku）。
 */
public class SkuForecastReceiptReq {
    /** 仓库编码 */
    private String whCode;
    /** 客户编码 */
    private String customerCode;
    /** 来源单号 */
    private String sourceNo;
    /** ASN 单号 */
    private String asnNo;
    /** 乐观锁版本号 */
    private String rowVersion;
    /** 非托盘方式收货的 SKU 明细 */
    private List<Sku> skuList;
    /** 按托盘收货时的托盘明细 */
    private List<SkuPallet> skuPalletList;

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

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }

    public List<SkuPallet> getSkuPalletList() {
        return skuPalletList;
    }

    public void setSkuPalletList(List<SkuPallet> skuPalletList) {
        this.skuPalletList = skuPalletList;
    }

    /** 单条 SKU 收货明细。 */
    public static class Sku {
        /** 产品 SKU */
        private String productSku;
        /** 收货数量 */
        private int receiptQty;
        /** 生产日期 */
        private String productionTime;
        /** 到期日期 */
        private String expiryTime;

        public String getProductSku() {
            return productSku;
        }

        public void setProductSku(String productSku) {
            this.productSku = productSku;
        }

        public int getReceiptQty() {
            return receiptQty;
        }

        public void setReceiptQty(int receiptQty) {
            this.receiptQty = receiptQty;
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

    /** 托盘明细；Bs 结尾字段为英制单位（basic/secondary）尺寸。 */
    public static class SkuPallet {
        /** 托盘编码 */
        private String palletCode;
        /** 托盘上的 SKU 明细 */
        private List<PalletSku> skuList;
        private double palletLength;
        private double palletLengthBs;
        private double palletWidth;
        private double palletWidthBs;
        private double palletHeight;
        private double palletHeightBs;
        private double palletWeight;
        private double palletWeightBs;
        private String remark;

        public String getPalletCode() {
            return palletCode;
        }

        public void setPalletCode(String palletCode) {
            this.palletCode = palletCode;
        }

        public List<PalletSku> getSkuList() {
            return skuList;
        }

        public void setSkuList(List<PalletSku> skuList) {
            this.skuList = skuList;
        }

        public double getPalletLength() {
            return palletLength;
        }

        public void setPalletLength(double palletLength) {
            this.palletLength = palletLength;
        }

        public double getPalletLengthBs() {
            return palletLengthBs;
        }

        public void setPalletLengthBs(double palletLengthBs) {
            this.palletLengthBs = palletLengthBs;
        }

        public double getPalletWidth() {
            return palletWidth;
        }

        public void setPalletWidth(double palletWidth) {
            this.palletWidth = palletWidth;
        }

        public double getPalletWidthBs() {
            return palletWidthBs;
        }

        public void setPalletWidthBs(double palletWidthBs) {
            this.palletWidthBs = palletWidthBs;
        }

        public double getPalletHeight() {
            return palletHeight;
        }

        public void setPalletHeight(double palletHeight) {
            this.palletHeight = palletHeight;
        }

        public double getPalletHeightBs() {
            return palletHeightBs;
        }

        public void setPalletHeightBs(double palletHeightBs) {
            this.palletHeightBs = palletHeightBs;
        }

        public double getPalletWeight() {
            return palletWeight;
        }

        public void setPalletWeight(double palletWeight) {
            this.palletWeight = palletWeight;
        }

        public double getPalletWeightBs() {
            return palletWeightBs;
        }

        public void setPalletWeightBs(double palletWeightBs) {
            this.palletWeightBs = palletWeightBs;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

    /** 托盘上的单条 SKU 明细。 */
    public static class PalletSku {
        private String productSku;
        private int receiptQty;
        private String productionTime;
        private String expiryTime;

        public String getProductSku() {
            return productSku;
        }

        public void setProductSku(String productSku) {
            this.productSku = productSku;
        }

        public int getReceiptQty() {
            return receiptQty;
        }

        public void setReceiptQty(int receiptQty) {
            this.receiptQty = receiptQty;
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
