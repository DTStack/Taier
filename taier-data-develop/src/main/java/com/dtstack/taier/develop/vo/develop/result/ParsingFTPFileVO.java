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

package com.dtstack.taier.develop.vo.develop.result;

import com.dtstack.taier.develop.service.template.ftp.FTPColumn;

import java.util.List;

/**
 * parsing ftp files
 * 
 * @since 1.3.1
 */
public class ParsingFTPFileVO {

    private List<FTPColumn> column;

    private String filetype;

    public List<FTPColumn> getColumn() {
        return column;
    }

    public void setColumn(List<FTPColumn> column) {
        this.column = column;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    @Override
    public String toString() {
        return "ParsingFTPFileVO{" +
                "column=" + column +
                ", fileType='" + filetype + '\'' +
                '}';
    }
}
