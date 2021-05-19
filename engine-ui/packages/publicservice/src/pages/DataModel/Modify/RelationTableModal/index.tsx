import React, { useEffect, useImperativeHandle, useRef, useState } from 'react';
import { Form, Row, Col, Select, Input } from 'antd';
import { IModelDetail } from '@/pages/DataModel/types';
import { JoinType } from '@/pages/DataModel/types';
import Message from 'pages/DataModel/components/Message';
import { API } from '@/services';
import DynamicSelect from '../DynamicSelect';
import _ from 'lodash';
import './style';
interface TableItem {
  dsId: number;
  schema: string;
  tableName: string;
  tableAlias?: string;
}

export const joinTypeList = [
  { key: JoinType.LEFT_JOIN, label: 'left join' },
  { key: JoinType.RIGHT_JOIN, label: 'right join' },
  { key: JoinType.INNER_JOIN, label: 'inner join' },
];

const tableParser = {
  parser: (table: string) => {
    if (!table) return {};
    const [dsId, schema, tableName, tableAlias] = table.split('-');
    return {
      dsId,
      schema,
      tableName,
      tableAlias,
    };
  },
  encode: (table: TableItem) =>
    `${table.dsId}-${table.schema}-${table.tableName}-${table.tableAlias}`,
};

enum Mode {
  ADD = 'ADD',
  EDIT = 'EDIT',
}

interface IPropsRelationTableModal {
  form?: any;
  updateTypeList: any[];
  tableList: TableItem[];
  mode: Mode;
  value: any;
  modelDetail: IModelDetail;
  cref: any;
}

const formItemLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 19 },
};

