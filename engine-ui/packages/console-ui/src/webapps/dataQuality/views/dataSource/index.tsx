import * as React from 'react';
import { connect } from 'react-redux';
import {
    Input,
    Button,
    Popconfirm,
    Table,
    message,
    Card,
    Icon,
    Tooltip
} from 'antd';

import { ExtTableCell } from '../../components/extDataSourceMsg';
import { Circle } from 'widgets/circle';
import DataSourceForm from './editModal';
import { dataSourceFilter } from '../../consts';
import { dataSourceActions } from '../../actions/dataSource';
import DSApi from '../../api/dataSource';
import { pickBy } from 'lodash';

const Search = Input.Search;

const mapStateToProps = (state: any) => {
    const { dataSource } = state;
    return { dataSource };
};

const mapDispatchToProps = (dispatch: any) => ({
    getDataSources (params: any) {
        dispatch(dataSourceActions.getDataSources(params));
    }
});

@(connect(
    mapStateToProps,
    mapDispatchToProps
) as any)
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
    };

    componentDidMount () {
        this.props.getDataSources(this.state.params);
    }

    searchDataSources = (name: any) => {
        let params: any = {
            ...this.state.params,
            name: name || undefined,
            currentPage: 1
        };

        this.setState({ params });
        this.props.getDataSources(params);
    };

    editDataSource = (sourceFormData: any, formObj: any) => {
        const { title, status, source, params } = this.state;
        if (status === 'edit') {
            let reqSource = {
                ...source,
                ...sourceFormData
            }
            if (reqSource.dataJson.openKerberos) {
                reqSource.dataJsonString = JSON.stringify(reqSource.dataJson)
                console.log(reqSource)
                delete reqSource.modifyUser;
                delete reqSource.dataJson;
                reqSource = pickBy(reqSource, (item, key) => { // 过滤掉空字符串和值为null的属性，并且过滤掉编辑时的kerberos字段
                    if (key === 'kerberosFile' && (!item.type)) {
                        return false
                    }
                    return item != null
                })
                DSApi.updateDataSourceKerberos(reqSource).then((res: any) => {
                    if (res.code === 1) {
                        this.setState({ visible: false });
                        formObj.resetFields();
                        message.success(`${title}成功！`);
                        this.props.getDataSources(params);
                    }
                });
            } else {
                DSApi.updateDataSource(reqSource).then((res: any) => {
                    if (res.code === 1) {
                        this.setState({ visible: false });
                        formObj.resetFields();
                        message.success(`${title}成功！`);
                        this.props.getDataSources(params);
                    }
                });
            }
        } else {
            let reqSource = sourceFormData;
            if (reqSource.dataJson.openKerberos) {
                reqSource.dataJsonString = JSON.stringify(reqSource.dataJson)
                console.log(reqSource)
                delete reqSource.modifyUser;
                delete reqSource.dataJson;
                reqSource = pickBy(reqSource, (item, key) => { // 过滤掉空字符串和值为null的属性，并且过滤掉编辑时的kerberos字段
                    if (key === 'kerberosFile' && (!item.type)) {
                        return false
                    }
                    return item != null
                });
                DSApi.addDataSourceKerberos(sourceFormData).then((res: any) => {
                    if (res.code === 1) {
                        this.setState({ visible: false });
                        formObj.resetFields();
                        message.success(`${title}成功！`);
                        this.props.getDataSources(params);
                    }
                });
            } else {
                DSApi.addDataSource(sourceFormData).then((res: any) => {
                    if (res.code === 1) {
                        this.setState({ visible: false });
                        formObj.resetFields();
                        message.success(`${title}成功！`);
                        this.props.getDataSources(params);
                    }
                });
            }
        }
    };

    // 点击新增数据源
    openAddDatasource = () => {
        DSApi.checkDataSourcePermission().then((res: any) => {
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
            message.info('此数据源已在任务中被引用，无法删除!');
            return;
        }

        DSApi.deleteDataSource({ sourceId: record.id }).then((res: any) => {
            if (res.code === 1) {
                message.success('移除数据源成功！');
                this.props.getDataSources(this.state.params);
            }
        });
    };

    handleTableChange = (page: any, filters: any) => {
        let active = filters.active;

        let type = filters.type;

        let params: any = {
            ...this.state.params,
            currentPage: page.current,
            active: active ? active[0] : undefined,
            type: type ? type[0] : undefined
        };

        this.setState({ params });
        this.props.getDataSources(params);
    };

    initEdit = (source: any) => {
        DSApi.checkDataSourcePermission().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    visible: true,
                    title: '编辑数据源',
                    status: 'edit',
                    source
                })
            }
        })
    };

    initColumns = () => {
        const text =
            '系统每隔10分钟会尝试连接一次数据源，如果无法连通，则会显示连接失败的状态。数据源连接失败会导致同步任务执行失败。';
        return [
            {
                title: '数据源名称',
                dataIndex: 'dataName',
                key: 'dataName',
                width: '200px',
                render: (text: any) => (
                    <div className="ellipsis-td" title={text}>
                        {text}
                    </div>
                )
            },
            {
                title: '类型',
                dataIndex: 'type',
                key: 'type',
                filters: dataSourceFilter,
                filterMultiple: false,
                width: '100px',
                render (text: any, record: any) {
                    return record.sourceTypeValue;
                }
            },
            {
                title: '描述信息',
                dataIndex: 'dataDesc',
                key: 'dataDesc',
                width: '250px'
            },
            {
                title: '连接信息',
                dataIndex: 'ext',
                key: 'ext',
                render: (empty: any, record: any) => {
                    return <ExtTableCell key={record.id} sourceData={record} />
                }
            },
            {
                title: '应用状态',
                dataIndex: 'active',
                key: 'active',
                filters: [
                    {
                        text: '未使用',
                        value: 0
                    },
                    {
                        text: '使用中',
                        value: 1
                    }
                ],
                filterMultiple: false,
                render: (active: any) => {
                    return active === 1 ? '使用中' : '未使用';
                },
                width: '100px'
            },
            {
                title: (
                    <Tooltip placement="top" title={text} arrowPointAtCenter>
                        <span>
                            连接状态 &nbsp;
                            <Icon type="question-circle-o" />
                        </span>
                    </Tooltip>
                ),
                dataIndex: 'linkState',
                key: 'linkState',
                width: '90px',
                render: (linkState: any) => {
                    return linkState === 1 ? (
                        <span>
                            <Circle style={{ background: '#00A755' }} /> 正常
                        </span>
                    ) : (
                        <span>
                            <Circle style={{ background: '#EF5350' }} />{' '}
                            连接失败
                        </span>
                    );
                }
            },
            {
                title: '操作',
                width: '120px',
                render: (text: any, record: any) => {
                    return (
                        <span>
                            <a
                                onClick={() => {
                                    this.initEdit(record);
                                }}
                            >
                                编辑
                            </a>
                            <span className="ant-divider" />
                            <Popconfirm
                                title="确定删除此数据源？"
                                okText="确定"
                                cancelText="取消"
                                onConfirm={() => {
                                    this.remove(record);
                                }}
                            >
                                <a>删除</a>
                            </Popconfirm>
                        </span>
                    );
                }
            }
        ];
    };

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
        );

        const cardExtra = (
            <Button
                type="primary"
                style={{ margin: '10px 0' }}
                className="right"
                onClick={this.openAddDatasource}
            >
                新增数据源
            </Button>
        );

        return (
            <div className="source-page">
                <h1 className="box-title">数据源管理</h1>

                <div className="box-2 m-card shadow">
                    <Card
                        noHovering
                        bordered={false}
                        title={cardTitle}
                        extra={cardExtra}
                    >
                        <Table
                            rowKey="id"
                            loading={loading}
                            className="m-table fixed-table"
                            pagination={pagination}
                            columns={this.initColumns()}
                            dataSource={sourceQuery.data}
                            onChange={this.handleTableChange}
                        />
                    </Card>
                </div>

                <DataSourceForm
                    title={title}
                    status={status}
                    visible={visible}
                    sourceData={source}
                    editDataSource={this.editDataSource}
                    handCancel={() => {
                        this.setState({ visible: false, source: {} });
                    }}
                />
            </div>
        );
    }
}
export default DataSource;
