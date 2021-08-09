const path = require('path');
const webpack = require('webpack');
const HtmlWebPackPlugin = require('html-webpack-plugin');
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const ROOT_PATH = path.resolve(__dirname, '../')
const APP_PATH = path.resolve(ROOT_PATH, 'src') // 应用根路径
const WEB_PUBLIC = path.resolve(ROOT_PATH, 'public') // 公开资源  

module.exports = {
    resolve: {
        extensions: ['.js', '.jsx', '.tsx', '.ts'],
        alias: {
            '@': APP_PATH,
            public: WEB_PUBLIC,
            'react': path.resolve(__dirname, '../node_modules/react'),
            'react-dom': path.resolve(__dirname, '../node_modules/react-dom'),
            'monaco-editor': path.resolve(
                __dirname,
                '../node_modules/monaco-editor'
            ),
        },
        fallback: {
            fs: false,
        },
    },
    entry: {
        app: path.resolve(__dirname, '../src/index.tsx'),
        // 'sparksql.worker': path.resolve(__dirname, '../src/sparksql/sparksql.worker.js'),
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
                use: ['style-loader', 'css-loader'],
            },
            {
                test: /\.less$/i,
                use: [
                    {
                        loader: 'style-loader', // creates style nodes from JS strings
                    },
                    {
                        loader: 'css-loader', // translates CSS into CommonJS
                    },
                    {
                        loader: 'less-loader', // compiles Less to CSS
                        options: {
                            javascriptEnabled: true,
                        },
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
        new webpack.DefinePlugin({
            __DEVELOPMENT__: false,
        }),
        new HtmlWebPackPlugin({
            template: path.resolve(__dirname, '../public/index.html'),
        }),
    ],
};
