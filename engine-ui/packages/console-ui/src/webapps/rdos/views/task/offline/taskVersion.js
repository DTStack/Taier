import React from "react";
import { Table, Modal, message } from "antd";

import utils from "utils";

import DiffCodeEditor from "../../../components/diff-code-editor";
import { TASK_TYPE } from "../../../comm/const";
import DiffParams from "./diffParams";

export default class TaskVersion extends React.Component {
    state = {
        showDiff: false,
        campareTo: "",
        diffParams: {
            showDiffparams: false,
            tableInfo: ""
        }
    };

    constructor(props) {
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
            campareTo: ""
        });
    };

    closeParamsModal = () => {
        const { diffParams } = this.state;
        diffParams.showDiffparams = false;
        diffParams.tableInfo = "";
        this.setState({
            diffParams
        });
    };

    codeChange = (old, newVal) => {
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

    render() {
        const { taskInfo, taskType } = this.props;
        const { showDiff, campareTo, diffParams } = this.state;

        const isLocked =
            taskInfo.readWriteLockVO && !taskInfo.readWriteLockVO.getLock;
        let sqlTextJSON = taskInfo.sqlText;
        let compareToText = campareTo.sqlText;

        // 增加数据同步，JSON配置格式化操作
        if (taskInfo.taskType === TASK_TYPE.SYNC && taskInfo.sqlText) {
            sqlTextJSON = this.getFomatedJSON(taskInfo.sqlText);
            compareToText = this.getFomatedJSON(campareTo.sqlText);
        }

        return (
            <div>
                <Table
                    className="m-table"
                    rowKey="id"
                    dataSource={taskInfo.taskVersions || []}
                    columns={this.taskVersionCols()}
                    pagination={false}
                />
                <Modal
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    title="代码对比"
                    width="900px"
                    bodyStyle={{ minHeight: "500px" }}
                    visible={showDiff}
                    onCancel={this.close}
                    cancelText="关闭"
                    footer={null}
                >
                    <DiffCodeEditor
                        readOnly={isLocked}
                        value={sqlTextJSON}
                        compareTo={compareToText}
                        onChange={this.codeChange}
                    />
                </Modal>
                <Modal
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    title="参数对比"
                    width="900px"
                    bodyStyle={{ minHeight: "500px",paddingBottom: 20 }}
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

    taskTypeJudge = (taskInfo, record) => {
        if (taskInfo.taskType === TASK_TYPE.SQL || taskInfo.taskType === TASK_TYPE.SYNC) {
            return (
                <div>
                    <a onClick={() => this.diffCode(record)}>代码</a>
                    <span className="ant-divider" />
                    <a onClick={() => this.diffParams(record)}>参数</a>
                </div>
            );
        } else {
            return "-";
        }
    };

    taskVersionCols = () => {
        const taskInfo = this.props.taskInfo;
        return [
            {
                width: 120,
                title: "发布时间",
                dataIndex: "gmtCreate",
                key: "gmtCreate",
                render: text => {
                    return utils.formatDateTime(text);
                }
            },
            {
                title: "发布人",
                dataIndex: "userName",
                key: "userName"
            },
            {
                width: 120,
                title: "描述",
                dataIndex: "publishDesc",
                key: "publishDesc"
            },
            {
                title: "操作",
                dataIndex: "operation",
                width: 80,
                key: "operation",
                render: (text, record) => {
                    return this.taskTypeJudge(taskInfo, record);
                }
            }
        ];
    };
}
