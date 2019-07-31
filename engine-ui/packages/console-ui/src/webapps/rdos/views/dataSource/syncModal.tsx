import * as React from 'react';
import { connect } from 'react-redux';
import { Modal, Table, Button, Tooltip, Icon } from 'antd';

import Editor from 'widgets/code-editor';

import Api from '../../api';
import { workbenchActions } from '../../store/modules/offlineTask/offlineAction';

const editorOptions: any = {
    mode: 'text',
    lineNumbers: true,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    lineWrapping: true,
    smartIndent: true,
    scrollbarStyle: 'simple'
};

class dbSyncHistoryModal extends React.Component<any, any> {
    state: any = {
        listData: {},
        showDetail: false,
        syncDetail: {},
        showReport: false,
        report: ''
    };

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (nextProps.source.id) {
            Api.getSyncHistoryList({
                dataSourceId: nextProps.source.id,
                currentPage: 1,
                pageSize: 5
            }).then((res: any) => {
                if (res.code === 1) {
                    this.setState({ listData: res.data });
                }
            });
        }
    }

    showConfigDetail = (id: any) => {
        this.setState({ showDetail: true });

        Api.getSyncDetail({ migrationId: id }).then((res: any) => {
            if (res.code === 1) {
                this.setState({ syncDetail: res.data });
            }
        });
    };

    showReport = (record: any) => {
        this.setState({
            showReport: true,
            report: record.report
        });
    };

    // table设置
    initColumns = () => {
        return [
            {
                title: '配置时间',
                dataIndex: 'gmtCreateFormat',
                key: 'gmtCreateFormat',
                width: '30%'
            },
            {
                title: '配置表数量',
                dataIndex: 'taskCount',
                key: 'taskCount',
                width: '25%'
            },
            {
                title: '配置人',
                dataIndex: 'createUserName',
                key: 'createUserName',
                width: '30%'
            },
            {
                title: '操作',
                width: '10%',
                render: (text: any, record: any) => {
                    return (
                        <a
                            onClick={this.showConfigDetail.bind(
                                this,
                                record.id
                            )}
                        >
                            查看详情
                        </a>
                    );
                }
            }
        ];
    };

    // table设置
    initDbTableColumns = () => {
        return [
            {
                title: '表名',
                dataIndex: 'tableName',
                key: 'tableName',
                width: '40%'
            },
            {
                title: 'DTinsight.IDE',
                dataIndex: 'ideTableName',
                key: 'ideTableName',
                width: '30%'
            },
            {
                title: '任务状态',
                width: '30%',
                render: (text: any, record: any) => {
                    if (record.status) {
                        return record.status === 1 ? (
                            <div>
                                <span className="m-r-8">
                                    <Icon
                                        type="check-circle"
                                        style={{
                                            color: 'green',
                                            marginRight: 8
                                        }}
                                    />
                                    成功
                                </span>
                                <a
                                    className="m-l-8"
                                    onClick={this.checkTask.bind(
                                        this,
                                        record.taskId
                                    )}
                                >
                                    查看任务
                                </a>
                            </div>
                        ) : (
                            <div>
                                <span className="m-r-8">
                                    <Icon
                                        type="close-circle"
                                        style={{ color: 'red', marginRight: 8 }}
                                    />
                                    <Tooltip
                                        overlayClassName="sync-tooltip"
                                        placement="bottom"
                                        title={record.report}
                                    >
                                        失败
                                    </Tooltip>
                                </span>
                                <a
                                    className="m-l-8"
                                    onClick={this.showReport.bind(this, record)}
                                >
                                    查看报告
                                </a>
                            </div>
                        );
                    }
                }
            }
        ];
    };

    checkTask = (taskId: any) => {
        this.props.goToTaskDev(taskId);
    };

    back = () => {
        this.setState({
            showDetail: false,
            syncDetail: {}
        });
    };

    renderContent = () => {
        const { showDetail, listData, syncDetail } = this.state;
        const { source } = this.props;

        if (!showDetail) {
            return (
                <div className="sync-list">
                    <h2>{source.dataName}</h2>

                    <Table
                        rowKey="id"
                        className="dt-ant-table dt-ant-table--border"
                        style={{ marginTop: 10 }}
                        columns={this.initColumns()}
                        pagination={false}
                        dataSource={listData.data}
                    />
                </div>
            );
        } else {
            let scheduleConf = syncDetail.scheduleConf
                ? JSON.parse(syncDetail.scheduleConf)
                : {};

            return (
                <div className="sync-detail">
                    <Icon
                        type="left-circle"
                        style={{ cursor: 'pointer', fontSize: 14 }}
                        onClick={this.back}
                    />{' '}
                    返回
                    <Table
                        rowKey="id"
                        className="dt-ant-table dt-ant-table--border m-v-10"
                        columns={this.initDbTableColumns()}
                        pagination={false}
                        dataSource={syncDetail.migrationTasks}
                    />
                    <p>
                        生效日期：
                        {`${scheduleConf.beginDate} 到 ${scheduleConf.endDate}`}
                    </p>
                    <p>调度周期：天</p>
                    <p>
                        选择时间：
                        {scheduleConf.hour < 10
                            ? `0${scheduleConf.hour} : 00`
                            : `${scheduleConf.hour} : 00`}
                    </p>
                    <p>
                        同步方式：{syncDetail.syncType === 1 ? '增量' : '全量'}
                    </p>
                    {syncDetail.syncType === 1 && (
                        <p>根据日期字段：{syncDetail.timeFieldIdentifier}</p>
                    )}
                    <p>
                        并发配置：
                        {syncDetail.parallelType === 1
                            ? '分批上传'
                            : '整批上传'}
                    </p>
                    {syncDetail.parallelType === 1 && (
                        <p>
                            从启动时间开始，每
                            {syncDetail.parallelConfig.hourTime}小时同步
                            {syncDetail.parallelConfig.tableNum}张表
                        </p>
                    )}
                </div>
            );
        }
    };

    closeModal = () => {
        this.props.cancel();

        this.setState({
            syncDetail: {},
            showDetail: false
        });
    };

    hideReport = () => {
        this.setState({ showReport: false, report: '' });
    };

    render () {
        const { visible } = this.props;
        return (
            <div>
                <Modal
                    title="整库同步配置历史"
                    wrapClassName="sync-history-modal"
                    width={'50%'}
                    visible={visible}
                    maskClosable={false}
                    onCancel={this.closeModal}
                    footer={
                        <Button
                            key="back"
                            type="primary"
                            onClick={this.closeModal}
                        >
                            关闭
                        </Button>
                    }
                >
                    {this.renderContent()}
                </Modal>
                <Modal
                    title="任务日志"
                    wrapClassName="vertical-center-modal m-log-modal"
                    width={800}
                    visible={this.state.showReport}
                    maskClosable={false}
                    onCancel={this.hideReport}
                    footer={
                        <Button
                            key="back"
                            type="primary"
                            onClick={this.hideReport}
                        >
                            关闭
                        </Button>
                    }
                >
                    <Editor
                        sync
                        style={{ height: '520px' }}
                        value={this.state.report}
                        options={editorOptions}
                    />
                </Modal>
            </div>
        );
    }
}

export default connect(
    (state: any) => {
        return {
            project: state.project,
            projectUsers: state.projectUsers
        };
    },
    (dispatch: any) => {
        const actions = workbenchActions(dispatch);
        return {
            goToTaskDev: (id: any) => {
                actions.openTaskInDev(id);
            }
        };
    }
)(dbSyncHistoryModal);
