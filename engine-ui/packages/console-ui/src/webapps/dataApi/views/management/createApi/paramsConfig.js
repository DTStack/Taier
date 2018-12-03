import React, { Component } from 'react'
import { Button, Select, Form, Table, message } from 'antd';

import ColumnsConfig from './params/columnsConfig'
import ColumnsModel from '../../../model/columnsModel'
import ApiSqlEditor from './sql'
import { API_MODE } from '../../../consts'

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
        resultPageChecked: false,
        resultPage: undefined,
        sqlModeShow: true,
        editor: {
            sql: '',
            cursor: undefined,
            sync: true
        }

    }

    // eslint-disable-next-line
    componentWillMount () {
        const { tableName, dataSrcId, inputParam, outputParam, resultPageChecked, resultPage, sql,
            InputIsEdit, OutputIsEdit, dataSourceType } = this.props;
        this.setState({
            InputColumns: inputParam || [],
            OutputColums: outputParam || [],
            resultPageChecked: resultPageChecked,
            resultPage: resultPage,
            editor: {
                sql: sql,
                sync: true
            },
            sqlModeShow: InputIsEdit && OutputIsEdit
        });
        if (dataSourceType || dataSourceType == 0 || dataSrcId || dataSrcId == 0) {
            this.getDataSource(dataSourceType);
        }
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
        // this.getDataSource();
        this.props.getDataSourcesType();
    }
    sqlOnChange (value, doc) {
        this.setState({
            editor: {
                sql: value,
                // cursor: doc.getCursor(),
                sync: false
            }
        })
        this.props.changeColumnsEditStatus(true, true)
    }
    resultPageCheckedChange (evt) {
        this.setState({
            resultPageChecked: evt.target.checked
        })
    }
    resultPageChange (value) {
        this.setState({
            resultPage: value
        })
    }
    changeColumnsEditStatus (input, output) {
        this.props.changeColumnsEditStatus(input, output)
    }
    updateColumns (columns, type) {
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
            }
        }
    }
    addColumns (type) {
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
        }
    }

    removeColumns (removeRows, type) {
        function filterArr (Columns) {
            return Columns.filter(
                (column) => {
                    return !idArr.includes(column.id)
                }
            )
        }

        const { InputColumns, OutputColums } = this.state;
        const idArr = removeRows.map(
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
            }
        }
    }

    initColumns () {
        return [
            {
                title: '字段',
                dataIndex: 'key'
            },
            {
                title: '字段类型',
                dataIndex: 'type',
                width: '100px'
            }
        ]
    }

    getDataSource (type) {
        this.props.getDataSourceList(type)
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
    dataSourceChange (key) {
        // 数据源改变，获取表
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
    dataSourceTypeChange (key) {
        this.setState({
            dataSourceList: [],
            tableData: [],
            tableList: [],
            InputColumns: [],
            OutputColums: [],
            selectedRows: []
        })
        this.props.form.setFieldsValue({
            'tableSource': undefined,
            'dataSource': undefined

        })
        this.getDataSource(key);
    }
    tableChange (key) {
        const dataSource = this.props.form.getFieldValue('dataSource');
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
    rowSelection () {
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
    getSaveData () {
        const { InputColumns, OutputColums, resultPageChecked, resultPage, editor } = this.state;
        const dataSource = this.props.form.getFieldValue('dataSource');
        const tableSource = this.props.form.getFieldValue('tableSource');
        const dataSourceType = this.props.form.getFieldValue('dataSourceType');
        const params = {
            dataSrcId: dataSource,
            dataSourceType: dataSourceType,
            tableName: tableSource,
            inputParam: InputColumns,
            outputParam: OutputColums,
            resultPageChecked: resultPageChecked,
            resultPage: resultPage,
            sql: editor.sql
        };
        return params;
    }
    cancelAndSave () {
        const { cancelAndSave } = this.props;
        cancelAndSave(this.getSaveData());
    }
    pass () {
        const { InputIsEdit, OutputIsEdit, mode } = this.props;
        const isEdit = InputIsEdit || OutputIsEdit;
        const { InputColumns, OutputColums, editor } = this.state;
        if (!editor.sql && mode == API_MODE.SQL) {
            message.warning('sql不能为空')
            return
        }
        if (isEdit) {
            message.warning('请先完成参数编辑', 2)
            if (mode == API_MODE.SQL) {
                this.sqlModeShowChange(true);
            }
            return;
        }
        if (!OutputColums || OutputColums.length == 0) {
            message.error('输出参数不能为空')
            return;
        }
        if (!this.checkRepeat(InputColumns)) {
            message.error('输入参数不能相同')
            return;
        }
        if (!this.checkRepeat(OutputColums)) {
            message.error('输出参数不能相同')
            return;
        }
        this.props.dataChange(this.getSaveData())
    }
    prev () {
        const { mode, InputIsEdit, OutputIsEdit } = this.props;
        const isEdit = InputIsEdit || OutputIsEdit;
        if (isEdit && API_MODE.GUIDE == mode) {
            message.warning('请先完成参数编辑', 2)
            return;
        }
        this.props.saveData(this.getSaveData());
        this.props.prev();
    }
    checkRepeat (columns) {
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
    sqlModeShowChange (isHide) {
        isHide = typeof isHide == 'boolean' ? isHide : false;
        const dataSource = this.props.form.getFieldValue('dataSource');
        const sql = this.state.editor.sql;
        const show = !this.state.sqlModeShow;
        if (show) {
            this.setState({
                sqlModeShow: !isHide
            });
            return;
        }
        if (!dataSource && dataSource != 0) {
            message.warning('请选择数据源!');
            return;
        }
        if (!sql) {
            message.warning('sql不能为空!');
            return;
        }
        this.setState({
            loading: true
        })
        this.props.sqlParser(sql, dataSource)
            .then(
                (res) => {
                    this.setState({
                        loading: false
                    })
                    if (res) {
                        this.setState({
                            InputColumns: this.exchangeServerParams(res.data.inputParam, 'in'),
                            OutputColums: this.exchangeServerParams(res.data.outputParam, 'out'),
                            sqlModeShow: !this.state.sqlModeShow
                        })
                    }
                }
            )
    }
    exchangeServerParams (columns, type) {
        const { InputColumns, OutputColums } = this.state;
        let nowColumns = type == 'in' ? InputColumns : OutputColums;
        const tmpCache = {};
        for (let i = 0; i < nowColumns.length; i++) {
            let column = nowColumns[i];
            tmpCache[column.paramsName] = column;
        }
        return columns ? columns.map(
            (column) => {
                const cacheColumn = tmpCache[column.paramName];
                let id, desc, required;

                if (cacheColumn && cacheColumn.columnName == column.fieldName) {
                    id = cacheColumn.id;
                    desc = cacheColumn.desc;
                    required = cacheColumn.required;
                }
                return new ColumnsModel({
                    key: column.fieldName,
                    type: column.paramType,
                    paramsName: column.paramName,
                    operator: column.operator,
                    id: id,
                    desc: desc,
                    required: required
                })
            }
        ) : [];
    }
    sqlFormat () {
        this.props.sqlFormat(this.state.editor.sql, this.props.dataSourceType)
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
    render () {
        const columns = this.initColumns();
        const { mode, InputIsEdit, OutputIsEdit, dataSource, apiEdit, disAbleTipChange, apiManage } = this.props;
        const { tableData, dataSourceList, tableList, InputColumns, OutputColums, selectedRows, resultPageChecked, resultPage, sqlModeShow, editor, loading } = this.state;
        const { getFieldDecorator } = this.props.form;
        const dataSourceType = dataSource.sourceType || [];

        const dataSourceOptions = dataSourceList.map(
            (data) => {
                return <Option key={data.id} value={data.id}>{data.name}</Option>
            }
        )
        const tableOptions = tableList.map(
            (data) => {
                return <Option key={data} value={data}>{data}</Option>
            }
        )
        const dataSourceTypeOption = dataSourceType.map(
            (data) => {
                return <Option key={data.value} value={data.value}>{data.name}</Option>
            }
        )
        const modeType = mode == API_MODE.SQL;
        const dataFieldsClass = modeType ? 'middle-title' : 'required-tip middle-title';
        const paramsConfigClass = modeType ? 'paramsSql_arrow' : 'paramsConfig_arrow';
        return (
            <div>
                <div className="steps-content">
                    <div className="paramsConfigBox">
                        <div className="paramsConfig_data">
                            <p className="required-tip middle-title">数据源配置:</p>
                            <section style={{ paddingTop: '10px' }}>
                                <FormItem>
                                    {getFieldDecorator('dataSourceType', {
                                        initialValue: this.props.dataSourceType
                                    })(
                                        <Select
                                            placeholder="数据源类型"
                                            style={{ width: '100%' }}
                                            showSearch
                                            onChange={this.dataSourceTypeChange.bind(this)}
                                        >
                                            {dataSourceTypeOption}
                                        </Select>
                                    )}
                                </FormItem>
                                <FormItem>
                                    {getFieldDecorator('dataSource', {
                                        initialValue: this.props.dataSrcId
                                    })(
                                        <Select
                                            placeholder="数据源"
                                            style={{ width: '100%' }}
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
                                            style={{ width: '100%' }}
                                            showSearch
                                            onChange={this.tableChange.bind(this)}
                                        >
                                            {tableOptions}
                                        </Select>
                                    )}
                                </FormItem>
                            </section>
                            <p className={dataFieldsClass} >数据字段:</p>
                            <section style={{ padding: '15px 0px 10px' }}>
                                <Table
                                    style={{ background: '#fff' }}
                                    className="shadow m-table m-table-showselect"
                                    columns={columns}
                                    dataSource={tableData}
                                    pagination={false}
                                    rowSelection={this.rowSelection()}
                                    scroll={{ y: 297 }}
                                />
                            </section>
                        </div>
                        <div className={paramsConfigClass}></div>
                        <div className="paramsConfig_param">
                            {mode == API_MODE.SQL && sqlModeShow
                                ? <ApiSqlEditor
                                    updateColumns={this.updateColumns.bind(this)}
                                    sqlModeShowChange={this.sqlModeShowChange.bind(this)}
                                    sqlOnChange={this.sqlOnChange.bind(this)}
                                    sqlFormat={this.sqlFormat.bind(this)}
                                    editor={editor}
                                    loading={loading}
                                    apiEdit={apiEdit}
                                    disAbleTipChange={disAbleTipChange}
                                    disAbleTip={apiManage.disAbleTip}

                                />
                                : (
                                    <ColumnsConfig
                                        addColumns={this.addColumns.bind(this)}
                                        removeColumns={this.removeColumns.bind(this)}
                                        updateColumns={this.updateColumns.bind(this)}
                                        changeColumnsEditStatus={this.changeColumnsEditStatus.bind(this)}
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
                                        InputIsEdit={InputIsEdit}
                                        OutputIsEdit={OutputIsEdit}
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
