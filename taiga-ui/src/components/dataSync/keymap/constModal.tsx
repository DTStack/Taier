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

/* eslint-disable no-template-curly-in-string */
import * as React from 'react';
import { Modal, Input, message, Select } from 'antd';

import { Utils } from '@dtinsight/dt-utils';
import HelpDoc, { relativeStyle } from '../../../components/helpDoc';
import './index.scss';

const systemVariable = [
	'${bdp.system.bizdate}',
	'${bdp.system.bizdate2}',
	'${bdp.system.cyctime}',
	'${bdp.system.premonth}',
	'${bdp.system.currmonth}',
	'${bdp.system.runtime}',
];
const Option = Select.Option;
export default class ConstModal extends React.Component<any, any> {
	state: any = {
		constValue: '',
		constName: '',
		constFormat: '',
		type: 'STRING',
	};

	onChange = (e: any) => {
		this.setState({
			constValue: e.target.value,
		});
	};

	onChangeType = (type: any) => {
		this.setState({
			type,
		});
	};

	onNameChange = (e: any) => {
		this.setState({
			constName: e.target.value,
		});
	};

	oFormatChange = (e: any) => {
		this.setState({
			constFormat: e.target.value,
		});
	};

	submit = () => {
		const { onOk } = this.props;
		const constValue = Utils.trim(this.state.constValue);
		const constName = Utils.trim(this.state.constName);
		const constFormat = Utils.trim(this.state.constFormat);
		const type = Utils.trim(this.state.type);

		if (constName === '') {
			message.error('常量名称不可为空！');
			return;
		}

		if (systemVariable.indexOf(constValue) > -1 && type === 'TIMESTAMP') {
			message.error('常量的值中存在参数时类型不可选timestamp！');
			return;
		}

		if (constValue === '') {
			message.error('常量值不可为空！');
			return;
		}
		const constObj: any = {
			type,
			key: constName,
			value: constValue,
			format: constFormat,
		};
		if (onOk) {
			onOk(constObj);
			this.close();
		}
	};

	close = () => {
		const { onCancel } = this.props;
		this.setState({ constValue: '', constName: '' }, () => {
			if (onCancel) onCancel();
		});
	};

	render() {
		const { constValue, constName, constFormat, type } = this.state;
		const { visible } = this.props;
		/* eslint-disable */
		return (
			<Modal
				title="添加常量"
				onOk={this.submit}
				onCancel={this.close}
				visible={visible}
			>
				<div className="flex batch-dataSync_form">
					<span>名称 :</span>
					<Input
						value={constName}
						onChange={this.onNameChange}
						placeholder="请输入常量名称"
					/>
				</div>
				<div className="flex batch-dataSync_form">
					<span>值 : </span>
					<Input
						style={{ width: '440px', marginLeft: 11 }}
						value={constValue}
						onChange={this.onChange}
						placeholder="请输入常量值"
					/>
				</div>
				<div className="flex batch-dataSync_form">
					<span>类型 :</span>
					<Select
						style={{ width: '440px' }}
						placeholder="请选择类型"
						value={type}
						onChange={this.onChangeType}
					>
						<Option value="STRING">STRING</Option>
						<Option value="DATE">DATE</Option>
						<Option value="TIMESTAMP">TIMESTAMP</Option>
					</Select>
				</div>
				<div className="flex batch-dataSync_form">
					<span>格式 :</span>
					<Input
						value={constFormat}
						onChange={this.oFormatChange}
						placeholder="格式化, 例如：yyyy-MM-dd"
					/>
				</div>
				<p style={{ marginTop: '10px' }}>
					1.输入的常量值将会被英文单引号包括，如'abc'、'123'等
				</p>
				<p>
					2.可以配合调度参数使用，如 ${`{bdp.system.bizdate}`}等{' '}
					<HelpDoc style={relativeStyle} doc="customSystemParams" />
				</p>
				<p>3.如果您输入的值无法解析，则类型显示为'未识别'</p>
			</Modal>
		);
	}
}
