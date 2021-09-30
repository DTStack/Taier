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

import * as React from 'react';
import { Checkbox, Form, Tooltip, Icon } from 'antd';
import { MAPPING_DEFAULT_VERSION } from '../../../const';

interface IProps {
	comp: any;
	form: any;
	view: boolean;
	isDefault: boolean;
}

export default class DefaultVersionCheckbox extends React.PureComponent<IProps, any> {
	getCheckValue = () => {
		const { comp, isDefault } = this.props;
		if (isDefault) return true;
		return comp?.isDefault ?? false;
	};

	componentDidUpdate(props) {
		const { comp, isDefault, form } = this.props;
		const typeCode = comp?.componentTypeCode ?? '';
		const hadoopVersion = comp?.hadoopVersion ?? '';
		if (isDefault && isDefault !== props.isDefault) {
			form.setFieldsValue({
				[`${typeCode}.${hadoopVersion}.isDefault`]: true,
			});
		}
	}

	validDefaultdata = (rule: any, value: any, callback: any) => {
		const { form, comp, isDefault } = this.props;
		const error = '请设置默认版本';
		// 只有一个版本时
		if (isDefault && !value) {
			callback(error);
			return;
		}
		// 有多个版本时，都没选得提示
		let hasTrue = false;
		const typeCode = comp?.componentTypeCode ?? '';
		for (const v of MAPPING_DEFAULT_VERSION) {
			hasTrue = hasTrue || !!form.getFieldValue(`${typeCode}.${v}.isDefault`);
		}
		// 传 null 或者 undefined 回调都不会继续，没法三元
		if (hasTrue) {
			callback();
		} else {
			callback(error);
		}
	};

	handleChange = (e: any) => {
		const { form, comp, isDefault } = this.props;
		const typeCode = comp?.componentTypeCode ?? '';
		const hadoopVersion = comp?.hadoopVersion ?? '';

		if (!isDefault && e.target.checked) {
			for (const v of MAPPING_DEFAULT_VERSION) {
				if (v !== hadoopVersion) {
					form.setFieldsValue({
						[`${typeCode}.${v}.isDefault`]: !e.target.checked,
					});
				}
			}
		}
		form.setFieldsValue({
			[`${typeCode}.${hadoopVersion}.isDefault`]: e.target.checked,
		});
	};

	render() {
		const { form, comp, view } = this.props;
		const typeCode = comp?.componentTypeCode ?? '';
		const hadoopVersion = comp?.hadoopVersion ?? '';

		return (
			<>
				<Form.Item label={null} colon={false}>
					{form.getFieldDecorator(`${typeCode}.${hadoopVersion}.isDefault`, {
						valuePropName: 'checked',
						initialValue: this.getCheckValue(),
						rules: [
							{
								validator: this.validDefaultdata,
							},
						],
					})(
						<Checkbox disabled={view} onChange={this.handleChange}>
							设置为默认版本
						</Checkbox>,
					)}
					<Tooltip
						overlayClassName="big-tooltip"
						title="默认版本将用于离线开发的数据同步与实时开发的实时采集"
					>
						<Icon style={{ marginLeft: 4 }} type="question-circle-o" />
					</Tooltip>
				</Form.Item>
			</>
		);
	}
}
