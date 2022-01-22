const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const MY_PATH = require('../consts');

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
            sourceMapContents: false,
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
            javascriptEnabled: true,
          }
        }
      ]
    }
  ],
}

