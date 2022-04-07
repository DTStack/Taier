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

import * as React from 'react'
import moment from 'moment'
import {
    message, Modal,
    DatePicker, Radio,
    Select, Alert, Button, Col, Input, Form, RadioChangeEvent
} from 'antd'
import { DateTime, Utils } from '@dtinsight/dt-utils'
import { CHECK_TYPE_VALUE } from '@/constant'
import { ValidateStatus } from 'antd/lib/form/FormItem'
import { RangePickerProps } from 'antd/lib/date-picker'
import { isEmpty } from 'lodash'

const Api = {} as any

const { RangePicker } = DatePicker;
const Option = Select.Option

interface IProps {
    taskId: number | undefined;
    visible: boolean;
    onOk: () => void;
    onCancel: () => void;
}

interface IState {
    checkPoints: ICheckPoint[];
    dateRange: {
        startTime?: string;
        endTime?: string;
    } | null;
    externalPath: string;
    rangeValue: RangePickerProps['value'];
    savePoint: any;
    checkedValue: CHECK_TYPE_VALUE;
    filePath: string;
    filePathValid: {
        help: string;
        validateStatus: ValidateStatus;
    }
}

interface ICheckPoint {
    id: number;
    time: string;
    externalPath: string;
}

interface IReqParamsCheckPoint {
    taskId: number | undefined;
    startTime: number | undefined;
    endTime: number | undefined;
}

@Utils.shouldRender
class GoOnTask extends React.Component<IProps, IState> {
    state: IState = {
        checkPoints: [],
        dateRange: null,
        externalPath: '',
        rangeValue: undefined,
        savePoint: {},
        checkedValue: CHECK_TYPE_VALUE.CHECK_POINT_FILE,
        filePath: '',
        filePathValid: {
            help: '',
            validateStatus: 'success'
        }
    }
    async componentDidMount () {
        const taskId = this.props.taskId;
        if (taskId) {
            /**
             * 优先级
             * 有 savepoint 默认选 savepoint 续跑，没有就默认选择 checkpoint, 最后文件续跑
             **/
            await this.getSavePoint({ taskId })
            this.getCheckPointRange({ taskId })
        }
    }

    getCheckPointRange = (params: { taskId: number }) => {
        const { savePoint } = this.state;
        Api.getCheckPointRange(params).then((res: any) => {
            if (res.code === 1) {
                const { startTime, endTime } = res.data;
                if (startTime && endTime) {
                    this.setState({
                        dateRange: res.data,
                        checkedValue: isEmpty(savePoint) ? CHECK_TYPE_VALUE.CHECK_POINT : CHECK_TYPE_VALUE.SAVE_POINT
                    })
                }
            }
        })
    }

    getSavePoint = async (params: { taskId: number }) => {
        const res = await Api.getSavePoint(params);
        if (res.code === 1) {
            if (Object.values(res.data).every(d => !!d)) {
                this.setState({
                    savePoint: res.data,
                    checkedValue: CHECK_TYPE_VALUE.SAVE_POINT
                })
            }
        }
    }

    // 提交续跑
    doGoOn = () => {
        const { checkedValue, filePathValid } = this.state;
        // 文件路径校验
        if (checkedValue === CHECK_TYPE_VALUE.CHECK_POINT_FILE && filePathValid.validateStatus === 'error') {
            return;
        }
        let externalPath = this.setExternalPath();
        if (!externalPath) {
            message.error('请选择续跑点！')
            return;
        }
        Api.startTask({
            id: this.props.taskId,
            externalPath,
            isRestoration: 0
        }).then((res: any) => {
            if (res.code === 1) {
                message.success('续跑操作成功！')
                this.props.onOk();
            }
        })
    }

    // 处理 path
    setExternalPath = () => {
        const { checkedValue, externalPath: checkPath, savePoint, filePath } = this.state
        let externalPath = ''
        switch (checkedValue) {
            case CHECK_TYPE_VALUE.CHECK_POINT:
                externalPath = checkPath;
                break;
            case CHECK_TYPE_VALUE.SAVE_POINT:
                externalPath = savePoint.externalPath;
                break;
            case CHECK_TYPE_VALUE.CHECK_POINT_FILE:
                externalPath = filePath;
                break;
            default:
                break;
        }
        return externalPath;
    }

    cancel = () => {
        this.setState({
            externalPath: '',
            dateRange: null,
            checkPoints: [],
            rangeValue: undefined,
            savePoint: {},
            checkedValue: CHECK_TYPE_VALUE.CHECK_POINT_FILE,
            filePath: '',
            filePathValid: {
                help: '',
                validateStatus: 'success'
            }
        }, () => {
            this.props.onCancel();
        })
    }

