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
import { Modal, Alert, Radio, DatePicker,
    Tooltip, message, RadioChangeEvent } from 'antd';
import { ExclamationCircleFilled } from '@ant-design/icons';
import moment from 'moment';
import { showTimeForOffsetReset, formatOffsetResetTime } from '@/utils';

const Api = {} as any

const offsetResetFormat = 'YYYY-MM-DD HH:mm:ss'

interface IProps {
    visible?: boolean;
    taskId?: number | undefined;
    refresh: () => void;
    onCancel: () => void;
}

interface IStates {
    checkedValue?: RERUN_TYPE;
    offsetSource: IOffsetSource[];
}

interface IOffsetSource {
    sourceId: number;
    tableName?: string;
    timestampOffset?: number;
    type: number;
    table?: string;
}

enum RERUN_TYPE  {
    LAST = 'last',
    OFFSET = 'offset'
}

class ReRunModal extends React.Component<IProps, IStates> {
    constructor (props: IProps) {
        super(props);
        this.state = {
            checkedValue: RERUN_TYPE.LAST,
            offsetSource: []
        }
    }

    componentDidUpdate (prevProps: IProps) {
        const { taskId, visible } = this.props;
        if (taskId && taskId != prevProps.taskId && visible) {
            this.getTaskInfo(taskId)
        }
    }

    getTaskInfo = async (id: number) => {
        let res = await Api.getTask({ id });
        if (res.code === 1) {
            const sourceMap: IOffsetSource[] = res.data?.source || [];
            const offsetSource = sourceMap.map(({ sourceId, table, timestampOffset, type }) => (
                {
                    sourceId,
                    tableName: table,
                    timestampOffset: timestampOffset || moment().valueOf(),
                    type
                }
            ))
            this.setState({ offsetSource: offsetSource || [] })
        }
    }

    handleConfirmReRun = async () => {
        const { checkedValue, offsetSource } = this.state;
        const { refresh, onCancel, taskId } = this.props;
        let reqParams = {}
        let apiName = ''
        if (checkedValue === RERUN_TYPE.LAST) {
            reqParams = {
                id: taskId,
                isRestoration: 0
            }
            apiName = 'startTask';
        } else {
            reqParams = {
                taskId: taskId,
                kafkaOffsetVOS: offsetSource
            }
            apiName = 'reRunTaskByOffset'
        }
        let res = await Api[apiName](reqParams);
        if (res.code === 1) {
            message.success('任务操作成功！');
            onCancel();
            refresh();
        }
    }

    handleChangeRadioValue = (e: RadioChangeEvent) => {
        this.setState({ checkedValue: e.target.value })
    }

    changeDateTime = (value: moment.Moment | null, index: number) => {
        let { offsetSource } = this.state;
        let clone = [...offsetSource];
        clone[index].timestampOffset = value?.valueOf();
        this.setState({
            offsetSource: clone
        })
    }

    range (start: number, end: number) {
        const result = [];
        for (let i = start; i < end; i++) {
            result.push(i);
        }
        return result;
    }

    disabledDate = (current: moment.Moment) => {
        return current && current > moment().endOf('day');
    }

    disabledTime: any = (date: any) => {
        const formatType = 'YYYY-MM-DD';
        const nowMomentDate = moment(new Date());
        const nowDate = nowMomentDate.format(formatType);
        if (nowDate === date.format(formatType)) { // select today
            if (date.hours() < nowMomentDate.hours()) {
                return {
                    disabledHours: () => this.range(nowMomentDate?.hours() + 1, 24)
                }
            }
            if (date.minutes() < nowMomentDate.minutes()) {
                return {
                    disabledHours: () => this.range(nowMomentDate?.hours() + 1, 24),
                    disabledMinutes: () => this.range(nowMomentDate?.minutes() + 1, 60)
                }
            }
            return {
                disabledHours: () => this.range(nowMomentDate?.hours() + 1, 24),
                disabledMinutes: () => this.range(nowMomentDate?.minutes() + 1, 60),
                disabledSeconds: () => this.range(nowMomentDate?.seconds(), 60)
            };
        }
    }

    loopOffsetDatePicker = () => {
        const { offsetSource } = this.state;
        if (offsetSource?.length === 0) return <div className='o-modal__datepick--empty'>暂未配置Offset Time</div>;
        return offsetSource.map((item, index) => {
            const { tableName, type, timestampOffset } = item;
            return <div key={index} className='o-modal__radio--content'>
                <span title={tableName}>{`源表${index + 1}(${tableName})`}</span> ：
                {showTimeForOffsetReset(type) ? <DatePicker
                    className='o-modal__datepick--content'
                    showTime
                    allowClear={false}
                    disabledDate={this.disabledDate}
                    disabledTime={this.disabledTime}
                    onChange={(value) => {
                        this.changeDateTime(value, index)
                    }}
                    value={formatOffsetResetTime(timestampOffset)}
                    format={offsetResetFormat} /> : <span className='o-modal__datepick--text'>Kafka 版本不支持指定Time重跑</span>}<br />
            </div>
        })
    }

    render () {
        const { visible, onCancel } = this.props;
        const { checkedValue } = this.state;
        return (
            <Modal
                className='o-modal'
                title='重跑任务'
                visible={visible}
                onCancel={onCancel}
                onOk={this.handleConfirmReRun}
            >
                <Alert message="重跑，则任务将丢弃停止前的状态，重新运行，若存在启停策略，将恢复自动启停" type="warning" />
                <Radio.Group defaultValue={RERUN_TYPE.LAST} onChange={this.handleChangeRadioValue}>
                    <Radio className='o-modal__radio' value={RERUN_TYPE.LAST}>使用上次任务参数重跑</Radio>
                    <Radio className='o-modal__radio--padding' value={RERUN_TYPE.OFFSET}>指定Offset Time位置重跑</Radio>
                    <Tooltip title="仅支持Kafka 0.10版本以上的源表从指定Offset Time开始消费，确定后任务自动保存历史版本并进行重跑">
                        <ExclamationCircleFilled />
                    </Tooltip>
                </Radio.Group>
                {
                    checkedValue === RERUN_TYPE.OFFSET && (
                        <div className='o-modal__content'>
                            {this.loopOffsetDatePicker()}
                        </div>
                    )
                }
            </Modal>
        )
    }
}

export default ReRunModal;
