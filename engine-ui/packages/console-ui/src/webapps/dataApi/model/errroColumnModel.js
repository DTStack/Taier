import BaseModel from './baseModel';

class ErrorColumnModel extends BaseModel {
    static columnKeys = {
        ERRORCODE: 'errorCode',
        MSG: 'errorMsg',
        SOLUTION: 'solution'
    }
    constructor (initData) {
        super(initData);
    }
}
export default ErrorColumnModel;
