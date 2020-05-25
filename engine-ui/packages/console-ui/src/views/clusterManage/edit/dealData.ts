import _ from 'lodash';
import { COMPONEMT_CONFIG_KEY_ENUM, COMPONENT_TYPE_VALUE } from '../../../consts';

// 设置版本默认值
function getCompsVersion (componentTypeCode: number, compVersion: string) {
    let version: any = '';
    if (componentTypeCode === COMPONENT_TYPE_VALUE.FLINK) {
        version = compVersion === '' ? '180' : compVersion;
    }
    if (componentTypeCode === COMPONENT_TYPE_VALUE.SPARK) {
        version = compVersion === '' ? '2.1.x' : compVersion;
    }
    return version;
}

// 是否为yarn、hdfs、Kubernetes组件
function checkUplaodFileComps (componentTypeCode: number) {
    return componentTypeCode === COMPONENT_TYPE_VALUE.YARN ||
        componentTypeCode === COMPONENT_TYPE_VALUE.KUBERNETES || componentTypeCode === COMPONENT_TYPE_VALUE.HDFS;
}

// 返回组件信息
function handleCompsData (data: any) {
    let newCompConfig: any = {};
    newCompConfig.clusterName = data.data.clusterName;
    data.data.scheduling.map((item: any) => {
        item.components.map((comps: any) => {
            newCompConfig[COMPONEMT_CONFIG_KEY_ENUM[comps.componentTypeCode]] = {
                configInfo: JSON.parse(comps.componentConfig) || {},
                loadTemplate: JSON.parse(comps.componentTemplate) || [],
                fileName: comps.uploadFileName || '',
                kerFileName: comps.kerberosFileName || '',
                id: comps.id || '',
                hadoopVersion: comps.hadoopVersion,
                params: getLoadTemplateParams(JSON.parse(comps.componentTemplate))
            }
        })
    })
    return newCompConfig;
}

// 对自定义参数的key值进行处理
function handleCustomParams (data: any) {
    let paramsArr = []
    let tmpParam: any = {};
    let configParams = _.cloneDeep(data);
    // console.log('configParams---------tempkey', configParams)
    for (let key in configParams) {
        // key的数据结构为%1532398855125918-key, %1532398855125918-value
        if (key.startsWith('%')) {
            let tmpKeys = key.split('%')[1].split('-');
            let id = tmpKeys[0]; // 自定义参数的id
            let idParam = tmpKeys[1];
            if (!tmpParam[id]) {
                tmpParam[id] = {};
            }
            tmpParam[id][idParam] = configParams[key];
        }
    }
    for (let key in tmpParam) {
        let params: any = {};
        let item = tmpParam[key];
        params.key = item.key;
        params.value = item.value;
        params.id = key;
        paramsArr.push(params);
    }
    return paramsArr;
}

/**
 * 处理添加、更新组件数据参数
 * @values 表单变更值
 * @components 组件
 */
function getComponentConfigPrames (values: any, components: any, config: any) {
    const componentTypeCode = components.componentTypeCode;
    // 组件配置相关 配置文件、组件id、组件模板
    const { uploadFileName = {}, loadTemplate = [], kerberosFileName = {}, kerFileName = '' } = config;
    const files = uploadFileName.files && uploadFileName.files[0] ? uploadFileName.files[0] : '';
    const kerFiles = kerberosFileName.files && kerberosFileName.files[0] ? kerberosFileName.files[0] : '';

    // 各组件表单对应更改值
    let saveConfig = values[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]];
    const { hadoopVersion = '', params = {}, configInfo } = saveConfig;
    const { clusterName } = values;
    const formConfig = _.cloneDeep(configInfo);
    const customParams = _.cloneDeep(params);
    let componentTemplate = _.cloneDeep(loadTemplate);

    // 返回模板信息以及相关输入值
    componentTemplate.forEach((val: any) => {
        // console.log('val-----formConfig', val)
        if (val.type === 'GROUP') {
            for (let groupKey in formConfig[val.key]) {
                val.values.forEach((vals: any) => {
                    if (vals.key === groupKey.split('%').join('.')) vals.value = formConfig[val.key][groupKey];
                })
            }
        } else {
            val.value = formConfig[val.key.split('%').join('.')]
        }
    })
    componentTemplate = getCustomParams(customParams, componentTemplate)
    // console.log('componentTemplate=====ddd======hadoopVersion', componentTemplate, hadoopVersion)
    /**
     * 配置信息或者配置表单键值
     * formValues 表单键值
     * configInfo 组件配置信息
     */
    const formValues = handleFormValues(formConfig, customParams, componentTypeCode);
    const paramsConfig = Object.keys(formValues).length === 0 ? config.configInfo : formValues;
    // console.log('formValues------dsds------paramsConfig', formValues, paramsConfig)
    return {
        resources1: files,
        resources2: kerFiles,
        clusterName: clusterName,
        componentConfig: JSON.stringify({ ...paramsConfig }),
        kerberosFileName: kerFileName,
        hadoopVersion: hadoopVersion || '',
        componentCode: componentTypeCode,
        componentTemplate: JSON.stringify(componentTemplate)
    }
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
            temp.values = Object.keys(cloneCustomParams).length === 0 ? temp.values : temp.values.concat(handleCustomParams(cloneCustomParams[temp.key]));
        }
    })
    if (!isGroup) {
        cloneLoadTemp = Object.keys(cloneCustomParams).length === 0 ? cloneLoadTemp : cloneLoadTemp.concat(handleCustomParams(cloneCustomParams))
    }
    // console.log('cloneLoadTemp-----dfa-----cloneLoadTemp', cloneLoadTemp)
    return cloneLoadTemp;
}

