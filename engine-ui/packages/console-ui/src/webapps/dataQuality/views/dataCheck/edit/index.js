import React, { Component } from 'react';
import { Steps } from 'antd';

import StepOne from './stepOne';
import StepTwo from './stepTwo';
import StepThree from './stepThree';
import StepFour from './stepFour';
import GoBack from 'main/components/go-back';
import DCApi from '../../../api/dataCheck';

const Step = Steps.Step;

export default class DataCheckEdit extends Component {
    state = {
        current: 0,
        editParams: {
            origin: {},
            target: {},
            mappedPK: {},
            executeType: 0,
            setting: {},
            scheduleConf: undefined,
            notifyVO: null
        },
        editStatus: 'new',
        havePart: false
    }

    componentWillMount () {
        const { verifyId } = this.props.routeParams;
        const { editParams } = this.state;

        if (verifyId) {
            this.setState({ editStatus: 'edit' });

            DCApi.getCheckDetail({ verifyId }).then((res) => {
                if (res.code === 1) {
                    let data = res.data;

                    this.setState({
                        editParams: {
                            ...editParams,
                            id: data.id,
                            origin: data.origin,
                            target: data.target,
                            setting: data.setting,
                            mappedPK: data.mappedPK,
                            notifyVO: data.notifyVO,
                            executeType: data.executeType,
                            scheduleConf: data.scheduleConf
                        }
                    });
                }
            });
        }
    }

    changeParams = (obj) => {
        let editParams = { ...this.state.editParams, ...obj };
        console.log(obj, editParams)
        this.setState({ editParams });
    }

    changeHavePart = (havePart) => {
        this.setState({ havePart });
    }

    navToStep = (current) => {
        this.setState({ current });
    }

    render () {
        const { current, editParams, editStatus, havePart } = this.state;
        const steps = [
            {
                title: '选择左侧表',
                content: <StepOne
                    currentStep={current}
                    navToStep={this.navToStep}
                    havePart={havePart}
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                    changeHavePart={this.changeHavePart}
                />
            },
            {
                title: '选择右侧表',
                content: <StepTwo
                    currentStep={current}
                    navToStep={this.navToStep}
                    havePart={havePart}
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            },
            {
                title: '选择字段',
                content: <StepThree
                    currentStep={current}
                    navToStep={this.navToStep}
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            },
            {
                title: '执行配置',
                content: <StepFour
                    currentStep={current}
                    navToStep={this.navToStep}
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            }
        ];

        return (
            <div className="box-1 check-setting">
                <h1 className="box-title">
                    <GoBack />
                    <span className="m-l-8">
                        { editStatus === 'new' ? '新建逐行校验' : '编辑逐行校验' }
                    </span>
                </h1>

                <div className="steps-container">
                    <Steps current={current}>
                        { steps.map(item => <Step key={item.title} title={item.title} />) }
                    </Steps>
                    { steps[current].content }
                </div>
            </div>
        )
    }
}
