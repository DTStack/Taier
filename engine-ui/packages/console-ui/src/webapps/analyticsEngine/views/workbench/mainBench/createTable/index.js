import React, { Component } from 'react';
import { Steps, message } from 'antd';

import StepOne from './stepOne'
import StepTwo from './StepTwo'
import StepThree from './StepThree'
import StepFour from './StepFour'
import { connect } from "react-redux";
import { bindActionCreators } from 'redux';
import * as tableActions from '../../../../actions/workbenchActions/table';


const Step = Steps.Step;

@connect(
    state => {
        const { workbench } = state;
        return {
            workbench,
        };
    },
    dispatch => {
        const actions = bindActionCreators(tableActions, dispatch);
        return actions;
    }
)
class CreateTable extends Component {

    constructor(){
        super();
    }

    componentWillReceiveProps(nextProps){
        console.log('PPP')
        console.log(nextProps)
    }
    render () {
        const { currentStep, currentTab, newanalyEngineTableDataList} = this.props.workbench.mainBench;
        const newanalyEngineTableData = newanalyEngineTableDataList[`tableItem${currentTab}`] || {};

        const steps = [
            {
                title: '基本信息',
                content: <StepOne formData={newanalyEngineTableData || {}} handleLastStep={this.props.handleLastStep} handleNextStep={this.props.handleNextStep} saveNewTableData={this.props.saveNewTableData}/>
            },{
                title: '字段与分区',
                content: <StepTwo formData={newanalyEngineTableData || {}} handleLastStep={this.props.handleLastStep} handleNextStep={this.props.handleNextStep} saveNewTableData={this.props.saveNewTableData}/>
            },{
                title: '索引',
                content: <StepThree formData={newanalyEngineTableData || {}} handleSave={this.props.handleSave} handleLastStep={this.props.handleLastStep} handleNextStep={this.props.handleNextStep} saveNewTableData={this.props.saveNewTableData}/>
            },{
                title: '新建完成',
                content: <StepFour/>
            },
        ]
        return (
            <div className="create-table-container">
                <Steps current={currentStep}>
                    {
                        steps.map(o=>(
                            <Step key={o.title} title={o.title}/>
                        ))
                    }
                </Steps>
                <div className="form-box">
                    {steps[currentStep].content}
                </div>
            </div>
        )
    }
}

export default CreateTable