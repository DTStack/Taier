import { FieldColumn } from '../../types';

export const columnsTreeParser = (columns: Partial<FieldColumn>[]) => {
  const res = {};
  columns.forEach((colItem) => {
    const key = `${colItem.tableName}(${colItem.tableAlias})`;
    if (!res[key]) {
      res[key] = [];
    }
    res[key].push(colItem);
  });
  return res;
};

export const columnSrtingParser = {
  decode: (str: string) => {
    if (!str) return {};
    const [schema, tableName, columnName] = str.split('-');
    return {
      schema,
      tableName,
      columnName,
    };
  },
  encode: (obj) => {
    if (!obj) obj = {};
    const { schema, tableName, columnName } = obj;
    if (!schema || !tableName || !columnName) return undefined;
    return `${schema}-${tableName}-${columnName}`;
  },
};
