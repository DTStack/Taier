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
import { connect } from 'react-redux';
import { Modal, Button, Form, Input, message, Select, Icon } from 'antd';
import FolderPicker from '../../components/folderPicker';
import { getContainer } from '../resourceManager/resModal';

const { Option } = Select;
const FormItem = Form.Item;
let sqlHead: string;
class FnForm extends React.Component<any, any> {
	constructor(props: any) {
		super(props);
		this.state = {
			textAreaClass: 'dt-fake-textArea',
			type: this.props.fnType || undefined,
			name: 0,
			sqlName: this?.props?.defaultData?.formData?.data?.name || '',
		};
	}

	handleSelectTreeChange(value: any) {
		this.props.form.setFieldsValue({ nodePid: value });
	}

	checkName = (e: any) => {
		if (!e) return;
		this.setState({
			sqlName: e.target.value,
		});
	};

	handleResSelectTreeChange(value: any) {
		this.props.form.setFieldsValue({ resourceIds: value });
		this.props.form.validateFields(['resourceIds']);
	}
	changeFun(e: any) {
		this.setState({
			name: e,
		});
		this.props.isFeture(e);
	}
	render() {
		const { getFieldDecorator } = this.props.form;
		const {
			flags,
			defaultData,
			isCreateFromMenu,
			isCreateNormal,
			fnType,
			schemaName,
			greenPlumFuncTreeData,
		} = this.props;
		const { textAreaClass, name, type, sqlName } = this.state;
		const formItemLayout: any = {
			labelCol: {
				xs: { span: 24 },
				sm: { span: 6 },
			},
			wrapperCol: {
				xs: { span: 24 },
				sm: { span: 14 },
			},
		};
		const formData = defaultData?.formData?.data;
		sqlHead = `create or replace function ${schemaName}.${formData?.name || sqlName}`;

		return (
			<>
				<Form>
					<FormItem {...formItemLayout} label="函数类型">
						{getFieldDecorator('engineType', {
							rules: [
								{
									required: true,
									message: '函数类型不可为空！',
								},
							],
							initialValue: formData?.engineType
								? formData?.engineType
								: Array.isArray(flags) && flags.indexOf('Hadoop') !== -1
								? 1
								: undefined,
						})(
							<Select
								disabled={!!fnType}
								getPopupContainer={() => document.getElementById('molecule')!}
								onChange={(e) => this.changeFun(e)}
							>
								{Array.isArray(flags) && flags.indexOf('Hadoop') !== -1 && (
									<Option value={1}>Spark SQL</Option>
								)}
							</Select>,
						)}
					</FormItem>
					{name !== 6 ? (
						<>
							<FormItem {...formItemLayout} label="函数名称">
								{getFieldDecorator('name', {
									rules: [
										{
											required: true,
											message: '函数名称不可为空！',
										},
										{
											pattern: /^[a-z0-9_]+$/,
											message: '函数名称只能由小写字母、数字、下划线组成!',
										},
										{
											max: 20,
											message: '函数名称不得超过20个字符！',
										},
									],
									initialValue: formData ? formData.name : undefined,
								})(<Input placeholder="请输入函数名称" disabled={!!formData} />)}
							</FormItem>
							<FormItem {...formItemLayout} label="类名">
								{getFieldDecorator('className', {
									rules: [
										{
											required: true,
											message: '类名不能为空',
										},
										{
											pattern:
												/^[a-zA-Z]+[0-9a-zA-Z_]*(\.[a-zA-Z]+[0-9a-zA-Z_]*)*$/,
											message: '请输入有效的类名!',
										},
									],
									initialValue: formData ? formData.className : undefined,
								})(<Input placeholder="请输入类名"></Input>)}
							</FormItem>
							<FormItem {...formItemLayout} label="资源">
								{getFieldDecorator('resourceIds', {
									rules: [
										{
											required: true,
											message: '请选择关联资源',
										},
										{
											validator: this.checkNotDir.bind(this),
										},
									],
									initialValue: formData ? formData.resources : undefined,
								})(<Input type="hidden"></Input>)}
								<FolderPicker
									dataType="resource"
									showFile
									defaultValue={formData ? formData.resources : undefined}
									onChange={this.handleResSelectTreeChange.bind(this)}
								/>
							</FormItem>
							<FormItem {...formItemLayout} label="用途">
								{getFieldDecorator('purpose', {
									initialValue: formData ? formData.purpose : undefined,
								})(<Input placeholder=""></Input>)}
							</FormItem>
							<FormItem {...formItemLayout} label="命令格式">
								{getFieldDecorator('commandFormate', {
									rules: [
										{
											required: true,
											message: '请输入命令格式',
										},
										{
											max: 128,
											message: '描述请控制在128个字符以内！',
										},
									],
									initialValue: formData ? formData.commandFormate : undefined,
								})(<Input placeholder=""></Input>)}
							</FormItem>
							<FormItem {...formItemLayout} label="参数说明">
								{getFieldDecorator('paramDesc', {
									rules: [
										{
											max: 200,
											message: '描述请控制在200个字符以内！',
										},
									],
									initialValue: formData?.paramDesc
										? formData?.paramDesc
										: undefined,
								})(<Input.TextArea rows={4} placeholder="请输入函数的参数说明" />)}
							</FormItem>
							<FormItem {...formItemLayout} label="选择存储位置">
								{getFieldDecorator('nodePid', {
									rules: [
										{
											required: true,
											message: '存储位置必选！',
										},
									],
									initialValue: formData
										? formData?.nodePid
										: isCreateNormal
										? this.props.functionTreeData.id
										: isCreateFromMenu
										? defaultData.parentId
										: undefined,
								})(<Input type="hidden"></Input>)}
								<FolderPicker
									showFile={false}
									dataType="function"
									onChange={this.handleSelectTreeChange.bind(this)}
									defaultValue={
										formData
											? formData?.nodePid
											: isCreateNormal
											? this.props.functionTreeData.id
											: isCreateFromMenu
											? defaultData.parentId
											: undefined
									}
								/>
							</FormItem>
						</>
					) : (
						<>
							<FormItem {...formItemLayout} label="函数名称">
								{getFieldDecorator('name', {
									rules: [
										{
											required: true,
											message: '函数名称不可为空！',
										},
										{
											pattern: /^[a-z|A-Z]{1}/,
											message: '函数名称开头必须是英文!',
										},
										{
											pattern: /^[a-z0-9_]+$/,
											message: '函数名称只能由小写字母、数字、下划线组成!',
										},
										{
											max: 20,
											message: '函数名称不得超过20个字符！',
										},
									],
									initialValue: formData ? formData.name : undefined,
								})(
									<Input
										placeholder="请输入函数名称"
										onChange={(e) => {
											this.checkName(e);
										}}
										disabled={!!formData}
									/>,
								)}
							</FormItem>
							<FormItem {...formItemLayout} label="用途">
								{getFieldDecorator('purposes', {
									initialValue: formData ? formData.purpose : undefined,
								})(<Input placeholder=""></Input>)}
							</FormItem>
							<FormItem {...formItemLayout} label="SQL">
								<div className={textAreaClass}>
									<span>{sqlHead}</span>
									{getFieldDecorator('sqlText', {
										rules: [
											{
												required: true,
												message: 'SQL不能为空！',
											},
										],
										initialValue:
											formData && formData.sqlText
												? formData.sqlText.substring(
														formData.sqlText.indexOf('('),
														formData.sqlText.length,
												  )
												: undefined,
									})(
										<Input.TextArea
											rows={6}
											placeholder={`()\nRETURNS character varying\n   LANGUAGE 'plpgsql'\n   COST 100\n   VOLATILE\nAS $BODY$ BEGIN perform "PRO_BU_MAINTAIN"(to_Char(now(),'yyyy-mm-dd')::varchar);\n     RETURN 'ok';\nEND\n$BODY$;\n\nALTER FUNCTION ods."PRO_COMPUT_MAINTAINCOMPUT"() OWNER TO bi;`}
											onFocus={() => {
												this.setState({
													textAreaClass:
														'dt-fake-textArea dt-fake-textarea-focus',
												});
											}}
											onBlur={() => {
												this.setState({
													textAreaClass: 'dt-fake-textArea',
												});
											}}
										/>,
									)}
								</div>
							</FormItem>
							<FormItem {...formItemLayout} label="参数说明">
								{getFieldDecorator('paramDescs', {
									rules: [
										{
											max: 200,
											message: '描述请控制在200个字符以内！',
										},
									],
									initialValue: formData ? formData.paramDesc : undefined,
								})(<Input.TextArea rows={4} placeholder="请输入函数的参数说明" />)}
							</FormItem>
							<FormItem {...formItemLayout} label="存储位置">
								{getFieldDecorator('nodePid', {
									rules: [
										{
											required: true,
											message: '存储位置必选！',
										},
									],
									initialValue: formData
										? formData.nodePid
										: greenPlumFuncTreeData?.id,
								})(<Input type="hidden"></Input>)}
								<FolderPicker
									dataType="function"
									showFile={false}
									onChange={this.handleSelectTreeChange.bind(this)}
									defaultValue={
										formData ? formData.nodePid : greenPlumFuncTreeData?.id
									}
								/>
							</FormItem>
						</>
					)}
				</Form>
			</>
		);
	}

