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
    static exchangeServerParams (columns) {
        if (!columns) {
            return [];
        }
        return columns.map(
            (column) => {
                return new ColumnsModel({
                    key: column.fieldName,
                    type: column.paramType,
                    paramsName: column.paramName,
                    operator: column.operator,
                    desc: column.desc,
                    required: column.required
                })
            }
        );
    }
}
