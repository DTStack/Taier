package com.dtstack.taier.pluginapi.pojo;

public class FileResult {

    private String name;

    private String path;

    private Long modificationTime;

    private Long blockSize;

    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    @Override
    public String toString() {
        return "FileResult{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", modificationTime=" + modificationTime +
                ", blockSize=" + blockSize +
                '}';
    }
}
