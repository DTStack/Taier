/* eslint-disable react/no-unescaped-entities */
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

import './tooltips.scss';

export default () => (
    <div className="dt-datasource-hdfs-tooltips">
        高可用模式下的填写规则：
        <ul className="dt-datasource-hdfs-tooltips-list">
            <li>
                分别要填写：
                <p>- nameservice名称</p>
                <p>- namenode名称（多个以逗号分隔）</p>
                <p>- proxy.provider参数；</p>
            </li>
            <li>所有参数以JSON格式填写；</li>
            <li>
                格式为：
                <br />
                "dfs.nameservices": "nameservice名称", "dfs.ha.namenodes.nameservice名称": "namenode名称，以逗号分隔",
                "dfs.namenode.rpc-address.nameservice名称.namenode名称": "",
                "dfs.namenode.rpc-address.nameservice名称.namenode名称": "",
                "dfs.client.failover.proxy.provider.nameservice名称":
                "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
            </li>
            <li>
                详细参数含义请参考《帮助文档》或
                <a
                    href="http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html"
                    target="blank"
                >
                    Hadoop官方文档
                </a>
            </li>
        </ul>
    </div>
);
