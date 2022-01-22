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

package com.dtstack.taiga.develop.service.task;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taiga.dao.domain.TaskParamTemplate;
import com.dtstack.taiga.dao.mapper.TaskParamTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2020/9/29 4:38 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class TaskParamTemplateService {

    @Autowired
    private TaskParamTemplateMapper taskParamTemplateMapper;

    public TaskParamTemplate getTaskParamTemplate(String version, Integer taskType) {
      return taskParamTemplateMapper.selectOne(Wrappers.lambdaQuery(TaskParamTemplate.class)
               .eq(TaskParamTemplate::getTaskType,taskType)
               .eq(StringUtils.isNotBlank(version),TaskParamTemplate::getTaskVersion,version));
    }
}
