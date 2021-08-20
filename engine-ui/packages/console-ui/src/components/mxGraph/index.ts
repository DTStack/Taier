/**
 * Global Export mxGraph object
 */

const Mx = require('mxgraph');

const MxFactory: any = {
    config: {
        mxImageBasePath: 'public/mxgraph/images',
        mxLanguage: 'none',
        mxLoadResources: false,
        mxLoadStylesheets: false
    },
    create () {
        return Mx(MxFactory.config);
    }
}

export default MxFactory;
