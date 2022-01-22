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
                secure: false,
            },
            '/api/rdos': {
                target: 'http://172.16.101.187:8090/',
                // target: 'http://172.16.100.225:3001',
                changeOrigin: true,
                secure: false,
            },
            '/api/publicService': {
                target: 'http://172.16.101.189:8077',
                changeOrigin: true,
            },
        },
    },
};
