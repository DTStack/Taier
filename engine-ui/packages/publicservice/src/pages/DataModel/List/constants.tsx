import React from 'react';
import { Divider } from 'antd';
import { modelStatusMap } from '../constants';
import { EnumModalActionType } from './types';

export const columnsGenerator = ({
  handleModelAction,
  handleDeleteBtnClick,
}) => {
  return [
    { title: '模型名称', dataIndex: 'modelName', key: 'modelName', width: 120 },
    {
      title: '模型英文名',
      dataIndex: 'modelEnName',
      key: 'modelEnName',
      width: 120,
    },
    {
      title: '数据源',
      dataIndex: '',
      width: 80,
      ellipsis: true,
      // TODO:
      filters: [
        { text: 'aaa', value: 1 },
        { text: 'bbb', value: 2 },
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
      width: 80,
      // TODO:
      filters: [
        { text: 'aaa', value: 1 },
        { text: 'bbb', value: 2 },
      ],
      render: (modelStatus) => {
        return modelStatusMap.get(modelStatus);
      },
    },
    { title: '创建人', dataIndex: 'creator', key: 'creator', width: 120 },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 120,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 120,
    },
    { title: '备注', dataIndex: 'remark', key: 'remark', width: 120 },
    {
      title: '操作',
      dataIndex: '',
      width: 200,
      key: 'x',
      fixed: 'right',
      render: (text, record, index) => {
        const isPublished = record.modelStatus === 1;
        const btnRelease = (
          <a
            onClick={() =>
              handleModelAction({
                type: EnumModalActionType.RELEASE,
                id: record.id,
              })
            }>
            发布
          </a>
        );
        const btnUnrelease = (
          <a
            onClick={() =>
              handleModelAction({
                type: EnumModalActionType.UNRELEASE,
                id: record.id,
              })
            }>
            下线
          </a>
        );
        const btnDelete = (
          <a
            onClick={() => {
              handleDeleteBtnClick(record.id);
            }}>
            删除
          </a>
        );
        // TODO:
        const btnEdit = (
          <a
            onClick={() => {
              alert('编辑');
            }}>
            编辑
          </a>
        );
        if (!isPublished) {
          return (
            <span>
              {btnRelease}
              <Divider type="vertical" />
              {btnEdit}
              <Divider type="vertical" />
              {btnDelete}
            </span>
          );
        } else {
          return (
            <span>
              {btnUnrelease}
              <Divider type="vertical" />
              {btnEdit}
            </span>
          );
        }
      },
    },
  ];
};
