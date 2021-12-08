const fs = require('fs')

class VersionPlugin {

    constructor(options = { fileName: 'version.json' }) {
        this.options = options
    }

    get version() {
        let versionInfo = ''
        const packageContent = fs.readFileSync('./package.json', 'utf8', function (err) {
            if (err) throw err;
        })

        versionInfo = packageContent ? JSON.parse(packageContent).version + Date.now() : ''
        return versionInfo
    }

    apply(compiler) {
        const plugin = 'Version Plugin';
        const { fileName } = this.options;

        compiler.hooks.emit.tap(
            plugin,
            (compilation) => {
                let versionFile = this.version
                compilation.assets[fileName] = {
                    source: function () {
                        return versionFile;
                    },
                    size: function () {
                        return versionFile.length;
                    }
                };
            }
        )
    }
}

module.exports = VersionPlugin;