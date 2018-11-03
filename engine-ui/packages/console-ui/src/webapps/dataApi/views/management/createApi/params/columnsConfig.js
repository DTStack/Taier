import React from "react";
import { Card, Table, Form, Icon, Input, Select, Checkbox, Tooltip, message, InputNumber } from "antd";
import classnames from "classnames";
import { cloneDeep } from "lodash"
import { connect } from 'react-redux';
import { apiManageActions } from '../../../../actions/apiManage';
import { API_MODE } from "../../../../consts"

const FormItem = Form.Item;
const TextArea = Input.TextArea;
const Option = Select.Option;
const DELIMITER = '~'

class ColumnsConfig extends React.Component {
    state = {
        InputSelectedRows: [],
        OutSelectedRows: [],
    }

    changeEditStatus(input, output) {
        const {InputIsEdit,OutputIsEdit} = this.props;

        this.props.changeColumnsEditStatus(typeof input=="boolean"?input:InputIsEdit,typeof output=="boolean"?output:OutputIsEdit);         
    }
    renderEdit(dataIndex, id, type, initialValue) {
        const { getFieldDecorator } = this.props.form;
        switch (dataIndex) {
            case 'paramsName': {
                return (<FormItem
                    style={{ marginBottom: "0px" }}
                >
                    {getFieldDecorator(`paramsName${DELIMITER}${id}${DELIMITER}${type}`, {
                        initialValue: initialValue,
                        rules: [{
                            required: true, message: "请输入参数名称"
                        }]
                    })(
                        <Input />
                    )}
                </FormItem>);
            }
            case 'operator': {
                return (<FormItem
                    style={{ marginBottom: "0px" }}
                >
                    {getFieldDecorator(`operator${DELIMITER}${id}${DELIMITER}${type}`, {
                        initialValue: initialValue
                    })(
                        <Select style={{ width: "100%" }}>
                            <Option value="=">=</Option>
                            <Option value=">">&gt;</Option>
                            <Option value=">=">&gt;=</Option>
                            <Option value="<"> &lt;</Option>
                            <Option value="<=">&lt;=</Option>
                            <Option value="!=">!=</Option>
                            <Option value="in">in</Option>
                            <Option value="not in">not in</Option>
                            <Option value="like">like</Option>
                            <Option value="not like">not like</Option>
                        </Select>
                    )}
                </FormItem>);
            }
            case 'required': {
                return (<FormItem
                    style={{ marginBottom: "0px" }}
                >
                    {getFieldDecorator(`required${DELIMITER}${id}${DELIMITER}${type}`, {
                        initialValue: initialValue,
                        valuePropName: 'checked',
                    })(
                        <Checkbox></Checkbox>
                    )}
                </FormItem>);
            }
            case 'desc': {
                return (<FormItem
                    style={{ marginBottom: "0px" }}
                >
                    {getFieldDecorator(`desc${DELIMITER}${id}${DELIMITER}${type}`, {
                        initialValue: initialValue
                    })(
                        <TextArea placeholder="参数描述" autosize={{ minRows: 2, maxRows: 4 }} />
                    )}
                </FormItem>);
            }
        }
    }
    initColumns(type) {
        const { InputIsEdit, OutputIsEdit } = this.props;
        const {mode} =this.props;
        const isGuideMode=mode==API_MODE.GUIDE;
        if (type == 'in') {
            return [
                {
                    title: '参数名称',
                    dataIndex: 'paramsName',
                    width: "150px",
                    render: (text, record) => {
                        return InputIsEdit&&isGuideMode ? this.renderEdit('paramsName', record.id, type, text) : text;
                    }
                },
                {
                    title: '绑定字段',
                    dataIndex: 'columnName',
                    width: "150px"
                },
                {
                    title: "字段类型",
                    dataIndex: "type",
                    width: "100px"
                },
                {
                    title: "操作符",
                    dataIndex: "operator",
                    width: "85px",
                    render: (text, record) => {
                        return InputIsEdit&&isGuideMode ? this.renderEdit('operator', record.id, type, text) : text;
                    }
                },
                {
                    title: "必填",
                    dataIndex: "required",
                    render: (text, record) => {
                        if (InputIsEdit) {
                            return this.renderEdit('required', record.id, type, text)
                        }
                        return text ? '是' : '否'
                    },
                    width: "40px"
                },
                {
                    title: "说明",
                    dataIndex: "desc",
                    render: (text, record) => {
                        if (InputIsEdit) {
                            return this.renderEdit('desc', record.id, type, text)
                        }
                        return text && text.length > 15 ? (<span title={text}>{text.substr(0, 15)}...</span>) : text;
                    }
                }
            ]
        } else if (type == 'out') {
            return [
                {
                    title: '参数名称',
                    dataIndex: 'paramsName',
                    width: "150px",
                    render: (text, record) => {
                        return OutputIsEdit&&isGuideMode ? this.renderEdit('paramsName', record.id, type, text) : text;
                    }
                },
                {
                    title: '绑定字段',
                    dataIndex: 'columnName',
                    width: "150px"
                },
                {
                    title: "字段类型",
                    dataIndex: "type",
                    width: "100px"
                },
                {
                    title: "说明",
                    dataIndex: "desc",
                    render: (text, record) => {
                        if (OutputIsEdit) {
                            return this.renderEdit('desc', record.id, type, text)
                        }
                        return text && text.length > 30 ? (<span title={text}>{text.substr(0, 30)}...</span>) : text;
                    }
                }
            ]
        }

    }

