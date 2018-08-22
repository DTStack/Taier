import React from 'react';
import { Steps, message } from 'antd';
import { connect } from 'react-redux';
import { cloneDeep } from 'lodash'

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

const Step = Steps.Step;

class DataSync extends React.Component{

    constructor(props) {
        super(props);
    }

    state = {
        currentStep: 0,
        loading:true,
    }

    componentDidMount() {
        const { id } = this.props;

        this.getJobData({ taskId: id });
        this.props.setTabId(id);
        this.props.getDataSource();
    }

    getJobData = (params) => {
        const { dataSyncSaved } = this.props;

        ajax.getOfflineJobData(params).then(res => {
            if (!dataSyncSaved) {
                if(res.data){
                    const {sourceMap} = res.data
                    sourceMap.sourceList?(sourceMap.sourceList=sourceMap.sourceList.map(
                        (source,index)=>{
                            return {
                                ...source,
                                key:index==0?"main":("key"+~~Math.random()*10000000)
                            }
                        }
                    )):null;
                }
                this.props.initJobData(res.data);
            } else {
                // tabs中有则把数据取出来
                this.props.getDataSyncSaved(dataSyncSaved);
                this.setState({ currentStep: dataSyncSaved.currentStep.step });
            }

            if (!res.data) {
                this.props.setTabNew();
            } else {
                this.props.setTabSaved();
                if(!dataSyncSaved){
                    this.navtoStep(4)
                }
            }

            this.setState({ loading: false });
        });
    }

    // 组件离开保存数据到tabs中
    componentWillUnmount() {
        const { id, dataSync } = this.props;
        if(this.state.loading){
            return ;
        }
        this.props.saveDataSyncToTab({
            id: id,
            data: dataSync
        })
    }

    next() {
        this.setState({
            currentStep: this.state.currentStep + 1
        })
    }

    prev() {
        this.setState({
            currentStep: this.state.currentStep - 1
        })
    }

    navtoStep(step) {
        this.setState({ currentStep: step });
        this.props.setCurrentStep(step);
    }

    save() {
        const { saveJobData, dataSync } = this.props;
        const formatted = this.generateRqtBody(dataSync);
        saveJobData(formatted);
    }

    /**
     * @description 拼装接口所需数据格式
     * @param {any} data 数据同步job配置对象
     * @returns {any} result 接口所需数据结构
     * @memberof DataSync
     */
    generateRqtBody(data) {
        // 深刻龙避免直接mutate store
        let clone = cloneDeep(data);

        const { tabs, currentTab } = this.props;
        const { keymap, sourceMap, targetMap } = clone;
        const { source, target } = keymap;
        const { name, id } = this.props;

        // 接口要求keymap中的连线映射数组放到sourceMap中
        clone.sourceMap.column = source;
        clone.targetMap.column = target;
        clone.settingMap = clone.setting;
        clone.name = name;
        clone.taskId = id;

        // type中的特定配置项也放到sourceMap中
        const targetTypeObj = targetMap.type;
        const sourceTypeObj = sourceMap.type;

        for(let key in sourceTypeObj) {
            if (sourceTypeObj.hasOwnProperty(key)) {
                sourceMap[key] = sourceTypeObj[key]
            }
        }
        for(let k2 in targetTypeObj) {
            if(targetTypeObj.hasOwnProperty(k2)) {
                targetMap[k2] = targetTypeObj[k2]
            }
        }

        // 删除接口不必要的字段
        delete clone.keymap;
        delete clone.setting;
        delete clone.dataSourceList;
        delete clone.currentStep;

        // 获取当前task对象并深克隆guest
        let result = cloneDeep(tabs.filter(tab => tab.id === currentTab)[0]);

        // 将以上步骤生成的数据同步配置拼装到task对象中
        for(let key in clone) {
            if(clone.hasOwnProperty(key)) {
                result[key] = clone[key];
            }
        }

        // 修改task配置时接口要求的标记位
        result.preSave = true;

        // 接口要求上游任务字段名修改为dependencyTasks
        if(result.taskVOS) {
            result.dependencyTasks = result.taskVOS.map(o => o);
            result.taskVOS = null;
        }

        // 数据拼装结果
        return result;
    }

    render() {
        const { currentStep, loading } = this.state;
        const { readWriteLockVO, notSynced } =this.props;
        const isLocked = readWriteLockVO && !readWriteLockVO.getLock;

        const steps = [
            {title: '数据来源', content: <DataSyncSource
                    currentStep={currentStep}
                    navtoStep={ this.navtoStep.bind(this) }
                />
            },
            {title: '选择目标', content: <DataSyncTarget
                    currentStep={currentStep}
                    navtoStep={ this.navtoStep.bind(this) }
                />
            },
            {title: '字段映射', content: <DataSyncKeymap
                    currentStep={currentStep}
                    navtoStep={ this.navtoStep.bind(this) }
                />
            },
            {title: '通道控制', content: <DataSyncChannel
                    currentStep={currentStep}
                    navtoStep={ this.navtoStep.bind(this) }
                />
            },
            {title: '预览保存', content: <DataSyncSave
                    currentStep={currentStep}
                    notSynced={notSynced}
                    navtoStep={ this.navtoStep.bind(this) }
                    saveJob={ this.save.bind(this) }
                />
            },
        ];

        return loading?null:<div className="m-datasync">
            <Steps current={currentStep}>
                { steps.map(item => <Step key={item.title} title={item.title} />) }
            </Steps>
            <div className="steps-content" style={{position:"relative"}}>
                {isLocked?<div className="steps-mask"></div>:null}
                { steps[currentStep].content }
            </div>
      </div>
    }
}

const mapState = (state) => {
    const currentTab = state.offlineTask.workbench.currentTab
    return {
        dataSync: state.offlineTask.dataSync,
        tabs: state.offlineTask.workbench.tabs,
        currentTab: currentTab
    }
};

const mapDispatch = dispatch => {
    return {
        getDataSource: () => {
            ajax.getOfflineDataSource()
                .then(res => {
                    let data = []
                    if(res.code === 1) {
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
        saveJobData(params) {
            ajax.saveOfflineJobData(params)
                .then(res => {
                    if(res.code === 1) {
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
    }
}

export default connect(mapState, mapDispatch)(DataSync);