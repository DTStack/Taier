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
import { Button, Form, Input } from 'antd';
import molecule from '@dtinsight/molecule/esm';
import { Scrollable } from '@dtinsight/molecule/esm/components';
import { WrappedFormUtils } from 'antd/lib/form/Form';
import FormItem from 'antd/lib/form/FormItem';

import FolderPicker from '../../components/folderPicker';

const formItemLayout = {
	labelCol: {
		xs: { span: 24 },
		sm: { span: 6 },
	},
	wrapperCol: {
		xs: { span: 24 },
		sm: { span: 14 },
	},
};
const tailFormItemLayout = {
	wrapperCol: {
		xs: {
			span: 24,
			offset: 0,
		},
		sm: {
			span: 16,
			offset: 8,
		},
	},
};

interface EditFolderProps {
	currentId?: number;
	onSubmitFolder?: (values: any) => Promise<boolean>;
	record?: any;
	form: WrappedFormUtils<any>;
	current?: any;
	tabId?: string | number;
}

class EditFolder extends React.PureComponent<EditFolderProps, {}> {
	state = {
		loading: false,
	};

	handleSubmit = (e: any) => {
		e.preventDefault();
		this.props.form.validateFieldsAndScroll((err, values) => {
			if (!err) {
				this.setState(
					{
						loading: true,
					},
					() => {
						const params = { ...values };
						this.props.onSubmitFolder?.(params).then((loading) => {
							this.setState({
								loading,
							});
						});
					},
				);
			}
		});
	};

	componentDidMount() {
		const { record, current, tabId } = this.props;
		const {
			tab: { data },
		} = current;
		if (data.id === undefined) {
			this.updateTabData({
				id: record?.id ?? tabId,
				nodeName: record?.name,
				nodePid: record?.parentId,
			});
		}
		this.syncTabData2Form();
	}

	syncTabData2Form = () => {
		const { form, current } = this.props;
		const { data } = current.tab;
		const { nodePid, nodeName } = data;
		form.setFieldsValue({
			nodeName,
			nodePid,
		});
	};

	updateTabData = (values: any) => {
		const { current } = this.props;
		molecule.editor.updateTab({
			...current.tab,
			data: {
				...current.tab.data,
				...values,
			},
		});
	};

	render() {
		const { getFieldDecorator } = this.props.form;
		const { loading } = this.state;
		return (
			<Scrollable>
				<Form onSubmit={this.handleSubmit} className="mo-open-task">
					<FormItem {...formItemLayout} label="目录名称">
						{getFieldDecorator('nodeName', {
							rules: [
								{
									max: 64,
									message: '任务名称不得超过20个字符！',
								},
								{
									required: true,
								},
							],
						})(
							<Input
								autoComplete={'off'}
								onChange={(e: any) => {
									this.updateTabData({ nodeName: e.target.value });
								}}
							/>,
						)}
					</FormItem>
					<FormItem {...formItemLayout} label="选择目录位置">
						{getFieldDecorator('nodePid', {
							rules: [
								{
									required: true,
								},
							],
						})(
							<FolderPicker
								showFile={false}
								dataType="task"
								onChange={(value: any) => {
									this.updateTabData({ nodePid: value });
								}}
							/>,
						)}
					</FormItem>
					<FormItem {...tailFormItemLayout}>
						<Button type="primary" htmlType="submit" loading={loading}>
							确认
						</Button>
					</FormItem>
				</Form>
			</Scrollable>
		);
	}
}

export default Form.create<EditFolderProps>({ name: 'editFolder' })(EditFolder);
