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

import React, { useState, useRef } from 'react';
import { withRouter } from 'react-router';
import { Steps, Button } from 'antd';
import BreadComponent from '../components/BreadComponent';
import SelectSource from '../components/SelectSource';
import ProduceAuth from '../components/ProduceAuth';
import InfoConfig from '../components/InfoConfig';
import './style.scss';
import stepIconRender from '@/utils/stepIconRender';

const { Step } = Steps;

function index(props) {
	const childRef = useRef(null);

	const [current, setCurrent] = useState<number>(0);

	const [submitBtnStatus, setSubmitBtnStatus] = useState(false);

	//1.选择数据源
	const nextType = (value) => {
		setCurrent(1);
	};

	//3.信息配置
	//测试连通性
	const testConnect = () => {
		childRef.current.testForm();
	};
	//确定按钮
	const submitConfig = () => {
		setSubmitBtnStatus(true);
		childRef.current.submitForm();
	};

	//子组件调用父组件方法
	const changeBtnStatus = () => {
		setSubmitBtnStatus(false);
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
								key="1"
								style={{ marginRight: 8, width: 80 }}
								onClick={() => {
									props.router.push('/data-source/list');
								}}
							>
								取消
							</Button>
						</div>
					</>
				);
				return content0;
			case 1:
				let content1 = (
					<>
						<div className="step-info">
							<ProduceAuth></ProduceAuth>
						</div>
						<div className="footer-select">
							<Button
								key="3"
								style={{ marginRight: 8, width: 80 }}
								onClick={() => {
									setCurrent(0);
								}}
							>
								上一步
							</Button>
							<Button
								style={{ width: 80 }}
								type="primary"
								onClick={() => {
									setCurrent(2);
								}}
							>
								下一步
							</Button>
						</div>
					</>
				);
				return content1;
			case 2:
				let content2 = (
					<>
						<div className="step-info">
							<InfoConfig
								cRef={childRef}
								record={''}
								changeBtnStatus={changeBtnStatus}
							></InfoConfig>
						</div>
						<div className="footer-select">
							<Button
								key="4"
								type="primary"
								icon="sync"
								onClick={testConnect}
								style={{ width: 108 }}
							>
								<span>测试连通性</span>
							</Button>

							<Button
								key="5"
								style={{ marginLeft: 60, marginRight: 8, width: 80 }}
								onClick={() => {
									setCurrent(1);
								}}
							>
								上一步
							</Button>
							<Button
								key="6"
								type="primary"
								onClick={submitConfig}
								disabled={submitBtnStatus}
								style={{ width: 80 }}
							>
								确定
							</Button>
						</div>
					</>
				);
				return content2;
			default:
				break;
		}
	};
	return (
		<div className="source">
			<BreadComponent name="新增"></BreadComponent>

			<div className="content">
				<div className="top-steps">
					<Steps className="dm-steps" current={current}>
						{['选择数据源', '产品授权', '信息配置'].map((title, index) => (
							<Step title={title} key={index} icon={stepIconRender(index, current)} />
						))}
					</Steps>
				</div>
				{switchContent(current)}
			</div>
		</div>
	);
}

export default withRouter(index);
