const path = require("path");
const webpack = require("webpack");
const webpackMerge = require("webpack-merge");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const cssLoader = require("./loader/css-loader.js").pro;
const BundleAnalyzerPlugin = require("webpack-bundle-analyzer")
    .BundleAnalyzerPlugin;
//centos有bug,暂不启动
// const ImageminPlugin = require("imagemin-webpack-plugin").default;
const UglifyJsPlugin = require("uglifyjs-webpack-plugin");

const MY_PATH = require("./consts");

const baseConf = require("./base.js")();

/**
 * Sets process.env.NODE_ENV on DefinePlugin to value production. 
 * Enables FlagDependencyUsagePlugin, FlagIncludedChunksPlugin, ModuleConcatenationPlugin, NoEmitOnErrorsPlugin, OccurrenceOrderPlugin, SideEffectsFlagPlugin and UglifyJsPlugin
 *  **/
baseConf.mode = "production";

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
    new UglifyJsPlugin({
        parallel: true,
        cache: true,
        uglifyOptions: {
            compress: {
                drop_console: true,
                drop_debugger: true,
                ecma: 6,
            }
        }
    })
];

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
        minifyURLs: true
    };

    const appConfs = require(path.resolve(
        MY_PATH.APP_PATH,
        "config/defaultApps"
    ));

    for (var i = 0; i < appConfs.length; i++) {
        const app = appConfs[i];
        if (app.enable) {
            const tmp = path.resolve(
                MY_PATH.WEB_PUBLIC,
                `${app.id}/index.html`
            );
            htmlPlugs.push(
                new HtmlWebpackPlugin({
                    filename: app.filename,
                    template: tmp,
                    inject: "body",
                    chunks: [app.id, "manifest"],
                    showErrors: true,
                    hash: true,
                    minify: htmlMinify
                })
            );
        }
    }
}

loadHtmlPlugs();

module.exports = function(env) {
    return webpackMerge(baseConf, {
        plugins: htmlPlugs,
        module: {
            rules: [...cssLoader]
        }
    });
};
