// cluster function
import { COMPONENT_TYPE_VALUE, validateFlinkParams, validateHiveParams,
    validateCarbonDataParams, validateSparkParams, validateDtYarnShellParams, validateLearningParams, validateLibraParams } from './index';
export function getComponentConfKey (componentValue) { // 不同component显示不同配置项参数
    switch (componentValue) {
        case COMPONENT_TYPE_VALUE.FLINK: {
            return 'flinkConf'
        }
        case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: {
            return 'sparkThriftConf'
        }
        case COMPONENT_TYPE_VALUE.CARBONDATA: {
            return 'carbonConf'
        }
        case COMPONENT_TYPE_VALUE.SPARK: {
            return 'sparkConf'
        }
        case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
            return 'dtyarnshellConf'
        }
        case COMPONENT_TYPE_VALUE.LEARNING: {
            return 'learningConf'
        }
        case COMPONENT_TYPE_VALUE.HDFS: {
            return 'hadoopConf'
        }
        case COMPONENT_TYPE_VALUE.YARN: {
            return 'yarnConf'
        }
        case COMPONENT_TYPE_VALUE.LIBRASQL: {
            return 'libraSqlConf'
        }
        default: {
            return ''
        }
    }
}

// 单引擎校验
// hdfs yarn 不校验
export function validateCompParams (componentValue) {
    switch (componentValue) {
        case COMPONENT_TYPE_VALUE.FLINK: {
            return validateFlinkParams
        }
        case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
            return validateHiveParams
        }
        case COMPONENT_TYPE_VALUE.CARBONDATA: {
            return validateCarbonDataParams
        }
        case COMPONENT_TYPE_VALUE.SPARK: {
            return validateSparkParams
        }
        case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
            return validateDtYarnShellParams
        }
        case COMPONENT_TYPE_VALUE.LEARNING: {
            return validateLearningParams
        }
        case COMPONENT_TYPE_VALUE.HDFS: {
            return []
        }
        case COMPONENT_TYPE_VALUE.YARN: {
            return []
        }
        case COMPONENT_TYPE_VALUE.LIBRASQL: {
            return validateLibraParams
        }
        default: {
            return null
        }
    }
}

// 表单字段. => 驼峰转化
export function myUpperCase (obj) {
    let after = {};

    let keys = [];

    let values = [];

    let newKeys = [];
    // . --> 驼峰
    for (let i in obj) {
        if (obj.hasOwnProperty(i)) {
            keys.push(i);
            values.push(obj[i]);
        }
    }
    keys.forEach(function (item, index) {
        let itemSplit = item.split('.');
        let newItem = itemSplit[0];
        for (let i = 1; i < itemSplit.length; i++) {
            let letters = itemSplit[i].split('');
            let firstLetter = letters.shift();
            firstLetter = firstLetter.toUpperCase();
            letters.unshift(firstLetter);
            newItem += letters.join('')
        }
        newKeys[index] = newItem;
    })
    for (let i = 0; i < values.length; i++) {
        after[newKeys[i]] = values[i]
    }
    return after;
}

// 驼峰 => .转化
export function myLowerCase (obj) {
    let after = {};

    let alphabet = 'QWERTYUIOPLKJHGFDSAZXCVBNM';
    for (let i in obj) {
        if (obj.hasOwnProperty(i)) {
            let keySplit = '';
            keySplit = i.split('');
            for (let j = 0; j < keySplit.length; j++) {
                if (keySplit[j] == '.') {
                    keySplit.splice(j, 1);
                    keySplit[j] = keySplit[j].toUpperCase();
                } else if (alphabet.indexOf(keySplit[j]) != -1) {
                    keySplit[j] = keySplit[j].toLowerCase();
                    keySplit.splice(j, 0, '.');
                    j++;
                }
            }
            keySplit = keySplit.join('');
            after[keySplit] = obj[i];
        }
    }
    return after;
}
/**
 * PYspark两字段需转化(spark.yarn.appMasterEnv.PYSPARK_PYTHON,
 * spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON)
 * @param obj 传入对象
 * @param keyMap key映射关系
 */
export function toChsKeys (obj, keyMap) {
    return Object.keys(obj).reduce((newObj, key) => {
        let newKey = keyMap[key] || key;
        newObj[newKey] = obj[key];
        return newObj
    }, {})
}
