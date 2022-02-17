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
import { Steps } from 'antd';
import { connect } from 'react-redux';
import { isEqual, get } from 'lodash';

import DataSyncSource from './source';
import DataSyncTarget from './target';
import DataSyncKeymap from './keymap';
import DataSyncChannel from './channel';
import DataSyncSave from './save';
import ajax from '../../api';
import { DATA_SYNC_MODE } from '@/constant';
import {
	dataSourceListAction,
	dataSyncAction,
	workbenchAction,
} from '@/reducer/dataSync/actionType';
import {
	getDataSyncSaveTabParams,
	workbenchActions as WBenchActions,
} from '@/reducer/dataSync/offlineAction';
import { getTenantId, getUserId } from '@/utils';
import { API } from '../../api/dataSource';
import './dataSync.scss';

const Step = Steps.Step;

class DataSync extends React.Component<any, any> {
	state: any = {
		currentStep: 0,
		loading: false,
	};

	_datasyncDom: any;
	componentDidMount() {
		const { currentTabData } = this.props;

		this.getJobData({ taskId: currentTabData?.id });
		this.props.setTabId(currentTabData?.id);
		this.props.getDataSource();
	}

	//   eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
		// 如果列发生变化，则检测更新系统变量
		if (this.onVariablesChange(this.props, nextProps)) {
			this.props.updateDataSyncVariables(
				nextProps.sourceMap,
				nextProps.targetMap,
				nextProps.taskCustomParams,
			);
		}
	}

	onVariablesChange = (oldProps: any, nextProps: any) => {
		const { sourceMap: oldSource, targetMap: oldTarget } = oldProps;
		const { sourceMap, targetMap } = nextProps;
		if (
			(Number(oldSource.sourceId) !== Number(sourceMap.sourceId) ||
				Number(oldTarget.sourceId) !== Number(targetMap.sourceId)) &&
			oldSource.sourceId &&
			oldTarget.sourceId
		) {
			return false;
		}

		const oldSQL = oldTarget && oldTarget.type && oldTarget.type.preSql;
		const newSQL = targetMap && targetMap.type && targetMap.type.preSql;

		const oldPostSQL = get(oldTarget, 'type.postSql');
		const newPostSQL = get(targetMap, 'type.postSql');
		const oldWhere = get(oldSource, 'type.where');
		const newWhere = get(sourceMap, 'type.where');

		const oldSourceObject = get(oldSource, 'type.objects');
		const newSourceObejct = get(sourceMap, 'type.objects');
		const oldTargetObject = get(oldTarget, 'type.object');
		const newTargetObject = get(targetMap, 'type.object');

		const oldSourceTable = get(oldSource, 'type.table');
		const newSourceTable = get(sourceMap, 'type.table');
		const oldTargetTable = get(oldTarget, 'type.table');
		const newTargetTable = get(targetMap, 'type.table');

		const oldSourcePartition = get(oldSource, 'type.partition');
		const newSourcePartition = get(sourceMap, 'type.partition');
		const oldTargetPartition = get(oldTarget, 'type.partition');
		const newTargetPartition = get(targetMap, 'type.partition');

		const oldSourcePath = get(oldSource, 'type.path');
		const newSourcePath = get(sourceMap, 'type.path');
		const oldTargetPath = get(oldTarget, 'type.path');
		const newTargetPath = get(targetMap, 'type.path');

		const oldTargetFileName = get(oldTarget, 'type.fileName');
		const newTargetFileName = get(targetMap, 'type.fileName');

		const oldSourceStart = get(oldSource, 'type.start');
		const newSourceStart = get(sourceMap, 'type.start');
		const oldSourceEnd = get(oldSource, 'type.end');
		const newSourceEnd = get(sourceMap, 'type.end');

		const isWhereChange = oldSourceTable === newSourceTable && oldWhere !== newWhere;
		const isSourcePartitionChange =
			oldSourceTable === newSourceTable &&
			(oldSourcePartition !== undefined || newSourcePartition) &&
			oldSourcePartition !== newSourcePartition;
		const isTargetPartitionChange =
			oldTargetTable === newTargetTable &&
			(oldTargetPartition !== undefined || newTargetPartition !== undefined) &&
			oldTargetPartition !== newTargetPartition;
		const isSQLChange = oldSQL !== newSQL;
		const isPostSQLChange = oldPostSQL !== newPostSQL;
		const isSourceColumnChange =
			sourceMap &&
			!isEqual(oldSource.column, sourceMap.column) &&
			this.state.currentStep === 2;
		const isSourcePathChange = oldSourcePath !== newSourcePath;
		const isTargetPathChange =
			(oldTargetPath !== undefined || newTargetPath !== undefined) &&
			oldTargetPath !== newTargetPath;
		const isTargetFileNameChange =
			(oldTargetFileName !== undefined || newTargetFileName !== undefined) &&
			oldTargetFileName !== newTargetFileName;
		const isObjectsChange =
			!!newSourceObejct && !!oldSourceObject && !isEqual(newSourceObejct, oldSourceObject);
		const isObjectChange =
			!!oldTargetObject && !!newTargetObject && oldTargetObject !== newTargetObject;
		const isStartChange =
			!!oldSourceStart && !!newSourceStart && newSourceStart !== oldSourceStart;
		const isEndChange = !!oldSourceEnd && !!newSourceEnd && newSourceEnd !== oldSourceEnd;

		return (
			isWhereChange || // source type update
			isSourcePartitionChange || // source type update
			isTargetPartitionChange || // target type update
			isSQLChange || // target type update
			isPostSQLChange || // target type update
			isSourceColumnChange || // source columns update
			isSourcePathChange || // Path change
			isTargetPathChange || // target path
			isTargetFileNameChange || // 目标文件名
			isObjectChange ||
			isObjectsChange ||
			isStartChange ||
			isEndChange
		);
	};

	getJobData = (params: any) => {
		const { currentTabData } = this.props;
		const { dataSyncSaved } = currentTabData;
		const { taskId } = params;
		ajax.getOfflineJobData(params).then((res: any) => {
			const { data = {} } = res;
			if (res?.code === 1 && data) {
				const { taskId: nextTaskId } = data;
				if (nextTaskId !== taskId) return;
				if (!dataSyncSaved) {
					if (res.data) {
						const { sourceMap } = res.data;
						if (sourceMap.sourceList) {
							const loop = (source: any, index: any) => {
								return {
									...source,
									key: index == 0 ? 'main' : 'key' + ~~Math.random() * 10000000,
								};
							};
							sourceMap.sourceList = sourceMap.sourceList.map(loop);
						}
						this.props.initJobData(res.data);
					}
				} else {
					// tabs中有则把数据取出来
					this.props.getDataSyncSaved(dataSyncSaved);
					this.navtoStep(dataSyncSaved.currentStep.step);
				}
			} else {
				if (!dataSyncSaved) {
					this.props.initJobData(res.data);
				} else {
					this.props.getDataSyncSaved(dataSyncSaved);
					if (
						JSON.stringify(dataSyncSaved.targetMap) === '{}' ||
						!dataSyncSaved.targetMap
					) {
						this.navtoStep(0);
					} else {
						this.navtoStep(dataSyncSaved.currentStep.step);
					}
				}
			}
			if (!res.data) {
				this.props.setTabNew();
			} else {
				this.props.setTabSaved();
				if (!dataSyncSaved) {
					this.navtoStep(4);
				}
			}
			this.setState({ loading: false });
		});
	};

	// 组件离开保存数据到tabs中
	componentWillUnmount() {
		const { currentTabData, dataSync } = this.props;
		if (this.state.loading) {
			return;
		}
		// reset dataSync in redux when closing the tab
		this.props.initJobData({
			sourceMap: {},
			targetMap: {},
			keymap: {},
			setting: {},
		});
		this.props.saveDataSyncToTab({
			id: currentTabData?.id,
			data: dataSync,
		});
	}

	next() {
		this.setState({
			currentStep: this.state.currentStep + 1,
		});
	}

	prev() {
		this.setState({
			currentStep: this.state.currentStep - 1,
		});
	}

	navtoStep(step: any) {
		this.setState({ currentStep: step });
		// this.props.setCurrentStep(step);
	}

	save() {
		const { dataSync, currentTabData } = this.props;
		const params = getDataSyncSaveTabParams(currentTabData, dataSync);
		this.props.saveTab(params);
	}

	getPopupContainer = () => {
		return this._datasyncDom;
	};

	render() {
		const { currentStep, loading } = this.state;
		const {
			currentTabData = {},
			taskCustomParams,
			updateDataSyncVariable,
			sourceMap,
			targetMap,
		} = this.props;

		const { syncModel, notSynced } = currentTabData;

		const isLocked = false;
		const isIncrementMode = syncModel !== undefined && syncModel === DATA_SYNC_MODE.INCREMENT;

		const steps: any = [
			{
				title: '数据来源',
				content: (
					<DataSyncSource
						getPopupContainer={this.getPopupContainer}
						currentStep={currentStep}
						currentTabData={currentTabData}
						isIncrementMode={isIncrementMode}
						updateDataSyncVariables={updateDataSyncVariable}
						taskCustomParams={taskCustomParams}
						navtoStep={this.navtoStep.bind(this)}
					/>
				),
			},
			{
				title: '选择目标',
				content: (
					<DataSyncTarget
						getPopupContainer={this.getPopupContainer}
						currentStep={currentStep}
						currentTabData={currentTabData}
						isIncrementMode={isIncrementMode}
						navtoStep={this.navtoStep.bind(this)}
					/>
				),
			},
			{
				title: '字段映射',
				content: (
					<DataSyncKeymap
						currentStep={currentStep as any}
						currentTabData={currentTabData}
						sourceMap={sourceMap}
						targetMap={targetMap}
						navtoStep={this.navtoStep.bind(this)}
					/>
				),
			},
			{
				title: '通道控制',
				content: (
					<DataSyncChannel
						getPopupContainer={this.getPopupContainer}
						currentStep={currentStep}
						currentTabData={currentTabData}
						isIncrementMode={isIncrementMode}
						navtoStep={this.navtoStep.bind(this)}
					/>
				),
			},
			{
				title: '预览保存',
				content: (
					<DataSyncSave
						currentStep={currentStep}
						notSynced={notSynced}
						isIncrementMode={isIncrementMode}
						navtoStep={this.navtoStep.bind(this)}
						saveJob={this.save.bind(this)}
					/>
				),
			},
		];

		return loading ? null : (
			<div className="m-datasync">
				<Steps current={currentStep}>
					{steps.map((item: any) => (
						<Step key={item.title} title={item.title} />
					))}
				</Steps>
				<div
					ref={(ref: any) => {
						this._datasyncDom = ref;
					}}
					className="steps-content"
					style={{ position: 'relative' }}
				>
					{isLocked ? <div className="steps-mask"></div> : null}
					{steps[currentStep].content}
				</div>
			</div>
		);
	}
}

