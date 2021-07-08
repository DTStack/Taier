import React from 'react';
import { FieldColumn } from 'pages/DataModel/types';
import { Input } from 'antd';
import _ from 'lodash';

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
      ellipsis: true,
    },
    {
      title: '表',
      dataIndex: 'tableName',
      width: 120,
      filters: _.uniqBy(data, (item) => (item as any).tableName).map((item) => ({
        text: (item as any).tableName,
        value: (item as any).tableName,
      })),
      onFilter: (value, record) => value === record.tableName,
      ellipsis: true,
    },
    // TODO: 字段
    {
      title: '表别名',
      width: 120,
      dataIndex: 'tableAlias',
      ellipsis: true,
    },
    {
      title: 'schema',
      dataIndex: 'schema',
      width: 120,
      filters: _.uniqBy(data, (item) => (item as any).schema).map((item) => ({
        text: (item as any).schema,
        value: (item as any).schema,
      })),
      onFilter: (value, record) => value === record.schema,
      ellipsis: true,
    },
    {
      title: '字段名称',
      dataIndex: 'columnName',
      width: 140,
      ellipsis: true,
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
            autoComplete="off"
          />
        );
      },
    },
    {
      title: '字段类型',
      dataIndex: 'columnType',
      width: 120,
      ellipsis: true,
    },
  ];
};
