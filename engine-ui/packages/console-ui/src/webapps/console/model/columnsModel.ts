export default class ColumnsModel {
    public columnName: any;
    public type: any;
    public paramsName: any;
    public operator: any;
    public required: any;
    public desc: any;
    public id: any;
    constructor (column: any) {
        this.columnName = column.key;
        this.type = column.type;
        this.paramsName = column.paramsName || column.key;
        this.operator = column.operator || '=';
        this.required = typeof column.required == 'undefined' ? true : column.required;
        this.desc = column.desc || null;
        this.id = column.id || new Date().getTime() + '' + ~~(Math.random() * 10000);
    }
    resetDataFromService (column: any) {

    }
}
