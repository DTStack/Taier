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

package com.dtstack.taier.datasource.plugin.hdfs3.fileMerge.core;

import com.dtstack.taier.datasource.api.enums.FileFormat;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class CombineMergeBuilder {

    /**
     * 需要合并的文件目录 源路径
     */
    private String sourcePath;

    /**
     * 移动的目录文件
     */
    private String mergedPath;

    /**
     * 需要合并的文件的阈值
     * 只有低于此大小的文件才会被合并
     */
    private Long needCombineFileSizeLimit = 50 * 1024 * 1024L;

    /**
     * 文件类型
     */
    private FileFormat fileFormat;

    /**
     * hadoop配置文件
     */
    private Configuration configuration;


    /**
     * 合并后的文件的最大值
     */
    private long maxCombinedFileSize = 125 * 1024 * 1024L;


    public CombineMergeBuilder() {
    }

    public CombineMergeBuilder sourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
        return this;
    }

    public CombineMergeBuilder mergedPath(String mergedPath) {
        this.mergedPath = mergedPath;
        return this;
    }

    public CombineMergeBuilder needCombineFileSizeLimit(long needCombineFileSizeLimit) {
        this.needCombineFileSizeLimit = needCombineFileSizeLimit;
        return this;
    }

    public CombineMergeBuilder maxCombinedFileSize(long maxCombinedFileSize) {
        this.maxCombinedFileSize = maxCombinedFileSize;
        return this;
    }

    public CombineMergeBuilder fileType(FileFormat fileFormat) {
        this.fileFormat = fileFormat;
        return this;
    }


    public CombineMergeBuilder configuration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    public CombineServer build() throws IOException {
        check();

        CombineServer combine = FileCombineFactory.getCombine(new Path(sourcePath), configuration, fileFormat);
        combine.setSourcePath(new Path(sourcePath));
        combine.setMergedTempPath(new Path(mergedPath));
        combine.setConfiguration(configuration);
        combine.setNeedCombineFileSizeLimit(needCombineFileSizeLimit);
        combine.setMaxCombinedFileSize(maxCombinedFileSize);
        combine.setFs(FileSystem.get(configuration));

        log.info("combine info {}",combine);
        return combine;
    }

    private void check() {
        StringBuilder errorInfo = new StringBuilder(128);
        if (StringUtils.isBlank(sourcePath)) {
            errorInfo.append("sourcePath not null").append("\n");
        }

        if (StringUtils.isBlank(mergedPath)) {
            errorInfo.append("mergedPath not null").append("\n");
        }

        if (Objects.isNull(fileFormat)) {
            errorInfo.append("fileType not null,it support orc,parquet,text").append("\n");
        }

        if (Objects.isNull(configuration)) {
            errorInfo.append("configuration not null").append("\n");
        }

        if (maxCombinedFileSize <= needCombineFileSizeLimit) {
            errorInfo.append("maxCombinedFileSize not allow less than needCombineFileSizeLimit").append("\n");
        }

        if (errorInfo.length() > 0) {
            throw new SourceException(errorInfo.toString());
        }
    }
}
