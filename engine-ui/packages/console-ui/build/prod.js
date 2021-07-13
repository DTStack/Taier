const path = require("path");
const webpack = require("webpack");
const webpackMerge = require("webpack-merge");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const cssLoader = require("./loader/css-loader.js").pro;
const VersionPlugin = require("./version-webpack-plugin");

// const BundleAnalyzerPlugin = require("webpack-bundle-analyzer").BundleAnalyzerPlugin;
// baseConf.plugins.push(
//     new BundleAnalyzerPlugin(),
//     // new ImageminPlugin({ test: /\.(jpe?g|png|gif|svg)$/i, quality: 80 }),
//     // new webpack.DefinePlugin({
//     //     'process.env': {
//     //         'NODE_ENV': JSON.stringify('production')
//     //     }
//     // }),
// );

//centos有bug,暂不启动
// const ImageminPlugin = require("imagemin-webpack-plugin").default;
const TerserPlugin = require('terser-webpack-plugin');

const MY_PATH = require("./consts");
const baseConf = require("./base.js")();

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

baseConf.optimization.minimizer = [
    new TerserPlugin({
        parallel: true,
        cache: true,
        terserOptions: {
            compress: {
                drop_console: true,
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
                showErrors: true,
                hash: true,
                minify: htmlMinify
            }),
            new VersionPlugin()
        ],
        module: {
            rules: [...cssLoader]
        }
    });
};
