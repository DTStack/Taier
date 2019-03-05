import React from 'react';
import { Table, Modal, message } from 'antd';

import utils from 'utils';

import DiffCodeEditor from 'widgets/editor/diff';
import { TASK_TYPE, DEAL_MODEL_TYPE } from '../../../comm/const';
import DiffParams from './diffParams';

export default class TaskVersion extends React.Component {
    state = {
        showDiff: false,
        campareTo: '',
        diffParams: {
            showDiffparams: false,
            tableInfo: ''
        }
    };

    constructor (props) {
        super(props);
    }

    diffCode = target => {
        this.setState({
            showDiff: true,
            campareTo: target
        });
    };

    diffParams = data => {
        const { diffParams } = this.state;
        diffParams.showDiffparams = true;
        diffParams.tableInfo = data;
        this.setState({
            diffParams
        });
    };

    close = () => {
        this.setState({
            showDiff: false,
            campareTo: {}
        });
        this._modalKey = ~~(Math.random() * 10000)
    };

    closeParamsModal = () => {
        const { diffParams } = this.state;
        diffParams.showDiffparams = false;
        diffParams.tableInfo = '';
        this.setState({
            diffParams
        });
    };

    codeChange = (newVal) => {
        this.props.changeSql(newVal);
    };

    getFomatedJSON = (jsonText) => {
        if (!jsonText) return '';
        const output = utils.jsonFormat(jsonText);
        if (!output) {
            message.error('您的数据同步JSON配置格式有误');
            return jsonText;
        }
        return output;
    }

    render () {
        const { taskInfo, taskType, editor } = this.props;
        const { showDiff, campareTo, diffParams } = this.state;

        let sqlTextJSON = taskInfo.sqlText;
        let compareToText = campareTo.sqlText;
        let language;
        switch (taskInfo.taskType) {
            case TASK_TYPE.SYNC: {
                language = 'json';
                break;
            }
            case TASK_TYPE.PYTHON_23: {
                language = 'python';
                break;
            }
            case TASK_TYPE.SQL: {
                language = 'dtsql';
                break;
            }
            default: {
                language = 'dtsql';
            }
        }

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
                    key={this._modalKey}
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    title="代码对比"
                    width="900px"
                    bodyStyle={{ height: '500px' }}
                    visible={showDiff}
                    onCancel={this.close}
                    cancelText="关闭"
                    footer={null}
                >
                    <DiffCodeEditor
                        className="merge-text"
                        original={{ value: sqlTextJSON }}
                        modified={{ value: compareToText }}
                        options={{ readOnly: true }}
                        theme={ editor.options.theme }
                        onChange={this.codeChange}
                        language={language}
                    />
                </Modal>
                <Modal
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    title="参数对比"
                    width="900px"
                    bodyStyle={{ minHeight: '500px', paddingBottom: 20 }}
                    visible={diffParams.showDiffparams}
                    onCancel={this.closeParamsModal}
                    cancelText="关闭"
                    footer={null}
                >
                    <DiffParams
                        value={taskInfo}
                        diffParams={diffParams.tableInfo}
                        taskType={taskType}
                    />
                </Modal>
            </div>
        );
    }

    renderTaskOperation = (taskInfo, record) => {
        const showCodeAndConfig = <div>
            <a onClick={() => this.diffCode(record)}>代码</a>
            <span className="ant-divider" />
            <a onClick={() => this.diffParams(record)}>参数</a>
        </div>;
        const showConfig = <div>
            <a onClick={() => this.diffParams(record)}>参数</a>
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
