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
