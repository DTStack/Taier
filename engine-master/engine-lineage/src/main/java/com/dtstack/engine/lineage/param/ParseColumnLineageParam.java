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

package com.dtstack.engine.lineage.param;

import com.dtstack.engine.pluginapi.pojo.Column;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author chener
 * @Classname ParseColumnLineageParam
 * @Description 查询字段血缘关系参数
 * @Date 2020/11/3 17:12
 * @Created chener@dtstack.com
 */
@ApiModel("查询字段血缘参数")
public class ParseColumnLineageParam extends ParseTableLineageParam{

    @ApiModelProperty("表字段map")
    private Map<String, List<Column>> tableColumnsMap;

    public Map<String, List<Column>> getTableColumnsMap() {
        return tableColumnsMap;
    }

    public void setTableColumnsMap(Map<String, List<Column>> tableColumnsMap) {
        this.tableColumnsMap = tableColumnsMap;
    }
}
