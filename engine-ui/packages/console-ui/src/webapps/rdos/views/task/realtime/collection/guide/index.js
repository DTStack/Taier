import React from "react";
import { Steps, message } from "antd";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";

import ajax from "../../../../../api/index"
import { actions as collectionActions, dataKey as collectionKey } from '../../../../../store/modules/realtimeTask/collection';

import Source from "./collectionSource";
import Target from "./collectionTarget";
import Complete from "./complete";

const Step = Steps.Step;

class CollectionGuide extends React.Component {

    componentWillMount() {
        this.props.getDataSource();
        this.props.initCollectionTask(this.props.currentPage.id);
    }

    navtoStep(step) {
        this.setState({ currentStep: step });
        this.props.setCurrentStep(step);
    }

    save() {
        console.log(save)
    }

    render() {
        const { currentPage } = this.props;
        const collectionData=currentPage[collectionKey]||{};
        const { currentStep } = collectionData;
        const isLocked = false;
        const steps = [
            {
                title: '选择来源', content: <Source
                    currentStep={currentStep}
                    updateSourceMap={this.props.updateSourceMap}
                    navtoStep={this.navtoStep.bind(this)}
                    collectionData={collectionData}
                />
            },
            {
                title: '选择目标', content: <Target
                    currentStep={currentStep}
                    navtoStep={this.navtoStep.bind(this)}
                    collectionData={collectionData}
                />
            },
            {
                title: '预览保存', content: <Complete
                    currentStep={currentStep}
                    navtoStep={this.navtoStep.bind(this)}
                    collectionData={collectionData}
                    saveJob={this.save.bind(this)}
                />
            },
        ];
        return (
            (currentStep||currentStep==0)?<div className="m-datasync">
                <Steps current={currentStep}>
                    {steps.map(item => <Step key={item.title} title={item.title} />)}
                </Steps>
                <div className="steps-content" style={{ position: "relative" }}>
                    {isLocked ? <div className="steps-mask"></div> : null}
                    {steps[currentStep].content}
                </div>
            </div>:<p>loading</p>
        )
    }
}

const mapState = (state) => {
    const currentPage = state.realtimeTask.currentPage
    return {
        pages: state.realtimeTask.pages,
        currentPage: currentPage,
        user: state.user,
        project: state.project
    }
};

const mapDispatch = dispatch => {
    const actions = bindActionCreators(collectionActions, dispatch);
    return actions;
}

export default connect(mapState, mapDispatch)(CollectionGuide);