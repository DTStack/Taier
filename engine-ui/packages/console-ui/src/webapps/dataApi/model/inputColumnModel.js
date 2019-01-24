
import { PARAMS_POSITION, FIELD_TYPE_LIST } from '../consts';
import BaseModel from './baseModel';
export const inputColumnsKeys = {
    NAME: 'paramName',
    POSITION: 'paramLocation',
    TYPE: 'paramType',
    ISREQUIRED: 'required',
    DESC: 'desc'
}

export default class InputColumnModel extends BaseModel {
    constructor (initData = {}) {
        super(initData)
        if (!(inputColumnsKeys.POSITION in initData)) {
            this[inputColumnsKeys.POSITION] = PARAMS_POSITION.QUERY;
        }
        if (!(inputColumnsKeys.TYPE in initData)) {
            this[inputColumnsKeys.TYPE] = FIELD_TYPE_LIST[0];
        }
        if (!(inputColumnsKeys.ISREQUIRED in initData)) {
            this[inputColumnsKeys.ISREQUIRED] = true;
        }
    }
}
