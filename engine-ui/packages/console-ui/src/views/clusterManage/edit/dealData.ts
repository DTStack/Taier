import _ from 'lodash';
import { COMPONEMT_CONFIG_KEY_ENUM, COMPONENT_TYPE_VALUE } from '../../../consts';

function getActionType (mode: string) {
    switch (mode) {
        case 'view':
            return '查看集群';
        case 'new':
            return '新增集群';
        case 'edit':
            return '编辑集群';
        default:
            return null;
    }
}
// 设置版本默认值
function getCompsVersion (componentTypeCode: number, compVersion: string) {
    let version: any = '';
    switch (componentTypeCode) {
        case COMPONENT_TYPE_VALUE.FLINK:
            version = !compVersion ? '180' : compVersion;
            break;
        case COMPONENT_TYPE_VALUE.SPARK:
            version = !compVersion ? '210' : compVersion;
            break;
        case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
        case COMPONENT_TYPE_VALUE.HIVE_SERVER: {
            version = !compVersion ? '2.x' : compVersion;
            break;
        }
        default:
            break;
    }
    return version;
}

// 是否为yarn、hdfs、Kubernetes组件
function checkUplaodFileComps (componentTypeCode: number) {
    return componentTypeCode === COMPONENT_TYPE_VALUE.YARN ||
        componentTypeCode === COMPONENT_TYPE_VALUE.KUBERNETES ||
            componentTypeCode === COMPONENT_TYPE_VALUE.HDFS;
}

function checkHaveGroupComps (componentTypeCode: number) {
    return componentTypeCode === COMPONENT_TYPE_VALUE.FLINK ||
        componentTypeCode === COMPONENT_TYPE_VALUE.SPARK ||
            componentTypeCode === COMPONENT_TYPE_VALUE.DTYARNSHELL ||
                componentTypeCode === COMPONENT_TYPE_VALUE.LEARNING;
}

function changeVersion (componentTypeCode: number, compVersion: string) {
    return (componentTypeCode === COMPONENT_TYPE_VALUE.SPARK && compVersion) ||
        (componentTypeCode === COMPONENT_TYPE_VALUE.FLINK && compVersion)
}

function versionComps (componentTypeCode: number) {
    return (componentTypeCode === COMPONENT_TYPE_VALUE.SPARK) ||
        (componentTypeCode === COMPONENT_TYPE_VALUE.FLINK)
}

// 对自定义参数的key值进行处理
function handleCustomParams (data: any) {
    let paramsArr = []
    let tmpParam: any = {};
    let configParams = _.cloneDeep(data);
    for (const key in configParams) {
        // key的数据结构为%1532398855125918-key, %1532398855125918-value
        if (key.startsWith('%')) {
            const tmpKeys = key.split('%')[1].split('-');
            const id = tmpKeys[0]; // 自定义参数的id
            const idParam = tmpKeys[1];
            if (!tmpParam[id]) tmpParam[id] = {};
            tmpParam[id][idParam] = configParams[key];
        }
    }
    for (const key in tmpParam) {
        const item = tmpParam[key];
        let params: any = {};
        params.key = item.key;
        params.value = item.value;
        params.id = key;
        paramsArr = [...paramsArr, params]
    }
    return paramsArr;
}

// 返回含有自定义参数的模板
function getCustomParams (customParams: any, componentTemplate: any) {
    let cloneLoadTemp = _.cloneDeep(componentTemplate);
    let cloneCustomParams = _.cloneDeep(customParams)
    let isGroup = false;
    cloneLoadTemp = cloneLoadTemp.filter((item: any) => !item.id);
    cloneLoadTemp.forEach((temp: any, index: any) => {
        if (temp.type === 'GROUP') {
            temp.values = temp.values.filter((item: any) => !item.id)
        }
    })
    cloneLoadTemp.forEach((temp: any) => {
        if (temp.type === 'GROUP') {
            isGroup = true;
            temp.values = Object.keys(cloneCustomParams).length === 0
                ? temp.values : temp.values.concat(handleCustomParams(cloneCustomParams[temp.key]));
        }
    })
    if (!isGroup) {
        cloneLoadTemp = Object.keys(cloneCustomParams).length === 0
            ? cloneLoadTemp : cloneLoadTemp.concat(handleCustomParams(cloneCustomParams));
    }
    // console.log('cloneLoadTemp-----dfa-----cloneLoadTemp', cloneLoadTemp)
    return cloneLoadTemp;
}

// 从模板中获取自定义参数
function getLoadTemplateParams (loadTemplate: any) {
    let params: any = [];
    loadTemplate && loadTemplate.forEach((temps: any) => {
        if (temps.id && temps.type !== 'Group') params = [...params, temps]
        if (temps.type === 'GROUP') {
            const groupParams = temps.values.filter((val: any) => val.id)
            params = [...params, { key: temps.key, groupParams }]
        }
    })
    // console.log('params===========', params)
    return params;
}

