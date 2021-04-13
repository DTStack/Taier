import React, { useRef, useState } from 'react';
import BreadComponent from '../components/BreadComponent';
import { Steps, Button } from 'antd';
import InfoConfig from '../components/InfoConfig';

import '../AddSource/style.scss';

const { Step } = Steps;

export default function index(props) {
  const childRef = useRef(null);
  const [submitBtnStatus, setSubmitBtnStatus] = useState(false);

  //测试连通性
  const testConnect = () => {
    childRef.current.testForm();
  };
  //确定按钮
  const submitConfig = () => {
    childRef.current.submitForm();
    setSubmitBtnStatus(true);
  };

  const record = props.location.state.record;

  const changeBtnStatus = () => {
    setSubmitBtnStatus(false);
  };
  return (
    <div className="source">
      <BreadComponent name="编辑"></BreadComponent>

      <div className="content">
        <div className="top-steps edit-steps">
          <Steps current={2}>
            <Step title="选择数据源" disabled />
            <Step title="产品授权" disabled />
            <Step title="信息配置" />
          </Steps>
        </div>
        <div className="step-info">
          <InfoConfig
            cRef={childRef}
            record={record}
            changeBtnStatus={changeBtnStatus}></InfoConfig>
        </div>
        <div className="footer-select">
          <Button type="primary" onClick={testConnect}>
            测试连通性
          </Button>
          <Button style={{ marginLeft: 60, marginRight: 8 }} disabled>
            上一步
          </Button>
          <Button
            type="primary"
            onClick={submitConfig}
            disabled={submitBtnStatus}>
            确定
          </Button>
        </div>
      </div>
    </div>
  );
}