	/* eslint-disable */
	/**
	 * @description 检查所选是否为文件夹
	 * @param {any} rule
	 * @param {any} value
	 * @param {any} cb
	 * @memberof TaskForm
	 */
	checkNotDir(rule: any, value: any, callback: any) {
		const { resTreeData } = this.props;
		let nodeType: any;

		let loop = (arr: any) => {
			arr.forEach((node: any, i: any) => {
				if (node.id === value) {
					nodeType = node.type;
				} else {
					loop(node.children || []);
				}
			});
		};

		loop([resTreeData]);

		if (nodeType === 'folder') {
			callback('请选择具体文件, 而非文件夹');
		}
		callback();
	}
	/* eslint-disable */

	/**
	 * @description 获取节点名称
	 * @param {any} id
	 * @memberof FolderForm
	 */
	getFolderName(id: any) {
		const { functionTreeData } = this.props;
		let name: any;
		let loop = (arr: any) => {
			arr.forEach((node: any, i: any) => {
				if (node.id === id) {
					name = node.id;
				} else {
					loop(node.children || []);
				}
			});
		};

		loop([functionTreeData]);

		return name;
	}
}

const FnFormWrapper = Form.create<any>()(FnForm);

class FnModal extends React.Component<any, any> {
	constructor(props: any) {
		super(props);
		this.state = {
			schemaName: undefined,
			type: 'spark',
			flags: [],
		};
		this.dtcount = 0;
	}
	form: any;
	dtcount: number;
	componentDidMount() {
		const { isModalShow, engine } = this.props;
		const engineArray: any[] = [];
		if (isModalShow === true) {
			engine.forEach((item: any) => {
				engineArray.push(item?.name);
			});
			this.setState({
				flags: engineArray,
			});
		}
	}
	componentDidUpdate(prevProps: any) {
		const { isModalShow, engine } = this.props;
		const { flags } = this.state;
		let engineArray: any[] = [];
		if (flags.length === 0 && isModalShow === true) {
			engine.forEach((item: any) => {
				engineArray.push(item?.name);
			});
			this.setState({
				flags: engineArray,
			});
		} else {
			engineArray = flags;
		}
	}

