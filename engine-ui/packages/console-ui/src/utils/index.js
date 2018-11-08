import moment from "moment";
/**
 * utils
 */
/* eslint-disable */
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
        return prop !== undefined && prop !== null;
    },

    isMacOs: function() {
        return navigator.userAgent.indexOf("Macintosh") > -1;
    },

    isWindows: function() {
        return navigator.userAgent.indexOf("Windows") > -1;
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
        moment.locale("zh-cn");
        return moment(timestap).format("YYYY-MM-DD HH:mm:ss");
    },

    formatDate: function(timestap) {
        moment.locale("zh-cn");
        return moment(timestap).format("YYYY-MM-DD");
    },
    formatDateHours: function(timestap) {
        moment.locale("zh-cn");
        return moment(timestap).format("YYYY-MM-DD HH:mm");
    },
    formatHours: function(timestap) {
        moment.locale("zh-cn");
        return moment(timestap).format("HH:mm");
    },
    formatMinute: function(timestap) {
        moment.locale("zh-cn");
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
        var d = new Date();
        d.setDate(d.getDate() - 1);
        var domain = domain || document.domain;
        var path = path || "/";
        document.cookie =
            name + "=; expires=" + d + "; domain=" + domain + "; path=" + path;
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

    convertBytes: function(value) {
        if (value > 1024) {
            const val0 = (value / 1024).toFixed(2);

            if (val0 > 1024) {
                // to KB

                const val1 = (val0 / 1024).toFixed(2);

                if (val1 > 1024) {
                    // MB

                    const val2 = (val1 / 1024).toFixed(2);

                    return `${val2} GB`;
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
        if(!text){
            return text
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
    sortByCompareFunctions(arr,...compareFunctions){
        arr.sort(
            (a,b)=>{
                let result=0;
                for(let func of compareFunctions){
                    result=func(a,b);
                    if(result!=0){
                        return result;
                    }
                }
                return result;
            }
        )
    },
    /**
     * 转换排序字段
     */
    exchangeOrder(order){
        switch(order){
            case "ascend":{
                return "asc"
            }
            case "descend":{
                return "desc"
            }
            default:{
                return undefined
            }
        }
    }
};

export default utils;
/* eslint-disable */
