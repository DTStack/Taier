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

import React, { Component } from 'react';
import { Form } from 'antd';
import BaseLoginFrom from './login/baseLoginFrom';
import background from './public/background.jpg';
import cover from './public/login-cover.png';

import '@/styles/style.css';

class LoginContainer extends Component<any, any> {
	state = {
		loading: true,
		sysId: '',
		sysType: 0,
		showButton: false,
		loginUrl: '',
		defineIntoUIC: false,
	};

	getRenderLoginForm = () => {
		const { form } = this.props;
		const { sysType, loginUrl, showButton } = this.state;

		return (
			<BaseLoginFrom
				form={form}
				loginUrl={loginUrl}
				sysType={sysType}
				showButton={showButton}
			/>
		);
	};

	render() {
		return (
			<div className="login-container">
				<img className="c-login__bg" alt="" src={background} />
				<div className="c-login__wrap">
					<img alt="" style={{ width: 540, height: 540 }} src={cover} />
					<div className="c-login__container">
						<div className="c-login__container__title" style={{ color: '#000' }}>
							欢迎登录 DAGScheduleX
						</div>
						<Form
							className="c-login__container__form"
							layout="vertical"
							hideRequiredMark={true}
						>
							{this.getRenderLoginForm()}
						</Form>
					</div>
				</div>
			</div>
		);
	}
}

export default Form.create()(LoginContainer);
