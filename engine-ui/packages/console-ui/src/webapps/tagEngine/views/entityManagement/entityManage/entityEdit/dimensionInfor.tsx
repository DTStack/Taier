import * as React from 'react';
import { Input, Table, Checkbox, Icon, Select, Tooltip, message as Message, Tag } from 'antd';
import ConfigDictModal from './configDictModal';
import './style.scss';
import { isEqual, isEmpty } from 'lodash';

const { Search } = Input;
const { Option } = Select;

interface Iprops {
    infor: any[];
    handleChange: any;
}

interface IState {
    dataSource: any[];
    searchVal: any;
    dataTypeOption: any[];
    configModalVisble: boolean;
    total: number;
    selectNum: number;
}

export default class DimensionInfor extends React.Component<Iprops, IState> {
    state: IState = {
        dataSource: [],
        searchVal: undefined,
        dataTypeOption: [
            { label: '字符型', value: 'char' },
            { label: '数值型', value: 'number' },
            { label: '时间型', value: 'time' }
        ],
        configModalVisble: false,
        total: 0,
        selectNum: 0

    }

    componentDidMount () {
        const { infor } = this.props;
        let selectNum = infor.filter((item: any) => { return item.select; }).length;
        this.setState({
            dataSource: infor,
            total: infor.length,
            selectNum
        })
    }

    componentDidUpdate (preProps: any) {
        const { infor } = this.props;
        const { searchVal } = this.state;
        if (!isEqual(infor, preProps.infor)) {
            let selectNum = infor.filter((item: any) => { return item.select; }).length;
            let newDataSource = infor.filter((item: any) => {
                return isEmpty(searchVal) || (item.name && item.name.indexOf(searchVal) != -1) || (item.chName && item.chName.indexOf(searchVal) != -1)
            });
            this.setState({
                dataSource: newDataSource,
                selectNum,
                total: infor.length
            })
        }
    }

    handleSearch = (val: string) => {
        const { infor } = this.props;
        let newDataSource = infor.filter((item: any) => {
            return isEmpty(val) || (item.name && item.name.indexOf(val) != -1) || (item.chName && item.chName.indexOf(val) != -1)
        });
        this.setState({
            searchVal: val,
            dataSource: newDataSource
        })
    }

    handleConfig = () => {
        this.setState({
            configModalVisble: true
        })
    }

    handleModelOk = () => {
        this.setState({
            configModalVisble: false
        })
    }

    handleModelCancel = () => {
        this.setState({
            configModalVisble: false
        })
    }

    handleTableChange = (type: string, record: any, e: any) => {
        const infor = [...this.props.infor];
        let editIndex = infor.findIndex((item: any) => { return item.id == record.id; });
        switch (type) {
            case 'select': {
                infor[editIndex] = {
                    ...record,
                    select: e.target.checked
                }
                break;
            }
            case 'chName': {
                let inputVal = e.target.value;
                if (inputVal && inputVal.length > 20) {
                    Message.warning('字符长度不可超过20！');
                    return false;
                }
                infor[editIndex] = {
                    ...record,
                    chName: inputVal
                }
                break;
            }
            case 'dataType': {
                infor[editIndex] = {
                    ...record,
                    type: e
                }
                break;
            }
            default: ;
        }
        this.props.handleChange([...infor]);
    }

    initColumns = () => {
        const { dataTypeOption } = this.state;
        return [{
            title: '操作',
            dataIndex: 'select',
            key: 'select',
            width: 150,
            render: (text: boolean, record: any) => {
                return <Checkbox disabled={record.isKey} onChange={this.handleTableChange.bind(this, 'select', record)} checked={text} />
            }
        }, {
            title: '维度名称',
            dataIndex: 'name',
            key: 'name',
            width: 200,
            render: (text: any, record: any) => {
                return (
                    <div className="di-table-name-col">
                        <div className="tag-box">
                            {record.new ? <Tag color="green">新增</Tag> : null}
                            {record.isKey ? <a><i className='iconfont iconicon_key'></i></a> : null}
                        </div>
                        <span>{text}</span>
                        {record.isKey ? '(主键)' : ''}
                    </div>
                )
            }
        }, {
            title: <span>中文名<i className="iconfont iconicon_import" style={{ marginLeft: '10px', color: '#999999', cursor: 'pointer', fontSize: 13 }} onClick={this.handleConfig}></i></span>,
            dataIndex: 'chName',
            key: 'chName',
            width: 200,
            render: (text: any, record: any) => {
                return (<Input style={{ width: 150 }} value={text} onChange={this.handleTableChange.bind(this, 'chName', record)} />)
            }
        }, {
            title: '数据类型',
            dataIndex: 'type',
            key: 'type',
            width: 200,
            render: (text: any, record: any) => {
                return (<Select
                    style={{ width: 150 }}
                    value={text || 'char'}
                    placeholder="请选择属性分组"
                    onChange={this.handleTableChange.bind(this, 'dataType', record)}
                >
                    {dataTypeOption.map((item: any) => (
                        <Option key={item.value} value={item.value}>{item.label}</Option>
                    ))}
                </Select>)
            }
        }, {
            title: '属性值数量',
            dataIndex: 'propertyNum',
            key: 'propertyNum',
            width: 150
        }, {
            title: <span>多值列
                <Tooltip title="勾选多值将标识此属性可有多个值同时存在，默认分隔符为“，”">
                    <Icon style={{ marginLeft: '10px', color: '#999999', cursor: 'pointer' }} type="question-circle-o" />
                </Tooltip>
            </span>,
            dataIndex: 'isMultiply',
            key: 'isMultiply',
            render: (text: boolean, record: any) => {
                return text ? '是' : '';
            }
        }];
    }

    render () {
        const { dataSource, configModalVisble, total, selectNum } = this.state;
        return (
            <div className="dimension-infor">
                <div className="top-box">
                    <div>
                        <span>共计&nbsp;{total}个&nbsp;数据维度</span>
                        <span style={{ marginLeft: 12 }}>已选择&nbsp;<a>{selectNum}</a>个</span>
                    </div>
                    <Search
                        placeholder="搜索维度名称、中文名称"
                        style={{ width: 200, padding: 0 }}
                        onSearch={this.handleSearch}
                    />
                </div>
                <Table
                    rowKey="id"
                    className="di-table-border"
                    pagination={false}
                    loading={false}
                    columns={this.initColumns()}
                    scroll={{ y: 400 }}
                    dataSource={dataSource}
                />
                <ConfigDictModal
                    visible={configModalVisble}
                    isLabel={false}
                    onOk={this.handleModelOk}
                    onCancel={this.handleModelCancel}
                />
            </div>
        )
    }
}
