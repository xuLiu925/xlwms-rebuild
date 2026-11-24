package com.xlwms.rebuild.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 库位实体，对应表 basic_cell。字段、库位类型、占用状态与接口路径均为本项目占位（真实接口未抓取）。 */
@TableName("basic_cell")
public class BasicCell {
    @TableId
    private Long id;
    private String cellCode;
    private String areaCode;
    private String areaName;
    private String whCode;
    private String cellType;
    private Integer usageStatus;
    private String usageName;
    private String createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCellCode() { return cellCode; }
    public void setCellCode(String cellCode) { this.cellCode = cellCode; }
    public String getAreaCode() { return areaCode; }
    public void setAreaCode(String areaCode) { this.areaCode = areaCode; }
    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public String getWhCode() { return whCode; }
    public void setWhCode(String whCode) { this.whCode = whCode; }
    public String getCellType() { return cellType; }
    public void setCellType(String cellType) { this.cellType = cellType; }
    public Integer getUsageStatus() { return usageStatus; }
    public void setUsageStatus(Integer usageStatus) { this.usageStatus = usageStatus; }
    public String getUsageName() { return usageName; }
    public void setUsageName(String usageName) { this.usageName = usageName; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
