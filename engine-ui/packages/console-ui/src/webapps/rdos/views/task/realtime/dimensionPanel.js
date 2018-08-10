import React, { Component } from 'react'
import {
    Row, Col, Modal, Tag, Icon,Tooltip,Table,Input,
    message, Select, Collapse, Button,Radio,Popover,
    Form,InputNumber
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
            case "columnType":
               return arrData.map((v,index)=>{
                    return  <Option key={index} value={`${v.key}`}>{v.key}</Option>
                }) 
            case "primaryType":
                return arrData.map((v,index)=>{
                    return  <Option key={index} value={`${v.column}`}>{v.column}</Option>
                }) 
            default:
                return null;
        }
    }

    render(){
        const { handleInputChange,index, originOptionType,tableOptionType,panelColumn,tableColumnOptionType } = this.props;
        const { getFieldDecorator } = this.props.form;
        const originOptionTypes = this.originOption('originType',originOptionType[index]||[]);
        const tableOptionTypes = this.originOption('currencyType',tableOptionType[index]||[]);
        const tableColumnOptionTypes = this.originOption('columnType',tableColumnOptionType[index]||[]);
        const primaryKeyOptionTypes = this.originOption('primaryType',panelColumn[index].columns||[]);
        const formItemLayout = {
            labelCol: {
              xs: { span: 24 },
              sm: { span: 6 },
            },
            wrapperCol: {
              xs: { span: 24 },
              sm: { span: 18 },
            },
        };
        return (
            <Row className="title-content">
                <FormItem
                    {...formItemLayout}
                    label="存储类型"
                >
                    {getFieldDecorator('type', {
                        rules: [
                            {required: true, message: '请选择存储类型',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("type",index,v)}}
                            showSearch filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                                <Option value="1">MySQL</Option>
                                {/* <Option value="8">HBase</Option>
                                <Option value="11">ElasticSearch</Option> */}
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
                        <Select className="right-select" onChange={(v)=>{handleInputChange("sourceId",index,v)}}
                            showSearch filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                               {
                                   originOptionTypes
                               }
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="表"
                >
                    {getFieldDecorator('table', {
                        rules: [
                            {required: true, message: '请选择表',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("table",index,v)}}
                            showSearch filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                                {
                                    tableOptionTypes
                                }
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="映射表"
                >
                    {getFieldDecorator('tableName')(
                        <Input  placeholder="请输入映射表名" onChange={e => handleInputChange('tableName',index,e.target.value)}/>
                    )}
                </FormItem>
                <Row>
                    <Col span="6">
                        <span style={{color: "rgba(0, 0, 0, 0.85)",paddingRight: 10,float: "right"}}>字段 : 
                    </span>
                    </Col>
                    <Col span="18" style={{marginBottom: 20,border: "1px solid #ddd"}}>
                        <Table dataSource={panelColumn[index].columns} className="table-small" pagination={false} size="small" >
                            <Column
                                title="字段"
                                dataIndex="column"
                                key="字段"
                                width='50%'
                                render={(text,record,subIndex)=>{ 
                                    return  <Select className="sub-right-select" value={text} onChange={(v)=>{handleInputChange("subColumn",index,subIndex,v)}}
                                                showSearch filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                            >
                                                {
                                                    tableColumnOptionTypes
                                                }
                                            </Select>
                                }}
                            />
                            <Column
                                title="类型"
                                dataIndex="type"
                                key="类型"
                                width='40%'
                                render={(text,record,subIndex)=>{ 
                                    return <Input value={text} disabled/>
                                }}
                            />
                            <Column
                                key="delete"
                                render={(text,record,subIndex)=>{return <Icon type="close" style={{fontSize: 16,color: "#888"}} onClick={()=>{handleInputChange("deleteColumn",index,subIndex)}}/>}}
                            />
                        </Table>
                        <div style={{padding: "0 20 20"}}>
                            <Button className="stream-btn" type="dashed" style={{borderRadius: 5}} onClick={()=>{handleInputChange("columns",index,{})}}>
                                <Icon type="plus" /><span> 添加输入</span>
                            </Button>
                        </div>
                    </Col>
                </Row>
                <FormItem
                    {...formItemLayout}
                    label="主键"
                >
                    {getFieldDecorator('primaryKey',{
                        rules: [
                            {required: true, message: '请选择主键',}
                        ],
                    })(
                        <Select className="right-select" onChange={(v)=>{handleInputChange("primaryKey",index,v)}} mode="multiple"
                            showSearch filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                            {
                                primaryKeyOptionTypes
                            }
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="并行度"
                >
                    {getFieldDecorator('parallelism')(
                        <InputNumber className="number-input" min={1} onChange={value => handleInputChange('parallelism',index,value)}/>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="缓存策略"
                >
                    {getFieldDecorator('cache', {
                        rules: [
                            {required: true, message: '请选择缓存策略',}
                        ],
                    })(
                        <Select placeholder="请选择" className="right-select" onChange={(v)=>{handleInputChange("cache",index,v)}}
                            showSearch filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                                <Option key="None" value="None">None</Option>
                                <Option key="LRU" value="LRU">LRU</Option>
                        </Select>
                    )}
                </FormItem>
                {
                    panelColumn[index].cache === "LRU" ? 
                        <FormItem
                            {...formItemLayout}
                            label="缓存大小(行)"
                        >
                            {getFieldDecorator('cacheSize', {
                                rules: [
                                    {required: true, message: '请输入缓存大小'},
                                    // { validator: this.checkConfirm }
                                ],
                            })(
                                <InputNumber className="number-input" min={0} onChange={value => handleInputChange('cacheSize',index,value)}/>
                            )}
                        </FormItem>: undefined
                }
                {
                    panelColumn[index].cache === "LRU" ? 
                        <FormItem
                            {...formItemLayout}
                            label="缓存超时时间(ms)"
                        >
                            {getFieldDecorator('cacheTTLMs', {
                                rules: [
                                    {required: true, message: '请输入缓存超时时间'},
                                    // { validator: this.checkConfirm }
                                ],
                            })(
                                <InputNumber className="number-input" min={0} onChange={value => handleInputChange('cacheTTLMs',index,value)}/>
                            )}
                        </FormItem>: undefined
                }
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
            const { type, sourceId, table, columns, parallelism, cache, cacheSize, cacheTTLMs, tableName, primaryKey } = props.panelColumn[props.index];
            return {
                type: { value: type },
                sourceId: { value: sourceId },
                table: { value: table },
                tableName: { value: tableName },
                columns: { value: columns },
                parallelism: { value: parallelism },
                cache: { value: cache },
                cacheSize: { value: cacheSize },
                cacheTTLMs: { value: cacheTTLMs },
                primaryKey: { value: primaryKey },
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
    originOptionType: [],//数据源选择数据
    tableOptionType: [],//表选择数据
    tableColumnOptionType: [],//表字段选择的类型
}

export default class OutputPanel extends Component {

    constructor(props) {
        super(props)
        this.state =  {
            popoverVisible: false,
            tabTemplate: [],//模版存储,所有输出源(记录个数)
            panelActiveKey: [],//输出源是打开或关闭状态
            popoverVisible: [],//删除显示按钮状态
            panelColumn: [],//存储数据
            checkFormParams: [],//存储要检查的参数from
            originOptionType: [],//数据源选择数据
            tableOptionType: [],//表选择数据
            tableColumnOptionType: [],//表字段选择的类型
        };
    }
    
    componentDidMount(){
        const { side } = this.props.currentPage;
        if(side&&side.length>0){
            this.currentInitData(side)
        }
    }

    currentInitData = (side) => {
        const {tabTemplate,panelColumn} = this.state;
        side.map( (v,index) => {
            tabTemplate.push(OutputForm);
            panelColumn.push(v);
            this.getTypeOriginData(index,v.type);
            this.getTableType(index,v.sourceId);
            this.getTableColumns(index,v.sourceId,v.table)
        })
        this.setOutputData({ tabTemplate, panelColumn })
        this.setState({
            tabTemplate,
            panelColumn
        })
    }

    getCurrentData = (taskId,nextProps) => {
        const { currentPage,dimensionData,dispatch } = nextProps;
        const { side } = currentPage;
        if(!dimensionData[taskId]&&side.length>0){
            this.receiveState(taskId,side,dispatch)
        }else{
            const copyInitialData = JSON.parse(JSON.stringify(initialData));
            const data = dimensionData[taskId]||copyInitialData;
            this.setState({...data})
        }
    }

    receiveState = (taskId,side,dispatch) => {
        const tabTemplate = [];
        const panelColumn = [];
        const panelActiveKey = [];
        const popoverVisible = [];
        const checkFormParams = [];
        const originOptionType = [];
        const tableOptionType = [];
        const tableColumnOptionType = []
        side.map( v => {
            tabTemplate.push(OutputForm);
            panelColumn.push(v);
        })
        dispatch(BrowserAction.setDimensionData({taskId ,side: {tabTemplate,panelColumn,panelActiveKey,popoverVisible,checkFormParams,originOptionType,tableOptionType,tableColumnOptionType}}));
        this.setState({
            tabTemplate,panelColumn,panelActiveKey,popoverVisible,checkFormParams,originOptionType,tableOptionType,tableColumnOptionType
        },()=>{
            side.map((v,index)=>{
                this.getTypeOriginData(index,v.type)
                this.getTableType(index,v.sourceId)
                this.getTableColumns(index,v.sourceId,v.table)
            })
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
            this.setOutputData({originOptionType});
            this.setState({
                originOptionType
            })
        })
    }

    getTableType = (index,sourceId,type) => {
        const { tableOptionType } = this.state;
        if(sourceId){
            Api.getStremTableType({sourceId,"isSys":false}).then(v=>{
                if(index==='add'){
                    if(v.code===1){
                        tableOptionType.push(v.data) 
                    }else{
                        tableOptionType.push([]) 
                    }
                }else{
                    if(v.code===1){
                        tableOptionType[index] = v.data;
                    }else{
                        tableOptionType[index] = [];
                    }
                }
                this.setOutputData({tableOptionType});
                this.setState({
                    tableOptionType
                })
            })
        }else{
            if(index="add"){
                tableOptionType.push([]);
            }else{
                tableOptionType[index] = [];
            }
            this.setOutputData({tableOptionType});
            this.setState({
                tableOptionType
            })
        }
    }

    getTableColumns = (index,sourceId,tableName) => {
        const { tableColumnOptionType } = this.state;
        Api.getStreamTableColumn({sourceId,tableName}).then(v=>{
            if(v.code === 1){
               tableColumnOptionType[index] = v.data;
            }else{
               tableColumnOptionType[index] = []
            }
            this.setOutputData({tableColumnOptionType})
            this.setState({
                tableColumnOptionType
            })
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
            type: "1",
            columns: [],
            sourceId: undefined,
            table: undefined,
            tableName: undefined,
            primaryKey: undefined,
            parallelism: 1,
            cache: "LRU",
            cacheSize: 10000,
            cacheTTLMs: 60000,
        }
        let { tabTemplate, panelActiveKey, popoverVisible, panelColumn, checkFormParams, originOptionType,tableOptionType,tableColumnOptionType } = this.state;
        if(type==="add"){
            tabTemplate.push(OutputForm);
            panelColumn.push(inputData);
            this.getTypeOriginData("add",inputData.type);
            this.getTableType('add',inputData.table);
            tableColumnOptionType.push([]);
            let pushIndex = `${tabTemplate.length}`;
            panelActiveKey.push(pushIndex)
        }else{
            tabTemplate.splice(index,1);
            panelColumn.splice(index,1);
            originOptionType.splice(index,1);
            tableOptionType.splice(index,1);
            tableColumnOptionType.splice(index,1);
            checkFormParams.pop();
            panelActiveKey = this.changeActiveKey(index);
            popoverVisible[index] = false;
        }
        this.props.tableParamsChange()//添加数据改变标记
        this.setOutputData({tabTemplate,panelActiveKey,popoverVisible,panelColumn,checkFormParams,originOptionType,tableOptionType,tableColumnOptionType});
        this.setState({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams,
            originOptionType,
            tableOptionType,
            tableColumnOptionType,
        })
    }

    setOutputData = (data) => {
        const { dispatch, currentPage } = this.props;
        const dispatchSource = {...this.state,...data};
        dispatch(BrowserAction.setDimensionData({taskId: currentPage.id ,side: dispatchSource}));
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

    tableColumnType = (index,column) => {
        const { tableColumnOptionType } = this.state;
        const filterColumn = tableColumnOptionType[index].filter(v=>{
            return v.key === column
        })
        return filterColumn[0].type
    }

    filterPrimaryKey = (columns,primaryKeys) => {//删除导致原始的primaryKey不存在
        return primaryKeys.filter(v=>{
            let flag = false;
            columns.map(value=>{
                if(value.column === v){
                    flag = true
                }
            })
            return flag;
        })
    }

    handleInputChange = (type,index,value,subValue) => {//监听数据改变
        const { panelColumn, originOptionType, tableOptionType, tableColumnOptionType } = this.state;
        if(type === 'columns'){
            panelColumn[index][type].push(value);
        }else if(type === "deleteColumn"){
            panelColumn[index]["columns"].splice(value,1);
            const filterPrimaryKeys = this.filterPrimaryKey(panelColumn[index]["columns"],panelColumn[index].primaryKey||[]);
            panelColumn[index].primaryKey = filterPrimaryKeys;
        }else if(type ==="subColumn"){
            panelColumn[index]["columns"][value].column = subValue;
            const subType = this.tableColumnType(index,subValue);
            panelColumn[index]["columns"][value].type = subType;
        }else{
            panelColumn[index][type] = value;
        }
        if(type==="type"){
            originOptionType[index] = [];
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            panelColumn[index].columns = [];
            panelColumn[index]["sourceId"] = undefined;
            panelColumn[index]["table"] = undefined;
            panelColumn[index]["tableName"] = undefined;
            panelColumn[index]["primaryKey"] = undefined;
            panelColumn[index]["parallelism"] = 1;
            panelColumn[index]["cache"] = 'LRU';
            panelColumn[index]["cacheSize"] = 10000;
            panelColumn[index]["cacheTTLMs"] = 60000;
            //this.clearCurrentInfo(type,index,value)
            this.getTypeOriginData(index,value);
        }else if(type==="sourceId"){
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            panelColumn[index].columns = [];
            panelColumn[index]["table"] = undefined;
            panelColumn[index]["tableName"] = undefined;
            panelColumn[index]["primaryKey"] = undefined;
            //this.clearCurrentInfo(type,index,value)
            this.getTableType(index,value,type)
        }else if (type==="table"){
            tableColumnOptionType[index] = [];
            const { sourceId } = panelColumn[index];
            panelColumn[index].columns = [];
            panelColumn[index]["primaryKey"] = undefined;
            panelColumn[index]["parallelism"] = 1;
            panelColumn[index]["cache"] = 'LRU';
            panelColumn[index]["cacheSize"] = 10000;
            panelColumn[index]["cacheTTLMs"] = 60000;
            this.getTableColumns(index,sourceId,value)
        }
        this.props.tableParamsChange()//添加数据改变标记
        this.setOutputData({panelColumn})
        this.setState({
            panelColumn,
        })
    }

    clearCurrentInfo = (type,index,value) => {
        const { panelColumn, originOptionType, tableOptionType } = this.state;
        const inputData = {
            type: undefined,
            columns: [],
            sourceId: undefined,
            table: undefined,
            tableName: undefined,
            parallelism: 1,
            cache: "LRU",
            cacheSize: 10000,
            cacheTTLMs: 60000,
        }
        if(type==="type"){
            inputData.type = value;
            panelColumn[index] = inputData;
            originOptionType[index] = [];
            tableOptionType[index] = [];
        }else if(type==="sourceId"){
            inputData.type = panelColumn[index]['type']
            inputData.sourceId = value;
            panelColumn[index] = inputData;
            tableOptionType[index] = [];
        }
        this.setOutputData({panelColumn,originOptionType,tableOptionType})
        this.setState({panelColumn,originOptionType,tableOptionType});
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
            <span>{` 维表 ${index+1}`}</span>
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
        const { tabTemplate,panelActiveKey,panelColumn,originOptionType,tableOptionType,tableColumnOptionType } = this.state;
        return (
            <div className="m-taksdetail panel-content">
                <Collapse activeKey={panelActiveKey} bordered={false} onChange={this.handleActiveKey}>
                    {
                        tabTemplate.map( (OutputPutOrigin,index) => {
                            return  (
                                <Panel header={this.panelHeader(index)} key={index+1} style={{borderRadius: 5}} className="input-panel">
                                    <OutputForm 
                                        index={index} 
                                        handleInputChange={this.handleInputChange}
                                        panelColumn={panelColumn} originOptionType={originOptionType} 
                                        tableOptionType = {tableOptionType}
                                        tableColumnOptionType = {tableColumnOptionType}
                                        onRef={this.recordForm}
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

