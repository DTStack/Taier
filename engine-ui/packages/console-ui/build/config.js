'use strict';

module.exports = {
    server: {
        port: 8080,
        host: '0.0.0.0',
        proxy: {
            '/api/dq': {
                target: 'http://172.16.8.108:8089', // 开发环境
                // target: "http://172.16.6.135:8089", // tmp server
                // target: "http://172.16.10.45:8089",
                // ignorePath: true,
                changeOrigin: true,
                secure: false
            },
            '/api/rdos': {
                // target: 'http://172.16.10.86:9020', // test
                // target: "http://172.16.8.107:9020", // dev server
                // target: "http://172.16.0.14:9020", // dev server
                target: "http://172.16.1.22:9020", // dev server
                // target: "http://172.16.10.51:9020", // test
                // target: "http://172.16.10.97:9020",
                // target: "http://127.0.0.1:3001", // tmp server
                // ignorePath: true,
                // Mock
                changeOrigin: true,
                secure: false
            },
            '/api/streamapp': {
                // target: "http://172.16.1.191:9021", // formal test ser
                // target: "http://172.16.8.104:9020", // formal test ser
                // target: "http://172.16.8.105:9021", // formal test ser
                target: 'http://172.16.10.86:9021', // test
                // ignorePath: true,
                // Mock
                // pathRewrite:{"^/api/streamapp":"/api/rdos"},
                changeOrigin: true,
                secure: false
            },
            '/uic': { // UIC地址
                // target: 'http://dtuic.dtstack.net',
                target: 'http://uic.insight.cn',
                // target: "http://uic.insight.com",
                // target: 'http://172.16.1.92:8668',
                changeOrigin: true,
                // pathRewrite:{"^/uic":"/"},
                secure: false
            },
            '/api/dataScience': { // 算法平台
                target: 'http://172.16.8.107:9029', // 开发环境
                // target: 'http://127.0.0.1:3000', // 测试环境
                // target: 'http://172.16.3.30:9029', // 联调环境
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false
            },
            '/api/da': { // da地址
                // target: 'http://172.16.10.45:8087',
                target: "http://172.16.8.108:8087",//开发环境

                // pathRewrite:{"^/api/da/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/da"},
                changeOrigin: true,
                secure: false
            },
            '/api/tag': { // 数据标签
                target: 'http://172.16.8.107:8085', // 开发环境
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false
            },
            '/api/console': { // 控制台
                // target: 'http://172.16.8.109:8084', // 开发环境
                target: 'http://172.16.8.108:8084',
                // target: "http://172.16.10.34:8084",   // test
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false
            },
            '/api/analysis': { // 分析引擎
                target: 'http://172.16.8.105:9022', // 开发环境
                // target: "http://172.16.10.45:9022", // 测试环境
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false
            },
            '/public/helpSite': { // 分析引擎
                // target: 'http://172.16.8.104', // 开发环境
                target: 'http://172.16.10.34', // 测试环境
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false
            }
        }
    }
};
