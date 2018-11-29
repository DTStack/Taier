import { DATA_SOURCE } from '../../../../comm/const';

export function haveTableList (type) {
    const list = [DATA_SOURCE.MYSQL, DATA_SOURCE.HBASE, DATA_SOURCE.MONGODB]
    return list.indexOf(type) > -1;
}
export function haveCustomParams (type) {
    const list = [DATA_SOURCE.REDIS, DATA_SOURCE.MONGODB, DATA_SOURCE.ES, DATA_SOURCE.HBASE]
    return list.indexOf(type) > -1;
}
