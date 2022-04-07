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
import moment from 'moment';
import { Tabs, Radio } from 'antd'
import type { RadioChangeEvent } from 'antd/es/radio'
import SlidePane from '@/components/slidePane';
import type { IStreamTaskProps, ITaskParams } from '@/interface';
import { TaskStatus } from '@/utils/enums';
import RunLog from './components/runLog'
import Failover from './components/runLog/failover';
import CheckPoint from './components/runLog/checkPoint'
import RunCode from './components/runCode'
import History from './components/runLog/historyLog'

import { TASK_TYPE_ENUM } from '@/constant';
import TaskManager from './components/taskManager';
import RunMsg from './tabs/runMsg';
import StreamDetailGraph from '@/views/operation/realtime/pane/tabs/graph';

const Api = {} as any

const TabPane = Tabs.TabPane;

interface IProps {
    data: IStreamTaskProps | undefined;
    visibleSlidePane: boolean;
    extButton: React.ReactNode;
    closeSlidePane: () => void;
}

interface IState {
    tabKey: string;
    logSubTabKey: string;
    taskParams: Partial<ITaskParams>;
}

class TaskDetailPane extends React.Component<IProps, IState> {
    constructor (props: IProps) {
        super(props);
        this.state = {
            tabKey: 'taskGraph',
            logSubTabKey: 'runLog',
            taskParams: {}
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: IProps) {
        const { data } = this.props;
        const { data: nextData } = nextProps;
        if (data?.id != nextData?.id) {
            this.setState({ tabKey: 'taskGraph', logSubTabKey: 'runLog' });
        }
    }

    onTabChange (activeKey: string) {
        this.setState({ tabKey: activeKey })
        if (activeKey === 'runCode') this.getTaskParams()
    }

    subTabChange = (e: RadioChangeEvent) => {
        const logSubTabKey = e.target.value
        this.setState({ logSubTabKey })
    }

    getTaskParams = async () => {
        const { taskId, componentVersion } = this.props?.data || {};
        let res = await Api.getTaskParams({ taskId, componentVersion })
        if (res?.code === 1) {
            this.setState({ taskParams: res.data || {} })
        }
    }

    getTabs () {
        const { tabKey, taskParams, logSubTabKey } = this.state;
        const { data } = this.props;
        const { taskType, id, taskId } = data || {};

        const scrollStyle: React.CSSProperties = {
            position: 'absolute',
            top: '40px',
            bottom: '1px',
            paddingBottom: '1px',
            paddingTop: '16px'
        }

        const scrollStyleNoPt: React.CSSProperties = {
            position: 'absolute',
            top: '40px',
            bottom: '1px',
            overflow: 'auto',
            paddingBottom: '1px'
        }

        const runCodeView = (
            <TabPane style={scrollStyle} tab="属性参数" key="runCode">
                <RunCode isShow={tabKey == 'runCode'} data={taskParams} />
            </TabPane>
        )

        const taskGraph = (
            <TabPane style={scrollStyleNoPt} tab="数据曲线" key="taskGraph">
                {tabKey == 'taskGraph' && <StreamDetailGraph data={data} />}
            </TabPane>
        )

        const runMsg = (
            <TabPane style={scrollStyleNoPt} tab="运行信息" key="runMsg">
                <RunMsg
                    key={id}
                    isShow={tabKey == 'runMsg'}
                    data={data}
                />
            </TabPane>
        )

        const log = (
            <TabPane style={{ ...scrollStyle, paddingTop: 0 }} tab="日志" key="log">
                <Radio.Group style={{ padding: '12px 20px' }} value={logSubTabKey} onChange={this.subTabChange}>
                    <Radio.Button value="runLog">运行日志</Radio.Button>
                    <Radio.Button value="failover">failover</Radio.Button>
                    <Radio.Button value="taskManager">Task Manager</Radio.Button>
                    {taskType !== TASK_TYPE_ENUM.DATA_COLLECTION && <Radio.Button value="checkpoint">checkpoint</Radio.Button>}
                    <Radio.Button value="historyLog">历史日志</Radio.Button>
                </Radio.Group>
                {/* RunLog 里有定时器，tab 时不展示时不会销毁，手动销毁一下 */}
                {logSubTabKey === 'runLog' && tabKey === 'log' && <RunLog key={id} data={data} isShow={logSubTabKey === 'runLog'} />}
                {logSubTabKey === 'failover' && <Failover key={id} isShow={logSubTabKey === 'failover'} data={data} />}
                {logSubTabKey === 'taskManager' && <TaskManager key={id} isShow={logSubTabKey === 'taskManager'} data={data} />}
                {logSubTabKey === 'checkpoint' && <CheckPoint data={data} tabKey={logSubTabKey} />}
                {logSubTabKey === 'historyLog' && <History id={id!} jobId={taskId!} isShow={logSubTabKey === 'historyLog'} />}
            </TabPane>
        )

        let tabs: React.ReactNode[] = [];
        tabs.unshift(taskGraph, runMsg, log);
        tabs.push(runCodeView)
        return tabs.filter(Boolean);
    }

    formatTime = (timeStamp: number): string | moment.Moment => {
        if (!timeStamp) return '--';
        return moment(timeStamp).format('YYYY-MM-DD HH:mm:ss')
    }

    render () {
        const {
            visibleSlidePane, data, extButton,
            closeSlidePane
        } = this.props;
        const { tabKey } = this.state;
        const { name, status } = data || {}

        const extButtonStyle: React.CSSProperties = {
            position: 'absolute',
            right: '16px',
            top: '11px'
        }
        
        return (
            <SlidePane
                onClose={closeSlidePane}
                visible={visibleSlidePane}
                className="dt-slide-pane"
				style={{ top: '33px' }}
            >
                <div className="c-operation__slidePane">
                    <header className="detailPane-header">
                        <span style={{ fontSize: 14, fontWeight: 500, color: 'rgba(51,51,51,1)' }}>{name}</span>
                        <span style={{ marginLeft: '25px' }}><TaskStatus value={status!} /></span>
                        <span style={extButtonStyle}>{extButton}</span>
                    </header>
                    <Tabs
                        className="c-operation__pane__tabs"
                        style={{ position: 'relative' }}
                        animated={false}
                        onChange={this.onTabChange.bind(this)}
                        activeKey={tabKey}
                    >
                        {this.getTabs()}
                    </Tabs>
                </div>
            </SlidePane>
        )
    }
}

export default TaskDetailPane;
