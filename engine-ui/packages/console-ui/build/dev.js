const path = require('path');
const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const cssLoader=require("./loader/css-loader.js").dev;
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

const MY_PATH = require('./consts');

const baseConf = require('./base.js')();
var config = require('./config');
baseConf.output={
    path: MY_PATH.BUILD_PATH,
            chunkFilename: '[name].js',
            filename: '[name].js',
            sourceMapFilename: '[name].map',
            publicPath: '/'
}
baseConf.mode="development"
baseConf.plugins.push(
    new BundleAnalyzerPlugin(),
    new webpack.SourceMapDevToolPlugin({
        filename: '[file].map'
    }),
    new webpack.HotModuleReplacementPlugin(), // 开启全局的模块热替换(HMR)
)


const htmlPlugs = [];
function loadHtmlPlugs() {
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
                    chunks: ['vendor',app.id, 'manifest'],
                    showErrors: true,
                    hash: true,
                })
            )
        }
    }
}

loadHtmlPlugs();

const devServer = Object.assign({
            hot: true, // 开启服务器的模块热替换
            host: '0.0.0.0',
            port: 8080,
            historyApiFallback: true,
            disableHostCheck:true,
            stats: {
                colors: true,
                'errors-only': true,
                cached: true,
            },
            contentBase: baseConf.output.path,
            publicPath: baseConf.output.publicPath
        }, config.server)

const merged = function(env) {
    return webpackMerge(baseConf, {
        devtool: 'cheap-module-eval-source-map', //
        devServer: devServer,
        plugins: htmlPlugs,
        module:{
            rules:[
                ...cssLoader
            ]
        }
        
    })
}

module.exports = merged;