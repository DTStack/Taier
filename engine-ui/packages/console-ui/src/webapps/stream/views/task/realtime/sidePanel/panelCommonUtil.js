import { DATA_SOURCE } from '../../../../comm/const';

export function haveTableList (type) {
    const list = [DATA_SOURCE.MYSQL, DATA_SOURCE.HBASE, DATA_SOURCE.MONGODB, DATA_SOURCE.ORACLE]
    return list.indexOf(type) > -1;
}
export function haveCustomParams (type) {
    const list = [DATA_SOURCE.REDIS, DATA_SOURCE.MONGODB, DATA_SOURCE.ES, DATA_SOURCE.HBASE]
    return list.indexOf(type) > -1;
}
// 是否拥有字段列的权限
export function haveTableColumn (type) {
    const list = [DATA_SOURCE.MYSQL, DATA_SOURCE.ORACLE]
    return list.indexOf(type) > -1;
}
// 是否拥有主键列的权限
export function havePrimaryKey (type) {
    const list = [DATA_SOURCE.MYSQL, DATA_SOURCE.ORACLE]
    return list.indexOf(type) > -1;
}

// 是否拥有Topic
export function haveTopic (type) {
    const list = [DATA_SOURCE.KAFKA, DATA_SOURCE.KAFKA_09, DATA_SOURCE.KAFKA_10]
    return list.indexOf(type) > -1;
}
