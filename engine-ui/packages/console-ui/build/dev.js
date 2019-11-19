const path = require("path");
const webpack = require("webpack");
const webpackMerge = require("webpack-merge");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const FriendlyErrorsWebpackPlugin = require("friendly-errors-webpack-plugin");
const cssLoader = require("./loader/css-loader.js").dev;
const notifier = require("node-notifier");

const MY_PATH = require("./consts");

const baseConf = require("./base.js")();
var config = require("./config");
/**
 * Sets process.env.NODE_ENV on DefinePlugin to value development. Enables NamedChunksPlugin and NamedModulesPlugin.
 *  **/
baseConf.mode = "development";

baseConf.output = {
    ...baseConf.output,
    path: MY_PATH.BUILD_PATH,
    chunkFilename: "[name].js",
    filename: "[name].js",
    sourceMapFilename: "[name].map",
    publicPath: "/"
};

// Add react-hot-loader
baseConf.module.rules[1].loader.unshift("react-hot-loader/webpack");


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

const htmlPlugs = [];
function loadHtmlPlugs() {
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
                    hash: true
                })
            );
        }
    }
}

loadHtmlPlugs();

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
        watchOptions: {
            aggregateTimeout: 300,
            poll: false,
            ignored: /node_modules/
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
        plugins: htmlPlugs,

        module: {
            rules: [...cssLoader]
        }
    });
};

module.exports = merged;