// 从模板中获取表单键值对
function getCompoentsConfigInfo (data: any) {
    let newConfigInfo: any = {};
    data.forEach((temp: any) => {
        if (temp.type === 'GROUP') {
            let groupValue: any = {}
            temp.values.forEach((t: any) => {
                groupValue[t.key] = t.value
            })
            newConfigInfo[temp.key] = groupValue;
        } else {
            newConfigInfo[temp.key] = temp.value;
        }
    })
    // console.log('newConfigInfo=========aa', newConfigInfo)
    return newConfigInfo;
}

// 检查模板里面是否有值
function checkFormHaveValue (loadTemplate: any) {
    let isHaveValue = false;
    loadTemplate.forEach((temp: any) => {
        if (temp.type === 'GROUP') {
            temp.values.forEach((val: any) => {
                if (val.value) isHaveValue = true;
            })
        } else {
            if (temp.value) isHaveValue = true;
        }
    })
    return isHaveValue;
}

// 处理上传参数
function handleBatchParams (data: any) {
    let batchParams: any = {}
    for (const key in data) {
        if (_.isObject(data[key]) && !_.isArray(data[key])) {
            let groupBatchParams: any = {}
            for (let groupKey in data[key]) {
                groupBatchParams[groupKey.split('.').join('%')] = data[key][groupKey];
            }
            batchParams[key.split('.').join('%')] = groupBatchParams;
        } else {
            batchParams[key.split('.').join('%')] = data[key];
        }
    }
    // console.log('batchParams=====sa======', batchParams)
    return batchParams;
}

// 返回模板和自定义参数键值对
function handleFormValues (formConfig: any, customParams: any, componentTypeCode: number) {
    let formValues: any = {};
    const isGroupConfig = checkHaveGroupComps(componentTypeCode);
    for (const key in formConfig) {
        if (!isGroupConfig || key === 'deploymode') {
            formValues[key.split('%').join('.')] = formConfig[key];
            if (Object.keys(customParams).length !== 0) {
                const paramsKey = handleCustomParams(customParams);
                paramsKey.forEach((p: any) => {
                    formValues[p.key] = p.value
                })
            }
        } else {
            let val: any = {}
            for (const groupKey in formConfig[key]) {
                val[groupKey.split('%').join('.')] = handleSingQuoteKeys(formConfig[key][groupKey], groupKey.split('%').join('.'))
            }
            if (Object.keys(customParams).length !== 0) {
                const paramsKey = handleCustomParams(customParams[key]);
                paramsKey.forEach((p: any) => {
                    val[p.key] = p.value
                })
            }
            formValues[key.split('%').join('.')] = val;
        }
    }
    return formValues;
}

function handleCancleParams (params: any) {
    let dealParams: any = []
    params.forEach((param: any) => {
        let p: any = {};
        if (param.key) {
            p.key = param.key;
            p.groupParams = param.groupParams.filter((groupParam: any) => Object.keys(groupParam).length > 1)
            dealParams = [...dealParams, p]
        } else {
            if (Object.keys(param).length > 1) { dealParams = [...dealParams, param] }
        }
    })
    // console.log('dealParams======sss', dealParams)
    return dealParams;
}

function handleUploadFile (fileName: string) {
    if (!fileName || typeof fileName !== 'string') return '';
    const baseFile = fileName.split('\\');
    // console.log('baseFile=======', fileName, baseFile)
    return baseFile[baseFile.length - 1];
}

// 返回组件信息
function handleCompsData (data: any) {
    let newCompConfig: any = {};
    newCompConfig.clusterName = data.data.clusterName;
    const scheduling = data.data.scheduling || [];
    scheduling.forEach((item: any) => {
        item.components.forEach((comps: any) => {
            newCompConfig[COMPONEMT_CONFIG_KEY_ENUM[comps.componentTypeCode]] = {
                configInfo: JSON.parse(comps.componentConfig) || {},
                loadTemplate: JSON.parse(comps.componentTemplate) || [],
                fileName: comps.uploadFileName || '',
                kerFileName: comps.kerberosFileName || '',
                id: comps.id || '',
                hadoopVersion: comps.hadoopVersion,
                params: getLoadTemplateParams(JSON.parse(comps.componentTemplate)),
                storeType: comps.storeType || '',
                principal: comps.principal || '',
                principals: comps.principals?.split(',') || []
            }
        })
    })
    return newCompConfig;
}

