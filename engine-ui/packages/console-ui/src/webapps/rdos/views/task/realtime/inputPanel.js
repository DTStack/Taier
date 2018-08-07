import React, { Component } from 'react'
import {
    Row,Col,Icon,Tooltip,Table,Input,Select,
    Collapse, Button,Radio,Popover,Form,InputNumber
} from 'antd'

import Api from '../../../api';
import { mysqlFieldTypes } from '../../../comm/const';

import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import Editor from '../../../components/code-editor'
import { jsonEditorOptions, LOCK_TYPE } from '../../../comm/const'



const Option = Select.Option;
const Panel = Collapse.Panel;
const RadioGroup = Radio.Group;
const { Column, ColumnGroup } = Table;
const FormItem = Form.Item;

class InputOrigin extends Component {
    componentDidMount(){
        this.props.onRef(this);
    }

    checkParams = () => {
        //手动检测table参数
        const { index,panelColumn } = this.props;
        const tableColumns = panelColumn[index].columns;
        let result = {};
        this.props.form.validateFields((err, values) => {
            if (!err) {
                result.status = true;
                // if(tableColumns.length === 0){
                //     result.status = false;
                //     result.message = "至少添加一个字段"
                // }else{
                //     tableColumns.map(v=>{
                //         if(!v.column||!v.type){
                //             result.status = false;
                //             result.message= "有未填写的字段或类型"
                //         }
                //     })
                // }
            }else{
                result.status = false;
            }
        });
        return result
    }

    originOption = (type,arrData)=> {
        switch (type) {
            case "originType":
                return arrData.map(v=>{
                    return  <Option key={v} value={`${v.id}`}>{v.name}</Option>
                 })
            case "currencyType":
               return arrData.map(v=>{
                    return  <Option key={v} value={`${v}`}>{v}</Option>
                })
            case "eventTime":
                return arrData.map((v,index)=>{
                    return  <Option key={index} value={`${v.column}`}>{v.column}</Option>
                })  
            default:
                return null;
        }
    }

    editorParamsChange(a,b,c){
        const { handleInputChange, index, } = this.props;
        this._syncEditor=false;
        handleInputChange("columnsText",index,b);
        //this.props.editorParamsChange(...arguments);
    }

