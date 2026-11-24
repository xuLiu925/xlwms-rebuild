package com.xlwms.rebuild.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 库区实体，对应表 basic_area。字段、库区类型与接口路径均为本项目占位（真实接口未抓取）。 */
@TableName("basic_area")
public class BasicArea {
    @TableId
    private Long id;
    private String areaCode;
    private String areaName;
    private String whCode;
    private String areaType;
    private Integer cellCount;
    private Integer status;
    private String statusName;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAreaCode() { return areaCode; }
    public void setAreaCode(String areaCode) { this.areaCode = areaCode; }
    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public String getWhCode() { return whCode; }
    public void setWhCode(String whCode) { this.whCode = whCode; }
    public String getAreaType() { return areaType; }
    public void setAreaType(String areaType) { this.areaType = areaType; }
    public Integer getCellCount() { return cellCount; }
    public void setCellCount(Integer cellCount) { this.cellCount = cellCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
