/**
 * Global Export mxGraph object
 */

const Mx = require('public/main/mxgraph');

const MxFactory = {
    config: {
        mxBasePath: 'public/main/mxgraph',
        mxImageBasePath: 'public/main/mxgraph/images',
        mxLanguage: 'none',
        mxLoadResources: false,
        mxLoadStylesheets: false
    },
    create: () => {
        return Mx(this.config);
    }
}

module.exports = MxFactory;
