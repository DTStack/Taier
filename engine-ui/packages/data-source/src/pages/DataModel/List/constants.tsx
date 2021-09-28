import React from 'react';
import { Divider, Icon } from 'antd';
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

const container = (isPublished: boolean) => {
  return (title, action) => {
    return (
      <a
        className={classnames({
          'btn-link': !isPublished,
          'btn-disabled': isPublished,
        })}
        onClick={() =>
          !isPublished && typeof action === 'function' && action()
        }>
        {title}
      </a>
    );
  };
};

export const columnsGenerator = ({
  handleModelAction,
  handleDeleteBtnClick,
  handleModelNameClick,
  dataSourceFilterOptions,
  handleEditBtnClick,
}) => {
  return [
    {
      title: '模型名称',
      dataIndex: 'modelName',
      key: 'modelName',
      width: 240,
      fixed: true,
      ellipsis: true,
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
      title: '模型编码',
      dataIndex: 'modelEnName',
      key: 'modelEnName',
      width: 200,
      ellipsis: true,
    },
    {
      title: '数据源',
      dataIndex: 'dataSourceType',
      width: 200,
      ellipsis: true,
      filters: dataSourceFilterOptions,
      filterIcon: (filtered) => (
        <Icon
          component={() => (
            <span
              className={classnames({
                iconfont2: true,
                iconFilltianchong_shaixuan: true,
                'icon-filter': true,
                filtered: filtered,
              })}
            />
          )}
          style={{ cursor: 'pointer', position: 'relative', width: 16 }}
        />
      ),
      filterMultiple: true,
      render: (text, record) => {
        return `${record.dsName}(${record.dsTypeName})`;
      },
    },
    {
      title: '状态',
      dataIndex: 'modelStatus',
      key: 'modelStatus',
      width: 160,
      ellipsis: true,
      filters: Array.from(modelStatusMap).map((item) => ({
        text: item[1],
        value: item[0],
      })),
      filterMultiple: true,
      filterIcon: (filtered) => (
        <Icon
          component={() => (
            <span
              className={classnames({
                iconfont2: true,
                iconFilltianchong_shaixuan: true,
                'icon-filter': true,
                filtered: filtered,
              })}
            />
          )}
          style={{
            cursor: 'pointer',
            position: 'relative',
            width: 16,
            marginLeft: 4,
          }}
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
    {
      title: '创建人',
      dataIndex: 'creator',
      key: 'creator',
      width: 160,
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 200,
      ellipsis: true,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 200,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      width: 200,
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'operation',
      width: 200,
      fixed: 'right',
      render: (text, record) => {
        const isPublished = record.modelStatus === 1;
        const wrapper = container(isPublished);
        const relaeseWrapper = container(false);
        const btnRelease = relaeseWrapper('发布', () =>
          handleModelAction({
            type: EnumModelActionType.RELEASE,
            id: record.id,
          })
        );
        const btnUnrelease = relaeseWrapper('下线', () =>
          handleModelAction({
            type: EnumModelActionType.UNRELEASE,
            id: record.id,
          })
        );
        const btnDelete = wrapper('删除', () =>
          handleDeleteBtnClick(record.id)
        );
        const btnEdit = wrapper('编辑', () => handleEditBtnClick(record.id));
        return (
          <span>
            {isPublished ? btnUnrelease : btnRelease}
            <Divider type="vertical" />
            {btnEdit}
            <Divider type="vertical" />
            {btnDelete}
          </span>
        );
      },
    },
  ];
};
