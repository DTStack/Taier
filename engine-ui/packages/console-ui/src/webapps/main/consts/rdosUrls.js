import mc from 'mirror-creator';

const BASE_URL = '/api';

/**
 * RDOS 全局接口请求地址
 */
export default {
    // ===== RDOS 消息 ===== //
    // ===== 消息管理 ===== //
    MASSAGE_QUERY: `${BASE_URL}/common/notifyRecord/pageQuery`,
    GET_MASSAGE_BY_ID: `${BASE_URL}/common/notifyRecord/getOne`,
    MASSAGE_MARK_AS_READ: `${BASE_URL}/common/notifyRecord/tabRead`,
    MASSAGE_MARK_AS_ALL_READ: `${BASE_URL}/common/notifyRecord/allRead`,
    MASSAGE_DELETE: `${BASE_URL}/common/notifyRecord/delete`,
}

