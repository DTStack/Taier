// 从config文件全局读取
import { ANALYTICS_ENGINE_BASE_URL } from 'config/base';

export default {

    // ===== 系统管理 ===== //
    GET_CATALOGUES: `${ANALYTICS_ENGINE_BASE_URL}/getCatalogue`, // 获取目录
}
