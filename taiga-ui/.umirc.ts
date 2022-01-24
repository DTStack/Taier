import { defineConfig } from 'umi';
import MonacoWebpackPlugin from 'monaco-editor-webpack-plugin';

export default defineConfig({
	title: 'Taiga | DTstack',
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
		memo.plugin('monaco-editor').use(MonacoWebpackPlugin, [
			{
				languages: ['markdown'],
			},
		]);
		return memo;
	},
	esbuild: {},
	theme: {
		'primary-color': '#3f87ff',
	},
	tailwindcss: {},
	proxy: {
		'/node': {
			target: 'http://172.16.101.187:8090',
			// target: 'http://192.168.96.190:8090',
			changeOrigin: true,
			secure: false,
		},
		'/api/rdos': {
			target: 'http://172.16.101.187:8090/',
			// target: 'http://192.168.96.190:8090',
			changeOrigin: true,
			secure: false,
		},
	},
});
