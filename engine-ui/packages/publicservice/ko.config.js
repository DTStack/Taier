const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = (context) => {
  // const { webpack } = context;
  return {
    server: {
      "host": '127.0.0.1',
      "port": "8090"
    },
    proxy: [{
      "path": '/westLake/**',
      // "target": 'http://172.16.10.91:12000', //http://172.16.8.194:9999/
      "target": 'http://172.16.10.91:84', //http://172.16.8.194:9999/
      "changeOrigin": true
    }],
    dll:[
    "moment","mirror-creator","lodash",
    "echarts","echarts-wordcloud",'roo-tool',
    "immutable","classnames",'object-assign'
    ], 
    webpack: {
      //  entry: {
      //  index:path.resolve(__dirname,'src/index.tsx')
      //  },
      output: {},
      module: {
        rules: []
      },
      plugins:[
        new CopyWebpackPlugin([ 
            {from: path.resolve(__dirname,'public/config'),to:'config'},
            {from: path.resolve(__dirname,'public/mock'),to:'mock'},
            {from: path.resolve(__dirname,'public/assets'),to:'assets'},
          ]),
      ],
      externals :{
        'FRONT_CONF': 'FRONT_CONF',
      }
    }
  };
};