(window["webpackJsonp_console-ui"] = window["webpackJsonp_console-ui"] || []).push([[51],{

/***/ "ChvI":
/***/ (function(module, exports, __webpack_require__) {

var map = {
	"vs/language/json/jsonWorker": "UXsU"
};


function webpackContext(req) {
	var id = webpackContextResolve(req);
	return __webpack_require__(id);
}
function webpackContextResolve(req) {
	if(!__webpack_require__.o(map, req)) {
		var e = new Error("Cannot find module '" + req + "'");
		e.code = 'MODULE_NOT_FOUND';
		throw e;
	}
	return map[req];
}
webpackContext.keys = function webpackContextKeys() {
	return Object.keys(map);
};
webpackContext.resolve = webpackContextResolve;
module.exports = webpackContext;
webpackContext.id = "ChvI";

/***/ })

}]);