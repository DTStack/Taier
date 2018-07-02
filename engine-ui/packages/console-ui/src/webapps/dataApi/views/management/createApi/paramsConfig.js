import React, { Component } from "react"
import { Input, Icon, Button, Checkbox, Select, Form, Table, message, Card } from "antd";
import {cloneDeep} from "lodash";

import ColumnsConfig from "./params/columnsConfig"
import ColumnsModel from "../../../model/columnsModel"
import ApiSqlEditor from "./sql"
import { API_MODE } from "../../../consts"
import { RDOS_BASE_URL } from "../../../../../config/base";
import { API_STATUS } from "../../../../dataLabel/consts";

const TextArea = Input.TextArea;
const Option = Select.Option;
const FormItem = Form.Item;


class ManageParamsConfig extends Component {

    state = {
        tableData: [],
        dataSourceList: [],
        tableList: [],
        InputColumns: [],
        OutputColums: [],
        selectedRows: [],
        isEdit: false,
        resultPageChecked: false,
        resultPage: undefined,
        sqlModeShow: true,
        editor: {
            sql: "",
            cursor: undefined,
            sync: true
        }

    }

    componentWillMount() {
        const { tableName, dataSrcId, inputParam, outputParam, resultPageChecked, resultPage, mode, sql } = this.props;
        this.setState({
            InputColumns: inputParam || [],
            OutputColums: outputParam || [],
            resultPageChecked: resultPageChecked,
            resultPage: resultPage,
            isEdit: API_MODE.SQL == mode ? true : false,
            editor: {
                sql: sql,
                sync: true
            }
        });
        if (dataSrcId || dataSrcId == 0) {
            this.props.tablelist(dataSrcId)
                .then(
                    (res) => {
                        if (res) {
                            this.setState({
                                tableList: res.data
                            })
                        }
                    }
                );
        }
        if (tableName) {
            this.props.tablecolumn(dataSrcId, tableName)
                .then(
                    (res) => {
                        if (res) {
                            this.setState({
                                tableData: res.data
                            })
                        }
                    }
                )
        }
        this.getDataSource();
    }
    sqlOnChange(initValue, value, doc) {
        this.setState({
            editor: {
                sql: value,
                cursor: doc.getCursor(),
                sync: false,
            },
            isEdit: true
        })
    }
    resultPageCheckedChange(evt) {
        this.setState({
            resultPageChecked: evt.target.checked
        })
    }
    resultPageChange(value) {
        this.setState({
            resultPage: value
        })
    }
    changeEditStatus(value) {
        this.setState({
            isEdit: value
        })
    }
    updateColumns(columns, type) {
        switch (type) {
            case 'in': {
                this.setState({
                    InputColumns: columns
                })
                return;
            }
            case 'out': {
                this.setState({
                    OutputColums: columns
                })
                return;
            }
        }
    }
    addColumns(type) {
        const { selectedRows } = this.state;
        switch (type) {
            case 'in': {
                this.setState({
                    InputColumns: [...this.state.InputColumns, ...selectedRows.map(
                        (data) => {
                            return new ColumnsModel(data);
                        }
                    )]
                })
                return;
            }
            case 'out': {
                this.setState({
                    OutputColums: [...this.state.OutputColums, ...selectedRows.map(
                        (data) => {
                            return new ColumnsModel(data);
                        }
                    )]
                })
            }
                return;
        }

    }

    removeColumns(removeRows, type) {
        function filterArr(Columns) {
            return Columns.filter(
                (column) => {
                    return !id_arr.includes(column.id)
                }
            )
        }

        const { InputColumns, OutputColums } = this.state;
        const id_arr = removeRows.map(
            (row) => {
                return row.id;
            }
        );

        switch (type) {
            case 'in': {
                this.setState({
                    InputColumns: filterArr(InputColumns)
                })
                return;
            }
            case 'out': {
                this.setState({
                    OutputColums: filterArr(OutputColums)
                })
                return;
            }
        }

    }

    initColumns() {
        return [
            {
                title: '字段',
                dataIndex: 'key'
            },
            {
                title: '字段类型',
                dataIndex: 'type'
            }
        ]
    }

