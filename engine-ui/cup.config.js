/**
 * GitHub: https://github.com/wewoor/cup/blob/HEAD/README_zh.md
 * 用于本地模拟 NGINX
 * 使用：
 * > npm install -g mini-cup
 * > cup config // 按配置文件运行
 */

module.exports = {
    'name': 'DAGScheduleX',
    'listen': 8080,
    'root': './out',
    'location': {
        '/ide': `./out/`,
        '/console-ui': `./out/`,
        '/data-source': './out/',
    },
    'proxyTable': {
        '/node': { // 控制台
            target: 'http://schedule.dtstack.cn:8090', // doraemon
            changeOrigin: true,
            secure: false
        },
        '/api/rdos': {
            target: "http://schedule.dtstack.cn:8090", // doraam
            changeOrigin: true,
            secure: false
        },
    }
}
