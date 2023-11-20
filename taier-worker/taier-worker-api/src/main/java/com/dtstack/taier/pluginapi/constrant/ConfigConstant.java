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

package com.dtstack.taier.pluginapi.constrant;

import java.io.File;

/**
 * 常量
 * Date: 2018/1/19
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ConfigConstant {

    public static final String REQUEST_PREFIX = "/taier/api";


    public static final String CONSOLE = "CONSOLE";
    public static String SP = File.separator;
    public static final String SPLIT = "_";
    public static final String RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT = "default";

    /**
     * first clusterName，second queueName
     */
    public static final String DEFAULT_GROUP_NAME = String.join(SPLIT, RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT, RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT);

    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String LOCAL_KEYTAB_DIR_PARENT = USER_DIR + ConfigConstant.SP + "kerberos" + ConfigConstant.SP + "keytab";
    public static final String LOCAL_KRB5_MERGE_DIR_PARENT = USER_DIR + ConfigConstant.SP + "kerberos" + ConfigConstant.SP + "merge";
    public static final String MERGE_KRB5_NAME = "mergeKrb5.conf";
    public static final String MERGE_KRB5_CONTENT_KEY = "mergeKrbContent";


    public static final String VERSION = "version";
    public static final String VERSION_NAME = "versionName";
    public static final String CUSTOMER_PRIORITY_VAL = "job.priority";

    public static final String DATA_SOURCE_TYPE = "dataSourceType";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static final String SQL_CHECKPOINT_TIMEOUT = "sql.checkpoint.timeout";
    public static final String FLINK_CHECKPOINT_TIMEOUT = "flink.checkpoint.timeout";
    public static final Long DEFAULT_CHECKPOINT_TIMEOUT = 600000L;

    public static final String KERBEROS = "kerberos";

    public static final String REMOTE_DIR = "remoteDir";
    public static final String PRINCIPAL_FILE = "principalFile";
    public static final String KERBEROS_FILE_TIMESTAMP = "kerberosFileTimestamp";
    public static final String PRINCIPAL = "principal";
    public static final String KRB_NAME = "krbName";
    public static final String OPEN_KERBEROS = "openKerberos";
    public static final String JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
    public static final String KRB5_CONF = "krb5.conf";
    public static final String KEYTAB_SUFFIX = ".keytab";
    public static final String KERBEROS_CONFIG = "kerberosConfig";

    public static final String FS_DEFAULT = "fs.defaultFS";


    public static final String USER_DIR_UNZIP = System.getProperty("user.dir") + File.separator + "unzip";
    public static final String USER_DIR_DOWNLOAD = System.getProperty("user.dir") + File.separator + "download";


    /**
     * component_config other类型 key
     */
    public static final String TYPE_NAME_KEY = "typeName";
    public static final String TYPE_NAME = "typeName";
    public static final String MD5_SUM_KEY = "md5zip";


    public static final Long DEFAULT_CLUSTER_ID = -1L;

    public final static String CLUSTER = "cluster";
    public final static String QUEUE = "queue";
    public static final String DEPLOY_MODEL = "deployMode";
    public static final String CHECK_POINTS_DIR = "state.checkpoints.dir";
    public static final String SAVE_POINTS_DIR = "state.savepoints.dir";
    public static final String APP_TYPE = "--app-type";


    public static final String COMPONENT_PYTHON_2_BIN = "script.python2.path";

    public static final String COMPONENT_PYTHON_3_BIN = "script.python3.path";

    public static final String COMPONENT_EXECUTE_DIR = "execute.dir";

    public static final String DATAX_TASK_TEMP = "DataX.task.temp";

    public static final String DATAX_LOCAL_PATH = "DataX.local.path";

    public static final String DATAX_PYTHON_BIN = "DataX.python.path";


    public static final String SPARK_KERBEROS_REMOTE_KRB5 = "spark.kerberos.remotekrb5";

    public static final String SPARK_KERBEROS_REMOTE_KEYTAB = "spark.kerberos.remotekeytab";

    public static final String SPARK_HADOOP_CONF_REMOTE_DIR = "spark.hadoopconf.remotedir";

}
