import * as React from 'react';

import {
    DATA_SOURCE
} from '../../consts';

export function dataSourceText (type: any) {
    switch (type) {
        case DATA_SOURCE.MYSQL: {
            return 'MySQL'
        }
        case DATA_SOURCE.ORACLE: {
            return 'Oracle'
        }
        case DATA_SOURCE.SQLSERVER: {
            return 'SQLServer'
        }
        case DATA_SOURCE.HDFS: {
            return 'HDFS'
        }
        case DATA_SOURCE.HIVE: {
            return 'Hive'
        }
        case DATA_SOURCE.HBASE: {
            return 'HBase'
        }
        case DATA_SOURCE.FTP: {
            return 'FTP'
        }
        case DATA_SOURCE.MAXCOMPUTE: {
            return 'MaxCompute'
        }
        default: {
            return '其他'
        }
    }
}

/**
 * 字段状态校验
 * @param {*} status
 */
export function DetailCheckStatus (props: any) {
    switch (props.value) {
        case true: {
            return <span>通过</span>
        }
        case false: {
            return <span>未通过</span>
        }
        default: {
            return <span>--</span>
        }
    }
}
