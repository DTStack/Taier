'use strict';

module.exports = {
    devServer: {
        port: 8080,
        host: '0.0.0.0',
        proxy: {
            '/node': {
                // Login
                target: 'http://172.16.101.187:8090',
                changeOrigin: true,
                secure: false
            },
            // TODO 临时任务树接口
            '/api/rdos/batch/batchCatalogue/getCatalogue': 'http://172.16.100.225:3001',
            '/api/rdos': {
                target: "http://172.16.101.187:8090/", // doraam
                changeOrigin: true,
                secure: false
            }
        }
    }
};
