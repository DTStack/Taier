/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { FLINK_SQL_TYPE, TASK_TYPE_ENUM } from "@/constant";
import { DateTime } from "@dtinsight/dt-utils/lib";
import molecule from "@dtinsight/molecule";
import { Button, message, Modal, Table } from "antd";
import { useState } from "react";
import DiffParams from "./diffParams";

interface IProps extends molecule.model.IEditor {
    changeSql: (orgs: any) => void;
    updateTaskField: (orgs: any) => void;
    taskType: string;
    isLocked: boolean;
    isPro?: boolean
}
interface IVersionDetail {
    taskType: number;
    sqlText: string;
    gmtCreate: string;
    userName: string;
    publishDesc: string;
    taskParams: string;
    side: string;
    sideParams: string;
    sink: string;
    sinkParams: string;
    source: string;
    sourceParams: string;
    createModel: number;
}

export default function TaskVersion(props: IProps) {
    const { current, isPro, isLocked, updateTaskField } = props;
    const taskInfo = current?.tab?.data || {};

    const [selectVersions, setSelectVersions] = useState<any[]>([]);
    const [currentData, setCurrentData] = useState<IVersionDetail>({} as IVersionDetail);
    const [diffData, setDiffData] = useState<IVersionDetail>({} as IVersionDetail);
    const [showDiffModal, setShowDiffModal] = useState(false)
    const [modelKey, setModelKey] = useState(0);

    const taskVersionCols = () => {
        const pre = isPro ? '发布' : '提交'
        return [
            {
                width:200,
                title: pre + '时间',
                dataIndex: 'gmtCreate',
                key: 'gmtCreate',
                render: (text: any) => {
                    return DateTime.formatDateTime(text);
                }
            },
            {
                width:200,
                title: pre + '人',
                dataIndex: 'userName',
                key: 'userName'
            },
            {
                width:120,
                title: '描述',
                dataIndex: 'publishDesc',
                key: 'publishDesc'
            }
        ];
    }
    const isRenderOperate = () => {
        if ([TASK_TYPE_ENUM.SQL, TASK_TYPE_ENUM.DATA_COLLECTION].includes(taskInfo.taskType)) {
            return true
        } else {
            return false
        }
    }
    const openDiff = () => {
        setCurrentData(selectVersions?.[0]);
        setDiffData(selectVersions?.[1]);
        setShowDiffModal(true)
    }

    const closeDiff = () => {
        setDiffData({} as IVersionDetail);
        setShowDiffModal(false)
        setModelKey(Math.random())
    }
    const getEditorHeader= (data: IVersionDetail | any) => {
        const { gmtCreate } = data || {}
        const pre = isPro ? '发布' : '提交'
        return `${pre}时间： ${DateTime.formatDateTime(gmtCreate)}`
    }
    const handleRollBack = () => {
        const rollBackVersion = selectVersions?.[0]
        let couldRollBack = true
        const isSql = taskInfo?.taskType == TASK_TYPE_ENUM.SQL
        /**
         * sql下不同模式和老版本后端情况下不能回滚
         */
        if (isSql) {
            if (taskInfo.createModel !== rollBackVersion.createModel || !rollBackVersion.sourceParams) {
                couldRollBack = false;
            }
        } else {
            couldRollBack = false;
        }
        if (!couldRollBack) return message.warn('该任务版本不支持回滚!')
        taskRollsBack(rollBackVersion)
    }
    const taskRollsBack = (rollBackVersion: IVersionDetail) => {
        const { sqlText, taskParams, side, sideParams, sink, sinkParams, source, sourceParams } = rollBackVersion || {}
        Modal.confirm({
            title: '确认执行任务回滚操作吗？',
            content: '任务回滚可帮助您恢复历史版本的任务数据！',
            onOk: () => {
                if (taskInfo.taskType == TASK_TYPE_ENUM.SQL && taskInfo.createModel == FLINK_SQL_TYPE.GUIDE) {
                    updateTaskField({
                        sqlText,
                        taskParams,
                        side: side || JSON.parse(sideParams),
                        sink: sink || JSON.parse(sinkParams),
                        source: source || JSON.parse(sourceParams),
                        merged: true
                    });
                } else {
                    updateTaskField({
                        sqlText: sqlText,
                        taskParams: taskParams,
                        merged: true
                    });
                }
                message.success('回滚成功！');
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }

    const disabledCompare = selectVersions?.length !== 2 || isLocked
    const diabledRollBack = selectVersions?.length !== 1 || isLocked
    return <div className='history-version__container'>
        <Table
            className="dt-table-border"
            scroll={{ x: '340' }}
            size="middle"
            rowSelection={{
                onChange: (_, selectedRows) => {
                    setSelectVersions(selectedRows)
                }
            }}
            rowKey={(record, index) => { return '' + index; }}
            dataSource={taskInfo.taskVersions || []}
            columns={taskVersionCols()}
            pagination={false}
        />
        {isRenderOperate() &&
            (<div className='footer-btn__group'>
                <Button
                    type="link"
                    disabled={disabledCompare}
                    onClick={openDiff}
                >
                    版本对比
                </Button>
                <Button
                    type="link"
                    disabled={diabledRollBack}
                    onClick={handleRollBack}
                >
                    版本回滚
                </Button>
            </div>)
        }
        <Modal
            key={modelKey}
            wrapClassName="modal-body-nopadding dt-modal-overflow-auto"
            title="版本对比"
            width="900px"
            bodyStyle={{ paddingBottom: 20 }}
            visible={showDiffModal}
            onCancel={closeDiff}
            cancelText="关闭"
            footer={<></>}
        >
            <div className='comparedModal__content-currentVersion-header'>
                {getEditorHeader(currentData)}
            </div>
            <div className='comparedModal__content-diffVersion-header'>
                {getEditorHeader(diffData)}
            </div>
            <DiffParams
                currentData={currentData}
                versionData={diffData}
            />
        </Modal>
    </div>
}