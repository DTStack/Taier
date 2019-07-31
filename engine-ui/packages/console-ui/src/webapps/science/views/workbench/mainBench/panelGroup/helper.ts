import { Modal } from 'antd';
export function checkAndcloseTabs (tabs: any, closetabs: any) {
    return new Promise((resolve: any, reject: any) => {
        if (!checkTabIsSaved(tabs, closetabs)) {
            Modal.confirm({
                title: '确认关闭?',
                content: '任务未保存，是否确认关闭？',
                onOk: () => {
                    resolve(true)
                },
                onCancel: () => {
                    resolve(false)
                }
            })
        } else {
            resolve(true)
        }
    })
}
export function checkTabIsSaved (tabs: any, closetabs: any) {
    const tab = tabs.find((tab: any) => {
        return closetabs.indexOf(tab.id) > -1 && tab.isDirty
    });
    return !tab;
}