const RelationTableModal = (props: IPropsRelationTableModal) => {
  const {
    form,
    updateTypeList,
    tableList,
    value,
    modelDetail,
    mode,
    cref,
  } = props;
  const {
    getFieldsValue,
    getFieldDecorator,
    setFieldsValue,
    validateFields,
  } = form;
  const [schemaList, setSchemaList] = useState<string[]>([]);
  const [relationTableList, setRelationTableList] = useState<
    { tableName: string }[]
  >([]);
  const [visibleUpdateType, setVisibleUpdateType] = useState<boolean>(false);
  const isDisabled = mode === Mode.EDIT;
  const currentFormValue = getFieldsValue();
  const refDynamicSelect = useRef(null);

  useImperativeHandle(cref, () => {
    return {
      validate: () =>
        new Promise((resolve, reject) => {
          validateFields((error, data) => {
            if (error) return reject(error.message);
            const table = tableParser.parser(data.leftTable);
            const _data = {
              ...data,
              leftTable: table.tableName,
              leftSchema: table.schema,
              leftTableAlias: table.tableAlias,
              partition: visibleUpdateType,
            };
            refDynamicSelect.current.getJoinPairs().then((joinPairsObj) => {
              const group = _.groupBy(
                Object.keys(joinPairsObj),
                (key) => key.split('_')[1]
              );
              const joinPairs = Object.keys(group).map((index) => {
                return group[index].reduce((temp, key) => {
                  const [schema, tableName, columnName] = joinPairsObj[
                    key
                  ].split('-');
                  let _key = 'rightValue';
                  if (/left/.test(key)) {
                    _key = 'leftValue';
                  }
                  temp[_key] = {
                    schema,
                    tableName,
                    columnName,
                  };
                  return temp;
                }, {});
              });
              _data.joinPairs = joinPairs;
              return resolve(_data);
            });
          });
        }),
    };
  });

  useEffect(() => {
    const mainTable: TableItem = {
      dsId: modelDetail.dsId,
      schema: value.leftSchema,
      tableName: value.leftTable,
      tableAlias: value.leftTableAlias,
    };
    const leftTable =
      value.leftSchema && value.leftTable
        ? tableParser.encode(mainTable)
        : undefined;
    setFieldsValue({
      leftTable,
      joinType: value.joinType,
      schema: value.schema,
      table: value.table,
      tableAlias: value.tableAlias,
    });
  }, [value]);

  useEffect(() => {
    setFieldsValue({
      ...currentFormValue,
      updateType: visibleUpdateType ? value.updateType : undefined,
    });
  }, [visibleUpdateType]);

  const getSchemaList = async (dsId: number) => {
    if (!dsId) return;
    try {
      const { success, data, message } = await API.getDataModelSchemaList({
        datasourceId: dsId,
      });
      if (success) {
        setSchemaList(data);
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  const getRelationTableList = async (dsId: number, schema: string) => {
    if (!dsId || !schema) return;
    try {
      const { success, data, message } = await API.getDataModelTableList({
        datasourceId: dsId,
        schema,
      });
      if (success) {
        setRelationTableList(data);
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  // 请求接口判断表是否为分区表
  const isPartition = async (
    dsId: number,
    schema: string,
    tableName: string
  ) => {
    if (!dsId || !schema || !tableName) return;
    try {
      const { success, data, message } = await API.isPartition({
        datasourceId: dsId,
        schema,
        tableName,
      });
      if (success) {
        setVisibleUpdateType(data);
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  useEffect(() => {
    getSchemaList(modelDetail.dsId);
  }, [modelDetail.dsId]);

  useEffect(() => {
    setRelationTableList([]);
    getRelationTableList(modelDetail.dsId, currentFormValue.schema);
  }, [currentFormValue.schema]);

  useEffect(() => {
    isPartition(
      modelDetail.dsId,
      currentFormValue.schema,
      currentFormValue.table
    );
  }, [currentFormValue.table]);

  const requiredRule = (msg: string) => [{ required: true, message: msg }];

  // 表别名重复性校验
  const repeatValidator = (rule, tableAlias, callback) => {
    let filter = (v) => true;
    if (mode === Mode.EDIT) {
      // 编辑状态下需要过滤当前别名的表
      filter = (item) => item.tableAlias !== value.tableAlias;
    }
    const isRepeat =
      tableList
        .filter((item) => item.tableAlias)
        .filter(filter)
        .findIndex((item) => item.tableAlias === tableAlias) === -1;
    if (isRepeat) {
      callback();
    } else {
      callback('表别名不能重复');
    }
  };

  const securityValidator = (rule, value = '', callback) => {
    const securityList = ['delete', 'truncate'];

    if (
      securityList.findIndex((word) =>
        value.toLowerCase().includes(word.toLocaleLowerCase())
      ) > -1
    ) {
      callback('表名不能包含类似delete、truncate等敏感词汇');
    }
    callback();
  };

  const leftTable = tableParser.parser(currentFormValue.leftTable);

  return (
    <div ref={cref} className="relation-table-modal">
      <Form className="dm-form" layout="horizontal" {...formItemLayout}>
        <Form.Item label="选择表" required={false}>
          {getFieldDecorator('leftTable', {
            rules: requiredRule('请选择表'),
          })(
            <Select
              dropdownClassName="dm-form-select-drop"
              placeholder="请选择表"
              disabled={isDisabled}
              onChange={() => {
                refDynamicSelect.current.resetColumns('left');
              }}>
              {tableList.map((item, index) => (
                <Select.Option key={index} value={tableParser.encode(item)}>
                  {item.tableName}({item.tableAlias})
                </Select.Option>
              ))}
            </Select>
          )}
        </Form.Item>
        <Form.Item label="关联关系" required={false}>
          {getFieldDecorator('joinType', {
            rules: requiredRule('请选择关联关系'),
          })(
            <Select
              dropdownClassName="dm-form-select-drop"
              placeholder="请选择关联关系">
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
            <Select
              dropdownClassName="dm-form-select-drop"
              disabled={isDisabled}
              placeholder="请选择schema"
              onChange={() => {
                refDynamicSelect.current.resetColumns('right');
                setFieldsValue({
                  ...currentFormValue,
                  table: undefined,
                });
              }}>
              {schemaList.map((schema) => (
                <Select.Option key={schema} value={schema}>
                  {schema}
                </Select.Option>
              ))}
            </Select>
          )}
        </Form.Item>
        <Form.Item className="relation-row" label="关联表" required={false}>
          <Row>
            <Col span={11}>
              {getFieldDecorator('table', {
                rules: requiredRule('请选择关联表'),
              })(
                <Select
                  disabled={isDisabled}
                  placeholder="请选择关联表"
                  dropdownClassName="dm-form-select-drop"
                  onChange={() => {
                    refDynamicSelect.current.resetColumns('right');
                  }}>
                  {relationTableList.map((item) => (
                    <Select.Option key={item.tableName} value={item.tableName}>
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
                      validator: securityValidator,
                    },
                    {
                      validator: repeatValidator,
                    },
                  ],
                })(<Input placeholder="请输入表名" autoComplete="off" />)}
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
                  <Select
                    dropdownClassName="dm-form-select-drop"
                    placeholder="请选择更新方式">
                    {updateTypeList.map((item) => (
                      <Select.Option
                        key={item.leftValue}
                        value={item.leftValue}>
                        {item.rightValue}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Col>
            </Row>
          </Form.Item>
        ) : null}
        <span className="relation-table-modal-subtitle">设置关联键</span>
        <DynamicSelect
          cref={refDynamicSelect}
          leftTable={leftTable}
          rightTable={{
            dsId: modelDetail.dsId,
            schema: currentFormValue.schema,
            tableName: currentFormValue.table,
          }}
          joinPairs={value.joinPairs}
        />
      </Form>
    </div>
  );
};

export default Form.create()(RelationTableModal) as any;
