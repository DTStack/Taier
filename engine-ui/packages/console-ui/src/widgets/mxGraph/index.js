/**
 * Global Export mxGraph object
 */

const Mx = require('public/main/mxgraph')({
    mxBasePath: 'public/main/mxgraph',
    mxImageBasePath: 'public/main/mxgraph/images',
    mxLanguage: 'none',
    mxLoadResources: false,
    mxLoadStylesheets: false
})

module.exports = Mx;
