/**
 * GitHub: https://github.com/wewoor/cup/blob/HEAD/README_zh.md
 * 用于本地模拟 NGINX
 * 使用：
 * > npm install -g mini-cup
 * > cup config // 按配置文件运行
 */

const publicURL = 'http://taiga.dtstack.cn';

module.exports = {
	name: 'taiga',
	listen: 8080,
	root: './dist',
	proxyTable: {
		'/node': {
			target: `${publicURL}:8090`,
			changeOrigin: true,
			secure: false,
		},
		'/api': {
			target: `${publicURL}:8090`,
			changeOrigin: true,
			secure: false,
		},
	},
};
