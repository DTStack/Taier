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
                // target: "http://172.16.10.51:9020",
                target: "http://172.16.8.104:9020", // dev server
                // target: "http://127.0.0.1:3001", // tmp server
                // ignorePath: true,  
                // Mock
                changeOrigin: true,
                secure: false,
            },
            "/api/streamapp": {
                // target: "http://172.16.1.191:9021", // formal test ser
                // target: "http://172.16.8.104:9020", // formal test ser
                target: "http://172.16.8.105:9021", // formal test ser
                // target: "http://172.16.8.162:9020", // tmp test server
                // ignorePath: true,  
                // Mock
                // pathRewrite:{"^/api/streamapp":"/api/rdos"},
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
                target: "http://172.16.8.107:8087",//开发环境

                // pathRewrite:{"^/api/da/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/da"},
                changeOrigin: true,
                secure: false,
            },
            "/api/tag": { // 数据标签
                target: "http://172.16.8.107:8085",//开发环境
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false,
            },
            "/api/console": { // 控制台
                target: "http://172.16.8.107:8084",//开发环境
                // target: "http://172.16.1.195:8084",   
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false,
            },
            "/api/analysis": { // 分析引擎
                target: "http://172.16.8.105:9022",// 开发环境
                // target: "http://172.16.10.45:9022", // 测试环境
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false,
            },
        }
    },
};