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
            changeOrigin: true,
            secure: false
        },
        '/api/rdos': {
            // target: "http://172.16.10.51:9020",
            target: 'http://172.16.8.104:9020', // formal test ser
            changeOrigin: true,
            secure: false
        },
        '/uic': { // UIC地址
            target: 'http://dtuic.dtstack.net',
            changeOrigin: true,
            secure: false
        },
        '/api/da': { // da地址
            target: 'http://172.16.8.107:8087', // 测试环境
            changeOrigin: true,
            secure: false
        },
        '/api/tag': { // 数据标签
            target: 'http://172.16.8.107:8085', // 测试环境
            changeOrigin: true,
            secure: false
        }
        // "/api/analysis": { // 数据标签
        //     target: "http://172.16.2.157:9021",//测试环境
        //     changeOrigin: true,
        //     secure: false,
        // },
    }
}
