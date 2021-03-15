/*
 * @Author: 云乐
 * @Date: 2021-03-10 16:19:35
 * @LastEditTime: 2021-03-15 17:36:04
 * @LastEditors: 云乐
 * @Description: 新增数据源
 */
import React, { useState, useRef } from "react";
import { Steps, Button } from "antd";
import BreadCom from "./BreadCom";
import SelectSource from "../components/SelectSource";
import ProduceAuth from "../components/ProduceAuth";
import InfoConfig from "../components/InfoConfig";
import { useHistory } from "react-router";
import "./style.scss";

const { Step } = Steps;

export default function index() {
  const childRef = useRef();
  const history = new useHistory();

  const [current, setCurrent] = useState(0);

  const onChange = (current) => {
    setCurrent(current);
  };

  //信息配置
  //测试连通性
  const testConnect = () => {
    childRef.current.testForm() //父组件调用子组件的方法
  };
  // 
  const submitConfig=()=>{
    childRef.current.submitForm() //父组件调用子组件的方法
  }
  return (
    <div className="source">
      <BreadCom></BreadCom>

      <div className="content">
        <div className="top-steps">
          <Steps current={current} onChange={onChange}>
            <Step title="选择数据源" />
            <Step title="产品授权" />
            <Step title="信息配置" />
          </Steps>
        </div>

        <div className="step-info">
          {current === 0 && <SelectSource></SelectSource>}
          {current === 1 && <ProduceAuth></ProduceAuth>}
          {current === 2 && (
            <InfoConfig cRef={childRef}></InfoConfig>
          )}
        </div>

        <div className="footer-select">
          {current === 0 && (
            <div>
              <Button
                style={{ marginRight: 8 }}
                onClick={() => {
                  history.push("data-source");
                }}
              >
                取消
              </Button>
              <Button
                type="primary"
                onClick={() => {
                  setCurrent(1);
                }}
              >
                下一步
              </Button>
            </div>
          )}
          {current === 1 && (
            <div>
              <Button
                style={{ marginRight: 8 }}
                type="primary"
                onClick={() => {
                  setCurrent(0);
                }}
              >
                上一步
              </Button>
              <Button
                type="primary"
                onClick={() => {
                  setCurrent(2);
                }}
              >
                下一步
              </Button>
            </div>
          )}
          {current === 2 && (
            <div>
              <Button type="primary" onClick={testConnect}>
                测试连通性
              </Button>
              <Button
                style={{ marginLeft: 60, marginRight: 8 }}
                type="primary"
                onClick={() => {
                  setCurrent(1);
                }}
              >
                上一步
              </Button>
              <Button type="primary" onClick={submitConfig}>确定</Button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
