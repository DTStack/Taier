import { DATA_SOURCE } from '../../../../comm/const';

export function haveTableList (type: any) {
    const list: any = [DATA_SOURCE.MYSQL, DATA_SOURCE.POLAR_DB, DATA_SOURCE.HBASE, DATA_SOURCE.MONGODB, DATA_SOURCE.ORACLE,
        DATA_SOURCE.POSTGRESQL, DATA_SOURCE.KUDU
    ]
    return list.indexOf(type) > -1;
}
export function haveCustomParams (type: any) {
    const list: any = [DATA_SOURCE.REDIS, DATA_SOURCE.MONGODB, DATA_SOURCE.ES, DATA_SOURCE.HBASE]
    return list.indexOf(type) > -1;
}
// 是否拥有字段列的权限
export function haveTableColumn (type: any) {
    const list: any = [DATA_SOURCE.MYSQL, DATA_SOURCE.POLAR_DB, DATA_SOURCE.POLAR_DB, DATA_SOURCE.ORACLE, DATA_SOURCE.POSTGRESQL, DATA_SOURCE.KUDU]
    return list.indexOf(type) > -1;
}
// 是否拥有主键列的权限
export function havePrimaryKey (type: any) {
    const list: any = [DATA_SOURCE.MYSQL, DATA_SOURCE.POLAR_DB, DATA_SOURCE.ORACLE, DATA_SOURCE.POSTGRESQL, DATA_SOURCE.KUDU]
    return list.indexOf(type) > -1;
}

// 是否拥有Topic
export function haveTopic (type: any) {
    const list: any = [DATA_SOURCE.KAFKA, DATA_SOURCE.KAFKA_11, DATA_SOURCE.KAFKA_09, DATA_SOURCE.KAFKA_10]
    return list.indexOf(type) > -1;
}
