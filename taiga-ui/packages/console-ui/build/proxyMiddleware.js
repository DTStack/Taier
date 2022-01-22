function getLocalIP() {  //目前先使用此方法获取真实ip
    const interfaces = require('os').networkInterfaces(); 
    let locatIp = '';
     for (let devName in interfaces) { 
         let iface = interfaces[devName]; 
         for (let i = 0; i < iface.length; i++) {
              let alias = iface[i];
               if (alias.family === 'IPv4' && alias.address !== '127.0.0.1' && !alias.internal) { 
                locatIp = alias.address; 
                } 
            } 
    }
    return locatIp;
}
let locatIp = getLocalIP(); //获取请求真实ip
module.exports = function(proxyReq, req, res) {
    proxyReq.setHeader('X-Real-IP', locatIp)
}; 
