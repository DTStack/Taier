import UserAPI from 'main/api/user';
import { API } from './apiMap';

export default {
    // ========== User ========== //
    logout () { // 注销退出
        UserAPI.logout();
    },
    getLoginedUser (params: any) {
        return API.getLoginedUser(params);
    },
    getProjects (params: any) {
        return API.getAllProjects(params);
    },
    getAllProjects (params: any) { // 获取所有项目
        return API.getAllProjects(params);
    },
    getProjectByID (params: any) { // 获取项目通过id
        return API.getProjectByID(params);
    }
}
