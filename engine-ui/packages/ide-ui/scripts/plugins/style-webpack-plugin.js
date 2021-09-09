class InsertHTMLPlugin {
    constructor(options = {}) {
        this.options = options;
    }

    getResource(compilation) {
        const { modifyVars } = this.options;
        const _myAssets = new Map();
        const container = new Map(Object.entries(modifyVars));

        try {
            const assets = compilation.assets;
            // eslint-disable-next-line prefer-regex-literals
            const regExp = new RegExp(/\.css/g);

            for (const asset in assets) {
                if (regExp.test(asset)) {
                    let source = assets[asset].source();

                    /**
                     * There may be buffer resource interference
                     */
                    if (typeof source === 'string') {
                        container.forEach((value, key) => {
                            source = source.replace(
                                new RegExp(`^${key}`, 'g'),
                                value
                            );
                        });
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

module.exports = InsertHTMLPlugin;
