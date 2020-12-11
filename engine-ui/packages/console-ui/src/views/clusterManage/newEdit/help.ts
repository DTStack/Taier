import { TABS_TITLE_KEY } from './const'

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
