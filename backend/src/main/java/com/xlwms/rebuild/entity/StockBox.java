package com.xlwms.rebuild.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 按箱库存实体，对应表 stock_box。字段与接口路径均为本项目占位（真实接口未抓取）。 */
@TableName("stock_box")
public class StockBox {
    @TableId
    private Long id;
    private String boxNo;
    private String sku;
    private String productName;
    private String customerCode;
    private String customerName;
    private String cellCode;
    private String boxSpec;
    private String stockAttr;
    private Integer qty;
    private String shelfTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBoxNo() { return boxNo; }
    public void setBoxNo(String boxNo) { this.boxNo = boxNo; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCellCode() { return cellCode; }
    public void setCellCode(String cellCode) { this.cellCode = cellCode; }
    public String getBoxSpec() { return boxSpec; }
    public void setBoxSpec(String boxSpec) { this.boxSpec = boxSpec; }
    public String getStockAttr() { return stockAttr; }
    public void setStockAttr(String stockAttr) { this.stockAttr = stockAttr; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    public String getShelfTime() { return shelfTime; }
    public void setShelfTime(String shelfTime) { this.shelfTime = shelfTime; }
}
