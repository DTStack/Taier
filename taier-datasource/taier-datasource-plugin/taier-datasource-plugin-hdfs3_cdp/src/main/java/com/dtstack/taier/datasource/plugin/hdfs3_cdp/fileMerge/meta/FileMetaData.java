package com.dtstack.taier.datasource.plugin.hdfs3_cdp.fileMerge.meta;


import com.dtstack.taier.datasource.plugin.hdfs3_cdp.fileMerge.ECompressType;

/**
 * 文件的元信息
 */
public class FileMetaData {

    //是否压缩
    private boolean isCompressed;
    //压缩的格式
    private ECompressType eCompressType;
    //新文件的写入上限
    private long limitSize;

    public boolean isCompressed() {
        return isCompressed;
    }

    public void setCompressed(boolean compressed) {
        isCompressed = compressed;
    }

    public ECompressType geteCompressType() {
        return eCompressType;
    }

    public void seteCompressType(ECompressType eCompressType) {
        this.eCompressType = eCompressType;
    }

    public long getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(long limitSize) {
        this.limitSize = limitSize;
    }

    @Override
    public String toString() {
        return "FileMetaData{" +
                "isCompressed=" + isCompressed +
                ", eCompressType=" + eCompressType +
                ", limitSize=" + limitSize +
                '}';
    }
}
