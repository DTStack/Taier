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
import { Button, Popconfirm, Checkbox, Radio, Row, Col, message } from 'antd';
import _ from 'lodash';

import { isSourceTab } from '../../help';
import { CONFIG_BUTTON_TYPE, COMPONENT_CONFIG_NAME, COMP_ACTION } from '@/constant';
import './index.scss';

const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;

interface IProps {
	activeKey: number;
	comps: any[];
	popVisible: boolean;
	handleConfirm: Function;
	handlePopVisible: Function;
}

interface IState {
	visible: boolean;
	addComps: any[];
	initialValues: any[];
}

export default class ComponentButton extends React.Component<IProps, IState> {
	state: IState = {
		visible: false,
		addComps: [],
		initialValues: [],
	};

	componentDidMount() {
		this.setState({
			initialValues: this.getInitialValues(),
		});
	}

	componentDidUpdate(preProps: any) {
		const { comps, popVisible } = this.props;
		if (preProps.comps !== comps || (preProps.popVisible !== popVisible && popVisible)) {
			this.setState({
				initialValues: this.getInitialValues(),
			});
		}
	}

	getInitialValues = () => {
		const { comps = [] } = this.props;
		return comps.map((comp: any) => comp?.componentTypeCode);
	};

	handleSelectValue = () => {
		const { comps } = this.props;
		const selectValues = comps.map((comp) => comp.componentTypeCode);
		return selectValues;
	};

	handleCheckValues = (value: any[]) => {
		const { activeKey } = this.props;
		const initialValues = this.getInitialValues();

		if (isSourceTab(activeKey)) {
			return;
		}

		// 和初始值取一次合集，一次交集可得增加的组件
		const unionArr = _.union(value, initialValues);
		const addComps = _.xor(unionArr, initialValues);
		this.setState({
			addComps,
			initialValues: value,
		});
	};

	handleRadioValues = (e: any) => {
		const initialValues = this.getInitialValues();

		/**
		 * 初始值和选中值不一致时，若已选中值，则提示需删除组件
		 */
		if (!_.isEqual(initialValues[0], e.target.value)) {
			if (initialValues[0]) {
				message.error(
					`先删除${COMPONENT_CONFIG_NAME[initialValues[0] as keyof typeof COMPONENT_CONFIG_NAME]}，才能切换为${
						COMPONENT_CONFIG_NAME[e.target.value as keyof typeof COMPONENT_CONFIG_NAME]
					}`,
				);
				return;
			}
			const addComps = [];
			addComps.push(e.target.value);
			this.setState({
				addComps,
				initialValues: [e.target.value],
			});
		}
	};

	renderTitle = () => {
		return (
			<div className="c-componentButton__title">
				<span>组件配置</span>
			</div>
		);
	};

	renderContent = () => {
		const { activeKey } = this.props;
		const { initialValues } = this.state;

		if (isSourceTab(activeKey)) {
			return (
				<>
					{this.renderTitle()}
					<RadioGroup
						className="c-componentButton__content"
						defaultValue={initialValues[0]}
						value={initialValues[0]}
						onChange={this.handleRadioValues}
					>
						<Row>
							{CONFIG_BUTTON_TYPE[activeKey].map((item: any) => {
								return (
									<Col key={`${item.code}`} span={24}>
										<Radio
											disabled={
												this.getInitialValues().indexOf(item.code) > -1
											}
											value={item.code}
										>
											{item.componentName}
										</Radio>
									</Col>
								);
							})}
						</Row>
					</RadioGroup>
				</>
			);
		}
		return (
			<>
				{this.renderTitle()}
				<CheckboxGroup
					className="c-componentButton__content"
					value={initialValues}
					defaultValue={initialValues}
					onChange={this.handleCheckValues}
				>
					<Row>
						{CONFIG_BUTTON_TYPE[activeKey].map((item: any) => {
							return (
								<Col key={`${item.code}`} span={24}>
									<Checkbox
										disabled={this.getInitialValues().indexOf(item.code) > -1}
										value={item.code}
									>
										{item.componentName}
									</Checkbox>
								</Col>
							);
						})}
					</Row>
				</CheckboxGroup>
			</>
		);
	};

	handleConfirm = () => {
		const { addComps } = this.state;
		this.props.handlePopVisible(false);
		this.props.handleConfirm(COMP_ACTION.ADD, addComps);
	};

	handleCancel = () => {
		this.setState({
			addComps: [],
			visible: false,
		});
		this.props.handlePopVisible(false);
	};

	render() {
		return (
			<>
				<Popconfirm
					icon={null}
					placement="topRight"
					title={this.renderContent()}
					onConfirm={this.handleConfirm}
					onCancel={this.handleCancel}
				>
					<Button
						className="c-editCluster__componentButton"
						onClick={() => this.props.handlePopVisible()}
						block
					>
						<i className="iconfont icon-zujianpeizhi" style={{ marginRight: 2 }} />
						组件配置
					</Button>
				</Popconfirm>
			</>
		);
	}
}
