const path = require('path');
const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const cssLoader = require('./loader/css-loader.js').pro;
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer')
  .BundleAnalyzerPlugin;
// const ImageminPlugin = require("imagemin-webpack-plugin").default; //centos 有bug,暂不启动
const TerserPlugin = require('terser-webpack-plugin');

const MY_PATH = require('./consts');
const baseConf = require('./base.js')();

/**
 * Sets process.env.NODE_ENV on DefinePlugin to value production.
 * Enables FlagDependencyUsagePlugin, FlagIncludedChunksPlugin, ModuleConcatenationPlugin, NoEmitOnErrorsPlugin, OccurrenceOrderPlugin, SideEffectsFlagPlugin and UglifyJsPlugin
 *  **/
baseConf.mode = 'production';

// baseConf.plugins.push(
//     new BundleAnalyzerPlugin(),
//     // new ImageminPlugin({ test: /\.(jpe?g|png|gif|svg)$/i, quality: 80 }),
//     // new webpack.DefinePlugin({
//     //     'process.env': {
//     //         'NODE_ENV': JSON.stringify('production')
//     //     }
//     // }),
// );

baseConf.optimization.minimizer = [
  new TerserPlugin({
    parallel: true,
    cache: true,
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true,
      },
    },
  }),
];

module.exports = function (env) {
  return webpackMerge(baseConf, {
    plugins: [
      new HtmlWebpackPlugin({
        filename: 'index.html',
        template: path.resolve(MY_PATH.WEB_PUBLIC, `index.html`),
        inject: 'body',
        chunks: ['app', 'manifest'],
        showErrors: true,
        hash: true,
        minify: {
          removeComments: true,
          collapseWhitespace: true,
          removeRedundantAttributes: true,
          useShortDoctype: true,
          removeEmptyAttributes: true,
          removeStyleLinkTypeAttributes: true,
          keepClosingSlash: true,
          minifyJS: true,
          minifyCSS: true,
          minifyURLs: true,
        },
      }),
    ],
    module: {
      rules: [...cssLoader],
    },
  });
};
