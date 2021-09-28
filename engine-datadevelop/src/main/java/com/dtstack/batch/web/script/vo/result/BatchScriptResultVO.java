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

package com.dtstack.batch.web.script.vo.result;

import com.dtstack.batch.web.script.vo.query.BatchReadWriteLockBaseVO;
import com.dtstack.batch.web.user.vo.query.BatchUserBaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("脚本返回信息")
public class BatchScriptResultVO {

    @ApiModelProperty(value = "创建用户")
    private BatchUserBaseVO createUser;

    @ApiModelProperty(value = "修改用户")
    private BatchUserBaseVO modifyUser;

    @ApiModelProperty(value = "读写锁")
    private BatchReadWriteLockBaseVO readWriteLockVO;

    @ApiModelProperty(value = "锁版本", example = "0")
    private Integer lockVersion = 0;

    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;

    @ApiModelProperty(value = "强制更新", example = "false")
    private Boolean forceUpdate = false;

    @ApiModelProperty(value = "脚本名称", example = "脚本名称")
    private String name;

    @ApiModelProperty(value = "脚本描述", example = "脚本描述")
    private String scriptDesc;

    @ApiModelProperty(value = "父文件夹ID", example = "1")
    private Long nodePid;

    @ApiModelProperty(value = "创建者用户ID", example = "1")
    private Long createUserId;

    @ApiModelProperty(value = "修改者用户id", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "脚本类型,0-sql,1-python,2-shell", example = "1")
    private Integer type;

    @ApiModelProperty(value = "脚本内容", example = "1")
    private String scriptText;

    @ApiModelProperty(value = "脚本环境参数", example = "##")
    private String taskParams;

    @ApiModelProperty(value = "脚本版本号", example = "1")
    private Integer version;

    @ApiModelProperty(value = "租户id", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "项目id", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", example = "1")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;
}
