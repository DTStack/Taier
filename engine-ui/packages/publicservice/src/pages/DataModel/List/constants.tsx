import React from 'react';
import { Divider } from 'antd';
import { EnumModelActionType } from './types';
import { EnumModelStatus } from 'pages/DataModel/types';
import classnames from 'classnames';

export const modelStatusMap = new Map([
  [EnumModelStatus.UNRELEASE, '未发布'],
  [EnumModelStatus.RELEASE, '已发布'],
  [EnumModelStatus.OFFLINE, '已下线'],
]);

const getColorByModelStatus = (status: EnumModelStatus) => {
  switch (status) {
    case EnumModelStatus.RELEASE:
      return '#12BC6A';
    case EnumModelStatus.UNRELEASE:
      return '#FF5F5C';
    case EnumModelStatus.OFFLINE:
      return '#BFBFBF';
  }
};

export const columnsGenerator = ({
  handleModelAction,
  handleDeleteBtnClick,
  handleModelNameClick,
  dataSourceFilterOptions,
  router,
}) => {
  return [
    {
      title: '模型名称',
      dataIndex: 'modelName',
      key: 'modelName',
      width: 240,
      fixed: true,
      render: (modelName, record) => {
        return (
          <a
            onClick={() => handleModelNameClick(record.id)}
            className="btn-link">
            {modelName}
          </a>
        );
      },
    },
    {
      title: '模型英文名',
      dataIndex: 'modelEnName',
      key: 'modelEnName',
      width: 200,
    },
    {
      title: '数据源',
      dataIndex: 'dataSourceType',
      width: 200,
      ellipsis: true,
      filters: dataSourceFilterOptions,
      filterIcon: (filtered) => (
        <span
          className={classnames({
            iconfont2: true,
            iconOutlinedxianxing_filter: true,
            'icon-filter': true,
            filtered: filtered,
          })}
        />
      ),
      filterMultiple: true,
      render: (text, record) => {
        return (
          <span>
            {record.dsName}({record.dsTypeName})
          </span>
        );
      },
    },
    {
      title: '状态',
      dataIndex: 'modelStatus',
      key: 'modelStatus',
      width: 160,
      filters: Array.from(modelStatusMap).map((item) => ({
        text: item[1],
        value: item[0],
      })),
      filterMultiple: true,
      filterIcon: (filtered) => (
        <span
          className={classnames({
            iconfont2: true,
            iconOutlinedxianxing_filter: true,
            'icon-filter': true,
            filtered: filtered,
          })}
        />
      ),
      render: (modelStatus) => {
        return (
          <div>
            <div
              className="dot"
              style={{ background: getColorByModelStatus(modelStatus) }}
            />
            {modelStatusMap.get(modelStatus)}
          </div>
        );
      },
    },
    { title: '创建人', dataIndex: 'creator', key: 'creator', width: 160 },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 200,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 200,
    },
    { title: '备注', dataIndex: 'remark', key: 'remark', width: 200 },
    {
      title: '操作',
      key: 'operation',
      width: 200,
      fixed: 'right',
      render: (text, record) => {
        const isPublished = record.modelStatus === 1;
        const btnRelease = (
          <a
            className="btn-link"
            onClick={() =>
              handleModelAction({
                type: EnumModelActionType.RELEASE,
                id: record.id,
              })
            }>
            发布
          </a>
        );
        const btnUnrelease = (
          <a
            className="btn-link"
            onClick={() =>
              handleModelAction({
                type: EnumModelActionType.UNRELEASE,
                id: record.id,
              })
            }>
            下线
          </a>
        );
        const btnDelete = (isPublish: boolean) => {
          if (!isPublish) {
            return (
              <a
                className="btn-link"
                onClick={() => {
                  handleDeleteBtnClick(record.id);
                }}>
                删除
              </a>
            );
          } else {
            return <span className="btn-disabled">删除</span>;
          }
        };
        const btnEdit = (
          <a
            className="btn-link"
            onClick={() => {
              router.push(`/data-model/edit/${record.id}`);
            }}>
            编辑
          </a>
        );
        return (
          <span>
            {isPublished ? btnUnrelease : btnRelease}
            <Divider type="vertical" />
            {btnEdit}
            <Divider type="vertical" />
            {btnDelete(isPublished)}
          </span>
        );
      },
    },
  ];
};