    rowSelection(type) {
        const {mode } =this.props;
        if(mode==API_MODE.SQL){
            return undefined;
        }
        return {
            onChange: (selectedRowKeys, selectedRows) => {
                switch (type) {
                    case 'in': {
                        this.setState({
                            InputSelectedRows: selectedRows
                        })
                        return;
                    }
                    case 'out': {
                        this.setState({
                            OutSelectedRows: selectedRows
                        })
                        return;
                    }
                }
            }
        }
    }

    onEdit(type) {
        if (type == 'in') {
            this.changeEditStatus(true)
        } else if (type == 'out') {
            this.changeEditStatus(null,true)
        }
    }
    cancelEdit(type) {
        if (type == 'in') {
            this.changeEditStatus(false)
        } else if (type == 'out') {
            this.changeEditStatus(null,false)
        }
    }
    filterSelectRow(rows, type) {
        const { InputSelectedRows, OutSelectedRows } = this.state;
        const id_arr = rows.map(
            (row) => {
                return row.id;
            }
        )
        if (type == "in") {
            this.setState({
                InputSelectedRows: InputSelectedRows.filter(
                    (row) => {
                        return !id_arr.includes(row.id);
                    }
                )
            })
        } else if (type == 'out') {
            this.setState({
                OutSelectedRows: OutSelectedRows.filter(
                    (row) => {
                        return !id_arr.includes(row.id);
                    }
                )
            })
        }
    }
    paramsChange(type) {
        const { InputColumns, OutputColums, updateColumns, checkRepeat } = this.props;
        const { getFieldsValue, validateFields } = this.props.form;
        const values = getFieldsValue();
        const keys = Object.entries(values);
        const validateFieldsKeys = [];
        const formItemMap = {};
        for (let i = 0; i < keys.length; i++) {
            let key = keys[i][0];
            let value = keys[i][1];
            let key_arr = key.split(DELIMITER);
            if (!key_arr || key_arr.length != 3) {
                continue;
            }

            let formItemName = key_arr[0];
            let id = key_arr[1];
            let itemType = key_arr[2];
            if (type == itemType) {
                formItemMap[id] = formItemMap[id] || {};
                formItemMap[id][formItemName] = value;
                validateFieldsKeys.push(key);
            }
        }
        validateFields(validateFieldsKeys, (errors, values) => {
            if (!errors) {
                let Columns;
                let input,output;
                if (type == 'in') {
                    Columns = cloneDeep(InputColumns);
                    input=false;
                } else if (type == 'out') {
                    Columns = cloneDeep(OutputColums);
                    output = false;

                }
                const updateData = Columns.map(
                    (column) => {
                        const id = column.id;
                        if (formItemMap[id]) {
                            const keys = Object.entries(formItemMap[id]);
                            for (let i = 0; i < keys.length; i++) {
                                const key = keys[i][0];
                                const value = keys[i][1];
                                column[key] = value;
                            }
                        }
                        return column;
                    }
                )
                if (checkRepeat(updateData)) {
                    updateColumns(updateData, type)
                    this.changeEditStatus(input,output)
                } else {
                    message.error("参数不能重复", 2)
                }
            }
        })
    }

