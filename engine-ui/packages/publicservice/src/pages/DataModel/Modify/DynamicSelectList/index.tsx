import React, { useEffect, useState } from 'react';
import { Form, Row, Col, Select, Icon } from 'antd';
import { JoinKey } from '../../types';
const idGenerator = () => {
  let _id = 0;
  return () => {
    return ++_id;
  };
};
const id = idGenerator();

interface IPropsDynamicSelectList {
  form: any;
  data?: JoinKey[];
  leftColumns: any[];
  rightColumns: any[];
}

const DynamicSelectList = (props: IPropsDynamicSelectList) => {
  const { form, data, leftColumns, rightColumns } = props;
  const { getFieldDecorator } = form;
  const [relationKeysList, setRelationKeysList] = useState([
    { id: 0, leftValue: {}, rightValue: {} },
  ]);

  useEffect(() => {
    if (!data) return;
    setRelationKeysList(
      data.map((item) => ({
        id: id(),
        leftValue: {},
        rightValue: {},
      }))
    );
    const joinList = data.reduce((temp, cur, index) => {
      temp[
        `relation-key-left_${index}`
      ] = `${cur.leftValue.schema}-${cur.leftValue.tableName}-${cur.leftValue.columnName}`;
      temp[
        `relation-key-right_${index}`
      ] = `${cur.rightValue.schema}-${cur.rightValue.tableName}-${cur.rightValue.columnName}`;
      return temp;
    }, {});

    setTimeout(() => {
      form.setFieldsValue({
        ...joinList,
      });
    }, 100);
  }, []);

  const deleteRelationList = (id: number) => {
    setRelationKeysList((relationKeysList) =>
      relationKeysList.filter((item) => item.id !== id)
    );
  };

  const addRelationTable = () => {
    setRelationKeysList(
      relationKeysList.concat({
        id: id(),
        leftValue: {},
        rightValue: {},
      })
    );
  };

  return (
    <div className="dynamic-select-list">
      {relationKeysList.map((item, index) => {
        return (
          <Row key={item.id}>
            <Col offset={4} span={20}>
              <Row className="white-space-nowrap">
                <Col span={11}>
                  <Form.Item wrapperCol={{ span: 24 }}>
                    {getFieldDecorator(`relation-key-left_${index}`, {
                      rules: [{ required: true, message: '请选择关联条件' }],
                    })(
                      <Select placeholder="请选择">
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
                    {getFieldDecorator(`relation-key-right_${index}`, {
                      rules: [{ required: true, message: '请选择关联条件' }],
                    })(
                      <Select placeholder="请选择">
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
                    onClick={addRelationTable}
                  />
                  {index === 0 && relationKeysList.length <= 1 ? null : (
                    <Icon
                      className="icon icon-minus-circle"
                      type="minus-circle"
                      onClick={() => deleteRelationList(item.id)}
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

export default DynamicSelectList;