    getCheckPoints = (params: IReqParamsCheckPoint) => {
        const { dateRange } = this.state
        if (!dateRange) return;
        Api.getCheckPoints(params).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    checkPoints: res.data
                })
            }
        })
    }

    taskReadRangeChange = (value: RangePickerProps['value']) => {
        this.setState({
            rangeValue: value,
            externalPath: '',
            checkPoints: []
        })
        if (!value) return;

        const start = value[0]?.hour(0).minute(0).second(0)
        const end = value[1]?.hour(23).minute(59).second(59)

        this.getCheckPoints({
            taskId: this.props.taskId,
            startTime: start?.valueOf(),
            endTime: end?.valueOf()
        })
    }

    taskReadTimeChange = (value: string) => {
        this.setState({
            externalPath: value
        })
    }

    /**
     * 置灰startTime至endTime时间段之外的时间
     */
    disabledDate = (current: moment.Moment) => {
        const { dateRange } = this.state
        if(!dateRange) return false
        const startTime = moment(dateRange.startTime)
        const endTime = moment(dateRange.endTime)
        startTime.set({ hour: 0, minute: 0, second: 0, millisecond: 0 })
        endTime.set({ hour: 23, minute: 59, second: 59, millisecond: 0 })

        return current.valueOf() < startTime.valueOf() || current.valueOf() > endTime.valueOf()
    }

    // 单选框选择
    handleCheckedChange = (e: RadioChangeEvent) => {
        this.setState({
            checkedValue: e.target.value,
            filePathValid: {
                help: '',
                validateStatus: 'success'
            }
        })
    }

    // 通过文件续跑
    handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { value } = e.target
        const filePathValid = this.validateFilePath(value);
        this.setState({
            filePath: value,
            filePathValid
        })
    }

    // 文件路径校验
    validateFilePath = (value: string) => {
        let help = '';
        let validateStatus: ValidateStatus = 'success';
        if (!(/^hdfs:\/\//.test(value))) {
            help = '请输入以”hdfs://”开头的HDFS地址';
            validateStatus = 'error';
        } else if (/\s/.test(value)) {
            help = '文件路径不支持空格';
            validateStatus = 'error';
        }
        return { help, validateStatus }
    }

    render () {
        const { visible } = this.props
        const { dateRange, checkPoints, rangeValue, externalPath, checkedValue, savePoint, filePath, filePathValid } = this.state;

        const options = checkPoints?.map((item) => {
            const { time, id, externalPath } = item || {}
            const title = DateTime.formatDateTime(time)
            const nameFix = { name: item }

            return (
                <Option 
                    title={title} 
                    key={id} 
                    value={externalPath} 
                    {...nameFix}
                >
                    {time}
                </Option>
            )
        })

        return (
            <Modal
                title="续跑任务"
                visible={visible}
                okText="确认"
                onCancel={this.cancel}
                cancelText="取消"
                maskClosable={false}
                footer={
                    <span>
                        <Button onClick={this.cancel}>取消</Button>
                        <Button type="primary" onClick={this.doGoOn}>确认</Button>
                    </span>
                }
            >
                <Alert
                    className="modal-alert"
                    message="续跑，任务将恢复至停止前的状态继续运行，若存在启停策略，将恢复自动启停!"
                    type="warning"
                    showIcon
                />
                <Radio.Group value={checkedValue} onChange={this.handleCheckedChange}>
                    <Col style={{ marginBottom: 20 }}>
                        <Radio disabled={!savePoint?.time} value={CHECK_TYPE_VALUE.SAVE_POINT}>
                            {`通过SavePoint恢复并续跑（上次保存时间：${savePoint?.time ? DateTime.formatDateTime(savePoint.time) : '- '}）`}
                        </Radio>
                    </Col>
                    <Col style={{ marginBottom: 20 }}>
                        <Radio disabled={!dateRange} value={CHECK_TYPE_VALUE.CHECK_POINT} style={{ marginBottom: 12 }}>通过CheckPoint恢复并续跑</Radio>
                        <div>
                            <span style={{ marginRight: '12px' }}>
                                <RangePicker
                                    style={{ width: '280px' }}
                                    format="YYYY-MM-DD"
                                    disabledDate={this.disabledDate}
                                    onChange={this.taskReadRangeChange}
                                    value={rangeValue}
                                    disabled={!dateRange || checkedValue !== CHECK_TYPE_VALUE.CHECK_POINT}
                                />
                            </span>
                            <span>
                                <Select
                                    showSearch
                                    style={{ width: '180px' }}
                                    placeholder="时间点"
                                    optionFilterProp="name"
                                    onChange={this.taskReadTimeChange}
                                    disabled={!dateRange || checkedValue !== CHECK_TYPE_VALUE.CHECK_POINT}
                                    value={externalPath}
                                >
                                    {options}
                                </Select>
                            </span>
                        </div>
                    </Col>
                    <Col>
                        <Radio value={CHECK_TYPE_VALUE.CHECK_POINT_FILE} style={{ marginBottom: 12 }}>通过指定文件恢复并续跑</Radio>
                        <Form.Item
                            style={{ marginBottom: 0 }}
                            help={filePathValid.help}
                            validateStatus={filePathValid.validateStatus}
                        >
                            <Input
                                placeholder="请输入HDFS中CheckPoin文件完整路径，例如：hdfs://"
                                disabled={checkedValue !== CHECK_TYPE_VALUE.CHECK_POINT_FILE}
                                value={filePath}
                                onChange={this.handleInputChange}
                            />
                        </Form.Item>
                    </Col>
                </Radio.Group>
            </Modal>
        )
    }
}

export default GoOnTask
