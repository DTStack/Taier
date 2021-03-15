import React from 'react';
import { Divider } from 'antd';

export const columns = [
  { title: '模型名称', dataIndex: 'modelName', key: 'modelName', width: 120 },
  { title: '模型英文名', dataIndex: 'modelEnName', key: 'modelEnName', width: 120 },
  {
    title: '数据源',
    dataIndex: '',
    width: 80,
    ellipsis: true,
    filters: [
      { text: 'aaa', value: 1 },
      { text: 'bbb', value: 2 },
    ],
    render: (text, record) => {
      return <span>{record.dsUrl}({record.dsTypeName})</span>
    }
  },
  {
    title: '状态',
    dataIndex: 'modelStatus',
    key: 'modelStatus',
    width: 80,
    filters: [
      { text: 'aaa', value: 1 },
      { text: 'bbb', value: 2 },
    ]
  },
  { title: '创建人', dataIndex: 'creator', key: 'creator', width: 120 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 120 },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 120 },
  { title: '备注', dataIndex: 'remark', key: 'remark', width: 120 },
  {
    title: '操作',
    dataIndex: '',
    width: 200,
    key: 'x',
    fixed: 'right',
    render: (text, record, index) => {
      if(index % 2 === 0) {
        return (
          <span>
            <a>发布</a>
            <Divider type="vertical" />
            <a>编辑</a>
            <Divider type="vertical" />
            <a>删除</a>
          </span>
        )
      } else {
        return (
          <span>
            <a>下线</a>
            <Divider type="vertical" />
            <a>编辑</a>
          </span>
        )
      }
    },
  },
];
