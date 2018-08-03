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
        console.log("componentDidMount=----componentDidMount");
        
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

    // checkConfirm = (rule, value, callback)=>{
    //     const number = parseInt(value || 0, 10);
    //     if (isNaN(number)) {
    //         callback("请输入数字");
    //     }else{
    //         return
    //     }
    // }

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

    // editorParamsChange(a,b,c){
    //     this._syncEditor=false;
    //     console.log('editorParamsChange',b);

    //     console.log('editorParamsChange',...arguments);
        
    //     this.props.editorParamsChange(...arguments);
    // }

    render(){
        const { handleInputChange, index, panelColumn,inputSearchParams } = this.props;
        const currentOpt = inputSearchParams[index];
        const originOptionType = this.originOption('originType',currentOpt&&currentOpt.originType||[]);
        const topicOptionType = this.originOption('currencyType',currentOpt&&currentOpt.topic||[]);
        const eventTimeOptionType = this.originOption('eventTime',panelColumn[index].columns)
        const mysqlOptionType = this.originOption('currencyType',mysqlFieldTypes)
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
                                    originOptionType
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
                                    topicOptionType
                                }
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
                            rules: [
                                {required: true, message: '请输入Table',}
                            ],
                        })(
                            <Input  placeholder="请输入Table" className="right-input" onChange={e => handleInputChange('table',index,e.target.value)}/>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="字段"
                    >
                    </FormItem>
                    {/* <Col style={{marginBottom: 20}}>
                        <Editor 
                             key="params-editor"
                             //sync={this._syncEditor}
                             options={jsonEditorOptions}
                             value={panelColumn[index].columns}
                             onChange={this.editorParamsChange.bind(this)}
                        />
                    </Col> */}
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
                                {getFieldDecorator('timeColum', {
                                    rules: [
                                        {required: true, message: '请选择时间列',}
                                    ],
                                })(
                                    <Select placeholder="请选择" className="right-select" onChange={(v)=>{handleInputChange("timeColum",index,v)}}>
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
                                label="偏移量(毫秒)"
                            >
                                {getFieldDecorator('offset', {
                                    rules: [
                                        {required: true, message: '请输入时间偏移量'},
                                        // { validator: this.checkConfirm }
                                    ],
                                })(
                                    <InputNumber className="number-input" onChange={value => handleInputChange('offset',index,value)}/>
                                )}
                            </FormItem>: undefined
                    }
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
            const { type, sourceId, topic, table, model, columns, timeType, timeColum, offset } = props.panelColumn[props.index];
            return {
                type: { value: type },
                sourceId: { value: sourceId },
                topic: { value: topic },
                table: { value: table },
                model: { value: model },
                columns: { value: columns },
                timeType: { value: timeType },
                timeColum: { value: timeColum },
                offset: { value: offset}
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
    inputSearchParams: [],//所有输入选择的参数
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
        source.map( v => {
            tabTemplate.push(InputForm);
            panelColumn.push(v);
        })
        this.setCurrentSource({tabTemplate,panelColumn})
        this.setState({
            tabTemplate,
            panelColumn
        })
    }

    getTypeOriginData = (index,type) => {
        const { inputSearchParams } = this.state;
        const selectData = { topic: [], originType: [] };
        Api.getTypeOriginData({type}).then(v=>{
            if(v.code===1){
                if(index === "add"){
                    selectData.originType = v.data;
                    inputSearchParams.push(selectData);
                }else{
                    inputSearchParams[index].originType = v.data;
                }
            }else{
                if(index === "add"){
                    inputSearchParams.push(selectData);
                }
            }
            this.setCurrentSource({inputSearchParams});
            this.setState({
                inputSearchParams
            })
        })
    }

    getTopicType = (index,sourceId) => {
        const { inputSearchParams } = this.state;
        const selectData = inputSearchParams[index];
        Api.getTopicType({sourceId}).then(v=>{
            if(v.code===1){
                selectData.topic = v.data
                this.setState({
                    inputSearchParams
                })
            }
        })
    }

    getCurrentData = (taskId,nextProps) => {
        const { dispatch,inputData,currentPage } = nextProps;
        const { source } = currentPage;
        if(!inputData[taskId]&&source.length>0){
            this.receiveState(taskId,source,dispatch)
        }else{
            const copyInitialData = JSON.parse(JSON.stringify(initialData));
            const data = inputData[taskId]||copyInitialData;
            this.setState({...data})
        }
    }

    receiveState = (taskId,source,dispatch) => {
        const copyInitialData = JSON.parse(JSON.stringify(initialData));
        const {tabTemplate,panelColumn} = copyInitialData;
        source.map( v => {
            tabTemplate.push(InputForm);
            panelColumn.push(v);
        })
        dispatch(BrowserAction.setInputData({taskId ,source: copyInitialData}));
        this.setState({
            copyInitialData
        })
    }

    componentWillReceiveProps(nextProps) {
        const currentPage = nextProps.currentPage
        const oldPage = this.props.currentPage
        if (currentPage.id !== oldPage.id) {
            this.getCurrentData(currentPage.id,nextProps)
        }
    }

    changeInputTabs = (type,index) =>{
        const inputData = {
            type: "14",
            sourceId: undefined,
            topic: undefined,
            table: undefined,
            model: 1,
            columns: [],
            timeType: 1,
            timeColum: undefined,
            offset: 0
            // alias: undefined,
        }
        
        let { tabTemplate, panelActiveKey, popoverVisible, panelColumn, checkFormParams ,inputSearchParams } = this.state;
        if(type==="add"){
            tabTemplate.push(InputForm);
            panelColumn.push(inputData);
            this.getTypeOriginData("add",inputData.type);
            let pushIndex = `${tabTemplate.length}`;
            panelActiveKey.push(pushIndex)
        }else{
            tabTemplate.splice(index,1);
            panelColumn.splice(index,1);
            inputSearchParams.splice(index,1);
            checkFormParams.pop();
            panelActiveKey = this.changeActiveKey(index);
            popoverVisible[index] = false;
        }
        this.setCurrentSource({tabTemplate,panelActiveKey,popoverVisible,panelColumn,inputSearchParams});
        this.setState({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams,
            inputSearchParams
        })
    }

    setCurrentSource = (data) => {
        const { dispatch, currentPage } = this.props;
        console.log("inputPanel-setCurrentSource:",this.props);
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
        console.log('checkFormParams-ref:',ref);
        
        checkFormParams.push(ref);
        this.setCurrentSource({checkFormParams})
        this.setState({
            checkFormParams
        })
    }


    render() {
        const { tabTemplate,panelActiveKey,panelColumn,inputSearchParams } = this.state;
        return (
            <div className="m-taksdetail panel-content">
                <Collapse activeKey={panelActiveKey}  onChange={this.handleActiveKey} className="input-panel">
                    {
                        tabTemplate.map( (InputPutOrigin,index) => {
                            return  (
                                <Panel header={this.panelHeader(index)} key={index+1} style={{borderRadius: 5}}>
                                    <InputForm index={index} key={index+1} handleInputChange={this.handleInputChange} panelColumn={panelColumn} inputSearchParams={inputSearchParams} onRef={this.recordForm}/>
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

