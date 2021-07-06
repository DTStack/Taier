const path = require('path');
const corejs = require.resolve('core-js/stable');
const regenerator = require.resolve('regenerator-runtime/runtime');
const proxy = require('./config');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HAPPY_PACK = require.resolve('happypack/loader');

const isDev = process.env.NODE_ENV === 'development';

const copyConfig = [
  { from: path.resolve(__dirname, 'public/config'), to: 'config' },
  { from: path.resolve(__dirname, 'public/assets'), to: 'assets' },
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
    proxy,
    dll: ['classnames'],
    webpack: {
      entry: [corejs, regenerator, './src/app.tsx'],
      output: {
        publicPath: isDev ? '/' : '/publicService/',
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
        ],
      },
      plugins: [new CopyWebpackPlugin(copyConfig)],
      externals: {
        APP_CONF: 'APP_CONF',
      },
    },
  };
};
