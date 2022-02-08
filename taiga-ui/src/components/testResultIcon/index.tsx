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

import { CheckCircleFilled, CloseCircleFilled } from '@ant-design/icons';
import { Tooltip, Modal } from 'antd';
import { isArray } from 'lodash';
import classNames from 'classnames';
import './index.scss';

const TEST_STATUS = {
	SUCCESS: true,
	FAIL: false,
};

interface ITestRestIconProps {
	testStatus: {
		componentVersion: null | string;
		errorMsg: null | string;
		result: null | boolean;
	};
}

export default function TestRestIcon({ testStatus }: ITestRestIconProps) {
	const showDetailErrMessage = (engine: ITestRestIconProps['testStatus']) => {
		Modal.error({
			title: `错误信息`,
			content: `${engine.errorMsg}`,
			zIndex: 1061,
		});
	};

	const matchCompTest = (testResult: ITestRestIconProps['testStatus']) => {
		switch (testResult?.result) {
			case TEST_STATUS.SUCCESS: {
				return <CheckCircleFilled className="success-icon" />;
			}
			case TEST_STATUS.FAIL: {
				return (
					<Tooltip
						title={
							<a
								className={classNames('text-white', 'overflow-scroll')}
								onClick={() => showDetailErrMessage(testResult)}
							>
								{!isArray(testResult?.errorMsg) ? (
									<span>{testResult?.errorMsg}</span>
								) : (
									testResult?.errorMsg?.map((msg) => {
										return (
											<p key={msg.componentVersion}>
												{msg.componentVersion
													? `${msg.componentVersion} : `
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
						<CloseCircleFilled className="err-icon" />
					</Tooltip>
				);
			}
			default: {
				return null;
			}
		}
	};
	return matchCompTest(testStatus);
}
