import moment from 'moment';
import { Utils } from 'dt-utils';

moment.locale('zh-cn');
export interface BrowserInter {
    chrome?: string;
    ie?: string;
    edge?: string;
    firefox?: string;
    safari?: string;
    opera?: string;
}

const utils = {
    
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
     * @description 浏览器类型和版本检测
     * @returns {Boolean} `true`表示通过兼容性检测,`false`表示不通过兼容性检测
     */
    browserCheck () {
        const Sys: BrowserInter = {};
        if (Utils.isMobileDevice()) { return true; } // 忽略移动设备
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
};

export default utils;
