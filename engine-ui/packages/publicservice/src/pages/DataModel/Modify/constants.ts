import { IFormItem, EnumFormItemType } from './FormRender/types';
import _ from 'lodash';
import { EnumModifyStep } from './types';
import { API } from '@/services';
const idGenerator = () => {
  let _id = 0;
  return () => {
    return ++_id;
  };
};
const id = idGenerator();

interface BasicInfoParams {
  options: any[];
  onDataSourceChange: Function;
  id?: number;
  isDisabled: boolean;
}

// 基础信息表单配置
export const basicInfoFormListGenerator = (
  params: BasicInfoParams
): IFormItem[] => {
  const { options, onDataSourceChange, id, isDisabled } = params;
  return [
    {
      key: 'modelName',
      label: '模型名称',
      type: EnumFormItemType.INPUT,
      placeholder: '请输入模型名称',
      rules: [
        { required: true, message: '请输入模型名称' },
        { max: 50, message: '不超过50个字符' },
        {
          pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/g,
          message: '仅支持中文、字母、数字和下划线',
        },
        {
          validator: async (rule, value, callback) => {
            const { success, data, message } = await API.repeatValidate({
              fieldCode: 1,
              value,
              id,
            });
            if (success && data) {
              callback('模型名称不能重复');
            } else if (success && !data) {
              callback();
            } else {
              callback(message);
            }
          },
        },
      ],
    },
    {
      key: 'modelEnName',
      label: '模型英文名',
      type: EnumFormItemType.INPUT,
      placeholder: '清输入模型英文名',
      rules: [
        { required: true, message: '请输入模型英文名' },
        { max: 50, message: '不超过50个字符' },
        { pattern: /^[a-zA-Z0-9_]+$/g, message: '仅支持字母、数字和下划线' },
        {
          validator: async (rule, value, callback) => {
            const { success, data, message } = await API.repeatValidate({
              fieldCode: 2,
              value,
              id,
            });
            if (success && data) {
              callback('模型英文名称不能重复');
            } else if (success && !data) {
              callback();
            } else {
              callback(message);
            }
          },
        },
      ],
      ext: {
        disabled: isDisabled,
      },
    },
    {
      key: 'dsId',
      label: '数据源',
      type: EnumFormItemType.SELECT,
      placeholder: '请选择数据源',
      rules: [{ required: true, message: '请选择数据源' }],
      options: options || [],
      ext: {
        onChange: onDataSourceChange,
        disabled: isDisabled,
      },
    },
    {
      key: 'remark',
      label: '备注',
      type: EnumFormItemType.TEXT_AREA,
      placeholder: '清输入备注，不超过50个字',
      rules: [{ max: 50, message: '不超过50个字' }],
    },
  ];
};

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
  // onMasterTableChange,
}): IFormItem[] => {
  return [
    {
      key: 'schema',
      label: 'schema',
      type: EnumFormItemType.SELECT,
      placeholder: '请选择schema',
      rules: [{ required: true, message: '请选择schema' }],
      options: schemaListOptions || [],
      ext: {
        onChange: onSchemaChange,
      },
    },
    {
      key: 'tableName',
      label: '选择表',
      type: EnumFormItemType.SELECT,
      placeholder: '请选择表',
      rules: [{ required: true, message: '请选择表' }],
      options: tableListOptions || [],
      // ext: {
      //   // onChange: onMasterTableChange,
      // },
    },
    {
      key: 'updateType',
      label: '更新方式',
      type: EnumFormItemType.SELECT,
      placeholder: '请选择更新方式',
      visible: visibleUpdateType,
      rules: [{ required: true, message: '请选择更新方式' }],
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
      },
    },
  ];
};

// 添加关联表，form数据转换
export const joinItemParser = (data) => {
  const filterKeys = [];
  const unFilterKeys = [];
  Object.keys(data).forEach((key) => {
    if (/^relation-key/.test(key)) {
      filterKeys.push(key);
    } else {
      unFilterKeys.push(key);
    }
  });
  const target = unFilterKeys.reduce((temp, cur) => {
    temp[cur] = data[cur];
    return temp;
  }, {});
  // id注入
  target.id = id();
  // 根据index分组
  const group = _.groupBy(filterKeys, (key) => key.split('_')[1]);
  /**
   *  {
   *    0: ['relation-left_0', 'relation-right_0'],
   *    1: ['relation-left_0', 'relation-right_0']
   *  }
   */
  target.joinPairs = Object.keys(group).map((index) => {
    const target = group[index];
    return target.reduce((temp, cur) => {
      if (/left/.test(cur)) {
        temp.leftValue = data[cur];
      } else if (/right/.test(cur)) {
        temp.rightValue = data[cur];
      }
      return temp;
    }, {});
  });
  return target;
};

// export const stepContentRender = (step: EnumModifyStep, props: any) => {
//   const { form, cref, formValue } = props;
//   switch (step) {
//     case EnumModifyStep.BASIC_STEP:
//       return <FormRender form={form} formList={props.formList || []} />;
//     case EnumModifyStep.RELATION_TABLE_STEP:
//       return <FormRender form={form} formList={props.formList || []} />;
//     case EnumModifyStep.DIMENSION_STEP:
//       return <FieldsSelect step={step} cref={cref} formValue={formValue} />;
//     case EnumModifyStep.METRIC_STEP:
//       return <FieldsSelect step={step} cref={cref} formValue={formValue} />;
//     case EnumModifyStep.SETTING_STEP:
//       return <PartitionField form={form} formValue={formValue} />;
//   }
// };

export const layoutGenerator = (step: EnumModifyStep) => {
  switch (step) {
    case EnumModifyStep.BASIC_STEP:
    case EnumModifyStep.RELATION_TABLE_STEP:
      return {
        labelCol: { span: 3 },
        wrapperCol: { span: 21 },
      };
    case EnumModifyStep.SETTING_STEP:
      return {
        labelCol: { span: 5 },
        wrapperCol: { span: 19 },
      };
  }
};

export const restoreKeysMap = new Map([
  [EnumModifyStep.BASIC_STEP, ['modelName', 'modelEnName', 'dsId', 'remark']],
  [EnumModifyStep.RELATION_TABLE_STEP, ['schema', 'tableName', 'updateType']],
  [EnumModifyStep.DIMENSION_STEP, ['columns']],
  [EnumModifyStep.METRIC_STEP, ['columns']],
  [EnumModifyStep.SETTING_STEP, ['modelPartition']],
]);
