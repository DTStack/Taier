import { IFormItem, EnumFormItemType } from './FormRender/types';

export const formList: IFormItem[] = [
  {
    key: 'modelName',
    label: '模型名称',
    type: EnumFormItemType.INPUT,
    placeholder: '请输入模型名称',
    rules: [
      { required: true, message: '请输入模型名称' },
    ]
  },
  {
    key: 'modelEnName',
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

export const formListGenerator = ({ handleClick }) => {
  return [
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
    },
    {
      key: 'relationList',
      label: '',
      type: EnumFormItemType.RELATION_LIST,
      ext: {
        onClick: handleClick,
      }
    }
  ]
}

