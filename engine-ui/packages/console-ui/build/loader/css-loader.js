const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const theme = require('../../src/theme')();

module.exports = {
  dev: [
    {
      test: /\.css$/,
      use: [
        'css-hot-loader',
        MiniCssExtractPlugin.loader,
        {
          loader: "css-loader",
          options: {
            sourceMap: true
          }
        }
      ]
    },
    {
      test: /\.scss$/,
      use: [
        'css-hot-loader',
        MiniCssExtractPlugin.loader,
        {
<<<<<<< e8dda9c91a5f7e0f3fc48c961ea4a0c234053014
<<<<<<< a2d378b31d5e11c8814b8a24c9c833d471f17749
          test:/\.css$/,
          use:[
          'css-hot-loader',
          MiniCssExtractPlugin.loader,
            {
              loader:"css-loader",
              options:{
                sourceMap:false
              }
            }
          ]
        },
        {
          test: /\.scss$/,
          use:[
           'css-hot-loader',
           MiniCssExtractPlugin.loader,
            {
              loader:"css-loader",
              options:{
                sourceMap:false
              }
            },
            {
              loader:"sass-loader",
              options:{
                sourceMap:false,
                outputStyle:"expanded",
                sourceMapContents:true
              }
            }
          ]
        },
        {
          test: /\.less$/,
          use:[
           'css-hot-loader',
            MiniCssExtractPlugin.loader,
            {
              loader:"css-loader",
              options:{
                sourceMap:false
              }
            },
            {
              loader:"less-loader",
              options:{
                sourceMap:false,
                modifyVars:theme,
                javascriptEnabled:true
              }
            }
          ]
        }
          
        
	],
	pro:[
   {
          test:/\.css$/,
          use:[
          MiniCssExtractPlugin.loader,
            {
              loader:"css-loader",
              options:{
                sourceMap:true
              }
            }
          ]
        },
        {
          test: /\.scss$/,
          use:[
           MiniCssExtractPlugin.loader,
            {
              loader:"css-loader",
              options:{
                sourceMap:true
              }
            },
            {
              loader:"sass-loader",
              options:{
                sourceMap:true,
                outputStyle:"expanded",
                sourceMapContents:true
              }
            }
          ]
        },
        {
          test: /\.less$/,
          use:[
           MiniCssExtractPlugin.loader,
            {
              loader:"css-loader",
              options:{
                sourceMap:true
              }
            },
            {
              loader:"less-loader",
              options:{
                sourceMap:true,
                modifyVars:theme,
                javascriptEnabled:true
              }
            }
          ]
        }

          
        ],
=======
=======
>>>>>>> conflict
          loader: "css-loader",
          options: {
            sourceMap: true
          }
        },
        {
          loader: "sass-loader",
          options: {
            sourceMap: true,
            sourceMapContents: true
          }
        }
      ]
    },
    {
      test: /\.less$/,
      use: [
        'css-hot-loader',
        MiniCssExtractPlugin.loader,
        {
          loader: "css-loader",
          options: {
            sourceMap: true
          }
        },
        {
          loader: "less-loader",
          options: {
            sourceMap: true,
            modifyVars: theme,
            javascriptEnabled: true
          }
        }
      ]
    }
  ],
  pro: [
    {
      test: /\.css$/,
      use: [
        MiniCssExtractPlugin.loader,
        {
          loader: "css-loader",
          options: {
            sourceMap: false
          }
        }
      ]
    },
    {
      test: /\.scss$/,
      use: [
        MiniCssExtractPlugin.loader,
        {
          loader: "css-loader",
          options: {
            sourceMap: false
          }
        },
        {
          loader: "sass-loader",
          options: {
            sourceMap: false,
            outputStyle: "expanded",
            sourceMapContents: false
          }
        }
      ]
    },
    {
      test: /\.less$/,
      use: [
        MiniCssExtractPlugin.loader,
        {
          loader: "css-loader",
          options: {
            sourceMap: false
          }
        },
        {
          loader: "less-loader",
          options: {
            sourceMap: false,
            modifyVars: theme,
            javascriptEnabled: true
          }
        }
      ]
    }
  ],
<<<<<<< e8dda9c91a5f7e0f3fc48c961ea4a0c234053014
>>>>>>> 图片压缩，更多模块分割，sourcemap减少
=======
>>>>>>> conflict
}

