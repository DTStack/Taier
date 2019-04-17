'use strict';

module.exports = {
    server: {
        port: 8888,
        host: '0.0.0.0',
        proxy: {
            '/uic': { // UIC地址
                target: "http://127.0.0.1:3001", // tmp server
                changeOrigin: true,
                secure: false
            },
            '/api': { // da地址
                target: "http://127.0.0.1:3001", // tmp server
                changeOrigin: true,
                secure: false
            },
            '/public/helpSite': { // 分析引擎
                // target: 'http://172.16.8.104', // 开发环境
                target: "http://172.16.10.34", // 测试环境
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false
            }
        }
    }
};
