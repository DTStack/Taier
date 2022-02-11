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


package com.dtstack.taiga.common.util;

import java.io.File;


/**
 *
 * @author sishu.yss
 *
 */
public class SystemPropertyUtil {

    public static void setSystemUserDir() {
        String dir = System.getProperty("user.dir");
        String conf = String.format("%s/%s", new Object[]{dir, "conf"});
        File file = new File(conf);
        if(!file.exists()) {
            dir = dir.substring(0, dir.lastIndexOf("/"));
            conf = String.format("%s/%s", new Object[]{dir, "conf"});
            file = new File(conf);
            if(file.exists()) {
                System.setProperty("user.dir", dir);
            }
        }
        System.setProperty("user.dir.conf", System.getProperty("user.dir") + "/conf");

    }

    public static void setHadoopUserName(String userName) {
        System.setProperty("HADOOP_USER_NAME", userName);
    }
}
