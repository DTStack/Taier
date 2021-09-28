import * as React from 'react';

export const hdfsConfig = ( // hdfs config
  <div>
    高可用模式下的填写规则：
    <ul>
      <li>
        1、分别要填写：nameservice名称、
        namenode名称（多个以逗号分隔）、proxy.provider参数；
      </li>
      <li>2、所有参数以JSON格式填写；</li>
      <li>
        3、格式为：
        <br />
        {`"dfs.nameservices": "nameservice名称",`}
        {`"dfs.ha.namenodes.nameservice名称": "namenode名称，以逗号分隔",`}
        {`"dfs.namenode.rpc-address.nameservice名称.namenode名称": "",`}
        {`"dfs.namenode.rpc-address.nameservice名称.namenode名称": "",`}
        {`"dfs.client.failover.proxy.provider.`}
        <br />
        {`nameservice名称": "org.apache.hadoop.`}
        <br />
        hdfs.server.namenode.ha.
        <br />
        {`ConfiguredFailoverProxyProvider"`}
      </li>
      <li>
        4、详细参数含义请参考《帮助文档》或
        <a
          href="http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html"
          target="blank">
          Hadoop官方文档
        </a>
      </li>
    </ul>
  </div>
);