    render(){
        const { handleInputChange, index, panelColumn,sync,timeColumoption=[],originOptionType=[],topicOptionType=[] } = this.props;
        console.log('timeColumoption',timeColumoption);
        
        const originOptionTypes = this.originOption('originType',originOptionType[index]||[]);
        const topicOptionTypes = this.originOption('currencyType',topicOptionType[index]||[]);
        const eventTimeOptionType = this.originOption('eventTime',timeColumoption[index]||[]);
        //const mysqlOptionType = this.originOption('currencyType',mysqlFieldTypes)
        const { getFieldDecorator } = this.props.form;
        const formItemLayout = {
            labelCol: {
              xs: { span: 24 },
              sm: { span: 8 },
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
                            rules: [
                                {required: true, message: '请选择类型',}
                            ],
                        })(
                            <Select placeholder="请选择" className="right-select" onChange={(v)=>{handleInputChange("type",index,v)}}>
                                    <Option value="14">Kafka</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="数据源"
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [
                                {required: true, message: '请选择数据源',}
                            ],
                        })(
                            <Select placeholder="请选择" className="right-select" onChange={(v)=>{handleInputChange("sourceId",index,v)}}>
                                {
                                    originOptionTypes
                                }
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="Topic"
                    >
                        {getFieldDecorator('topic', {
                            rules: [
                                {required: true, message: '请选择Topic',}
                            ],
                        })(
                            <Select placeholder="请选择" className="right-select" onChange={(v)=>{handleInputChange("topic",index,v)}}>
                                {
                                    topicOptionTypes
                                }
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label={(
                            <span >
                                映射表&nbsp;
                                <Tooltip title="该表是kafka中的topic映射而成，可以以SQL的方式使用它。">
                                    <Icon type="question-circle-o" /> 
                                </Tooltip>
                            </span>
                            )}
                    >
                        {getFieldDecorator('table', {
                            rules: [
                                {required: true, message: '请输入映射表名',}
                            ],
                        })(
                            <Input  placeholder="请输入映射表名" className="right-input" onChange={e => handleInputChange('table',index,e.target.value)}/>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="字段"
                    >
                    </FormItem>
                    <Col style={{marginBottom: 20,height: 200}}>
                        <Editor 
                            style={{height: 200}}
                            key="params-editor"
                            sync={sync}
                            placeholder="字段:类型, 比如id:int 一行一个字段"
                            // options={jsonEditorOptions}
                            value={panelColumn[index].columnsText}
                            onChange={this.editorParamsChange.bind(this)}
                        />
                    </Col>
                    {/* <Col style={{marginBottom: 20}}>
                        <Table dataSource={panelColumn[index].columns} className="table-small" pagination={false} size="small" >
                            <Column
                                title="字段"
                                dataIndex="column"
                                key="字段"
                                width='50%'
                                render={(text,record,subIndex)=>{return <Input value={text} placeholder="支持字母、数字和下划线" onChange={e => handleInputChange('subColumn',index,subIndex,e.target.value)}/>}}
                            />
                            <Column
                                title="类型"
                                dataIndex="type"
                                key="类型"
                                width='40%'
                                render={(text,record,subIndex)=>{
                                    return (
                                        <Select placeholder="请选择" value={text} className="sub-right-select" onChange={(v)=>{handleInputChange("subType",index,subIndex,v)}}>
                                            {
                                                mysqlOptionType
                                            }
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
                            <Button className="stream-btn" type="dashed" style={{borderRadius: 5}} onClick={()=>{handleInputChange("columns",index,{})}}>
                                <Icon type="plus" /><span> 添加输入</span>
                            </Button>
                        </div>
                    </Col> */}
                    <FormItem
                        {...formItemLayout}
                        label="时间特征"
                    >
                        {getFieldDecorator('timeType')(
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
                                {getFieldDecorator('timeColumn', {
                                    rules: [
                                        {required: true, message: '请选择时间列',}
                                    ],
                                })(
                                    <Select placeholder="请选择" className="right-select" onChange={(v)=>{handleInputChange("timeColumn",index,v)}}>
                                           {
                                              eventTimeOptionType
                                           }
                                    </Select>
                                )}
                            </FormItem>: undefined
                    }
                    {
                        panelColumn[index].timeType === 2 ? 
                            <FormItem
                                {...formItemLayout}
                                label={(
                                    <span >
                                        偏移量(毫秒)&nbsp;
                                        <Tooltip title="watermark值与event time值的偏移量">
                                            <Icon type="question-circle-o" /> 
                                        </Tooltip>
                                    </span>
                                )}
                            >
                                {getFieldDecorator('offset', {
                                    rules: [
                                        {required: true, message: '请输入时间偏移量'},
                                        // { validator: this.checkConfirm }
                                    ],
                                })(
                                    <InputNumber className="number-input" min={0} onChange={value => handleInputChange('offset',index,value)}/>
                                )}
                            </FormItem>: undefined
                    }
                    <FormItem
                        {...formItemLayout}
                        label="并行度"
                    >
                        {getFieldDecorator('parallelism')(
                            <InputNumber className="number-input" min={1} onChange={value => handleInputChange('parallelism',index,value)}/>
                        )}
                    </FormItem>
                    {/* <FormItem
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
                    </FormItem> */}
                </Form>
            </Row>
        )
    }
}

const InputForm = Form.create({
    mapPropsToFields(props) {
            const { type, sourceId, topic, table , columns, timeType, timeColumn, offset,columnsText, parallelism } = props.panelColumn[props.index];
            console.log('props.panelColumn[props.index]',props.panelColumn[props.index]);
            return {
                type: { value: type },
                sourceId: { value: sourceId },
                topic: { value: topic },
                table: { value: table },
                columns: { value: columns },
                timeType: { value: timeType },
                timeColumn: { value: timeColumn },
                offset: { value: offset},
                columnsText: { value: columnsText},
                parallelism: { value: parallelism},

                // alias: { value: alias },
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
    timeColumoption: [],//时间列选择数据
    topicOptionType: [],//topic选择数据
    originOptionType: [],//数据源选择数据
}

export default class InputPanel extends Component {

    constructor(props) {
        super(props)
        const taskId = this.props.currentPage.id;
        const copyInitialData = JSON.parse(JSON.stringify(initialData));
        const data = props.inputData[taskId]||copyInitialData;
        this.state = {...data};
    }
    
    componentDidMount(){
        const { source } = this.props.currentPage;
        if(source&&source.length>0){
            this.currentInitData(source)
        }
    }

    currentInitData = (source) => {
        const {tabTemplate,panelColumn} = this.state;
        source.map( (v,index) => {
            tabTemplate.push(InputForm);
            panelColumn.push(v);
            this.getTypeOriginData(index,v.type);
            this.parseColumnsText(index,v.columnsText);
            this.getTopicType(index,v.sourceId);
        })
        this.setCurrentSource({tabTemplate,panelColumn})
        this.setState({
            tabTemplate,
            panelColumn
        })
    }

    parseColumnsText = (index,text="")=>{
        const { timeColumoption } = this.state;
        const columns =  text.split("\n").map(v=>{
            let column;
            if(v.includes(" as ")){
                column = v.split(" as ")
            }else{
                column = v.split(":");
            }
            return { column: column[0],type: column[1] }
        })
       const filterColumns = columns.filter(v=>{
            return v.column&&v.type
        })
        timeColumoption[index] = filterColumns;
        this.setCurrentSource({timeColumoption})
        this.setState({
            timeColumoption
        })
    }

    getTypeOriginData = (index,type) => {
        const { originOptionType } = this.state;
        Api.getTypeOriginData({type}).then(v=>{
            if(index==='add'){
                if(v.code===1){
                    originOptionType.push(v.data) 
                }else{
                    originOptionType.push([]) 
                }
            }else{
                if(v.code===1){
                    originOptionType[index] = v.data;
                }else{
                    originOptionType[index] = [];
                }
            }
            this.setCurrentSource({originOptionType});
            this.setState({
                originOptionType
            })
        })
    }

    getTopicType = (index,sourceId) => {
        const { topicOptionType } = this.state;
        console.log('getTopicType',index,sourceId);
        
        if(sourceId){
            Api.getTopicType({sourceId}).then(v=>{
                if(index==='add'){
                    if(v.code===1){
                        topicOptionType.push(v.data) 
                    }else{
                        topicOptionType.push([]) 
                    }
                }else{
                    if(v.code===1){
                        topicOptionType[index] = v.data;
                    }else{
                        topicOptionType[index] = [];
                    }
                }
                console.log('topicOptionType',topicOptionType);
                this.setCurrentSource({topicOptionType});
                this.setState({
                    topicOptionType
                })
            })
        }else{
            if(index==="add"){
                topicOptionType.push([]);
            }else{
                topicOptionType[index] = [];
            }
            this.setCurrentSource({topicOptionType});
            this.setState({
                topicOptionType
            })
        }
    }

    getCurrentData = (taskId,nextProps) => {
        const { dispatch,inputData,currentPage } = nextProps;
        const { source } = currentPage;
        console.log('-----inputData:',inputData);
        console.log('inputData[taskId]:',inputData[taskId]);
        console.log('componentWillReceiveProps-source',source);
        
        if(!inputData[taskId]&&source.length>0){
            this.receiveState(taskId,source,dispatch)
        }else{
            const copyInitialData = JSON.parse(JSON.stringify(initialData));
            const data = inputData[taskId]||copyInitialData;
            this.setState({...data})
        }
    }

    receiveState = (taskId,source,dispatch) => {
        const tabTemplate = [];
        const panelColumn = [];
        const panelActiveKey = [];
        const popoverVisible = [];
        const checkFormParams = [];
        const timeColumoption = [];
        const originOptionType = [];
        const topicOptionType = [];
        source.map( v => {
            tabTemplate.push(InputForm);
            panelColumn.push(v);
        })
        dispatch(BrowserAction.setInputData({
            taskId ,
            source: {
                tabTemplate,panelColumn,panelActiveKey,
                popoverVisible,checkFormParams,timeColumoption,
                originOptionType,topicOptionType
            }
        }));
        this.setState({
            tabTemplate,panelColumn,panelActiveKey,popoverVisible,checkFormParams,originOptionType,topicOptionType,timeColumoption
        },()=>{ source.map((v,index)=>{
            this.getTypeOriginData(index,v.type);
            this.getTopicType(index,v.sourceId)
            this.parseColumnsText(index,v.columnsText)
        })})
    }

    componentWillReceiveProps(nextProps) {
        const currentPage = nextProps.currentPage
        const oldPage = this.props.currentPage
        console.log('oldPage.id----currentPage.id',currentPage.id,oldPage.id);
        
        if (currentPage.id !== oldPage.id) {
            this._syncEditor=true;
            this.getCurrentData(currentPage.id,nextProps)
        }
    }
    changeInputTabs = (type,index) =>{
        const inputData = {
            type: "14",
            sourceId: undefined,
            topic: undefined,
            table: undefined,
            //model: 1,
            // columns: [],
            timeType: 1,
            timeColumn: undefined,
            offset: 0,
            columnsText: undefined,
            parallelism: 1,
            // alias: undefined,
        }
        
        let { tabTemplate, panelActiveKey, popoverVisible, panelColumn, checkFormParams ,originOptionType,topicOptionType } = this.state;
        if(type==="add"){
            tabTemplate.push(InputForm);
            panelColumn.push(inputData);
            this.getTypeOriginData("add",inputData.type);
            this.getTopicType("add",inputData.topic)
            let pushIndex = `${tabTemplate.length}`;
            panelActiveKey.push(pushIndex)
        }else{
            tabTemplate.splice(index,1);
            panelColumn.splice(index,1);
            originOptionType.splice(index,1);
            topicOptionType.splice(index,1);
            checkFormParams.pop();
            panelActiveKey = this.changeActiveKey(index);
            popoverVisible[index] = false;
        }
        this.setCurrentSource({tabTemplate,panelActiveKey,popoverVisible,panelColumn,originOptionType,topicOptionType});
        this.setState({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams,
            originOptionType,
            topicOptionType
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
        const { panelColumn } = this.state;
        // if(type === 'columns'){
        //     panelColumn[index][type].push(value);
        // }else if(type === "deleteColumn"){
        //     panelColumn[index]["columns"].splice(value,1);
        // }else if(type ==="subColumn"){
        //     panelColumn[index]["columns"][value].column = subValue;
        // }else if(type === "subType"){
        //     panelColumn[index]["columns"][value].type = subValue;
        // }else{
        //     panelColumn[index][type] = value;
        // }
        if(type === "columnsText"){
            this._syncEditor=false;
            this.parseColumnsText(index,value)
        }
        panelColumn[index][type] = value;
        if(type==="type"){
            this.getTypeOriginData(index,value);
        }else if(type==="sourceId"){
            this.getTopicType(index,value);
        }
        this.setCurrentSource({panelColumn})
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
        const { tabTemplate,panelActiveKey,panelColumn,timeColumoption,topicOptionType,originOptionType } = this.state;
        return (
            <div className="m-taksdetail panel-content">
                <Collapse activeKey={panelActiveKey}  onChange={this.handleActiveKey} className="input-panel">
                    {
                        tabTemplate.map( (InputPutOrigin,index) => {
                            return  (
                                <Panel header={this.panelHeader(index)} key={index+1} style={{borderRadius: 5}}>
                                    <InputForm  
                                        sync={this._syncEditor} index={index} key={index+1} 
                                        handleInputChange={this.handleInputChange} panelColumn={panelColumn} 
                                        onRef={this.recordForm} 
                                        editorParamsChange={this.props.editorParamsChange}
                                        timeColumoption = { timeColumoption }
                                        topicOptionType = { topicOptionType }
                                        originOptionType = { originOptionType }
                                    />
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

