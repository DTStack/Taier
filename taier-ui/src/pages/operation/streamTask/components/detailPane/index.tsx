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

import { useEffect, useState } from 'react';
import { Radio,Tabs } from 'antd';
import type { RadioChangeEvent } from 'antd/es/radio';

import stream from '@/api';
import SlidePane from '@/components/slidePane';
import type { IStreamJobProps } from '@/interface';
import { TaskStatus } from '@/utils/enums';
import StreamDetailGraph from './components/detailGraph';
import RunCode, { IRunCodeDataProps } from './components/runCode';
import RunLog from './components/runLog';
import CheckPoint from './components/runLog/checkPoint';
import Failover from './components/runLog/failover';
import History from './components/runLog/historyLog';
import RunMsg from './components/runMsg';
import TaskManager from './components/taskManager';
import './index.scss';

const TabPane = Tabs.TabPane;

interface IProps {
    data?: IStreamJobProps;
    visibleSlidePane: boolean;
    extButton: React.ReactNode;
    closeSlidePane: () => void;
}

enum TABS_ENUM {
    /**
     * 数据曲线
     */
    GRAPH = 'taskGraph',
    /**
     * 运行信息
     */
    MESSAGE = 'runMsg',
    /**
     * 日志
     */
    LOG = 'log',
    /**
     * 属性参数
     */
    PARAMS = 'runCode',
}

enum TABS_LOG_ENUM {
    RUN_LOG = 'runLog',
    FAILOVER = 'failover',
    TASK_MANAGER = 'taskManager',
    CHECKPOINT = 'checkpoint',
    HISTORY_LOG = 'historyLog',
}

const TABS_OPTIONS = [
    {
        label: '数据曲线',
        value: TABS_ENUM.GRAPH,
    },
    {
        label: '运行信息',
        value: TABS_ENUM.MESSAGE,
    },
    {
        label: '日志',
        value: TABS_ENUM.LOG,
    },
    {
        label: '属性参数',
        value: TABS_ENUM.PARAMS,
    },
];

export default function TaskDetailPane({ visibleSlidePane, data, extButton, closeSlidePane }: IProps) {
    const [tabKey, setTabKey] = useState<TABS_ENUM>(TABS_ENUM.GRAPH);
    const [logSubTabKey, setLogSubTabKey] = useState(TABS_LOG_ENUM.RUN_LOG);
    const [taskParams, setTaskParams] = useState<IRunCodeDataProps | undefined>(undefined);

    const getTaskParams = async () => {
        const res = await stream.getTaskSqlText({ taskId: data?.id });
        if (res?.code === 1) {
            setTaskParams(res.data || {});
        }
    };

    const onTabChange = (activeKey: string) => {
        setTabKey(activeKey as TABS_ENUM);
        if (activeKey === TABS_ENUM.PARAMS) {
            getTaskParams();
        }
    };

    const subTabChange = (e: RadioChangeEvent) => {
        setLogSubTabKey(e.target.value);
    };

    const resetTabs = () => {
        setTabKey(TABS_ENUM.GRAPH);
        setLogSubTabKey(TABS_LOG_ENUM.RUN_LOG);
        setTaskParams(undefined);
    };

    const handleCloseSlidePane = () => {
        resetTabs();
        closeSlidePane();
    };

    const renderSubContent = (key: TABS_LOG_ENUM) => {
        const { id, jobId } = data!;
        switch (key) {
            case TABS_LOG_ENUM.RUN_LOG:
                return <RunLog key={id} data={data} />;
            case TABS_LOG_ENUM.FAILOVER:
                return <Failover key={id} isShow={logSubTabKey === 'failover'} data={data} />;
            case TABS_LOG_ENUM.TASK_MANAGER:
                return <TaskManager key={id} data={data} />;
            case TABS_LOG_ENUM.CHECKPOINT:
                return <CheckPoint data={data} tabKey={logSubTabKey} />;
            case TABS_LOG_ENUM.HISTORY_LOG:
                return <History id={id!} jobId={jobId!} isShow={logSubTabKey === 'historyLog'} />;
            default:
                return null;
        }
    };

    const renderTabContent = (key: TABS_ENUM) => {
        switch (key) {
            case TABS_ENUM.GRAPH:
                return <StreamDetailGraph data={data} />;
            case TABS_ENUM.MESSAGE:
                return <RunMsg data={data} />;
            case TABS_ENUM.LOG:
                return (
                    <>
                        <Radio.Group style={{ padding: '12px 20px' }} value={logSubTabKey} onChange={subTabChange}>
                            <Radio.Button value={TABS_LOG_ENUM.RUN_LOG}>运行日志</Radio.Button>
                            <Radio.Button value={TABS_LOG_ENUM.FAILOVER}>failover</Radio.Button>
                            <Radio.Button value={TABS_LOG_ENUM.TASK_MANAGER}>Task Manager</Radio.Button>
                            {/* {taskType !== TASK_TYPE_ENUM.DATA_ACQUISITION && (
								<Radio.Button value={TABS_LOG_ENUM.CHECKPOINT}>
									checkpoint
								</Radio.Button>
							)} */}
                            <Radio.Button value={TABS_LOG_ENUM.HISTORY_LOG}>历史日志</Radio.Button>
                        </Radio.Group>
                        {renderSubContent(logSubTabKey)}
                    </>
                );
            case TABS_ENUM.PARAMS:
                return <RunCode key={data?.id} data={taskParams} />;
            default:
                return null;
        }
    };

    useEffect(() => {
        if (visibleSlidePane) {
            // while data changed, reset the tabKey
            resetTabs();
        }
    }, [data]);

    return (
        <SlidePane
            onClose={handleCloseSlidePane}
            visible={visibleSlidePane}
            style={{
                top: 33,
                right: 0,
                bottom: 22,
                width: '60%',
                position: 'fixed',
            }}
        >
            {data && visibleSlidePane && (
                <div className="c-operation__slidePane">
                    <header className="detailPane-header">
                        <span style={{ fontSize: 14, fontWeight: 500 }}>{data.name}</span>
                        <span style={{ marginLeft: '25px' }}>
                            <TaskStatus value={data.status} />
                        </span>
                        <span className="detailPane-header-extra">{extButton}</span>
                    </header>
                    <Tabs
                        className="c-operation__pane__tabs"
                        animated={false}
                        onChange={onTabChange}
                        activeKey={tabKey}
                        destroyInactiveTabPane
                    >
                        {TABS_OPTIONS.map((option) => (
                            <TabPane tab={option.label} key={option.value}>
                                {renderTabContent(option.value)}
                            </TabPane>
                        ))}
                    </Tabs>
                </div>
            )}
        </SlidePane>
    );
}
