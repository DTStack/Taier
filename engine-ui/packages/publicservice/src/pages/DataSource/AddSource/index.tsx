import React, { useState, useRef } from 'react';
import { withRouter } from 'react-router';
import { Steps, Button } from 'antd';
import BreadComponent from '../components/BreadComponent';
import SelectSource from '../components/SelectSource';
import ProduceAuth from '../components/ProduceAuth';
import InfoConfig from '../components/InfoConfig';
import './style.scss';

const { Step } = Steps;

function index(props) {
  const childRef = useRef(null);

  const [current, setCurrent] = useState<number>(0);

  const [showFirstNext, setShowFirstNext] = useState<boolean>(false); //选择数据源-是否显示下一步

  //1.选择数据源
  const nextType = (value) => {
    setShowFirstNext(value);
  };

  //3.信息配置
  //测试连通性
  const testConnect = () => {
    childRef.current.testForm();
  };
  //确定按钮
  const submitConfig = () => {
    childRef.current.submitForm();
  };

  const switchContent = (step) => {
    switch (step) {
      case 0:
        let content0 = (
          <>
            <div className="step-info">
              <SelectSource nextType={nextType}></SelectSource>
            </div>
            <div className="footer-select">
              <Button
                style={{ marginRight: 8 }}
                onClick={() => {
                  props.router.push('/data-source/list');
                }}>
                取消
              </Button>

              {(showFirstNext || sessionStorage.getItem('sqlType')) && (
                <Button
                  type="primary"
                  onClick={() => {
                    setCurrent(1);
                  }}>
                  下一步
                </Button>
              )}
            </div>
          </>
        );
        return content0;
        break;
      case 1:
        let content1 = (
          <>
            <div className="step-info">
              <ProduceAuth></ProduceAuth>
            </div>
            <div className="footer-select">
              <Button
                style={{ marginRight: 8 }}
                onClick={() => {
                  setCurrent(0);
                }}>
                上一步
              </Button>
              <Button
                type="primary"
                onClick={() => {
                  setCurrent(2);
                }}>
                下一步
              </Button>
            </div>
          </>
        );
        return content1;
        break;
      case 2:
        let content2 = (
          <>
            <div className="step-info">
              <InfoConfig cRef={childRef} record={''}></InfoConfig>
            </div>
            <div className="footer-select">
              <Button type="primary" icon="sync" onClick={testConnect}>
                <span>测试连通性</span>
              </Button>

              <Button
                style={{ marginLeft: 60, marginRight: 8 }}
                onClick={() => {
                  setCurrent(1);
                }}>
                上一步
              </Button>
              <Button type="primary" onClick={submitConfig}>
                确定
              </Button>
            </div>
          </>
        );
        return content2;
        break;
      default:
        break;
    }
  };
  return (
    <div className="source">
      <BreadComponent name="新增"></BreadComponent>

      <div className="content">
        <div className="top-steps">
          <Steps current={current}>
            <Step title="选择数据源" />
            <Step title="产品授权" />
            <Step title="信息配置" />
          </Steps>
        </div>
        {switchContent(current)}
      </div>
    </div>
  );
}

export default withRouter(index);
