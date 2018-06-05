const path = require('path');
const webpack = require('webpack');
const ExtractTextPlugin = require('extract-text-webpack-plugin');   // css单独打包
const CopyWebpackPlugin = require('copy-webpack-plugin');

const MY_PATH = require('./consts');
const VERSION = JSON.stringify(require('../package.json').version); // app version.
const theme = require('../src/theme')();

module.exports = function() {
    return {
        entry: {
            main: MY_PATH.MAIN_APP_FILE,
            rdos: MY_PATH.RDOS_APP_FILE,
            dataQuality: MY_PATH.DATA_QUALITY_APP_FILE,
            dataApi: MY_PATH.DATA_API_APP_FILE,
            dataLabel: MY_PATH.DATA_LABEL_APP_FILE,
            vendor: [
                'react', 'react-dom',
                'react-router', 'prop-types',
                'react-redux', 'redux',
                'react-router-redux', 'redux-thunk',
                'moment', 'lodash', 'mirror-creator',
                'object-assign',
            ],
        },
        output: {
            path: MY_PATH.BUILD_PATH,
            chunkFilename: '[name].[hash].js',
            filename: '[name].[hash].js',
            sourceMapFilename: '[name].map',
            publicPath: '/'
        },
        module: {
            rules: [
                {
                    test: /\.js$/,
                    enforce: "pre",
                    include: MY_PATH.APP_PATH,
                    exclude: [
                        path.resolve(MY_PATH.ROOT_PATH, 'node_modules'),
                        path.resolve(MY_PATH.WEB_PUBLIC),
                    ],
                    loader: ['babel-loader']
                }, {
                    test: /\.css$/,
                    use: ['css-hot-loader'].concat(ExtractTextPlugin.extract({
                        fallback: "style-loader",
                        use: ['css-loader?sourceMap']
                    })),
                }, {
                    test: /\.scss$/,
                    use: ['css-hot-loader'].concat(ExtractTextPlugin.extract({
                        fallback: "style-loader",
                        use: ["css-loader?sourceMap", "sass-loader?outputStyle=expanded&sourceMap=true&sourceMapContents=true"]
                    }))
                }, {
                    test: /\.less$/,
                    use: ExtractTextPlugin.extract({
                        fallback: "style-loader",
                        use:["css-loader?sourceMap", 
                            `less-loader?{"sourceMap":true,
                            "modifyVars": ${JSON.stringify(theme)}}`
                        ] //
                    }),
                },{
                    test: /\.(jpg|png|gif)$/,
                    loader: ['file-loader', "url-loader?limit=100000"]
                }, {
                    test: /\.(eot|woff|svg|ttf|woff2|gif|appcache|webp)(\?|$)/,
                    loader: ['file-loader?name=[name].[ext]', "url-loader?limit=100000"]
                }
            ]
        },
        resolve: {
            modules: ['node_modules'],
            extensions: ['.js', '.jsx', '.scss', '.css'],    //后缀名自动补全
            alias: {
                // 全局公共模块目录
                utils: path.resolve(MY_PATH.APP_PATH, 'utils'),     // 工具文件夹
                widgets: path.resolve(MY_PATH.APP_PATH, 'widgets'), // 工具文件夹
                consts: path.resolve(MY_PATH.APP_PATH, 'consts'),   // 工具文件夹
                styles: path.resolve(MY_PATH.APP_PATH, 'styles'),   // 样式文件
                funcs: path.resolve(MY_PATH.APP_PATH, 'funcs'),     // 零碎的公共方法，抽象成工具类的则移至utils目录
                public: path.resolve(MY_PATH.APP_PATH, 'public'),   // 公共资源
                config: path.resolve(MY_PATH.APP_PATH, 'config'),   // 数据文件，后期可能替换为借口

                // 应用根目录
                main: MY_PATH.MAIN_APP_PATH,            // 主应用
                rdos: MY_PATH.RDOS_PATH,                // RDOS
                dataQuality: MY_PATH.DATA_QUALITY_PATH, // 数据质量
                dataApi: MY_PATH.DATA_API_PATH,         // 数据API
                dataLabel: MY_PATH.DATA_LABEL_PATH,     // 标签工厂
                dataMap: MY_PATH.DATA_MAP_PATH,         // 数据地图
                metaData: MY_PATH.META_DATA_PATH,       // 元数据
                coreData: MY_PATH.CORE_DATA_PATH,       // 主数据
            }
        },
        plugins: [
            new ExtractTextPlugin('[name].[hash].css'),
            new CopyWebpackPlugin([{
                from: path.resolve(MY_PATH.WEB_PUBLIC),
                to: path.resolve(MY_PATH.BUILD_PATH, 'public'), 
                ignore: ['*/index.html']
            }]),

            new webpack.optimize.CommonsChunkPlugin({
                names: ['vendor', 'manifest'],    // 指定公共 bundle 的名字。
                minChunks: Infinity
            }),
        ]
    }
}