export default class ColumnsModel {
    public columnName: any;
    public type: any;
    public paramsName: any;
    public operator: any;
    public required: any;
    public desc: any;
    public id: any;
    public groupId: any;
    constructor (column: any) {
        this.columnName = column.key;
        this.type = column.type;
        this.paramsName = column.paramsName || column.key;
        this.operator = column.operator || '=';
        this.required = typeof column.required == 'undefined' ? false : column.required;
        this.desc = column.desc || column.comment || null; // 目前后端获取字段类型通过 desc 或者 comment 字段来获取
        this.id = column.id || new Date().getTime() + '' + ~~(Math.random() * 10000);
        this.groupId = column.groupId;
    }
    resetDataFromService (column: any) {

    }
    static exchangeServerParams (columns: any) {
        if (!columns) {
            return [];
        }
        return columns.map(
            (column: any) => {
                return new ColumnsModel({
                    key: column.fieldName,
                    type: column.paramType,
                    paramsName: column.paramName,
                    operator: column.operator,
                    desc: column.desc,
                    required: column.required,
                    groupId: column.groupId
                })
            }
        );
    }
}
