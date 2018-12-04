/**
 * 应用公用配置
 */
window.COMMON_CONF = {
    UIC_URL: 'http://dtuic.dtstack.net',  // UIC中心地址
    UIC_DOMAIN: '.dtstack.net', // UIC域名
    prefix: "DTinsight",//应用前缀
    indexTitle: "袋鼠云·数栈V3.0",//主页的大标题
    showCopyright: true,//是否显示版权信息
    name:"数栈",//网页的title
}
/**
 * assign polyfill
 */
if (typeof Object.assign != 'function') {
    Object.assign = function (target) {
        'use strict';
        if (target == null) {
            throw new TypeError('Cannot convert undefined or null to object');
        }

        target = Object(target);
        for (var index = 1; index < arguments.length; index++) {
            var source = arguments[index];
            if (source != null) {
                for (var key in source) {
                    if (Object.prototype.hasOwnProperty.call(source, key)) {
                        target[key] = source[key];
                    }
                }
            }
        }
        return target;
    };
}

(function () {
    mergeConfig();
    initTitle();
    initLoading();
})();
/**
 * 合并配置
 */
function mergeConfig() {
    var app_conf = window.APP_CONF;
    var common_conf = window.COMMON_CONF;
    window.APP_CONF = Object.assign({}, common_conf, app_conf);
}
/**
 * 设置title
 */
function initTitle() {
    document.title = APP_CONF.prefix + "-" + APP_CONF.name;
}
/**
 * 设置loading
 */
function initLoading() {
    var dom = document.getElementById("loading-prefix");
    dom.innerHTML = APP_CONF.prefix;
}