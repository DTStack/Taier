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
function mergeConfig () {
    var app_conf = window.APP_CONF;
    var common_conf = window.COMMON_CONF;
    window.APP_CONF = Object.assign({}, common_conf, app_conf);
}
/**
 * 设置title
 */
function initTitle () {
    var name = APP_CONF.titleName ? ('-' + APP_CONF.titleName) : '';
    document.title = APP_CONF.prefix + name;
}
/**
 * 设置loading
 */
function initLoading () {
    var dom = document.getElementById('loading-prefix');
    var loadingText = APP_CONF.prefix + ' ' + APP_CONF.loadingTitle;
    dom.innerHTML = loadingText;
}
