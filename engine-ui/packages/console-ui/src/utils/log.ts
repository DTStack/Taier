declare var APP: any;

/**
 * 输出应用版本以及运维信息
 */
export function appInfo () {
    window.console.log(`%cApp current version: v${APP.VERSION}`, 'font-family: Cabin, Helvetica, Arial, sans-serif;text-align: left;font-size:32px;color:#B21212;');
}
