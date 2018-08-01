import React, { Component } from 'react'
import {
    Row, Col, Modal, Tag, Icon,Tooltip,Table,Input,
    message, Select, Collapse, Button,Radio,Popover,
    Form
} from 'antd'

import utils from 'utils'
import Api from '../../../api'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import TaskVersion from '../offline/taskVersion';

const Option = Select.Option;
const Panel = Collapse.Panel;
const RadioGroup = Radio.Group;
const { Column, ColumnGroup } = Table;
const FormItem = Form.Item;

class OutputOrigin extends Component {

    componentDidMount(){
        this.props.onRef(this);
    }

    checkParams = () => {
        let result;
        this.props.form.validateFields((err, values) => {
            if (!err) {
                result = true;
            }else{
                result = false;
            }
        });
        return result
    }

    render(){
        const { handleInputChange,index,panelColumn } = this.props;
        const { getFieldDecorator } = this.props.form;
        const formItemLayout = {
            labelCol: {
              xs: { span: 24 },
              sm: { span: 6 },
            },
            wrapperCol: {
              xs: { span: 24 },
              sm: { span: 14 },
            },
        };
        return (
            <Row className="title-content">
                <FormItem
                    {...formItemLayout}
                    label="存储类型"
                >
                    {getFieldDecorator('saveType', {
                        initialValue: "disabled",
                        rules: [
                            {required: true, message: '请选择存储类型',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("saveType",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" >Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="数据源"
                >
                    {getFieldDecorator('dataOrigin', {
                        initialValue: "disabled",
                        rules: [
                            {required: true, message: '请选择数据源',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("dataOrigin",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" >Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="Topic"
                >
                    {getFieldDecorator('topic', {
                        initialValue: "disabled",
                        rules: [
                            {required: true, message: '请选择Topic',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("topic",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" >Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                        </Select>
                    )}
                </FormItem>
                {/* <FormItem
                    {...formItemLayout}
                    label="表"
                >
                    {getFieldDecorator('table', {
                        initialValue: "disabled",
                        rules: [
                            {required: true, message: '请选择表',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("table",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" >Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                        </Select>
                    )}
                </FormItem> 
                <FormItem
                    {...formItemLayout}
                    label="索引"
                >
                    {getFieldDecorator('index', {
                        initialValue: "disabled",
                        rules: [
                            {required: true, message: '请选择索引',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("index",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" >Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="类型"
                >
                    {getFieldDecorator('type', {
                        initialValue: "disabled",
                        rules: [
                            {required: true, message: '请选择类型',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("type",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" >Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="写入策略"
                >
                    {getFieldDecorator('writePolicy', {
                        initialValue: "disabled",
                        rules: [
                            {required: true, message: '请选择写入策略',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("writePolicy",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" >Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                        </Select>
                    )}
                </FormItem> */}
            </Row>
        )
    }
}

const OutputForm = Form.create({
    mapPropsToFields(props) {
            const { saveType, dataOrigin, topic } = props.panelColumn[props.index];
            return {
                saveType: { value: saveType },
                dataOrigin: { value: dataOrigin },
                topic: { value: topic },
            }
        } 
})(OutputOrigin);

const initialData = {
    popoverVisible: false,
    tabTemplate: [],//模版存储,所有输出源(记录个数)
    panelActiveKey: [],//输出源是打开或关闭状态
    popoverVisible: [],//删除显示按钮状态
    panelColumn: [],//存储数据
    checkFormParams: [],//存储要检查的参数from
}

export default class OutputPanel extends Component {

    constructor(props) {
        super(props)
        const taskId = this.props.currentPage.id;
        const copyInitialData = JSON.parse(JSON.stringify(initialData));
        const data = props.outputData[taskId]||copyInitialData;
        this.state = {...data};
    }
    
    getCurrentData = (taskId) => {
        const { dispatch,outputData } = this.props;
        const copyInitialData = JSON.parse(JSON.stringify(initialData));
        dispatch(BrowserAction.getOutputData(taskId));
        const data = outputData[taskId]||copyInitialData;
        this.setState({...data})
    }

    componentWillReceiveProps(nextProps) {
        const currentPage = nextProps.currentPage
        const oldPage = this.props.currentPage
        if (currentPage.id !== oldPage.id) {
            this.getCurrentData(currentPage.id)
        }
    }

    changeInputTabs = (type,index) =>{
        const inputData = {
            tysaveTypepe: undefined,
            dataOrigin: undefined,
            topic: undefined,
        }
        let { tabTemplate, panelActiveKey, popoverVisible, panelColumn, checkFormParams } = this.state;
        if(type==="add"){
            tabTemplate.push(OutputForm);
            panelColumn.push(inputData);
            let pushIndex = `${tabTemplate.length}`;
            panelActiveKey.push(pushIndex)
        }else{
            tabTemplate.splice(index,1);
            panelColumn.splice(index,1);
            checkFormParams.pop();
            panelActiveKey = this.changeActiveKey(index);
            popoverVisible[index] = false;
        }
        this.setOutputData({tabTemplate,panelActiveKey,popoverVisible,panelColumn});
        this.setState({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams
        })
    }

    setOutputData = (data) => {
        const { dispatch, currentPage } = this.props;
        const dispatchSource = {...this.state,...data};
        dispatch(BrowserAction.setOutputData({taskId: currentPage.id ,source: dispatchSource}));
    }

    changeActiveKey = (index) => {// 删除导致key改变,处理被改变key的值
        const {panelActiveKey} = this.state;
        const deleteActiveKey = `${index+1}`;
        const deleteActiveKeyIndex  = panelActiveKey.indexOf(deleteActiveKey);
        if(deleteActiveKeyIndex > -1){
            panelActiveKey.splice(deleteActiveKeyIndex,1)
        }
        return panelActiveKey.map(v=>{
            return Number(v) > Number(index) ? `${Number(v) -1}`: v
        });
    }

    handleActiveKey = (key) => {
        let { panelActiveKey } = this.state;
        panelActiveKey = key
        this.setOutputData({panelActiveKey})
        this.setState({
            panelActiveKey,
        })
    }
      
    handleInputChange = (type,index,value,) => {//监听数据改变
        const { panelColumn } = this.state;
        panelColumn[index][type] = value;
        this.setOutputData({panelColumn})
        this.setState({
            panelColumn,
        })
    }

    handlePopoverVisibleChange = (e,index,visible) => {
        let { popoverVisible } = this.state;
        popoverVisible[index] = visible;
        if (e) {
            e.stopPropagation();//阻止删除按钮点击后冒泡到panel
            if(visible){//只打开一个Popover提示
                popoverVisible = popoverVisible.map( (v,i) => {
                    return index == i ? true : false
                })
            }
        }
        this.setOutputData({popoverVisible})
        this.setState({ popoverVisible });
    }

    panelHeader = (index) => {
        const { popoverVisible } = this.state;
        const popoverContent = <div className="input-panel-title">
            <div style={{padding: "8 0 12"}}> <Icon type="exclamation-circle" style={{color: "#faad14",}}/>  你确定要删除此输出源吗？</div>
            <div style={{textAlign: "right",padding: "0 0 8"}}>
                <Button style={{marginRight: 8}} size="small" onClick={()=>{this.handlePopoverVisibleChange(null,index,false)}}>取消</Button>
                <Button type="primary" size="small" onClick={()=>{this.changeInputTabs('delete',index)}}>确定</Button>
            </div>
        </div>
        return <div className="input-panel-title">
            <span>{` 输出源 ${index+1} (仅支持Json)`}</span>
            <Popover
                trigger="click"
                placement="topLeft"
                content={popoverContent}
                visible={popoverVisible[index]}
                onClick={(e)=>{this.handlePopoverVisibleChange(e,index,!popoverVisible[index])}}
            >
                <span className="title-icon input-panel-title" ><Icon type="delete" /></span>
            </Popover>
        </div>
    }

    recordForm = (ref) => {//存储子组建的所有要检查的form表单
        const { checkFormParams } = this.state;
        checkFormParams.push(ref);
        this.setOutputData({checkFormParams})
        this.setState({
            checkFormParams
        })
    }


    render() {
        const { tabTemplate,panelActiveKey,panelColumn } = this.state;
        return (
            <div className="m-taksdetail panel-content">
                <Collapse activeKey={panelActiveKey}  onChange={this.handleActiveKey} className="input-panel">
                    {
                        tabTemplate.map( (OutputPutOrigin,index) => {
                            return  (
                                <Panel header={this.panelHeader(index)} key={index+1} style={{borderRadius: 5}}>
                                    <OutputForm index={index} handleInputChange={this.handleInputChange} panelColumn={panelColumn}  onRef={this.recordForm}/>
                                </Panel>
                            )
                        })
                    }
                </Collapse>
                <Button className="stream-btn" onClick={()=> {this.changeInputTabs('add')}} style={{borderRadius: 5}}><Icon type="plus" /><span> 添加输入</span></Button>
            </div>
        )
    }
}

