import { TABS_TITLE_KEY, COMPONENT_TYPE_VALUE } from './const'

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
