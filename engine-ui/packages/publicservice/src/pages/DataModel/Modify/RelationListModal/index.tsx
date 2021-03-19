import React from 'react';
import FormRender from '../FormRender';
import { Form, Select, Input, Row, Col } from 'antd';
import { EnumFormItemType, IFormItem } from '../FormRender/types';
import "./style";


const DanamicSelectList = () => {
  return (
    <div>
      hello, this is dynamic select list component...
    </div>
  )
}

const formList: IFormItem[] = [
  {
    key: 'table',
    label: '选择表',
    type: EnumFormItemType.SELECT,
    placeholder: '请选择表',
    rules: [
      { required: true, message: '请选择表' }
    ]
  },
  {
    key: 'joinType',
    label: '关联关系',
    type: EnumFormItemType.SELECT,
    placeholder: '请选择关联关系',
    rules: [
      { required: true, message: '请选择关联关系' }
    ]
  },
  {
    key: 'schema',
    label: 'schema',
    type: EnumFormItemType.SELECT,
    placeholder: '请选择schema',
    rules: [
      { required: true, message: '请选择schema' }
    ]
  },
]

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 21 },
}

const RelationTableModal = (props: { form: any }) => {
  const { form } = props;
  return (
    <Form layout="horizontal" {...formItemLayout}>
      <FormRender form={form} formList={formList} />
      <Form.Item label="关联表" >
        <Row>
          <Col span={11}>
            <Select>
              <Select.Option key="111" value="e">ww</Select.Option>
              <Select.Option key="qqq" value="rr">ww</Select.Option>
              <Select.Option key="qwww" value="rrq">ddaaa</Select.Option>
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
      <Form.Item label="更新方式" >
        <Row>
          <Col span={11}>
            <Select>
              <Select.Option key={1} value="1">全量更新</Select.Option>
              <Select.Option key={2} value="2">增量更新</Select.Option>
            </Select>
          </Col>
        </Row>
      </Form.Item>
      <span className="relation-table-modal-subtitle">
        设置关联键
      </span>
      <Form.Item>
        <DanamicSelectList></DanamicSelectList>
      </Form.Item>
    </Form>
  )
}

export default Form.create()(RelationTableModal);
