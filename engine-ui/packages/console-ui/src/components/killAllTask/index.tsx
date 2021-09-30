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
import { Modal, message } from 'antd';

import styled from 'styled-components';

import Api from '../../api/console';

export const RedTxt = styled.span`
	color: #ff5f5c;
`;
class KillAllTask extends React.Component<any, any> {
	state: any = {
		confirmLoading: false,
	};
	// 请求杀任务接口
	killTask() {
		const { killResource = [] } = this.props;
		const params = this.getReqParams();
		Api.killTasks(params).then((res: any) => {
			if (res.code == 1) {
				// 杀死全部任务为异步有延迟，需要延迟执行刷新数据操作
				setTimeout(() => {
					message.success('操作成功');
					this.props.killSuccess(killResource);
					this.props.autoRefresh();
					this.props.onCancel();
					this.setState({
						confirmLoading: false,
					});
				}, 1000);
			} else {
				this.setState({
					confirmLoading: false,
				});
			}
		});
	}

	getReqParams() {
		const { killResource = [], node, stage, jobResource, totalModel } = this.props;
		let params = {
			stage,
			jobResource,
			jobIdList: [],
			nodeAddress: node,
		};
		const isKillAll = totalModel !== undefined;
		if (isKillAll) {
			params.jobIdList = []; // Kill All when array is null
		} else {
			// 杀死选中的任务
			params.jobIdList = killResource.map((job) => job.jobId);
		}
		console.log('params:', params);
		return params;
	}

	confirmKillTask() {
		this.setState({
			confirmLoading: true,
		});
		this.killTask();
	}

	render() {
		const { totalModel } = this.props;
		const isKillAll = totalModel !== undefined;
		const title = isKillAll ? `杀死全部任务` : `杀死选中任务`;
		const htmlText = isKillAll ? (
			<div>
				<RedTxt>本操作将杀死列表（跨分页）中的全部任务，不仅是当前页</RedTxt>
				<br />
				<RedTxt>杀死运行中的任务需要较长时间</RedTxt>
			</div>
		) : (
			<RedTxt>本操作将杀死列表（非跨分页）中的选中任务</RedTxt>
		);
		return (
			<Modal
				title={title}
				visible={this.props.visible}
				okText={title}
				okType="danger"
				confirmLoading={this.state.confirmLoading}
				onCancel={this.props.onCancel}
				onOk={this.confirmKillTask.bind(this)}
			>
				{htmlText}
			</Modal>
		);
	}
}
export default KillAllTask;
