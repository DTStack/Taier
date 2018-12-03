import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Form, Button, Card, message } from 'antd';
import { isEmpty } from 'lodash';

import InputParams from './paramsTable/inputParams';
import OutputParams from './paramsTable/outputParams';
import { dataSourceActions } from '../../../actions/dataSource';
import TCApi from '../../../api/tagConfig';

const mapStateToProps = state => {
    const { dataSource, tagConfig, apiMarket } = state;
    return { dataSource, tagConfig, apiMarket }
}

const mapDispatchToProps = dispatch => ({
    getDataSourcesColumn (params) {
        dispatch(dataSourceActions.getDataSourcesColumn(params));
    }
})

@connect(mapStateToProps, mapDispatchToProps)
class StepTwo extends Component {
    state = {
        addInputsignal: false,
        addOutputsignal: false,
        inputData: [],
        outputData: [],
        inputToOutputData: {},
        initInputValue: [],
        initOutputValue: []
    }

    componentDidMount () {
        const { basicInfo, paramsConfig } = this.props;

        if (!isEmpty(paramsConfig)) {
            this.setState({
                initInputValue: paramsConfig.inputParams,
                initOutputValue: paramsConfig.outputParams
            });
        }

        this.props.getDataSourcesColumn({
            sourceId: basicInfo.dataSourceId,
            tableName: basicInfo.originTable
        });
    }

    // input子组件处理信号结束
    changeAddinputOverSignal = () => {
        this.setState({
            addInputsignal: false
        });
    }

    // 给input子组件发送信号
    addInput = (data) => {
        this.setState({
            addInputsignal: true
        });
    }

    // input子组件数据更改
    inputParamsChange = (data, newItem) => {
        // 发送output添加新字段信号
        // if(newItem){
        //     this.addOutput(newItem);
        // }
        this.setState({
            inputData: data
        });
    }

    // output子组件处理信号结束
    changeAddoutputOverSignal = () => {
        this.setState({
            addOutputsignal: false
        });
    }

    // 给output子组件发送信号
    addOutput = (data) => {
        if (data && !data.param) {
            data = null;
        }
        this.setState({
            addOutputsignal: true,
            inputToOutputData: data
        })
    }

    // output子组件数据更改
    outputParamsChange = (data) => {
        this.setState({
            outputData: data
        })
    }

    prev = () => {
        const { inputData, outputData } = this.state;
        const { currentStep, navToStep } = this.props;

        navToStep(currentStep - 1);

        this.props.changeParamsConfig({
            inputParams: inputData,
            outputParams: outputData
        });
    }

    next = () => {
        const { inputData, outputData } = this.state;
        const { tagId, currentStep, navToStep, basicInfo } = this.props;

        if (!inputData || inputData.length < 1) {
            message.error('请设置输入参数');
            return
        }

        if (!outputData || outputData.length < 1) {
            message.error('请设置输出参数');
            return
        }

        if (inputData && outputData && inputData.length > 0 && outputData.length > 0) {
            if (!inputData.some(item => item.required)) {
                message.error('输入参数必须有一个必填项');
                return
            }

            this.props.changeParamsConfig({
                inputParams: inputData,
                outputParams: outputData
            });

            TCApi.publishTag({
                ...basicInfo,
                id: tagId,
                inputParams: inputData,
                outputParams: outputData
            }).then((res) => {
                if (res.code === 1) {
                    navToStep(currentStep + 1);
                }
            });
        } else {
            message.error('请设置输入输出参数');
        }
    }

    render () {
        const { initInputValue, initOutputValue } = this.state;
        return (
            <div>
                <div className="steps-content">
                    <Card
                        title={<span><span style={{ color: '#f04134' }}>*</span> 输入参数</span>}
                        extra={<Button onClick={this.addInput} style={{ marginTop: '10px' }} type="primary">添加输入参数</Button>}
                        className="box-2"
                        noHovering>
                        <InputParams
                            initValue={initInputValue}
                            inputParamsChange={this.inputParamsChange}
                            changeAddinputOverSignal={this.changeAddinputOverSignal}
                            addInputsignal={this.state.addInputsignal} {...this.props} >
                        </InputParams>
                    </Card>
                    <Card
                        title={<span><span style={{ color: '#f04134' }}>*</span> 输出参数</span>}
                        extra={<Button onClick={this.addOutput} style={{ marginTop: '10px' }} type="primary">添加输出参数</Button>}
                        style={{ marginTop: '20px' }}
                        className="box-2"
                        noHovering>
                        <OutputParams
                            initValue={initOutputValue}
                            inputToOutputData={this.state.inputToOutputData}
                            outputParamsChange={this.outputParamsChange}
                            changeAddoutputOverSignal={this.changeAddoutputOverSignal}
                            addOutputsignal={this.state.addOutputsignal} {...this.props} >
                        </OutputParams>
                    </Card>
                </div>
                <div className="steps-action">
                    <Button
                        className="m-r-8"
                        onClick={this.prev}>
                        上一步
                    </Button>
                    <Button
                        type="primary"
                        onClick={this.next}>
                        下一步
                    </Button>
                </div>
            </div>
        )
    }
}

export default (Form.create()(StepTwo));
