import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Input, Icon, Button, Checkbox, Select, Row, Card, Col, Table, message } from 'antd';
import { cloneDeep } from 'lodash';

const TextArea = Input.TextArea;
const Option = Select.Option;

const mapStateToProps = state => {
    const { dataSource } = state;
    return { dataSource }
}

@connect(mapStateToProps)
class InputParams extends Component {
    state = {
        loading: false,
        tableColumns: [],
        dataSource: []
    }
    componentDidMount () {
    }
    componentWillReceiveProps (nextProps) {
        if (nextProps.addInputsignal && !this.props.addInputsignal) {
            this.addInput();
        }
        if (nextProps.initValue != this.props.initValue) {
            this.initDataSource(nextProps.initValue);
        }
    }
    initDataSource (initValue) {
        this.backMsg(initValue);
        this.setState({
            dataSource: initValue
        })
    }
    // 通知父组件最新的值
    backMsg (table, newItem) {
        let arr = [];
        for (let i in table) {
            let item = table[i];
            if (item.tmp) { // 假如存在回滚数据，则返回tmp
                arr.push(item.tmp)
            } else if (!item.isEdit) { // 不存在回滚，并且不处于编辑状态，则返回item
                arr.push(item);
            }
        }
        this.props.inputParamsChange(arr, newItem);
    }
    // 新建输入参数事件
    addInput () {
        console.log('add')
        const table = cloneDeep(this.state.dataSource);
        table.unshift({
            key: Math.random(),
            fieldName: '',
            paramType: '',
            paramName: '',
            operator: '',
            required: true,
            desc: '',
            isEdit: true
        });
        this.setState({
            dataSource: table
        }
        , () => {
            this.props.changeAddinputOverSignal();
        })
    }
    getTableColumns () {
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
    getColumnsView () {
        const { sourceColumn } = this.props.dataSource;
        // const data = cloneDeep(this.state.tableColumns);
        return sourceColumn.map(
            (item) => {
                return (<Option title={item.key} key={item.key} value={item.type + '@@' + item.key}>{item.key}</Option>)
            }
        )
    }
    // 字段改变
    changeTableParam (index, key) {
        key = key.split('@@');
        if (!key) {
            return;
        }
        console.log(index, key, 'key')
        const table = cloneDeep(this.state.dataSource);
        table[index].paramType = key[0];
        table[index].fieldName = key[1];
        // table[index].param = {
        //     paramType: key[0],
        //     key: key[1]
        // }
        this.setState({
            dataSource: table
        })
    }
    // 选择框改变
    setRequired (index, e) {
        const table = cloneDeep(this.state.dataSource);
        table[index].required = e.target.checked
        this.setState({
            dataSource: table
        })
    }
    // 编辑说明
    textAreaChange (index, e) {
        const value = e.target.value;

        const table = cloneDeep(this.state.dataSource);
        table[index].desc = value
        this.setState({
            dataSource: table
        })
    }
    // 参数名
    paramNameChange (index, e) {
        const value = e.target.value;

        const table = cloneDeep(this.state.dataSource);
        table[index].paramName = value
        this.setState({
            dataSource: table
        })
    }
    // 编辑操作符
    operatorsChange (index, value) {
        const table = cloneDeep(this.state.dataSource);
        table[index].operator = value
        this.setState({
            dataSource: table
        })
    }

    // 验证数据合法性
    checkInfo (index, isTmp) {
        const table = cloneDeep(this.state.dataSource);
        let checkItem = table[index];
        if (isTmp) {
            checkItem = checkItem.tmp;
        }
        if (!checkItem.fieldName) {
            message.error('请选择字段');
            return false;
        }
        if (!checkItem.paramName) {
            message.error('请填写参数名');
            return false;
        }
        if (!checkItem.operator) {
            message.error('请选择操作符');
            return false;
        }
        if (!this.checkVal(checkItem)) {
            return false;
        }
        for (let i in table) {
            if (i == index) {
                continue;
            }
            if (table[i].fieldName == checkItem.fieldName && table[i].operator == checkItem.operator) {
                if (isTmp) {
                    message.error('原数据与现有数据重复，请修改保存');
                } else {
                    message.error('不能设置相同字段的相同规则')
                }

                return false;
            }
        }
        return true;
    }

    checkVal (item) {
        if (item.desc && item.desc.length > 200) {
            message.error('说明不得大于200字符')
            return false;
        }
        if (item.paramName && item.paramName.length > 16) {
            message.error('参数名不得大于16字符')
            return false;
        }

        return true;
    }

    // 保存信息
    saveInfo (index) {
        const table = cloneDeep(this.state.dataSource);
        if (!this.checkInfo(index)) {
            return;
        }

        table[index].isEdit = false;
        const isNew = !table[index].tmp;// 是否新增
        if (table[index].tmp) {
            table[index].tmp = null;
        }
        if (isNew) { // 假如新增，则添加新增字段
            this.backMsg(table, table[index]);
        } else {
            this.backMsg(table);
        }

        this.setState({
            dataSource: table
        })
    }

    // 取消保存，假如是已有信息，则回滚，新增的则直接删除
    cancelSave (index) {
        const table = cloneDeep(this.state.dataSource);

        if (table[index].tmp) {
            // 回滚时候，需要确认不会重复
            if (!this.checkInfo(index, true)) {
                return;
            }
            table[index] = cloneDeep(table[index].tmp);
            table[index].isEdit = false;
        } else {
            table.splice(index, 1);
        }

        this.setState({
            dataSource: table
        })
    }

    // 编辑操作
    editInfo (index) {
        const table = cloneDeep(this.state.dataSource);

        table[index].tmp = cloneDeep(table[index]);
        table[index].isEdit = true;

        this.setState({
            dataSource: table
        })
    }

    // 删除
    removeInfo (index) {
        const table = cloneDeep(this.state.dataSource);
        table.splice(index, 1);
        this.backMsg(table);
        this.setState({
            dataSource: table
        })
    }

    initColumns () {
        return [{
            title: '字段',
            dataIndex: 'fieldName',
            key: 'fieldName',
            width: '200px',
            render: (text, record, index) => {
                if (record.isEdit) {
                    return (
                        <Select
                            showSearch
                            defaultValue={record.paramType ? (record.paramType + '@@' + record.fieldName) : null}
                            onChange={this.changeTableParam.bind(this, index)} style={{ width: '100%' }}>
                            {this.getColumnsView()}
                        </Select>
                    )
                }
                return record.fieldName;
            }
        }, {
            title: '参数名',
            dataIndex: 'paramName',
            key: 'paramName',
            width: '100px',
            render: (text, record, index) => {
                return record.isEdit
                    ? <Input
                        onBlur={this.paramNameChange.bind(this, index)}
                        defaultValue={record.paramName}
                    />
                    : record.paramName;
            }
        }, {
            title: '数据类型',
            dataIndex: 'paramType',
            key: 'paramType',
            render (text, record) {
                return record.paramType || '';
            }
        }, {
            title: '操作符',
            dataIndex: 'operator',
            key: 'operator',
            width: '100px',
            render: (text, record, index) => {
                if (record.isEdit) {
                    return (
                        <Select
                            style={{ width: '100%' }}
                            defaultValue={record.operator}
                            onChange={this.operatorsChange.bind(this, index)}>
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
                    )
                }
                return record.operator;
            }
        },
        {
            title: '是否必填',
            dataIndex: 'required',
            key: 'required',
            render: (text, record, index) => {
                return record.isEdit
                    ? <Checkbox
                        onClick={this.setRequired.bind(this, index)}
                        defaultChecked={record.required}
                    />
                    : <Checkbox defaultChecked={record.required} disabled />
            }
        },
        {
            title: '说明',
            dataIndex: 'desc',
            key: 'desc',
            render: (text, record, index) => {
                return record.isEdit
                    ? <TextArea
                        onBlur={this.textAreaChange.bind(this, index)}
                        defaultValue={record.desc}
                    />
                    : <TextArea
                        key="textareadisabled"
                        defaultValue={record.desc}
                        disabled
                    />
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
        }];
    }

    render () {
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

export default InputParams
