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

package com.dtstack.taier.develop.service.develop;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/12
 */
public interface IJdbcService {

    /**
     * 执行查询，带前缀信息
     *
     * @param taskParam
     * @return
     */
    List<List<Object>> executeQuery(ISourceDTO sourceDTO,List<String> sqls, String taskParam,Integer limit);

    /**
     *  执行sql 忽略查询结果
     * @return
     */
    Boolean executeQueryWithoutResult(ISourceDTO sourceDTO, String sql);

    /**
     * 获取所有的databases
     *
     * @return
     */
    List<String> getAllDataBases(ISourceDTO sourceDTO);

}
