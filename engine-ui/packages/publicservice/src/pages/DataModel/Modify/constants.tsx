import { IFormItem, EnumFormItemType } from './FormRender/types';
import _ from 'lodash';

const idGenerator = () => {
  let _id = 0;
  return () => {
    return ++_id;
  }
}
const id = idGenerator();

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
  onSchemaChange,
  visibleUpdateType,
  joinList,
  onRelationListDelete,
  onRelationListEdit,
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
      ext: {
        onChange: onSchemaChange,
      }
    },
    {
      key: 'table',
      label: '选择表',
      type: EnumFormItemType.SELECT,
      placeholder: '请选择表',
      rules: [
        { required: true, message: '请选择表' },
      ],
      options: tableListOptions || []
    },
    {
      key: 'updateType',
      label: '更新方式',
      type: EnumFormItemType.SELECT,
      placeholder: '请选择更新方式',
      visible: visibleUpdateType,
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
        data: joinList,
        onRelationListDelete,
        onRelationListEdit,
      }
    }
  ]
}

// 添加关联表，form数据转换
export const joinItemParser = (data) => {
  const filterKeys = [];
  const unFilterKeys = [];
  Object.keys(data).forEach(key => {
    if(/^relation-key/.test(key)) {
      filterKeys.push(key);
    } else {
      unFilterKeys.push(key);
    }
  })
  const target = unFilterKeys.reduce((temp, cur) => {
    temp[cur] = data[cur];
    return temp;
  }, {});
  // id注入
  target.id = id();
  // 根据index分组
  const group = _.groupBy(filterKeys, key => key.split('_')[1]);
  /**
   *  {
   *    0: ['relation-left_0', 'relation-right_0'],
   *    1: ['relation-left_0', 'relation-right_0']
   *  }
   */
  target.joinPairs = Object.keys(group)
    .map(index => {
      const target = group[index];
      return target.reduce((temp, cur) => {
        // TODO: 字段
        if(/left/.test(cur)) {
          temp.leftValue = data[cur];
        } else if(/right/.test(cur)) {
          temp.rightValue = data[cur];
        }
        return temp;
      }, {})
    })
  return target;
}
