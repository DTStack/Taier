import * as React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import {
    Table,
    Button,
    Icon,
    Input,
    DatePicker,
    Menu,
    Dropdown,
    Select,
    message,
    Card,
    Tooltip
} from 'antd';

import { dataCheckActions } from '../../../actions/dataCheck';
import { dataSourceActions } from '../../../actions/dataSource';
import { DataCheckStatus } from '../../../components/display';
import { CHECK_STATUS, CHECK_STATUS_CN, DATA_SOURCE } from '../../../consts';
import DCApi from '../../../api/dataCheck';
import EnvModal from '../../envModal';

import '../../../styles/views/dataCheck.scss';

const Search = Input.Search;
const Option = Select.Option;

const mapStateToProps = (state: any) => {
    const { dataCheck, dataSource, common, project } = state;
    return { dataCheck, dataSource, common, project };
};

const mapDispatchToProps = (dispatch: any) => ({
    getLists (params: any) {
        dispatch(dataCheckActions.getLists(params));
    },
    getDataSourcesList (params: any) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    }
});

@(connect(
    mapStateToProps,
    mapDispatchToProps
) as any)
class DataCheck extends React.Component<any, any> {
    state: any = {
        params: {
            currentPage: 1,
            pageSize: 20,
            tableName: undefined,
            lastModifyUserId: undefined,
            executeTime: undefined,

            visibleEnvModal: false,
            selectedRecord: ''
        }
    };

