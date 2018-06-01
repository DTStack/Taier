const path = require('path');
const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const cssLoader = require("./loader/css-loader.js").pro;
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;
const ImageminPlugin = require('imagemin-webpack-plugin').default

const MY_PATH = require('./consts');

const baseConf = require('./base.js')();

baseConf.plugins.push(
    new BundleAnalyzerPlugin(),
    new webpack.optimize.LimitChunkCountPlugin({ maxChunks: 15 }),
    new ImageminPlugin({ test: /\.(jpe?g|png|gif|svg)$/i, quality: 80 })
);
baseConf.mode = "production";

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

    for (var i = 0; i < appConfs.length; i++) {
        const app = appConfs[i];
        if (app.enable) {
            const tmp = path.resolve(MY_PATH.WEB_PUBLIC, `${app.id}/index.html`)
            htmlPlugs.push(
                new HtmlWebpackPlugin({
                    filename: app.filename,
                    template: tmp,
                    inject: 'body',
                    chunks: [app.id, 'manifest'],
                    showErrors: true,
                    hash: true,
                    minify: htmlMinify,
                })
            )
        }
    }
}

loadHtmlPlugs();

module.exports = function (env) {
    return webpackMerge(baseConf, {
        plugins: htmlPlugs,
        module: {
            rules: [
                ...cssLoader
            ]
        }
    })
}