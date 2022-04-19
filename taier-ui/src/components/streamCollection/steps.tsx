import * as React from 'react';
import { Steps } from 'antd';
import Source from './source';
import Target from './targetSource';
import Channel from './channel';
import Complete from './complete';
import { connect } from '@dtinsight/molecule/esm/react';
import molecule from '@dtinsight/molecule';
import { streamTaskActions } from './taskFunc';

const Step = Steps.Step;

class CollectionGuide extends React.Component<any, any> {
    // eslint-disable-next-line
    componentDidMount () {
        // this.props.getDataSource();
        if (this.props.currentPage) {
            streamTaskActions.initCollectionTask(this.props.currentPage.id);
        }
    }

    navtoStep = (step: any) => {
        streamTaskActions.navtoStep(step);
    }

    save () {
        this.props.saveTask();
    }

    render () {
        // const { currentPage } = this.props;
        const currentPage = this.props.current?.tab?.data;
        const { updateCurrentPage, updateSourceMap, updateTargetMap, updateChannelControlMap } = streamTaskActions;
        const collectionData = currentPage || {};
        const { currentStep } = collectionData;
        // const isLocked = currentPage.readWriteLockVO && !currentPage.readWriteLockVO.getLock;
        const isLocked = false;
        const steps: any = [
            {
                title: '选择来源',
                content: <Source
                    collectionData={collectionData}
                />
            },
            {
                title: '选择目标',
                content: <Target
                    collectionData={collectionData}
                />
            },
            {
                title: '通道控制',
                content: <Channel
                    collectionData={collectionData}
                />
            },
            {
                title: '预览保存',
                content: <Complete
                    collectionData={collectionData}
                    saveJob={this.save.bind(this)}
                />
            }
        ];
        return (currentStep || currentStep == 0) && (
                <div className="m-datasync">
                    <Steps current={currentStep}>
                        {steps.map((item: any) => <Step key={item.title} title={item.title} />)}
                    </Steps>
                    <div
                        style={{ pointerEvents: isLocked ? 'none' : 'unset' }}
                        className={currentStep === 3 ? 'steps-content step-content-complete' : 'steps-content'}
                    >
                        {steps[currentStep] && steps[currentStep].content}
                    </div>
                </div>
        )
    }
}


export default connect(molecule.editor, CollectionGuide);
