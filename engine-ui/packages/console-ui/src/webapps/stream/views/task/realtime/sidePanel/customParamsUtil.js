import utils from "utils";
/**
 * 生成form Mapfield方法的自定义属性对象
 * @param {*} customParams 
 */
export function generateMapValues(customParams = []) {
    const map = {};
    customParams.forEach((customParam) => {
        map[customParam.id + "-key"] = { value: customParam.key };
        map[customParam.id + "-value"] = { value: customParam.value };
    })
    return map;
}
/**
 * 改变自定义参数触发事件
 * @param {*} panel 
 * @param {*} value 
 * @param {*} id 
 * @param {*} type 
 */
export function changeCustomParams(panel, value, extParam = {}) {
    /**
     * 这边取得传进来的customParams的引用，以便直接操作原对象
     */
    const customParams = panel.customParams ? (panel.customParams) : (panel.customParams = []);
    /**
     * 检查是不是新增一个自定义参数
     */
    if (extParam.type == "newCustomParam") {
        customParams.push({
            id: utils.generateAKey()
        })
        return;
    }
    /**
     * 检查是不是删除自定义参数
     */
    else if(extParam.type == "deleteCustomParam"){
        const index = customParams.findIndex((customParam)=>{
            return customParam.id == extParam.id;
        })
        if(index||index==0){
            customParams.splice(index,1);
        }
        return ;
    }
    const param = customParams.find((customParam) => {
        return customParam.id == extParam.id;
    })
    if (param) {
        param[extParam.type] = value;
    }
}

export function initCustomParam(panel) {
    const { customParams = [] } = panel;
    for (let i = 0; i < customParams.length; i++) {
        let customParam = customParams[i];
        if (!customParam.id) {
            customParam.id=utils.generateAKey();
        }
    }
}

