import * as React from 'react'
import { Button, Select, Form, Table, message, Checkbox, Tooltip, Icon } from 'antd';

import utils from 'utils';

import ColumnsConfig from './container'
import ColumnsModel from '../../../../model/columnsModel'
import ApiSqlEditor from './sql'
import { API_MODE, API_METHOD } from '../../../../consts'

const Option = Select.Option;
const FormItem = Form.Item;

class ManageParamsConfig extends React.Component<any, any> {
    state: any = {
        tableData: [],
        dataSourceList: [],
        tableList: [],
        InputColumns: [],
        OutputColums: [],
        selectedRows: [],
        resultPageChecked: false,
        resultPage: undefined,
        sqlModeShow: true,
        passLoading: false,
        saveLoading: false,
        isCheckParams: false, // 是否检查了参数
        editor: {
            sql: '',
            cursor: undefined,
            sync: true
        }

    }
    columnsRef = React.createRef();
    // eslint-disable-next-line
    componentWillMount () {
        const { tableName, dataSrcId, inputParam, outputParam, resultPageChecked, resultPage, sql,
            dataSourceType } = this.props;
        this.setState({
            InputColumns: inputParam || [],
            OutputColums: outputParam || [],
            resultPageChecked: resultPageChecked,
            resultPage: resultPage,
            editor: {
                sql: sql,
                sync: true
            },
            sqlModeShow: true
        });
        /**
         * 获取数据源类型
         */
        this.props.getDataSourcesType();
        /**
         * 获取数据源列表
         */
        if (dataSourceType || dataSourceType == 0 || dataSrcId || dataSrcId == 0) {
            this.getDataSource(dataSourceType);
        }
        /**
         * 获取表列表
         */
        if (dataSrcId || dataSrcId == 0) {
            this.props.tablelist(dataSrcId)
                .then(
                    (res: any) => {
                        if (res) {
                            this.setState({
                                tableList: res.data
                            })
                        }
                    }
                );
        }
        /**
         * 获取表的字段名
         */
        if (tableName) {
            this.props.tablecolumn(dataSrcId, tableName)
                .then(
                    (res: any) => {
                        if (res) {
                            this.setState({
                                tableData: res.data
                            })
                        }
                    }
                )
        }
    }
    sqlOnChange (value: any, doc: any) {
        this.setState({
            editor: {
                sql: value,
                // cursor: doc.getCursor(),
                sync: false
            }
        })
    }
    resultPageCheckedChange (evt: any) {
        this.setState({
            resultPageChecked: evt.target.checked
        })
    }
    onCheckedContainPage = (evt: any) => {
        if (evt.target.checked) {
            this.setState({
                resultPageChecked: true
            })
        }
    }
    resultPageChange (value: any) {
        this.setState({
            resultPage: value
        })
    }
    updateColumns (columns: any, type: any) {
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
    addColumns (type: any) {
        const { selectedRows } = this.state;
        switch (type) {
            case 'in': {
                this.setState({
                    InputColumns: [...this.state.InputColumns, ...selectedRows.map(
                        (data: any) => {
                            return new ColumnsModel(data);
                        }
                    )]
                })
                return;
            }
            case 'out': {
                this.setState({
                    OutputColums: [...this.state.OutputColums, ...selectedRows.map(
                        (data: any) => {
                            return new ColumnsModel(data);
                        }
                    )]
                })
            }
        }
    }

    removeColumns (removeRows: any, type: any) {
        function filterArr (Columns: any) {
            return Columns.filter(
                (column: any) => {
                    return !idArr.includes(column.id)
                }
            )
        }

        const { InputColumns, OutputColums } = this.state;
        const idArr = removeRows.map(
            (row: any) => {
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
                width: '120px'
            }
        ]
    }

    getDataSource (type: any) {
        this.props.getDataSourceList(type)
            .then(
                (res: any) => {
                    if (res) {
                        this.setState({
                            dataSourceList: res.data
                        })
                    }
                }
            )
    }
    dataSourceChange (key: any) {
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
                (res: any) => {
                    if (res) {
                        this.setState({
                            tableList: res.data
                        })
                    }
                }
            );
    }
    dataSourceTypeChange (key: any) {
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
    tableChange (key: any) {
        const dataSource = this.props.form.getFieldValue('dataSource');
        this.setState({
            tableData: [],
            InputColumns: [],
            OutputColums: [],
            selectedRows: []
        })

        this.props.tablecolumn(dataSource, key)
            .then(
                (res: any) => {
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
            onChange: (selectedRowKeys: any, selectedRows: any) => {
                console.log(selectedRowKeys);
                this.setState({
                    selectedRows: selectedRows
                })
            },
            selectedRowKeys: this.state.selectedRows.map(
                (row: any) => {
                    return row.key
                }
            )
        }
    }
    getSaveData () {
        const { InputColumns, OutputColums, resultPageChecked, resultPage, editor } = this.state;
        const form = this.props.form;
        const dataSource = form.getFieldValue('dataSource');
        const tableSource = form.getFieldValue('tableSource');
        const dataSourceType = form.getFieldValue('dataSourceType');
        const containHeader = form.getFieldValue('containHeader');
        const containPage = form.getFieldValue('containPage');

        const params: any = {
            dataSrcId: dataSource,
            dataSourceType: dataSourceType,
            tableName: tableSource,
            inputParam: InputColumns,
            outputParam: OutputColums,
            resultPageChecked: resultPageChecked,
            resultPage: resultPage,
            sql: editor.sql,
            containHeader: containHeader ? '1' : '0', // 1 表示包含，0 表示不包含
            containPage: containPage ? '1' : '0' // 1 表示包含，0 表示不包含

        };
        return params;
    }
    cancelAndSave () {
        const { cancelAndSave } = this.props;
        const { InputColumns } = this.state;
        if (!this.checkSameName(InputColumns)) {
            message.error('同一参数名称的必填项选择必须一致!')
            return;
        }
        this.setState({
            saveLoading: true
        })
        cancelAndSave(this.getSaveData()).then(() => {
            this.setState({
                saveLoading: false
            })
        }).catch(() => {
            this.setState({
                saveLoading: false
            })
        });
    }
    setPassLoading (isLoading: any) {
        this.setState({
            passLoading: isLoading
        })
    }
    async pass () {
        const { mode, basicProperties, apiEdit } = this.props;
        const { editor, isCheckParams } = this.state;
        if (!apiEdit && !isCheckParams) {
            this.sqlModeShowChange(false);
            return false;
        }
        this.setPassLoading(true);
        const nextStep = async () => {
            const { InputColumns, OutputColums } = this.state;
            if ((!OutputColums || OutputColums.length == 0) && basicProperties.method !== API_METHOD.GET) { // GET 请求方式，输入参数未非必填
                message.error('输出参数不能为空')
                this.setPassLoading(false)
                return;
            }
            let isPass = await (this.columnsRef.current as any).getWrappedInstance().validateFields();
            this.setPassLoading(false)
            if (!isPass) {
                return false;
            }
            if (!this.checkRepeat(InputColumns)) {
                message.error('输入参数不能相同')
                return;
            }
            if (!this.checkSameName(InputColumns)) {
                message.error('同一参数名称的必填项选择必须一致!')
                return;
            }
            if (!this.checkRepeat(OutputColums)) {
                message.error('输出参数不能相同')
                return;
            }
            this.props.dataChange(this.getSaveData())
        }

        if (mode == API_MODE.SQL) {
            if (!utils.trim(editor.sql)) {
                message.error('SQL 不能为空')
                this.setPassLoading(false)
                return
            }
            let checkSql = await this.sqlModeShowChange(true);
            if (!checkSql) {
                this.setPassLoading(false)
                return;
            }
            nextStep();
        } else {
            nextStep();
        }
    }
    prev () {
        this.props.saveData(this.getSaveData());
        this.props.prev();
    }
    checkSameName (columns: any) { // 校验同一参数名称的必填项选择必须是一致的。
        const map: any = {};
        for (let i = 0; i < columns.length; i++) {
            const column = columns[i];
            if (map[column.paramsName] == undefined) {
                map[column.paramsName] = column.required;
            } else {
                if (column.required !== map[column.paramsName]) {
                    return false;
                }
            }
        }
        return true;
    }
    checkRepeat (columns: any) {
        const map: any = {};
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
    async sqlModeShowChange (isHide: any) {
        isHide = typeof isHide == 'boolean' ? isHide : false;
        const { sqlModeShow, editor } = this.state;
        const dataSource = this.props.form.getFieldValue('dataSource');
        const sql = utils.trim(editor.sql);

        /**
         * 是否在参数编辑页面
         */
        const show = !sqlModeShow;
        /**
         * 在参数编辑页面，则直接切换，不用请求
         */
        if (show) {
            this.setState({
                sqlModeShow: !isHide
            });
            return true;
        }
        if (!dataSource && dataSource != 0) {
            message.warning('请选择数据源!');
            return;
        }
        if (!sql) {
            message.warning('SQL 不能为空');
            return;
        }
        this.setState({
            loading: true,
            isCheckParams: true
        })
        let res = await this.props.sqlParser(sql, dataSource);
        this.setState({
            loading: false
        })
        if (res && res.code == 1) {
            this.setState({
                InputColumns: this.exchangeServerParams(res.data.inputParam, 'in'),
                OutputColums: this.exchangeServerParams(res.data.outputParam, 'out'),
                sqlModeShow: !sqlModeShow
            })
            return true;
        }
    }
    /**
     * 将服务端的数据和本地已有数据merge
     * @param {arr} columns 服务端的columns
     * @param {string} type 输入还是输出类型
     */
    exchangeServerParams (columns: any, type: any) {
        const { InputColumns, OutputColums } = this.state;
        let nowColumns = type == 'in' ? InputColumns : OutputColums;
        const tmpCache: any = {};
        for (let i = 0; i < nowColumns.length; i++) {
            let column = nowColumns[i];
            tmpCache[column.paramsName] = column;
        }
        return columns ? columns.map(
            (column: any) => {
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
                (res: any) => {
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
        const {
            mode,
            dataSource,
            apiEdit,
            disAbleTipChange,
            apiManage,
            containPage,
            containHeader
        } = this.props;
        const {
            tableData,
            dataSourceList,
            tableList,
            InputColumns,
            OutputColums,
            selectedRows,
            resultPageChecked,
            resultPage,
            sqlModeShow,
            editor,
            loading,
            saveLoading,
            passLoading
        } = this.state;
        const { getFieldDecorator } = this.props.form;
        const dataSourceType = dataSource.sourceType || [];
        /**
         * 数据源类型列表
         */
        const dataSourceTypeOption = dataSourceType.map(
            (data: any) => {
                return <Option key={data.value} value={data.value}>{data.name}</Option>
            }
        )
        /**
         * 数据源列表
         */
        const dataSourceOptions = dataSourceList.map(
            (data: any) => {
                return <Option key={data.id} value={data.id}>{data.name}</Option>
            }
        )
        /**
         * 表列表
         */
        const tableOptions = tableList.map(
            (data: any) => {
                return <Option key={data.tableName} value={data.tableName}>
                    <div style={{ verticalAlign: 'middle' }}>
                        <img style={{ width: 18, verticalAlign: 'middle', marginRight: 5 }} src={data.view ? 'public/dataApi/img/database_view.svg' : 'public/dataApi/img/database_table.svg'} />
                        <span style={{ verticalAlign: 'middle' }}>{data.tableName}</span>
                    </div>
                </Option>
            }
        )
        const isSqlMode = mode == API_MODE.SQL;
        const dataFieldsClass = isSqlMode ? 'middle-title' : 'required-tip middle-title';
        const paramsConfigClass = isSqlMode ? 'paramsSql_arrow' : 'paramsConfig_arrow';
        const filterOption = function (input: any, option: any) {
            return option.props.children
                .toLowerCase()
                .indexOf(input.toLowerCase()) >= 0
        }
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
                                            onSelect={this.dataSourceTypeChange.bind(this)}
                                            filterOption={filterOption}
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
                                            onSelect={this.dataSourceChange.bind(this)}
                                            filterOption={filterOption}
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
                                            filterOption={(input: any, option: any) => option.props.value.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                            onSelect={this.tableChange.bind(this)}
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
                                    className="shadow m-table m-table--border m-table-showselect"
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
                            {
                                mode == API_MODE.SQL && sqlModeShow
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
                                            ref={this.columnsRef as any}
                                            addColumns={this.addColumns.bind(this)}
                                            removeColumns={this.removeColumns.bind(this)}
                                            updateColumns={this.updateColumns.bind(this)}
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
                                        />
                                    )}
                            <div style={{ marginTop: 10 }}>
                                <p className="middle-title">高级配置</p>
                                <FormItem style={{ marginBottom: 0 }}>
                                    {getFieldDecorator('containHeader', {
                                        initialValue: containHeader
                                    })(
                                        <Checkbox defaultChecked={containHeader === '1'}>返回结果中携带 Request Header 参数</Checkbox>
                                    )}
                                </FormItem>
                                <FormItem>
                                    {getFieldDecorator('containPage', {
                                        initialValue: containPage
                                    })(
                                        <Checkbox onChange={this.onCheckedContainPage} defaultChecked={containPage === '1'}>返回结果携带分页参数</Checkbox>
                                    )}
                                    <Tooltip title={<div><p>分页参数包含：</p><p>currentPage, pageSize, totalCount, totalPage</p></div>}>
                                        <Icon type="question-circle-o" />
                                    </Tooltip>
                                </FormItem>
                            </div>
                        </div>
                    </div>
                </div>
                <div
                    className="steps-action"
                >
                    {
                        <Button loading={saveLoading} onClick={this.cancelAndSave.bind(this)}>
                            保存并退出
                        </Button>
                    }
                    {
                        <Button style={{ marginLeft: 8 }} onClick={() => this.prev()}>上一步</Button>
                    }
                    {
                        <Button type="primary" loading={passLoading} style={{ marginLeft: 8 }} onClick={() => this.pass()}>下一步</Button>
                    }

                </div>
            </div>
        )
    }
}

export default Form.create<any>()(ManageParamsConfig);
