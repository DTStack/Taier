/**
 * GitHub: https://github.com/wewoor/cup/blob/HEAD/README_zh.md
 * 用于本地模拟 NGINX
 * 使用：
 * > npm install -g mini-cup
 * > cup config // 按配置文件运行
 */

const publicURL = 'http://schedule.dtstack.cn';

module.exports = {
	name: 'taier',
	listen: 8080,
	root: './dist',
	proxyTable: {
		'/taier': {
			target: `${publicURL}:8090`,
			changeOrigin: true,
			secure: false,
		}
	},
};
