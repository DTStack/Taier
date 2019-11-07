import { cloneDeep } from 'lodash';
import { DATA_SOURCE, writeStrategys, TASK_TYPE } from '../../../../../comm/const';

export function cleanCollectionParams (data: any) {
    let newData = cloneDeep(data);
    if (newData.taskType != TASK_TYPE.DATA_COLLECTION) {
        return data;
    }
    const { sourceMap = {}, targetMap = {} } = newData;
    if (!sourceMap || !targetMap) {
        return data;
    }
    const isMysqlSource = sourceMap.type == DATA_SOURCE.MYSQL;
    if (!isMysqlSource) {
        targetMap.analyticalRules = undefined;
    }
    if (targetMap.writeStrategy == writeStrategys.FILESIZE) {
        targetMap.interval = undefined;
    }
    return newData;
}
