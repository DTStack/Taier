// 从config文件全局读取
import { ANALYTICS_ENGINE_BASE_URL } from 'config/base';

export default {

    GET_CATALOGUES: `${ANALYTICS_ENGINE_BASE_URL}/getCatalogue`, // 获取目录
    CREATE_OR_UPDATE_DB: `${ANALYTICS_ENGINE_BASE_URL}/createOrUpdateDB`, //创建或者修改数据库

}
