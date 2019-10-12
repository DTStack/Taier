import { message } from 'antd';
import { debounce } from 'lodash';

import {
    TASK_TYPE,
    TASK_STATUS,
    RESOURCE_TYPE,
    RDB_TYPE_ARRAY,
    LEARNING_TYPE,
    PYTON_VERSION,
    SCRIPT_TYPE,
    DATA_SOURCE,
    PROJECT_TYPE,
    ENGINE_SOURCE_TYPE,
    TABLE_TYPE
} from './const';

// 请求防抖动
export function debounceEventHander (func: any, wait?: number, options?: any) {
    const debounced = debounce(func, wait, options)
    return function (e: any) {
        e.persist()
        return debounced(e)
    }
}

/**
 * 默认的SQL模板
 */
export function getDefaultSQLTemp (data: any) {
    let temp = '';
    for (let i = 0; i < data.length; i++) {
        temp += `ADDJAR ADD JAR WITH 
        ${data[i]};
    `;
    }
    return temp;
}

/**
 * 获取任务类型指定图表className
 */
export function taskTypeIcon (type: any, task = {}) {
    const { pythonVersion, learningType, scriptType }: any = task;
    if (scriptType == null) {
        switch (type) {
            case TASK_TYPE.HIVESQL: {
                return 's-hivesql';
            }
            case TASK_TYPE.CUBE_KYLIN: {
                return 's-cubeKylin';
            }
            case TASK_TYPE.SQL: {
                return 's-sqlicon';
            }
            case TASK_TYPE.MR: {
                return 's-mricon';
            }
            case TASK_TYPE.HAHDOOPMR: {
                return 's-mricon';
            }
            case TASK_TYPE.SYNC: {
                return 's-datasyncicon';
            }
            case TASK_TYPE.PYTHON: {
                return 's-sparkpythonicon';
            }
            case TASK_TYPE.VIRTUAL_NODE: {
                return 's-virtualicon';
            }
            case TASK_TYPE.DEEP_LEARNING: {
                if (learningType == LEARNING_TYPE.MXNET) {
                    return 's-mxnet';
                } else if (learningType == LEARNING_TYPE.TENSORFLOW) {
                    return 's-tensorflow';
                } else {
                    return 's-deeplearning';
                }
            }
            case TASK_TYPE.PYTHON_23: {
                if (pythonVersion == PYTON_VERSION.PYTHON2) {
                    return 's-python2icon';
                } else if (pythonVersion == PYTON_VERSION.PYTHON3) {
                    return 's-python3icon';
                } else {
                    return 's-pythonicon';
                }
            }
            case TASK_TYPE.SHELL: {
                return 's-shell';
            }
            case TASK_TYPE.WORKFLOW: {
                return 's-workflow';
            }
            case TASK_TYPE.DATA_COLLECTION: {
                return 's-collection';
            }
            case TASK_TYPE.ML: {
                return 's-mlicon';
            }
            case TASK_TYPE.CARBONSQL: {
                return 's-carbonsql';
            }
            case TASK_TYPE.NOTEBOOK: {
                return 's-notebook';
            }
            case TASK_TYPE.EXPERIMENT: {
                return 's-experiment';
            }
            case TASK_TYPE.LIBRASQL: {
                return 's-librasql';
            }
            case TASK_TYPE.IMPALA_SQL: {
                return 's-impalasql';
            }
            default:
                return '';
        }
    } else {
        switch (type) {
            case SCRIPT_TYPE.SQL: {
                return 's-sqlicon';
            }
            case SCRIPT_TYPE.PYTHON2: {
                return 's-python2icon';
            }
            case SCRIPT_TYPE.PYTHON3: {
                return 's-python3icon';
            }
            case SCRIPT_TYPE.SHELL: {
                return 's-shell';
            }
            case SCRIPT_TYPE.LIBRASQL: {
                return 's-librasql';
            }
            case SCRIPT_TYPE.IMPALA_SQL: {
                return 's-impalasql';
            }
            default:
                return '';
        }
    }
}

export function resourceTypeIcon (type: any) {
    switch (type) {
        case RESOURCE_TYPE.JAR: {
            return 's-jaricon-r';
        }
        case RESOURCE_TYPE.PY: {
            return 's-pythonicon-r';
        }
        case RESOURCE_TYPE.ZIP: {
            return 's-zipicon-r';
        }
        case RESOURCE_TYPE.EGG: {
            return 's-eggicon-r';
        }
        case RESOURCE_TYPE.OTHER: {
            return 's-othericon-r';
        }
        default:
            return '';
    }
}

/**
 * 查找树中的某个节点
 */
export function findTreeNode (treeNode: any, node: any) {
    let result = '';
    if (treeNode.id === parseInt(node.id, 10)) {
        return treeNode;
    }
    if (treeNode.children) {
        const children = treeNode.children;
        for (let i = 0; i < children.length; i += 1) {
            result = findTreeNode(children[i], node);
            if (result) return result;
        }
    }
}

/**
 *
 * @param {Array} treeNodes 树状节点数据
 * @param {Object} target 目标节点
 */
export function removeTreeNode (treeNodes: any, target: any) {
    for (let i = 0; i < treeNodes.length; i += 1) {
        if (treeNodes[i].id === target.id) {
            treeNodes.splice(i, 1); // remove节点
            break;
        }
        if (treeNodes[i].children) {
            removeTreeNode(treeNodes[i].children, target);
        }
    }
}

/**
 * 追加子节点
 */
export function appendTreeNode (treeNode: any, append: any, target: any) {
    const targetId = parseInt(target.id, 10);
    if (treeNode.children) {
        const children = treeNode.children;
        for (let i = 0; i < children.length; i += 1) {
            appendTreeNode(children[i], append, target);
        }
    }
    if (treeNode.id === targetId && treeNode.children) {
        treeNode.children.push(append);
    }
}

