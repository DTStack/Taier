import React from 'react';
import { FieldColumn } from 'pages/DataModel/types';
import { Input } from 'antd';

// interface Column {
//   dataIndex?: keyof FieldColumn;
//   title: string;
//   width?: number;
//   render?: any;
// }

export const data: FieldColumn[] = [
  {
    columnComment: 'aaa',
    columnName: 'bbb',
    columnType: 'ccc',
    schema: 'schema',
    tableName: 'tableName',
  },
  {
    columnComment: 'aaa',
    columnName: 'bbb',
    columnType: 'ccc',
    schema: 'schema',
    tableName: 'tableName',
  },
  {
    columnComment: 'aaa',
    columnName: 'bbb',
    columnType: 'ccc',
    schema: 'schema',
    tableName: 'tableName',
  },
];

export const idGenerator = () => {
  let _id = 0;
  return () => ++_id;
};

export const columnsGenerator = ({ onInputBlur, data }): any[] => {
  return [
    {
      title: '序号',
      width: 80,
      render: (value, record, index) => index + 1,
    },
    {
      title: '表',
      dataIndex: 'tableName',
      width: 120,
      filters: [...new Set(data.map((item) => item.tableName))].map((item) => ({
        text: item,
        value: item,
      })),
      onFilter: (value, record) => value === record.tableName,
    },
    {
      title: 'schema',
      dataIndex: 'schema',
      width: 120,
      filters: [...new Set(data.map((item) => item.schema))].map((item) => ({
        text: item,
        value: item,
      })),
      onFilter: (value, record) => value === record.schema,
    },
    {
      title: '字段名称',
      dataIndex: 'columnName',
      width: 140,
    },
    {
      title: '描述',
      dataIndex: 'columnComment',
      width: 160,
      render: (comment, record) => {
        return (
          <Input
            defaultValue={comment}
            onBlur={(e) => onInputBlur(record.id, e.currentTarget.value)}
          />
        );
      },
    },
    {
      title: '字段类型',
      dataIndex: 'columnType',
      width: 120,
    },
  ];
};
