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

package com.dtstack.taier.develop.service.develop.impl;

import com.dtstack.taier.dao.domain.DevelopSysParameter;
import com.dtstack.taier.dao.mapper.DevelopSysParamMapper;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 系统参数,用于离线任务中的参数替换
 * eg:${bdp.system.bizdate}：yyyyMMdd-1
 * Date: 2017/6/7
 * Company: www.dtstack.com
 * @author xuchao
 */
@Service
public class DevelopSysParamService {

    @Autowired
    private DevelopSysParamMapper developSysParamDao;

    private Map<String, DevelopSysParameter> cache = null;

    public Collection<DevelopSysParameter> listSystemParam(){
        if (cache == null){
            loadSystemParam();
        }
        return cache.values();
    }

    public void loadSystemParam(){
        cache = Maps.newHashMap();
        List<DevelopSysParameter> sysParamList = developSysParamDao.listAll();
        for(DevelopSysParameter tmp : sysParamList){
            cache.put(tmp.getParamName(), tmp);
        }
    }

    public DevelopSysParameter getBatchSysParamByName(String name){

        if(cache == null){
            loadSystemParam();
        }

        return cache.get(name);
    }

}
