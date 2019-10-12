import * as React from 'react';
import { connect } from 'react-redux';
import {
    Input, Table, Form, message,
    Card, Select, Spin, Tooltip
} from 'antd';
import { Link, hashHistory } from 'react-router';
import utils from 'utils';
import { APPLY_RESOURCE_TYPE, TABLE_NAME_BY_TABLE_TYPE } from '../../../comm/const';
import { getTenantTableTypes } from '../../../store/modules/tableType'
import EngineSelect from '../../../components/engineSelect';
import CatalogueTree from '../catalogTree';
import TableApplyModal from './tableApply';
import ajax from '../../../api/dataManage';

const FormItem = Form.Item
const Option: any = Select.Option

const ROUTER_BASE = '/data-manage/table';

@(connect((state: any) => {
    return {
        allProjects: state.allProjects,
        allTenantsProjects: state.allTenantsProjects,
        dataCatalogues: state.dataManage.dataCatalogues,
        teantTableTypes: state.tableTypes.teantTableTypes
    }
}, (dispatch: any) => {
    return {
        getTenantTableTypes: (params: any) => {
            dispatch(getTenantTableTypes(params))
        }
    }
}) as any)
class SearchTable extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        const { pId, pageIndex, permissionStatus, tableName, catalogueId, tableType } = this.props.location.query;
        this.state = {
            visible: false,
            table: [],
            editRecord: {},
            cardLoading: false,
            dataCatalogue: [props.dataCatalogues],
            queryParams: {
                pId,
                pageIndex: pageIndex || 1,
                catalogueId,
                permissionStatus,
                tableName,
                tableType,
                pageSize: 20
            }
        }
    }

    componentDidMount () {
        this.props.getTenantTableTypes();
        this.search();
    }

    search = () => {
        this.setState({
            cardLoading: true,
            table: []
        })
        const { queryParams } = this.state;
        const pathname = this.props.location.pathname;
        hashHistory.push({
            pathname,
            query: queryParams
        })
        ajax.newSearchTable(queryParams).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    table: res.data,
                    cardLoading: false
                })
            } else {
                this.setState({
                    cardLoading: false
                })
            }
        })
    }

    apply = (applyData: any) => {
        const { editRecord } = this.state;
        const params: any = { ...applyData };
        params.applyResourceType = APPLY_RESOURCE_TYPE.TABLE;
        params.resourceId = editRecord.id;
        ajax.applyTable(params).then((res: any) => {
            if (res.code === 1) {
                message.success('申请成功！')
                this.setState({ visible: false }, this.search)
            }
        })
    }

    changeParams = (field: any, value: any) => {
        let queryParams = Object.assign(this.state.queryParams);
        queryParams.pageIndex = 1;
        if (field) {
            queryParams[field] = value === ' ' ? ' ' : value;
        }
        this.setState({
            queryParams
        }, this.search)
    }

    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        const queryParams = Object.assign(this.state.queryParams, {
            pageIndex: pagination.current
        })
        this.setState({
            queryParams
        }, this.search)
    }

    onTableNameChange = (e: any) => {
        this.setState({
            queryParams: Object.assign(this.state.queryParams, {
                tableName: e.target.value,
                pageIndex: 1
            })
        })
    }

    showModal = (record: any) => {
        this.setState({
            visible: true,
            editRecord: record
        });
    }

    characterProcess = (text = '', maxWidth = '300px') => {
        const style: any = { overflow: 'hidden',
            maxWidth,
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap' }
        const content = (
            <Tooltip title={text} >
                <div style ={style}>{text}</div>
            </Tooltip>
        )

        return content
    }

    initialColumns = () => {
        const ctx = this;
        return [
            {
                title: '表名',
                width: 120,
                key: 'tableName',
                dataIndex: 'tableName',
                render (text: any, record: any) {
                    return <Link to={`${ROUTER_BASE}/view/${record.id}`}>{text}</Link>
                }
            },
            {
                title: '类目',
                key: 'catalogue',
                dataIndex: 'catalogue',
                render (text: any, record: any) {
                    return text
                }
            },
            {
                title: '项目名称',
                key: 'project',
                dataIndex: 'project'
            },
            {
                title: '表类型',
                key: 'tableType',
                dataIndex: 'tableType',
                render (text: any, record: any) {
                    return (TABLE_NAME_BY_TABLE_TYPE as any)[text]
                }
            },
            {
                title: '项目显示名称',
                key: 'projectAlias',
                dataIndex: 'projectAlias'
            },
            {
                title: '负责人',
                key: 'chargeUser',
                dataIndex: 'chargeUser',
                render (text: any, record: any) {
                    return text
                }
            },
            {
                title: '描述',
                width: 150,
                key: 'tableDesc',
                dataIndex: 'tableDesc',
                render: (text: any) => this.characterProcess(text, '150px')

            },
            {
                title: '创建时间',
                key: 'gmtCreate',
                dataIndex: 'gmtCreate',
                render (text: any, record: any) {
                    return utils.formatDateTime(text)
                }
            },
            {
                title: '表结构最后变更时间',
                key: 'lastDdlTime',
                dataIndex: 'lastDdlTime',
                render (text: any) {
                    return utils.formatDateTime(text)
                }
            },
            {
                title: '数据最后变更时间',
                key: 'lastDmlTime',
                dataIndex: 'lastDmlTime',
                render (text: any) {
                    return utils.formatDateTime(text)
                }
            },
            {
                title: '操作',
                key: 'id',
                dataIndex: 'permissionStatus',
                width: 100,
                render (status: any, record: any) {
                    // 授权状态 0-未授权，1-已授权,2-待审批
                    switch (status) {
                        case 0:
                            return <span><a onClick={() => ctx.showModal(record)}>申请授权</a></span>
                        case 1:
                            return <span>授权成功</span>
                        case 2:
                            return <span>等待授权</span>
                        default: return '-';
                        // default: return <span><a onClick={() => ctx.showModal(record)}>申请授权</a></span>
                    }
                }
            }
        ];
    }

    render () {
        const { table, queryParams, visible, editRecord, cardLoading, dataCatalogue } = this.state;
        const { allTenantsProjects, teantTableTypes } = this.props;
        const projectOptions = allTenantsProjects.map((proj: any) => <Option
            title={proj.projectAlias}
            key={proj.id}
            name={proj.projectAlias}
            value={`${proj.id}`}
        >
            {proj.projectAlias}
        </Option>)
        const title = (
            <Form className="m-form-inline" layout="inline">
                <FormItem label="类目">
                    <span style={{ width: 200, display: 'inline-block' }}>
                        <CatalogueTree
                            id="filter-catalogue"
                            isPicker
                            isFolderPicker
                            value={queryParams.catalogueId}
                            placeholder="按数据类目查询"
                            onChange={(value: any) => this.changeParams('catalogueId', value)}
                            treeData={dataCatalogue && dataCatalogue[0].children}
                        />
                    </span>
                </FormItem>
                <FormItem label="授权状态">
                    <Select
                        allowClear
                        style={{ width: 126 }}
                        placeholder="选择指标类型"
                        value={queryParams.permissionStatus}
                        onChange={(value: any) => this.changeParams('permissionStatus', value)}
                    >
                        <Option value=" ">全部表</Option>
                        <Option value="0">申请授权</Option>
                        <Option value="1">授权成功</Option>
                        <Option value="2">等待授权</Option>
                    </Select>
                </FormItem>
                <FormItem label="项目">
                    <Select
                        allowClear
                        showSearch
                        optionFilterProp="name"
                        style={{ width: 126 }}
                        placeholder="选择项目"
                        value={queryParams.pId}
                        onChange={(value: any) => this.changeParams('pId', value)}
                    >
                        {projectOptions}
                    </Select>
                </FormItem>
                <FormItem label="表类型">
                    <EngineSelect
                        allowClear
                        showSearch
                        style={{ width: 126 }}
                        placeholder="选择表类型"
                        tableTypes={teantTableTypes}
                        value={queryParams.tableType}
                        onChange={(value: any) => this.changeParams('tableType', value)}
                    />
                </FormItem>
                <FormItem>
                    <Input.Search
                        placeholder="按表名搜索"
                        style={{ width: 200 }}
                        size="default"
                        value={queryParams.tableName}
                        onChange={this.onTableNameChange}
                        onSearch={this.search}
                    />
                </FormItem>
            </Form>
        )

        const pagination: any = {
            total: Number(table.totalCount),
            defaultPageSize: 20,
            current: Number(queryParams.pageIndex)
        };
        return <div className="m-tablelist">
            <div className="box-1 m-card card-tree-select">
                <Spin tip="正在加载中..." spinning={cardLoading}>
                    <Card noHovering bordered={false} title={title} >
                        <div style={{ marginTop: '1px' }}>
                            <Table
                                rowKey="id"
                                className="dt-ant-table dt-ant-table--border rdos-ant-table-placeholder full-screen-table-90"
                                columns={this.initialColumns()}
                                dataSource={table.data}
                                pagination={pagination}
                                onChange={this.handleTableChange.bind(this)}
                            />
                        </div>
                    </Card>
                </Spin>
                <TableApplyModal
                    visible={visible}
                    table={editRecord}
                    onOk={this.apply}
                    onCancel={() => { this.setState({ visible: false }) }}
                />
            </div>
        </div>
    }
}

export default SearchTable;
