import React from "react";
import { Table } from "antd";
import molecule from "molecule/esm";

const columns = [
  {
    title: " ",
    dataIndex: "id",
    width: 50,
    align: 'center',
    key: "id",
  },
  {
    title: "tableName",
    dataIndex: "tableName",
    align: 'center',
    key: "tableName",
  },
];

export default function Result({ data }: any) {
  return (
    <molecule.component.Scrollable>
      <Table
        // @ts-ignore
        columns={columns}
        dataSource={data.map((item: any, index: any) => ({
          id: index + 1,
          tableName: item,
        }))}
      />
    </molecule.component.Scrollable>
  );
}
