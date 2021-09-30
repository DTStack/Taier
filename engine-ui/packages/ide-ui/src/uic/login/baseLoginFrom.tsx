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

import React, { Component, Fragment } from 'react';
import { setItem } from '../../utils/local';
import { Form, Button, message as Message } from 'antd';
import { USER_NAME } from '../../consts';
import '@/styles/base.css';

class BaseLoginFrom extends Component<any, any> {
	state = {
		submitLoading: false,
	};

	handleLoginSubmit = async () => {
		const { form } = this.props;
		form.validateFields(async (err: any, value: any) => {
			if (!err) {
				this.setState({
					submitLoading: true,
				});
				let urlCoding = '';
				for (const item in value) {
					urlCoding += `${encodeURIComponent(item)}=${encodeURIComponent(value[item])}&`;
				}
				urlCoding = urlCoding.slice(0, urlCoding.length - 1);
				const response = await fetch('/node/login/submit', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
					},
					body: urlCoding,
				});
				const { ok } = response;
				this.setState({
					submitLoading: false,
				});
				if (!ok) {
					return Message.error('登录失败');
				}
				const { code, data, message } = await response.json();
				if (message || code !== 1) {
					return Message.error(message || '登录异常');
				}

				setItem(USER_NAME, data);
				window.location.href = '#/';
			}
		});
	};

	render() {
		const { form } = this.props;
		const { submitLoading } = this.state;
		const { getFieldDecorator } = form;
		return (
			<Fragment>
				<Form.Item>
					{getFieldDecorator(USER_NAME, {
						initialValue: '',
						rules: [
							{
								type: 'email',
								message: '请输入正确格式的邮箱账号',
							},
							{
								required: true,
								message: '账号不能为空',
							},
						],
					})(
						<input
							className="c-login__container__form__input"
							placeholder="请输入注册账号"
						/>,
					)}
				</Form.Item>
				<Form.Item>
					{getFieldDecorator('password', {
						initialValue: '',
						rules: [
							{
								required: true,
								message: '密码不能为空',
							},
						],
					})(
						<input
							className="c-login__container__form__input"
							type="password"
							placeholder="请输入密码"
						/>,
					)}
				</Form.Item>
				<Button
					htmlType="submit"
					type="primary"
					loading={submitLoading}
					className="c-login__container__form__btn"
					onClick={this.handleLoginSubmit}
				>
					登录
				</Button>
			</Fragment>
		);
	}
}
export default (Form.create() as any)(BaseLoginFrom);
