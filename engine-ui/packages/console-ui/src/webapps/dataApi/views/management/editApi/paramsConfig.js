import React, { Component } from "react"
import { Input, Icon, Button, Checkbox, Select, Row, Card, Col, Table,message } from "antd";
import { Link } from 'react-router';
import InputParams from "./paramsTable/inputParams"
import OutputParams from "./paramsTable/outputParams"
import { formItemLayout } from "../../../consts"
const TextArea=Input.TextArea;
class ManageParamsConfig extends Component {
    state = {
        addInputsignal:false,
        addOutputsignal:false,
        inputData:[],
        outputData:[],
        inputToOutputData:{}
    }
    //input子组件处理信号结束
    changeAddinputOverSignal(){
        this.setState({
            addInputsignal:false
        })
    }
    //给input子组件发送信号
    addInput(data){
        this.setState({
            addInputsignal:true
            
        })
    }
    //input子组件数据更改
    inputParamsChange(data,newItem){
        // if(newItem){
        //     this.addOutput(newItem);
        // }
        this.setState({
            inputData:data
        })
    }
    //output子组件处理信号结束
    changeAddoutputOverSignal(){
        this.setState({
            addOutputsignal:false
        })
    }
    //给output子组件发送信号
    addOutput(data){
        if(data&&!data.param){
            data=null;
        }
        this.setState({
            addOutputsignal:true,
            inputToOutputData:data
        })
    }
     //output子组件数据更改
     outputParamsChange(data){
        this.setState({
            outputData:data
        })
    }
 
    pass() {
        if(!this.state.inputData||this.state.inputData.length<1){
            message.error("请设置输入参数")
            return;
        }
        if(!this.state.outputData||this.state.outputData.length<1){
            message.error("请设置输出参数")
            return;
        }
        if(this.state.inputData&&this.state.outputData&&this.state.inputData.length>0&&this.state.outputData.length>0){
            let haveRequired=false;
            for(let i in this.state.inputData){
                if(this.state.inputData[i].isRequired){
                    haveRequired=true;
                    break;
                }
            }
            if(!haveRequired){
                message.error("输入参数必须有一个必填项")
                return;
            }
            this.props.dataChange({
                inputData:this.state.inputData,
                outputData:this.state.outputData
            })
        }else{
            message.error("请设置输入输出参数")
        }
        

    }

    render() {
        return (
            <div>
                <div className="steps-content">
                    <Card
                        title={
                            (() => {
                                return <span><span style={{ color: "#f04134" }}>*</span> 输入参数</span>
                            })()
                        }
                        extra={<Button onClick={this.addInput.bind(this)} style={{ marginTop: "10px" }} type="primary">添加输入参数</Button>}
                        className="box-2"
                        noHovering
                    >
                        <InputParams initValue={this.props.initValues&&this.props.initValues.inputParam} inputParamsChange={this.inputParamsChange.bind(this)} changeAddinputOverSignal={this.changeAddinputOverSignal.bind(this)} addInputsignal={this.state.addInputsignal} {...this.props} ></InputParams>
                    </Card>
                    <Card
                        title={
                            (() => {
                                return <span><span style={{ color: "#f04134" }}>*</span> 输出参数</span>
                            })()
                        }
                        extra={<Button onClick={this.addOutput.bind(this)} style={{ marginTop: "10px" }} type="primary">添加输出参数</Button>}
                        style={{ marginTop: "20px" }}
                        className="box-2"
                        noHovering
                    >
                         <OutputParams initValue={this.props.initValues&&this.props.initValues.outputParam} inputToOutputData={this.state.inputToOutputData} outputParamsChange={this.outputParamsChange.bind(this)} changeAddoutputOverSignal={this.changeAddoutputOverSignal.bind(this)} addOutputsignal={this.state.addOutputsignal} {...this.props} ></OutputParams>
                       
                </Card>
                </div>
                <div
                    className="steps-action"
                >
                    {
                        <Button onClick={() => this.props.cancel()}>
                            取消
                        </Button>
                    }
                    {
                        <Button style={{ marginLeft: 8 }} onClick={() => this.props.prev()}>上一步</Button>
                    }
                    {
                        <Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.pass()}>保存</Button>
                    }

                </div>
            </div>
        )
    }
}

export default ManageParamsConfig