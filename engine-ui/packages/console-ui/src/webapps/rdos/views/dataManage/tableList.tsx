import * as React from 'react';
import { connect } from 'react-redux';
import {
    Input, Table, Form, message,
    Card, Select, Tabs, Spin, Checkbox
} from 'antd';

import { Link, hashHistory } from 'react-router';
import { TABLE_NAME_BY_TABLE_TYPE } from '../../comm/const';
import utils from 'utils';
import { getTenantTableTypes } from '../../store/modules/tableType';
import EngineSelect from '../../components/engineSelect';
import SlidePane from 'widgets/slidePane';
import TableLog from './tableLog';
import CatalogueTree from './catalogTree';
import ajax from '../../api/dataManage';

const FormItem = Form.Item
const Option: any = Select.Option
const TabPane = Tabs.TabPane

const ROUTER_BASE = '/data-manage/table';
const ORDER_BY: any = {
    'ascend': 1,
    'descend': 2
}
@(connect((state: any) => {
    return {
        allProjects: state.allProjects,
        allTenantsProjects: state.allTenantsProjects,
        user: state.user,
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
class TableList extends React.Component<any, any> {
    isAdminAbove: any;
    constructor (props: any) {
        super(props);
        const { listType, pId, tableName, pageIndex, catalogueId, tableType } = props.location.query;

        this.state = {
            table: [],
            editRecord: {},
            loading: false,
            dataCatalogue: [props.dataCatalogues],
            tableLog: {
                tableId: undefined,
                tableName,
                visible: false
            },
            queryParams: {
                listType: listType || '1',
                pageIndex: pageIndex || 1,
                pageSize: 20,
                catalogueId,
                tableType,
                pId,
                tableName,
                showDeleted: false
            }
        }
        this.isAdminAbove = this.props.user && this.props.user.isAdminAbove;
    }

    componentDidMount () {
        this.props.getTenantTableTypes();
        this.search();
    }

    search = () => {
        const { queryParams } = this.state;
        this.setState({ table: [], loading: true });
        const pathname = this.props.location.pathname;
        hashHistory.push({
            pathname,
            query: queryParams
        })
        ajax.newSearchTable(queryParams).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    table: res.data,
                    loading: false
                })
            } else {
                this.setState({
                    loading: false
                })
            }
        })
    }

    cancleMark = (tableId: any) => {
        const params: any = { tableId };
        ajax.cancelMark(params).then((res: any) => {
            if (res.code === 1) {
                message.success('取消成功！')
                this.search()
            }
        })
    }

    changeParams = (field: any, value: any) => {
        let queryParams = Object.assign({}, this.state.queryParams);
        if (field) {
            queryParams[field] = value;
            queryParams.pageIndex = 1;
        }

        this.setState({
            queryParams
        }, this.search)
    }
    handleTabsChange (value: any) {
        let queryParams = Object.assign({}, this.state.queryParams, {
            listType: value,
            pageIndex: 1,
            sizeOrder: undefined,
            lifeDayOrder: undefined
        });
        this.setState({
            queryParams
        }, this.search)
    }
    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        let { queryParams } = this.state;
        queryParams = Object.assign(queryParams, {
            pageIndex: pagination.current,
            sizeOrder: undefined,
            lifeDayOrder: undefined
        })
        if (sorter) {
            const { field, order } = sorter;
            if (field === 'tableSize') {
                queryParams.sizeOrder = ORDER_BY[order];
            } else if (field === 'lifeDay') {
                queryParams.lifeDayOrder = ORDER_BY[order];
            }
        }
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

    showModal = (editRecord: any) => {
        this.setState({
            visible: true,
            editRecord
        });
    }

    showTableLog (table: any) {
        const { id, tableName } = table;
        const { tableLog } = this.state;
        tableLog.tableId = id;
        tableLog.tableName = tableName;
        tableLog.visible = true;
        this.setState({
            tableLog,
            editRecord: table
        })
    }

    closeSlidePane = () => {
        const { tableLog } = this.state;
        tableLog.visible = false;
        tableLog.tableId = undefined;
        tableLog.tableName = undefined;
        this.setState({
            tableLog,
            editRecord: {}
        })
    }

    initialColumns = () => {
        const ctx = this;
        const { queryParams } = this.state
        let initialColumns: any = [
            {
                title: '表名',
                width: 250,
                fixed: 'left',
                key: 'tableName',
                dataIndex: 'tableName',
                render (text: any, record: any) {
                    const isDeleted = record.isDeleted;
                    if (isDeleted) {
                        return <span>{text}（已删除）</span>
                    }
                    return <Link to={`${ROUTER_BASE}/view/${record.id}`}>{text}</Link>
                }
            },
            {
                title: '类目',
                width: 100,
                fixed: 'left',
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
                title: '项目显示名称',
                key: 'projectAlias',
                dataIndex: 'projectAlias'
            },
            {
                title: '表类型',
                key: 'tableType',
                dataIndex: 'tableType',
                render (text: any) {
                    return TABLE_NAME_BY_TABLE_TYPE[text]
                }
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
                title: '占用存储',
                key: 'tableSize',
                width: 110,
                fixed: 'right',
                sorter: true,
                dataIndex: 'tableSize'
            },
            {
                title: '生命周期',
                key: 'lifeDay',
                width: 110,
                fixed: 'right',
                sorter: true,
                dataIndex: 'lifeDay'
            },
            {
                title: '操作',
                key: 'id',
                fixed: 'right',
                width: 120,
                render (text: any, record: any) {
                    const isDeleted = record.isDeleted;
                    switch (queryParams.listType) {
                        case '1':
                        case '2':
                        case '3':
                            return <span>
                                {!isDeleted && (
                                    <React.Fragment>
                                        <Link to={`${ROUTER_BASE}/edit/${record.id}`}>编辑</Link>
                                        <span className="ant-divider"></span>
                                    </React.Fragment>
                                )}

                                {/* <Link to={`/data-manage/log/${record.id}/${record.tableName}`}>操作记录</Link> */}
                                <a href="javascript:void(0)" onClick={ctx.showTableLog.bind(ctx, record)}>操作记录</a>
                            </span>
                        case '5':
                            return <span>
                                <a onClick={() => ctx.cancleMark(record.id)}>取消收藏</a>
                            </span>
                        default:
                            return '--';
                    }
                }
            }
        ];
        if (queryParams.listType == '4') {
            initialColumns.pop(1);
            let addInitalColumns: any = [{
                title: '有效期',
                key: 'timeTeft',
                fixed: 'right',
                width: 90,
                dataIndex: 'timeTeft',
                render (text: any, record: any) {
                    return text ? `${text}天` : ' '
                }
            }, {
                title: '通过时间',
                key: 'passTime',
                fixed: 'right',
                width: 90,
                dataIndex: 'passTime',
                render (text: any, record: any) {
                    return text ? utils.formatDateTime(text) : ' '
                }
            }]
            initialColumns = [...initialColumns, ...addInitalColumns]
        }
        return initialColumns;
    }

    renderPane = () => {
        const { table, queryParams, dataCatalogue, loading } = this.state;
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
                <FormItem>
                    <Checkbox
                        checked={queryParams.showDeleted}
                        onChange={(e: any) => this.changeParams('showDeleted', e.target.checked)}
                    >
                        显示已删除的表
                    </Checkbox>
                </FormItem>
            </Form>
        )
        const pagination: any = {
            total: Number(table.totalCount),
            defaultPageSize: 20,
            current: Number(queryParams.pageIndex)
        };

        return <div className="m-tablelist">
            <div className="m-card card-tree-select">
                <Spin spinning={loading} tip="正在加载中...">
                    <Card noHovering bordered={false} title={title}>
                        <div style={{ marginTop: '1px' }}>
                            <Table
                                rowKey="id"
                                className="dt-ant-table dt-ant-table--border rdos-ant-table-placeholder"
                                rowClassName={
                                    (record: any, index: any) => {
                                        if (this.state.tableLog && this.state.tableLog.tableId == record.id) {
                                            return 'row-select'
                                        } else {
                                            return '';
                                        }
                                    }
                                }
                                columns={this.initialColumns()}
                                dataSource={table.data}
                                pagination={pagination}
                                onChange={this.handleTableChange}
                                scroll={{ x: '1100px' }}
                            />
                        </div>
                    </Card>
                </Spin>
            </div>
        </div>
    }

    render () {
        const { tableLog, queryParams, editRecord } = this.state;
        const projectUsers: any = [];
        return (
            <div className="box-1 m-tabs">
                <Tabs
                    activeKey={queryParams.listType}
                    animated={false}
                    style={{ overflow: 'visible', height: 'calc(100% - 40px)' }}
                    onChange={this.handleTabsChange.bind(this)}
                >
                    <TabPane tab="我近期操作的表" key="1">
                        {this.renderPane()}
                    </TabPane>
                    <TabPane tab="个人账号的表" key="2">
                        {this.renderPane()}
                    </TabPane>
                    {
                        this.isAdminAbove == 1 ? <TabPane tab="我管理的表" key="3">{this.renderPane()}</TabPane> : ''
                    }
                    <TabPane tab="被授权的表" key="4">
                        {this.renderPane()}
                    </TabPane>
                    <TabPane tab="我收藏的表" key="5">
                        {this.renderPane()}
                    </TabPane>
                </Tabs>
                <SlidePane
                    onClose={this.closeSlidePane}
                    visible={tableLog.visible}
                    style={{ right: '-20px', width: '80%', height: '100%', minHeight: '600px' }}
                >
                    {tableLog.visible ? <div className="m-loglist">
                        <TableLog key={tableLog.tableId} {...tableLog} projectUsers={projectUsers} editRecord={editRecord} />
                    </div> : ''}
                </SlidePane>
            </div>
        )
    }
}

export default TableList;
