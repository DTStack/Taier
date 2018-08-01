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

class InputOrigin extends Component {

    // state = {
    //     panelColumn: this.props.panelColumn
    // }
    // componentWillReceiveProps = (nextProps) => {
    //     if(nextProps.panelColumn!= this.props.panelColumn){
    //         this.setState({
    //             panelColumn: nextProps.panelColumn
    //         })
    //     }
    // }

    componentDidMount(){
        this.props.onRef(this);
    }

    checkParams = () => {
        let result;
        console.log('checkParams-this.props.form',this.props.form);
        this.props.form.validateFields((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);
                result = true;
            }else{
                result = false;
            }
        });
        return result
    }

    render(){
        const { handleInputChange, index,panelColumn } = this.props;
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
                <Form >
                    <FormItem
                        {...formItemLayout}
                        label="类型"
                    >
                        {getFieldDecorator('type', {
                            initialValue: panelColumn[index].type||"disabled",
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
                        label="数据源"
                    >
                        {getFieldDecorator('dataOrigin', {
                            initialValue: panelColumn[index].dataOrigin||"lucy",
                            rules: [
                                {required: true, message: '请选择数据源',}
                            ],
                        })(
                            <Select className="right-select" onChange={(v)=>{handleInputChange("dataOrigin",index,v)}}>
                                    <Option value="jack">Jack</Option>
                                    <Option value="lucy">Lucy</Option>
                                    <Option value="disabled" disabled>Disabled</Option>
                                    <Option value="Yiminghe">yiminghe</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="Topic"
                    >
                        {getFieldDecorator('topic', {
                            initialValue: panelColumn[index].topic||"jack",
                            rules: [
                                {required: true, message: '请选择Topic',}
                            ],
                        })(
                            <Select className="right-select" onChange={(v)=>{handleInputChange("topic",index,v)}}>
                                    <Option value="jack">Jack</Option>
                                    <Option value="lucy">Lucy</Option>
                                    <Option value="disabled" disabled>Disabled</Option>
                                    <Option value="Yiminghe">yiminghe</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label={(
                            <span > 
                                Table&nbsp;
                                <Tooltip title="该表是kafka中的topic映射而成，可以以SQL的方式使用它。">
                                    <Icon type="question-circle-o" /> 
                                </Tooltip>
                            </span>
                            )}
                    >
                        {getFieldDecorator('table', {
                            initialValue: panelColumn[index].table,
                            rules: [
                                {required: true, message: '请输入Table',}
                            ],
                        })(
                            <Input  placeholder="请输入Table" className="right-input" onChange={e => handleInputChange('table',index,e.target.value)}/>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="model"
                    >
                        {getFieldDecorator('model',{
                            initialValue: panelColumn[index].model||1,
                        })(
                            <Radio.Group className="right-select" onChange={(e)=>{handleInputChange("model",index,e.target.value)}}>
                                <Radio.Button value={1}>键值模式</Radio.Button>
                                <Radio.Button value={2}>脚本模式</Radio.Button>
                            </Radio.Group>
                        )}
                    </FormItem>
                    <Col style={{marginBottom: 20}}>
                        <Table dataSource={panelColumn[index].column} pagination={false} >
                            <Column
                                title="字段"
                                dataIndex="column"
                                key="字段"
                                width='50%'
                                render={(text,record,subIndex)=>{return <Input  placeholder="支持字母、数字和下划线" onBlur={e => handleInputChange('subColumn',index,subIndex,e.target.value)}/>}}
                            />
                            <Column
                                title="类型"
                                dataIndex="type"
                                key="类型"
                                width='40%'
                                render={(text,record,subIndex)=>{
                                    return (
                                        <Select defaultValue="lucy"   className="sub-right-select" onChange={(v)=>{handleInputChange("subType",index,subIndex,v)}}>
                                            <Option value="jack">Jack</Option>
                                            <Option value="lucy">Lucy</Option>
                                            <Option value="disabled" disabled>Disabled</Option>
                                            <Option value="Yiminghe">yiminghe</Option>
                                        </Select>
                                    )
                                }}
                            />
                            <Column
                                key="delete"
                                render={(text,record,subIndex)=>{return <Icon type="close" style={{fontSize: 16,color: "#888"}} onClick={()=>{handleInputChange("deleteColumn",index,subIndex)}}/>}}
                            />
                        </Table>
                        <div style={{padding: "0 20"}}>
                            <Button className="stream-btn" type="dashed" style={{borderRadius: 5}} onClick={()=>{handleInputChange("column",index,{})}}>
                                <Icon type="plus" /><span> 添加输入</span>
                            </Button>
                        </div>
                    </Col>
                    <FormItem
                        {...formItemLayout}
                        label="时间特征"
                    >
                        {getFieldDecorator('timeType',{
                            initialValue: panelColumn[index].timeType||1,
                        })(
                            <RadioGroup  className="right-select" onChange={(v)=>{handleInputChange("timeType",index,v.target.value)}}>
                                <Radio value={1}>ProcTime</Radio>
                                <Radio value={2}>EventTime</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                { 
                    panelColumn[index].timeType === 2 ? 
                        <FormItem
                            {...formItemLayout}
                            label="时间列"
                        >
                            {getFieldDecorator('timeColum', {
                                initialValue: panelColumn[index].timeColum||"jack",
                                rules: [
                                    {required: true, message: '请选择时间列',}
                                ],
                            })(
                                <Select className="right-select" onChange={(v)=>{handleInputChange("timeColum",index,v)}}>
                                        <Option value="jack">Jack</Option>
                                        <Option value="lucy">Lucy</Option>
                                        <Option value="disabled" disabled>Disabled</Option>
                                        <Option value="Yiminghe">yiminghe</Option>
                                </Select>
                            )}
                        </FormItem>: undefined
                    }
                    <FormItem
                        {...formItemLayout}
                        label="别名"
                    >
                        {getFieldDecorator('alias', {
                            initialValue: panelColumn[index].alias,
                            rules: [
                                {required: true, message: '请输入别名',}
                            ],
                        })(
                            <Input  placeholder="请输入别名" className="right-input" onChange={e => handleInputChange('alias',index,e.target.value)}/>
                        )}
                    </FormItem>
                </Form>
            </Row>
        )
    }
}

const InputForm = Form.create({
    mapPropsToFields(props) {
            const { type, dataOrigin, topic, table, model, column, timeType, timeColum, alias } = props.panelColumn[props.index];
            return {
                type: { value: type },
                dataOrigin: { value: dataOrigin },
                topic: { value: topic },
                table: { value: table },
                model: { value: model },
                column: { value: column },
                timeType: { value: timeType },
                timeColum: { value: timeColum },
                alias: { value: alias },
            }
        } 
})(InputOrigin);

const initialData = {
    popoverVisible: false,
    tabTemplate: [],//模版存储,所有输入源
    panelActiveKey: [],//输入源是打开或关闭状态
    popoverVisible: [],//删除显示按钮状态
    panelColumn: [],//存储数据
    checkFormParams: [],//存储要检查的参数from
}

export default class InputPanel extends Component {

    constructor(props) {
        super(props)
        const taskId = this.props.currentPage.id;
        const copyInitialData = JSON.parse(JSON.stringify(initialData));
        const data = props.inputData[taskId]||copyInitialData;
        console.log('InputPanel----props',props);
        
        this.state = {...data};
    }
    
    getCurrentData = (taskId) => {
        const { dispatch,inputData } = this.props;
        const copyInitialData = JSON.parse(JSON.stringify(initialData));
        dispatch(BrowserAction.getInputData(taskId));
        const data = inputData[taskId]||copyInitialData;
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
            type: undefined,
            dataOrigin: undefined,
            topic: undefined,
            table: undefined,
            model: undefined,
            column: [],
            timeType: undefined,
            timeColum: undefined,
            alias: undefined,
        }
        let { tabTemplate, panelActiveKey, popoverVisible, panelColumn, checkFormParams } = this.state;
        if(type==="add"){
            tabTemplate.push(InputForm);
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
        this.setCurrentSource({tabTemplate,panelActiveKey,popoverVisible,panelColumn});
        this.setState({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams
        })
    }

    setCurrentSource = (data) => {
        const { dispatch, currentPage } = this.props;
        const dispatchSource = {...this.state,...data};
        dispatch(BrowserAction.setInputData({taskId: currentPage.id ,source: dispatchSource}));
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
        this.setCurrentSource({panelActiveKey})
        this.setState({
            panelActiveKey,
        })
    }
      
    handleInputChange = (type,index,value,subValue) => {//监听数据改变
        const { panelColumn, tabTemplate } = this.state;
        if(type === 'column'){
            panelColumn[index][type].push(value);
        }else if(type === "deleteColumn"){
            panelColumn[index]["column"].splice(value,1);
        }else if(type ==="subColumn"){
            panelColumn[index]["column"][value].column = subValue;
        }else if(type === "subType"){
            panelColumn[index]["column"][value].type = subValue;
        }else{
            panelColumn[index][type] = value;
        }
        tabTemplate[index] = InputForm;
        this.setCurrentSource({panelColumn})
        this.setState({
            panelColumn,
            tabTemplate
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
        this.setCurrentSource({popoverVisible})
        this.setState({ popoverVisible });
    }

    panelHeader = (index) => {
        const { popoverVisible } = this.state;
        const popoverContent = <div className="input-panel-title">
            <div style={{padding: "8 0 12"}}> <Icon type="exclamation-circle" style={{color: "#faad14",}}/>  你确定要删除此输入源吗？</div>
            <div style={{textAlign: "right",padding: "0 0 8"}}>
                <Button style={{marginRight: 8}} size="small" onClick={()=>{this.handlePopoverVisibleChange(null,index,false)}}>取消</Button>
                <Button type="primary" size="small" onClick={()=>{this.changeInputTabs('delete',index)}}>确定</Button>
            </div>
        </div>
        return <div className="input-panel-title">
            <span>{` 输入源 ${index+1} (仅支持Json)`}</span>
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
        this.setCurrentSource({checkFormParams})
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
                        tabTemplate.map( (InputPutOrigin,index) => {
                            return  (
                                <Panel header={this.panelHeader(index)} key={index+1} style={{borderRadius: 5}}>
                                    <InputForm index={index} handleInputChange={this.handleInputChange} panelColumn={panelColumn}  onRef={this.recordForm}/>
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

