const path = require("path");
const webpack = require("webpack");
const webpackMerge = require("webpack-merge");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const FriendlyErrorsWebpackPlugin = require("friendly-errors-webpack-plugin");
const cssLoader = require("./loader/css-loader.js").dev;
const notifier = require("node-notifier");
const ForkTsCheckerNotifierWebpackPlugin = require('fork-ts-checker-notifier-webpack-plugin');
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');

const MY_PATH = require("./consts");

const baseConf = require("./base.js")();
var config = require("./config");

/**
 * Sets process.env.NODE_ENV on DefinePlugin to value development. Enables NamedChunksPlugin and NamedModulesPlugin.
 *  **/
baseConf.mode = "development";

baseConf.plugins.push(
    new FriendlyErrorsWebpackPlugin({
        clearConsole: true,
        onErrors: function(severity, errors) {
            if (severity !== "error") return;

            const error = errors[0];
            const filename = error.file && error.file.split("!").pop();

            notifier.notify({
                title: "构建出错啦",
                message: severity + ": " + error.name,
                subtitle: filename || "",
                icon: path.join(__dirname, "icon.png")
            });
        }
    }),
    new webpack.SourceMapDevToolPlugin({
        filename: "[file].map",
        columns: false
    }),
    new webpack.LoaderOptionsPlugin({ options: {} }),
    new webpack.HotModuleReplacementPlugin() // 开启全局的模块热替换(HMR)
);

const devServer = Object.assign(
    {
        hot: true, // 开启服务器的模块热替换
        host: "0.0.0.0",
        port: 8080,
        historyApiFallback: true,
        disableHostCheck: true,
        quiet: true,
        stats: {
            colors: true,
            "errors-only": false,
            cached: true
        },
        useLocalIp: true,
        watchOptions: {
            ignored: /node_modules/,
            aggregateTimeout: 600,
        },
        contentBase: baseConf.output.path,
        publicPath: baseConf.output.publicPath
    },
    config.server
);

const merged = function(env) {
    return webpackMerge(baseConf, {
        devtool: "cheap-module-eval-source-map", //
        devServer: devServer,
        plugins: [
            new ForkTsCheckerWebpackPlugin({
                eslint: true,
                compilerOptions: {
                    skipLibCheck: true
                },
                reportFiles: ['src/**/*.{ts,tsx}']
            }),
            new ForkTsCheckerNotifierWebpackPlugin({ title: 'TypeScript', excludeWarnings: false }),
            new HtmlWebpackPlugin({
                filename: 'index.html',
                template: path.resolve(MY_PATH.WEB_PUBLIC, `index.html`),
                inject: "body",
                chunks: ['app', "manifest"],
                showErrors: true,
                hash: true
            })
        ],
        module: {
            rules: [...cssLoader]
        }
    });
};

module.exports = merged;
