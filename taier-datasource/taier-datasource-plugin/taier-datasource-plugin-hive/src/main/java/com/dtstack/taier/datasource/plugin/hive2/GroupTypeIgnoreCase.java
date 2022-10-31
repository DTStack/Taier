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

package com.dtstack.taier.datasource.plugin.hive2;

import org.apache.parquet.io.InvalidRecordException;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 20:33 2020/9/1
 * @Description：Parquet 组信息
 */
public class GroupTypeIgnoreCase {
    private List<Type> fields;
    private Map<String, Integer> indexByName;

    public GroupTypeIgnoreCase(GroupType groupType) {
        this.fields = groupType.getFields();
        this.indexByName = new HashMap();

        for(int i = 0; i < this.fields.size(); ++i) {
            this.indexByName.put((this.fields.get(i)).getName().toLowerCase(), i);
        }

    }

    public boolean containsField(String name) {
        return this.indexByName.containsKey(name.toLowerCase());
    }

    public int getFieldIndex(String name) {
        name = name.toLowerCase();
        if (!this.indexByName.containsKey(name)) {
            throw new InvalidRecordException(name + " not found in " + this);
        } else {
            return (Integer)this.indexByName.get(name);
        }
    }
}