const mapState = (state: any) => {
	const { workbench, dataSync } = state.dataSync;
	return {
		user: state.user,
		project: state.project,
		tabs: workbench.tabs,
		dataSync: dataSync,
		isCurrentTabNew: workbench.isCurrentTabNew,
		sourceMap: dataSync?.sourceMap,
		targetMap: dataSync?.targetMap,
		currentTab: workbench.currentTab,
		taskCustomParams: workbench.taskCustomParams,
	};
};

const mapDispatch = (dispatch: any, ownProps: any) => {
	const wbActions = WBenchActions(dispatch);

	return {
		getDataSource: () => {
			API.queryByTenantId({ tenantId: getTenantId() }).then((res) => {
				let data: any = [];
				if (res.code === 1) {
					data = res.data;
				}
				dispatch({
					type: dataSourceListAction.LOAD_DATASOURCE,
					payload: data,
				});
			});
		},
		initJobData: (data: any) => {
			dispatch({
				type: dataSyncAction.INIT_JOBDATA,
				payload: data,
			});
		},
		setTabNew: () => {
			dispatch({
				type: workbenchAction.SET_CURRENT_TAB_NEW,
			});
		},
		setTabSaved: () => {
			dispatch({
				type: workbenchAction.SET_CURRENT_TAB_SAVED,
			});
		},
		saveDataSyncToTab: (params: any) => {
			dispatch({
				type: workbenchAction.SAVE_DATASYNC_TO_TAB,
				payload: {
					id: params.id,
					data: params.data,
				},
			});
		},
		getDataSyncSaved: (params: any) => {
			dispatch({
				type: dataSyncAction.GET_DATASYNC_SAVED,
				payload: params,
			});
		},
		setTabId: (id: any) => {
			dispatch({
				type: dataSyncAction.SET_TABID,
				payload: id,
			});
		},
		setCurrentStep: (step: any) => {
			dispatch({
				type: dataSyncAction.SET_CURRENT_STEP,
				payload: step,
			});
		},
		updateDataSyncVariables(sourceMap: any, targetMap: any, taskCustomParams: any) {
			wbActions.updateDataSyncVariables(sourceMap, targetMap, taskCustomParams);
		},
	};
};

export default connect(mapState, mapDispatch)(DataSync);
