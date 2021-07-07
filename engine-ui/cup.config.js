/**
 * GitHub: https://github.com/wewoor/cup/blob/HEAD/README_zh.md
 * 用于本地模拟 NGINX
 * 使用：
 * > npm install -g mini-cup
 * > cup config // 按配置文件运行
 */

module.exports = {
    'name': 'DAGScheduleX',
    'listen': 3000,
    'root': './out',
    'location': {
        '/ide': `./out/`,
        '/console-ui': `./out/`
    },
    'proxyTable': {
        '/node': { // 控制台
            target: 'http://172.16.100.225:7001/proxy/44', // doraemon
            changeOrigin: true,
            secure: false
        },
    }
}
