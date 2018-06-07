'use strict';
module.exports = {
    server: {
        port: 8080,
        host: '0.0.0.0',
        proxy: {
            "/api/dq": {
                target: "http://172.16.8.104:8089",
                // target: "http://172.16.6.135:8089", // tmp server
                // target: "http://172.16.0.79:8089",
                // ignorePath: true, 
                changeOrigin: true,
                secure: false,
            },
            "/api/rdos": {
                // target: "http://172.16.8.104:9020", // formal test ser
                target: "http://172.16.1.101:9020", // local
                // target: "http://172.16.8.104:9020",test
                // ignorePath: true, 
                changeOrigin: true,
                secure: false,
            },
            "/uic": { // UIC地址
                target: "http://dtuic.dtstack.net",
                changeOrigin: true,
                secure: false,
            },
            "/api/da": { // da地址
                // target: "http://172.16.8.106",
                target: "http://172.16.8.107:8087",//测试环境

                // pathRewrite:{"^/api/da/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/da"},
                changeOrigin: true,
                secure: false,
            },
            "/api/tag": { // 数据标签
                target: "http://172.16.8.107:8085",//测试环境
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false,
            },
        }
    },
};