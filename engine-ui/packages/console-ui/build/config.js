'use strict';
module.exports = {
    server: {
        port: 8080,
        host: 'rdos.yarn.dtstack.net',
        proxy: {
            "/api": {
                // target: "http://172.16.8.104:9020", formal ser
                target: "http://172.16.8.106:9020", // tmp server
                // ignorePath: true, 
                changeOrigin: true,
                secure: false,
            },
            "/uic": { // UIC地址
                target: "http://dtuic.dtstack.net",
                changeOrigin: true,
                secure: false,
            }
        }
    },
};