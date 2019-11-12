import * as React from 'react';
import { connect } from 'react-redux';
import {
    Input, Button, Popconfirm,
    Table, message, Card, Icon, Tooltip
} from 'antd';

import { Circle } from 'widgets/circle';
import { ExtTableCell } from '../../components/extDataSourceMsg';
import DataSourceForm from './editModal';
import { dataSourceActions } from '../../actions/dataSource';
import Api from '../../api/dataSource';
import '../../styles/views/dataSource.scss';

const Search = Input.Search

const mapStateToProps = (state: any) => {
    const { dataSource, project } = state;
    return { dataSource, project }
}

const mapDispatchToProps = (dispatch: any) => ({
    getDataSources (params: any) {
        dispatch(dataSourceActions.getDataSources(params));
    }
})

@(connect(mapStateToProps, mapDispatchToProps) as any)
class DataSource extends React.Component<any, any> {
    state: any = {
        visible: false,
        title: '新增数据源',
        status: 'add',
        source: {},
        params: {
            currentPage: 1,
            pageSize: 20,
            name: undefined,
            type: undefined,
            active: undefined
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.props.getDataSources(this.state.params);
        }
    }

    componentDidMount () {
        this.props.getDataSources(this.state.params);
    }

    searchDataSources = (name: any) => {
        let params: any = { ...this.state.params, name };
        params.currentPage = 1;
        this.setState({ params });
        this.props.getDataSources(params);
    }

    editDataSource = (sourceFormData: any, formObj: any) => {
        const { title, status, source, params } = this.state;

        if (status === 'edit') {
            Api.updateDataSource({
                ...source, ...sourceFormData
            }).then((res: any) => {
                if (res.code === 1) {
                    formObj.resetFields();
                    this.setState({ visible: false });
                    message.success(`${title}成功！`);
                    this.props.getDataSources(params);
                }
            });
        } else {
            Api.addDataSource(sourceFormData).then((res: any) => {
                if (res.code === 1) {
                    formObj.resetFields();
                    this.setState({ visible: false });
                    message.success(`${title}成功！`);
                    this.props.getDataSources(params);
                }
            });
        }
    }
    // 点击新增数据源
    openAddDatasource = () => {
        Api.checkDataSourcePermission().then((res: any) => {
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
    remove = (record: any) => {
        if (record.active === 1) {
            message.info('此数据源已在任务中被引用，无法删除!')
            return;
        }

        Api.deleteDataSource({ sourceId: record.id }).then((res: any) => {
            if (res.code === 1) {
                message.success('移除数据源成功！');
                this.props.getDataSources(this.state.params);
            }
        });
    }

    testConnection = (params: any) => { // 测试数据源连通性
        Api.testDSConnection(params).then((res: any) => {
            if (res.code === 1) {
                message.success('数据源连接正常！')
            }
        })
    }

    handleTableChange = (page: any, filters: any) => {
        let active = filters.active;
        let type = filters.type;

        console.log(filters)

        let params: any = {
            ...this.state.params,
            currentPage: page.current,
            active: active ? active[0] : undefined,
            type: type ? type[0] : undefined
        };

        this.setState({ params });
        this.props.getDataSources(params);
    }

    initEdit = (source: any) => {
        Api.checkDataSourcePermission().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    visible: true,
                    title: '编辑数据源',
                    status: 'edit',
                    source
                })
            }
        })
    }
    exchangeSourceType () {
        let arr: any = [];
        let dic: any = {};

        const items = this.props.dataSource.sourceType;
        for (let i in items) {
            let item = items[i];
            dic[item.value] = item.name;
            arr.push({
                text: item.name,
                value: item.value
            })
        }

        return { typeList: arr, typeDic: dic };
    }

    initColumns = () => {
        const { typeList, typeDic } = this.exchangeSourceType();
        const text = '系统每隔10分钟会尝试连接一次数据源，如果无法连通，则会显示连接失败的状态。数据源连接失败会导致同步任务执行失败。';
        return [{
            title: '数据源名称',
            dataIndex: 'dataName',
            key: 'dataName',
            width: '180px'
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            filters: typeList,
            width: '100px',
            filterMultiple: false,
            render: (text: any) => typeDic[text]
        },
        {
            title: '描述信息',
            dataIndex: 'dataDesc',
            key: 'dataDesc',
            width: 250
        }, {
            title: '连接信息',
            dataIndex: 'ext',
            key: 'ext',
            render: (empty: any, record: any) => {
                return <ExtTableCell key={record.id} sourceData={record} />
            }
        }, {
            title: '应用状态',
            dataIndex: 'active',
            key: 'active',
            filters: [{
                text: '未启用',
                value: 0
            }, {
                text: '使用中',
                value: 1
            }],
            filterMultiple: false,
            width: 90,
            render: (active: any) => {
                return active === 1 ? '使用中' : '未启用'
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
            render: (linkState: any) => {
                return linkState === 1 ? <span><Circle style={{ background: '#00A755' }} /> 正常</span>
                    : <span><Circle style={{ background: '#EF5350' }} /> 连接失败</span>
            }
        },
        {
            title: '操作',
            width: '120px',
            key: 'operation',
            render: (text: any, record: any) => {
                // active  '0：未启用，1：使用中'。  只有为0时，可以修改
                return (
                    <span key={record.id}>
                        <a onClick={() => { this.initEdit(record) }}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        <Popconfirm
                            title="确定删除此数据源？"
                            okText="确定" cancelText="取消"
                            onConfirm={() => { this.remove(record) }}
                        >
                            <a>删除</a>
                        </Popconfirm>
                    </span>
                )
            }
        }]
    }

    render () {
        const { visible, title, status, source, params } = this.state;
        const { sourceQuery, loading } = this.props.dataSource;

        const pagination: any = {
            current: params.currentPage,
            pageSize: params.pageSize,
            total: sourceQuery.totalCount
        };
        const cardTitle = (
            <Search
                placeholder="数据源名称"
                style={{ width: 200 }}
                onSearch={this.searchDataSources}
            />
        )
        const cardExtra = (
            <Button
                type="primary"
                style={{ margin: '10px 0' }}
                className="right"
                onClick={this.openAddDatasource}
            >
                新增数据源
            </Button>
        )

        return (
            <div className="source-page">
                <h1 className="box-title">
                    数据源管理
                </h1>

                <div className="box-2 m-card shadow">
                    <Card
                        title={cardTitle}
                        extra={cardExtra}
                        noHovering
                        bordered={false}
                    >
                        <Table
                            className="m-table"
                            rowKey="id"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={loading}
                            columns={this.initColumns() as any}
                            dataSource={sourceQuery.data}
                        />
                    </Card>
                </div>

                <DataSourceForm
                    title={title}
                    visible={visible}
                    status={status}
                    editDataSource={this.editDataSource}
                    testConnection={this.testConnection}
                    sourceData={source}
                    handCancel={() => { this.setState({ visible: false }) }}
                />
            </div>
        )
    }
}
export default DataSource;
