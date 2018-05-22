import React, { Component } from 'react';
import { Steps } from 'antd';

import StepOne from './stepOne';
import StepTwo from './stepTwo';
import StepThree from './stepThree';
import GoBack from 'main/components/go-back';

const Step = Steps.Step;

export default class RuleConfigEdit extends Component {

    state = {
        current: 0,
        editParams: {
            dataSourceId: undefined,
            tableName: undefined,
            partition: undefined,
            isSubscribe: 1,
            scheduleConf: '',
            sendTypes: [],
            notifyUser: [],
            rules: []
        },
        havePart: false,
        useInput: true
    }

    changeParams = (obj) => {
        let editParams = { ...this.state.editParams, ...obj };
        this.setState({ editParams });
    }

    changeHavePart = (havePart) => {
        this.setState({ havePart });
    }

    changeUseInput = (useInput) => {
        this.setState({ useInput });
    }

    navToStep = (value) => {
        this.setState({ current: value });
    }
 
    render() {
        const { current, editParams, havePart, useInput } = this.state;
        const steps = [
            {
                title: '监控对象', content: <StepOne
                    currentStep={current}
                    navToStep={this.navToStep}
                    havePart={havePart}
                    useInput={useInput}
                    editParams={editParams}
                    changeParams={this.changeParams}
                    changeHavePart={this.changeHavePart}
                    changeUseInput={this.changeUseInput}
                />
            },
            {
                title: '监控规则', content: <StepTwo
                    currentStep={current}
                    navToStep={this.navToStep}
                    editParams={editParams}
                    changeParams={this.changeParams}
                />
            },
            {
                title: '监控执行', content: <StepThree
                    currentStep={current}
                    navToStep={this.navToStep}
                    editParams={editParams}
                    changeParams={this.changeParams}
                />
            }
        ];
        
        return (
            <div className="box-1 rule-edit">
                <h1 className="box-title">
                    <GoBack /> 
                    <span className="m-l-8">
                        新建质量监控
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

