function getLocalIP() {
  const interfaces = require('os').networkInterfaces();
  let locatIp = '';
  for (let devName in interfaces) {
    let iface = interfaces[devName];
    for (let i = 0; i < iface.length; i++) {
      let alias = iface[i];
      if (
        alias.family === 'IPv4' &&
        alias.address !== '127.0.0.1' &&
        !alias.internal
      ) {
        locatIp = alias.address;
      }
    }
  }
  return locatIp;
}
let locatIp = getLocalIP();

const proxy = {
  '/api/publicService': {
    target: 'http://172.16.101.189:8077',
    changeOrigin: true,
  },
  '/dassets/v1': {
    // target: 'http://192.168.106.7:8876', // 本地环境
    // target: 'http://172.16.100.241:8080', // 测试环境
    // target: 'http://172.16.100.225:7001/proxy/34', // 哆啦a梦3.x环境
    target: 'http://172.16.100.225:7001/proxy/50', // 多啦2a梦 4.x环境
    changeOrigin: true,
    secure: false,
    onProxyReq: function (proxyReq, req, res) {
      proxyReq.setHeader('X-Real-IP', locatIp);
    },
  },
  '/dt-common': {
    target: 'http://dev.insight.dtstack.cn/',
    changeOrigin: true,
    secure: false,
  },
  '/api/dq': {
    target: 'http://172.16.10.251:8089', // 开发环境
    changeOrigin: true,
    secure: false,
  },
  '/api/rdos': {
    target: 'http://172.16.10.168:9020', // dev server
    changeOrigin: true,
    secure: false,
  },
  '/api/streamapp': {
    // target: "http://172.16.1.191:9021", // formal test ser
    target: 'http://172.16.10.251:9023', // formal test ser
    changeOrigin: true,
    secure: false,
  },
  '/uic': {
    // UIC地址
    target: 'http://dtuic.dtstack.cn',
    changeOrigin: true,
    // pathRewrite:{"^/uic":"/"},
    secure: false,
  },
  '/api/dataScience': {
    // 算法平台
    target: 'http://172.16.10.251:9029', // 开发环境
    changeOrigin: true,
    secure: false,
  },
  '/api/da': {
    // da地址
    target: 'http://172.16.10.251:8087', //开发环境
    changeOrigin: true,
    secure: false,
  },
  '/api/console': {
    // 控制台
    target: 'http://172.16.10.168:8084', // 开发环境
    changeOrigin: true,
    secure: false,
  },
  '/api/analysis': {
    // 分析引擎
    target: 'http://172.16.10.168:9022', // 开发环境
    changeOrigin: true,
    secure: false,
  },
  '/public/helpSite': {
    // 分析引擎
    // target: 'http://172.16.8.104', // 开发环境
    target: 'http://172.16.10.34', // 测试环境
    changeOrigin: true,
    secure: false,
  },
  '/api/v1': {
    // 标签引擎
    target: 'http://172.16.8.194:8084', // 开发环境
    // target: 'http://172.16.8.163:7001/proxy/16/api/v1/', // 哆啦A梦
    changeOrigin: true,
    secure: false,
  },
  '/node': {
    // 控制台
    target: 'http://172.16.100.251:8090', // v4 开发环境
    changeOrigin: true,
    secure: false,
  },
};

const ret = Object.keys(proxy).map((p) => ({
  path: p,
  ...proxy[p],
}));

module.exports = ret;
