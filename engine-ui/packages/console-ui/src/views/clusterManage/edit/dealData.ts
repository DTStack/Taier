import _ from 'lodash';
import { COMPONEMT_CONFIG_KEY_ENUM, COMPONENT_TYPE_VALUE } from '../../../consts';

function dealData (data: any) {
    let newData: any = _.cloneDeep(data)
    newData.engines = _.map(data.engines, (item) => {
        let newItem = _.cloneDeep(item);
        newItem.components = _.map(item.components, (com) => {
            let newCom = _.cloneDeep(com);
            if (com.componentName === 'YARN' || com.componentName === 'HDFS') {
                delete newCom.config.openKerberos;
                delete newCom.config.kerberosFile;
            }
            delete newCom.config.kerberosConf;
            return newCom;
        })
        return newItem;
    });
    return newData;
}

function getKerberosObj (data: any, componentName: any) { // 需要返回{openKerberos: true, kerberosConf: kerberosConf}
    // console.log(data, componentName)
    let kerberosObj: any = {};
    const hadoopEngine = _.find(data.engines, (item) => {
        return item.engineName === 'Hadoop'
    });
    const targeComponent = _.find(hadoopEngine.components, (com) => {
        return componentName === com.componentName
    });
    let config = targeComponent.config;
    // console.log(targeComponent)
    if (componentName === 'YARN' || componentName === 'HDFS') {
        if (config && config.openKerberos != null) {
            kerberosObj.openKerberos = config.openKerberos;
        }
    }
    if (config && config.kerberosConf) {
        kerberosObj.kerberosConf = config.kerberosConf;
    }

    return kerberosObj;
}

function getCustomParams (data: any) {
    let paramsArr = []
    let tmpParam: any = {};
    for (let key in data) {
        // key的数据结构为%1532398855125918-key, %1532398855125918-value
        if (key.startsWith('%')) {
            let tmpKeys = key.split('%')[1].split('-');
            let id = tmpKeys[0]; // 自定义参数的id
            let idParam = tmpKeys[1];
            if (!tmpParam[id]) {
                tmpParam[id] = {};
            }
            tmpParam[id][idParam] = data[key];
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

function getLoadTemplateParams (loadTemplate: any = []) {
    let params: any = [];
    loadTemplate.map((temp: any) => {
        if (temp.id) { params.push(temp) }
    })
    return params;
}

function getLoadTemplates (loadTemplate: any = []) {
    let params: any = [];
    loadTemplate.map((temp: any) => {
        if (!temp.id) { params.push(temp) }
    })
    return params;
}

/**
 * 处理添加、更新组件数据参数
 * @values 表单变更值
 * @components 组件
 */
function getComponentConfigPrames (values: any, components: any, config: any) {
    const componentTypeCode = components.componentTypeCode;
    // 组件配置相关 配置文件、组件id、组件模板、
    const {
        uploadFileName = {}, configInfo = {}, loadTemplate = [], kerberosFileName = {},
        kerFileName = '' } = config;
    const files = uploadFileName.files && uploadFileName.files[0] ? uploadFileName.files[0] : '';
    const kerFiles = kerberosFileName.files && kerberosFileName.files[0] ? kerberosFileName.files[0] : '';
    console.log('files-------------kerFiles', files, kerFiles)
    // 各组件表单对应更改值
    let saveConfig = values[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]];
    const { hadoopVersion } = saveConfig;
    const { clusterName } = values;
    // const customParams = dealData.getCustomParams(params);
    const formConfig = _.cloneDeep(saveConfig.configInfo);

    // 返回模板信息以及相关输入值
    let componentTemplate = _.cloneDeep(loadTemplate)
    if (componentTypeCode === COMPONENT_TYPE_VALUE.FLINK) {
        componentTemplate.forEach((val: any) => {
            // console.log('val-----formConfig', val)
            if (val.key !== 'deploymode') {
                for (let groupKey in formConfig[val.key]) {
                    val.values.forEach((vals: any) => {
                        if (vals.key === groupKey.split('%').join('.')) vals.value = formConfig[val.key][groupKey];
                    })
                }
            } else {
                val.value = formConfig[val.key]
            }
        })
    } else {
        componentTemplate.forEach((item: any) => {
            if (saveConfig.configInfo[item.key]) {
                item.value = saveConfig.configInfo[item.key]
            }
        })
    }
    console.log('componentTemplate-------componentTypeCode----saveConfig', componentTemplate, componentTypeCode, saveConfig)

    /**
     * 配置信息或者配置表单键值
     * saveConfig.configInfo 表单键值
     * configInfo 组件配置信息
     */
    // let formValues = cloneDeep(saveConfig.configInfo)
    let formValues = _.cloneDeep(saveConfig.configInfo);
    if (componentTypeCode === COMPONENT_TYPE_VALUE.FLINK) {
        for (let key in formConfig) {
            if (key !== 'deploymode') {
                for (let groupKey in formConfig[key]) {
                    formValues[key][groupKey.split('%').join('.')] = formConfig[key][groupKey]
                    delete formValues[key][groupKey]
                }
            }
        }
    }
    // console.log('formConfig------------', formConfig)
    const paramsConfig = formValues || configInfo;
    return {
        resources1: files,
        resources2: kerFiles,
        clusterName: clusterName,
        componentConfig: JSON.stringify({ ...paramsConfig }),
        kerberosFileName: kerFileName,
        hadoopVersion: hadoopVersion,
        componentCode: componentTypeCode,
        componentTemplate: JSON.stringify(componentTemplate)
    }
}

export default {
    dealData,
    getKerberosObj,
    getCustomParams,
    getLoadTemplateParams,
    getLoadTemplates,
    getComponentConfigPrames
}
