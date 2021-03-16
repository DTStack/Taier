const path = require('path');
const corejs = require.resolve('core-js/stable');
const regenerator = require.resolve('regenerator-runtime/runtime');
const CopyWebpackPlugin = require('copy-webpack-plugin');

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
    proxy: [
      {
        path: '/publicService/v1/**',
        target: 'http://172.16.101.189:8077',
        changeOrigin: true,
      },
    ],
    dll: [
      'react',
      'react-dom',
      'redux',
      'react-redux',
      'react-router-dom',
      'classnames',
    ],
    webpack: {
      entry: [corejs, regenerator, './src/index.tsx'],
      plugins: [
        new CopyWebpackPlugin({
          patterns: copyConfig,
        }),
      ],
      externals: {
        frontConf: 'frontConf',
      },
    },
  };
};
