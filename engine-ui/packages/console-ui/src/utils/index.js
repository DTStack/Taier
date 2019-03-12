import moment from 'moment';
/**
 * utils
 */
/* eslint-disable */

moment.locale("zh-cn");

const utils = {
    /**
     * 获取页面宽度
     * @return {[type]} [description]
     */
    pageWidth: function() {
        return Math.max(
            document.documentElement.clientWidth,
            window.innerWidth || 0
        );
    },

    /**
     * 获取页面高度
     * @return {[type]} [description]
     */
    pageHeight: function() {
        return Math.max(
            document.documentElement.clientHeight,
            window.innerHeight || 0
        );
    },

    checkExist: function(prop) {
        return prop !== undefined && prop !== null && prop !== '';
    },

    isMacOs: function() {
        return navigator.userAgent.indexOf("Macintosh") > -1;
    },

    isWindows: function() {
        return navigator.userAgent.indexOf("Windows") > -1;
    },
    /**
     * @description 浏览器类型和版本检测
     * @returns {Boolean} `true`表示通过兼容性检测,`false`表示不通过兼容性检测
     */
    browserCheck() {
        let Sys = {};  
        let ua = navigator.userAgent.toLowerCase();
        let s;
        (s = ua.match(/rv:([\d.]+)\) like gecko/)) ? Sys.ie = s[1] :
            (s = ua.match(/msie ([\d\.]+)/)) ? Sys.ie = s[1] :
                (s = ua.match(/edge\/([\d\.]+)/)) ? Sys.edge = s[1] :
                    (s = ua.match(/firefox\/([\d\.]+)/)) ? Sys.firefox = s[1] :
                        (s = ua.match(/(?:opera|opr).([\d\.]+)/)) ? Sys.opera = s[1] :
                            (s = ua.match(/chrome\/([\d\.]+)/)) ? Sys.chrome = s[1] :
                                (s = ua.match(/version\/([\d\.]+).*safari/)) ? Sys.safari = s[1] : 0;
        if (
            (Sys.chrome && parseInt(Sys.chrome.split('.'[0])) >= 66) || 
            Sys.firefox
        ) return true
        return false;
    },
    /**
     * 根据参数名获取URL数据
     * @param  {[type]} name [description]
     * @param  {[type]} url  [description]
     * @return {[type]}      [description]
     */
    getParameterByName: function(name, url) {
        if (!url) url = window.location.href;
        name = name.replace(/[\[\]]/g, "\\$&");
        var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return "";
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    },

    /**
     * 获取图片的Base64格式
     * @param  {[type]}   img      [description]
     * @param  {Function} callback [description]
     * @return {[type]}            [description]
     */
    getBase64: function(img, callback) {
        const reader = new FileReader();
        reader.addEventListener("load", () => callback(reader.result));
        reader.readAsDataURL(img);
    },

    /**
     * 百分比转换
     * @param  {[type]} num       [description]
     * @param  {[type]} precision [description]
     * @return {[type]}           [description]
     */
    percent: function(num, precision) {
        if (!num || num === Infinity) return 0 + "%";
        if (num > 1) num = 1;
        precision = precision ? precision : 2;
        precision = Math.pow(10, precision);
        return Math.round(num * precision * 100) / precision + "%";
    },

    getCssText: function(object) {
        var str = "";
        for (var attr in object) {
            str += attr + ":" + object[attr] + ";";
        }
        return str;
    },

    formatDateTime: function(timestap) {
        return moment(timestap).format("YYYY-MM-DD HH:mm:ss");
    },

    formatDate: function(timestap) {
        return moment(timestap).format("YYYY-MM-DD");
    },
    formatDateHours: function(timestap) {
        return moment(timestap).format("YYYY-MM-DD HH:mm");
    },
    formatDayHours: function(timestap) {
        return moment(timestap).format("MM-DD HH:mm");
    },
    formatHours: function(timestap) {
        return moment(timestap).format("HH:mm");
    },
    formatMinute: function(timestap) {
        return moment(timestap).format("HH:mm:ss");
    },

    /**
     * 去除空串
     */
    trim: function(str) {
        return typeof str === "string"
            ? str.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, "")
            : str;
    },

    trimlr: function(str) {
        const res = str.replace(/^\s*/, ""); // 去左边
        return res.replace(/\s*$/, ""); // 去右边
    },

    // 原生 JavaScript 获取 cookie 值
    getCookie: function(name) {
        const arr = document.cookie.match(
            new RegExp("(^| )" + name + "=([^;]*)(;|$)")
        );
        if (arr != null) return unescape(decodeURI(arr[2]));
        return null;
    },

    deleteCookie: function(name, domain, path) {
        var d = new Date(0);
        var domain = domain?`; domain=${domain}`:'';
        var path = path || "/";
        document.cookie =
            name + "=; expires=" + d.toUTCString() + domain + "; path=" + path;
    },

    deleteAllCookies: function(domain, path) {
        var cookies = document.cookie.split(";");
        for (var i = 0; i < cookies.length; i++)
            if (cookies[i]) {
                this.deleteCookie(cookies[i].split("=")[0], path, domain);
            }
    },

    setCookie: function(name, value, days) {
        var expires = "";
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + value + expires + "; path=/";
    },

    // TODO, 可以修改为递归算法
    convertBytes: function(value) {
        if (value >= 1024) {
            const val0 = (value / 1024).toFixed(2);

            if (val0 >= 1024) {
                // to KB

                const val1 = (val0 / 1024).toFixed(2);

                if (val1 >= 1024) {
                    // MB

                    const val2 = (val1 / 1024).toFixed(2);

                    if (val2 >= 1024) {
                        const val3 = (val2 / 1024).toFixed(2);
                        return `${val3} PB`;
                    } else {
                        return `${val2} GB`;
                    }

                } else {
                    return `${val1} MB`;
                }
            } else {
                return `${val0} KB`;
            }
        } else {
            return `${value} B`;
        }
    },
    /**
     * 时间转换 3661s--->1小时1分钟1秒
     */
    formatTime(time=0){
        let second=0;
        let minute=0;
        let hour=0;
        
        function _formatHour(time){
            hour = Math.floor(time/3600);
            return time-hour*3600;
        }
        function _formatMinute(time){
            minute = Math.floor(time/60);
            return time-minute*60;
        }
        function _formatSecond(time){
            second =  time;
            return second;
        }
        _formatSecond(_formatMinute(_formatHour(time)))
        return `${hour?hour+'h':''}${minute?minute+'m':''}${second?second+'s':''}`||"0s"
    },
    //千位分割
    toQfw: function(str) {
        if (!str) {
            return 0;
        }
        str = str.toString ? str.toString() : str;
        let re = /(?=(?!(\b))(\d{3})+$)/g;
        str = str.replace(re, ",");
        return str;
    },
    //文字溢出转换
    textOverflowExchange(text, length) {
        if (text && text.length > length) {
            return text.substring(0, length) + "...";
        }
        return text;
    },
    /**
     * json格式化
     * @param {格式化内容} text
     * @param {格式化占位符} space
     */
    jsonFormat(text, space) {
        if (!text) {
            return text;
        }
        try {
            const json = JSON.parse(text);
            const output = JSON.stringify(json, null, space || 2);

            return output;
        } catch (e) {
            return null;
        }
    },
    /**
     * 多函数排序，匹配到0为止
     */
    sortByCompareFunctions(arr, ...compareFunctions) {
        arr.sort((a, b) => {
            let result = 0;
            for (let func of compareFunctions) {
                result = func(a, b);
                if (result != 0) {
                    return result;
                }
            }
            return result;
        });
    },
    /**
     * 转换排序字段
     */
    exchangeOrder(order) {
        switch (order) {
            case "ascend": {
                return "asc";
            }
            case "descend": {
                return "desc";
            }
            default: {
                return undefined;
            }
        }
    },
    /**
     * 生成一个key
     */
    generateAKey(){
        return ''+new Date().getTime()+~~(Math.random()*1000000)
    }
};

export default utils;
/* eslint-disable */
