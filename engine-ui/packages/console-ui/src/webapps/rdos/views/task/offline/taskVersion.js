import React from 'react';
import { connect } from 'react-redux';
import { Row, Table, Modal } from 'antd';

import utils from 'utils';

import DiffCodeEditor from '../../../components/diff-code-editor';
import { TASK_TYPE } from '../../../comm/const';

export default class TaskVersion extends React.Component {

    state = {
        showDiff: false,
        campareTo: '',
    }

    constructor(props) {
        super(props);
    }

    diffCode = (target) => {
        this.setState({
            showDiff: true,
            campareTo: target
        })
    }

    close = () => {
        this.setState({
            showDiff: false,
            campareTo: '',
        })
    }

    codeChange = (old, newVal) => {
        this.props.changeSql(newVal)
    }
    
    render() {
        const { taskInfo } = this.props;
        const { showDiff, campareTo } = this.state;
        return (
            <div>
                <Table
                    rowKey="id"
                    dataSource={taskInfo.taskVersions || []}
                    columns={this.taskVersionCols()}
                    pagination={false}
                />
                <Modal
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    title="历史版本对比"
                    width="900px"
                    bodyStyle={{height: '500px'}}
                    visible={showDiff}
                    onCancel={this.close}
                    cancelText="关闭"
                    footer={null}
                >
                    <DiffCodeEditor 
                        value={taskInfo.sqlText} 
                        compareTo={campareTo.sqlText}
                        onChange={this.codeChange}
                    />
                </Modal>
            </div>
        )
    }

    taskVersionCols = () => {
        const taskInfo = this.props.taskInfo
        return [
            {
                title: '发布时间',
                dataIndex: 'gmtCreate',
                key: 'gmtCreate',
                render: (text) => {
                    return utils.formatDateTime(text)
                },
            }, {
                title: '发布人',
                dataIndex: 'userName',
                key: 'userName',
                render: (text, record) => {
                    return record.task && record.task.modifyUser && record.task.modifyUser.userName
                },
            }, {
                title: '描述',
                dataIndex: 'publishDesc',
                key: 'publishDesc',
            }, {
                title: '操作',
                dataIndex: 'id',
                width: 80,
                key: 'operation',
                render: (text, record) => {
                    return <span>
                        {taskInfo.taskType === TASK_TYPE.SQL ? 
                            <a onClick={() => this.diffCode(record)}>
                                代码
                            </a> : '-'
                        }
                    </span>
                },
            }
        ]
    }

}