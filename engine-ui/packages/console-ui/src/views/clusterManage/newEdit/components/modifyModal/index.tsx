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
import { Modal } from 'antd';
import { COMPONENT_CONFIG_NAME, COMPONENT_TYPE_VALUE } from '../../const';

interface IProps {
	deleteComps: any[];
	addComps: any[];
	visible: boolean;
	onOk: Function;
	onCancel: Function;
}

export default class ModifyCompsModal extends React.Component<IProps, any> {
	render() {
		const { onOk, onCancel, deleteComps, addComps, visible } = this.props;
		const compsName = deleteComps.map((code) => COMPONENT_CONFIG_NAME[code]);
		const isRadio =
			[COMPONENT_TYPE_VALUE.YARN, COMPONENT_TYPE_VALUE.KUBERNETES].indexOf(addComps[0]) > -1;

		return (
			<Modal
				title="修改组件配置"
				onOk={() => onOk()}
				onCancel={() => onCancel()}
				visible={visible}
				className="c-clusterManage__modal"
			>
				{isRadio ? (
					<span>
						切换到 {COMPONENT_CONFIG_NAME[addComps[0]]} 后 {compsName[0]}{' '}
						的配置信息将丢失，确认切换到 {COMPONENT_CONFIG_NAME[addComps[0]]}？
					</span>
				) : (
					<span>
						删除 {compsName.join('、')} 组件后相应配置信息将丢失，确定删除{' '}
						{compsName.join('、')} 组件？
					</span>
				)}
			</Modal>
		);
	}
}
