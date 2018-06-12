import { debounce } from 'lodash';

/**
 * 存放一些零碎的公共方法
 */

// 请求防抖动
export function debounceEventHander(...args) {
    const debounced = debounce(...args)
    return function (e) {
        e.persist()
        return debounced(e)
    }
}

/**
 * 查找树中的某个节点
 */
export function findTreeNode(treeNode, node) {
    let result = ""
    if (treeNode.id === parseInt(node.id, 10)) {
        return treeNode;
    }
    if (treeNode.children) {
        const children = treeNode.children
        for (let i = 0; i < children.length; i += 1) {
            result = findTreeNode(children[i], node)
            if (result) return result
        }
    }
}

/**
 * 
 * @param {Array} treeNodes 树状节点数据
 * @param {Object} target 目标节点
 */
export function removeTreeNode(treeNodes, target) {
    for (let i = 0; i < treeNodes.length; i += 1) {
        if (treeNodes[i].id === target.id) {
            treeNodes.splice(i, 1) // remove节点
            break;
        }
        if (treeNodes[i].children) {
            removeTreeNode(treeNodes[i].children, target)
        }
    }
}

/**
 * 追加子节点
 */
export function appendTreeNode(treeNode, append, target) {
    const targetId = parseInt(target.id, 10)
    if (treeNode.children) {
        const children = treeNode.children
        for (let i = 0; i < children.length; i += 1) {
            appendTreeNode(children[i], append, target)
        }
    }
    if (treeNode.id === targetId && treeNode.children) {
        treeNode.children.push(append)
    }
}

/**
 * 遍历树形节点，用新节点替换老节点
*/
export function replaceTreeNode(treeNode, replace) {
    if (
        treeNode.id === parseInt(replace.id, 10)
        // && 
        // treeNode.level === replace.level
    ) {
        treeNode = Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.children) {
        const children = treeNode.children
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace)
        }
    }
}

/**
 * 打开新窗口
 * @param {*} url 
 * @param {*} target 
 */
export function openNewWindow(url, target) {
    window.open(url, target || '_blank')
}

/**
 * 检验改应用是否包含项目选项
 * @param {s} app 
 */
export function hasProject(app) {
    return app === 'rdos'
}

/**
 * 字符串替换
 */
export function replaceStrFormBeginToEnd(str, replaceStr, begin, end) {

    return str.substring(0, begin) + replaceStr + str.substring(end + 1)
}

/**
 * 字符串替换（根据索引数组）
 */
export function replaceStrFormIndexArr(str, replaceStr, indexArr) {
    let arr = [];
    let result = "";
    let index = 0;

    if (!indexArr || indexArr.length < 1) {
        return str;
    }
    for (let i = 0; i < indexArr.length; i++) {
        let indexItem = indexArr[i];
        let begin = indexItem.begin;

        result = result + str.substring(index, begin) + replaceStr;
        index = indexItem.end + 1;

        if (i == indexArr.length - 1) {
            result = result + str.substring(index);
        }
    }

    return result;
}

/**
 * 过滤sql中的注释
 * @param {s} app 
 */
export function filterComments(sql) {
    let tmpArr = [];
    const comments = [];

    for (let i = 0; i < sql.length; i++) {
        let char = sql[i];

        //读取字符
        if (char == "'" || char == "\"" || char == "-" || char == "\n") {
            //推入数组
            tmpArr.push({
                index: i,
                char: char
            });
        }
        //校验数组是否有匹配语法
        if (tmpArr.length < 2) {
            if (tmpArr[0] && tmpArr[0].char == "\n") {
                tmpArr = [];
            }
            continue;
        }

        let firstChar = tmpArr[0];
        let lastChar = tmpArr[tmpArr.length - 1];

        if (firstChar.char == "'" || firstChar.char == "\"") {
            //引号匹配，则清空
            if (lastChar.char == firstChar.char) {
                tmpArr = [];
                continue;
            }
        } else if (firstChar.char == "-") {
            //假如第一个是横线，则开始校验注释规则

            //判断是否为两个注释符号，不是，则清空
            if (tmpArr[1].char != "-") {
                tmpArr = [];
                continue;
            }
            //为注释作用域，遇到换行符，则结束注释
            else if (lastChar.char == "\n") {
                comments.push({
                    begin: firstChar.index,
                    end: lastChar.index
                })
                tmpArr = [];
                continue;
            }
            //解析结束
            else if (i == sql.length - 1) {
                comments.push({
                    begin: firstChar.index,
                    end: i
                })
                continue;
            }
        } else {
            tmpArr = [];
        }
    }

    sql = replaceStrFormIndexArr(sql, '', comments)

    return sql;
}


export function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min)) + min; //The maximum is exclusive and the minimum is inclusive
}