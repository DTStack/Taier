const path = require('path');

const ROOT_PATH = path.resolve(__dirname, '../');
const APP_PATH = path.resolve(ROOT_PATH, 'src'); // 应用根路径
const WEB_PUBLIC = path.resolve(APP_PATH, 'public'); // 公开资源

// 根目录文件app地址
const MAIN_APP_FILE = path.resolve(APP_PATH, 'app');

const BASE_NAME = '/portal/'; // 资源目录 默认访问路径
const BUILD_PATH = path.resolve(ROOT_PATH, `dist${BASE_NAME}`); // 发布文件所存放的目录

module.exports = {
  ROOT_PATH,
  APP_PATH,
  WEB_PUBLIC,
  MAIN_APP_FILE,
  BASE_NAME,
  BUILD_PATH,
};
