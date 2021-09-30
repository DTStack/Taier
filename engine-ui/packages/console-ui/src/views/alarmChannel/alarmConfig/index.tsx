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

import React, { useState, useEffect } from 'react';
import { Breadcrumb, Card, Button, Form, Modal, Icon, Popconfirm, message } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import Api from '../../../api/console';
import { validateConfig, getConfig, getTemplate, formItemLayout, getInitailConfig } from './help';

/** 复用集群管理组件渲染表单逻辑 */
import FormConfig from '../../clusterManage/newEdit/formConfig';
import { COMPONENT_TYPE_VALUE } from '../../clusterManage/newEdit/const';

const confirm = Modal.confirm;

interface IProps extends FormComponentProps {
	router?: any;
}
interface IState {
	loading: boolean;
	componentConfig: string;
	componentTemplate: string;
}

const AlarmConfig: React.FC<IProps> = (props) => {
	const [state, setState] = useState<IState>({
		loading: false,
		componentConfig: JSON.stringify({}),
		componentTemplate: JSON.stringify([]),
	});

	const comp = {
		componentTypeCode: COMPONENT_TYPE_VALUE.SFTP,
		componentTemplate: state.componentTemplate,
		componentConfig: state.componentConfig,
	};

	useEffect(() => {
		const getAlarmConfig = async () => {
			const res = await Api.getAlarmConfig();
			if (res && res?.code == 1) {
				const {
					componentConfig = JSON.stringify({}),
					componentTemplate = JSON.stringify([]),
				} = res?.data;
				setState((state) => ({ ...state, componentConfig, componentTemplate }));
			}
		};
		getAlarmConfig().then();
	}, []);

	const onCancle = () => {
		props.form.setFieldsValue({
			[comp.componentTypeCode]: { componentConfig: getInitailConfig(comp) },
		});
	};

	const onOk = (isTestConnect?: boolean) => {
		props.form.validateFields((err: any, values: any) => {
			console.log(err, values);
			if (err) {
				message.error('请检查配置');
				return;
			}
			const currentComp = values[comp.componentTypeCode];
			if (!isTestConnect) {
				saveConfig(currentComp);
				return;
			}
			testConect(currentComp);
		});
	};

	const saveConfig = (currentComp: any) => {
		const params = {
			componentConfig: JSON.stringify(getConfig(currentComp, comp)),
			componentTemplate: JSON.stringify(getTemplate(currentComp, comp)),
		};
		if (validateConfig(currentComp, comp)) {
			message.success('保存成功');
			setState((state) => ({ ...state, ...params }));
			return;
		}
		confirm({
			title: '确认变更配置？',
			content: '配置变更，可能导致告警通道无法使用，确认变更？',
			icon: <Icon style={{ color: '#FAAD14' }} type="exclamation-circle" theme="filled" />,
			okText: '保存',
			cancelText: '取消',
			onOk: async () => {
				const res = await Api.updateAlarmConfig({ ...params });
				if (res.code !== 1) {
					message.success('保存失败');
					return;
				}
				message.success('保存成功');
				setState((state) => ({ ...state, ...params }));
			},
			onCancel: () => {},
		});
	};

	const testConect = async (currentComp: any) => {
		const params = {
			componentConfig: JSON.stringify(getConfig(currentComp, comp)),
			componentTemplate: JSON.stringify(getTemplate(currentComp, comp)),
		};
		if (!validateConfig(currentComp, comp)) {
			message.error('SFTP配置参数变更未保存，请先保存再测试组件连通性');
			return;
		}
		setState((state) => ({ ...state, loading: true }));
		const res = await Api.testAlarmConfig();
		if (res.code == 1 && res?.data?.result) {
			message.success('测试连通性成功');
			setState({ ...state, ...params, loading: false });
			return;
		}
		message.error('测试连通性失败');
		setState((state) => ({ ...state, loading: false }));
	};

	return (
		<div className="alarm-config__wrapper">
			<Breadcrumb>
				<Breadcrumb.Item>
					{' '}
					<a
						onClick={() => {
							props.router.push('/console-ui/alarmChannel');
						}}
					>
						告警通道
					</a>
				</Breadcrumb.Item>
				<Breadcrumb.Item>SFTP配置</Breadcrumb.Item>
			</Breadcrumb>
			<Card bordered={false}>
				<FormConfig
					view={false}
					form={props.form}
					itemLayout={formItemLayout}
					comp={comp}
				/>
				<footer className="alarm-config__footer">
					<Popconfirm
						title="确认取消当前更改？"
						okText="确认"
						cancelText="取消"
						onConfirm={() => onCancle()}
					>
						<Button>取消</Button>
					</Popconfirm>
					<Button ghost loading={state.loading} onClick={() => onOk(true)}>
						测试SFTP连通性
					</Button>
					<Button type="primary" onClick={() => onOk()}>
						保存SFTP组件
					</Button>
				</footer>
			</Card>
		</div>
	);
};

export default Form.create()(AlarmConfig);
