import { changeTab } from '../base/tab';
import { siderBarType } from '../../consts';
export function changeContent (newContent, tab, isDirty = true) {
    return changeTab(siderBarType.notebook, {
        ...tab,
        ...newContent,
        isDirty
    })
}
export function changeText (text, tab) {
    return changeContent({
        sqlText: text
    }, tab)
}
