const path = require('path');

const ROOT_PATH = path.resolve(__dirname, '../');
const APP_PATH = path.resolve(ROOT_PATH, 'src');        // 应用根路径
const WEB_APPS = path.resolve(APP_PATH, 'webapps');     // 所有应用
const WEB_PUBLIC = path.resolve(APP_PATH, 'public');    // 公开资源

// 所有应用
const MAIN_APP_PATH = path.resolve(WEB_APPS, 'main');             // 主应用
const RDOS_PATH = path.resolve(WEB_APPS, 'rdos');                 // RDOS
const DATA_QUALITY_PATH = path.resolve(WEB_APPS, 'dataQuality');  // 数据质量
const DATA_API_PATH = path.resolve(WEB_APPS, 'dataApi');          // 数据API
const DATA_LABEL_PATH = path.resolve(WEB_APPS, 'dataLabel');      // 标签工厂
const DATA_MAP_PATH = path.resolve(WEB_APPS, 'dataMap');          // 数据地图
const META_DATA_PATH = path.resolve(WEB_APPS, 'metaData');        // 元数据
const CORE_DATA_PATH = path.resolve(WEB_APPS, 'coreData');        // 主数据

// 根目录文件app地址
const MAIN_APP_FILE = path.resolve(MAIN_APP_PATH, 'app');
const RDOS_APP_FILE = path.resolve(RDOS_PATH, 'app');
const DATA_QUALITY_APP_FILE = path.resolve(DATA_QUALITY_PATH, 'app');
const DATA_API_APP_FILE = path.resolve(DATA_API_PATH, 'app');
const DATA_LABEL_APP_FILE = path.resolve(DATA_LABEL_PATH, 'app');
const DATA_MAP_APP_FILE = path.resolve(DATA_MAP_PATH, 'app');
const META_DATA_APP_FILE = path.resolve(META_DATA_PATH, 'app');
const CORE_DATA_APP_FILE = path.resolve(CORE_DATA_PATH, 'app');

const BUILD_PATH = path.resolve(ROOT_PATH, 'dist');      // 发布文件所存放的目录

module.exports = {

    ROOT_PATH,
    APP_PATH,
    WEB_APPS,
    WEB_PUBLIC,

    MAIN_APP_PATH,
    RDOS_PATH,
    DATA_QUALITY_PATH,
    DATA_API_PATH,
    DATA_LABEL_PATH,
    DATA_MAP_PATH,
    META_DATA_PATH,
    CORE_DATA_PATH,

    MAIN_APP_FILE,
    RDOS_APP_FILE,
    DATA_QUALITY_APP_FILE,
    DATA_API_APP_FILE,
    DATA_LABEL_APP_FILE,
    DATA_MAP_APP_FILE,
    META_DATA_APP_FILE,
    CORE_DATA_APP_FILE,
    
    BUILD_PATH,
}