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

export default {
    dealData,
    getKerberosObj
}
