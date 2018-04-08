import { RDOS_BASE_URL } from 'config/base';

/**
 * 数据模型
 */
export default {

    // ============ 配置中心 ============
    MODEL_ADD: `${RDOS_BASE_URL}/batch/modelTable/add`,     // 新增模型层级/主题域/频率/增量
    MODEL_UPDATE: `${RDOS_BASE_URL}/batch/modelTable/update`, // 编辑 模型层级/主题域/频率/增量
    MODEL_DELETE: `${RDOS_BASE_URL}/batch/modelTable/delete`,    // 新增 原子指标/衍生指标
    MODEL_LIST: `${RDOS_BASE_URL}/batch/modelTable/pageQuery`,    // 分页查询 层级/主题域/频率/增量

    MODEL_INDEX_ADD: `${RDOS_BASE_URL}/batch/modelColumn/add`,    // 新增 原子指标/衍生指标
    MODEL_INDEX_UPDATE: `${RDOS_BASE_URL}/batch/modelColumn/update`,     // 编辑 原子指标/衍生指标
    MODEL_INDEX_DELETE: `${RDOS_BASE_URL}/batch/modelColumn/delete`,     // 删除原子/衍生指标
    MODEL_INDEX_LIST: `${RDOS_BASE_URL}/batch/modelColumn/pageQuery`,     // 分页查询 原子/衍生

    MODEL_RULE_CREATE: `${RDOS_BASE_URL}/batch/modelRule/save`, // 保存 表命名规则生成配置
    MODEL_RULE_LIST: `${RDOS_BASE_URL}/batch/modelTable/getTableRules`, // 获取配置表名的几大模块（层级/主题域/频率/增量）

    // ============ 模型设计 ============
    TABLE_LIST: `${RDOS_BASE_URL}/batch/modelTable/tablePageQuery`, // 模型设计 - 表管理
    TABLE_CREATE: `${RDOS_BASE_URL}/batch/modelTable/createTable`, // 模型设计 - 创建表
    TABLE_NAME_RULE: `${RDOS_BASE_URL}/batch/modelRule/getModelRule`, // 模型设计 - 获取表名规则

}