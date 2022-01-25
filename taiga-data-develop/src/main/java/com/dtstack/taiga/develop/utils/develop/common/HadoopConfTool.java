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

package com.dtstack.taiga.develop.utils.develop.common;


/**
 * 解析配置获取Hadoop配置
 * Date: 2018/5/3
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class HadoopConfTool {

    public static final String FS_DEFAULTFS = "fs.defaultFS";
    public final static String KEY_JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
    public final static String KEYTAB_PATH = "keytabPath";
    public final static String PRINCIPAL_FILE = "principalFile";
    public static final String IS_HADOOP_AUTHORIZATION = "hadoop.security.authorization";
    public static final String HADOOP_AUTH_TYPE = "hadoop.security.authentication";

}
