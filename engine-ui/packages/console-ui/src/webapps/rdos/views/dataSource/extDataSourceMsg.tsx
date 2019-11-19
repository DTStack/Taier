import * as React from 'react';

import { DATA_SOURCE } from '../../comm/const'

const showMapArr: any = {
    [DATA_SOURCE.KYLIN]: [
        ['authURL', 'authURL'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.GBASE]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.MYSQL]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.POLAR_DB]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.CLICK_HOUSE]: [
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
    [DATA_SOURCE.POSTGRESQL]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.LIBRASQL]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.DB2]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['username', '用户名']
    ],
    [DATA_SOURCE.HDFS]: [
        ['defaultFS', 'defaultFS']
    ],
    [DATA_SOURCE.HIVE_2]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['defaultFS', 'defaultFS']
    ],
    [DATA_SOURCE.HIVE_1]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['defaultFS', 'defaultFS']
    ],
    [DATA_SOURCE.CARBONDATA]: [
        ['jdbcUrl', 'jdbcUrl'],
        ['defaultFS', 'defaultFS'],
        ['username', '用户名']
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
    [DATA_SOURCE.KUDU]: [
        ['hostPorts', '集群地址']
    ]
}

export function ExtTableCell (props: any) {
    const { sourceData, ...other } = props;
    const arr = showMapArr[sourceData.type];
    if (arr) {
        return <div {...other}>
            {arr.map(([ key, text ]: any) => {
                return <p key={key} style={{ display: 'flex', lineHeight: 1.5, wordBreak: 'break-all' }}>
                    <span style={{ color: '#999', flexShrink: 0 }}>{text}：</span>
                    <span style={{ display: 'inline-block' }}>{sourceData.dataJson[key] || ''}</span>
                </p>
            })}
        </div>
    } else {
        return null;
    }
}