    componentDidMount () {
        this.props.getDataSourcesList();
        this.props.getLists(this.state.params);
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.props.getDataSourcesList();
            this.props.getLists(this.state.params);
        }
    }

    // table设置
    initColumns = () => {
        return [
            {
                title: '左侧表',
                dataIndex: 'originTableName',
                key: 'originTableName',
                width: '10%'
            },
            {
                title: '分区',
                dataIndex: 'originPartition',
                key: 'originPartition',
                width: '10%',
                render: (text: any, record: any) => {
                    return text || '--';
                }
            },
            {
                title: '右侧表',
                dataIndex: 'targetTableName',
                key: 'targetTableName',
                width: '10%'
            },
            {
                title: '分区',
                dataIndex: 'targetPartition',
                key: 'targetPartition',
                width: '10%',
                render: (text: any, record: any) => {
                    return text || '--';
                }
            },
            {
                title: '类型',
                dataIndex: 'sourceTypeName',
                key: 'sourceTypeName',
                width: '90px',
                render: (text: any, record: any) => {
                    return text || '--';
                }
            },
            {
                title: (
                    <span>
                        校验结果
                        <Tooltip
                            placement="bottom"
                            overlayClassName="m-tooltip"
                            title={
                                <div>
                                    <p>以下条件同时满足计为校验通过：</p>
                                    <p>1、逻辑主键匹配，但数据不匹配=0</p>
                                    <p>{`2、max（左表数据在右表未找到，右表数据在左表未找到）<（右表记录数*记录数差异比例配置）`}</p>
                                </div>
                            }
                        >
                            <Icon
                                className="font-12 m-l-8"
                                type="question-circle-o"
                            />
                        </Tooltip>
                    </span>
                ),
                dataIndex: 'status',
                key: 'status',
                width: '125px',
                render: (text: any, record: any) => {
                    return text == 3 ? (
                        <span>
                            <DataCheckStatus
                                style={{ flexBasis: '60%' }}
                                value={text}
                            />
                            &nbsp;
                            <Tooltip
                                placement="right"
                                title={record.report}
                                overlayClassName="m-tooltip"
                            >
                                <Icon
                                    className="font-14"
                                    type="info-circle-o"
                                />
                            </Tooltip>
                        </span>
                    ) : (
                        <DataCheckStatus value={text} />
                    );
                }
            },
            {
                title: (
                    <div>
                        差异总数
                        <Tooltip
                            placement="bottom"
                            overlayClassName="m-tooltip"
                            title={
                                '差异总数 = 逻辑主键匹配但数据不匹配 + 左表数据在右表未找到 + 右表数据在左表未找到'
                            }
                        >
                            <Icon
                                className="font-12 m-l-8"
                                type="question-circle-o"
                            />
                        </Tooltip>
                    </div>
                ),
                dataIndex: 'diverseNum',
                key: 'diverseNum',
                width: '100px'
                // sorter: true
            },
            {
                title: (
                    <div>
                        差异比例
                        <Tooltip
                            placement="bottom"
                            overlayClassName="m-tooltip"
                            title={
                                '统计左右2表的记录数最大值，统计整体匹配条数，整体匹配条数/记录数最大值为匹配率，差异比例=1-匹配率'
                            }
                        >
                            <Icon
                                className="font-12 m-l-8"
                                type="question-circle-o"
                            />
                        </Tooltip>
                    </div>
                ),
                dataIndex: 'diverseRatio',
                key: 'diverseRatio',
                width: '100px',
                render: (text: any) => (text ? `${text} %` : text)
                // sorter: true
            },
            {
                title: '最近修改人',
                dataIndex: 'modifyUserName',
                key: 'modifyUserName'
            },
            {
                title: '执行时间',
                dataIndex: 'executeTimeFormat',
                key: 'executeTimeFormat',
                render: (text: any, record: any) => {
                    return text || '--';
                }
            },
            {
                title: '操作',
                width: '100px',
                render: (text: any, record: any) => {
                    let menu = (
                        <Menu>
                            {this.enableCheckReport(record.status) && (
                                <Menu.Item>
                                    <Link
                                        to={`dq/dataCheck/report/${record.id}`}
                                    >
                                        查看报告
                                    </Link>
                                </Menu.Item>
                            )}
                            <Menu.Item>
                                <Link
                                    to={`dq/dataCheck/edit/${record.verifyId}`}
                                >
                                    再次运行
                                </Link>
                            </Menu.Item>
                            {record.dataSourceType === DATA_SOURCE.HIVE && (
                                <Menu.Item>
                                    <a
                                        onClick={() => {
                                            this.setState({
                                                selectedRecord: record,
                                                visibleEnvModal: true
                                            });
                                        }}
                                    >
                                        环境参数
                                    </a>
                                </Menu.Item>
                            )}
                            {/* <Menu.Item>
                            <Popconfirm
                                title="确定删除此校验？"
                                okText="确定" cancelText="取消"
                                onConfirm={() => {this.deleteDataCheck(record.id)}}
                            >
                                <a type="danger">删除</a>
                            </Popconfirm>
                        </Menu.Item> */}
                        </Menu>
                    );
                    return (
                        <Dropdown overlay={menu} trigger={['click']}>
                            <Button>
                                操作
                                <Icon type="down" />
                            </Button>
                        </Dropdown>
                    );
                }
            }
        ];
    };

    // 是否能查看报告
    enableCheckReport = (status: any) => {
        return (
            status === CHECK_STATUS.SUCCESS ||
            status === CHECK_STATUS.PASS ||
            status === CHECK_STATUS.UNPASS ||
            status === CHECK_STATUS.EXPIRED
        );
    };

    // 删除逐行校验
    deleteDataCheck = (id: any) => {
        DCApi.deleteCheck({ verifyRecordId: id }).then((res: any) => {
            if (res.code === 1) {
                message.success('删除成功！');
                this.props.getLists(this.state.params);
            }
        });
    };

    // 修改任务参数
    updateEnvParams = (value: any) => {
        if (!value) {
            message.error('环境参数为空！');
            return;
        }
        const { selectedRecord } = this.state;

        DCApi.updateTaskParams({
            verifyId: selectedRecord.verifyId,
            taskParams: value
        }).then((res: any) => {
            if (res.code === 1) {
                message.success('修改环境参数成功');
                this.setState({
                    visibleEnvModal: false,
                    selectedRecord: ''
                });
                this.props.getLists(this.state.params);
            }
        });
    };

    // 表格回调
    onTableChange = (page: any, filter: any, sorter: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: page.current
            // sortBy: sorter.columnKey ? sorter.columnKey : '',
            // orderBy: sorter.columnKey ? (sorter.order == 'ascend' ? '01' : '02') : ''
        };
        this.props.getLists(params);
        this.setState({ params });
    };

    // 数据源下拉框
    renderUserSource = (data: any) => {
        return data.map((source: any) => {
            let title = `${source.dataName}（${source.sourceTypeValue}）`;

            return (
                <Option
                    key={source.id}
                    value={source.id.toString()}
                    title={title}
                >
                    {title}
                </Option>
            );
        });
    };

    // 数据源筛选
    onUserSourceChange = (id: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: 1,
            dataSourceId: id || undefined
        };

        this.props.getLists(params);
        this.setState({ params });
    };

    // 校验状态下拉框
    renderCheckStatus = (data: any) => {
        return data.map((item: any) => {
            return (
                <Option key={item.value} value={item.value} title={item.text}>
                    {item.text}
                </Option>
            );
        });
    };

    // 校验状态筛选
    onCheckStatusChange = (status: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: 1,
            status: status || undefined
        };

        this.props.getLists(params);
        this.setState({ params });
    };

    // user的select选项
    renderUserList = (data: any) => {
        return data.map((item: any) => {
            return (
                <Option
                    key={item.id}
                    value={item.id.toString()}
                    {...{ name: item.userName }}
                >
                    {item.userName}
                </Option>
            );
        });
    };

    // 监听userList的select
    onUserChange = (value: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: 1,
            lastModifyUserId: value || undefined
        };

        this.props.getLists(params);
        this.setState({ params });
    };

    // 执行时间改变
    onDateChange = (date: any, dateString: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: 1,
            executeTime: date ? date.valueOf() : undefined
        };

        this.props.getLists(params);
        this.setState({ params });
    };

    // table搜索
    onTableSearch = (name: any) => {
        this.props.getLists(this.state.params);
    };

    onTableNameChange = (e: any) => {
        let params: any = {
            ...this.state.params,
            currentPage: 1,
            tableName: e.target.value || undefined
        };
        this.setState({ params });
    }

    // 时间不能超过当天
    disabledDate = (current: any) => {
        return current && current.valueOf() > Date.now();
    };

    render () {
        const { dataCheck, dataSource, common } = this.props;
        const { userList } = common;
        const { sourceList } = dataSource;
        const { lists, loading } = dataCheck;

        const { params, visibleEnvModal, selectedRecord } = this.state;

        const pagination: any = {
            current: params.currentPage,
            pageSize: params.pageSize,
            total: lists.totalCount
        };

        const cardTitle = (
            <div className="flex font-12">
                <Search
                    placeholder="输入表名搜索"
                    onSearch={this.onTableSearch}
                    onChange={this.onTableNameChange}
                    style={{ width: 200 }}
                />

                <div className="m-l-8">
                    数据源：
                    <Select
                        allowClear
                        showSearch
                        style={{ width: 150 }}
                        optionFilterProp="title"
                        placeholder="选择数据源类型"
                        onChange={this.onUserSourceChange}
                    >
                        {this.renderUserSource(sourceList)}
                    </Select>
                </div>

                <div className="m-l-8">
                    校验结果：
                    <Select
                        allowClear
                        style={{ width: 150 }}
                        placeholder="选择校验结果"
                        onChange={this.onCheckStatusChange}
                    >
                        {this.renderCheckStatus(CHECK_STATUS_CN)}
                    </Select>
                </div>

                <div className="m-l-8">
                    最近修改人：
                    <Select
                        allowClear
                        showSearch
                        style={{ width: 150 }}
                        optionFilterProp="name"
                        placeholder="选择最近修改人"
                        onChange={this.onUserChange}
                    >
                        {this.renderUserList(userList)}
                    </Select>
                </div>

                <div className="m-l-8">
                    执行时间：
                    <DatePicker
                        format="YYYY-MM-DD"
                        style={{ width: 150 }}
                        placeholder="选择日期"
                        onChange={this.onDateChange}
                        disabledDate={this.disabledDate}
                    />
                </div>
                <div className="m-l-8">
                    <Tooltip title="刷新数据">
                        <Icon
                            type="sync"
                            onClick={() => {
                                this.props.getLists(params);
                            }}
                            style={{
                                cursor: 'pointer',
                                marginTop: '18px',
                                color: '#94A8C6'
                            }}
                        />
                    </Tooltip>
                </div>
            </div>
        );

        const cardExtra = (
            <Button type="primary" style={{ margin: '10px 0' }}>
                <Link to="/dq/dataCheck/add">新建逐行校验</Link>
            </Button>
        );

        return (
            <div className="check-dashboard">
                <h1 className="box-title">逐行校验</h1>

                <div className="box-2 m-card shadow">
                    <Card
                        title={cardTitle}
                        extra={cardExtra}
                        noHovering
                        bordered={false}
                    >
                        <Table
                            rowKey="id"
                            className="m-table"
                            columns={this.initColumns()}
                            loading={loading}
                            pagination={pagination}
                            dataSource={lists.data}
                            onChange={this.onTableChange}
                        />
                    </Card>
                </div>
                <EnvModal
                    key="ruleConfigEnvModal"
                    title="配置环境参数"
                    visible={visibleEnvModal}
                    onCancel={() =>
                        this.setState({
                            visibleEnvModal: false
                        })
                    }
                    value={selectedRecord && selectedRecord.taskParams}
                    onOk={this.updateEnvParams}
                />
            </div>
        );
    }
}
export default DataCheck;
