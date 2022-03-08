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
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { Scrollable } from '@dtinsight/molecule/esm/components';
import DataSync from './dataSync';
import { cloneDeep, assign } from 'lodash';
import { connect as moleculeConnect } from '@dtinsight/molecule/esm/react';
import molecule from '@dtinsight/molecule';
import type { IEditor } from '@dtinsight/molecule/esm/model';
import store from '../../store';
import { workbenchActions, getDataSyncReqParams } from '@/reducer/dataSync/offlineAction';
import * as editorActions from '@/reducer/editor/editorAction';
import { TASK_TYPE_ENUM, DATA_SYNC_MODE } from '@/constant';
import './index.scss';

const propType: any = {
	editor: PropTypes.object,
	toolbar: PropTypes.object,
	console: PropTypes.object,
};
const initialState = {
	changeTab: true,
	size: undefined,
	runTitle: 'Command/Ctrl + R',
};
type Istate = typeof initialState;

/**
 * 数据同步任务拼接参数
 */
export function generateRqtBody() {
	const currentTabData = molecule.editor.getState().current?.tab?.data;
	const dataSync = (store.getState() as any).dataSync.dataSync;

	// deepClone避免直接mutate store
	let reqBody = cloneDeep(currentTabData);
	// 如果当前任务为数据同步任务
	if (currentTabData.taskType === TASK_TYPE_ENUM.SYNC) {
		const isIncrementMode =
			currentTabData.syncModel !== undefined &&
			DATA_SYNC_MODE.INCREMENT === currentTabData.syncModel;
		reqBody = assign(reqBody, getDataSyncReqParams(dataSync));
		if (!isIncrementMode) {
			reqBody.sourceMap.increColumn = undefined; // Delete increColumn
		}
	}
	// 修改task配置时接口要求的标记位
	reqBody.preSave = true;

	// 接口要求上游任务字段名修改为dependencyTasks
	if (reqBody.taskVOS) {
		reqBody.dependencyTasks = reqBody.taskVOS.map((o: any) => o);
		reqBody.taskVOS = null;
	}

	// 删除不必要的字段
	delete reqBody.taskVersions;
	delete reqBody.dataSyncSaved;

	// 数据拼装结果
	return reqBody;
}

@(connect(null, (dispatch: any) => {
	const taskAc = workbenchActions(dispatch);
	const editorAc = bindActionCreators(editorActions, dispatch);
	const actions = Object.assign(editorAc, taskAc);
	return actions;
}) as any)
class DataSyncWorkbench extends React.Component<
	IEditor & ReturnType<typeof workbenchActions>,
	Istate
> {
	state = {
		changeTab: true,
		size: undefined,
		runTitle: 'Command/Ctrl + R',
	};

	static propTypes = propType;

	changeTab = (state: any) => {
		let changeTab = false;
		if (state) {
			changeTab = true;
		} else {
			changeTab = false;
		}

		this.setState({
			changeTab,
		});
	};
	/**
	 * @description 拼装接口所需数据格式
	 * @param {any} data 数据同步job配置对象
	 * @returns {any} result 接口所需数据结构
	 * @memberof DataSync
	 */

	generateRqtBody() {
		return generateRqtBody();
	}

	saveTab(isSave: any, saveMode: any) {
		// 每次保存都意味着当前tab不是第一次打开，重置当前标示
		this.setState({
			changeTab: false,
		});
		const isButtonSubmit = saveMode === 'popOut';
		this.props.isSaveFInish(false);
		const { saveTab } = this.props;

		const saveData = this.generateRqtBody();
		const type = 'task';

		saveTab(saveData, isSave, type, isButtonSubmit);
	}

	render() {
		const currentTabData = this.props.current?.tab?.data;

		return (
			<Scrollable>
				<div className="ide-editor">
					<div style={{ zIndex: 901 }} className="ide-content">
						<div
							style={{
								width: '100%',
								height: '100%',
								minHeight: '400px',
								position: 'relative',
							}}
						>
							<DataSync
								saveTab={this.saveTab.bind(this, true)}
								currentTabData={currentTabData}
							/>
						</div>
					</div>
				</div>
			</Scrollable>
		);
	}
}

export default moleculeConnect(molecule.editor, DataSyncWorkbench);
