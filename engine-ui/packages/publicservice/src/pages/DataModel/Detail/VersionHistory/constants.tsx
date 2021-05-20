import React from 'react';
import { Divider } from 'antd';
import { EnumModalActionType } from './types';

export const columnsGenerator = ({ handleModalDetailAction, modelId }) => {
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
                handleModalDetailAction({
                  type: EnumModalActionType.OPEN,
                  payload: {
                    modelId,
                    version: record.version,
                  },
                });
              }}>
              查看
            </a>
            <Divider type="vertical" />
            <a className="btn-link" onClick={() => {}}>
              恢复
            </a>
          </span>
        );
      },
    },
  ];
};

export const dataSource = [
  {
    operateTime: '2020-08-22 19:00:00',
    operator: 'xiaoliu',
    version: 'V1.1',
  },
  {
    operateTime: '2020-08-22 19:00:00',
    operator: 'xiaoliu',
    version: 'V1.2',
  },
  {
    operateTime: '2020-08-22 19:00:00',
    operator: 'xiaoliu',
    version: 'V1.3',
  },
  {
    operateTime: '2020-08-22 19:00:00',
    operator: 'xiaoliu',
    version: 'V1.4',
  },
  {
    operateTime: '2020-08-22 19:00:00',
    operator: 'xiaoliu',
    version: 'V1.5',
  },
  {
    operateTime: '2020-08-22 19:00:00',
    operator: 'xiaoliu',
    version: 'V1.6',
  },
];
