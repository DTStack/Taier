// cluster function
import { TASK_STATE, COMPONENT_TYPE_VALUE, ENGINE_TYPE, validateFlinkParams, validateHiveParams,
    validateCarbonDataParams, validateSparkParams, validateDtYarnShellParams, validateLearningParams, validateHiveServerParams, validateLibraParams } from './index';

/**
 * 返回组件不同key
 */
export function getComponentConfKey (componentValue: any) {
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
        case COMPONENT_TYPE_VALUE.HIVESERVER: {
            return 'hiveServerConf'
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
export function validateCompParams (componentValue: any) {
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
        case COMPONENT_TYPE_VALUE.HIVESERVER: {
            return validateHiveServerParams
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
export function exChangeComponentConf (hadoopComp: any = [], libraComp: any = []) {
    const comp = hadoopComp.concat(libraComp);
    let componentConf: any = {};
    comp.map((item: any) => {
        const componentTypeCode = item && item.componentTypeCode;
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
            case COMPONENT_TYPE_VALUE.HIVESERVER: {
                componentConf = Object.assign(componentConf, {
                    hiveServerConf: item.config
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
export function showTestResult (testResults: any, engineType: any) {
    let testStatus: any = {}
    const isHadoop = isHadoopEngine(engineType);
    testResults && testResults.map((comp: any) => {
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
            case COMPONENT_TYPE_VALUE.HIVESERVER: {
                testStatus = Object.assign(testStatus, {
                    hiveServerTestResult: isHadoop ? comp : {}
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
/**
 * 校验组件必填项未填标识
 * @param tabCompData 不同engine的组件数据
 */
export function validateAllRequired (validateFieldsAndScroll: any, tabCompData: any) {
    let obj: any = {}
    tabCompData && tabCompData.map((item: any) => {
        validateFieldsAndScroll(validateCompParams(item.componentTypeCode), {
            force: true,
            scroll: {
                offsetBottom: 150
            }
        }, (err: any, values: any) => {
            if (item.componentTypeCode == COMPONENT_TYPE_VALUE.FLINK) {
                if (!err) {
                    obj = Object.assign(obj, {
                        flinkShowRequired: false
                    })
                } else {
                    obj = Object.assign(obj, {
                        flinkShowRequired: true
                    })
                }
            } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER) {
                if (!err) {
                    obj = Object.assign(obj, {
                        hiveShowRequired: false
                    })
                } else {
                    obj = Object.assign(obj, {
                        hiveShowRequired: true
                    })
                }
            } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.CARBONDATA) {
                if (!err) {
                    obj = Object.assign(obj, {
                        carbonShowRequired: false
                    })
                } else {
                    obj = Object.assign(obj, {
                        carbonShowRequired: true
                    })
                }
            } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.HIVESERVER) {
                if (!err) {
                    obj = Object.assign(obj, {
                        hiveServerShowRequired: false
                    })
                } else {
                    obj = Object.assign(obj, {
                        hiveServerShowRequired: true
                    })
                }
            } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.SPARK) {
                if (!err) {
                    obj = Object.assign(obj, {
                        sparkShowRequired: false
                    })
                } else {
                    obj = Object.assign(obj, {
                        sparkShowRequired: true
                    })
                }
            } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.DTYARNSHELL) {
                if (!err) {
                    obj = Object.assign(obj, {
                        dtYarnShellShowRequired: false
                    })
                } else {
                    obj = Object.assign(obj, {
                        dtYarnShellShowRequired: true
                    })
                }
            } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.LEARNING) {
                if (!err) {
                    obj = Object.assign(obj, {
                        learningShowRequired: false
                    })
                } else {
                    obj = Object.assign(obj, {
                        learningShowRequired: true
                    })
                }
            } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.HDFS) {
                if (!err) {
                    obj = Object.assign(obj, {
                        hdfsShowRequired: false
                    })
                } else {
                    obj = Object.assign(obj, {
                        hdfsShowRequired: true
                    })
                }
            } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.YARN) {
                if (!err) {
                    obj = Object.assign(obj, {
                        yarnShowRequired: false
                    })
                } else {
                    obj = Object.assign(obj, {
                        yarnShowRequired: true
                    })
                }
            } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.LIBRASQL) {
                if (!err) {
                    obj = Object.assign(obj, {
                        libraShowRequired: false
                    })
                } else {
                    obj = Object.assign(obj, {
                        libraShowRequired: true
                    })
                }
            } else {
                console.log('error')
            }
        })
    })
    return obj
}

export function displayTaskStatus (status: any) {
    switch (status) {
        case TASK_STATE.UNSUBMIT:
            return 'UNSUBMIT';
        case TASK_STATE.CREATED:
            return 'CREATED';
        case TASK_STATE.SCHEDULED:
            return 'SCHEDULED';
        case TASK_STATE.DEPLOYING:
            return 'DEPLOYING';
        case TASK_STATE.RUNNING:
            return 'RUNNING';
        case TASK_STATE.FINISHED:
            return 'FINISHED';
        case TASK_STATE.CANCELLING:
            return 'CANCELLING';
        case TASK_STATE.CANCELED:
            return 'CANCELED';
        case TASK_STATE.FAILED:
            return 'FAILED';
        case TASK_STATE.SUBMITFAILD:
            return 'SUBMITFAILD';
        case TASK_STATE.SUBMITTING:
            return 'SUBMITTING';
        case TASK_STATE.RESTARTING:
            return 'RESTARTING';
        case TASK_STATE.MANUALSUCCESS:
            return 'MANUALSUCCESS';
        case TASK_STATE.KILLED:
            return 'KILLED';
        case TASK_STATE.SUBMITTED:
            return 'SUBMITTED';
        case TASK_STATE.NOTFOUND:
            return 'NOTFOUND';
        case TASK_STATE.WAITENGINE:
            return 'WAITENGINE';
        case TASK_STATE.WAITCOMPUTE:
            return 'WAITCOMPUTE';
        case TASK_STATE.FROZEN:
            return 'FROZEN';
        case TASK_STATE.ENGINEACCEPTED:
            return 'ENGINEACCEPTED';
        case TASK_STATE.ENGINEDISTRIBUTE:
            return 'ENGINEDISTRIBUTE';
        default:
            return null;
    }
}

// 表单字段. => 驼峰转化
export function myUpperCase (obj: any) {
    let after: any = {};

    let keys: any = [];

    let values: any = [];

    let newKeys: any = [];
    // . --> 驼峰
    for (let i in obj) {
        if (obj.hasOwnProperty(i)) {
            keys.push(i);
            values.push(obj[i]);
        }
    }
    keys.forEach(function (item: any, index: any) {
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
export function myLowerCase (obj: any) {
    let after: any = {};

    let alphabet = 'QWERTYUIOPLKJHGFDSAZXCVBNM';
    for (let i in obj) {
        if (obj.hasOwnProperty(i)) {
            let keySplit: any[];
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
            const keySplitStr: string = keySplit.join('');
            // keySplit = keySplit.join('');
            after[keySplitStr] = obj[i];
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
export function toChsKeys (obj: any, keyMap: any) {
    return Object.keys(obj).reduce((newObj: any, key: any) => {
        let newKey = keyMap[key] || key;
        newObj[newKey] = obj[key];
        return newObj
    }, {})
}

// 是否是hadoop引擎
export function isHadoopEngine (engineType: any) {
    return engineType == ENGINE_TYPE.HADOOP
}
