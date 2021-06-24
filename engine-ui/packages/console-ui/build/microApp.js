const path = require("path");
const webpack = require("webpack");
const webpackMerge = require("webpack-merge");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const cssLoader = require("./loader/css-loader.js").pro;

const TerserPlugin = require('terser-webpack-plugin');

const MY_PATH = require("./consts");
const baseConf = require("./base.js")();
const packageName = require('../package.json').name;
const base = require("./base.js");

baseConf.entry = {
    main: path.resolve(MY_PATH.APP_PATH, 'microAppEntry'),
}

baseConf.output = {
    path: MY_PATH.BUILD_PATH,
    publicPath: MY_PATH.BASE_NAME,
    library: `DTConsoleApp`,
    libraryTarget: 'umd',
    globalObject: 'window',
    jsonpFunction: `webpackJsonp_${packageName}`,
};

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
    minifyURLs: true
};

/**
 * Sets process.env.NODE_ENV on DefinePlugin to value production. 
 * Enables FlagDependencyUsagePlugin, FlagIncludedChunksPlugin, ModuleConcatenationPlugin, NoEmitOnErrorsPlugin, OccurrenceOrderPlugin, SideEffectsFlagPlugin and UglifyJsPlugin
 *  **/
baseConf.mode = "production";

baseConf.optimization.minimize = false;
baseConf.optimization.minimizer = [
    new TerserPlugin({
        parallel: true,
        cache: true,
        terserOptions: {
            mangle: true, // Note `mangle.properties` is `false` by default.
            keep_classnames: true,
            keep_fnames: true,
            compress: {
                drop_console: false,
                drop_debugger: true
            }
        }
    })
];

module.exports = function(env) {
    return webpackMerge(baseConf, {
        plugins: [
            new HtmlWebpackPlugin({
                filename: 'index.html',
                template: path.resolve( MY_PATH.WEB_PUBLIC, `index.html`),
                inject: "body",
                chunks: ["manifest", 'main'],
                chunksSortMode: 'manual',
                showErrors: true,
                hash: true,
                minify: htmlMinify
            })
        ],
        module: {
            rules: [...cssLoader]
        }
    });
};
