import { experimentTabType, notebookTabType } from '../../consts/actionType/tabType'

const typeMap = {
    experiment: experimentTabType,
    notebook: notebookTabType
}
export function addTab (type, tab) {
    return {
        type: typeMap[type].ADD_TAB,
        payload: tab
    }
}
export function deleteTab (type, tab) {
    return {
        type: typeMap[type].DELETE_TAB,
        payload: tab
    }
}
export function changeTab (type, tab) {
    return {
        type: typeMap[type].CHANGE_TAB,
        payload: tab
    }
}
export function deleteAllTab (type) {
    return {
        type: typeMap[type].DELETE_ALL_TAB
    }
}
export function setCurrentTab (type, tabId) {
    return {
        type: typeMap[type].SET_CURRENT_TAB,
        payload: tabId
    }
}
