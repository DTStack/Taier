const path = require('path');
const webpack = require('webpack');
const HtmlWebPackPlugin = require('html-webpack-plugin');
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const ROOT_PATH = path.resolve(__dirname, '../');
const APP_PATH = path.resolve(ROOT_PATH, 'src'); // 应用根路径
const WEB_PUBLIC = path.resolve(ROOT_PATH, 'public'); // 公开资源
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const packageName = require('../package.json').name;
const StyleWebpackPlugin = require('./plugins/style-webpack-plugin');
const ReplaceWebpackPlugin = require('./plugins/prefix-webpack-plugin');

module.exports = {
    resolve: {
        extensions: ['.js', '.jsx', '.tsx', '.ts'],
        alias: {
            '@': APP_PATH,
            public: WEB_PUBLIC,
            react: path.resolve(__dirname, '../node_modules/react'),
            'react-dom': path.resolve(__dirname, '../node_modules/react-dom'),
            'monaco-editor': path.resolve(
                __dirname,
                '../node_modules/monaco-editor'
            ),
            'antd/lib/modal$': path.resolve(
                __dirname,
                '../src/assets/modal/index.js'
            ),
            'antd/lib/message$': path.resolve(
                __dirname,
                '../src/assets/message/index.js'
            ),
        },
        fallback: {
            fs: false,
            process: false,
        },
    },
    entry: {
        app: path.resolve(__dirname, '../src/index.tsx'),
        'sparksql.worker':
            'monaco-sql-languages/out/esm/sparksql/sparksql.worker.js',
    },
    output: {
        globalObject: 'this',
        path: path.resolve(__dirname, '../build'),
        chunkFilename: '[name].[contenthash].js',
        filename: '[name].js',
        clean: true,
    },
    module: {
        rules: [
            {
                test: /\.m?js/,
                resolve: {
                    fullySpecified: false,
                },
            },
            {
                test: /\.(js|jsx|tsx|ts)$/,
                exclude: /node_modules/,
                use: [
                    {
                        loader: 'babel-loader',
                        options: {},
                    },
                ],
            },
            {
                test: /\.css$/i,
                use: [MiniCssExtractPlugin.loader, 'css-loader'],
            },
            {
                test: /\.less$/i,
                include: /node_modules/,
                use: [
                    MiniCssExtractPlugin.loader,
                    {
                        loader: 'css-loader', // translates CSS into CommonJS
                    },
                    {
                        loader: 'less-loader', // compiles Less to CSS
                        options: {
                            modifyVars: {
                                '@ant-prefix': packageName,
                            },
                            javascriptEnabled: true,
                        },
                    },
                ],
            },
            {
                test: /\.scss$/i,
                use: [
                    MiniCssExtractPlugin.loader,
                    {
                        loader: 'css-loader', // translates CSS into CommonJS
                    },
                    {
                        loader: 'sass-loader',
                    },
                ],
            },
            {
                test: /\.(jpg|png|gif|eot|woff|svg|ttf|woff2|gif|appcache|webp)(\?|$)/,
                type: 'asset/resource',
            },
        ],
    },
    plugins: [
        new MonacoWebpackPlugin({
            languages: ['javascript', 'typescript', 'json', 'clojure'],
        }),
        new MiniCssExtractPlugin({
            filename: '[name].css?v=[contenthash:8]',
        }),
        new webpack.DefinePlugin({
            __DEVELOPMENT__: false,
        }),
        new HtmlWebPackPlugin({
            template: path.resolve(__dirname, '../public/index.html'),
        }),
        new StyleWebpackPlugin({
            modifyVars: {
                'ant-': `${packageName}-`,
            },
        }),
        new ReplaceWebpackPlugin({}),
    ],
};
