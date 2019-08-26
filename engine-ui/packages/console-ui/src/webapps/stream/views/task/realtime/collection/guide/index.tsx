import * as React from 'react';
import { Steps } from 'antd';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import { actions as collectionActions } from '../../../../../store/modules/realtimeTask/collection';
import { updateCurrentPage } from '../../../../../store/modules/realtimeTask/browser';
import SplitPane from 'react-split-pane';
import ToolBar from 'main/components/ide/toolbar';
import Source from './collectionSource';
import Target from './collectionTarget';
import ChannelControl from './channelControl';
import Complete from './complete';
import ConvertToScript from '../../convertToScript';
const Step = Steps.Step;

class CollectionGuide extends React.Component<any, any> {
    // eslint-disable-next-line
	componentDidMount () {
        this.props.getDataSource();
        if (this.props.currentPage) {
            this.props.initCollectionTask(this.props.currentPage.id);
        }
    }
    navtoStep (step: any) {
        this.props.navtoStep(step);
    }

    save () {
        this.props.saveTask();
    }

    render () {
        const { currentPage, updateCurrentPage } = this.props;
        const collectionData = currentPage || {};
        const { currentStep } = collectionData;
        const isLocked = currentPage.readWriteLockVO && !currentPage.readWriteLockVO.getLock;
        const steps: any = [
            {
                title: '选择来源',
                content: <Source
                    updateSourceMap={this.props.updateSourceMap}
                    navtoStep={this.navtoStep.bind(this)}
                    collectionData={collectionData}
                    updateCurrentPage={updateCurrentPage}
                />
            },
            {
                title: '选择目标',
                content: <Target
                    updateTargetMap={this.props.updateTargetMap}
                    navtoStep={this.navtoStep.bind(this)}
                    collectionData={collectionData}
                    updateCurrentPage={updateCurrentPage}
                />
            },
            {
                title: '通道控制',
                content: <ChannelControl
                    updateChannelControlMap={this.props.updateChannelControlMap}
                    navtoStep={this.navtoStep.bind(this)}
                    collectionData={collectionData}
                    updateCurrentPage={updateCurrentPage}
                />
            },
            {
                title: '预览保存',
                content: <Complete
                    navtoStep={this.navtoStep.bind(this)}
                    collectionData={collectionData}
                    updateTargetMap={this.props.updateTargetMap}
                    saveJob={this.save.bind(this)}
                    currentPage={currentPage}
                />
            }
        ];
        const toolbar: any = {
            enable: true,
            enableRun: false,
            disableEdit: true,
            leftCustomButton: <ConvertToScript isLocked={isLocked} />
        }
        return (
            <div className="ide-editor">
                <div className="ide-header bd-bottom">
                    <ToolBar
                        {...toolbar}
                    />
                </div>
                <div style={{ zIndex: 901 }} className="ide-content">
                    <SplitPane
                        split="horizontal"
                        minSize={100}
                        maxSize={-77}
                        defaultSize='100%'
                        primary="first"
                        key={`ide-split-pane`}
                        size='100%'
                    >
                        <div style={{
                            width: '100%',
                            height: '100%',
                            minHeight: '400px',
                            position: 'relative'
                        }}>
                            {
                                (currentStep || currentStep == 0) ? <div className="m-datasync">
                                    <Steps current={currentStep}>
                                        {steps.map((item: any) => <Step key={item.title} title={item.title} />)}
                                    </Steps>
                                    <div className="steps-content" style={{ position: 'relative' }}>
                                        {isLocked ? <div className="steps-mask"></div> : null}
                                        {steps[currentStep] && steps[currentStep].content}
                                    </div>
                                </div> : null
                            }
                        </div>
                    </SplitPane>
                </div>
            </div>
        )
    }
}

const mapState = (state: any) => {
    const currentPage = state.realtimeTask.currentPage
    return {
        pages: state.realtimeTask.pages,
        currentPage: currentPage,
        user: state.user,
        project: state.project
    }
};

const mapDispatch = (dispatch: any) => {
    const actions = bindActionCreators(collectionActions, dispatch);
    return Object.assign(actions, {
        updateCurrentPage: function (data: any) {
            dispatch(updateCurrentPage(data))
        }
    });
}

export default connect(mapState, mapDispatch)(CollectionGuide);
