import React from 'react';
import { Divider } from 'antd';
import { EnumModalActionType } from './types';

export const columnsGenerator = ({
  handleModalDetailAction,
  handleModelRecover,
}) => {
  return [
    {
      title: '版本号',
      dataIndex: 'version',
      key: 'version',
      render: (version) => `V${version}`,
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
                handleModalDetailAction({
                  type: EnumModalActionType.OPEN,
                  payload: {
                    modelId: record.modelId,
                    version: record.version,
                  },
                });
              }}>
              查看
            </a>
            <Divider type="vertical" />
            <a
              className="btn-link"
              onClick={() => {
                handleModelRecover(record.modelId, record.version);
              }}>
              恢复
            </a>
          </span>
        );
      },
    },
  ];
};
