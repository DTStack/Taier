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

import * as React from 'react';
import { Modal, Table, Tooltip, Button, Input, message } from 'antd';
import { QuestionCircleFilled } from '@ant-design/icons';
import Circle from '@/components/circle';
import { HAND_BUTTON_STATUS, HAND_TIED_STATUS, BIND_TYPE } from '@/constant';
import { cloneDeep } from 'lodash';

const Api = {} as any

interface IProps {
    visible: boolean;
    onCancel: () => void;
    selectedRowKeys: React.Key[];
    finishTied: () => void;
}

interface IState {
    tiedStatus: number;
    loading: boolean;
    dataSource: RecordType[];
    handleLoading: boolean;
}

interface RecordType {
    taskId: number;
    name: string;
    applicationId: string;
    bindStatus: number;
    errMessage: string;
}

class HandTiedModal extends React.Component<IProps, IState> {
    constructor (props: IProps) {
        super(props)
        this.state = {
            tiedStatus: 0,
            loading: false,
            dataSource: [],
            handleLoading: false
        }
    }
    bindStatusTimer: NodeJS.Timeout | undefined 

    componentWillUnmount () {
        if(this.bindStatusTimer) {
            clearInterval(this.bindStatusTimer)
        }
    }

    componentDidMount () {
        const { selectedRowKeys } = this.props
        this.yarnStreamTask(BIND_TYPE.MANUAL, selectedRowKeys)
    }

    yarnStreamTask = async (bindType: number, taskIdList: React.Key[]) => {
        this.setState({ handleLoading: true })
        let reqParams = {
            bindType,
            taskIds: taskIdList
        }
        await Api.streamTask(reqParams).then((res: { code: number; data: RecordType[]; }) => {
            if (res?.code == 1) {
                this.setState({ handleLoading: false, dataSource: res?.data || [] })
            }
        })
    }
    
    getBindStatus = (bindUniqueKey: string) => { 
        this.bindStatusTimer = setInterval(() => {
            Api.bindStatus({ bindUniqueKey }).then((res: any) => {
                let response = res?.data || []
                let loopStatus = new Set(response.map((item: Record<string, string>) => item['bindStatus'] ))
                if (loopStatus.size === 1 && loopStatus.has(HAND_TIED_STATUS.TIED_SUCCESS)) { // 全部绑定成功
                    this.bindStatusTimer && clearInterval(this.bindStatusTimer)
                    this.setState({ dataSource: response, tiedStatus: HAND_BUTTON_STATUS.TIED_SUCCESS })
                } else if (loopStatus.has(HAND_TIED_STATUS.TIED_FAIED) && !loopStatus.has(HAND_TIED_STATUS.TIED_WAIT)) { // 不存在运行中，存在失败
                    this.bindStatusTimer && clearInterval(this.bindStatusTimer)
                    this.setState({ dataSource: response, loading: false })
                } else {
                    this.setState({ dataSource: response })
                }
            })
        }, 10000)
    }

    handleSave = (key: 'applicationId' ,index: number, value: string) => {
        if (!value) {
            message.error('applicationId 不能为空')
        }
        const newData = cloneDeep(this.state.dataSource)
        newData[index][key] = value;
        this.setState({ dataSource: newData })
    }

    initColumns () {
        const { tiedStatus } = this.state
        function TiedStatus (props: { value: number, errMessage: string }) {
            const { value, errMessage } = props
            switch (value) {
                case HAND_TIED_STATUS.TIED_WAIT:
                    return <span><Circle className="mr-8" type="running" />绑定中</span>
                case HAND_TIED_STATUS.TIED_SUCCESS:
                    return <span><Circle className="mr-8" type="finished" />绑定成功</span>
                case HAND_TIED_STATUS.TIED_FAIED:
                    return <span><Circle className="mr-8" type="fail" />绑定失败
                        <Tooltip title={errMessage}>
                            <QuestionCircleFilled className="ml-8" type="question-circle" style={{ color: '#666' }} />
                        </Tooltip>
                    </span>
                default:
                    return null
            }
        }

        let columnsMap = {
            name: {
                title: '任务名称',
                dataIndex: 'name',
                key: 'name',
                width: 240
            },
            task: {
                title: '绑定任务',
                dataIndex: 'applicationId',
                key: 'applicationId',
                render: (applicationId: string, record: RecordType, index: number) => {
                    return (
                        <Input defaultValue={applicationId} onChange={(e) => { this.handleSave('applicationId', index, e.target.value) }}/>
                    )
                }
            },
            status: {
                title: '状态',
                dataIndex: 'bindStatus',
                key: 'bindStatus',
                render: (bindStatus: number, record: RecordType) => {
                    return <TiedStatus value={bindStatus || HAND_TIED_STATUS.TIED_WAIT} {...record}/>
                }
            }
        }
        
        if (tiedStatus == HAND_BUTTON_STATUS.READY_TIED) {
            return [columnsMap.name, columnsMap.task]
        } else {
            return [columnsMap.name, columnsMap.status]
        }
    }

