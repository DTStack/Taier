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

package com.dtstack.taiga.develop.utils.develop.sync.handler;

import com.alibaba.fastjson.JSON;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taiga.develop.common.template.Reader;
import com.dtstack.taiga.develop.common.template.Writer;

import java.util.List;
import java.util.Map;

/**
 * Date: 2019/12/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public interface SyncBuilder {


    void setReaderJson(Map<String, Object> map, Map<String, Object> dataSource,Map<String,Object> kerberos);

    void setWriterJson(Map<String, Object> map, Map<String, Object> dataSource,Map<String,Object> kerberos);

    Reader syncReaderBuild(Map<String, Object> sourceMap, List<Long> sourceIds);

    Writer syncWriterBuild(List<Long> targetIds, Map<String, Object> targetMap, Reader reader);

    DataSourceType getDataSourceType();

    default  <T> T objToObject(Object params,Class<T> clazz)  {
        if(params ==null) {return null;}
        return  JSON.parseObject(JSON.toJSONString(params), clazz);
    }
}
