const path = require('path');
const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const MY_PATH = require('./consts');

const baseConf = require('./base.js')();

const htmlMinify = {
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
}

baseConf.plugins.push(
    new webpack.DefinePlugin({
        'process.env': {
            'NODE_ENV': JSON.stringify('production')
        }
    }),
    new webpack.optimize.LimitChunkCountPlugin({maxChunks: 15}),
    new webpack.optimize.MinChunkSizePlugin({minChunkSize: 10000}),
    new webpack.LoaderOptionsPlugin({
        minimize: true,
        debug: false
    }),
    new webpack.optimize.UglifyJsPlugin({
        compress: {
            warnings: false,
            drop_console: true,
            // This feature has been reported as buggy a few times, such as:
            // https://github.com/mishoo/UglifyJS2/issues/1964
            // We'll wait with enabling it by default until it is more solid.
            reduce_vars: false,
        },
        output: {
            comments: false,
        },
    }),
    new HtmlWebpackPlugin({
        filename: 'index.html',
        template: path.resolve(MY_PATH.WEB_PUBLIC, 'main/index.html'),
        inject: 'body',
        chunks: ['vendor', 'main', 'manifest'],
        showErrors: true,
        hash: true,
        minify: htmlMinify,
    }),
    new HtmlWebpackPlugin({
        filename: 'rdos.html',
        template: path.resolve(MY_PATH.WEB_PUBLIC, 'rdos/index.html'),
        inject: 'body',
        chunks: ['vendor', 'rdos', 'manifest'],
        showErrors: true,
        hash: true,
        minify: htmlMinify,
    }),
    new HtmlWebpackPlugin({
        filename: 'dataQuality.html',
        template: path.resolve(MY_PATH.WEB_PUBLIC, 'dataQuality/index.html'),
        inject: 'body',
        chunks: ['vendor', 'quality', 'manifest'],
        showErrors: true,
        hash: true,
        minify: htmlMinify,
    }),
)

module.exports = function(env) {
    return webpackMerge(baseConf)
}