package com.dtstack.engine.api.dto;

import io.swagger.annotations.ApiModel;

@ApiModel
public class Resource {

    public Resource() {
    }

    public Resource(String fileName, String uploadedFileName, int size, String contentType, String key) {
        this.uploadedFileName = uploadedFileName;
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.key = key;
    }

    private String uploadedFileName;

    /**
     * @return the file name of the upload as provided in the form submission
     */
    private String fileName;

    /**
     * @return the size of the upload, in bytes
     */
    private int size;

    private String contentType;

    /**
     * file header key
     */
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}

