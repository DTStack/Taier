import React, { useState } from 'react';
import Container from 'pages/DataModel/components/Container';
import { Breadcrumb, Steps, Button, Form, Modal } from 'antd';
import './style';
import FormRender from './FormRender';
import RelationTableModal from './RelationListModal';
import { formListGenerator } from './constants';

enum EnumModifyStep {
  BASIC_STEP = 0,
  RELATION_TABLE_STEP = 1,
  DIMENSION_STEP = 2,
  METRIC_STRP = 3,
  SETTING_STRP = 4,
}

interface IPropsModify {
  form: any;
}

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 21 },
}
const { Step } = Steps;

const Modify = (props: IPropsModify) => {
  const [current, setCurrent] = useState<EnumModifyStep>(EnumModifyStep.BASIC_STEP);
  const [visibleRelationModal, setVisibleRelationModal] = useState(false);
  const { validateFields } = props.form;
  return (
    <Container>
      <div className="dm-model-modify">
        <div className="breadcrumb-area">
          <Breadcrumb>
            <Breadcrumb.Item>
              <a href="/data-model/list">数据模型</a>
            </Breadcrumb.Item>
            <Breadcrumb.Item>
              <a href="">新建模型</a>
            </Breadcrumb.Item>
          </Breadcrumb>
        </div>
        <div className="content">
          <header className="step-header">
            <Steps current={current}>
              <Step title="基础信息" />
              <Step title="表关联" />
              <Step title="选择维度" />
              <Step title="选择度量" />
              <Step title="设置" />
            </Steps>
          </header>
          <div className="step-content padding-tb-20">
            <div className="inner-container overflow-auto">
              <Form layout="horizontal" className="form-area" {...formItemLayout}>
                <FormRender
                  form={props.form}
                  formList={formListGenerator({ handleClick: () => {setVisibleRelationModal(true)} })}
                />
              </Form>
              <Modal
                title="添加关联表"
                visible={visibleRelationModal}
                onCancel={() => setVisibleRelationModal(false)}
              >
                <RelationTableModal />
              </Modal>
            </div>
          </div>
          <footer className="step-footer">
              <div className="button-area">
                { current === EnumModifyStep.BASIC_STEP ? <Button className="margin-right-8 width-80">取消</Button> : null }
                { current !== EnumModifyStep.BASIC_STEP ? <Button className="margin-right-8 width-80" onClick={() => setCurrent(prev => prev - 1)}>上一步</Button> : null }
                <Button className="margin-right-8 width-80" type="primary" onClick={() => {
                  validateFields((err, data) => {
                    if(err) {
                      return;
                    }
                    setCurrent(prev => prev + 1);
                  })
                }}>下一步</Button>
                <Button type="primary">保存并退出</Button>
              </div>
          </footer>
        </div>
      </div>
    </Container>
  )
}

export default Form.create()(Modify);