/**
 * 遍历树形节点，用新节点替换老节点
 */
export function replaceTreeNode (treeNode: any, replace: any) {
    if (
        treeNode.id === parseInt(replace.id, 10) &&
        treeNode.level === replace.level
    ) {
        treeNode = Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.children) {
        const children = treeNode.children;
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace);
        }
    }
}

/**
 * 匹配自定义任务参数
 * @param {Array} taskCustomParams
 * @param {String} sqlText
 */
export function matchTaskParams (taskCustomParams: any, sqlText: any) {
    const regx = /\$\{([.\w]+)\}/g;
    const data: any = [];
    let res = null;
    while ((res = regx.exec(sqlText)) !== null) {
        const name = res[1];
        const param: any = {
            paramName: name,
            paramCommand: ''
        };
        const sysParam = taskCustomParams.find((item: any) => item.paramName === name);
        if (sysParam) {
            param.type = 0;
            param.paramCommand = sysParam.paramCommand;
        } else {
            param.type = 1;
        }
        // 去重
        const exist = data.find((item: any) => name === item.paramName);
        if (!exist) {
            data.push(param);
        }
    }
    return data;
}

export function getVertxtStyle (type: any) {
    switch (type) {
        case TASK_STATUS.FINISHED: // 完成
        case TASK_STATUS.SET_SUCCESS:
            return 'whiteSpace=wrap;fillColor=#F6FFED;strokeColor=#B7EB8F;';
        case TASK_STATUS.SUBMITTING:
        case TASK_STATUS.TASK_STATUS_NOT_FOUND:
        case TASK_STATUS.RUNNING:
            return 'whiteSpace=wrap;fillColor=#E6F7FF;strokeColor=#90D5FF;';
        case TASK_STATUS.RESTARTING:
        case TASK_STATUS.STOPING:
        case TASK_STATUS.DEPLOYING:
        case TASK_STATUS.WAIT_SUBMIT:
        case TASK_STATUS.WAIT_RUN:
            return 'whiteSpace=wrap;fillColor=#FFFBE6;strokeColor=#FFE58F;';
        case TASK_STATUS.RUN_FAILED:
        case TASK_STATUS.PARENT_FAILD:
        case TASK_STATUS.SUBMIT_FAILED:
            return 'whiteSpace=wrap;fillColor=#FFF1F0;strokeColor=#FFA39E;';
        case TASK_STATUS.FROZEN:
            return 'whiteSpace=wrap;fillColor=#EFFFFE;strokeColor=#26DAD1;';
        case TASK_STATUS.STOPED: // 已停止
        default:
        // 默认
            return 'whiteSpace=wrap;fillColor=#F3F3F3;strokeColor=#D4D4D4;';
    }
}

/**
 * 判断当前在离线应用
 */
export function inOffline () {
    return location.href.indexOf('offline') > -1;
}

/**
 * 判断当前在实时应用
 */
export function inRealtime () {
    return location.href.indexOf('realtime') > -1;
}

/**
 * 是否为HDFS类型
 * @param {*} type
 */
export function isHdfsType (type: any) {
    return DATA_SOURCE.HDFS === parseInt(type, 10);
}

/**
 * 是否为FTP类型
 * @param {*} type
 */
export function isFtpType (type: any) {
    return DATA_SOURCE.FTP === parseInt(type, 10);
}

/**
 * 是否属于关系型数据源
 * @param {*} type
 */
export function isRDB (type: any) {
    return RDB_TYPE_ARRAY.indexOf(parseInt(type, 10)) > -1;
}

/**
 * 该项目是否可以编辑
 * @param {project} project
 * @param {user} user
 */
export function isProjectCouldEdit (project: any, user: any) {
    const { adminUsers = [], projectType } = project;
    const isPro = projectType == PROJECT_TYPE.PRO;
    if (!isPro) {
        return true;
    }
    const { id } = user;
    for (let i = 0; i < adminUsers.length; i++) {
        const adminUser = adminUsers[i];
        if (adminUser.id == id) {
            return true;
        }
    }
    return false;
}

/**
 * @description 检查所选是否为文件夹
 * @param {any} rule
 * @param {any} value
 * @param {any} cb
 * @memberof TaskForm
 */
export function checkNotDir (value: any, folderTree: any) {
    let nodeType: any;

    const loop = (arr: any) => {
        arr.forEach((node: any, i: any) => {
            if (node.id === value) {
                nodeType = node.type;
            } else {
                loop(node.children || []);
            }
        });
    };

    loop([folderTree]);

    if (nodeType === 'folder') {
        message.error('请选择具体文件, 而非文件夹');
        return false;
    }
    return true;
}
export function formJsonValidator (rule: any, value: any, callback: any) {
    let msg: any;
    try {
        if (value) {
            let t = JSON.parse(value);
            if (typeof t != 'object') {
                msg = '请填写正确的JSON'
            }
        }
    } catch (e) {
        msg = '请检查JSON格式，确认无中英文符号混用！'
    } finally {
        callback(msg);
    }
}

// Judge spark engine
export function isSparkEngine (engineType: any) {
    return ENGINE_SOURCE_TYPE.HADOOP === parseInt(engineType, 10);
}

// Judge libra engine
export function isLibrAEngine (engineType: any) {
    return ENGINE_SOURCE_TYPE.LIBRA === parseInt(engineType, 10);
}
export function isHiveTable (tableType: any) {
    return TABLE_TYPE.HIVE === parseInt(tableType, 10);
}
export function isLibraTable (tableType: any) {
    return TABLE_TYPE.LIBRA === parseInt(tableType, 10);
}
