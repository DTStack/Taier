const path = require('path');

const ROOT_PATH = path.resolve(__dirname, '../');
const APP_PATH = path.resolve(ROOT_PATH, 'src');        // 应用根路径
const WEB_APPS = path.resolve(APP_PATH, 'webapps');     // 所有应用
const WEB_PUBLIC = path.resolve(APP_PATH, 'public');    // 公开资源
const PWA = path.resolve(APP_PATH, 'pwa');              // pwa

const BASE_NAME = process.env.BASE_NAME || '/console/'; // 资源目录 默认访问路径
const BUILD_PATH = path.resolve(ROOT_PATH, `dist${BASE_NAME}`);      // 发布文件所存放的目录

// app 文件地址
const APP_FILE = path.resolve(APP_PATH, 'app');

module.exports = {

    ROOT_PATH,
    APP_PATH,
    WEB_APPS,
    WEB_PUBLIC,
    PWA,

    APP_FILE,
    BASE_NAME,
    BUILD_PATH
}
