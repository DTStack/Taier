import React from 'react';
import { Steps, message } from 'antd';
import { connect } from 'react-redux';
import { isEqual } from 'lodash';

import DataSyncSource from './source';
import DataSyncTarget from './target';
import DataSyncKeymap from './keymap';
import DataSyncChannel from './channel';
import DataSyncSave from './save';

import ajax from '../../../../api';
import {
    dataSourceListAction,
    dataSyncAction,
    workbenchAction
} from '../../../../store/modules/offlineTask/actionType';
import {
    workbenchActions as WBenchActions
} from '../../../../store/modules/offlineTask/offlineAction';

import {
    DATA_SYNC_MODE
} from '../../../../comm/const';

import { isProjectCouldEdit } from '../../../../comm'

const Step = Steps.Step;

class DataSync extends React.Component {
    constructor (props) {
        super(props);
    }

    state = {
        currentStep: 0,
        loading: true
    }

    componentDidMount () {
        const { currentTabData } = this.props;

        this.getJobData({ taskId: currentTabData.id });
        this.props.setTabId(currentTabData.id);
        this.props.getDataSource();
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        // 如果列发生变化，则检测更新系统变量
        if (this.onVariablesChange(this.props, nextProps)) {
            this.props.updateDataSyncVariables(
                nextProps.sourceMap,
                nextProps.targetMap,
                nextProps.taskCustomParams
            )
        }
    }

    onVariablesChange = (oldProps, nextProps) => {
        const { sourceMap: oldSource, targetMap: oldTarget } = oldProps;
        const { sourceMap, targetMap } = nextProps;

        if (oldSource.sourceId !== sourceMap.sourceId || oldTarget.sourceId !== targetMap.sourceId) { return false };

        const oldSQL = oldTarget && oldTarget.type && oldTarget.type.preSql;
        const newSQL = targetMap && targetMap.type && targetMap.type.preSql;

        const oldPostSQL = oldTarget && oldTarget.type && oldTarget.type.postSql;
        const newPostSQL = targetMap && targetMap.type && targetMap.type.postSql;

        const isWhereChange = oldSource && oldSource.type && sourceMap.type && sourceMap.type.where !== undefined && oldSource.type.where !== undefined && oldSource.type.where !== sourceMap.type.where;
        const isSourceParitionChange = oldSource && oldSource.type && sourceMap.type && (oldSource.type.partition !== undefined || sourceMap.type.partition !== undefined) && oldSource.type.partition !== sourceMap.type.partition;
        const isTargetPartitionChange = oldTarget && oldTarget.type && targetMap.type && (oldTarget.type.partition !== undefined || targetMap.type.partition !== undefined) && oldTarget.type.partition !== targetMap.type.partition;
        const isSQLChange = oldSQL !== undefined && newSQL !== undefined && oldSQL !== newSQL;
        const isPostSQLChange = oldPostSQL !== undefined && newPostSQL !== undefined && oldPostSQL !== newPostSQL;
        const isSourceColumnChange = sourceMap && !isEqual(oldSource.column, sourceMap.column) && this.state.currentStep === 2;
        const isPathChange = oldSource && oldSource.type && sourceMap.type && sourceMap.type.path !== undefined && oldSource.type.path !== undefined && oldSource.type.path !== sourceMap.type.path;
        const isTargetFileNameChange = oldTarget && oldTarget.type && targetMap.type && (oldTarget.type.fileName !== undefined || targetMap.type.fileName !== undefined) && oldTarget.type.fileName !== targetMap.type.fileName;

        // Output test conditions
        console.log('dataSync', oldProps.dataSync, nextProps.dataSync)
        console.log('old', oldSource, oldTarget);
        console.log('new', sourceMap, targetMap);
        console.log('isWhereChange', isWhereChange);
        console.log('isSourceParitionChange', isSourceParitionChange);
        console.log('isTargetPartitionChange', isTargetPartitionChange);
        console.log('isSQLChange', isSQLChange);
        console.log('isSourceColumnChange', isSourceColumnChange);
        console.log('isPathChange', isPathChange);

        return isWhereChange || // source type update
        isSourceParitionChange || // source type update
        isTargetPartitionChange || // target type update
        isSQLChange || // target type update
        isPostSQLChange || // target type update
        isSourceColumnChange || // source columns update
        isPathChange || // Path change
        isTargetFileNameChange // 目标文件名
    }

    getJobData = (params) => {
        const { currentTabData } = this.props;
        const { dataSyncSaved } = currentTabData;

        ajax.getOfflineJobData(params).then(res => {
            if (!dataSyncSaved) {
                if (res.data) {
                    const { sourceMap } = res.data;
                    if (sourceMap.sourceList) {
                        const loop = (source, index) => {
                            return {
                                ...source,
                                key: index == 0 ? 'main' : ('key' + ~~Math.random() * 10000000)
                            }
                        }
                        sourceMap.sourceList = sourceMap.sourceList.map(loop)
                    }
                }
                this.props.initJobData(res.data);
            } else {
                // tabs中有则把数据取出来
                this.props.getDataSyncSaved(dataSyncSaved);
                this.setState({ currentStep: dataSyncSaved.currentStep.step });
            }

            if (!res.data) {
                this.props.setTabNew();
                this.navtoStep(0)
            } else {
                this.props.setTabSaved();
                if (!dataSyncSaved) {
                    this.navtoStep(4)
                }
            }

            this.setState({ loading: false });
        });
    }

    // 组件离开保存数据到tabs中
    componentWillUnmount () {
        const { currentTabData, dataSync } = this.props;
        if (this.state.loading) {
            return;
        }
        this.props.saveDataSyncToTab({
            id: currentTabData.id,
            data: dataSync
        })
    }

