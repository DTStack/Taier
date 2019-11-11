import * as React from 'react';
import { Input, Table, Checkbox, Icon, Select, Tooltip } from 'antd';
import ConfigDictModal from './configDictModal';

const { Search } = Input;
const { Option } = Select;

export default class DimensionInfor extends React.Component<any, any> {
    state: any = {
        dataSource: [
            { operate: false, name: 'xxx1', chName: '中文名', type: 'char', propertyNum: 200, isMutiply: false, isKey: true },
            { operate: false, name: 'xxx2', chName: '中文名', type: 'number', propertyNum: 100, isMutiply: false, isKey: false },
            { operate: false, name: 'xxx3', chName: '中文名', type: undefined, propertyNum: 30, isMutiply: false, isKey: false }
        ],
        searchVal: undefined,
        dataTypeOption: [
            { label: '字符型', value: 'char' },
            { label: '数值型', value: 'number' },
            { label: '时间型', value: 'time' }
        ],
        configModalVisble: false

    }

    componentDidMount () {

    }

    handleSearch = () => {

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

    handleDataTypeSelect = (record: any, e: any) => {

    }

    handleChNameChange = (record: any, e: any) => {
        // TODO 需要限制字数不超过20
    }

    initColumns = () => {
        const { dataTypeOption } = this.state;
        return [{
            title: '操作',
            dataIndex: 'operate',
            key: 'operate',
            width: 150,
            render: (text: any, record: any) => {
                return <Checkbox checked={false} />
            }
        }, {
            title: '维度名称',
            dataIndex: 'name',
            key: 'name',
            width: 200,
            render: (text: any, record: any) => {
                return (<span>
                    {text}
                    {record.isKey ? '(主键)' : ''}
                </span>)
            }
        }, {
            title: <span>中文名<Icon style={{ marginLeft: '5px' }} type="setting" onClick={this.handleConfig} /></span>,
            dataIndex: 'chName',
            key: 'chName',
            width: 200,
            render: (text: any, record: any) => {
                return (<Input style={{ width: 150 }} value={text} onChange={this.handleChNameChange.bind(this, record)} />)
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
                    onChange={this.handleDataTypeSelect.bind(this, record)}
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
                    <Icon style={{ marginLeft: '5px' }} type="question-circle-o" />
                </Tooltip>
            </span>,
            dataIndex: 'isMutiply',
            key: 'isMutiply',
            render: (text: any, record: any) => {
                return record.isKey ? '' : <Checkbox checked={false} />
            }
        }];
    }

    render () {
        const { dataSource, configModalVisble } = this.state;
        return (
            <div className="dimension-infor">
                <div>
                    <div>
                        <span>共计7个数据维度</span>
                        <span style={{ color: 'blue' }}>已选择5个</span>
                    </div>
                    <Search
                        placeholder="搜索维度名称、中文名称"
                        style={{ width: 200, padding: 0 }}
                        onSearch={this.handleSearch}
                    />
                </div>
                <Table
                    rowKey="id"
                    className="dt-ant-table dt-ant-table--border full-screen-table-47"
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
