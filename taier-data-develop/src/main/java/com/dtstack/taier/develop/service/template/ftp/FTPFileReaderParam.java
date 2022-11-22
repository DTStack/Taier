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

package com.dtstack.taier.develop.service.template.ftp;

import com.dtstack.taier.common.enums.EFTPTaskFileType;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.util.FileUtil;
import com.dtstack.taier.common.util.StringUtils;
import com.dtstack.taier.develop.service.template.DaPluginParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ftp file data source parameters
 * @author bnyte
 * @since 1.3.1
 */
public class FTPFileReaderParam extends DaPluginParam {

    public static final List<String> EXCEL_FILE_TYPES = Stream.of(
            ".xls", ".xlsx"
    ).collect(Collectors.toList());

    public static final List<String> CSV = Stream.of(
            ".csv"
    ).collect(Collectors.toList());

    public static final List<String> TXT = Stream.of(
            ".txt"
    ).collect(Collectors.toList());

    /**
     * the ftp server path where the file is located
     */
    private String path;

    /**
     * is the header of the first line of the file
     */
    private Boolean isFirstLineHeader;

    /**
     * column separator
     */
    private String fieldDelimiter;

    /**
     * encoding format
     */
    private String encoding;

    /**
     * source data file type
     * currently only csv and Excel are supported see
     *  {@link <a href="https://github.com/DTStack/chunjun/blob/master/chunjun-connectors/chunjun-connector-ftp/src/main/java/com/dtstack/chunjun/connector/ftp/enums/FileType.java">ChunJun FTP supported</a>}
     */
    private String fileType;

    private List<FTPColumn> column;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getFirstLineHeader() {
        return isFirstLineHeader;
    }

    public void setFirstLineHeader(Boolean firstLineHeader) {
        isFirstLineHeader = firstLineHeader;
    }

    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public List<FTPColumn> getColumn() {
        return column;
    }

    public void setColumn(List<FTPColumn> column) {
        this.column = column;
    }
}
