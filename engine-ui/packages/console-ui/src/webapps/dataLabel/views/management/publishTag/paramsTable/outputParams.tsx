import * as React from 'react';
import { connect } from 'react-redux';
import { Input, Select, Table, message } from 'antd';
import { cloneDeep } from 'lodash';

const TextArea = Input.TextArea;
const Option = Select.Option;

const mapStateToProps = (state: any) => {
    const { dataSource } = state;
    return { dataSource }
}

const mapDispatchToProps = (dispatch: any) => ({

})

@(connect(mapStateToProps, mapDispatchToProps) as any)
class OutputParams extends React.Component<any, any> {
    state: any = {
        loading: false,
        tableColumns: [],
        dataSource: []
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps(nextProps: any) {
        if (nextProps.addOutputsignal && !this.props.addOutputsignal) {
            this.addOutput(nextProps.inputToOutputData);
        }
        // input参数不等，添加新的值
        if (nextProps.initValue != this.props.initValue) {
            this.initDataSource(nextProps.initValue);
        }
    }
    initDataSource(initValue: any) {
        let data = initValue;
        if (!initValue) {
            data: [];
        }
        if (initValue && initValue.some((item: any) => !item.fieldName)) {
            data: [];
        }
        this.backMsg(data);
        this.setState({
            dataSource: data
        });
    }

    // 通知父组件最新的值
    backMsg(table: any) {
        let arr: any = [];
        for (let i in table) {
            let item = table[i];
            if (item.tmp) { // 假如存在回滚数据，则返回tmp
                arr.push(item.tmp)
            } else if (!item.isEdit) { // 不存在回滚，并且不处于编辑状态，则返回item
                arr.push(item);
            }
        }
        this.props.outputParamsChange(arr);
    }

    addOutput(inputToOutputData: any) {
        const table = cloneDeep(this.state.dataSource);
        let dic = {
            key: Math.random(),
            fieldName: '',
            paramType: '',
            paramName: '',
            operator: '',
            required: true,
            desc: '',
            isEdit: true
        }

        if (inputToOutputData) {
            console.log(inputToOutputData)

            dic.fieldName = inputToOutputData.fieldName;
            dic.paramType = inputToOutputData.paramType;
            dic.paramName = inputToOutputData.paramName;
            dic.operator = inputToOutputData.operator;
            dic.required = inputToOutputData.required;
            dic.desc = inputToOutputData.desc;
        }
        table.unshift(dic);

        this.setState({
            dataSource: table
        }
        , () => {
            this.props.changeAddoutputOverSignal();
        })
    }

    getTableColumns () {
        this.props.tablecolumn(this.props.dataSourceId, this.props.tableId)
            .then(
                (res: any) => {
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
        return sourceColumn.map(
            (item: any) => {
                return (<Option title={item.key} key={item.key} value={item.type + '@@' + item.key}>{item.key}</Option>)
            }
        )
    }

    // 字段改变
    changeTableParam(index: any, key: any) {
        key = key.split('@@');
        if (!key) {
            return;
        }
        const table = cloneDeep(this.state.dataSource);
        table[index].paramType = key[0];
        table[index].fieldName = key[1];

        this.setState({
            dataSource: table
        })
    }
    // 选择框改变
    setRequired(index: any, e: any) {
        const table = cloneDeep(this.state.dataSource);
        table[index].required = e.target.checked
        this.setState({
            dataSource: table
        })
    }
    // 编辑说明
    textAreaChange(index: any, e: any) {
        const value = e.target.value;

        const table = cloneDeep(this.state.dataSource);
        table[index].desc = value
        this.setState({
            dataSource: table
        })
    }
    // 参数名
    paramNameChange(index: any, e: any) {
        const value = e.target.value;

        const table = cloneDeep(this.state.dataSource);
        table[index].paramName = value
        this.setState({
            dataSource: table
        })
    }
    // 编辑操作符
    operatorsChange(index: any, value: any) {
        const table = cloneDeep(this.state.dataSource);
        table[index].operator = value
        this.setState({
            dataSource: table
        })
    }
    // 验证数据合法性
    checkInfo(index: any, isTmp?: any) {
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
        if (!this.checkVal(checkItem)) {
            return false;
        }
        for (let i in table) {
            if (i == index) {
                continue;
            }
            if (table[i].fieldName == checkItem.fieldName) {
                if (isTmp) {
                    message.error('原数据与现有数据重复，请修改保存');
                } else {
                    message.error('不能设置相同字段')
                }

                return false;
            }
        }
        return true;
    }
    checkVal(item: any) {
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
    saveInfo(index: any) {
        const table = cloneDeep(this.state.dataSource);
        if (!this.checkInfo(index)) {
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
    // 取消保存，假如是已有信息，则回滚，新增的则直接删除
    cancelSave(index: any) {
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
    editInfo(index: any) {
        const table = cloneDeep(this.state.dataSource);

        table[index].tmp = cloneDeep(table[index]);
        table[index].isEdit = true;

        this.setState({
            dataSource: table
        })
    }
    // 删除
    removeInfo(index: any) {
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
            render: (text: any, record: any, index: any) => {
                if (record.isEdit) {
                    return (
                        <Select showSearch defaultValue={record.paramType ? (record.paramType + '@@' + record.paramType) : null} onChange={this.changeTableParam.bind(this, index)} style={{ width: '100%' }} >
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
            render: (text: any, record: any, index: any) => {
                if (record.isEdit) {
                    return (
                        <Input onBlur={this.paramNameChange.bind(this, index)} defaultValue={record.paramName} />
                    )
                }
                return record.paramName;
            }
        }, {
            title: '数据类型',
            dataIndex: 'paramType',
            key: 'paramType',
            render(text: any, record: any) {
                return record.paramType || '';
            }

        },

        {
            title: '说明',
            dataIndex: 'desc',
            key: 'desc',
            render: (text: any, record: any, index: any) => {
                if (record.isEdit) {
                    return (<TextArea onBlur={this.textAreaChange.bind(this, index)} defaultValue={record.desc} />)
                }
                return (<TextArea key="textareadisabled" defaultValue={record.desc} disabled />)
            }
        },
        {
            title: '操作',
            dataIndex: 'deal',
            key: 'deal',
            render: (text: any, record: any, index: any) => {
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

export default OutputParams
