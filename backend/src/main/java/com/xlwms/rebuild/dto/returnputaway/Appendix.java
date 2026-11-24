package com.xlwms.rebuild.dto.returnputaway;

/**
 * 业务附件（图片/文件）条目。来自 APK 反编译的 com.lingxing.wms.data.model.bean.Appendix。
 * 退货上架/销毁提交时用于回传处理凭证。
 */
public class Appendix {
    private int bizAppendixType;
    private String bizAppendixTypeName;
    private String bizCode;
    private String bizName;
    private String bizNo;
    private String customerCode;
    private String fileExt;
    private String fileKey;
    private String fileName;
    private String filePath;
    private String fileSize;
    private String originName;
    private String remark;
    private String tenantCode;
    private String uploadTime;
    private String url;
    private String whCode;

    public int getBizAppendixType() {
        return bizAppendixType;
    }

    public void setBizAppendixType(int bizAppendixType) {
        this.bizAppendixType = bizAppendixType;
    }

    public String getBizAppendixTypeName() {
        return bizAppendixTypeName;
    }

    public void setBizAppendixTypeName(String bizAppendixTypeName) {
        this.bizAppendixTypeName = bizAppendixTypeName;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }
}
