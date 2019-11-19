import * as React from 'react';

import { DATA_SOURCE } from '../../comm/const'

const showMapArr: any = {
    [DATA_SOURCE.MYSQL]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.ORACLE]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.SQLSERVER]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.KUDU]: [
        ['hostPorts', '集群地址'],
        ['others', '其他参数']
    ],
    [DATA_SOURCE.POSTGRESQL]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.HDFS]: [
        ['defaultFS', 'defaultFS']
    ],
    [DATA_SOURCE.HIVE]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['defaultFS', 'defaultFS']
    ],
    [DATA_SOURCE.HBASE]: [
        ['hbase_quorum', 'Zookeeper集群地址']
    ],
    [DATA_SOURCE.FTP]: [
        ['protocol', 'Protocol'],
        ['host', 'Host'],
        ['port', 'Port'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.MAXCOMPUTE]: [
        ['endPoint', 'endPoint'],
        ['project', '项目名称'],
        ['accessId', 'Access Id']
    ],
    [DATA_SOURCE.ES]: [
        ['address', '集群地址']
    ],
    [DATA_SOURCE.REDIS]: [
        ['hostPort', '地址'],
        ['database', '数据库']
    ],
    [DATA_SOURCE.MONGODB]: [
        ['hostPorts', '集群地址'],
        ['database', '数据库']
    ],
    [DATA_SOURCE.KAFKA_11]: [
        ['address', '集群地址']
    ],
    [DATA_SOURCE.KAFKA_09]: [
        ['address', '集群地址']
    ],
    [DATA_SOURCE.KAFKA_10]: [
        ['address', '集群地址']
    ],
    [DATA_SOURCE.KAFKA]: [
        ['address', '集群地址']
    ]
}

export function ExtTableCell (props: any) {
    const { sourceData, ...other } = props;
    const arr = showMapArr[sourceData.type];
    if (arr) {
        return <div {...other}>
            {arr.map(([ key, text ]: any) => {
                return (
                    <p key={key} style={{ display: 'flex', lineHeight: 1.5 }}>
                        <span style={{ color: '#999', flexShrink: 0 }}>{text}：</span>
                        <span style={{ display: 'inline-block' }}>{sourceData.dataJson[key] || ''}</span>
                    </p>
                )
            })}
        </div>
    } else {
        return null;
    }
}
