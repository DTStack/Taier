const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HAPPY_PACK = require.resolve('happypack/loader');
const BASE_NAME = '/datasource/'; // 资源目录 默认访问路径
const ROOT_PATH = path.resolve(__dirname, './');
const BUILD_PATH = path.resolve(ROOT_PATH, `dist${BASE_NAME}`);
const packageName = require('./package.json').name;

const copyConfig = [
	{ from: path.resolve(__dirname, 'public/assets'), to: 'assets' },
	{ from: path.resolve(__dirname, 'public/public'), to: 'public' },
];

if (process.env.NODE !== 'production') {
	copyConfig.push({ from: path.resolve(__dirname, 'public/mock'), to: 'mock' });
}

module.exports = () => {
	return {
		server: {
			host: '127.0.0.1',
			port: 8083,
		},
		// proxy,
		webpack: {
			entry: './src/app.tsx',
			output: {
				path: BUILD_PATH,
				publicPath: BASE_NAME,
				globalObject: 'window',
				jsonpFunction: `webpackJsonp_${packageName}`,
				library: `Datasource`,
				libraryTarget: 'umd',
			},
			resolve: {
				alias: {
					'antd/lib/modal$': path.resolve(__dirname, './src/assets/modal/index.js'),
					'antd/lib/message$': path.resolve(__dirname, './src/assets/message/index.js'),
					'antd/lib/notification$': path.resolve(
						__dirname,
						'./src/assets/notification/index.js',
					),
				},
			},
			module: {
				rules: [
					{
						test: /\.(ts|tsx)$/,
						// TODO: exlude，include配置未生效问题
						// exclude: (modulePath) => {
						//   return /---/.test(modulePath);
						// },
						// include: [
						//   path.resolve(__dirname, 'src'),
						//   // path.resolve
						// ],
						loader: HAPPY_PACK,
						options: {
							id: 'happy-babel-ts',
						},
					},
					{
						test: /\.(png|jpe?g|gif|webp|woff2?|eot|ttf|otf)$/i,
						use: [
							{
								loader: 'url-loader',
								options: {},
							},
						],
					},
				],
			},
			plugins: [new CopyWebpackPlugin(copyConfig)],
			externals: {
				APP_CONF: 'APP_CONF',
			},
		},
	};
};
