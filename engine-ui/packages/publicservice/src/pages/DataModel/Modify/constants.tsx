import { IFormItem, EnumFormItemType } from './FormRender/types';

// 基础信息表单配置
export const basicInfoFormListGenerator = (options: any[]): IFormItem[] => {
  return [
    {
      key: 'modelName',
      label: '模型名称',
      type: EnumFormItemType.INPUT,
      placeholder: '请输入模型名称',
      rules: [
        { required: true, message: '请输入模型名称' },
        { max: 50, message: '不超过50个字符' },
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
      key: 'dataSource',
      label: '数据源',
      type: EnumFormItemType.SELECT,
      placeholder: '请选择数据源',
      rules: [
        { required: true, message: '请选择数据源' },
      ],
      options: options || [],
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
  ];
}

// 关联表信息form表单配置
export const relationFormListGenerator = ({
  handleClick,
  updateTyleListOptions,
  schemaListOptions,
  tableListOptions,
}): IFormItem[] => {
  return [
    {
      key: 'schema',
      label: 'schema',
      type: EnumFormItemType.SELECT,
      placeholder: '请选择schema',
      rules: [
        { required: true, message: '请选择schema' },
      ],
      options: schemaListOptions || [],
    },
    {
      key: 'table',
      label: '选择表',
      type: EnumFormItemType.SELECT,
      placeholder: '请选择表',
      rules: [
        { required: true, message: '请选择表' },
      ],
      options: tableListOptions || [],
    },
    {
      key: 'updateType',
      label: '更新方式',
      type: EnumFormItemType.SELECT,
      placeholder: '请选择更新方式',
      rules: [
        { required: true, message: '请选择更新方式' }
      ],
      options: updateTyleListOptions || [],
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
