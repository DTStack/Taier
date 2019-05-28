import { RDOS_BASE_URL } from 'config/base';

/**
 * 数据模型
 */
export default {

    // ============ 配置中心 ============
    MODEL_ADD: `${RDOS_BASE_URL}/batch/batchModelTable/add`, // 新增模型层级/主题域/频率/增量
    MODEL_UPDATE: `${RDOS_BASE_URL}/batch/batchModelTable/update`, // 编辑 模型层级/主题域/频率/增量
    MODEL_DELETE: `${RDOS_BASE_URL}/batch/batchModelTable/remove`, // 新增 原子指标/衍生指标
    MODEL_LIST: `${RDOS_BASE_URL}/batch/batchModelTable/pageQuery`, // 分页查询 层级/主题域/频率/增量

    MODEL_INDEX_ADD: `${RDOS_BASE_URL}/batch/batchModelColumn/add`, // 新增 原子指标/衍生指标
    MODEL_INDEX_UPDATE: `${RDOS_BASE_URL}/batch/batchModelColumn/update`, // 编辑 原子指标/衍生指标
    MODEL_INDEX_DELETE: `${RDOS_BASE_URL}/batch/batchModelColumn/remove`, // 删除原子/衍生指标
    MODEL_INDEX_LIST: `${RDOS_BASE_URL}/batch/batchModelColumn/pageQuery`, // 分页查询 原子/衍生

    MODEL_RULE_CREATE: `${RDOS_BASE_URL}/batch/batchModelRule/save`, // 保存 表命名规则生成配置
    MODEL_RULE_LIST: `${RDOS_BASE_URL}/batch/batchModelTable/getTableRules`, // 获取配置表名的几大模块（层级/主题域/频率/增量）

    COLUMN_TYPE_LIST: `${RDOS_BASE_URL}/batch/batchModelColumn/getColumnType`, // 获取原子和衍生的数据类型

    TYPE_LIST: `${RDOS_BASE_URL}/batch/batchModelColumn/getType`, // 获取原子指标类型

    // ============ 模型设计 ============
    TABLE_LIST: `${RDOS_BASE_URL}/batch/batchModelTable/tablePageQuery`, // 模型设计 - 表管理
    TABLE_CREATE: `${RDOS_BASE_URL}/batch/batchTableInfo/createTable`, // 模型设计 - 创建表
    TABLE_DELETE: `${RDOS_BASE_URL}/batch/batchTableInfo/dropTable`, // 模型设计 - 删除表
    TABLE_ALTER: `${RDOS_BASE_URL}/batch/batchTableInfo/alterTable`, // 模型设计 - 修改表
    TABLE_NAME_RULE: `${RDOS_BASE_URL}/batch/batchModelRule/getModelRule`, // 模型设计 - 获取表名规则
    TABLE_LIST_BY_TYPE: `${RDOS_BASE_URL}/batch/batchModelTable/listByType`, // 获取 层级/主题域/频率/增量 的配置列表
    TABLE_PARITIONS: `${RDOS_BASE_URL}/batch/batchModelColumn/listAll`, // 获取模型字段配置列表
    TABLE_CREATE_BY_DDL: `${RDOS_BASE_URL}/batch/batchTableInfo/ddlCreateTable`, // ddl建表

    // ============ 检测中心 ============
    CHECK_LIST: `${RDOS_BASE_URL}/batch/batchModelMonitorData/pageQuery`, // 查询 ---模型检测 / 字段检测
    CHECK_IGNORE: `${RDOS_BASE_URL}/batch/batchModelMonitorData/ignore`, // 忽略操作

    // ============ 总览 ============
    STATISTICS_USAGE: `${RDOS_BASE_URL}/batch/batchModelMonitorData/usage`, // 模型汇总信息
    STATISTICS_TABLE_RATE: `${RDOS_BASE_URL}/batch/batchModelMonitorData/tableRate`, // 模型不规范原因分布
    STATISTICS_COLUMN_RATE: `${RDOS_BASE_URL}/batch/batchModelMonitorData/columnRate`, // 字段不规范原因分布
    STATISTICS_TABLE_TREND: `${RDOS_BASE_URL}/batch/batchModelMonitorData/tableTrend`, // 模型不规范趋势分析
    STATISTICS_COLUMN_TREND: `${RDOS_BASE_URL}/batch/batchModelMonitorData/columnTrend` // 字段不规范趋势
}
