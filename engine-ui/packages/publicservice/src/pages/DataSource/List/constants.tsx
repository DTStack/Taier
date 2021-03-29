import React from 'react';
import { Divider, Popconfirm, Icon, Tag, Badge, notification } from 'antd';
import './style.scss';
import { ExtTableCell } from './components/extTableCell';
import { MAIN_COLOR } from '../constants/theme';

const columns = (props: any) => {
  const { toEdit, toAuth, toDelete, left, right, filters } = props;

  return [
    {
      title: '数据源名称',
      key: 'dataName',
      fixed: left,
      width: 200,
      render: (_, record) =>
        //	是否有meta标志 0-否 1-是
        record.isMeta === 0 ? (
          <span style={{ color: MAIN_COLOR }}>{record.dataName}</span>
        ) : (
          <div style={{ color: MAIN_COLOR }}>
            <span style={{ marginRight: 4 }}>{record.dataName}</span>
            <Tag style={{ borderColor: MAIN_COLOR, color: MAIN_COLOR }}>
              meta
            </Tag>
          </div>
        ),
    },
    {
      title: '类型',
      dataIndex: 'dataType',
      key: 'dataType',
      ellipsis: true,
      width: 120,
      render: (_, record) => {
        return <span>{record.dataType + record.dataVersion}</span>;
      },
    },
    {
      title: '授权产品',
      dataIndex: 'appNames',
      key: 'appNames',
      ellipsis: true,
      width: 220,
    },
    {
      title: '描述',
      dataIndex: 'dataDesc',
      key: 'dataDesc',
      ellipsis: true,
      width: 160,
    },
    {
      title: '连接信息',
      dataIndex: 'linkJson',
      ellipsis: true,
      width: 200,
      render: (_, record) => {
        return <ExtTableCell sourceData={record} />;
      },
    },
    {
      title: '连接状态',
      dataIndex: 'status',
      ellipsis: true,
      width: 200,
      render: (text, _) =>
        text === 0 ? (
          <span>
            <Badge status="error" />
            连接失败
          </span>
        ) : (
          <span>
            <Badge status="success" />
            正常
          </span>
        ),
      filters: filters,
    },
    {
      title: '最近修改时间',
      dataIndex: 'gmtModified',
      key: 'gmtModified',
      ellipsis: true,
      width: 120,
    },
    {
      title: '操作',
      key: 'action',
      fixed: right,
      width: 160,
      render: (_, record) => {
        return (
          <>
            <span onClick={() => toEdit(record)}>
              <a className={record.isMeta === 0 ? 'data-view' : 'gray'}>编辑</a>
            </span>
            <Divider type="vertical" />
            <span
              className="data-view"
              onClick={(event) => toAuth(record, event)}>
              <a>授权</a>
            </span>
            <Divider type="vertical" />

            {/* 是否授权，0为未授权，1为已授权 */}
            {!record.isAuth && record.isMeta !== 1 ? (
              <span className="data-view">
                <Popconfirm
                  title="是否删除此条记录？"
                  icon={
                    <Icon type="question-circle-o" style={{ color: 'red' }} />
                  }
                  onConfirm={() => toDelete(record)}
                  okText="删除"
                  cancelText="取消">
                  <a>删除</a>
                </Popconfirm>
              </span>
            ) : (
              <span
                onClick={() => {
                  notification.error({
                    message: '错误！',
                    description: '数据源已授权给产品，不可删除',
                  });
                }}>
                <a className="gray">删除</a>
              </span>
            )}
          </>
        );
      },
    },
  ];
};

export { columns };