    next () {
        this.setState({
            currentStep: this.state.currentStep + 1
        })
    }

    prev () {
        this.setState({
            currentStep: this.state.currentStep - 1
        })
    }

    navtoStep (step) {
        this.setState({ currentStep: step });
        this.props.setCurrentStep(step);
    }

    save () {
        this.props.saveTab();
    }

    getPopupContainer = () => {
        return this._datasyncDom;
    }

    render () {
        const { currentStep, loading } = this.state;
        const {
            user, project,
            sourceMap, targetMap,
            currentTabData,
            taskCustomParams,
            updateDataSyncVariables
        } = this.props;

        const { readWriteLockVO, notSynced, syncModel } = currentTabData;

        const isLocked = readWriteLockVO && !readWriteLockVO.getLock;
        const couldEdit = isProjectCouldEdit(project, user);
        const isIncrementMode = syncModel !== undefined && syncModel === DATA_SYNC_MODE.INCREMENT;

        const steps = [
            {
                title: '数据来源',
                content: <DataSyncSource
                    getPopupContainer={this.getPopupContainer}
                    currentStep={currentStep}
                    currentTabData={currentTabData}
                    isIncrementMode={isIncrementMode}
                    updateDataSyncVariables={updateDataSyncVariables}
                    taskCustomParams={taskCustomParams}
                    navtoStep={this.navtoStep.bind(this)}
                />
            },
            {
                title: '选择目标',
                content: <DataSyncTarget
                    getPopupContainer={this.getPopupContainer}
                    currentStep={currentStep}
                    currentTabData={currentTabData}
                    isIncrementMode={isIncrementMode}
                    navtoStep={this.navtoStep.bind(this)}
                />
            },
            {
                title: '字段映射',
                content: <DataSyncKeymap
                    currentStep={currentStep}
                    sourceMap={sourceMap}
                    targetMap={targetMap}
                    navtoStep={this.navtoStep.bind(this)}
                />
            },
            {
                title: '通道控制',
                content: <DataSyncChannel
                    getPopupContainer={this.getPopupContainer}
                    currentStep={currentStep}
                    isIncrementMode={isIncrementMode}
                    navtoStep={this.navtoStep.bind(this)}
                />
            },
            {
                title: '预览保存',
                content: <DataSyncSave
                    currentStep={currentStep}
                    notSynced={notSynced}
                    isIncrementMode={isIncrementMode}
                    navtoStep={this.navtoStep.bind(this)}
                    saveJob={this.save.bind(this)}
                />
            }
        ];

        return loading ? null : <div className="m-datasync">
            <Steps current={currentStep}>
                {steps.map(item => <Step key={item.title} title={item.title} />)}
            </Steps>
            <div ref={(ref) => { this._datasyncDom = ref; }} className="steps-content" style={{ position: 'relative' }}>
                {isLocked || !couldEdit ? <div className="steps-mask"></div> : null}
                {steps[currentStep].content}
            </div>
        </div>
    }
}

const mapState = (state) => {
    const { workbench, dataSync } = state.offlineTask;
    return {
        user: state.user,
        project: state.project,
        tabs: workbench.tabs,
        dataSync: dataSync,
        isCurrentTabNew: workbench.isCurrentTabNew,
        sourceMap: dataSync.sourceMap,
        targetMap: dataSync.targetMap,
        currentTab: workbench.currentTab,
        taskCustomParams: workbench.taskCustomParams
    }
};

const mapDispatch = (dispatch, ownProps) => {
    const wbActions = new WBenchActions(dispatch, ownProps);

    return {
        getDataSource: () => {
            ajax.getOfflineDataSource()
                .then(res => {
                    let data = []
                    if (res.code === 1) {
                        data = res.data
                    }
                    dispatch({
                        type: dataSourceListAction.LOAD_DATASOURCE,
                        payload: data
                    });
                });
        },
        initJobData: (data) => {
            dispatch({
                type: dataSyncAction.INIT_JOBDATA,
                payload: data
            });
        },
        setTabNew: () => {
            dispatch({
                type: workbenchAction.SET_CURRENT_TAB_NEW
            });
        },
        setTabSaved: () => {
            dispatch({
                type: workbenchAction.SET_CURRENT_TAB_SAVED
            });
        },
        saveDataSyncToTab: (params) => {
            dispatch({
                type: workbenchAction.SAVE_DATASYNC_TO_TAB,
                payload: {
                    id: params.id,
                    data: params.data
                }
            });
        },
        getDataSyncSaved: (params) => {
            dispatch({
                type: dataSyncAction.GET_DATASYNC_SAVED,
                payload: params
            });
        },
        setTabId: (id) => {
            dispatch({
                type: dataSyncAction.SET_TABID,
                payload: id
            });
        },
        setCurrentStep: (step) => {
            dispatch({
                type: dataSyncAction.SET_CURRENT_STEP,
                payload: step
            });
        },
        saveJobData (params) {
            ajax.saveOfflineJobData(params)
                .then(res => {
                    if (res.code === 1) {
                        message.success('保存成功！');
                        dispatch({
                            type: workbenchAction.SET_CURRENT_TAB_SAVED
                        });
                        dispatch({
                            type: workbenchAction.MAKE_TAB_CLEAN
                        })
                    }
                })
        },
        updateDataSyncVariables (sourceMap, targetMap, taskCustomParams) {
            wbActions.updateDataSyncVariables(sourceMap, targetMap, taskCustomParams);
        }
    }
}

export default connect(mapState, mapDispatch)(DataSync);
