'use strict'

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
            '/api': 'http://172.16.100.225:3001'
        }
    }
}
