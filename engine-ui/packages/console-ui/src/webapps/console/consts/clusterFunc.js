// cluster function
import { COMPONENT_TYPE_VALUE, ENGINE_TYPE, validateFlinkParams, validateHiveParams,
    validateCarbonDataParams, validateSparkParams, validateDtYarnShellParams, validateLearningParams, validateLibraParams } from './index';

/**
 * 返回组件不同key
 */
export function getComponentConfKey (componentValue) {
    switch (componentValue) {
        case COMPONENT_TYPE_VALUE.FLINK: {
            return 'flinkConf'
        }
        case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: {
            return 'hiveConf'
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
            return 'libraConf'
        }
        default: {
            return ''
        }
    }
}
/**
 * 返回不同组件校验参数
 * @param componentValue 组件
 */
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
/**
 * hadoop,libra服务端数据转化
 * 接口数据一次全部返回，这里合并处理
 * @param hadoopComp hadoop参数配置项
 * @param libraComp libra参数配置项
 */
export function exChangeComponentConf (hadoopComp, libraComp) {
    const comp = hadoopComp.concat(libraComp);
    let componentConf = {
        flinkConf: {},
        sparkConf: {},
        learningConf: {},
        dtyarnshellConf: {},
        hadoopConf: {},
        yarnConf: {},
        hiveConf: {}, // 对应sparkThrift
        carbonConf: {},
        libraConf: {}
    };
    comp.map(item => {
        const componentTypeCode = item.componentTypeCode;
        switch (componentTypeCode) {
            case COMPONENT_TYPE_VALUE.FLINK: {
                componentConf = Object.assign(componentConf, {
                    flinkConf: item.config
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARK: {
                componentConf = Object.assign(componentConf, {
                    sparkConf: item.config
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                componentConf = Object.assign(componentConf, {
                    learningConf: item.config
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                componentConf = Object.assign(componentConf, {
                    dtyarnshellConf: item.config
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.HDFS: {
                componentConf = Object.assign(componentConf, {
                    hadoopConf: item.config
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                componentConf = Object.assign(componentConf, {
                    yarnConf: item.config
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: {
                componentConf = Object.assign(componentConf, {
                    hiveConf: item.config
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                componentConf = Object.assign(componentConf, {
                    carbonConf: item.config
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.LIBRASQL: {
                componentConf = Object.assign(componentConf, {
                    libraConf: item.config
                })
                break;
            }
        }
    })
    return componentConf
}
/**
 * 引擎显示测试结果
 * @param testResults 测试结果
 * @param engineType 引擎类型
 */
export function showTestResult (testResults, engineType) {
    let testStatus = {}
    const isHadoop = engineType == ENGINE_TYPE.HADOOP;
    testResults && testResults.map(comp => {
        switch (comp.componentTypeCode) {
            case COMPONENT_TYPE_VALUE.FLINK: {
                testStatus = Object.assign(testStatus, {
                    flinkTestResult: isHadoop ? comp : {} // 区分Hadoop, libra，单独显示
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: {
                testStatus = Object.assign(testStatus, {
                    sparkThriftTestResult: isHadoop ? comp : {}
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                testStatus = Object.assign(testStatus, {
                    carbonTestResult: isHadoop ? comp : {}
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARK: {
                testStatus = Object.assign(testStatus, {
                    sparkTestResult: isHadoop ? comp : {}
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                testStatus = Object.assign(testStatus, {
                    dtYarnShellTestResult: isHadoop ? comp : {}
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                testStatus = Object.assign(testStatus, {
                    learningTestResult: isHadoop ? comp : {}
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.HDFS: {
                testStatus = Object.assign(testStatus, {
                    hdfsTestResult: isHadoop ? comp : {}
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                testStatus = Object.assign(testStatus, {
                    yarnTestResult: isHadoop ? comp : {}
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.LIBRASQL: {
                testStatus = Object.assign(testStatus, {
                    libraSqlTestResult: !isHadoop ? comp : {}
                })
                break;
            }
            default: {
                testStatus = Object.assign(testStatus, {})
            }
        }
    })
    return testStatus
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
