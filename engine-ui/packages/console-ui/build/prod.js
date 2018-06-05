const path = require('path');
const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const VERSION = JSON.stringify(require('../package.json').version); // app version.
// const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

const MY_PATH = require('./consts');

const baseConf = require('./base.js')();

baseConf.plugins.push(
    // new BundleAnalyzerPlugin(),
    new webpack.DefinePlugin({
        'process.env': {
            'NODE_ENV': JSON.stringify('production'),
            'VERSION': VERSION,
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
    })
);


const htmlPlugs = [];
function loadHtmlPlugs() {

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

    const appConfs = require(path.resolve(MY_PATH.APP_PATH, 'config/defaultApps'));

    for (var i = 0 ; i < appConfs.length; i++) {
        const app = appConfs[i];
        if (app.enable) {
            const tmp = path.resolve(MY_PATH.WEB_PUBLIC, `${app.id}/index.html`)
            htmlPlugs.push(
                new HtmlWebpackPlugin({
                    filename: app.filename,
                    template: tmp,
                    inject: 'body',
                    chunks: ['vendor', app.id, 'manifest'],
                    showErrors: true,
                    hash: true,
                    minify: htmlMinify,
                })
            )
        }
    }
}

loadHtmlPlugs();

module.exports = function(env) {
    return webpackMerge(baseConf, {
        plugins: htmlPlugs,
    })
}