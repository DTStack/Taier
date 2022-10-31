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

package com.dtstack.taier.datasource.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * <p>HDFS文件内容摘要</>
 * 包括包括文件的数量，文件夹的数量，以及这个文件夹的大小等内容
 *
 * @author ：wangchuan
 * date：Created in 11:39 上午 2020/12/11
 * company: www.dtstack.com
 */
@Slf4j
@Data
@Builder
public class HDFSContentSummary implements Serializable {

    // 文件数量
    private Long fileCount;

    // 文件夹数量
    private Long directoryCount;

    // 占用存储
    private Long spaceConsumed;

    // 文件(夹)更新时间
    private Long ModifyTime;

    // 路径是否存在
    private Boolean isExists;
}
