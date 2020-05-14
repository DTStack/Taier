export default {
    /* -------- basic-error-controller Basic Error Controller -------- */
    errorHtmlUsingGet: { // errorHtml
        method: 'get',
        url: `/error` 
    }, 

    /* -------- 登录,注销 Login Controller -------- */
    loginUsingPost: { // 用户登录
        method: 'post',
        url: `/westLake/user/login` 
    }, 
    loginOutUsingDelete: { // 用户注销
        method: 'delete',
        url: `/westLake/user/logout` 
    }, 

    /* -------- 西湖接入系统统计信息 West Lake Datasource Access Controller -------- */
    deleteDataByIdUsingDelete: { // 删除系统信息
        method: 'delete',
        url: `/westLake/data/deleteDataById` 
    }, 
    findDataByIdUsingGet: { // 根据系统id查询系统信息
        method: 'get',
        url: `/westLake/data/findDataById` 
    }, 
    findDataListUsingGet: { // 查询系统列表信息
        method: 'get',
        url: `/westLake/data/findDataList` 
    }, 
    saveDataUsingPost: { // 添加系统信息
        method: 'post',
        url: `/westLake/data/saveData` 
    }, 
    updateDataUsingPut: { // 修改系统信息
        method: 'put',
        url: `/westLake/data/updateData` 
    }, 

}