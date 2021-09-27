/**
 * GitHub: https://github.com/wewoor/cup/blob/HEAD/README_zh.md
 * 用于本地模拟 NGINX，测试打包的dist文件，亦或者做接口模拟操作
 * 使用：
 * > npm install -g mini-cup
 * > cup config // 按配置文件运行
 */

const base = '/mock';

module.exports = {
    'name': 'DTinsight build test',
    'listen': 3001,
    'root': 'dist',
    'location': {
        '/api/dataScience/service/scienceTask/getData': `${base}/res/get_res.json`
        // "/api/rdos/batch/batchTask/getTaskById": `${base}/task/get_task.json`,
        // "/api/task/add": `${base}/add.json`
        // "/api/analysis/getCatalogue": `${base}/catalogue/get_catalogues.json`,
        // "/api/analysis/createOrUpdateDB": `${base}/is_ok.json`,
        // "/api/analysis/createSql": `${base}/table/createSQL.json`,
        // "/api/rdos/batch/batchJobJob/displayOffSpring": `${base}/task/get_task_instances.json`,
    },
    'proxyTable': {
        '/api/dq': {
            target: 'http://172.16.8.104:8089',
            // target: "http://172.16.6.135:8089", // tmp server
            // target: 'http://172.16.10.45:8089', // test
            // ignorePath: true,
            changeOrigin: true,
            secure: false
        },
        '/api/rdos': {
            // target: "http://172.16.8.107:9020", // dev server
            target: 'http://172.16.10.86:9020', // test ser
            // ignorePath: true,
            // Mock
            changeOrigin: true,
            secure: false
        },
        '/api/streamapp': {
            target: 'http://172.16.10.86:9021', // test ser
            // target: 'http://172.16.0.157:9020', // temp ser
            // target: 'http://172.16.8.107:9020', // formal test ser
            changeOrigin: true,
            secure: false
        },
        '/uic': { // UIC地址
            target: 'http://dtuic.dtstack.net',
            changeOrigin: true,
            secure: false
        },
        '/api/da': { // da地址
            // target: 'http://172.16.10.45:8087',
            target: 'http://172.16.8.108:8087',
            // pathRewrite:{"^/api/da/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/da"},
            changeOrigin: true,
            secure: false
        },
        '/api/tag': { // 数据标签
            target: 'http://172.16.8.107:8085', // 开发环境
            // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
            changeOrigin: true,
            secure: false
        },
        '/api/console': { // 控制台
            // target: 'http://172.16.8.109:8084', // 开发环境
            // target: 'http://172.16.10.34:8084', // 测试环境
            target: 'http://172.16.100.168:80890', // 测试环境
            // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
            changeOrigin: true,
            secure: false
        },
        '/api/analysis': { // 分析引擎
            // target: 'http://172.16.8.108:9022', // 开发环境
            target: 'http://172.16.10.45:9022', // 测试环境
            // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
            changeOrigin: true,
            secure: false
        },
        '/api/dataScience': { // 算法平台
            target: 'http://172.16.8.107:9029', // 开发环境
            // target: 'http://172.16.2.131:9029', // 联调环境
            changeOrigin: true,
            secure: false
        }
    }
}
