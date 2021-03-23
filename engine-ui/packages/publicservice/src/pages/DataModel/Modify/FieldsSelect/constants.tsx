import React from 'react';
import { FieldColumn } from 'pages/DataModel/types';
import { Input } from 'antd';

interface Column {
  dataIndex?: keyof FieldColumn;
  title: string;
  width?: number;
  render?: any;
}

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
  }
];

export const idGenerator = () => {
  let _id = 0;
  return () => ++_id;
}

export const columnsGenerator = ({ onInputBlur }): Column[] => {
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
    },
    {
      title: '字段名称',
      dataIndex: 'tableName',
      width: 140,
    },
    {
      title: '描述',
      dataIndex: 'columnComment',
      width: 160,
      render: (comment, record) => {
        return (
          <Input defaultValue={comment} onBlur={(e) => onInputBlur(record.id, e.currentTarget.value)} />
        )
      }
    },
    {
      title: '字段类型',
      dataIndex: 'columnType',
      width: 120,
    }
  ];
}
