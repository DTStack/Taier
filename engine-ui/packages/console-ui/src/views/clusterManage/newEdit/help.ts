import _ from 'lodash';
import { TABS_TITLE_KEY, COMPONENT_TYPE_VALUE, CONFIG_ITEM_TYPE, FILE_TYPE } from './const'

// 是否为yarn、hdfs、Kubernetes组件
export function isNeedTemp (typeCode: number): boolean {
    return [COMPONENT_TYPE_VALUE.YARN, COMPONENT_TYPE_VALUE.HDFS,
        COMPONENT_TYPE_VALUE.KUBERNETES].indexOf(typeCode) > -1
}

export function isKubernetes (typeCode: number): boolean {
    return COMPONENT_TYPE_VALUE.KUBERNETES == typeCode
}

export function isHaveGroup (typeCode: number): boolean {
    return [COMPONENT_TYPE_VALUE.FLINK, COMPONENT_TYPE_VALUE.SPARK,
        COMPONENT_TYPE_VALUE.LEARNING, COMPONENT_TYPE_VALUE.DTYARNSHELL].indexOf(typeCode) > -1
}

export function notCustomParam (typeCode: number): boolean {
    return [COMPONENT_TYPE_VALUE.SFTP, COMPONENT_TYPE_VALUE.LIBRA_SQL,
        COMPONENT_TYPE_VALUE.ORACLE_SQL, COMPONENT_TYPE_VALUE.TIDB_SQL,
        COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL, COMPONENT_TYPE_VALUE.IMPALA_SQL,
        COMPONENT_TYPE_VALUE.PRESTO_SQL].indexOf(typeCode) > -1
}

export function isOtherVersion (code: number): boolean {
    return [COMPONENT_TYPE_VALUE.FLINK, COMPONENT_TYPE_VALUE.SPARK,
        COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER, COMPONENT_TYPE_VALUE.HIVE_SERVER].indexOf(code) > -1
}

export function isSameVersion (code: number): boolean {
    return [COMPONENT_TYPE_VALUE.HDFS, COMPONENT_TYPE_VALUE.YARN].indexOf(code) > -1
}

export function isMulitiVersion (code: number): boolean {
    return [COMPONENT_TYPE_VALUE.FLINK, COMPONENT_TYPE_VALUE.SPARK].indexOf(code) > -1
}

export function needZipFile (type: number): boolean {
    return [FILE_TYPE.KERNEROS, FILE_TYPE.CONFIGS].indexOf(type) > -1
}

export function getActionType (mode: string): string {
    switch (mode) {
        case 'view': return '查看集群'
        case 'new': return '新增集群'
        case 'edit': return '编辑集群'
        default: return ''
    }
}

export function isSourceTab (activeKey: number): boolean {
    return activeKey == TABS_TITLE_KEY.SOURCE
}

export function initialScheduling (): any[] {
    let arr = []
    return Object.values(TABS_TITLE_KEY).map((tabKey: number) => {
        return arr[tabKey] = []
    })
}

export function giveMeAKey (): string {
    return (new Date().getTime() + '' + ~~(Math.random() * 100000))
}

export function isViewMode (mode: string): boolean {
    return mode == 'view'
}

export function isFileParam (key: string): boolean {
    return ['kerberosFileName', 'uploadFileName'].indexOf(key) > -1
}

export function isDeployMode (key: string): boolean {
    return key === 'deploymode'
}

export function isRadioLinkage (type: string): boolean {
    return type === CONFIG_ITEM_TYPE.RADIO_LINKAGE
}

export function isGroupType (type: string): boolean {
    return type === CONFIG_ITEM_TYPE.GROUP
}

export function isCustomType (type: string): boolean {
    return type === CONFIG_ITEM_TYPE.CUSTOM_CONTROL
}

// 模版中存在id则为自定义参数
export function getCustomerParams (temps: any): any[] {
    return temps.filter(temp => isCustomType(temp.type))
}

export function getCompsId (currentComps: any[], typeCodes: any[]): any[] {
    let ids = []
    currentComps.forEach(comp => {
        if (typeCodes.indexOf(comp?.componentTypeCode) > -1 && comp?.id) {
            ids.push(comp.id)
        }
    })
    return ids
}

export function getValueByJson (value: any): any {
    return value ? JSON.parse(value) : null
}

export function getOptions (version: any[]): any[] {
    let opt = []
    version.forEach((ver: any, index: number) => {
        opt[index] = { label: ver.key, value: ver.key }
        if (ver?.values && ver?.values?.length > 0) {
            opt[index] = {
                ...opt[index],
                children: getOptions(ver.values)
            }
        }
    })
    return opt
}