	changeType = (e: any) => {
		this.setState({
			type: 'spark',
		});
	};
	handleSubmit = () => {
		const { addFn, engineType, editFn } = this.props;
		const form = this.form;
		form.validateFields((err: any, values: any) => {
			if (!err) {
				const { defaultData } = this.props;
				const formData = defaultData?.formData?.data;
				const form = this.form;
				const id = formData?.id;
				if (id) {
					editFn(
						Object.assign(values, {
							engineType: engineType,
							id,
						}),
					)
						.then((res: any) => {
							if (res && res.code == 1) {
								message.success('编辑成功');
								this.closeModal();
								form.resetFields();
							}
						})
						.catch((err: Error) => {
							this.setState({ errorMsg: err.message });
						});
				} else {
					addFn(
						Object.assign(values, {
							engineType: engineType,
						}),
					)
						.then((res: any) => {
							if (res && res.code == 1) {
								message.success('创建成功');
								this.closeModal();
								form.resetFields();
							}
						})
						.catch((err: Error) => {
							this.setState({ errorMsg: err.message });
						});
				}
			}
		});
	};

	handleCancel = () => {
		this.closeModal();
		this.setState({ errorMsg: null });
	};
	showLog = () => {
		const { errMsg } = this.state;
		Modal.error({
			title: '错误日志',
			content: errMsg,
		});
	};
	closeModal() {
		this.dtcount++;
		this.props.toggleCreateFn();
		this.setState({
			flags: [],
		});
	}

	render() {
		const {
			fnType,
			isModalShow,
			functionTreeData,
			greenPlumFuncTreeData,
			resTreeData,
			defaultData,
		} = this.props;
		const { schemaName, flags } = this.state;
		const isCreateNormal = typeof defaultData === 'undefined';
		const isCreateFromMenu = !isCreateNormal && typeof defaultData.id === 'undefined';
		const isEditExist = !isCreateNormal && !isCreateFromMenu;
		const flag = defaultData?.formData?.data?.name;
		const master = flags.indexOf('Greenplum') !== -1 ? schemaName : true;
		let title;
		if (flag) title = '修改自定义函数';
		else title = '新建自定义函数';
		return (
			<div id="JS_func_modal">
				<Modal
					title={title}
					visible={isModalShow && master}
					footer={[
						<Button key="back" size="large" onClick={this.handleCancel}>
							取消
						</Button>,
						<Button
							key="submit"
							type="primary"
							size="large"
							onClick={this.handleSubmit}
						>
							{' '}
							确认{' '}
						</Button>,
					]}
					key={this.dtcount}
					onCancel={this.handleCancel}
					getContainer={() => getContainer('JS_func_modal')}
				>
					{flag && (
						<div className="task_offline_message">
							<Icon type="exclamation-circle-o" style={{ marginRight: 7 }} />
							替换资源时，如果资源的新文件与现有文件名称保持一致，那么替换后关联函数对应任务可立即生效，否则关联函数对应任务需重新提交才可生效。
						</div>
					)}
					<FnFormWrapper
						ref={(el: any) => (this.form = el)}
						functionTreeData={functionTreeData}
						greenPlumFuncTreeData={greenPlumFuncTreeData}
						resTreeData={resTreeData}
						fnType={fnType}
						flags={flags}
						schemaName={schemaName}
						isCreateFromMenu={isCreateFromMenu}
						isCreateNormal={isCreateNormal}
						isEditExist={isEditExist}
						defaultData={defaultData}
						isFeture={this.changeType}
					/>
				</Modal>
			</div>
		);
	}
}

export default connect((state: any) => {
	return {
		resTreeData: state.catalogue.resourceTree,
	};
})(FnModal);
