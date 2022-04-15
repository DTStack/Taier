import { defineConfig } from 'umi';
import MonacoWebpackPlugin from 'monaco-editor-webpack-plugin';

export default defineConfig({
	title: 'Taier | DTStack',
	favicon: 'images/favicon.png',
	targets: {
		ios: false,
	},
	nodeModulesTransform: {
		type: 'none',
	},
	routes: [
		{
			path: '/',
			component: '@/layout/index',
			routes: [
				{
					path: '/',
					component: '@/pages/index',
				},
			],
		},
	],
	chainWebpack(memo) {
		memo.output.globalObject('this').set('globalObject', 'this');
		memo.entry('sparksql.worker').add(
			'monaco-sql-languages/out/esm/sparksql/sparksql.worker.js',
		);
		memo.entry('hivesql.worker').add(
			'monaco-sql-languages/out/esm/hivesql/hivesql.worker.js',
		);
		memo.entry('mysql.worker').add(
			'monaco-sql-languages/out/esm/mysql/mysql.worker.js',
		);
		memo.plugin('monaco-editor').use(MonacoWebpackPlugin, [
			{
				languages: ['markdown', 'json'],
			},
		]);
		return memo;
	},
	esbuild: {},
	theme: {
		'primary-color': '#3f87ff',
		'border-radius-base': '0px',
	},
	tailwindcss: {},
	proxy: {
		'/taier': {
			target: 'http://172.16.100.225:7001/proxy/156',
			changeOrigin: true,
			secure: false,
		},
	},
});
