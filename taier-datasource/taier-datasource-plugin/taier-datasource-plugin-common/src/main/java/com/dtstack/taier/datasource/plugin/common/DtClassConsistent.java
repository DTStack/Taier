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

package com.dtstack.taier.datasource.plugin.common;

import java.util.regex.Pattern;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 01:37 2020/2/27
 * @Description：常量
 */
public interface DtClassConsistent {
    class PublicConsistent {
        /**
         * 用户名
         */
        public static final String USER_NAME = "userName";

        /**
         * Driver Properties 用户信息
         */
        public static final String USER = "user";

        /**
         * Driver Properties 密码信息
         */
        public static final String PASSWORD = "password";

        /**
         * 密码
         */
        public static final String PWD = "pwd";

        /**
         * URL
         */
        public static final String HOST_KEY = "host";

        /**
         * 端口
         */
        public static final String PORT_KEY = "port";

        /**
         * 数据库 schema
         */
        public static final String DB_KEY = "db";

        /**
         * 其他参数
         */
        public static final String PARAM_KEY = "param";

        /**
         * 换行符
         */
        public static final String LINE_SEPARATOR = "\n";

        /**
         * 默认隐藏文件前缀
         */
        public static final String DOT = ".";

        /**
         * keytab 文件后缀
         */
        public static final String KEYTAB_SUFFIX = ".keytab";

        /**
         * krb5 文件名称
         */
        public static final String KRB5CONF_FILE = "krb5.conf";

        /**
         * XML 文件后缀
         */
        public static final String XML_SUFFIX = ".xml";

        /**
         * .crt文件后缀
         */
        public static final String CRT_SUFFIX = ".crt";

        /**
         * .p12 文件后缀
         */
        public static final String P12_SUFFIX = ".p12";

        /**
         * spark desc database xx 返回字段 key
         */
        public static final String SPARK_DESC_DATABASE_KEY = "database_description_item";

        /**
         * spark desc database xx 返回字段 value
         */
        public static final String SPARK_DESC_DATABASE_VALUE = "database_description_value";

        /**
         * 数据库中对应关系字段
         */
        public static final String KEY = "key";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String COMMENT = "comment";
        public static final String DB_NAME = "db_name";
        public static final String DATABASE_NAME = "Database Name";
        public static final String DESCRIPTION = "Description";
        public static final String LOCATION_SPARK = "Location";
        public static final String OWNER_NAME = "owner_name";
        public static final String LOCATION = "location";
        public static final String IS_PART = "isPart";
        public static final String COL_NAME = "col_name";
        public static final String DATA_TYPE = "data_type";
        public static final String PRIMARY_KEY = "primary_key";
        public static final String REMARKS = "REMARKS";

        public static final String LEFT_PARENTHESIS_SYMBOL = "(";
        public static final String RIGHT_PARENTHESIS_SYMBOL = ")";


        public static final String DATA_TYPE_UNSIGNED = "UNSIGNED";


        public static final String LEFT_GREATER_SYMBOL = "<";
        public static final String RIGHT_GREATER_SYMBOL = ">";

        @Deprecated
        public static final String USE_DB = "use `%s`";
    }

    class PatternConsistent {
        /**
         * JDBC 正则
         */
        @Deprecated
        public static Pattern JDBC_PATTERN = Pattern.compile("(?i)jdbc:[a-zA-Z0-9\\.]+://(?<host>[0-9a-zA-Z\\.-]+):(?<port>\\d+)/(?<db>[0-9a-zA-Z_%\\.]+)(?<param>[\\?;#].*)*");

        /**
         * HIVE_JDBC_URL 正则解析
         */
        @Deprecated
        public static final Pattern HIVE_JDBC_PATTERN = Pattern.compile("(?i)jdbc:hive2://(?<url>[0-9a-zA-Z,\\:\\-\\.]+)(/(?<db>[0-9a-z_%]+)*(?<param>[\\?;#].*)*)*");

        /**
         * IMPALA JDBC_URL 正则
         */
        @Deprecated
        public static final Pattern IMPALA_JDBC_PATTERN = Pattern.compile("(?i)jdbc:impala://[0-9a-zA-Z\\-\\.]+:[\\d]+/(?<db>[0-9a-zA-Z_%\\-]+);.*");

        /**
         * GREENPLUM JDBC_URL 正则
         */
        @Deprecated
        public static final Pattern GREENPLUM_JDBC_PATTERN = Pattern.compile("(?i)jdbc:pivotal:greenplum://[0-9a-zA-Z\\-\\.]+:[\\d]+;DatabaseName=(?<db>[0-9a-zA-Z\\-]+);.*");
    }

    class HadoopConfConsistent {
        public static final String HADOOP_CONFIG = "hadoopConfig";

        public static final String TABLE_INFORMATION = "detailed table information";

        public static final String COMMENT_WITH_COLON = "Comment:";

        public static final String COMMENT = "Comment";

        public static final String HIVE_COMMENT = "comment=";

        public static final String DESCRIBE_EXTENDED = "describe extended %s";
    }

    class HBaseConsistent {
        /**
         * HBase 集群 根目录
         */
        public static final String KEY_ZOOKEEPER_ZNODE_PARENT = "zookeeper.znode.parent";

        /**
         * HBase 集群地址 KEY
         */
        public static final String KEY_HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";
    }

    class HiveConsistent {
        /**
         * Hive text 表默认表分隔符
         */
        public static final String DEFAULT_FIELD_DELIMIT = "\001";
    }

    class SSLConsistent {

        public static final String SSL = "SSL";

        public static final String SSL_KEYSTORE_PATH = "SSLKeyStorePath";

        public static final String SSL_KEYSTORE_PASSWORD = "SSLKeyStorePassword";

        public static final String SSL_KEYSTORE_TYPE = "SSLKeyStoreType";

        public static final String SSL_TRUST_STORE_PATH = "SSLTrustStorePath";

        public static final String SSL_TRUSTSTORE_PASSWORD = "SSLTrustStorePassword";

        public static final String SSL_TRUSTSTORE_TYPE = "SSLTrustStoreType";
    }
}
