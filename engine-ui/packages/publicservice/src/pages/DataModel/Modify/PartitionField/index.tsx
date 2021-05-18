import React, { useCallback, useEffect, useImperativeHandle } from 'react';
import { Form, Select, Switch, Row, Col, Tooltip } from 'antd';
import { IModelDetail, EnumModelStatus } from '@/pages/DataModel/types';
import './style';
import { EnumModifyMode } from '../types';

interface IPropsPartitionField {
  form?: any;
  cref: any;
  modelDetail?: IModelDetail;
  mode?: EnumModifyMode;
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
  return (
    <Switch
      className="margin-top-5"
      checked={props.value}
      ref={ref}
      {..._props}
    />
  );
});

const layout = {
  wrapperCol: {
    span: 19,
  },
  labelCol: {
    span: 5,
  },
};

/**
 * 设置时间分区后，其他项均为必填项
 */
const PartitionField = (props: IPropsPartitionField) => {
  const { form, cref, modelDetail, mode } = props;
  const {
    getFieldDecorator,
    getFieldsValue,
    validateFields,
    setFieldsValue,
  } = form;
  const currentForm = getFieldsValue();
  const { columns = [] } = modelDetail;
  // 新增模型或者模型处于未发布状态下可编辑分区字段
  const isEnabledPartition =
    modelDetail.modelStatus === EnumModelStatus.UNRELEASE ||
    mode === EnumModifyMode.ADD;
  // TODO:树形列表功能
  // console.log(columns)

  const columnSrtingParser = {
    decode: (str: string) => {
      if (!str) return {};
      const [schema, tableName, columnName] = str.split('-');
      return {
        schema,
        tableName,
        columnName,
      };
    },
    encode: (obj) => {
      if (!obj) obj = {};
      const { schema, tableName, columnName } = obj;
      if (!schema || !tableName || !columnName) return undefined;
      return `${schema}-${tableName}-${columnName}`;
    },
  };

  const modelPartitionParser = (data): void => {
    const dateColString =
      data.modelPartition?.datePartitionColumn?.columnName || '';
    const datePartition = columnSrtingParser.decode(dateColString);
    data.modelPartition.datePartitionColumn = datePartition;
    const timeColString =
      data.modelPartition?.timePartitionColumn?.columnName || '';
    const timePartition = columnSrtingParser.decode(timeColString);
    data.modelPartition.timePartitionColumn = timePartition;
  };

  useImperativeHandle(cref, () => {
    return {
      validate: () =>
        new Promise((resolve, reject) => {
          validateFields((error, data) => {
            if (error) reject(error);
            // 数据格式转换
            modelPartitionParser(data);
            if (!data.modelPartition.timePartition) {
              data.modelPartition.timePartitionColumn = null;
              data.modelPartition.timeFmt = null;
            }
            return resolve(data);
          });
        }),
      getValue: () => {
        const data = getFieldsValue();
        modelPartitionParser(data);
        return data;
      },
    };
  });

  useEffect(() => {
    const dateColumn = modelDetail?.modelPartition?.datePartitionColumn;
    // const timeColumn = modelDetail?.modelPartition?.timePartitionColumn;
    // const timeFmt = modelDetail?.modelPartition?.timeFmt;
    const dateFmt = modelDetail?.modelPartition?.dateFmt;
    const timePartition = modelDetail?.modelPartition?.timePartition;
    setFieldsValue({
      modelPartition: {
        dateFmt: dateFmt === null ? undefined : dateFmt,
        datePartitionColumn: {
          columnName: dateColumn
            ? columnSrtingParser.encode(dateColumn)
            : undefined,
        },
        // timeFmt: timeFmt === null ? undefined : timeFmt,
        // timePartitionColumn: {
        //   columnName:
        //     timeColumn === null
        //       ? undefined
        //       : columnSrtingParser.encode(timeColumn),
        // },
        timePartition,
      },
    });

    console.log(getFieldsValue());
  }, [modelDetail]);

  const rules = useCallback(
    (msg: string) => [
      { required: currentForm.modelPartition?.timePartition, message: msg },
    ],
    [currentForm.modelPartition?.timePartition]
  );

  useEffect(() => {
    if (currentForm.modelPartition?.timePartition === true) {
      const timeColumn = modelDetail?.modelPartition?.timePartitionColumn;
      const timeFmt = modelDetail?.modelPartition?.timeFmt;
      const formValue = getFieldsValue();
      const modelPartition = {
        ...formValue.modelPartition,
        timeFmt: timeFmt === null ? undefined : timeFmt,
        timePartitionColumn: {
          columnName:
            timeColumn === null
              ? undefined
              : columnSrtingParser.encode(timeColumn),
        },
      };
      setFieldsValue({
        modelPartition,
      });
    }
  }, [currentForm.modelPartition?.timePartition]);

  return (
    <Form className=" padding-top-20 partition-field dm-form" {...layout}>
      <Row className="mb-12">
        <Col span={5}>
          <span className="text-main ml-32">分区设置</span>
          <Tooltip
            placement="right"
            title="如果数据模型不需要增量更新，可不设置分区">
            <i className="text-main-icon ml-8 iconfont2 iconOutlinedxianxing_Question" />
          </Tooltip>
        </Col>
      </Row>
      <Form.Item label="分区字段（日期）">
        {getFieldDecorator('modelPartition.datePartitionColumn.columnName', {
          rules: rules('分区字段（日期）不可为空'),
        })(
          <Select
            placeholder="请选择分区字段（日期）"
            dropdownClassName="dm-form-select-drop"
            showSearch
            disabled={!isEnabledPartition}
            optionFilterProp="children">
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
          <Select
            dropdownClassName="dm-form-select-drop"
            disabled={!isEnabledPartition}
            placeholder="请选择日期格式">
            {dateFmtList.map((item) => (
              <Select.Option key={item} value={item}>
                {item}
              </Select.Option>
            ))}
          </Select>
        )}
      </Form.Item>
      <Form.Item label="是否设置时间分区">
        {getFieldDecorator('modelPartition.timePartition')(
          <WrapperSwitch disabled={!isEnabledPartition} />
        )}
      </Form.Item>
      {currentForm.modelPartition?.timePartition ? (
        <>
          <Form.Item label="分区字段（时间）">
            {getFieldDecorator(
              'modelPartition.timePartitionColumn.columnName',
              {
                rules: rules('分区字段（时间）不可为空'),
              }
            )(
              <Select
                placeholder="请选择分区字段（时间）"
                dropdownClassName="dm-form-select-drop"
                showSearch
                disabled={!isEnabledPartition}
                optionFilterProp="children">
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
              <Select
                dropdownClassName="dm-form-select-drop"
                placeholder="请选择时间格式"
                disabled={!isEnabledPartition}>
                {timeFmtList.map((item) => (
                  <Select.Option key={item} value={item}>
                    {item}
                  </Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
        </>
      ) : null}
    </Form>
  );
};

export default Form.create()(PartitionField) as any;
