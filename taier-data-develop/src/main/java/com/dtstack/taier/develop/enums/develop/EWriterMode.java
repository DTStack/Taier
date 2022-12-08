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

package com.dtstack.taier.develop.enums.develop;

import com.dtstack.taier.common.enums.DataSourceTypeEnum;
import com.dtstack.taier.common.exception.TaierDefineException;

/**
 * 
 * @since 1.0.0
 */
public enum EWriterMode {

    FTP(DataSourceTypeEnum.FTP.getVal()) {
        @Override
        public String rewriterWriterMode(String frontendParam) {
            if ("replace".equals(frontendParam)) {
                return "overwrite";
            } else if ("insert".equals(frontendParam)){
                return "append";
            }
            throw new TaierDefineException("writer mode not found on the " + frontendParam + ", maybe you can try replace or insert");
        }
    },
    ;

    public abstract String rewriterWriterMode(String frontendParam);

    public static EWriterMode sourceType(Integer typeCode) {
        for (EWriterMode value : values()) {
            if (value.sourceType.equals(typeCode)) {
                return value;
            }
        }
        throw new TaierDefineException("unsupported data source type");
    }

    /**
     * data source type var
     * @see DataSourceTypeEnum
     */
    private final Integer sourceType;

    EWriterMode(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getSourceType() {
        return sourceType;
    }

}
