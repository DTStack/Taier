const { merge } = require('webpack-merge');
const webpackConf = require('./webpack.base');
// const TerserPlugin = require('terser-webpack-plugin');

module.exports = function (env) {
    return merge(webpackConf, {
        mode: 'production',
        devtool: 'source-map',
        optimization: {
            minimizer: [
                // new TerserPlugin({
                //     parallel: true,
                //     terserOptions: {
                //         keep_classnames: true,
                //         keep_fnames: true,
                //     },
                // }),
            ],
        },
    });
};