    // 获取重绑所需 bindUniqueKey
    getBindUniqueKey = async () => {
        const { code, data } = await Api.getBindUniqueKey();
        return code === 1 && data ? data : ''
    }

    refreshStreamTask = async (bindUniqueKey: string, refreshList: any[]) => {
        let res = await Api.refreshStreamTask({ bindUniqueKey, refreshList })
        if (res?.code === 1) {
            return true
        }
        return false
    }

    // 校验 applicationid 为空
    validateApplicationId = () => {
        const { dataSource } = this.state;
        const haveEmpty = dataSource.some((item) => !item.applicationId);
        if (haveEmpty) {
            message.error('applicationId 不能为空')
        }
        return haveEmpty;
    }

    handleChangeStatus = async () => {
        const { tiedStatus, dataSource } = this.state
        if (tiedStatus === HAND_BUTTON_STATUS.READY_TIED && dataSource.length > 0) {
            const haveEmptyId = this.validateApplicationId();
            if (haveEmptyId) {
                return;
            }
            this.setState({ tiedStatus: HAND_BUTTON_STATUS.TIED_MIDDLE, loading: true })
            let bindUniqueKey = await this.getBindUniqueKey();
            let res = await this.refreshStreamTask(bindUniqueKey, dataSource)
            if (res) { this.getBindStatus(bindUniqueKey) }
        } else if (tiedStatus == HAND_BUTTON_STATUS.TIED_MIDDLE) {
            let failResponse = dataSource.filter(
                (item) => { return item['bindStatus'] === HAND_TIED_STATUS.TIED_FAIED }).map(
                (item) => { return item['taskId'] })
            await this.yarnStreamTask(BIND_TYPE.MANUAL, failResponse)
            this.setState({ tiedStatus: HAND_BUTTON_STATUS.READY_TIED })
        }
    }
    render () {
        const { visible, onCancel, finishTied } = this.props
        const { dataSource, handleLoading, tiedStatus, loading } = this.state
        let footerNode: React.ReactNode = []
        let handleCancel = onCancel;
        switch (tiedStatus) {
            case HAND_BUTTON_STATUS.READY_TIED:
                footerNode = [
                    <Button key="back" onClick={onCancel}>取消</Button>,
                    <Button key="submit" type="primary" onClick={this.handleChangeStatus}>确定</Button>]
                break;
            case HAND_BUTTON_STATUS.TIED_MIDDLE:
                handleCancel = finishTied;
                footerNode = [
                    <Button key="back" onClick={finishTied}>完成</Button>,
                    <Button key="submit" type="primary" onClick={this.handleChangeStatus} loading={loading}>继续绑定</Button>]
                break;
            case HAND_BUTTON_STATUS.TIED_SUCCESS:
                handleCancel = finishTied;
                footerNode = [<Button key="submit" type="primary" onClick={finishTied}>完成</Button>]
                break;
        }

        return (
            <Modal
                className='dt-modal-body-top-nopadding'
                title='手动重绑'
                visible={visible}
                footer={footerNode}
                onCancel={handleCancel}
            >
                <span style={{ color: '#666', lineHeight: '38px' }}>将任务与Yarn任务绑定保持状态一致；仅显示yarn上为running状态的任务重绑；</span>
                <Table
                    rowKey="taskId"
                    size="middle"
                    className="dt-pagination-lower dt-table-border dt-table-last-row-noborder"
                    dataSource={dataSource}
                    columns={this.initColumns()}
                    pagination={false}
                    style={{ maxHeight: '500px', overflow: 'auto' }}
                    loading={handleLoading}
                />
            </Modal>
        )
    }
}

export default HandTiedModal;
