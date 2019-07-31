
import { PARAMS_POSITION, FIELD_TYPE_LIST } from '../consts';
import BaseModel from './baseModel';
export const constColumnsKeys: any = {
    NAME: 'paramName',
    POSITION: 'paramLocation',
    TYPE: 'paramType',
    VALUE: 'defaultValue',
    DESC: 'desc'
}

export default class ConstColumnModel extends BaseModel {
    constructor (initData = {}) {
        super(initData)
        if (!(constColumnsKeys.POSITION in initData)) {
            this[constColumnsKeys.POSITION] = PARAMS_POSITION.QUERY;
        }
        if (!(constColumnsKeys.TYPE in initData)) {
            this[constColumnsKeys.TYPE] = FIELD_TYPE_LIST[0];
        }
        this.isRequired = true;
    }
}
