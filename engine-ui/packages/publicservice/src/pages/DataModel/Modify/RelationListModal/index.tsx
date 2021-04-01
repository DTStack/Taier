import React, { useEffect, useImperativeHandle, useRef, useState } from 'react';
import { Form, Select, Input, Row, Col } from 'antd';
import DynamicSelectList from '../DynamicSelectList';
import './style';
import { joinTypeList, updateTypeList } from './constants';
import { TableJoinInfo } from 'pages/DataModel/types';
import { API } from '@/services';
import Message from '../../components/Message';

const formItemLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 20 },
};

enum EnumRelationModifyMode {
  EDIT = 'EDIT',
  ADD = 'ADD',
}

interface ITableItem {
  id: number;
  tableName: string;
  schema: string;
  tableAlias: string;
}
interface IPropsRelationTableModal {
  form?: any;
  cref: any;
  data?: TableJoinInfo;
  tables: ITableItem[];
  schemaList: any[];
  dataSourceId: number;
}

const RelationTableModal = (props: IPropsRelationTableModal) => {
  const {
    getFieldDecorator,
    validateFields,
    setFieldsValue,
    getFieldsValue,
  } = props.form;
  const { cref, data, tables, schemaList, dataSourceId } = props;
  const mode = data ? EnumRelationModifyMode.EDIT : EnumRelationModifyMode.ADD;
  const id = mode === EnumRelationModifyMode.EDIT ? data.id : undefined;
  const isDisabled = mode === EnumRelationModifyMode.EDIT ? true : false;
  useEffect(() => {
    if (data) {
      setFieldsValue({
        ...data,
        joinPairs: undefined,
      });
    }
  }, []);

  const [tableList, setTableList] = useState([]);
  const firstRender = useRef(true);
  const [leftColLsit, setLeftColList] = useState([]);
  const [rightColList, setRightColList] = useState([]);
  const [visibleUpdateType, setVisibleUpdateType] = useState(false);

  const validate = (callback) => {
    validateFields((err, data) => {
      callback(err, data, id);
    });
  };

  useImperativeHandle(cref, () => ({
    // validate 就是暴露给父组件的方法
    validate,
  }));

  const requiredRule = (message: string) => {
    return [{ required: true, message }];
  };

  const getDataModelTableList = async (
    datasourceId: number,
    schema: string
  ) => {
    try {
      const { success, data, message } = await API.getDataModelTableList({
        datasourceId,
        schema,
      });
      if (success) {
        setTableList(data);
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  const currentFormValue = getFieldsValue();

  useEffect(() => {
    getDataModelTableList(dataSourceId, currentFormValue.schema);
    if (!currentFormValue.schema) return;
    if (firstRender.current) {
      firstRender.current = false;
      return;
    }
    setFieldsValue({
      table: undefined,
    });
  }, [currentFormValue.schema]);

  const getColumnList = (
    datasourceId: number,
    schema: string,
    tableNames: string[]
  ) => {
    return API.getDataModelColumns({
      datasourceId,
      schema,
      tableNames,
    });
  };
  // 表别名重复性校验
  const repeatValidator = (rule, value, callback) => {
    let filter = (v) => true;
    if (mode === EnumRelationModifyMode.EDIT) {
      // 编辑状态下需要过滤当前id的表名
      filter = (item) => item.id !== id;
    }
    const isRepeat =
      tables
        .filter((item) => item.tableAlias)
        .filter(filter)
        .findIndex((item) => item.tableAlias === value) === -1;
    if (isRepeat) {
      callback();
    } else {
      callback('表别名不能重复');
    }
  };

  useEffect(() => {
    if (!currentFormValue || !currentFormValue.leftTable) return;
    const [schema, tableName] = currentFormValue.leftTable.split('-');
    getColumnList(dataSourceId, schema, [tableName])
      .then(({ success, data, message }) => {
        if (success) {
          setLeftColList(data);
        } else {
          Message.error(message);
        }
      })
      .catch((err) => {
        Message.error(err.message);
      });
  }, [currentFormValue.leftTable]);

  useEffect(() => {
    if (!currentFormValue.table || !currentFormValue.schema) return;
    getColumnList(dataSourceId, currentFormValue.schema, [
      currentFormValue.table,
    ])
      .then(({ success, data, message }) => {
        if (success) {
          setRightColList(data);
        } else {
          Message.error(message);
        }
      })
      .catch((err) => {
        Message.error(err.message);
      });
  }, [currentFormValue.table, currentFormValue.schema]);

  return (
    <div ref={cref} className="relation-table-modal">
      <Form layout="horizontal" {...formItemLayout}>
        <Form.Item label="选择表" required={false}>
          {getFieldDecorator('leftTable', {
            rules: requiredRule('请选择表'),
          })(
            <Select placeholder="请选择表">
              {tables.map((item) => (
                <Select.Option
                  key={`${item.tableName}-${item.schema}`}
                  value={`${item.tableName}-${item.schema}`}>
                  {item.tableName}
                </Select.Option>
              ))}
            </Select>
          )}
        </Form.Item>
        <Form.Item label="关联关系" required={false}>
          {getFieldDecorator('joinType', {
            rules: requiredRule('请选择关联关系'),
          })(
            <Select placeholder="请选择关联关系">
              {joinTypeList.map((item) => (
                <Select.Option key={item.key} value={item.key}>
                  {item.label}
                </Select.Option>
              ))}
            </Select>
          )}
        </Form.Item>
        <Form.Item label="schema" required={false}>
          {getFieldDecorator('schema', {
            rules: requiredRule('请选择scehma'),
          })(
            <Select placeholder="请选择schema">
              {schemaList.map((schema) => (
                <Select.Option key={schema.key} value={schema.value}>
                  {schema.label}
                </Select.Option>
              ))}
            </Select>
          )}
        </Form.Item>
        <Form.Item label="关联表" required={false}>
          <Row>
            <Col span={11}>
              {getFieldDecorator('table', {
                rules: requiredRule('请选择关联表'),
              })(
                <Select
                  placeholder="请选择关联表"
                  onChange={(value, target) => {
                    const isPartition = (target as any).props['data-ext'];
                    setVisibleUpdateType(isPartition);
                  }}>
                  {tableList.map((item) => (
                    <Select.Option
                      key={item.tableName}
                      value={item.tableName}
                      data-ext={item.partition}>
                      {item.tableName}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Col>
            <Col span={2} className="text-align-center">
              AS
            </Col>
            <Col span={11}>
              <Form.Item className="special-form-item">
                {getFieldDecorator('tableAlias', {
                  rules: [
                    { required: true, message: '请输入表名称' },
                    { max: 20, message: '表别名不能超过20个字符' },
                    {
                      pattern: /^[a-zA-Z0-9_]+$/g,
                      message: '仅支持数字、字母、下划线',
                    },
                    {
                      pattern: /[a-zA-Z]+/,
                      message: '表名至少包含1个英文字母',
                    },
                    {
                      validator: repeatValidator,
                    },
                  ],
                })(<Input placeholder="请输入表名" />)}
              </Form.Item>
            </Col>
          </Row>
        </Form.Item>
        {visibleUpdateType ? (
          <Form.Item label="更新方式" required={false}>
            <Row>
              <Col span={11}>
                {getFieldDecorator('updateType', {
                  rules: requiredRule('请选择更新方式'),
                })(
                  <Select placeholder="请选择更新方式">
                    {updateTypeList.map((item) => (
                      <Select.Option key={item.key} value={item.key}>
                        {item.label}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Col>
            </Row>
          </Form.Item>
        ) : null}
        <span className="relation-table-modal-subtitle">设置关联键</span>
        <DynamicSelectList
          leftColumns={leftColLsit}
          rightColumns={rightColList}
          form={props.form}
          data={data && data.joinPairs}
        />
      </Form>
    </div>
  );
};
// TODO: 类型报错问题
export default Form.create()(RelationTableModal) as any;
