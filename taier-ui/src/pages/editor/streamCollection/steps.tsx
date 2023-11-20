import * as React from 'react';
import molecule from '@dtinsight/molecule';
import { connect } from '@dtinsight/molecule/esm/react';
import { Steps } from 'antd';
import { cloneDeep } from 'lodash';

import API from '@/api';
import Channel from './channel';
import Complete from './complete';
import Source from './source';
import Target from './targetSource';
import { streamTaskActions } from './taskFunc';

const Step = Steps.Step;

class CollectionGuide extends React.Component<any, any> {
    state = {
        sourceList: [],
    };

    componentDidMount() {
        // this.props.getDataSource();
        if (this.props.currentPage) {
            streamTaskActions.initCollectionTask();
        }
        this.loadSourceList();
    }

    navtoStep = (step: any) => {
        streamTaskActions.navtoStep(step);
    };

    save() {
        this.props.saveTask();
    }

    loadSourceList = () => {
        API.getAllDataSource({}).then((res) => {
            if (res.code === 1) {
                this.setState({ sourceList: res.data || [] });
            }
        });
    };

    render() {
        const currentPage = this.props.current?.tab?.data;
        const collectionData = cloneDeep(currentPage || {});
        const { currentStep } = collectionData;
        const isLocked = false;

        const steps: any = [
            {
                title: '选择来源',
                content: <Source collectionData={collectionData} sourceList={this.state.sourceList} />,
            },
            {
                title: '选择目标',
                content: <Target collectionData={collectionData} sourceList={this.state.sourceList} />,
            },
            {
                title: '通道控制',
                content: <Channel collectionData={collectionData} sourceList={this.state.sourceList} />,
            },
            {
                title: '预览保存',
                content: (
                    <Complete
                        collectionData={collectionData}
                        sourceList={this.state.sourceList}
                        saveJob={this.save.bind(this)}
                    />
                ),
            },
        ];
        return (
            (currentStep || currentStep == 0) && (
                <div className="dt-datasync">
                    <Steps size="small" current={currentStep}>
                        {steps.map((item: any) => (
                            <Step key={item.title} title={item.title} />
                        ))}
                    </Steps>
                    <div
                        style={{ pointerEvents: isLocked ? 'none' : 'unset' }}
                        className={`steps-content step-content-complete dt-datasync-content ${
                            currentStep === 3 && 'step-content-complete'
                        }`}
                    >
                        {steps[currentStep] && steps[currentStep].content}
                    </div>
                </div>
            )
        );
    }
}

export default connect(molecule.editor, CollectionGuide);
