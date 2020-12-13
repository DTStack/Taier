import _ from 'lodash';
import { TABS_TITLE_KEY, COMPONENT_TYPE_VALUE, CONFIG_ITEM_TYPE } from './const'

// 是否为yarn、hdfs、Kubernetes组件
export function isNeedTemp (typeCode: number): boolean {
    return [COMPONENT_TYPE_VALUE.YARN, COMPONENT_TYPE_VALUE.HDFS,
        COMPONENT_TYPE_VALUE.KUBERNETES].indexOf(typeCode) > -1
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

export function getFileDesc (typeCode: number): string {
    switch (typeCode) {
        case COMPONENT_TYPE_VALUE.YARN:
            return 'zip格式，至少包括yarn-site.xml'
        case COMPONENT_TYPE_VALUE.HDFS:
            return 'zip格式，至少包括core-site.xml、hdfs-site.xml、hive-site.xml'
        case COMPONENT_TYPE_VALUE.KUBERNETES:
            return 'zip格式，至少包括kubernetes.config'
        default:
            return null
    }
}

export function isViewMode (mode: string): boolean {
    return mode == 'view'
}

// 模版中存在id则为自定义参数
export function getCustomerParams (temps: any): any[] {
    return temps.filter(temp => temp.id)
}

export function isFileParam (key: string): boolean {
    return ['kerberosFileName', 'uploadFileName'].indexOf(key) > -1
}

/**
 * @param param
 * 处理单条自定义参数的key\value值
 * 数据结构为%1532398855125918-key, %1532398855125918-value
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
        // customParamConfig[customParamArr[key].key] = customParamArr[key].value
        let config: any = {}
        config.key = customParamArr[key].key
        config.value = customParamArr[key].value
        config.id = key
        customParamConfig.push(config)
    }
    return customParamConfig
}

/**
 * @param param 自定义参数对象
 * 先组内处理自定义参数再处理普通类型的自定义参数
 */
export function handleCustomParam (params: any): any {
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
    return customParam.concat(handleSingleParam(params))
}
/**
 * @param comp 表单组件值
 * 自定义参数和componentTemplate和并, 表单回显值
 */
export function handleComponentTemplate (comp: any, initialCompData: any): any {
    let newComponentTemplate = JSON.parse(initialCompData.componentTemplate).filter(v => !v.id)
    const customParamConfig = handleCustomParam(comp.customParam)
    let isGroup = false
    for (let key in customParamConfig) {
        if (!customParamConfig[key]?.id) {
            isGroup = true
            newComponentTemplate.map(temp => {
                if (temp.key == key && temp.type == CONFIG_ITEM_TYPE.GROUP) {
                    return temp.values.concat(customParamConfig[key])
                }
                return temp
            })
        }
    }
    // console.log('newComponentTemplate === ', newComponentTemplate.concat(customParamConfig), customParamConfig)
    if (!isGroup) return newComponentTemplate.concat(customParamConfig)
    return newComponentTemplate
}

/**
 * @param comp
 * 返回包含自定义参数的componentConfig
 */
export function handleComponentConfig (comp: any): any {
    // 处理componentConfig
    let componentConfig = {}
    for (let [key, values] of Object.entries(comp.componentConfig)) {
        componentConfig[key] = values
        if (!_.isString(values) && !_.isArray(values)) {
            let groupConfig = {}
            for (let [groupKey, value] of Object.entries(values)) {
                groupConfig[groupKey.split('%').join('.')] = value
            }
            componentConfig[key] = groupConfig
        }
    }

    // 自定义参数和componentConfig和并
    let customParamConfig = handleCustomParam(comp.customParam)
    let isGroup = false
    for (let key in customParamConfig) {
        if (_.isArray(customParamConfig[key])) {
            isGroup = true
            for (let item of customParamConfig[key]) {
                componentConfig[key] = {
                    ...componentConfig[key],
                    [item.key]: item.value
                }
            }
        }
    }
    if (!isGroup && Object.values(customParamConfig).length) {
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
 *
 */
export function getModifyComp (comps: any, initialCompData: any[]): any {
    /**
    * 基本参数对比
    * 文件对比，只比较文件名称
    */
    const defaulParams = ['storeType', 'principal', 'hadoopVersion', 'kerberosFileName', 'uploadFileName']
    let modifyComps = new Set()
    for (let [typeCode, comp] of Object.entries(comps)) {
        const initialComp = getInitialComp(initialCompData, Number(typeCode))
        for (let param of defaulParams) {
            let compValue = comp[param]
            if (isFileParam(param)) {
                compValue = comp[param]?.name ?? comp[param]
            }
            if (compValue && !_.isEqual(compValue, initialComp[param])) {
                modifyComps.add(typeCode)
            }
        }
        /**
         * 对比之前先处理一遍表单的数据和自定义参数
         */
        const compConfig = handleComponentConfig(comp)
        if (!_.isEqual(compConfig, JSON.parse(initialComp.componentConfig))) {
            modifyComps.add(typeCode)
        }
    }
    // console.log('modifyComps ==== ', modifyComps)
    return modifyComps
}
