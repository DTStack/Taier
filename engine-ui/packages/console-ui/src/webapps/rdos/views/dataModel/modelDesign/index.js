import React, { Component } from 'react';
import { findDOMNode } from 'react-dom';
import { connect } from 'react-redux';
import { 
    Input, Button, Table, Form,
    Pagination, Modal, message,
    Tag, Icon, Card, Select
} from 'antd';

import { Link } from 'react-router';

import Editor from '../../../components/code-editor';

import ajax from '../../../api/dataModel';

const FormItem = Form.Item;
const Option = Select.Option;

class TableList extends Component {

    constructor(props) {

        super(props);

        this.state = {
            visible: false,
            filterDropdownVisible: false,

            params: {
                currentPage: 1,
                fuzzyName: '',
                isDeleted: 0, // 添加删除标记
                isDirtyDataTable: 0, // 非脏数据标记
            },

            table: { data: [] },
            subjectFields: [], 
            modelLevels: []
        }
    }

    componentDidMount() {
        this.search();
    }

    componentWillReceiveProps(nextProps) {
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
            if(res.code === 1) {
                this.setState({
                    table: res.data,
                })
            }
        })
    }

    cleanSearch() {
        const $input = findDOMNode(this.searchInput).querySelector('input');

        if($input.value.trim() === '') return;

        $input.value = '';
        this.search();
    }

    changeParams = (field, value) => {
        let params = Object.assign(this.state.params);
        if (field) {
            params[field] = value;
        }
        this.setState({
            params,
        }, this.search)
    }

    onTableNameChange = (e) => {
        this.setState({
            params: Object.assign(this.state.params, {
                currentPage: 1,
                fuzzyName: e.target.value
            })
        })
    }

    showModal() {
        this.setState({
            visible: true
        });
    }

    handleOk() {
        if(this._DDL) {
            ajax.createTableByDDL({
                sql: this._DDL
            }).then(res => {
                if(res.code === 1) {
                    if(!res.data) {
                        this._DDL = undefined;
                        // 设置值
                        this.DDLEditor.self.doc.setValue('');
                        this.setState({
                            visible: false
                        });
                        message.info('建表成功');
                        this.search();
                    }
                    else {
                        message.error(res.data.message)
                    }
                }
            })
        }
        else {
            message.error('请输入建表语句!');
        }
    }

    handleCancel() {
        this._DDL = undefined;
        this.setState({
            visible: false
        })
    }

    handleDdlChange(previous, value) {
        this._DDL = value;
    }

    render() {
        const ROUTER_BASE = '/data-model/table';
        const { subjectFields, modelLevels } = this.state
        const tableList = this.state.table;
        const { project } = this.props;
        const { totalCount, currentPage, data } = tableList;

        const pagination = {
            total: totalCount,
            defaultPageSize: 20,
            current: currentPage,
        };

        const marginTop10 = { marginTop: '8px' };

        const subjectFieldsOptions = subjectFields && subjectFields.map(field =>
            <Option key={field.id} value={field.value}>{field.name}</Option>
        )

        const modelLevelOptions = modelLevels && modelLevels.map(level =>
            <Option key={level.id} value={level.value}>{level.name}</Option>
        )

        const columns = [
            {
                title: '表名',
                width: 120,
                key: 'tableName',
                dataIndex: 'tableName',
                render(text, record) {
                    return <Link to={`${ROUTER_BASE}/view/${record.tableId}`}>{ text }</Link>
                }
            },
            {
                title: '表描述',
                width: 150,
                key: 'tableDesc',
                dataIndex: 'tableDesc',
                render(text, record) {
                    return text
                }
            },
            {
                title: '所属项目',
                key: 'project',
                dataIndex: 'project',
                render(text, record) {
                    return project && project.projectName;
                },
            },
            {
                title: '模型层级',
                key: 'modelLevel',
                width: 90,
                dataIndex: 'modelLevel',
            },
            {
                title: '主题域',
                key: 'subjectField',
                width: 90,
                dataIndex: 'subjectField',
            },
            {
                title: '创建者',
                key: 'userName',
                dataIndex: 'userName',
                render(text, record) {
                    return text
                }
            },
            {
                title: '操作',
                key: 'action',
                render(text, record) {
                    return <span>
                        <Link to={`${ROUTER_BASE}/edit/${record.tableId}`}>编辑</Link>
                        <span className="ant-divider"></span>
                        <Link to={`/data-manage/log/${record.tableId}/${record.tableName}`}>删除</Link>
                    </span>
                }
            }
        ];

        const title = (
            <Form className="m-form-inline" layout="inline" style={marginTop10}>
                <FormItem label="主题域">
                    <Select 
                        placeholder="选择主题域"
                        style={{ width: '120px'}}
                        onChange={(value) => this.changeParams('subject', value)}
                    >
                        { subjectFieldsOptions }
                    </Select>
                </FormItem>
                <FormItem label="模型层级">
                    <Select 
                        placeholder="选择模型层级"
                        onChange={(value) => this.changeParams('grade', value)}
                        style={{ width: '120px'}}
                    >
                        {  modelLevelOptions }
                    </Select>
                </FormItem>
                <FormItem>
                    <Input.Search
                        placeholder="按表名搜索"
                        style={{ width: 200 }}
                        size="default"
                        onChange={ this.onTableNameChange }
                        onSearch={ this.search }
                        ref={ el => this.searchInput = el }
                    />
                </FormItem>
            </Form>
        )

        const extra = (
            <div style={marginTop10}>
                <Button type="primary" style={{ float: 'right', marginLeft: 5 }}>
                    <Link to={`${ROUTER_BASE}/design`}>新建表</Link>
                </Button>
                <Button type="primary" style={{ float: 'right' }}
                    onClick={ this.showModal.bind(this) }
                >DDL建表</Button>
            </div>
        )

        return <div className="m-tablelist">
            <h1 className="box-title"> 表管理 </h1>
            <div className="box-2 m-card card-tree-select">
                <Card noHovering bordered={false} title={title} extra={extra}>
                    <div style={{ marginTop: '1px' }}>
                        <Table
                            rowKey="tableId"
                            className="m-table"
                            columns={ columns }
                            dataSource={ data }
                            pagination={ pagination }
                            onChange={(pagination) => this.changeParams('currentPage', pagination.current )}
                        />
                        <Modal className="m-codemodal"
                            width={750}
                            title="DDL建表"
                            visible={this.state.visible}
                            onOk={this.handleOk.bind(this)}
                            onCancel={this.handleCancel.bind(this)}
                        >
                            <Editor
                                onChange={ this.handleDdlChange.bind(this) } 
                                value={ this._DDL } ref={(e) => { this.DDLEditor = e }}
                            />
                        </Modal>
                    </div>
                </Card>
            </div>
        </div>
    }
}

export default connect((state) => {
    return {
        project: state.project,
    }
}, null)(TableList);