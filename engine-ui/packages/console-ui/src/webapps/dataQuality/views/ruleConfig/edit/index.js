import React, { Component } from 'react';
import { Link } from 'react-router';
import { Steps, Button, Icon } from 'antd';
import StepOne from './stepOne';
import StepTwo from './stepTwo';
import StepThree from './stepThree';

const Step = Steps.Step;

export default class RuleConfigEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            current: 0,
            editParams: {
                dataSourceId: undefined,
                tableName: undefined,
                partitionColumn: undefined,
                partitionValue: undefined,
                isSubscribe: 0,
                scheduleConf: '',
                sendTypes: [],
                notifyUser: [],
                rules: []
            },
            editStatus: 'new'
        }
    }

    componentWillMount() {}

    componentDidMount() {}

    changeParams = (obj) => {
        let editParams = { ...this.state.editParams, ...obj };
        this.setState({ editParams });
        console.log(this,obj,'editParams')
    }

    navToStep = (value) => {
        this.setState({ current: value });
    }
 
    render() {
        const { current, editParams, editStatus } = this.state;
        const steps = [
            {
                title: '监控对象', content: <StepOne
                    currentStep={current}
                    navToStep={this.navToStep}
                    {...this.props} 
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            },
            {
                title: '监控规则', content: <StepTwo
                    currentStep={current}
                    navToStep={this.navToStep}
                    {...this.props} 
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            },
            {
                title: '监控执行', content: <StepThree
                    currentStep={current}
                    navToStep={this.navToStep}
                    {...this.props} 
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            }
        ];
        return (
            <div className="inner-container rule-edit">
                <h3>
                    <Link to="/dq/rule">
                        <Icon type="left-circle-o m-r-8" />新建质量监控
                    </Link>
                </h3>
                
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

