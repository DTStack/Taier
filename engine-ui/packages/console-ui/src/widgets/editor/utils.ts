
const commonEvent: any = {
    OPEN_COMMAND_LINE: 'editor.action.quickCommand',
    OPEN_FIND_TOOL: 'actions.find',
    OPEN_REPLACE_TOOL: 'editor.action.startFindReplaceAction'
}
/**
 * 编辑菜单事件委托
 * @param {IStandaloneCodeEditor} editor 编辑器实例
 * @param {Object} customKeys 事件对应的键值对，默认为{find: "find", replace: "replace", commandPane: "commandPane"}
 */
export function commonFileEditDelegator (editor, customKeys = {}) {
    const defaultKeys: any = { find: 'find', replace: 'replace', commandPane: 'commandPane' };
    const keys: any = { ...defaultKeys, ...customKeys };

    return function(key: any) {
        switch (key) {
            case keys.find: {
                editor.trigger('anyString', commonEvent.OPEN_FIND_TOOL)
                return;
            }
            case keys.replace: {
                editor.trigger('anyString', commonEvent.OPEN_REPLACE_TOOL)
                return;
            }
            case keys.commandPane: {
                editor.trigger('anyString', commonEvent.OPEN_COMMAND_LINE)
            }
        }
    }
}

export function jsonEqual (newJson: any, oldJson: any) {
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
/**
* 该函数delaytime时间内顶多执行一次func（最后一次），如果freshTime时间内没有执行，则强制执行一次。
* @param {function} func
*/
export function delayFunctionWrap(func: any) {
    /**
     * 最小执行间隔，每隔一段时间强制执行一次函数
     * 这里不能太小，因为太小会导致大的解析任务没执行完阻塞。
     */
    let freshTime = 3000;
    /**
     * 函数延迟时间
     */
    let delayTime = 500;

    let outTime: any;
    let _timeClock: any;
    return function () {
        const arg = arguments;
        _timeClock && clearTimeout(_timeClock);
        // 这边设置在一定时间内，必须执行一次函数
        if (outTime) {
            let now = new Date();
            if (now - outTime > freshTime) {
                func(...arg);
            }
        } else {
            outTime = new Date();
        }
        _timeClock = setTimeout(() => {
            outTime = null;
            func(...arg);
        }, delayTime)
    }
}
