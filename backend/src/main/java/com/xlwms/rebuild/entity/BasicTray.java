package com.xlwms.rebuild.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 托盘实体，对应表 basic_tray。字段、占用状态与接口路径均为本项目占位（真实接口未抓取）。 */
@TableName("basic_tray")
public class BasicTray {
    @TableId
    private Long id;
    private String trayNo;
    private String whCode;
    private String areaCode;
    private String cellCode;
    private Integer status;
    private String statusName;
    private Integer skuKinds;
    private Integer totalQty;
    private String bindTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTrayNo() { return trayNo; }
    public void setTrayNo(String trayNo) { this.trayNo = trayNo; }
    public String getWhCode() { return whCode; }
    public void setWhCode(String whCode) { this.whCode = whCode; }
    public String getAreaCode() { return areaCode; }
    public void setAreaCode(String areaCode) { this.areaCode = areaCode; }
    public String getCellCode() { return cellCode; }
    public void setCellCode(String cellCode) { this.cellCode = cellCode; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public Integer getSkuKinds() { return skuKinds; }
    public void setSkuKinds(Integer skuKinds) { this.skuKinds = skuKinds; }
    public Integer getTotalQty() { return totalQty; }
    public void setTotalQty(Integer totalQty) { this.totalQty = totalQty; }
    public String getBindTime() { return bindTime; }
    public void setBindTime(String bindTime) { this.bindTime = bindTime; }
}
