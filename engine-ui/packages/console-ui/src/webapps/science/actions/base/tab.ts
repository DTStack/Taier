import { experimentTabType, notebookTabType } from '../../consts/actionType/tabType'
import { siderBarType } from '../../consts';
import { checkAndcloseTabs } from './helper';

const typeMap: any = {
    [siderBarType.experiment]: experimentTabType,
    [siderBarType.notebook]: notebookTabType
}
export function addTab (type: any, tab: any) {
    return {
        type: typeMap[type].ADD_TAB,
        payload: tab
    }
}
export function deleteTab (type: any, tab: any) {
    return {
        type: typeMap[type].DELETE_TAB,
        payload: tab
    }
}
export function changeTab (type: any, tab: any) {
    return {
        type: typeMap[type].CHANGE_TAB,
        payload: tab
    }
}
export function changeTabSlient (type: any, tab: any) {
    return {
        type: typeMap[type].CHANGE_TAB_SLIENT,
        payload: tab
    }
}
export function deleteAllTab (type: any) {
    return {
        type: typeMap[type].DELETE_ALL_TAB
    }
}
export function setCurrentTab (type: any, tabId: any) {
    return {
        type: typeMap[type].SET_CURRENT_TAB,
        payload: tabId
    }
}
export function deleteOtherTab (type: any, tabId: any) {
    return {
        type: typeMap[type].DELETE_OTHER_TAB,
        payload: tabId
    }
}

export function closeTab (type: any, tabId: any, tabs: any, currentTabIndex: any) {
    return async (dispatch: any) => {
        let isChecked = await checkAndcloseTabs(tabs, [parseInt(tabId)]);
        if (isChecked) {
            if (currentTabIndex == tabId && tabs.length > 1) {
                dispatch(setCurrentTab(type, tabs.filter((tab: any) => {
                    return tab.id != currentTabIndex
                }).pop().id));
            }
            dispatch(deleteTab(type, tabId));
        }
    }
}
