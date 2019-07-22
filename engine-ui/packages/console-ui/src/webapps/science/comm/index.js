import {
    PROJECT_TYPE,
    COMPONENT_TYPE,
    INPUT_TYPE,
    RESOURCE_TYPE
} from '../consts';

/**
 * 该项目是否可以编辑
 * @param {project} project
 * @param {user} user
 */
export function isProjectCouldEdit (project, user) {
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
 * 匹配自定义任务参数
 * @param {Array} taskCustomParams
 * @param {String} sqlText
 */
export function matchTaskParams (taskCustomParams, sqlText) {
    const regx = /\$\{([.\w]+)\}/g;
    const data = [];
    let res = null;
    while ((res = regx.exec(sqlText)) !== null) {
        const name = res[1];
        const param = {
            paramName: name,
            paramCommand: ''
        };
        const sysParam = taskCustomParams.find(item => item.paramName === name);
        if (sysParam) {
            param.type = 0;
            param.paramCommand = sysParam.paramCommand;
        } else {
            param.type = 1;
        }
        // 去重
        const exist = data.find(item => name === item.paramName);
        if (!exist) {
            data.push(param);
        }
    }
    return data;
}

/**
 * 获取输入类型项
 * @param {number} componentType 组件类型
 */
export function getInputTypeItems (componentType) {
    let menuArr = []
    switch (componentType) {
        case COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT: {
            menuArr = [
                {
                    inputType: INPUT_TYPE.SQL_1,
                    index: 1
                }, {
                    inputType: INPUT_TYPE.SQL_2,
                    index: 2
                }, {
                    inputType: INPUT_TYPE.SQL_3,
                    index: 3
                }, {
                    inputType: INPUT_TYPE.SQL_4,
                    index: 4
                }
            ]
            break;
        }
        case COMPONENT_TYPE.DATA_MERGE.NORMALIZE: {
            menuArr = [
                {
                    inputType: INPUT_TYPE.NORMALIZATION_INPUT_DATA,
                    index: 1
                }, {
                    inputType: INPUT_TYPE.NORMALIZATION_INPUT_PARAM,
                    index: 2
                }, {
                    inputType: INPUT_TYPE.NORMALIZATION_OUTPUT_DATA,
                    index: 3
                }, {
                    inputType: INPUT_TYPE.NORMALIZATION_OUTPUT_PARAM,
                    index: 4
                }
            ]
            break;
        }
        case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT: {
            menuArr = [
                {
                    inputType: INPUT_TYPE.PREDICT_INPUT_MODAL,
                    index: 1
                }, {
                    inputType: INPUT_TYPE.PREDICT_INPUT_DATA,
                    index: 2
                }
            ]
            break;
        }
        case COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE: {
            menuArr = [
                {
                    inputType: INPUT_TYPE.SOURCE_READ,
                    index: 1
                }
            ];
            break;
        }
        case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT: {
            menuArr = [
                {
                    inputType: INPUT_TYPE.DATA_SPLIT_LEFT,
                    index: 1
                }, {
                    inputType: INPUT_TYPE.DATA_SPLIT_RIGHT,
                    index: 2
                }
            ];
            break;
        }
        case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION: {
            menuArr = [];
            break;
        }
        case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION: {
            menuArr = [
                {
                    inputType: INPUT_TYPE.EVALUATION_OVERALL_DATA,
                    index: 1
                }, {
                    inputType: INPUT_TYPE.EVALUATION_FREQUENCY_DATA,
                    index: 2
                }, {
                    inputType: INPUT_TYPE.EVALUATION_WIDTH_DATA,
                    index: 3
                }
            ]
            break;
        }
        default: {
            menuArr = [{
                inputType: INPUT_TYPE.NORMAL,
                index: 1
            }];
            break;
        }
    }
    return menuArr;
}

export function resourceTypeIcon (type) {
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
