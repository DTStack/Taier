import React, { Component } from 'react';
import { Steps, message } from 'antd';

import StepOne from './stepOne'
import StepTwo from './stepTwo'
import StepThree from './stepThree'
import StepFour from './stepFour'
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as tableActions from '../../../../actions/workbenchActions/table';

const Step = Steps.Step;

@connect(
    state => {
        const { workbench } = state;
        return {
            workbench
        };
    },
    dispatch => {
        const actions = bindActionCreators(tableActions, dispatch);
        return actions;
    }
)
class CreateTable extends Component {
    constructor () {
        super();
    }

    componentWillReceiveProps (nextProps) {
        console.log('PPP')
        console.log(nextProps)
    }
    render () {
        const { currentStep } = this.props.data;
        const { folderTree } = this.props.workbench
        console.log(this.props)

        console.log(this.props.data)

        const steps = [
            {
                title: '基本信息',
                content: <StepOne databaseList={folderTree.children || []} tabData={this.props.data} handleLastStep={this.props.handleLastStep} handleNextStep={this.props.handleNextStep} saveNewTableData={this.props.saveNewTableData} handleCancel={this.props.handleCancel} toTableDetail={this.props.toTableDetail}/>
            }, {
                title: '字段与分区',
                content: <StepTwo tabData={this.props.data} handleSave={this.props.handleSave} handleLastStep={this.props.handleLastStep} handleNextStep={this.props.handleNextStep} saveNewTableData={this.props.saveNewTableData}/>
            }, {
                title: '新建完成',
                content: <StepFour tabData={this.props.data} toTableDetail={this.props.toTableDetail}/>
            }
        ]
        return (
            <div className="create-table-container pane-wrapper">
                <Steps current={currentStep}>
                    {
                        steps.map(o => (
                            <Step key={o.title} title={o.title}/>
                        ))
                    }
                </Steps>
                <div className="form-box" id="form-box">
                    {steps[currentStep].content}
                </div>
            </div>
        )
    }
}

export default CreateTable
