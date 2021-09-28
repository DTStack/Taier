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

package com.dtstack.engine.lineage.adapter;

import com.dtstack.engine.common.enums.TableOperateEnum;
import com.dtstack.engine.lineage.vo.AlterResult;

/**
 * @Author: ZYD
 * Date: 2021/4/2 10:24
 * Description: alter解析结果转换器
 * @since 1.0.0
 */
public class AlterResultAdapter {


    public static AlterResult sqlAlterResult2ApiResult(com.dtstack.sqlparser.common.client.domain.AlterResult alterResult){

        if(null == alterResult){
            return null;
        }
        AlterResult aResult = new AlterResult();
        aResult.setNewDB(alterResult.getNewDB());
        aResult.setOldDB(alterResult.getOldDB());
        aResult.setNewTableName(alterResult.getNewTableName());
        aResult.setOldTableName(alterResult.getOldTableName());
        aResult.setNewLocation(alterResult.getNewLocation());
        aResult.setSerdeProperties(alterResult.getSerdeProperties());
        aResult.setTableProperties(alterResult.getTableProperties());
        aResult.setAlterType(TableOperateEnum.valueOf(alterResult.getAlterType().name()));
        aResult.setNewLocationPart(alterResult.getNewLocationPart());
        return aResult;
    }

}
