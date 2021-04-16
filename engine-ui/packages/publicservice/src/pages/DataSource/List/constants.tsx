import React from 'react';
import { Divider, Popconfirm, Icon, Tag, Badge, message } from 'antd';
import { ExtTableCell } from './components/extTableCell';
import { MAIN_COLOR } from '../constants/theme';
import './style.scss';

const columns = (props: any) => {
  const { toEdit, toAuth, toDelete, left, right, filters } = props;
  const IconFilter = () => (
    <i className="iconfont2 iconOutlinedxianxing_filter filter-status"></i>
  );
  return [
    {
      title: '数据源名称',
      width: 240,
      key: 'dataName',
      fixed: left,
      ellipsis: true,
      render: (_, record) =>
        //	meta标志 0-否 1-是
        record.isMeta === 0 ? (
          <span
            style={{
              color: MAIN_COLOR,
            }}
            className="ellipsis">
            {record.dataName}
          </span>
        ) : (
          <div style={{ color: MAIN_COLOR }} className="ellipsis">
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
      width: 200,
    },
    {
      title: '授权产品',
      dataIndex: 'appNames',
      ellipsis: true,
      width: 200,
      render: (_, record) => {
        return <span className="ellipsis">{record.appNames || '-'}</span>;
      },
    },
    {
      title: '描述',
      dataIndex: 'dataDesc',
      ellipsis: true,
      width: 200,
      render: (_, record) => {
        return <span className="ellipsis">{record.dataDesc || '-'}</span>;
      },
    },
    {
      title: '连接信息',
      dataIndex: 'linkJson',
      width: 240,
      render: (_, record) => {
        return <ExtTableCell sourceData={record} />;
      },
    },
    {
      title: '连接状态',
      dataIndex: 'status',
      width: 132,
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
      filterIcon: () => (
        <Icon component={IconFilter} style={{ cursor: 'pointer' }} />
      ),
    },
    {
      title: '最近修改时间',
      dataIndex: 'gmtModified',
      key: 'gmtModified',
      ellipsis: true,
      width: 200,
    },
    {
      title: '操作',
      key: 'action',
      fixed: right,
      width: 200,
      render: (_, record) => {
        return (
          <>
            <span onClick={() => toEdit(record)}>
              <a
                className={
                  record.isMeta === 0 ? 'data-view' : 'operate-forbid'
                }>
                编辑
              </a>
            </span>
            <Divider type="vertical" />
            <span
              className="data-view"
              onClick={(event) => toAuth(record, event)}>
              <a>授权</a>
            </span>
            <Divider type="vertical" />

            {/* isImport ：0为未应用，1为已应用 */}
            {!record.isMeta && record.isImport !== 1 ? (
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
                  record.isMeta
                    ? message.error('带meta标识的数据源不能编辑、删除')
                    : message.error('数据源已授权给产品，不可删除');
                }}>
                <a className="operate-forbid">删除</a>
              </span>
            )}
          </>
        );
      },
    },
  ];
};

export { columns };
