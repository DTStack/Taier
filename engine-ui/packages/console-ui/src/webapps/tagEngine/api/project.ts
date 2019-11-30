export default {
    getAllProjects: { // 获取所以项目列表
        method: 'post',
        url: `/api/v1/project/getProjects`
    },
    getProjects: { // 获取项目列表
        method: 'post',
        url: `/api/v1/project/projectPage`
    },
    getProjectByID: { // 获取项目详情
        method: 'post',
        url: `/api/v1/project/projectDetail`
    },
    createProject: { // 新建项目
        method: 'post',
        url: `/api/v1/project/createProject`
    },
    updateProjectName: { // 修改项目名称
        method: 'post',
        url: `/api/v1/project/updateProjectName`
    },
    deleteProject: { // 删除项目
        method: 'post',
        url: `/api/v1/project/deleteProject`
    },
    getProjectUsers: { // 获取项目下的用户
        method: 'post',
        url: `/api/v1/project/getProjectUsers`
    }
}
