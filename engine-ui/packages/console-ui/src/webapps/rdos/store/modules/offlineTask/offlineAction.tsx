import * as React from 'react';
import { message, Modal, Tag } from 'antd'
import { hashHistory } from 'react-router'
import { uniqBy, cloneDeep, isArray, get, assign } from 'lodash'

import utils from 'utils';
import { offlineWorkbenchDB as idb } from '../../../../../database';

import ajax from '../../../api'
import { MENU_TYPE, DATA_SYNC_MODE, ENGINE_SOURCE_TYPE } from '../../../comm/const'

import {
    stopSql,
    setSelectionContent
} from '../../../store/modules/editor/editorAction';

import { matchTaskParams } from '../../../comm';

import {
    modalAction,
    sourceMapAction,
    targetMapAction,
    keyMapAction,
    workbenchAction,
    taskTreeAction,
    resTreeAction,
    fnTreeAction,
    sparkFnTreeAction,
    libraFnTreeAction,
    libraSysFnTreeActon,
    sysFnTreeActon,
    scriptTreeAction,
    tableTreeAction,
    workflowAction
} from './actionType';

const confirm = Modal.confirm;

// keyMap模块
export const keyMapActions = (dispatch: any, ownProps: any) => {
    return {
        addLinkedKeys: (params: any) => {
            dispatch({
                type: keyMapAction.ADD_LINKED_KEYS,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        delLinkedKeys: (params: any) => {
            dispatch({
                type: keyMapAction.DEL_LINKED_KEYS,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        handleTargetMapChange (srcmap: any) {
            dispatch({
                type: targetMapAction.DATA_TARGETMAP_CHANGE,
                payload: srcmap
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        setRowMap: (params: any) => {
            dispatch({
                type: keyMapAction.SET_ROW_MAP,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        setNameMap: (params: any) => {
            dispatch({
                type: keyMapAction.SET_NAME_MAP,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        resetLinkedKeys: () => {
            dispatch({
                type: keyMapAction.RESET_LINKED_KEYS
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        addSourceKeyRow (params: any) {
            dispatch({
                type: sourceMapAction.ADD_SOURCE_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        addBatchSourceKeyRow (params: any) {
            dispatch({
                type: sourceMapAction.ADD_BATCH_SOURCE_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        replaceBatchSourceKeyRow (params: any) {
            dispatch({
                type: sourceMapAction.REPLACE_BATCH_SOURCE_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },

        /**
         * 拷贝目标字段到源表
         */
        copyTargetRowsToSource (params: any) {
            dispatch({
                type: sourceMapAction.COPY_TARGET_ROWS_TO_SOURCE,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        editSourceKeyRow (params: any) {
            dispatch({
                type: sourceMapAction.EDIT_SOURCE_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        removeSourceKeyRow (source: any, index: any) {
            dispatch({
                type: sourceMapAction.REMOVE_SOURCE_KEYROW,
                payload: index
            });
            dispatch({
                type: keyMapAction.REMOVE_KEYMAP,
                payload: { source }
            });
        },
        addTargetKeyRow (params: any) {
            dispatch({
                type: targetMapAction.ADD_TARGET_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        editKeyMapSource (params: any) {
            dispatch({
                type: keyMapAction.EDIT_KEYMAP_SOURCE,
                payload: params
            });
        },
        editKeyMapTarget (params: any) {
            dispatch({
                type: keyMapAction.EDIT_KEYMAP_TARGET,
                payload: params
            });
        },
        editTargetKeyRow (params: any) {
            dispatch({
                type: targetMapAction.EDIT_TARGET_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        addBatchTargetKeyRow (params: any) {
            dispatch({
                type: targetMapAction.ADD_BATCH_TARGET_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        replaceBatchTargetKeyRow (params: any) {
            dispatch({
                type: targetMapAction.REPLACE_BATCH_TARGET_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        removeTargetKeyRow (target: any, index: any) {
            dispatch({
                type: targetMapAction.REMOVE_TARGET_KEYROW,
                payload: index
            });
            dispatch({
                type: keyMapAction.REMOVE_KEYMAP,
                payload: { target }
            });
        },

        removeKeyMap ({ source, target }: any) {
            dispatch({
                type: keyMapAction.REMOVE_KEYMAP,
                payload: { source, target }
            });
        }
    }
};

// workbenchActions
export const workbenchActions = (dispatch?: any) => {
    const closeAll = (tabs: any) => {
        for (let i in tabs) {
            dispatch(stopSql(tabs[i].id, null, true))
        }
        dispatch({
            type: workbenchAction.CLOSE_ALL_TABS
        })
    };

    const closeOthers = (id: any, tabs: any) => {
        for (let i in tabs) {
            if (tabs[i].id == id) {
                continue;
            }
            dispatch(stopSql(tabs[i].id, null, true))
        }
        dispatch({
            type: workbenchAction.CLOSE_OTHER_TABS,
            payload: id
        })
    };

    const reloadTaskTab = (taskId: any, isScript?: any) => {
        let method: any = isScript ? 'getScriptById' : 'getOfflineTaskDetail';
        // 更新tabs数据
        (ajax as any)[method]({
            id: taskId
        }).then((res: any) => {
            if (res.code === 1) {
                dispatch({
                    type: workbenchAction.MAKE_TAB_CLEAN
                });
                dispatch({
                    type: workbenchAction.UPDATE_TASK_TAB,
                    payload: res.data
                });
            }
        });
    };

    /**
     * 确定保存，提交Modal
     */
    const toggleConfirmModal = (data: any) => {
        dispatch({
            type: modalAction.TOGGLE_SAVE_MODAL,
            payload: data
        });
    };
    const togglePublishModal = (data: any) => {
        dispatch({
            type: modalAction.TOGGLE_PUBLISH_MODAL,
            payload: data
        });
    };
    const isSaveFInish = (data: any) => {
        dispatch({
            type: modalAction.IS_SAVE_FINISH,
            payload: data
        });
    };

    /**
     * 在 Modal 框编辑任务
     * @param {*} task 任务对象
     */
    const onEditTaskByModal = (task: any) => {
        ajax.getOfflineTaskByID({
            id: task.id,
            lockVersion: task.readWriteLockVO.version
        }).then((res: any) => {
            if (res.code === 1) {
                const data = res.data;
                dispatch({
                    type: modalAction.SET_MODAL_DEFAULT,
                    payload: data
                })
                dispatch({
                    type: modalAction.TOGGLE_CREATE_TASK,
                    payload: data
                });
            }
        })
    }

    /**
     * 从IndexDB读取 workbench 缓存数据，
     * 并初始化
     */
    const initWorkbenchCacheData = async (projectId: string | number) => {
        if (projectId && projectId > 0) {
            let pid = projectId && projectId !== 0 ? projectId : '';

            const db = await idb.open();
            if (db) {
                const result = await idb.get(`${pid ? pid + '_' : ''}offline_workbench`);
                dispatch({
                    type: workbenchAction.INIT_WORKBENCH,
                    payload: result
                });
            }
        }
    }

    return {
        dispatch,

        /**
         * 重新加载任务Tab中的数据
         */
        reloadTaskTab,
        /**
         * 在 Modal 框编辑任务
         */
        onEditTaskByModal,
        /**
         * 保存提交Modal
        */
        toggleConfirmModal,
        togglePublishModal,
        isSaveFInish,
        initWorkbenchCacheData,
        /**
         * 更新目录
         */
        updateCatalogue: (catalogue: any) => {
            dispatch({
                type: taskTreeAction.EDIT_FOLDER_CHILD_FIELDS,
                payload: catalogue
            });
        },

        convertDataSyncToScriptMode: async (task: any) => {
            const reqParams: any = {
                id: task.id,
                syncModel: task.syncModel,
                lockVersion: task.lockVersion,
                version: task.version,
                preSave: task.preSave,
                readWriteLockVO: task.readWriteLockVO
            }
            const res = await ajax.convertDataSyncToScriptMode(reqParams);
            if (res.code === 1) {
                message.success('转换成功！');
                reloadTaskTab(task.id);
            }
        },

        /**
         * 更新Tab数据
         */
        updateTabData: (data: any) => {
            dispatch({
                type: workbenchAction.UPDATE_TASK_TAB,
                payload: data
            });
        },

        /**
         * 发布任务
         * @param {*} res
        */
        publishTask (res: any) {
            dispatch({
                type: workbenchAction.CHANGE_TASK_SUBMITSTATUS,
                payload: (res.data && res.data.submitStatus) || 1
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_CLEAN
            })
        },

        /**
         * 更新当前任务的字段
         * @param {*} taskFields
        */
        updateTaskField (taskFields: any) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: taskFields
            });
        },

        /**
         * TODO 可以先组装需要匹配的字符串，再用 matchTaskParams 统一处理
         * 集中处理Data同步中的变量,例如${system.date}
         * @param {Object} dataSync
         */
        updateDataSyncVariables (sourceMap: any, targetMap: any, taskCustomParams: any) {
            let taskVariables: any = [];

            // SourceMapupdateDataSyncVariables
            if (sourceMap) {
                if (sourceMap.type && sourceMap.type.where) {
                    const vbs = matchTaskParams(taskCustomParams, sourceMap.type.where)
                    taskVariables = taskVariables.concat(vbs);
                }

                // 分区，获取任务自定义参数
                if (sourceMap.type && sourceMap.type.partition) {
                    const vbs = matchTaskParams(taskCustomParams, sourceMap.type.partition)
                    taskVariables = taskVariables.concat(vbs);
                }

                // 匹配原表字段中的系统变量
                if (sourceMap.column && sourceMap.column.length > 0) {
                    let str = '';
                    for (let i = 0; i < sourceMap.column.length; i++) {
                        str += `${sourceMap.column[i].key || sourceMap.column[i].index} ${sourceMap.column[i].value || ''}`;
                    }
                    const vbs = matchTaskParams(taskCustomParams, str);
                    taskVariables = taskVariables.concat(vbs);
                }

                // 处理路径中的变量
                let path = get(sourceMap, 'type.path')
                if (path) {
                    if (isArray(path)) {
                        path = path.map((o: any) => `${o}`).join(',')
                    }
                    const vbs = matchTaskParams(taskCustomParams, path)
                    taskVariables = taskVariables.concat(vbs);
                }
            }

            // TagetMap
            // where, 获取任务自定义参数
            if (targetMap && targetMap.type) {
                const sqlText = `${targetMap.type.preSql} ${targetMap.type.postSql}`
                if (sqlText) {
                    const vbs = matchTaskParams(taskCustomParams, sqlText)
                    taskVariables = taskVariables.concat(vbs);
                }

                if (targetMap.type.partition) {
                    const vbs = matchTaskParams(taskCustomParams, targetMap.type.partition)
                    taskVariables = taskVariables.concat(vbs);
                }

                if (targetMap.type.fileName) {
                    const vbs = matchTaskParams(taskCustomParams, targetMap.type.fileName)
                    taskVariables = taskVariables.concat(vbs);
                }

                // 处理路径中的变量
                let path = get(targetMap, 'type.path')
                if (path) {
                    if (isArray(path)) {
                        path = path.map((o: any) => `${o}`).join(',')
                    }
                    const vbs = matchTaskParams(taskCustomParams, path)
                    taskVariables = taskVariables.concat(vbs);
                }
            }
            // 去重复参数
            const uniqArr = uniqBy(taskVariables, (o: any) => o.paramName);
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: {
                    taskVariables: uniqArr
                }
            });
        },

        openTaskInDev: (id: any) => {
            ajax.getOfflineTaskDetail({
                id: id
            }).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: workbenchAction.LOAD_TASK_DETAIL,
                        payload: res.data
                    });
                    dispatch({
                        type: workbenchAction.OPEN_TASK_TAB,
                        payload: id
                    });
                    hashHistory.push('/offline/task');
                }
            });
        },

        createWorkflowTask (data: any) {
            return ajax.addOfflineTask(data)
                .then((res: any) => {
                    if (res.code === 1) {
                        const newTask = res.data;
                        dispatch({
                            type: workflowAction.UPDATE,
                            payload: {
                                node: newTask,
                                status: 'created'
                            }
                        })
                        return true;
                    }
                });
        },

        // 确定克隆
        confirmClone (data: any) {
            return ajax.cloneTask(data)
                .then((res: any) => {
                    if (res.code == 1) {
                        dispatch({
                            type: workflowAction.CLONE
                        })
                        return true;
                    }
                })
        },

        saveTask (task?: any, noMsg?: any) {
            // 删除不必要的字段
            delete task.taskVersions;

            task.preSave = true;
            task.submitStatus = 0;

            // 接口要求上游任务字段名修改为dependencyTasks
            if (task.taskVOS) {
                task.dependencyTasks = task.taskVOS.map((o: any) => o);
                task.taskVOS = null;
            }

            const succCallback = (res: any) => {
                const updateTabData = (res: any) => {
                    const resData = res.data;
                    const data: any = {
                        id: task.id,
                        name: resData.name,
                        version: resData.version,
                        readWriteLockVO: resData.readWriteLockVO
                    }

                    dispatch({
                        type: workbenchAction.UPDATE_TASK_TAB,
                        payload: data
                    });
                }

                if (res.code === 1) {
                    const fileData = res.data;
                    const lockInfo = fileData.readWriteLockVO;
                    const lockStatus = lockInfo.result; // 1-正常，2-被锁定，3-需同步

                    if (lockStatus === 0) {
                        updateTabData(res);
                        if (!noMsg) message.success('保存成功！');
                    // 如果是锁定状态，点击确定按钮，强制更新，否则，取消保存
                    } else if (lockStatus === 1) { // 2-被锁定
                        confirm({
                            title: '锁定提醒', // 锁定提示
                            content: <span>
                                文件正在被{lockInfo.lastKeepLockUserName}编辑中，开始编辑时间为
                                {utils.formatDateTime(lockInfo.gmtModified)}。
                                强制保存可能导致{lockInfo.lastKeepLockUserName}对文件的修改无法正常保存！
                            </span>,
                            okText: '确定保存',
                            okType: 'danger',
                            cancelText: '取消',
                            onOk () {
                                ajax.forceUpdateOfflineTask(task).then(updateTabData)
                            }
                        });
                        // 如果同步状态，则提示会覆盖代码，
                        // 点击确认，重新拉取代码并覆盖当前代码，取消则退出
                    } else if (lockStatus === 2) { // 2-需同步
                        confirm({
                            title: '保存警告',
                            content: <span>
                                文件已经被{lockInfo.lastKeepLockUserName}编辑过，编辑时间为
                                {utils.formatDateTime(lockInfo.gmtModified)}。
                                点击确认按钮会<Tag color="orange">覆盖</Tag>
                                您本地的代码，请您提前做好备份！
                            </span>,
                            okText: '确定覆盖',
                            okType: 'danger',
                            cancelText: '取消',
                            onOk () {
                                const reqParams: any = {
                                    id: task.id,
                                    lockVersion: lockInfo.version
                                }
                                // 更新version, getLock信息
                                ajax.getOfflineTaskDetail(reqParams).then(updateTabData);
                            }
                        });
                    }
                }
                return res;
            }

            return ajax.saveOfflineJobData(task).then(succCallback);
        },

        /**
         * 保存Tab数据
         * @param {} params
         * @param {*} isSave
         * @param {*} type
         * @param {*} isButtonSubmit // 判断保存操作
         * 是否是从提交确认保存 还是直接保存
         */
        saveTab (params: any, isSave: any, type: any, isButtonSubmit: any) {
            const updateTaskInfo = function (data: any) {
                dispatch({
                    type: workbenchAction.SET_TASK_FIELDS_VALUE,
                    payload: data
                });
                dispatch({
                    type: workbenchAction.MAKE_TAB_CLEAN
                });
            }

            const succCallback = (res: any) => {
                if (res.code === 1) {
                    const fileData = res.data;
                    const lockInfo = fileData.readWriteLockVO;
                    const lockStatus = lockInfo.result; // 1-正常，2-被锁定，3-需同步
                    if (lockStatus === 0) {
                        message.success(isSave ? '保存成功！' : '发布成功！');
                        setTimeout(() => {
                            if (isButtonSubmit) {
                                isSaveFInish(true);
                                toggleConfirmModal(false);
                                togglePublishModal(true);
                            } else {
                                isSaveFInish(true);
                            }
                        }, 500)
                        reloadTaskTab(fileData.id, typeof params.type !== 'undefined');
                        // 如果是锁定状态，点击确定按钮，强制更新，否则，取消保存
                    } else if (lockStatus === 1) { // 2-被锁定
                        confirm({
                            title: '锁定提醒', // 锁定提示
                            content: <span>
                                文件正在被{lockInfo.lastKeepLockUserName}编辑中，开始编辑时间为
                                {utils.formatDateTime(lockInfo.gmtModified)}。
                                强制保存可能导致{lockInfo.lastKeepLockUserName}对文件的修改无法正常保存！
                            </span>,
                            okText: '确定保存',
                            okType: 'danger',
                            cancelText: '取消',
                            onOk () {
                                const succCall = (res: any) => {
                                    if (res.code === 1) {
                                        message.success('保存成功！')
                                        updateTaskInfo({
                                            version: res.data.version,
                                            readWriteLockVO: res.data.readWriteLockVO
                                        })
                                    }
                                }
                                if (type === 'task') {
                                    ajax.forceUpdateOfflineTask(params).then(succCall)
                                } else if (type === 'script') {
                                    ajax.forceUpdateOfflineScript(params).then(succCall)
                                }
                            }
                        });
                        // 如果同步状态，则提示会覆盖代码，
                        // 点击确认，重新拉取代码并覆盖当前代码，取消则退出
                    } else if (lockStatus === 2) { // 2-需同步
                        confirm({
                            title: '保存警告',
                            content: <span>
                                文件已经被{lockInfo.lastKeepLockUserName}编辑过，编辑时间为
                                {utils.formatDateTime(lockInfo.gmtModified)}。
                                点击确认按钮会<Tag color="orange">覆盖</Tag>
                                您本地的代码，请您提前做好备份！
                            </span>,
                            okText: '确定覆盖',
                            okType: 'danger',
                            cancelText: '取消',
                            onOk () {
                                const reqParams: any = {
                                    id: params.id,
                                    lockVersion: lockInfo.version
                                }
                                if (type === 'task') {
                                    // 更新version, getLock信息
                                    ajax.getOfflineTaskDetail(reqParams).then((res: any) => {
                                        if (res.code === 1) {
                                            const taskInfo = res.data
                                            taskInfo.merged = true;
                                            updateTaskInfo(taskInfo)
                                        }
                                    })
                                } else if (type === 'script') {
                                    ajax.getScriptById(reqParams).then((res: any) => {
                                        if (res.code === 1) {
                                            const scriptInfo = res.data
                                            scriptInfo.merged = true;
                                            updateTaskInfo(scriptInfo)
                                        }
                                    })
                                }
                            }
                        });
                    }
                    return res;
                }
            }

            params.lockVersion = params.readWriteLockVO.version;
            if (type === 'task') {
                return ajax.saveOfflineJobData(params).then(succCallback);
            } else if (type === 'script') {
                return ajax.saveScript(params).then(succCallback);
            }
        },

        openTab: function (data: any) {
            const { id, tabs, currentTab, treeType } = data
            const isExist = tabs && tabs.find((tab: any) => tab.id === id);
            if (!isExist) {
                const succCallBack = (res: any) => {
                    if (res.code === 1) {
                        dispatch({
                            type: workbenchAction.LOAD_TASK_DETAIL,
                            payload: res.data
                        });
                    }
                }
                if (treeType && treeType === MENU_TYPE.SCRIPT) { // 脚本类型
                    ajax.getScriptById({
                        id: id
                    }).then(succCallBack);
                } else { // 默认任务类型
                    ajax.getOfflineTaskDetail({
                        id: id
                    }).then(succCallBack);
                }
            } else {
                id !== currentTab && dispatch({
                    type: workbenchAction.OPEN_TASK_TAB,
                    payload: id
                });
            }
            dispatch(setSelectionContent(''));
        },

        closeTab: (tabId: any, tabs: any) => {
            const doClose = (id: any) => {
                dispatch(stopSql(id, null, true))
                dispatch({
                    type: workbenchAction.CLOSE_TASK_TAB,
                    payload: id
                })
            }

            let dirty = tabs.filter((tab: any) => {
                return tab.id == tabId
            })[0].notSynced;

            if (!dirty) {
                doClose(+tabId);
            } else {
                confirm({
                    title: '修改尚未同步到服务器，是否强制关闭 ?',
                    content: '强制关闭将丢弃当前修改数据',
                    onOk () {
                        doClose(+tabId);
                    },
                    onCancel () { }
                });
            }
        },

        closeAllorOthers: (action: any, tabs: any, currentTab: any) => {
            if (action === 'ALL') {
                let allClean = true;

                for (let tab of tabs) {
                    console.log('ALL notSynced:', tab.notSynced)
                    if (tab.notSynced) {
                        allClean = false;
                        break;
                    }
                }

                if (allClean) {
                    closeAll(tabs);
                } else {
                    confirm({
                        title: '部分任务修改尚未同步到服务器，是否强制关闭 ?',
                        content: '强制关闭将丢弃所有修改数据',
                        onOk () {
                            closeAll(tabs);
                        },
                        onCancel () { }
                    });
                }
            } else {
                let allClean = true;
                for (let tab of tabs) {
                    console.log('notSynced:', tab.notSynced)
                    if (tab.notSynced && tab.id !== currentTab) {
                        allClean = false;
                        break;
                    }
                }

                if (allClean) {
                    closeOthers(currentTab, tabs);
                } else {
                    confirm({
                        title: '部分任务修改尚未同步到服务器，是否强制关闭 ?',
                        content: '强制关闭将丢弃这些修改数据',
                        onOk () {
                            closeOthers(currentTab, tabs);
                        },
                        onCancel () { }
                    });
                }
            }
        },

        /**
         * 定位文件位置
         */
        locateFilePos (data: any, type: any) {
            if (type === MENU_TYPE.TASK || type === MENU_TYPE.TASK_DEV) {
                dispatch({
                    type: taskTreeAction.MERGE_FOLDER_CONTENT,
                    payload: data
                });
            } else if (MENU_TYPE.SCRIPT) {
                dispatch({
                    type: scriptTreeAction.MERGE_FOLDER_CONTENT,
                    payload: data
                });
            }
        },
        loadTableListNodeByName: (nodePid: any, option = {}) => {
            ajax.getTableListByName({
                ...option
            })
                .then(
                    (res: any) => {
                        if (res.code == 1) {
                            let { data } = res;
                            data.children && dispatch({
                                type: tableTreeAction.LOAD_FOLDER_CONTENT,
                                payload: data
                            });
                        }
                    }
                )
        },
        /**
         * @param isFunc // 函数管理，默认请求第一层数据
         */
        loadTreeNode: async (nodePid?: any, type?: any, option = {}, isFunc?: any) => {
            const res = await ajax.getOfflineCatalogue({
                isGetFile: !!1,
                nodePid,
                catalogueType: type,
                ...option
            });
            if (res.code === 1) {
                let { data } = res;
                let action: any;

                switch (type) {
                    case MENU_TYPE.TASK:
                    case MENU_TYPE.TASK_DEV:
                        action = taskTreeAction;
                        break;
                    case MENU_TYPE.RESOURCE:
                        action = resTreeAction;
                        break;
                    case MENU_TYPE.SPARKFUNC:
                        action = sparkFnTreeAction;
                        break;
                    case MENU_TYPE.LIBRAFUNC:
                        action = libraFnTreeAction;
                        break;
                    case MENU_TYPE.LIBRASYSFUN:
                        action = libraSysFnTreeActon;
                        break;
                    case MENU_TYPE.FUNCTION:
                    case MENU_TYPE.COSTOMFUC:
                        action = fnTreeAction;
                        break;
                    case MENU_TYPE.SYSFUC:
                        action = sysFnTreeActon;
                        break;
                    case MENU_TYPE.SCRIPT:
                        action = scriptTreeAction;
                        break;
                    case MENU_TYPE.TABLE:
                        action = tableTreeAction
                        break;
                    default:
                        action = taskTreeAction;
                }

                data.children && dispatch({
                    type: action.LOAD_FOLDER_CONTENT,
                    payload: data
                });
                if (isFunc) {
                    const sparkCusFunc = data.children && data.children.find((item: any) => item.catalogueType == MENU_TYPE.COSTOMFUC && item.engineType == ENGINE_SOURCE_TYPE.HADOOP);
                    const sparkSysFunc = data.children && data.children.find((item: any) => item.catalogueType == MENU_TYPE.SYSFUC && item.engineType == ENGINE_SOURCE_TYPE.HADOOP);
                    const libraSysFunc = data.children && data.children.find((item: any) => item.catalogueType == MENU_TYPE.SYSFUC && item.engineType == ENGINE_SOURCE_TYPE.LIBRA);
                    dispatch({
                        type: fnTreeAction.RESET_FUC_TREE, // spark自定义函数
                        payload: sparkCusFunc
                    })
                    dispatch({
                        type: sysFnTreeActon.RESET_SYSFUC_TREE, // spark系统函数
                        payload: sparkSysFunc
                    })
                    dispatch({
                        type: libraSysFnTreeActon.RESET_SYSFUC_TREE,
                        payload: libraSysFunc // libra系统函数
                    })
                }
            }
        },
        delOfflineTask (params?: any, nodePid?: any, type?: any) {
            return ajax.delOfflineTask(params)
                .then((res: any) => {
                    if (res.code == 1) {
                        message.success('删除成功');
                        dispatch({
                            type: taskTreeAction.DEL_OFFLINE_TASK,
                            payload: {
                                id: res.data,
                                parentId: nodePid
                            }
                        });
                        dispatch({
                            type: workbenchAction.CLOSE_TASK_TAB,
                            payload: res.data
                        });
                    }
                    return res;
                });
        },

        delOfflineScript (params?: any, nodePid?: any, type?: any) {
            ajax.deleteScript(params)
                .then((res: any) => {
                    if (res.code == 1) {
                        message.success('删除成功');
                        dispatch({
                            type: scriptTreeAction.DEL_SCRIPT,
                            payload: {
                                id: params.scriptId,
                                parentId: nodePid
                            }
                        });
                        dispatch({
                            type: workbenchAction.CLOSE_TASK_TAB,
                            payload: params.scriptId
                        });
                    }
                });
        },

        delOfflineFolder (params: any, nodePid: any, cateType: any) {
            ajax.delOfflineFolder(params)
                .then((res: any) => {
                    if (res.code === 1) {
                        let action: any;

                        switch (cateType) {
                            case MENU_TYPE.TASK:
                            case MENU_TYPE.TASK_DEV:
                                action = taskTreeAction;
                                break;
                            case MENU_TYPE.RESOURCE:
                                action = resTreeAction;
                                break;
                            case MENU_TYPE.FUNCTION:
                            case MENU_TYPE.COSTOMFUC:
                                action = fnTreeAction;
                                break;
                            case MENU_TYPE.SCRIPT:
                                action = scriptTreeAction;
                                break;
                            default:
                                action = taskTreeAction;
                        }

                        dispatch({
                            type: action.DEL_OFFLINE_FOLDER,
                            payload: {
                                id: params.id,
                                parentId: nodePid
                            }
                        })
                    }
                })
        },

        loadTaskParams () {
            ajax.getCustomParams()
                .then((res: any) => {
                    if (res.code === 1) {
                        dispatch({
                            type: workbenchAction.LOAD_TASK_CUSTOM_PARAMS,
                            payload: res.data
                        })
                    }
                })
        },
        saveEngineType (data: any) { // 保存节点engineType
            dispatch({
                type: modalAction.SET_ENGINE_TYPE,
                payload: data
            })
        },
        setModalKey (data: any) {
            dispatch({
                type: modalAction.SET_MODAL_KEY,
                payload: data
            })
        },
        setModalDefault (data: any) {
            dispatch({
                type: modalAction.SET_MODAL_DEFAULT,
                payload: data
            })
        },

        toggleCreateFolder: function (type: any) {
            dispatch({
                type: modalAction.TOGGLE_CREATE_FOLDER,
                payload: type
            });
        },

        toggleCreateTask: function (data?: any) {
            dispatch({
                type: modalAction.TOGGLE_CREATE_TASK,
                payload: data
            });
        },
        // 克隆任务
        toggleCloneTask: function (data?: any) {
            dispatch({
                type: modalAction.TOGGLE_CLONE_TASK,
                payload: data
            });
        },
        // 克隆至工作流
        toggleCloneToWorkflow: function (data?: any) {
            dispatch({
                type: modalAction.TOGGLE_CLONE_TO_WORKFLOW,
                payload: data
            });
        },
        // 获取工作流列表
        getWorkFlowList (params: any) {
            ajax.getWorkflowList(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: modalAction.GET_WORKFLOW_LIST,
                        payload: res.data
                    });
                }
            })
        },
        toggleCreateScript: function () {
            dispatch({
                type: modalAction.TOGGLE_CREATE_SCRIPT
            });
        },

        toggleCreateFn: function () {
            dispatch({
                type: modalAction.TOGGLE_CREATE_FN
            });
        },

        toggleMoveFn: function (params: any) {
            dispatch({
                type: modalAction.TOGGLE_MOVE_FN,
                payload: params
            });
        },

        toggleUpload: function () {
            dispatch({
                type: modalAction.TOGGLE_UPLOAD
            });
        },

        toggleCoverUpload: function () {
            dispatch({
                type: modalAction.TOGGLE_UPLOAD,
                payload: {
                    isCoverUpload: true
                }
            });
        },

        delOfflineRes (params: any, nodePid: any) {
            ajax.delOfflineRes(params)
                .then((res: any) => {
                    if (res.code === 1) {
                        dispatch({
                            type: resTreeAction.DEL_OFFLINE_RES,
                            payload: {
                                id: params.resourceId,
                                parentId: nodePid
                            }
                        })
                    }
                })
        },

        delOfflineFn (params: any, nodePid: any) {
            ajax.delOfflineFn(params)
                .then((res: any) => {
                    if (res.code === 1) {
                        dispatch({
                            type: fnTreeAction.DEL_OFFLINE_FN,
                            payload: {
                                id: params.functionId,
                                parentId: nodePid
                            }
                        })
                    }
                })
        },

        showFnViewModal (id: any) {
            dispatch({
                type: modalAction.SHOW_FNVIEW_MODAL,
                payload: id
            })
        },

        showResViewModal (id: any) {
            dispatch({
                type: modalAction.SHOW_RESVIEW_MODAL,
                payload: id
            });
        },

        /**
         *  The below is workflow actions
         */
        updateWorkflow (data: any) {
            dispatch({
                type: workflowAction.UPDATE,
                payload: data
            })
        },

        resetWorkflow () {
            dispatch({
                type: workflowAction.RESET
            })
        },

        reloadWorkflowTabNode (flowId: any, tabs: any) {
            if (tabs && tabs.length > 0) {
                for (let i = 0; i < tabs.length; i++) {
                    const tab = tabs[i];
                    if (tab.flowId === flowId) {
                        reloadTaskTab(tab.id);
                    }
                }
            }
        }
    }
}

/**
 * 获取 数据同步请求对象
 * @param {*} dataSyncStore redux tore 对象
 */
export const getDataSyncReqParams = (dataSyncStore: any) => {
    // deepClone避免直接mutate store
    let clone = cloneDeep(dataSyncStore);

    const { keymap, sourceMap, targetMap } = clone;
    let { source = [], target = [] } = keymap;
    let serverSource: any = []; let serverTarget: any = [];

    /**
     * 获取source或者target的key,因为RDB和非RDB存储结构不一样，所以要区分
     */
    function getKey (item: any) {
        if (typeof item == 'string') {
            return item
        } else {
            return item.key;
        }
    }
    /**
     * 获取targetMap的顺序
     */
    const { column: targetColumn = [] } = targetMap;
    let indexMap: any = {};// 顺序记录表
    let tmpTarget: any = [];// 含有映射关系的target数组
    for (let i = 0; i < target.length; i++) {
        const targetItem = target[i];
        const sourceItem = source[i];
        tmpTarget[i] = {
            target: targetItem,
            source: sourceItem
        }
    }
    targetColumn.map((item: any, index: any) => {
        indexMap[getKey(item)] = index;
    })

    tmpTarget.sort(
        (a: any, b: any) => {
            const indexA = indexMap[getKey(a.target)];
            const indexB = indexMap[getKey(b.target)];
            return indexA - indexB;
        }
    )
    serverSource = tmpTarget.map(
        (item: any) => {
            return item.source;
        }
    )
    serverTarget = tmpTarget.map(
        (item: any) => {
            return item.target
        }
    )

    // 转换字段
    // 接口要求keymap中的连线映射数组放到sourceMap中
    clone.sourceMap.column = serverSource;
    clone.targetMap.column = serverTarget;
    clone.settingMap = clone.setting;

    // type中的特定配置项也放到sourceMap中
    const targetTypeObj = targetMap.type;
    const sourceTypeObj = sourceMap.type;

    for (let key in sourceTypeObj) {
        if (sourceTypeObj.hasOwnProperty(key)) {
            sourceMap[key] = sourceTypeObj[key]
        }
    }
    for (let k2 in targetTypeObj) {
        if (targetTypeObj.hasOwnProperty(k2)) {
            targetMap[k2] = targetTypeObj[k2]
        }
    }

    // 删除接口不必要的字段
    delete clone.keymap;
    delete clone.setting;
    delete clone.dataSourceList;
    delete clone.dataSyncSaved;

    const paths = get(clone, 'sourceMap.path');
    if (paths && isArray(paths)) {
        clone.sourceMap.path = paths.filter((o: any) => o !== '');
    }

    // 数据拼装结果
    return clone;
}

/**
 *  获取数据同步Tab保存的数据参数
 *  ! 当保存Tab中的数据同步时使用
 */
export const getDataSyncSaveTabParams = (currentTabData: any, dataSync: any) => {
    // deepClone避免直接mutate store
    let reqBody = cloneDeep(currentTabData);
    // 如果当前任务为数据同步任务
    if (currentTabData.id === dataSync.tabId) {
        const isIncrementMode = currentTabData.syncModel !== undefined && DATA_SYNC_MODE.INCREMENT === currentTabData.syncModel;
        reqBody = assign(reqBody, getDataSyncReqParams(dataSync));
        if (!isIncrementMode) {
            reqBody.sourceMap.increColumn = undefined; // Delete increColumn
        }
    }
    // 修改task配置时接口要求的标记位
    reqBody.preSave = true;

    // 接口要求上游任务字段名修改为dependencyTasks
    if (reqBody.taskVOS) {
        reqBody.dependencyTasks = reqBody.taskVOS.map((o: any) => o);
        reqBody.taskVOS = null;
    }

    // 删除不必要的字段
    delete reqBody.taskVersions;
    delete reqBody.dataSyncSaved;

    // 数据拼装结果
    return reqBody;
}
