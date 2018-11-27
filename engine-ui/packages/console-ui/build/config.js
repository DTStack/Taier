'use strict';

module.exports = {
    server: {
        port: 8080,
        host: '0.0.0.0',
        proxy: {
            "/api/dq": {
                target: "http://172.16.8.192:8089",
                // target: "http://172.16.6.135:8089", // tmp server
                // target: "http://172.16.0.79:8089",
                // ignorePath: true, 
                changeOrigin: true,
                secure: false,
            },
            "/api/rdos": {
                // target: "http://172.16.10.51:9020", // formal test ser
                target: "http://172.16.8.192:9020", // formal test ser
                // target: "http://172.16.8.162:9020", // tmp test server
                // ignorePath: true,  
                // Mock
                changeOrigin: true,
                secure: false,
            },
            "/uic": { // UIC地址
                target: "http://dtuic.dtstack.net",
                changeOrigin: true,
                pathRewrite:{"^/uic":"/"},
                secure: false,
            },
            "/api/da": { // da地址
                // target: "http://172.16.1.104:8087",
                target: "http://172.16.8.192:8087",//测试环境

                // pathRewrite:{"^/api/da/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/da"},
                changeOrigin: true,
                secure: false,
            },
            "/api/tag": { // 数据标签
                target: "http://172.16.8.192:8085",//测试环境
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false,
            },
            "/api/console": { // 控制台
                target: "http://172.16.8.192:8084",//测试环境
                // target: "http://172.16.10.34:8084",
                // target: "http://172.16.0.225:8084",
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false,
            },

        }
    },
};