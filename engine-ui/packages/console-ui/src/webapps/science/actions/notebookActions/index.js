import { changeTab } from '../base/tab';
export function changeContent (newContent, tab, isDirty = true) {
    return changeTab('notebook', {
        ...tab,
        ...newContent,
        isDirty
    })
}
export function changeText (text, tab) {
    return changeContent({
        sqlText: 'text'
    }, tab)
}
