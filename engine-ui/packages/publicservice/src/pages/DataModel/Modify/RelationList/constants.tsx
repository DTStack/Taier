import React from 'react';
import { Divider } from 'antd';

export const columns = [
  {
    title: '序号',
    dataIndex: 'name',
    key: 'Name',
    width: 80,
    ellipsis: true,
  },
  {
    title: '表别名',
    dataIndex: 'age',
    key: 'age',
    width: 100,
    ellipsis: true,
  },
  {
    title: 'schema',
    dataIndex: 'address',
    key: 'address',
    width: 120,
    ellipsis: true,
  },
  {
    title: '表名',
    dataIndex: 'age',
    key: 'age',
    width: 100,
    ellipsis: true,
  },
  {
    title: '关联类型',
    dataIndex: 'address',
    key: 'address',
    width: 120,
    ellipsis: true,
  },
  {
    title: '关联条件',
    dataIndex: 'address',
    key: 'address',
    width: 160,
    ellipsis: true,
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
    // type hack
    fixed: 'right' as 'right',
    render: (text, record) => (
      <span>
        <a>编辑</a>
        <Divider type="vertical" />
        <a>删除</a>
      </span>
    ),
  },
];

export const data = [
  {
    key: '1',
    name: 'John Brown',
    age: 32,
    address: 'New York No. 1 Lake Park',
  },
  {
    key: '2',
    name: 'Jim Green',
    age: 42,
    address: 'London No. 1 Lake Park',
  },
  {
    key: '3',
    name: 'Joe Black',
    age: 32,
    address: 'Sidney No. 1 Lake Park',
  },
];
