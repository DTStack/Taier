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

package com.dtstack.taier.develop.utils.develop.hive.util;

/**
 * Date: 2020/3/23
 * Company: www.dtstack.com
 * @Description 默认计算引擎使用spark。查询表元数据信息使用spark thriftServer
 * @author xiaochen
 */
public class SparkThriftConnectionUtils {

    public enum HiveVersion{

        /**
         * hive_1.x
         */
        HIVE_1x("1.x"),

        /**
         * hive_1.x
         */
        HIVE_2x("2.x"),

        /**
         * hive_3.x
         */
        HIVE_3x("3.x");
        private String version;

        public String getVersion() {
            return version;
        }

        HiveVersion(String version) {
            this.version = version;
        }

        public static HiveVersion getByVersion(String versionStr){
            for (HiveVersion version:values()){
                if (version.getVersion().equalsIgnoreCase(versionStr)){
                    return version;
                }
            }
            return null;
        }
    }

}
