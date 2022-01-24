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

import React from 'react';
import { Icon } from '@ant-design/compatible';
import { Tooltip, Modal } from 'antd';
import { isArray } from 'lodash';
import './index.scss';

const TEST_STATUS: any = {
	SUCCESS: true,
	FAIL: false,
};
export default class TestRestIcon extends React.Component<any, any> {
	constructor(props: any) {
		super(props);
		this.state = {};
	}
	// show err message
	showDetailErrMessage(engine: any) {
		Modal.error({
			title: `错误信息`,
			content: `${engine.errorMsg}`,
			zIndex: 1061,
		});
	}
	matchCompTest(testResult: any) {
		switch (testResult?.result) {
			case TEST_STATUS.SUCCESS: {
				return (
					<Icon
						className="success-icon"
						type="check-circle"
						theme="filled"
					/>
				);
			}
			case TEST_STATUS.FAIL: {
				return (
					<Tooltip
						title={
							<a
								style={{ color: '#fff', overflow: 'scroll' }}
								onClick={this.showDetailErrMessage.bind(
									this,
									testResult,
								)}
							>
								{!isArray(testResult?.errorMsg) ? (
									<span>{testResult?.errorMsg}</span>
								) : (
									testResult?.errorMsg?.map((msg: any) => {
										return (
											<p key={msg.componentVersion}>
												{msg.componentVersion
													? msg.componentVersion +
													  ' : '
													: ''}
												{msg.errorMsg}
											</p>
										);
									})
								)}
							</a>
						}
						placement="right"
					>
						<Icon
							className="err-icon"
							type="close-circle"
							theme="filled"
						/>
					</Tooltip>
				);
			}
			default: {
				return null;
			}
		}
	}
	render() {
		const { testStatus } = this.props;
		return this.matchCompTest(testStatus);
	}
}
