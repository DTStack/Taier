import React, { Component } from "react"
import { Input, Icon, Button, Checkbox, Select, Row, Card, Col, Table ,message} from "antd";
import { cloneDeep } from 'lodash';
const TextArea = Input.TextArea;
const Option = Select.Option;
class OutputParams extends Component {
    state = {
        loading: false,
        tableColumns: [],
        dataSource: [
            
        ],
        
    }
    componentDidMount() {
        this.getTableColumns();
    }
    componentWillReceiveProps(nextProps) {
        if (nextProps.addOutputsignal && !this.props.addOutputsignal) {
            this.addOutput(nextProps.inputToOutputData);
        }
        //input参数不等，添加新的值
        // if(this.props.inputParam!=nextProps.inputParam){
        //     this.addNewInputParam(nextProps.inputParam);
        // }
    }
     //通知父组件最新的值
     backMsg(table) {
        let arr = [];
        for (let i in table) {
            let item = table[i];
            if (item.tmp) {//假如存在回滚数据，则返回tmp
                arr.push(item.tmp)
            } else if (!item.isEdit) {//不存在回滚，并且不处于编辑状态，则返回item
                
                arr.push(item);
            } 
        }
        this.props.outputParamsChange(arr);
    }
    addOutput(inputToOutputData){
        const table = cloneDeep(this.state.dataSource);
        let dic={
            key: Math.random(),
            param: {},
            paramName: "",
            operators: '',
            isRequired: true,
            instructions: "",
            isEdit: true
        }
        if(inputToOutputData){
            
            console.log(inputToOutputData)
            dic.param=inputToOutputData.param;
            dic.paramName=inputToOutputData.paramName;
            dic.operators=inputToOutputData.operators;
            dic.isRequired=inputToOutputData.isRequired;
            dic.instructions=inputToOutputData.instructions;

        }
        table.unshift(dic);
        
        this.setState({
            dataSource: table
        }
            , () => {
                this.props.changeAddoutputOverSignal();
            })
    }
    getTableColumns() {
        this.props.tablecolumn(this.props.dataSourceId, this.props.tableId)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            tableColumns: res.data
                        })
                    }
                }
            )
    }
    getColumnsView() {
        const data = cloneDeep(this.state.tableColumns);
        return data.map(
            (item) => {
                return (<Option key={item.key} value={item.type + "@@" + item.key}>{item.key}</Option>)
            }
        )
    }
    //字段改变
    changeTableParam(index, key) {
        key = key.split("@@");
        if (!key) {
            return;
        }
        const table = cloneDeep(this.state.dataSource);
        table[index].param = {
            type: key[0],
            key: key[1]
        }
        this.setState({
            dataSource: table
        })
    }
    //选择框改变
    setRequired(index,e){
        const table = cloneDeep(this.state.dataSource);
        table[index].isRequired = e.target.checked
        this.setState({
            dataSource: table
        })
    }
    //编辑说明
    textAreaChange(index,e){
        const value=e.target.value;
       
        const table = cloneDeep(this.state.dataSource);
        table[index].instructions = value
        this.setState({
            dataSource: table
        })
    }
    //参数名
    paramNameChange(index,e){
        const value=e.target.value;
       
        const table = cloneDeep(this.state.dataSource);
        table[index].paramName = value
        this.setState({
            dataSource: table
        })
    }
    //编辑操作符
    operatorsChange(index,value){
        const table = cloneDeep(this.state.dataSource);
        table[index].operators = value
        this.setState({
            dataSource: table
        })
    }
    //验证数据合法性
    checkInfo(index,isTmp){
        const table = cloneDeep(this.state.dataSource);
        let checkItem=table[index];
        if(isTmp){
            checkItem=checkItem.tmp;
        }
        if(!checkItem.param.key){
            message.error("请选择字段");
            return false;
        }
       
        for(let i in table){
            if(i==index){
                continue;
            }
            if(table[i].param.key==checkItem.param.key){
                if(isTmp){
                    message.error("原数据与现有数据重复，请修改保存");
                    
                }else{
                    message.error("不能设置相同字段")
                }
                
                return false;
            }
        }
        return true;
    }
    //保存信息
    saveInfo(index) {
        
        const table = cloneDeep(this.state.dataSource);
        if(!this.checkInfo(index)){
            return;
        }
        table[index].isEdit = false;
        if (table[index].tmp) {
            table[index].tmp = null;
        }
        this.backMsg(table);
        this.setState({
            dataSource: table
        })
    }
    //取消保存，假如是已有信息，则回滚，新增的则直接删除
    cancelSave(index) {
        const table = cloneDeep(this.state.dataSource);

        if (table[index].tmp) {
            //回滚时候，需要确认不会重复
            if(!this.checkInfo(index,true)){
                return;
            }
            table[index] = cloneDeep(table[index].tmp);
            table[index].isEdit=false;
        } else {
            table.splice(index, 1);
        }
        this.setState({
            dataSource: table
        })
    }
    //编辑操作
    editInfo(index) {
        const table = cloneDeep(this.state.dataSource);

        table[index].tmp = cloneDeep(table[index]);
        table[index].isEdit=true;

        this.setState({
            dataSource: table
        })
    }
    //删除
    removeInfo(index){
        const table = cloneDeep(this.state.dataSource);
        table.splice(index, 1);
        this.backMsg(table);
        this.setState({
            dataSource: table
        })
    }

    initColumns() {

        return [{
            title: '字段',
            dataIndex: 'param',
            key: 'param',
            width: "100px",
            render: (text, record, index) => {
                if (record.isEdit) {
                    return (
                        <Select defaultValue={record.param.type?(record.param.type+"@@"+record.param.key):null} onChange={this.changeTableParam.bind(this, index)} style={{ width: "100%" }} >
                            {this.getColumnsView()}

                        </Select>
                    )
                }
                return record.param.key;

            }

        }, {
            title: '参数名',
            dataIndex: 'paramName',
            key: 'paramName',
            width: "100px",
            render:(text, record,index)=> {
                if (record.isEdit) {
                    return (
                        <Input onBlur={this.paramNameChange.bind(this,index)} defaultValue={record.paramName} />
                    )
                }
                return record.paramName;

            }
        }, {
            title: '数据类型',
            dataIndex: 'dataType',
            key: 'dataType',
            render(text, record) {
                return record.param.type || "";
            }

        }, 
        
        {
            title: '说明',
            dataIndex: 'instructions',
            key: 'instructions',
            render:(text, record,index)=>{
                if (record.isEdit) {
                    return (<TextArea onBlur={this.textAreaChange.bind(this,index)} defaultValue={record.instructions} />)
                }
                return (<TextArea key="textareadisabled" defaultValue={record.instructions} disabled />)

            }
        },
        {
            title: '操作',
            dataIndex: 'deal',
            key: 'deal',
            render: (text, record, index) => {
                if (record.isEdit) {
                    return (
                        <div>
                            <a onClick={this.saveInfo.bind(this, index)}>保存</a>
                            <span className="ant-divider"></span>
                            <a onClick={this.cancelSave.bind(this, index)}>取消</a>
                        </div>
                    )
                }
                return (
                    <div>
                        <a onClick={this.editInfo.bind(this, index)}>编辑</a>
                        <span className="ant-divider"></span>
                        <a onClick={this.removeInfo.bind(this, index)}>删除</a>
                    </div>
                )

            }
        }]
    }

    getPagination() {
        return {
            current: this.state.pageIndex,
            pageSize: 20,
            total: 30,
        }
    }
    pass() {
        this.props.dataChange({
            ...values
        })

    }

    render() {
        return (

            <Table
                className="m-table monitor-table"
                columns={this.initColumns()}
                loading={this.state.loading}
                pagination={false}
                dataSource={this.state.dataSource}

            />

        )
    }
}

export default OutputParams