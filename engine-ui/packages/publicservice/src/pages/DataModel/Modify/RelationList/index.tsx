import React, { useMemo } from 'react';
import { Table } from 'antd';
import { columnsGenerator } from './constants';

interface IPropsRelationList {
  onClick?: () => void;
  data?: any[];
  onRelationListDelete?: (id: number) => void;
  onRelationListEdit?: (id: number) => void;
}

const RelationList = (props: IPropsRelationList) => {

  const columns = useMemo(() => columnsGenerator({
    onDelete: props.onRelationListDelete,
    onEdit: props.onRelationListEdit,
  }), []);

  const { onClick = () => {}, data = [] } = props;
  return (
    <>
      <span className="btn-link" onClick={onClick}>+ 添加关联表</span>
      <Table
        rowKey="id"
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