export function getInitialValue (version: any[], commVersion: string): any[] {
    const parentNode = {}
    function setParentNode (nodes: any[], parent?: any) {
        if (!nodes) return
        return nodes.map(data => {
            const node = { value: data.key, parent }
            parentNode[data.key] = node
            setParentNode(data.values, node)
        })
    }

    function getParentNode (value: string) {
        let node = []
        let currentNode = parentNode[value]
        node.push(currentNode.value)
        if (currentNode.parent) {
            node = [...getParentNode(currentNode.parent.value), ...node]
        }
        return node
    }
    setParentNode(version)
    return getParentNode(commVersion)
}

/**
 * @param param
 * 处理单条自定义参数的key\value值
 * 处理数据结构为%1532398855125918-key, %1532398855125918-value
 * 返回数据结构[{key: key, value: value, id: id}]
 */
function handleSingleParam (params: any) {
    let customParamArr = []
    let customParamConfig = []
    if (!params) return {}
    for (let [keys, values] of Object.entries(params)) {
        if (values && _.isString(values) && _.isString(keys)) {
            let p = keys.split('%')[1].split('-')
            customParamArr[p[0]] = { ...customParamArr[p[0]], [p[1]]: values }
        }
    }
    for (let key in customParamArr) {
        let config: any = {}
        config.key = customParamArr[key].key
        config.value = customParamArr[key].value
        // config.id = key
        config.type = CONFIG_ITEM_TYPE.CUSTOM_CONTROL
        customParamConfig.push(config)
    }
    return customParamConfig
}

/**
 * @param param 自定义参数对象
 * @param turnp 转化为{key:value}型数据，仅支持不含group类型组组件
 * 先组内处理自定义参数再处理普通类型的自定义参数
 * 返回数据结构
 * [{
 *  group: {
 *    key: key,
 *    value: value
 *  }
 * }]
 */
export function handleCustomParam (params: any, turnp?: boolean): any {
    let customParam: any = []
    if (!params) return {}
    for (let [key, value] of Object.entries(params)) {
        if (value && !_.isString(value)) {
            customParam = [
                ...customParam,
                { [key]: handleSingleParam(value) }
            ]
        }
    }
    if (turnp) {
        let config = {}
        for (let item of customParam.concat(handleSingleParam(params))) {
            config[item.key] = item.value
        }
        return config
    }
    return customParam.concat(handleSingleParam(params))
}

/**
 * @param temp 初始模版值
 * 处理初始模版值返回只包含自定义参数的键值
 * 返回结构如下
 * {
 *   %1532398855125918-key: key,
 *   %1532398855125918-value: value,
 *     group: {
 *        %1532398855125918-key: key,
 *        %1532398855125918-value: value,
 *     }
 * }
 */
