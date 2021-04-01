import React, { useCallback } from 'react';
import { Form, Select, Switch } from 'antd';

interface IPropsPartitionField {
  form: any;
  formValue: any;
}

const dateFmtList = [
  'yyyy-MM-dd HH:mm:ss',
  'yyyy-MM-dd HH:mm',
  'yyyy-MM-dd HH',
  'yyyy-MM-dd',
  'yyyy-MM',
];
const timeFmtList = ['HH:mm:ss', 'HH:mm', 'HH'];

const WrapperSwitch = React.forwardRef((props: any, ref: any) => {
  const _props = { ...props };
  delete _props.value;
  return <Switch checked={props.value} ref={ref} {..._props} />;
});

/**
 * 设置时间分区后，其他项均为必填项
 */
const PartitionField = (props: IPropsPartitionField) => {
  const { form, formValue } = props;
  const { getFieldDecorator, getFieldsValue } = form;
  const currentForm = getFieldsValue();
  const { columns } = formValue;

  const rules = useCallback(
    (msg: string) => [
      { required: currentForm.modelPartition?.timePartition, message: msg },
    ],
    [currentForm.modelPartition?.timePartition]
  );

  return (
    <>
      <Form.Item label="分区字段（日期）">
        {getFieldDecorator('modelPartition.datePartitionColumn.columnName', {
          rules: rules('分区字段（日期）不可为空'),
        })(
          <Select placeholder="请选择分区字段（日期）">
            {columns.map((item) => {
              const key = `${item.schema}-${item.tableName}-${item.columnName}`;
              return (
                <Select.Option key={key} value={key}>
                  {item.columnName}
                </Select.Option>
              );
            })}
          </Select>
        )}
      </Form.Item>
      <Form.Item label="日期格式">
        {getFieldDecorator('modelPartition.dateFmt', {
          rules: rules('日期格式不可为空'),
        })(
          <Select placeholder="请选择日期格式">
            {dateFmtList.map((item) => (
              <Select.Option key={item} value={item}>
                {item}
              </Select.Option>
            ))}
          </Select>
        )}
      </Form.Item>
      <Form.Item label="是否设置时间分区">
        {getFieldDecorator('modelPartition.timePartition')(<WrapperSwitch />)}
      </Form.Item>
      <Form.Item label="分区字段（时间）">
        {getFieldDecorator('modelPartition.timePartitionColumn.columnName', {
          rules: rules('分区字段（时间）不可为空'),
        })(
          <Select placeholder="请选择分区字段（时间）">
            {columns.map((item) => {
              const key = `${item.schema}-${item.tableName}-${item.columnName}`;
              return (
                <Select.Option key={key} value={key}>
                  {item.columnName}
                </Select.Option>
              );
            })}
          </Select>
        )}
      </Form.Item>
      <Form.Item label="时间格式">
        {getFieldDecorator('modelPartition.timeFmt', {
          rules: rules('时间格式不可为空'),
        })(
          <Select placeholder="请选择时间格式">
            {timeFmtList.map((item) => (
              <Select.Option key={item} value={item}>
                {item}
              </Select.Option>
            ))}
          </Select>
        )}
      </Form.Item>
    </>
  );
};

export default PartitionField;
