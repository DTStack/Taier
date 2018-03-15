import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Steps, Button, Icon } from 'antd';

import StepOne from './stepOne';
import StepTwo from './stepTwo';
import StepThree from './stepThree';
import StepFour from './stepFour';

import DCApi from '../../../api/dataCheck';

const Step = Steps.Step;

export default class DataCheckEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            current: 0,
            editParams: {
                origin: {},
                target: {},
                setting: {},
                mappedPK: null,
                executeType: 0,
                scheduleConf: '',
                notifyVO: null,
            },
            editStatus: 'new'
        }
    }

    componentWillMount() {
        const { verifyId } = this.props.routeParams;
        const { editParams } = this.state;

        if (verifyId) {
            this.setState({ editStatus: 'edit' });
            DCApi.getCheckDetail({ verifyId: verifyId }).then((res) => {
                if (res.code === 1) {
                    this.setState({ 
                        editParams: { ...editParams, 
                            id: res.data.id,
                            origin: res.data.origin,
                            target: res.data.target,
                            setting: res.data.setting,
                            scheduleConf: res.data.scheduleConf,
                            executeType: res.data.executeType,
                            mappedPK: res.data.mappedPK,
                            notifyVO: res.data.notifyVO
                        }
                    });
                }
            });
        }
    }

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
                title: '选择左侧表', content: <StepOne
                    currentStep={current}
                    navToStep={this.navToStep}
                    // {...this.props} 
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            },
            {
                title: '选择右侧表', content: <StepTwo
                    currentStep={current}
                    navToStep={this.navToStep}
                    // {...this.props} 
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            },
            {
                title: '选择字段', content: <StepThree
                    currentStep={current}
                    navToStep={this.navToStep}
                    // {...this.props} 
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            },
            {
                title: '执行配置', content: <StepFour
                    currentStep={current}
                    navToStep={this.navToStep}
                    // {...this.props} 
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            }
        ];
        return (
            <div className="inner-container check-setting">
                <h3>
                    <Link to="/dq/dataCheck">
                        <Icon type="left-circle-o m-r-8" />新建逐行校验
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

