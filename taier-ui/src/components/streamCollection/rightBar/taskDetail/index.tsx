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
import { FLINK_SQL_TYPE, FLINK_VERSION_TYPE, TASK_TYPE_ENUM } from "@/constant";
import { IEditor } from "@dtinsight/molecule/esm/model";
import { Collapse, Descriptions, message, Modal, Select, Tag } from "antd";
import { CloseOutlined, CheckOutlined, EditOutlined } from '@ant-design/icons'
import { useEffect, useMemo, useState } from "react";
import stream from "@/api/stream";
import { DateTime } from "@dtinsight/dt-utils/lib";
import { preparePage, streamTaskActions, transformOffsetUnit, transformTimeType, validTableData } from "../../taskFunc";
import './index.scss'
import LockPanel from "../lockPanel";
import TaskVersion from "./taskVersion";
import classNames from "classnames";
import { TAB_WITHOUT_DATA } from "@/pages/rightBar";
import { getFlinkVersion } from "../panelData";

const Panel = Collapse.Panel;
const Option = Select.Option;

export default function StreamTaskDetail({ current }: Pick<IEditor, 'current'>) {
    const currentPage = current?.tab?.data || {};
    const { taskType, resourceList, readWriteLockVO } = currentPage;
    const showResource = taskType != TASK_TYPE_ENUM.DATA_COLLECTION;
    const isLocked = readWriteLockVO && !readWriteLockVO.getLock;

    const [showVersionEditBtn, setShowVersionEditBtn] = useState(false);
    const [componentVersion, setComponentVersion] = useState('');
    const [visibleAlterRes, setVisibleAlterRes] = useState(false);
    const [resList, setResList] = useState([]);
    const [resources, setResources] = useState([]);
    const [flinkVersions, setFlinkVersions] = useState<string[]>([])

    const getTaskName = (type: number) => {
        switch (type) {
            case TASK_TYPE_ENUM.SQL: {
                return 'FlinkSQL'
            }
            case TASK_TYPE_ENUM.MR: {
                return 'FlinkMR'
            }
            case TASK_TYPE_ENUM.DATA_COLLECTION: {
                return '实时采集'
            }
        }
    }
    
    const getResourceList = () => {
        stream.getResList().then((res: any) => {
            setResources(res.data || [])
        })
    }

    const changeFlinkVersion = (componentVersion: string) => {
        setComponentVersion(componentVersion);
        setShowVersionEditBtn(true);
    }
    const saveVersion = () => {
        Modal.confirm({
            title: '正在切换引擎版本',
            content: <><span style={{ color: 'red' }}>切换引擎版本后将重置环境参数</span>，请确认是否继续？</>,
            onOk: async () => {
                let task = {
                    ...currentPage,
                    componentVersion: componentVersion,
                    isDirtyDataManage: false,
                    preSave: true
                }
                transformTimeType(currentPage, task)
                transformOffsetUnit(currentPage, task)

                const isFlinkSQLGuide = task.createModel == FLINK_SQL_TYPE.GUIDE || !task.createModel;
                if (task.taskType == TASK_TYPE_ENUM.SQL && isFlinkSQLGuide) {
                    const { source = [], sink = [], side = [] } = task
                    // 切换 flink 版本前先校验源表、结果表、维表
                    const error = await validTableData(currentPage, { source, sink, side })
                    if (error) { return }
                }
                // 处理实时采集部分数据格式
                task = preparePage(task);
                stream.saveTask(task).then(res => {
                    // 覆盖更新
                    if (res.code === 1) {
                        streamTaskActions.setCurrentPage(res.data)
                        message.success('版本更新成功');
                        setShowVersionEditBtn(false);
                    }
                })
            }
        })
    }

    const reloadTask = (id: any) => {
        stream.getTask({ taskId: id }).then((res: any) => {
            if (res.code === 1) {
                streamTaskActions.setCurrentPage(res.data)
            }
        })
    }

    const alterRes = () => {
        let resListData = Array.isArray(resList) ? resList : [resList]

        if (resListData.length === 0) {
            message.info('您没有选择任何资源！')
            return
        }
        if (currentPage && currentPage.id && resListData.length > 0) {
            stream.updateTaskRes({
                id: currentPage.id,
                resources: resListData
            }).then((res: any) => {
                if (res.code === 1) {
                    message.success('资源修改成功！')
                    reloadTask(currentPage.id)
                    setVisibleAlterRes(false);
                    setResList([])
                }
            })
        }
    }
    const handleChange = (val: any) => {
        setResList(val)
    }

    const editorChange = (data: any) => {
        data.notSynced = true
        streamTaskActions.updateCurrentPage(data)
    }
    const getFlinkVersions = async () => {
		const list: string[] = await getFlinkVersion();
		setFlinkVersions(list);
	}

    useEffect(() => {
        getResourceList()
        getFlinkVersions()
    }, [])

    /**
     * 当前的 tab 是否不合法，如不合法则展示 Empty
     */
     const isInValidTab = useMemo(
        () =>
            !current ||
            !current.activeTab ||
            TAB_WITHOUT_DATA.some((prefix) => current.activeTab?.toString().includes(prefix)),
        [current],
    );
    if (isInValidTab) {
		return <div className={classNames('text-center', 'mt-10px')}>无法获取任务详情</div>;
	}

    const taskRes = resourceList?.map((item: any) => {
        return <Tag key={item.id} color="blue">{item.resourceName}</Tag>
    })
    const modeFix = {
        modex: taskType === 0 ? 'multiple' : ''
    }
    const resOptions = resources && resources.map((item: any) => {
        const nameFix = {
            name: item.resourceName
        }
        return (
            <Option value={item.id} key={item.id} {...nameFix}>
                {item.resourceName}
            </Option>
        )
    })
    return <Collapse className="task-detail-panel" bordered={false} defaultActiveKey={['1', '2']}>
    <Panel key="1" header="任务属性">
        <Descriptions bordered className="task-info" column={24}>
            <Descriptions.Item label="任务名称" span={24}>{currentPage.name}</Descriptions.Item>
            <Descriptions.Item label="任务类型" span={24}>
                {getTaskName(currentPage.taskType)}
            </Descriptions.Item>
            <Descriptions.Item label="引擎版本" span={24}>
                {/* 加一层固定宽度包裹，不然按钮变化时宽度会自适应变化。。 */}
                <div style={{ width: '190px', display: 'flex', alignItems: 'center' }}>
                    {showVersionEditBtn
                        ? <>
                            <Select
                                className="o-select__height28"
                                style={{ width: '160px' }}
                                value={componentVersion}
                                onChange={changeFlinkVersion}
                            >
                                {FLINK_VERSION_TYPE.map(({ value, label }) =>
                                    <Option key={value} value={value} disabled={!flinkVersions.includes(value)}>{label}</Option>
                                )}
                            </Select>
                            <CloseOutlined
                                style={{ color: '#999', margin: '0 4px', fontSize: 16, cursor: 'pointer' }}
                                className="project__cancel__span"
                                onClick={() => {
                                    setComponentVersion(componentVersion);
                                    setShowVersionEditBtn(false)
                                }}
                            />
                            <CheckOutlined
                                style={{ color: '#16DE9A', fontSize: 16, cursor: 'pointer' }}
                                onClick={saveVersion}
                            />
                        </>
                        : <>
                            {componentVersion}
                            <EditOutlined
                                style={{ color: '#3F87FF', fontSize: 12, marginLeft: 9, cursor: 'pointer' }}
                                onClick={() => { setShowVersionEditBtn(true) }}
                            />
                        </>}
                </div>
            </Descriptions.Item>
            {showResource && <Descriptions.Item label="资源" span={24}>
                {taskRes || '-'}
            </Descriptions.Item>}
            <Descriptions.Item label="创建人员" span={24}>{currentPage.createUserName}</Descriptions.Item>
            <Descriptions.Item label="创建时间" span={24}>
                {DateTime.formatDateTime(currentPage.gmtCreate)}
            </Descriptions.Item>
            <Descriptions.Item label="最近修改时间" span={24}>
                {DateTime.formatDateTime(currentPage.gmtModified)}
            </Descriptions.Item>
            <Descriptions.Item label="描述" span={24}>
                {currentPage.taskDesc || '-'}
            </Descriptions.Item>
        </Descriptions>
        <Modal
            title="修改任务资源"
            visible={visibleAlterRes}
            onCancel={() => { setVisibleAlterRes(false) }}
            onOk={alterRes}
        >
            <Select
                style={{ width: '100%' }}
                showSearch
                value={resList}
                placeholder="请选择资源"
                optionFilterProp="name"
                onChange={handleChange}
                {...modeFix}
            >
                {resOptions}
            </Select>
        </Modal>
        <LockPanel lockTarget={currentPage} />
    </Panel>
    <Panel key="2" header={`历史提交版本`}>
        <TaskVersion
            current={current}
            changeSql={editorChange}
            updateTaskField={(data: any) => streamTaskActions.updateCurrentPage(data)}
            taskType="realTimeTask"
            isLocked={isLocked}
        />
    </Panel>
</Collapse>
}