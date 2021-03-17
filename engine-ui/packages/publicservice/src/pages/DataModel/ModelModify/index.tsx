import React from 'react';
import Container from 'pages/DataModel/components/Container';
import { Breadcrumb, Steps } from 'antd';
import './style';

const { Step } = Steps;

const ModelModify = () => {
  return (
    <Container>
      <div className="dm-model-modify">
        <div className="breadcrumb">
          <Breadcrumb>
            <Breadcrumb.Item>
              <a href="/data-model/list">数据模型</a>
            </Breadcrumb.Item>
            <Breadcrumb.Item>
              <a href="">新建模型</a>
            </Breadcrumb.Item>
          </Breadcrumb>
        </div>
        <div style={{ marginTop: '12px', width: '100%', height: 'calc(100% - 32px)', background: '#fff' }}>
        <div style={{ width: '100%', height: 52, background: 'orange' }}>
          <Steps current={1}>
            <Step title="基础信息" />
            <Step title="表关联" />
            <Step title="选择维度" />
          </Steps>
        </div>
        </div>
      </div>
    </Container>
  )
}

export default ModelModify;
