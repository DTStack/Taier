class ReplaceHTMLPlugin {
    constructor(options = {}) {
        this.options = options;
    }

    getResource(compilation) {
        const _myAssets = new Map();
        try {
            const assets = compilation.assets;
            // eslint-disable-next-line prefer-regex-literals
            const regExp = new RegExp(/\.js$/g);

            for (const asset in assets) {
                if (regExp.test(asset)) {
                    let source = assets[asset].source();

                    /**
                     * There may be buffer resource interference
                     */
                    if (typeof source === 'string') {
                        source = source.replace(/('|")ant('|")/g, `'ide-ui'`);
                        source = source.replace(/('|")ant-('|")/g, `'ide-ui-'`);
                        _myAssets.set(asset, {
                            source: function () {
                                return source;
                            },
                            size: function () {
                                return source.length;
                            },
                        });
                    }
                }
            }
            return _myAssets;
        } catch (error) {
            console.error(
                error,
                'Something went wrong with your packaging process, check the HTML'
            );
        }
    }

    apply(compiler) {
        const plugin = 'StyleHTMLPlugin';

        compiler.hooks.emit.tap(plugin, (compilation) => {
            const test = this.getResource(compilation);
            test.forEach((assetItem, assetKey) => {
                compilation.assets[assetKey] = assetItem;
            });
        });
    }
}

module.exports = ReplaceHTMLPlugin;
