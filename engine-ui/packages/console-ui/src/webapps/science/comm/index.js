import {
    PROJECT_TYPE
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
