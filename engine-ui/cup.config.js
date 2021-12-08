/**
 * GitHub: https://github.com/wewoor/cup/blob/HEAD/README_zh.md
 * 用于本地模拟 NGINX
 * 使用：
 * > npm install -g mini-cup
 * > cup config // 按配置文件运行
 */
const path = require('path');
const rootPath = path.resolve(__dirname, './');
const publicURL = require(path.join(rootPath,'./package.json')).microHost;

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
        '/node': {
            target: `${publicURL}:8090`, 
            changeOrigin: true,
            secure: false
        },
        '/api': {
            target: `${publicURL}:8090`, 
            changeOrigin: true,
            secure: false
        },
    }
}
