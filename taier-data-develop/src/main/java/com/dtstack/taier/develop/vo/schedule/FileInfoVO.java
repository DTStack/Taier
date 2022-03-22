package com.dtstack.taier.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

public class FileInfoVO {

    @ApiModelProperty(value = "文件路径")
    private String path;

    @ApiModelProperty(value = "修改时间")
    private Long modificationTime;

    @ApiModelProperty(value = "文件大小")
    private Long blockSize;

    @ApiModelProperty(value = "所有者")
    private String owner;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Long modificationTime) {
        this.modificationTime = modificationTime;
    }

    public Long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
