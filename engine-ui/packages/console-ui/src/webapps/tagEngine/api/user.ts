export default {
    logout: {
        method: 'post',
        url: `/api/v1/login/out`
    },
    getLoginedUser: { // 获取所以项目列表
        method: 'post',
        url: `/api/v1/user/getUserById`
    }
}
