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

package com.dtstack.taiga.develop.utils.develop.common.enums;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:30 2021/04/21
 * @Description：数据源插件类型枚举
 */
public enum StoredType {
    /**
     * LazySimpleSerDe
     */
    SEQUENCEFILE("sequence", "LazySimpleSerDe", "SequenceFileInputFormat", "HiveSequenceFileOutputFormat"),

    /**
     * Default, depending on hive.default.fileformat configuration
     */
    TEXTFILE("text", "LazySimpleSerDe", "TextInputFormat", "HiveIgnoreKeyTextOutputFormat"),

    /**
     * Note: Available in Hive 0.6.0 and later)
     */
    RCFILE("rc", "LazyBinaryColumnarSerDe", "RCFileInputFormat", "RCFileOutputFormat"),

    /**
     * Note: Available in Hive 0.11.0 and later
     */
    ORC("orc", "OrcSerde", "OrcInputFormat", "OrcOutputFormat"),

    /**
     * Note: Available in Hive 0.13.0 and later
     */
    PARQUET("parquet", "ParquetHiveSerDe", "MapredParquetInputFormat", "MapredParquetOutputFormat"),

    /**
     * Note: Available in Hive 0.14.0 and later)
     */
    AVRO("avro", "AvroSerDe", "AvroContainerInputFormat", "AvroContainerOutputFormat"),

    /**
     * KUDU
     */
    KUDU("kudu", "KUDU","KuduInputFormat","KuduOutputFormat"),

    /**
     * 未知存储类型
     */
    UN_KNOW_TYPE("unknown stored type","unknown stored type","unknown stored type","unknown stored type"),
    ;

    private String value;
    private String serde;
    private String inputFormatClass;
    private String outputFormatClass;

    StoredType(String value, String serde, String inputFormatClass, String outputFormatClass) {
        this.value = value;
        this.serde = serde;
        this.inputFormatClass = inputFormatClass;
        this.outputFormatClass = outputFormatClass;
    }

    public String getInputFormatClass() {
        return inputFormatClass;
    }

    public String getValue() {
        return value;
    }
}
