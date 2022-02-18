/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.dto.devlop;

import com.dtstack.taier.develop.common.convert.BinaryConversion;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public Long getNumRead() {
        return numRead;
    }

    public void setNumRead(Long numRead) {
        this.numRead = numRead;
    }

    public Long getByteRead() {
        return byteRead;
    }

    public void setByteRead(Long byteRead) {
        this.byteRead = byteRead;
    }

    public Long getReadDuration() {
        return readDuration;
    }

    public void setReadDuration(Long readDuration) {
        this.readDuration = readDuration;
    }

    public Long getNumWrite() {
        return numWrite;
    }

    public void setNumWrite(Long numWrite) {
        this.numWrite = numWrite;
    }

    public Long getByteWrite() {
        return byteWrite;
    }

    public void setByteWrite(Long byteWrite) {
        this.byteWrite = byteWrite;
    }

    public Long getWriteDuration() {
        return writeDuration;
    }

    public void setWriteDuration(Long writeDuration) {
        this.writeDuration = writeDuration;
    }

    public Long getnErrors() {
        return nErrors;
    }

    public void setnErrors(Long nErrors) {
        this.nErrors = nErrors;
    }

}