// 更新存储组件配置信息
function updateCompsConfig (components: any, componentTypeCode: number, data: any) {
    return {
        ...components,
        [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
            ...components[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
            configInfo: JSON.parse(data.data.componentConfig) || {},
            loadTemplate: JSON.parse(data.data.componentTemplate),
            id: data.data.id || '',
            hadoopVersion: data.data.hadoopVersion,
            fileName: data.data.uploadFileName || '',
            kerFileName: data.data.kerberosFileName || '',
            params: getLoadTemplateParams(JSON.parse(data.data.componentTemplate)),
            storeType: data.data.storeType || '',
            principal: data.data.principal || '',
            principals: data.data.principals?.split(',') || []
        }
    }
}

// 后端需要value值加单引号处理
function handleSingQuoteKeys (val: string, key: string) {
    const singQuoteKeys = ['c.NotebookApp.ip', 'c.NotebookApp.token', 'c.NotebookApp.default_url'];
    let newVal = val;
    singQuoteKeys.forEach(singlekey => {
        if (singlekey === key && val.indexOf("'") === -1) {
            newVal = `'${val}'`
        }
    })
    return newVal;
}

/**
 * 处理添加、更新组件数据参数
 * @values 表单变更值
 * @components 组件
 */
function getComponentConfigPrames (values: any, components: any, config: any) {
    const componentTypeCode = components.componentTypeCode;
    const { uploadFileName = {}, loadTemplate = [], kerberosFileName = {}, kerFileName = '' } = config;
    const files = uploadFileName.files && uploadFileName.files[0] ? uploadFileName.files[0] : '';
    const kerFiles = kerberosFileName.files && kerberosFileName.files[0] ? kerberosFileName.files[0] : '';
    const saveConfig = values[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]];
    const { hadoopVersion = '', storeType = '', params = {}, configInfo, principal = '', principals = [] } = saveConfig;

    const { clusterName } = values;
    const formConfig = _.cloneDeep(configInfo);
    const customParams = _.cloneDeep(params);
    let componentTemplate = _.cloneDeep(loadTemplate);
    componentTemplate.forEach((val: any) => {
        if (val.type === 'GROUP') {
            for (const groupKey in formConfig[val.key]) {
                val.values.forEach((vals: any) => {
                    if (vals.key === groupKey.split('%').join('.')) {
                        vals.value = handleSingQuoteKeys(formConfig[val.key][groupKey], vals.key)
                    }
                })
            }
        } else {
            val.value = formConfig[val.key.split('%').join('.')]
        }
    })
    componentTemplate = getCustomParams(customParams, componentTemplate)
    /**
     * 配置信息或者配置表单键值
     * formValues 表单键值
     * configInfo 组件配置信息
     */
    const formValues = handleFormValues(formConfig, customParams, componentTypeCode);
    const paramsConfig = Object.keys(formValues).length === 0 ? config.configInfo : formValues;
    // console.log('formValues------dsds------paramsConfig', formValues, config.configInfo)
    return {
        resources1: files,
        resources2: kerFiles,
        clusterName,
        storeType,
        principal,
        principals,
        componentConfig: JSON.stringify(paramsConfig),
        kerberosFileName: kerFileName,
        hadoopVersion: hadoopVersion || '',
        componentCode: componentTypeCode,
        componentTemplate: JSON.stringify(componentTemplate)
    }
}

/**
 * 比较组件是否变更
 * @param values 表单配置键值
 * @param componentConfig 备份存储的组件配置
 */
function getMoadifyComps (values: any, componentConfig: any) {
    const componentTypeCodeArr = Object.values(COMPONENT_TYPE_VALUE);
    let modifyCompsArr: any = [];

    componentTypeCodeArr.forEach((componentTypeCode: number) => {
        const formConfig = values[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {};
        if (Object.keys(formConfig).length !== 0) {
            const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {};
            const compConfigInfo = config.configInfo;
            const compHadoopVersion = config.hadoopVersion;
            const compKerberosFileName = config.kerFileName;
            const compUploadFileName = config.fileName;
            const compStoreType = config.storeType
            const compPrincipal = config.principal
            const compprincipals = config.principals
            const { configInfo = {}, params = {}, hadoopVersion = '', kerberosFileName = '', uploadFileName = '', storeType = '', principal = '', principals = [] } = formConfig;
            const formValues = handleFormValues(configInfo, params, componentTypeCode);
            const isUploadFileComps = checkUplaodFileComps(Number(componentTypeCode))

            const isModify = (hadoopVersion && !_.isEqual(compHadoopVersion, hadoopVersion)) ||
                (uploadFileName && !_.isEqual(compUploadFileName, handleUploadFile(uploadFileName))) ||
                    (kerberosFileName && !_.isEqual(compKerberosFileName, handleUploadFile(kerberosFileName))) ||
                        (storeType && !_.isEqual(compStoreType, storeType)) ||
                            (principal && !_.isEqual(compPrincipal, principal)) ||
                                (principals && !_.isEqual(compprincipals, principals))
            if (!config.id) { modifyCompsArr = [...modifyCompsArr, componentTypeCode]; return; }
            if (isModify) { modifyCompsArr = [...modifyCompsArr, componentTypeCode]; return; }
            if (!_.isEqual(compConfigInfo, formValues) && !isUploadFileComps) {
                modifyCompsArr = [...modifyCompsArr, componentTypeCode];
            }
            // console.log('compConfigInfo===========formValues', compConfigInfo, formValues)
        }
    })
    return modifyCompsArr;
}

export default {
    getActionType,
    getCompsVersion,
    versionComps,
    updateCompsConfig,
    checkUplaodFileComps,
    changeVersion,
    handleCompsData,
    handleCustomParams,
    getComponentConfigPrames,
    getLoadTemplateParams,
    getCompoentsConfigInfo,
    checkFormHaveValue,
    handleBatchParams,
    handleFormValues,
    getMoadifyComps,
    handleCancleParams
}
