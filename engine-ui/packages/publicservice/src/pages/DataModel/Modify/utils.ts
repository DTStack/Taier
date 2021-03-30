
export const joinPairsParser = {
  // Object 2 string
  encode: (joinObj) => {
    const lSchema = joinObj.leftValue.schema;
    const lTable = joinObj.leftValue.tableName;
    const lCol = joinObj.leftValue.columnName;
    const rSchema = joinObj.rightValue.schema;
    const rTable = joinObj.rightValue.tableName;
    const rCol = joinObj.rightValue.columnName;
    return {
      leftValue: `${lSchema}-${lTable}-${lCol}`,
      rightvalue: `${rSchema}-${rTable}-${rCol}`,
    }
  },
  // string 2 Object
  /**
   * @param joinItemStr {leftValue: string, rightValue: string }
   * @returns 
   */
  decode: (joinItemStr) => {
    const [lSchema, lTable, lCol] = joinItemStr.leftValue.split(
      '-'
    );
    const [rSchema, rTable, rCol] = joinItemStr.rightValue.split(
      '-'
    );
    return {
      leftValue: {
        schema: lSchema,
        tableName: lTable,
        columnName: lCol,
      },
      rightValue: {
        schema: rSchema,
        tableName: rTable,
        columnName: rCol,
      },
    };
  }
}
