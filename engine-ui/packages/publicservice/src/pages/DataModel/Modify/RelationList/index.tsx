import React from 'react';
import { Table } from 'antd';
import { columns, data } from './constants';

interface IPropsRelationList {
  onClick?: () => void;
}

const RelationList = (props: IPropsRelationList) => {
  const { onClick = () => {} } = props;
  return (
    <>
      <span className="btn-link" onClick={onClick}>+ 添加关联表</span>
      <Table
        className="relation-list dt-table-border"
        columns={columns}
        dataSource={data}
        pagination={false}
        scroll={{ x: 600, y: 300 }}
      />
    </>
  )
}

export default RelationList;
