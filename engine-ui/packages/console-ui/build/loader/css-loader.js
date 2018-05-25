const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const theme = require('../../src/theme')();


module.exports={
	dev:[
        {
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
}

