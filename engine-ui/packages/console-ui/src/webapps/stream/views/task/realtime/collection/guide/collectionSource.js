import React from "react";
import moment from "moment";

import { Form, Select, Radio, Checkbox, DatePicker, Input, Button } from "antd";

import { formItemLayout, DATA_SOURCE_TEXT, DATA_SOURCE, CAT_TYPE, collect_type } from "../../../../../comm/const"

import ajax from "../../../../../api/index"

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const CheckboxGroup = Checkbox.Group;



class CollectionSource extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            tableList: [],
            binLogList:[]
        }
    }
    componentDidMount() {
        const { collectionData } = this.props;
        const { sourceMap = {} } = collectionData;
        if (sourceMap.sourceId) {
            this.getTableList(sourceMap.sourceId)
            this.getBinLogList(sourceMap.sourceId);
        }
    }

    componentWillReceiveProps(nextProps) {
        const { collectionData } = nextProps;
        const { sourceMap } = collectionData;
        const { collectionData: old_col } = this.props;
        const { sourceMap: old_source } = old_col;
        if(collectionData.id!=old_col.id){
            this.setState({
                tableList:[],
                binLogList:[]
            })
        }
        if (sourceMap.sourceId && old_source.sourceId != sourceMap.sourceId) {
            this.getTableList(sourceMap.sourceId)
        }
        /**
         * 当collectType是File，并且此时collectType发生过改变或者tab的id发生了改变，则去请求binlog列表
         */
        if(
            (sourceMap.collectType!=old_source.collectType||collectionData.id!=old_col.id)
            &&sourceMap.collectType==collect_type.FILE){
            this.getBinLogList(sourceMap.sourceId);
        }
    }

    getTableList(sourceId) {
        ajax.getStreamTablelist({
            sourceId,
            isSys: false
        }).then(res => {
            if (res.code === 1) {
                this.setState({
                    tableList: res.data || []
                });
            }
        });
    }
    getBinLogList(sourceId){
        ajax.getBinlogListBySource({sourceId})
        .then((res)=>{
            if(res.code==1){
                this.setState({
                    binLogList:res.data
                })
            }
        })
    }
    clearBinLog(){
        this.setState({
            binLogList:[]
        })
    }
    next(){
        this._form.validateFields(null,{},(err,values)=>{
            if(!err){
                this.props.navtoStep(1)
            }
        })
    }
    render() {
        const { tableList, binLogList } = this.state;
        return (
            <div>
                <WrapCollectionSourceForm ref={(f)=>{this._form=f}} binLogList={binLogList} tableList={tableList} {...this.props} />
                {!this.props.readonly && (
                    <div className="steps-action">
                        <Button type="primary" onClick={() => this.next()}>下一步</Button>
                    </div>
                )}
            </div>
        )
    }
}

