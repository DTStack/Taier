/**
 * Global Export mxGraph object
 */

const Mx = require('public/rdos/mxgraph')({
    mxBasePath: 'public/rdos/mxgraph',
    mxImageBasePath: 'public/rdos/mxgraph/images',
    mxLanguage: 'none',
    mxLoadResources: false,
    mxLoadStylesheets: false
})

export default Mx;
