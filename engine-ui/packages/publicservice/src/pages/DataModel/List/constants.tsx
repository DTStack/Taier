import React from 'react';
import { Divider } from 'antd';
import { EnumModelActionType, EnumModelStatus } from './types';

export const modelStatusMap = new Map([
  [EnumModelStatus.UNRELEASE, '未发布'],
  [EnumModelStatus.RELEASE, '已发布'],
  [EnumModelStatus.OFFLINE, '已下线'],
]);

const getColorByModelStatus = (status: EnumModelStatus) => {
  switch(status) {
    case EnumModelStatus.RELEASE:
      return '#12BC6A';
    case EnumModelStatus.UNRELEASE:
      return '#FF5F5C';
    case EnumModelStatus.OFFLINE:
      return '#BFBFBF'
  }
}

export const columnsGenerator = ({ handleModelAction, handleDeleteBtnClick, handleModelNameClick }) => {
  return [
    {
      title: '模型名称',
      dataIndex: 'modelName',
      key: 'modelName',
      width: 240,
      fixed: true,
      render: (modelName, record) => {
        return <a onClick={() => handleModelNameClick(record.id)}>
          {modelName}
        </a>
      }
    },
    { title: '模型英文名', dataIndex: 'modelEnName', key: 'modelEnName', width: 200 },
    {
      title: '数据源',
      dataIndex: '',
      width: 200,
      ellipsis: true,
      // TODO:
      filters: [
        {text: 'aaa', value: 1},
        {text: 'bbb', value: 2},
      ],
      render: (text, record) => {
        return (
          <span>
            {record.dsUrl}({record.dsTypeName})
          </span>
        );
      },
    },
    {
      title: '状态',
      dataIndex: 'modelStatus',
      key: 'modelStatus',
      width: 160,
      // TODO:
      filters: Array.from(modelStatusMap).map(item => ({ text: item[1], value: item[0] })),
      filterMultiple: true,
      onFilter: (value, record) => {
        return record.modelStatus === value;
      },
      render: (modelStatus) => {
        return (
          <div>
            <div
              className="dot"
              style={{ background: getColorByModelStatus(modelStatus) }}
            />
            {modelStatusMap.get(modelStatus)}
          </div>
        )
      }
    },
    { title: '创建人', dataIndex: 'creator', key: 'creator', width: 160 },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 200 },
    { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 200 },
    { title: '备注', dataIndex: 'remark', key: 'remark', width: 200 },
    {
      title: '操作',
      key: 'operation',
      width: 200,
      fixed: 'right',
      render: (text, record) => {
        const isPublished = record.modelStatus === 1;
        const btnRelease = (
          <a onClick={() => handleModelAction({ type: EnumModelActionType.RELEASE, id: record.id })}>发布</a>
        );
        const btnUnrelease = (
          <a onClick={() => handleModelAction({ type: EnumModelActionType.UNRELEASE, id: record.id })}>下线</a>
        );
        const btnDelete = (isPublish: boolean) => {
          if(!isPublish) {
            return (
              <a onClick={() => {
                handleDeleteBtnClick(record.id);
              }}>删除</a>
            )
          } else {
            return <span className="btn-disabled">删除</span>
          }
        }
        // TODO:
        const btnEdit = (
          <a onClick={() => {
            alert('编辑')
          }}>编辑</a>
        )
        return (
          <span>
            { isPublished ? btnUnrelease : btnRelease }
            <Divider type="vertical" />
            {btnEdit}
            <Divider type="vertical" />
            {btnDelete(isPublished)}
          </span>
        )
      },
    },
  ];
};
