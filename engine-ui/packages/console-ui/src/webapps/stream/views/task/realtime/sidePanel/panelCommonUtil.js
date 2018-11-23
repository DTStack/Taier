import { DATA_SOURCE } from "../../../../comm/const";

export function havaTableList(type){
    const list=[DATA_SOURCE.MYSQL,DATA_SOURCE.HBASE,DATA_SOURCE.MONGODB]
    return list.indexOf(type)>-1;
}