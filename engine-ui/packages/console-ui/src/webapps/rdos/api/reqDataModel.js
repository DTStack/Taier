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
    TABLE_LIST_BY_TYPE: `${RDOS_BASE_URL}/batch/modelTable/listByType`, // 获取 层级/主题域/频率/增量 的配置列表
    TABLE_PARITIONS: `${RDOS_BASE_URL}/batch/modelColumn/listAll`, // 获取模型字段配置列表
    TABLE_CREATE_BY_DDL: `${RDOS_BASE_URL}/batch/hiveMetaData/ddlCreateTable`, // ddl建表

    // ============ 检测中心 ============
    CHECK_LIST: `${RDOS_BASE_URL}/batch/modelMonitorData/tablePageQuery`, // 查询 ---模型检测
    CHECK_PARTITIONS_LIST: `${RDOS_BASE_URL}/batch/modelMonitorData/columnPageQuery`, // 查询----字段检测
    CHECK_IGNORE: `${RDOS_BASE_URL}/batch/modelMonitorData/ignore`, // 忽略操作

    // ============ 总览 ============
    STATISTICS_USAGE: `${RDOS_BASE_URL}/batch/modelRule/usage`, // 模型汇总信息
    STATISTICS_TABLE_RATE: `${RDOS_BASE_URL}/batch/modelRule/tableRate`, // 模型不规范原因分布
    STATISTICS_COLUMN_RATE: `${RDOS_BASE_URL}/batch/modelTable/columnRate`, // 字段不规范原因分布
    STATISTICS_TABLE_TREND: `${RDOS_BASE_URL}/batch/modelColumn/tableTrend`, // 模型不规范趋势分析
    STATISTICS_COLUMN_TREND: `${RDOS_BASE_URL}/batch/hiveMetaData/columnTrend`, // ddl建表
}