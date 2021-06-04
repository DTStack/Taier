import React from 'react';

import { DATA_SOURCE } from '../../constants/index';

const showMapArr: any = {
  [DATA_SOURCE.MYSQL]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.CARBONDATA]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.CLICKHOUSE]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.POLAR_DB]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.ORACLE]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.SQLSERVER]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.SQLSERVER2017]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.DB2]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.KUDU]: [['hostPorts', '集群地址']],
  [DATA_SOURCE.IMPALA]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['defaultFS', 'defaultFS'],
  ],
  [DATA_SOURCE.POSTGRESQL]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.HDFS]: [['defaultFS', 'defaultFS']],
  [DATA_SOURCE.HIVE]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['defaultFS', 'defaultFS'],
  ],
  [DATA_SOURCE.Hive_1X]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['defaultFS', 'defaultFS'],
  ],
  [DATA_SOURCE.Hive_2X]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['defaultFS', 'defaultFS'],
  ],
  [DATA_SOURCE.HBASE]: [['hbase_quorum', 'Zookeeper集群地址']],
  [DATA_SOURCE.HBase_1X]: [['hbase_quorum', 'Zookeeper集群地址']],
  [DATA_SOURCE.HBase_2X]: [['hbase_quorum', 'Zookeeper集群地址']],
  [DATA_SOURCE.FTP]: [
    ['protocol', 'Protocol'],
    ['host', 'Host'],
    ['port', 'Port'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.MAXCOMPUTE]: [
    ['endPoint', 'endPoint'],
    ['project', '项目名称'],
    ['accessId', 'Access Id'],
  ],
  [DATA_SOURCE.ES]: [['address', '集群地址']],
  [DATA_SOURCE.ES6]: [['address', '集群地址']],
  [DATA_SOURCE.ES7]: [['address', '集群地址']],
  [DATA_SOURCE.ELASTICSEARCH]: [['address', '集群地址']],
  [DATA_SOURCE.REDIS]: [
    ['hostPort', '地址'],
    ['database', '数据库'],
  ],
  [DATA_SOURCE.MONGODB]: [
    ['hostPorts', '集群地址'],
    ['database', '数据库'],
  ],
  [DATA_SOURCE.KAFKA_11]: [
    ['address', '集群地址'],
    ['brokerList', 'broker地址'],
  ],
  [DATA_SOURCE.KAFKA_09]: [
    ['address', '集群地址'],
    ['brokerList', 'broker地址'],
  ],
  [DATA_SOURCE.KAFKA_10]: [
    ['address', '集群地址'],
    ['brokerList', 'broker地址'],
  ],
  [DATA_SOURCE.KAFKA]: [
    ['address', '集群地址'],
    ['brokerList', 'broker地址'],
  ],
  [DATA_SOURCE.KAFKA_2X]: [
    ['address', '集群地址'],
    ['brokerList', 'broker地址'],
  ],
  [DATA_SOURCE.KAFKA_NEW]: [
    ['address', '集群地址'],
    ['brokerList', 'broker地址'],
  ],
  [DATA_SOURCE.EMQ]: [['address', 'Broker URL']],
  [DATA_SOURCE.TIDB]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.KINGBASEES8]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.GBASE_8A]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.S3]: [['hostname', 'hostname']],
  [DATA_SOURCE.WEBSOCKET]: [['url', 'url']],
  [DATA_SOURCE.SOCKET]: [['url', 'url']],
  [DATA_SOURCE.GREENPLUM]: [['jdbcUrl', 'jdbcUrl']],
  [DATA_SOURCE.KYLIN]: [
    ['authURL', 'authURL'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.PHOENIX]: [['jdbcUrl', 'jdbcUrl']],
  [DATA_SOURCE.PRESTO]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.VERTICA]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.ADB_POSTGRESQL]: [
    ['jdbcUrl', 'jdbcUrl'],
    ['username', '用户名'],
  ],
  [DATA_SOURCE.SOLR]: [['hostPorts', '集群地址']],
};

export function ExtTableCell(props: any) {
  const { sourceData } = props;
  const arr = showMapArr[sourceData.type];

  let data = {};
  try {
    data = JSON.parse(sourceData.linkJson) || {};
  } catch (error) {}
  console.log(sourceData, 'arr----');
  if (arr) {
    return (
      <div>
        {arr.map(([key, text]: any) => {
          return (
            <p
              key={key}
              style={{ display: 'flex', lineHeight: 1.5, marginBottom: 0 }}>
              <span style={{ color: '#999', flexShrink: 0 }}>{text}：</span>
              <span className="link-json" title={data[key] || ''}>
                {data[key] || ''}
              </span>
            </p>
          );
        })}
      </div>
    );
  } else {
    return null;
  }
}
