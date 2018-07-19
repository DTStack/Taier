import { debounce, endsWith, cloneDeep } from 'lodash';

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
 * 
 * @param {*} origin
 * @param {*} mergeTo
 */
export function mergeTreeNodes(origin, target) {
    replaceTreeNode(origin, target);
    if (target.children) {
        const children = target.children
        for (let i = 0; i < children.length; i += 1) {
            mergeTreeNodes(origin, children[i])
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
        //推入数组
        tmpArr.push({
            index: i,
            char: char
        });
        let firstChar = tmpArr[0];
        //校验数组是否有匹配语法
        if (tmpArr.length < 2) {
            if (firstChar.char != "'" && firstChar.char != "\"" && firstChar.char != "-") {
                tmpArr = [];
            }
            continue;
        }

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

/**
 * 分割sql
 * @param {String} sqlText 
 */
export function splitSql(sqlText) {
    if (!sqlText) {
        return sqlText;
    }
    sqlText = sqlText.trim();
    if (!endsWith(sqlText, ';')) {
        sqlText += ';';
    }

    let results = [];
    let index = 0;
    let tmpChar = null;
    for (let i = 0; i < sqlText.length; i++) {
        let char = sqlText[i];

        if (char == "'" || char == '"') {
            if (tmpChar == char) {
                tmpChar = null;
            } else {
                tmpChar = char;
            }
        } else if (char == ';') {
            if (tmpChar == null) {
                results.push(sqlText.substring(index, i));
                index = i + 1;
            }
        }
    }

    return results;
}


export function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min)) + min; //The maximum is exclusive and the minimum is inclusive
}

/**
 * 滚动元素到视窗范围内
 */
export function scrollToView(id) {
    const ele = document.getElementById(id);
    if (ele && ele.scrollIntoViewIfNeeded) {
        ele.scrollIntoViewIfNeeded()
    } else if (ele && ele.scrollIntoView) {
        ele.scrollIntoView({ behavior: "smooth", block: "center", inline: "center" });
    }
}