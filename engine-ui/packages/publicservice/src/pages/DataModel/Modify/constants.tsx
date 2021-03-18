import React from 'react';
import { Divider } from 'antd';
import { IFormItem, EnumFormItemType } from './FormRender/types';

export const formList: IFormItem[] = [
  {
    key: 'labelName',
    label: '模型名称',
    type: EnumFormItemType.INPUT,
    placeholder: '请输入模型名称',
    rules: [
      { required: true, message: '请输入模型名称' },
    ]
  },
  {
    key: 'labelEnName',
    label: '模型英文名',
    type: EnumFormItemType.INPUT,
    placeholder: '清输入模型英文名',
    rules: [
      { required: true, message: '请输入模型英文名' }
    ]
  },
  {
    key: 'dataSourceType',
    label: '数据源',
    type: EnumFormItemType.SELECT,
    placeholder: '请选择数据源',
    rules: [
      { required: true, message: '请选择数据源' },
    ],
    options: [
      { key: 1, value: 1, label: 'aaa' },
      { key: 2, value: 2, label: 'bbb' },
    ]
  },
  {
    key: 'remark',
    label: '备注',
    type: EnumFormItemType.TEXT_AREA,
    placeholder: '清输入备注，不超过50个字',
    rules: [
      { maxLength: 50, message: '不超过50个字' }
    ]
  }
]

export const formList2: IFormItem[] = [
  {
    key: 'schema',
    label: 'schema',
    type: EnumFormItemType.SELECT,
    placeholder: '请选择表',
    rules: [
      { required: true, message: '请选择表' },
    ]
  },
  {
    key: 'table',
    label: '选择表',
    type: EnumFormItemType.SELECT,
    placeholder: '请选择表',
    rules: [
      { required: true, message: '请选择表' },
    ]
  },
  {
    key: 'updateType',
    label: '更新方式',
    type: EnumFormItemType.SELECT,
    placeholder: '请选择更新方式',
    rules: [
      { required: true, message: '请选择更新方式' }
    ]
  }
]

export const columns = [
  {
    title: '序号',
    dataIndex: 'name',
    key: 'Name',
    width: 80,
    ellipsis: true,
  },
  {
    title: '表别名',
    dataIndex: 'age',
    key: 'age',
    width: 100,
    ellipsis: true,
  },
  {
    title: 'schema',
    dataIndex: 'address',
    key: 'address',
    width: 120,
    ellipsis: true,
  },
  {
    title: '表名',
    dataIndex: 'age',
    key: 'age',
    width: 100,
    ellipsis: true,
  },
  {
    title: '关联类型',
    dataIndex: 'address',
    key: 'address',
    width: 120,
    ellipsis: true,
  },
  {
    title: '关联条件',
    dataIndex: 'address',
    key: 'address',
    width: 160,
    ellipsis: true,
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
    // type hack
    fixed: 'right' as 'right',
    render: (text, record) => (
      <span>
        <a>编辑</a>
        <Divider type="vertical" />
        <a>删除</a>
      </span>
    ),
  }
];

export const data = [
  {
    key: '1',
    name: 'John Brown',
    age: 32,
    address: 'New York No. 1 Lake Park',
  },
  {
    key: '2',
    name: 'Jim Green',
    age: 42,
    address: 'London No. 1 Lake Park',
  },
  {
    key: '3',
    name: 'Joe Black',
    age: 32,
    address: 'Sidney No. 1 Lake Park',
  }
];
