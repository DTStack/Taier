/* eslint-disable no-useless-escape */
/* eslint-disable no-cond-assign */
/* eslint-disable no-unused-expressions */
import moment from 'moment';

import {
    BrowserInter
} from './interface';

moment.locale('zh-cn');

const utils = {
    /**
     * 获取页面宽度
     * @return {[type]} [description]
     */
    pageWidth () {
        return Math.max(
            document.documentElement.clientWidth,
            window.innerWidth || 0
        );
    },

    /**
     * 获取页面高度
     * @return {[type]} [description]
     */
    pageHeight () {
        return Math.max(
            document.documentElement.clientHeight,
            window.innerHeight || 0
        );
    },

    checkExist (prop: any) {
        return prop !== undefined && prop !== null && prop !== '';
    },

    isMacOs () {
        return navigator.userAgent.indexOf('Macintosh') > -1;
    },

    isWindows () {
        return navigator.userAgent.indexOf('Windows') > -1;
    },
    isMobileDevice () {
        return (typeof window.orientation !== 'undefined') || (navigator.userAgent.indexOf('IEMobile') !== -1);
    },
    /**
     * @description 浏览器类型和版本检测
     * @returns {Boolean} `true`表示通过兼容性检测,`false`表示不通过兼容性检测
     */
    browserCheck () {
        const Sys: BrowserInter = {};
        if (this.isMobileDevice()) { return true; } // 忽略移动设备
        const ua = navigator.userAgent.toLowerCase();
        let s;
        // eslint:disable:no-conditional-assignment
        (s = ua.match(/rv:([\d.]+)\) like gecko/)) ? Sys.ie = s[1]
            : (s = ua.match(/msie ([\d\.]+)/)) ? Sys.ie = s[1]
                : (s = ua.match(/edge\/([\d\.]+)/)) ? Sys.edge = s[1]
                    : (s = ua.match(/firefox\/([\d\.]+)/)) ? Sys.firefox = s[1]
                        : (s = ua.match(/(?:opera|opr).([\d\.]+)/)) ? Sys.opera = s[1]
                            : (s = ua.match(/chrome\/([\d\.]+)/)) ? Sys.chrome = s[1]
                                // tslint:disable-next-line:no-unused-expression
                                : (s = ua.match(/version\/([\d\.]+).*safari/)) ? Sys.safari = s[1] : 0;
        if (
            (Sys.chrome && parseInt(Sys.chrome.split('.')[0], 10) >= 66) ||
            Sys.firefox
        ) { return true; }
        return false;
    },
    /**
     * 根据参数名获取URL数据
     * @param  {[type]} name [description]
     * @param  {[type]} url  [description]
     */
    getParameterByName (name: string, url?: string) {
        if (!url) { url = window.location.href; }
        name = name.replace(/[\[\]]/g, '\\$&');
        const regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)');

        const results = regex.exec(url);
        if (!results) { return null; }
        if (!results[2]) { return ''; }
        return decodeURIComponent(results[2].replace(/\+/g, ' '));
    },

    /**
     * 获取图片的Base64格式
     * @param  {[type]}   img      [description]
     * @param  {Function} callback [description]
     */
    getBase64 (img: any, callback: (img: any) => void) {
        const reader = new FileReader();
        reader.addEventListener('load', () => callback(reader.result));
        reader.readAsDataURL(img);
    },

    /**
     * 百分比转换
     * @param  {[type]} num       [description]
     * @param  {[type]} precision [description]
     */
    percent (num: number, precision?: number) {
        if (!num || num === Infinity) { return 0 + '%'; }
        if (num > 1) { num = 1; }
        precision = precision || 2;
        precision = Math.pow(10, precision);
        return Math.round(num * precision * 100) / precision + '%';
    },

    getCssText (object: any = {}) {
        let str = '';
        for (const attr in object) {
            if (object.hasOwnProperty(attr)) {
                str += attr + ':' + object[attr] + ';';
            }
        }
        return str;
    },

    formatDateTime (timestap: string | number | Date) {
        return moment(timestap).format('YYYY-MM-DD HH:mm:ss');
    },

    formatDate (timestap: string | number | Date) {
        return moment(timestap).format('YYYY-MM-DD');
    },
    formatDateHours (timestap: string | number | Date) {
        return moment(timestap).format('YYYY-MM-DD HH:mm');
    },
    formatDayHours (timestap: string | number | Date) {
        return moment(timestap).format('MM-DD HH:mm');
    },
    formatHours (timestap: string | number | Date) {
        return moment(timestap).format('HH:mm');
    },
    formatMinute (timestap: string | number | Date) {
        return moment(timestap).format('HH:mm:ss');
    },

    /**
     * 去除空串
     */
    trim (str: string) {
        return typeof str === 'string'
            ? str.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, '')
            : str;
    },

    trimlr (str: string) {
        const res = str.replace(/^\s*/, ''); // 去左边
        return res.replace(/\s*$/, ''); // 去右边
    },

    removeAllSpaces (str: string) {
        return typeof str === 'string'
            ? str.replace(/\s*/g, '') // 去除全部空串
            : str;
    },

    /**
     * 原生 JavaScript 获取 cookie 值
     * @param name
     */
    getCookie (name: string) {
        const arr = document.cookie.match(
            new RegExp('(^| )' + name + '=([^;]*)(;|$)')
        );
        if (arr != null) { return unescape(decodeURI(arr[2])); }
        return null;
    },

    deleteCookie (name: string, domain?: string, path?: string) {
        const d = new Date(0);
        domain = domain ? `; domain=${domain}` : '';
        path = path || '/';
        document.cookie =
            name + '=; expires=' + d.toUTCString() + domain + '; path=' + path;
    },

    deleteAllCookies (domain: string, path: string) {
        const cookies = document.cookie.split(';');
        // tslint:disable-next-line:prefer-for-of
        for (let i = 0; i < cookies.length; i++) {
            if (cookies[i]) {
                this.deleteCookie(cookies[i].split('=')[0], domain, path);
            }
        }
    },

    setCookie (name: string, value: string | number | object | boolean, days?: number) {
        let expires = '';
        if (days) {
            const date = new Date();
            date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
            expires = '; expires=' + date.toUTCString();
        }
        document.cookie = name + '=' + value + expires + '; path=/';
    },

    /**
     * 转换 Byte 为指定单位的值
     * @param value 'B' | 'KB' | 'MB' | 'GB' | 'TB' | 'PB' 转换原始值
     * @param targetUnit 转换目标单位，默认转换为小于1024值最大单位
     */
    convertBytes (value: number, targetUnit?: 'B' | 'KB' | 'MB' | 'GB' | 'TB' | 'PB') {
        if (targetUnit && targetUnit === 'B') return `${value} ${targetUnit}`;

        const units = ['KB', 'MB', 'GB', 'TB', 'PB'];
        for (let i = 0; i < units.length; ++i) {
            if (value >= 1024) {
                value = Number((value / 1024).toFixed(2));
                if (targetUnit && (units[i] === targetUnit || value < 1024)) {
                    return `${value} ${targetUnit}`;
                }
            } else {
                return `${value} ${units[i]}`;
            }
        }
    },

    /**
     * 时间转换 3661s--->1小时1分钟1秒
     */
    formatTime (time: number = 0) {
        let second = 0;
        let minute = 0;
        let hour = 0;

        function _formatHour (timestap: number) {
            hour = Math.floor(timestap / 3600);
            return timestap - hour * 3600;
        }
        function _formatMinute (timestap: number) {
            minute = Math.floor(timestap / 60);
            return timestap - minute * 60;
        }
        function _formatSecond (timestap: number) {
            second = timestap;
            return second;
        }
        _formatSecond(_formatMinute(_formatHour(time)));
        return `${hour ? hour + 'h' : ''}${minute ? minute + 'm' : ''}${second ? second + 's' : ''}` || '0s';
    },

    // 千位分割
    toQfw (str: string) {
        if (!str) {
            return 0;
        }
        str = str.toString ? str.toString() : str;
        const re = /(?=(?!(\b))(\d{3})+$)/g;
        str = str.replace(re, ',');
        return str;
    },
    // 文字溢出转换
    textOverflowExchange (text: string, length: number) {
        if (text && text.length > length) {
            return text.substring(0, length) + '...';
        }
        return text;
    },
    /**
     * json格式化
     * @param {格式化内容} text
     * @param {格式化占位符} space
     */
    jsonFormat (text: string, space?: number) {
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
    sortByCompareFunctions (arr: any[], ...compareFunctions: any[]) {
        arr.sort((a, b) => {
            let result = 0;
            for (const func of compareFunctions) {
                result = func(a, b);
                if (result !== 0) {
                    return result;
                }
            }
            return result;
        });
    },
    /**
     * 转换排序字段
     */
    exchangeOrder (order: string) {
        switch (order) {
            case 'ascend': {
                return 'asc';
            }
            case 'descend': {
                return 'desc';
            }
            default: {
                return undefined;
            }
        }
    },
    /**
     * 生成一个key
     */
    generateAKey () {
        // tslint:disable-next-line:no-bitwise
        return '' + new Date().getTime() + ~~(Math.random() * 1000000);
    },

    /**
     * 判断是否是JSON string
     * @param  {String}  str 所要验证的字符串
     * @return {Boolean}   是否是JSON字符串
     */
    isJSONStr (str: string) {
        str = this.trimlr(str);
        return (
            (str.charAt(0) === '{' && str.charAt(str.length - 1) === '}') ||
            (str.charAt(0) === '[' && str.charAt(str.length - 1) === ']')
        );
    },
    /**
     * 随机生成一串6位同时包含数字、大小写字母的字符串
     * @param len number
     */
    getRandomStr (len: number): string {
        const numChar = '0123456789';
        const lowerCaseChar = 'abcdefghijklmnopqrstuvwxyz';
        const upperCaseChar = 'ABCDEFGHIJKLMNOPQRSTUVXYZ';
        function getChar (baseChar: string) {
            const randomIndex = Math.random() * (baseChar.length - 1);
            return baseChar.charAt(randomIndex);
        }
        let currentChar = 'num';
        let str = '';
        for (let i = 0; i < len; i++) {
            if (currentChar === 'num') {
                str += getChar(numChar);
                currentChar = 'lower';
            } else if (currentChar === 'lower') {
                str += getChar(lowerCaseChar);
                currentChar = 'upper';
            } else if (currentChar === 'upper') {
                str += getChar(upperCaseChar);
                currentChar = 'num';
            }
        }
        return str;
    },
    /**
     * simply judge whether the array is equal
     * @param arr1
     * @param arr2
     * @returns arr1 === arr2
     */
    isEqualArr (arr1: string[], arr2: string[]): boolean {
        const toString = JSON.stringify;
        return toString(arr1.sort()) === toString(arr2.sort());
    }
};

export default utils;
