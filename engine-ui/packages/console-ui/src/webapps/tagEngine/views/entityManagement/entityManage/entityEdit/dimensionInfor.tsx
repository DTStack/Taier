import * as React from 'react';
import { Input, Table, Checkbox, Icon, Select, Tooltip, message as Message, Tag } from 'antd';
import ConfigDictModal from './configDictModal';
import EditInput from '../../../../components/editInput';
import './style.scss';
import { API } from '../../../../api/apiMap';
import { isEqual, isEmpty } from 'lodash';

const { Search } = Input;
const { Option } = Select;

interface Iprops {
    infor: any[];
    handleChange: any;
    attrTypeOptions: any[];
    baseInfor: any;
}

interface IState {
    dataSource: any[];
    searchVal: any;
    configModalVisble: boolean;
    total: number;
    selectNum: number;
    configModalKey: number;
}

export default class DimensionInfor extends React.Component<Iprops, IState> {
    state: IState = {
        dataSource: [],
        searchVal: undefined,
        configModalVisble: false,
        total: 0,
        selectNum: 0,
        configModalKey: +new Date()
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
                return isEmpty(searchVal) || (item.entityAttr && item.entityAttr.indexOf(searchVal) != -1) || (item.entityAttrCn && item.entityAttrCn.indexOf(searchVal) != -1)
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
            return isEmpty(val) || (item.entityAttr && item.entityAttr.indexOf(val) != -1) || (item.entityAttrCn && item.entityAttrCn.indexOf(val) != -1)
        });
        this.setState({
            searchVal: val,
            dataSource: newDataSource
        })
    }

    handleConfig = () => {
        this.setState({
            configModalVisble: true,
            configModalKey: +new Date()
        })
    }

    handleModelOk = (value) => {
        const { infor } = this.props;
        API.getDictDetail({
            dictId: value.dictRef
        }).then((res: any) => {
            const { data = {}, code } = res;
            if (code === 1) {
                const { dictValueVoList = [] } = data;
                let newInfor = infor.map(item => {
                    let relaItem = dictValueVoList.find(ele => ele.value == item.entityAttr);
                    return {
                        ...item,
                        entityAttrCn: relaItem ? relaItem.valueName : item.entityAttrCn
                    }
                })
                this.props.handleChange(newInfor);
                this.setState({
                    configModalVisble: false
                })
            }
        })
    }

    handleModelCancel = () => {
        this.setState({
            configModalVisble: false
        })
    }

    handleTableChange = (type: string, record: any, e: any) => {
        const infor = [...this.props.infor];
        let editIndex = infor.findIndex((item: any) => { return item.entityAttr == record.entityAttr; });
        switch (type) {
            case 'isAtomTag': {
                infor[editIndex] = {
                    ...record,
                    isAtomTag: e.target.checked
                }
                break;
            }
            case 'entityAttrCn': {
                let inputVal = e.target.value;
                if (inputVal && inputVal.length > 20) {
                    Message.warning('字符长度不可超过20！');
                    return false;
                }
                infor[editIndex] = {
                    ...record,
                    entityAttrCn: inputVal
                }
                break;
            }
            case 'dataType': {
                infor[editIndex] = {
                    ...record,
                    dataType: e
                }
                break;
            }
            default: ;
        }
        this.props.handleChange([...infor]);
    }

    initColumns = () => {
        const { attrTypeOptions, baseInfor } = this.props;
        let isEdit = false;
        if (baseInfor.id) {
            isEdit = true;
        }
        return [{
            title: '操作',
            dataIndex: 'isAtomTag',
            key: 'isAtomTag',
            width: 150,
            render: (text: boolean, record: any) => {
                return <Checkbox disabled={(isEdit && record.id) || record.isPrimaryKey} onChange={this.handleTableChange.bind(this, 'isAtomTag', record)} checked={text} />
            }
        }, {
            title: '维度名称',
            dataIndex: 'entityAttr',
            key: 'entityAttr',
            width: 200,
            render: (text: any, record: any) => {
                return (
                    <div className="di-table-name-col">
                        <div className="tag-box">
                            {record.id ? <Tag color="green">新增</Tag> : null}
                            {record.isPrimaryKey ? <a><i className='iconfont iconicon_key'></i></a> : null}
                        </div>
                        <span>{text}</span>
                        {record.isPrimaryKey ? '(主键)' : ''}
                    </div>
                )
            }
        }, {
            title: <span>中文名<i className="iconfont iconicon_import" style={{ marginLeft: '10px', color: '#999999', cursor: 'pointer', fontSize: 13 }} onClick={this.handleConfig}></i></span>,
            dataIndex: 'entityAttrCn',
            key: 'entityAttrCn',
            width: 200,
            render: (text: any, record: any) => {
                return <EditInput
                    onChange={this.handleTableChange.bind(this, 'entityAttrCn', record)}
                    value={text}
                    style={{ width: 150 }}
                />
            }
        }, {
            title: '数据类型',
            dataIndex: 'dataType',
            key: 'dataType',
            width: 200,
            render: (text: any, record: any) => {
                return (<Select
                    style={{ width: 150 }}
                    value={text}
                    placeholder="请选择属性分组"
                    onChange={this.handleTableChange.bind(this, 'dataType', record)}
                >
                    {attrTypeOptions.map((item: any) => (
                        <Option key={item.value} value={item.value}>{item.label}</Option>
                    ))}
                </Select>)
            }
        }, {
            title: '属性值数量',
            dataIndex: 'tagValueCount',
            key: 'tagValueCount',
            width: 150
        }, {
            title: <span>多值列
                <Tooltip title="勾选多值将标识此属性可有多个值同时存在，默认分隔符为“，”">
                    <Icon style={{ marginLeft: '10px', color: '#999999', cursor: 'pointer' }} type="question-circle-o" />
                </Tooltip>
            </span>,
            dataIndex: 'isMultipleValue',
            key: 'isMultipleValue',
            render: (text: boolean, record: any) => {
                return text ? '是' : '';
            }
        }];
    }

    render () {
        const { dataSource, configModalVisble, total, selectNum, configModalKey } = this.state;
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
                    rowKey="entityAttr"
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
                    key={configModalKey}
                    onOk={this.handleModelOk}
                    onCancel={this.handleModelCancel}
                />
            </div>
        )
    }
}
