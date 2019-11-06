const path = require('path');

const ROOT_PATH = path.resolve(__dirname, '../');
const APP_PATH = path.resolve(ROOT_PATH, 'src');        // 应用根路径
const WEB_APPS = path.resolve(APP_PATH, 'webapps');     // 所有应用
const WEB_PUBLIC = path.resolve(APP_PATH, 'public');    // 公开资源
const PWA = path.resolve(APP_PATH, 'pwa');              // pwa

// 所有应用
const MAIN_APP_PATH = path.resolve(WEB_APPS, 'main');             // 主应用
const RDOS_PATH = path.resolve(WEB_APPS, 'rdos');                 // RDOS
const STREAM_PATH = path.resolve(WEB_APPS, 'stream');                 // 流计算
const DATA_QUALITY_PATH = path.resolve(WEB_APPS, 'dataQuality');  // 数据质量
const DATA_API_PATH = path.resolve(WEB_APPS, 'dataApi');          // 数据API
const DATA_TAG_PATH = path.resolve(WEB_APPS, 'tagEngine');      // 标签工厂
const DATA_MAP_PATH = path.resolve(WEB_APPS, 'dataMap');          // 数据地图
const CONSOLE_PATH = path.resolve(WEB_APPS, 'console');          // 控制台
const ANALYTICS_ENGINE_PATH = path.resolve(WEB_APPS, 'analyticsEngine'); // 分析引擎
const SCIENCE_PATH = path.resolve(WEB_APPS, 'science'); // 分析引擎
const META_DATA_PATH = path.resolve(WEB_APPS, 'metaData');        // 元数据
const CORE_DATA_PATH = path.resolve(WEB_APPS, 'coreData');        // 主数据

// 根目录文件app地址
const MAIN_APP_FILE = path.resolve(MAIN_APP_PATH, 'app');
const RDOS_APP_FILE = path.resolve(RDOS_PATH, 'app');
const STREAM_APP_FILE = path.resolve(STREAM_PATH, 'app');
const DATA_QUALITY_APP_FILE = path.resolve(DATA_QUALITY_PATH, 'app');
const DATA_API_APP_FILE = path.resolve(DATA_API_PATH, 'app');
const DATA_TAG_APP_FILE = path.resolve(DATA_TAG_PATH, 'app');
const CONSOLE_APP_FILE = path.resolve(CONSOLE_PATH, 'app');
const DATA_MAP_APP_FILE = path.resolve(DATA_MAP_PATH, 'app');
const META_DATA_APP_FILE = path.resolve(META_DATA_PATH, 'app');
const CORE_DATA_APP_FILE = path.resolve(CORE_DATA_PATH, 'app');
const ANALYTICS_ENGINE_APP_FILE = path.resolve(ANALYTICS_ENGINE_PATH, 'app');
const SCIENCE_APP_FILE = path.resolve(SCIENCE_PATH, 'app');


const BUILD_PATH = path.resolve(ROOT_PATH, 'dist');      // 发布文件所存放的目录

module.exports = {

    ROOT_PATH,
    APP_PATH,
    WEB_APPS,
    WEB_PUBLIC,
    PWA,

    MAIN_APP_PATH,
    RDOS_PATH,
    STREAM_PATH,
    DATA_QUALITY_PATH,
    DATA_API_PATH,
    DATA_TAG_PATH,
    CONSOLE_PATH,
    DATA_MAP_PATH,
    META_DATA_PATH,
    CORE_DATA_PATH,
    ANALYTICS_ENGINE_PATH,
    SCIENCE_PATH,

    MAIN_APP_FILE,
    RDOS_APP_FILE,
    STREAM_APP_FILE,
    DATA_QUALITY_APP_FILE,
    DATA_API_APP_FILE,
    DATA_TAG_APP_FILE,
    CONSOLE_APP_FILE,
    DATA_MAP_APP_FILE,
    META_DATA_APP_FILE,
    CORE_DATA_APP_FILE,
    ANALYTICS_ENGINE_APP_FILE,
    SCIENCE_APP_FILE,

    BUILD_PATH
}
