import { STREAM_BASE_URL } from 'config/base';

export default {
    getLoginedUser: { // 获取所以项目列表
        method: 'post',
        url: `${STREAM_BASE_URL}/user/getUserById`
    }
}