class CollectionSourceForm extends React.Component {
    renderByCatType() {
        const { collectionData, form, binLogList } = this.props;
        const { getFieldDecorator } = form;
        const { sourceMap, isEdit } = collectionData;
        const collectType = sourceMap.collectType
        switch (collectType) {
            case collect_type.ALL: {
                return null
            }
            case collect_type.TIME: {
                return <FormItem
                    {...formItemLayout}
                    label="起始时间"
                    style={{ textAlign: "left" }}
                >
                    {getFieldDecorator('timestamp', {
                        rules: [{
                            required: true, message: "请选择起始时间"
                        }]
                    })(
                        <DatePicker
                            disabled={isEdit}
                            showTime
                            placeholder="请选择起始时间"
                            format="YYYY-MM-DD HH:mm:ss"
                        />
                    )}
                </FormItem>
            }
            case collect_type.FILE: {
                return <FormItem
                    {...formItemLayout}
                    label="起始文件"
                >
                    {getFieldDecorator('journalName', {
                        rules: [{
                            required: true, message: "请填写起始文件"
                        }]
                    })(
                        <Select
                        placeholder="请填写起始文件"
                        disabled={isEdit}
                        >
                            {binLogList.map((binlog)=>{
                                return <Option key={binlog}>{binlog}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
            }

        }
    }
    render() {
        let { collectionData, tableList } = this.props;
        let { dataSourceList = [],sourceMap, isEdit } = collectionData;
        const { getFieldDecorator } = this.props.form;
        const allTable=sourceMap.allTable;
        return (
            <div>
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="数据源"
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [{ required: true, message: '请选择数据源' }],
                        })(
                            <Select
                                disabled={isEdit}
                                placeholder="请选择数据源"
                                style={{ width: "100%" }}
                            >
                                {dataSourceList.map((item) => {
                                    if (item.type != DATA_SOURCE.MYSQL) {
                                        return null
                                    }
                                    return <Option key={item.id} value={item.id}>{item.dataName}({DATA_SOURCE_TEXT[item.type]})</Option>
                                }).filter(Boolean)}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="表"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true, message: "请选择表"
                            }]
                        })(
                            <Select
                                mode="multiple"
                                style={{ width: '100%' }}
                                placeholder="请选择表"

                            >   
                                {tableList.length?[<Option key={-1} value={-1}>全部</Option>].concat(tableList.map(
                                    (table) => {
                                        return <Option disabled={allTable} key={`${table}`} value={table}>
                                            {table}
                                        </Option>
                                    }
                                )):[]}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="采集起点"
                        style={{ textAlign: "left" }}
                    >
                        {getFieldDecorator('collectType', {
                            rules: [{
                                required: true, message: "请选择采集起点"
                            }]
                        })(
                            <RadioGroup disabled={isEdit}>
                                <Radio value={collect_type.ALL}>全部</Radio>
                                <Radio value={collect_type.TIME}>按时间选择</Radio>
                                <Radio value={collect_type.FILE}>按文件选择</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    {this.renderByCatType()}
                    <FormItem
                        {...formItemLayout}
                        label="数据操作"
                    >
                        {getFieldDecorator('cat', {
                            rules: [{
                                required: true, message: "请选择数据操作"
                            }]
                        })(
                            <CheckboxGroup options={
                                [{ label: 'Insert', value: CAT_TYPE.INSERT },
                                { label: 'Update', value: CAT_TYPE.UPDATE },
                                { label: 'Delete', value: CAT_TYPE.DELETE }]
                            }
                            />
                        )}
                    </FormItem>
                </Form>
            </div>
        )
    }
}

const WrapCollectionSourceForm = Form.create({
    onValuesChange(props, fields) {
        /**
         * sourceId改变,则清空表
         */
        let clear = false;
        if (fields.sourceId != undefined) {
            clear = true
        }
        /**
         * moment=>时间戳,并且清除其他的选项
         */
        if(fields.timestamp){
            fields.timestamp=fields.timestamp.valueOf()
            fields.journalName=null;
        }
        if(fields.journalName){
            fields.timestamp=null;
        }
        if(fields.collectType!=undefined&&fields.collectType==collect_type.ALL){
            fields.journalName=null;
            fields.timestamp=null;
        }
        /**
         * 改变table的情况
         * 1.包含全部，则剔除所有其他选项，设置alltable=true
         * 2.不包含全部，设置alltable=false
         */
        if(fields.table){
            if(fields.table.includes(-1)){
                fields.table=[];
                fields.allTable=true;
            }else{
                fields.allTable=false;
            }
        }
        props.updateSourceMap(fields, clear);
    },
    mapPropsToFields(props) {
        const { collectionData } = props;
        const sourceMap = collectionData.sourceMap;
        return {
            sourceId: {
                value: sourceMap.sourceId
            },
            table: {
                value: sourceMap.allTable?-1:sourceMap.table
            },
            collectType: {
                value: sourceMap.collectType
            },
            cat: {
                value: sourceMap.cat
            },
            timestamp: {
                value: sourceMap.timestamp?new moment(sourceMap.timestamp):undefined
            },
            journalName: {
                value: sourceMap.journalName
            }
        }

    }
})(CollectionSourceForm);

export default CollectionSource;