const path = require('path');
const corejs = require.resolve('core-js/stable');
const regenerator = require.resolve('regenerator-runtime/runtime');
const proxy = require('./config');
const CopyWebpackPlugin = require('copy-webpack-plugin');

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
      port: 8080,
    },
    proxy,
    dll: [
      // 'react',
      // 'react-dom',
      // 'redux',
      // 'react-redux',
      // 'react-router',
      'classnames',
    ],
    webpack: {
      entry: [corejs, regenerator, './src/app.tsx'],
      output: {
        publicPath: isDev ? '/' : '/publicService/',
      },
      plugins: [new CopyWebpackPlugin(copyConfig)],
      externals: {
        APP_CONF: 'APP_CONF',
      },
    },
  };
};
