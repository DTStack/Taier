import React from 'react';
import { Form, Select, Input, Row, Col, Icon } from 'antd';
import './style';

const DynamicSelectList = () => {
  return (
    <div className="dynamic-select-list">
      <Row>
        <Col offset={4} span={20}>
          <Row className="white-space-nowrap">
            <Col span={11}>
              <Select>
                <Select.Option key="aaa" value="aaa">
                  aaa
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
            </Col>
            <Col span={2} className="text-equal">
              =
            </Col>
            <Col span={11}>
              <Select>
                <Select.Option key="aaa" value="aaa">
                  aaa
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
            </Col>
            <div className="operation">
              <Icon className="icon icon-plus-circle" type="plus-circle" />
              <Icon className="icon icon-minus-circle" type="minus-circle" />
            </div>
          </Row>
        </Col>
      </Row>
    </div>
  );
};

// const formList: IFormItem[] = [
//   {
//     key: 'table',
//     label: '选择表',
//     type: EnumFormItemType.SELECT,
//     placeholder: '请选择表',
//     rules: [
//       { required: true, message: '请选择表' }
//     ]
//   },
//   {
//     key: 'joinType',
//     label: '关联关系',
//     type: EnumFormItemType.SELECT,
//     placeholder: '请选择关联关系',
//     rules: [
//       { required: true, message: '请选择关联关系' }
//     ]
//   },
//   {
//     key: 'schema',
//     label: 'schema',
//     type: EnumFormItemType.SELECT,
//     placeholder: '请选择schema',
//     rules: [
//       { required: true, message: '请选择schema' }
//     ]
//   },
// ]

const formItemLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 20 },
};

const RelationTableModal = (props: { form: any }) => {
  // const { form } = props;
  return (
    <div style={{ width: '100%', padding: '0 68px 0 44px' }}>
      <Form layout="horizontal" {...formItemLayout}>
        {/* <FormRender form={form} formList={formList} /> */}
        <Form.Item label="选择表">
          <Select>
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
        </Form.Item>
        <Form.Item label="关联关系">
          <Select>
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
        </Form.Item>
        <Form.Item label="schema">
          <Select>
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
        </Form.Item>
        <Form.Item label="关联表">
          <Row>
            <Col span={11}>
              <Select>
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
            </Col>
            <Col span={2} style={{ textAlign: 'center' }}>
              AS
            </Col>
            <Col span={11}>
              <Input />
            </Col>
          </Row>
        </Form.Item>
        <Form.Item label="更新方式">
          <Row>
            <Col span={11}>
              <Select>
                <Select.Option key={1} value="1">
                  全量更新
                </Select.Option>
                <Select.Option key={2} value="2">
                  增量更新
                </Select.Option>
              </Select>
            </Col>
          </Row>
        </Form.Item>
        <span className="relation-table-modal-subtitle">设置关联键</span>
        <DynamicSelectList />
      </Form>
    </div>
  );
};

export default Form.create()(RelationTableModal);
