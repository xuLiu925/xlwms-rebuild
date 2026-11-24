package com.xlwms.rebuild.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 一件代发出库单实体，对应表 bl_delivery。status 编码为占位，真实编码未知。 */
@TableName("bl_delivery")
public class BlDelivery {
    @TableId
    private Long id;
    private String deliveryNo;
    private String customerCode;
    private String customerName;
    private String skuKind;
    private String channelName;
    private String carrier;
    private String countryCode;
    private String trackingNo;
    private Integer parcelQty;
    private String platform;
    private Integer status;
    private String statusName;
    private String waveNo;
    private Boolean labelPrinted;
    private String shipDeadline;
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryNo() {
        return deliveryNo;
    }

    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSkuKind() {
        return skuKind;
    }

    public void setSkuKind(String skuKind) {
        this.skuKind = skuKind;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public Integer getParcelQty() {
        return parcelQty;
    }

    public void setParcelQty(Integer parcelQty) {
        this.parcelQty = parcelQty;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getWaveNo() {
        return waveNo;
    }

    public void setWaveNo(String waveNo) {
        this.waveNo = waveNo;
    }

    public Boolean getLabelPrinted() {
        return labelPrinted;
    }

    public void setLabelPrinted(Boolean labelPrinted) {
        this.labelPrinted = labelPrinted;
    }

    public String getShipDeadline() {
        return shipDeadline;
    }

    public void setShipDeadline(String shipDeadline) {
        this.shipDeadline = shipDeadline;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
