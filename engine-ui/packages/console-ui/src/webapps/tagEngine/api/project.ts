export default {
    getAllProjects: { // 获取所以项目列表
        method: 'post',
        url: `/api/v1/project/getProjects`
    },
    getProjects: { // 获取所以项目列表
        method: 'post',
        url: `/api/v1/project/projectPage`
    },
    getProjectByID: { // 获取所以项目列表
        method: 'post',
        url: `/api/v1/project/projectDetail`
    }
}
