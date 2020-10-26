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


package com.dtstack.engine.sql.utils;

import com.dtstack.engine.sql.ColumnLineage;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangbo
 * @date 2019/6/12
 */
public class ColumnLineageUtil {

    /**
     * {
     *     "db.table":["col1","col2"]
     * }
     */
    public static Map<String, List<String>> toTableColsMap(List<ColumnLineage> columnLineages){
        Map<String, List<String>> tableColsMap = new HashMap<>();

        if (CollectionUtils.isEmpty(columnLineages)) {
            return tableColsMap;
        }

        for (ColumnLineage columnLineage : columnLineages) {
            String fromDbTable = String.format("%s.%s", columnLineage.getFromDb(), columnLineage.getFromTable());
            if(tableColsMap.containsKey(fromDbTable)){
                tableColsMap.get(fromDbTable).add(columnLineage.getFromColumn());
            } else {
                List<String> cols = new ArrayList<>();
                cols.add(columnLineage.getFromColumn());
                tableColsMap.put(fromDbTable, cols);
            }

            String toDbTable = String.format("%s.%s", columnLineage.getToDb(), columnLineage.getToTable());
            if(tableColsMap.containsKey(toDbTable)){
                tableColsMap.get(toDbTable).add(columnLineage.getToColumn());
            } else {
                List<String> cols = new ArrayList<>();
                cols.add(columnLineage.getToColumn());
                tableColsMap.put(toDbTable, cols);
            }
        }

        return tableColsMap;
    }
}
