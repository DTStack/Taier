import React from 'react';
import { IFormItem, EnumFormItemType } from './FormRender/types';
import _ from 'lodash';
import FormRender from './FormRender';
import { EnumModifyStep } from './types';
import FieldsSelect from './FieldsSelect';
const idGenerator = () => {
  let _id = 0;
  return () => {
    return ++_id;
  };
};
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
        {
          pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/g,
          message: '仅支持中文、字母、数字和下划线',
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
      ],
    },
    {
      key: 'dsId',
      label: '数据源',
      type: EnumFormItemType.SELECT,
      placeholder: '请选择数据源',
      rules: [{ required: true, message: '请选择数据源' }],
      options: options || [],
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
  onMasterTableChange,
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
      ext: {
        onChange: onMasterTableChange,
      },
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

const dateFmtList = [
  'yyyy-MM-dd HH:mm:ss',
  'yyyy-MM-dd HH:mm',
  'yyyy-MM-dd HH',
  'yyyy-MM-dd',
  'yyyy-MM',
];
const timeFmtList = ['HH:mm:ss', 'HH:mm', 'HH'];

export const settingFormListgenerator = (columns): IFormItem[] => {
  return [
    {
      type: EnumFormItemType.SELECT,
      label: '分区字段（日期）',
      placeholder: '请选择分区字段（日期）',
      key: 'modelPartition.datePartitionColumn.columnName',
      options: columns.map((item) => ({
        key: `${item.schema}-${item.tableName}-${item.columnName}`,
        label: item.columnName,
        value: `${item.schema}-${item.tableName}-${item.columnName}`,
      })),
    },
    {
      key: 'modelPartition.dateFmt',
      label: '日期格式',
      placeholder: '请选择日期格式',
      type: EnumFormItemType.SELECT,
      options: dateFmtList.map((item) => ({
        label: item,
        key: item,
        value: item,
      })),
    },
    {
      key: 'modelPartition.timePartition',
      type: EnumFormItemType.SWITCH,
      label: '是否设置时间分区',
    },
    {
      type: EnumFormItemType.SELECT,
      label: '分区字段（时间）',
      placeholder: '请选择分区字段（时间）',
      key: 'modelPartition.timePartitionColumn.columnName',
      options: columns.map((item) => ({
        key: `${item.schema}-${item.tableName}-${item.columnName}`,
        label: item.columnName,
        value: `${item.schema}-${item.tableName}-${item.columnName}`,
      })),
    },
    {
      key: 'modelPartition.timeFmt',
      label: '时间格式',
      placeholder: '请选择时间格式',
      type: EnumFormItemType.SELECT,
      options: timeFmtList.map((item) => ({
        key: item,
        label: item,
        value: item,
      })),
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
      // TODO: 字段
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

export const stepContentRender = (step: EnumModifyStep, props: any) => {
  const { form, cref, formValue } = props;
  switch (step) {
    case EnumModifyStep.BASIC_STEP:
      return <FormRender form={form} formList={props.formList || []} />;
    case EnumModifyStep.RELATION_TABLE_STEP:
      return <FormRender form={form} formList={props.formList || []} />;
    case EnumModifyStep.DIMENSION_STEP:
      return <FieldsSelect step={step} cref={cref} formValue={formValue} />;
    case EnumModifyStep.METRIC_STEP:
      return <FieldsSelect step={step} cref={cref} formValue={formValue} />;
    case EnumModifyStep.SETTING_STEP:
      return <FormRender form={form} formList={props.formList || []} />;
  }
};

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
