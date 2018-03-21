import mc from 'mirror-creator';

const BASE_URL = '/api/service'; // 从config文件全局读取
import { UIC_BASE_URL } from 'config/base';

export default {
    // ===== 用户模块 ===== //
    LOGOUT: `${UIC_BASE_URL}/v2/logout`,
}

