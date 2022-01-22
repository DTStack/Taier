/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    setSubmitBtnStatus(true);
    childRef.current.submitForm();
  };

  const record = props.location.state.record;

  //子组件调用父组件方法
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
          <Button type="primary" onClick={testConnect} style={{ width: 108 }}>
            测试连通性
          </Button>
          <Button
            style={{ marginLeft: 60, marginRight: 8, width: 80 }}
            disabled>
            上一步
          </Button>
          <Button
            type="primary"
            onClick={submitConfig}
            disabled={submitBtnStatus}
            style={{ width: 80 }}>
            确定
          </Button>
        </div>
      </div>
    </div>
  );
}
