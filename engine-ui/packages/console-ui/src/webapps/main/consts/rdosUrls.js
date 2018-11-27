import mc from 'mirror-creator';
import { RDOS_BASE_URL } from 'config/base';

/**
 * RDOS 全局接口请求地址
 */
export default {
    // ===== 消息管理 ===== //
    MASSAGE_QUERY: `${RDOS_BASE_URL}/common/notifyRecord/pageQuery`,
    GET_MASSAGE_BY_ID: `${RDOS_BASE_URL}/common/notifyRecord/getOne`,
    MASSAGE_MARK_AS_READ: `${RDOS_BASE_URL}/common/notifyRecord/tabRead`,
    MASSAGE_MARK_AS_ALL_READ: `${RDOS_BASE_URL}/common/notifyRecord/allRead`,
    MASSAGE_DELETE: `${RDOS_BASE_URL}/common/notifyRecord/delete`
}
