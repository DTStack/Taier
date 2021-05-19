import React from 'react';
import { Divider } from 'antd';


export const columnsGenerator = ({
  handleModalDetailAction
}) => {
  return [
    {
      title: '版本号',
      dataIndex: 'version',
      key: 'version',
    },
    {
      title: '操作人',
      dataIndex: 'operator',
      key: 'operator',
    },
    {
      title: '操作时间',
      dataIndex: 'operateTime',
      key: 'operateTime',
    },
    {
      title: '操作',
      key: 'operation',
      width: 200,
      fixed: 'right',
      render: (text, record) => {
        return (
          <span>
            <a
              className="btn-link"
              onClick={() => {
                handleModalDetailAction({ type: 'OPEN', payload: 1 });
              }}
            >
              查看
            </a>
            <Divider type="vertical" />
            <a
              className="btn-link"
              onClick={() => {}}
            >
              恢复
            </a>
          </span>
        );
      },
    },
  ];
};