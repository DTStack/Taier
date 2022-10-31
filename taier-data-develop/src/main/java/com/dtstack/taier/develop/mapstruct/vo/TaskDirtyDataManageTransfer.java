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

package com.dtstack.taier.develop.mapstruct.vo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.dao.domain.TaskDirtyDataManage;
import com.dtstack.taier.develop.vo.develop.query.TaskDirtyDataManageVO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author zhiChen
 * @date 2021/5/12 17:31
 */
@Mapper(builder = @Builder(disableBuilder = true), nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskDirtyDataManageTransfer {
    TaskDirtyDataManageTransfer INSTANCE = Mappers.getMapper(TaskDirtyDataManageTransfer.class);

    TaskDirtyDataManageVO taskDirtyDataManageToTaskDirtyDataManageVO(TaskDirtyDataManage dto);

    TaskDirtyDataManage taskDirtyDataManageVOToTaskDirtyDataManage(TaskDirtyDataManageVO dto);

    default String linkInfoJsonToString(JSONObject src) {
        return src.toString();
    }

    default JSONObject linkInfoStringToJSON(String src) {
        return JSONObject.parseObject(src);
    }
}
