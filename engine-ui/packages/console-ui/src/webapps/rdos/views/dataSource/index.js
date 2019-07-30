import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { cloneDeep } from 'lodash';
import {
    Input, Button, Popconfirm,
    Table, message, Card, Icon, Tooltip, Menu,
    Dropdown
} from 'antd';

import { Circle } from 'widgets/circle';

import Api from '../../api';
import { PROJECT_TYPE } from '../../comm/const';
import { isRDB } from '../../comm';
import { getSourceTypes } from '../../store/modules/dataSource/sourceTypes';

import DataSourceTaskListModal from './dataSourceTaskListModal';
import DataSourceForm from './form';
import DbSyncModal from './syncModal';
import LinkModal from './linkModal';
import { ExtTableCell } from './extDataSourceMsg'

const Search = Input.Search
const MenuItem = Menu.Item;

class DataSourceMana extends Component {
    state = {
        dataSource: {
            data: []
        },
        visible: false,
        syncModalVisible: false,
        loading: false,
        title: '新增数据源',
        status: 'add',
        source: {},
        params: {
            pageSize: 10,
            currentPage: 1,
            name: '',
            type: undefined
        } // 请求参数
    }

    componentDidMount () {
        this.loadDataSources()
        this.props.getSourceTypes();
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj.id !== 0 && project && oldProj.id !== project.id) {
            this.loadDataSources()
        }
    }

    loadDataSources = () => {
        const ctx = this
        const { params } = this.state;
        this.setState({ loading: true })
        Api.queryDataSource(params).then((res) => {
            this.setState({
                loading: false
            })
            if (res.code === 1) {
                const data = res.data;
                // 当前页数据删完，currentpage - 1
                const isReduceCurrPage = (data.totalCount != 0 && data.totalCount % 10 == 0) && (data.data && data.data.length == 0)
                ctx.setState({
                    dataSource: data || [],
                    params: Object.assign(this.state.params, {
                        currentPage: isReduceCurrPage ? this.state.params.currentPage - 1 : this.state.params.currentPage
                    })
                }, () => {
                    isReduceCurrPage && ctx.loadDataSources()
                })
            }
        })
    }

    searchDataSources = (query) => {
        const { params } = this.state;
        params.currentPage = 1; // 此处需要重置currentPage 为1
        this.setState({
            params: {
                ...params,
                name: query.trim()
            }
        }, () => {
            this.loadDataSources()
        })
    }

    addOrUpdateDataSource = (sourceFormData, formObj, callBack) => {
        const ctx = this
        const { title, status, source } = this.state
        let reqSource = sourceFormData
        if (status === 'edit') { // 编辑数据
            reqSource = Object.assign(source, sourceFormData)
        }
        Api.addOrUpdateSource(reqSource).then((res) => {
            if (res.code === 1) {
                formObj.resetFields()
                message.success(`${title}成功！`)
                ctx.setState({
                    visible: false
                })
                ctx.loadDataSources()
                callBack();
            }
        })
    }
    // 点击新增数据源
    openAddDatasource = () => {
        Api.checkIsPermission().then(res => {
            if (res.code === 1) {
                this.setState({
                    visible: true,
                    source: {},
                    status: 'add',
                    title: '添加数据源'
                })
            }
        })
    }
    remove = (source) => {
        const ctx = this
        if (source.active === 1) {
            message.info('此数据源已在任务中被引用，无法删除!')
            return;
        }
        Api.deleteDataSource({ sourceId: source.id }).then((res) => {
            if (res.code === 1) {
                message.success('移除数据源成功！')
                ctx.loadDataSources()
            }
        })
    }

    testConnection = (source) => { // 测试数据源连通性
        Api.testDSConnection(source).then((res) => {
            if (res.code === 1 && res.data) {
                message.success('数据源连接正常！')
            } else if (res.code === 1 && !res.data) {
                message.error('数据源连接异常')
            }
        })
    }

    handleTableChange = (pagination, filters) => {
        const params = {}
        if (filters.type) {
            params.type = filters.type[0]
        }
        params.currentPage = pagination.current
        this.setState({
            current: pagination.current,
            params: Object.assign(this.state.params, params)
        }, () => {
            this.loadDataSources()
        })
    }

    initEdit = (source) => {
        Api.checkIsPermission().then(res => {
            if (res.code === 1) {
                this.setState({
                    visible: true,
                    title: '编辑数据源',
                    status: 'edit',
                    source: cloneDeep(source)
                })
            }
        })
    }
    getTypeName (type) {
        const { sourceTypes = [] } = this.props;
        const source = sourceTypes.find((source) => {
            return source.value == type
        })
        return source ? source.name : ''
    }
    initColumns = () => {
        const text = '系统每隔10分钟会尝试连接一次数据源，如果无法连通，则会显示连接失败的状态。数据源连接失败会导致同步任务执行失败。';
        const { sourceTypes } = this.props;
        return [{
            title: '数据源名称',
            dataIndex: 'dataName',
            key: 'dataName',
            width: '180px'
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            width: '100px',
            render: (text, record) => {
                return this.getTypeName(text);
            },
            filters: sourceTypes.map((source) => {
                source.text = source.name;
                return source;
            }),
            filterMultiple: false
        },
        {
            title: '描述',
            dataIndex: 'dataDesc',
            key: 'dataDesc',
            width: '300px'
        },
        {
            title: '连接信息',
            dataIndex: 'ext',
            key: 'ext',
            render: (empty, record) => {
                return <ExtTableCell key={record.id} sourceData={record} />
            }
        },
        {
            title: '应用状态',
            dataIndex: 'active',
            key: 'active',
            width: '70px',
            render: (active, record) => {
                return active === 1 ? <DataSourceTaskListModal type="offline" dataSource={record}>使用中</DataSourceTaskListModal> : '未使用'
            }
        },
        {
            title: <Tooltip placement="top" title={text} arrowPointAtCenter>
                <span>连接状态 &nbsp;
                    <Icon type="question-circle-o" />
                </span>
            </Tooltip>,
            dataIndex: 'linkState',
            key: 'linkState',
            width: '100px',
            render: (linkState) => {
                return linkState === 1
                    ? <span><Circle style={{ background: '#00A755' }} /> 正常</span>
                    : <span><Circle style={{ background: '#EF5350' }} /> 连接失败</span>
            }
        },
        {
            title: '映射状态',
            dataIndex: 'linkSourceName',
            width: '80px',
            key: 'linkSourceName',
            render: (linkSourceName, record) => {
                return linkSourceName ? '已配置' : '未配置';
            }
        },
        {
            title: '操作',
            width: '130px',
            key: 'operation',
            render: (text, record) => {
                // active  '0：未启用，1：使用中'。  只有为0时，可以修改
                const { project } = this.props;
                /**
                 * 是否为普通项目（非测试也非生产）
                 */
                const isCommon = project.projectType == PROJECT_TYPE.COMMON;
                const isRDBType = isRDB(record.type);

                const isActive = record.active === 1;
                let menuItem = [];
                let extAction = null;
                const splitView = (<span className="ant-divider" />);
                const deleteView = isActive ? (
                    <span style={{ color: '#ccc' }}>删除</span>
                ) : (<Popconfirm
                    title="确定删除此数据源？"
                    okText="确定" cancelText="取消"
                    onConfirm={() => { this.remove(record) }}
                >
                    <a>删除</a>
                </Popconfirm>);
                const editView = (<a onClick={() => { this.initEdit(record) }}>编辑</a>);
                const linkView = (<a onClick={() => { this.setState({ linkModalVisible: true, source: record }) }} >映射配置</a>);

                /**
                 * 假如是mysql，那么整库同步放在最外层
                 */
                if (isRDBType) {
                    extAction = (
                        <span>
                            <Link to={`database/offLineData/db-sync/${record.id}/${record.dataName}`}>
                                整库同步
                            </Link>
                            {splitView}
                        </span>
                    )
                    if (!isCommon) {
                        menuItem.push(
                            <MenuItem key="linkView">
                                {linkView}
                            </MenuItem>
                        )
                    }
                    menuItem.push(
                        <MenuItem key="syncHistory">
                            <a onClick={this.openSyncModal.bind(this, record)}>
                                同步历史
                            </a>
                        </MenuItem>

                    )
                    menuItem.push(
                        <MenuItem key="editView">
                            {editView}
                        </MenuItem>
                    )
                    menuItem.push(
                        <MenuItem key="delView">
                            {deleteView}
                        </MenuItem>
                    )
                } else if (isCommon) {
                    extAction = (
                        <span>
                            {editView}
                            {splitView}
                            {deleteView}
                        </span>
                    )
                } else {
                    extAction = (
                        <span>
                            {linkView}
                            {splitView}
                        </span>
                    )
                    menuItem.push(
                        <MenuItem key="editView">
                            {editView}
                        </MenuItem>
                    )
                    menuItem.push(
                        <MenuItem key="delView">
                            {deleteView}
                        </MenuItem>
                    )
                }
                return (
                    <span>
                        {extAction}
                        {menuItem.length ? (
                            <Dropdown overlay={(
                                <Menu>
                                    {menuItem}
                                </Menu>
                            )} trigger={['click']}>
                                <a>操作<Icon type="down" /></a>
                            </Dropdown>
                        ) : null}
                    </span>

                )
            }
        }]
    }

    openSyncModal = (record) => {
        Api.checkSyncPermission().then(res => {
            if (res.code === 1) {
                this.setState({
                    syncModalVisible: true,
                    source: record
                });
            }
        })
    }

    closeSyncModal = () => {
        this.setState({
            syncModalVisible: false,
            source: {}
        });
    }

    render () {
        const { visible, title, status, source, syncModalVisible, dataSource, linkModalVisible } = this.state
        const { sourceTypes, project } = this.props;
        const pagination = {
            total: dataSource.totalCount,
            defaultPageSize: 10
        };
        const isPro = project && project.projectType == PROJECT_TYPE.PRO;
        const isTest = project && project.projectType == PROJECT_TYPE.TEST;
        const titles = (
            <div>
                <Search
                    placeholder="数据源名称/描述"
                    style={{ width: 200 }}
                    onSearch={this.searchDataSources}
                />&nbsp;&nbsp;
            </div>
        )
        const extra = (
            <Button
                type="primary"
                style={{ marginTop: 10 }}
                className="right"
                onClick={this.openAddDatasource}
            >新增数据源</Button>
        )

        return (
            <div>
                <div className="shadow rdos-data-source">
                    <Card
                        title={titles}
                        extra={extra}
                        noHovering
                        bordered={false}
                    >
                        <Table
                            className="dt-ant-table dt-ant-table--border full-screen-table-47"
                            rowKey="id"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={this.state.loading}
                            columns={this.initColumns()}
                            dataSource={dataSource.data}
                        />
                    </Card>
                </div>

                <DataSourceForm
                    isPro={isPro}
                    isTest={isTest}
                    showSync={true}
                    title={title}
                    visible={visible}
                    status={status}
                    handOk={this.addOrUpdateDataSource}
                    testConnection={this.testConnection}
                    project={project}
                    sourceData={source}
                    sourceTypes={sourceTypes}
                    handCancel={() => { this.setState({ visible: false }) }}
                />

                <DbSyncModal
                    visible={syncModalVisible}
                    source={source}
                    cancel={this.closeSyncModal}
                />
                <LinkModal
                    sourceData={source}
                    visible={linkModalVisible}
                    type="offline"
                    onCancel={() => { this.setState({ linkModalVisible: false }) }}
                    onOk={() => { this.loadDataSources(); this.setState({ linkModalVisible: false }) }}
                />
            </div>
        )
    }
}
class WrapDataSourceMana extends React.Component {
    render () {
        const { project } = this.props;
        return (
            <DataSourceMana key={project.id} {...this.props} />
        )
    }
}
export default connect((state) => {
    console.log('connect', state);

    return {
        project: state.project,
        sourceTypes: state.dataSource.sourceTypes
    }
}, dispatch => {
    return {
        getSourceTypes: function () {
            dispatch(getSourceTypes())
        }
    }
})(WrapDataSourceMana)
