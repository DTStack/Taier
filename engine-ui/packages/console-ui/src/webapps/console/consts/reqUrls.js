// 从config文件全局读取
import { CONSOLE_BASE_URL, DQ_BASE_URL } from 'config/base';

export default {

    // ===== 用户相关 ===== //
    DA_GET_USER_BY_ID: `${CONSOLE_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    DA_GET_USER_AUTH_BY_ID:`${CONSOLE_BASE_URL}/user/getUserById`, // 根据用户ID获取用户权限
    
    //集群
    GET_RESOURCE_LIST:`${CONSOLE_BASE_URL}/cluster/pageQueue`,//查看资源列表
    GET_CLUSTER_LIST:`${CONSOLE_BASE_URL}/cluster/pageQuery`,//查看集群列表
}   
