package com.dtstack.batch.vo;

import com.dtstack.batch.datamask.util.BinaryConversion;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncStatusLogInfoVO {
    /**
     * 读取记录数
     */
    private Long numRead;

    /**
     * 读取字节数
     */
    private Long byteRead;

    /**
     * 读取速率
     */
    private Long readDuration;

    /**
     * 写入记录数
     */
    private Long numWrite;

    /**
     * 写入字节数
     */
    private Long byteWrite;

    /**
     * 写入速率
     */
    private Long writeDuration;

    /**
     * 错误记录数
     */
    @JsonProperty("nErrors")
    private Long nErrors;


    public String buildReadableLog() {
        StringBuffer sb = new StringBuffer();
        if (numRead == null) {
            numRead = 0L;
        }
        sb.append("读取记录数:\t").append(numRead).append("\n");
        if (byteRead == null) {
            byteRead = 0L;
        }
        sb.append("读取字节数:\t").append(byteRead).append("\n");
        if (readDuration == null) {
            readDuration = 0L;
        }
        sb.append("读取速率:\t").append(BinaryConversion.getPrintSize(getSpeed(byteRead, readDuration), true)).append("\n");
        if (numWrite == null) {
            numWrite = 0L;
        }
        sb.append("写入记录数:\t").append(numWrite).append("\n");
        if (byteWrite == null) {
            byteWrite = 0L;
        }
        sb.append("写入字节数:\t").append(byteWrite).append("\n");

        if (writeDuration == null) {
            writeDuration = 0L;
        }
        sb.append("写入速率:\t").append(BinaryConversion.getPrintSize(getLongValue(getSpeed(byteWrite, writeDuration)), true)).append("\n");
        if (nErrors == null) {
            nErrors = 0L;
        }
        sb.append("错误记录数:\t").append(nErrors).append("\n");
        return sb.toString();
    }

    private long getSpeed(long bytes, long duration) {
        if (duration == 0L) {
            return 0L;
        }

        if (duration < 1000) {
            return bytes;
        }

        return bytes / (duration / 1000);
    }

    private long getLongValue(Object obj) {
        if (obj == null) {
            return 0L;
        }

        return Long.valueOf(obj.toString());
    }
}
