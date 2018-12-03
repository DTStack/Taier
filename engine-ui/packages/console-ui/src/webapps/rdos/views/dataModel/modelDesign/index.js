import React, { Component } from 'react';
import { findDOMNode } from 'react-dom';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Form,
    Modal, message, Card, Select
} from 'antd';

import { Link } from 'react-router';

import Editor from 'widgets/editor';
import CopyIcon from 'main/components/copy-icon';
import { DDL_PLACEHOLDER } from '../../../comm/DDLCommon'
import SlidePane from 'widgets/slidePane';
import TableLog from '../../dataManage/tableLog';

import ajax from '../../../api/dataModel';

const FormItem = Form.Item;
const Option = Select.Option;

class TableList extends Component {
    constructor (props) {
        super(props);

        this.state = {
            visible: false,
            filterDropdownVisible: false,
            params: {
                pageIndex: 1,
                tableName: '',
                isDeleted: 0, // 添加删除标记
                isDirtyDataTable: 0 // 非脏数据标记
            },
            tableLog: {
                tableId: undefined,
                tableName: undefined,
                visible: false
            },
            table: { data: [] },
            subjectFields: [],
            modelLevels: []
        }
    }

    componentDidMount () {
        this.search();
        this.loadOptionData();
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.setState({ current: 1 }, () => {
                this.search()
            })
        }
    }

    search = () => {
        const { params } = this.state;
        ajax.getTableList(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data
                })
            }
        })
    }

    loadOptionData = () => {
        ajax.getModels({
            currentPage: 1,
            pageSize: 1000,
            type: 1 // 模型层级
        }).then(res => {
            if (res.code === 1) {
                this.setState({
                    modelLevels: res.data ? res.data.data : []
                })
            }
        });
        ajax.getModels({
            currentPage: 1,
            pageSize: 1000,
            type: 2 // 主题域
        }).then(res => {
            if (res.code === 1) {
                this.setState({
                    subjectFields: res.data ? res.data.data : []
                })
            }
        });
    }

    cleanSearch () {
        const $input = findDOMNode(this.searchInput).querySelector('input');// eslint-disable-line

        if ($input.value.trim() === '') return;

        $input.value = '';
        this.search();
    }

    changeParams = (field, value) => {
        let params = Object.assign(this.state.params);
        if (field) {
            params[field] = value;
        }
        this.setState({
            params
        }, this.search)
    }

    onTableNameChange = (e) => {
        this.setState({
            params: Object.assign(this.state.params, {
                pageIndex: 1,
                tableName: e.target.value
            })
        })
    }

    showModal () {
        this.setState({
            visible: true
        });
    }

    handleOk () {
        if (this._DDL) {
            ajax.createTableByDDL({
                sql: this._DDL
            }).then(res => {
                if (res.code === 1) {
                    this._DDL = undefined;
                    // 设置值
                    this.DDLEditor.setValue('');
                    this.setState({
                        visible: false
                    });
                    message.success('建表成功');
                    this.search();
                }
            })
        } else {
            message.error('请输入建表语句!');
        }
    }

    handleCancel () {
        this._DDL = undefined;
        this.DDLEditor.setValue('');
        this.setState({
            visible: false
        })
    }

    handleDdlChange (value) {
        this._DDL = value;
    }

    showTableLog (table) {
        const { id, tableName } = table;
        const { tableLog } = this.state;
        tableLog.tableId = id;
        tableLog.tableName = tableName;
        tableLog.visible = true;
        this.setState({
            tableLog
        })
    }

    closeSlidePane = () => {
        const { tableLog } = this.state;
        tableLog.visible = false;
        tableLog.tableId = undefined;
        tableLog.tableName = undefined;
        this.setState({
            tableLog
        })
    }

    render () {
        const ROUTER_BASE = '/data-model/table';
        const { subjectFields, modelLevels, params, tableLog } = this.state
        const tableList = this.state.table;
        const { project } = this.props;
        const { totalCount, data } = tableList;
        const projectUsers = [];
        const pagination = {
            total: totalCount,
            defaultPageSize: 10,
            current: params.pageIndex
        };

        const marginTop10 = { marginTop: '8px' };

        const subjectFieldsOptions = subjectFields && subjectFields.map(field =>
            <Option key={field.id} value={field.name}>{field.name}</Option>
        )

        const modelLevelOptions = modelLevels && modelLevels.map(level =>
            <Option key={level.id} value={level.name}>{level.name}</Option>
        )
        const ctx = this;
        const columns = [
            {
                title: '表名',
                width: 120,
                key: 'tableName',
                dataIndex: 'tableName',
                render (text, record) {
                    return <Link to={`data-manage/table/view/${record.id}`}>{text}</Link>
                }
            },
            {
                title: '表描述',
                width: 150,
                key: 'tableDesc',
                dataIndex: 'tableDesc',
                render (text, record) {
                    return text
                }
            },
            {
                title: '所属项目',
                key: 'project',
                dataIndex: 'project',
                render (text, record) {
                    return project && project.projectName;
                }
            },
            {
                title: '模型层级',
                key: 'grade',
                width: 90,
                dataIndex: 'grade'
            },
            {
                title: '主题域',
                key: 'subject',
                width: 90,
                dataIndex: 'subject'
            },
            {
                title: '生命周期',
                key: 'lifeDay',
                width: 90,
                dataIndex: 'lifeDay',
                render (text, record) {
                    return `${text}天`;
                }
            },
            {
                title: '负责人',
                key: 'chargeUser',
                dataIndex: 'chargeUser',
                render (text, record) {
                    return text
                }
            },
            {
                title: '操作',
                key: 'action',
                render (text, record) {
                    return <span>
                        <Link to={`${ROUTER_BASE}/modify/${record.id}`}>编辑</Link>
                        <span className="ant-divider"></span>
                        {/* <Link to={`/data-manage/log/${record.id}/${record.tableName}`}>操作记录</Link> */}
                        <a href="javascript:void(0)" onClick={ctx.showTableLog.bind(ctx, record)}>操作记录</a>
                    </span>
                }
            }
        ];

        const title = (
            <Form className="m-form-inline" layout="inline" style={marginTop10}>
                <FormItem label="主题域">
                    <Select
                        allowClear
                        placeholder="选择主题域"
                        style={{ width: '120px' }}
                        onChange={(value) => this.changeParams('subject', value)}
                    >
                        {subjectFieldsOptions}
                    </Select>
                </FormItem>
                <FormItem label="模型层级">
                    <Select
                        allowClear
                        placeholder="选择模型层级"
                        onChange={(value) => this.changeParams('grade', value)}
                        style={{ width: '120px' }}
                    >
                        {modelLevelOptions}
                    </Select>
                </FormItem>
                <FormItem>
                    <Input.Search
                        placeholder="按表名搜索"
                        style={{ width: 200 }}
                        size="default"
                        onChange={this.onTableNameChange}
                        onSearch={this.search}
                        ref={el => this.searchInput = el}
                    />
                </FormItem>
            </Form>
        )

        const extra = (
            <div style={marginTop10}>
                <Button type="primary" style={{ float: 'right', marginLeft: 5 }}>
                    <Link to={`${ROUTER_BASE}/design`}>模型建表</Link>
                </Button>
                <Button type="primary" style={{ float: 'right', marginLeft: 5 }}>
                    <Link to={`/data-model/table/create`}>普通建表</Link>
                </Button>
                <Button type="primary" style={{ float: 'right' }}
                    onClick={this.showModal.bind(this)}
                >DDL建表</Button>
            </div>
        )

        return <div className="m-tablelist">
            <h1 className="box-title"> 表管理 </h1>
            <div className="box-2 m-card card-tree-select">
                <Card noHovering bordered={false} title={title} extra={extra} className="full-screen-table-70">
                    <div style={{ marginTop: '1px' }}>
                        <Table
                            rowKey="id"
                            className="m-table"
                            columns={columns}
                            dataSource={data}
                            pagination={pagination}
                            onChange={(pagination) => this.changeParams('pageIndex', pagination.current)}
                        />
                        <Modal className="m-codemodal"
                            width={750}
                            title={(
                                <span>DDL建表<CopyIcon title="复制模版" style={{ marginLeft: '8px' }} copyText={DDL_PLACEHOLDER} /></span>
                            )}
                            visible={this.state.visible}
                            onOk={this.handleOk.bind(this)}
                            onCancel={this.handleCancel.bind(this)}
                            maskClosable={false}
                        >
                            <Editor
                                style={{ height: '400px' }}
                                placeholder={DDL_PLACEHOLDER}
                                language="dtsql"
                                options={{ readOnly: false }}
                                onChange={this.handleDdlChange.bind(this)}
                                value={this._DDL} editorInstanceRef={(e) => { this.DDLEditor = e }}
                            />
                        </Modal>
                    </div>
                </Card>
                {
                    tableLog.visible ? <SlidePane
                        onClose={this.closeSlidePane}
                        visible={tableLog.visible}
                        style={{ right: '-20px', width: '80%', height: '100%', minHeight: '600px' }}
                    >
                        <div className="m-loglist">
                            <TableLog key={tableLog.tableId} {...tableLog} projectUsers={projectUsers} />
                        </div>
                    </SlidePane> : ''
                }
            </div>
        </div>
    }
}

export default connect((state) => {
    return {
        project: state.project
    }
}, null)(TableList);
