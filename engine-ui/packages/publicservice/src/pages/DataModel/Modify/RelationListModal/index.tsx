import React, { useEffect, useImperativeHandle } from 'react';
import { Form, Select, Input, Row, Col } from 'antd';
import DynamicSelectList from '../DynamicSelectList';
import './style';
import { joinTypeList, updateTypeList } from './constants';
import { TableJoinInfo } from 'pages/DataModel/types';

const formItemLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 20 },
};

interface IPropsRelationTableModal {
  form?: any;
  cref: any;
  data?: TableJoinInfo;
  tables: string[];
}

const RelationTableModal = (props: IPropsRelationTableModal) => {
  const { getFieldDecorator, validateFields, setFieldsValue } = props.form;
  const { cref, data, tables } = props;

  useEffect(() => {
    if (data) {
      setFieldsValue({
        ...data,
        joinPairs: undefined,
      });
    }
  }, []);

  const validate = (callback) => {
    validateFields((err, data) => {
      callback(err, data);
    });
  };

  useImperativeHandle(cref, () => ({
    // validate 就是暴露给父组件的方法
    validate,
  }));

  const requiredRule = (message: string) => {
    return [{ required: true, message }];
  };

  return (
    <div ref={cref} className="relation-table-modal">
      <Form layout="horizontal" {...formItemLayout}>
        <Form.Item label="选择表" required={false}>
          {getFieldDecorator('leftTable', {
            rules: requiredRule('请选择表'),
          })(
            <Select placeholder="请选择表">
              {tables.map((item) => (
                <Select.Option key={item} value={item}>
                  {item}
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
              <Select.Option key="aaa" value="aaa">
                aaaa
              </Select.Option>
              <Select.Option key="bbb" value="bbb">
                bbb
              </Select.Option>
              <Select.Option key="ccc" value="ccc">
                ccc
              </Select.Option>
              <Select.Option key="ddd" value="ddd">
                ddd
              </Select.Option>
            </Select>
          )}
        </Form.Item>
        <Form.Item label="关联表" required={false}>
          <Row>
            <Col span={11}>
              {getFieldDecorator('table', {
                rules: requiredRule('请选择关联表'),
              })(
                <Select placeholder="请选择关联表">
                  <Select.Option key="111" value="e">
                    ww
                  </Select.Option>
                  <Select.Option key="qqq" value="rr">
                    ww
                  </Select.Option>
                  <Select.Option key="qwww" value="rrq">
                    ddaaa
                  </Select.Option>
                </Select>
              )}
            </Col>
            <Col span={2} className="text-align-center">
              AS
            </Col>
            <Col span={11}>
              <Form.Item className="special-form-item">
                {getFieldDecorator('tableAlias', {
                  rules: requiredRule('请输入表名'),
                })(<Input placeholder="请输入表名" />)}
              </Form.Item>
            </Col>
          </Row>
        </Form.Item>
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
        <span className="relation-table-modal-subtitle">设置关联键</span>
        <DynamicSelectList form={props.form} data={data && data.joinPairs} />
      </Form>
    </div>
  );
};
// TODO: 类型报错问题
export default Form.create()(RelationTableModal) as any;
