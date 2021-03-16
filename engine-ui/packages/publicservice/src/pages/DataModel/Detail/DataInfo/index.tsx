import React from 'react';
import { Table } from 'antd';
import './style';

const dataSource = [
  {
    key: '1',
    name: '胡彦斌',
    age: 32,
    address: '西湖区湖底公园1号',
  },
  {
    key: '2',
    name: '胡彦祖',
    age: 42,
    address: '西湖区湖底公园1号',
  },
];

const columns = [
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '年龄',
    dataIndex: 'age',
    key: 'age',
  },
  {
    title: '住址',
    dataIndex: 'address',
    key: 'address',
  },
];

const DataInfo = () => {
  return (
    <div className="data-info">
      <div className="title">
        关联表：
      </div>
      <Table
        className="dt-table-border"
        columns={columns}
        dataSource={dataSource}
        pagination={false}
      />
      <div className="title">
        维度：
      </div>
      <Table
        className="dt-table-border"
        columns={columns}
        dataSource={dataSource}
        pagination={false}
      />
      <div className="title">
        度量：
      </div>
      <Table
        className="dt-table-border"
        columns={columns}
        dataSource={dataSource}
        pagination={false}
      />
    </div>
  )
}

export default DataInfo;
