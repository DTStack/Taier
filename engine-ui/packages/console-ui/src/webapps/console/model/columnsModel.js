export default class ColumnsModel {
    constructor (column) {
        this.columnName = column.key;
        this.type = column.type;
        this.paramsName = column.paramsName || column.key;
        this.operator = column.operator || '=';
        this.required = typeof column.required == 'undefined' ? true : column.required;
        this.desc = column.desc || null;
        this.id = column.id || new Date().getTime() + '' + ~~(Math.random() * 10000);
    }
    resetDataFromService (column) {

    }
}
