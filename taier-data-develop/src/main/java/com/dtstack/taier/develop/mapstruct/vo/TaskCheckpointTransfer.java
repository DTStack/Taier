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

import com.dtstack.taier.develop.dto.devlop.CheckPointTimeRangeResultDTO;
import com.dtstack.taier.develop.dto.devlop.StreamTaskCheckpointVO;
import com.dtstack.taier.develop.vo.develop.result.GetCheckPointTimeRangeResultVO;
import com.dtstack.taier.develop.vo.develop.result.GetCheckpointListResultVO;
import com.dtstack.taier.develop.vo.develop.result.GetSavePointResultVO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:55 2021/1/6
 * @Description：数据源信息转化
 */
@Mapper(builder = @Builder(disableBuilder = true), nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TaskCheckpointTransfer {
    TaskCheckpointTransfer INSTANCE = Mappers.getMapper(TaskCheckpointTransfer.class);

    GetSavePointResultVO getSavePointResult(StreamTaskCheckpointVO savePoint);

    GetCheckPointTimeRangeResultVO getCheckPointTimeRangeResult(CheckPointTimeRangeResultDTO dto);
}