// 从模板中获取自定义参数
function getLoadTemplateParams (loadTemplate: any) {
    let params: any = [];
    loadTemplate.map((temps: any) => {
        if (temps.id && temps.type !== 'Group') params.push(temps)
        if (temps.type === 'GROUP') {
            let groupParams: any = [];
            temps.values.map((val: any) => {
                if (val.id) groupParams.push(val)
            })
            params.push({ key: temps.key, groupParams })
        }
    })
    // console.log('params===========', params)
    return params;
}

// 从模板中获取表单键值对
function setCompoentsConfigInfo (data: any) {
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
    loadTemplate.map((temp: any) => {
        if (temp.type === 'GROUP') {
            temp.values.map((val: any) => {
                if (val.value) isHaveValue = true;
            })
        } else {
            if (temp.value) isHaveValue = true;
        }
    })
    return isHaveValue;
}

function handleBatchParams (data: any) {
    let batchParams: any = {}
    for (let key in data) {
        if (_.isObject(data[key])) {
            let groupBatchParams: any = {}
            for (let groupKey in data[key]) {
                groupBatchParams[groupKey.split('.').join('%')] = data[key][groupKey];
            }
            batchParams[key.split('.').join('%')] = groupBatchParams;
        } else {
            batchParams[key.split('.').join('%')] = data[key];
        }
    }
    console.log('batchParams=====sa======', batchParams)
    return batchParams;
}

// 返回模板和自定义参数键值对
function handleFormValues (formConfig: any, customParams: any, componentTypeCode: number) {
    let formValues: any = {};
    const isGroupConfig = componentTypeCode === COMPONENT_TYPE_VALUE.FLINK || componentTypeCode === COMPONENT_TYPE_VALUE.SPARK || componentTypeCode === COMPONENT_TYPE_VALUE.DTYARNSHELL
    for (let key in formConfig) {
        if (!isGroupConfig || key === 'deploymode') {
            formValues[key.split('%').join('.')] = formConfig[key];
            if (Object.keys(customParams).length !== 0) {
                const paramsKey = handleCustomParams(customParams);
                paramsKey.map((p: any) => {
                    formValues[p.key] = p.value
                })
            }
        } else {
            let val: any = {}
            for (let groupKey in formConfig[key]) {
                val[groupKey.split('%').join('.')] = formConfig[key][groupKey]
            }
            if (Object.keys(customParams).length !== 0) {
                const paramsKey = handleCustomParams(customParams[key]);
                paramsKey.map((p: any) => {
                    val[p.key] = p.value
                })
            }
            formValues[key.split('%').join('.')] = val;
        }
    }
    return formValues;
}

/**
 * 比较组件是否变更
 * @param values 表单配置键值
 * @param componentConfig 存储的组件配置
 */
function getMoadifyComps (values: any, componentConfig: any) {
    const componentTypeCodeArr = Object.values(COMPONENT_TYPE_VALUE);
    let modifyCompsArr: any = [];
    componentTypeCodeArr.map((componentTypeCode: any) => {
        const formConfig = values[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {};
        if (Object.keys(formConfig).length !== 0) {
            const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {};
            const compConfigInfo = config.configInfo;
            const compHadoopVersion = config.hadoopVersion;
            const compKerberosFileName = config.kerFileName;
            const compUploadFileName = config.fileName;
            const { configInfo = {}, params = {}, hadoopVersion = '', kerberosFileName = '', uploadFileName = '' } = formConfig;
            const formValues = handleFormValues(configInfo, params, componentTypeCode);
            const isUploadFileComps = checkUplaodFileComps(Number(componentTypeCode))
            const isModify = (hadoopVersion && !_.isEqual(compHadoopVersion, hadoopVersion)) || (uploadFileName && !_.isEqual(compUploadFileName, uploadFileName)) ||
                    (kerberosFileName && !_.isEqual(kerberosFileName, compKerberosFileName))
            if (isModify) { modifyCompsArr.push(componentTypeCode) }
            if (!_.isEqual(compConfigInfo, formValues) && !isModify && !isUploadFileComps) {
                modifyCompsArr.push(componentTypeCode)
            }
            // console.log('isModify==========isUploadFileComps', isModify, isUploadFileComps)
            // console.log('kerberosFileName========compKerberosFileName', kerberosFileName, compKerberosFileName)
            // console.log('compConfigInfo========formValues', kerberosFileName, compConfigInfo, formValues)
        }
    })
    console.log('modifyCompsArr========', modifyCompsArr)
    return modifyCompsArr;
}

function handleCancleParams (params: any) {
    let dealParams: any = []
    params.map((param: any) => {
        let p: any = {};
        if (param.key) {
            p.key = param.key;
            p.groupParams = param.groupParams.filter((groupParam: any) => Object.keys(groupParam).length > 1)
            dealParams.push(p)
        } else {
            if (Object.keys(param).length > 1) {
                dealParams.push(param)
            }
        }
    })
    // console.log('dealParams======sss', dealParams)
    return dealParams;
}

export default {
    getCompsVersion,
    checkUplaodFileComps,
    handleCompsData,
    handleCustomParams,
    getComponentConfigPrames,
    getLoadTemplateParams,
    setCompoentsConfigInfo,
    checkFormHaveValue,
    handleBatchParams,
    handleFormValues,
    getMoadifyComps,
    handleCancleParams
}
