import { isEqual } from 'lodash'
import { handleComponentConfigAndCustom, handleComponentTemplate,
    handleComponentConfig } from '../../clusterManage/newEdit/help'

export const formItemLayout: any = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 9 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 9 }
    }
}

export function getConfig (comp: any, preComp: any): any {
    const { componentTypeCode } = preComp
    return handleComponentConfigAndCustom(comp, componentTypeCode)
}

export function getTemplate (comp: any, preComp: any): any {
    return handleComponentTemplate(comp, preComp)
}

export function getInitailConfig (comp: any): any {
    return handleComponentConfig({
        componentConfig: JSON.parse(comp.componentConfig)
    }, true)
}

export function validateConfig (comp: any, preComp: any): boolean {
    const { componentConfig } = preComp
    const wrrapConfig = getConfig(comp, preComp)
    if (!isEqual(wrrapConfig, JSON.parse(componentConfig))) return false
    return true
}
