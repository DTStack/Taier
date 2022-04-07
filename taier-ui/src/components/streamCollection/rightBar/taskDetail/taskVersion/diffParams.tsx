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

import DiffEditor from "@/components/editor/diff";
import { TASK_TYPE_ENUM } from "@/constant";
import { Tabs } from "antd";
import { useState } from "react";

interface IProps {
    currentData: IVersionDetail,
    versionData: IVersionDetail
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
const TabPane = Tabs.TabPane;

export default function DiffParams({ currentData, versionData }: IProps) {
    const [tabKey, setTabKey] = useState('code');
    const isSQL = currentData?.taskType === 0;
    const isGuide = !currentData?.createModel

    const callback = (key: string) => {
        useState(key)
    }

    const getLanguge = (taskType: number) => {
        let language: string;
        switch (taskType) {
            case TASK_TYPE_ENUM.SYNC: {
                language = 'json';
                break;
            }
            case TASK_TYPE_ENUM.PYTHON_23: {
                language = 'python';
                break;
            }
            case TASK_TYPE_ENUM.SQL: {
                language = 'dtsql';
                break;
            }
            default: {
                language = 'dtsql';
            }
        }
        return language;
    }

    return <div className="m-taksdetail diff-params-modal" style={{ marginTop: '12px' }}>
        <Tabs onChange={callback} type="card" activeKey={tabKey}>
            <TabPane tab="代码" key="code">
                <DiffEditor
                    className="merge-text"
                    style={{ height: '500px' }}
                    original={{ value: currentData.sqlText }}
                    modified={{ value: versionData.sqlText }}
                    options={{ readOnly: true }}
                    language={getLanguge(currentData.taskType)}
                />
            </TabPane>
            {(isSQL && isGuide) && <TabPane tab="源表" key="sourceTable">
                <DiffEditor
                    className="merge-text"
                    style={{ height: '500px' }}
                    original={{ value: currentData.sourceParams }}
                    modified={{ value: versionData.sourceParams }}
                    options={{ readOnly: true }}
                    language={getLanguge(currentData.taskType)}
                />
            </TabPane>}
            {(isSQL && isGuide) && <TabPane tab="结果表" key="resultTable">
                <DiffEditor
                    className="merge-text"
                    style={{ height: '500px' }}
                    original={{ value: currentData.sinkParams }}
                    modified={{ value: versionData.sinkParams }}
                    options={{ readOnly: true }}
                    language={getLanguge(currentData.taskType)}
                />
            </TabPane>}
            {(isSQL && isGuide) && <TabPane tab="维表" key="dimensionTable">
                <DiffEditor
                    className="merge-text"
                    style={{ height: '500px' }}
                    original={{ value: currentData.sideParams }}
                    modified={{ value: versionData.sideParams }}
                    options={{ readOnly: true }}
                    language={getLanguge(currentData.taskType)}
                />
            </TabPane>}
            <TabPane tab="环境参数" key="params">
                <DiffEditor
                    language="ini"
                    className="merge-text"
                    style={{ height: '500px' }}
                    options={{ readOnly: true }}
                    sync={true}
                    modified={{ value: (versionData && versionData.taskParams) || ' ' }}
                    original={{ value: (currentData && currentData.taskParams) || ' ' }}
                />
            </TabPane>
        </Tabs>
    </div>
}