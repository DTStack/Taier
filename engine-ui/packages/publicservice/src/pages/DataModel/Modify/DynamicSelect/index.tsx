import React, { useEffect, useImperativeHandle, useState } from 'react';
import { Row, Col, Form, Select, Icon } from 'antd';
import { JoinKey } from '@/pages/DataModel/types';
import Message from 'pages/DataModel/components/Message';
import { API } from '@/services';
import _ from 'lodash';
import './style';

interface ITableItem {
  dsId: number;
  schema: string;
  tableName: string;
  tableAlias?: string;
}

interface IPropsDynamicSelect {
  form?: any;
  joinPairs: JoinKey[];
  leftTable: ITableItem;
  rightTable: ITableItem;
  cref: any;
}

const idGenerator = () => {
  let _id = 0;
  return () => ++_id;
};

const identifyJoinPairs = idGenerator();
const setId = (item) => {
  item.id = identifyJoinPairs();
};

const DynamicSelect = (props: IPropsDynamicSelect) => {
  const { form, joinPairs = [], leftTable, rightTable, cref } = props;
  const {
    getFieldDecorator,
    validateFields,
    setFieldsValue,
    getFieldsValue,
  } = form;
  const [joinPairsList, setJoinPairsList] = useState(() => {
    joinPairs.forEach(setId);
    return joinPairs;
  });
  const [leftColumns, setLeftColumns] = useState([]);
  const [rightColumns, setRightColumns] = useState([]);

  useImperativeHandle(cref, () => {
    return {
      getJoinPairs: () =>
        new Promise((resolve, reject) => {
          validateFields((error, data) => {
            if (error) return reject(error);
            // resetFields();
            return resolve(data);
          });
        }),
      resetColumns,
    };
  });

  useEffect(() => {
    const value = joinPairs.reduce((temp, item) => {
      const lSchema = item.leftValue.schema;
      const lTable = item.leftValue.tableName;
      const lCol = item.leftValue.columnName;
      const rSchema = item.rightValue.schema;
      const rTable = item.rightValue.tableName;
      const rCol = item.rightValue.columnName;
      if (!lCol || !rCol) return temp;
      temp[`left_${item.id}`] = `${lSchema}-${lTable}-${lCol}`;
      temp[`right_${item.id}`] = `${rSchema}-${rTable}-${rCol}`;
      return temp;
    }, {});
    setFieldsValue(value);
  }, [joinPairsList]);

  const getColumnList = async (options: any[], setCol: Function) => {
    if (!options || options.length === 0) return;
    if (!options[0].datasourceId || !options[0].schema || !options[0].tableName)
      return;
    try {
      const { success, data, message } = await API.getDataModelColumns(options);
      if (success) {
        setCol(_.uniqBy(data, (item) => item.columnName));
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  const resetColumns = (flag: 'left' | 'right') => {
    const currentForm = getFieldsValue();
    const setCol = flag === 'left' ? setLeftColumns : setRightColumns;
    setCol([]);
    for (let key in currentForm) {
      if (new RegExp(flag).test(key)) {
        currentForm[key] = undefined;
      }
    }
    setFieldsValue(currentForm);
  };

  useEffect(() => {
    getColumnList(
      [
        {
          datasourceId: leftTable.dsId,
          schema: leftTable.schema,
          tableName: leftTable.tableName,
        },
      ],
      setLeftColumns
    );
  }, [
    leftTable.dsId,
    leftTable.schema,
    leftTable.tableName,
    leftTable.tableAlias,
  ]);

  useEffect(() => {
    getColumnList(
      [
        {
          datasourceId: rightTable.dsId,
          schema: rightTable.schema,
          tableName: rightTable.tableName,
        },
      ],
      setRightColumns
    );
  }, [rightTable.dsId, rightTable.schema, rightTable.tableName]);

  const onAdd = (id) => {
    setJoinPairsList((joinPairsList) => {
      const next = [];
      const fresh = {
        id: identifyJoinPairs(),
        leftValue: {
          tableName: '',
          schema: '',
          columnType: '',
          columnName: undefined,
          columnComment: '',
        },
        rightValue: {
          tableName: '',
          schema: '',
          columnType: '',
          columnName: undefined,
          columnComment: '',
        },
      };
      joinPairsList.forEach((item) => {
        next.push(item);
        if (item.id === id) {
          next.push(fresh);
        }
      });
      return next;
    });
  };

  const onRemove = (id) => {
    setJoinPairsList((joinPairsList) =>
      joinPairsList.filter((item) => item.id !== id)
    );
  };

  return (
    <div className="dynamic-select-list">
      {joinPairsList.map((item, index) => {
        return (
          <Row className="form-item-row" key={item.id}>
            <Col offset={5} span={19}>
              <Row className="white-space-nowrap margin-bottom-0">
                <Col span={11}>
                  <Form.Item wrapperCol={{ span: 24 }}>
                    {getFieldDecorator(`left_${item.id}`, {
                      rules: [{ required: true, message: '请选择关联条件' }],
                    })(
                      <Select
                        showSearch={true}
                        optionFilterProp="children"
                        dropdownClassName="dm-form-select-drop"
                        placeholder="请选择">
                        {leftColumns.map((item) => {
                          const _id = `${item.schema}-${item.tableName}-${item.columnName}`;
                          return (
                            <Select.Option key={_id} value={_id}>
                              {item.columnName}
                            </Select.Option>
                          );
                        })}
                      </Select>
                    )}
                  </Form.Item>
                </Col>
                <Col span={2} className="text-equal">
                  =
                </Col>
                <Col span={11}>
                  <Form.Item wrapperCol={{ span: 24 }}>
                    {getFieldDecorator(`right_${item.id}`, {
                      rules: [{ required: true, message: '请选择关联条件' }],
                    })(
                      <Select
                        showSearch={true}
                        optionFilterProp="children"
                        dropdownClassName="dm-form-select-drop"
                        placeholder="请选择">
                        {rightColumns.map((item) => {
                          const _id = `${item.schema}-${item.tableName}-${item.columnName}`;
                          return (
                            <Select.Option key={_id} value={_id}>
                              {item.columnName}
                            </Select.Option>
                          );
                        })}
                      </Select>
                    )}
                  </Form.Item>
                </Col>
                <div className="operation">
                  <Icon
                    className="icon icon-plus-circle"
                    type="plus-circle"
                    onClick={() => onAdd(item.id)}
                  />
                  {index === 0 && joinPairsList.length <= 1 ? null : (
                    <Icon
                      className="icon icon-minus-circle"
                      type="minus-circle"
                      onClick={() => onRemove(item.id)}
                    />
                  )}
                </div>
              </Row>
            </Col>
          </Row>
        );
      })}
    </div>
  );
};

export default Form.create()(DynamicSelect) as any;
