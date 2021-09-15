const path = require('path');
const webpack = require('webpack');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const HappyPack = require('happypack');
const ProgressBarPlugin = require('progress-bar-webpack-plugin');
const os = require('os');
const happyThreadPool = HappyPack.ThreadPool({ size: os.cpus().length });
const InsertHtmlPlugin = require("./plugins/insert-html-webpack-plugin");

const PublicPath = require("./consts").PUBLICPATH
const MY_PATH = require('./consts');
const monacoConfig = require('./monacoConfig');
const splitChunksConfig = require('./splitChunksConfig');
const VERSION = JSON.stringify(require('../package.json').version); // app version.
const packageName = require('../package.json').name;

module.exports = function () {
    return {
        entry: {
            main: MY_PATH.APP_FILE,
        },
        output: {
            pathinfo: false,
            path: MY_PATH.BUILD_PATH,
            // chunkFilename: "[name].[hash].js",
            // filename: "[name].[hash].js",
            // sourceMapFilename: "[name].map",
            publicPath: MY_PATH.BASE_NAME,
            globalObject: 'window',
            jsonpFunction: `webpackJsonp_${packageName}`,
            library: `DTConsoleApp`,
            libraryTarget: 'umd',
        },
        optimization: {
            splitChunks: {
                chunks: "all",
                minSize: 30000,
                minChunks: 1,
                maxAsyncRequests: 5,
                maxInitialRequests: 8,
                automaticNameDelimiter: "~",
                name: true,
                cacheGroups: {
                    baseCommon: {
                        test: splitChunksConfig.baseCommonRegExp,
                        priority: 1
                    }
                }
            },
            runtimeChunk: {
                name: "manifest"
            }
        },
        node: {
            fs: 'empty',
            path: 'empty'
        },
        module: {
            rules: [
                {
                    test: /\.worker.[jt]s$/,
                    include: [
                        path.resolve(MY_PATH.APP_PATH)
                    ],
                    loader: [
                        'worker-loader',
                    ]
                },
                {
                    test: /\.[jt]sx?$/,
                    include: [
                        path.resolve(MY_PATH.APP_PATH)
                    ],
                    loader: [
                        "happypack/loader?id=happy-babel",
                    ]
                },
                {
                    test: /\.(jpg|png|gif)$/,
                    loader: ["file-loader", "url-loader?limit=100000"]
                },
                {
                    test: /\.(eot|woff|svg|ttf|woff2|gif|appcache|webp)(\?|$)/,
                    loader: [
                        "file-loader?name=[name].[ext]",
                        "url-loader?limit=100000"
                    ]
                }
            ]
        },
        resolve: {
            modules: ["node_modules"],
            extensions: [".ts", ".tsx", ".js", ".jsx", ".scss", ".css"], //后缀名自动补全
            alias: {
                'antd/lib/modal$': path.resolve(MY_PATH.ROOT_PATH, './build/assets/modal/index.js'),
                'antd/lib/message$': path.resolve(MY_PATH.ROOT_PATH, './build/assets/message/index.js'),
                'antd/lib/notification$': path.resolve(MY_PATH.ROOT_PATH, './build/assets/notification/index.js'),
                'react-dom': '@hot-loader/react-dom'
            }
        },
        context: process.cwd(), // to automatically find tsconfig.json
        plugins: [
            new webpack.HashedModuleIdsPlugin(),
            new MonacoWebpackPlugin({
                features: monacoConfig.features,
                languages: monacoConfig.languages
            }),
            new HappyPack({
                id: 'happy-babel',
                loaders: [{
                    loader: 'babel-loader',
                    options: {
                        cacheDirectory: true
                    }
                }],
                threadPool: happyThreadPool
            }),
            new MiniCssExtractPlugin({
                // 提取为外部css代码
                filename: '[name].css?v=[contenthash:8]'
            }),
            new InsertHtmlPlugin({
                addCode: PublicPath
            }),
            new CopyWebpackPlugin([
                {
                    from: path.resolve(MY_PATH.WEB_PUBLIC),
                    to: path.resolve(MY_PATH.BUILD_PATH, 'public'),
                    ignore: ['*/index.html']
                }, {
                    from: path.resolve(MY_PATH.ROOT_PATH, 'docs'),
                    to: path.resolve(MY_PATH.BUILD_PATH, 'docs')
                }, {
                    from: path.resolve(MY_PATH.ROOT_PATH, '*.md'),
                    to: path.resolve(MY_PATH.BUILD_PATH, 'docs')
                }, {
                    from: path.resolve(MY_PATH.PWA, 'sw.js'),
                    to: path.resolve(MY_PATH.BUILD_PATH)
                }, {
                    from: path.resolve(MY_PATH.PWA, 'manifest.json'),
                    to: path.resolve(MY_PATH.BUILD_PATH)
                }
            ]),
            new webpack.DefinePlugin({
                APP: {
                    VERSION: VERSION
                }
            }),
            new ProgressBarPlugin()
        ]
    };
};
