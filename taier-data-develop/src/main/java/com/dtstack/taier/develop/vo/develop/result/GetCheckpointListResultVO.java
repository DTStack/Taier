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

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhiChen
 * @date 2021/1/7 20:13
 * @see TaskCheckpointTransfer
 */
public class GetCheckpointListResultVO {

    @ApiModelProperty(value = "间隔符", example = "_")
    private static String SPLIT = "_";

    @ApiModelProperty(value = "ID", example = "rdos_stream_task_checkpoint的记录id + checkpoint内容的具体id")
    private String id;

    @ApiModelProperty(value = "时间", example = "213")
    private Long time;

    /**其实应该服务器重新查询,太麻烦了暂时直接让客户端回传*/
    @ApiModelProperty(value = "外部路径", example = "213")
    private String externalPath;

}
