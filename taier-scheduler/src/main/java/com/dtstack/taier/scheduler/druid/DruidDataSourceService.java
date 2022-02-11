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

package com.dtstack.taier.scheduler.druid;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 修改druid数据源的自动释放连接
 * @author xinge
 */
@Component
public class DruidDataSourceService {

    private final boolean isDruidDataSource;

    private final DruidDataSource druidDataSource;


    public DruidDataSourceService(DataSource dataSource){
        if (dataSource instanceof DruidDataSource){
            isDruidDataSource = true;
            druidDataSource = (DruidDataSource) dataSource;
            return;
        }
        isDruidDataSource = false;
        druidDataSource = null;
    }

    /**
     * 禁止druid数据源后台定时任务释放长期连接
     * isDruidDataSource 为 false 时 druidDataSource必然不能为空
     */
    public void forbidRemoveAbandoned(){
        if (!isDruidDataSource || !druidDataSource.isRemoveAbandoned()){
            return;
        }
        // 指定的状态
        druidDataSource.setRemoveAbandoned(false);
    }

    /**
     * 恢复数据库默认状态
     */
    public void releaseRemoveAbandoned(){
        if (!isDruidDataSource || ! druidDataSource.isRemoveAbandoned()){
            return;
        }
        druidDataSource.setRemoveAbandoned(true);
    }


}
