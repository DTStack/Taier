/**
 * GitHub: https://github.com/wewoor/cup/blob/HEAD/README_zh.md
 * 用于本地模拟 NGINX，测试打包的dist文件，亦或者做接口模拟操作
 * 使用：
 * > npm install -g mini-cup
 * > cup config // 按配置文件运行
 */

const base = '/mock';

module.exports = {
    'name': 'DAGScheduleX',
    'listen': 3000,
    'root': './out',
    'location': {
        '/ide': `./out/`,
        '/console-ui': `./out/`
    },
    'proxyTable': {
        '/api/console': { // 控制台
            // target: 'http://172.16.8.109:8084', // 开发环境
            // target: 'http://172.16.10.34:8084', // 测试环境
            target: 'http://172.16.100.168:80890', // 测试环境
            // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
            changeOrigin: true,
            secure: false
        },
    }
}
