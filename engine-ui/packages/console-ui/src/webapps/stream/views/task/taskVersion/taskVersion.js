import React from 'react';
import { Table, Modal } from 'antd';

import utils from 'utils';

import { TASK_TYPE } from '../../../comm/const';
import DiffParams from './diffParams';

class TaskVersion extends React.Component {
    state = {
        showDiffModal: false,
        campareTo: '',
        diffData: {},
        _modelKey: null
    };

    constructor (props) {
        super(props);
    }
    openDiff = (data) => {
        this.setState({
            diffData: data,
            showDiffModal: true
        });
    }
    closeDiff = () => {
        this.setState({
            diffData: {},
            showDiffModal: false,
            _modelKey: Math.random()
        });
    }

    codeChange = (newVal) => {
        this.props.changeSql(newVal);
    };

    render () {
        const { taskInfo } = this.props;
        const { showDiffModal, diffData } = this.state;
        return (
            <div>
                <Table
                    className="m-table"
                    rowKey="version"
                    dataSource={taskInfo.taskVersions || []}
                    columns={this.taskVersionCols()}
                    pagination={false}
                />
                <Modal
                    key={this.state._modelKey}
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    title="版本对比"
                    width="900px"
                    bodyStyle={{ minHeight: '500px', paddingBottom: 20 }}
                    visible={showDiffModal}
                    onCancel={this.closeDiff}
                    cancelText="关闭"
                    footer={null}
                >
                    <DiffParams
                        currentData={taskInfo}
                        versionData={diffData}
                    />
                </Modal>
            </div>
        );
    }

    taskTypeJudge = (taskInfo, record) => {
        if (taskInfo.taskType === TASK_TYPE.SQL ||
            taskInfo.taskType == TASK_TYPE.DATA_COLLECTION) {
            return (
                <div>
                    <a onClick={() => { this.openDiff(record) }}>版本对比</a>
                </div>
            );
        } else {
            return '-';
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
                    return this.taskTypeJudge(taskInfo, record);
                }
            }
        ];
    };
}

export default TaskVersion;
