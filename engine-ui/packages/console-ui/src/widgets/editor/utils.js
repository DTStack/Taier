
const commonEvent = {
    OPEN_COMMAND_LINE: "editor.action.quickCommand",
    OPEN_FIND_TOOL: "actions.find",
    OPEN_REPLACE_TOOL: "editor.action.startFindReplaceAction"
}
/**
 * 编辑菜单事件委托
 * @param {IStandaloneCodeEditor} editor 编辑器实例
 * @param {Object} customKeys 事件对应的键值对，默认为{find: "find", replace: "replace", commandPane: "commandPane"}
 */
export function commonFileEditDelegator(editor, customKeys = {}) {
    const defaultKeys = { find: "find", replace: "replace", commandPane: "commandPane" };
    const keys = { ...defaultKeys, ...customKeys };

    return function (key) {
        switch (key) {
            case keys.find: {
                editor.trigger("anyString", commonEvent.OPEN_FIND_TOOL)
                return;
            }
            case keys.replace: {
                editor.trigger("anyString", commonEvent.OPEN_REPLACE_TOOL)
                return;
            }
            case keys.commandPane: {
                editor.trigger("anyString", commonEvent.OPEN_COMMAND_LINE)
                return;
            }
        }
    }
}

export function jsonEqual(newJson, oldJson) {
    if (newJson == oldJson) {
        return true;
    }
    const newStr = JSON.stringify(newJson);
    const oldStr = JSON.stringify(oldJson);
    if (newStr == oldStr) {
        return true;
    }
    return false;
}