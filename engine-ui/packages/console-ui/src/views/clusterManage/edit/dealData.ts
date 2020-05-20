import _ from 'lodash';

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

export default {
    dealData,
    getKerberosObj,
    getCustomParams,
    getLoadTemplateParams,
    getLoadTemplates
}
