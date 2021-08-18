const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HAPPY_PACK = require.resolve('happypack/loader');
const InsertHtmlPlugin = require('./plugins/insert-html-webpack-plugin');
const BASE_NAME = '/datasource/'; // 资源目录 默认访问路径
const ROOT_PATH = path.resolve(__dirname, './');
const BUILD_PATH = path.resolve(ROOT_PATH, `dist${BASE_NAME}`);
const packageName = require('./package.json').name;
const PUBLICPATH =
  process.env.NODE_ENV === 'production'
    ? 'http://schedule.dtstack.cn/datasource'
    : `http://localhost:8082`;

const copyConfig = [
  { from: path.resolve(__dirname, 'public/config'), to: 'config' },
  { from: path.resolve(__dirname, 'public/assets'), to: 'assets' },
  // dt-common定制化配置
  { from: path.resolve(__dirname, 'public/public'), to: 'public' },
];

if (process.env.NODE !== 'production') {
  copyConfig.push({ from: path.resolve(__dirname, 'public/mock'), to: 'mock' });
}

module.exports = () => {
  return {
    server: {
      host: '127.0.0.1',
      port: 8082,
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
      plugins: [
        new CopyWebpackPlugin(copyConfig),
        new InsertHtmlPlugin({ addCode: PUBLICPATH }),
      ],
      externals: {
        APP_CONF: 'APP_CONF',
      },
    },
  };
};
