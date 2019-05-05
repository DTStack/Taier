import { Modal } from 'antd';
export function checkAndcloseTabs (tabs, closetabs) {
    return new Promise((resolve, reject) => {
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
export function checkTabIsSaved (tabs, closetabs) {
    const tab = tabs.find((tab) => {
        return closetabs.indexOf(tab.id) > -1 && tab.isDirty
    });
    return !tab;
}
