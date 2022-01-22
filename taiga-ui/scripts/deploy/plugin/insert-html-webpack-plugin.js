class InsertHTMLPlugin {
    constructor(options = { delimiter: '', addCode: '' }) {
        this.options = options;
    }

    getResource(compilation) {
        const { addCode } = this.options;
        const _myAssets = new Map();

        try {
            const assets = compilation.assets;
            const regExp = new RegExp(/^(?!public|docs).*(?<=js)$/g);

            for (let asset in assets) {
                if (regExp.test(asset)) {
                    let source = assets[asset].source();

                    /**
                     * There may be buffer resource interference
                     */
                    if (typeof source === 'string') {
                        source = source.replace(/(public\/)/g, `${addCode}/$1`);
                        _myAssets.set(asset, {
                            source: function() {
                                return source;
                            },
                            size: function() {
                                return source.length;
                            },
                        });
                    }
                }
            }
            return _myAssets;
      } catch (error) {
            console.error(error, 'Something went wrong with your packaging process, check the HTML');
      }
    }

    apply(compiler) {
        const plugin = 'InsertHTMLPlugin';

        compiler.hooks.emit.tap(plugin, compilation => {
            let test = this.getResource(compilation);
            test.forEach((assetItem, assetKey) => {
                compilation.assets[assetKey] = assetItem;
            });
        });
    }
}

module.exports = InsertHTMLPlugin;
