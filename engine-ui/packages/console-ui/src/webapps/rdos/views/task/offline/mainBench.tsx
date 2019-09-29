import * as React from 'react';
import { Alert, Modal, message } from 'antd';

import utils from 'utils'

import Api from '../../../api'
import {
    TASK_TYPE, LOCK_TYPE, DATA_SYNC_TYPE,
    DEAL_MODEL_TYPE, SCRIPT_TYPE, MENU_TYPE
} from '../../../comm/const'

import DataSync from './dataSync';
import DataSyncScript from './dataSync/dataSyncScript'
import NormalTaskForm from './normalTask';

import EditorContainer from './sqlEditor';
import CommonEditor from './commonEditor';
import WorkFlowEditor from './workflowEditor';

import KylinEditor from './kylinEditor/index';

const confirm = Modal.confirm;

export default class MainBench extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    unLock = () => {
        const {
            tabData, updateTaskFields, updateCatalogue,
            loadTreeNode, reloadWorkflowTabNode, tabs
        } = this.props;

        const lockInfo = tabData.readWriteLockVO;

        // 解锁
        confirm({
            title: '解锁提醒',
            content: `文件正在被${lockInfo.lastKeepLockUserName}编辑中，开始编辑时间
                ${utils.formatDateTime(lockInfo.gmtModified)}。
                强制编辑可能导致${lockInfo.lastKeepLockUserName}对该文件的修改无法保存！
            `,
            okText: '确定',
            okType: 'danger', // warning
            cancelText: '取消',
            onOk () {
                const params: any = {
                    fileId: tabData.id,
                    lockVersion: lockInfo.version
                }; // 文件ID
                if (utils.checkExist(tabData.taskType)) {
                    params.type = LOCK_TYPE.OFFLINE_TASK; // 离线任务锁
                } else if (utils.checkExist(tabData.type)) {
                    params.type = LOCK_TYPE.OFFLINE_SCRIPT; // 离线脚本锁
                }
                // 如果是工作流，解锁时需要加上子节点的ID;
                if (tabData.taskType === TASK_TYPE.WORKFLOW && tabData.sqlText) {
                    const workflowNodes = JSON.parse(tabData.sqlText);
                    const ids: any = [];
                    workflowNodes.forEach((flow: any) => {
                        if (flow.vertex && flow.data) {
                            ids.push(flow.data.id)
                        }
                    });
                    params.subFileIds = ids;
                }

                Api.unlockFile(params).then((res: any) => {
                    if (res.code === 1) {
                        const lockData = res.data;
                        if (lockData.getLock) { // 解锁成功，更改所状态
                            message.success('文件解锁成功！');
                        } else { // 解锁失败重新reload任务代码
                            Modal.error({
                                title: '解锁失败',
                                content: `文件正在被${lockData.lastKeepLockUserName}编辑中! 开始编辑时间
                                ${utils.formatDateTime(lockData.gmtModified)}.`
                            });
                        }
                        // reload task info
                        const reqParams: any = {
                            id: tabData.id
                        }
                        if (params.type === LOCK_TYPE.OFFLINE_TASK) {
                            if (tabData.taskType === TASK_TYPE.WORKFLOW) {
                                // 如果是工作流解锁成功，则需要重新刷新工作流子节点
                                loadTreeNode(tabData.id, MENU_TYPE.TASK_DEV, {
                                    taskType: TASK_TYPE.WORKFLOW,
                                    parentId: tabData.nodePid
                                });
                                // 重新加载Tab上所以相关的工作流子节点
                                reloadWorkflowTabNode(tabData.id, tabs);
                            }

                            Api.getOfflineTaskDetail(reqParams).then((res: any) => {
                                if (res.code === 1) {
                                    const taskInfo = res.data
                                    taskInfo.merged = true;
                                    const updated: any = {
                                        id: tabData.id,
                                        type: 'file',
                                        readWriteLockVO: taskInfo.readWriteLockVO
                                    }
                                    updateCatalogue(updated)
                                    updateTaskFields(taskInfo)
                                }
                            })
                        } else if (params.type === LOCK_TYPE.OFFLINE_SCRIPT) {
                            Api.getScriptById(reqParams).then((res: any) => {
                                if (res.code === 1) {
                                    const scriptInfo = res.data
                                    scriptInfo.merged = true;
                                    const updated: any = {
                                        id: tabData.id,
                                        type: 'file',
                                        readWriteLockVO: scriptInfo.readWriteLockVO
                                    }
                                    updateCatalogue(updated)
                                    updateTaskFields(scriptInfo)
                                }
                            })
                        }
                    }
                })
            },
            onCancel () { console.log('Cancel'); }
        });
    }

    render () {
        const { tabData } = this.props;
        return <div className="m-mainbench editor-container">
            {tabData && this.renderBench(tabData)}
            {tabData && this.renderLock(tabData)}
        </div>
    }

    renderLock (tabData: any) {
        const isLocked = tabData.readWriteLockVO && !tabData.readWriteLockVO.getLock
        const isSyncScript = tabData.createModel && tabData.createModel == DATA_SYNC_TYPE.SCRIPT;
        const isEditor = (tabData.taskType == TASK_TYPE.SQL) || isSyncScript || utils.checkExist(tabData && tabData.type);

        // 根据不同的类型设置不通的锁样式，编辑器-只添加按钮点击部分遮罩，界面-添加全部遮罩，脚本向导模式-不添加遮罩，由内部来添加屏蔽遮罩
        let lockClassName = 'lock-layer';
        if (isEditor) {
            lockClassName = 'lock-layer-editor';
        } else if (tabData.taskType == TASK_TYPE.SYNC) {
            lockClassName = 'lock-layer-sync';
        } else if (tabData.taskType == TASK_TYPE.WORKFLOW) {
            lockClassName = 'lock-layer-editor';
        }

        return isLocked ? (
            <div className={lockClassName}>
                <Alert
                    style={{ position: 'absolute', top: '-2px', left: '35%', zIndex: 999, height: '35px' }}
                    showIcon
                    message={<span>当前文件为只读状态！{<a onClick={this.unLock}>解锁</a>}</span>}
                    type="warning"
                />
            </div>
        ) : ''
    }

    renderBench (tabData: any) {
        const { taskCustomParams, saveTab } = this.props;
        const isWorkflowNode = tabData && tabData.flowId && tabData.flowId !== 0;

        // 任务类型
        if (utils.checkExist(tabData && tabData.taskType)) {
            switch (tabData.taskType) {
                case TASK_TYPE.CUBE_KYLIN:
                    return <KylinEditor
                        mode="kylin"
                        taskCustomParams={taskCustomParams}
                        key={tabData.id}
                        currentTab={tabData.id}
                        currentTabData={tabData} />;
                case TASK_TYPE.MR:
                case TASK_TYPE.VIRTUAL_NODE:
                case TASK_TYPE.ML:
                case TASK_TYPE.HAHDOOPMR:
                case TASK_TYPE.EXPERIMENT: {
                    return <NormalTaskForm
                        isWorkflowNode={isWorkflowNode}
                        key={tabData.id}
                        {...tabData}
                    />
                }
                case TASK_TYPE.SYNC: // 数据同步
                    if (tabData.createModel && tabData.createModel == DATA_SYNC_TYPE.SCRIPT) {
                        return <DataSyncScript
                            key={tabData.id}
                            {...tabData}
                            currentTabData={tabData}
                            taskCustomParams={taskCustomParams}
                        />
                    }
                    return <DataSync saveTab={saveTab} key={tabData.id} {...tabData} />
                case TASK_TYPE.SQL: // SQL
                case TASK_TYPE.HIVESQL:
                    return <EditorContainer
                        taskCustomParams={taskCustomParams}
                        key={tabData.id}
                        value={tabData.sqlText}
                        currentTab={tabData.id}
                        currentTabData={tabData}
                    />
                case TASK_TYPE.LIBRASQL:
                case TASK_TYPE.IMPALA_SQL:
                    return <CommonEditor
                        mode="sql"
                        singleLineMode={true}
                        toolBarOptions={{
                            enableFormat: true
                        }}
                        key={tabData.id}
                        value={tabData.sqlText}
                        currentTab={tabData.id}
                        taskCustomParams={taskCustomParams}
                        currentTabData={tabData} />;
                case TASK_TYPE.CARBONSQL:
                    return <CommonEditor
                        mode="sql"
                        taskCustomParams={taskCustomParams}
                        key={tabData.id}
                        value={tabData.sqlText}
                        currentTab={tabData.id}
                        currentTabData={tabData} />;
                case TASK_TYPE.SHELL:
                    return <CommonEditor
                        mode="shell"
                        taskCustomParams={taskCustomParams}
                        key={tabData.id}
                        value={tabData.sqlText}
                        currentTab={tabData.id}
                        currentTabData={tabData} />;
                case TASK_TYPE.DEEP_LEARNING:
                case TASK_TYPE.PYTHON:
                case TASK_TYPE.PYTHON_23:
                case TASK_TYPE.NOTEBOOK:
                    if (tabData.operateModel == DEAL_MODEL_TYPE.EDIT) {
                        return <CommonEditor
                            mode="python"
                            taskCustomParams={taskCustomParams}
                            key={tabData.id}
                            value={tabData.sqlText}
                            currentTab={tabData.id}
                            currentTabData={tabData} />;
                    } else {
                        return <NormalTaskForm key={tabData.id + '.' + tabData.version} {...tabData} />
                    }
                case TASK_TYPE.WORKFLOW: {
                    const isLocked = tabData.readWriteLockVO && !tabData.readWriteLockVO.getLock;
                    const editorKey = `${tabData.id}_${isLocked}_${tabData.version}`;
                    return <WorkFlowEditor
                        data={tabData}
                        key={editorKey}
                    />
                }
                default:
                    return <p className="txt-center" style={{ lineHeight: '60px' }}>
                        未知任务类型
                    </p>
            }
            // 脚本类型
        } else if (utils.checkExist(tabData && tabData.type)) {
            switch (tabData.type) {
                case SCRIPT_TYPE.SQL:
                case SCRIPT_TYPE.IMPALA_SQL:
                case SCRIPT_TYPE.LIBRASQL: {
                    return <EditorContainer
                        taskCustomParams={taskCustomParams}
                        key={tabData.id}
                        value={tabData.scriptText}
                        currentTab={tabData.id}
                        currentTabData={tabData}
                    />
                }
                case SCRIPT_TYPE.SHELL:
                case SCRIPT_TYPE.PYTHON2:
                case SCRIPT_TYPE.PYTHON3: {
                    let mode = 'python';
                    if (tabData.type === SCRIPT_TYPE.SHELL) {
                        mode = 'shell';
                    }
                    return <CommonEditor
                        mode={mode}
                        key={tabData.id}
                        value={tabData.scriptText}
                        currentTab={tabData.id}
                        currentTabData={tabData} />;
                }
            }
        }
    }
}
