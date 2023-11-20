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

package com.dtstack.taier.sparkyarn.sparkext;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.ClientArguments;
import org.apache.spark.deploy.yarn.DtClient;

/**
 * 修改Saprk yarn client ---> 修改提交之前的配置包打包
 * Date: 2018/5/9
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ClientExt extends DtClient {

    public ClientExt(ClientArguments args, Configuration hadoopConf, SparkConf sparkConf, YarnClient yarnClient) {
        super(args, hadoopConf, sparkConf, yarnClient);
    }
}