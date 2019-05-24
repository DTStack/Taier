// cluster function
import { ENGINE_TYPES, validateFlinkParams, validateHiveParams,
    validateCarbonDataParams, validateSparkParams, validateDtYarnShellParams, validateLearningParams } from './index';
export function engineTypeConfig (engineType) { // 不同engine显示不同配置项参数
    switch (engineType) {
        case ENGINE_TYPES.FLINK: {
            return 'flinkConf'
        }
        case ENGINE_TYPES.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
            return 'hiveConf'
        }
        case ENGINE_TYPES.CARBONDATA: {
            return 'carbonConf'
        }
        case ENGINE_TYPES.SPARK: {
            return 'sparkConf'
        }
        case ENGINE_TYPES.DTYARNSHELL: {
            return 'dtyarnshellConf'
        }
        case ENGINE_TYPES.LEARNING: {
            return 'learningConf'
        }
        case ENGINE_TYPES.HDFS: {
            return 'hadoopConf'
        }
        case ENGINE_TYPES.YARN: {
            return 'yarnConf'
        }
        case ENGINE_TYPES.LIBRA: {
            return 'libraConf'
        }
        default: {
            return ''
        }
    }
}

// 单引擎校验
// hdfs yarn libra 暂不知其确定参数
export function validateEngine (engineType) {
    switch (engineType) {
        case ENGINE_TYPES.FLINK: {
            return validateFlinkParams
        }
        case ENGINE_TYPES.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
            return validateHiveParams
        }
        case ENGINE_TYPES.CARBONDATA: {
            return validateCarbonDataParams
        }
        case ENGINE_TYPES.SPARK: {
            return validateSparkParams
        }
        case ENGINE_TYPES.DTYARNSHELL: {
            return validateDtYarnShellParams
        }
        case ENGINE_TYPES.LEARNING: {
            return validateLearningParams
        }
        case ENGINE_TYPES.HDFS: {
            return []
        }
        case ENGINE_TYPES.YARN: {
            return []
        }
        case ENGINE_TYPES.LIBRA: {
            return []
        }
        case null: {
            return null
        }
        default: {
            return null
        }
    }
}
