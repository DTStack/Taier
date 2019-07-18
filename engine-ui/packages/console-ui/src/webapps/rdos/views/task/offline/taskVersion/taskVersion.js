import React from 'react';
import { Table, Modal, Button, message } from 'antd';

import utils from 'utils';

import { TASK_TYPE, DEAL_MODEL_TYPE } from '../../../../comm/const';
import DiffTask from './diffTask';
import ajax from '../../../../api/index';

const confirm = Modal.confirm;

const getLanguage = (type) => {
    switch (type) {
        case TASK_TYPE.SYNC: {
            return 'json';
        }
        case TASK_TYPE.PYTHON_23: {
            return 'python';
        }
        case TASK_TYPE.SQL: {
            return 'dtsql';
        }
        default: {
            return 'dtsql';
        }
    }
}

export default class TaskVersion extends React.Component {
    state = {
        showDiff: false,
        haveCode: false,
        compareTo: '',
        historyValue: '' // 历史数据
    };

    _modalKey = 0;

    constructor (props) {
        super(props);
    }

    /**
     * 加载调度配置
     */
    getScheduleConfData = (id) => {
        this.setState({
            historyValue: {}
        })
        ajax.taskVersionScheduleConf({ versionId: id }).then(res => {
            if (res.code == 1) {
                this.setState({
                    historyValue: res.data || {}
                })
            } else {
                this.setState({
                    historyValue: {}
                })
            }
        })
    }

    diffParams = (target, haveCode) => {
        this.setState({
            showDiff: true,
            compareTo: target,
            haveCode: haveCode
        });
        this.getScheduleConfData(target.id);
    };

    closeDiffModal = () => {
        this.setState({
            showDiff: false,
            compareTo: {},
            hoveCode: false
        });
    };

    codeChange = (newVal) => {
        this.props.changeSql(newVal);
    };

    taskRollsBack = () => {
        const { updateTaskField } = this.props;
        const { historyValue, compareTo } = this.state;
        const ctx = this;
        confirm({
            title: '确认执行任务回滚操作吗？',
            content: '任务回滚可帮助您恢复历史版本的任务数据！',
            onOk () {
                message.success('回滚成功！');
                updateTaskField({
                    sqlText: compareTo.sqlText,
                    scheduleConf: historyValue.scheduleConf,
                    taskParams: historyValue.taskParams,
                    merged: true
                });
                ctx._modalKey++;
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }

    render () {
        const { taskInfo, taskType, editor } = this.props;
        const { showDiff, compareTo, haveCode, historyValue } = this.state;

        let sqlTextJSON = taskInfo.sqlText || '';
        let compareToText = compareTo.sqlText || '';
        const language = getLanguage(taskInfo.taskType);

        const diffCode = {
            original: { value: sqlTextJSON },
            modified: { value: compareToText },
            options: { readOnly: true },
            theme: editor.options.theme,
            onChange: this.codeChange,
            language: language
        }
        let isSupportRollback = false;
        if (haveCode && taskInfo.taskType !== TASK_TYPE.SYNC) {
            isSupportRollback = true;
        }
        // 目前暂时只支持有代码的任务类型进行回滚操作
        const footer = isSupportRollback ? <div style={{ marginRight: 30 }}>
            <Button type="primary" onClick={this.taskRollsBack}>版本回滚</Button>
            <Button type="primary" onClick={this.closeDiffModal}>关闭</Button>
        </div> : null;

        return (
            <div style={{ marginBottom: 16 }}>
                <Table
                    className="m-table"
                    rowKey="id"
                    dataSource={taskInfo.taskVersions || []}
                    columns={this.taskVersionCols()}
                    pagination={false}
                />
                <Modal
                    wrapClassName=".vertical-center-modal-no-width modal-body-nopadding"
                    title="版本对比"
                    width="900px"
                    bodyStyle={{ minHeight: '500px', paddingBottom: 20 }}
                    visible={showDiff}
                    onCancel={this.closeDiffModal}
                    cancelText="关闭"
                    footer={footer}
                >
                    <DiffTask
                        key={`${taskInfo.id}-${compareTo.id}-${this._modalKey}`}
                        currentTabData={taskInfo}
                        diffParams={compareTo}
                        taskType={taskType}
                        diffCode={diffCode}
                        editor={editor}
                        showDiffCode={haveCode}
                        historyValue={historyValue}
                    />
                </Modal>
            </div>
        );
    }

    renderTaskOperation = (taskInfo, record) => {
        const showCodeAndConfig = <div>
            <a onClick={() => this.diffParams(record, true)}>版本对比</a>
        </div>;
        const showConfig = <div>
            <a onClick={() => this.diffParams(record, false)}>版本对比</a>
        </div>;

        switch (taskInfo.taskType) {
            case TASK_TYPE.SQL:
            case TASK_TYPE.CARBONSQL:
            case TASK_TYPE.SYNC:
            case TASK_TYPE.SHELL:
            case TASK_TYPE.DATA_COLLECTION: {
                return showCodeAndConfig;
            }
            case TASK_TYPE.DEEP_LEARNING:
            case TASK_TYPE.PYTHON_23:
            case TASK_TYPE.PYTHON: {
                // 脚本模式
                if (taskInfo.operateModel == DEAL_MODEL_TYPE.EDIT) {
                    return showCodeAndConfig;
                }
                return showConfig;
            }
            case TASK_TYPE.MR:
            case TASK_TYPE.ML:
            case TASK_TYPE.WORKFLOW:
            case TASK_TYPE.HAHDOOPMR:
            case TASK_TYPE.VIRTUAL_NODE: {
                return showConfig;
            }

            default: return '-';
        }
    };

    taskVersionCols = () => {
        const isPro = this.props.isPro;
        const taskInfo = this.props.taskInfo;
        const pre = isPro ? '发布' : '提交'
        return [
            {
                width: 120,
                title: pre + '时间',
                dataIndex: 'gmtCreate',
                key: 'gmtCreate',
                render: text => {
                    return utils.formatDateTime(text);
                }
            },
            {
                title: pre + '人',
                dataIndex: 'userName',
                key: 'userName'
            },
            {
                width: 120,
                title: '描述',
                dataIndex: 'publishDesc',
                key: 'publishDesc'
            },
            {
                title: '操作',
                dataIndex: 'operation',
                width: 80,
                key: 'operation',
                render: (text, record) => {
                    return this.renderTaskOperation(taskInfo, record);
                }
            }
        ];
    };
}