    apiParamsConfig = ()=>{
        const { mode, InputIsEdit, selectedRows, addColumns, removeColumns} =this.props;
        const { InputSelectedRows } = this.state;

        const inputAdd = classnames('params_exchange_button', {
            'params_exchange_button_disable': !selectedRows || selectedRows.length == 0
        })
        const inputRemove = classnames('params_exchange_button', {
            'params_exchange_button_disable': !InputSelectedRows || InputSelectedRows.length == 0
        })
        return <p className="required-tip middle-title middle-header">
                输入参数：
                {InputIsEdit ?
                    <span>
                        <a onClick={this.paramsChange.bind(this, 'in')} style={{ float: "right" }}>完成</a>
                        <a onClick={this.cancelEdit.bind(this, 'in')} style={{ float: "right", marginRight: "8px" }}>取消</a>
                    </span>
                    :
                    <a onClick={this.onEdit.bind(this, 'in')} style={{ float: "right" }}>编辑</a>}
                {mode == API_MODE.GUIDE && (
                    <div className="params_exchange_box">
                        <Icon
                            type="right-square-o"
                            className={inputAdd}
                            onClick={addColumns.bind(null, 'in')}
                        />
                        <Icon
                            type="left-square-o"
                            className={inputRemove}
                            onClick={() => {
                                this.filterSelectRow(InputSelectedRows, 'in');
                                removeColumns(InputSelectedRows, 'in')
                            }}
                        />
                    </div>
                )}
            </p>
    }
    
    outputParams = () => {
        const { addColumns, removeColumns, selectedRows,resultPageChecked, resultPage, 
            resultPageCheckedChange, resultPageChange, mode,OutputIsEdit } = this.props;
        const { OutSelectedRows } = this.state;
        const outAdd = classnames('params_exchange_button', {
            'params_exchange_button_disable': !selectedRows || selectedRows.length == 0
        })
        const outRemove = classnames('params_exchange_button', {
            'params_exchange_button_disable': !OutSelectedRows || OutSelectedRows.length == 0
        })
        return <div className="required-tip middle-title middle-header">
                输出参数：
                <span className="params_result_check">
                    <Checkbox checked={resultPageChecked} onChange={resultPageCheckedChange} >返回结果分页</Checkbox>
                    <Tooltip title="当查询结果大于1000条时，请选择分页查询，每页最大返回1000条结果。若没有选择，默认分页查询。">
                        <Icon type="question-circle-o" />
                    </Tooltip>
                    {resultPageChecked?<InputNumber  placeholder="请输入分页大小" style={{ marginLeft: "8px",width:"115px" }} min={1} max={1000} value={resultPage} onChange={resultPageChange} />:null}
                </span>
                {OutputIsEdit ?
                    <span>
                        <a onClick={this.paramsChange.bind(this, 'out')} style={{ float: "right" }}>完成</a>
                        <a onClick={this.cancelEdit.bind(this, 'out')} style={{ float: "right", marginRight: "8px" }}>取消</a>
                    </span>
                    :
                    <a onClick={this.onEdit.bind(this, 'out')} style={{ float: "right" }}>编辑</a>}
                {mode == API_MODE.GUIDE && (
                    <div className="params_exchange_box">
                        <Icon
                            type="right-square-o"
                            className={outAdd}
                            onClick={addColumns.bind(null, 'out')}
                        />
                        <Icon
                            type="left-square-o"
                            className={outRemove}
                            onClick={() => {
                                this.filterSelectRow(OutSelectedRows, 'out');
                                removeColumns(OutSelectedRows, 'out')
                            }} />
                    </div>
                )}
            </div>
    }

    render() {
        const { InputColumns, OutputColums, mode, sqlModeShowChange } = this.props;
        const inputTableColumns = this.initColumns('in');
        const outputTableColumns = this.initColumns('out');
        return (
            <div>
                {
                    mode==API_MODE.SQL
                    ?<span>
                            API参数配置
                            <span style={{float:"right",marginLeft:"8px",fontSize:"12px",color:"#888"}}>编辑参数</span>
                            <a onClick={() => {
                                this.props.handleClickCode();
                                sqlModeShowChange()
                            }} style={{float:"right",fontSize:"12px",}}>代码</a>
                        </span>
                    :<p className='middle-title'>API参数配置</p>
                }
                <Card title={this.apiParamsConfig()} style={{marginTop: 10}}>
                    <Table
                        rowKey="id"
                        className="m-table m-table-showselect"
                        style={{ background: "#fff" }}
                        columns={inputTableColumns}
                        dataSource={InputColumns}
                        pagination={false}
                        rowSelection={this.rowSelection('in')}
                        scroll={{ y: 175 }}
                    />
                </Card>
                <Card title={this.outputParams()} style={{marginTop: 20}}>
                    <Table
                        rowKey="id"
                        className="m-table m-table-showselect"
                        style={{ background: "#fff" }}
                        columns={outputTableColumns}
                        dataSource={OutputColums}
                        pagination={false}
                        rowSelection={this.rowSelection('out')}
                        scroll={{ y: 175 }}
                    />
                </Card>
            </div>
        )
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        handleClickCode() {
            dispatch(apiManageActions.clickCode())
        }
    }
}

export default connect(null, mapDispatchToProps)(Form.create()(ColumnsConfig));