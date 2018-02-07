const path = require('path');
const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const MY_PATH = require('./consts');

const baseConf = require('./base.js')();
var config = require('./config');

baseConf.plugins.push(
    new webpack.DefinePlugin({
        'process.env': {
            'NODE_ENV': JSON.stringify('development')
        }
    }),
    new webpack.SourceMapDevToolPlugin({
        filename: '[file].map'
    }),
    new webpack.HotModuleReplacementPlugin(), // 开启全局的模块热替换(HMR)
    new webpack.NamedModulesPlugin(), // 当模块热替换(HMR)时在浏览器控制台输出对用户更友好的模块名字信息
    new HtmlWebpackPlugin({
        filename: 'index.html',
        template: path.resolve(MY_PATH.WEB_PUBLIC, 'main/index.html'),
        inject: 'body',
        chunks: ['vendor', 'main', 'manifest'],
        showErrors: true,
        hash: true
    }),
    new HtmlWebpackPlugin({
        filename: 'rdos.html',
        template: path.resolve(MY_PATH.WEB_PUBLIC, 'rdos/index.html'),
        inject: 'body',
        chunks: ['vendor', 'rdos', 'manifest'],
        showErrors: true,
        hash: true
    }),
    new HtmlWebpackPlugin({
        filename: 'dataQuality.html',
        template: path.resolve(MY_PATH.WEB_PUBLIC, 'dataQuality/index.html'),
        inject: 'body',
        chunks: ['vendor', 'quality', 'manifest'],
        showErrors: true,
        hash: true
    }),
)

const devServer = Object.assign({
            hot: true, // 开启服务器的模块热替换
            host: '0.0.0.0',
            port: 8080,
            historyApiFallback: true,
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
        devServer: devServer
    })
}

module.exports = merged;