import { TableJoinInfo } from 'pages/DataModel/types';
interface ITableItem {
  schema: string;
  tableName: string;
}

export const isTableSame = (
  table1: { schema: string; tableName: string },
  table2: { schema: string; tableName: string }
) => {
  return (
    table1.schema === table2.schema && table1.tableName === table2.tableName
  );
};

export const deleteLinkChain = (list, id, masterTable) => {
  const ids = [];
  ids.push(id);
  while (ids.length > 0) {
    const id = ids.pop();
    const target = list.find((item) => item.id === id);
    const filterIds = list
      .filter((item) => {
        return (
          isTableSame(
            {
              schema: item.leftSchema,
              tableName: item.leftTable,
            },
            {
              schema: target.schema,
              tableName: target.table,
            }
          ) &&
          !isTableSame(
            {
              schema: item.leftSchema,
              tableName: item.leftTable,
            },
            masterTable
          )
        );
      })
      .map((item) => item.id);

    list = list.filter((item) => item.id !== id);
    ids.push(...filterIds);
  }
  return list;
};

export const relationListRemove = (
  relationList: TableJoinInfo[],
  id: number | string,
  masterTable: ITableItem
) => {
  const target = relationList.find((item) => item.id === id);
  const rightTable = {
    schema: target.schema,
    tableName: target.table,
  };
  const isFind =
    relationList.findIndex(
      (item) =>
        item.id !== id &&
        isTableSame(rightTable, {
          schema: item.schema,
          tableName: item.table,
        }) &&
        !isTableSame(
          { schema: item.leftSchema, tableName: item.leftTable },
          { schema: item.schema, tableName: item.table }
        )
    ) > -1;
  if (isFind) {
    return relationList.filter((item) => item.id !== id);
  } else {
    const list = deleteLinkChain(relationList, id, masterTable);
    return list;
  }
};
