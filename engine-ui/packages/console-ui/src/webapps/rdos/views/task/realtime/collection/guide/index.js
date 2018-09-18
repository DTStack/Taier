import React from "react";
import { Steps, message } from "antd";
import {connect} from "react-redux";

import ajax from "../../../../../api/index"
import {
    dataSourceListAction,
    collectionAction
} from '../../../../../store/modules/realtimeTask/actionTypes';

import Source from "./collectionSource";
import Target from "./collectionTarget";
import Complete from "./complete";

const Step = Steps.Step;

class CollectionGuide extends React.Component {
    state = {
        currentStep: 0
    }

    componentDidMount() {
        this.props.getDataSource();
    }

    navtoStep(step) {
        this.setState({ currentStep: step });
        this.props.setCurrentStep(step);
    }

    save() {
        console.log(save)
    }

    render() {
        const { currentStep } = this.state;
        const isLocked = false;
        const steps = [
            {
                title: '选择来源', content: <Source
                    currentStep={currentStep}
                    navtoStep={this.navtoStep.bind(this)}
                />
            },
            {
                title: '选择目标', content: <Target
                    currentStep={currentStep}
                    navtoStep={this.navtoStep.bind(this)}
                />
            },
            {
                title: '预览保存', content: <Complete
                    currentStep={currentStep}
                    navtoStep={this.navtoStep.bind(this)}
                    saveJob={this.save.bind(this)}
                />
            },
        ];
        return (
            <div className="m-datasync">
                <Steps current={currentStep}>
                    {steps.map(item => <Step key={item.title} title={item.title} />)}
                </Steps>
                <div className="steps-content" style={{ position: "relative" }}>
                    {isLocked ? <div className="steps-mask"></div> : null}
                    {steps[currentStep].content}
                </div>
            </div>
        )
    }
}

const mapState = (state) => {
    const currentTab = state.offlineTask.workbench.currentTab
    return {
        dataSync: state.offlineTask.dataSync,
        tabs: state.offlineTask.workbench.tabs,
        currentTab: currentTab,
        user:state.user,
        project:state.project
    }
};

const mapDispatch = dispatch => {
    return {
        getDataSource: () => {
            ajax.getStreamDataSourceList()
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
                type: collectionAction.INIT_JOBDATA,
                payload: data
            });
        },
        getDataSyncSaved: (params) => {
            dispatch({
                type: collectionAction.GET_DATASYNC_SAVED,
                payload: params
            });
        },
        setTabId: (id) => {
            dispatch({
                type: collectionAction.SET_TABID,
                payload: id
            });
        },
        setCurrentStep: (step) => {
            dispatch({
                type: collectionAction.SET_CURRENT_STEP,
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

export default connect(mapState, mapDispatch)(CollectionGuide);