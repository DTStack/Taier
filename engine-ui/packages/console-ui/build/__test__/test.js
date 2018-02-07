const path = require('path');
const MY_PATH = require('./consts');

const a = [{filename: 'abc'}]

const htmlPlugs = [];
function loadHtmlPlugs() {
    const appConfs = require(path.resolve(MY_PATH.APP_PATH, 'config/defaultApps'));
    for (var i = 0 ; i < appConfs.length; i++) {
        const app = appConfs[i];
        if (app.enable) {
            const tmp = path.resolve(MY_PATH.WEB_PUBLIC, `${app.id}/index.html`)
            htmlPlugs.push(
                {
                    filename: app.filename,
                    template: tmp,
                    inject: 'body',
                    chunks: ['vendor', app.id, 'manifest'],
                    showErrors: true,
                    hash: true,
                }
            )
        }
    }
    // console.log('htmlPlugs:', JSON.stringify(htmlPlugs))
}

loadHtmlPlugs();

const abc = a.push(htmlPlugs)

console.log('htmlPlugs a:', JSON.stringify(a))
