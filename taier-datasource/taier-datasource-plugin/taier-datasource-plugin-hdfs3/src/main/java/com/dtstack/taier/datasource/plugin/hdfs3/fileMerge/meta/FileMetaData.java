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

package com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.meta;


import com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.ECompressType;

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
