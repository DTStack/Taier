const path = require("path");
const webpack = require("webpack");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const HappyPack = require("happypack");
const ProgressBarPlugin = require('progress-bar-webpack-plugin');
const os = require("os");
const happyThreadPool = HappyPack.ThreadPool({ size: os.cpus().length });

const MY_PATH = require("./consts");
const monacoConfig = require("./monacoConfig");
const splitChunksConfig = require("./splitChunksConfig");
const VERSION = JSON.stringify(require("../package.json").version); // app version.

module.exports = function () {
    return {
        entry: {
            main: MY_PATH.MAIN_APP_FILE,
            rdos: MY_PATH.RDOS_APP_FILE,
            stream: MY_PATH.STREAM_APP_FILE,
            dataQuality: MY_PATH.DATA_QUALITY_APP_FILE,
            dataApi: MY_PATH.DATA_API_APP_FILE,
            dataLabel: MY_PATH.DATA_LABEL_APP_FILE,
            console: MY_PATH.CONSOLE_APP_FILE,
            analyticsEngine: MY_PATH.ANALYTICS_ENGINE_APP_FILE,
        },
        output: {
            path: MY_PATH.BUILD_PATH,
            chunkFilename: "[name].[chunkhash].js",
            filename: "[name].[chunkhash].js",
            sourceMapFilename: "[name].map",
            publicPath: "/",
            globalObject: 'self',
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
                    test: /\.(jpg|png|gif)$/,
                    loader: ["file-loader", "url-loader?limit=100000"]
                },
                {
                    test: /\.(eot|woff|svg|ttf|woff2|gif|appcache|webp)(\?|$)/,
                    loader: [
                        "file-loader?name=[name].[ext]",
                        "url-loader?limit=100000"
                    ]
                },
                {
                    test: /\.worker.js$/,
                    exclude: /node_modules/,
                    use: {
                        loader: 'worker-loader'
                    }
                }
            ]
        },
        resolve: {
            modules: ["node_modules"],
            extensions: [".js", ".jsx", ".scss", ".css"], //后缀名自动补全
            alias: {
                // 全局公共模块目录
                utils: path.resolve(MY_PATH.APP_PATH, "utils"), // 工具文件夹
                widgets: path.resolve(MY_PATH.APP_PATH, "widgets"), // 工具文件夹
                consts: path.resolve(MY_PATH.APP_PATH, "consts"), // 工具文件夹
                styles: path.resolve(MY_PATH.APP_PATH, "styles"), // 样式文件
                funcs: path.resolve(MY_PATH.APP_PATH, "funcs"), // 零碎的公共方法，抽象成工具类的则移至utils目录
                public: path.resolve(MY_PATH.APP_PATH, "public"), // 公共资源
                config: path.resolve(MY_PATH.APP_PATH, "config"), // 数据文件，后期可能替换为借口

                // 应用根目录
                main: MY_PATH.MAIN_APP_PATH, // 主应用
                rdos: MY_PATH.RDOS_PATH, // RDOS
                stream: MY_PATH.STREAM_PATH, // 流计算
                dataQuality: MY_PATH.DATA_QUALITY_PATH, // 数据质量
                dataApi: MY_PATH.DATA_API_PATH, // 数据API
                dataLabel: MY_PATH.DATA_LABEL_PATH, // 标签工厂
                console: MY_PATH.CONSOLE_PATH, // 控制台
                dataMap: MY_PATH.DATA_MAP_PATH, // 数据地图
                metaData: MY_PATH.META_DATA_PATH, // 元数据
                coreData: MY_PATH.CORE_DATA_PATH, // 主数据
                analytics: MY_PATH.ANALYTICS_ENGINE_PATH,//分析引擎
            }
        },
        plugins: [
            new webpack.HashedModuleIdsPlugin(),
            new MonacoWebpackPlugin({
                features: monacoConfig.features,
                languages: monacoConfig.languages
            }),
            new HappyPack({
                id: "happy-babel-js",
                loaders: ["babel-loader?cacheDirectory=true"],
                threadPool: happyThreadPool,
            }),
            new MiniCssExtractPlugin({
                //提取为外部css代码
                filename: "[name].css?v=[contenthash]"
            }),
            new CopyWebpackPlugin([
                {
                    from: path.resolve(MY_PATH.WEB_PUBLIC),
                    to: path.resolve(MY_PATH.BUILD_PATH, "public"),
                    ignore: ["*/index.html"]
                }, {
                    from: path.resolve(MY_PATH.ROOT_PATH, 'docs'),
                    to: path.resolve(MY_PATH.BUILD_PATH, "docs"),
                }, {
                    from: path.resolve(MY_PATH.ROOT_PATH, '*.md'),
                    to: path.resolve(MY_PATH.BUILD_PATH, "docs"),
                }, {
                    from: path.resolve(MY_PATH.PWA, 'sw.js'),
                    to: path.resolve(MY_PATH.BUILD_PATH),
                }, {
                    from: path.resolve(MY_PATH.PWA, 'manifest.json'),
                    to: path.resolve(MY_PATH.BUILD_PATH),
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
