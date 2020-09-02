import * as React from 'react';
import { Button, Table } from 'antd';

interface IProps {
  recordList: any[];
  handleAdd: (record: any) => void;
  columns: any[];
  pagination: any;
  handlePageChange: (pagination: any) => void;
  tableLoading: boolean;
}

const TableArea: React.FC<IProps> = (props) => {
  const {
    recordList,
    columns,
    handleAdd,
    pagination,
    handlePageChange,
    tableLoading,
  } = props;
  return (
    <>
      <Button
        type="primary"
        className="btn-add"
        onClick={() => handleAdd({})}
      >添加记录</Button>
      <Table
        className="table"
        dataSource={recordList}
        columns={columns}
        size="middle"
        onChange={pagination => handlePageChange(pagination)}
        pagination={{
          pageSize: pagination.pageSize,
          // current: pagination.current,
          total: pagination.total,
        }}
        loading={tableLoading}
        bordered={true}
      />
    </>
  )
}

export default TableArea;