    getDataSource() {
        this.props.getDataSourceList(null)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            dataSourceList: res.data
                        })
                    }
                }
            )
    }
    dataSourceChange(key) {
        //数据源改变，获取表
        this.setState({
            tableData: [],
            tableList: [],
            InputColumns: [],
            OutputColums: [],
            selectedRows: []
        })
        this.props.form.setFieldsValue({
            'tableSource': undefined
        })
        this.props.tablelist(key)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            tableList: res.data
                        })
                    }
                }
            );
    }
    tableChange(key) {
        const dataSource = this.props.form.getFieldValue("dataSource");
        this.setState({
            tableData: [],
            InputColumns: [],
            OutputColums: [],
            selectedRows: []
        })

        this.props.tablecolumn(dataSource, key)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            tableData: res.data
                        })
                    }
                }
            )
    }
    rowSelection() {
        const { mode } = this.props;
        if (mode == API_MODE.SQL) {
            return undefined;
        }
        return {
            onChange: (selectedRowKeys, selectedRows) => {
                console.log(selectedRowKeys);
                this.setState({
                    selectedRows: selectedRows
                })
            },
            selectedRowKeys: this.state.selectedRows.map(
                (row) => {
                    return row.key
                }
            )
        }
    }
    getSaveData() {
        const { InputColumns, OutputColums, resultPageChecked, resultPage, editor } = this.state;
        const dataSource = this.props.form.getFieldValue("dataSource");
        const tableSource = this.props.form.getFieldValue("tableSource");
        const params = {
            dataSrcId: dataSource,
            tableName: tableSource,
            inputParam: InputColumns,
            outputParam: OutputColums,
            resultPageChecked: resultPageChecked,
            resultPage: resultPage,
            sql: editor.sql
        };
        return params;
    }
    cancelAndSave() {
        const { cancelAndSave } = this.props;
        cancelAndSave(this.getSaveData());
    }
    pass() {
        const { isEdit, InputColumns, OutputColums } = this.state;
        if (isEdit) {
            message.warning("请先完成参数编辑", 2)
            this.sqlModeShowChange(true);
            return;
        }
        if (!OutputColums || OutputColums.length == 0) {
            message.error("输出参数不能为空")
            return;
        }
        if (!this.checkRepeat(InputColumns)) {
            message.error("输入参数不能相同")
            return;
        }
        if (!this.checkRepeat(OutputColums)) {
            message.error("输出参数不能相同")
            return;
        }
        this.props.dataChange(this.getSaveData())
    }
    prev() {
        const { isEdit } = this.state;
        const { mode } = this.props;
        if (isEdit && API_MODE.GUIDE == mode) {
            message.warning("请先完成参数编辑", 2)
            return;
        }
        this.props.saveData(this.getSaveData());
        this.props.prev();
    }
    checkRepeat(columns) {
        const map = {};
        for (let i = 0; i < columns.length; i++) {
            const column = columns[i];
            if (!map[column.columnName]) {
                map[column.columnName] = column.paramsName;
            } else {
                if (column.paramsName == map[column.columnName]) {
                    return false;
                }
            }
        }
        return true;
    }
    sqlModeShowChange(isHide) {
        isHide=typeof isHide=="boolean"?isHide:false;
        const dataSource = this.props.form.getFieldValue("dataSource");
        const sql = this.state.editor.sql;
        const show = !this.state.sqlModeShow;
        if(show){
            this.setState({
                sqlModeShow: isHide?false:true
            });
            return ;
        }
        if(!dataSource&&dataSource!=0){
            message.warning("请选择数据源!");
            return;
        }
        if(!sql){
            message.warning("sql不能为空!");
            return;
        }
        this.setState({
            loading:true
        })
        this.props.sqlParser(sql,dataSource)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            InputColumns:this.exchangeServerParams(res.data.inputParam,'in'),
                            OutputColums:this.exchangeServerParams(res.data.outputParam,'out'),
                            sqlModeShow: !this.state.sqlModeShow,
                            loading:false
                        })
                    }
                }
            )
    }
    exchangeServerParams(columns,type){
        const {InputColumns, OutputColums} = this.state;
        let nowColumns=type=='in'?InputColumns:OutputColums;
        const tmpCache={};
        for(let i=0;i<nowColumns.length;i++){
            let column=nowColumns[i];
            tmpCache[column.columnName]=column;
        }
        return columns?columns.map(
            (column)=>{
                const cacheColumn=tmpCache[column.fieldName];
                let id,desc,required;

                if(cacheColumn){
                    id=cacheColumn.id;
                    desc=cacheColumn.desc;
                    required=cacheColumn.required;
                }
                return new ColumnsModel({
                    key:column.fieldName,
                    type:column.paramType,
                    paramsName:column.paramName,
                    operator:column.operator,
                    id:id,
                    desc: desc,
                    required: required
                })
            }
        ):[];
    }
    sqlFormat() {
        this.props.sqlFormat(this.state.editor.sql)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            editor: {
                                sync: true,
                                sql: res.data
                            }
                        })
                    }
                }
            )
    }
    render() {
        const columns = this.initColumns();
        const { mode } = this.props;
        const { tableData, dataSourceList, tableList, InputColumns, OutputColums, selectedRows, resultPageChecked, resultPage, sqlModeShow, editor, isEdit, loading } = this.state;
        const { getFieldDecorator } = this.props.form;

        const dataSourceOptions = dataSourceList.map(
            (data) => {
                return <Option value={data.id}>{data.name}</Option>
            }
        )
        const tableOptions = tableList.map(
            (data) => {
                return <Option value={data}>{data}</Option>
            }
        )

        return (
            <div>
                <div className="steps-content">
                    <div className="paramsConfigBox">
                        <div className="paramsConfig_data">
                            <p className="required-tip middle-title">数据源配置:</p>
                            <section style={{ paddingTop: "10px" }}>
                                <FormItem>
                                    {getFieldDecorator('dataSource', {
                                        initialValue: this.props.dataSrcId
                                    })(
                                        <Select
                                            placeholder="数据源"
                                            style={{ width: "100%" }}
                                            showSearch
                                            onChange={this.dataSourceChange.bind(this)}
                                        >
                                            {dataSourceOptions}
                                        </Select>
                                    )}
                                </FormItem>
                                <FormItem>
                                    {getFieldDecorator('tableSource', {
                                        initialValue: this.props.tableName
                                    })(
                                        <Select
                                            placeholder="数据表"
                                            style={{ width: "100%" }}
                                            showSearch
                                            onChange={this.tableChange.bind(this)}
                                        >
                                            {tableOptions}
                                        </Select>
                                    )}
                                </FormItem>
                            </section>
                            <p className="required-tip middle-title">数据字段:</p>
                            <section style={{ padding: "10px 0px" }}>
                                <Table
                                    style={{ background: "#fff" }}
                                    className="shadow m-table m-table-showselect"
                                    columns={columns}
                                    dataSource={tableData}
                                    pagination={false}
                                    rowSelection={this.rowSelection()}
                                />
                            </section>
                        </div>
                        <div className="paramsConfig_arrow"></div>
                        <div className="paramsConfig_param">
                            {mode == API_MODE.SQL && sqlModeShow ?
                                <ApiSqlEditor
                                    updateColumns={this.updateColumns.bind(this)}
                                    sqlModeShowChange={this.sqlModeShowChange.bind(this)}
                                    sqlOnChange={this.sqlOnChange.bind(this)}
                                    sqlFormat={this.sqlFormat.bind(this)}
                                    editor={editor}
                                    loading={loading}
                                />
                                : (
                                    <ColumnsConfig
                                        addColumns={this.addColumns.bind(this)}
                                        removeColumns={this.removeColumns.bind(this)}
                                        updateColumns={this.updateColumns.bind(this)}
                                        changeEditStatus={this.changeEditStatus.bind(this)}
                                        checkRepeat={this.checkRepeat.bind(this)}
                                        resultPageChange={this.resultPageChange.bind(this)}
                                        resultPageCheckedChange={this.resultPageCheckedChange.bind(this)}
                                        sqlModeShowChange={this.sqlModeShowChange.bind(this)}
                                        InputColumns={InputColumns}
                                        OutputColums={OutputColums}
                                        selectedRows={selectedRows}
                                        resultPageChecked={resultPageChecked}
                                        resultPage={resultPage}
                                        mode={mode}
                                        isEdit={isEdit}
                                    />
                                )}
                        </div>
                    </div>
                </div>
                <div
                    className="steps-action"
                >
                    {
                        <Button onClick={this.cancelAndSave.bind(this)}>
                            保存并退出
                        </Button>
                    }
                    {
                        <Button style={{ marginLeft: 8 }} onClick={() => this.prev()}>上一步</Button>
                    }
                    {
                        <Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.pass()}>下一步</Button>
                    }

                </div>
            </div>
        )
    }
}

export default Form.create()(ManageParamsConfig);