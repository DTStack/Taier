import React, { Component } from 'react'
import {
    Row, Col, Modal, Tag, Icon,Tooltip,Table,Input,
    message, Select, Collapse, Button,Radio,Popover,
    Form
} from 'antd'

import utils from 'utils'
import Api from '../../../api'
import { mysqlFieldTypes } from '../../../comm/const';
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

    checkParams = (v) => {
        //手动检测table参数
        const { index,panelColumn } = this.props;
        const tableColumns = panelColumn[index].columns;
        
        let result = {};
        this.props.form.validateFields((err, values) => {
            if (!err) {
                result.status = true;
                if(tableColumns.length === 0){
                    result.status = false;
                    result.message = "至少添加一个字段"
                }else{
                    tableColumns.map(v=>{
                        if(!v.column||!v.type){
                            result.status = false;
                            result.message= "有未填写的字段或类型"
                        }
                    })
                }
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
            default:
                return null;
        }
    }

    render(){
        const { handleInputChange,index, outputSearchParams,panelColumn } = this.props;
        const { getFieldDecorator } = this.props.form;
        const currentOpt = outputSearchParams[index];
        const originOptionType = this.originOption('originType',currentOpt&&currentOpt.originType||[]);
        const tableOptionType = this.originOption('currencyType',currentOpt&&currentOpt.tableType||[]);
        const mysqlOptionType = this.originOption('currencyType',mysqlFieldTypes)
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
                    {getFieldDecorator('type', {
                        initialValue: "disabled",
                        rules: [
                            {required: true, message: '请选择存储类型',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("type",index,v)}}>
                                <Option value="1">Mysql</Option>
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="数据源"
                >
                    {getFieldDecorator('sourceId', {
                        initialValue: "disabled",
                        rules: [
                            {required: true, message: '请选择数据源',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("sourceId",index,v)}}>
                               {
                                   originOptionType
                               }
                        </Select>
                    )}
                </FormItem>
                <FormItem
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
                                {
                                    tableOptionType
                                }
                        </Select>
                    )}
                </FormItem>
                <FormItem
                        {...formItemLayout}
                        label="字段"
                    >
                </FormItem>

                <Col style={{marginBottom: 20}}>
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
                </Col>
                {/* <FormItem
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
                </FormItem> */}
                {/*<FormItem
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
            const { type, sourceId, table, columns } = props.panelColumn[props.index];
            return {
                type: { value: type },
                sourceId: { value: sourceId },
                table: { value: table },
                columns: { value: columns },
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
    outputSearchParams: [],//所有输入选择的参数
}

export default class OutputPanel extends Component {

    constructor(props) {
        super(props)
        const taskId = this.props.currentPage.id;
        const copyInitialData = JSON.parse(JSON.stringify(initialData));
        const data = props.outputData[taskId]||copyInitialData;
        this.state = {...data};
    }
    
    componentDidMount(){
        const { sink } = this.props.currentPage;
        if(sink&&sink.length>0){
            this.currentInitData(sink)
        }
    }

    currentInitData = (sink) => {
        const {tabTemplate,panelColumn} = this.state;
        sink.map( v => {
            tabTemplate.push(OutputForm);
            panelColumn.push(v);
            this.getTypeOriginData("add",v.type);
        })
        this.setState({
            tabTemplate,
            panelColumn
        })
    }

    getCurrentData = (taskId) => {
        const { dispatch,outputData } = this.props;
        const copyInitialData = JSON.parse(JSON.stringify(initialData));
        dispatch(BrowserAction.getOutputData(taskId));
        const data = outputData[taskId]||copyInitialData;
        this.setState({...data})
    }

    getTypeOriginData = (index,type) => {
        const { outputSearchParams } = this.state;
        const selectData = { originType: [],tableType: [] };
        Api.getTypeOriginData({type}).then(v=>{
            if(v.code===1){
                if(index === "add"){
                    selectData.originType = v.data;
                    outputSearchParams.push(selectData);
                }else{
                    outputSearchParams[index].originType = v.data;
                }
            }else{
                if(index === "add"){
                    outputSearchParams.push(selectData);
                }
            }
            this.setOutputData({outputSearchParams});
            this.setState({
                outputSearchParams
            })
        })
    }

    getTableType = (index,sourceId) => {
        const { outputSearchParams } = this.state;
        const selectData = outputSearchParams[index];
        Api.getOfflineTableList({sourceId,"isSys":false}).then(v=>{
            if(v.code===1){
                selectData.tableType = v.data
                this.setState({
                    outputSearchParams
                })
            }
        })
    }

    componentWillReceiveProps(nextProps) {
        const currentPage = nextProps.currentPage
        const oldPage = this.props.currentPage
        if (currentPage.id !== oldPage.id) {
            this.setOutputData(currentPage.id)
        }
    }

    changeInputTabs = (type,index) =>{
        const inputData = {
            type: "1",
            columns: [],
            sourceId: undefined,
            table: undefined,
        }
        let { tabTemplate, panelActiveKey, popoverVisible, panelColumn, checkFormParams, outputSearchParams } = this.state;
        if(type==="add"){
            tabTemplate.push(OutputForm);
            panelColumn.push(inputData);
            this.getTypeOriginData("add",inputData.type);
            let pushIndex = `${tabTemplate.length}`;
            panelActiveKey.push(pushIndex)
        }else{
            tabTemplate.splice(index,1);
            panelColumn.splice(index,1);
            outputSearchParams.splice(index,1);
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
            checkFormParams,
            outputSearchParams
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
      
    handleInputChange = (type,index,value,subValue) => {//监听数据改变
        const { panelColumn } = this.state;
        if(type === 'columns'){
            panelColumn[index][type].push(value);
        }else if(type === "deleteColumn"){
            panelColumn[index]["columns"].splice(value,1);
        }else if(type ==="subColumn"){
            panelColumn[index]["columns"][value].column = subValue;
        }else if(type === "subType"){
            panelColumn[index]["columns"][value].type = subValue;
        }else{
            panelColumn[index][type] = value;
        }
        if(type==="type"){
            this.getTypeOriginData(index,value);
        }else if(type==="sourceId"){
            this.getTableType(index,value)
        }
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
        const { tabTemplate,panelActiveKey,panelColumn,outputSearchParams } = this.state;
        return (
            <div className="m-taksdetail panel-content">
                <Collapse activeKey={panelActiveKey}  onChange={this.handleActiveKey} className="input-panel">
                    {
                        tabTemplate.map( (OutputPutOrigin,index) => {
                            return  (
                                <Panel header={this.panelHeader(index)} key={index+1} style={{borderRadius: 5}}>
                                    <OutputForm index={index} handleInputChange={this.handleInputChange} panelColumn={panelColumn} outputSearchParams={outputSearchParams} onRef={this.recordForm}/>
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

