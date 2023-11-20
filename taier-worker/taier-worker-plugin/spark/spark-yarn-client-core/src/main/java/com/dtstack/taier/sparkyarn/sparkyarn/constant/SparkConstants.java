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
package com.dtstack.taier.sparkyarn.sparkyarn.constant;

import java.util.ArrayList;
import java.util.Arrays;

public class SparkConstants {
    public static final String HADOOP_CONF = "__hadoop_conf__";

    public static final String HIVE_SITE = "/hive-site.xml";

    public static final String CORE_SITE = "/core-site.xml";

    public static final String YARN_SITE = "/yarn-site.xml";

    public static final ArrayList<String> FILTER_PARAM =
            new ArrayList<>(
                    Arrays.asList(
                            "fs.hdfs.impl.disable.cache",
                            "fs.file.impl.disable.cache",
                            "hive.execution.engine"));

    public static final String SPARK_LOG4J_CONTENT =
            "log4j.rootLogger=INFO,Client\n"
                    + "log4j.logger.Client=INFO,Client\n"
                    + "log4j.additivity.Client = false\n"
                    + "log4j.appender.console.target=System.err\n"
                    + "log4j.appender.Client=org.apache.log4j.ConsoleAppender\n"
                    + "log4j.appender.Client.layout=org.apache.log4j.PatternLayout\n"
                    + "log4j.appender.Client.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p %-60c %x - %m%n";

    public static final String SPARK_JAVA_OPTIONS_LOG4J_CONTENT =
            "-Dlog4j.configuration=./__spark_conf__/log4j.properties";


    public static final String SPARK_DRIVER_EXTRA_JAVA_OPTIONS = "spark.driver.extraJavaOptions";

    public static final String SPARK_EXECUTOR_EXTRA_JAVA_OPTIONS =
            "spark.executor.extraJavaOptions";

    public static final String LOG_LEVEL_KEY = "logLevel";
}