export function getParamsByTemp (temp: any[]): any {
    let batchParams: any = {};
    (isDeployMode(temp[0]?.key)
        ? temp[0].values : temp).forEach((item: any) => {
        if (item.type == CONFIG_ITEM_TYPE.GROUP) {
            let params = {}
            item.values.forEach((groupItem: any) => {
                if (groupItem.id) {
                    params['%' + groupItem.id + '-key'] = groupItem?.key ?? ''
                    params['%' + groupItem.id + '-value'] = groupItem?.value ?? ''
                }
            })
            batchParams[item.key] = params
        }
        if (isCustomType(item.type)) {
            batchParams['%' + item.id + '-key'] = item?.key ?? ''
            batchParams['%' + item.id + '-value'] = item?.value ?? ''
        }
    })
    return batchParams
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
 * @param
 * comp 表单组件值
 * initialCompData 初始表单组件值
 * componentTemplate用于表单回显值需要包含表单对应的value和并自定义参数
 */
export function handleComponentTemplate (comp: any, initialCompData: any): any {
    /** 外层数据先删除一层自定义参数 */
    let newComponentTemplate = JSON.parse(initialCompData.componentTemplate).filter(v =>
        v.type !== CONFIG_ITEM_TYPE.CUSTOM_CONTROL)
    const componentConfig = handleComponentConfig(comp)
    const customParamConfig = handleCustomParam(comp.customParam)
    let isGroup = false

    // componentTemplate 存入 componentConfig 对应值
    for (let [key, values] of Object.entries(componentConfig)) {
        if (!_.isString(values) && !_.isArray(values)) {
            for (let [groupKey, value] of Object.entries(values)) {
                (isDeployMode(newComponentTemplate[0].key)
                    ? newComponentTemplate[0].values : newComponentTemplate).map(temps => {
                    if (temps.key == key) {
                        temps.values = temps.values.filter(temp =>
                            temp.type !== CONFIG_ITEM_TYPE.CUSTOM_CONTROL)
                        temps.values.map(temp => {
                            if (temp.key == groupKey) {
                                temp.value = value
                            }
                        })
                    }
                })
            }
        } else {
            newComponentTemplate.map(temps => {
                if (temps.key == key) {
                    temps.value = values
                } else if (isRadioLinkage(temps.type)) {
                    temps.values.map(temp => {
                        if (temp.values[0].key == key) temp.values[0].value = values
                    })
                }
            })
        }
    }

    if (Object.values(customParamConfig).length == 0) {
        return newComponentTemplate
    }

    // 和并自定义参数
    for (let config in customParamConfig) {
        if (!customParamConfig[config]?.type) {
            isGroup = true
            for (let [key, value] of Object.entries(customParamConfig[config])) {
                (isDeployMode(newComponentTemplate[0].key)
                    ? newComponentTemplate[0].values : newComponentTemplate).map(temp => {
                    if (temp.key == key && temp.type == CONFIG_ITEM_TYPE.GROUP) {
                        temp.values = temp.values.concat(value)
                    }
                })
            }
        }
    }
    if (!isGroup) return newComponentTemplate.concat(customParamConfig)
    return newComponentTemplate
}

/**
 * @param comp
 * @param turnp 格式 => 为tue时对应componentConfig格式为{%-key:value}
 * 返回componentConfig
 */
export function handleComponentConfig (comp: any, turnp?: boolean): any {
    // 处理componentConfig
    let componentConfig = {}
    for (let [key, values] of Object.entries(comp.componentConfig)) {
        componentConfig[key] = values
        if (!_.isString(values) && !_.isArray(values)) {
            let groupConfig = {}
            for (let [groupKey, value] of Object.entries(values)) {
                if (turnp) {
                    groupConfig[groupKey.split('.').join('%')] = value
                } else {
                    groupConfig[groupKey.split('%').join('.')] = handleSingQuoteKeys(value, groupKey.split('%').join('.'))
                }
            }
            componentConfig[key] = groupConfig
        }
    }
    return componentConfig
}

/**
 * @param comp
 * @param typeCode
 * 返回包含自定义参数的componentConfig
 * typeCode识别是否有组类别
 */
export function handleComponentConfigAndCustom (comp: any, typeCode: number): any {
    // 处理componentConfig
    let componentConfig = handleComponentConfig(comp)

    // 自定义参数和componentConfig和并
    let customParamConfig = handleCustomParam(comp.customParam)
    if (isHaveGroup(typeCode) && customParamConfig.length) {
        for (let config of customParamConfig) {
            for (let key in config) {
                for (let groupConfig of config[key]) {
                    componentConfig[key] = {
                        ...componentConfig[key],
                        [groupConfig.key]: groupConfig.value
                    }
                }
            }
        }
    }
    if (!isHaveGroup(typeCode) && Object.values(customParamConfig).length) {
        for (let item of customParamConfig) {
            componentConfig = {
                ...componentConfig,
                [item.key]: item.value
            }
        }
    }
    return componentConfig
}

export function getInitialComp (initialCompDataArr: any[], typeCode: number): any {
    let initialCompData = {}
    for (let comps of initialCompDataArr) {
        for (let item of comps) {
            if (item.componentTypeCode == typeCode) {
                initialCompData = item
            }
        }
    }
    return initialCompData
}

/**
 * @param comps 已渲染各组件表单值
 * @param initialCompData 各组件初始值
 *
 * 通过比对表单值和初始值对比是否变更
 * 返回含有组件code数组
 *
 */
export function getModifyComp (comps: any, initialCompData: any[]): any {
    /**
    * 基本参数对比
    * 文件对比，只比较文件名称
    */
    const defaulParams = ['storeType', 'principal', 'hadoopVersion', 'kerberosFileName',
        'uploadFileName']
    let modifyComps = new Set()
    for (let [typeCode, comp] of Object.entries(comps)) {
        const initialComp = getInitialComp(initialCompData, Number(typeCode))
        for (let param of defaulParams) {
            let compValue = comp[param]
            if (isFileParam(param)) {
                compValue = comp[param]?.name ?? comp[param]
            }
            if (compValue && !_.isEqual(compValue, initialComp[param]?.name ?? initialComp[param])) {
                modifyComps.add(typeCode)
            }
        }
        /**
         * 除 hdfs、yarn、kerberos组件
         * 对比之前先处理一遍表单的数据和自定义参数, 获取含有自定义参数的componentConfig
         */
        if (!isNeedTemp(Number(typeCode))) {
            const compConfig = handleComponentConfigAndCustom(comp, Number(typeCode))
            if (!_.isEqual(compConfig, initialComp?.componentConfig ? JSON.parse(initialComp.componentConfig) : {})) {
                modifyComps.add(typeCode)
            }
        } else {
            /** 比对 hdfs、yarn 自定义参数 */
            const compTemp = comp['customParam'] ? handleSingleParam(comp['customParam']) : []
            const initialTemp = getCustomerParams(getValueByJson(initialComp?.componentTemplate) ?? [])
            if (!_.isEqual(compTemp, initialTemp)) {
                modifyComps.add(typeCode)
            }
        }
    }
    return modifyComps
}
