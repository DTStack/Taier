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

package com.dtstack.taier.sparkyarn.sparkyarn.enums;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by sishu.yss on 2018/6/27.
 */
public class ExceptionInfoConstrant {

    private static Logger logger = LoggerFactory.getLogger(ExceptionInfoConstrant.class);

    private static List<String> needRestartExceptions = Lists.newArrayList();

    static {
        try{
            Field[] fields = ExceptionInfoConstrant.class.getDeclaredFields();
            for(Field f:fields){
                String name = f.getName();
                if(name.indexOf("RESTART_EXCEPTION")>=0){
                    needRestartExceptions.add(f.get(f.getName()).toString());
                }
            }
        }catch (Throwable e){
            logger.error("",e);
        }
    }

}
