'use strict';
const ProxyReq = require('./proxyMiddleware');

module.exports = {
    server: {
        port: 8080,
        host: '0.0.0.0',
        proxy: {
            '/uic': { // UIC地址
                target: 'http://dtuic.dtstack.cn',
                // target: 'http://uic.insight.cn',
                // target: 'http://172.16.1.92:8668',
                changeOrigin: true,
                // pathRewrite:{"^/uic":"/"},
                secure: false
            },
            '/node': { // 控制台
                target: 'http://172.16.100.225:7001/proxy/44', // doraemon
                // pathRewrite:{"^/api/tag/service":"/server/index.php?g=Web&c=Mock&o=simple&projectID=5&uri=/api/tag"},
                changeOrigin: true,
                secure: false,
                onProxyReq: ProxyReq
            }
        }
    }
};
