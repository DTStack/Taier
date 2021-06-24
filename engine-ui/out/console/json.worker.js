/******/ (function(modules) { // webpackBootstrap
/******/ 	this["webpackChunkDTConsoleApp"] = function webpackChunkCallback(chunkIds, moreModules) {
/******/ 		for(var moduleId in moreModules) {
/******/ 			modules[moduleId] = moreModules[moduleId];
/******/ 		}
/******/ 		while(chunkIds.length)
/******/ 			installedChunks[chunkIds.pop()] = 1;
/******/ 	};
/******/
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// object to store loaded chunks
/******/ 	// "1" means "already loaded"
/******/ 	var installedChunks = {
/******/ 		0: 1
/******/ 	};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/ 	// The chunk loading function for additional chunks
/******/ 	// Since all referenced chunks are already included
/******/ 	// in this file, this function is empty here.
/******/ 	__webpack_require__.e = function requireEnsure() {
/******/ 		return Promise.resolve();
/******/ 	};
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "/console/";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = "8Z8O");
/******/ })
/************************************************************************/
/******/ ({

/***/ "8Z8O":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__);

// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/base/common/platform.js
var platform = __webpack_require__("MNsG");

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/uri.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var __extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();

function _encode(ch) {
    return '%' + ch.charCodeAt(0).toString(16).toUpperCase();
}
// see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent
function encodeURIComponent2(str) {
    return encodeURIComponent(str).replace(/[!'()*]/g, _encode);
}
function encodeNoop(str) {
    return str.replace(/[#?]/, _encode);
}
var _schemePattern = /^\w[\w\d+.-]*$/;
var _singleSlashStart = /^\//;
var _doubleSlashStart = /^\/\//;
function _validateUri(ret) {
    // scheme, https://tools.ietf.org/html/rfc3986#section-3.1
    // ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
    if (ret.scheme && !_schemePattern.test(ret.scheme)) {
        throw new Error('[UriError]: Scheme contains illegal characters.');
    }
    // path, http://tools.ietf.org/html/rfc3986#section-3.3
    // If a URI contains an authority component, then the path component
    // must either be empty or begin with a slash ("/") character.  If a URI
    // does not contain an authority component, then the path cannot begin
    // with two slash characters ("//").
    if (ret.path) {
        if (ret.authority) {
            if (!_singleSlashStart.test(ret.path)) {
                throw new Error('[UriError]: If a URI contains an authority component, then the path component must either be empty or begin with a slash ("/") character');
            }
        }
        else {
            if (_doubleSlashStart.test(ret.path)) {
                throw new Error('[UriError]: If a URI does not contain an authority component, then the path cannot begin with two slash characters ("//")');
            }
        }
    }
}
var _empty = '';
var _slash = '/';
var _regexp = /^(([^:/?#]+?):)?(\/\/([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?/;
var _driveLetterPath = /^\/[a-zA-Z]:/;
var _upperCaseDrive = /^(\/)?([A-Z]:)/;
var _driveLetter = /^[a-zA-Z]:/;
/**
 * Uniform Resource Identifier (URI) http://tools.ietf.org/html/rfc3986.
 * This class is a simple parser which creates the basic component paths
 * (http://tools.ietf.org/html/rfc3986#section-3) with minimal validation
 * and encoding.
 *
 *       foo://example.com:8042/over/there?name=ferret#nose
 *       \_/   \______________/\_________/ \_________/ \__/
 *        |           |            |            |        |
 *     scheme     authority       path        query   fragment
 *        |   _____________________|__
 *       / \ /                        \
 *       urn:example:animal:ferret:nose
 *
 *
 */
var uri_URI = /** @class */ (function () {
    /**
     * @internal
     */
    function URI(schemeOrData, authority, path, query, fragment) {
        if (typeof schemeOrData === 'object') {
            this.scheme = schemeOrData.scheme || _empty;
            this.authority = schemeOrData.authority || _empty;
            this.path = schemeOrData.path || _empty;
            this.query = schemeOrData.query || _empty;
            this.fragment = schemeOrData.fragment || _empty;
            // no validation because it's this URI
            // that creates uri components.
            // _validateUri(this);
        }
        else {
            this.scheme = schemeOrData || _empty;
            this.authority = authority || _empty;
            this.path = path || _empty;
            this.query = query || _empty;
            this.fragment = fragment || _empty;
            _validateUri(this);
        }
    }
    URI.isUri = function (thing) {
        if (thing instanceof URI) {
            return true;
        }
        if (!thing) {
            return false;
        }
        return typeof thing.authority === 'string'
            && typeof thing.fragment === 'string'
            && typeof thing.path === 'string'
            && typeof thing.query === 'string'
            && typeof thing.scheme === 'string';
    };
    Object.defineProperty(URI.prototype, "fsPath", {
        // ---- filesystem path -----------------------
        /**
         * Returns a string representing the corresponding file system path of this URI.
         * Will handle UNC paths and normalize windows drive letters to lower-case. Also
         * uses the platform specific path separator. Will *not* validate the path for
         * invalid characters and semantics. Will *not* look at the scheme of this URI.
         */
        get: function () {
            return _makeFsPath(this);
        },
        enumerable: true,
        configurable: true
    });
    // ---- modify to new -------------------------
    URI.prototype.with = function (change) {
        if (!change) {
            return this;
        }
        var scheme = change.scheme, authority = change.authority, path = change.path, query = change.query, fragment = change.fragment;
        if (scheme === void 0) {
            scheme = this.scheme;
        }
        else if (scheme === null) {
            scheme = _empty;
        }
        if (authority === void 0) {
            authority = this.authority;
        }
        else if (authority === null) {
            authority = _empty;
        }
        if (path === void 0) {
            path = this.path;
        }
        else if (path === null) {
            path = _empty;
        }
        if (query === void 0) {
            query = this.query;
        }
        else if (query === null) {
            query = _empty;
        }
        if (fragment === void 0) {
            fragment = this.fragment;
        }
        else if (fragment === null) {
            fragment = _empty;
        }
        if (scheme === this.scheme
            && authority === this.authority
            && path === this.path
            && query === this.query
            && fragment === this.fragment) {
            return this;
        }
        return new _URI(scheme, authority, path, query, fragment);
    };
    // ---- parse & validate ------------------------
    URI.parse = function (value) {
        var match = _regexp.exec(value);
        if (!match) {
            return new _URI(_empty, _empty, _empty, _empty, _empty);
        }
        return new _URI(match[2] || _empty, decodeURIComponent(match[4] || _empty), decodeURIComponent(match[5] || _empty), decodeURIComponent(match[7] || _empty), decodeURIComponent(match[9] || _empty));
    };
    URI.file = function (path) {
        var authority = _empty;
        // normalize to fwd-slashes on windows,
        // on other systems bwd-slashes are valid
        // filename character, eg /f\oo/ba\r.txt
        if (platform["c" /* isWindows */]) {
            path = path.replace(/\\/g, _slash);
        }
        // check for authority as used in UNC shares
        // or use the path as given
        if (path[0] === _slash && path[1] === _slash) {
            var idx = path.indexOf(_slash, 2);
            if (idx === -1) {
                authority = path.substring(2);
                path = _slash;
            }
            else {
                authority = path.substring(2, idx);
                path = path.substring(idx) || _slash;
            }
        }
        // Ensure that path starts with a slash
        // or that it is at least a slash
        if (_driveLetter.test(path)) {
            path = _slash + path;
        }
        else if (path[0] !== _slash) {
            // tricky -> makes invalid paths
            // but otherwise we have to stop
            // allowing relative paths...
            path = _slash + path;
        }
        return new _URI('file', authority, path, _empty, _empty);
    };
    URI.from = function (components) {
        return new _URI(components.scheme, components.authority, components.path, components.query, components.fragment);
    };
    // ---- printing/externalize ---------------------------
    /**
     *
     * @param skipEncoding Do not encode the result, default is `false`
     */
    URI.prototype.toString = function (skipEncoding) {
        if (skipEncoding === void 0) { skipEncoding = false; }
        return _asFormatted(this, skipEncoding);
    };
    URI.prototype.toJSON = function () {
        var res = {
            $mid: 1,
            fsPath: this.fsPath,
            external: this.toString(),
        };
        if (this.path) {
            res.path = this.path;
        }
        if (this.scheme) {
            res.scheme = this.scheme;
        }
        if (this.authority) {
            res.authority = this.authority;
        }
        if (this.query) {
            res.query = this.query;
        }
        if (this.fragment) {
            res.fragment = this.fragment;
        }
        return res;
    };
    URI.revive = function (data) {
        if (!data) {
            return data;
        }
        else if (data instanceof URI) {
            return data;
        }
        else {
            var result = new _URI(data);
            result._fsPath = data.fsPath;
            result._formatted = data.external;
            return result;
        }
    };
    return URI;
}());
/* harmony default export */ var common_uri = (uri_URI);
// tslint:disable-next-line:class-name
var _URI = /** @class */ (function (_super) {
    __extends(_URI, _super);
    function _URI() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this._formatted = null;
        _this._fsPath = null;
        return _this;
    }
    Object.defineProperty(_URI.prototype, "fsPath", {
        get: function () {
            if (!this._fsPath) {
                this._fsPath = _makeFsPath(this);
            }
            return this._fsPath;
        },
        enumerable: true,
        configurable: true
    });
    _URI.prototype.toString = function (skipEncoding) {
        if (skipEncoding === void 0) { skipEncoding = false; }
        if (!skipEncoding) {
            if (!this._formatted) {
                this._formatted = _asFormatted(this, false);
            }
            return this._formatted;
        }
        else {
            // we don't cache that
            return _asFormatted(this, true);
        }
    };
    return _URI;
}(uri_URI));
/**
 * Compute `fsPath` for the given uri
 * @param uri
 */
function _makeFsPath(uri) {
    var value;
    if (uri.authority && uri.path && uri.scheme === 'file') {
        // unc path: file://shares/c$/far/boo
        value = "//" + uri.authority + uri.path;
    }
    else if (_driveLetterPath.test(uri.path)) {
        // windows drive letter: file:///c:/far/boo
        value = uri.path[1].toLowerCase() + uri.path.substr(2);
    }
    else {
        // other path
        value = uri.path;
    }
    if (platform["c" /* isWindows */]) {
        value = value.replace(/\//g, '\\');
    }
    return value;
}
/**
 * Create the external version of a uri
 */
function _asFormatted(uri, skipEncoding) {
    var encoder = !skipEncoding
        ? encodeURIComponent2
        : encodeNoop;
    var parts = [];
    var scheme = uri.scheme, authority = uri.authority, path = uri.path, query = uri.query, fragment = uri.fragment;
    if (scheme) {
        parts.push(scheme, ':');
    }
    if (authority || scheme === 'file') {
        parts.push('//');
    }
    if (authority) {
        var idx = authority.indexOf('@');
        if (idx !== -1) {
            var userinfo = authority.substr(0, idx);
            authority = authority.substr(idx + 1);
            idx = userinfo.indexOf(':');
            if (idx === -1) {
                parts.push(encoder(userinfo));
            }
            else {
                parts.push(encoder(userinfo.substr(0, idx)), ':', encoder(userinfo.substr(idx + 1)));
            }
            parts.push('@');
        }
        authority = authority.toLowerCase();
        idx = authority.indexOf(':');
        if (idx === -1) {
            parts.push(encoder(authority));
        }
        else {
            parts.push(encoder(authority.substr(0, idx)), authority.substr(idx));
        }
    }
    if (path) {
        // lower-case windows drive letters in /C:/fff or C:/fff
        var m = _upperCaseDrive.exec(path);
        if (m) {
            if (m[1]) {
                path = '/' + m[2].toLowerCase() + path.substr(3); // "/c:".length === 3
            }
            else {
                path = m[2].toLowerCase() + path.substr(2); // // "c:".length === 2
            }
        }
        // encode every segement but not slashes
        // make sure that # and ? are always encoded
        // when occurring in paths - otherwise the result
        // cannot be parsed back again
        var lastIdx = 0;
        while (true) {
            var idx = path.indexOf(_slash, lastIdx);
            if (idx === -1) {
                parts.push(encoder(path.substring(lastIdx)));
                break;
            }
            parts.push(encoder(path.substring(lastIdx, idx)), _slash);
            lastIdx = idx + 1;
        }
    }
    if (query) {
        parts.push('?', encoder(query));
    }
    if (fragment) {
        parts.push('#', encoder(fragment));
    }
    return parts.join(_empty);
}

// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/base/common/winjs.base.js
var winjs_base = __webpack_require__("VMIq");

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/core/position.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

/**
 * A position in the editor.
 */
var Position = /** @class */ (function () {
    function Position(lineNumber, column) {
        this.lineNumber = lineNumber;
        this.column = column;
    }
    /**
     * Test if this position equals other position
     */
    Position.prototype.equals = function (other) {
        return Position.equals(this, other);
    };
    /**
     * Test if position `a` equals position `b`
     */
    Position.equals = function (a, b) {
        if (!a && !b) {
            return true;
        }
        return (!!a &&
            !!b &&
            a.lineNumber === b.lineNumber &&
            a.column === b.column);
    };
    /**
     * Test if this position is before other position.
     * If the two positions are equal, the result will be false.
     */
    Position.prototype.isBefore = function (other) {
        return Position.isBefore(this, other);
    };
    /**
     * Test if position `a` is before position `b`.
     * If the two positions are equal, the result will be false.
     */
    Position.isBefore = function (a, b) {
        if (a.lineNumber < b.lineNumber) {
            return true;
        }
        if (b.lineNumber < a.lineNumber) {
            return false;
        }
        return a.column < b.column;
    };
    /**
     * Test if this position is before other position.
     * If the two positions are equal, the result will be true.
     */
    Position.prototype.isBeforeOrEqual = function (other) {
        return Position.isBeforeOrEqual(this, other);
    };
    /**
     * Test if position `a` is before position `b`.
     * If the two positions are equal, the result will be true.
     */
    Position.isBeforeOrEqual = function (a, b) {
        if (a.lineNumber < b.lineNumber) {
            return true;
        }
        if (b.lineNumber < a.lineNumber) {
            return false;
        }
        return a.column <= b.column;
    };
    /**
     * A function that compares positions, useful for sorting
     */
    Position.compare = function (a, b) {
        var aLineNumber = a.lineNumber | 0;
        var bLineNumber = b.lineNumber | 0;
        if (aLineNumber === bLineNumber) {
            var aColumn = a.column | 0;
            var bColumn = b.column | 0;
            return aColumn - bColumn;
        }
        return aLineNumber - bLineNumber;
    };
    /**
     * Clone this position.
     */
    Position.prototype.clone = function () {
        return new Position(this.lineNumber, this.column);
    };
    /**
     * Convert to a human-readable representation.
     */
    Position.prototype.toString = function () {
        return '(' + this.lineNumber + ',' + this.column + ')';
    };
    // ---
    /**
     * Create a `Position` from an `IPosition`.
     */
    Position.lift = function (pos) {
        return new Position(pos.lineNumber, pos.column);
    };
    /**
     * Test if `obj` is an `IPosition`.
     */
    Position.isIPosition = function (obj) {
        return (obj
            && (typeof obj.lineNumber === 'number')
            && (typeof obj.column === 'number'));
    };
    return Position;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/core/range.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


/**
 * A range in the editor. (startLineNumber,startColumn) is <= (endLineNumber,endColumn)
 */
var range_Range = /** @class */ (function () {
    function Range(startLineNumber, startColumn, endLineNumber, endColumn) {
        if ((startLineNumber > endLineNumber) || (startLineNumber === endLineNumber && startColumn > endColumn)) {
            this.startLineNumber = endLineNumber;
            this.startColumn = endColumn;
            this.endLineNumber = startLineNumber;
            this.endColumn = startColumn;
        }
        else {
            this.startLineNumber = startLineNumber;
            this.startColumn = startColumn;
            this.endLineNumber = endLineNumber;
            this.endColumn = endColumn;
        }
    }
    /**
     * Test if this range is empty.
     */
    Range.prototype.isEmpty = function () {
        return Range.isEmpty(this);
    };
    /**
     * Test if `range` is empty.
     */
    Range.isEmpty = function (range) {
        return (range.startLineNumber === range.endLineNumber && range.startColumn === range.endColumn);
    };
    /**
     * Test if position is in this range. If the position is at the edges, will return true.
     */
    Range.prototype.containsPosition = function (position) {
        return Range.containsPosition(this, position);
    };
    /**
     * Test if `position` is in `range`. If the position is at the edges, will return true.
     */
    Range.containsPosition = function (range, position) {
        if (position.lineNumber < range.startLineNumber || position.lineNumber > range.endLineNumber) {
            return false;
        }
        if (position.lineNumber === range.startLineNumber && position.column < range.startColumn) {
            return false;
        }
        if (position.lineNumber === range.endLineNumber && position.column > range.endColumn) {
            return false;
        }
        return true;
    };
    /**
     * Test if range is in this range. If the range is equal to this range, will return true.
     */
    Range.prototype.containsRange = function (range) {
        return Range.containsRange(this, range);
    };
    /**
     * Test if `otherRange` is in `range`. If the ranges are equal, will return true.
     */
    Range.containsRange = function (range, otherRange) {
        if (otherRange.startLineNumber < range.startLineNumber || otherRange.endLineNumber < range.startLineNumber) {
            return false;
        }
        if (otherRange.startLineNumber > range.endLineNumber || otherRange.endLineNumber > range.endLineNumber) {
            return false;
        }
        if (otherRange.startLineNumber === range.startLineNumber && otherRange.startColumn < range.startColumn) {
            return false;
        }
        if (otherRange.endLineNumber === range.endLineNumber && otherRange.endColumn > range.endColumn) {
            return false;
        }
        return true;
    };
    /**
     * A reunion of the two ranges.
     * The smallest position will be used as the start point, and the largest one as the end point.
     */
    Range.prototype.plusRange = function (range) {
        return Range.plusRange(this, range);
    };
    /**
     * A reunion of the two ranges.
     * The smallest position will be used as the start point, and the largest one as the end point.
     */
    Range.plusRange = function (a, b) {
        var startLineNumber;
        var startColumn;
        var endLineNumber;
        var endColumn;
        if (b.startLineNumber < a.startLineNumber) {
            startLineNumber = b.startLineNumber;
            startColumn = b.startColumn;
        }
        else if (b.startLineNumber === a.startLineNumber) {
            startLineNumber = b.startLineNumber;
            startColumn = Math.min(b.startColumn, a.startColumn);
        }
        else {
            startLineNumber = a.startLineNumber;
            startColumn = a.startColumn;
        }
        if (b.endLineNumber > a.endLineNumber) {
            endLineNumber = b.endLineNumber;
            endColumn = b.endColumn;
        }
        else if (b.endLineNumber === a.endLineNumber) {
            endLineNumber = b.endLineNumber;
            endColumn = Math.max(b.endColumn, a.endColumn);
        }
        else {
            endLineNumber = a.endLineNumber;
            endColumn = a.endColumn;
        }
        return new Range(startLineNumber, startColumn, endLineNumber, endColumn);
    };
    /**
     * A intersection of the two ranges.
     */
    Range.prototype.intersectRanges = function (range) {
        return Range.intersectRanges(this, range);
    };
    /**
     * A intersection of the two ranges.
     */
    Range.intersectRanges = function (a, b) {
        var resultStartLineNumber = a.startLineNumber;
        var resultStartColumn = a.startColumn;
        var resultEndLineNumber = a.endLineNumber;
        var resultEndColumn = a.endColumn;
        var otherStartLineNumber = b.startLineNumber;
        var otherStartColumn = b.startColumn;
        var otherEndLineNumber = b.endLineNumber;
        var otherEndColumn = b.endColumn;
        if (resultStartLineNumber < otherStartLineNumber) {
            resultStartLineNumber = otherStartLineNumber;
            resultStartColumn = otherStartColumn;
        }
        else if (resultStartLineNumber === otherStartLineNumber) {
            resultStartColumn = Math.max(resultStartColumn, otherStartColumn);
        }
        if (resultEndLineNumber > otherEndLineNumber) {
            resultEndLineNumber = otherEndLineNumber;
            resultEndColumn = otherEndColumn;
        }
        else if (resultEndLineNumber === otherEndLineNumber) {
            resultEndColumn = Math.min(resultEndColumn, otherEndColumn);
        }
        // Check if selection is now empty
        if (resultStartLineNumber > resultEndLineNumber) {
            return null;
        }
        if (resultStartLineNumber === resultEndLineNumber && resultStartColumn > resultEndColumn) {
            return null;
        }
        return new Range(resultStartLineNumber, resultStartColumn, resultEndLineNumber, resultEndColumn);
    };
    /**
     * Test if this range equals other.
     */
    Range.prototype.equalsRange = function (other) {
        return Range.equalsRange(this, other);
    };
    /**
     * Test if range `a` equals `b`.
     */
    Range.equalsRange = function (a, b) {
        return (!!a &&
            !!b &&
            a.startLineNumber === b.startLineNumber &&
            a.startColumn === b.startColumn &&
            a.endLineNumber === b.endLineNumber &&
            a.endColumn === b.endColumn);
    };
    /**
     * Return the end position (which will be after or equal to the start position)
     */
    Range.prototype.getEndPosition = function () {
        return new Position(this.endLineNumber, this.endColumn);
    };
    /**
     * Return the start position (which will be before or equal to the end position)
     */
    Range.prototype.getStartPosition = function () {
        return new Position(this.startLineNumber, this.startColumn);
    };
    /**
     * Transform to a user presentable string representation.
     */
    Range.prototype.toString = function () {
        return '[' + this.startLineNumber + ',' + this.startColumn + ' -> ' + this.endLineNumber + ',' + this.endColumn + ']';
    };
    /**
     * Create a new range using this range's start position, and using endLineNumber and endColumn as the end position.
     */
    Range.prototype.setEndPosition = function (endLineNumber, endColumn) {
        return new Range(this.startLineNumber, this.startColumn, endLineNumber, endColumn);
    };
    /**
     * Create a new range using this range's end position, and using startLineNumber and startColumn as the start position.
     */
    Range.prototype.setStartPosition = function (startLineNumber, startColumn) {
        return new Range(startLineNumber, startColumn, this.endLineNumber, this.endColumn);
    };
    /**
     * Create a new empty range using this range's start position.
     */
    Range.prototype.collapseToStart = function () {
        return Range.collapseToStart(this);
    };
    /**
     * Create a new empty range using this range's start position.
     */
    Range.collapseToStart = function (range) {
        return new Range(range.startLineNumber, range.startColumn, range.startLineNumber, range.startColumn);
    };
    // ---
    Range.fromPositions = function (start, end) {
        if (end === void 0) { end = start; }
        return new Range(start.lineNumber, start.column, end.lineNumber, end.column);
    };
    /**
     * Create a `Range` from an `IRange`.
     */
    Range.lift = function (range) {
        if (!range) {
            return null;
        }
        return new Range(range.startLineNumber, range.startColumn, range.endLineNumber, range.endColumn);
    };
    /**
     * Test if `obj` is an `IRange`.
     */
    Range.isIRange = function (obj) {
        return (obj
            && (typeof obj.startLineNumber === 'number')
            && (typeof obj.startColumn === 'number')
            && (typeof obj.endLineNumber === 'number')
            && (typeof obj.endColumn === 'number'));
    };
    /**
     * Test if the two ranges are touching in any way.
     */
    Range.areIntersectingOrTouching = function (a, b) {
        // Check if `a` is before `b`
        if (a.endLineNumber < b.startLineNumber || (a.endLineNumber === b.startLineNumber && a.endColumn < b.startColumn)) {
            return false;
        }
        // Check if `b` is before `a`
        if (b.endLineNumber < a.startLineNumber || (b.endLineNumber === a.startLineNumber && b.endColumn < a.startColumn)) {
            return false;
        }
        // These ranges must intersect
        return true;
    };
    /**
     * A function that compares ranges, useful for sorting ranges
     * It will first compare ranges on the startPosition and then on the endPosition
     */
    Range.compareRangesUsingStarts = function (a, b) {
        var aStartLineNumber = a.startLineNumber | 0;
        var bStartLineNumber = b.startLineNumber | 0;
        if (aStartLineNumber === bStartLineNumber) {
            var aStartColumn = a.startColumn | 0;
            var bStartColumn = b.startColumn | 0;
            if (aStartColumn === bStartColumn) {
                var aEndLineNumber = a.endLineNumber | 0;
                var bEndLineNumber = b.endLineNumber | 0;
                if (aEndLineNumber === bEndLineNumber) {
                    var aEndColumn = a.endColumn | 0;
                    var bEndColumn = b.endColumn | 0;
                    return aEndColumn - bEndColumn;
                }
                return aEndLineNumber - bEndLineNumber;
            }
            return aStartColumn - bStartColumn;
        }
        return aStartLineNumber - bStartLineNumber;
    };
    /**
     * A function that compares ranges, useful for sorting ranges
     * It will first compare ranges on the endPosition and then on the startPosition
     */
    Range.compareRangesUsingEnds = function (a, b) {
        if (a.endLineNumber === b.endLineNumber) {
            if (a.endColumn === b.endColumn) {
                if (a.startLineNumber === b.startLineNumber) {
                    return a.startColumn - b.startColumn;
                }
                return a.startLineNumber - b.startLineNumber;
            }
            return a.endColumn - b.endColumn;
        }
        return a.endLineNumber - b.endLineNumber;
    };
    /**
     * Test if the range spans multiple lines.
     */
    Range.spansMultipleLines = function (range) {
        return range.endLineNumber > range.startLineNumber;
    };
    return Range;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/diff/diffChange.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

/**
 * Represents information about a specific difference between two sequences.
 */
var DiffChange = /** @class */ (function () {
    /**
     * Constructs a new DiffChange with the given sequence information
     * and content.
     */
    function DiffChange(originalStart, originalLength, modifiedStart, modifiedLength) {
        //Debug.Assert(originalLength > 0 || modifiedLength > 0, "originalLength and modifiedLength cannot both be <= 0");
        this.originalStart = originalStart;
        this.originalLength = originalLength;
        this.modifiedStart = modifiedStart;
        this.modifiedLength = modifiedLength;
    }
    /**
     * The end point (exclusive) of the change in the original sequence.
     */
    DiffChange.prototype.getOriginalEnd = function () {
        return this.originalStart + this.originalLength;
    };
    /**
     * The end point (exclusive) of the change in the modified sequence.
     */
    DiffChange.prototype.getModifiedEnd = function () {
        return this.modifiedStart + this.modifiedLength;
    };
    return DiffChange;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/diff/diff.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


function createStringSequence(a) {
    return {
        getLength: function () { return a.length; },
        getElementHash: function (pos) { return a[pos]; }
    };
}
function stringDiff(original, modified, pretty) {
    return new diff_LcsDiff(createStringSequence(original), createStringSequence(modified)).ComputeDiff(pretty);
}
//
// The code below has been ported from a C# implementation in VS
//
var Debug = /** @class */ (function () {
    function Debug() {
    }
    Debug.Assert = function (condition, message) {
        if (!condition) {
            throw new Error(message);
        }
    };
    return Debug;
}());

var MyArray = /** @class */ (function () {
    function MyArray() {
    }
    /**
     * Copies a range of elements from an Array starting at the specified source index and pastes
     * them to another Array starting at the specified destination index. The length and the indexes
     * are specified as 64-bit integers.
     * sourceArray:
     *		The Array that contains the data to copy.
     * sourceIndex:
     *		A 64-bit integer that represents the index in the sourceArray at which copying begins.
     * destinationArray:
     *		The Array that receives the data.
     * destinationIndex:
     *		A 64-bit integer that represents the index in the destinationArray at which storing begins.
     * length:
     *		A 64-bit integer that represents the number of elements to copy.
     */
    MyArray.Copy = function (sourceArray, sourceIndex, destinationArray, destinationIndex, length) {
        for (var i = 0; i < length; i++) {
            destinationArray[destinationIndex + i] = sourceArray[sourceIndex + i];
        }
    };
    return MyArray;
}());

//*****************************************************************************
// LcsDiff.cs
//
// An implementation of the difference algorithm described in
// "An O(ND) Difference Algorithm and its variations" by Eugene W. Myers
//
// Copyright (C) 2008 Microsoft Corporation @minifier_do_not_preserve
//*****************************************************************************
// Our total memory usage for storing history is (worst-case):
// 2 * [(MaxDifferencesHistory + 1) * (MaxDifferencesHistory + 1) - 1] * sizeof(int)
// 2 * [1448*1448 - 1] * 4 = 16773624 = 16MB
var MaxDifferencesHistory = 1447;
//let MaxDifferencesHistory = 100;
/**
 * A utility class which helps to create the set of DiffChanges from
 * a difference operation. This class accepts original DiffElements and
 * modified DiffElements that are involved in a particular change. The
 * MarktNextChange() method can be called to mark the separation between
 * distinct changes. At the end, the Changes property can be called to retrieve
 * the constructed changes.
 */
var diff_DiffChangeHelper = /** @class */ (function () {
    /**
     * Constructs a new DiffChangeHelper for the given DiffSequences.
     */
    function DiffChangeHelper() {
        this.m_changes = [];
        this.m_originalStart = Number.MAX_VALUE;
        this.m_modifiedStart = Number.MAX_VALUE;
        this.m_originalCount = 0;
        this.m_modifiedCount = 0;
    }
    /**
     * Marks the beginning of the next change in the set of differences.
     */
    DiffChangeHelper.prototype.MarkNextChange = function () {
        // Only add to the list if there is something to add
        if (this.m_originalCount > 0 || this.m_modifiedCount > 0) {
            // Add the new change to our list
            this.m_changes.push(new DiffChange(this.m_originalStart, this.m_originalCount, this.m_modifiedStart, this.m_modifiedCount));
        }
        // Reset for the next change
        this.m_originalCount = 0;
        this.m_modifiedCount = 0;
        this.m_originalStart = Number.MAX_VALUE;
        this.m_modifiedStart = Number.MAX_VALUE;
    };
    /**
     * Adds the original element at the given position to the elements
     * affected by the current change. The modified index gives context
     * to the change position with respect to the original sequence.
     * @param originalIndex The index of the original element to add.
     * @param modifiedIndex The index of the modified element that provides corresponding position in the modified sequence.
     */
    DiffChangeHelper.prototype.AddOriginalElement = function (originalIndex, modifiedIndex) {
        // The 'true' start index is the smallest of the ones we've seen
        this.m_originalStart = Math.min(this.m_originalStart, originalIndex);
        this.m_modifiedStart = Math.min(this.m_modifiedStart, modifiedIndex);
        this.m_originalCount++;
    };
    /**
     * Adds the modified element at the given position to the elements
     * affected by the current change. The original index gives context
     * to the change position with respect to the modified sequence.
     * @param originalIndex The index of the original element that provides corresponding position in the original sequence.
     * @param modifiedIndex The index of the modified element to add.
     */
    DiffChangeHelper.prototype.AddModifiedElement = function (originalIndex, modifiedIndex) {
        // The 'true' start index is the smallest of the ones we've seen
        this.m_originalStart = Math.min(this.m_originalStart, originalIndex);
        this.m_modifiedStart = Math.min(this.m_modifiedStart, modifiedIndex);
        this.m_modifiedCount++;
    };
    /**
     * Retrieves all of the changes marked by the class.
     */
    DiffChangeHelper.prototype.getChanges = function () {
        if (this.m_originalCount > 0 || this.m_modifiedCount > 0) {
            // Finish up on whatever is left
            this.MarkNextChange();
        }
        return this.m_changes;
    };
    /**
     * Retrieves all of the changes marked by the class in the reverse order
     */
    DiffChangeHelper.prototype.getReverseChanges = function () {
        if (this.m_originalCount > 0 || this.m_modifiedCount > 0) {
            // Finish up on whatever is left
            this.MarkNextChange();
        }
        this.m_changes.reverse();
        return this.m_changes;
    };
    return DiffChangeHelper;
}());
var diff_hasOwnProperty = Object.prototype.hasOwnProperty;
/**
 * An implementation of the difference algorithm described in
 * "An O(ND) Difference Algorithm and its variations" by Eugene W. Myers
 */
var diff_LcsDiff = /** @class */ (function () {
    /**
     * Constructs the DiffFinder
     */
    function LcsDiff(originalSequence, newSequence, continueProcessingPredicate) {
        if (continueProcessingPredicate === void 0) { continueProcessingPredicate = null; }
        this.OriginalSequence = originalSequence;
        this.ModifiedSequence = newSequence;
        this.ContinueProcessingPredicate = continueProcessingPredicate;
        this.m_originalIds = [];
        this.m_modifiedIds = [];
        this.m_forwardHistory = [];
        this.m_reverseHistory = [];
        this.ComputeUniqueIdentifiers();
    }
    LcsDiff.prototype.ComputeUniqueIdentifiers = function () {
        var originalSequenceLength = this.OriginalSequence.getLength();
        var modifiedSequenceLength = this.ModifiedSequence.getLength();
        this.m_originalIds = new Array(originalSequenceLength);
        this.m_modifiedIds = new Array(modifiedSequenceLength);
        // Create a new hash table for unique elements from the original
        // sequence.
        var hashTable = {};
        var currentUniqueId = 1;
        var i;
        // Fill up the hash table for unique elements
        for (i = 0; i < originalSequenceLength; i++) {
            var originalElementHash = this.OriginalSequence.getElementHash(i);
            if (!diff_hasOwnProperty.call(hashTable, originalElementHash)) {
                // No entry in the hashtable so this is a new unique element.
                // Assign the element a new unique identifier and add it to the
                // hash table
                this.m_originalIds[i] = currentUniqueId++;
                hashTable[originalElementHash] = this.m_originalIds[i];
            }
            else {
                this.m_originalIds[i] = hashTable[originalElementHash];
            }
        }
        // Now match up modified elements
        for (i = 0; i < modifiedSequenceLength; i++) {
            var modifiedElementHash = this.ModifiedSequence.getElementHash(i);
            if (!diff_hasOwnProperty.call(hashTable, modifiedElementHash)) {
                this.m_modifiedIds[i] = currentUniqueId++;
                hashTable[modifiedElementHash] = this.m_modifiedIds[i];
            }
            else {
                this.m_modifiedIds[i] = hashTable[modifiedElementHash];
            }
        }
    };
    LcsDiff.prototype.ElementsAreEqual = function (originalIndex, newIndex) {
        return this.m_originalIds[originalIndex] === this.m_modifiedIds[newIndex];
    };
    LcsDiff.prototype.OriginalElementsAreEqual = function (index1, index2) {
        return this.m_originalIds[index1] === this.m_originalIds[index2];
    };
    LcsDiff.prototype.ModifiedElementsAreEqual = function (index1, index2) {
        return this.m_modifiedIds[index1] === this.m_modifiedIds[index2];
    };
    LcsDiff.prototype.ComputeDiff = function (pretty) {
        return this._ComputeDiff(0, this.OriginalSequence.getLength() - 1, 0, this.ModifiedSequence.getLength() - 1, pretty);
    };
    /**
     * Computes the differences between the original and modified input
     * sequences on the bounded range.
     * @returns An array of the differences between the two input sequences.
     */
    LcsDiff.prototype._ComputeDiff = function (originalStart, originalEnd, modifiedStart, modifiedEnd, pretty) {
        var quitEarlyArr = [false];
        var changes = this.ComputeDiffRecursive(originalStart, originalEnd, modifiedStart, modifiedEnd, quitEarlyArr);
        if (pretty) {
            // We have to clean up the computed diff to be more intuitive
            // but it turns out this cannot be done correctly until the entire set
            // of diffs have been computed
            return this.ShiftChanges(changes);
        }
        return changes;
    };
    /**
     * Private helper method which computes the differences on the bounded range
     * recursively.
     * @returns An array of the differences between the two input sequences.
     */
    LcsDiff.prototype.ComputeDiffRecursive = function (originalStart, originalEnd, modifiedStart, modifiedEnd, quitEarlyArr) {
        quitEarlyArr[0] = false;
        // Find the start of the differences
        while (originalStart <= originalEnd && modifiedStart <= modifiedEnd && this.ElementsAreEqual(originalStart, modifiedStart)) {
            originalStart++;
            modifiedStart++;
        }
        // Find the end of the differences
        while (originalEnd >= originalStart && modifiedEnd >= modifiedStart && this.ElementsAreEqual(originalEnd, modifiedEnd)) {
            originalEnd--;
            modifiedEnd--;
        }
        // In the special case where we either have all insertions or all deletions or the sequences are identical
        if (originalStart > originalEnd || modifiedStart > modifiedEnd) {
            var changes = void 0;
            if (modifiedStart <= modifiedEnd) {
                Debug.Assert(originalStart === originalEnd + 1, 'originalStart should only be one more than originalEnd');
                // All insertions
                changes = [
                    new DiffChange(originalStart, 0, modifiedStart, modifiedEnd - modifiedStart + 1)
                ];
            }
            else if (originalStart <= originalEnd) {
                Debug.Assert(modifiedStart === modifiedEnd + 1, 'modifiedStart should only be one more than modifiedEnd');
                // All deletions
                changes = [
                    new DiffChange(originalStart, originalEnd - originalStart + 1, modifiedStart, 0)
                ];
            }
            else {
                Debug.Assert(originalStart === originalEnd + 1, 'originalStart should only be one more than originalEnd');
                Debug.Assert(modifiedStart === modifiedEnd + 1, 'modifiedStart should only be one more than modifiedEnd');
                // Identical sequences - No differences
                changes = [];
            }
            return changes;
        }
        // This problem can be solved using the Divide-And-Conquer technique.
        var midOriginalArr = [0], midModifiedArr = [0];
        var result = this.ComputeRecursionPoint(originalStart, originalEnd, modifiedStart, modifiedEnd, midOriginalArr, midModifiedArr, quitEarlyArr);
        var midOriginal = midOriginalArr[0];
        var midModified = midModifiedArr[0];
        if (result !== null) {
            // Result is not-null when there was enough memory to compute the changes while
            // searching for the recursion point
            return result;
        }
        else if (!quitEarlyArr[0]) {
            // We can break the problem down recursively by finding the changes in the
            // First Half:   (originalStart, modifiedStart) to (midOriginal, midModified)
            // Second Half:  (midOriginal + 1, minModified + 1) to (originalEnd, modifiedEnd)
            // NOTE: ComputeDiff() is inclusive, therefore the second range starts on the next point
            var leftChanges = this.ComputeDiffRecursive(originalStart, midOriginal, modifiedStart, midModified, quitEarlyArr);
            var rightChanges = [];
            if (!quitEarlyArr[0]) {
                rightChanges = this.ComputeDiffRecursive(midOriginal + 1, originalEnd, midModified + 1, modifiedEnd, quitEarlyArr);
            }
            else {
                // We did't have time to finish the first half, so we don't have time to compute this half.
                // Consider the entire rest of the sequence different.
                rightChanges = [
                    new DiffChange(midOriginal + 1, originalEnd - (midOriginal + 1) + 1, midModified + 1, modifiedEnd - (midModified + 1) + 1)
                ];
            }
            return this.ConcatenateChanges(leftChanges, rightChanges);
        }
        // If we hit here, we quit early, and so can't return anything meaningful
        return [
            new DiffChange(originalStart, originalEnd - originalStart + 1, modifiedStart, modifiedEnd - modifiedStart + 1)
        ];
    };
    LcsDiff.prototype.WALKTRACE = function (diagonalForwardBase, diagonalForwardStart, diagonalForwardEnd, diagonalForwardOffset, diagonalReverseBase, diagonalReverseStart, diagonalReverseEnd, diagonalReverseOffset, forwardPoints, reversePoints, originalIndex, originalEnd, midOriginalArr, modifiedIndex, modifiedEnd, midModifiedArr, deltaIsEven, quitEarlyArr) {
        var forwardChanges = null, reverseChanges = null;
        // First, walk backward through the forward diagonals history
        var changeHelper = new diff_DiffChangeHelper();
        var diagonalMin = diagonalForwardStart;
        var diagonalMax = diagonalForwardEnd;
        var diagonalRelative = (midOriginalArr[0] - midModifiedArr[0]) - diagonalForwardOffset;
        var lastOriginalIndex = Number.MIN_VALUE;
        var historyIndex = this.m_forwardHistory.length - 1;
        var diagonal;
        do {
            // Get the diagonal index from the relative diagonal number
            diagonal = diagonalRelative + diagonalForwardBase;
            // Figure out where we came from
            if (diagonal === diagonalMin || (diagonal < diagonalMax && forwardPoints[diagonal - 1] < forwardPoints[diagonal + 1])) {
                // Vertical line (the element is an insert)
                originalIndex = forwardPoints[diagonal + 1];
                modifiedIndex = originalIndex - diagonalRelative - diagonalForwardOffset;
                if (originalIndex < lastOriginalIndex) {
                    changeHelper.MarkNextChange();
                }
                lastOriginalIndex = originalIndex;
                changeHelper.AddModifiedElement(originalIndex + 1, modifiedIndex);
                diagonalRelative = (diagonal + 1) - diagonalForwardBase; //Setup for the next iteration
            }
            else {
                // Horizontal line (the element is a deletion)
                originalIndex = forwardPoints[diagonal - 1] + 1;
                modifiedIndex = originalIndex - diagonalRelative - diagonalForwardOffset;
                if (originalIndex < lastOriginalIndex) {
                    changeHelper.MarkNextChange();
                }
                lastOriginalIndex = originalIndex - 1;
                changeHelper.AddOriginalElement(originalIndex, modifiedIndex + 1);
                diagonalRelative = (diagonal - 1) - diagonalForwardBase; //Setup for the next iteration
            }
            if (historyIndex >= 0) {
                forwardPoints = this.m_forwardHistory[historyIndex];
                diagonalForwardBase = forwardPoints[0]; //We stored this in the first spot
                diagonalMin = 1;
                diagonalMax = forwardPoints.length - 1;
            }
        } while (--historyIndex >= -1);
        // Ironically, we get the forward changes as the reverse of the
        // order we added them since we technically added them backwards
        forwardChanges = changeHelper.getReverseChanges();
        if (quitEarlyArr[0]) {
            // TODO: Calculate a partial from the reverse diagonals.
            //       For now, just assume everything after the midOriginal/midModified point is a diff
            var originalStartPoint = midOriginalArr[0] + 1;
            var modifiedStartPoint = midModifiedArr[0] + 1;
            if (forwardChanges !== null && forwardChanges.length > 0) {
                var lastForwardChange = forwardChanges[forwardChanges.length - 1];
                originalStartPoint = Math.max(originalStartPoint, lastForwardChange.getOriginalEnd());
                modifiedStartPoint = Math.max(modifiedStartPoint, lastForwardChange.getModifiedEnd());
            }
            reverseChanges = [
                new DiffChange(originalStartPoint, originalEnd - originalStartPoint + 1, modifiedStartPoint, modifiedEnd - modifiedStartPoint + 1)
            ];
        }
        else {
            // Now walk backward through the reverse diagonals history
            changeHelper = new diff_DiffChangeHelper();
            diagonalMin = diagonalReverseStart;
            diagonalMax = diagonalReverseEnd;
            diagonalRelative = (midOriginalArr[0] - midModifiedArr[0]) - diagonalReverseOffset;
            lastOriginalIndex = Number.MAX_VALUE;
            historyIndex = (deltaIsEven) ? this.m_reverseHistory.length - 1 : this.m_reverseHistory.length - 2;
            do {
                // Get the diagonal index from the relative diagonal number
                diagonal = diagonalRelative + diagonalReverseBase;
                // Figure out where we came from
                if (diagonal === diagonalMin || (diagonal < diagonalMax && reversePoints[diagonal - 1] >= reversePoints[diagonal + 1])) {
                    // Horizontal line (the element is a deletion))
                    originalIndex = reversePoints[diagonal + 1] - 1;
                    modifiedIndex = originalIndex - diagonalRelative - diagonalReverseOffset;
                    if (originalIndex > lastOriginalIndex) {
                        changeHelper.MarkNextChange();
                    }
                    lastOriginalIndex = originalIndex + 1;
                    changeHelper.AddOriginalElement(originalIndex + 1, modifiedIndex + 1);
                    diagonalRelative = (diagonal + 1) - diagonalReverseBase; //Setup for the next iteration
                }
                else {
                    // Vertical line (the element is an insertion)
                    originalIndex = reversePoints[diagonal - 1];
                    modifiedIndex = originalIndex - diagonalRelative - diagonalReverseOffset;
                    if (originalIndex > lastOriginalIndex) {
                        changeHelper.MarkNextChange();
                    }
                    lastOriginalIndex = originalIndex;
                    changeHelper.AddModifiedElement(originalIndex + 1, modifiedIndex + 1);
                    diagonalRelative = (diagonal - 1) - diagonalReverseBase; //Setup for the next iteration
                }
                if (historyIndex >= 0) {
                    reversePoints = this.m_reverseHistory[historyIndex];
                    diagonalReverseBase = reversePoints[0]; //We stored this in the first spot
                    diagonalMin = 1;
                    diagonalMax = reversePoints.length - 1;
                }
            } while (--historyIndex >= -1);
            // There are cases where the reverse history will find diffs that
            // are correct, but not intuitive, so we need shift them.
            reverseChanges = changeHelper.getChanges();
        }
        return this.ConcatenateChanges(forwardChanges, reverseChanges);
    };
    /**
     * Given the range to compute the diff on, this method finds the point:
     * (midOriginal, midModified)
     * that exists in the middle of the LCS of the two sequences and
     * is the point at which the LCS problem may be broken down recursively.
     * This method will try to keep the LCS trace in memory. If the LCS recursion
     * point is calculated and the full trace is available in memory, then this method
     * will return the change list.
     * @param originalStart The start bound of the original sequence range
     * @param originalEnd The end bound of the original sequence range
     * @param modifiedStart The start bound of the modified sequence range
     * @param modifiedEnd The end bound of the modified sequence range
     * @param midOriginal The middle point of the original sequence range
     * @param midModified The middle point of the modified sequence range
     * @returns The diff changes, if available, otherwise null
     */
    LcsDiff.prototype.ComputeRecursionPoint = function (originalStart, originalEnd, modifiedStart, modifiedEnd, midOriginalArr, midModifiedArr, quitEarlyArr) {
        var originalIndex, modifiedIndex;
        var diagonalForwardStart = 0, diagonalForwardEnd = 0;
        var diagonalReverseStart = 0, diagonalReverseEnd = 0;
        var numDifferences;
        // To traverse the edit graph and produce the proper LCS, our actual
        // start position is just outside the given boundary
        originalStart--;
        modifiedStart--;
        // We set these up to make the compiler happy, but they will
        // be replaced before we return with the actual recursion point
        midOriginalArr[0] = 0;
        midModifiedArr[0] = 0;
        // Clear out the history
        this.m_forwardHistory = [];
        this.m_reverseHistory = [];
        // Each cell in the two arrays corresponds to a diagonal in the edit graph.
        // The integer value in the cell represents the originalIndex of the furthest
        // reaching point found so far that ends in that diagonal.
        // The modifiedIndex can be computed mathematically from the originalIndex and the diagonal number.
        var maxDifferences = (originalEnd - originalStart) + (modifiedEnd - modifiedStart);
        var numDiagonals = maxDifferences + 1;
        var forwardPoints = new Array(numDiagonals);
        var reversePoints = new Array(numDiagonals);
        // diagonalForwardBase: Index into forwardPoints of the diagonal which passes through (originalStart, modifiedStart)
        // diagonalReverseBase: Index into reversePoints of the diagonal which passes through (originalEnd, modifiedEnd)
        var diagonalForwardBase = (modifiedEnd - modifiedStart);
        var diagonalReverseBase = (originalEnd - originalStart);
        // diagonalForwardOffset: Geometric offset which allows modifiedIndex to be computed from originalIndex and the
        //    diagonal number (relative to diagonalForwardBase)
        // diagonalReverseOffset: Geometric offset which allows modifiedIndex to be computed from originalIndex and the
        //    diagonal number (relative to diagonalReverseBase)
        var diagonalForwardOffset = (originalStart - modifiedStart);
        var diagonalReverseOffset = (originalEnd - modifiedEnd);
        // delta: The difference between the end diagonal and the start diagonal. This is used to relate diagonal numbers
        //   relative to the start diagonal with diagonal numbers relative to the end diagonal.
        // The Even/Oddn-ness of this delta is important for determining when we should check for overlap
        var delta = diagonalReverseBase - diagonalForwardBase;
        var deltaIsEven = (delta % 2 === 0);
        // Here we set up the start and end points as the furthest points found so far
        // in both the forward and reverse directions, respectively
        forwardPoints[diagonalForwardBase] = originalStart;
        reversePoints[diagonalReverseBase] = originalEnd;
        // Remember if we quit early, and thus need to do a best-effort result instead of a real result.
        quitEarlyArr[0] = false;
        // A couple of points:
        // --With this method, we iterate on the number of differences between the two sequences.
        //   The more differences there actually are, the longer this will take.
        // --Also, as the number of differences increases, we have to search on diagonals further
        //   away from the reference diagonal (which is diagonalForwardBase for forward, diagonalReverseBase for reverse).
        // --We extend on even diagonals (relative to the reference diagonal) only when numDifferences
        //   is even and odd diagonals only when numDifferences is odd.
        var diagonal, tempOriginalIndex;
        for (numDifferences = 1; numDifferences <= (maxDifferences / 2) + 1; numDifferences++) {
            var furthestOriginalIndex = 0;
            var furthestModifiedIndex = 0;
            // Run the algorithm in the forward direction
            diagonalForwardStart = this.ClipDiagonalBound(diagonalForwardBase - numDifferences, numDifferences, diagonalForwardBase, numDiagonals);
            diagonalForwardEnd = this.ClipDiagonalBound(diagonalForwardBase + numDifferences, numDifferences, diagonalForwardBase, numDiagonals);
            for (diagonal = diagonalForwardStart; diagonal <= diagonalForwardEnd; diagonal += 2) {
                // STEP 1: We extend the furthest reaching point in the present diagonal
                // by looking at the diagonals above and below and picking the one whose point
                // is further away from the start point (originalStart, modifiedStart)
                if (diagonal === diagonalForwardStart || (diagonal < diagonalForwardEnd && forwardPoints[diagonal - 1] < forwardPoints[diagonal + 1])) {
                    originalIndex = forwardPoints[diagonal + 1];
                }
                else {
                    originalIndex = forwardPoints[diagonal - 1] + 1;
                }
                modifiedIndex = originalIndex - (diagonal - diagonalForwardBase) - diagonalForwardOffset;
                // Save the current originalIndex so we can test for false overlap in step 3
                tempOriginalIndex = originalIndex;
                // STEP 2: We can continue to extend the furthest reaching point in the present diagonal
                // so long as the elements are equal.
                while (originalIndex < originalEnd && modifiedIndex < modifiedEnd && this.ElementsAreEqual(originalIndex + 1, modifiedIndex + 1)) {
                    originalIndex++;
                    modifiedIndex++;
                }
                forwardPoints[diagonal] = originalIndex;
                if (originalIndex + modifiedIndex > furthestOriginalIndex + furthestModifiedIndex) {
                    furthestOriginalIndex = originalIndex;
                    furthestModifiedIndex = modifiedIndex;
                }
                // STEP 3: If delta is odd (overlap first happens on forward when delta is odd)
                // and diagonal is in the range of reverse diagonals computed for numDifferences-1
                // (the previous iteration; we haven't computed reverse diagonals for numDifferences yet)
                // then check for overlap.
                if (!deltaIsEven && Math.abs(diagonal - diagonalReverseBase) <= (numDifferences - 1)) {
                    if (originalIndex >= reversePoints[diagonal]) {
                        midOriginalArr[0] = originalIndex;
                        midModifiedArr[0] = modifiedIndex;
                        if (tempOriginalIndex <= reversePoints[diagonal] && MaxDifferencesHistory > 0 && numDifferences <= (MaxDifferencesHistory + 1)) {
                            // BINGO! We overlapped, and we have the full trace in memory!
                            return this.WALKTRACE(diagonalForwardBase, diagonalForwardStart, diagonalForwardEnd, diagonalForwardOffset, diagonalReverseBase, diagonalReverseStart, diagonalReverseEnd, diagonalReverseOffset, forwardPoints, reversePoints, originalIndex, originalEnd, midOriginalArr, modifiedIndex, modifiedEnd, midModifiedArr, deltaIsEven, quitEarlyArr);
                        }
                        else {
                            // Either false overlap, or we didn't have enough memory for the full trace
                            // Just return the recursion point
                            return null;
                        }
                    }
                }
            }
            // Check to see if we should be quitting early, before moving on to the next iteration.
            var matchLengthOfLongest = ((furthestOriginalIndex - originalStart) + (furthestModifiedIndex - modifiedStart) - numDifferences) / 2;
            if (this.ContinueProcessingPredicate !== null && !this.ContinueProcessingPredicate(furthestOriginalIndex, this.OriginalSequence, matchLengthOfLongest)) {
                // We can't finish, so skip ahead to generating a result from what we have.
                quitEarlyArr[0] = true;
                // Use the furthest distance we got in the forward direction.
                midOriginalArr[0] = furthestOriginalIndex;
                midModifiedArr[0] = furthestModifiedIndex;
                if (matchLengthOfLongest > 0 && MaxDifferencesHistory > 0 && numDifferences <= (MaxDifferencesHistory + 1)) {
                    // Enough of the history is in memory to walk it backwards
                    return this.WALKTRACE(diagonalForwardBase, diagonalForwardStart, diagonalForwardEnd, diagonalForwardOffset, diagonalReverseBase, diagonalReverseStart, diagonalReverseEnd, diagonalReverseOffset, forwardPoints, reversePoints, originalIndex, originalEnd, midOriginalArr, modifiedIndex, modifiedEnd, midModifiedArr, deltaIsEven, quitEarlyArr);
                }
                else {
                    // We didn't actually remember enough of the history.
                    //Since we are quiting the diff early, we need to shift back the originalStart and modified start
                    //back into the boundary limits since we decremented their value above beyond the boundary limit.
                    originalStart++;
                    modifiedStart++;
                    return [
                        new DiffChange(originalStart, originalEnd - originalStart + 1, modifiedStart, modifiedEnd - modifiedStart + 1)
                    ];
                }
            }
            // Run the algorithm in the reverse direction
            diagonalReverseStart = this.ClipDiagonalBound(diagonalReverseBase - numDifferences, numDifferences, diagonalReverseBase, numDiagonals);
            diagonalReverseEnd = this.ClipDiagonalBound(diagonalReverseBase + numDifferences, numDifferences, diagonalReverseBase, numDiagonals);
            for (diagonal = diagonalReverseStart; diagonal <= diagonalReverseEnd; diagonal += 2) {
                // STEP 1: We extend the furthest reaching point in the present diagonal
                // by looking at the diagonals above and below and picking the one whose point
                // is further away from the start point (originalEnd, modifiedEnd)
                if (diagonal === diagonalReverseStart || (diagonal < diagonalReverseEnd && reversePoints[diagonal - 1] >= reversePoints[diagonal + 1])) {
                    originalIndex = reversePoints[diagonal + 1] - 1;
                }
                else {
                    originalIndex = reversePoints[diagonal - 1];
                }
                modifiedIndex = originalIndex - (diagonal - diagonalReverseBase) - diagonalReverseOffset;
                // Save the current originalIndex so we can test for false overlap
                tempOriginalIndex = originalIndex;
                // STEP 2: We can continue to extend the furthest reaching point in the present diagonal
                // as long as the elements are equal.
                while (originalIndex > originalStart && modifiedIndex > modifiedStart && this.ElementsAreEqual(originalIndex, modifiedIndex)) {
                    originalIndex--;
                    modifiedIndex--;
                }
                reversePoints[diagonal] = originalIndex;
                // STEP 4: If delta is even (overlap first happens on reverse when delta is even)
                // and diagonal is in the range of forward diagonals computed for numDifferences
                // then check for overlap.
                if (deltaIsEven && Math.abs(diagonal - diagonalForwardBase) <= numDifferences) {
                    if (originalIndex <= forwardPoints[diagonal]) {
                        midOriginalArr[0] = originalIndex;
                        midModifiedArr[0] = modifiedIndex;
                        if (tempOriginalIndex >= forwardPoints[diagonal] && MaxDifferencesHistory > 0 && numDifferences <= (MaxDifferencesHistory + 1)) {
                            // BINGO! We overlapped, and we have the full trace in memory!
                            return this.WALKTRACE(diagonalForwardBase, diagonalForwardStart, diagonalForwardEnd, diagonalForwardOffset, diagonalReverseBase, diagonalReverseStart, diagonalReverseEnd, diagonalReverseOffset, forwardPoints, reversePoints, originalIndex, originalEnd, midOriginalArr, modifiedIndex, modifiedEnd, midModifiedArr, deltaIsEven, quitEarlyArr);
                        }
                        else {
                            // Either false overlap, or we didn't have enough memory for the full trace
                            // Just return the recursion point
                            return null;
                        }
                    }
                }
            }
            // Save current vectors to history before the next iteration
            if (numDifferences <= MaxDifferencesHistory) {
                // We are allocating space for one extra int, which we fill with
                // the index of the diagonal base index
                var temp = new Array(diagonalForwardEnd - diagonalForwardStart + 2);
                temp[0] = diagonalForwardBase - diagonalForwardStart + 1;
                MyArray.Copy(forwardPoints, diagonalForwardStart, temp, 1, diagonalForwardEnd - diagonalForwardStart + 1);
                this.m_forwardHistory.push(temp);
                temp = new Array(diagonalReverseEnd - diagonalReverseStart + 2);
                temp[0] = diagonalReverseBase - diagonalReverseStart + 1;
                MyArray.Copy(reversePoints, diagonalReverseStart, temp, 1, diagonalReverseEnd - diagonalReverseStart + 1);
                this.m_reverseHistory.push(temp);
            }
        }
        // If we got here, then we have the full trace in history. We just have to convert it to a change list
        // NOTE: This part is a bit messy
        return this.WALKTRACE(diagonalForwardBase, diagonalForwardStart, diagonalForwardEnd, diagonalForwardOffset, diagonalReverseBase, diagonalReverseStart, diagonalReverseEnd, diagonalReverseOffset, forwardPoints, reversePoints, originalIndex, originalEnd, midOriginalArr, modifiedIndex, modifiedEnd, midModifiedArr, deltaIsEven, quitEarlyArr);
    };
    /**
     * Shifts the given changes to provide a more intuitive diff.
     * While the first element in a diff matches the first element after the diff,
     * we shift the diff down.
     *
     * @param changes The list of changes to shift
     * @returns The shifted changes
     */
    LcsDiff.prototype.ShiftChanges = function (changes) {
        var mergedDiffs;
        do {
            mergedDiffs = false;
            // Shift all the changes down first
            for (var i = 0; i < changes.length; i++) {
                var change = changes[i];
                var originalStop = (i < changes.length - 1) ? changes[i + 1].originalStart : this.OriginalSequence.getLength();
                var modifiedStop = (i < changes.length - 1) ? changes[i + 1].modifiedStart : this.ModifiedSequence.getLength();
                var checkOriginal = change.originalLength > 0;
                var checkModified = change.modifiedLength > 0;
                while (change.originalStart + change.originalLength < originalStop &&
                    change.modifiedStart + change.modifiedLength < modifiedStop &&
                    (!checkOriginal || this.OriginalElementsAreEqual(change.originalStart, change.originalStart + change.originalLength)) &&
                    (!checkModified || this.ModifiedElementsAreEqual(change.modifiedStart, change.modifiedStart + change.modifiedLength))) {
                    change.originalStart++;
                    change.modifiedStart++;
                }
            }
            // Build up the new list (we have to build a new list because we
            // might have changes we can merge together now)
            var result = new Array();
            var mergedChangeArr = [null];
            for (var i = 0; i < changes.length; i++) {
                if (i < changes.length - 1 && this.ChangesOverlap(changes[i], changes[i + 1], mergedChangeArr)) {
                    mergedDiffs = true;
                    result.push(mergedChangeArr[0]);
                    i++;
                }
                else {
                    result.push(changes[i]);
                }
            }
            changes = result;
        } while (mergedDiffs);
        // Shift changes back up until we hit empty or whitespace-only lines
        for (var i = changes.length - 1; i >= 0; i--) {
            var change = changes[i];
            var originalStop = 0;
            var modifiedStop = 0;
            if (i > 0) {
                var prevChange = changes[i - 1];
                if (prevChange.originalLength > 0) {
                    originalStop = prevChange.originalStart + prevChange.originalLength;
                }
                if (prevChange.modifiedLength > 0) {
                    modifiedStop = prevChange.modifiedStart + prevChange.modifiedLength;
                }
            }
            var checkOriginal = change.originalLength > 0;
            var checkModified = change.modifiedLength > 0;
            var bestDelta = 0;
            var bestScore = this._boundaryScore(change.originalStart, change.originalLength, change.modifiedStart, change.modifiedLength);
            for (var delta = 1;; delta++) {
                var originalStart = change.originalStart - delta;
                var modifiedStart = change.modifiedStart - delta;
                if (originalStart < originalStop || modifiedStart < modifiedStop) {
                    break;
                }
                if (checkOriginal && !this.OriginalElementsAreEqual(originalStart, originalStart + change.originalLength)) {
                    break;
                }
                if (checkModified && !this.ModifiedElementsAreEqual(modifiedStart, modifiedStart + change.modifiedLength)) {
                    break;
                }
                var score = this._boundaryScore(originalStart, change.originalLength, modifiedStart, change.modifiedLength);
                if (score > bestScore) {
                    bestScore = score;
                    bestDelta = delta;
                }
            }
            change.originalStart -= bestDelta;
            change.modifiedStart -= bestDelta;
        }
        return changes;
    };
    LcsDiff.prototype._OriginalIsBoundary = function (index) {
        if (index <= 0 || index >= this.OriginalSequence.getLength() - 1) {
            return true;
        }
        return /^\s*$/.test(this.OriginalSequence.getElementHash(index));
    };
    LcsDiff.prototype._OriginalRegionIsBoundary = function (originalStart, originalLength) {
        if (this._OriginalIsBoundary(originalStart) || this._OriginalIsBoundary(originalStart - 1)) {
            return true;
        }
        if (originalLength > 0) {
            var originalEnd = originalStart + originalLength;
            if (this._OriginalIsBoundary(originalEnd - 1) || this._OriginalIsBoundary(originalEnd)) {
                return true;
            }
        }
        return false;
    };
    LcsDiff.prototype._ModifiedIsBoundary = function (index) {
        if (index <= 0 || index >= this.ModifiedSequence.getLength() - 1) {
            return true;
        }
        return /^\s*$/.test(this.ModifiedSequence.getElementHash(index));
    };
    LcsDiff.prototype._ModifiedRegionIsBoundary = function (modifiedStart, modifiedLength) {
        if (this._ModifiedIsBoundary(modifiedStart) || this._ModifiedIsBoundary(modifiedStart - 1)) {
            return true;
        }
        if (modifiedLength > 0) {
            var modifiedEnd = modifiedStart + modifiedLength;
            if (this._ModifiedIsBoundary(modifiedEnd - 1) || this._ModifiedIsBoundary(modifiedEnd)) {
                return true;
            }
        }
        return false;
    };
    LcsDiff.prototype._boundaryScore = function (originalStart, originalLength, modifiedStart, modifiedLength) {
        var originalScore = (this._OriginalRegionIsBoundary(originalStart, originalLength) ? 1 : 0);
        var modifiedScore = (this._ModifiedRegionIsBoundary(modifiedStart, modifiedLength) ? 1 : 0);
        return (originalScore + modifiedScore);
    };
    /**
     * Concatenates the two input DiffChange lists and returns the resulting
     * list.
     * @param The left changes
     * @param The right changes
     * @returns The concatenated list
     */
    LcsDiff.prototype.ConcatenateChanges = function (left, right) {
        var mergedChangeArr = [];
        var result = null;
        if (left.length === 0 || right.length === 0) {
            return (right.length > 0) ? right : left;
        }
        else if (this.ChangesOverlap(left[left.length - 1], right[0], mergedChangeArr)) {
            // Since we break the problem down recursively, it is possible that we
            // might recurse in the middle of a change thereby splitting it into
            // two changes. Here in the combining stage, we detect and fuse those
            // changes back together
            result = new Array(left.length + right.length - 1);
            MyArray.Copy(left, 0, result, 0, left.length - 1);
            result[left.length - 1] = mergedChangeArr[0];
            MyArray.Copy(right, 1, result, left.length, right.length - 1);
            return result;
        }
        else {
            result = new Array(left.length + right.length);
            MyArray.Copy(left, 0, result, 0, left.length);
            MyArray.Copy(right, 0, result, left.length, right.length);
            return result;
        }
    };
    /**
     * Returns true if the two changes overlap and can be merged into a single
     * change
     * @param left The left change
     * @param right The right change
     * @param mergedChange The merged change if the two overlap, null otherwise
     * @returns True if the two changes overlap
     */
    LcsDiff.prototype.ChangesOverlap = function (left, right, mergedChangeArr) {
        Debug.Assert(left.originalStart <= right.originalStart, 'Left change is not less than or equal to right change');
        Debug.Assert(left.modifiedStart <= right.modifiedStart, 'Left change is not less than or equal to right change');
        if (left.originalStart + left.originalLength >= right.originalStart || left.modifiedStart + left.modifiedLength >= right.modifiedStart) {
            var originalStart = left.originalStart;
            var originalLength = left.originalLength;
            var modifiedStart = left.modifiedStart;
            var modifiedLength = left.modifiedLength;
            if (left.originalStart + left.originalLength >= right.originalStart) {
                originalLength = right.originalStart + right.originalLength - left.originalStart;
            }
            if (left.modifiedStart + left.modifiedLength >= right.modifiedStart) {
                modifiedLength = right.modifiedStart + right.modifiedLength - left.modifiedStart;
            }
            mergedChangeArr[0] = new DiffChange(originalStart, originalLength, modifiedStart, modifiedLength);
            return true;
        }
        else {
            mergedChangeArr[0] = null;
            return false;
        }
    };
    /**
     * Helper method used to clip a diagonal index to the range of valid
     * diagonals. This also decides whether or not the diagonal index,
     * if it exceeds the boundary, should be clipped to the boundary or clipped
     * one inside the boundary depending on the Even/Odd status of the boundary
     * and numDifferences.
     * @param diagonal The index of the diagonal to clip.
     * @param numDifferences The current number of differences being iterated upon.
     * @param diagonalBaseIndex The base reference diagonal.
     * @param numDiagonals The total number of diagonals.
     * @returns The clipped diagonal index.
     */
    LcsDiff.prototype.ClipDiagonalBound = function (diagonal, numDifferences, diagonalBaseIndex, numDiagonals) {
        if (diagonal >= 0 && diagonal < numDiagonals) {
            // Nothing to clip, its in range
            return diagonal;
        }
        // diagonalsBelow: The number of diagonals below the reference diagonal
        // diagonalsAbove: The number of diagonals above the reference diagonal
        var diagonalsBelow = diagonalBaseIndex;
        var diagonalsAbove = numDiagonals - diagonalBaseIndex - 1;
        var diffEven = (numDifferences % 2 === 0);
        if (diagonal < 0) {
            var lowerBoundEven = (diagonalsBelow % 2 === 0);
            return (diffEven === lowerBoundEven) ? 0 : 1;
        }
        else {
            var upperBoundEven = (diagonalsAbove % 2 === 0);
            return (diffEven === upperBoundEven) ? numDiagonals - 1 : numDiagonals - 2;
        }
    };
    return LcsDiff;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/map.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var map_extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();

function values(forEachable) {
    var result = [];
    forEachable.forEach(function (value) { return result.push(value); });
    return result;
}
function keys(map) {
    var result = [];
    map.forEach(function (value, key) { return result.push(key); });
    return result;
}
function getOrSet(map, key, value) {
    var result = map.get(key);
    if (result === void 0) {
        result = value;
        map.set(key, result);
    }
    return result;
}
var StringIterator = /** @class */ (function () {
    function StringIterator() {
        this._value = '';
        this._pos = 0;
    }
    StringIterator.prototype.reset = function (key) {
        this._value = key;
        this._pos = 0;
        return this;
    };
    StringIterator.prototype.next = function () {
        this._pos += 1;
        return this;
    };
    StringIterator.prototype.join = function (parts) {
        return parts.join('');
    };
    StringIterator.prototype.hasNext = function () {
        return this._pos < this._value.length - 1;
    };
    StringIterator.prototype.cmp = function (a) {
        var aCode = a.charCodeAt(0);
        var thisCode = this._value.charCodeAt(this._pos);
        return aCode - thisCode;
    };
    StringIterator.prototype.value = function () {
        return this._value[this._pos];
    };
    return StringIterator;
}());

var PathIterator = /** @class */ (function () {
    function PathIterator() {
    }
    PathIterator.prototype.reset = function (key) {
        this._value = key.replace(/\\$|\/$/, '');
        this._from = 0;
        this._to = 0;
        return this.next();
    };
    PathIterator.prototype.hasNext = function () {
        return this._to < this._value.length;
    };
    PathIterator.prototype.join = function (parts) {
        return parts.join('/');
    };
    PathIterator.prototype.next = function () {
        // this._data = key.split(/[\\/]/).filter(s => !!s);
        this._from = this._to;
        var justSeps = true;
        for (; this._to < this._value.length; this._to++) {
            var ch = this._value.charCodeAt(this._to);
            if (ch === PathIterator._fwd || ch === PathIterator._bwd) {
                if (justSeps) {
                    this._from++;
                }
                else {
                    break;
                }
            }
            else {
                justSeps = false;
            }
        }
        return this;
    };
    PathIterator.prototype.cmp = function (a) {
        var aPos = 0;
        var aLen = a.length;
        var thisPos = this._from;
        while (aPos < aLen && thisPos < this._to) {
            var cmp = a.charCodeAt(aPos) - this._value.charCodeAt(thisPos);
            if (cmp !== 0) {
                return cmp;
            }
            aPos += 1;
            thisPos += 1;
        }
        if (aLen === this._to - this._from) {
            return 0;
        }
        else if (aPos < aLen) {
            return -1;
        }
        else {
            return 1;
        }
    };
    PathIterator.prototype.value = function () {
        return this._value.substring(this._from, this._to);
    };
    PathIterator._fwd = '/'.charCodeAt(0);
    PathIterator._bwd = '\\'.charCodeAt(0);
    return PathIterator;
}());

var TernarySearchTreeNode = /** @class */ (function () {
    function TernarySearchTreeNode() {
    }
    TernarySearchTreeNode.prototype.isEmpty = function () {
        return !this.left && !this.mid && !this.right && !this.element;
    };
    return TernarySearchTreeNode;
}());
var TernarySearchTree = /** @class */ (function () {
    function TernarySearchTree(segments) {
        this._iter = segments;
    }
    TernarySearchTree.forPaths = function () {
        return new TernarySearchTree(new PathIterator());
    };
    TernarySearchTree.forStrings = function () {
        return new TernarySearchTree(new StringIterator());
    };
    TernarySearchTree.prototype.clear = function () {
        this._root = undefined;
    };
    TernarySearchTree.prototype.set = function (key, element) {
        var iter = this._iter.reset(key);
        var node;
        if (!this._root) {
            this._root = new TernarySearchTreeNode();
            this._root.str = iter.value();
        }
        node = this._root;
        while (true) {
            var val = iter.cmp(node.str);
            if (val > 0) {
                // left
                if (!node.left) {
                    node.left = new TernarySearchTreeNode();
                    node.left.str = iter.value();
                }
                node = node.left;
            }
            else if (val < 0) {
                // right
                if (!node.right) {
                    node.right = new TernarySearchTreeNode();
                    node.right.str = iter.value();
                }
                node = node.right;
            }
            else if (iter.hasNext()) {
                // mid
                iter.next();
                if (!node.mid) {
                    node.mid = new TernarySearchTreeNode();
                    node.mid.str = iter.value();
                }
                node = node.mid;
            }
            else {
                break;
            }
        }
        var oldElement = node.element;
        node.element = element;
        return oldElement;
    };
    TernarySearchTree.prototype.get = function (key) {
        var iter = this._iter.reset(key);
        var node = this._root;
        while (node) {
            var val = iter.cmp(node.str);
            if (val > 0) {
                // left
                node = node.left;
            }
            else if (val < 0) {
                // right
                node = node.right;
            }
            else if (iter.hasNext()) {
                // mid
                iter.next();
                node = node.mid;
            }
            else {
                break;
            }
        }
        return node ? node.element : undefined;
    };
    TernarySearchTree.prototype.delete = function (key) {
        var iter = this._iter.reset(key);
        var stack = [];
        var node = this._root;
        // find and unset node
        while (node) {
            var val = iter.cmp(node.str);
            if (val > 0) {
                // left
                stack.push([1, node]);
                node = node.left;
            }
            else if (val < 0) {
                // right
                stack.push([-1, node]);
                node = node.right;
            }
            else if (iter.hasNext()) {
                // mid
                iter.next();
                stack.push([0, node]);
                node = node.mid;
            }
            else {
                // remove element
                node.element = undefined;
                // clean up empty nodes
                while (stack.length > 0 && node.isEmpty()) {
                    var _a = stack.pop(), dir = _a[0], parent_1 = _a[1];
                    switch (dir) {
                        case 1:
                            parent_1.left = undefined;
                            break;
                        case 0:
                            parent_1.mid = undefined;
                            break;
                        case -1:
                            parent_1.right = undefined;
                            break;
                    }
                    node = parent_1;
                }
                break;
            }
        }
    };
    TernarySearchTree.prototype.findSubstr = function (key) {
        var iter = this._iter.reset(key);
        var node = this._root;
        var candidate;
        while (node) {
            var val = iter.cmp(node.str);
            if (val > 0) {
                // left
                node = node.left;
            }
            else if (val < 0) {
                // right
                node = node.right;
            }
            else if (iter.hasNext()) {
                // mid
                iter.next();
                candidate = node.element || candidate;
                node = node.mid;
            }
            else {
                break;
            }
        }
        return node && node.element || candidate;
    };
    TernarySearchTree.prototype.findSuperstr = function (key) {
        var iter = this._iter.reset(key);
        var node = this._root;
        while (node) {
            var val = iter.cmp(node.str);
            if (val > 0) {
                // left
                node = node.left;
            }
            else if (val < 0) {
                // right
                node = node.right;
            }
            else if (iter.hasNext()) {
                // mid
                iter.next();
                node = node.mid;
            }
            else {
                // collect
                if (!node.mid) {
                    return undefined;
                }
                var ret = new TernarySearchTree(this._iter);
                ret._root = node.mid;
                return ret;
            }
        }
        return undefined;
    };
    TernarySearchTree.prototype.forEach = function (callback) {
        this._forEach(this._root, [], callback);
    };
    TernarySearchTree.prototype._forEach = function (node, parts, callback) {
        if (node) {
            // left
            this._forEach(node.left, parts, callback);
            // node
            parts.push(node.str);
            if (node.element) {
                callback(node.element, this._iter.join(parts));
            }
            // mid
            this._forEach(node.mid, parts, callback);
            parts.pop();
            // right
            this._forEach(node.right, parts, callback);
        }
    };
    return TernarySearchTree;
}());

var map_ResourceMap = /** @class */ (function () {
    function ResourceMap() {
        this.map = new Map();
        this.ignoreCase = false; // in the future this should be an uri-comparator
    }
    ResourceMap.prototype.set = function (resource, value) {
        this.map.set(this.toKey(resource), value);
    };
    ResourceMap.prototype.get = function (resource) {
        return this.map.get(this.toKey(resource));
    };
    ResourceMap.prototype.has = function (resource) {
        return this.map.has(this.toKey(resource));
    };
    Object.defineProperty(ResourceMap.prototype, "size", {
        get: function () {
            return this.map.size;
        },
        enumerable: true,
        configurable: true
    });
    ResourceMap.prototype.clear = function () {
        this.map.clear();
    };
    ResourceMap.prototype.delete = function (resource) {
        return this.map.delete(this.toKey(resource));
    };
    ResourceMap.prototype.forEach = function (clb) {
        this.map.forEach(clb);
    };
    ResourceMap.prototype.values = function () {
        return values(this.map);
    };
    ResourceMap.prototype.toKey = function (resource) {
        var key = resource.toString();
        if (this.ignoreCase) {
            key = key.toLowerCase();
        }
        return key;
    };
    ResourceMap.prototype.keys = function () {
        return keys(this.map).map(common_uri.parse);
    };
    return ResourceMap;
}());

var Touch;
(function (Touch) {
    Touch[Touch["None"] = 0] = "None";
    Touch[Touch["AsOld"] = 1] = "AsOld";
    Touch[Touch["AsNew"] = 2] = "AsNew";
})(Touch || (Touch = {}));
var LinkedMap = /** @class */ (function () {
    function LinkedMap() {
        this._map = new Map();
        this._head = undefined;
        this._tail = undefined;
        this._size = 0;
    }
    LinkedMap.prototype.clear = function () {
        this._map.clear();
        this._head = undefined;
        this._tail = undefined;
        this._size = 0;
    };
    LinkedMap.prototype.isEmpty = function () {
        return !this._head && !this._tail;
    };
    Object.defineProperty(LinkedMap.prototype, "size", {
        get: function () {
            return this._size;
        },
        enumerable: true,
        configurable: true
    });
    LinkedMap.prototype.has = function (key) {
        return this._map.has(key);
    };
    LinkedMap.prototype.get = function (key, touch) {
        if (touch === void 0) { touch = Touch.None; }
        var item = this._map.get(key);
        if (!item) {
            return undefined;
        }
        if (touch !== Touch.None) {
            this.touch(item, touch);
        }
        return item.value;
    };
    LinkedMap.prototype.set = function (key, value, touch) {
        if (touch === void 0) { touch = Touch.None; }
        var item = this._map.get(key);
        if (item) {
            item.value = value;
            if (touch !== Touch.None) {
                this.touch(item, touch);
            }
        }
        else {
            item = { key: key, value: value, next: undefined, previous: undefined };
            switch (touch) {
                case Touch.None:
                    this.addItemLast(item);
                    break;
                case Touch.AsOld:
                    this.addItemFirst(item);
                    break;
                case Touch.AsNew:
                    this.addItemLast(item);
                    break;
                default:
                    this.addItemLast(item);
                    break;
            }
            this._map.set(key, item);
            this._size++;
        }
    };
    LinkedMap.prototype.delete = function (key) {
        return !!this.remove(key);
    };
    LinkedMap.prototype.remove = function (key) {
        var item = this._map.get(key);
        if (!item) {
            return undefined;
        }
        this._map.delete(key);
        this.removeItem(item);
        this._size--;
        return item.value;
    };
    LinkedMap.prototype.shift = function () {
        if (!this._head && !this._tail) {
            return undefined;
        }
        if (!this._head || !this._tail) {
            throw new Error('Invalid list');
        }
        var item = this._head;
        this._map.delete(item.key);
        this.removeItem(item);
        this._size--;
        return item.value;
    };
    LinkedMap.prototype.forEach = function (callbackfn, thisArg) {
        var current = this._head;
        while (current) {
            if (thisArg) {
                callbackfn.bind(thisArg)(current.value, current.key, this);
            }
            else {
                callbackfn(current.value, current.key, this);
            }
            current = current.next;
        }
    };
    LinkedMap.prototype.values = function () {
        var result = [];
        var current = this._head;
        while (current) {
            result.push(current.value);
            current = current.next;
        }
        return result;
    };
    LinkedMap.prototype.keys = function () {
        var result = [];
        var current = this._head;
        while (current) {
            result.push(current.key);
            current = current.next;
        }
        return result;
    };
    /* VS Code / Monaco editor runs on es5 which has no Symbol.iterator
    public keys(): IterableIterator<K> {
        let current = this._head;
        let iterator: IterableIterator<K> = {
            [Symbol.iterator]() {
                return iterator;
            },
            next():IteratorResult<K> {
                if (current) {
                    let result = { value: current.key, done: false };
                    current = current.next;
                    return result;
                } else {
                    return { value: undefined, done: true };
                }
            }
        };
        return iterator;
    }

    public values(): IterableIterator<V> {
        let current = this._head;
        let iterator: IterableIterator<V> = {
            [Symbol.iterator]() {
                return iterator;
            },
            next():IteratorResult<V> {
                if (current) {
                    let result = { value: current.value, done: false };
                    current = current.next;
                    return result;
                } else {
                    return { value: undefined, done: true };
                }
            }
        };
        return iterator;
    }
    */
    LinkedMap.prototype.trimOld = function (newSize) {
        if (newSize >= this.size) {
            return;
        }
        if (newSize === 0) {
            this.clear();
            return;
        }
        var current = this._head;
        var currentSize = this.size;
        while (current && currentSize > newSize) {
            this._map.delete(current.key);
            current = current.next;
            currentSize--;
        }
        this._head = current;
        this._size = currentSize;
        current.previous = void 0;
    };
    LinkedMap.prototype.addItemFirst = function (item) {
        // First time Insert
        if (!this._head && !this._tail) {
            this._tail = item;
        }
        else if (!this._head) {
            throw new Error('Invalid list');
        }
        else {
            item.next = this._head;
            this._head.previous = item;
        }
        this._head = item;
    };
    LinkedMap.prototype.addItemLast = function (item) {
        // First time Insert
        if (!this._head && !this._tail) {
            this._head = item;
        }
        else if (!this._tail) {
            throw new Error('Invalid list');
        }
        else {
            item.previous = this._tail;
            this._tail.next = item;
        }
        this._tail = item;
    };
    LinkedMap.prototype.removeItem = function (item) {
        if (item === this._head && item === this._tail) {
            this._head = void 0;
            this._tail = void 0;
        }
        else if (item === this._head) {
            this._head = item.next;
        }
        else if (item === this._tail) {
            this._tail = item.previous;
        }
        else {
            var next = item.next;
            var previous = item.previous;
            if (!next || !previous) {
                throw new Error('Invalid list');
            }
            next.previous = previous;
            previous.next = next;
        }
    };
    LinkedMap.prototype.touch = function (item, touch) {
        if (!this._head || !this._tail) {
            throw new Error('Invalid list');
        }
        if ((touch !== Touch.AsOld && touch !== Touch.AsNew)) {
            return;
        }
        if (touch === Touch.AsOld) {
            if (item === this._head) {
                return;
            }
            var next = item.next;
            var previous = item.previous;
            // Unlink the item
            if (item === this._tail) {
                // previous must be defined since item was not head but is tail
                // So there are more than on item in the map
                previous.next = void 0;
                this._tail = previous;
            }
            else {
                // Both next and previous are not undefined since item was neither head nor tail.
                next.previous = previous;
                previous.next = next;
            }
            // Insert the node at head
            item.previous = void 0;
            item.next = this._head;
            this._head.previous = item;
            this._head = item;
        }
        else if (touch === Touch.AsNew) {
            if (item === this._tail) {
                return;
            }
            var next = item.next;
            var previous = item.previous;
            // Unlink the item.
            if (item === this._head) {
                // next must be defined since item was not tail but is head
                // So there are more than on item in the map
                next.previous = void 0;
                this._head = next;
            }
            else {
                // Both next and previous are not undefined since item was neither head nor tail.
                next.previous = previous;
                previous.next = next;
            }
            item.next = void 0;
            item.previous = this._tail;
            this._tail.next = item;
            this._tail = item;
        }
    };
    LinkedMap.prototype.toJSON = function () {
        var data = [];
        this.forEach(function (value, key) {
            data.push([key, value]);
        });
        return data;
    };
    LinkedMap.prototype.fromJSON = function (data) {
        this.clear();
        for (var _i = 0, data_1 = data; _i < data_1.length; _i++) {
            var _a = data_1[_i], key = _a[0], value = _a[1];
            this.set(key, value);
        }
    };
    return LinkedMap;
}());

var LRUCache = /** @class */ (function (_super) {
    map_extends(LRUCache, _super);
    function LRUCache(limit, ratio) {
        if (ratio === void 0) { ratio = 1; }
        var _this = _super.call(this) || this;
        _this._limit = limit;
        _this._ratio = Math.min(Math.max(0, ratio), 1);
        return _this;
    }
    Object.defineProperty(LRUCache.prototype, "limit", {
        get: function () {
            return this._limit;
        },
        set: function (limit) {
            this._limit = limit;
            this.checkTrim();
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(LRUCache.prototype, "ratio", {
        get: function () {
            return this._ratio;
        },
        set: function (ratio) {
            this._ratio = Math.min(Math.max(0, ratio), 1);
            this.checkTrim();
        },
        enumerable: true,
        configurable: true
    });
    LRUCache.prototype.get = function (key) {
        return _super.prototype.get.call(this, key, Touch.AsNew);
    };
    LRUCache.prototype.peek = function (key) {
        return _super.prototype.get.call(this, key, Touch.None);
    };
    LRUCache.prototype.set = function (key, value) {
        _super.prototype.set.call(this, key, value, Touch.AsNew);
        this.checkTrim();
    };
    LRUCache.prototype.checkTrim = function () {
        if (this.size > this._limit) {
            this.trimOld(Math.round(this._limit * this._ratio));
        }
    };
    return LRUCache;
}(LinkedMap));


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/strings.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


/**
 * The empty string.
 */
var empty = '';
function isFalsyOrWhitespace(str) {
    if (!str || typeof str !== 'string') {
        return true;
    }
    return str.trim().length === 0;
}
/**
 * @returns the provided number with the given number of preceding zeros.
 */
function pad(n, l, char) {
    if (char === void 0) { char = '0'; }
    var str = '' + n;
    var r = [str];
    for (var i = str.length; i < l; i++) {
        r.push(char);
    }
    return r.reverse().join('');
}
var _formatRegexp = /{(\d+)}/g;
/**
 * Helper to produce a string with a variable number of arguments. Insert variable segments
 * into the string using the {n} notation where N is the index of the argument following the string.
 * @param value string to which formatting is applied
 * @param args replacements for {n}-entries
 */
function format(value) {
    var args = [];
    for (var _i = 1; _i < arguments.length; _i++) {
        args[_i - 1] = arguments[_i];
    }
    if (args.length === 0) {
        return value;
    }
    return value.replace(_formatRegexp, function (match, group) {
        var idx = parseInt(group, 10);
        return isNaN(idx) || idx < 0 || idx >= args.length ?
            match :
            args[idx];
    });
}
/**
 * Converts HTML characters inside the string to use entities instead. Makes the string safe from
 * being used e.g. in HTMLElement.innerHTML.
 */
function strings_escape(html) {
    return html.replace(/[<|>|&]/g, function (match) {
        switch (match) {
            case '<': return '&lt;';
            case '>': return '&gt;';
            case '&': return '&amp;';
            default: return match;
        }
    });
}
/**
 * Escapes regular expression characters in a given string
 */
function escapeRegExpCharacters(value) {
    return value.replace(/[\-\\\{\}\*\+\?\|\^\$\.\[\]\(\)\#]/g, '\\$&');
}
/**
 * Removes all occurrences of needle from the beginning and end of haystack.
 * @param haystack string to trim
 * @param needle the thing to trim (default is a blank)
 */
function trim(haystack, needle) {
    if (needle === void 0) { needle = ' '; }
    var trimmed = ltrim(haystack, needle);
    return rtrim(trimmed, needle);
}
/**
 * Removes all occurrences of needle from the beginning of haystack.
 * @param haystack string to trim
 * @param needle the thing to trim
 */
function ltrim(haystack, needle) {
    if (!haystack || !needle) {
        return haystack;
    }
    var needleLen = needle.length;
    if (needleLen === 0 || haystack.length === 0) {
        return haystack;
    }
    var offset = 0, idx = -1;
    while ((idx = haystack.indexOf(needle, offset)) === offset) {
        offset = offset + needleLen;
    }
    return haystack.substring(offset);
}
/**
 * Removes all occurrences of needle from the end of haystack.
 * @param haystack string to trim
 * @param needle the thing to trim
 */
function rtrim(haystack, needle) {
    if (!haystack || !needle) {
        return haystack;
    }
    var needleLen = needle.length, haystackLen = haystack.length;
    if (needleLen === 0 || haystackLen === 0) {
        return haystack;
    }
    var offset = haystackLen, idx = -1;
    while (true) {
        idx = haystack.lastIndexOf(needle, offset - 1);
        if (idx === -1 || idx + needleLen !== offset) {
            break;
        }
        if (idx === 0) {
            return '';
        }
        offset = idx;
    }
    return haystack.substring(0, offset);
}
function convertSimple2RegExpPattern(pattern) {
    return pattern.replace(/[\-\\\{\}\+\?\|\^\$\.\,\[\]\(\)\#\s]/g, '\\$&').replace(/[\*]/g, '.*');
}
function stripWildcards(pattern) {
    return pattern.replace(/\*/g, '');
}
/**
 * Determines if haystack starts with needle.
 */
function startsWith(haystack, needle) {
    if (haystack.length < needle.length) {
        return false;
    }
    if (haystack === needle) {
        return true;
    }
    for (var i = 0; i < needle.length; i++) {
        if (haystack[i] !== needle[i]) {
            return false;
        }
    }
    return true;
}
/**
 * Determines if haystack ends with needle.
 */
function endsWith(haystack, needle) {
    var diff = haystack.length - needle.length;
    if (diff > 0) {
        return haystack.indexOf(needle, diff) === diff;
    }
    else if (diff === 0) {
        return haystack === needle;
    }
    else {
        return false;
    }
}
function createRegExp(searchString, isRegex, options) {
    if (options === void 0) { options = {}; }
    if (!searchString) {
        throw new Error('Cannot create regex from empty string');
    }
    if (!isRegex) {
        searchString = escapeRegExpCharacters(searchString);
    }
    if (options.wholeWord) {
        if (!/\B/.test(searchString.charAt(0))) {
            searchString = '\\b' + searchString;
        }
        if (!/\B/.test(searchString.charAt(searchString.length - 1))) {
            searchString = searchString + '\\b';
        }
    }
    var modifiers = '';
    if (options.global) {
        modifiers += 'g';
    }
    if (!options.matchCase) {
        modifiers += 'i';
    }
    if (options.multiline) {
        modifiers += 'm';
    }
    return new RegExp(searchString, modifiers);
}
function regExpLeadsToEndlessLoop(regexp) {
    // Exit early if it's one of these special cases which are meant to match
    // against an empty string
    if (regexp.source === '^' || regexp.source === '^$' || regexp.source === '$' || regexp.source === '^\\s*$') {
        return false;
    }
    // We check against an empty string. If the regular expression doesn't advance
    // (e.g. ends in an endless loop) it will match an empty string.
    var match = regexp.exec('');
    return (match && regexp.lastIndex === 0);
}
function regExpContainsBackreference(regexpValue) {
    return !!regexpValue.match(/([^\\]|^)(\\\\)*\\\d+/);
}
/**
 * The normalize() method returns the Unicode Normalization Form of a given string. The form will be
 * the Normalization Form Canonical Composition.
 *
 * @see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/normalize}
 */
var canNormalize = typeof (''.normalize) === 'function';
var nfcCache = new LRUCache(10000); // bounded to 10000 elements
function normalizeNFC(str) {
    return normalize(str, 'NFC', nfcCache);
}
var nfdCache = new LRUCache(10000); // bounded to 10000 elements
function normalizeNFD(str) {
    return normalize(str, 'NFD', nfdCache);
}
var nonAsciiCharactersPattern = /[^\u0000-\u0080]/;
function normalize(str, form, normalizedCache) {
    if (!canNormalize || !str) {
        return str;
    }
    var cached = normalizedCache.get(str);
    if (cached) {
        return cached;
    }
    var res;
    if (nonAsciiCharactersPattern.test(str)) {
        res = str.normalize(form);
    }
    else {
        res = str;
    }
    // Use the cache for fast lookup
    normalizedCache.set(str, res);
    return res;
}
/**
 * Returns first index of the string that is not whitespace.
 * If string is empty or contains only whitespaces, returns -1
 */
function firstNonWhitespaceIndex(str) {
    for (var i = 0, len = str.length; i < len; i++) {
        var chCode = str.charCodeAt(i);
        if (chCode !== 32 /* Space */ && chCode !== 9 /* Tab */) {
            return i;
        }
    }
    return -1;
}
/**
 * Returns the leading whitespace of the string.
 * If the string contains only whitespaces, returns entire string
 */
function getLeadingWhitespace(str, start, end) {
    if (start === void 0) { start = 0; }
    if (end === void 0) { end = str.length; }
    for (var i = start; i < end; i++) {
        var chCode = str.charCodeAt(i);
        if (chCode !== 32 /* Space */ && chCode !== 9 /* Tab */) {
            return str.substring(start, i);
        }
    }
    return str.substring(start, end);
}
/**
 * Returns last index of the string that is not whitespace.
 * If string is empty or contains only whitespaces, returns -1
 */
function lastNonWhitespaceIndex(str, startIndex) {
    if (startIndex === void 0) { startIndex = str.length - 1; }
    for (var i = startIndex; i >= 0; i--) {
        var chCode = str.charCodeAt(i);
        if (chCode !== 32 /* Space */ && chCode !== 9 /* Tab */) {
            return i;
        }
    }
    return -1;
}
function compare(a, b) {
    if (a < b) {
        return -1;
    }
    else if (a > b) {
        return 1;
    }
    else {
        return 0;
    }
}
function compareIgnoreCase(a, b) {
    var len = Math.min(a.length, b.length);
    for (var i = 0; i < len; i++) {
        var codeA = a.charCodeAt(i);
        var codeB = b.charCodeAt(i);
        if (codeA === codeB) {
            // equal
            continue;
        }
        if (isUpperAsciiLetter(codeA)) {
            codeA += 32;
        }
        if (isUpperAsciiLetter(codeB)) {
            codeB += 32;
        }
        var diff = codeA - codeB;
        if (diff === 0) {
            // equal -> ignoreCase
            continue;
        }
        else if (isLowerAsciiLetter(codeA) && isLowerAsciiLetter(codeB)) {
            //
            return diff;
        }
        else {
            return compare(a.toLowerCase(), b.toLowerCase());
        }
    }
    if (a.length < b.length) {
        return -1;
    }
    else if (a.length > b.length) {
        return 1;
    }
    else {
        return 0;
    }
}
function isLowerAsciiLetter(code) {
    return code >= 97 /* a */ && code <= 122 /* z */;
}
function isUpperAsciiLetter(code) {
    return code >= 65 /* A */ && code <= 90 /* Z */;
}
function isAsciiLetter(code) {
    return isLowerAsciiLetter(code) || isUpperAsciiLetter(code);
}
function equalsIgnoreCase(a, b) {
    var len1 = a ? a.length : 0;
    var len2 = b ? b.length : 0;
    if (len1 !== len2) {
        return false;
    }
    return doEqualsIgnoreCase(a, b);
}
function doEqualsIgnoreCase(a, b, stopAt) {
    if (stopAt === void 0) { stopAt = a.length; }
    if (typeof a !== 'string' || typeof b !== 'string') {
        return false;
    }
    for (var i = 0; i < stopAt; i++) {
        var codeA = a.charCodeAt(i);
        var codeB = b.charCodeAt(i);
        if (codeA === codeB) {
            continue;
        }
        // a-z A-Z
        if (isAsciiLetter(codeA) && isAsciiLetter(codeB)) {
            var diff = Math.abs(codeA - codeB);
            if (diff !== 0 && diff !== 32) {
                return false;
            }
        }
        // Any other charcode
        else {
            if (String.fromCharCode(codeA).toLowerCase() !== String.fromCharCode(codeB).toLowerCase()) {
                return false;
            }
        }
    }
    return true;
}
function startsWithIgnoreCase(str, candidate) {
    var candidateLength = candidate.length;
    if (candidate.length > str.length) {
        return false;
    }
    return doEqualsIgnoreCase(str, candidate, candidateLength);
}
/**
 * @returns the length of the common prefix of the two strings.
 */
function commonPrefixLength(a, b) {
    var i, len = Math.min(a.length, b.length);
    for (i = 0; i < len; i++) {
        if (a.charCodeAt(i) !== b.charCodeAt(i)) {
            return i;
        }
    }
    return len;
}
/**
 * @returns the length of the common suffix of the two strings.
 */
function commonSuffixLength(a, b) {
    var i, len = Math.min(a.length, b.length);
    var aLastIndex = a.length - 1;
    var bLastIndex = b.length - 1;
    for (i = 0; i < len; i++) {
        if (a.charCodeAt(aLastIndex - i) !== b.charCodeAt(bLastIndex - i)) {
            return i;
        }
    }
    return len;
}
function substrEquals(a, aStart, aEnd, b, bStart, bEnd) {
    while (aStart < aEnd && bStart < bEnd) {
        if (a[aStart] !== b[bStart]) {
            return false;
        }
        aStart += 1;
        bStart += 1;
    }
    return true;
}
/**
 * Return the overlap between the suffix of `a` and the prefix of `b`.
 * For instance `overlap("foobar", "arr, I'm a pirate") === 2`.
 */
function overlap(a, b) {
    var aEnd = a.length;
    var bEnd = b.length;
    var aStart = aEnd - bEnd;
    if (aStart === 0) {
        return a === b ? aEnd : 0;
    }
    else if (aStart < 0) {
        bEnd += aStart;
        aStart = 0;
    }
    while (aStart < aEnd && bEnd > 0) {
        if (substrEquals(a, aStart, aEnd, b, 0, bEnd)) {
            return bEnd;
        }
        bEnd -= 1;
        aStart += 1;
    }
    return 0;
}
// --- unicode
// http://en.wikipedia.org/wiki/Surrogate_pair
// Returns the code point starting at a specified index in a string
// Code points U+0000 to U+D7FF and U+E000 to U+FFFF are represented on a single character
// Code points U+10000 to U+10FFFF are represented on two consecutive characters
//export function getUnicodePoint(str:string, index:number, len:number):number {
//	let chrCode = str.charCodeAt(index);
//	if (0xD800 <= chrCode && chrCode <= 0xDBFF && index + 1 < len) {
//		let nextChrCode = str.charCodeAt(index + 1);
//		if (0xDC00 <= nextChrCode && nextChrCode <= 0xDFFF) {
//			return (chrCode - 0xD800) << 10 + (nextChrCode - 0xDC00) + 0x10000;
//		}
//	}
//	return chrCode;
//}
function isHighSurrogate(charCode) {
    return (0xD800 <= charCode && charCode <= 0xDBFF);
}
function isLowSurrogate(charCode) {
    return (0xDC00 <= charCode && charCode <= 0xDFFF);
}
/**
 * Generated using https://github.com/alexandrudima/unicode-utils/blob/master/generate-rtl-test.js
 */
var CONTAINS_RTL = /(?:[\u05BE\u05C0\u05C3\u05C6\u05D0-\u05F4\u0608\u060B\u060D\u061B-\u064A\u066D-\u066F\u0671-\u06D5\u06E5\u06E6\u06EE\u06EF\u06FA-\u0710\u0712-\u072F\u074D-\u07A5\u07B1-\u07EA\u07F4\u07F5\u07FA-\u0815\u081A\u0824\u0828\u0830-\u0858\u085E-\u08BD\u200F\uFB1D\uFB1F-\uFB28\uFB2A-\uFD3D\uFD50-\uFDFC\uFE70-\uFEFC]|\uD802[\uDC00-\uDD1B\uDD20-\uDE00\uDE10-\uDE33\uDE40-\uDEE4\uDEEB-\uDF35\uDF40-\uDFFF]|\uD803[\uDC00-\uDCFF]|\uD83A[\uDC00-\uDCCF\uDD00-\uDD43\uDD50-\uDFFF]|\uD83B[\uDC00-\uDEBB])/;
/**
 * Returns true if `str` contains any Unicode character that is classified as "R" or "AL".
 */
function containsRTL(str) {
    return CONTAINS_RTL.test(str);
}
/**
 * Generated using https://github.com/alexandrudima/unicode-utils/blob/master/generate-emoji-test.js
 */
var CONTAINS_EMOJI = /(?:[\u231A\u231B\u23F0\u23F3\u2600-\u27BF\u2B50\u2B55]|\uD83C[\uDDE6-\uDDFF\uDF00-\uDFFF]|\uD83D[\uDC00-\uDE4F\uDE80-\uDEF8]|\uD83E[\uDD00-\uDDE6])/;
function containsEmoji(str) {
    return CONTAINS_EMOJI.test(str);
}
var IS_BASIC_ASCII = /^[\t\n\r\x20-\x7E]*$/;
/**
 * Returns true if `str` contains only basic ASCII characters in the range 32 - 126 (including 32 and 126) or \n, \r, \t
 */
function isBasicASCII(str) {
    return IS_BASIC_ASCII.test(str);
}
function containsFullWidthCharacter(str) {
    for (var i = 0, len = str.length; i < len; i++) {
        if (isFullWidthCharacter(str.charCodeAt(i))) {
            return true;
        }
    }
    return false;
}
function isFullWidthCharacter(charCode) {
    // Do a cheap trick to better support wrapping of wide characters, treat them as 2 columns
    // http://jrgraphix.net/research/unicode_blocks.php
    //          2E80 — 2EFF   CJK Radicals Supplement
    //          2F00 — 2FDF   Kangxi Radicals
    //          2FF0 — 2FFF   Ideographic Description Characters
    //          3000 — 303F   CJK Symbols and Punctuation
    //          3040 — 309F   Hiragana
    //          30A0 — 30FF   Katakana
    //          3100 — 312F   Bopomofo
    //          3130 — 318F   Hangul Compatibility Jamo
    //          3190 — 319F   Kanbun
    //          31A0 — 31BF   Bopomofo Extended
    //          31F0 — 31FF   Katakana Phonetic Extensions
    //          3200 — 32FF   Enclosed CJK Letters and Months
    //          3300 — 33FF   CJK Compatibility
    //          3400 — 4DBF   CJK Unified Ideographs Extension A
    //          4DC0 — 4DFF   Yijing Hexagram Symbols
    //          4E00 — 9FFF   CJK Unified Ideographs
    //          A000 — A48F   Yi Syllables
    //          A490 — A4CF   Yi Radicals
    //          AC00 — D7AF   Hangul Syllables
    // [IGNORE] D800 — DB7F   High Surrogates
    // [IGNORE] DB80 — DBFF   High Private Use Surrogates
    // [IGNORE] DC00 — DFFF   Low Surrogates
    // [IGNORE] E000 — F8FF   Private Use Area
    //          F900 — FAFF   CJK Compatibility Ideographs
    // [IGNORE] FB00 — FB4F   Alphabetic Presentation Forms
    // [IGNORE] FB50 — FDFF   Arabic Presentation Forms-A
    // [IGNORE] FE00 — FE0F   Variation Selectors
    // [IGNORE] FE20 — FE2F   Combining Half Marks
    // [IGNORE] FE30 — FE4F   CJK Compatibility Forms
    // [IGNORE] FE50 — FE6F   Small Form Variants
    // [IGNORE] FE70 — FEFF   Arabic Presentation Forms-B
    //          FF00 — FFEF   Halfwidth and Fullwidth Forms
    //               [https://en.wikipedia.org/wiki/Halfwidth_and_fullwidth_forms]
    //               of which FF01 - FF5E fullwidth ASCII of 21 to 7E
    // [IGNORE]    and FF65 - FFDC halfwidth of Katakana and Hangul
    // [IGNORE] FFF0 — FFFF   Specials
    charCode = +charCode; // @perf
    return ((charCode >= 0x2E80 && charCode <= 0xD7AF)
        || (charCode >= 0xF900 && charCode <= 0xFAFF)
        || (charCode >= 0xFF01 && charCode <= 0xFF5E));
}
/**
 * Given a string and a max length returns a shorted version. Shorting
 * happens at favorable positions - such as whitespace or punctuation characters.
 */
function lcut(text, n) {
    if (text.length < n) {
        return text;
    }
    var re = /\b/g;
    var i = 0;
    while (re.test(text)) {
        if (text.length - re.lastIndex < n) {
            break;
        }
        i = re.lastIndex;
        re.lastIndex += 1;
    }
    return text.substring(i).replace(/^\s/, empty);
}
// Escape codes
// http://en.wikipedia.org/wiki/ANSI_escape_code
var EL = /\x1B\x5B[12]?K/g; // Erase in line
var COLOR_START = /\x1b\[\d+m/g; // Color
var COLOR_END = /\x1b\[0?m/g; // Color
function removeAnsiEscapeCodes(str) {
    if (str) {
        str = str.replace(EL, '');
        str = str.replace(COLOR_START, '');
        str = str.replace(COLOR_END, '');
    }
    return str;
}
// -- UTF-8 BOM
var UTF8_BOM_CHARACTER = String.fromCharCode(65279 /* UTF8_BOM */);
function startsWithUTF8BOM(str) {
    return (str && str.length > 0 && str.charCodeAt(0) === 65279 /* UTF8_BOM */);
}
function stripUTF8BOM(str) {
    return startsWithUTF8BOM(str) ? str.substr(1) : str;
}
function safeBtoa(str) {
    return btoa(encodeURIComponent(str)); // we use encodeURIComponent because btoa fails for non Latin 1 values
}
function repeat(s, count) {
    var result = '';
    for (var i = 0; i < count; i++) {
        result += s;
    }
    return result;
}
/**
 * Checks if the characters of the provided query string are included in the
 * target string. The characters do not have to be contiguous within the string.
 */
function fuzzyContains(target, query) {
    if (!target || !query) {
        return false; // return early if target or query are undefined
    }
    if (target.length < query.length) {
        return false; // impossible for query to be contained in target
    }
    var queryLen = query.length;
    var targetLower = target.toLowerCase();
    var index = 0;
    var lastIndexOf = -1;
    while (index < queryLen) {
        var indexOf = targetLower.indexOf(query[index], lastIndexOf + 1);
        if (indexOf < 0) {
            return false;
        }
        lastIndexOf = indexOf;
        index++;
    }
    return true;
}
function containsUppercaseCharacter(target, ignoreEscapedChars) {
    if (ignoreEscapedChars === void 0) { ignoreEscapedChars = false; }
    if (!target) {
        return false;
    }
    if (ignoreEscapedChars) {
        target = target.replace(/\\./g, '');
    }
    return target.toLowerCase() !== target;
}

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/diff/diffComputer.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var diffComputer_extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();


var MAXIMUM_RUN_TIME = 5000; // 5 seconds
var MINIMUM_MATCHING_CHARACTER_LENGTH = 3;
function computeDiff(originalSequence, modifiedSequence, continueProcessingPredicate, pretty) {
    var diffAlgo = new diff_LcsDiff(originalSequence, modifiedSequence, continueProcessingPredicate);
    return diffAlgo.ComputeDiff(pretty);
}
var MarkerSequence = /** @class */ (function () {
    function MarkerSequence(buffer, startMarkers, endMarkers) {
        this.buffer = buffer;
        this.startMarkers = startMarkers;
        this.endMarkers = endMarkers;
    }
    MarkerSequence.prototype.getLength = function () {
        return this.startMarkers.length;
    };
    MarkerSequence.prototype.getElementHash = function (i) {
        return this.buffer.substring(this.startMarkers[i].offset, this.endMarkers[i].offset);
    };
    MarkerSequence.prototype.getStartLineNumber = function (i) {
        if (i === this.startMarkers.length) {
            // This is the special case where a change happened after the last marker
            return this.startMarkers[i - 1].lineNumber + 1;
        }
        return this.startMarkers[i].lineNumber;
    };
    MarkerSequence.prototype.getStartColumn = function (i) {
        return this.startMarkers[i].column;
    };
    MarkerSequence.prototype.getEndLineNumber = function (i) {
        return this.endMarkers[i].lineNumber;
    };
    MarkerSequence.prototype.getEndColumn = function (i) {
        return this.endMarkers[i].column;
    };
    return MarkerSequence;
}());
var diffComputer_LineMarkerSequence = /** @class */ (function (_super) {
    diffComputer_extends(LineMarkerSequence, _super);
    function LineMarkerSequence(lines) {
        var _this = this;
        var buffer = '';
        var startMarkers = [];
        var endMarkers = [];
        for (var pos = 0, i = 0, length_1 = lines.length; i < length_1; i++) {
            buffer += lines[i];
            var startColumn = LineMarkerSequence._getFirstNonBlankColumn(lines[i], 1);
            var endColumn = LineMarkerSequence._getLastNonBlankColumn(lines[i], 1);
            startMarkers.push({
                offset: pos + startColumn - 1,
                lineNumber: i + 1,
                column: startColumn
            });
            endMarkers.push({
                offset: pos + endColumn - 1,
                lineNumber: i + 1,
                column: endColumn
            });
            pos += lines[i].length;
        }
        _this = _super.call(this, buffer, startMarkers, endMarkers) || this;
        return _this;
    }
    LineMarkerSequence._getFirstNonBlankColumn = function (txt, defaultValue) {
        var r = firstNonWhitespaceIndex(txt);
        if (r === -1) {
            return defaultValue;
        }
        return r + 1;
    };
    LineMarkerSequence._getLastNonBlankColumn = function (txt, defaultValue) {
        var r = lastNonWhitespaceIndex(txt);
        if (r === -1) {
            return defaultValue;
        }
        return r + 2;
    };
    LineMarkerSequence.prototype.getCharSequence = function (startIndex, endIndex) {
        var startMarkers = [];
        var endMarkers = [];
        for (var index = startIndex; index <= endIndex; index++) {
            var startMarker = this.startMarkers[index];
            var endMarker = this.endMarkers[index];
            for (var i = startMarker.offset; i < endMarker.offset; i++) {
                startMarkers.push({
                    offset: i,
                    lineNumber: startMarker.lineNumber,
                    column: startMarker.column + (i - startMarker.offset)
                });
                endMarkers.push({
                    offset: i + 1,
                    lineNumber: startMarker.lineNumber,
                    column: startMarker.column + (i - startMarker.offset) + 1
                });
            }
        }
        return new MarkerSequence(this.buffer, startMarkers, endMarkers);
    };
    return LineMarkerSequence;
}(MarkerSequence));
var CharChange = /** @class */ (function () {
    function CharChange(originalStartLineNumber, originalStartColumn, originalEndLineNumber, originalEndColumn, modifiedStartLineNumber, modifiedStartColumn, modifiedEndLineNumber, modifiedEndColumn) {
        this.originalStartLineNumber = originalStartLineNumber;
        this.originalStartColumn = originalStartColumn;
        this.originalEndLineNumber = originalEndLineNumber;
        this.originalEndColumn = originalEndColumn;
        this.modifiedStartLineNumber = modifiedStartLineNumber;
        this.modifiedStartColumn = modifiedStartColumn;
        this.modifiedEndLineNumber = modifiedEndLineNumber;
        this.modifiedEndColumn = modifiedEndColumn;
    }
    CharChange.createFromDiffChange = function (diffChange, originalCharSequence, modifiedCharSequence) {
        var originalStartLineNumber;
        var originalStartColumn;
        var originalEndLineNumber;
        var originalEndColumn;
        var modifiedStartLineNumber;
        var modifiedStartColumn;
        var modifiedEndLineNumber;
        var modifiedEndColumn;
        if (diffChange.originalLength === 0) {
            originalStartLineNumber = 0;
            originalStartColumn = 0;
            originalEndLineNumber = 0;
            originalEndColumn = 0;
        }
        else {
            originalStartLineNumber = originalCharSequence.getStartLineNumber(diffChange.originalStart);
            originalStartColumn = originalCharSequence.getStartColumn(diffChange.originalStart);
            originalEndLineNumber = originalCharSequence.getEndLineNumber(diffChange.originalStart + diffChange.originalLength - 1);
            originalEndColumn = originalCharSequence.getEndColumn(diffChange.originalStart + diffChange.originalLength - 1);
        }
        if (diffChange.modifiedLength === 0) {
            modifiedStartLineNumber = 0;
            modifiedStartColumn = 0;
            modifiedEndLineNumber = 0;
            modifiedEndColumn = 0;
        }
        else {
            modifiedStartLineNumber = modifiedCharSequence.getStartLineNumber(diffChange.modifiedStart);
            modifiedStartColumn = modifiedCharSequence.getStartColumn(diffChange.modifiedStart);
            modifiedEndLineNumber = modifiedCharSequence.getEndLineNumber(diffChange.modifiedStart + diffChange.modifiedLength - 1);
            modifiedEndColumn = modifiedCharSequence.getEndColumn(diffChange.modifiedStart + diffChange.modifiedLength - 1);
        }
        return new CharChange(originalStartLineNumber, originalStartColumn, originalEndLineNumber, originalEndColumn, modifiedStartLineNumber, modifiedStartColumn, modifiedEndLineNumber, modifiedEndColumn);
    };
    return CharChange;
}());
function postProcessCharChanges(rawChanges) {
    if (rawChanges.length <= 1) {
        return rawChanges;
    }
    var result = [rawChanges[0]];
    var prevChange = result[0];
    for (var i = 1, len = rawChanges.length; i < len; i++) {
        var currChange = rawChanges[i];
        var originalMatchingLength = currChange.originalStart - (prevChange.originalStart + prevChange.originalLength);
        var modifiedMatchingLength = currChange.modifiedStart - (prevChange.modifiedStart + prevChange.modifiedLength);
        // Both of the above should be equal, but the continueProcessingPredicate may prevent this from being true
        var matchingLength = Math.min(originalMatchingLength, modifiedMatchingLength);
        if (matchingLength < MINIMUM_MATCHING_CHARACTER_LENGTH) {
            // Merge the current change into the previous one
            prevChange.originalLength = (currChange.originalStart + currChange.originalLength) - prevChange.originalStart;
            prevChange.modifiedLength = (currChange.modifiedStart + currChange.modifiedLength) - prevChange.modifiedStart;
        }
        else {
            // Add the current change
            result.push(currChange);
            prevChange = currChange;
        }
    }
    return result;
}
var LineChange = /** @class */ (function () {
    function LineChange(originalStartLineNumber, originalEndLineNumber, modifiedStartLineNumber, modifiedEndLineNumber, charChanges) {
        this.originalStartLineNumber = originalStartLineNumber;
        this.originalEndLineNumber = originalEndLineNumber;
        this.modifiedStartLineNumber = modifiedStartLineNumber;
        this.modifiedEndLineNumber = modifiedEndLineNumber;
        this.charChanges = charChanges;
    }
    LineChange.createFromDiffResult = function (diffChange, originalLineSequence, modifiedLineSequence, continueProcessingPredicate, shouldPostProcessCharChanges) {
        var originalStartLineNumber;
        var originalEndLineNumber;
        var modifiedStartLineNumber;
        var modifiedEndLineNumber;
        var charChanges;
        if (diffChange.originalLength === 0) {
            originalStartLineNumber = originalLineSequence.getStartLineNumber(diffChange.originalStart) - 1;
            originalEndLineNumber = 0;
        }
        else {
            originalStartLineNumber = originalLineSequence.getStartLineNumber(diffChange.originalStart);
            originalEndLineNumber = originalLineSequence.getEndLineNumber(diffChange.originalStart + diffChange.originalLength - 1);
        }
        if (diffChange.modifiedLength === 0) {
            modifiedStartLineNumber = modifiedLineSequence.getStartLineNumber(diffChange.modifiedStart) - 1;
            modifiedEndLineNumber = 0;
        }
        else {
            modifiedStartLineNumber = modifiedLineSequence.getStartLineNumber(diffChange.modifiedStart);
            modifiedEndLineNumber = modifiedLineSequence.getEndLineNumber(diffChange.modifiedStart + diffChange.modifiedLength - 1);
        }
        if (diffChange.originalLength !== 0 && diffChange.modifiedLength !== 0 && continueProcessingPredicate()) {
            var originalCharSequence = originalLineSequence.getCharSequence(diffChange.originalStart, diffChange.originalStart + diffChange.originalLength - 1);
            var modifiedCharSequence = modifiedLineSequence.getCharSequence(diffChange.modifiedStart, diffChange.modifiedStart + diffChange.modifiedLength - 1);
            var rawChanges = computeDiff(originalCharSequence, modifiedCharSequence, continueProcessingPredicate, true);
            if (shouldPostProcessCharChanges) {
                rawChanges = postProcessCharChanges(rawChanges);
            }
            charChanges = [];
            for (var i = 0, length_2 = rawChanges.length; i < length_2; i++) {
                charChanges.push(CharChange.createFromDiffChange(rawChanges[i], originalCharSequence, modifiedCharSequence));
            }
        }
        return new LineChange(originalStartLineNumber, originalEndLineNumber, modifiedStartLineNumber, modifiedEndLineNumber, charChanges);
    };
    return LineChange;
}());
var DiffComputer = /** @class */ (function () {
    function DiffComputer(originalLines, modifiedLines, opts) {
        this.shouldPostProcessCharChanges = opts.shouldPostProcessCharChanges;
        this.shouldIgnoreTrimWhitespace = opts.shouldIgnoreTrimWhitespace;
        this.shouldMakePrettyDiff = opts.shouldMakePrettyDiff;
        this.maximumRunTimeMs = MAXIMUM_RUN_TIME;
        this.originalLines = originalLines;
        this.modifiedLines = modifiedLines;
        this.original = new diffComputer_LineMarkerSequence(originalLines);
        this.modified = new diffComputer_LineMarkerSequence(modifiedLines);
    }
    DiffComputer.prototype.computeDiff = function () {
        if (this.original.getLength() === 1 && this.original.getElementHash(0).length === 0) {
            // empty original => fast path
            return [{
                    originalStartLineNumber: 1,
                    originalEndLineNumber: 1,
                    modifiedStartLineNumber: 1,
                    modifiedEndLineNumber: this.modified.getLength(),
                    charChanges: [{
                            modifiedEndColumn: 0,
                            modifiedEndLineNumber: 0,
                            modifiedStartColumn: 0,
                            modifiedStartLineNumber: 0,
                            originalEndColumn: 0,
                            originalEndLineNumber: 0,
                            originalStartColumn: 0,
                            originalStartLineNumber: 0
                        }]
                }];
        }
        if (this.modified.getLength() === 1 && this.modified.getElementHash(0).length === 0) {
            // empty modified => fast path
            return [{
                    originalStartLineNumber: 1,
                    originalEndLineNumber: this.original.getLength(),
                    modifiedStartLineNumber: 1,
                    modifiedEndLineNumber: 1,
                    charChanges: [{
                            modifiedEndColumn: 0,
                            modifiedEndLineNumber: 0,
                            modifiedStartColumn: 0,
                            modifiedStartLineNumber: 0,
                            originalEndColumn: 0,
                            originalEndLineNumber: 0,
                            originalStartColumn: 0,
                            originalStartLineNumber: 0
                        }]
                }];
        }
        this.computationStartTime = (new Date()).getTime();
        var rawChanges = computeDiff(this.original, this.modified, this._continueProcessingPredicate.bind(this), this.shouldMakePrettyDiff);
        // The diff is always computed with ignoring trim whitespace
        // This ensures we get the prettiest diff
        if (this.shouldIgnoreTrimWhitespace) {
            var lineChanges = [];
            for (var i = 0, length_3 = rawChanges.length; i < length_3; i++) {
                lineChanges.push(LineChange.createFromDiffResult(rawChanges[i], this.original, this.modified, this._continueProcessingPredicate.bind(this), this.shouldPostProcessCharChanges));
            }
            return lineChanges;
        }
        // Need to post-process and introduce changes where the trim whitespace is different
        // Note that we are looping starting at -1 to also cover the lines before the first change
        var result = [];
        var originalLineIndex = 0;
        var modifiedLineIndex = 0;
        for (var i = -1 /* !!!! */, len = rawChanges.length; i < len; i++) {
            var nextChange = (i + 1 < len ? rawChanges[i + 1] : null);
            var originalStop = (nextChange ? nextChange.originalStart : this.originalLines.length);
            var modifiedStop = (nextChange ? nextChange.modifiedStart : this.modifiedLines.length);
            while (originalLineIndex < originalStop && modifiedLineIndex < modifiedStop) {
                var originalLine = this.originalLines[originalLineIndex];
                var modifiedLine = this.modifiedLines[modifiedLineIndex];
                if (originalLine !== modifiedLine) {
                    // These lines differ only in trim whitespace
                    // Check the leading whitespace
                    {
                        var originalStartColumn = diffComputer_LineMarkerSequence._getFirstNonBlankColumn(originalLine, 1);
                        var modifiedStartColumn = diffComputer_LineMarkerSequence._getFirstNonBlankColumn(modifiedLine, 1);
                        while (originalStartColumn > 1 && modifiedStartColumn > 1) {
                            var originalChar = originalLine.charCodeAt(originalStartColumn - 2);
                            var modifiedChar = modifiedLine.charCodeAt(modifiedStartColumn - 2);
                            if (originalChar !== modifiedChar) {
                                break;
                            }
                            originalStartColumn--;
                            modifiedStartColumn--;
                        }
                        if (originalStartColumn > 1 || modifiedStartColumn > 1) {
                            this._pushTrimWhitespaceCharChange(result, originalLineIndex + 1, 1, originalStartColumn, modifiedLineIndex + 1, 1, modifiedStartColumn);
                        }
                    }
                    // Check the trailing whitespace
                    {
                        var originalEndColumn = diffComputer_LineMarkerSequence._getLastNonBlankColumn(originalLine, 1);
                        var modifiedEndColumn = diffComputer_LineMarkerSequence._getLastNonBlankColumn(modifiedLine, 1);
                        var originalMaxColumn = originalLine.length + 1;
                        var modifiedMaxColumn = modifiedLine.length + 1;
                        while (originalEndColumn < originalMaxColumn && modifiedEndColumn < modifiedMaxColumn) {
                            var originalChar = originalLine.charCodeAt(originalEndColumn - 1);
                            var modifiedChar = originalLine.charCodeAt(modifiedEndColumn - 1);
                            if (originalChar !== modifiedChar) {
                                break;
                            }
                            originalEndColumn++;
                            modifiedEndColumn++;
                        }
                        if (originalEndColumn < originalMaxColumn || modifiedEndColumn < modifiedMaxColumn) {
                            this._pushTrimWhitespaceCharChange(result, originalLineIndex + 1, originalEndColumn, originalMaxColumn, modifiedLineIndex + 1, modifiedEndColumn, modifiedMaxColumn);
                        }
                    }
                }
                originalLineIndex++;
                modifiedLineIndex++;
            }
            if (nextChange) {
                // Emit the actual change
                result.push(LineChange.createFromDiffResult(nextChange, this.original, this.modified, this._continueProcessingPredicate.bind(this), this.shouldPostProcessCharChanges));
                originalLineIndex += nextChange.originalLength;
                modifiedLineIndex += nextChange.modifiedLength;
            }
        }
        return result;
    };
    DiffComputer.prototype._pushTrimWhitespaceCharChange = function (result, originalLineNumber, originalStartColumn, originalEndColumn, modifiedLineNumber, modifiedStartColumn, modifiedEndColumn) {
        if (this._mergeTrimWhitespaceCharChange(result, originalLineNumber, originalStartColumn, originalEndColumn, modifiedLineNumber, modifiedStartColumn, modifiedEndColumn)) {
            // Merged into previous
            return;
        }
        result.push(new LineChange(originalLineNumber, originalLineNumber, modifiedLineNumber, modifiedLineNumber, [
            new CharChange(originalLineNumber, originalStartColumn, originalLineNumber, originalEndColumn, modifiedLineNumber, modifiedStartColumn, modifiedLineNumber, modifiedEndColumn)
        ]));
    };
    DiffComputer.prototype._mergeTrimWhitespaceCharChange = function (result, originalLineNumber, originalStartColumn, originalEndColumn, modifiedLineNumber, modifiedStartColumn, modifiedEndColumn) {
        var len = result.length;
        if (len === 0) {
            return false;
        }
        var prevChange = result[len - 1];
        if (prevChange.originalEndLineNumber === 0 || prevChange.modifiedEndLineNumber === 0) {
            // Don't merge with inserts/deletes
            return false;
        }
        if (prevChange.originalEndLineNumber + 1 === originalLineNumber && prevChange.modifiedEndLineNumber + 1 === modifiedLineNumber) {
            prevChange.originalEndLineNumber = originalLineNumber;
            prevChange.modifiedEndLineNumber = modifiedLineNumber;
            prevChange.charChanges.push(new CharChange(originalLineNumber, originalStartColumn, originalLineNumber, originalEndColumn, modifiedLineNumber, modifiedStartColumn, modifiedLineNumber, modifiedEndColumn));
            return true;
        }
        return false;
    };
    DiffComputer.prototype._continueProcessingPredicate = function () {
        if (this.maximumRunTimeMs === 0) {
            return true;
        }
        var now = (new Date()).getTime();
        return now - this.computationStartTime < this.maximumRunTimeMs;
    };
    return DiffComputer;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/core/uint.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var Uint8Matrix = /** @class */ (function () {
    function Uint8Matrix(rows, cols, defaultValue) {
        var data = new Uint8Array(rows * cols);
        for (var i = 0, len = rows * cols; i < len; i++) {
            data[i] = defaultValue;
        }
        this._data = data;
        this.rows = rows;
        this.cols = cols;
    }
    Uint8Matrix.prototype.get = function (row, col) {
        return this._data[row * this.cols + col];
    };
    Uint8Matrix.prototype.set = function (row, col, value) {
        this._data[row * this.cols + col] = value;
    };
    return Uint8Matrix;
}());

function toUint8(v) {
    if (v < 0) {
        return 0;
    }
    if (v > 255 /* MAX_UINT_8 */) {
        return 255 /* MAX_UINT_8 */;
    }
    return v | 0;
}
function toUint32(v) {
    if (v < 0) {
        return 0;
    }
    if (v > 4294967295 /* MAX_UINT_32 */) {
        return 4294967295 /* MAX_UINT_32 */;
    }
    return v | 0;
}
function toUint32Array(arr) {
    var len = arr.length;
    var r = new Uint32Array(len);
    for (var i = 0; i < len; i++) {
        r[i] = toUint32(arr[i]);
    }
    return r;
}

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/viewModel/prefixSumComputer.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


var PrefixSumIndexOfResult = /** @class */ (function () {
    function PrefixSumIndexOfResult(index, remainder) {
        this.index = index;
        this.remainder = remainder;
    }
    return PrefixSumIndexOfResult;
}());

var prefixSumComputer_PrefixSumComputer = /** @class */ (function () {
    function PrefixSumComputer(values) {
        this.values = values;
        this.prefixSum = new Uint32Array(values.length);
        this.prefixSumValidIndex = new Int32Array(1);
        this.prefixSumValidIndex[0] = -1;
    }
    PrefixSumComputer.prototype.getCount = function () {
        return this.values.length;
    };
    PrefixSumComputer.prototype.insertValues = function (insertIndex, insertValues) {
        insertIndex = toUint32(insertIndex);
        var oldValues = this.values;
        var oldPrefixSum = this.prefixSum;
        var insertValuesLen = insertValues.length;
        if (insertValuesLen === 0) {
            return false;
        }
        this.values = new Uint32Array(oldValues.length + insertValuesLen);
        this.values.set(oldValues.subarray(0, insertIndex), 0);
        this.values.set(oldValues.subarray(insertIndex), insertIndex + insertValuesLen);
        this.values.set(insertValues, insertIndex);
        if (insertIndex - 1 < this.prefixSumValidIndex[0]) {
            this.prefixSumValidIndex[0] = insertIndex - 1;
        }
        this.prefixSum = new Uint32Array(this.values.length);
        if (this.prefixSumValidIndex[0] >= 0) {
            this.prefixSum.set(oldPrefixSum.subarray(0, this.prefixSumValidIndex[0] + 1));
        }
        return true;
    };
    PrefixSumComputer.prototype.changeValue = function (index, value) {
        index = toUint32(index);
        value = toUint32(value);
        if (this.values[index] === value) {
            return false;
        }
        this.values[index] = value;
        if (index - 1 < this.prefixSumValidIndex[0]) {
            this.prefixSumValidIndex[0] = index - 1;
        }
        return true;
    };
    PrefixSumComputer.prototype.removeValues = function (startIndex, cnt) {
        startIndex = toUint32(startIndex);
        cnt = toUint32(cnt);
        var oldValues = this.values;
        var oldPrefixSum = this.prefixSum;
        if (startIndex >= oldValues.length) {
            return false;
        }
        var maxCnt = oldValues.length - startIndex;
        if (cnt >= maxCnt) {
            cnt = maxCnt;
        }
        if (cnt === 0) {
            return false;
        }
        this.values = new Uint32Array(oldValues.length - cnt);
        this.values.set(oldValues.subarray(0, startIndex), 0);
        this.values.set(oldValues.subarray(startIndex + cnt), startIndex);
        this.prefixSum = new Uint32Array(this.values.length);
        if (startIndex - 1 < this.prefixSumValidIndex[0]) {
            this.prefixSumValidIndex[0] = startIndex - 1;
        }
        if (this.prefixSumValidIndex[0] >= 0) {
            this.prefixSum.set(oldPrefixSum.subarray(0, this.prefixSumValidIndex[0] + 1));
        }
        return true;
    };
    PrefixSumComputer.prototype.getTotalValue = function () {
        if (this.values.length === 0) {
            return 0;
        }
        return this._getAccumulatedValue(this.values.length - 1);
    };
    PrefixSumComputer.prototype.getAccumulatedValue = function (index) {
        if (index < 0) {
            return 0;
        }
        index = toUint32(index);
        return this._getAccumulatedValue(index);
    };
    PrefixSumComputer.prototype._getAccumulatedValue = function (index) {
        if (index <= this.prefixSumValidIndex[0]) {
            return this.prefixSum[index];
        }
        var startIndex = this.prefixSumValidIndex[0] + 1;
        if (startIndex === 0) {
            this.prefixSum[0] = this.values[0];
            startIndex++;
        }
        if (index >= this.values.length) {
            index = this.values.length - 1;
        }
        for (var i = startIndex; i <= index; i++) {
            this.prefixSum[i] = this.prefixSum[i - 1] + this.values[i];
        }
        this.prefixSumValidIndex[0] = Math.max(this.prefixSumValidIndex[0], index);
        return this.prefixSum[index];
    };
    PrefixSumComputer.prototype.getIndexOf = function (accumulatedValue) {
        accumulatedValue = Math.floor(accumulatedValue); //@perf
        // Compute all sums (to get a fully valid prefixSum)
        this.getTotalValue();
        var low = 0;
        var high = this.values.length - 1;
        var mid;
        var midStop;
        var midStart;
        while (low <= high) {
            mid = low + ((high - low) / 2) | 0;
            midStop = this.prefixSum[mid];
            midStart = midStop - this.values[mid];
            if (accumulatedValue < midStart) {
                high = mid - 1;
            }
            else if (accumulatedValue >= midStop) {
                low = mid + 1;
            }
            else {
                break;
            }
        }
        return new PrefixSumIndexOfResult(mid, accumulatedValue - midStart);
    };
    return PrefixSumComputer;
}());

var PrefixSumComputerWithCache = /** @class */ (function () {
    function PrefixSumComputerWithCache(values) {
        this._cacheAccumulatedValueStart = 0;
        this._cache = null;
        this._actual = new prefixSumComputer_PrefixSumComputer(values);
        this._bustCache();
    }
    PrefixSumComputerWithCache.prototype._bustCache = function () {
        this._cacheAccumulatedValueStart = 0;
        this._cache = null;
    };
    PrefixSumComputerWithCache.prototype.insertValues = function (insertIndex, insertValues) {
        if (this._actual.insertValues(insertIndex, insertValues)) {
            this._bustCache();
        }
    };
    PrefixSumComputerWithCache.prototype.changeValue = function (index, value) {
        if (this._actual.changeValue(index, value)) {
            this._bustCache();
        }
    };
    PrefixSumComputerWithCache.prototype.removeValues = function (startIndex, cnt) {
        if (this._actual.removeValues(startIndex, cnt)) {
            this._bustCache();
        }
    };
    PrefixSumComputerWithCache.prototype.getTotalValue = function () {
        return this._actual.getTotalValue();
    };
    PrefixSumComputerWithCache.prototype.getAccumulatedValue = function (index) {
        return this._actual.getAccumulatedValue(index);
    };
    PrefixSumComputerWithCache.prototype.getIndexOf = function (accumulatedValue) {
        accumulatedValue = Math.floor(accumulatedValue); //@perf
        if (this._cache !== null) {
            var cacheIndex = accumulatedValue - this._cacheAccumulatedValueStart;
            if (cacheIndex >= 0 && cacheIndex < this._cache.length) {
                // Cache hit!
                return this._cache[cacheIndex];
            }
        }
        // Cache miss!
        return this._actual.getIndexOf(accumulatedValue);
    };
    /**
     * Gives a hint that a lot of requests are about to come in for these accumulated values.
     */
    PrefixSumComputerWithCache.prototype.warmUpCache = function (accumulatedValueStart, accumulatedValueEnd) {
        var newCache = [];
        for (var accumulatedValue = accumulatedValueStart; accumulatedValue <= accumulatedValueEnd; accumulatedValue++) {
            newCache[accumulatedValue - accumulatedValueStart] = this.getIndexOf(accumulatedValue);
        }
        this._cache = newCache;
        this._cacheAccumulatedValueStart = accumulatedValueStart;
    };
    return PrefixSumComputerWithCache;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/model/mirrorTextModel.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/



var mirrorTextModel_MirrorTextModel = /** @class */ (function () {
    function MirrorTextModel(uri, lines, eol, versionId) {
        this._uri = uri;
        this._lines = lines;
        this._eol = eol;
        this._versionId = versionId;
    }
    MirrorTextModel.prototype.dispose = function () {
        this._lines.length = 0;
    };
    Object.defineProperty(MirrorTextModel.prototype, "version", {
        get: function () {
            return this._versionId;
        },
        enumerable: true,
        configurable: true
    });
    MirrorTextModel.prototype.getText = function () {
        return this._lines.join(this._eol);
    };
    MirrorTextModel.prototype.onEvents = function (e) {
        if (e.eol && e.eol !== this._eol) {
            this._eol = e.eol;
            this._lineStarts = null;
        }
        // Update my lines
        var changes = e.changes;
        for (var i = 0, len = changes.length; i < len; i++) {
            var change = changes[i];
            this._acceptDeleteRange(change.range);
            this._acceptInsertText(new Position(change.range.startLineNumber, change.range.startColumn), change.text);
        }
        this._versionId = e.versionId;
    };
    MirrorTextModel.prototype._ensureLineStarts = function () {
        if (!this._lineStarts) {
            var eolLength = this._eol.length;
            var linesLength = this._lines.length;
            var lineStartValues = new Uint32Array(linesLength);
            for (var i = 0; i < linesLength; i++) {
                lineStartValues[i] = this._lines[i].length + eolLength;
            }
            this._lineStarts = new prefixSumComputer_PrefixSumComputer(lineStartValues);
        }
    };
    /**
     * All changes to a line's text go through this method
     */
    MirrorTextModel.prototype._setLineText = function (lineIndex, newValue) {
        this._lines[lineIndex] = newValue;
        if (this._lineStarts) {
            // update prefix sum
            this._lineStarts.changeValue(lineIndex, this._lines[lineIndex].length + this._eol.length);
        }
    };
    MirrorTextModel.prototype._acceptDeleteRange = function (range) {
        if (range.startLineNumber === range.endLineNumber) {
            if (range.startColumn === range.endColumn) {
                // Nothing to delete
                return;
            }
            // Delete text on the affected line
            this._setLineText(range.startLineNumber - 1, this._lines[range.startLineNumber - 1].substring(0, range.startColumn - 1)
                + this._lines[range.startLineNumber - 1].substring(range.endColumn - 1));
            return;
        }
        // Take remaining text on last line and append it to remaining text on first line
        this._setLineText(range.startLineNumber - 1, this._lines[range.startLineNumber - 1].substring(0, range.startColumn - 1)
            + this._lines[range.endLineNumber - 1].substring(range.endColumn - 1));
        // Delete middle lines
        this._lines.splice(range.startLineNumber, range.endLineNumber - range.startLineNumber);
        if (this._lineStarts) {
            // update prefix sum
            this._lineStarts.removeValues(range.startLineNumber, range.endLineNumber - range.startLineNumber);
        }
    };
    MirrorTextModel.prototype._acceptInsertText = function (position, insertText) {
        if (insertText.length === 0) {
            // Nothing to insert
            return;
        }
        var insertLines = insertText.split(/\r\n|\r|\n/);
        if (insertLines.length === 1) {
            // Inserting text on one line
            this._setLineText(position.lineNumber - 1, this._lines[position.lineNumber - 1].substring(0, position.column - 1)
                + insertLines[0]
                + this._lines[position.lineNumber - 1].substring(position.column - 1));
            return;
        }
        // Append overflowing text from first line to the end of text to insert
        insertLines[insertLines.length - 1] += this._lines[position.lineNumber - 1].substring(position.column - 1);
        // Delete overflowing text from first line and insert text on first line
        this._setLineText(position.lineNumber - 1, this._lines[position.lineNumber - 1].substring(0, position.column - 1)
            + insertLines[0]);
        // Insert new lines & store lengths
        var newLengths = new Uint32Array(insertLines.length - 1);
        for (var i = 1; i < insertLines.length; i++) {
            this._lines.splice(position.lineNumber + i - 1, 0, insertLines[i]);
            newLengths[i - 1] = insertLines[i].length + this._eol.length;
        }
        if (this._lineStarts) {
            // update prefix sum
            this._lineStarts.insertValues(position.lineNumber, newLengths);
        }
    };
    return MirrorTextModel;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/core/characterClassifier.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


/**
 * A fast character classifier that uses a compact array for ASCII values.
 */
var characterClassifier_CharacterClassifier = /** @class */ (function () {
    function CharacterClassifier(_defaultValue) {
        var defaultValue = toUint8(_defaultValue);
        this._defaultValue = defaultValue;
        this._asciiMap = CharacterClassifier._createAsciiMap(defaultValue);
        this._map = new Map();
    }
    CharacterClassifier._createAsciiMap = function (defaultValue) {
        var asciiMap = new Uint8Array(256);
        for (var i = 0; i < 256; i++) {
            asciiMap[i] = defaultValue;
        }
        return asciiMap;
    };
    CharacterClassifier.prototype.set = function (charCode, _value) {
        var value = toUint8(_value);
        if (charCode >= 0 && charCode < 256) {
            this._asciiMap[charCode] = value;
        }
        else {
            this._map.set(charCode, value);
        }
    };
    CharacterClassifier.prototype.get = function (charCode) {
        if (charCode >= 0 && charCode < 256) {
            return this._asciiMap[charCode];
        }
        else {
            return (this._map.get(charCode) || this._defaultValue);
        }
    };
    return CharacterClassifier;
}());

var CharacterSet = /** @class */ (function () {
    function CharacterSet() {
        this._actual = new characterClassifier_CharacterClassifier(0 /* False */);
    }
    CharacterSet.prototype.add = function (charCode) {
        this._actual.set(charCode, 1 /* True */);
    };
    CharacterSet.prototype.has = function (charCode) {
        return (this._actual.get(charCode) === 1 /* True */);
    };
    return CharacterSet;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/modes/linkComputer.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/



var linkComputer_StateMachine = /** @class */ (function () {
    function StateMachine(edges) {
        var maxCharCode = 0;
        var maxState = 0 /* Invalid */;
        for (var i = 0, len = edges.length; i < len; i++) {
            var _a = edges[i], from = _a[0], chCode = _a[1], to = _a[2];
            if (chCode > maxCharCode) {
                maxCharCode = chCode;
            }
            if (from > maxState) {
                maxState = from;
            }
            if (to > maxState) {
                maxState = to;
            }
        }
        maxCharCode++;
        maxState++;
        var states = new Uint8Matrix(maxState, maxCharCode, 0 /* Invalid */);
        for (var i = 0, len = edges.length; i < len; i++) {
            var _b = edges[i], from = _b[0], chCode = _b[1], to = _b[2];
            states.set(from, chCode, to);
        }
        this._states = states;
        this._maxCharCode = maxCharCode;
    }
    StateMachine.prototype.nextState = function (currentState, chCode) {
        if (chCode < 0 || chCode >= this._maxCharCode) {
            return 0 /* Invalid */;
        }
        return this._states.get(currentState, chCode);
    };
    return StateMachine;
}());
// State machine for http:// or https:// or file://
var _stateMachine = null;
function getStateMachine() {
    if (_stateMachine === null) {
        _stateMachine = new linkComputer_StateMachine([
            [1 /* Start */, 104 /* h */, 2 /* H */],
            [1 /* Start */, 72 /* H */, 2 /* H */],
            [1 /* Start */, 102 /* f */, 6 /* F */],
            [1 /* Start */, 70 /* F */, 6 /* F */],
            [2 /* H */, 116 /* t */, 3 /* HT */],
            [2 /* H */, 84 /* T */, 3 /* HT */],
            [3 /* HT */, 116 /* t */, 4 /* HTT */],
            [3 /* HT */, 84 /* T */, 4 /* HTT */],
            [4 /* HTT */, 112 /* p */, 5 /* HTTP */],
            [4 /* HTT */, 80 /* P */, 5 /* HTTP */],
            [5 /* HTTP */, 115 /* s */, 9 /* BeforeColon */],
            [5 /* HTTP */, 83 /* S */, 9 /* BeforeColon */],
            [5 /* HTTP */, 58 /* Colon */, 10 /* AfterColon */],
            [6 /* F */, 105 /* i */, 7 /* FI */],
            [6 /* F */, 73 /* I */, 7 /* FI */],
            [7 /* FI */, 108 /* l */, 8 /* FIL */],
            [7 /* FI */, 76 /* L */, 8 /* FIL */],
            [8 /* FIL */, 101 /* e */, 9 /* BeforeColon */],
            [8 /* FIL */, 69 /* E */, 9 /* BeforeColon */],
            [9 /* BeforeColon */, 58 /* Colon */, 10 /* AfterColon */],
            [10 /* AfterColon */, 47 /* Slash */, 11 /* AlmostThere */],
            [11 /* AlmostThere */, 47 /* Slash */, 12 /* End */],
        ]);
    }
    return _stateMachine;
}
var _classifier = null;
function getClassifier() {
    if (_classifier === null) {
        _classifier = new characterClassifier_CharacterClassifier(0 /* None */);
        var FORCE_TERMINATION_CHARACTERS = ' \t<>\'\"、。｡､，．：；？！＠＃＄％＆＊‘“〈《「『【〔（［｛｢｣｝］）〕】』」》〉”’｀～…';
        for (var i = 0; i < FORCE_TERMINATION_CHARACTERS.length; i++) {
            _classifier.set(FORCE_TERMINATION_CHARACTERS.charCodeAt(i), 1 /* ForceTermination */);
        }
        var CANNOT_END_WITH_CHARACTERS = '.,;';
        for (var i = 0; i < CANNOT_END_WITH_CHARACTERS.length; i++) {
            _classifier.set(CANNOT_END_WITH_CHARACTERS.charCodeAt(i), 2 /* CannotEndIn */);
        }
    }
    return _classifier;
}
var LinkComputer = /** @class */ (function () {
    function LinkComputer() {
    }
    LinkComputer._createLink = function (classifier, line, lineNumber, linkBeginIndex, linkEndIndex) {
        // Do not allow to end link in certain characters...
        var lastIncludedCharIndex = linkEndIndex - 1;
        do {
            var chCode = line.charCodeAt(lastIncludedCharIndex);
            var chClass = classifier.get(chCode);
            if (chClass !== 2 /* CannotEndIn */) {
                break;
            }
            lastIncludedCharIndex--;
        } while (lastIncludedCharIndex > linkBeginIndex);
        // Handle links enclosed in parens, square brackets and curlys.
        if (linkBeginIndex > 0) {
            var charCodeBeforeLink = line.charCodeAt(linkBeginIndex - 1);
            var lastCharCodeInLink = line.charCodeAt(lastIncludedCharIndex);
            if ((charCodeBeforeLink === 40 /* OpenParen */ && lastCharCodeInLink === 41 /* CloseParen */)
                || (charCodeBeforeLink === 91 /* OpenSquareBracket */ && lastCharCodeInLink === 93 /* CloseSquareBracket */)
                || (charCodeBeforeLink === 123 /* OpenCurlyBrace */ && lastCharCodeInLink === 125 /* CloseCurlyBrace */)) {
                // Do not end in ) if ( is before the link start
                // Do not end in ] if [ is before the link start
                // Do not end in } if { is before the link start
                lastIncludedCharIndex--;
            }
        }
        return {
            range: {
                startLineNumber: lineNumber,
                startColumn: linkBeginIndex + 1,
                endLineNumber: lineNumber,
                endColumn: lastIncludedCharIndex + 2
            },
            url: line.substring(linkBeginIndex, lastIncludedCharIndex + 1)
        };
    };
    LinkComputer.computeLinks = function (model) {
        var stateMachine = getStateMachine();
        var classifier = getClassifier();
        var result = [];
        for (var i = 1, lineCount = model.getLineCount(); i <= lineCount; i++) {
            var line = model.getLineContent(i);
            var len = line.length;
            var j = 0;
            var linkBeginIndex = 0;
            var linkBeginChCode = 0;
            var state = 1 /* Start */;
            var hasOpenParens = false;
            var hasOpenSquareBracket = false;
            var hasOpenCurlyBracket = false;
            while (j < len) {
                var resetStateMachine = false;
                var chCode = line.charCodeAt(j);
                if (state === 13 /* Accept */) {
                    var chClass = void 0;
                    switch (chCode) {
                        case 40 /* OpenParen */:
                            hasOpenParens = true;
                            chClass = 0 /* None */;
                            break;
                        case 41 /* CloseParen */:
                            chClass = (hasOpenParens ? 0 /* None */ : 1 /* ForceTermination */);
                            break;
                        case 91 /* OpenSquareBracket */:
                            hasOpenSquareBracket = true;
                            chClass = 0 /* None */;
                            break;
                        case 93 /* CloseSquareBracket */:
                            chClass = (hasOpenSquareBracket ? 0 /* None */ : 1 /* ForceTermination */);
                            break;
                        case 123 /* OpenCurlyBrace */:
                            hasOpenCurlyBracket = true;
                            chClass = 0 /* None */;
                            break;
                        case 125 /* CloseCurlyBrace */:
                            chClass = (hasOpenCurlyBracket ? 0 /* None */ : 1 /* ForceTermination */);
                            break;
                        /* The following three rules make it that ' or " or ` are allowed inside links if the link began with a different one */
                        case 39 /* SingleQuote */:
                            chClass = (linkBeginChCode === 34 /* DoubleQuote */ || linkBeginChCode === 96 /* BackTick */) ? 0 /* None */ : 1 /* ForceTermination */;
                            break;
                        case 34 /* DoubleQuote */:
                            chClass = (linkBeginChCode === 39 /* SingleQuote */ || linkBeginChCode === 96 /* BackTick */) ? 0 /* None */ : 1 /* ForceTermination */;
                            break;
                        case 96 /* BackTick */:
                            chClass = (linkBeginChCode === 39 /* SingleQuote */ || linkBeginChCode === 34 /* DoubleQuote */) ? 0 /* None */ : 1 /* ForceTermination */;
                            break;
                        default:
                            chClass = classifier.get(chCode);
                    }
                    // Check if character terminates link
                    if (chClass === 1 /* ForceTermination */) {
                        result.push(LinkComputer._createLink(classifier, line, i, linkBeginIndex, j));
                        resetStateMachine = true;
                    }
                }
                else if (state === 12 /* End */) {
                    var chClass = classifier.get(chCode);
                    // Check if character terminates link
                    if (chClass === 1 /* ForceTermination */) {
                        resetStateMachine = true;
                    }
                    else {
                        state = 13 /* Accept */;
                    }
                }
                else {
                    state = stateMachine.nextState(state, chCode);
                    if (state === 0 /* Invalid */) {
                        resetStateMachine = true;
                    }
                }
                if (resetStateMachine) {
                    state = 1 /* Start */;
                    hasOpenParens = false;
                    hasOpenSquareBracket = false;
                    hasOpenCurlyBracket = false;
                    // Record where the link started
                    linkBeginIndex = j + 1;
                    linkBeginChCode = chCode;
                }
                j++;
            }
            if (state === 13 /* Accept */) {
                result.push(LinkComputer._createLink(classifier, line, i, linkBeginIndex, len));
            }
        }
        return result;
    };
    return LinkComputer;
}());
/**
 * Returns an array of all links contains in the provided
 * document. *Note* that this operation is computational
 * expensive and should not run in the UI thread.
 */
function computeLinks(model) {
    if (!model || typeof model.getLineCount !== 'function' || typeof model.getLineContent !== 'function') {
        // Unknown caller!
        return [];
    }
    return LinkComputer.computeLinks(model);
}

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/modes/supports/inplaceReplaceSupport.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var BasicInplaceReplace = /** @class */ (function () {
    function BasicInplaceReplace() {
        this._defaultValueSet = [
            ['true', 'false'],
            ['True', 'False'],
            ['Private', 'Public', 'Friend', 'ReadOnly', 'Partial', 'Protected', 'WriteOnly'],
            ['public', 'protected', 'private'],
        ];
    }
    BasicInplaceReplace.prototype.navigateValueSet = function (range1, text1, range2, text2, up) {
        if (range1 && text1) {
            var result = this.doNavigateValueSet(text1, up);
            if (result) {
                return {
                    range: range1,
                    value: result
                };
            }
        }
        if (range2 && text2) {
            var result = this.doNavigateValueSet(text2, up);
            if (result) {
                return {
                    range: range2,
                    value: result
                };
            }
        }
        return null;
    };
    BasicInplaceReplace.prototype.doNavigateValueSet = function (text, up) {
        var numberResult = this.numberReplace(text, up);
        if (numberResult !== null) {
            return numberResult;
        }
        return this.textReplace(text, up);
    };
    BasicInplaceReplace.prototype.numberReplace = function (value, up) {
        var precision = Math.pow(10, value.length - (value.lastIndexOf('.') + 1));
        var n1 = Number(value);
        var n2 = parseFloat(value);
        if (!isNaN(n1) && !isNaN(n2) && n1 === n2) {
            if (n1 === 0 && !up) {
                return null; // don't do negative
                //			} else if(n1 === 9 && up) {
                //				return null; // don't insert 10 into a number
            }
            else {
                n1 = Math.floor(n1 * precision);
                n1 += up ? precision : -precision;
                return String(n1 / precision);
            }
        }
        return null;
    };
    BasicInplaceReplace.prototype.textReplace = function (value, up) {
        return this.valueSetsReplace(this._defaultValueSet, value, up);
    };
    BasicInplaceReplace.prototype.valueSetsReplace = function (valueSets, value, up) {
        var result = null;
        for (var i = 0, len = valueSets.length; result === null && i < len; i++) {
            result = this.valueSetReplace(valueSets[i], value, up);
        }
        return result;
    };
    BasicInplaceReplace.prototype.valueSetReplace = function (valueSet, value, up) {
        var idx = valueSet.indexOf(value);
        if (idx >= 0) {
            idx += up ? +1 : -1;
            if (idx < 0) {
                idx = valueSet.length - 1;
            }
            else {
                idx %= valueSet.length;
            }
            return valueSet[idx];
        }
        return null;
    };
    BasicInplaceReplace.INSTANCE = new BasicInplaceReplace();
    return BasicInplaceReplace;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/model/wordHelper.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var USUAL_WORD_SEPARATORS = '`~!@#$%^&*()-=+[{]}\\|;:\'",.<>/?';
/**
 * Create a word definition regular expression based on default word separators.
 * Optionally provide allowed separators that should be included in words.
 *
 * The default would look like this:
 * /(-?\d*\.\d\w*)|([^\`\~\!\@\#\$\%\^\&\*\(\)\-\=\+\[\{\]\}\\\|\;\:\'\"\,\.\<\>\/\?\s]+)/g
 */
function createWordRegExp(allowInWords) {
    if (allowInWords === void 0) { allowInWords = ''; }
    var source = '(-?\\d*\\.\\d\\w*)|([^';
    for (var i = 0; i < USUAL_WORD_SEPARATORS.length; i++) {
        if (allowInWords.indexOf(USUAL_WORD_SEPARATORS[i]) >= 0) {
            continue;
        }
        source += '\\' + USUAL_WORD_SEPARATORS[i];
    }
    source += '\\s]+)';
    return new RegExp(source, 'g');
}
// catches numbers (including floating numbers) in the first group, and alphanum in the second
var DEFAULT_WORD_REGEXP = createWordRegExp();
function ensureValidWordDefinition(wordDefinition) {
    var result = DEFAULT_WORD_REGEXP;
    if (wordDefinition && (wordDefinition instanceof RegExp)) {
        if (!wordDefinition.global) {
            var flags = 'g';
            if (wordDefinition.ignoreCase) {
                flags += 'i';
            }
            if (wordDefinition.multiline) {
                flags += 'm';
            }
            result = new RegExp(wordDefinition.source, flags);
        }
        else {
            result = wordDefinition;
        }
    }
    result.lastIndex = 0;
    return result;
}
function getWordAtPosFast(column, wordDefinition, text, textOffset) {
    // find whitespace enclosed text around column and match from there
    var pos = column - 1 - textOffset;
    var start = text.lastIndexOf(' ', pos - 1) + 1;
    var end = text.indexOf(' ', pos);
    if (end === -1) {
        end = text.length;
    }
    wordDefinition.lastIndex = start;
    var match;
    while (match = wordDefinition.exec(text)) {
        if (match.index <= pos && wordDefinition.lastIndex >= pos) {
            return {
                word: match[0],
                startColumn: textOffset + 1 + match.index,
                endColumn: textOffset + 1 + wordDefinition.lastIndex
            };
        }
    }
    return null;
}
function getWordAtPosSlow(column, wordDefinition, text, textOffset) {
    // matches all words starting at the beginning
    // of the input until it finds a match that encloses
    // the desired column. slow but correct
    var pos = column - 1 - textOffset;
    wordDefinition.lastIndex = 0;
    var match;
    while (match = wordDefinition.exec(text)) {
        if (match.index > pos) {
            // |nW -> matched only after the pos
            return null;
        }
        else if (wordDefinition.lastIndex >= pos) {
            // W|W -> match encloses pos
            return {
                word: match[0],
                startColumn: textOffset + 1 + match.index,
                endColumn: textOffset + 1 + wordDefinition.lastIndex
            };
        }
    }
    return null;
}
function getWordAtText(column, wordDefinition, text, textOffset) {
    // if `words` can contain whitespace character we have to use the slow variant
    // otherwise we use the fast variant of finding a word
    wordDefinition.lastIndex = 0;
    var match = wordDefinition.exec(text);
    if (!match) {
        return null;
    }
    // todo@joh the `match` could already be the (first) word
    var ret = match[0].indexOf(' ') >= 0
        // did match a word which contains a space character -> use slow word find
        ? getWordAtPosSlow(column, wordDefinition, text, textOffset)
        // sane word definition -> use fast word find
        : getWordAtPosFast(column, wordDefinition, text, textOffset);
    // both (getWordAtPosFast and getWordAtPosSlow) leave the wordDefinition-RegExp
    // in an undefined state and to not confuse other users of the wordDefinition
    // we reset the lastIndex
    wordDefinition.lastIndex = 0;
    return ret;
}

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/functional.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

function once(fn) {
    var _this = this;
    var didCall = false;
    var result;
    return function () {
        if (didCall) {
            return result;
        }
        didCall = true;
        result = fn.apply(_this, arguments);
        return result;
    };
}

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/lifecycle.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


var lifecycle_empty = Object.freeze({
    dispose: function () { }
});
function isDisposable(thing) {
    return typeof thing.dispose === 'function'
        && thing.dispose.length === 0;
}
function lifecycle_dispose(first) {
    var rest = [];
    for (var _i = 1; _i < arguments.length; _i++) {
        rest[_i - 1] = arguments[_i];
    }
    if (Array.isArray(first)) {
        first.forEach(function (d) { return d && d.dispose(); });
        return [];
    }
    else if (rest.length === 0) {
        if (first) {
            first.dispose();
            return first;
        }
        return undefined;
    }
    else {
        lifecycle_dispose(first);
        lifecycle_dispose(rest);
        return [];
    }
}
function combinedDisposable(disposables) {
    return { dispose: function () { return lifecycle_dispose(disposables); } };
}
function toDisposable() {
    var fns = [];
    for (var _i = 0; _i < arguments.length; _i++) {
        fns[_i] = arguments[_i];
    }
    return {
        dispose: function () {
            for (var _i = 0, fns_1 = fns; _i < fns_1.length; _i++) {
                var fn = fns_1[_i];
                fn();
            }
        }
    };
}
var Disposable = /** @class */ (function () {
    function Disposable() {
        this._toDispose = [];
    }
    Disposable.prototype.dispose = function () {
        this._toDispose = lifecycle_dispose(this._toDispose);
    };
    Disposable.prototype._register = function (t) {
        this._toDispose.push(t);
        return t;
    };
    return Disposable;
}());

var lifecycle_ReferenceCollection = /** @class */ (function () {
    function ReferenceCollection() {
        this.references = Object.create(null);
    }
    ReferenceCollection.prototype.acquire = function (key) {
        var _this = this;
        var reference = this.references[key];
        if (!reference) {
            reference = this.references[key] = { counter: 0, object: this.createReferencedObject(key) };
        }
        var object = reference.object;
        var dispose = once(function () {
            if (--reference.counter === 0) {
                _this.destroyReferencedObject(reference.object);
                delete _this.references[key];
            }
        });
        reference.counter++;
        return { object: object, dispose: dispose };
    };
    return ReferenceCollection;
}());

var ImmortalReference = /** @class */ (function () {
    function ImmortalReference(object) {
        this.object = object;
    }
    ImmortalReference.prototype.dispose = function () { };
    return ImmortalReference;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/errors.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


// ------ BEGIN Hook up error listeners to winjs promises
var outstandingPromiseErrors = {};
function promiseErrorHandler(e) {
    //
    // e.detail looks like: { exception, error, promise, handler, id, parent }
    //
    var details = e.detail;
    var id = details.id;
    // If the error has a parent promise then this is not the origination of the
    //  error so we check if it has a handler, and if so we mark that the error
    //  was handled by removing it from outstandingPromiseErrors
    //
    if (details.parent) {
        if (details.handler && outstandingPromiseErrors) {
            delete outstandingPromiseErrors[id];
        }
        return;
    }
    // Indicate that this error was originated and needs to be handled
    outstandingPromiseErrors[id] = details;
    // The first time the queue fills up this iteration, schedule a timeout to
    // check if any errors are still unhandled.
    if (Object.keys(outstandingPromiseErrors).length === 1) {
        setTimeout(function () {
            var errors = outstandingPromiseErrors;
            outstandingPromiseErrors = {};
            Object.keys(errors).forEach(function (errorId) {
                var error = errors[errorId];
                if (error.exception) {
                    onUnexpectedError(error.exception);
                }
                else if (error.error) {
                    onUnexpectedError(error.error);
                }
                console.log('WARNING: Promise with no error callback:' + error.id);
                console.log(error);
                if (error.exception) {
                    console.log(error.exception.stack);
                }
            });
        }, 0);
    }
}
winjs_base["b" /* TPromise */].addEventListener('error', promiseErrorHandler);
// Avoid circular dependency on EventEmitter by implementing a subset of the interface.
var ErrorHandler = /** @class */ (function () {
    function ErrorHandler() {
        this.listeners = [];
        this.unexpectedErrorHandler = function (e) {
            setTimeout(function () {
                if (e.stack) {
                    throw new Error(e.message + '\n\n' + e.stack);
                }
                throw e;
            }, 0);
        };
    }
    ErrorHandler.prototype.addListener = function (listener) {
        var _this = this;
        this.listeners.push(listener);
        return function () {
            _this._removeListener(listener);
        };
    };
    ErrorHandler.prototype.emit = function (e) {
        this.listeners.forEach(function (listener) {
            listener(e);
        });
    };
    ErrorHandler.prototype._removeListener = function (listener) {
        this.listeners.splice(this.listeners.indexOf(listener), 1);
    };
    ErrorHandler.prototype.setUnexpectedErrorHandler = function (newUnexpectedErrorHandler) {
        this.unexpectedErrorHandler = newUnexpectedErrorHandler;
    };
    ErrorHandler.prototype.getUnexpectedErrorHandler = function () {
        return this.unexpectedErrorHandler;
    };
    ErrorHandler.prototype.onUnexpectedError = function (e) {
        this.unexpectedErrorHandler(e);
        this.emit(e);
    };
    // For external errors, we don't want the listeners to be called
    ErrorHandler.prototype.onUnexpectedExternalError = function (e) {
        this.unexpectedErrorHandler(e);
    };
    return ErrorHandler;
}());

var errorHandler = new ErrorHandler();
function setUnexpectedErrorHandler(newUnexpectedErrorHandler) {
    errorHandler.setUnexpectedErrorHandler(newUnexpectedErrorHandler);
}
function onUnexpectedError(e) {
    // ignore errors from cancelled promises
    if (!isPromiseCanceledError(e)) {
        errorHandler.onUnexpectedError(e);
    }
    return undefined;
}
function onUnexpectedExternalError(e) {
    // ignore errors from cancelled promises
    if (!isPromiseCanceledError(e)) {
        errorHandler.onUnexpectedExternalError(e);
    }
    return undefined;
}
function transformErrorForSerialization(error) {
    if (error instanceof Error) {
        var name_1 = error.name, message = error.message;
        var stack = error.stacktrace || error.stack;
        return {
            $isError: true,
            name: name_1,
            message: message,
            stack: stack
        };
    }
    // return as is
    return error;
}
var canceledName = 'Canceled';
/**
 * Checks if the given error is a promise in canceled state
 */
function isPromiseCanceledError(error) {
    return error instanceof Error && error.name === canceledName && error.message === canceledName;
}
/**
 * Returns an error that signals cancellation.
 */
function canceled() {
    var error = new Error(canceledName);
    error.name = error.message;
    return error;
}
function illegalArgument(name) {
    if (name) {
        return new Error("Illegal argument: " + name);
    }
    else {
        return new Error('Illegal argument');
    }
}
function illegalState(name) {
    if (name) {
        return new Error("Illegal state: " + name);
    }
    else {
        return new Error('Illegal state');
    }
}
function readonly(name) {
    return name
        ? new Error("readonly property '" + name + " cannot be changed'")
        : new Error('readonly property cannot be changed');
}
function disposed(what) {
    var result = new Error(what + " has been disposed");
    result.name = 'DISPOSED';
    return result;
}
function isErrorWithActions(obj) {
    return obj instanceof Error && Array.isArray(obj.actions);
}
function create(message, options) {
    if (options === void 0) { options = Object.create(null); }
    var result = new Error(message);
    if (options.actions) {
        result.actions = options.actions;
    }
    return result;
}
function getErrorMessage(err) {
    if (!err) {
        return 'Error';
    }
    if (err.message) {
        return err.message;
    }
    if (err.stack) {
        return err.stack.split('\n')[0];
    }
    return String(err);
}

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/linkedList.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var Node = /** @class */ (function () {
    function Node(element) {
        this.element = element;
    }
    return Node;
}());
var LinkedList = /** @class */ (function () {
    function LinkedList() {
    }
    LinkedList.prototype.isEmpty = function () {
        return !this._first;
    };
    LinkedList.prototype.clear = function () {
        this._first = undefined;
        this._last = undefined;
    };
    LinkedList.prototype.unshift = function (element) {
        return this.insert(element, false);
    };
    LinkedList.prototype.push = function (element) {
        return this.insert(element, true);
    };
    LinkedList.prototype.insert = function (element, atTheEnd) {
        var _this = this;
        var newNode = new Node(element);
        if (!this._first) {
            this._first = newNode;
            this._last = newNode;
        }
        else if (atTheEnd) {
            // push
            var oldLast = this._last;
            this._last = newNode;
            newNode.prev = oldLast;
            oldLast.next = newNode;
        }
        else {
            // unshift
            var oldFirst = this._first;
            this._first = newNode;
            newNode.next = oldFirst;
            oldFirst.prev = newNode;
        }
        return function () {
            for (var candidate = _this._first; candidate instanceof Node; candidate = candidate.next) {
                if (candidate !== newNode) {
                    continue;
                }
                if (candidate.prev && candidate.next) {
                    // middle
                    var anchor = candidate.prev;
                    anchor.next = candidate.next;
                    candidate.next.prev = anchor;
                }
                else if (!candidate.prev && !candidate.next) {
                    // only node
                    _this._first = undefined;
                    _this._last = undefined;
                }
                else if (!candidate.next) {
                    // last
                    _this._last = _this._last.prev;
                    _this._last.next = undefined;
                }
                else if (!candidate.prev) {
                    // first
                    _this._first = _this._first.next;
                    _this._first.prev = undefined;
                }
                // done
                break;
            }
        };
    };
    LinkedList.prototype.iterator = function () {
        var element = {
            done: undefined,
            value: undefined,
        };
        var node = this._first;
        return {
            next: function () {
                if (!node) {
                    element.done = true;
                    element.value = undefined;
                }
                else {
                    element.done = false;
                    element.value = node.element;
                    node = node.next;
                }
                return element;
            }
        };
    };
    LinkedList.prototype.toArray = function () {
        var result = [];
        for (var node = this._first; node instanceof Node; node = node.next) {
            result.push(node.element);
        }
        return result;
    };
    return LinkedList;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/event.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/






var Event;
(function (Event) {
    var _disposable = { dispose: function () { } };
    Event.None = function () { return _disposable; };
})(Event || (Event = {}));
/**
 * The Emitter can be used to expose an Event to the public
 * to fire it from the insides.
 * Sample:
    class Document {

        private _onDidChange = new Emitter<(value:string)=>any>();

        public onDidChange = this._onDidChange.event;

        // getter-style
        // get onDidChange(): Event<(value:string)=>any> {
        // 	return this._onDidChange.event;
        // }

        private _doIt() {
            //...
            this._onDidChange.fire(value);
        }
    }
 */
var event_Emitter = /** @class */ (function () {
    function Emitter(_options) {
        this._options = _options;
    }
    Object.defineProperty(Emitter.prototype, "event", {
        /**
         * For the public to allow to subscribe
         * to events from this Emitter
         */
        get: function () {
            var _this = this;
            if (!this._event) {
                this._event = function (listener, thisArgs, disposables) {
                    if (!_this._listeners) {
                        _this._listeners = new LinkedList();
                    }
                    var firstListener = _this._listeners.isEmpty();
                    if (firstListener && _this._options && _this._options.onFirstListenerAdd) {
                        _this._options.onFirstListenerAdd(_this);
                    }
                    var remove = _this._listeners.push(!thisArgs ? listener : [listener, thisArgs]);
                    if (firstListener && _this._options && _this._options.onFirstListenerDidAdd) {
                        _this._options.onFirstListenerDidAdd(_this);
                    }
                    if (_this._options && _this._options.onListenerDidAdd) {
                        _this._options.onListenerDidAdd(_this, listener, thisArgs);
                    }
                    var result;
                    result = {
                        dispose: function () {
                            result.dispose = Emitter._noop;
                            if (!_this._disposed) {
                                remove();
                                if (_this._options && _this._options.onLastListenerRemove && _this._listeners.isEmpty()) {
                                    _this._options.onLastListenerRemove(_this);
                                }
                            }
                        }
                    };
                    if (Array.isArray(disposables)) {
                        disposables.push(result);
                    }
                    return result;
                };
            }
            return this._event;
        },
        enumerable: true,
        configurable: true
    });
    /**
     * To be kept private to fire an event to
     * subscribers
     */
    Emitter.prototype.fire = function (event) {
        if (this._listeners) {
            // put all [listener,event]-pairs into delivery queue
            // then emit all event. an inner/nested event might be
            // the driver of this
            if (!this._deliveryQueue) {
                this._deliveryQueue = [];
            }
            for (var iter = this._listeners.iterator(), e = iter.next(); !e.done; e = iter.next()) {
                this._deliveryQueue.push([e.value, event]);
            }
            while (this._deliveryQueue.length > 0) {
                var _a = this._deliveryQueue.shift(), listener = _a[0], event_1 = _a[1];
                try {
                    if (typeof listener === 'function') {
                        listener.call(undefined, event_1);
                    }
                    else {
                        listener[0].call(listener[1], event_1);
                    }
                }
                catch (e) {
                    onUnexpectedError(e);
                }
            }
        }
    };
    Emitter.prototype.dispose = function () {
        if (this._listeners) {
            this._listeners = undefined;
        }
        if (this._deliveryQueue) {
            this._deliveryQueue.length = 0;
        }
        this._disposed = true;
    };
    Emitter._noop = function () { };
    return Emitter;
}());

var event_EventMultiplexer = /** @class */ (function () {
    function EventMultiplexer() {
        var _this = this;
        this.hasListeners = false;
        this.events = [];
        this.emitter = new event_Emitter({
            onFirstListenerAdd: function () { return _this.onFirstListenerAdd(); },
            onLastListenerRemove: function () { return _this.onLastListenerRemove(); }
        });
    }
    Object.defineProperty(EventMultiplexer.prototype, "event", {
        get: function () {
            return this.emitter.event;
        },
        enumerable: true,
        configurable: true
    });
    EventMultiplexer.prototype.add = function (event) {
        var _this = this;
        var e = { event: event, listener: null };
        this.events.push(e);
        if (this.hasListeners) {
            this.hook(e);
        }
        var dispose = function () {
            if (_this.hasListeners) {
                _this.unhook(e);
            }
            var idx = _this.events.indexOf(e);
            _this.events.splice(idx, 1);
        };
        return toDisposable(once(dispose));
    };
    EventMultiplexer.prototype.onFirstListenerAdd = function () {
        var _this = this;
        this.hasListeners = true;
        this.events.forEach(function (e) { return _this.hook(e); });
    };
    EventMultiplexer.prototype.onLastListenerRemove = function () {
        var _this = this;
        this.hasListeners = false;
        this.events.forEach(function (e) { return _this.unhook(e); });
    };
    EventMultiplexer.prototype.hook = function (e) {
        var _this = this;
        e.listener = e.event(function (r) { return _this.emitter.fire(r); });
    };
    EventMultiplexer.prototype.unhook = function (e) {
        e.listener.dispose();
        e.listener = null;
    };
    EventMultiplexer.prototype.dispose = function () {
        this.emitter.dispose();
    };
    return EventMultiplexer;
}());

function fromCallback(fn) {
    var listener;
    var emitter = new event_Emitter({
        onFirstListenerAdd: function () { return listener = fn(function (e) { return emitter.fire(e); }); },
        onLastListenerRemove: function () { return listener.dispose(); }
    });
    return emitter.event;
}
function fromPromise(promise) {
    var emitter = new event_Emitter();
    var shouldEmit = false;
    promise
        .then(null, function () { return null; })
        .then(function () {
        if (!shouldEmit) {
            setTimeout(function () { return emitter.fire(); }, 0);
        }
        else {
            emitter.fire();
        }
    });
    shouldEmit = true;
    return emitter.event;
}
function toPromise(event) {
    return new winjs_base["b" /* TPromise */](function (complete) {
        var sub = event(function (e) {
            sub.dispose();
            complete(e);
        });
    });
}
function event_once(event) {
    return function (listener, thisArgs, disposables) {
        if (thisArgs === void 0) { thisArgs = null; }
        var result = event(function (e) {
            result.dispose();
            return listener.call(thisArgs, e);
        }, null, disposables);
        return result;
    };
}
function anyEvent() {
    var events = [];
    for (var _i = 0; _i < arguments.length; _i++) {
        events[_i] = arguments[_i];
    }
    return function (listener, thisArgs, disposables) {
        if (thisArgs === void 0) { thisArgs = null; }
        return combinedDisposable(events.map(function (event) { return event(function (e) { return listener.call(thisArgs, e); }, null, disposables); }));
    };
}
function debounceEvent(event, merger, delay, leading) {
    if (delay === void 0) { delay = 100; }
    if (leading === void 0) { leading = false; }
    var subscription;
    var output = undefined;
    var handle = undefined;
    var numDebouncedCalls = 0;
    var emitter = new event_Emitter({
        onFirstListenerAdd: function () {
            subscription = event(function (cur) {
                numDebouncedCalls++;
                output = merger(output, cur);
                if (leading && !handle) {
                    emitter.fire(output);
                }
                clearTimeout(handle);
                handle = setTimeout(function () {
                    var _output = output;
                    output = undefined;
                    handle = undefined;
                    if (!leading || numDebouncedCalls > 1) {
                        emitter.fire(_output);
                    }
                    numDebouncedCalls = 0;
                }, delay);
            });
        },
        onLastListenerRemove: function () {
            subscription.dispose();
        }
    });
    return emitter.event;
}
/**
 * The EventDelayer is useful in situations in which you want
 * to delay firing your events during some code.
 * You can wrap that code and be sure that the event will not
 * be fired during that wrap.
 *
 * ```
 * const emitter: Emitter;
 * const delayer = new EventDelayer();
 * const delayedEvent = delayer.wrapEvent(emitter.event);
 *
 * delayedEvent(console.log);
 *
 * delayer.bufferEvents(() => {
 *   emitter.fire(); // event will not be fired yet
 * });
 *
 * // event will only be fired at this point
 * ```
 */
var EventBufferer = /** @class */ (function () {
    function EventBufferer() {
        this.buffers = [];
    }
    EventBufferer.prototype.wrapEvent = function (event) {
        var _this = this;
        return function (listener, thisArgs, disposables) {
            return event(function (i) {
                var buffer = _this.buffers[_this.buffers.length - 1];
                if (buffer) {
                    buffer.push(function () { return listener.call(thisArgs, i); });
                }
                else {
                    listener.call(thisArgs, i);
                }
            }, void 0, disposables);
        };
    };
    EventBufferer.prototype.bufferEvents = function (fn) {
        var buffer = [];
        this.buffers.push(buffer);
        fn();
        this.buffers.pop();
        buffer.forEach(function (flush) { return flush(); });
    };
    return EventBufferer;
}());

function mapEvent(event, map) {
    return function (listener, thisArgs, disposables) {
        if (thisArgs === void 0) { thisArgs = null; }
        return event(function (i) { return listener.call(thisArgs, map(i)); }, null, disposables);
    };
}
function forEach(event, each) {
    return function (listener, thisArgs, disposables) {
        if (thisArgs === void 0) { thisArgs = null; }
        return event(function (i) { each(i); listener.call(thisArgs, i); }, null, disposables);
    };
}
function filterEvent(event, filter) {
    return function (listener, thisArgs, disposables) {
        if (thisArgs === void 0) { thisArgs = null; }
        return event(function (e) { return filter(e) && listener.call(thisArgs, e); }, null, disposables);
    };
}
var ChainableEvent = /** @class */ (function () {
    function ChainableEvent(_event) {
        this._event = _event;
    }
    Object.defineProperty(ChainableEvent.prototype, "event", {
        get: function () { return this._event; },
        enumerable: true,
        configurable: true
    });
    ChainableEvent.prototype.map = function (fn) {
        return new ChainableEvent(mapEvent(this._event, fn));
    };
    ChainableEvent.prototype.forEach = function (fn) {
        return new ChainableEvent(forEach(this._event, fn));
    };
    ChainableEvent.prototype.filter = function (fn) {
        return new ChainableEvent(filterEvent(this._event, fn));
    };
    ChainableEvent.prototype.latch = function () {
        return new ChainableEvent(latch(this._event));
    };
    ChainableEvent.prototype.on = function (listener, thisArgs, disposables) {
        return this._event(listener, thisArgs, disposables);
    };
    return ChainableEvent;
}());
function chain(event) {
    return new ChainableEvent(event);
}
function stopwatch(event) {
    var start = new Date().getTime();
    return mapEvent(event_once(event), function (_) { return new Date().getTime() - start; });
}
/**
 * Buffers the provided event until a first listener comes
 * along, at which point fire all the events at once and
 * pipe the event from then on.
 *
 * ```typescript
 * const emitter = new Emitter<number>();
 * const event = emitter.event;
 * const bufferedEvent = buffer(event);
 *
 * emitter.fire(1);
 * emitter.fire(2);
 * emitter.fire(3);
 * // nothing...
 *
 * const listener = bufferedEvent(num => console.log(num));
 * // 1, 2, 3
 *
 * emitter.fire(4);
 * // 4
 * ```
 */
function buffer(event, nextTick, buffer) {
    if (nextTick === void 0) { nextTick = false; }
    if (buffer === void 0) { buffer = []; }
    buffer = buffer.slice();
    var listener = event(function (e) {
        if (buffer) {
            buffer.push(e);
        }
        else {
            emitter.fire(e);
        }
    });
    var flush = function () {
        buffer.forEach(function (e) { return emitter.fire(e); });
        buffer = null;
    };
    var emitter = new event_Emitter({
        onFirstListenerAdd: function () {
            if (!listener) {
                listener = event(function (e) { return emitter.fire(e); });
            }
        },
        onFirstListenerDidAdd: function () {
            if (buffer) {
                if (nextTick) {
                    setTimeout(flush);
                }
                else {
                    flush();
                }
            }
        },
        onLastListenerRemove: function () {
            listener.dispose();
            listener = null;
        }
    });
    return emitter.event;
}
/**
 * Similar to `buffer` but it buffers indefinitely and repeats
 * the buffered events to every new listener.
 */
function echo(event, nextTick, buffer) {
    if (nextTick === void 0) { nextTick = false; }
    if (buffer === void 0) { buffer = []; }
    buffer = buffer.slice();
    event(function (e) {
        buffer.push(e);
        emitter.fire(e);
    });
    var flush = function (listener, thisArgs) { return buffer.forEach(function (e) { return listener.call(thisArgs, e); }); };
    var emitter = new event_Emitter({
        onListenerDidAdd: function (emitter, listener, thisArgs) {
            if (nextTick) {
                setTimeout(function () { return flush(listener, thisArgs); });
            }
            else {
                flush(listener, thisArgs);
            }
        }
    });
    return emitter.event;
}
var event_Relay = /** @class */ (function () {
    function Relay() {
        this.emitter = new event_Emitter();
        this.event = this.emitter.event;
        this.disposable = lifecycle_empty;
    }
    Object.defineProperty(Relay.prototype, "input", {
        set: function (event) {
            this.disposable.dispose();
            this.disposable = event(this.emitter.fire, this.emitter);
        },
        enumerable: true,
        configurable: true
    });
    Relay.prototype.dispose = function () {
        this.disposable.dispose();
        this.emitter.dispose();
    };
    return Relay;
}());

function fromNodeEventEmitter(emitter, eventName, map) {
    if (map === void 0) { map = function (id) { return id; }; }
    var fn = function () {
        var args = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            args[_i] = arguments[_i];
        }
        return result.fire(map.apply(void 0, args));
    };
    var onFirstListenerAdd = function () { return emitter.on(eventName, fn); };
    var onLastListenerRemove = function () { return emitter.removeListener(eventName, fn); };
    var result = new event_Emitter({ onFirstListenerAdd: onFirstListenerAdd, onLastListenerRemove: onLastListenerRemove });
    return result.event;
}
function latch(event) {
    var firstCall = true;
    var cache;
    return filterEvent(event, function (value) {
        var shouldEmit = firstCall || value !== cache;
        firstCall = false;
        cache = value;
        return shouldEmit;
    });
}

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/keyCodes.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var KeyCodeStrMap = /** @class */ (function () {
    function KeyCodeStrMap() {
        this._keyCodeToStr = [];
        this._strToKeyCode = Object.create(null);
    }
    KeyCodeStrMap.prototype.define = function (keyCode, str) {
        this._keyCodeToStr[keyCode] = str;
        this._strToKeyCode[str.toLowerCase()] = keyCode;
    };
    KeyCodeStrMap.prototype.keyCodeToStr = function (keyCode) {
        return this._keyCodeToStr[keyCode];
    };
    KeyCodeStrMap.prototype.strToKeyCode = function (str) {
        return this._strToKeyCode[str.toLowerCase()] || 0 /* Unknown */;
    };
    return KeyCodeStrMap;
}());
var uiMap = new KeyCodeStrMap();
var userSettingsUSMap = new KeyCodeStrMap();
var userSettingsGeneralMap = new KeyCodeStrMap();
(function () {
    function define(keyCode, uiLabel, usUserSettingsLabel, generalUserSettingsLabel) {
        if (usUserSettingsLabel === void 0) { usUserSettingsLabel = uiLabel; }
        if (generalUserSettingsLabel === void 0) { generalUserSettingsLabel = usUserSettingsLabel; }
        uiMap.define(keyCode, uiLabel);
        userSettingsUSMap.define(keyCode, usUserSettingsLabel);
        userSettingsGeneralMap.define(keyCode, generalUserSettingsLabel);
    }
    define(0 /* Unknown */, 'unknown');
    define(1 /* Backspace */, 'Backspace');
    define(2 /* Tab */, 'Tab');
    define(3 /* Enter */, 'Enter');
    define(4 /* Shift */, 'Shift');
    define(5 /* Ctrl */, 'Ctrl');
    define(6 /* Alt */, 'Alt');
    define(7 /* PauseBreak */, 'PauseBreak');
    define(8 /* CapsLock */, 'CapsLock');
    define(9 /* Escape */, 'Escape');
    define(10 /* Space */, 'Space');
    define(11 /* PageUp */, 'PageUp');
    define(12 /* PageDown */, 'PageDown');
    define(13 /* End */, 'End');
    define(14 /* Home */, 'Home');
    define(15 /* LeftArrow */, 'LeftArrow', 'Left');
    define(16 /* UpArrow */, 'UpArrow', 'Up');
    define(17 /* RightArrow */, 'RightArrow', 'Right');
    define(18 /* DownArrow */, 'DownArrow', 'Down');
    define(19 /* Insert */, 'Insert');
    define(20 /* Delete */, 'Delete');
    define(21 /* KEY_0 */, '0');
    define(22 /* KEY_1 */, '1');
    define(23 /* KEY_2 */, '2');
    define(24 /* KEY_3 */, '3');
    define(25 /* KEY_4 */, '4');
    define(26 /* KEY_5 */, '5');
    define(27 /* KEY_6 */, '6');
    define(28 /* KEY_7 */, '7');
    define(29 /* KEY_8 */, '8');
    define(30 /* KEY_9 */, '9');
    define(31 /* KEY_A */, 'A');
    define(32 /* KEY_B */, 'B');
    define(33 /* KEY_C */, 'C');
    define(34 /* KEY_D */, 'D');
    define(35 /* KEY_E */, 'E');
    define(36 /* KEY_F */, 'F');
    define(37 /* KEY_G */, 'G');
    define(38 /* KEY_H */, 'H');
    define(39 /* KEY_I */, 'I');
    define(40 /* KEY_J */, 'J');
    define(41 /* KEY_K */, 'K');
    define(42 /* KEY_L */, 'L');
    define(43 /* KEY_M */, 'M');
    define(44 /* KEY_N */, 'N');
    define(45 /* KEY_O */, 'O');
    define(46 /* KEY_P */, 'P');
    define(47 /* KEY_Q */, 'Q');
    define(48 /* KEY_R */, 'R');
    define(49 /* KEY_S */, 'S');
    define(50 /* KEY_T */, 'T');
    define(51 /* KEY_U */, 'U');
    define(52 /* KEY_V */, 'V');
    define(53 /* KEY_W */, 'W');
    define(54 /* KEY_X */, 'X');
    define(55 /* KEY_Y */, 'Y');
    define(56 /* KEY_Z */, 'Z');
    define(57 /* Meta */, 'Meta');
    define(58 /* ContextMenu */, 'ContextMenu');
    define(59 /* F1 */, 'F1');
    define(60 /* F2 */, 'F2');
    define(61 /* F3 */, 'F3');
    define(62 /* F4 */, 'F4');
    define(63 /* F5 */, 'F5');
    define(64 /* F6 */, 'F6');
    define(65 /* F7 */, 'F7');
    define(66 /* F8 */, 'F8');
    define(67 /* F9 */, 'F9');
    define(68 /* F10 */, 'F10');
    define(69 /* F11 */, 'F11');
    define(70 /* F12 */, 'F12');
    define(71 /* F13 */, 'F13');
    define(72 /* F14 */, 'F14');
    define(73 /* F15 */, 'F15');
    define(74 /* F16 */, 'F16');
    define(75 /* F17 */, 'F17');
    define(76 /* F18 */, 'F18');
    define(77 /* F19 */, 'F19');
    define(78 /* NumLock */, 'NumLock');
    define(79 /* ScrollLock */, 'ScrollLock');
    define(80 /* US_SEMICOLON */, ';', ';', 'OEM_1');
    define(81 /* US_EQUAL */, '=', '=', 'OEM_PLUS');
    define(82 /* US_COMMA */, ',', ',', 'OEM_COMMA');
    define(83 /* US_MINUS */, '-', '-', 'OEM_MINUS');
    define(84 /* US_DOT */, '.', '.', 'OEM_PERIOD');
    define(85 /* US_SLASH */, '/', '/', 'OEM_2');
    define(86 /* US_BACKTICK */, '`', '`', 'OEM_3');
    define(110 /* ABNT_C1 */, 'ABNT_C1');
    define(111 /* ABNT_C2 */, 'ABNT_C2');
    define(87 /* US_OPEN_SQUARE_BRACKET */, '[', '[', 'OEM_4');
    define(88 /* US_BACKSLASH */, '\\', '\\', 'OEM_5');
    define(89 /* US_CLOSE_SQUARE_BRACKET */, ']', ']', 'OEM_6');
    define(90 /* US_QUOTE */, '\'', '\'', 'OEM_7');
    define(91 /* OEM_8 */, 'OEM_8');
    define(92 /* OEM_102 */, 'OEM_102');
    define(93 /* NUMPAD_0 */, 'NumPad0');
    define(94 /* NUMPAD_1 */, 'NumPad1');
    define(95 /* NUMPAD_2 */, 'NumPad2');
    define(96 /* NUMPAD_3 */, 'NumPad3');
    define(97 /* NUMPAD_4 */, 'NumPad4');
    define(98 /* NUMPAD_5 */, 'NumPad5');
    define(99 /* NUMPAD_6 */, 'NumPad6');
    define(100 /* NUMPAD_7 */, 'NumPad7');
    define(101 /* NUMPAD_8 */, 'NumPad8');
    define(102 /* NUMPAD_9 */, 'NumPad9');
    define(103 /* NUMPAD_MULTIPLY */, 'NumPad_Multiply');
    define(104 /* NUMPAD_ADD */, 'NumPad_Add');
    define(105 /* NUMPAD_SEPARATOR */, 'NumPad_Separator');
    define(106 /* NUMPAD_SUBTRACT */, 'NumPad_Subtract');
    define(107 /* NUMPAD_DECIMAL */, 'NumPad_Decimal');
    define(108 /* NUMPAD_DIVIDE */, 'NumPad_Divide');
})();
var KeyCodeUtils;
(function (KeyCodeUtils) {
    function toString(keyCode) {
        return uiMap.keyCodeToStr(keyCode);
    }
    KeyCodeUtils.toString = toString;
    function fromString(key) {
        return uiMap.strToKeyCode(key);
    }
    KeyCodeUtils.fromString = fromString;
    function toUserSettingsUS(keyCode) {
        return userSettingsUSMap.keyCodeToStr(keyCode);
    }
    KeyCodeUtils.toUserSettingsUS = toUserSettingsUS;
    function toUserSettingsGeneral(keyCode) {
        return userSettingsGeneralMap.keyCodeToStr(keyCode);
    }
    KeyCodeUtils.toUserSettingsGeneral = toUserSettingsGeneral;
    function fromUserSettings(key) {
        return userSettingsUSMap.strToKeyCode(key) || userSettingsGeneralMap.strToKeyCode(key);
    }
    KeyCodeUtils.fromUserSettings = fromUserSettings;
})(KeyCodeUtils || (KeyCodeUtils = {}));
function KeyChord(firstPart, secondPart) {
    var chordPart = ((secondPart & 0x0000ffff) << 16) >>> 0;
    return (firstPart | chordPart) >>> 0;
}
function createKeybinding(keybinding, OS) {
    if (keybinding === 0) {
        return null;
    }
    var firstPart = (keybinding & 0x0000ffff) >>> 0;
    var chordPart = (keybinding & 0xffff0000) >>> 16;
    if (chordPart !== 0) {
        return new ChordKeybinding(createSimpleKeybinding(firstPart, OS), createSimpleKeybinding(chordPart, OS));
    }
    return createSimpleKeybinding(firstPart, OS);
}
function createSimpleKeybinding(keybinding, OS) {
    var ctrlCmd = (keybinding & 2048 /* CtrlCmd */ ? true : false);
    var winCtrl = (keybinding & 256 /* WinCtrl */ ? true : false);
    var ctrlKey = (OS === 2 /* Macintosh */ ? winCtrl : ctrlCmd);
    var shiftKey = (keybinding & 1024 /* Shift */ ? true : false);
    var altKey = (keybinding & 512 /* Alt */ ? true : false);
    var metaKey = (OS === 2 /* Macintosh */ ? ctrlCmd : winCtrl);
    var keyCode = (keybinding & 255 /* KeyCode */);
    return new SimpleKeybinding(ctrlKey, shiftKey, altKey, metaKey, keyCode);
}
var SimpleKeybinding = /** @class */ (function () {
    function SimpleKeybinding(ctrlKey, shiftKey, altKey, metaKey, keyCode) {
        this.type = 1 /* Simple */;
        this.ctrlKey = ctrlKey;
        this.shiftKey = shiftKey;
        this.altKey = altKey;
        this.metaKey = metaKey;
        this.keyCode = keyCode;
    }
    SimpleKeybinding.prototype.equals = function (other) {
        if (other.type !== 1 /* Simple */) {
            return false;
        }
        return (this.ctrlKey === other.ctrlKey
            && this.shiftKey === other.shiftKey
            && this.altKey === other.altKey
            && this.metaKey === other.metaKey
            && this.keyCode === other.keyCode);
    };
    SimpleKeybinding.prototype.getHashCode = function () {
        var ctrl = this.ctrlKey ? '1' : '0';
        var shift = this.shiftKey ? '1' : '0';
        var alt = this.altKey ? '1' : '0';
        var meta = this.metaKey ? '1' : '0';
        return "" + ctrl + shift + alt + meta + this.keyCode;
    };
    SimpleKeybinding.prototype.isModifierKey = function () {
        return (this.keyCode === 0 /* Unknown */
            || this.keyCode === 5 /* Ctrl */
            || this.keyCode === 57 /* Meta */
            || this.keyCode === 6 /* Alt */
            || this.keyCode === 4 /* Shift */);
    };
    /**
     * Does this keybinding refer to the key code of a modifier and it also has the modifier flag?
     */
    SimpleKeybinding.prototype.isDuplicateModifierCase = function () {
        return ((this.ctrlKey && this.keyCode === 5 /* Ctrl */)
            || (this.shiftKey && this.keyCode === 4 /* Shift */)
            || (this.altKey && this.keyCode === 6 /* Alt */)
            || (this.metaKey && this.keyCode === 57 /* Meta */));
    };
    return SimpleKeybinding;
}());

var ChordKeybinding = /** @class */ (function () {
    function ChordKeybinding(firstPart, chordPart) {
        this.type = 2 /* Chord */;
        this.firstPart = firstPart;
        this.chordPart = chordPart;
    }
    ChordKeybinding.prototype.getHashCode = function () {
        return this.firstPart.getHashCode() + ";" + this.chordPart.getHashCode();
    };
    return ChordKeybinding;
}());

var ResolvedKeybindingPart = /** @class */ (function () {
    function ResolvedKeybindingPart(ctrlKey, shiftKey, altKey, metaKey, kbLabel, kbAriaLabel) {
        this.ctrlKey = ctrlKey;
        this.shiftKey = shiftKey;
        this.altKey = altKey;
        this.metaKey = metaKey;
        this.keyLabel = kbLabel;
        this.keyAriaLabel = kbAriaLabel;
    }
    return ResolvedKeybindingPart;
}());

/**
 * A resolved keybinding. Can be a simple keybinding or a chord keybinding.
 */
var ResolvedKeybinding = /** @class */ (function () {
    function ResolvedKeybinding() {
    }
    return ResolvedKeybinding;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/core/selection.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var selection_extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();


/**
 * The direction of a selection.
 */
var SelectionDirection;
(function (SelectionDirection) {
    /**
     * The selection starts above where it ends.
     */
    SelectionDirection[SelectionDirection["LTR"] = 0] = "LTR";
    /**
     * The selection starts below where it ends.
     */
    SelectionDirection[SelectionDirection["RTL"] = 1] = "RTL";
})(SelectionDirection || (SelectionDirection = {}));
/**
 * A selection in the editor.
 * The selection is a range that has an orientation.
 */
var selection_Selection = /** @class */ (function (_super) {
    selection_extends(Selection, _super);
    function Selection(selectionStartLineNumber, selectionStartColumn, positionLineNumber, positionColumn) {
        var _this = _super.call(this, selectionStartLineNumber, selectionStartColumn, positionLineNumber, positionColumn) || this;
        _this.selectionStartLineNumber = selectionStartLineNumber;
        _this.selectionStartColumn = selectionStartColumn;
        _this.positionLineNumber = positionLineNumber;
        _this.positionColumn = positionColumn;
        return _this;
    }
    /**
     * Clone this selection.
     */
    Selection.prototype.clone = function () {
        return new Selection(this.selectionStartLineNumber, this.selectionStartColumn, this.positionLineNumber, this.positionColumn);
    };
    /**
     * Transform to a human-readable representation.
     */
    Selection.prototype.toString = function () {
        return '[' + this.selectionStartLineNumber + ',' + this.selectionStartColumn + ' -> ' + this.positionLineNumber + ',' + this.positionColumn + ']';
    };
    /**
     * Test if equals other selection.
     */
    Selection.prototype.equalsSelection = function (other) {
        return (Selection.selectionsEqual(this, other));
    };
    /**
     * Test if the two selections are equal.
     */
    Selection.selectionsEqual = function (a, b) {
        return (a.selectionStartLineNumber === b.selectionStartLineNumber &&
            a.selectionStartColumn === b.selectionStartColumn &&
            a.positionLineNumber === b.positionLineNumber &&
            a.positionColumn === b.positionColumn);
    };
    /**
     * Get directions (LTR or RTL).
     */
    Selection.prototype.getDirection = function () {
        if (this.selectionStartLineNumber === this.startLineNumber && this.selectionStartColumn === this.startColumn) {
            return SelectionDirection.LTR;
        }
        return SelectionDirection.RTL;
    };
    /**
     * Create a new selection with a different `positionLineNumber` and `positionColumn`.
     */
    Selection.prototype.setEndPosition = function (endLineNumber, endColumn) {
        if (this.getDirection() === SelectionDirection.LTR) {
            return new Selection(this.startLineNumber, this.startColumn, endLineNumber, endColumn);
        }
        return new Selection(endLineNumber, endColumn, this.startLineNumber, this.startColumn);
    };
    /**
     * Get the position at `positionLineNumber` and `positionColumn`.
     */
    Selection.prototype.getPosition = function () {
        return new Position(this.positionLineNumber, this.positionColumn);
    };
    /**
     * Create a new selection with a different `selectionStartLineNumber` and `selectionStartColumn`.
     */
    Selection.prototype.setStartPosition = function (startLineNumber, startColumn) {
        if (this.getDirection() === SelectionDirection.LTR) {
            return new Selection(startLineNumber, startColumn, this.endLineNumber, this.endColumn);
        }
        return new Selection(this.endLineNumber, this.endColumn, startLineNumber, startColumn);
    };
    // ----
    /**
     * Create a `Selection` from one or two positions
     */
    Selection.fromPositions = function (start, end) {
        if (end === void 0) { end = start; }
        return new Selection(start.lineNumber, start.column, end.lineNumber, end.column);
    };
    /**
     * Create a `Selection` from an `ISelection`.
     */
    Selection.liftSelection = function (sel) {
        return new Selection(sel.selectionStartLineNumber, sel.selectionStartColumn, sel.positionLineNumber, sel.positionColumn);
    };
    /**
     * `a` equals `b`.
     */
    Selection.selectionsArrEqual = function (a, b) {
        if (a && !b || !a && b) {
            return false;
        }
        if (!a && !b) {
            return true;
        }
        if (a.length !== b.length) {
            return false;
        }
        for (var i = 0, len = a.length; i < len; i++) {
            if (!this.selectionsEqual(a[i], b[i])) {
                return false;
            }
        }
        return true;
    };
    /**
     * Test if `obj` is an `ISelection`.
     */
    Selection.isISelection = function (obj) {
        return (obj
            && (typeof obj.selectionStartLineNumber === 'number')
            && (typeof obj.selectionStartColumn === 'number')
            && (typeof obj.positionLineNumber === 'number')
            && (typeof obj.positionColumn === 'number'));
    };
    /**
     * Create with a direction.
     */
    Selection.createWithDirection = function (startLineNumber, startColumn, endLineNumber, endColumn, direction) {
        if (direction === SelectionDirection.LTR) {
            return new Selection(startLineNumber, startColumn, endLineNumber, endColumn);
        }
        return new Selection(endLineNumber, endColumn, startLineNumber, startColumn);
    };
    return Selection;
}(range_Range));


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/cancellation.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


var shortcutEvent = Object.freeze(function (callback, context) {
    var handle = setTimeout(callback.bind(context), 0);
    return { dispose: function () { clearTimeout(handle); } };
});
var cancellation_CancellationToken;
(function (CancellationToken) {
    CancellationToken.None = Object.freeze({
        isCancellationRequested: false,
        onCancellationRequested: Event.None
    });
    CancellationToken.Cancelled = Object.freeze({
        isCancellationRequested: true,
        onCancellationRequested: shortcutEvent
    });
})(cancellation_CancellationToken || (cancellation_CancellationToken = {}));
var cancellation_MutableToken = /** @class */ (function () {
    function MutableToken() {
        this._isCancelled = false;
    }
    MutableToken.prototype.cancel = function () {
        if (!this._isCancelled) {
            this._isCancelled = true;
            if (this._emitter) {
                this._emitter.fire(undefined);
                this.dispose();
            }
        }
    };
    Object.defineProperty(MutableToken.prototype, "isCancellationRequested", {
        get: function () {
            return this._isCancelled;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(MutableToken.prototype, "onCancellationRequested", {
        get: function () {
            if (this._isCancelled) {
                return shortcutEvent;
            }
            if (!this._emitter) {
                this._emitter = new event_Emitter();
            }
            return this._emitter.event;
        },
        enumerable: true,
        configurable: true
    });
    MutableToken.prototype.dispose = function () {
        if (this._emitter) {
            this._emitter.dispose();
            this._emitter = undefined;
        }
    };
    return MutableToken;
}());
var CancellationTokenSource = /** @class */ (function () {
    function CancellationTokenSource() {
    }
    Object.defineProperty(CancellationTokenSource.prototype, "token", {
        get: function () {
            if (!this._token) {
                // be lazy and create the token only when
                // actually needed
                this._token = new cancellation_MutableToken();
            }
            return this._token;
        },
        enumerable: true,
        configurable: true
    });
    CancellationTokenSource.prototype.cancel = function () {
        if (!this._token) {
            // save an object by returning the default
            // cancelled token when cancellation happens
            // before someone asks for the token
            this._token = cancellation_CancellationToken.Cancelled;
        }
        else if (this._token instanceof cancellation_MutableToken) {
            // actually cancel
            this._token.cancel();
        }
    };
    CancellationTokenSource.prototype.dispose = function () {
        if (!this._token) {
            // ensure to initialize with an empty token if we had none
            this._token = cancellation_CancellationToken.None;
        }
        else if (this._token instanceof cancellation_MutableToken) {
            // actually dispose
            this._token.dispose();
        }
    };
    return CancellationTokenSource;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/core/token.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var Token = /** @class */ (function () {
    function Token(offset, type, language) {
        this.offset = offset | 0; // @perf
        this.type = type;
        this.language = language;
    }
    Token.prototype.toString = function () {
        return '(' + this.offset + ', ' + this.type + ')';
    };
    return Token;
}());

var TokenizationResult = /** @class */ (function () {
    function TokenizationResult(tokens, endState) {
        this.tokens = tokens;
        this.endState = endState;
    }
    return TokenizationResult;
}());

var TokenizationResult2 = /** @class */ (function () {
    function TokenizationResult2(tokens, endState) {
        this.tokens = tokens;
        this.endState = endState;
    }
    return TokenizationResult2;
}());


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/standalone/standaloneBase.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/










// --------------------------------------------
// This is repeated here so it can be exported
// because TS inlines const enums
// --------------------------------------------
var Severity;
(function (Severity) {
    Severity[Severity["Ignore"] = 0] = "Ignore";
    Severity[Severity["Info"] = 1] = "Info";
    Severity[Severity["Warning"] = 2] = "Warning";
    Severity[Severity["Error"] = 3] = "Error";
})(Severity || (Severity = {}));
var MarkerSeverity;
(function (MarkerSeverity) {
    MarkerSeverity[MarkerSeverity["Hint"] = 1] = "Hint";
    MarkerSeverity[MarkerSeverity["Info"] = 2] = "Info";
    MarkerSeverity[MarkerSeverity["Warning"] = 4] = "Warning";
    MarkerSeverity[MarkerSeverity["Error"] = 8] = "Error";
})(MarkerSeverity || (MarkerSeverity = {}));
// --------------------------------------------
// This is repeated here so it can be exported
// because TS inlines const enums
// --------------------------------------------
var standaloneBase_KeyMod = /** @class */ (function () {
    function KeyMod() {
    }
    KeyMod.chord = function (firstPart, secondPart) {
        return KeyChord(firstPart, secondPart);
    };
    KeyMod.CtrlCmd = 2048 /* CtrlCmd */;
    KeyMod.Shift = 1024 /* Shift */;
    KeyMod.Alt = 512 /* Alt */;
    KeyMod.WinCtrl = 256 /* WinCtrl */;
    return KeyMod;
}());

// --------------------------------------------
// This is repeated here so it can be exported
// because TS inlines const enums
// --------------------------------------------
/**
 * Virtual Key Codes, the value does not hold any inherent meaning.
 * Inspired somewhat from https://msdn.microsoft.com/en-us/library/windows/desktop/dd375731(v=vs.85).aspx
 * But these are "more general", as they should work across browsers & OS`s.
 */
var KeyCode;
(function (KeyCode) {
    /**
     * Placed first to cover the 0 value of the enum.
     */
    KeyCode[KeyCode["Unknown"] = 0] = "Unknown";
    KeyCode[KeyCode["Backspace"] = 1] = "Backspace";
    KeyCode[KeyCode["Tab"] = 2] = "Tab";
    KeyCode[KeyCode["Enter"] = 3] = "Enter";
    KeyCode[KeyCode["Shift"] = 4] = "Shift";
    KeyCode[KeyCode["Ctrl"] = 5] = "Ctrl";
    KeyCode[KeyCode["Alt"] = 6] = "Alt";
    KeyCode[KeyCode["PauseBreak"] = 7] = "PauseBreak";
    KeyCode[KeyCode["CapsLock"] = 8] = "CapsLock";
    KeyCode[KeyCode["Escape"] = 9] = "Escape";
    KeyCode[KeyCode["Space"] = 10] = "Space";
    KeyCode[KeyCode["PageUp"] = 11] = "PageUp";
    KeyCode[KeyCode["PageDown"] = 12] = "PageDown";
    KeyCode[KeyCode["End"] = 13] = "End";
    KeyCode[KeyCode["Home"] = 14] = "Home";
    KeyCode[KeyCode["LeftArrow"] = 15] = "LeftArrow";
    KeyCode[KeyCode["UpArrow"] = 16] = "UpArrow";
    KeyCode[KeyCode["RightArrow"] = 17] = "RightArrow";
    KeyCode[KeyCode["DownArrow"] = 18] = "DownArrow";
    KeyCode[KeyCode["Insert"] = 19] = "Insert";
    KeyCode[KeyCode["Delete"] = 20] = "Delete";
    KeyCode[KeyCode["KEY_0"] = 21] = "KEY_0";
    KeyCode[KeyCode["KEY_1"] = 22] = "KEY_1";
    KeyCode[KeyCode["KEY_2"] = 23] = "KEY_2";
    KeyCode[KeyCode["KEY_3"] = 24] = "KEY_3";
    KeyCode[KeyCode["KEY_4"] = 25] = "KEY_4";
    KeyCode[KeyCode["KEY_5"] = 26] = "KEY_5";
    KeyCode[KeyCode["KEY_6"] = 27] = "KEY_6";
    KeyCode[KeyCode["KEY_7"] = 28] = "KEY_7";
    KeyCode[KeyCode["KEY_8"] = 29] = "KEY_8";
    KeyCode[KeyCode["KEY_9"] = 30] = "KEY_9";
    KeyCode[KeyCode["KEY_A"] = 31] = "KEY_A";
    KeyCode[KeyCode["KEY_B"] = 32] = "KEY_B";
    KeyCode[KeyCode["KEY_C"] = 33] = "KEY_C";
    KeyCode[KeyCode["KEY_D"] = 34] = "KEY_D";
    KeyCode[KeyCode["KEY_E"] = 35] = "KEY_E";
    KeyCode[KeyCode["KEY_F"] = 36] = "KEY_F";
    KeyCode[KeyCode["KEY_G"] = 37] = "KEY_G";
    KeyCode[KeyCode["KEY_H"] = 38] = "KEY_H";
    KeyCode[KeyCode["KEY_I"] = 39] = "KEY_I";
    KeyCode[KeyCode["KEY_J"] = 40] = "KEY_J";
    KeyCode[KeyCode["KEY_K"] = 41] = "KEY_K";
    KeyCode[KeyCode["KEY_L"] = 42] = "KEY_L";
    KeyCode[KeyCode["KEY_M"] = 43] = "KEY_M";
    KeyCode[KeyCode["KEY_N"] = 44] = "KEY_N";
    KeyCode[KeyCode["KEY_O"] = 45] = "KEY_O";
    KeyCode[KeyCode["KEY_P"] = 46] = "KEY_P";
    KeyCode[KeyCode["KEY_Q"] = 47] = "KEY_Q";
    KeyCode[KeyCode["KEY_R"] = 48] = "KEY_R";
    KeyCode[KeyCode["KEY_S"] = 49] = "KEY_S";
    KeyCode[KeyCode["KEY_T"] = 50] = "KEY_T";
    KeyCode[KeyCode["KEY_U"] = 51] = "KEY_U";
    KeyCode[KeyCode["KEY_V"] = 52] = "KEY_V";
    KeyCode[KeyCode["KEY_W"] = 53] = "KEY_W";
    KeyCode[KeyCode["KEY_X"] = 54] = "KEY_X";
    KeyCode[KeyCode["KEY_Y"] = 55] = "KEY_Y";
    KeyCode[KeyCode["KEY_Z"] = 56] = "KEY_Z";
    KeyCode[KeyCode["Meta"] = 57] = "Meta";
    KeyCode[KeyCode["ContextMenu"] = 58] = "ContextMenu";
    KeyCode[KeyCode["F1"] = 59] = "F1";
    KeyCode[KeyCode["F2"] = 60] = "F2";
    KeyCode[KeyCode["F3"] = 61] = "F3";
    KeyCode[KeyCode["F4"] = 62] = "F4";
    KeyCode[KeyCode["F5"] = 63] = "F5";
    KeyCode[KeyCode["F6"] = 64] = "F6";
    KeyCode[KeyCode["F7"] = 65] = "F7";
    KeyCode[KeyCode["F8"] = 66] = "F8";
    KeyCode[KeyCode["F9"] = 67] = "F9";
    KeyCode[KeyCode["F10"] = 68] = "F10";
    KeyCode[KeyCode["F11"] = 69] = "F11";
    KeyCode[KeyCode["F12"] = 70] = "F12";
    KeyCode[KeyCode["F13"] = 71] = "F13";
    KeyCode[KeyCode["F14"] = 72] = "F14";
    KeyCode[KeyCode["F15"] = 73] = "F15";
    KeyCode[KeyCode["F16"] = 74] = "F16";
    KeyCode[KeyCode["F17"] = 75] = "F17";
    KeyCode[KeyCode["F18"] = 76] = "F18";
    KeyCode[KeyCode["F19"] = 77] = "F19";
    KeyCode[KeyCode["NumLock"] = 78] = "NumLock";
    KeyCode[KeyCode["ScrollLock"] = 79] = "ScrollLock";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the ';:' key
     */
    KeyCode[KeyCode["US_SEMICOLON"] = 80] = "US_SEMICOLON";
    /**
     * For any country/region, the '+' key
     * For the US standard keyboard, the '=+' key
     */
    KeyCode[KeyCode["US_EQUAL"] = 81] = "US_EQUAL";
    /**
     * For any country/region, the ',' key
     * For the US standard keyboard, the ',<' key
     */
    KeyCode[KeyCode["US_COMMA"] = 82] = "US_COMMA";
    /**
     * For any country/region, the '-' key
     * For the US standard keyboard, the '-_' key
     */
    KeyCode[KeyCode["US_MINUS"] = 83] = "US_MINUS";
    /**
     * For any country/region, the '.' key
     * For the US standard keyboard, the '.>' key
     */
    KeyCode[KeyCode["US_DOT"] = 84] = "US_DOT";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the '/?' key
     */
    KeyCode[KeyCode["US_SLASH"] = 85] = "US_SLASH";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the '`~' key
     */
    KeyCode[KeyCode["US_BACKTICK"] = 86] = "US_BACKTICK";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the '[{' key
     */
    KeyCode[KeyCode["US_OPEN_SQUARE_BRACKET"] = 87] = "US_OPEN_SQUARE_BRACKET";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the '\|' key
     */
    KeyCode[KeyCode["US_BACKSLASH"] = 88] = "US_BACKSLASH";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the ']}' key
     */
    KeyCode[KeyCode["US_CLOSE_SQUARE_BRACKET"] = 89] = "US_CLOSE_SQUARE_BRACKET";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the ''"' key
     */
    KeyCode[KeyCode["US_QUOTE"] = 90] = "US_QUOTE";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     */
    KeyCode[KeyCode["OEM_8"] = 91] = "OEM_8";
    /**
     * Either the angle bracket key or the backslash key on the RT 102-key keyboard.
     */
    KeyCode[KeyCode["OEM_102"] = 92] = "OEM_102";
    KeyCode[KeyCode["NUMPAD_0"] = 93] = "NUMPAD_0";
    KeyCode[KeyCode["NUMPAD_1"] = 94] = "NUMPAD_1";
    KeyCode[KeyCode["NUMPAD_2"] = 95] = "NUMPAD_2";
    KeyCode[KeyCode["NUMPAD_3"] = 96] = "NUMPAD_3";
    KeyCode[KeyCode["NUMPAD_4"] = 97] = "NUMPAD_4";
    KeyCode[KeyCode["NUMPAD_5"] = 98] = "NUMPAD_5";
    KeyCode[KeyCode["NUMPAD_6"] = 99] = "NUMPAD_6";
    KeyCode[KeyCode["NUMPAD_7"] = 100] = "NUMPAD_7";
    KeyCode[KeyCode["NUMPAD_8"] = 101] = "NUMPAD_8";
    KeyCode[KeyCode["NUMPAD_9"] = 102] = "NUMPAD_9";
    KeyCode[KeyCode["NUMPAD_MULTIPLY"] = 103] = "NUMPAD_MULTIPLY";
    KeyCode[KeyCode["NUMPAD_ADD"] = 104] = "NUMPAD_ADD";
    KeyCode[KeyCode["NUMPAD_SEPARATOR"] = 105] = "NUMPAD_SEPARATOR";
    KeyCode[KeyCode["NUMPAD_SUBTRACT"] = 106] = "NUMPAD_SUBTRACT";
    KeyCode[KeyCode["NUMPAD_DECIMAL"] = 107] = "NUMPAD_DECIMAL";
    KeyCode[KeyCode["NUMPAD_DIVIDE"] = 108] = "NUMPAD_DIVIDE";
    /**
     * Cover all key codes when IME is processing input.
     */
    KeyCode[KeyCode["KEY_IN_COMPOSITION"] = 109] = "KEY_IN_COMPOSITION";
    KeyCode[KeyCode["ABNT_C1"] = 110] = "ABNT_C1";
    KeyCode[KeyCode["ABNT_C2"] = 111] = "ABNT_C2";
    /**
     * Placed last to cover the length of the enum.
     * Please do not depend on this value!
     */
    KeyCode[KeyCode["MAX_VALUE"] = 112] = "MAX_VALUE";
})(KeyCode || (KeyCode = {}));
function createMonacoBaseAPI() {
    return {
        editor: undefined,
        languages: undefined,
        CancellationTokenSource: CancellationTokenSource,
        Emitter: event_Emitter,
        KeyCode: KeyCode,
        KeyMod: standaloneBase_KeyMod,
        Position: Position,
        Range: range_Range,
        Selection: selection_Selection,
        SelectionDirection: SelectionDirection,
        Severity: Severity,
        MarkerSeverity: MarkerSeverity,
        Promise: winjs_base["b" /* TPromise */],
        Uri: common_uri,
        Token: Token
    };
}

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/common/services/editorSimpleWorker.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var editorSimpleWorker_extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();












/**
 * @internal
 */
var editorSimpleWorker_MirrorModel = /** @class */ (function (_super) {
    editorSimpleWorker_extends(MirrorModel, _super);
    function MirrorModel() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Object.defineProperty(MirrorModel.prototype, "uri", {
        get: function () {
            return this._uri;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(MirrorModel.prototype, "version", {
        get: function () {
            return this._versionId;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(MirrorModel.prototype, "eol", {
        get: function () {
            return this._eol;
        },
        enumerable: true,
        configurable: true
    });
    MirrorModel.prototype.getValue = function () {
        return this.getText();
    };
    MirrorModel.prototype.getLinesContent = function () {
        return this._lines.slice(0);
    };
    MirrorModel.prototype.getLineCount = function () {
        return this._lines.length;
    };
    MirrorModel.prototype.getLineContent = function (lineNumber) {
        return this._lines[lineNumber - 1];
    };
    MirrorModel.prototype.getWordAtPosition = function (position, wordDefinition) {
        var wordAtText = getWordAtText(position.column, ensureValidWordDefinition(wordDefinition), this._lines[position.lineNumber - 1], 0);
        if (wordAtText) {
            return new range_Range(position.lineNumber, wordAtText.startColumn, position.lineNumber, wordAtText.endColumn);
        }
        return null;
    };
    MirrorModel.prototype.getWordUntilPosition = function (position, wordDefinition) {
        var wordAtPosition = this.getWordAtPosition(position, wordDefinition);
        if (!wordAtPosition) {
            return {
                word: '',
                startColumn: position.column,
                endColumn: position.column
            };
        }
        return {
            word: this._lines[position.lineNumber - 1].substring(wordAtPosition.startColumn - 1, position.column - 1),
            startColumn: wordAtPosition.startColumn,
            endColumn: position.column
        };
    };
    MirrorModel.prototype.createWordIterator = function (wordDefinition) {
        var _this = this;
        var obj = {
            done: false,
            value: ''
        };
        var lineNumber = 0;
        var lineText;
        var wordRangesIdx = 0;
        var wordRanges = [];
        var next = function () {
            if (wordRangesIdx < wordRanges.length) {
                obj.done = false;
                obj.value = lineText.substring(wordRanges[wordRangesIdx].start, wordRanges[wordRangesIdx].end);
                wordRangesIdx += 1;
            }
            else if (lineNumber >= _this._lines.length) {
                obj.done = true;
                obj.value = undefined;
            }
            else {
                lineText = _this._lines[lineNumber];
                wordRanges = _this._wordenize(lineText, wordDefinition);
                wordRangesIdx = 0;
                lineNumber += 1;
                return next();
            }
            return obj;
        };
        return { next: next };
    };
    MirrorModel.prototype._wordenize = function (content, wordDefinition) {
        var result = [];
        var match;
        wordDefinition.lastIndex = 0; // reset lastIndex just to be sure
        while (match = wordDefinition.exec(content)) {
            if (match[0].length === 0) {
                // it did match the empty string
                break;
            }
            result.push({ start: match.index, end: match.index + match[0].length });
        }
        return result;
    };
    MirrorModel.prototype.getValueInRange = function (range) {
        range = this._validateRange(range);
        if (range.startLineNumber === range.endLineNumber) {
            return this._lines[range.startLineNumber - 1].substring(range.startColumn - 1, range.endColumn - 1);
        }
        var lineEnding = this._eol;
        var startLineIndex = range.startLineNumber - 1;
        var endLineIndex = range.endLineNumber - 1;
        var resultLines = [];
        resultLines.push(this._lines[startLineIndex].substring(range.startColumn - 1));
        for (var i = startLineIndex + 1; i < endLineIndex; i++) {
            resultLines.push(this._lines[i]);
        }
        resultLines.push(this._lines[endLineIndex].substring(0, range.endColumn - 1));
        return resultLines.join(lineEnding);
    };
    MirrorModel.prototype.offsetAt = function (position) {
        position = this._validatePosition(position);
        this._ensureLineStarts();
        return this._lineStarts.getAccumulatedValue(position.lineNumber - 2) + (position.column - 1);
    };
    MirrorModel.prototype.positionAt = function (offset) {
        offset = Math.floor(offset);
        offset = Math.max(0, offset);
        this._ensureLineStarts();
        var out = this._lineStarts.getIndexOf(offset);
        var lineLength = this._lines[out.index].length;
        // Ensure we return a valid position
        return {
            lineNumber: 1 + out.index,
            column: 1 + Math.min(out.remainder, lineLength)
        };
    };
    MirrorModel.prototype._validateRange = function (range) {
        var start = this._validatePosition({ lineNumber: range.startLineNumber, column: range.startColumn });
        var end = this._validatePosition({ lineNumber: range.endLineNumber, column: range.endColumn });
        if (start.lineNumber !== range.startLineNumber
            || start.column !== range.startColumn
            || end.lineNumber !== range.endLineNumber
            || end.column !== range.endColumn) {
            return {
                startLineNumber: start.lineNumber,
                startColumn: start.column,
                endLineNumber: end.lineNumber,
                endColumn: end.column
            };
        }
        return range;
    };
    MirrorModel.prototype._validatePosition = function (position) {
        if (!Position.isIPosition(position)) {
            throw new Error('bad position');
        }
        var lineNumber = position.lineNumber, column = position.column;
        var hasChanged = false;
        if (lineNumber < 1) {
            lineNumber = 1;
            column = 1;
            hasChanged = true;
        }
        else if (lineNumber > this._lines.length) {
            lineNumber = this._lines.length;
            column = this._lines[lineNumber - 1].length + 1;
            hasChanged = true;
        }
        else {
            var maxCharacter = this._lines[lineNumber - 1].length + 1;
            if (column < 1) {
                column = 1;
                hasChanged = true;
            }
            else if (column > maxCharacter) {
                column = maxCharacter;
                hasChanged = true;
            }
        }
        if (!hasChanged) {
            return position;
        }
        else {
            return { lineNumber: lineNumber, column: column };
        }
    };
    return MirrorModel;
}(mirrorTextModel_MirrorTextModel));
/**
 * @internal
 */
var editorSimpleWorker_BaseEditorSimpleWorker = /** @class */ (function () {
    function BaseEditorSimpleWorker(foreignModuleFactory) {
        this._foreignModuleFactory = foreignModuleFactory;
        this._foreignModule = null;
    }
    // ---- BEGIN diff --------------------------------------------------------------------------
    BaseEditorSimpleWorker.prototype.computeDiff = function (originalUrl, modifiedUrl, ignoreTrimWhitespace) {
        var original = this._getModel(originalUrl);
        var modified = this._getModel(modifiedUrl);
        if (!original || !modified) {
            return null;
        }
        var originalLines = original.getLinesContent();
        var modifiedLines = modified.getLinesContent();
        var diffComputer = new DiffComputer(originalLines, modifiedLines, {
            shouldPostProcessCharChanges: true,
            shouldIgnoreTrimWhitespace: ignoreTrimWhitespace,
            shouldMakePrettyDiff: true
        });
        return winjs_base["b" /* TPromise */].as(diffComputer.computeDiff());
    };
    BaseEditorSimpleWorker.prototype.computeDirtyDiff = function (originalUrl, modifiedUrl, ignoreTrimWhitespace) {
        var original = this._getModel(originalUrl);
        var modified = this._getModel(modifiedUrl);
        if (!original || !modified) {
            return null;
        }
        var originalLines = original.getLinesContent();
        var modifiedLines = modified.getLinesContent();
        var diffComputer = new DiffComputer(originalLines, modifiedLines, {
            shouldPostProcessCharChanges: false,
            shouldIgnoreTrimWhitespace: ignoreTrimWhitespace,
            shouldMakePrettyDiff: true
        });
        return winjs_base["b" /* TPromise */].as(diffComputer.computeDiff());
    };
    BaseEditorSimpleWorker.prototype.computeMoreMinimalEdits = function (modelUrl, edits) {
        var model = this._getModel(modelUrl);
        if (!model) {
            return winjs_base["b" /* TPromise */].as(edits);
        }
        var result = [];
        var lastEol;
        for (var _i = 0, edits_1 = edits; _i < edits_1.length; _i++) {
            var _a = edits_1[_i], range = _a.range, text = _a.text, eol = _a.eol;
            if (typeof eol === 'number') {
                lastEol = eol;
            }
            if (!range) {
                // eol-change only
                continue;
            }
            var original = model.getValueInRange(range);
            text = text.replace(/\r\n|\n|\r/g, model.eol);
            if (original === text) {
                // noop
                continue;
            }
            // make sure diff won't take too long
            if (Math.max(text.length, original.length) > BaseEditorSimpleWorker._diffLimit) {
                result.push({ range: range, text: text });
                continue;
            }
            // compute diff between original and edit.text
            var changes = stringDiff(original, text, false);
            var editOffset = model.offsetAt(range_Range.lift(range).getStartPosition());
            for (var _b = 0, changes_1 = changes; _b < changes_1.length; _b++) {
                var change = changes_1[_b];
                var start = model.positionAt(editOffset + change.originalStart);
                var end = model.positionAt(editOffset + change.originalStart + change.originalLength);
                var newEdit = {
                    text: text.substr(change.modifiedStart, change.modifiedLength),
                    range: { startLineNumber: start.lineNumber, startColumn: start.column, endLineNumber: end.lineNumber, endColumn: end.column }
                };
                if (model.getValueInRange(newEdit.range) !== newEdit.text) {
                    result.push(newEdit);
                }
            }
        }
        if (typeof lastEol === 'number') {
            result.push({ eol: lastEol, text: undefined, range: undefined });
        }
        return winjs_base["b" /* TPromise */].as(result);
    };
    // ---- END minimal edits ---------------------------------------------------------------
    BaseEditorSimpleWorker.prototype.computeLinks = function (modelUrl) {
        var model = this._getModel(modelUrl);
        if (!model) {
            return null;
        }
        return winjs_base["b" /* TPromise */].as(computeLinks(model));
    };
    BaseEditorSimpleWorker.prototype.textualSuggest = function (modelUrl, position, wordDef, wordDefFlags) {
        var model = this._getModel(modelUrl);
        if (model) {
            var suggestions = [];
            var wordDefRegExp = new RegExp(wordDef, wordDefFlags);
            var currentWord = model.getWordUntilPosition(position, wordDefRegExp).word;
            var seen = Object.create(null);
            seen[currentWord] = true;
            for (var iter = model.createWordIterator(wordDefRegExp), e = iter.next(); !e.done && suggestions.length <= BaseEditorSimpleWorker._suggestionsLimit; e = iter.next()) {
                var word = e.value;
                if (seen[word]) {
                    continue;
                }
                seen[word] = true;
                if (!isNaN(Number(word))) {
                    continue;
                }
                suggestions.push({
                    type: 'text',
                    label: word,
                    insertText: word,
                    noAutoAccept: true,
                    overwriteBefore: currentWord.length
                });
            }
            return winjs_base["b" /* TPromise */].as({ suggestions: suggestions });
        }
        return undefined;
    };
    // ---- END suggest --------------------------------------------------------------------------
    BaseEditorSimpleWorker.prototype.navigateValueSet = function (modelUrl, range, up, wordDef, wordDefFlags) {
        var model = this._getModel(modelUrl);
        if (!model) {
            return null;
        }
        var wordDefRegExp = new RegExp(wordDef, wordDefFlags);
        if (range.startColumn === range.endColumn) {
            range = {
                startLineNumber: range.startLineNumber,
                startColumn: range.startColumn,
                endLineNumber: range.endLineNumber,
                endColumn: range.endColumn + 1
            };
        }
        var selectionText = model.getValueInRange(range);
        var wordRange = model.getWordAtPosition({ lineNumber: range.startLineNumber, column: range.startColumn }, wordDefRegExp);
        var word = null;
        if (wordRange !== null) {
            word = model.getValueInRange(wordRange);
        }
        var result = BasicInplaceReplace.INSTANCE.navigateValueSet(range, selectionText, wordRange, word, up);
        return winjs_base["b" /* TPromise */].as(result);
    };
    // ---- BEGIN foreign module support --------------------------------------------------------------------------
    BaseEditorSimpleWorker.prototype.loadForeignModule = function (moduleId, createData) {
        var _this = this;
        var ctx = {
            getMirrorModels: function () {
                return _this._getModels();
            }
        };
        if (this._foreignModuleFactory) {
            this._foreignModule = this._foreignModuleFactory(ctx, createData);
            // static foreing module
            var methods = [];
            for (var prop in this._foreignModule) {
                if (typeof this._foreignModule[prop] === 'function') {
                    methods.push(prop);
                }
            }
            return winjs_base["b" /* TPromise */].as(methods);
        }
        return new winjs_base["b" /* TPromise */](function (c, e) {
            Promise.resolve(/* AMD require */).then(function() { var __WEBPACK_AMD_REQUIRE_ARRAY__ = [__webpack_require__("ChvI")(moduleId)]; (function (foreignModule) {
                _this._foreignModule = foreignModule.create(ctx, createData);
                var methods = [];
                for (var prop in _this._foreignModule) {
                    if (typeof _this._foreignModule[prop] === 'function') {
                        methods.push(prop);
                    }
                }
                c(methods);
            }).apply(null, __WEBPACK_AMD_REQUIRE_ARRAY__);}).catch(e.bind(this));
        });
    };
    // foreign method request
    BaseEditorSimpleWorker.prototype.fmr = function (method, args) {
        if (!this._foreignModule || typeof this._foreignModule[method] !== 'function') {
            return winjs_base["b" /* TPromise */].wrapError(new Error('Missing requestHandler or method: ' + method));
        }
        try {
            return winjs_base["b" /* TPromise */].as(this._foreignModule[method].apply(this._foreignModule, args));
        }
        catch (e) {
            return winjs_base["b" /* TPromise */].wrapError(e);
        }
    };
    // ---- END diff --------------------------------------------------------------------------
    // ---- BEGIN minimal edits ---------------------------------------------------------------
    BaseEditorSimpleWorker._diffLimit = 10000;
    // ---- BEGIN suggest --------------------------------------------------------------------------
    BaseEditorSimpleWorker._suggestionsLimit = 10000;
    return BaseEditorSimpleWorker;
}());

/**
 * @internal
 */
var editorSimpleWorker_EditorSimpleWorkerImpl = /** @class */ (function (_super) {
    editorSimpleWorker_extends(EditorSimpleWorkerImpl, _super);
    function EditorSimpleWorkerImpl(foreignModuleFactory) {
        var _this = _super.call(this, foreignModuleFactory) || this;
        _this._models = Object.create(null);
        return _this;
    }
    EditorSimpleWorkerImpl.prototype.dispose = function () {
        this._models = Object.create(null);
    };
    EditorSimpleWorkerImpl.prototype._getModel = function (uri) {
        return this._models[uri];
    };
    EditorSimpleWorkerImpl.prototype._getModels = function () {
        var _this = this;
        var all = [];
        Object.keys(this._models).forEach(function (key) { return all.push(_this._models[key]); });
        return all;
    };
    EditorSimpleWorkerImpl.prototype.acceptNewModel = function (data) {
        this._models[data.url] = new editorSimpleWorker_MirrorModel(common_uri.parse(data.url), data.lines, data.EOL, data.versionId);
    };
    EditorSimpleWorkerImpl.prototype.acceptModelChanged = function (strURL, e) {
        if (!this._models[strURL]) {
            return;
        }
        var model = this._models[strURL];
        model.onEvents(e);
    };
    EditorSimpleWorkerImpl.prototype.acceptRemovedModel = function (strURL) {
        if (!this._models[strURL]) {
            return;
        }
        delete this._models[strURL];
    };
    return EditorSimpleWorkerImpl;
}(editorSimpleWorker_BaseEditorSimpleWorker));

/**
 * Called on the worker side
 * @internal
 */
function editorSimpleWorker_create() {
    return new editorSimpleWorker_EditorSimpleWorkerImpl(null);
}
if (typeof importScripts === 'function') {
    // Running in a web worker
    platform["a" /* globals */].monaco = createMonacoBaseAPI();
}

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/async.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var async_extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();





function isPromiseLike(obj) {
    return obj && typeof obj.then === 'function';
}
function toPromiseLike(arg) {
    if (isPromiseLike(arg)) {
        return arg;
    }
    else {
        return winjs_base["b" /* TPromise */].as(arg);
    }
}
function asWinJsPromise(callback) {
    var source = new CancellationTokenSource();
    return new winjs_base["b" /* TPromise */](function (resolve, reject, progress) {
        var item = callback(source.token);
        if (item instanceof winjs_base["b" /* TPromise */]) {
            item.then(function (result) {
                source.dispose();
                resolve(result);
            }, function (err) {
                source.dispose();
                reject(err);
            }, progress);
        }
        else if (isPromiseLike(item)) {
            item.then(function (result) {
                source.dispose();
                resolve(result);
            }, function (err) {
                source.dispose();
                reject(err);
            });
        }
        else {
            source.dispose();
            resolve(item);
        }
    }, function () {
        source.cancel();
    });
}
/**
 * Hook a cancellation token to a WinJS Promise
 */
function wireCancellationToken(token, promise, resolveAsUndefinedWhenCancelled) {
    var subscription = token.onCancellationRequested(function () { return promise.cancel(); });
    if (resolveAsUndefinedWhenCancelled) {
        promise = promise.then(undefined, function (err) {
            if (!isPromiseCanceledError(err)) {
                return winjs_base["b" /* TPromise */].wrapError(err);
            }
            return undefined;
        });
    }
    return always(promise, function () { return subscription.dispose(); });
}
/**
 * A helper to prevent accumulation of sequential async tasks.
 *
 * Imagine a mail man with the sole task of delivering letters. As soon as
 * a letter submitted for delivery, he drives to the destination, delivers it
 * and returns to his base. Imagine that during the trip, N more letters were submitted.
 * When the mail man returns, he picks those N letters and delivers them all in a
 * single trip. Even though N+1 submissions occurred, only 2 deliveries were made.
 *
 * The throttler implements this via the queue() method, by providing it a task
 * factory. Following the example:
 *
 * 		const throttler = new Throttler();
 * 		const letters = [];
 *
 * 		function deliver() {
 * 			const lettersToDeliver = letters;
 * 			letters = [];
 * 			return makeTheTrip(lettersToDeliver);
 * 		}
 *
 * 		function onLetterReceived(l) {
 * 			letters.push(l);
 * 			throttler.queue(deliver);
 * 		}
 */
var async_Throttler = /** @class */ (function () {
    function Throttler() {
        this.activePromise = null;
        this.queuedPromise = null;
        this.queuedPromiseFactory = null;
    }
    Throttler.prototype.queue = function (promiseFactory) {
        var _this = this;
        if (this.activePromise) {
            this.queuedPromiseFactory = promiseFactory;
            if (!this.queuedPromise) {
                var onComplete_1 = function () {
                    _this.queuedPromise = null;
                    var result = _this.queue(_this.queuedPromiseFactory);
                    _this.queuedPromiseFactory = null;
                    return result;
                };
                this.queuedPromise = new winjs_base["b" /* TPromise */](function (c, e, p) {
                    _this.activePromise.then(onComplete_1, onComplete_1, p).done(c);
                }, function () {
                    _this.activePromise.cancel();
                });
            }
            return new winjs_base["b" /* TPromise */](function (c, e, p) {
                _this.queuedPromise.then(c, e, p);
            }, function () {
                // no-op
            });
        }
        this.activePromise = promiseFactory();
        return new winjs_base["b" /* TPromise */](function (c, e, p) {
            _this.activePromise.done(function (result) {
                _this.activePromise = null;
                c(result);
            }, function (err) {
                _this.activePromise = null;
                e(err);
            }, p);
        }, function () {
            _this.activePromise.cancel();
        });
    };
    return Throttler;
}());

// TODO@Joao: can the previous throttler be replaced with this?
var async_SimpleThrottler = /** @class */ (function () {
    function SimpleThrottler() {
        this.current = winjs_base["b" /* TPromise */].wrap(null);
    }
    SimpleThrottler.prototype.queue = function (promiseTask) {
        return this.current = this.current.then(function () { return promiseTask(); });
    };
    return SimpleThrottler;
}());

/**
 * A helper to delay execution of a task that is being requested often.
 *
 * Following the throttler, now imagine the mail man wants to optimize the number of
 * trips proactively. The trip itself can be long, so the he decides not to make the trip
 * as soon as a letter is submitted. Instead he waits a while, in case more
 * letters are submitted. After said waiting period, if no letters were submitted, he
 * decides to make the trip. Imagine that N more letters were submitted after the first
 * one, all within a short period of time between each other. Even though N+1
 * submissions occurred, only 1 delivery was made.
 *
 * The delayer offers this behavior via the trigger() method, into which both the task
 * to be executed and the waiting period (delay) must be passed in as arguments. Following
 * the example:
 *
 * 		const delayer = new Delayer(WAITING_PERIOD);
 * 		const letters = [];
 *
 * 		function letterReceived(l) {
 * 			letters.push(l);
 * 			delayer.trigger(() => { return makeTheTrip(); });
 * 		}
 */
var async_Delayer = /** @class */ (function () {
    function Delayer(defaultDelay) {
        this.defaultDelay = defaultDelay;
        this.timeout = null;
        this.completionPromise = null;
        this.onSuccess = null;
        this.task = null;
    }
    Delayer.prototype.trigger = function (task, delay) {
        var _this = this;
        if (delay === void 0) { delay = this.defaultDelay; }
        this.task = task;
        this.cancelTimeout();
        if (!this.completionPromise) {
            this.completionPromise = new winjs_base["b" /* TPromise */](function (c) {
                _this.onSuccess = c;
            }, function () {
                // no-op
            }).then(function () {
                _this.completionPromise = null;
                _this.onSuccess = null;
                var task = _this.task;
                _this.task = null;
                return task();
            });
        }
        this.timeout = setTimeout(function () {
            _this.timeout = null;
            _this.onSuccess(null);
        }, delay);
        return this.completionPromise;
    };
    Delayer.prototype.isTriggered = function () {
        return this.timeout !== null;
    };
    Delayer.prototype.cancel = function () {
        this.cancelTimeout();
        if (this.completionPromise) {
            this.completionPromise.cancel();
            this.completionPromise = null;
        }
    };
    Delayer.prototype.cancelTimeout = function () {
        if (this.timeout !== null) {
            clearTimeout(this.timeout);
            this.timeout = null;
        }
    };
    return Delayer;
}());

/**
 * A helper to delay execution of a task that is being requested often, while
 * preventing accumulation of consecutive executions, while the task runs.
 *
 * Simply combine the two mail man strategies from the Throttler and Delayer
 * helpers, for an analogy.
 */
var ThrottledDelayer = /** @class */ (function (_super) {
    async_extends(ThrottledDelayer, _super);
    function ThrottledDelayer(defaultDelay) {
        var _this = _super.call(this, defaultDelay) || this;
        _this.throttler = new async_Throttler();
        return _this;
    }
    ThrottledDelayer.prototype.trigger = function (promiseFactory, delay) {
        var _this = this;
        return _super.prototype.trigger.call(this, function () { return _this.throttler.queue(promiseFactory); }, delay);
    };
    return ThrottledDelayer;
}(async_Delayer));

/**
 * A barrier that is initially closed and then becomes opened permanently.
 */
var async_Barrier = /** @class */ (function () {
    function Barrier() {
        var _this = this;
        this._isOpen = false;
        this._promise = new winjs_base["b" /* TPromise */](function (c, e, p) {
            _this._completePromise = c;
        }, function () {
            console.warn('You should really not try to cancel this ready promise!');
        });
    }
    Barrier.prototype.isOpen = function () {
        return this._isOpen;
    };
    Barrier.prototype.open = function () {
        this._isOpen = true;
        this._completePromise(true);
    };
    Barrier.prototype.wait = function () {
        return this._promise;
    };
    return Barrier;
}());

var async_ShallowCancelThenPromise = /** @class */ (function (_super) {
    async_extends(ShallowCancelThenPromise, _super);
    function ShallowCancelThenPromise(outer) {
        var _this = this;
        var completeCallback, errorCallback, progressCallback;
        _this = _super.call(this, function (c, e, p) {
            completeCallback = c;
            errorCallback = e;
            progressCallback = p;
        }, function () {
            // cancel this promise but not the
            // outer promise
            errorCallback(canceled());
        }) || this;
        outer.then(completeCallback, errorCallback, progressCallback);
        return _this;
    }
    return ShallowCancelThenPromise;
}(winjs_base["b" /* TPromise */]));

/**
 * Replacement for `WinJS.Promise.timeout`.
 */
function timeout(n) {
    return new winjs_base["a" /* Promise */](function (resolve) { return setTimeout(resolve, n); });
}
function isWinJSPromise(candidate) {
    return winjs_base["b" /* TPromise */].is(candidate) && typeof candidate.done === 'function';
}
function always(winjsPromiseOrPromiseLike, f) {
    if (isWinJSPromise(winjsPromiseOrPromiseLike)) {
        return new winjs_base["b" /* TPromise */](function (c, e, p) {
            winjsPromiseOrPromiseLike.done(function (result) {
                try {
                    f(result);
                }
                catch (e1) {
                    onUnexpectedError(e1);
                }
                c(result);
            }, function (err) {
                try {
                    f(err);
                }
                catch (e1) {
                    onUnexpectedError(e1);
                }
                e(err);
            }, function (progress) {
                p(progress);
            });
        }, function () {
            winjsPromiseOrPromiseLike.cancel();
        });
    }
    else {
        // simple
        winjsPromiseOrPromiseLike.then(function (_) { return f(); }, function (_) { return f(); });
        return winjsPromiseOrPromiseLike;
    }
}
/**
 * Runs the provided list of promise factories in sequential order. The returned
 * promise will complete to an array of results from each promise.
 */
function sequence(promiseFactories) {
    var results = [];
    // reverse since we start with last element using pop()
    promiseFactories = promiseFactories.reverse();
    function next() {
        if (promiseFactories.length) {
            return promiseFactories.pop()();
        }
        return null;
    }
    function thenHandler(result) {
        if (result !== undefined && result !== null) {
            results.push(result);
        }
        var n = next();
        if (n) {
            return n.then(thenHandler);
        }
        return winjs_base["b" /* TPromise */].as(results);
    }
    return winjs_base["b" /* TPromise */].as(null).then(thenHandler);
}
function first(promiseFactories, shouldStop) {
    if (shouldStop === void 0) { shouldStop = function (t) { return !!t; }; }
    promiseFactories = promiseFactories.reverse().slice();
    var loop = function () {
        if (promiseFactories.length === 0) {
            return winjs_base["b" /* TPromise */].as(null);
        }
        var factory = promiseFactories.pop();
        var promise = factory();
        return promise.then(function (result) {
            if (shouldStop(result)) {
                return winjs_base["b" /* TPromise */].as(result);
            }
            return loop();
        });
    };
    return loop();
}
/**
 * A helper to queue N promises and run them all with a max degree of parallelism. The helper
 * ensures that at any time no more than M promises are running at the same time.
 */
var async_Limiter = /** @class */ (function () {
    function Limiter(maxDegreeOfParalellism) {
        this.maxDegreeOfParalellism = maxDegreeOfParalellism;
        this.outstandingPromises = [];
        this.runningPromises = 0;
        this._onFinished = new event_Emitter();
    }
    Object.defineProperty(Limiter.prototype, "onFinished", {
        get: function () {
            return this._onFinished.event;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Limiter.prototype, "size", {
        get: function () {
            return this.runningPromises + this.outstandingPromises.length;
        },
        enumerable: true,
        configurable: true
    });
    Limiter.prototype.queue = function (promiseFactory) {
        var _this = this;
        return new winjs_base["b" /* TPromise */](function (c, e, p) {
            _this.outstandingPromises.push({
                factory: promiseFactory,
                c: c,
                e: e,
                p: p
            });
            _this.consume();
        });
    };
    Limiter.prototype.consume = function () {
        var _this = this;
        while (this.outstandingPromises.length && this.runningPromises < this.maxDegreeOfParalellism) {
            var iLimitedTask = this.outstandingPromises.shift();
            this.runningPromises++;
            var promise = iLimitedTask.factory();
            promise.done(iLimitedTask.c, iLimitedTask.e, iLimitedTask.p);
            promise.done(function () { return _this.consumed(); }, function () { return _this.consumed(); });
        }
    };
    Limiter.prototype.consumed = function () {
        this.runningPromises--;
        if (this.outstandingPromises.length > 0) {
            this.consume();
        }
        else {
            this._onFinished.fire();
        }
    };
    Limiter.prototype.dispose = function () {
        this._onFinished.dispose();
    };
    return Limiter;
}());

/**
 * A queue is handles one promise at a time and guarantees that at any time only one promise is executing.
 */
var Queue = /** @class */ (function (_super) {
    async_extends(Queue, _super);
    function Queue() {
        return _super.call(this, 1) || this;
    }
    return Queue;
}(async_Limiter));

/**
 * A helper to organize queues per resource. The ResourceQueue makes sure to manage queues per resource
 * by disposing them once the queue is empty.
 */
var ResourceQueue = /** @class */ (function () {
    function ResourceQueue() {
        this.queues = Object.create(null);
    }
    ResourceQueue.prototype.queueFor = function (resource) {
        var _this = this;
        var key = resource.toString();
        if (!this.queues[key]) {
            var queue_1 = new Queue();
            queue_1.onFinished(function () {
                queue_1.dispose();
                delete _this.queues[key];
            });
            this.queues[key] = queue_1;
        }
        return this.queues[key];
    };
    return ResourceQueue;
}());

function setDisposableTimeout(handler, timeout) {
    var args = [];
    for (var _i = 2; _i < arguments.length; _i++) {
        args[_i - 2] = arguments[_i];
    }
    var handle = setTimeout.apply(void 0, [handler, timeout].concat(args));
    return { dispose: function () { clearTimeout(handle); } };
}
var TimeoutTimer = /** @class */ (function (_super) {
    async_extends(TimeoutTimer, _super);
    function TimeoutTimer() {
        var _this = _super.call(this) || this;
        _this._token = -1;
        return _this;
    }
    TimeoutTimer.prototype.dispose = function () {
        this.cancel();
        _super.prototype.dispose.call(this);
    };
    TimeoutTimer.prototype.cancel = function () {
        if (this._token !== -1) {
            clearTimeout(this._token);
            this._token = -1;
        }
    };
    TimeoutTimer.prototype.cancelAndSet = function (runner, timeout) {
        var _this = this;
        this.cancel();
        this._token = setTimeout(function () {
            _this._token = -1;
            runner();
        }, timeout);
    };
    TimeoutTimer.prototype.setIfNotSet = function (runner, timeout) {
        var _this = this;
        if (this._token !== -1) {
            // timer is already set
            return;
        }
        this._token = setTimeout(function () {
            _this._token = -1;
            runner();
        }, timeout);
    };
    return TimeoutTimer;
}(Disposable));

var IntervalTimer = /** @class */ (function (_super) {
    async_extends(IntervalTimer, _super);
    function IntervalTimer() {
        var _this = _super.call(this) || this;
        _this._token = -1;
        return _this;
    }
    IntervalTimer.prototype.dispose = function () {
        this.cancel();
        _super.prototype.dispose.call(this);
    };
    IntervalTimer.prototype.cancel = function () {
        if (this._token !== -1) {
            clearInterval(this._token);
            this._token = -1;
        }
    };
    IntervalTimer.prototype.cancelAndSet = function (runner, interval) {
        this.cancel();
        this._token = setInterval(function () {
            runner();
        }, interval);
    };
    return IntervalTimer;
}(Disposable));

var RunOnceScheduler = /** @class */ (function () {
    function RunOnceScheduler(runner, timeout) {
        this.timeoutToken = -1;
        this.runner = runner;
        this.timeout = timeout;
        this.timeoutHandler = this.onTimeout.bind(this);
    }
    /**
     * Dispose RunOnceScheduler
     */
    RunOnceScheduler.prototype.dispose = function () {
        this.cancel();
        this.runner = null;
    };
    /**
     * Cancel current scheduled runner (if any).
     */
    RunOnceScheduler.prototype.cancel = function () {
        if (this.isScheduled()) {
            clearTimeout(this.timeoutToken);
            this.timeoutToken = -1;
        }
    };
    /**
     * Cancel previous runner (if any) & schedule a new runner.
     */
    RunOnceScheduler.prototype.schedule = function (delay) {
        if (delay === void 0) { delay = this.timeout; }
        this.cancel();
        this.timeoutToken = setTimeout(this.timeoutHandler, delay);
    };
    /**
     * Returns true if scheduled.
     */
    RunOnceScheduler.prototype.isScheduled = function () {
        return this.timeoutToken !== -1;
    };
    RunOnceScheduler.prototype.onTimeout = function () {
        this.timeoutToken = -1;
        if (this.runner) {
            this.runner();
        }
    };
    return RunOnceScheduler;
}());

function nfcall(fn) {
    var args = [];
    for (var _i = 1; _i < arguments.length; _i++) {
        args[_i - 1] = arguments[_i];
    }
    return new winjs_base["b" /* TPromise */](function (c, e) { return fn.apply(void 0, args.concat([function (err, result) { return err ? e(err) : c(result); }])); }, function () { return null; });
}
function ninvoke(thisArg, fn) {
    var args = [];
    for (var _i = 2; _i < arguments.length; _i++) {
        args[_i - 2] = arguments[_i];
    }
    return new winjs_base["b" /* TPromise */](function (c, e) { return fn.call.apply(fn, [thisArg].concat(args, [function (err, result) { return err ? e(err) : c(result); }])); }, function () { return null; });
}
/**
 * An emitter that will ignore any events that occur during a specific code
 * execution triggered via throttle() until the promise has finished (either
 * successfully or with an error). Only after the promise has finished, the
 * last event that was fired during the operation will get emitted.
 *
 */
var ThrottledEmitter = /** @class */ (function (_super) {
    async_extends(ThrottledEmitter, _super);
    function ThrottledEmitter() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    ThrottledEmitter.prototype.throttle = function (promise) {
        var _this = this;
        this.suspended = true;
        return always(promise, function () { return _this.resume(); });
    };
    ThrottledEmitter.prototype.fire = function (event) {
        if (this.suspended) {
            this.lastEvent = event;
            this.hasLastEvent = true;
            return;
        }
        return _super.prototype.fire.call(this, event);
    };
    ThrottledEmitter.prototype.resume = function () {
        this.suspended = false;
        if (this.hasLastEvent) {
            this.fire(this.lastEvent);
        }
        this.hasLastEvent = false;
        this.lastEvent = void 0;
    };
    return ThrottledEmitter;
}(event_Emitter));


// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/worker/simpleWorker.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var simpleWorker_extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();





var INITIALIZE = '$initialize';
var webWorkerWarningLogged = false;
function logOnceWebWorkerWarning(err) {
    if (!platform["b" /* isWeb */]) {
        // running tests
        return;
    }
    if (!webWorkerWarningLogged) {
        webWorkerWarningLogged = true;
        console.warn('Could not create web worker(s). Falling back to loading web worker code in main thread, which might cause UI freezes. Please see https://github.com/Microsoft/monaco-editor#faq');
    }
    console.warn(err.message);
}
var simpleWorker_SimpleWorkerProtocol = /** @class */ (function () {
    function SimpleWorkerProtocol(handler) {
        this._workerId = -1;
        this._handler = handler;
        this._lastSentReq = 0;
        this._pendingReplies = Object.create(null);
    }
    SimpleWorkerProtocol.prototype.setWorkerId = function (workerId) {
        this._workerId = workerId;
    };
    SimpleWorkerProtocol.prototype.sendMessage = function (method, args) {
        var req = String(++this._lastSentReq);
        var reply = {
            c: null,
            e: null
        };
        var result = new winjs_base["b" /* TPromise */](function (c, e, p) {
            reply.c = c;
            reply.e = e;
        }, function () {
            // Cancel not supported
        });
        this._pendingReplies[req] = reply;
        this._send({
            vsWorker: this._workerId,
            req: req,
            method: method,
            args: args
        });
        return result;
    };
    SimpleWorkerProtocol.prototype.handleMessage = function (serializedMessage) {
        var message;
        try {
            message = JSON.parse(serializedMessage);
        }
        catch (e) {
            // nothing
        }
        if (!message || !message.vsWorker) {
            return;
        }
        if (this._workerId !== -1 && message.vsWorker !== this._workerId) {
            return;
        }
        this._handleMessage(message);
    };
    SimpleWorkerProtocol.prototype._handleMessage = function (msg) {
        var _this = this;
        if (msg.seq) {
            var replyMessage = msg;
            if (!this._pendingReplies[replyMessage.seq]) {
                console.warn('Got reply to unknown seq');
                return;
            }
            var reply = this._pendingReplies[replyMessage.seq];
            delete this._pendingReplies[replyMessage.seq];
            if (replyMessage.err) {
                var err = replyMessage.err;
                if (replyMessage.err.$isError) {
                    err = new Error();
                    err.name = replyMessage.err.name;
                    err.message = replyMessage.err.message;
                    err.stack = replyMessage.err.stack;
                }
                reply.e(err);
                return;
            }
            reply.c(replyMessage.res);
            return;
        }
        var requestMessage = msg;
        var req = requestMessage.req;
        var result = this._handler.handleMessage(requestMessage.method, requestMessage.args);
        result.then(function (r) {
            _this._send({
                vsWorker: _this._workerId,
                seq: req,
                res: r,
                err: undefined
            });
        }, function (e) {
            if (e.detail instanceof Error) {
                // Loading errors have a detail property that points to the actual error
                e.detail = transformErrorForSerialization(e.detail);
            }
            _this._send({
                vsWorker: _this._workerId,
                seq: req,
                res: undefined,
                err: transformErrorForSerialization(e)
            });
        });
    };
    SimpleWorkerProtocol.prototype._send = function (msg) {
        var strMsg = JSON.stringify(msg);
        // console.log('SENDING: ' + strMsg);
        this._handler.sendMessage(strMsg);
    };
    return SimpleWorkerProtocol;
}());
/**
 * Main thread side
 */
var simpleWorker_SimpleWorkerClient = /** @class */ (function (_super) {
    simpleWorker_extends(SimpleWorkerClient, _super);
    function SimpleWorkerClient(workerFactory, moduleId) {
        var _this = _super.call(this) || this;
        var lazyProxyFulfill = null;
        var lazyProxyReject = null;
        _this._worker = _this._register(workerFactory.create('vs/base/common/worker/simpleWorker', function (msg) {
            _this._protocol.handleMessage(msg);
        }, function (err) {
            // in Firefox, web workers fail lazily :(
            // we will reject the proxy
            lazyProxyReject(err);
        }));
        _this._protocol = new simpleWorker_SimpleWorkerProtocol({
            sendMessage: function (msg) {
                _this._worker.postMessage(msg);
            },
            handleMessage: function (method, args) {
                // Intentionally not supporting worker -> main requests
                return winjs_base["b" /* TPromise */].as(null);
            }
        });
        _this._protocol.setWorkerId(_this._worker.getId());
        // Gather loader configuration
        var loaderConfiguration = null;
        if (typeof self.require !== 'undefined' && typeof self.require.getConfig === 'function') {
            // Get the configuration from the Monaco AMD Loader
            loaderConfiguration = self.require.getConfig();
        }
        else if (typeof self.requirejs !== 'undefined') {
            // Get the configuration from requirejs
            loaderConfiguration = self.requirejs.s.contexts._.config;
        }
        _this._lazyProxy = new winjs_base["b" /* TPromise */](function (c, e, p) {
            lazyProxyFulfill = c;
            lazyProxyReject = e;
        }, function () { });
        // Send initialize message
        _this._onModuleLoaded = _this._protocol.sendMessage(INITIALIZE, [
            _this._worker.getId(),
            moduleId,
            loaderConfiguration
        ]);
        _this._onModuleLoaded.then(function (availableMethods) {
            var proxy = {};
            for (var i = 0; i < availableMethods.length; i++) {
                proxy[availableMethods[i]] = createProxyMethod(availableMethods[i], proxyMethodRequest);
            }
            lazyProxyFulfill(proxy);
        }, function (e) {
            lazyProxyReject(e);
            _this._onError('Worker failed to load ' + moduleId, e);
        });
        // Create proxy to loaded code
        var proxyMethodRequest = function (method, args) {
            return _this._request(method, args);
        };
        var createProxyMethod = function (method, proxyMethodRequest) {
            return function () {
                var args = Array.prototype.slice.call(arguments, 0);
                return proxyMethodRequest(method, args);
            };
        };
        return _this;
    }
    SimpleWorkerClient.prototype.getProxyObject = function () {
        // Do not allow chaining promises to cancel the proxy creation
        return new async_ShallowCancelThenPromise(this._lazyProxy);
    };
    SimpleWorkerClient.prototype._request = function (method, args) {
        var _this = this;
        return new winjs_base["b" /* TPromise */](function (c, e, p) {
            _this._onModuleLoaded.then(function () {
                _this._protocol.sendMessage(method, args).then(c, e);
            }, e);
        }, function () {
            // Cancel intentionally not supported
        });
    };
    SimpleWorkerClient.prototype._onError = function (message, error) {
        console.error(message);
        console.info(error);
    };
    return SimpleWorkerClient;
}(Disposable));

/**
 * Worker side
 */
var simpleWorker_SimpleWorkerServer = /** @class */ (function () {
    function SimpleWorkerServer(postSerializedMessage, requestHandler) {
        var _this = this;
        this._requestHandler = requestHandler;
        this._protocol = new simpleWorker_SimpleWorkerProtocol({
            sendMessage: function (msg) {
                postSerializedMessage(msg);
            },
            handleMessage: function (method, args) { return _this._handleMessage(method, args); }
        });
    }
    SimpleWorkerServer.prototype.onmessage = function (msg) {
        this._protocol.handleMessage(msg);
    };
    SimpleWorkerServer.prototype._handleMessage = function (method, args) {
        if (method === INITIALIZE) {
            return this.initialize(args[0], args[1], args[2]);
        }
        if (!this._requestHandler || typeof this._requestHandler[method] !== 'function') {
            return winjs_base["b" /* TPromise */].wrapError(new Error('Missing requestHandler or method: ' + method));
        }
        try {
            return winjs_base["b" /* TPromise */].as(this._requestHandler[method].apply(this._requestHandler, args));
        }
        catch (e) {
            return winjs_base["b" /* TPromise */].wrapError(e);
        }
    };
    SimpleWorkerServer.prototype.initialize = function (workerId, moduleId, loaderConfig) {
        var _this = this;
        this._protocol.setWorkerId(workerId);
        if (this._requestHandler) {
            // static request handler
            var methods = [];
            for (var prop in this._requestHandler) {
                if (typeof this._requestHandler[prop] === 'function') {
                    methods.push(prop);
                }
            }
            return winjs_base["b" /* TPromise */].as(methods);
        }
        if (loaderConfig) {
            // Remove 'baseUrl', handling it is beyond scope for now
            if (typeof loaderConfig.baseUrl !== 'undefined') {
                delete loaderConfig['baseUrl'];
            }
            if (typeof loaderConfig.paths !== 'undefined') {
                if (typeof loaderConfig.paths.vs !== 'undefined') {
                    delete loaderConfig.paths['vs'];
                }
            }
            // Since this is in a web worker, enable catching errors
            loaderConfig.catchError = true;
            self.require.config(loaderConfig);
        }
        var cc;
        var ee;
        var r = new winjs_base["b" /* TPromise */](function (c, e, p) {
            cc = c;
            ee = e;
        });
        // Use the global require to be sure to get the global config
        self.require([moduleId], function () {
            var result = [];
            for (var _i = 0; _i < arguments.length; _i++) {
                result[_i] = arguments[_i];
            }
            var handlerModule = result[0];
            _this._requestHandler = handlerModule.create();
            var methods = [];
            for (var prop in _this._requestHandler) {
                if (typeof _this._requestHandler[prop] === 'function') {
                    methods.push(prop);
                }
            }
            cc(methods);
        }, ee);
        return r;
    };
    return SimpleWorkerServer;
}());

/**
 * Called on the worker side
 */
function simpleWorker_create(postMessage) {
    return new simpleWorker_SimpleWorkerServer(postMessage, null);
}

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/editor/editor.worker.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/



var initialized = false;
function initialize(foreignModule) {
    if (initialized) {
        return;
    }
    initialized = true;
    var editorWorker = new editorSimpleWorker_EditorSimpleWorkerImpl(foreignModule);
    var simpleWorker = new simpleWorker_SimpleWorkerServer(function (msg) {
        self.postMessage(msg);
    }, editorWorker);
    self.onmessage = function (e) {
        simpleWorker.onmessage(e.data);
    };
}
self.onmessage = function (e) {
    // Ignore first message in this case and initialize if not yet initialized
    if (!initialized) {
        initialize(null);
    }
};

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-languageserver-types/main.js
/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

/**
 * The Position namespace provides helper functions to work with
 * [Position](#Position) literals.
 */
var main_Position;
(function (Position) {
    /**
     * Creates a new Position literal from the given line and character.
     * @param line The position's line.
     * @param character The position's character.
     */
    function create(line, character) {
        return { line: line, character: character };
    }
    Position.create = create;
    /**
     * Checks whether the given liternal conforms to the [Position](#Position) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && Is.number(candidate.line) && Is.number(candidate.character);
    }
    Position.is = is;
})(main_Position || (main_Position = {}));
/**
 * The Range namespace provides helper functions to work with
 * [Range](#Range) literals.
 */
var main_Range;
(function (Range) {
    function create(one, two, three, four) {
        if (Is.number(one) && Is.number(two) && Is.number(three) && Is.number(four)) {
            return { start: main_Position.create(one, two), end: main_Position.create(three, four) };
        }
        else if (main_Position.is(one) && main_Position.is(two)) {
            return { start: one, end: two };
        }
        else {
            throw new Error("Range#create called with invalid arguments[" + one + ", " + two + ", " + three + ", " + four + "]");
        }
    }
    Range.create = create;
    /**
     * Checks whether the given literal conforms to the [Range](#Range) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && main_Position.is(candidate.start) && main_Position.is(candidate.end);
    }
    Range.is = is;
})(main_Range || (main_Range = {}));
/**
 * The Location namespace provides helper functions to work with
 * [Location](#Location) literals.
 */
var Location;
(function (Location) {
    /**
     * Creates a Location literal.
     * @param uri The location's uri.
     * @param range The location's range.
     */
    function create(uri, range) {
        return { uri: uri, range: range };
    }
    Location.create = create;
    /**
     * Checks whether the given literal conforms to the [Location](#Location) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && main_Range.is(candidate.range) && (Is.string(candidate.uri) || Is.undefined(candidate.uri));
    }
    Location.is = is;
})(Location || (Location = {}));
/**
 * The diagnostic's serverity.
 */
var DiagnosticSeverity;
(function (DiagnosticSeverity) {
    /**
     * Reports an error.
     */
    DiagnosticSeverity.Error = 1;
    /**
     * Reports a warning.
     */
    DiagnosticSeverity.Warning = 2;
    /**
     * Reports an information.
     */
    DiagnosticSeverity.Information = 3;
    /**
     * Reports a hint.
     */
    DiagnosticSeverity.Hint = 4;
})(DiagnosticSeverity || (DiagnosticSeverity = {}));
/**
 * The Diagnostic namespace provides helper functions to work with
 * [Diagnostic](#Diagnostic) literals.
 */
var Diagnostic;
(function (Diagnostic) {
    /**
     * Creates a new Diagnostic literal.
     */
    function create(range, message, severity, code, source) {
        var result = { range: range, message: message };
        if (Is.defined(severity)) {
            result.severity = severity;
        }
        if (Is.defined(code)) {
            result.code = code;
        }
        if (Is.defined(source)) {
            result.source = source;
        }
        return result;
    }
    Diagnostic.create = create;
    /**
     * Checks whether the given literal conforms to the [Diagnostic](#Diagnostic) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate)
            && main_Range.is(candidate.range)
            && Is.string(candidate.message)
            && (Is.number(candidate.severity) || Is.undefined(candidate.severity))
            && (Is.number(candidate.code) || Is.string(candidate.code) || Is.undefined(candidate.code))
            && (Is.string(candidate.source) || Is.undefined(candidate.source));
    }
    Diagnostic.is = is;
})(Diagnostic || (Diagnostic = {}));
/**
 * The Command namespace provides helper functions to work with
 * [Command](#Command) literals.
 */
var Command;
(function (Command) {
    /**
     * Creates a new Command literal.
     */
    function create(title, command) {
        var args = [];
        for (var _i = 2; _i < arguments.length; _i++) {
            args[_i - 2] = arguments[_i];
        }
        var result = { title: title, command: command };
        if (Is.defined(args) && args.length > 0) {
            result.arguments = args;
        }
        return result;
    }
    Command.create = create;
    /**
     * Checks whether the given literal conforms to the [Command](#Command) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && Is.string(candidate.title) && Is.string(candidate.title);
    }
    Command.is = is;
})(Command || (Command = {}));
/**
 * The TextEdit namespace provides helper function to create replace,
 * insert and delete edits more easily.
 */
var TextEdit;
(function (TextEdit) {
    /**
     * Creates a replace text edit.
     * @param range The range of text to be replaced.
     * @param newText The new text.
     */
    function replace(range, newText) {
        return { range: range, newText: newText };
    }
    TextEdit.replace = replace;
    /**
     * Creates a insert text edit.
     * @param position The position to insert the text at.
     * @param newText The text to be inserted.
     */
    function insert(position, newText) {
        return { range: { start: position, end: position }, newText: newText };
    }
    TextEdit.insert = insert;
    /**
     * Creates a delete text edit.
     * @param range The range of text to be deleted.
     */
    function del(range) {
        return { range: range, newText: '' };
    }
    TextEdit.del = del;
})(TextEdit || (TextEdit = {}));
/**
 * The TextDocumentEdit namespace provides helper function to create
 * an edit that manipulates a text document.
 */
var TextDocumentEdit;
(function (TextDocumentEdit) {
    /**
     * Creates a new `TextDocumentEdit`
     */
    function create(textDocument, edits) {
        return { textDocument: textDocument, edits: edits };
    }
    TextDocumentEdit.create = create;
    function is(value) {
        var candidate = value;
        return Is.defined(candidate)
            && VersionedTextDocumentIdentifier.is(candidate.textDocument)
            && Array.isArray(candidate.edits);
    }
    TextDocumentEdit.is = is;
})(TextDocumentEdit || (TextDocumentEdit = {}));
var TextEditChangeImpl = /** @class */ (function () {
    function TextEditChangeImpl(edits) {
        this.edits = edits;
    }
    TextEditChangeImpl.prototype.insert = function (position, newText) {
        this.edits.push(TextEdit.insert(position, newText));
    };
    TextEditChangeImpl.prototype.replace = function (range, newText) {
        this.edits.push(TextEdit.replace(range, newText));
    };
    TextEditChangeImpl.prototype.delete = function (range) {
        this.edits.push(TextEdit.del(range));
    };
    TextEditChangeImpl.prototype.add = function (edit) {
        this.edits.push(edit);
    };
    TextEditChangeImpl.prototype.all = function () {
        return this.edits;
    };
    TextEditChangeImpl.prototype.clear = function () {
        this.edits.splice(0, this.edits.length);
    };
    return TextEditChangeImpl;
}());
/**
 * A workspace change helps constructing changes to a workspace.
 */
var WorkspaceChange = /** @class */ (function () {
    function WorkspaceChange(workspaceEdit) {
        var _this = this;
        this._textEditChanges = Object.create(null);
        if (workspaceEdit) {
            this._workspaceEdit = workspaceEdit;
            if (workspaceEdit.documentChanges) {
                workspaceEdit.documentChanges.forEach(function (textDocumentEdit) {
                    var textEditChange = new TextEditChangeImpl(textDocumentEdit.edits);
                    _this._textEditChanges[textDocumentEdit.textDocument.uri] = textEditChange;
                });
            }
            else if (workspaceEdit.changes) {
                Object.keys(workspaceEdit.changes).forEach(function (key) {
                    var textEditChange = new TextEditChangeImpl(workspaceEdit.changes[key]);
                    _this._textEditChanges[key] = textEditChange;
                });
            }
        }
    }
    Object.defineProperty(WorkspaceChange.prototype, "edit", {
        /**
         * Returns the underlying [WorkspaceEdit](#WorkspaceEdit) literal
         * use to be returned from a workspace edit operation like rename.
         */
        get: function () {
            return this._workspaceEdit;
        },
        enumerable: true,
        configurable: true
    });
    WorkspaceChange.prototype.getTextEditChange = function (key) {
        if (VersionedTextDocumentIdentifier.is(key)) {
            if (!this._workspaceEdit) {
                this._workspaceEdit = {
                    documentChanges: []
                };
            }
            if (!this._workspaceEdit.documentChanges) {
                throw new Error('Workspace edit is not configured for versioned document changes.');
            }
            var textDocument = key;
            var result = this._textEditChanges[textDocument.uri];
            if (!result) {
                var edits = [];
                var textDocumentEdit = {
                    textDocument: textDocument,
                    edits: edits
                };
                this._workspaceEdit.documentChanges.push(textDocumentEdit);
                result = new TextEditChangeImpl(edits);
                this._textEditChanges[textDocument.uri] = result;
            }
            return result;
        }
        else {
            if (!this._workspaceEdit) {
                this._workspaceEdit = {
                    changes: Object.create(null)
                };
            }
            if (!this._workspaceEdit.changes) {
                throw new Error('Workspace edit is not configured for normal text edit changes.');
            }
            var result = this._textEditChanges[key];
            if (!result) {
                var edits = [];
                this._workspaceEdit.changes[key] = edits;
                result = new TextEditChangeImpl(edits);
                this._textEditChanges[key] = result;
            }
            return result;
        }
    };
    return WorkspaceChange;
}());

/**
 * The TextDocumentIdentifier namespace provides helper functions to work with
 * [TextDocumentIdentifier](#TextDocumentIdentifier) literals.
 */
var TextDocumentIdentifier;
(function (TextDocumentIdentifier) {
    /**
     * Creates a new TextDocumentIdentifier literal.
     * @param uri The document's uri.
     */
    function create(uri) {
        return { uri: uri };
    }
    TextDocumentIdentifier.create = create;
    /**
     * Checks whether the given literal conforms to the [TextDocumentIdentifier](#TextDocumentIdentifier) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && Is.string(candidate.uri);
    }
    TextDocumentIdentifier.is = is;
})(TextDocumentIdentifier || (TextDocumentIdentifier = {}));
/**
 * The VersionedTextDocumentIdentifier namespace provides helper functions to work with
 * [VersionedTextDocumentIdentifier](#VersionedTextDocumentIdentifier) literals.
 */
var VersionedTextDocumentIdentifier;
(function (VersionedTextDocumentIdentifier) {
    /**
     * Creates a new VersionedTextDocumentIdentifier literal.
     * @param uri The document's uri.
     * @param uri The document's text.
     */
    function create(uri, version) {
        return { uri: uri, version: version };
    }
    VersionedTextDocumentIdentifier.create = create;
    /**
     * Checks whether the given literal conforms to the [VersionedTextDocumentIdentifier](#VersionedTextDocumentIdentifier) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && Is.string(candidate.uri) && Is.number(candidate.version);
    }
    VersionedTextDocumentIdentifier.is = is;
})(VersionedTextDocumentIdentifier || (VersionedTextDocumentIdentifier = {}));
/**
 * The TextDocumentItem namespace provides helper functions to work with
 * [TextDocumentItem](#TextDocumentItem) literals.
 */
var TextDocumentItem;
(function (TextDocumentItem) {
    /**
     * Creates a new TextDocumentItem literal.
     * @param uri The document's uri.
     * @param languageId The document's language identifier.
     * @param version The document's version number.
     * @param text The document's text.
     */
    function create(uri, languageId, version, text) {
        return { uri: uri, languageId: languageId, version: version, text: text };
    }
    TextDocumentItem.create = create;
    /**
     * Checks whether the given literal conforms to the [TextDocumentItem](#TextDocumentItem) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && Is.string(candidate.uri) && Is.string(candidate.languageId) && Is.number(candidate.version) && Is.string(candidate.text);
    }
    TextDocumentItem.is = is;
})(TextDocumentItem || (TextDocumentItem = {}));
/**
 * Describes the content type that a client supports in various
 * result literals like `Hover`, `ParameterInfo` or `CompletionItem`.
 *
 * Please note that `MarkupKinds` must not start with a `$`. This kinds
 * are reserved for internal usage.
 */
var MarkupKind;
(function (MarkupKind) {
    /**
     * Plain text is supported as a content format
     */
    MarkupKind.PlainText = 'plaintext';
    /**
     * Markdown is supported as a content format
     */
    MarkupKind.Markdown = 'markdown';
})(MarkupKind || (MarkupKind = {}));
/**
 * The kind of a completion entry.
 */
var CompletionItemKind;
(function (CompletionItemKind) {
    CompletionItemKind.Text = 1;
    CompletionItemKind.Method = 2;
    CompletionItemKind.Function = 3;
    CompletionItemKind.Constructor = 4;
    CompletionItemKind.Field = 5;
    CompletionItemKind.Variable = 6;
    CompletionItemKind.Class = 7;
    CompletionItemKind.Interface = 8;
    CompletionItemKind.Module = 9;
    CompletionItemKind.Property = 10;
    CompletionItemKind.Unit = 11;
    CompletionItemKind.Value = 12;
    CompletionItemKind.Enum = 13;
    CompletionItemKind.Keyword = 14;
    CompletionItemKind.Snippet = 15;
    CompletionItemKind.Color = 16;
    CompletionItemKind.File = 17;
    CompletionItemKind.Reference = 18;
    CompletionItemKind.Folder = 19;
    CompletionItemKind.EnumMember = 20;
    CompletionItemKind.Constant = 21;
    CompletionItemKind.Struct = 22;
    CompletionItemKind.Event = 23;
    CompletionItemKind.Operator = 24;
    CompletionItemKind.TypeParameter = 25;
})(CompletionItemKind || (CompletionItemKind = {}));
/**
 * Defines whether the insert text in a completion item should be interpreted as
 * plain text or a snippet.
 */
var InsertTextFormat;
(function (InsertTextFormat) {
    /**
     * The primary text to be inserted is treated as a plain string.
     */
    InsertTextFormat.PlainText = 1;
    /**
     * The primary text to be inserted is treated as a snippet.
     *
     * A snippet can define tab stops and placeholders with `$1`, `$2`
     * and `${3:foo}`. `$0` defines the final tab stop, it defaults to
     * the end of the snippet. Placeholders with equal identifiers are linked,
     * that is typing in one will update others too.
     *
     * See also: https://github.com/Microsoft/vscode/blob/master/src/vs/editor/contrib/snippet/common/snippet.md
     */
    InsertTextFormat.Snippet = 2;
})(InsertTextFormat || (InsertTextFormat = {}));
/**
 * The CompletionItem namespace provides functions to deal with
 * completion items.
 */
var CompletionItem;
(function (CompletionItem) {
    /**
     * Create a completion item and seed it with a label.
     * @param label The completion item's label
     */
    function create(label) {
        return { label: label };
    }
    CompletionItem.create = create;
})(CompletionItem || (CompletionItem = {}));
/**
 * The CompletionList namespace provides functions to deal with
 * completion lists.
 */
var CompletionList;
(function (CompletionList) {
    /**
     * Creates a new completion list.
     *
     * @param items The completion items.
     * @param isIncomplete The list is not complete.
     */
    function create(items, isIncomplete) {
        return { items: items ? items : [], isIncomplete: !!isIncomplete };
    }
    CompletionList.create = create;
})(CompletionList || (CompletionList = {}));
var MarkedString;
(function (MarkedString) {
    /**
     * Creates a marked string from plain text.
     *
     * @param plainText The plain text.
     */
    function fromPlainText(plainText) {
        return plainText.replace(/[\\`*_{}[\]()#+\-.!]/g, "\\$&"); // escape markdown syntax tokens: http://daringfireball.net/projects/markdown/syntax#backslash
    }
    MarkedString.fromPlainText = fromPlainText;
})(MarkedString || (MarkedString = {}));
/**
 * The ParameterInformation namespace provides helper functions to work with
 * [ParameterInformation](#ParameterInformation) literals.
 */
var ParameterInformation;
(function (ParameterInformation) {
    /**
     * Creates a new parameter information literal.
     *
     * @param label A label string.
     * @param documentation A doc string.
     */
    function create(label, documentation) {
        return documentation ? { label: label, documentation: documentation } : { label: label };
    }
    ParameterInformation.create = create;
    ;
})(ParameterInformation || (ParameterInformation = {}));
/**
 * The SignatureInformation namespace provides helper functions to work with
 * [SignatureInformation](#SignatureInformation) literals.
 */
var SignatureInformation;
(function (SignatureInformation) {
    function create(label, documentation) {
        var parameters = [];
        for (var _i = 2; _i < arguments.length; _i++) {
            parameters[_i - 2] = arguments[_i];
        }
        var result = { label: label };
        if (Is.defined(documentation)) {
            result.documentation = documentation;
        }
        if (Is.defined(parameters)) {
            result.parameters = parameters;
        }
        else {
            result.parameters = [];
        }
        return result;
    }
    SignatureInformation.create = create;
})(SignatureInformation || (SignatureInformation = {}));
/**
 * A document highlight kind.
 */
var DocumentHighlightKind;
(function (DocumentHighlightKind) {
    /**
     * A textual occurrance.
     */
    DocumentHighlightKind.Text = 1;
    /**
     * Read-access of a symbol, like reading a variable.
     */
    DocumentHighlightKind.Read = 2;
    /**
     * Write-access of a symbol, like writing to a variable.
     */
    DocumentHighlightKind.Write = 3;
})(DocumentHighlightKind || (DocumentHighlightKind = {}));
/**
 * DocumentHighlight namespace to provide helper functions to work with
 * [DocumentHighlight](#DocumentHighlight) literals.
 */
var DocumentHighlight;
(function (DocumentHighlight) {
    /**
     * Create a DocumentHighlight object.
     * @param range The range the highlight applies to.
     */
    function create(range, kind) {
        var result = { range: range };
        if (Is.number(kind)) {
            result.kind = kind;
        }
        return result;
    }
    DocumentHighlight.create = create;
})(DocumentHighlight || (DocumentHighlight = {}));
/**
 * A symbol kind.
 */
var SymbolKind;
(function (SymbolKind) {
    SymbolKind.File = 1;
    SymbolKind.Module = 2;
    SymbolKind.Namespace = 3;
    SymbolKind.Package = 4;
    SymbolKind.Class = 5;
    SymbolKind.Method = 6;
    SymbolKind.Property = 7;
    SymbolKind.Field = 8;
    SymbolKind.Constructor = 9;
    SymbolKind.Enum = 10;
    SymbolKind.Interface = 11;
    SymbolKind.Function = 12;
    SymbolKind.Variable = 13;
    SymbolKind.Constant = 14;
    SymbolKind.String = 15;
    SymbolKind.Number = 16;
    SymbolKind.Boolean = 17;
    SymbolKind.Array = 18;
    SymbolKind.Object = 19;
    SymbolKind.Key = 20;
    SymbolKind.Null = 21;
    SymbolKind.EnumMember = 22;
    SymbolKind.Struct = 23;
    SymbolKind.Event = 24;
    SymbolKind.Operator = 25;
    SymbolKind.TypeParameter = 26;
})(SymbolKind || (SymbolKind = {}));
var SymbolInformation;
(function (SymbolInformation) {
    /**
     * Creates a new symbol information literal.
     *
     * @param name The name of the symbol.
     * @param kind The kind of the symbol.
     * @param range The range of the location of the symbol.
     * @param uri The resource of the location of symbol, defaults to the current document.
     * @param containerName The name of the symbol containg the symbol.
     */
    function create(name, kind, range, uri, containerName) {
        var result = {
            name: name,
            kind: kind,
            location: { uri: uri, range: range }
        };
        if (containerName) {
            result.containerName = containerName;
        }
        return result;
    }
    SymbolInformation.create = create;
})(SymbolInformation || (SymbolInformation = {}));
/**
 * The CodeActionContext namespace provides helper functions to work with
 * [CodeActionContext](#CodeActionContext) literals.
 */
var CodeActionContext;
(function (CodeActionContext) {
    /**
     * Creates a new CodeActionContext literal.
     */
    function create(diagnostics) {
        return { diagnostics: diagnostics };
    }
    CodeActionContext.create = create;
    /**
     * Checks whether the given literal conforms to the [CodeActionContext](#CodeActionContext) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && Is.typedArray(candidate.diagnostics, Diagnostic.is);
    }
    CodeActionContext.is = is;
})(CodeActionContext || (CodeActionContext = {}));
/**
 * The CodeLens namespace provides helper functions to work with
 * [CodeLens](#CodeLens) literals.
 */
var CodeLens;
(function (CodeLens) {
    /**
     * Creates a new CodeLens literal.
     */
    function create(range, data) {
        var result = { range: range };
        if (Is.defined(data))
            result.data = data;
        return result;
    }
    CodeLens.create = create;
    /**
     * Checks whether the given literal conforms to the [CodeLens](#CodeLens) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && main_Range.is(candidate.range) && (Is.undefined(candidate.command) || Command.is(candidate.command));
    }
    CodeLens.is = is;
})(CodeLens || (CodeLens = {}));
/**
 * The FormattingOptions namespace provides helper functions to work with
 * [FormattingOptions](#FormattingOptions) literals.
 */
var FormattingOptions;
(function (FormattingOptions) {
    /**
     * Creates a new FormattingOptions literal.
     */
    function create(tabSize, insertSpaces) {
        return { tabSize: tabSize, insertSpaces: insertSpaces };
    }
    FormattingOptions.create = create;
    /**
     * Checks whether the given literal conforms to the [FormattingOptions](#FormattingOptions) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && Is.number(candidate.tabSize) && Is.boolean(candidate.insertSpaces);
    }
    FormattingOptions.is = is;
})(FormattingOptions || (FormattingOptions = {}));
/**
 * A document link is a range in a text document that links to an internal or external resource, like another
 * text document or a web site.
 */
var DocumentLink = /** @class */ (function () {
    function DocumentLink() {
    }
    return DocumentLink;
}());

/**
 * The DocumentLink namespace provides helper functions to work with
 * [DocumentLink](#DocumentLink) literals.
 */
(function (DocumentLink) {
    /**
     * Creates a new DocumentLink literal.
     */
    function create(range, target) {
        return { range: range, target: target };
    }
    DocumentLink.create = create;
    /**
     * Checks whether the given literal conforms to the [DocumentLink](#DocumentLink) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && main_Range.is(candidate.range) && (Is.undefined(candidate.target) || Is.string(candidate.target));
    }
    DocumentLink.is = is;
})(DocumentLink || (DocumentLink = {}));
var EOL = ['\n', '\r\n', '\r'];
var TextDocument;
(function (TextDocument) {
    /**
     * Creates a new ITextDocument literal from the given uri and content.
     * @param uri The document's uri.
     * @param languageId  The document's language Id.
     * @param content The document's content.
     */
    function create(uri, languageId, version, content) {
        return new FullTextDocument(uri, languageId, version, content);
    }
    TextDocument.create = create;
    /**
     * Checks whether the given literal conforms to the [ITextDocument](#ITextDocument) interface.
     */
    function is(value) {
        var candidate = value;
        return Is.defined(candidate) && Is.string(candidate.uri) && (Is.undefined(candidate.languageId) || Is.string(candidate.languageId)) && Is.number(candidate.lineCount)
            && Is.func(candidate.getText) && Is.func(candidate.positionAt) && Is.func(candidate.offsetAt) ? true : false;
    }
    TextDocument.is = is;
    function applyEdits(document, edits) {
        var text = document.getText();
        var sortedEdits = mergeSort(edits, function (a, b) {
            var diff = a.range.start.line - b.range.start.line;
            if (diff === 0) {
                return a.range.start.character - b.range.start.character;
            }
            return 0;
        });
        var lastModifiedOffset = text.length;
        for (var i = sortedEdits.length - 1; i >= 0; i--) {
            var e = sortedEdits[i];
            var startOffset = document.offsetAt(e.range.start);
            var endOffset = document.offsetAt(e.range.end);
            if (endOffset <= lastModifiedOffset) {
                text = text.substring(0, startOffset) + e.newText + text.substring(endOffset, text.length);
            }
            else {
                throw new Error('Ovelapping edit');
            }
            lastModifiedOffset = startOffset;
        }
        return text;
    }
    TextDocument.applyEdits = applyEdits;
    function mergeSort(data, compare) {
        if (data.length <= 1) {
            // sorted
            return data;
        }
        var p = (data.length / 2) | 0;
        var left = data.slice(0, p);
        var right = data.slice(p);
        mergeSort(left, compare);
        mergeSort(right, compare);
        var leftIdx = 0;
        var rightIdx = 0;
        var i = 0;
        while (leftIdx < left.length && rightIdx < right.length) {
            var ret = compare(left[leftIdx], right[rightIdx]);
            if (ret <= 0) {
                // smaller_equal -> take left to preserve order
                data[i++] = left[leftIdx++];
            }
            else {
                // greater -> take right
                data[i++] = right[rightIdx++];
            }
        }
        while (leftIdx < left.length) {
            data[i++] = left[leftIdx++];
        }
        while (rightIdx < right.length) {
            data[i++] = right[rightIdx++];
        }
        return data;
    }
})(TextDocument || (TextDocument = {}));
/**
 * Represents reasons why a text document is saved.
 */
var TextDocumentSaveReason;
(function (TextDocumentSaveReason) {
    /**
     * Manually triggered, e.g. by the user pressing save, by starting debugging,
     * or by an API call.
     */
    TextDocumentSaveReason.Manual = 1;
    /**
     * Automatic after a delay.
     */
    TextDocumentSaveReason.AfterDelay = 2;
    /**
     * When the editor lost focus.
     */
    TextDocumentSaveReason.FocusOut = 3;
})(TextDocumentSaveReason || (TextDocumentSaveReason = {}));
var FullTextDocument = /** @class */ (function () {
    function FullTextDocument(uri, languageId, version, content) {
        this._uri = uri;
        this._languageId = languageId;
        this._version = version;
        this._content = content;
        this._lineOffsets = null;
    }
    Object.defineProperty(FullTextDocument.prototype, "uri", {
        get: function () {
            return this._uri;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FullTextDocument.prototype, "languageId", {
        get: function () {
            return this._languageId;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FullTextDocument.prototype, "version", {
        get: function () {
            return this._version;
        },
        enumerable: true,
        configurable: true
    });
    FullTextDocument.prototype.getText = function (range) {
        if (range) {
            var start = this.offsetAt(range.start);
            var end = this.offsetAt(range.end);
            return this._content.substring(start, end);
        }
        return this._content;
    };
    FullTextDocument.prototype.update = function (event, version) {
        this._content = event.text;
        this._version = version;
        this._lineOffsets = null;
    };
    FullTextDocument.prototype.getLineOffsets = function () {
        if (this._lineOffsets === null) {
            var lineOffsets = [];
            var text = this._content;
            var isLineStart = true;
            for (var i = 0; i < text.length; i++) {
                if (isLineStart) {
                    lineOffsets.push(i);
                    isLineStart = false;
                }
                var ch = text.charAt(i);
                isLineStart = (ch === '\r' || ch === '\n');
                if (ch === '\r' && i + 1 < text.length && text.charAt(i + 1) === '\n') {
                    i++;
                }
            }
            if (isLineStart && text.length > 0) {
                lineOffsets.push(text.length);
            }
            this._lineOffsets = lineOffsets;
        }
        return this._lineOffsets;
    };
    FullTextDocument.prototype.positionAt = function (offset) {
        offset = Math.max(Math.min(offset, this._content.length), 0);
        var lineOffsets = this.getLineOffsets();
        var low = 0, high = lineOffsets.length;
        if (high === 0) {
            return main_Position.create(0, offset);
        }
        while (low < high) {
            var mid = Math.floor((low + high) / 2);
            if (lineOffsets[mid] > offset) {
                high = mid;
            }
            else {
                low = mid + 1;
            }
        }
        // low is the least x for which the line offset is larger than the current offset
        // or array.length if no line offset is larger than the current offset
        var line = low - 1;
        return main_Position.create(line, offset - lineOffsets[line]);
    };
    FullTextDocument.prototype.offsetAt = function (position) {
        var lineOffsets = this.getLineOffsets();
        if (position.line >= lineOffsets.length) {
            return this._content.length;
        }
        else if (position.line < 0) {
            return 0;
        }
        var lineOffset = lineOffsets[position.line];
        var nextLineOffset = (position.line + 1 < lineOffsets.length) ? lineOffsets[position.line + 1] : this._content.length;
        return Math.max(Math.min(lineOffset + position.character, nextLineOffset), lineOffset);
    };
    Object.defineProperty(FullTextDocument.prototype, "lineCount", {
        get: function () {
            return this.getLineOffsets().length;
        },
        enumerable: true,
        configurable: true
    });
    return FullTextDocument;
}());
var Is;
(function (Is) {
    var toString = Object.prototype.toString;
    function defined(value) {
        return typeof value !== 'undefined';
    }
    Is.defined = defined;
    function undefined(value) {
        return typeof value === 'undefined';
    }
    Is.undefined = undefined;
    function boolean(value) {
        return value === true || value === false;
    }
    Is.boolean = boolean;
    function string(value) {
        return toString.call(value) === '[object String]';
    }
    Is.string = string;
    function number(value) {
        return toString.call(value) === '[object Number]';
    }
    Is.number = number;
    function func(value) {
        return toString.call(value) === '[object Function]';
    }
    Is.func = func;
    function typedArray(value, check) {
        return Array.isArray(value) && value.every(check);
    }
    Is.typedArray = typedArray;
})(Is || (Is = {}));

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/jsonc-parser/impl/scanner.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

/**
 * Creates a JSON scanner on the given text.
 * If ignoreTrivia is set, whitespaces or comments are ignored.
 */
function createScanner(text, ignoreTrivia) {
    if (ignoreTrivia === void 0) { ignoreTrivia = false; }
    var pos = 0, len = text.length, value = '', tokenOffset = 0, token = 16 /* Unknown */, scanError = 0 /* None */;
    function scanHexDigits(count, exact) {
        var digits = 0;
        var value = 0;
        while (digits < count || !exact) {
            var ch = text.charCodeAt(pos);
            if (ch >= 48 /* _0 */ && ch <= 57 /* _9 */) {
                value = value * 16 + ch - 48 /* _0 */;
            }
            else if (ch >= 65 /* A */ && ch <= 70 /* F */) {
                value = value * 16 + ch - 65 /* A */ + 10;
            }
            else if (ch >= 97 /* a */ && ch <= 102 /* f */) {
                value = value * 16 + ch - 97 /* a */ + 10;
            }
            else {
                break;
            }
            pos++;
            digits++;
        }
        if (digits < count) {
            value = -1;
        }
        return value;
    }
    function setPosition(newPosition) {
        pos = newPosition;
        value = '';
        tokenOffset = 0;
        token = 16 /* Unknown */;
        scanError = 0 /* None */;
    }
    function scanNumber() {
        var start = pos;
        if (text.charCodeAt(pos) === 48 /* _0 */) {
            pos++;
        }
        else {
            pos++;
            while (pos < text.length && isDigit(text.charCodeAt(pos))) {
                pos++;
            }
        }
        if (pos < text.length && text.charCodeAt(pos) === 46 /* dot */) {
            pos++;
            if (pos < text.length && isDigit(text.charCodeAt(pos))) {
                pos++;
                while (pos < text.length && isDigit(text.charCodeAt(pos))) {
                    pos++;
                }
            }
            else {
                scanError = 3 /* UnexpectedEndOfNumber */;
                return text.substring(start, pos);
            }
        }
        var end = pos;
        if (pos < text.length && (text.charCodeAt(pos) === 69 /* E */ || text.charCodeAt(pos) === 101 /* e */)) {
            pos++;
            if (pos < text.length && text.charCodeAt(pos) === 43 /* plus */ || text.charCodeAt(pos) === 45 /* minus */) {
                pos++;
            }
            if (pos < text.length && isDigit(text.charCodeAt(pos))) {
                pos++;
                while (pos < text.length && isDigit(text.charCodeAt(pos))) {
                    pos++;
                }
                end = pos;
            }
            else {
                scanError = 3 /* UnexpectedEndOfNumber */;
            }
        }
        return text.substring(start, end);
    }
    function scanString() {
        var result = '', start = pos;
        while (true) {
            if (pos >= len) {
                result += text.substring(start, pos);
                scanError = 2 /* UnexpectedEndOfString */;
                break;
            }
            var ch = text.charCodeAt(pos);
            if (ch === 34 /* doubleQuote */) {
                result += text.substring(start, pos);
                pos++;
                break;
            }
            if (ch === 92 /* backslash */) {
                result += text.substring(start, pos);
                pos++;
                if (pos >= len) {
                    scanError = 2 /* UnexpectedEndOfString */;
                    break;
                }
                ch = text.charCodeAt(pos++);
                switch (ch) {
                    case 34 /* doubleQuote */:
                        result += '\"';
                        break;
                    case 92 /* backslash */:
                        result += '\\';
                        break;
                    case 47 /* slash */:
                        result += '/';
                        break;
                    case 98 /* b */:
                        result += '\b';
                        break;
                    case 102 /* f */:
                        result += '\f';
                        break;
                    case 110 /* n */:
                        result += '\n';
                        break;
                    case 114 /* r */:
                        result += '\r';
                        break;
                    case 116 /* t */:
                        result += '\t';
                        break;
                    case 117 /* u */:
                        var ch_1 = scanHexDigits(4, true);
                        if (ch_1 >= 0) {
                            result += String.fromCharCode(ch_1);
                        }
                        else {
                            scanError = 4 /* InvalidUnicode */;
                        }
                        break;
                    default:
                        scanError = 5 /* InvalidEscapeCharacter */;
                }
                start = pos;
                continue;
            }
            if (ch >= 0 && ch <= 0x1f) {
                if (isLineBreak(ch)) {
                    result += text.substring(start, pos);
                    scanError = 2 /* UnexpectedEndOfString */;
                    break;
                }
                else {
                    scanError = 6 /* InvalidCharacter */;
                    // mark as error but continue with string
                }
            }
            pos++;
        }
        return result;
    }
    function scanNext() {
        value = '';
        scanError = 0 /* None */;
        tokenOffset = pos;
        if (pos >= len) {
            // at the end
            tokenOffset = len;
            return token = 17 /* EOF */;
        }
        var code = text.charCodeAt(pos);
        // trivia: whitespace
        if (isWhiteSpace(code)) {
            do {
                pos++;
                value += String.fromCharCode(code);
                code = text.charCodeAt(pos);
            } while (isWhiteSpace(code));
            return token = 15 /* Trivia */;
        }
        // trivia: newlines
        if (isLineBreak(code)) {
            pos++;
            value += String.fromCharCode(code);
            if (code === 13 /* carriageReturn */ && text.charCodeAt(pos) === 10 /* lineFeed */) {
                pos++;
                value += '\n';
            }
            return token = 14 /* LineBreakTrivia */;
        }
        switch (code) {
            // tokens: []{}:,
            case 123 /* openBrace */:
                pos++;
                return token = 1 /* OpenBraceToken */;
            case 125 /* closeBrace */:
                pos++;
                return token = 2 /* CloseBraceToken */;
            case 91 /* openBracket */:
                pos++;
                return token = 3 /* OpenBracketToken */;
            case 93 /* closeBracket */:
                pos++;
                return token = 4 /* CloseBracketToken */;
            case 58 /* colon */:
                pos++;
                return token = 6 /* ColonToken */;
            case 44 /* comma */:
                pos++;
                return token = 5 /* CommaToken */;
            // strings
            case 34 /* doubleQuote */:
                pos++;
                value = scanString();
                return token = 10 /* StringLiteral */;
            // comments
            case 47 /* slash */:
                var start = pos - 1;
                // Single-line comment
                if (text.charCodeAt(pos + 1) === 47 /* slash */) {
                    pos += 2;
                    while (pos < len) {
                        if (isLineBreak(text.charCodeAt(pos))) {
                            break;
                        }
                        pos++;
                    }
                    value = text.substring(start, pos);
                    return token = 12 /* LineCommentTrivia */;
                }
                // Multi-line comment
                if (text.charCodeAt(pos + 1) === 42 /* asterisk */) {
                    pos += 2;
                    var commentClosed = false;
                    while (pos < len) {
                        var ch = text.charCodeAt(pos);
                        if (ch === 42 /* asterisk */ && (pos + 1 < len) && text.charCodeAt(pos + 1) === 47 /* slash */) {
                            pos += 2;
                            commentClosed = true;
                            break;
                        }
                        pos++;
                    }
                    if (!commentClosed) {
                        pos++;
                        scanError = 1 /* UnexpectedEndOfComment */;
                    }
                    value = text.substring(start, pos);
                    return token = 13 /* BlockCommentTrivia */;
                }
                // just a single slash
                value += String.fromCharCode(code);
                pos++;
                return token = 16 /* Unknown */;
            // numbers
            case 45 /* minus */:
                value += String.fromCharCode(code);
                pos++;
                if (pos === len || !isDigit(text.charCodeAt(pos))) {
                    return token = 16 /* Unknown */;
                }
            // found a minus, followed by a number so
            // we fall through to proceed with scanning
            // numbers
            case 48 /* _0 */:
            case 49 /* _1 */:
            case 50 /* _2 */:
            case 51 /* _3 */:
            case 52 /* _4 */:
            case 53 /* _5 */:
            case 54 /* _6 */:
            case 55 /* _7 */:
            case 56 /* _8 */:
            case 57 /* _9 */:
                value += scanNumber();
                return token = 11 /* NumericLiteral */;
            // literals and unknown symbols
            default:
                // is a literal? Read the full word.
                while (pos < len && isUnknownContentCharacter(code)) {
                    pos++;
                    code = text.charCodeAt(pos);
                }
                if (tokenOffset !== pos) {
                    value = text.substring(tokenOffset, pos);
                    // keywords: true, false, null
                    switch (value) {
                        case 'true': return token = 8 /* TrueKeyword */;
                        case 'false': return token = 9 /* FalseKeyword */;
                        case 'null': return token = 7 /* NullKeyword */;
                    }
                    return token = 16 /* Unknown */;
                }
                // some
                value += String.fromCharCode(code);
                pos++;
                return token = 16 /* Unknown */;
        }
    }
    function isUnknownContentCharacter(code) {
        if (isWhiteSpace(code) || isLineBreak(code)) {
            return false;
        }
        switch (code) {
            case 125 /* closeBrace */:
            case 93 /* closeBracket */:
            case 123 /* openBrace */:
            case 91 /* openBracket */:
            case 34 /* doubleQuote */:
            case 58 /* colon */:
            case 44 /* comma */:
                return false;
        }
        return true;
    }
    function scanNextNonTrivia() {
        var result;
        do {
            result = scanNext();
        } while (result >= 12 /* LineCommentTrivia */ && result <= 15 /* Trivia */);
        return result;
    }
    return {
        setPosition: setPosition,
        getPosition: function () { return pos; },
        scan: ignoreTrivia ? scanNextNonTrivia : scanNext,
        getToken: function () { return token; },
        getTokenValue: function () { return value; },
        getTokenOffset: function () { return tokenOffset; },
        getTokenLength: function () { return pos - tokenOffset; },
        getTokenError: function () { return scanError; }
    };
}
function isWhiteSpace(ch) {
    return ch === 32 /* space */ || ch === 9 /* tab */ || ch === 11 /* verticalTab */ || ch === 12 /* formFeed */ ||
        ch === 160 /* nonBreakingSpace */ || ch === 5760 /* ogham */ || ch >= 8192 /* enQuad */ && ch <= 8203 /* zeroWidthSpace */ ||
        ch === 8239 /* narrowNoBreakSpace */ || ch === 8287 /* mathematicalSpace */ || ch === 12288 /* ideographicSpace */ || ch === 65279 /* byteOrderMark */;
}
function isLineBreak(ch) {
    return ch === 10 /* lineFeed */ || ch === 13 /* carriageReturn */ || ch === 8232 /* lineSeparator */ || ch === 8233 /* paragraphSeparator */;
}
function isDigit(ch) {
    return ch >= 48 /* _0 */ && ch <= 57 /* _9 */;
}
//# sourceMappingURL=scanner.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/jsonc-parser/impl/format.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


function format_format(documentText, range, options) {
    var initialIndentLevel;
    var formatText;
    var formatTextStart;
    var rangeStart;
    var rangeEnd;
    if (range) {
        rangeStart = range.offset;
        rangeEnd = rangeStart + range.length;
        formatTextStart = rangeStart;
        while (formatTextStart > 0 && !isEOL(documentText, formatTextStart - 1)) {
            formatTextStart--;
        }
        var endOffset = rangeEnd;
        while (endOffset < documentText.length && !isEOL(documentText, endOffset)) {
            endOffset++;
        }
        formatText = documentText.substring(formatTextStart, endOffset);
        initialIndentLevel = computeIndentLevel(formatText, 0, options);
    }
    else {
        formatText = documentText;
        initialIndentLevel = 0;
        formatTextStart = 0;
        rangeStart = 0;
        rangeEnd = documentText.length;
    }
    var eol = getEOL(options, documentText);
    var lineBreak = false;
    var indentLevel = 0;
    var indentValue;
    if (options.insertSpaces) {
        indentValue = format_repeat(' ', options.tabSize || 4);
    }
    else {
        indentValue = '\t';
    }
    var scanner = createScanner(formatText, false);
    var hasError = false;
    function newLineAndIndent() {
        return eol + format_repeat(indentValue, initialIndentLevel + indentLevel);
    }
    function scanNext() {
        var token = scanner.scan();
        lineBreak = false;
        while (token === 15 /* Trivia */ || token === 14 /* LineBreakTrivia */) {
            lineBreak = lineBreak || (token === 14 /* LineBreakTrivia */);
            token = scanner.scan();
        }
        hasError = token === 16 /* Unknown */ || scanner.getTokenError() !== 0 /* None */;
        return token;
    }
    var editOperations = [];
    function addEdit(text, startOffset, endOffset) {
        if (!hasError && startOffset < rangeEnd && endOffset > rangeStart && documentText.substring(startOffset, endOffset) !== text) {
            editOperations.push({ offset: startOffset, length: endOffset - startOffset, content: text });
        }
    }
    var firstToken = scanNext();
    if (firstToken !== 17 /* EOF */) {
        var firstTokenStart = scanner.getTokenOffset() + formatTextStart;
        var initialIndent = format_repeat(indentValue, initialIndentLevel);
        addEdit(initialIndent, formatTextStart, firstTokenStart);
    }
    while (firstToken !== 17 /* EOF */) {
        var firstTokenEnd = scanner.getTokenOffset() + scanner.getTokenLength() + formatTextStart;
        var secondToken = scanNext();
        var replaceContent = '';
        while (!lineBreak && (secondToken === 12 /* LineCommentTrivia */ || secondToken === 13 /* BlockCommentTrivia */)) {
            // comments on the same line: keep them on the same line, but ignore them otherwise
            var commentTokenStart = scanner.getTokenOffset() + formatTextStart;
            addEdit(' ', firstTokenEnd, commentTokenStart);
            firstTokenEnd = scanner.getTokenOffset() + scanner.getTokenLength() + formatTextStart;
            replaceContent = secondToken === 12 /* LineCommentTrivia */ ? newLineAndIndent() : '';
            secondToken = scanNext();
        }
        if (secondToken === 2 /* CloseBraceToken */) {
            if (firstToken !== 1 /* OpenBraceToken */) {
                indentLevel--;
                replaceContent = newLineAndIndent();
            }
        }
        else if (secondToken === 4 /* CloseBracketToken */) {
            if (firstToken !== 3 /* OpenBracketToken */) {
                indentLevel--;
                replaceContent = newLineAndIndent();
            }
        }
        else {
            switch (firstToken) {
                case 3 /* OpenBracketToken */:
                case 1 /* OpenBraceToken */:
                    indentLevel++;
                    replaceContent = newLineAndIndent();
                    break;
                case 5 /* CommaToken */:
                case 12 /* LineCommentTrivia */:
                    replaceContent = newLineAndIndent();
                    break;
                case 13 /* BlockCommentTrivia */:
                    if (lineBreak) {
                        replaceContent = newLineAndIndent();
                    }
                    else {
                        // symbol following comment on the same line: keep on same line, separate with ' '
                        replaceContent = ' ';
                    }
                    break;
                case 6 /* ColonToken */:
                    replaceContent = ' ';
                    break;
                case 10 /* StringLiteral */:
                    if (secondToken === 6 /* ColonToken */) {
                        replaceContent = '';
                        break;
                    }
                // fall through
                case 7 /* NullKeyword */:
                case 8 /* TrueKeyword */:
                case 9 /* FalseKeyword */:
                case 11 /* NumericLiteral */:
                case 2 /* CloseBraceToken */:
                case 4 /* CloseBracketToken */:
                    if (secondToken === 12 /* LineCommentTrivia */ || secondToken === 13 /* BlockCommentTrivia */) {
                        replaceContent = ' ';
                    }
                    else if (secondToken !== 5 /* CommaToken */ && secondToken !== 17 /* EOF */) {
                        hasError = true;
                    }
                    break;
                case 16 /* Unknown */:
                    hasError = true;
                    break;
            }
            if (lineBreak && (secondToken === 12 /* LineCommentTrivia */ || secondToken === 13 /* BlockCommentTrivia */)) {
                replaceContent = newLineAndIndent();
            }
        }
        var secondTokenStart = scanner.getTokenOffset() + formatTextStart;
        addEdit(replaceContent, firstTokenEnd, secondTokenStart);
        firstToken = secondToken;
    }
    return editOperations;
}
function format_repeat(s, count) {
    var result = '';
    for (var i = 0; i < count; i++) {
        result += s;
    }
    return result;
}
function computeIndentLevel(content, offset, options) {
    var i = 0;
    var nChars = 0;
    var tabSize = options.tabSize || 4;
    while (i < content.length) {
        var ch = content.charAt(i);
        if (ch === ' ') {
            nChars++;
        }
        else if (ch === '\t') {
            nChars += tabSize;
        }
        else {
            break;
        }
        i++;
    }
    return Math.floor(nChars / tabSize);
}
function getEOL(options, text) {
    for (var i = 0; i < text.length; i++) {
        var ch = text.charAt(i);
        if (ch === '\r') {
            if (i + 1 < text.length && text.charAt(i + 1) === '\n') {
                return '\r\n';
            }
            return '\r';
        }
        else if (ch === '\n') {
            return '\n';
        }
    }
    return (options && options.eol) || '\n';
}
function isEOL(text, offset) {
    return '\r\n'.indexOf(text.charAt(offset)) !== -1;
}
//# sourceMappingURL=format.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/jsonc-parser/impl/parser.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


/**
 * For a given offset, evaluate the location in the JSON document. Each segment in the location path is either a property name or an array index.
 */
function getLocation(text, position) {
    var segments = []; // strings or numbers
    var earlyReturnException = new Object();
    var previousNode = void 0;
    var previousNodeInst = {
        value: {},
        offset: 0,
        length: 0,
        type: 'object'
    };
    var isAtPropertyKey = false;
    function setPreviousNode(value, offset, length, type) {
        previousNodeInst.value = value;
        previousNodeInst.offset = offset;
        previousNodeInst.length = length;
        previousNodeInst.type = type;
        previousNodeInst.columnOffset = void 0;
        previousNode = previousNodeInst;
    }
    try {
        visit(text, {
            onObjectBegin: function (offset, length) {
                if (position <= offset) {
                    throw earlyReturnException;
                }
                previousNode = void 0;
                isAtPropertyKey = position > offset;
                segments.push(''); // push a placeholder (will be replaced)
            },
            onObjectProperty: function (name, offset, length) {
                if (position < offset) {
                    throw earlyReturnException;
                }
                setPreviousNode(name, offset, length, 'property');
                segments[segments.length - 1] = name;
                if (position <= offset + length) {
                    throw earlyReturnException;
                }
            },
            onObjectEnd: function (offset, length) {
                if (position <= offset) {
                    throw earlyReturnException;
                }
                previousNode = void 0;
                segments.pop();
            },
            onArrayBegin: function (offset, length) {
                if (position <= offset) {
                    throw earlyReturnException;
                }
                previousNode = void 0;
                segments.push(0);
            },
            onArrayEnd: function (offset, length) {
                if (position <= offset) {
                    throw earlyReturnException;
                }
                previousNode = void 0;
                segments.pop();
            },
            onLiteralValue: function (value, offset, length) {
                if (position < offset) {
                    throw earlyReturnException;
                }
                setPreviousNode(value, offset, length, getLiteralNodeType(value));
                if (position <= offset + length) {
                    throw earlyReturnException;
                }
            },
            onSeparator: function (sep, offset, length) {
                if (position <= offset) {
                    throw earlyReturnException;
                }
                if (sep === ':' && previousNode && previousNode.type === 'property') {
                    previousNode.columnOffset = offset;
                    isAtPropertyKey = false;
                    previousNode = void 0;
                }
                else if (sep === ',') {
                    var last = segments[segments.length - 1];
                    if (typeof last === 'number') {
                        segments[segments.length - 1] = last + 1;
                    }
                    else {
                        isAtPropertyKey = true;
                        segments[segments.length - 1] = '';
                    }
                    previousNode = void 0;
                }
            }
        });
    }
    catch (e) {
        if (e !== earlyReturnException) {
            throw e;
        }
    }
    return {
        path: segments,
        previousNode: previousNode,
        isAtPropertyKey: isAtPropertyKey,
        matches: function (pattern) {
            var k = 0;
            for (var i = 0; k < pattern.length && i < segments.length; i++) {
                if (pattern[k] === segments[i] || pattern[k] === '*') {
                    k++;
                }
                else if (pattern[k] !== '**') {
                    return false;
                }
            }
            return k === pattern.length;
        }
    };
}
/**
 * Parses the given text and returns the object the JSON content represents. On invalid input, the parser tries to be as fault tolerant as possible, but still return a result.
 * Therefore always check the errors list to find out if the input was valid.
 */
function parse(text, errors, options) {
    if (errors === void 0) { errors = []; }
    var currentProperty = null;
    var currentParent = [];
    var previousParents = [];
    function onValue(value) {
        if (Array.isArray(currentParent)) {
            currentParent.push(value);
        }
        else if (currentProperty) {
            currentParent[currentProperty] = value;
        }
    }
    var visitor = {
        onObjectBegin: function () {
            var object = {};
            onValue(object);
            previousParents.push(currentParent);
            currentParent = object;
            currentProperty = null;
        },
        onObjectProperty: function (name) {
            currentProperty = name;
        },
        onObjectEnd: function () {
            currentParent = previousParents.pop();
        },
        onArrayBegin: function () {
            var array = [];
            onValue(array);
            previousParents.push(currentParent);
            currentParent = array;
            currentProperty = null;
        },
        onArrayEnd: function () {
            currentParent = previousParents.pop();
        },
        onLiteralValue: onValue,
        onError: function (error, offset, length) {
            errors.push({ error: error, offset: offset, length: length });
        }
    };
    visit(text, visitor, options);
    return currentParent[0];
}
/**
 * Parses the given text and returns a tree representation the JSON content. On invalid input, the parser tries to be as fault tolerant as possible, but still return a result.
 */
function parseTree(text, errors, options) {
    if (errors === void 0) { errors = []; }
    var currentParent = { type: 'array', offset: -1, length: -1, children: [] }; // artificial root
    function ensurePropertyComplete(endOffset) {
        if (currentParent.type === 'property') {
            currentParent.length = endOffset - currentParent.offset;
            currentParent = currentParent.parent;
        }
    }
    function onValue(valueNode) {
        currentParent.children.push(valueNode);
        return valueNode;
    }
    var visitor = {
        onObjectBegin: function (offset) {
            currentParent = onValue({ type: 'object', offset: offset, length: -1, parent: currentParent, children: [] });
        },
        onObjectProperty: function (name, offset, length) {
            currentParent = onValue({ type: 'property', offset: offset, length: -1, parent: currentParent, children: [] });
            currentParent.children.push({ type: 'string', value: name, offset: offset, length: length, parent: currentParent });
        },
        onObjectEnd: function (offset, length) {
            currentParent.length = offset + length - currentParent.offset;
            currentParent = currentParent.parent;
            ensurePropertyComplete(offset + length);
        },
        onArrayBegin: function (offset, length) {
            currentParent = onValue({ type: 'array', offset: offset, length: -1, parent: currentParent, children: [] });
        },
        onArrayEnd: function (offset, length) {
            currentParent.length = offset + length - currentParent.offset;
            currentParent = currentParent.parent;
            ensurePropertyComplete(offset + length);
        },
        onLiteralValue: function (value, offset, length) {
            onValue({ type: getLiteralNodeType(value), offset: offset, length: length, parent: currentParent, value: value });
            ensurePropertyComplete(offset + length);
        },
        onSeparator: function (sep, offset, length) {
            if (currentParent.type === 'property') {
                if (sep === ':') {
                    currentParent.columnOffset = offset;
                }
                else if (sep === ',') {
                    ensurePropertyComplete(offset);
                }
            }
        },
        onError: function (error, offset, length) {
            errors.push({ error: error, offset: offset, length: length });
        }
    };
    visit(text, visitor, options);
    var result = currentParent.children[0];
    if (result) {
        delete result.parent;
    }
    return result;
}
/**
 * Finds the node at the given path in a JSON DOM.
 */
function findNodeAtLocation(root, path) {
    if (!root) {
        return void 0;
    }
    var node = root;
    for (var _i = 0, path_1 = path; _i < path_1.length; _i++) {
        var segment = path_1[_i];
        if (typeof segment === 'string') {
            if (node.type !== 'object' || !Array.isArray(node.children)) {
                return void 0;
            }
            var found = false;
            for (var _a = 0, _b = node.children; _a < _b.length; _a++) {
                var propertyNode = _b[_a];
                if (Array.isArray(propertyNode.children) && propertyNode.children[0].value === segment) {
                    node = propertyNode.children[1];
                    found = true;
                    break;
                }
            }
            if (!found) {
                return void 0;
            }
        }
        else {
            var index = segment;
            if (node.type !== 'array' || index < 0 || !Array.isArray(node.children) || index >= node.children.length) {
                return void 0;
            }
            node = node.children[index];
        }
    }
    return node;
}
/**
 * Evaluates the JavaScript object of the given JSON DOM node
 */
function getNodeValue(node) {
    if (node.type === 'array') {
        return node.children.map(getNodeValue);
    }
    else if (node.type === 'object') {
        var obj = Object.create(null);
        for (var _i = 0, _a = node.children; _i < _a.length; _i++) {
            var prop = _a[_i];
            obj[prop.children[0].value] = getNodeValue(prop.children[1]);
        }
        return obj;
    }
    return node.value;
}
/**
 * Parses the given text and invokes the visitor functions for each object, array and literal reached.
 */
function visit(text, visitor, options) {
    var _scanner = createScanner(text, false);
    function toNoArgVisit(visitFunction) {
        return visitFunction ? function () { return visitFunction(_scanner.getTokenOffset(), _scanner.getTokenLength()); } : function () { return true; };
    }
    function toOneArgVisit(visitFunction) {
        return visitFunction ? function (arg) { return visitFunction(arg, _scanner.getTokenOffset(), _scanner.getTokenLength()); } : function () { return true; };
    }
    var onObjectBegin = toNoArgVisit(visitor.onObjectBegin), onObjectProperty = toOneArgVisit(visitor.onObjectProperty), onObjectEnd = toNoArgVisit(visitor.onObjectEnd), onArrayBegin = toNoArgVisit(visitor.onArrayBegin), onArrayEnd = toNoArgVisit(visitor.onArrayEnd), onLiteralValue = toOneArgVisit(visitor.onLiteralValue), onSeparator = toOneArgVisit(visitor.onSeparator), onComment = toNoArgVisit(visitor.onComment), onError = toOneArgVisit(visitor.onError);
    var disallowComments = options && options.disallowComments;
    var allowTrailingComma = options && options.allowTrailingComma;
    function scanNext() {
        while (true) {
            var token = _scanner.scan();
            switch (_scanner.getTokenError()) {
                case 4 /* InvalidUnicode */:
                    handleError(14 /* InvalidUnicode */);
                    break;
                case 5 /* InvalidEscapeCharacter */:
                    handleError(15 /* InvalidEscapeCharacter */);
                    break;
                case 3 /* UnexpectedEndOfNumber */:
                    handleError(13 /* UnexpectedEndOfNumber */);
                    break;
                case 1 /* UnexpectedEndOfComment */:
                    if (!disallowComments) {
                        handleError(11 /* UnexpectedEndOfComment */);
                    }
                    break;
                case 2 /* UnexpectedEndOfString */:
                    handleError(12 /* UnexpectedEndOfString */);
                    break;
                case 6 /* InvalidCharacter */:
                    handleError(16 /* InvalidCharacter */);
                    break;
            }
            switch (token) {
                case 12 /* LineCommentTrivia */:
                case 13 /* BlockCommentTrivia */:
                    if (disallowComments) {
                        handleError(10 /* InvalidCommentToken */);
                    }
                    else {
                        onComment();
                    }
                    break;
                case 16 /* Unknown */:
                    handleError(1 /* InvalidSymbol */);
                    break;
                case 15 /* Trivia */:
                case 14 /* LineBreakTrivia */:
                    break;
                default:
                    return token;
            }
        }
    }
    function handleError(error, skipUntilAfter, skipUntil) {
        if (skipUntilAfter === void 0) { skipUntilAfter = []; }
        if (skipUntil === void 0) { skipUntil = []; }
        onError(error);
        if (skipUntilAfter.length + skipUntil.length > 0) {
            var token = _scanner.getToken();
            while (token !== 17 /* EOF */) {
                if (skipUntilAfter.indexOf(token) !== -1) {
                    scanNext();
                    break;
                }
                else if (skipUntil.indexOf(token) !== -1) {
                    break;
                }
                token = scanNext();
            }
        }
    }
    function parseString(isValue) {
        var value = _scanner.getTokenValue();
        if (isValue) {
            onLiteralValue(value);
        }
        else {
            onObjectProperty(value);
        }
        scanNext();
        return true;
    }
    function parseLiteral() {
        switch (_scanner.getToken()) {
            case 11 /* NumericLiteral */:
                var value = 0;
                try {
                    value = JSON.parse(_scanner.getTokenValue());
                    if (typeof value !== 'number') {
                        handleError(2 /* InvalidNumberFormat */);
                        value = 0;
                    }
                }
                catch (e) {
                    handleError(2 /* InvalidNumberFormat */);
                }
                onLiteralValue(value);
                break;
            case 7 /* NullKeyword */:
                onLiteralValue(null);
                break;
            case 8 /* TrueKeyword */:
                onLiteralValue(true);
                break;
            case 9 /* FalseKeyword */:
                onLiteralValue(false);
                break;
            default:
                return false;
        }
        scanNext();
        return true;
    }
    function parseProperty() {
        if (_scanner.getToken() !== 10 /* StringLiteral */) {
            handleError(3 /* PropertyNameExpected */, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
            return false;
        }
        parseString(false);
        if (_scanner.getToken() === 6 /* ColonToken */) {
            onSeparator(':');
            scanNext(); // consume colon
            if (!parseValue()) {
                handleError(4 /* ValueExpected */, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
            }
        }
        else {
            handleError(5 /* ColonExpected */, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
        }
        return true;
    }
    function parseObject() {
        onObjectBegin();
        scanNext(); // consume open brace
        var needsComma = false;
        while (_scanner.getToken() !== 2 /* CloseBraceToken */ && _scanner.getToken() !== 17 /* EOF */) {
            if (_scanner.getToken() === 5 /* CommaToken */) {
                if (!needsComma) {
                    handleError(4 /* ValueExpected */, [], []);
                }
                onSeparator(',');
                scanNext(); // consume comma
                if (_scanner.getToken() === 2 /* CloseBraceToken */ && allowTrailingComma) {
                    break;
                }
            }
            else if (needsComma) {
                handleError(6 /* CommaExpected */, [], []);
            }
            if (!parseProperty()) {
                handleError(4 /* ValueExpected */, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
            }
            needsComma = true;
        }
        onObjectEnd();
        if (_scanner.getToken() !== 2 /* CloseBraceToken */) {
            handleError(7 /* CloseBraceExpected */, [2 /* CloseBraceToken */], []);
        }
        else {
            scanNext(); // consume close brace
        }
        return true;
    }
    function parseArray() {
        onArrayBegin();
        scanNext(); // consume open bracket
        var needsComma = false;
        while (_scanner.getToken() !== 4 /* CloseBracketToken */ && _scanner.getToken() !== 17 /* EOF */) {
            if (_scanner.getToken() === 5 /* CommaToken */) {
                if (!needsComma) {
                    handleError(4 /* ValueExpected */, [], []);
                }
                onSeparator(',');
                scanNext(); // consume comma
                if (_scanner.getToken() === 4 /* CloseBracketToken */ && allowTrailingComma) {
                    break;
                }
            }
            else if (needsComma) {
                handleError(6 /* CommaExpected */, [], []);
            }
            if (!parseValue()) {
                handleError(4 /* ValueExpected */, [], [4 /* CloseBracketToken */, 5 /* CommaToken */]);
            }
            needsComma = true;
        }
        onArrayEnd();
        if (_scanner.getToken() !== 4 /* CloseBracketToken */) {
            handleError(8 /* CloseBracketExpected */, [4 /* CloseBracketToken */], []);
        }
        else {
            scanNext(); // consume close bracket
        }
        return true;
    }
    function parseValue() {
        switch (_scanner.getToken()) {
            case 3 /* OpenBracketToken */:
                return parseArray();
            case 1 /* OpenBraceToken */:
                return parseObject();
            case 10 /* StringLiteral */:
                return parseString(true);
            default:
                return parseLiteral();
        }
    }
    scanNext();
    if (_scanner.getToken() === 17 /* EOF */) {
        return true;
    }
    if (!parseValue()) {
        handleError(4 /* ValueExpected */, [], []);
        return false;
    }
    if (_scanner.getToken() !== 17 /* EOF */) {
        handleError(9 /* EndOfFileExpected */, [], []);
    }
    return true;
}
/**
 * Takes JSON with JavaScript-style comments and remove
 * them. Optionally replaces every none-newline character
 * of comments with a replaceCharacter
 */
function stripComments(text, replaceCh) {
    var _scanner = createScanner(text), parts = [], kind, offset = 0, pos;
    do {
        pos = _scanner.getPosition();
        kind = _scanner.scan();
        switch (kind) {
            case 12 /* LineCommentTrivia */:
            case 13 /* BlockCommentTrivia */:
            case 17 /* EOF */:
                if (offset !== pos) {
                    parts.push(text.substring(offset, pos));
                }
                if (replaceCh !== void 0) {
                    parts.push(_scanner.getTokenValue().replace(/[^\r\n]/g, replaceCh));
                }
                offset = _scanner.getPosition();
                break;
        }
    } while (kind !== 17 /* EOF */);
    return parts.join('');
}
function getLiteralNodeType(value) {
    switch (typeof value) {
        case 'boolean': return 'boolean';
        case 'number': return 'number';
        case 'string': return 'string';
        default: return 'null';
    }
}
//# sourceMappingURL=parser.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/jsonc-parser/impl/edit.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/



function removeProperty(text, path, formattingOptions) {
    return setProperty(text, path, void 0, formattingOptions);
}
function setProperty(text, path, value, formattingOptions, getInsertionIndex) {
    var errors = [];
    var root = parseTree(text, errors);
    var parent = void 0;
    var lastSegment = void 0;
    while (path.length > 0) {
        lastSegment = path.pop();
        parent = findNodeAtLocation(root, path);
        if (parent === void 0 && value !== void 0) {
            if (typeof lastSegment === 'string') {
                value = (_a = {}, _a[lastSegment] = value, _a);
            }
            else {
                value = [value];
            }
        }
        else {
            break;
        }
    }
    if (!parent) {
        // empty document
        if (value === void 0) {
            throw new Error('Can not delete in empty document');
        }
        return withFormatting(text, { offset: root ? root.offset : 0, length: root ? root.length : 0, content: JSON.stringify(value) }, formattingOptions);
    }
    else if (parent.type === 'object' && typeof lastSegment === 'string' && Array.isArray(parent.children)) {
        var existing = findNodeAtLocation(parent, [lastSegment]);
        if (existing !== void 0) {
            if (value === void 0) {
                if (!existing.parent) {
                    throw new Error('Malformed AST');
                }
                var propertyIndex = parent.children.indexOf(existing.parent);
                var removeBegin = void 0;
                var removeEnd = existing.parent.offset + existing.parent.length;
                if (propertyIndex > 0) {
                    // remove the comma of the previous node
                    var previous = parent.children[propertyIndex - 1];
                    removeBegin = previous.offset + previous.length;
                }
                else {
                    removeBegin = parent.offset + 1;
                    if (parent.children.length > 1) {
                        // remove the comma of the next node
                        var next = parent.children[1];
                        removeEnd = next.offset;
                    }
                }
                return withFormatting(text, { offset: removeBegin, length: removeEnd - removeBegin, content: '' }, formattingOptions);
            }
            else {
                // set value of existing property
                return withFormatting(text, { offset: existing.offset, length: existing.length, content: JSON.stringify(value) }, formattingOptions);
            }
        }
        else {
            if (value === void 0) {
                return []; // property does not exist, nothing to do
            }
            var newProperty = JSON.stringify(lastSegment) + ": " + JSON.stringify(value);
            var index = getInsertionIndex ? getInsertionIndex(parent.children.map(function (p) { return p.children[0].value; })) : parent.children.length;
            var edit = void 0;
            if (index > 0) {
                var previous = parent.children[index - 1];
                edit = { offset: previous.offset + previous.length, length: 0, content: ',' + newProperty };
            }
            else if (parent.children.length === 0) {
                edit = { offset: parent.offset + 1, length: 0, content: newProperty };
            }
            else {
                edit = { offset: parent.offset + 1, length: 0, content: newProperty + ',' };
            }
            return withFormatting(text, edit, formattingOptions);
        }
    }
    else if (parent.type === 'array' && typeof lastSegment === 'number' && Array.isArray(parent.children)) {
        var insertIndex = lastSegment;
        if (insertIndex === -1) {
            // Insert
            var newProperty = "" + JSON.stringify(value);
            var edit = void 0;
            if (parent.children.length === 0) {
                edit = { offset: parent.offset + 1, length: 0, content: newProperty };
            }
            else {
                var previous = parent.children[parent.children.length - 1];
                edit = { offset: previous.offset + previous.length, length: 0, content: ',' + newProperty };
            }
            return withFormatting(text, edit, formattingOptions);
        }
        else {
            if (value === void 0 && parent.children.length >= 0) {
                //Removal
                var removalIndex = lastSegment;
                var toRemove = parent.children[removalIndex];
                var edit = void 0;
                if (parent.children.length === 1) {
                    // only item
                    edit = { offset: parent.offset + 1, length: parent.length - 2, content: '' };
                }
                else if (parent.children.length - 1 === removalIndex) {
                    // last item
                    var previous = parent.children[removalIndex - 1];
                    var offset = previous.offset + previous.length;
                    var parentEndOffset = parent.offset + parent.length;
                    edit = { offset: offset, length: parentEndOffset - 2 - offset, content: '' };
                }
                else {
                    edit = { offset: toRemove.offset, length: parent.children[removalIndex + 1].offset - toRemove.offset, content: '' };
                }
                return withFormatting(text, edit, formattingOptions);
            }
            else {
                throw new Error('Array modification not supported yet');
            }
        }
    }
    else {
        throw new Error("Can not add " + (typeof lastSegment !== 'number' ? 'index' : 'property') + " to parent of type " + parent.type);
    }
    var _a;
}
function withFormatting(text, edit, formattingOptions) {
    // apply the edit
    var newText = applyEdit(text, edit);
    // format the new text
    var begin = edit.offset;
    var end = edit.offset + edit.content.length;
    if (edit.length === 0 || edit.content.length === 0) {
        while (begin > 0 && !isEOL(newText, begin - 1)) {
            begin--;
        }
        while (end < newText.length && !isEOL(newText, end)) {
            end++;
        }
    }
    var edits = format_format(newText, { offset: begin, length: end - begin }, formattingOptions);
    // apply the formatting edits and track the begin and end offsets of the changes
    for (var i = edits.length - 1; i >= 0; i--) {
        var edit_1 = edits[i];
        newText = applyEdit(newText, edit_1);
        begin = Math.min(begin, edit_1.offset);
        end = Math.max(end, edit_1.offset + edit_1.length);
        end += edit_1.content.length - edit_1.length;
    }
    // create a single edit with all changes
    var editLength = text.length - (newText.length - end) - begin;
    return [{ offset: begin, length: editLength, content: newText.substring(begin, end) }];
}
function applyEdit(text, edit) {
    return text.substring(0, edit.offset) + edit.content + text.substring(edit.offset + edit.length);
}
function isWS(text, offset) {
    return '\r\n \t'.indexOf(text.charAt(offset)) !== -1;
}
//# sourceMappingURL=edit.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/jsonc-parser/main.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/





/**
 * Creates a JSON scanner on the given text.
 * If ignoreTrivia is set, whitespaces or comments are ignored.
 */
var main_createScanner = createScanner;
/**
 * For a given offset, evaluate the location in the JSON document. Each segment in the location path is either a property name or an array index.
 */
var main_getLocation = getLocation;
/**
 * Parses the given text and returns the object the JSON content represents. On invalid input, the parser tries to be as fault tolerant as possible, but still return a result.
 * Therefore always check the errors list to find out if the input was valid.
 */
var main_parse = parse;
/**
 * Parses the given text and returns a tree representation the JSON content. On invalid input, the parser tries to be as fault tolerant as possible, but still return a result.
 */
var main_parseTree = parseTree;
/**
 * Finds the node at the given path in a JSON DOM.
 */
var main_findNodeAtLocation = findNodeAtLocation;
/**
 * Evaluates the JavaScript object of the given JSON DOM node
 */
var main_getNodeValue = getNodeValue;
/**
 * Parses the given text and invokes the visitor functions for each object, array and literal reached.
 */
var main_visit = visit;
/**
 * Takes JSON with JavaScript-style comments and remove
 * them. Optionally replaces every none-newline character
 * of comments with a replaceCharacter
 */
var main_stripComments = stripComments;
/**
 * Computes the edits needed to format a JSON document.
 *
 * @param documentText The input text
 * @param range The range to format or `undefined` to format the full content
 * @param options The formatting options
 * @returns A list of edit operations describing the formatting changes to the original document. Edits can be either inserts, replacements or
 * removals of text segments. All offsets refer to the original state of the document. No two edits must change or remove the same range of
 * text in the original document. However, multiple edits can have
 * the same offset, for example multiple inserts, or an insert followed by a remove or replace. The order in the array defines which edit is applied first.
 * To apply edits to an input, you can use `applyEdits`
 */
function main_format(documentText, range, options) {
    return format_format(documentText, range, options);
}
/**
 * Computes the edits needed to modify a value in the JSON document.
 *
 * @param documentText The input text
 * @param path The path of the value to change. The path represents either to the document root, a property or an array item.
 * If the path points to an non-existing property or item, it will be created.
 * @param value The new value for the specified property or item. If the value is undefined,
 * the property or item will be removed.
 * @param options Options
 * @returns A list of edit operations describing the formatting changes to the original document. Edits can be either inserts, replacements or
 * removals of text segments. All offsets refer to the original state of the document. No two edits must change or remove the same range of
 * text in the original document. However, multiple edits can have
 * the same offset, for example multiple inserts, or an insert followed by a remove or replace. The order in the array defines which edit is applied first.
 * To apply edits to an input, you can use `applyEdits`
 */
function modify(text, path, value, options) {
    return setProperty(text, path, value, options.formattingOptions, options.getInsertionIndex);
}
/**
 * Applies edits to a input string.
 */
function applyEdits(text, edits) {
    for (var i = edits.length - 1; i >= 0; i--) {
        text = applyEdit(text, edits[i]);
    }
    return text;
}
//# sourceMappingURL=main.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/utils/objects.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

function equals(one, other) {
    if (one === other) {
        return true;
    }
    if (one === null || one === undefined || other === null || other === undefined) {
        return false;
    }
    if (typeof one !== typeof other) {
        return false;
    }
    if (typeof one !== 'object') {
        return false;
    }
    if ((Array.isArray(one)) !== (Array.isArray(other))) {
        return false;
    }
    var i, key;
    if (Array.isArray(one)) {
        if (one.length !== other.length) {
            return false;
        }
        for (i = 0; i < one.length; i++) {
            if (!equals(one[i], other[i])) {
                return false;
            }
        }
    }
    else {
        var oneKeys = [];
        for (key in one) {
            oneKeys.push(key);
        }
        oneKeys.sort();
        var otherKeys = [];
        for (key in other) {
            otherKeys.push(key);
        }
        otherKeys.sort();
        if (!equals(oneKeys, otherKeys)) {
            return false;
        }
        for (i = 0; i < oneKeys.length; i++) {
            if (!equals(one[oneKeys[i]], other[oneKeys[i]])) {
                return false;
            }
        }
    }
    return true;
}
//# sourceMappingURL=objects.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/fillers/vscode-nls.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
function vscode_nls_format(message, args) {
    var result;
    if (args.length === 0) {
        result = message;
    }
    else {
        result = message.replace(/\{(\d+)\}/g, function (match, rest) {
            var index = rest[0];
            return typeof args[index] !== 'undefined' ? args[index] : match;
        });
    }
    return result;
}
function localize(key, message) {
    var args = [];
    for (var _i = 2; _i < arguments.length; _i++) {
        args[_i - 2] = arguments[_i];
    }
    return vscode_nls_format(message, args);
}
function loadMessageBundle(file) {
    return localize;
}
function vscode_nls_config(opt) {
    return loadMessageBundle;
}

// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-uri/index.js
var vscode_uri = __webpack_require__("xiP/");

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/parser/jsonParser.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var jsonParser_extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();





var jsonParser_localize = loadMessageBundle();
var ErrorCode;
(function (ErrorCode) {
    ErrorCode[ErrorCode["Undefined"] = 0] = "Undefined";
    ErrorCode[ErrorCode["EnumValueMismatch"] = 1] = "EnumValueMismatch";
    ErrorCode[ErrorCode["UnexpectedEndOfComment"] = 257] = "UnexpectedEndOfComment";
    ErrorCode[ErrorCode["UnexpectedEndOfString"] = 258] = "UnexpectedEndOfString";
    ErrorCode[ErrorCode["UnexpectedEndOfNumber"] = 259] = "UnexpectedEndOfNumber";
    ErrorCode[ErrorCode["InvalidUnicode"] = 260] = "InvalidUnicode";
    ErrorCode[ErrorCode["InvalidEscapeCharacter"] = 261] = "InvalidEscapeCharacter";
    ErrorCode[ErrorCode["InvalidCharacter"] = 262] = "InvalidCharacter";
    ErrorCode[ErrorCode["PropertyExpected"] = 513] = "PropertyExpected";
    ErrorCode[ErrorCode["CommaExpected"] = 514] = "CommaExpected";
    ErrorCode[ErrorCode["ColonExpected"] = 515] = "ColonExpected";
    ErrorCode[ErrorCode["ValueExpected"] = 516] = "ValueExpected";
    ErrorCode[ErrorCode["CommaOrCloseBacketExpected"] = 517] = "CommaOrCloseBacketExpected";
    ErrorCode[ErrorCode["CommaOrCloseBraceExpected"] = 518] = "CommaOrCloseBraceExpected";
    ErrorCode[ErrorCode["TrailingComma"] = 519] = "TrailingComma";
})(ErrorCode || (ErrorCode = {}));
var colorHexPattern = /^#([0-9A-Fa-f]{3,4}|([0-9A-Fa-f]{2}){3,4})$/;
var emailPattern = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
var ProblemSeverity;
(function (ProblemSeverity) {
    ProblemSeverity["Ignore"] = "ignore";
    ProblemSeverity["Error"] = "error";
    ProblemSeverity["Warning"] = "warning";
})(ProblemSeverity || (ProblemSeverity = {}));
var ASTNodeImpl = /** @class */ (function () {
    function ASTNodeImpl(parent, offset, length) {
        this.offset = offset;
        this.length = length;
        this.parent = parent;
    }
    Object.defineProperty(ASTNodeImpl.prototype, "children", {
        get: function () {
            return [];
        },
        enumerable: true,
        configurable: true
    });
    ASTNodeImpl.prototype.toString = function () {
        return 'type: ' + this.type + ' (' + this.offset + '/' + this.length + ')' + (this.parent ? ' parent: {' + this.parent.toString() + '}' : '');
    };
    return ASTNodeImpl;
}());

var NullASTNodeImpl = /** @class */ (function (_super) {
    jsonParser_extends(NullASTNodeImpl, _super);
    function NullASTNodeImpl(parent, offset) {
        var _this = _super.call(this, parent, offset) || this;
        _this.type = 'null';
        return _this;
    }
    return NullASTNodeImpl;
}(ASTNodeImpl));

var BooleanASTNodeImpl = /** @class */ (function (_super) {
    jsonParser_extends(BooleanASTNodeImpl, _super);
    function BooleanASTNodeImpl(parent, boolValue, offset) {
        var _this = _super.call(this, parent, offset) || this;
        _this.type = 'boolean';
        _this.value = boolValue;
        return _this;
    }
    return BooleanASTNodeImpl;
}(ASTNodeImpl));

var ArrayASTNodeImpl = /** @class */ (function (_super) {
    jsonParser_extends(ArrayASTNodeImpl, _super);
    function ArrayASTNodeImpl(parent, offset) {
        var _this = _super.call(this, parent, offset) || this;
        _this.type = 'array';
        _this.items = [];
        return _this;
    }
    Object.defineProperty(ArrayASTNodeImpl.prototype, "children", {
        get: function () {
            return this.items;
        },
        enumerable: true,
        configurable: true
    });
    return ArrayASTNodeImpl;
}(ASTNodeImpl));

var NumberASTNodeImpl = /** @class */ (function (_super) {
    jsonParser_extends(NumberASTNodeImpl, _super);
    function NumberASTNodeImpl(parent, offset) {
        var _this = _super.call(this, parent, offset) || this;
        _this.type = 'number';
        _this.isInteger = true;
        _this.value = Number.NaN;
        return _this;
    }
    return NumberASTNodeImpl;
}(ASTNodeImpl));

var StringASTNodeImpl = /** @class */ (function (_super) {
    jsonParser_extends(StringASTNodeImpl, _super);
    function StringASTNodeImpl(parent, offset, length) {
        var _this = _super.call(this, parent, offset, length) || this;
        _this.type = 'string';
        _this.value = '';
        return _this;
    }
    return StringASTNodeImpl;
}(ASTNodeImpl));

var PropertyASTNodeImpl = /** @class */ (function (_super) {
    jsonParser_extends(PropertyASTNodeImpl, _super);
    function PropertyASTNodeImpl(parent, offset) {
        var _this = _super.call(this, parent, offset) || this;
        _this.type = 'property';
        _this.colonOffset = -1;
        return _this;
    }
    Object.defineProperty(PropertyASTNodeImpl.prototype, "children", {
        get: function () {
            return this.valueNode ? [this.keyNode, this.valueNode] : [this.keyNode];
        },
        enumerable: true,
        configurable: true
    });
    return PropertyASTNodeImpl;
}(ASTNodeImpl));

var ObjectASTNodeImpl = /** @class */ (function (_super) {
    jsonParser_extends(ObjectASTNodeImpl, _super);
    function ObjectASTNodeImpl(parent, offset) {
        var _this = _super.call(this, parent, offset) || this;
        _this.type = 'object';
        _this.properties = [];
        return _this;
    }
    Object.defineProperty(ObjectASTNodeImpl.prototype, "children", {
        get: function () {
            return this.properties;
        },
        enumerable: true,
        configurable: true
    });
    return ObjectASTNodeImpl;
}(ASTNodeImpl));

function asSchema(schema) {
    if (typeof schema === 'boolean') {
        return schema ? {} : { "not": {} };
    }
    return schema;
}
var EnumMatch;
(function (EnumMatch) {
    EnumMatch[EnumMatch["Key"] = 0] = "Key";
    EnumMatch[EnumMatch["Enum"] = 1] = "Enum";
})(EnumMatch || (EnumMatch = {}));
var SchemaCollector = /** @class */ (function () {
    function SchemaCollector(focusOffset, exclude) {
        if (focusOffset === void 0) { focusOffset = -1; }
        if (exclude === void 0) { exclude = null; }
        this.focusOffset = focusOffset;
        this.exclude = exclude;
        this.schemas = [];
    }
    SchemaCollector.prototype.add = function (schema) {
        this.schemas.push(schema);
    };
    SchemaCollector.prototype.merge = function (other) {
        (_a = this.schemas).push.apply(_a, other.schemas);
        var _a;
    };
    SchemaCollector.prototype.include = function (node) {
        return (this.focusOffset === -1 || contains(node, this.focusOffset)) && (node !== this.exclude);
    };
    SchemaCollector.prototype.newSub = function () {
        return new SchemaCollector(-1, this.exclude);
    };
    return SchemaCollector;
}());
var NoOpSchemaCollector = /** @class */ (function () {
    function NoOpSchemaCollector() {
    }
    Object.defineProperty(NoOpSchemaCollector.prototype, "schemas", {
        get: function () { return []; },
        enumerable: true,
        configurable: true
    });
    NoOpSchemaCollector.prototype.add = function (schema) { };
    NoOpSchemaCollector.prototype.merge = function (other) { };
    NoOpSchemaCollector.prototype.include = function (node) { return true; };
    NoOpSchemaCollector.prototype.newSub = function () { return this; };
    NoOpSchemaCollector.instance = new NoOpSchemaCollector();
    return NoOpSchemaCollector;
}());
var ValidationResult = /** @class */ (function () {
    function ValidationResult() {
        this.problems = [];
        this.propertiesMatches = 0;
        this.propertiesValueMatches = 0;
        this.primaryValueMatches = 0;
        this.enumValueMatch = false;
        this.enumValues = null;
    }
    ValidationResult.prototype.hasProblems = function () {
        return !!this.problems.length;
    };
    ValidationResult.prototype.mergeAll = function (validationResults) {
        var _this = this;
        validationResults.forEach(function (validationResult) {
            _this.merge(validationResult);
        });
    };
    ValidationResult.prototype.merge = function (validationResult) {
        this.problems = this.problems.concat(validationResult.problems);
    };
    ValidationResult.prototype.mergeEnumValues = function (validationResult) {
        if (!this.enumValueMatch && !validationResult.enumValueMatch && this.enumValues && validationResult.enumValues) {
            this.enumValues = this.enumValues.concat(validationResult.enumValues);
            for (var _i = 0, _a = this.problems; _i < _a.length; _i++) {
                var error = _a[_i];
                if (error.code === ErrorCode.EnumValueMismatch) {
                    error.message = jsonParser_localize('enumWarning', 'Value is not accepted. Valid values: {0}.', this.enumValues.map(function (v) { return JSON.stringify(v); }).join(', '));
                }
            }
        }
    };
    ValidationResult.prototype.mergePropertyMatch = function (propertyValidationResult) {
        this.merge(propertyValidationResult);
        this.propertiesMatches++;
        if (propertyValidationResult.enumValueMatch || !propertyValidationResult.hasProblems() && propertyValidationResult.propertiesMatches) {
            this.propertiesValueMatches++;
        }
        if (propertyValidationResult.enumValueMatch && propertyValidationResult.enumValues && propertyValidationResult.enumValues.length === 1) {
            this.primaryValueMatches++;
        }
    };
    ValidationResult.prototype.compare = function (other) {
        var hasProblems = this.hasProblems();
        if (hasProblems !== other.hasProblems()) {
            return hasProblems ? -1 : 1;
        }
        if (this.enumValueMatch !== other.enumValueMatch) {
            return other.enumValueMatch ? -1 : 1;
        }
        if (this.primaryValueMatches !== other.primaryValueMatches) {
            return this.primaryValueMatches - other.primaryValueMatches;
        }
        if (this.propertiesValueMatches !== other.propertiesValueMatches) {
            return this.propertiesValueMatches - other.propertiesValueMatches;
        }
        return this.propertiesMatches - other.propertiesMatches;
    };
    return ValidationResult;
}());

function toProblemSeverity(diagnosticsSeverity) {
    switch (diagnosticsSeverity) {
        case DiagnosticSeverity.Error: return ProblemSeverity.Error;
        case DiagnosticSeverity.Warning: return ProblemSeverity.Warning;
        case DiagnosticSeverity.Information: return ProblemSeverity.Warning;
    }
    return ProblemSeverity.Ignore;
}
function newJSONDocument(root, diagnostics) {
    if (diagnostics === void 0) { diagnostics = []; }
    return new JSONDocument(root, [], [], diagnostics);
}
function jsonParser_getNodeValue(node) {
    switch (node.type) {
        case 'array':
            return node.items.map(jsonParser_getNodeValue);
        case 'object':
            var obj = Object.create(null);
            for (var _i = 0, _a = node.properties; _i < _a.length; _i++) {
                var prop = _a[_i];
                obj[prop.keyNode.value] = jsonParser_getNodeValue(prop.valueNode);
            }
            return obj;
        case 'string':
        case 'number':
        case 'boolean':
            return node.value;
    }
    return null;
}
function getNodePath(node) {
    if (!node.parent) {
        return [];
    }
    var path = getNodePath(node.parent);
    if (node.parent.type === 'property') {
        var key = node.parent.keyNode.value;
        path.push(key);
    }
    else if (node.parent.type === 'array') {
        var index = node.parent.items.indexOf(node);
        if (index !== -1) {
            path.push(index);
        }
    }
    return path;
}
function contains(node, offset, includeRightBound) {
    if (includeRightBound === void 0) { includeRightBound = false; }
    return offset >= node.offset && offset < (node.offset + node.length) || includeRightBound && offset === (node.offset + node.length);
}
var JSONDocument = /** @class */ (function () {
    function JSONDocument(root, syntaxErrors, comments, externalDiagnostic) {
        if (syntaxErrors === void 0) { syntaxErrors = []; }
        if (comments === void 0) { comments = []; }
        if (externalDiagnostic === void 0) { externalDiagnostic = []; }
        this.root = root;
        this.syntaxErrors = syntaxErrors;
        this.comments = comments;
        this.externalDiagnostic = externalDiagnostic;
    }
    JSONDocument.prototype.getNodeFromOffset = function (offset) {
        var findNode = function (node) {
            if (offset >= node.offset && offset < (node.offset + node.length)) {
                var children = node.children;
                for (var i = 0; i < children.length && children[i].offset <= offset; i++) {
                    var item = findNode(children[i]);
                    if (item) {
                        return item;
                    }
                }
                return node;
            }
            return null;
        };
        return this.root && findNode(this.root);
    };
    JSONDocument.prototype.getNodeFromOffsetEndInclusive = function (offset) {
        var findNode = function (node) {
            if (offset >= node.offset && offset <= (node.offset + node.length)) {
                var children = node.children;
                for (var i = 0; i < children.length && children[i].offset <= offset; i++) {
                    var item = findNode(children[i]);
                    if (item) {
                        return item;
                    }
                }
                return node;
            }
            return null;
        };
        return this.root && findNode(this.root);
    };
    JSONDocument.prototype.visit = function (visitor) {
        if (this.root) {
            var doVisit_1 = function (node) {
                var ctn = visitor(node);
                var children = node.children;
                for (var i = 0; i < children.length && ctn; i++) {
                    ctn = doVisit_1(children[i]);
                }
                return ctn;
            };
            doVisit_1(this.root);
        }
    };
    JSONDocument.prototype.validate = function (schema) {
        if (this.root && schema) {
            var validationResult = new ValidationResult();
            validate(this.root, schema, validationResult, NoOpSchemaCollector.instance);
            return validationResult.problems;
        }
        return null;
    };
    JSONDocument.prototype.getMatchingSchemas = function (schema, focusOffset, exclude) {
        if (focusOffset === void 0) { focusOffset = -1; }
        if (exclude === void 0) { exclude = null; }
        var matchingSchemas = new SchemaCollector(focusOffset, exclude);
        if (this.root && schema) {
            validate(this.root, schema, new ValidationResult(), matchingSchemas);
        }
        return matchingSchemas.schemas;
    };
    return JSONDocument;
}());

function validate(node, schema, validationResult, matchingSchemas) {
    if (!node || !matchingSchemas.include(node)) {
        return;
    }
    switch (node.type) {
        case 'object':
            _validateObjectNode(node, schema, validationResult, matchingSchemas);
            break;
        case 'array':
            _validateArrayNode(node, schema, validationResult, matchingSchemas);
            break;
        case 'string':
            _validateStringNode(node, schema, validationResult, matchingSchemas);
            break;
        case 'number':
            _validateNumberNode(node, schema, validationResult, matchingSchemas);
            break;
        case 'property':
            return validate(node.valueNode, schema, validationResult, matchingSchemas);
    }
    _validateNode();
    matchingSchemas.add({ node: node, schema: schema });
    function _validateNode() {
        function matchesType(type) {
            return node.type === type || (type === 'integer' && node.type === 'number' && node.isInteger);
        }
        if (Array.isArray(schema.type)) {
            if (!schema.type.some(matchesType)) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: ProblemSeverity.Warning,
                    message: schema.errorMessage || jsonParser_localize('typeArrayMismatchWarning', 'Incorrect type. Expected one of {0}.', schema.type.join(', '))
                });
            }
        }
        else if (schema.type) {
            if (!matchesType(schema.type)) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: ProblemSeverity.Warning,
                    message: schema.errorMessage || jsonParser_localize('typeMismatchWarning', 'Incorrect type. Expected "{0}".', schema.type)
                });
            }
        }
        if (Array.isArray(schema.allOf)) {
            schema.allOf.forEach(function (subSchemaRef) {
                validate(node, asSchema(subSchemaRef), validationResult, matchingSchemas);
            });
        }
        var notSchema = asSchema(schema.not);
        if (notSchema) {
            var subValidationResult = new ValidationResult();
            var subMatchingSchemas = matchingSchemas.newSub();
            validate(node, notSchema, subValidationResult, subMatchingSchemas);
            if (!subValidationResult.hasProblems()) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: ProblemSeverity.Warning,
                    message: jsonParser_localize('notSchemaWarning', "Matches a schema that is not allowed.")
                });
            }
            subMatchingSchemas.schemas.forEach(function (ms) {
                ms.inverted = !ms.inverted;
                matchingSchemas.add(ms);
            });
        }
        var testAlternatives = function (alternatives, maxOneMatch) {
            var matches = [];
            // remember the best match that is used for error messages
            var bestMatch = null;
            alternatives.forEach(function (subSchemaRef) {
                var subSchema = asSchema(subSchemaRef);
                var subValidationResult = new ValidationResult();
                var subMatchingSchemas = matchingSchemas.newSub();
                validate(node, subSchema, subValidationResult, subMatchingSchemas);
                if (!subValidationResult.hasProblems()) {
                    matches.push(subSchema);
                }
                if (!bestMatch) {
                    bestMatch = { schema: subSchema, validationResult: subValidationResult, matchingSchemas: subMatchingSchemas };
                }
                else {
                    if (!maxOneMatch && !subValidationResult.hasProblems() && !bestMatch.validationResult.hasProblems()) {
                        // no errors, both are equally good matches
                        bestMatch.matchingSchemas.merge(subMatchingSchemas);
                        bestMatch.validationResult.propertiesMatches += subValidationResult.propertiesMatches;
                        bestMatch.validationResult.propertiesValueMatches += subValidationResult.propertiesValueMatches;
                    }
                    else {
                        var compareResult = subValidationResult.compare(bestMatch.validationResult);
                        if (compareResult > 0) {
                            // our node is the best matching so far
                            bestMatch = { schema: subSchema, validationResult: subValidationResult, matchingSchemas: subMatchingSchemas };
                        }
                        else if (compareResult === 0) {
                            // there's already a best matching but we are as good
                            bestMatch.matchingSchemas.merge(subMatchingSchemas);
                            bestMatch.validationResult.mergeEnumValues(subValidationResult);
                        }
                    }
                }
            });
            if (matches.length > 1 && maxOneMatch) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: 1 },
                    severity: ProblemSeverity.Warning,
                    message: jsonParser_localize('oneOfWarning', "Matches multiple schemas when only one must validate.")
                });
            }
            if (bestMatch !== null) {
                validationResult.merge(bestMatch.validationResult);
                validationResult.propertiesMatches += bestMatch.validationResult.propertiesMatches;
                validationResult.propertiesValueMatches += bestMatch.validationResult.propertiesValueMatches;
                matchingSchemas.merge(bestMatch.matchingSchemas);
            }
            return matches.length;
        };
        if (Array.isArray(schema.anyOf)) {
            testAlternatives(schema.anyOf, false);
        }
        if (Array.isArray(schema.oneOf)) {
            testAlternatives(schema.oneOf, true);
        }
        if (Array.isArray(schema.enum)) {
            var val = jsonParser_getNodeValue(node);
            var enumValueMatch = false;
            for (var _i = 0, _a = schema.enum; _i < _a.length; _i++) {
                var e = _a[_i];
                if (equals(val, e)) {
                    enumValueMatch = true;
                    break;
                }
            }
            validationResult.enumValues = schema.enum;
            validationResult.enumValueMatch = enumValueMatch;
            if (!enumValueMatch) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: ProblemSeverity.Warning,
                    code: ErrorCode.EnumValueMismatch,
                    message: schema.errorMessage || jsonParser_localize('enumWarning', 'Value is not accepted. Valid values: {0}.', schema.enum.map(function (v) { return JSON.stringify(v); }).join(', '))
                });
            }
        }
        if (schema.const) {
            var val = jsonParser_getNodeValue(node);
            if (!equals(val, schema.const)) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: ProblemSeverity.Warning,
                    code: ErrorCode.EnumValueMismatch,
                    message: schema.errorMessage || jsonParser_localize('constWarning', 'Value must be {0}.', JSON.stringify(schema.const))
                });
                validationResult.enumValueMatch = false;
            }
            else {
                validationResult.enumValueMatch = true;
            }
            validationResult.enumValues = [schema.const];
        }
        if (schema.deprecationMessage && node.parent) {
            validationResult.problems.push({
                location: { offset: node.parent.offset, length: node.parent.length },
                severity: ProblemSeverity.Warning,
                message: schema.deprecationMessage
            });
        }
    }
    function _validateNumberNode(node, schema, validationResult, matchingSchemas) {
        var val = node.value;
        if (typeof schema.multipleOf === 'number') {
            if (val % schema.multipleOf !== 0) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: ProblemSeverity.Warning,
                    message: jsonParser_localize('multipleOfWarning', 'Value is not divisible by {0}.', schema.multipleOf)
                });
            }
        }
        function getExclusiveLimit(limit, exclusive) {
            if (typeof exclusive === 'number') {
                return exclusive;
            }
            if (typeof exclusive === 'boolean' && exclusive) {
                return limit;
            }
            return void 0;
        }
        function getLimit(limit, exclusive) {
            if (typeof exclusive !== 'boolean' || !exclusive) {
                return limit;
            }
            return void 0;
        }
        var exclusiveMinimum = getExclusiveLimit(schema.minimum, schema.exclusiveMinimum);
        if (typeof exclusiveMinimum === 'number' && val <= exclusiveMinimum) {
            validationResult.problems.push({
                location: { offset: node.offset, length: node.length },
                severity: ProblemSeverity.Warning,
                message: jsonParser_localize('exclusiveMinimumWarning', 'Value is below the exclusive minimum of {0}.', exclusiveMinimum)
            });
        }
        var exclusiveMaximum = getExclusiveLimit(schema.maximum, schema.exclusiveMaximum);
        if (typeof exclusiveMaximum === 'number' && val >= exclusiveMaximum) {
            validationResult.problems.push({
                location: { offset: node.offset, length: node.length },
                severity: ProblemSeverity.Warning,
                message: jsonParser_localize('exclusiveMaximumWarning', 'Value is above the exclusive maximum of {0}.', exclusiveMaximum)
            });
        }
        var minimum = getLimit(schema.minimum, schema.exclusiveMinimum);
        if (typeof minimum === 'number' && val < minimum) {
            validationResult.problems.push({
                location: { offset: node.offset, length: node.length },
                severity: ProblemSeverity.Warning,
                message: jsonParser_localize('minimumWarning', 'Value is below the minimum of {0}.', minimum)
            });
        }
        var maximum = getLimit(schema.maximum, schema.exclusiveMaximum);
        if (typeof maximum === 'number' && val > maximum) {
            validationResult.problems.push({
                location: { offset: node.offset, length: node.length },
                severity: ProblemSeverity.Warning,
                message: jsonParser_localize('maximumWarning', 'Value is above the maximum of {0}.', maximum)
            });
        }
    }
    function _validateStringNode(node, schema, validationResult, matchingSchemas) {
        if (schema.minLength && node.value.length < schema.minLength) {
            validationResult.problems.push({
                location: { offset: node.offset, length: node.length },
                severity: ProblemSeverity.Warning,
                message: jsonParser_localize('minLengthWarning', 'String is shorter than the minimum length of {0}.', schema.minLength)
            });
        }
        if (schema.maxLength && node.value.length > schema.maxLength) {
            validationResult.problems.push({
                location: { offset: node.offset, length: node.length },
                severity: ProblemSeverity.Warning,
                message: jsonParser_localize('maxLengthWarning', 'String is longer than the maximum length of {0}.', schema.maxLength)
            });
        }
        if (schema.pattern) {
            var regex = new RegExp(schema.pattern);
            if (!regex.test(node.value)) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: ProblemSeverity.Warning,
                    message: schema.patternErrorMessage || schema.errorMessage || jsonParser_localize('patternWarning', 'String does not match the pattern of "{0}".', schema.pattern)
                });
            }
        }
        if (schema.format) {
            switch (schema.format) {
                case 'uri':
                case 'uri-reference':
                    {
                        var errorMessage = void 0;
                        if (!node.value) {
                            errorMessage = jsonParser_localize('uriEmpty', 'URI expected.');
                        }
                        else {
                            try {
                                var uri = vscode_uri["a" /* default */].parse(node.value);
                                if (!uri.scheme && schema.format === 'uri') {
                                    errorMessage = jsonParser_localize('uriSchemeMissing', 'URI with a scheme is expected.');
                                }
                            }
                            catch (e) {
                                errorMessage = e.message;
                            }
                        }
                        if (errorMessage) {
                            validationResult.problems.push({
                                location: { offset: node.offset, length: node.length },
                                severity: ProblemSeverity.Warning,
                                message: schema.patternErrorMessage || schema.errorMessage || jsonParser_localize('uriFormatWarning', 'String is not a URI: {0}', errorMessage)
                            });
                        }
                    }
                    break;
                case 'email':
                    {
                        if (!node.value.match(emailPattern)) {
                            validationResult.problems.push({
                                location: { offset: node.offset, length: node.length },
                                severity: ProblemSeverity.Warning,
                                message: schema.patternErrorMessage || schema.errorMessage || jsonParser_localize('emailFormatWarning', 'String is not an e-mail address.')
                            });
                        }
                    }
                    break;
                case 'color-hex':
                    {
                        if (!node.value.match(colorHexPattern)) {
                            validationResult.problems.push({
                                location: { offset: node.offset, length: node.length },
                                severity: ProblemSeverity.Warning,
                                message: schema.patternErrorMessage || schema.errorMessage || jsonParser_localize('colorHexFormatWarning', 'Invalid color format. Use #RGB, #RGBA, #RRGGBB or #RRGGBBAA.')
                            });
                        }
                    }
                    break;
                default:
            }
        }
    }
    function _validateArrayNode(node, schema, validationResult, matchingSchemas) {
        if (Array.isArray(schema.items)) {
            var subSchemas_1 = schema.items;
            subSchemas_1.forEach(function (subSchemaRef, index) {
                var subSchema = asSchema(subSchemaRef);
                var itemValidationResult = new ValidationResult();
                var item = node.items[index];
                if (item) {
                    validate(item, subSchema, itemValidationResult, matchingSchemas);
                    validationResult.mergePropertyMatch(itemValidationResult);
                }
                else if (node.items.length >= subSchemas_1.length) {
                    validationResult.propertiesValueMatches++;
                }
            });
            if (node.items.length > subSchemas_1.length) {
                if (typeof schema.additionalItems === 'object') {
                    for (var i = subSchemas_1.length; i < node.items.length; i++) {
                        var itemValidationResult = new ValidationResult();
                        validate(node.items[i], schema.additionalItems, itemValidationResult, matchingSchemas);
                        validationResult.mergePropertyMatch(itemValidationResult);
                    }
                }
                else if (schema.additionalItems === false) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: ProblemSeverity.Warning,
                        message: jsonParser_localize('additionalItemsWarning', 'Array has too many items according to schema. Expected {0} or fewer.', subSchemas_1.length)
                    });
                }
            }
        }
        else {
            var itemSchema_1 = asSchema(schema.items);
            if (itemSchema_1) {
                node.items.forEach(function (item) {
                    var itemValidationResult = new ValidationResult();
                    validate(item, itemSchema_1, itemValidationResult, matchingSchemas);
                    validationResult.mergePropertyMatch(itemValidationResult);
                });
            }
        }
        var containsSchema = asSchema(schema.contains);
        if (containsSchema) {
            var doesContain = node.items.some(function (item) {
                var itemValidationResult = new ValidationResult();
                validate(item, containsSchema, itemValidationResult, NoOpSchemaCollector.instance);
                return !itemValidationResult.hasProblems();
            });
            if (!doesContain) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: ProblemSeverity.Warning,
                    message: schema.errorMessage || jsonParser_localize('requiredItemMissingWarning', 'Array does not contain required item.', schema.minItems)
                });
            }
        }
        if (schema.minItems && node.items.length < schema.minItems) {
            validationResult.problems.push({
                location: { offset: node.offset, length: node.length },
                severity: ProblemSeverity.Warning,
                message: jsonParser_localize('minItemsWarning', 'Array has too few items. Expected {0} or more.', schema.minItems)
            });
        }
        if (schema.maxItems && node.items.length > schema.maxItems) {
            validationResult.problems.push({
                location: { offset: node.offset, length: node.length },
                severity: ProblemSeverity.Warning,
                message: jsonParser_localize('maxItemsWarning', 'Array has too many items. Expected {0} or fewer.', schema.minItems)
            });
        }
        if (schema.uniqueItems === true) {
            var values_1 = jsonParser_getNodeValue(node);
            var duplicates = values_1.some(function (value, index) {
                return index !== values_1.lastIndexOf(value);
            });
            if (duplicates) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: ProblemSeverity.Warning,
                    message: jsonParser_localize('uniqueItemsWarning', 'Array has duplicate items.')
                });
            }
        }
    }
    function _validateObjectNode(node, schema, validationResult, matchingSchemas) {
        var seenKeys = Object.create(null);
        var unprocessedProperties = [];
        node.properties.forEach(function (node) {
            var key = node.keyNode.value;
            seenKeys[key] = node.valueNode;
            unprocessedProperties.push(key);
        });
        if (Array.isArray(schema.required)) {
            schema.required.forEach(function (propertyName) {
                if (!seenKeys[propertyName]) {
                    var keyNode = node.parent && node.parent.type === 'property' && node.parent.keyNode;
                    var location = keyNode ? { offset: keyNode.offset, length: keyNode.length } : { offset: node.offset, length: 1 };
                    validationResult.problems.push({
                        location: location,
                        severity: ProblemSeverity.Warning,
                        message: jsonParser_localize('MissingRequiredPropWarning', 'Missing property "{0}".', propertyName)
                    });
                }
            });
        }
        var propertyProcessed = function (prop) {
            var index = unprocessedProperties.indexOf(prop);
            while (index >= 0) {
                unprocessedProperties.splice(index, 1);
                index = unprocessedProperties.indexOf(prop);
            }
        };
        if (schema.properties) {
            Object.keys(schema.properties).forEach(function (propertyName) {
                propertyProcessed(propertyName);
                var propertySchema = schema.properties[propertyName];
                var child = seenKeys[propertyName];
                if (child) {
                    if (typeof propertySchema === 'boolean') {
                        if (!propertySchema) {
                            var propertyNode = child.parent;
                            validationResult.problems.push({
                                location: { offset: propertyNode.keyNode.offset, length: propertyNode.keyNode.length },
                                severity: ProblemSeverity.Warning,
                                message: schema.errorMessage || jsonParser_localize('DisallowedExtraPropWarning', 'Property {0} is not allowed.', propertyName)
                            });
                        }
                        else {
                            validationResult.propertiesMatches++;
                            validationResult.propertiesValueMatches++;
                        }
                    }
                    else {
                        var propertyValidationResult = new ValidationResult();
                        validate(child, propertySchema, propertyValidationResult, matchingSchemas);
                        validationResult.mergePropertyMatch(propertyValidationResult);
                    }
                }
            });
        }
        if (schema.patternProperties) {
            Object.keys(schema.patternProperties).forEach(function (propertyPattern) {
                var regex = new RegExp(propertyPattern);
                unprocessedProperties.slice(0).forEach(function (propertyName) {
                    if (regex.test(propertyName)) {
                        propertyProcessed(propertyName);
                        var child = seenKeys[propertyName];
                        if (child) {
                            var propertySchema = schema.patternProperties[propertyPattern];
                            if (typeof propertySchema === 'boolean') {
                                if (!propertySchema) {
                                    var propertyNode = child.parent;
                                    validationResult.problems.push({
                                        location: { offset: propertyNode.keyNode.offset, length: propertyNode.keyNode.length },
                                        severity: ProblemSeverity.Warning,
                                        message: schema.errorMessage || jsonParser_localize('DisallowedExtraPropWarning', 'Property {0} is not allowed.', propertyName)
                                    });
                                }
                                else {
                                    validationResult.propertiesMatches++;
                                    validationResult.propertiesValueMatches++;
                                }
                            }
                            else {
                                var propertyValidationResult = new ValidationResult();
                                validate(child, propertySchema, propertyValidationResult, matchingSchemas);
                                validationResult.mergePropertyMatch(propertyValidationResult);
                            }
                        }
                    }
                });
            });
        }
        if (typeof schema.additionalProperties === 'object') {
            unprocessedProperties.forEach(function (propertyName) {
                var child = seenKeys[propertyName];
                if (child) {
                    var propertyValidationResult = new ValidationResult();
                    validate(child, schema.additionalProperties, propertyValidationResult, matchingSchemas);
                    validationResult.mergePropertyMatch(propertyValidationResult);
                }
            });
        }
        else if (schema.additionalProperties === false) {
            if (unprocessedProperties.length > 0) {
                unprocessedProperties.forEach(function (propertyName) {
                    var child = seenKeys[propertyName];
                    if (child) {
                        var propertyNode = child.parent;
                        validationResult.problems.push({
                            location: { offset: propertyNode.keyNode.offset, length: propertyNode.keyNode.length },
                            severity: ProblemSeverity.Warning,
                            message: schema.errorMessage || jsonParser_localize('DisallowedExtraPropWarning', 'Property {0} is not allowed.', propertyName)
                        });
                    }
                });
            }
        }
        if (schema.maxProperties) {
            if (node.properties.length > schema.maxProperties) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: ProblemSeverity.Warning,
                    message: jsonParser_localize('MaxPropWarning', 'Object has more properties than limit of {0}.', schema.maxProperties)
                });
            }
        }
        if (schema.minProperties) {
            if (node.properties.length < schema.minProperties) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: ProblemSeverity.Warning,
                    message: jsonParser_localize('MinPropWarning', 'Object has fewer properties than the required number of {0}', schema.minProperties)
                });
            }
        }
        if (schema.dependencies) {
            Object.keys(schema.dependencies).forEach(function (key) {
                var prop = seenKeys[key];
                if (prop) {
                    var propertyDep = schema.dependencies[key];
                    if (Array.isArray(propertyDep)) {
                        propertyDep.forEach(function (requiredProp) {
                            if (!seenKeys[requiredProp]) {
                                validationResult.problems.push({
                                    location: { offset: node.offset, length: node.length },
                                    severity: ProblemSeverity.Warning,
                                    message: jsonParser_localize('RequiredDependentPropWarning', 'Object is missing property {0} required by property {1}.', requiredProp, key)
                                });
                            }
                            else {
                                validationResult.propertiesValueMatches++;
                            }
                        });
                    }
                    else {
                        var propertySchema = asSchema(propertyDep);
                        if (propertySchema) {
                            var propertyValidationResult = new ValidationResult();
                            validate(node, propertySchema, propertyValidationResult, matchingSchemas);
                            validationResult.mergePropertyMatch(propertyValidationResult);
                        }
                    }
                }
            });
        }
        var propertyNames = asSchema(schema.propertyNames);
        if (propertyNames) {
            node.properties.forEach(function (f) {
                var key = f.keyNode;
                if (key) {
                    validate(key, propertyNames, validationResult, NoOpSchemaCollector.instance);
                }
            });
        }
    }
}
function jsonParser_parse(textDocument, config) {
    var problems = [];
    var text = textDocument.getText();
    var scanner = main_createScanner(text, false);
    var comments = config && config.collectComments ? [] : void 0;
    function _scanNext() {
        while (true) {
            var token_1 = scanner.scan();
            _checkScanError();
            switch (token_1) {
                case 12 /* LineCommentTrivia */:
                case 13 /* BlockCommentTrivia */:
                    if (Array.isArray(comments)) {
                        comments.push({ offset: scanner.getTokenOffset(), length: scanner.getTokenLength() });
                    }
                    break;
                case 15 /* Trivia */:
                case 14 /* LineBreakTrivia */:
                    break;
                default:
                    return token_1;
            }
        }
    }
    function _accept(token) {
        if (scanner.getToken() === token) {
            _scanNext();
            return true;
        }
        return false;
    }
    function _errorAtRange(message, code, location) {
        if (problems.length === 0 || problems[problems.length - 1].location.offset !== location.offset) {
            problems.push({ message: message, location: location, code: code, severity: ProblemSeverity.Error });
        }
    }
    function _error(message, code, node, skipUntilAfter, skipUntil) {
        if (node === void 0) { node = null; }
        if (skipUntilAfter === void 0) { skipUntilAfter = []; }
        if (skipUntil === void 0) { skipUntil = []; }
        var start = scanner.getTokenOffset();
        var end = scanner.getTokenOffset() + scanner.getTokenLength();
        if (start === end && start > 0) {
            start--;
            while (start > 0 && /\s/.test(text.charAt(start))) {
                start--;
            }
            end = start + 1;
        }
        _errorAtRange(message, code, { offset: start, length: end - start });
        if (node) {
            _finalize(node, false);
        }
        if (skipUntilAfter.length + skipUntil.length > 0) {
            var token_2 = scanner.getToken();
            while (token_2 !== 17 /* EOF */) {
                if (skipUntilAfter.indexOf(token_2) !== -1) {
                    _scanNext();
                    break;
                }
                else if (skipUntil.indexOf(token_2) !== -1) {
                    break;
                }
                token_2 = _scanNext();
            }
        }
        return node;
    }
    function _checkScanError() {
        switch (scanner.getTokenError()) {
            case 4 /* InvalidUnicode */:
                _error(jsonParser_localize('InvalidUnicode', 'Invalid unicode sequence in string.'), ErrorCode.InvalidUnicode);
                return true;
            case 5 /* InvalidEscapeCharacter */:
                _error(jsonParser_localize('InvalidEscapeCharacter', 'Invalid escape character in string.'), ErrorCode.InvalidEscapeCharacter);
                return true;
            case 3 /* UnexpectedEndOfNumber */:
                _error(jsonParser_localize('UnexpectedEndOfNumber', 'Unexpected end of number.'), ErrorCode.UnexpectedEndOfNumber);
                return true;
            case 1 /* UnexpectedEndOfComment */:
                _error(jsonParser_localize('UnexpectedEndOfComment', 'Unexpected end of comment.'), ErrorCode.UnexpectedEndOfComment);
                return true;
            case 2 /* UnexpectedEndOfString */:
                _error(jsonParser_localize('UnexpectedEndOfString', 'Unexpected end of string.'), ErrorCode.UnexpectedEndOfString);
                return true;
            case 6 /* InvalidCharacter */:
                _error(jsonParser_localize('InvalidCharacter', 'Invalid characters in string. Control characters must be escaped.'), ErrorCode.InvalidCharacter);
                return true;
        }
        return false;
    }
    function _finalize(node, scanNext) {
        node.length = scanner.getTokenOffset() + scanner.getTokenLength() - node.offset;
        if (scanNext) {
            _scanNext();
        }
        return node;
    }
    function _parseArray(parent) {
        if (scanner.getToken() !== 3 /* OpenBracketToken */) {
            return null;
        }
        var node = new ArrayASTNodeImpl(parent, scanner.getTokenOffset());
        _scanNext(); // consume OpenBracketToken
        var count = 0;
        var needsComma = false;
        while (scanner.getToken() !== 4 /* CloseBracketToken */ && scanner.getToken() !== 17 /* EOF */) {
            if (scanner.getToken() === 5 /* CommaToken */) {
                if (!needsComma) {
                    _error(jsonParser_localize('ValueExpected', 'Value expected'), ErrorCode.ValueExpected);
                }
                var commaOffset = scanner.getTokenOffset();
                _scanNext(); // consume comma
                if (scanner.getToken() === 4 /* CloseBracketToken */) {
                    if (needsComma) {
                        _errorAtRange(jsonParser_localize('TrailingComma', 'Trailing comma'), ErrorCode.TrailingComma, { offset: commaOffset, length: 1 });
                    }
                    continue;
                }
            }
            else if (needsComma) {
                _error(jsonParser_localize('ExpectedComma', 'Expected comma'), ErrorCode.CommaExpected);
            }
            var item = _parseValue(node, count++);
            if (!item) {
                _error(jsonParser_localize('PropertyExpected', 'Value expected'), ErrorCode.ValueExpected, null, [], [4 /* CloseBracketToken */, 5 /* CommaToken */]);
            }
            else {
                node.items.push(item);
            }
            needsComma = true;
        }
        if (scanner.getToken() !== 4 /* CloseBracketToken */) {
            return _error(jsonParser_localize('ExpectedCloseBracket', 'Expected comma or closing bracket'), ErrorCode.CommaOrCloseBacketExpected, node);
        }
        return _finalize(node, true);
    }
    function _parseProperty(parent, keysSeen) {
        var node = new PropertyASTNodeImpl(parent, scanner.getTokenOffset());
        var key = _parseString(node);
        if (!key) {
            if (scanner.getToken() === 16 /* Unknown */) {
                // give a more helpful error message
                _error(jsonParser_localize('DoubleQuotesExpected', 'Property keys must be doublequoted'), ErrorCode.Undefined);
                var keyNode = new StringASTNodeImpl(node, scanner.getTokenOffset(), scanner.getTokenLength());
                keyNode.value = scanner.getTokenValue();
                key = keyNode;
                _scanNext(); // consume Unknown
            }
            else {
                return null;
            }
        }
        node.keyNode = key;
        var seen = keysSeen[key.value];
        if (seen) {
            problems.push({ location: { offset: node.keyNode.offset, length: node.keyNode.length }, message: jsonParser_localize('DuplicateKeyWarning', "Duplicate object key"), code: ErrorCode.Undefined, severity: ProblemSeverity.Warning });
            if (typeof seen === 'object') {
                problems.push({ location: { offset: seen.keyNode.offset, length: seen.keyNode.length }, message: jsonParser_localize('DuplicateKeyWarning', "Duplicate object key"), code: ErrorCode.Undefined, severity: ProblemSeverity.Warning });
            }
            keysSeen[key.value] = true; // if the same key is duplicate again, avoid duplicate error reporting
        }
        else {
            keysSeen[key.value] = node;
        }
        if (scanner.getToken() === 6 /* ColonToken */) {
            node.colonOffset = scanner.getTokenOffset();
            _scanNext(); // consume ColonToken
        }
        else {
            _error(jsonParser_localize('ColonExpected', 'Colon expected'), ErrorCode.ColonExpected);
            if (scanner.getToken() === 10 /* StringLiteral */ && textDocument.positionAt(key.offset + key.length).line < textDocument.positionAt(scanner.getTokenOffset()).line) {
                node.length = key.length;
                return node;
            }
        }
        var value = _parseValue(node, key.value);
        if (!value) {
            return _error(jsonParser_localize('ValueExpected', 'Value expected'), ErrorCode.ValueExpected, node, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
        }
        node.valueNode = value;
        node.length = value.offset + value.length - node.offset;
        return node;
    }
    function _parseObject(parent) {
        if (scanner.getToken() !== 1 /* OpenBraceToken */) {
            return null;
        }
        var node = new ObjectASTNodeImpl(parent, scanner.getTokenOffset());
        var keysSeen = Object.create(null);
        _scanNext(); // consume OpenBraceToken
        var needsComma = false;
        while (scanner.getToken() !== 2 /* CloseBraceToken */ && scanner.getToken() !== 17 /* EOF */) {
            if (scanner.getToken() === 5 /* CommaToken */) {
                if (!needsComma) {
                    _error(jsonParser_localize('PropertyExpected', 'Property expected'), ErrorCode.PropertyExpected);
                }
                var commaOffset = scanner.getTokenOffset();
                _scanNext(); // consume comma
                if (scanner.getToken() === 2 /* CloseBraceToken */) {
                    if (needsComma) {
                        _errorAtRange(jsonParser_localize('TrailingComma', 'Trailing comma'), ErrorCode.TrailingComma, { offset: commaOffset, length: 1 });
                    }
                    continue;
                }
            }
            else if (needsComma) {
                _error(jsonParser_localize('ExpectedComma', 'Expected comma'), ErrorCode.CommaExpected);
            }
            var property = _parseProperty(node, keysSeen);
            if (!property) {
                _error(jsonParser_localize('PropertyExpected', 'Property expected'), ErrorCode.PropertyExpected, null, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
            }
            else {
                node.properties.push(property);
            }
            needsComma = true;
        }
        if (scanner.getToken() !== 2 /* CloseBraceToken */) {
            return _error(jsonParser_localize('ExpectedCloseBrace', 'Expected comma or closing brace'), ErrorCode.CommaOrCloseBraceExpected, node);
        }
        return _finalize(node, true);
    }
    function _parseString(parent) {
        if (scanner.getToken() !== 10 /* StringLiteral */) {
            return null;
        }
        var node = new StringASTNodeImpl(parent, scanner.getTokenOffset());
        node.value = scanner.getTokenValue();
        return _finalize(node, true);
    }
    function _parseNumber(parent) {
        if (scanner.getToken() !== 11 /* NumericLiteral */) {
            return null;
        }
        var node = new NumberASTNodeImpl(parent, scanner.getTokenOffset());
        if (scanner.getTokenError() === 0 /* None */) {
            var tokenValue = scanner.getTokenValue();
            try {
                var numberValue = JSON.parse(tokenValue);
                if (typeof numberValue !== 'number') {
                    return _error(jsonParser_localize('InvalidNumberFormat', 'Invalid number format.'), ErrorCode.Undefined, node);
                }
                node.value = numberValue;
            }
            catch (e) {
                return _error(jsonParser_localize('InvalidNumberFormat', 'Invalid number format.'), ErrorCode.Undefined, node);
            }
            node.isInteger = tokenValue.indexOf('.') === -1;
        }
        return _finalize(node, true);
    }
    function _parseLiteral(parent) {
        var node;
        switch (scanner.getToken()) {
            case 7 /* NullKeyword */:
                return _finalize(new NullASTNodeImpl(parent, scanner.getTokenOffset()), true);
            case 8 /* TrueKeyword */:
                return _finalize(new BooleanASTNodeImpl(parent, true, scanner.getTokenOffset()), true);
            case 9 /* FalseKeyword */:
                return _finalize(new BooleanASTNodeImpl(parent, false, scanner.getTokenOffset()), true);
            default:
                return null;
        }
    }
    function _parseValue(parent, name) {
        return _parseArray(parent) || _parseObject(parent) || _parseString(parent) || _parseNumber(parent) || _parseLiteral(parent);
    }
    var _root = null;
    var token = _scanNext();
    if (token !== 17 /* EOF */) {
        _root = _parseValue(null, null);
        if (!_root) {
            _error(jsonParser_localize('Invalid symbol', 'Expected a JSON object, array or literal.'), ErrorCode.Undefined);
        }
        else if (scanner.getToken() !== 17 /* EOF */) {
            _error(jsonParser_localize('End of file expected', 'End of file expected.'), ErrorCode.Undefined);
        }
    }
    return new JSONDocument(_root, problems, comments);
}
//# sourceMappingURL=jsonParser.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/utils/json.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

function stringifyObject(obj, indent, stringifyLiteral) {
    if (obj !== null && typeof obj === 'object') {
        var newIndent = indent + '\t';
        if (Array.isArray(obj)) {
            if (obj.length === 0) {
                return '[]';
            }
            var result = '[\n';
            for (var i = 0; i < obj.length; i++) {
                result += newIndent + stringifyObject(obj[i], newIndent, stringifyLiteral);
                if (i < obj.length - 1) {
                    result += ',';
                }
                result += '\n';
            }
            result += indent + ']';
            return result;
        }
        else {
            var keys = Object.keys(obj);
            if (keys.length === 0) {
                return '{}';
            }
            var result = '{\n';
            for (var i = 0; i < keys.length; i++) {
                var key = keys[i];
                result += newIndent + JSON.stringify(key) + ': ' + stringifyObject(obj[key], newIndent, stringifyLiteral);
                if (i < keys.length - 1) {
                    result += ',';
                }
                result += '\n';
            }
            result += indent + '}';
            return result;
        }
    }
    return stringifyLiteral(obj);
}
//# sourceMappingURL=json.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/utils/strings.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

function strings_startsWith(haystack, needle) {
    if (haystack.length < needle.length) {
        return false;
    }
    for (var i = 0; i < needle.length; i++) {
        if (haystack[i] !== needle[i]) {
            return false;
        }
    }
    return true;
}
/**
 * Determines if haystack ends with needle.
 */
function strings_endsWith(haystack, needle) {
    var diff = haystack.length - needle.length;
    if (diff > 0) {
        return haystack.lastIndexOf(needle) === diff;
    }
    else if (diff === 0) {
        return haystack === needle;
    }
    else {
        return false;
    }
}
function strings_convertSimple2RegExpPattern(pattern) {
    return pattern.replace(/[\-\\\{\}\+\?\|\^\$\.\,\[\]\(\)\#\s]/g, '\\$&').replace(/[\*]/g, '.*');
}
function strings_repeat(value, count) {
    var s = '';
    while (count > 0) {
        if ((count & 1) === 1) {
            s += value;
        }
        value += value;
        count = count >>> 1;
    }
    return s;
}
//# sourceMappingURL=strings.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/services/jsonCompletion.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/







var jsonCompletion_localize = loadMessageBundle();
var jsonCompletion_JSONCompletion = /** @class */ (function () {
    function JSONCompletion(schemaService, contributions, promiseConstructor) {
        if (contributions === void 0) { contributions = []; }
        this.templateVarIdCounter = 0;
        this.schemaService = schemaService;
        this.contributions = contributions;
        this.promise = promiseConstructor || Promise;
    }
    JSONCompletion.prototype.doResolve = function (item) {
        for (var i = this.contributions.length - 1; i >= 0; i--) {
            if (this.contributions[i].resolveCompletion) {
                var resolver = this.contributions[i].resolveCompletion(item);
                if (resolver) {
                    return resolver;
                }
            }
        }
        return this.promise.resolve(item);
    };
    JSONCompletion.prototype.doComplete = function (document, position, doc) {
        var _this = this;
        var result = {
            items: [],
            isIncomplete: false
        };
        var offset = document.offsetAt(position);
        var node = doc.getNodeFromOffsetEndInclusive(offset);
        if (this.isInComment(document, node ? node.offset : 0, offset)) {
            return Promise.resolve(result);
        }
        var currentWord = this.getCurrentWord(document, offset);
        var overwriteRange = null;
        if (node && (node.type === 'string' || node.type === 'number' || node.type === 'boolean' || node.type === 'null')) {
            overwriteRange = main_Range.create(document.positionAt(node.offset), document.positionAt(node.offset + node.length));
        }
        else {
            var overwriteStart = offset - currentWord.length;
            if (overwriteStart > 0 && document.getText()[overwriteStart - 1] === '"') {
                overwriteStart--;
            }
            overwriteRange = main_Range.create(document.positionAt(overwriteStart), position);
        }
        var proposed = {};
        var collector = {
            add: function (suggestion) {
                var existing = proposed[suggestion.label];
                if (!existing) {
                    proposed[suggestion.label] = suggestion;
                    if (overwriteRange) {
                        suggestion.textEdit = TextEdit.replace(overwriteRange, suggestion.insertText);
                    }
                    result.items.push(suggestion);
                }
                else if (!existing.documentation) {
                    existing.documentation = suggestion.documentation;
                }
            },
            setAsIncomplete: function () {
                result.isIncomplete = true;
            },
            error: function (message) {
                console.error(message);
            },
            log: function (message) {
                console.log(message);
            },
            getNumberOfProposals: function () {
                return result.items.length;
            }
        };
        return this.schemaService.getSchemaForResource(document.uri, doc).then(function (schema) {
            var collectionPromises = [];
            var addValue = true;
            var currentKey = '';
            var currentProperty = null;
            if (node) {
                if (node.type === 'string') {
                    var parent = node.parent;
                    if (parent && parent.type === 'property' && parent.keyNode === node) {
                        addValue = !parent.valueNode;
                        currentProperty = parent;
                        currentKey = document.getText().substr(node.offset + 1, node.length - 2);
                        if (parent) {
                            node = parent.parent;
                        }
                    }
                }
            }
            // proposals for properties
            if (node && node.type === 'object') {
                // don't suggest keys when the cursor is just before the opening curly brace
                if (node.offset === offset) {
                    return result;
                }
                // don't suggest properties that are already present
                var properties = node.properties;
                properties.forEach(function (p) {
                    if (!currentProperty || currentProperty !== p) {
                        proposed[p.keyNode.value] = CompletionItem.create('__');
                    }
                });
                var separatorAfter_1 = '';
                if (addValue) {
                    separatorAfter_1 = _this.evaluateSeparatorAfter(document, document.offsetAt(overwriteRange.end));
                }
                if (schema) {
                    // property proposals with schema
                    _this.getPropertyCompletions(schema, doc, node, addValue, separatorAfter_1, collector);
                }
                else {
                    // property proposals without schema
                    _this.getSchemaLessPropertyCompletions(doc, node, currentKey, collector);
                }
                var location_1 = getNodePath(node);
                _this.contributions.forEach(function (contribution) {
                    var collectPromise = contribution.collectPropertyCompletions(document.uri, location_1, currentWord, addValue, separatorAfter_1 === '', collector);
                    if (collectPromise) {
                        collectionPromises.push(collectPromise);
                    }
                });
                if ((!schema && currentWord.length > 0 && document.getText().charAt(offset - currentWord.length - 1) !== '"')) {
                    collector.add({
                        kind: CompletionItemKind.Property,
                        label: _this.getLabelForValue(currentWord),
                        insertText: _this.getInsertTextForProperty(currentWord, null, false, separatorAfter_1),
                        insertTextFormat: InsertTextFormat.Snippet, documentation: ''
                    });
                }
            }
            // proposals for values
            var types = {};
            if (schema) {
                // value proposals with schema
                _this.getValueCompletions(schema, doc, node, offset, document, collector, types);
            }
            else {
                // value proposals without schema
                _this.getSchemaLessValueCompletions(doc, node, offset, document, collector);
            }
            if (_this.contributions.length > 0) {
                _this.getContributedValueCompletions(doc, node, offset, document, collector, collectionPromises);
            }
            return _this.promise.all(collectionPromises).then(function () {
                if (collector.getNumberOfProposals() === 0) {
                    var offsetForSeparator = offset;
                    if (node && (node.type === 'string' || node.type === 'number' || node.type === 'boolean' || node.type === 'null')) {
                        offsetForSeparator = node.offset + node.length;
                    }
                    var separatorAfter = _this.evaluateSeparatorAfter(document, offsetForSeparator);
                    _this.addFillerValueCompletions(types, separatorAfter, collector);
                }
                return result;
            });
        });
    };
    JSONCompletion.prototype.getPropertyCompletions = function (schema, doc, node, addValue, separatorAfter, collector) {
        var _this = this;
        var matchingSchemas = doc.getMatchingSchemas(schema.schema, node.offset);
        matchingSchemas.forEach(function (s) {
            if (s.node === node && !s.inverted) {
                var schemaProperties_1 = s.schema.properties;
                if (schemaProperties_1) {
                    Object.keys(schemaProperties_1).forEach(function (key) {
                        var propertySchema = schemaProperties_1[key];
                        if (typeof propertySchema === 'object' && !propertySchema.deprecationMessage && !propertySchema.doNotSuggest) {
                            var proposal = {
                                kind: CompletionItemKind.Property,
                                label: key,
                                insertText: _this.getInsertTextForProperty(key, propertySchema, addValue, separatorAfter),
                                insertTextFormat: InsertTextFormat.Snippet,
                                filterText: _this.getFilterTextForValue(key),
                                documentation: propertySchema.description || '',
                            };
                            if (strings_endsWith(proposal.insertText, "$1" + separatorAfter)) {
                                proposal.command = {
                                    title: 'Suggest',
                                    command: 'editor.action.triggerSuggest'
                                };
                            }
                            collector.add(proposal);
                        }
                    });
                }
            }
        });
    };
    JSONCompletion.prototype.getSchemaLessPropertyCompletions = function (doc, node, currentKey, collector) {
        var _this = this;
        var collectCompletionsForSimilarObject = function (obj) {
            obj.properties.forEach(function (p) {
                var key = p.keyNode.value;
                collector.add({
                    kind: CompletionItemKind.Property,
                    label: key,
                    insertText: _this.getInsertTextForValue(key, ''),
                    insertTextFormat: InsertTextFormat.Snippet,
                    filterText: _this.getFilterTextForValue(key),
                    documentation: ''
                });
            });
        };
        if (node.parent) {
            if (node.parent.type === 'property') {
                // if the object is a property value, check the tree for other objects that hang under a property of the same name
                var parentKey_1 = node.parent.keyNode.value;
                doc.visit(function (n) {
                    if (n.type === 'property' && n !== node.parent && n.keyNode.value === parentKey_1 && n.valueNode && n.valueNode.type === 'object') {
                        collectCompletionsForSimilarObject(n.valueNode);
                    }
                    return true;
                });
            }
            else if (node.parent.type === 'array') {
                // if the object is in an array, use all other array elements as similar objects
                node.parent.items.forEach(function (n) {
                    if (n.type === 'object' && n !== node) {
                        collectCompletionsForSimilarObject(n);
                    }
                });
            }
        }
        else if (node.type === 'object') {
            collector.add({
                kind: CompletionItemKind.Property,
                label: '$schema',
                insertText: this.getInsertTextForProperty('$schema', null, true, ''),
                insertTextFormat: InsertTextFormat.Snippet, documentation: '',
                filterText: this.getFilterTextForValue("$schema")
            });
        }
    };
    JSONCompletion.prototype.getSchemaLessValueCompletions = function (doc, node, offset, document, collector) {
        var _this = this;
        var offsetForSeparator = offset;
        if (node && (node.type === 'string' || node.type === 'number' || node.type === 'boolean' || node.type === 'null')) {
            offsetForSeparator = node.offset + node.length;
            node = node.parent;
        }
        if (!node) {
            collector.add({
                kind: this.getSuggestionKind('object'),
                label: 'Empty object',
                insertText: this.getInsertTextForValue({}, ''),
                insertTextFormat: InsertTextFormat.Snippet,
                documentation: ''
            });
            collector.add({
                kind: this.getSuggestionKind('array'),
                label: 'Empty array',
                insertText: this.getInsertTextForValue([], ''),
                insertTextFormat: InsertTextFormat.Snippet,
                documentation: ''
            });
            return;
        }
        var separatorAfter = this.evaluateSeparatorAfter(document, offsetForSeparator);
        var collectSuggestionsForValues = function (value) {
            if (!contains(value.parent, offset, true)) {
                collector.add({
                    kind: _this.getSuggestionKind(value.type),
                    label: _this.getLabelTextForMatchingNode(value, document),
                    insertText: _this.getInsertTextForMatchingNode(value, document, separatorAfter),
                    insertTextFormat: InsertTextFormat.Snippet, documentation: ''
                });
            }
            if (value.type === 'boolean') {
                _this.addBooleanValueCompletion(!value.value, separatorAfter, collector);
            }
        };
        if (node.type === 'property') {
            if (offset > node.colonOffset) {
                var valueNode = node.valueNode;
                if (valueNode && (offset > (valueNode.offset + valueNode.length) || valueNode.type === 'object' || valueNode.type === 'array')) {
                    return;
                }
                // suggest values at the same key
                var parentKey_2 = node.keyNode.value;
                doc.visit(function (n) {
                    if (n.type === 'property' && n.keyNode.value === parentKey_2 && n.valueNode) {
                        collectSuggestionsForValues(n.valueNode);
                    }
                    return true;
                });
                if (parentKey_2 === '$schema' && node.parent && !node.parent.parent) {
                    this.addDollarSchemaCompletions(separatorAfter, collector);
                }
            }
        }
        if (node.type === 'array') {
            if (node.parent && node.parent.type === 'property') {
                // suggest items of an array at the same key
                var parentKey_3 = node.parent.keyNode.value;
                doc.visit(function (n) {
                    var p = n;
                    if (n.type === 'property' && p.keyNode.value === parentKey_3 && p.valueNode && p.valueNode.type === 'array') {
                        p.valueNode.items.forEach(collectSuggestionsForValues);
                    }
                    return true;
                });
            }
            else {
                // suggest items in the same array
                node.items.forEach(collectSuggestionsForValues);
            }
        }
    };
    JSONCompletion.prototype.getValueCompletions = function (schema, doc, node, offset, document, collector, types) {
        var _this = this;
        var offsetForSeparator = offset;
        var parentKey = null;
        var valueNode = null;
        if (node && (node.type === 'string' || node.type === 'number' || node.type === 'boolean' || node.type === 'null')) {
            offsetForSeparator = node.offset + node.length;
            valueNode = node;
            node = node.parent;
        }
        if (!node) {
            this.addSchemaValueCompletions(schema.schema, '', collector, types);
            return;
        }
        if ((node.type === 'property') && offset > node.colonOffset) {
            var valueNode_1 = node.valueNode;
            if (valueNode_1 && offset > (valueNode_1.offset + valueNode_1.length)) {
                return; // we are past the value node
            }
            parentKey = node.keyNode.value;
            node = node.parent;
        }
        if (node && (parentKey !== null || node.type === 'array')) {
            var separatorAfter_2 = this.evaluateSeparatorAfter(document, offsetForSeparator);
            var matchingSchemas = doc.getMatchingSchemas(schema.schema, node.offset, valueNode);
            matchingSchemas.forEach(function (s) {
                if (s.node === node && !s.inverted && s.schema) {
                    if (node.type === 'array' && s.schema.items) {
                        if (Array.isArray(s.schema.items)) {
                            var index = _this.findItemAtOffset(node, document, offset);
                            if (index < s.schema.items.length) {
                                _this.addSchemaValueCompletions(s.schema.items[index], separatorAfter_2, collector, types);
                            }
                        }
                        else {
                            _this.addSchemaValueCompletions(s.schema.items, separatorAfter_2, collector, types);
                        }
                    }
                    if (s.schema.properties) {
                        var propertySchema = s.schema.properties[parentKey];
                        if (propertySchema) {
                            _this.addSchemaValueCompletions(propertySchema, separatorAfter_2, collector, types);
                        }
                    }
                }
            });
            if (parentKey === '$schema' && !node.parent) {
                this.addDollarSchemaCompletions(separatorAfter_2, collector);
            }
            if (types['boolean']) {
                this.addBooleanValueCompletion(true, separatorAfter_2, collector);
                this.addBooleanValueCompletion(false, separatorAfter_2, collector);
            }
            if (types['null']) {
                this.addNullValueCompletion(separatorAfter_2, collector);
            }
        }
    };
    JSONCompletion.prototype.getContributedValueCompletions = function (doc, node, offset, document, collector, collectionPromises) {
        if (!node) {
            this.contributions.forEach(function (contribution) {
                var collectPromise = contribution.collectDefaultCompletions(document.uri, collector);
                if (collectPromise) {
                    collectionPromises.push(collectPromise);
                }
            });
        }
        else {
            if (node.type === 'string' || node.type === 'number' || node.type === 'boolean' || node.type === 'null') {
                node = node.parent;
            }
            if ((node.type === 'property') && offset > node.colonOffset) {
                var parentKey_4 = node.keyNode.value;
                var valueNode = node.valueNode;
                if (!valueNode || offset <= (valueNode.offset + valueNode.length)) {
                    var location_2 = getNodePath(node.parent);
                    this.contributions.forEach(function (contribution) {
                        var collectPromise = contribution.collectValueCompletions(document.uri, location_2, parentKey_4, collector);
                        if (collectPromise) {
                            collectionPromises.push(collectPromise);
                        }
                    });
                }
            }
        }
    };
    JSONCompletion.prototype.addSchemaValueCompletions = function (schema, separatorAfter, collector, types) {
        var _this = this;
        if (typeof schema === 'object') {
            this.addEnumValueCompletions(schema, separatorAfter, collector);
            this.addDefaultValueCompletions(schema, separatorAfter, collector);
            this.collectTypes(schema, types);
            if (Array.isArray(schema.allOf)) {
                schema.allOf.forEach(function (s) { return _this.addSchemaValueCompletions(s, separatorAfter, collector, types); });
            }
            if (Array.isArray(schema.anyOf)) {
                schema.anyOf.forEach(function (s) { return _this.addSchemaValueCompletions(s, separatorAfter, collector, types); });
            }
            if (Array.isArray(schema.oneOf)) {
                schema.oneOf.forEach(function (s) { return _this.addSchemaValueCompletions(s, separatorAfter, collector, types); });
            }
        }
    };
    JSONCompletion.prototype.addDefaultValueCompletions = function (schema, separatorAfter, collector, arrayDepth) {
        var _this = this;
        if (arrayDepth === void 0) { arrayDepth = 0; }
        var hasProposals = false;
        if (schema.default) {
            var type = schema.type;
            var value = schema.default;
            for (var i = arrayDepth; i > 0; i--) {
                value = [value];
                type = 'array';
            }
            collector.add({
                kind: this.getSuggestionKind(type),
                label: this.getLabelForValue(value),
                insertText: this.getInsertTextForValue(value, separatorAfter),
                insertTextFormat: InsertTextFormat.Snippet,
                detail: jsonCompletion_localize('json.suggest.default', 'Default value'),
            });
            hasProposals = true;
        }
        if (Array.isArray(schema.defaultSnippets)) {
            schema.defaultSnippets.forEach(function (s) {
                var type = schema.type;
                var value = s.body;
                var label = s.label;
                var insertText;
                var filterText;
                if (typeof value !== 'undefined') {
                    var type_1 = schema.type;
                    for (var i = arrayDepth; i > 0; i--) {
                        value = [value];
                        type_1 = 'array';
                    }
                    insertText = _this.getInsertTextForSnippetValue(value, separatorAfter);
                    filterText = _this.getFilterTextForSnippetValue(value);
                    label = label || _this.getLabelForSnippetValue(value);
                }
                else if (typeof s.bodyText === 'string') {
                    var prefix = '', suffix = '', indent = '';
                    for (var i = arrayDepth; i > 0; i--) {
                        prefix = prefix + indent + '[\n';
                        suffix = suffix + '\n' + indent + ']';
                        indent += '\t';
                        type = 'array';
                    }
                    insertText = prefix + indent + s.bodyText.split('\n').join('\n' + indent) + suffix + separatorAfter;
                    label = label || insertText;
                    filterText = insertText.replace(/[\n]/g, ''); // remove new lines
                }
                collector.add({
                    kind: _this.getSuggestionKind(type),
                    label: label,
                    documentation: s.description,
                    insertText: insertText,
                    insertTextFormat: InsertTextFormat.Snippet,
                    filterText: filterText
                });
                hasProposals = true;
            });
        }
        if (!hasProposals && typeof schema.items === 'object' && !Array.isArray(schema.items)) {
            this.addDefaultValueCompletions(schema.items, separatorAfter, collector, arrayDepth + 1);
        }
    };
    JSONCompletion.prototype.addEnumValueCompletions = function (schema, separatorAfter, collector) {
        if (Array.isArray(schema.enum)) {
            for (var i = 0, length = schema.enum.length; i < length; i++) {
                var enm = schema.enum[i];
                var documentation = schema.description;
                if (schema.enumDescriptions && i < schema.enumDescriptions.length) {
                    documentation = schema.enumDescriptions[i];
                }
                collector.add({
                    kind: this.getSuggestionKind(schema.type),
                    label: this.getLabelForValue(enm),
                    insertText: this.getInsertTextForValue(enm, separatorAfter),
                    insertTextFormat: InsertTextFormat.Snippet,
                    documentation: documentation
                });
            }
        }
    };
    JSONCompletion.prototype.collectTypes = function (schema, types) {
        if (Array.isArray(schema.enum)) {
            return;
        }
        var type = schema.type;
        if (Array.isArray(type)) {
            type.forEach(function (t) { return types[t] = true; });
        }
        else {
            types[type] = true;
        }
    };
    JSONCompletion.prototype.addFillerValueCompletions = function (types, separatorAfter, collector) {
        if (types['object']) {
            collector.add({
                kind: this.getSuggestionKind('object'),
                label: '{}',
                insertText: this.getInsertTextForGuessedValue({}, separatorAfter),
                insertTextFormat: InsertTextFormat.Snippet,
                detail: jsonCompletion_localize('defaults.object', 'New object'),
                documentation: ''
            });
        }
        if (types['array']) {
            collector.add({
                kind: this.getSuggestionKind('array'),
                label: '[]',
                insertText: this.getInsertTextForGuessedValue([], separatorAfter),
                insertTextFormat: InsertTextFormat.Snippet,
                detail: jsonCompletion_localize('defaults.array', 'New array'),
                documentation: ''
            });
        }
    };
    JSONCompletion.prototype.addBooleanValueCompletion = function (value, separatorAfter, collector) {
        collector.add({
            kind: this.getSuggestionKind('boolean'),
            label: value ? 'true' : 'false',
            insertText: this.getInsertTextForValue(value, separatorAfter),
            insertTextFormat: InsertTextFormat.Snippet,
            documentation: ''
        });
    };
    JSONCompletion.prototype.addNullValueCompletion = function (separatorAfter, collector) {
        collector.add({
            kind: this.getSuggestionKind('null'),
            label: 'null',
            insertText: 'null' + separatorAfter,
            insertTextFormat: InsertTextFormat.Snippet,
            documentation: ''
        });
    };
    JSONCompletion.prototype.addDollarSchemaCompletions = function (separatorAfter, collector) {
        var _this = this;
        var schemaIds = this.schemaService.getRegisteredSchemaIds(function (schema) { return schema === 'http' || schema === 'https'; });
        schemaIds.forEach(function (schemaId) { return collector.add({
            kind: CompletionItemKind.Module,
            label: _this.getLabelForValue(schemaId),
            filterText: _this.getFilterTextForValue(schemaId),
            insertText: _this.getInsertTextForValue(schemaId, separatorAfter),
            insertTextFormat: InsertTextFormat.Snippet, documentation: ''
        }); });
    };
    JSONCompletion.prototype.getLabelForValue = function (value) {
        var label = JSON.stringify(value);
        if (label.length > 57) {
            return label.substr(0, 57).trim() + '...';
        }
        return label;
    };
    JSONCompletion.prototype.getFilterTextForValue = function (value) {
        return JSON.stringify(value);
    };
    JSONCompletion.prototype.getFilterTextForSnippetValue = function (value) {
        return JSON.stringify(value).replace(/\$\{\d+:([^}]+)\}|\$\d+/g, '$1');
    };
    JSONCompletion.prototype.getLabelForSnippetValue = function (value) {
        var label = JSON.stringify(value);
        label = label.replace(/\$\{\d+:([^}]+)\}|\$\d+/g, '$1');
        if (label.length > 57) {
            return label.substr(0, 57).trim() + '...';
        }
        return label;
    };
    JSONCompletion.prototype.getInsertTextForPlainText = function (text) {
        return text.replace(/[\\\$\}]/g, '\\$&'); // escape $, \ and } 
    };
    JSONCompletion.prototype.getInsertTextForValue = function (value, separatorAfter) {
        var text = JSON.stringify(value, null, '\t');
        if (text === '{}') {
            return '{\n\t$1\n}' + separatorAfter;
        }
        else if (text === '[]') {
            return '[\n\t$1\n]' + separatorAfter;
        }
        return this.getInsertTextForPlainText(text + separatorAfter);
    };
    JSONCompletion.prototype.getInsertTextForSnippetValue = function (value, separatorAfter) {
        var replacer = function (value) {
            if (typeof value === 'string') {
                if (value[0] === '^') {
                    return value.substr(1);
                }
            }
            return JSON.stringify(value);
        };
        return stringifyObject(value, '', replacer) + separatorAfter;
    };
    JSONCompletion.prototype.getInsertTextForGuessedValue = function (value, separatorAfter) {
        switch (typeof value) {
            case 'object':
                if (value === null) {
                    return '${1:null}' + separatorAfter;
                }
                return this.getInsertTextForValue(value, separatorAfter);
            case 'string':
                var snippetValue = JSON.stringify(value);
                snippetValue = snippetValue.substr(1, snippetValue.length - 2); // remove quotes
                snippetValue = this.getInsertTextForPlainText(snippetValue); // escape \ and }
                return '"${1:' + snippetValue + '}"' + separatorAfter;
            case 'number':
            case 'boolean':
                return '${1:' + JSON.stringify(value) + '}' + separatorAfter;
        }
        return this.getInsertTextForValue(value, separatorAfter);
    };
    JSONCompletion.prototype.getSuggestionKind = function (type) {
        if (Array.isArray(type)) {
            var array = type;
            type = array.length > 0 ? array[0] : null;
        }
        if (!type) {
            return CompletionItemKind.Value;
        }
        switch (type) {
            case 'string': return CompletionItemKind.Value;
            case 'object': return CompletionItemKind.Module;
            case 'property': return CompletionItemKind.Property;
            default: return CompletionItemKind.Value;
        }
    };
    JSONCompletion.prototype.getLabelTextForMatchingNode = function (node, document) {
        switch (node.type) {
            case 'array':
                return '[]';
            case 'object':
                return '{}';
            default:
                var content = document.getText().substr(node.offset, node.length);
                return content;
        }
    };
    JSONCompletion.prototype.getInsertTextForMatchingNode = function (node, document, separatorAfter) {
        switch (node.type) {
            case 'array':
                return this.getInsertTextForValue([], separatorAfter);
            case 'object':
                return this.getInsertTextForValue({}, separatorAfter);
            default:
                var content = document.getText().substr(node.offset, node.length) + separatorAfter;
                return this.getInsertTextForPlainText(content);
        }
    };
    JSONCompletion.prototype.getInsertTextForProperty = function (key, propertySchema, addValue, separatorAfter) {
        var propertyText = this.getInsertTextForValue(key, '');
        if (!addValue) {
            return propertyText;
        }
        var resultText = propertyText + ': ';
        var value;
        var nValueProposals = 0;
        if (propertySchema) {
            if (Array.isArray(propertySchema.defaultSnippets)) {
                if (propertySchema.defaultSnippets.length === 1) {
                    var body = propertySchema.defaultSnippets[0].body;
                    if (typeof body !== 'undefined') {
                        value = this.getInsertTextForSnippetValue(body, '');
                    }
                }
                nValueProposals += propertySchema.defaultSnippets.length;
            }
            if (propertySchema.enum) {
                if (!value && propertySchema.enum.length === 1) {
                    value = this.getInsertTextForGuessedValue(propertySchema.enum[0], '');
                }
                nValueProposals += propertySchema.enum.length;
            }
            if (typeof propertySchema.default !== 'undefined') {
                if (!value) {
                    value = this.getInsertTextForGuessedValue(propertySchema.default, '');
                }
                nValueProposals++;
            }
            if (nValueProposals === 0) {
                var type = Array.isArray(propertySchema.type) ? propertySchema.type[0] : propertySchema.type;
                if (!type) {
                    if (propertySchema.properties) {
                        type = 'object';
                    }
                    else if (propertySchema.items) {
                        type = 'array';
                    }
                }
                switch (type) {
                    case 'boolean':
                        value = '$1';
                        break;
                    case 'string':
                        value = '"$1"';
                        break;
                    case 'object':
                        value = '{\n\t$1\n}';
                        break;
                    case 'array':
                        value = '[\n\t$1\n]';
                        break;
                    case 'number':
                    case 'integer':
                        value = '${1:0}';
                        break;
                    case 'null':
                        value = '${1:null}';
                        break;
                    default:
                        return propertyText;
                }
            }
        }
        if (!value || nValueProposals > 1) {
            value = '$1';
        }
        return resultText + value + separatorAfter;
    };
    JSONCompletion.prototype.getCurrentWord = function (document, offset) {
        var i = offset - 1;
        var text = document.getText();
        while (i >= 0 && ' \t\n\r\v":{[,]}'.indexOf(text.charAt(i)) === -1) {
            i--;
        }
        return text.substring(i + 1, offset);
    };
    JSONCompletion.prototype.evaluateSeparatorAfter = function (document, offset) {
        var scanner = main_createScanner(document.getText(), true);
        scanner.setPosition(offset);
        var token = scanner.scan();
        switch (token) {
            case 5 /* CommaToken */:
            case 2 /* CloseBraceToken */:
            case 4 /* CloseBracketToken */:
            case 17 /* EOF */:
                return '';
            default:
                return ',';
        }
    };
    JSONCompletion.prototype.findItemAtOffset = function (node, document, offset) {
        var scanner = main_createScanner(document.getText(), true);
        var children = node.items;
        for (var i = children.length - 1; i >= 0; i--) {
            var child = children[i];
            if (offset > child.offset + child.length) {
                scanner.setPosition(child.offset + child.length);
                var token = scanner.scan();
                if (token === 5 /* CommaToken */ && offset >= scanner.getTokenOffset() + scanner.getTokenLength()) {
                    return i + 1;
                }
                return i;
            }
            else if (offset >= child.offset) {
                return i;
            }
        }
        return 0;
    };
    JSONCompletion.prototype.isInComment = function (document, start, offset) {
        var scanner = main_createScanner(document.getText(), false);
        scanner.setPosition(start);
        var token = scanner.scan();
        while (token !== 17 /* EOF */ && (scanner.getTokenOffset() + scanner.getTokenLength() < offset)) {
            token = scanner.scan();
        }
        return (token === 12 /* LineCommentTrivia */ || token === 13 /* BlockCommentTrivia */) && scanner.getTokenOffset() <= offset;
    };
    return JSONCompletion;
}());

//# sourceMappingURL=jsonCompletion.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/services/jsonHover.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/



var jsonHover_JSONHover = /** @class */ (function () {
    function JSONHover(schemaService, contributions, promiseConstructor) {
        if (contributions === void 0) { contributions = []; }
        this.schemaService = schemaService;
        this.contributions = contributions;
        this.promise = promiseConstructor || Promise;
    }
    JSONHover.prototype.doHover = function (document, position, doc) {
        var offset = document.offsetAt(position);
        var node = doc.getNodeFromOffset(offset);
        if (!node || (node.type === 'object' || node.type === 'array') && offset > node.offset + 1 && offset < node.offset + node.length - 1) {
            return this.promise.resolve(null);
        }
        var hoverRangeNode = node;
        // use the property description when hovering over an object key
        if (node.type === 'string') {
            var parent = node.parent;
            if (parent.type === 'property' && parent.keyNode === node) {
                node = parent.valueNode;
                if (!node) {
                    return this.promise.resolve(null);
                }
            }
        }
        var hoverRange = main_Range.create(document.positionAt(hoverRangeNode.offset), document.positionAt(hoverRangeNode.offset + hoverRangeNode.length));
        var createHover = function (contents) {
            var result = {
                contents: contents,
                range: hoverRange
            };
            return result;
        };
        var location = getNodePath(node);
        for (var i = this.contributions.length - 1; i >= 0; i--) {
            var contribution = this.contributions[i];
            var promise = contribution.getInfoContribution(document.uri, location);
            if (promise) {
                return promise.then(function (htmlContent) { return createHover(htmlContent); });
            }
        }
        return this.schemaService.getSchemaForResource(document.uri, doc).then(function (schema) {
            if (schema) {
                var matchingSchemas = doc.getMatchingSchemas(schema.schema, node.offset);
                var title_1 = null;
                var markdownDescription_1 = null;
                var markdownEnumValueDescription_1 = null, enumValue_1 = null;
                matchingSchemas.every(function (s) {
                    if (s.node === node && !s.inverted && s.schema) {
                        title_1 = title_1 || s.schema.title;
                        markdownDescription_1 = markdownDescription_1 || s.schema.markdownDescription || toMarkdown(s.schema.description);
                        if (s.schema.enum) {
                            var idx = s.schema.enum.indexOf(jsonParser_getNodeValue(node));
                            if (s.schema.markdownEnumDescriptions) {
                                markdownEnumValueDescription_1 = s.schema.markdownEnumDescriptions[idx];
                            }
                            else if (s.schema.enumDescriptions) {
                                markdownEnumValueDescription_1 = toMarkdown(s.schema.enumDescriptions[idx]);
                            }
                            if (markdownEnumValueDescription_1) {
                                enumValue_1 = s.schema.enum[idx];
                                if (typeof enumValue_1 !== 'string') {
                                    enumValue_1 = JSON.stringify(enumValue_1);
                                }
                            }
                        }
                    }
                    return true;
                });
                var result = '';
                if (title_1) {
                    result = toMarkdown(title_1);
                }
                if (markdownDescription_1) {
                    if (result.length > 0) {
                        result += "\n\n";
                    }
                    result += markdownDescription_1;
                }
                if (markdownEnumValueDescription_1) {
                    if (result.length > 0) {
                        result += "\n\n";
                    }
                    result += "`" + toMarkdown(enumValue_1) + "`: " + markdownEnumValueDescription_1;
                }
                return createHover([result]);
            }
            return null;
        });
    };
    return JSONHover;
}());

function toMarkdown(plain) {
    if (plain) {
        var res = plain.replace(/([^\n\r])(\r?\n)([^\n\r])/gm, '$1\n\n$3'); // single new lines to \n\n (Markdown paragraph)
        return res.replace(/[\\`*_{}[\]()#+\-.!]/g, "\\$&"); // escape markdown syntax tokens: http://daringfireball.net/projects/markdown/syntax#backslash
    }
    return void 0;
}
//# sourceMappingURL=jsonHover.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/services/jsonValidation.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/




var jsonValidation_localize = loadMessageBundle();
var jsonValidation_JSONValidation = /** @class */ (function () {
    function JSONValidation(jsonSchemaService, promiseConstructor) {
        this.jsonSchemaService = jsonSchemaService;
        this.promise = promiseConstructor;
        this.validationEnabled = true;
    }
    JSONValidation.prototype.configure = function (raw) {
        if (raw) {
            this.validationEnabled = raw.validate;
            this.commentSeverity = raw.allowComments ? ProblemSeverity.Ignore : ProblemSeverity.Error;
        }
    };
    JSONValidation.prototype.doValidation = function (textDocument, jsonDocument, documentSettings) {
        var _this = this;
        if (!this.validationEnabled) {
            return this.promise.resolve([]);
        }
        var diagnostics = [];
        var added = {};
        var addProblem = function (problem) {
            if (problem.severity === ProblemSeverity.Ignore) {
                return;
            }
            // remove duplicated messages
            var signature = problem.location.offset + ' ' + problem.location.length + ' ' + problem.message;
            if (!added[signature]) {
                added[signature] = true;
                var range = {
                    start: textDocument.positionAt(problem.location.offset),
                    end: textDocument.positionAt(problem.location.offset + problem.location.length)
                };
                var severity = problem.severity === ProblemSeverity.Error ? DiagnosticSeverity.Error : DiagnosticSeverity.Warning;
                diagnostics.push({ severity: severity, range: range, message: problem.message });
            }
        };
        return this.jsonSchemaService.getSchemaForResource(textDocument.uri, jsonDocument).then(function (schema) {
            var trailingCommaSeverity = documentSettings ? documentSettings.trailingCommas : ProblemSeverity.Error;
            var commentSeverity = documentSettings ? documentSettings.comments : _this.commentSeverity;
            if (schema) {
                if (schema.errors.length && jsonDocument.root) {
                    var astRoot = jsonDocument.root;
                    var property = astRoot.type === 'object' ? astRoot.properties[0] : null;
                    if (property && property.keyNode.value === '$schema') {
                        var node = property.valueNode || property;
                        addProblem({ location: { offset: node.offset, length: node.length }, message: schema.errors[0], severity: ProblemSeverity.Warning });
                    }
                    else {
                        addProblem({ location: { offset: astRoot.offset, length: 1 }, message: schema.errors[0], severity: ProblemSeverity.Warning });
                    }
                }
                else {
                    var semanticErrors = jsonDocument.validate(schema.schema);
                    if (semanticErrors) {
                        semanticErrors.forEach(addProblem);
                    }
                }
                if (schemaAllowsComments(schema.schema)) {
                    trailingCommaSeverity = commentSeverity = ProblemSeverity.Ignore;
                }
            }
            jsonDocument.syntaxErrors.forEach(function (p) {
                if (p.code === ErrorCode.TrailingComma) {
                    p.severity = trailingCommaSeverity;
                }
                addProblem(p);
            });
            diagnostics.push.apply(diagnostics, jsonDocument.externalDiagnostic);
            if (commentSeverity !== ProblemSeverity.Ignore) {
                var message_1 = jsonValidation_localize('InvalidCommentToken', 'Comments are not permitted in JSON.');
                jsonDocument.comments.forEach(function (c) {
                    addProblem({ location: c, severity: commentSeverity, message: message_1 });
                });
            }
            return diagnostics;
        });
    };
    return JSONValidation;
}());

function schemaAllowsComments(schemaRef) {
    if (schemaRef && typeof schemaRef === 'object') {
        if (schemaRef.allowComments) {
            return true;
        }
        if (schemaRef.allOf) {
            return schemaRef.allOf.some(schemaAllowsComments);
        }
    }
    return false;
}
//# sourceMappingURL=jsonValidation.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/utils/colors.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var Digit0 = 48;
var Digit9 = 57;
var A = 65;
var F = 70;
var a = 97;
var colors_f = 102;
function hexDigit(charCode) {
    if (charCode < Digit0) {
        return 0;
    }
    if (charCode <= Digit9) {
        return charCode - Digit0;
    }
    if (charCode < a) {
        charCode += (a - A);
    }
    if (charCode >= a && charCode <= colors_f) {
        return charCode - a + 10;
    }
    return 0;
}
function colorFromHex(text) {
    if (text[0] !== '#') {
        return null;
    }
    switch (text.length) {
        case 4:
            return {
                red: (hexDigit(text.charCodeAt(1)) * 0x11) / 255.0,
                green: (hexDigit(text.charCodeAt(2)) * 0x11) / 255.0,
                blue: (hexDigit(text.charCodeAt(3)) * 0x11) / 255.0,
                alpha: 1
            };
        case 5:
            return {
                red: (hexDigit(text.charCodeAt(1)) * 0x11) / 255.0,
                green: (hexDigit(text.charCodeAt(2)) * 0x11) / 255.0,
                blue: (hexDigit(text.charCodeAt(3)) * 0x11) / 255.0,
                alpha: (hexDigit(text.charCodeAt(4)) * 0x11) / 255.0,
            };
        case 7:
            return {
                red: (hexDigit(text.charCodeAt(1)) * 0x10 + hexDigit(text.charCodeAt(2))) / 255.0,
                green: (hexDigit(text.charCodeAt(3)) * 0x10 + hexDigit(text.charCodeAt(4))) / 255.0,
                blue: (hexDigit(text.charCodeAt(5)) * 0x10 + hexDigit(text.charCodeAt(6))) / 255.0,
                alpha: 1
            };
        case 9:
            return {
                red: (hexDigit(text.charCodeAt(1)) * 0x10 + hexDigit(text.charCodeAt(2))) / 255.0,
                green: (hexDigit(text.charCodeAt(3)) * 0x10 + hexDigit(text.charCodeAt(4))) / 255.0,
                blue: (hexDigit(text.charCodeAt(5)) * 0x10 + hexDigit(text.charCodeAt(6))) / 255.0,
                alpha: (hexDigit(text.charCodeAt(7)) * 0x10 + hexDigit(text.charCodeAt(8))) / 255.0
            };
    }
    return null;
}
function colorFrom256RGB(red, green, blue, alpha) {
    if (alpha === void 0) { alpha = 1.0; }
    return {
        red: red / 255.0,
        green: green / 255.0,
        blue: blue / 255.0,
        alpha: alpha
    };
}
//# sourceMappingURL=colors.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/services/jsonDocumentSymbols.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/





var jsonDocumentSymbols_JSONDocumentSymbols = /** @class */ (function () {
    function JSONDocumentSymbols(schemaService) {
        this.schemaService = schemaService;
    }
    JSONDocumentSymbols.prototype.findDocumentSymbols = function (document, doc) {
        var _this = this;
        var root = doc.root;
        if (!root) {
            return null;
        }
        // special handling for key bindings
        var resourceString = document.uri;
        if ((resourceString === 'vscode://defaultsettings/keybindings.json') || strings_endsWith(resourceString.toLowerCase(), '/user/keybindings.json')) {
            if (root.type === 'array') {
                var result_1 = [];
                root.items.forEach(function (item) {
                    if (item.type === 'object') {
                        for (var _i = 0, _a = item.properties; _i < _a.length; _i++) {
                            var property = _a[_i];
                            if (property.keyNode.value === 'key') {
                                if (property.valueNode) {
                                    var location = Location.create(document.uri, main_Range.create(document.positionAt(item.offset), document.positionAt(item.offset + item.length)));
                                    result_1.push({ name: jsonParser_getNodeValue(property.valueNode), kind: SymbolKind.Function, location: location });
                                }
                                return;
                            }
                        }
                    }
                });
                return result_1;
            }
        }
        var collectOutlineEntries = function (result, node, containerName) {
            if (node.type === 'array') {
                node.items.forEach(function (node) { return collectOutlineEntries(result, node, containerName); });
            }
            else if (node.type === 'object') {
                node.properties.forEach(function (property) {
                    var location = Location.create(document.uri, main_Range.create(document.positionAt(property.offset), document.positionAt(property.offset + property.length)));
                    var valueNode = property.valueNode;
                    if (valueNode) {
                        var childContainerName = containerName ? containerName + '.' + property.keyNode.value : property.keyNode.value;
                        result.push({ name: property.keyNode.value, kind: _this.getSymbolKind(valueNode.type), location: location, containerName: containerName });
                        collectOutlineEntries(result, valueNode, childContainerName);
                    }
                });
            }
            return result;
        };
        var result = collectOutlineEntries([], root, void 0);
        return result;
    };
    JSONDocumentSymbols.prototype.getSymbolKind = function (nodeType) {
        switch (nodeType) {
            case 'object':
                return SymbolKind.Module;
            case 'string':
                return SymbolKind.String;
            case 'number':
                return SymbolKind.Number;
            case 'array':
                return SymbolKind.Array;
            case 'boolean':
                return SymbolKind.Boolean;
            default:// 'null'
                return SymbolKind.Variable;
        }
    };
    JSONDocumentSymbols.prototype.findDocumentColors = function (document, doc) {
        return this.schemaService.getSchemaForResource(document.uri, doc).then(function (schema) {
            var result = [];
            if (schema) {
                var matchingSchemas = doc.getMatchingSchemas(schema.schema);
                var visitedNode = {};
                for (var _i = 0, matchingSchemas_1 = matchingSchemas; _i < matchingSchemas_1.length; _i++) {
                    var s = matchingSchemas_1[_i];
                    if (!s.inverted && s.schema && (s.schema.format === 'color' || s.schema.format === 'color-hex') && s.node && s.node.type === 'string') {
                        var nodeId = String(s.node.offset);
                        if (!visitedNode[nodeId]) {
                            var color = colorFromHex(jsonParser_getNodeValue(s.node));
                            if (color) {
                                var range = main_Range.create(document.positionAt(s.node.offset), document.positionAt(s.node.offset + s.node.length));
                                result.push({ color: color, range: range });
                            }
                            visitedNode[nodeId] = true;
                        }
                    }
                }
            }
            return result;
        });
    };
    JSONDocumentSymbols.prototype.getColorPresentations = function (document, doc, color, range) {
        var result = [];
        var red256 = Math.round(color.red * 255), green256 = Math.round(color.green * 255), blue256 = Math.round(color.blue * 255);
        function toTwoDigitHex(n) {
            var r = n.toString(16);
            return r.length !== 2 ? '0' + r : r;
        }
        var label;
        if (color.alpha === 1) {
            label = "#" + toTwoDigitHex(red256) + toTwoDigitHex(green256) + toTwoDigitHex(blue256);
        }
        else {
            label = "#" + toTwoDigitHex(red256) + toTwoDigitHex(green256) + toTwoDigitHex(blue256) + toTwoDigitHex(Math.round(color.alpha * 255));
        }
        result.push({ label: label, textEdit: TextEdit.replace(range, JSON.stringify(label)) });
        return result;
    };
    return JSONDocumentSymbols;
}());

//# sourceMappingURL=jsonDocumentSymbols.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/services/configuration.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


var configuration_localize = loadMessageBundle();
var schemaContributions = {
    schemaAssociations: {},
    schemas: {
        // bundle the schema-schema to include (localized) descriptions
        'http://json-schema.org/draft-04/schema#': {
            'title': configuration_localize('schema.json', 'Describes a JSON file using a schema. See json-schema.org for more info.'),
            '$schema': 'http://json-schema.org/draft-04/schema#',
            'definitions': {
                'schemaArray': {
                    'type': 'array',
                    'minItems': 1,
                    'items': { '$ref': '#' }
                },
                'positiveInteger': {
                    'type': 'integer',
                    'minimum': 0
                },
                'positiveIntegerDefault0': {
                    'allOf': [{ '$ref': '#/definitions/positiveInteger' }, { 'default': 0 }]
                },
                'simpleTypes': {
                    'type': 'string',
                    'enum': ['array', 'boolean', 'integer', 'null', 'number', 'object', 'string']
                },
                'stringArray': {
                    'type': 'array',
                    'items': { 'type': 'string' },
                    'minItems': 1,
                    'uniqueItems': true
                }
            },
            'type': 'object',
            'properties': {
                'id': {
                    'type': 'string',
                    'format': 'uri',
                    'description': configuration_localize('schema.json.id', 'A unique identifier for the schema.')
                },
                '$schema': {
                    'type': 'string',
                    'format': 'uri',
                    'description': configuration_localize('schema.json.$schema', 'The schema to verify this document against ')
                },
                'title': {
                    'type': 'string',
                    'description': configuration_localize('schema.json.title', 'A descriptive title of the element')
                },
                'description': {
                    'type': 'string',
                    'description': configuration_localize('schema.json.description', 'A long description of the element. Used in hover menus and suggestions.')
                },
                'default': {
                    'description': configuration_localize('schema.json.default', 'A default value. Used by suggestions.')
                },
                'multipleOf': {
                    'type': 'number',
                    'minimum': 0,
                    'exclusiveMinimum': true,
                    'description': configuration_localize('schema.json.multipleOf', 'A number that should cleanly divide the current value (i.e. have no remainder)')
                },
                'maximum': {
                    'type': 'number',
                    'description': configuration_localize('schema.json.maximum', 'The maximum numerical value, inclusive by default.')
                },
                'exclusiveMaximum': {
                    'type': 'boolean',
                    'default': false,
                    'description': configuration_localize('schema.json.exclusiveMaximum', 'Makes the maximum property exclusive.')
                },
                'minimum': {
                    'type': 'number',
                    'description': configuration_localize('schema.json.minimum', 'The minimum numerical value, inclusive by default.')
                },
                'exclusiveMinimum': {
                    'type': 'boolean',
                    'default': false,
                    'description': configuration_localize('schema.json.exclusiveMininum', 'Makes the minimum property exclusive.')
                },
                'maxLength': {
                    'allOf': [
                        { '$ref': '#/definitions/positiveInteger' }
                    ],
                    'description': configuration_localize('schema.json.maxLength', 'The maximum length of a string.')
                },
                'minLength': {
                    'allOf': [
                        { '$ref': '#/definitions/positiveIntegerDefault0' }
                    ],
                    'description': configuration_localize('schema.json.minLength', 'The minimum length of a string.')
                },
                'pattern': {
                    'type': 'string',
                    'format': 'regex',
                    'description': configuration_localize('schema.json.pattern', 'A regular expression to match the string against. It is not implicitly anchored.')
                },
                'additionalItems': {
                    'anyOf': [
                        { 'type': 'boolean' },
                        { '$ref': '#' }
                    ],
                    'default': {},
                    'description': configuration_localize('schema.json.additionalItems', 'For arrays, only when items is set as an array. If it is a schema, then this schema validates items after the ones specified by the items array. If it is false, then additional items will cause validation to fail.')
                },
                'items': {
                    'anyOf': [
                        { '$ref': '#' },
                        { '$ref': '#/definitions/schemaArray' }
                    ],
                    'default': {},
                    'description': configuration_localize('schema.json.items', 'For arrays. Can either be a schema to validate every element against or an array of schemas to validate each item against in order (the first schema will validate the first element, the second schema will validate the second element, and so on.')
                },
                'maxItems': {
                    'allOf': [
                        { '$ref': '#/definitions/positiveInteger' }
                    ],
                    'description': configuration_localize('schema.json.maxItems', 'The maximum number of items that can be inside an array. Inclusive.')
                },
                'minItems': {
                    'allOf': [
                        { '$ref': '#/definitions/positiveIntegerDefault0' }
                    ],
                    'description': configuration_localize('schema.json.minItems', 'The minimum number of items that can be inside an array. Inclusive.')
                },
                'uniqueItems': {
                    'type': 'boolean',
                    'default': false,
                    'description': configuration_localize('schema.json.uniqueItems', 'If all of the items in the array must be unique. Defaults to false.')
                },
                'maxProperties': {
                    'allOf': [
                        { '$ref': '#/definitions/positiveInteger' }
                    ],
                    'description': configuration_localize('schema.json.maxProperties', 'The maximum number of properties an object can have. Inclusive.')
                },
                'minProperties': {
                    'allOf': [
                        { '$ref': '#/definitions/positiveIntegerDefault0' },
                    ],
                    'description': configuration_localize('schema.json.minProperties', 'The minimum number of properties an object can have. Inclusive.')
                },
                'required': {
                    'allOf': [
                        { '$ref': '#/definitions/stringArray' }
                    ],
                    'description': configuration_localize('schema.json.required', 'An array of strings that lists the names of all properties required on this object.')
                },
                'additionalProperties': {
                    'anyOf': [
                        { 'type': 'boolean' },
                        { '$ref': '#' }
                    ],
                    'default': {},
                    'description': configuration_localize('schema.json.additionalProperties', 'Either a schema or a boolean. If a schema, then used to validate all properties not matched by \'properties\' or \'patternProperties\'. If false, then any properties not matched by either will cause this schema to fail.')
                },
                'definitions': {
                    'type': 'object',
                    'additionalProperties': { '$ref': '#' },
                    'default': {},
                    'description': configuration_localize('schema.json.definitions', 'Not used for validation. Place subschemas here that you wish to reference inline with $ref')
                },
                'properties': {
                    'type': 'object',
                    'additionalProperties': { '$ref': '#' },
                    'default': {},
                    'description': configuration_localize('schema.json.properties', 'A map of property names to schemas for each property.')
                },
                'patternProperties': {
                    'type': 'object',
                    'additionalProperties': { '$ref': '#' },
                    'default': {},
                    'description': configuration_localize('schema.json.patternProperties', 'A map of regular expressions on property names to schemas for matching properties.')
                },
                'dependencies': {
                    'type': 'object',
                    'additionalProperties': {
                        'anyOf': [
                            { '$ref': '#' },
                            { '$ref': '#/definitions/stringArray' }
                        ]
                    },
                    'description': configuration_localize('schema.json.dependencies', 'A map of property names to either an array of property names or a schema. An array of property names means the property named in the key depends on the properties in the array being present in the object in order to be valid. If the value is a schema, then the schema is only applied to the object if the property in the key exists on the object.')
                },
                'enum': {
                    'type': 'array',
                    'minItems': 1,
                    'uniqueItems': true,
                    'description': configuration_localize('schema.json.enum', 'The set of literal values that are valid')
                },
                'type': {
                    'anyOf': [
                        { '$ref': '#/definitions/simpleTypes' },
                        {
                            'type': 'array',
                            'items': { '$ref': '#/definitions/simpleTypes' },
                            'minItems': 1,
                            'uniqueItems': true
                        }
                    ],
                    'description': configuration_localize('schema.json.type', 'Either a string of one of the basic schema types (number, integer, null, array, object, boolean, string) or an array of strings specifying a subset of those types.')
                },
                'format': {
                    'anyOf': [
                        {
                            'type': 'string',
                            'description': configuration_localize('schema.json.format', 'Describes the format expected for the value.'),
                            'enum': ['date-time', 'uri', 'email', 'hostname', 'ipv4', 'ipv6', 'regex']
                        }, {
                            'type': 'string'
                        }
                    ]
                },
                'allOf': {
                    'allOf': [
                        { '$ref': '#/definitions/schemaArray' }
                    ],
                    'description': configuration_localize('schema.json.allOf', 'An array of schemas, all of which must match.')
                },
                'anyOf': {
                    'allOf': [
                        { '$ref': '#/definitions/schemaArray' }
                    ],
                    'description': configuration_localize('schema.json.anyOf', 'An array of schemas, where at least one must match.')
                },
                'oneOf': {
                    'allOf': [
                        { '$ref': '#/definitions/schemaArray' }
                    ],
                    'description': configuration_localize('schema.json.oneOf', 'An array of schemas, exactly one of which must match.')
                },
                'not': {
                    'allOf': [
                        { '$ref': '#' }
                    ],
                    'description': configuration_localize('schema.json.not', 'A schema which must not match.')
                }
            },
            'dependencies': {
                'exclusiveMaximum': ['maximum'],
                'exclusiveMinimum': ['minimum']
            },
            'default': {}
        }
    }
};
//# sourceMappingURL=configuration.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/services/jsonSchemaService.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/






var jsonSchemaService_localize = loadMessageBundle();
var jsonSchemaService_FilePatternAssociation = /** @class */ (function () {
    function FilePatternAssociation(pattern) {
        try {
            this.patternRegExp = new RegExp(strings_convertSimple2RegExpPattern(pattern) + '$');
        }
        catch (e) {
            // invalid pattern
            this.patternRegExp = null;
        }
        this.schemas = [];
    }
    FilePatternAssociation.prototype.addSchema = function (id) {
        this.schemas.push(id);
    };
    FilePatternAssociation.prototype.matchesPattern = function (fileName) {
        return this.patternRegExp && this.patternRegExp.test(fileName);
    };
    FilePatternAssociation.prototype.getSchemas = function () {
        return this.schemas;
    };
    return FilePatternAssociation;
}());
var SchemaHandle = /** @class */ (function () {
    function SchemaHandle(service, url, unresolvedSchemaContent) {
        this.service = service;
        this.url = url;
        if (unresolvedSchemaContent) {
            this.unresolvedSchema = this.service.promise.resolve(new UnresolvedSchema(unresolvedSchemaContent));
        }
    }
    SchemaHandle.prototype.getUnresolvedSchema = function () {
        if (!this.unresolvedSchema) {
            this.unresolvedSchema = this.service.loadSchema(this.url);
        }
        return this.unresolvedSchema;
    };
    SchemaHandle.prototype.getResolvedSchema = function () {
        var _this = this;
        if (!this.resolvedSchema) {
            this.resolvedSchema = this.getUnresolvedSchema().then(function (unresolved) {
                return _this.service.resolveSchemaContent(unresolved, _this.url);
            });
        }
        return this.resolvedSchema;
    };
    SchemaHandle.prototype.clearSchema = function () {
        this.resolvedSchema = null;
        this.unresolvedSchema = null;
    };
    return SchemaHandle;
}());
var UnresolvedSchema = /** @class */ (function () {
    function UnresolvedSchema(schema, errors) {
        if (errors === void 0) { errors = []; }
        this.schema = schema;
        this.errors = errors;
    }
    return UnresolvedSchema;
}());

var jsonSchemaService_ResolvedSchema = /** @class */ (function () {
    function ResolvedSchema(schema, errors) {
        if (errors === void 0) { errors = []; }
        this.schema = schema;
        this.errors = errors;
    }
    ResolvedSchema.prototype.getSection = function (path) {
        return asSchema(this.getSectionRecursive(path, this.schema));
    };
    ResolvedSchema.prototype.getSectionRecursive = function (path, schema) {
        var _this = this;
        if (!schema || typeof schema === 'boolean' || path.length === 0) {
            return schema;
        }
        var next = path.shift();
        if (schema.properties && typeof schema.properties[next]) {
            return this.getSectionRecursive(path, schema.properties[next]);
        }
        else if (schema.patternProperties) {
            Object.keys(schema.patternProperties).forEach(function (pattern) {
                var regex = new RegExp(pattern);
                if (regex.test(next)) {
                    return _this.getSectionRecursive(path, schema.patternProperties[pattern]);
                }
            });
        }
        else if (typeof schema.additionalProperties === 'object') {
            return this.getSectionRecursive(path, schema.additionalProperties);
        }
        else if (next.match('[0-9]+')) {
            if (Array.isArray(schema.items)) {
                var index = parseInt(next, 10);
                if (!isNaN(index) && schema.items[index]) {
                    return this.getSectionRecursive(path, schema.items[index]);
                }
            }
            else if (schema.items) {
                return this.getSectionRecursive(path, schema.items);
            }
        }
        return null;
    };
    return ResolvedSchema;
}());

var jsonSchemaService_JSONSchemaService = /** @class */ (function () {
    function JSONSchemaService(requestService, contextService, promiseConstructor) {
        this.contextService = contextService;
        this.requestService = requestService;
        this.promiseConstructor = promiseConstructor || Promise;
        this.callOnDispose = [];
        this.contributionSchemas = {};
        this.contributionAssociations = {};
        this.schemasById = {};
        this.filePatternAssociations = [];
        this.filePatternAssociationById = {};
        this.registeredSchemasIds = {};
    }
    JSONSchemaService.prototype.getRegisteredSchemaIds = function (filter) {
        return Object.keys(this.registeredSchemasIds).filter(function (id) {
            var scheme = vscode_uri["a" /* default */].parse(id).scheme;
            return scheme !== 'schemaservice' && (!filter || filter(scheme));
        });
    };
    Object.defineProperty(JSONSchemaService.prototype, "promise", {
        get: function () {
            return this.promiseConstructor;
        },
        enumerable: true,
        configurable: true
    });
    JSONSchemaService.prototype.dispose = function () {
        while (this.callOnDispose.length > 0) {
            this.callOnDispose.pop()();
        }
    };
    JSONSchemaService.prototype.onResourceChange = function (uri) {
        uri = this.normalizeId(uri);
        var schemaFile = this.schemasById[uri];
        if (schemaFile) {
            schemaFile.clearSchema();
            return true;
        }
        return false;
    };
    JSONSchemaService.prototype.normalizeId = function (id) {
        // remove trailing '#', normalize drive capitalization
        return vscode_uri["a" /* default */].parse(id).toString();
    };
    JSONSchemaService.prototype.setSchemaContributions = function (schemaContributions) {
        var _this = this;
        if (schemaContributions.schemas) {
            var schemas = schemaContributions.schemas;
            for (var id in schemas) {
                var normalizedId = this.normalizeId(id);
                this.contributionSchemas[normalizedId] = this.addSchemaHandle(normalizedId, schemas[id]);
            }
        }
        if (schemaContributions.schemaAssociations) {
            var schemaAssociations = schemaContributions.schemaAssociations;
            for (var pattern in schemaAssociations) {
                var associations = schemaAssociations[pattern];
                this.contributionAssociations[pattern] = associations;
                var fpa = this.getOrAddFilePatternAssociation(pattern);
                associations.forEach(function (schemaId) {
                    var id = _this.normalizeId(schemaId);
                    fpa.addSchema(id);
                });
            }
        }
    };
    JSONSchemaService.prototype.addSchemaHandle = function (id, unresolvedSchemaContent) {
        var schemaHandle = new SchemaHandle(this, id, unresolvedSchemaContent);
        this.schemasById[id] = schemaHandle;
        return schemaHandle;
    };
    JSONSchemaService.prototype.getOrAddSchemaHandle = function (id, unresolvedSchemaContent) {
        return this.schemasById[id] || this.addSchemaHandle(id, unresolvedSchemaContent);
    };
    JSONSchemaService.prototype.getOrAddFilePatternAssociation = function (pattern) {
        var fpa = this.filePatternAssociationById[pattern];
        if (!fpa) {
            fpa = new jsonSchemaService_FilePatternAssociation(pattern);
            this.filePatternAssociationById[pattern] = fpa;
            this.filePatternAssociations.push(fpa);
        }
        return fpa;
    };
    JSONSchemaService.prototype.registerExternalSchema = function (uri, filePatterns, unresolvedSchemaContent) {
        var _this = this;
        if (filePatterns === void 0) { filePatterns = null; }
        var id = this.normalizeId(uri);
        this.registeredSchemasIds[id] = true;
        if (filePatterns) {
            filePatterns.forEach(function (pattern) {
                _this.getOrAddFilePatternAssociation(pattern).addSchema(id);
            });
        }
        return unresolvedSchemaContent ? this.addSchemaHandle(id, unresolvedSchemaContent) : this.getOrAddSchemaHandle(id);
    };
    JSONSchemaService.prototype.clearExternalSchemas = function () {
        var _this = this;
        this.schemasById = {};
        this.filePatternAssociations = [];
        this.filePatternAssociationById = {};
        this.registeredSchemasIds = {};
        for (var id in this.contributionSchemas) {
            this.schemasById[id] = this.contributionSchemas[id];
            this.registeredSchemasIds[id] = true;
        }
        for (var pattern in this.contributionAssociations) {
            var fpa = this.getOrAddFilePatternAssociation(pattern);
            this.contributionAssociations[pattern].forEach(function (schemaId) {
                var id = _this.normalizeId(schemaId);
                fpa.addSchema(id);
            });
        }
    };
    JSONSchemaService.prototype.getResolvedSchema = function (schemaId) {
        var id = this.normalizeId(schemaId);
        var schemaHandle = this.schemasById[id];
        if (schemaHandle) {
            return schemaHandle.getResolvedSchema();
        }
        return this.promise.resolve(null);
    };
    JSONSchemaService.prototype.loadSchema = function (url) {
        if (!this.requestService) {
            var errorMessage = jsonSchemaService_localize('json.schema.norequestservice', 'Unable to load schema from \'{0}\'. No schema request service available', toDisplayString(url));
            return this.promise.resolve(new UnresolvedSchema({}, [errorMessage]));
        }
        return this.requestService(url).then(function (content) {
            if (!content) {
                var errorMessage = jsonSchemaService_localize('json.schema.nocontent', 'Unable to load schema from \'{0}\': No content.', toDisplayString(url));
                return new UnresolvedSchema({}, [errorMessage]);
            }
            var schemaContent = {};
            var jsonErrors = [];
            schemaContent = main_parse(content, jsonErrors);
            var errors = jsonErrors.length ? [jsonSchemaService_localize('json.schema.invalidFormat', 'Unable to parse content from \'{0}\': Parse error at offset {1}.', toDisplayString(url), jsonErrors[0].offset)] : [];
            return new UnresolvedSchema(schemaContent, errors);
        }, function (error) {
            var errorMessage = jsonSchemaService_localize('json.schema.unabletoload', 'Unable to load schema from \'{0}\': {1}', toDisplayString(url), error.toString());
            return new UnresolvedSchema({}, [errorMessage]);
        });
    };
    JSONSchemaService.prototype.resolveSchemaContent = function (schemaToResolve, schemaURL) {
        var _this = this;
        var resolveErrors = schemaToResolve.errors.slice(0);
        var schema = schemaToResolve.schema;
        var contextService = this.contextService;
        var findSection = function (schema, path) {
            if (!path) {
                return schema;
            }
            var current = schema;
            if (path[0] === '/') {
                path = path.substr(1);
            }
            path.split('/').some(function (part) {
                current = current[part];
                return !current;
            });
            return current;
        };
        var merge = function (target, sourceRoot, sourceURI, path) {
            var section = findSection(sourceRoot, path);
            if (section) {
                for (var key in section) {
                    if (section.hasOwnProperty(key) && !target.hasOwnProperty(key)) {
                        target[key] = section[key];
                    }
                }
            }
            else {
                resolveErrors.push(jsonSchemaService_localize('json.schema.invalidref', '$ref \'{0}\' in \'{1}\' can not be resolved.', path, sourceURI));
            }
        };
        var resolveExternalLink = function (node, uri, linkPath, parentSchemaURL) {
            if (contextService && !/^\w+:\/\/.*/.test(uri)) {
                uri = contextService.resolveRelativePath(uri, parentSchemaURL);
            }
            uri = _this.normalizeId(uri);
            return _this.getOrAddSchemaHandle(uri).getUnresolvedSchema().then(function (unresolvedSchema) {
                if (unresolvedSchema.errors.length) {
                    var loc = linkPath ? uri + '#' + linkPath : uri;
                    resolveErrors.push(jsonSchemaService_localize('json.schema.problemloadingref', 'Problems loading reference \'{0}\': {1}', loc, unresolvedSchema.errors[0]));
                }
                merge(node, unresolvedSchema.schema, uri, linkPath);
                return resolveRefs(node, unresolvedSchema.schema, uri);
            });
        };
        var resolveRefs = function (node, parentSchema, parentSchemaURL) {
            if (!node || typeof node !== 'object') {
                return Promise.resolve(null);
            }
            var toWalk = [node];
            var seen = [];
            var openPromises = [];
            var collectEntries = function () {
                var entries = [];
                for (var _i = 0; _i < arguments.length; _i++) {
                    entries[_i] = arguments[_i];
                }
                for (var _a = 0, entries_1 = entries; _a < entries_1.length; _a++) {
                    var entry = entries_1[_a];
                    if (typeof entry === 'object') {
                        toWalk.push(entry);
                    }
                }
            };
            var collectMapEntries = function () {
                var maps = [];
                for (var _i = 0; _i < arguments.length; _i++) {
                    maps[_i] = arguments[_i];
                }
                for (var _a = 0, maps_1 = maps; _a < maps_1.length; _a++) {
                    var map = maps_1[_a];
                    if (typeof map === 'object') {
                        for (var key in map) {
                            var entry = map[key];
                            if (typeof entry === 'object') {
                                toWalk.push(entry);
                            }
                        }
                    }
                }
            };
            var collectArrayEntries = function () {
                var arrays = [];
                for (var _i = 0; _i < arguments.length; _i++) {
                    arrays[_i] = arguments[_i];
                }
                for (var _a = 0, arrays_1 = arrays; _a < arrays_1.length; _a++) {
                    var array = arrays_1[_a];
                    if (Array.isArray(array)) {
                        for (var _b = 0, array_1 = array; _b < array_1.length; _b++) {
                            var entry = array_1[_b];
                            if (typeof entry === 'object') {
                                toWalk.push(entry);
                            }
                        }
                    }
                }
            };
            var handleRef = function (next) {
                while (next.$ref) {
                    var segments = next.$ref.split('#', 2);
                    delete next.$ref;
                    if (segments[0].length > 0) {
                        openPromises.push(resolveExternalLink(next, segments[0], segments[1], parentSchemaURL));
                        return;
                    }
                    else {
                        merge(next, parentSchema, parentSchemaURL, segments[1]); // can set next.$ref again
                    }
                }
                collectEntries(next.items, next.additionalProperties, next.not, next.contains, next.propertyNames);
                collectMapEntries(next.definitions, next.properties, next.patternProperties, next.dependencies);
                collectArrayEntries(next.anyOf, next.allOf, next.oneOf, next.items);
            };
            while (toWalk.length) {
                var next = toWalk.pop();
                if (seen.indexOf(next) >= 0) {
                    continue;
                }
                seen.push(next);
                handleRef(next);
            }
            return _this.promise.all(openPromises);
        };
        return resolveRefs(schema, schema, schemaURL).then(function (_) { return new jsonSchemaService_ResolvedSchema(schema, resolveErrors); });
    };
    JSONSchemaService.prototype.getSchemaForResource = function (resource, document) {
        // first use $schema if present
        if (document && document.root && document.root.type === 'object') {
            var schemaProperties = document.root.properties.filter(function (p) { return (p.keyNode.value === '$schema') && p.valueNode && p.valueNode.type === 'string'; });
            if (schemaProperties.length > 0) {
                var schemeId = jsonParser_getNodeValue(schemaProperties[0].valueNode);
                if (schemeId && strings_startsWith(schemeId, '.') && this.contextService) {
                    schemeId = this.contextService.resolveRelativePath(schemeId, resource);
                }
                if (schemeId) {
                    var id = this.normalizeId(schemeId);
                    return this.getOrAddSchemaHandle(id).getResolvedSchema();
                }
            }
        }
        var seen = Object.create(null);
        var schemas = [];
        for (var _i = 0, _a = this.filePatternAssociations; _i < _a.length; _i++) {
            var entry = _a[_i];
            if (entry.matchesPattern(resource)) {
                for (var _b = 0, _c = entry.getSchemas(); _b < _c.length; _b++) {
                    var schemaId = _c[_b];
                    if (!seen[schemaId]) {
                        schemas.push(schemaId);
                        seen[schemaId] = true;
                    }
                }
            }
        }
        if (schemas.length > 0) {
            return this.createCombinedSchema(resource, schemas).getResolvedSchema();
        }
        return this.promise.resolve(null);
    };
    JSONSchemaService.prototype.createCombinedSchema = function (resource, schemaIds) {
        if (schemaIds.length === 1) {
            return this.getOrAddSchemaHandle(schemaIds[0]);
        }
        else {
            var combinedSchemaId = 'schemaservice://combinedSchema/' + encodeURIComponent(resource);
            var combinedSchema = {
                allOf: schemaIds.map(function (schemaId) { return ({ $ref: schemaId }); })
            };
            return this.addSchemaHandle(combinedSchemaId, combinedSchema);
        }
    };
    return JSONSchemaService;
}());

function toDisplayString(url) {
    try {
        var uri = vscode_uri["a" /* default */].parse(url);
        if (uri.scheme === 'file') {
            return uri.fsPath;
        }
    }
    catch (e) {
        // ignore
    }
    return url;
}
//# sourceMappingURL=jsonSchemaService.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-json-languageservice/jsonLanguageService.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/











function getLanguageService(params) {
    var promise = params.promiseConstructor || Promise;
    var jsonSchemaService = new jsonSchemaService_JSONSchemaService(params.schemaRequestService, params.workspaceContext, promise);
    jsonSchemaService.setSchemaContributions(schemaContributions);
    var jsonCompletion = new jsonCompletion_JSONCompletion(jsonSchemaService, params.contributions, promise);
    var jsonHover = new jsonHover_JSONHover(jsonSchemaService, params.contributions, promise);
    var jsonDocumentSymbols = new jsonDocumentSymbols_JSONDocumentSymbols(jsonSchemaService);
    var jsonValidation = new jsonValidation_JSONValidation(jsonSchemaService, promise);
    return {
        configure: function (settings) {
            jsonSchemaService.clearExternalSchemas();
            if (settings.schemas) {
                settings.schemas.forEach(function (settings) {
                    jsonSchemaService.registerExternalSchema(settings.uri, settings.fileMatch, settings.schema);
                });
            }
            jsonValidation.configure(settings);
        },
        resetSchema: function (uri) { return jsonSchemaService.onResourceChange(uri); },
        doValidation: jsonValidation.doValidation.bind(jsonValidation),
        parseJSONDocument: function (document) { return jsonParser_parse(document, { collectComments: true }); },
        newJSONDocument: function (root, diagnostics) { return newJSONDocument(root, diagnostics); },
        doResolve: jsonCompletion.doResolve.bind(jsonCompletion),
        doComplete: jsonCompletion.doComplete.bind(jsonCompletion),
        findDocumentSymbols: jsonDocumentSymbols.findDocumentSymbols.bind(jsonDocumentSymbols),
        findColorSymbols: function (d, s) { return jsonDocumentSymbols.findDocumentColors(d, s).then(function (s) { return s.map(function (s) { return s.range; }); }); },
        findDocumentColors: jsonDocumentSymbols.findDocumentColors.bind(jsonDocumentSymbols),
        getColorPresentations: jsonDocumentSymbols.getColorPresentations.bind(jsonDocumentSymbols),
        doHover: jsonHover.doHover.bind(jsonHover),
        format: function (d, r, o) {
            var range = void 0;
            if (r) {
                var offset = d.offsetAt(r.start);
                var length = d.offsetAt(r.end) - offset;
                range = { offset: offset, length: length };
            }
            var options = { tabSize: o ? o.tabSize : 4, insertSpaces: o ? o.insertSpaces : true, eol: '\n' };
            return main_format(d.getText(), range, options).map(function (e) {
                return TextEdit.replace(main_Range.create(d.positionAt(e.offset), d.positionAt(e.offset + e.length)), e.content);
            });
        }
    };
}
//# sourceMappingURL=jsonLanguageService.js.map
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/jsonWorker.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var jsonWorker_Promise = monaco.Promise;


var PromiseAdapter = /** @class */ (function () {
    function PromiseAdapter(executor) {
        this.wrapped = new monaco.Promise(executor);
    }
    PromiseAdapter.prototype.then = function (onfulfilled, onrejected) {
        var thenable = this.wrapped;
        return thenable.then(onfulfilled, onrejected);
    };
    PromiseAdapter.prototype.getWrapped = function () {
        return this.wrapped;
    };
    PromiseAdapter.prototype.cancel = function () {
        this.wrapped.cancel();
    };
    PromiseAdapter.resolve = function (v) {
        return monaco.Promise.as(v);
    };
    PromiseAdapter.reject = function (v) {
        return monaco.Promise.wrapError(v);
    };
    PromiseAdapter.all = function (values) {
        return monaco.Promise.join(values);
    };
    return PromiseAdapter;
}());
var jsonWorker_JSONWorker = /** @class */ (function () {
    function JSONWorker(ctx, createData) {
        this._ctx = ctx;
        this._languageSettings = createData.languageSettings;
        this._languageId = createData.languageId;
        this._languageService = getLanguageService({ promiseConstructor: PromiseAdapter });
        this._languageService.configure(this._languageSettings);
    }
    JSONWorker.prototype.doValidation = function (uri) {
        var document = this._getTextDocument(uri);
        if (document) {
            var jsonDocument = this._languageService.parseJSONDocument(document);
            return this._languageService.doValidation(document, jsonDocument);
        }
        return jsonWorker_Promise.as([]);
    };
    JSONWorker.prototype.doComplete = function (uri, position) {
        var document = this._getTextDocument(uri);
        var jsonDocument = this._languageService.parseJSONDocument(document);
        return this._languageService.doComplete(document, position, jsonDocument);
    };
    JSONWorker.prototype.doResolve = function (item) {
        return this._languageService.doResolve(item);
    };
    JSONWorker.prototype.doHover = function (uri, position) {
        var document = this._getTextDocument(uri);
        var jsonDocument = this._languageService.parseJSONDocument(document);
        return this._languageService.doHover(document, position, jsonDocument);
    };
    JSONWorker.prototype.format = function (uri, range, options) {
        var document = this._getTextDocument(uri);
        var textEdits = this._languageService.format(document, range, options);
        return jsonWorker_Promise.as(textEdits);
    };
    JSONWorker.prototype.resetSchema = function (uri) {
        return jsonWorker_Promise.as(this._languageService.resetSchema(uri));
    };
    JSONWorker.prototype.findDocumentSymbols = function (uri) {
        var document = this._getTextDocument(uri);
        var jsonDocument = this._languageService.parseJSONDocument(document);
        var symbols = this._languageService.findDocumentSymbols(document, jsonDocument);
        return jsonWorker_Promise.as(symbols);
    };
    JSONWorker.prototype.findDocumentColors = function (uri) {
        var document = this._getTextDocument(uri);
        var stylesheet = this._languageService.parseJSONDocument(document);
        var colorSymbols = this._languageService.findDocumentColors(document, stylesheet);
        return jsonWorker_Promise.as(colorSymbols);
    };
    JSONWorker.prototype.getColorPresentations = function (uri, color, range) {
        var document = this._getTextDocument(uri);
        var stylesheet = this._languageService.parseJSONDocument(document);
        var colorPresentations = this._languageService.getColorPresentations(document, stylesheet, color, range);
        return jsonWorker_Promise.as(colorPresentations);
    };
    JSONWorker.prototype._getTextDocument = function (uri) {
        var models = this._ctx.getMirrorModels();
        for (var _i = 0, models_1 = models; _i < models_1.length; _i++) {
            var model = models_1[_i];
            if (model.uri.toString() === uri) {
                return TextDocument.create(uri, this._languageId, model.version, model.getValue());
            }
        }
        return null;
    };
    return JSONWorker;
}());

function jsonWorker_create(ctx, createData) {
    return new jsonWorker_JSONWorker(ctx, createData);
}

// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/json/json.worker.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/



self.onmessage = function () {
    // ignore the first message
    initialize(function (ctx, createData) {
        return new jsonWorker_JSONWorker(ctx, createData);
    });
};


/***/ }),

/***/ "8oxB":
/***/ (function(module, exports) {

// shim for using process in browser
var process = module.exports = {};

// cached from whatever global is present so that test runners that stub it
// don't break things.  But we need to wrap it in a try catch in case it is
// wrapped in strict mode code which doesn't define any globals.  It's inside a
// function because try/catches deoptimize in certain engines.

var cachedSetTimeout;
var cachedClearTimeout;

function defaultSetTimout() {
    throw new Error('setTimeout has not been defined');
}
function defaultClearTimeout () {
    throw new Error('clearTimeout has not been defined');
}
(function () {
    try {
        if (typeof setTimeout === 'function') {
            cachedSetTimeout = setTimeout;
        } else {
            cachedSetTimeout = defaultSetTimout;
        }
    } catch (e) {
        cachedSetTimeout = defaultSetTimout;
    }
    try {
        if (typeof clearTimeout === 'function') {
            cachedClearTimeout = clearTimeout;
        } else {
            cachedClearTimeout = defaultClearTimeout;
        }
    } catch (e) {
        cachedClearTimeout = defaultClearTimeout;
    }
} ())
function runTimeout(fun) {
    if (cachedSetTimeout === setTimeout) {
        //normal enviroments in sane situations
        return setTimeout(fun, 0);
    }
    // if setTimeout wasn't available but was latter defined
    if ((cachedSetTimeout === defaultSetTimout || !cachedSetTimeout) && setTimeout) {
        cachedSetTimeout = setTimeout;
        return setTimeout(fun, 0);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedSetTimeout(fun, 0);
    } catch(e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't trust the global object when called normally
            return cachedSetTimeout.call(null, fun, 0);
        } catch(e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error
            return cachedSetTimeout.call(this, fun, 0);
        }
    }


}
function runClearTimeout(marker) {
    if (cachedClearTimeout === clearTimeout) {
        //normal enviroments in sane situations
        return clearTimeout(marker);
    }
    // if clearTimeout wasn't available but was latter defined
    if ((cachedClearTimeout === defaultClearTimeout || !cachedClearTimeout) && clearTimeout) {
        cachedClearTimeout = clearTimeout;
        return clearTimeout(marker);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedClearTimeout(marker);
    } catch (e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't  trust the global object when called normally
            return cachedClearTimeout.call(null, marker);
        } catch (e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error.
            // Some versions of I.E. have different rules for clearTimeout vs setTimeout
            return cachedClearTimeout.call(this, marker);
        }
    }



}
var queue = [];
var draining = false;
var currentQueue;
var queueIndex = -1;

function cleanUpNextTick() {
    if (!draining || !currentQueue) {
        return;
    }
    draining = false;
    if (currentQueue.length) {
        queue = currentQueue.concat(queue);
    } else {
        queueIndex = -1;
    }
    if (queue.length) {
        drainQueue();
    }
}

function drainQueue() {
    if (draining) {
        return;
    }
    var timeout = runTimeout(cleanUpNextTick);
    draining = true;

    var len = queue.length;
    while(len) {
        currentQueue = queue;
        queue = [];
        while (++queueIndex < len) {
            if (currentQueue) {
                currentQueue[queueIndex].run();
            }
        }
        queueIndex = -1;
        len = queue.length;
    }
    currentQueue = null;
    draining = false;
    runClearTimeout(timeout);
}

process.nextTick = function (fun) {
    var args = new Array(arguments.length - 1);
    if (arguments.length > 1) {
        for (var i = 1; i < arguments.length; i++) {
            args[i - 1] = arguments[i];
        }
    }
    queue.push(new Item(fun, args));
    if (queue.length === 1 && !draining) {
        runTimeout(drainQueue);
    }
};

// v8 likes predictible objects
function Item(fun, array) {
    this.fun = fun;
    this.array = array;
}
Item.prototype.run = function () {
    this.fun.apply(null, this.array);
};
process.title = 'browser';
process.browser = true;
process.env = {};
process.argv = [];
process.version = ''; // empty string to avoid regexp issues
process.versions = {};

function noop() {}

process.on = noop;
process.addListener = noop;
process.once = noop;
process.off = noop;
process.removeListener = noop;
process.removeAllListeners = noop;
process.emit = noop;
process.prependListener = noop;
process.prependOnceListener = noop;

process.listeners = function (name) { return [] }

process.binding = function (name) {
    throw new Error('process.binding is not supported');
};

process.cwd = function () { return '/' };
process.chdir = function (dir) {
    throw new Error('process.chdir is not supported');
};
process.umask = function() { return 0; };


/***/ }),

/***/ "ChvI":
/***/ (function(module, exports) {

function webpackEmptyContext(req) {
	var e = new Error("Cannot find module '" + req + "'");
	e.code = 'MODULE_NOT_FOUND';
	throw e;
}
webpackEmptyContext.keys = function() { return []; };
webpackEmptyContext.resolve = webpackEmptyContext;
module.exports = webpackEmptyContext;
webpackEmptyContext.id = "ChvI";

/***/ }),

/***/ "MNsG":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(process, global) {/* unused harmony export LANGUAGE_DEFAULT */
/* unused harmony export Platform */
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "c", function() { return isWindows; });
/* unused harmony export isMacintosh */
/* unused harmony export isLinux */
/* unused harmony export isNative */
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "b", function() { return isWeb; });
/* unused harmony export platform */
/* unused harmony export isRootUser */
/* unused harmony export language */
/* unused harmony export locale */
/* unused harmony export translationsConfigFile */
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return globals; });
/* unused harmony export setImmediate */
/* unused harmony export OS */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

var _isWindows = false;
var _isMacintosh = false;
var _isLinux = false;
var _isNative = false;
var _isWeb = false;
var _locale = undefined;
var _language = undefined;
var _translationsConfigFile = undefined;
var LANGUAGE_DEFAULT = 'en';
// OS detection
if (typeof process === 'object' && typeof process.nextTick === 'function' && typeof process.platform === 'string') {
    _isWindows = (process.platform === 'win32');
    _isMacintosh = (process.platform === 'darwin');
    _isLinux = (process.platform === 'linux');
    var rawNlsConfig = process.env['VSCODE_NLS_CONFIG'];
    if (rawNlsConfig) {
        try {
            var nlsConfig = JSON.parse(rawNlsConfig);
            var resolved = nlsConfig.availableLanguages['*'];
            _locale = nlsConfig.locale;
            // VSCode's default language is 'en'
            _language = resolved ? resolved : LANGUAGE_DEFAULT;
            _translationsConfigFile = nlsConfig._translationsConfigFile;
        }
        catch (e) {
        }
    }
    _isNative = true;
}
else if (typeof navigator === 'object') {
    var userAgent = navigator.userAgent;
    _isWindows = userAgent.indexOf('Windows') >= 0;
    _isMacintosh = userAgent.indexOf('Macintosh') >= 0;
    _isLinux = userAgent.indexOf('Linux') >= 0;
    _isWeb = true;
    _locale = navigator.language;
    _language = _locale;
}
var Platform;
(function (Platform) {
    Platform[Platform["Web"] = 0] = "Web";
    Platform[Platform["Mac"] = 1] = "Mac";
    Platform[Platform["Linux"] = 2] = "Linux";
    Platform[Platform["Windows"] = 3] = "Windows";
})(Platform || (Platform = {}));
var _platform = Platform.Web;
if (_isNative) {
    if (_isMacintosh) {
        _platform = Platform.Mac;
    }
    else if (_isWindows) {
        _platform = Platform.Windows;
    }
    else if (_isLinux) {
        _platform = Platform.Linux;
    }
}
var isWindows = _isWindows;
var isMacintosh = _isMacintosh;
var isLinux = _isLinux;
var isNative = _isNative;
var isWeb = _isWeb;
var platform = _platform;
function isRootUser() {
    return _isNative && !_isWindows && (process.getuid() === 0);
}
/**
 * The language used for the user interface. The format of
 * the string is all lower case (e.g. zh-tw for Traditional
 * Chinese)
 */
var language = _language;
/**
 * The OS locale or the locale specified by --locale. The format of
 * the string is all lower case (e.g. zh-tw for Traditional
 * Chinese). The UI is not necessarily shown in the provided locale.
 */
var locale = _locale;
/**
 * The translatios that are available through language packs.
 */
var translationsConfigFile = _translationsConfigFile;
var _globals = (typeof self === 'object' ? self : typeof global === 'object' ? global : {});
var globals = _globals;
var _setImmediate = null;
function setImmediate(callback) {
    if (_setImmediate === null) {
        if (globals.setImmediate) {
            _setImmediate = globals.setImmediate.bind(globals);
        }
        else if (typeof process !== 'undefined' && typeof process.nextTick === 'function') {
            _setImmediate = process.nextTick.bind(process);
        }
        else {
            _setImmediate = globals.setTimeout.bind(globals);
        }
    }
    return _setImmediate(callback);
}
var OS = (_isMacintosh ? 2 /* Macintosh */ : (_isWindows ? 1 /* Windows */ : 3 /* Linux */));

/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__("8oxB"), __webpack_require__("yLpj")))

/***/ }),

/***/ "VMIq":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(global, process) {/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return Promise; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "b", function() { return TPromise; });
/* unused harmony export PPromise */
/**
 * Extracted from https://github.com/winjs/winjs
 * Version: 4.4.0(ec3258a9f3a36805a187848984e3bb938044178d)
 * Copyright (c) Microsoft Corporation.
 * All Rights Reserved.
 * Licensed under the MIT License.
 */
var __winjs_exports;

(function() {

var _modules = Object.create(null);//{};
_modules["WinJS/Core/_WinJS"] = {};

var _winjs = function(moduleId, deps, factory) {
    var exports = {};
    var exportsPassedIn = false;

    var depsValues = deps.map(function(dep) {
        if (dep === 'exports') {
            exportsPassedIn = true;
            return exports;
        }
        return _modules[dep];
    });

    var result = factory.apply({}, depsValues);

    _modules[moduleId] = exportsPassedIn ? exports : result;
};


_winjs("WinJS/Core/_Global", [], function () {
    "use strict";

    // Appease jshint
    /* global window, self, global */

    var globalObject =
        typeof window !== 'undefined' ? window :
        typeof self !== 'undefined' ? self :
        typeof global !== 'undefined' ? global :
        {};
    return globalObject;
});

_winjs("WinJS/Core/_BaseCoreUtils", ["WinJS/Core/_Global"], function baseCoreUtilsInit(_Global) {
    "use strict";

    var hasWinRT = !!_Global.Windows;

    function markSupportedForProcessing(func) {
        /// <signature helpKeyword="WinJS.Utilities.markSupportedForProcessing">
        /// <summary locid="WinJS.Utilities.markSupportedForProcessing">
        /// Marks a function as being compatible with declarative processing, such as WinJS.UI.processAll
        /// or WinJS.Binding.processAll.
        /// </summary>
        /// <param name="func" type="Function" locid="WinJS.Utilities.markSupportedForProcessing_p:func">
        /// The function to be marked as compatible with declarative processing.
        /// </param>
        /// <returns type="Function" locid="WinJS.Utilities.markSupportedForProcessing_returnValue">
        /// The input function.
        /// </returns>
        /// </signature>
        func.supportedForProcessing = true;
        return func;
    }

    var actualSetImmediate = null;

    return {
        hasWinRT: hasWinRT,
        markSupportedForProcessing: markSupportedForProcessing,
        _setImmediate: function (callback) {
            // BEGIN monaco change
            if (actualSetImmediate === null) {
                if (_Global.setImmediate) {
                    actualSetImmediate = _Global.setImmediate.bind(_Global);
                } else if (typeof process !== 'undefined' && typeof process.nextTick === 'function') {
                    actualSetImmediate = process.nextTick.bind(process);
                } else {
                    actualSetImmediate = _Global.setTimeout.bind(_Global);
                }
            }
            actualSetImmediate(callback);
            // END monaco change
        }
    };
});
_winjs("WinJS/Core/_WriteProfilerMark", ["WinJS/Core/_Global"], function profilerInit(_Global) {
    "use strict";

    return _Global.msWriteProfilerMark || function () { };
});
_winjs("WinJS/Core/_Base", ["WinJS/Core/_WinJS","WinJS/Core/_Global","WinJS/Core/_BaseCoreUtils","WinJS/Core/_WriteProfilerMark"], function baseInit(_WinJS, _Global, _BaseCoreUtils, _WriteProfilerMark) {
    "use strict";

    function initializeProperties(target, members, prefix) {
        var keys = Object.keys(members);
        var isArray = Array.isArray(target);
        var properties;
        var i, len;
        for (i = 0, len = keys.length; i < len; i++) {
            var key = keys[i];
            var enumerable = key.charCodeAt(0) !== /*_*/95;
            var member = members[key];
            if (member && typeof member === 'object') {
                if (member.value !== undefined || typeof member.get === 'function' || typeof member.set === 'function') {
                    if (member.enumerable === undefined) {
                        member.enumerable = enumerable;
                    }
                    if (prefix && member.setName && typeof member.setName === 'function') {
                        member.setName(prefix + "." + key);
                    }
                    properties = properties || {};
                    properties[key] = member;
                    continue;
                }
            }
            if (!enumerable) {
                properties = properties || {};
                properties[key] = { value: member, enumerable: enumerable, configurable: true, writable: true };
                continue;
            }
            if (isArray) {
                target.forEach(function (target) {
                    target[key] = member;
                });
            } else {
                target[key] = member;
            }
        }
        if (properties) {
            if (isArray) {
                target.forEach(function (target) {
                    Object.defineProperties(target, properties);
                });
            } else {
                Object.defineProperties(target, properties);
            }
        }
    }

    (function () {

        var _rootNamespace = _WinJS;
        if (!_rootNamespace.Namespace) {
            _rootNamespace.Namespace = Object.create(Object.prototype);
        }

        function createNamespace(parentNamespace, name) {
            var currentNamespace = parentNamespace || {};
            if (name) {
                var namespaceFragments = name.split(".");
                if (currentNamespace === _Global && namespaceFragments[0] === "WinJS") {
                    currentNamespace = _WinJS;
                    namespaceFragments.splice(0, 1);
                }
                for (var i = 0, len = namespaceFragments.length; i < len; i++) {
                    var namespaceName = namespaceFragments[i];
                    if (!currentNamespace[namespaceName]) {
                        Object.defineProperty(currentNamespace, namespaceName,
                            { value: {}, writable: false, enumerable: true, configurable: true }
                        );
                    }
                    currentNamespace = currentNamespace[namespaceName];
                }
            }
            return currentNamespace;
        }

        function defineWithParent(parentNamespace, name, members) {
            /// <signature helpKeyword="WinJS.Namespace.defineWithParent">
            /// <summary locid="WinJS.Namespace.defineWithParent">
            /// Defines a new namespace with the specified name under the specified parent namespace.
            /// </summary>
            /// <param name="parentNamespace" type="Object" locid="WinJS.Namespace.defineWithParent_p:parentNamespace">
            /// The parent namespace.
            /// </param>
            /// <param name="name" type="String" locid="WinJS.Namespace.defineWithParent_p:name">
            /// The name of the new namespace.
            /// </param>
            /// <param name="members" type="Object" locid="WinJS.Namespace.defineWithParent_p:members">
            /// The members of the new namespace.
            /// </param>
            /// <returns type="Object" locid="WinJS.Namespace.defineWithParent_returnValue">
            /// The newly-defined namespace.
            /// </returns>
            /// </signature>
            var currentNamespace = createNamespace(parentNamespace, name);

            if (members) {
                initializeProperties(currentNamespace, members, name || "<ANONYMOUS>");
            }

            return currentNamespace;
        }

        function define(name, members) {
            /// <signature helpKeyword="WinJS.Namespace.define">
            /// <summary locid="WinJS.Namespace.define">
            /// Defines a new namespace with the specified name.
            /// </summary>
            /// <param name="name" type="String" locid="WinJS.Namespace.define_p:name">
            /// The name of the namespace. This could be a dot-separated name for nested namespaces.
            /// </param>
            /// <param name="members" type="Object" locid="WinJS.Namespace.define_p:members">
            /// The members of the new namespace.
            /// </param>
            /// <returns type="Object" locid="WinJS.Namespace.define_returnValue">
            /// The newly-defined namespace.
            /// </returns>
            /// </signature>
            return defineWithParent(_Global, name, members);
        }

        var LazyStates = {
            uninitialized: 1,
            working: 2,
            initialized: 3,
        };

        function lazy(f) {
            var name;
            var state = LazyStates.uninitialized;
            var result;
            return {
                setName: function (value) {
                    name = value;
                },
                get: function () {
                    switch (state) {
                        case LazyStates.initialized:
                            return result;

                        case LazyStates.uninitialized:
                            state = LazyStates.working;
                            try {
                                _WriteProfilerMark("WinJS.Namespace._lazy:" + name + ",StartTM");
                                result = f();
                            } finally {
                                _WriteProfilerMark("WinJS.Namespace._lazy:" + name + ",StopTM");
                                state = LazyStates.uninitialized;
                            }
                            f = null;
                            state = LazyStates.initialized;
                            return result;

                        case LazyStates.working:
                            throw "Illegal: reentrancy on initialization";

                        default:
                            throw "Illegal";
                    }
                },
                set: function (value) {
                    switch (state) {
                        case LazyStates.working:
                            throw "Illegal: reentrancy on initialization";

                        default:
                            state = LazyStates.initialized;
                            result = value;
                            break;
                    }
                },
                enumerable: true,
                configurable: true,
            };
        }

        // helper for defining AMD module members
        function moduleDefine(exports, name, members) {
            var target = [exports];
            var publicNS = null;
            if (name) {
                publicNS = createNamespace(_Global, name);
                target.push(publicNS);
            }
            initializeProperties(target, members, name || "<ANONYMOUS>");
            return publicNS;
        }

        // Establish members of the "WinJS.Namespace" namespace
        Object.defineProperties(_rootNamespace.Namespace, {

            defineWithParent: { value: defineWithParent, writable: true, enumerable: true, configurable: true },

            define: { value: define, writable: true, enumerable: true, configurable: true },

            _lazy: { value: lazy, writable: true, enumerable: true, configurable: true },

            _moduleDefine: { value: moduleDefine, writable: true, enumerable: true, configurable: true }

        });

    })();

    (function () {

        function define(constructor, instanceMembers, staticMembers) {
            /// <signature helpKeyword="WinJS.Class.define">
            /// <summary locid="WinJS.Class.define">
            /// Defines a class using the given constructor and the specified instance members.
            /// </summary>
            /// <param name="constructor" type="Function" locid="WinJS.Class.define_p:constructor">
            /// A constructor function that is used to instantiate this class.
            /// </param>
            /// <param name="instanceMembers" type="Object" locid="WinJS.Class.define_p:instanceMembers">
            /// The set of instance fields, properties, and methods made available on the class.
            /// </param>
            /// <param name="staticMembers" type="Object" locid="WinJS.Class.define_p:staticMembers">
            /// The set of static fields, properties, and methods made available on the class.
            /// </param>
            /// <returns type="Function" locid="WinJS.Class.define_returnValue">
            /// The newly-defined class.
            /// </returns>
            /// </signature>
            constructor = constructor || function () { };
            _BaseCoreUtils.markSupportedForProcessing(constructor);
            if (instanceMembers) {
                initializeProperties(constructor.prototype, instanceMembers);
            }
            if (staticMembers) {
                initializeProperties(constructor, staticMembers);
            }
            return constructor;
        }

        function derive(baseClass, constructor, instanceMembers, staticMembers) {
            /// <signature helpKeyword="WinJS.Class.derive">
            /// <summary locid="WinJS.Class.derive">
            /// Creates a sub-class based on the supplied baseClass parameter, using prototypal inheritance.
            /// </summary>
            /// <param name="baseClass" type="Function" locid="WinJS.Class.derive_p:baseClass">
            /// The class to inherit from.
            /// </param>
            /// <param name="constructor" type="Function" locid="WinJS.Class.derive_p:constructor">
            /// A constructor function that is used to instantiate this class.
            /// </param>
            /// <param name="instanceMembers" type="Object" locid="WinJS.Class.derive_p:instanceMembers">
            /// The set of instance fields, properties, and methods to be made available on the class.
            /// </param>
            /// <param name="staticMembers" type="Object" locid="WinJS.Class.derive_p:staticMembers">
            /// The set of static fields, properties, and methods to be made available on the class.
            /// </param>
            /// <returns type="Function" locid="WinJS.Class.derive_returnValue">
            /// The newly-defined class.
            /// </returns>
            /// </signature>
            if (baseClass) {
                constructor = constructor || function () { };
                var basePrototype = baseClass.prototype;
                constructor.prototype = Object.create(basePrototype);
                _BaseCoreUtils.markSupportedForProcessing(constructor);
                Object.defineProperty(constructor.prototype, "constructor", { value: constructor, writable: true, configurable: true, enumerable: true });
                if (instanceMembers) {
                    initializeProperties(constructor.prototype, instanceMembers);
                }
                if (staticMembers) {
                    initializeProperties(constructor, staticMembers);
                }
                return constructor;
            } else {
                return define(constructor, instanceMembers, staticMembers);
            }
        }

        function mix(constructor) {
            /// <signature helpKeyword="WinJS.Class.mix">
            /// <summary locid="WinJS.Class.mix">
            /// Defines a class using the given constructor and the union of the set of instance members
            /// specified by all the mixin objects. The mixin parameter list is of variable length.
            /// </summary>
            /// <param name="constructor" locid="WinJS.Class.mix_p:constructor">
            /// A constructor function that is used to instantiate this class.
            /// </param>
            /// <returns type="Function" locid="WinJS.Class.mix_returnValue">
            /// The newly-defined class.
            /// </returns>
            /// </signature>
            constructor = constructor || function () { };
            var i, len;
            for (i = 1, len = arguments.length; i < len; i++) {
                initializeProperties(constructor.prototype, arguments[i]);
            }
            return constructor;
        }

        // Establish members of "WinJS.Class" namespace
        _WinJS.Namespace.define("WinJS.Class", {
            define: define,
            derive: derive,
            mix: mix
        });

    })();

    return {
        Namespace: _WinJS.Namespace,
        Class: _WinJS.Class
    };

});
_winjs("WinJS/Core/_ErrorFromName", ["WinJS/Core/_Base"], function errorsInit(_Base) {
    "use strict";

    var ErrorFromName = _Base.Class.derive(Error, function (name, message) {
        /// <signature helpKeyword="WinJS.ErrorFromName">
        /// <summary locid="WinJS.ErrorFromName">
        /// Creates an Error object with the specified name and message properties.
        /// </summary>
        /// <param name="name" type="String" locid="WinJS.ErrorFromName_p:name">The name of this error. The name is meant to be consumed programmatically and should not be localized.</param>
        /// <param name="message" type="String" optional="true" locid="WinJS.ErrorFromName_p:message">The message for this error. The message is meant to be consumed by humans and should be localized.</param>
        /// <returns type="Error" locid="WinJS.ErrorFromName_returnValue">Error instance with .name and .message properties populated</returns>
        /// </signature>
        this.name = name;
        this.message = message || name;
    }, {
        /* empty */
    }, {
        supportedForProcessing: false,
    });

    _Base.Namespace.define("WinJS", {
        // ErrorFromName establishes a simple pattern for returning error codes.
        //
        ErrorFromName: ErrorFromName
    });

    return ErrorFromName;

});


_winjs("WinJS/Core/_Events", ["exports","WinJS/Core/_Base"], function eventsInit(exports, _Base) {
    "use strict";


    function createEventProperty(name) {
        var eventPropStateName = "_on" + name + "state";

        return {
            get: function () {
                var state = this[eventPropStateName];
                return state && state.userHandler;
            },
            set: function (handler) {
                var state = this[eventPropStateName];
                if (handler) {
                    if (!state) {
                        state = { wrapper: function (evt) { return state.userHandler(evt); }, userHandler: handler };
                        Object.defineProperty(this, eventPropStateName, { value: state, enumerable: false, writable:true, configurable: true });
                        this.addEventListener(name, state.wrapper, false);
                    }
                    state.userHandler = handler;
                } else if (state) {
                    this.removeEventListener(name, state.wrapper, false);
                    this[eventPropStateName] = null;
                }
            },
            enumerable: true
        };
    }

    function createEventProperties() {
        /// <signature helpKeyword="WinJS.Utilities.createEventProperties">
        /// <summary locid="WinJS.Utilities.createEventProperties">
        /// Creates an object that has one property for each name passed to the function.
        /// </summary>
        /// <param name="events" locid="WinJS.Utilities.createEventProperties_p:events">
        /// A variable list of property names.
        /// </param>
        /// <returns type="Object" locid="WinJS.Utilities.createEventProperties_returnValue">
        /// The object with the specified properties. The names of the properties are prefixed with 'on'.
        /// </returns>
        /// </signature>
        var props = {};
        for (var i = 0, len = arguments.length; i < len; i++) {
            var name = arguments[i];
            props["on" + name] = createEventProperty(name);
        }
        return props;
    }

    var EventMixinEvent = _Base.Class.define(
        function EventMixinEvent_ctor(type, detail, target) {
            this.detail = detail;
            this.target = target;
            this.timeStamp = Date.now();
            this.type = type;
        },
        {
            bubbles: { value: false, writable: false },
            cancelable: { value: false, writable: false },
            currentTarget: {
                get: function () { return this.target; }
            },
            defaultPrevented: {
                get: function () { return this._preventDefaultCalled; }
            },
            trusted: { value: false, writable: false },
            eventPhase: { value: 0, writable: false },
            target: null,
            timeStamp: null,
            type: null,

            preventDefault: function () {
                this._preventDefaultCalled = true;
            },
            stopImmediatePropagation: function () {
                this._stopImmediatePropagationCalled = true;
            },
            stopPropagation: function () {
            }
        }, {
            supportedForProcessing: false,
        }
    );

    var eventMixin = {
        _listeners: null,

        addEventListener: function (type, listener, useCapture) {
            /// <signature helpKeyword="WinJS.Utilities.eventMixin.addEventListener">
            /// <summary locid="WinJS.Utilities.eventMixin.addEventListener">
            /// Adds an event listener to the control.
            /// </summary>
            /// <param name="type" locid="WinJS.Utilities.eventMixin.addEventListener_p:type">
            /// The type (name) of the event.
            /// </param>
            /// <param name="listener" locid="WinJS.Utilities.eventMixin.addEventListener_p:listener">
            /// The listener to invoke when the event is raised.
            /// </param>
            /// <param name="useCapture" locid="WinJS.Utilities.eventMixin.addEventListener_p:useCapture">
            /// if true initiates capture, otherwise false.
            /// </param>
            /// </signature>
            useCapture = useCapture || false;
            this._listeners = this._listeners || {};
            var eventListeners = (this._listeners[type] = this._listeners[type] || []);
            for (var i = 0, len = eventListeners.length; i < len; i++) {
                var l = eventListeners[i];
                if (l.useCapture === useCapture && l.listener === listener) {
                    return;
                }
            }
            eventListeners.push({ listener: listener, useCapture: useCapture });
        },
        dispatchEvent: function (type, details) {
            /// <signature helpKeyword="WinJS.Utilities.eventMixin.dispatchEvent">
            /// <summary locid="WinJS.Utilities.eventMixin.dispatchEvent">
            /// Raises an event of the specified type and with the specified additional properties.
            /// </summary>
            /// <param name="type" locid="WinJS.Utilities.eventMixin.dispatchEvent_p:type">
            /// The type (name) of the event.
            /// </param>
            /// <param name="details" locid="WinJS.Utilities.eventMixin.dispatchEvent_p:details">
            /// The set of additional properties to be attached to the event object when the event is raised.
            /// </param>
            /// <returns type="Boolean" locid="WinJS.Utilities.eventMixin.dispatchEvent_returnValue">
            /// true if preventDefault was called on the event.
            /// </returns>
            /// </signature>
            var listeners = this._listeners && this._listeners[type];
            if (listeners) {
                var eventValue = new EventMixinEvent(type, details, this);
                // Need to copy the array to protect against people unregistering while we are dispatching
                listeners = listeners.slice(0, listeners.length);
                for (var i = 0, len = listeners.length; i < len && !eventValue._stopImmediatePropagationCalled; i++) {
                    listeners[i].listener(eventValue);
                }
                return eventValue.defaultPrevented || false;
            }
            return false;
        },
        removeEventListener: function (type, listener, useCapture) {
            /// <signature helpKeyword="WinJS.Utilities.eventMixin.removeEventListener">
            /// <summary locid="WinJS.Utilities.eventMixin.removeEventListener">
            /// Removes an event listener from the control.
            /// </summary>
            /// <param name="type" locid="WinJS.Utilities.eventMixin.removeEventListener_p:type">
            /// The type (name) of the event.
            /// </param>
            /// <param name="listener" locid="WinJS.Utilities.eventMixin.removeEventListener_p:listener">
            /// The listener to remove.
            /// </param>
            /// <param name="useCapture" locid="WinJS.Utilities.eventMixin.removeEventListener_p:useCapture">
            /// Specifies whether to initiate capture.
            /// </param>
            /// </signature>
            useCapture = useCapture || false;
            var listeners = this._listeners && this._listeners[type];
            if (listeners) {
                for (var i = 0, len = listeners.length; i < len; i++) {
                    var l = listeners[i];
                    if (l.listener === listener && l.useCapture === useCapture) {
                        listeners.splice(i, 1);
                        if (listeners.length === 0) {
                            delete this._listeners[type];
                        }
                        // Only want to remove one element for each call to removeEventListener
                        break;
                    }
                }
            }
        }
    };

    _Base.Namespace._moduleDefine(exports, "WinJS.Utilities", {
        _createEventProperty: createEventProperty,
        createEventProperties: createEventProperties,
        eventMixin: eventMixin
    });

});


_winjs("WinJS/Core/_Trace", ["WinJS/Core/_Global"], function traceInit(_Global) {
    "use strict";

    function nop(v) {
        return v;
    }

    return {
        _traceAsyncOperationStarting: (_Global.Debug && _Global.Debug.msTraceAsyncOperationStarting && _Global.Debug.msTraceAsyncOperationStarting.bind(_Global.Debug)) || nop,
        _traceAsyncOperationCompleted: (_Global.Debug && _Global.Debug.msTraceAsyncOperationCompleted && _Global.Debug.msTraceAsyncOperationCompleted.bind(_Global.Debug)) || nop,
        _traceAsyncCallbackStarting: (_Global.Debug && _Global.Debug.msTraceAsyncCallbackStarting && _Global.Debug.msTraceAsyncCallbackStarting.bind(_Global.Debug)) || nop,
        _traceAsyncCallbackCompleted: (_Global.Debug && _Global.Debug.msTraceAsyncCallbackCompleted && _Global.Debug.msTraceAsyncCallbackCompleted.bind(_Global.Debug)) || nop
    };
});
_winjs("WinJS/Promise/_StateMachine", ["WinJS/Core/_Global","WinJS/Core/_BaseCoreUtils","WinJS/Core/_Base","WinJS/Core/_ErrorFromName","WinJS/Core/_Events","WinJS/Core/_Trace"], function promiseStateMachineInit(_Global, _BaseCoreUtils, _Base, _ErrorFromName, _Events, _Trace) {
    "use strict";

    _Global.Debug && (_Global.Debug.setNonUserCodeExceptions = true);

    var ListenerType = _Base.Class.mix(_Base.Class.define(null, { /*empty*/ }, { supportedForProcessing: false }), _Events.eventMixin);
    var promiseEventListeners = new ListenerType();
    // make sure there is a listeners collection so that we can do a more trivial check below
    promiseEventListeners._listeners = {};
    var errorET = "error";
    var canceledName = "Canceled";
    var tagWithStack = false;
    var tag = {
        promise: 0x01,
        thenPromise: 0x02,
        errorPromise: 0x04,
        exceptionPromise: 0x08,
        completePromise: 0x10,
    };
    tag.all = tag.promise | tag.thenPromise | tag.errorPromise | tag.exceptionPromise | tag.completePromise;

    //
    // Global error counter, for each error which enters the system we increment this once and then
    // the error number travels with the error as it traverses the tree of potential handlers.
    //
    // When someone has registered to be told about errors (WinJS.Promise.callonerror) promises
    // which are in error will get tagged with a ._errorId field. This tagged field is the
    // contract by which nested promises with errors will be identified as chaining for the
    // purposes of the callonerror semantics. If a nested promise in error is encountered without
    // a ._errorId it will be assumed to be foreign and treated as an interop boundary and
    // a new error id will be minted.
    //
    var error_number = 1;

    //
    // The state machine has a interesting hiccup in it with regards to notification, in order
    // to flatten out notification and avoid recursion for synchronous completion we have an
    // explicit set of *_notify states which are responsible for notifying their entire tree
    // of children. They can do this because they know that immediate children are always
    // ThenPromise instances and we can therefore reach into their state to access the
    // _listeners collection.
    //
    // So, what happens is that a Promise will be fulfilled through the _completed or _error
    // messages at which point it will enter a *_notify state and be responsible for to move
    // its children into an (as appropriate) success or error state and also notify that child's
    // listeners of the state transition, until leaf notes are reached.
    //

    var state_created,              // -> working
        state_working,              // -> error | error_notify | success | success_notify | canceled | waiting
        state_waiting,              // -> error | error_notify | success | success_notify | waiting_canceled
        state_waiting_canceled,     // -> error | error_notify | success | success_notify | canceling
        state_canceled,             // -> error | error_notify | success | success_notify | canceling
        state_canceling,            // -> error_notify
        state_success_notify,       // -> success
        state_success,              // -> .
        state_error_notify,         // -> error
        state_error;                // -> .

    // Noop function, used in the various states to indicate that they don't support a given
    // message. Named with the somewhat cute name '_' because it reads really well in the states.

    function _() { }

    // Initial state
    //
    state_created = {
        name: "created",
        enter: function (promise) {
            promise._setState(state_working);
        },
        cancel: _,
        done: _,
        then: _,
        _completed: _,
        _error: _,
        _notify: _,
        _progress: _,
        _setCompleteValue: _,
        _setErrorValue: _
    };

    // Ready state, waiting for a message (completed/error/progress), able to be canceled
    //
    state_working = {
        name: "working",
        enter: _,
        cancel: function (promise) {
            promise._setState(state_canceled);
        },
        done: done,
        then: then,
        _completed: completed,
        _error: error,
        _notify: _,
        _progress: progress,
        _setCompleteValue: setCompleteValue,
        _setErrorValue: setErrorValue
    };

    // Waiting state, if a promise is completed with a value which is itself a promise
    // (has a then() method) it signs up to be informed when that child promise is
    // fulfilled at which point it will be fulfilled with that value.
    //
    state_waiting = {
        name: "waiting",
        enter: function (promise) {
            var waitedUpon = promise._value;
            // We can special case our own intermediate promises which are not in a
            //  terminal state by just pushing this promise as a listener without
            //  having to create new indirection functions
            if (waitedUpon instanceof ThenPromise &&
                waitedUpon._state !== state_error &&
                waitedUpon._state !== state_success) {
                pushListener(waitedUpon, { promise: promise });
            } else {
                var error = function (value) {
                    if (waitedUpon._errorId) {
                        promise._chainedError(value, waitedUpon);
                    } else {
                        // Because this is an interop boundary we want to indicate that this
                        //  error has been handled by the promise infrastructure before we
                        //  begin a new handling chain.
                        //
                        callonerror(promise, value, detailsForHandledError, waitedUpon, error);
                        promise._error(value);
                    }
                };
                error.handlesOnError = true;
                waitedUpon.then(
                    promise._completed.bind(promise),
                    error,
                    promise._progress.bind(promise)
                );
            }
        },
        cancel: function (promise) {
            promise._setState(state_waiting_canceled);
        },
        done: done,
        then: then,
        _completed: completed,
        _error: error,
        _notify: _,
        _progress: progress,
        _setCompleteValue: setCompleteValue,
        _setErrorValue: setErrorValue
    };

    // Waiting canceled state, when a promise has been in a waiting state and receives a
    // request to cancel its pending work it will forward that request to the child promise
    // and then waits to be informed of the result. This promise moves itself into the
    // canceling state but understands that the child promise may instead push it to a
    // different state.
    //
    state_waiting_canceled = {
        name: "waiting_canceled",
        enter: function (promise) {
            // Initiate a transition to canceling. Triggering a cancel on the promise
            // that we are waiting upon may result in a different state transition
            // before the state machine pump runs again.
            promise._setState(state_canceling);
            var waitedUpon = promise._value;
            if (waitedUpon.cancel) {
                waitedUpon.cancel();
            }
        },
        cancel: _,
        done: done,
        then: then,
        _completed: completed,
        _error: error,
        _notify: _,
        _progress: progress,
        _setCompleteValue: setCompleteValue,
        _setErrorValue: setErrorValue
    };

    // Canceled state, moves to the canceling state and then tells the promise to do
    // whatever it might need to do on cancelation.
    //
    state_canceled = {
        name: "canceled",
        enter: function (promise) {
            // Initiate a transition to canceling. The _cancelAction may change the state
            // before the state machine pump runs again.
            promise._setState(state_canceling);
            promise._cancelAction();
        },
        cancel: _,
        done: done,
        then: then,
        _completed: completed,
        _error: error,
        _notify: _,
        _progress: progress,
        _setCompleteValue: setCompleteValue,
        _setErrorValue: setErrorValue
    };

    // Canceling state, commits to the promise moving to an error state with an error
    // object whose 'name' and 'message' properties contain the string "Canceled"
    //
    state_canceling = {
        name: "canceling",
        enter: function (promise) {
            var error = new Error(canceledName);
            error.name = error.message;
            promise._value = error;
            promise._setState(state_error_notify);
        },
        cancel: _,
        done: _,
        then: _,
        _completed: _,
        _error: _,
        _notify: _,
        _progress: _,
        _setCompleteValue: _,
        _setErrorValue: _
    };

    // Success notify state, moves a promise to the success state and notifies all children
    //
    state_success_notify = {
        name: "complete_notify",
        enter: function (promise) {
            promise.done = CompletePromise.prototype.done;
            promise.then = CompletePromise.prototype.then;
            if (promise._listeners) {
                var queue = [promise];
                var p;
                while (queue.length) {
                    p = queue.shift();
                    p._state._notify(p, queue);
                }
            }
            promise._setState(state_success);
        },
        cancel: _,
        done: null, /*error to get here */
        then: null, /*error to get here */
        _completed: _,
        _error: _,
        _notify: notifySuccess,
        _progress: _,
        _setCompleteValue: _,
        _setErrorValue: _
    };

    // Success state, moves a promise to the success state and does NOT notify any children.
    // Some upstream promise is owning the notification pass.
    //
    state_success = {
        name: "success",
        enter: function (promise) {
            promise.done = CompletePromise.prototype.done;
            promise.then = CompletePromise.prototype.then;
            promise._cleanupAction();
        },
        cancel: _,
        done: null, /*error to get here */
        then: null, /*error to get here */
        _completed: _,
        _error: _,
        _notify: notifySuccess,
        _progress: _,
        _setCompleteValue: _,
        _setErrorValue: _
    };

    // Error notify state, moves a promise to the error state and notifies all children
    //
    state_error_notify = {
        name: "error_notify",
        enter: function (promise) {
            promise.done = ErrorPromise.prototype.done;
            promise.then = ErrorPromise.prototype.then;
            if (promise._listeners) {
                var queue = [promise];
                var p;
                while (queue.length) {
                    p = queue.shift();
                    p._state._notify(p, queue);
                }
            }
            promise._setState(state_error);
        },
        cancel: _,
        done: null, /*error to get here*/
        then: null, /*error to get here*/
        _completed: _,
        _error: _,
        _notify: notifyError,
        _progress: _,
        _setCompleteValue: _,
        _setErrorValue: _
    };

    // Error state, moves a promise to the error state and does NOT notify any children.
    // Some upstream promise is owning the notification pass.
    //
    state_error = {
        name: "error",
        enter: function (promise) {
            promise.done = ErrorPromise.prototype.done;
            promise.then = ErrorPromise.prototype.then;
            promise._cleanupAction();
        },
        cancel: _,
        done: null, /*error to get here*/
        then: null, /*error to get here*/
        _completed: _,
        _error: _,
        _notify: notifyError,
        _progress: _,
        _setCompleteValue: _,
        _setErrorValue: _
    };

    //
    // The statemachine implementation follows a very particular pattern, the states are specified
    // as static stateless bags of functions which are then indirected through the state machine
    // instance (a Promise). As such all of the functions on each state have the promise instance
    // passed to them explicitly as a parameter and the Promise instance members do a little
    // dance where they indirect through the state and insert themselves in the argument list.
    //
    // We could instead call directly through the promise states however then every caller
    // would have to remember to do things like pumping the state machine to catch state transitions.
    //

    var PromiseStateMachine = _Base.Class.define(null, {
        _listeners: null,
        _nextState: null,
        _state: null,
        _value: null,

        cancel: function () {
            /// <signature helpKeyword="WinJS.PromiseStateMachine.cancel">
            /// <summary locid="WinJS.PromiseStateMachine.cancel">
            /// Attempts to cancel the fulfillment of a promised value. If the promise hasn't
            /// already been fulfilled and cancellation is supported, the promise enters
            /// the error state with a value of Error("Canceled").
            /// </summary>
            /// </signature>
            this._state.cancel(this);
            this._run();
        },
        done: function Promise_done(onComplete, onError, onProgress) {
            /// <signature helpKeyword="WinJS.PromiseStateMachine.done">
            /// <summary locid="WinJS.PromiseStateMachine.done">
            /// Allows you to specify the work to be done on the fulfillment of the promised value,
            /// the error handling to be performed if the promise fails to fulfill
            /// a value, and the handling of progress notifications along the way.
            ///
            /// After the handlers have finished executing, this function throws any error that would have been returned
            /// from then() as a promise in the error state.
            /// </summary>
            /// <param name='onComplete' type='Function' locid="WinJS.PromiseStateMachine.done_p:onComplete">
            /// The function to be called if the promise is fulfilled successfully with a value.
            /// The fulfilled value is passed as the single argument. If the value is null,
            /// the fulfilled value is returned. The value returned
            /// from the function becomes the fulfilled value of the promise returned by
            /// then(). If an exception is thrown while executing the function, the promise returned
            /// by then() moves into the error state.
            /// </param>
            /// <param name='onError' type='Function' optional='true' locid="WinJS.PromiseStateMachine.done_p:onError">
            /// The function to be called if the promise is fulfilled with an error. The error
            /// is passed as the single argument. If it is null, the error is forwarded.
            /// The value returned from the function is the fulfilled value of the promise returned by then().
            /// </param>
            /// <param name='onProgress' type='Function' optional='true' locid="WinJS.PromiseStateMachine.done_p:onProgress">
            /// the function to be called if the promise reports progress. Data about the progress
            /// is passed as the single argument. Promises are not required to support
            /// progress.
            /// </param>
            /// </signature>
            this._state.done(this, onComplete, onError, onProgress);
        },
        then: function Promise_then(onComplete, onError, onProgress) {
            /// <signature helpKeyword="WinJS.PromiseStateMachine.then">
            /// <summary locid="WinJS.PromiseStateMachine.then">
            /// Allows you to specify the work to be done on the fulfillment of the promised value,
            /// the error handling to be performed if the promise fails to fulfill
            /// a value, and the handling of progress notifications along the way.
            /// </summary>
            /// <param name='onComplete' type='Function' locid="WinJS.PromiseStateMachine.then_p:onComplete">
            /// The function to be called if the promise is fulfilled successfully with a value.
            /// The value is passed as the single argument. If the value is null, the value is returned.
            /// The value returned from the function becomes the fulfilled value of the promise returned by
            /// then(). If an exception is thrown while this function is being executed, the promise returned
            /// by then() moves into the error state.
            /// </param>
            /// <param name='onError' type='Function' optional='true' locid="WinJS.PromiseStateMachine.then_p:onError">
            /// The function to be called if the promise is fulfilled with an error. The error
            /// is passed as the single argument. If it is null, the error is forwarded.
            /// The value returned from the function becomes the fulfilled value of the promise returned by then().
            /// </param>
            /// <param name='onProgress' type='Function' optional='true' locid="WinJS.PromiseStateMachine.then_p:onProgress">
            /// The function to be called if the promise reports progress. Data about the progress
            /// is passed as the single argument. Promises are not required to support
            /// progress.
            /// </param>
            /// <returns type="WinJS.Promise" locid="WinJS.PromiseStateMachine.then_returnValue">
            /// The promise whose value is the result of executing the complete or
            /// error function.
            /// </returns>
            /// </signature>
            return this._state.then(this, onComplete, onError, onProgress);
        },

        _chainedError: function (value, context) {
            var result = this._state._error(this, value, detailsForChainedError, context);
            this._run();
            return result;
        },
        _completed: function (value) {
            var result = this._state._completed(this, value);
            this._run();
            return result;
        },
        _error: function (value) {
            var result = this._state._error(this, value, detailsForError);
            this._run();
            return result;
        },
        _progress: function (value) {
            this._state._progress(this, value);
        },
        _setState: function (state) {
            this._nextState = state;
        },
        _setCompleteValue: function (value) {
            this._state._setCompleteValue(this, value);
            this._run();
        },
        _setChainedErrorValue: function (value, context) {
            var result = this._state._setErrorValue(this, value, detailsForChainedError, context);
            this._run();
            return result;
        },
        _setExceptionValue: function (value) {
            var result = this._state._setErrorValue(this, value, detailsForException);
            this._run();
            return result;
        },
        _run: function () {
            while (this._nextState) {
                this._state = this._nextState;
                this._nextState = null;
                this._state.enter(this);
            }
        }
    }, {
        supportedForProcessing: false
    });

    //
    // Implementations of shared state machine code.
    //

    function completed(promise, value) {
        var targetState;
        if (value && typeof value === "object" && typeof value.then === "function") {
            targetState = state_waiting;
        } else {
            targetState = state_success_notify;
        }
        promise._value = value;
        promise._setState(targetState);
    }
    function createErrorDetails(exception, error, promise, id, parent, handler) {
        return {
            exception: exception,
            error: error,
            promise: promise,
            handler: handler,
            id: id,
            parent: parent
        };
    }
    function detailsForHandledError(promise, errorValue, context, handler) {
        var exception = context._isException;
        var errorId = context._errorId;
        return createErrorDetails(
            exception ? errorValue : null,
            exception ? null : errorValue,
            promise,
            errorId,
            context,
            handler
        );
    }
    function detailsForChainedError(promise, errorValue, context) {
        var exception = context._isException;
        var errorId = context._errorId;
        setErrorInfo(promise, errorId, exception);
        return createErrorDetails(
            exception ? errorValue : null,
            exception ? null : errorValue,
            promise,
            errorId,
            context
        );
    }
    function detailsForError(promise, errorValue) {
        var errorId = ++error_number;
        setErrorInfo(promise, errorId);
        return createErrorDetails(
            null,
            errorValue,
            promise,
            errorId
        );
    }
    function detailsForException(promise, exceptionValue) {
        var errorId = ++error_number;
        setErrorInfo(promise, errorId, true);
        return createErrorDetails(
            exceptionValue,
            null,
            promise,
            errorId
        );
    }
    function done(promise, onComplete, onError, onProgress) {
        var asyncOpID = _Trace._traceAsyncOperationStarting("WinJS.Promise.done");
        pushListener(promise, { c: onComplete, e: onError, p: onProgress, asyncOpID: asyncOpID });
    }
    function error(promise, value, onerrorDetails, context) {
        promise._value = value;
        callonerror(promise, value, onerrorDetails, context);
        promise._setState(state_error_notify);
    }
    function notifySuccess(promise, queue) {
        var value = promise._value;
        var listeners = promise._listeners;
        if (!listeners) {
            return;
        }
        promise._listeners = null;
        var i, len;
        for (i = 0, len = Array.isArray(listeners) ? listeners.length : 1; i < len; i++) {
            var listener = len === 1 ? listeners : listeners[i];
            var onComplete = listener.c;
            var target = listener.promise;

            _Trace._traceAsyncOperationCompleted(listener.asyncOpID, _Global.Debug && _Global.Debug.MS_ASYNC_OP_STATUS_SUCCESS);

            if (target) {
                _Trace._traceAsyncCallbackStarting(listener.asyncOpID);
                try {
                    target._setCompleteValue(onComplete ? onComplete(value) : value);
                } catch (ex) {
                    target._setExceptionValue(ex);
                } finally {
                    _Trace._traceAsyncCallbackCompleted();
                }
                if (target._state !== state_waiting && target._listeners) {
                    queue.push(target);
                }
            } else {
                CompletePromise.prototype.done.call(promise, onComplete);
            }
        }
    }
    function notifyError(promise, queue) {
        var value = promise._value;
        var listeners = promise._listeners;
        if (!listeners) {
            return;
        }
        promise._listeners = null;
        var i, len;
        for (i = 0, len = Array.isArray(listeners) ? listeners.length : 1; i < len; i++) {
            var listener = len === 1 ? listeners : listeners[i];
            var onError = listener.e;
            var target = listener.promise;

            var errorID = _Global.Debug && (value && value.name === canceledName ? _Global.Debug.MS_ASYNC_OP_STATUS_CANCELED : _Global.Debug.MS_ASYNC_OP_STATUS_ERROR);
            _Trace._traceAsyncOperationCompleted(listener.asyncOpID, errorID);

            if (target) {
                var asyncCallbackStarted = false;
                try {
                    if (onError) {
                        _Trace._traceAsyncCallbackStarting(listener.asyncOpID);
                        asyncCallbackStarted = true;
                        if (!onError.handlesOnError) {
                            callonerror(target, value, detailsForHandledError, promise, onError);
                        }
                        target._setCompleteValue(onError(value));
                    } else {
                        target._setChainedErrorValue(value, promise);
                    }
                } catch (ex) {
                    target._setExceptionValue(ex);
                } finally {
                    if (asyncCallbackStarted) {
                        _Trace._traceAsyncCallbackCompleted();
                    }
                }
                if (target._state !== state_waiting && target._listeners) {
                    queue.push(target);
                }
            } else {
                ErrorPromise.prototype.done.call(promise, null, onError);
            }
        }
    }
    function callonerror(promise, value, onerrorDetailsGenerator, context, handler) {
        if (promiseEventListeners._listeners[errorET]) {
            if (value instanceof Error && value.message === canceledName) {
                return;
            }
            promiseEventListeners.dispatchEvent(errorET, onerrorDetailsGenerator(promise, value, context, handler));
        }
    }
    function progress(promise, value) {
        var listeners = promise._listeners;
        if (listeners) {
            var i, len;
            for (i = 0, len = Array.isArray(listeners) ? listeners.length : 1; i < len; i++) {
                var listener = len === 1 ? listeners : listeners[i];
                var onProgress = listener.p;
                if (onProgress) {
                    try { onProgress(value); } catch (ex) { }
                }
                if (!(listener.c || listener.e) && listener.promise) {
                    listener.promise._progress(value);
                }
            }
        }
    }
    function pushListener(promise, listener) {
        var listeners = promise._listeners;
        if (listeners) {
            // We may have either a single listener (which will never be wrapped in an array)
            // or 2+ listeners (which will be wrapped). Since we are now adding one more listener
            // we may have to wrap the single listener before adding the second.
            listeners = Array.isArray(listeners) ? listeners : [listeners];
            listeners.push(listener);
        } else {
            listeners = listener;
        }
        promise._listeners = listeners;
    }
    // The difference beween setCompleteValue()/setErrorValue() and complete()/error() is that setXXXValue() moves
    // a promise directly to the success/error state without starting another notification pass (because one
    // is already ongoing).
    function setErrorInfo(promise, errorId, isException) {
        promise._isException = isException || false;
        promise._errorId = errorId;
    }
    function setErrorValue(promise, value, onerrorDetails, context) {
        promise._value = value;
        callonerror(promise, value, onerrorDetails, context);
        promise._setState(state_error);
    }
    function setCompleteValue(promise, value) {
        var targetState;
        if (value && typeof value === "object" && typeof value.then === "function") {
            targetState = state_waiting;
        } else {
            targetState = state_success;
        }
        promise._value = value;
        promise._setState(targetState);
    }
    function then(promise, onComplete, onError, onProgress) {
        var result = new ThenPromise(promise);
        var asyncOpID = _Trace._traceAsyncOperationStarting("WinJS.Promise.then");
        pushListener(promise, { promise: result, c: onComplete, e: onError, p: onProgress, asyncOpID: asyncOpID });
        return result;
    }

    //
    // Internal implementation detail promise, ThenPromise is created when a promise needs
    // to be returned from a then() method.
    //
    var ThenPromise = _Base.Class.derive(PromiseStateMachine,
        function (creator) {

            if (tagWithStack && (tagWithStack === true || (tagWithStack & tag.thenPromise))) {
                this._stack = Promise._getStack();
            }

            this._creator = creator;
            this._setState(state_created);
            this._run();
        }, {
            _creator: null,

            _cancelAction: function () { if (this._creator) { this._creator.cancel(); } },
            _cleanupAction: function () { this._creator = null; }
        }, {
            supportedForProcessing: false
        }
    );

    //
    // Slim promise implementations for already completed promises, these are created
    // under the hood on synchronous completion paths as well as by WinJS.Promise.wrap
    // and WinJS.Promise.wrapError.
    //

    var ErrorPromise = _Base.Class.define(
        function ErrorPromise_ctor(value) {

            if (tagWithStack && (tagWithStack === true || (tagWithStack & tag.errorPromise))) {
                this._stack = Promise._getStack();
            }

            this._value = value;
            callonerror(this, value, detailsForError);
        }, {
            cancel: function () {
                /// <signature helpKeyword="WinJS.PromiseStateMachine.cancel">
                /// <summary locid="WinJS.PromiseStateMachine.cancel">
                /// Attempts to cancel the fulfillment of a promised value. If the promise hasn't
                /// already been fulfilled and cancellation is supported, the promise enters
                /// the error state with a value of Error("Canceled").
                /// </summary>
                /// </signature>
            },
            done: function ErrorPromise_done(unused, onError) {
                /// <signature helpKeyword="WinJS.PromiseStateMachine.done">
                /// <summary locid="WinJS.PromiseStateMachine.done">
                /// Allows you to specify the work to be done on the fulfillment of the promised value,
                /// the error handling to be performed if the promise fails to fulfill
                /// a value, and the handling of progress notifications along the way.
                ///
                /// After the handlers have finished executing, this function throws any error that would have been returned
                /// from then() as a promise in the error state.
                /// </summary>
                /// <param name='onComplete' type='Function' locid="WinJS.PromiseStateMachine.done_p:onComplete">
                /// The function to be called if the promise is fulfilled successfully with a value.
                /// The fulfilled value is passed as the single argument. If the value is null,
                /// the fulfilled value is returned. The value returned
                /// from the function becomes the fulfilled value of the promise returned by
                /// then(). If an exception is thrown while executing the function, the promise returned
                /// by then() moves into the error state.
                /// </param>
                /// <param name='onError' type='Function' optional='true' locid="WinJS.PromiseStateMachine.done_p:onError">
                /// The function to be called if the promise is fulfilled with an error. The error
                /// is passed as the single argument. If it is null, the error is forwarded.
                /// The value returned from the function is the fulfilled value of the promise returned by then().
                /// </param>
                /// <param name='onProgress' type='Function' optional='true' locid="WinJS.PromiseStateMachine.done_p:onProgress">
                /// the function to be called if the promise reports progress. Data about the progress
                /// is passed as the single argument. Promises are not required to support
                /// progress.
                /// </param>
                /// </signature>
                var value = this._value;
                if (onError) {
                    try {
                        if (!onError.handlesOnError) {
                            callonerror(null, value, detailsForHandledError, this, onError);
                        }
                        var result = onError(value);
                        if (result && typeof result === "object" && typeof result.done === "function") {
                            // If a promise is returned we need to wait on it.
                            result.done();
                        }
                        return;
                    } catch (ex) {
                        value = ex;
                    }
                }
                if (value instanceof Error && value.message === canceledName) {
                    // suppress cancel
                    return;
                }
                // force the exception to be thrown asyncronously to avoid any try/catch blocks
                //
                Promise._doneHandler(value);
            },
            then: function ErrorPromise_then(unused, onError) {
                /// <signature helpKeyword="WinJS.PromiseStateMachine.then">
                /// <summary locid="WinJS.PromiseStateMachine.then">
                /// Allows you to specify the work to be done on the fulfillment of the promised value,
                /// the error handling to be performed if the promise fails to fulfill
                /// a value, and the handling of progress notifications along the way.
                /// </summary>
                /// <param name='onComplete' type='Function' locid="WinJS.PromiseStateMachine.then_p:onComplete">
                /// The function to be called if the promise is fulfilled successfully with a value.
                /// The value is passed as the single argument. If the value is null, the value is returned.
                /// The value returned from the function becomes the fulfilled value of the promise returned by
                /// then(). If an exception is thrown while this function is being executed, the promise returned
                /// by then() moves into the error state.
                /// </param>
                /// <param name='onError' type='Function' optional='true' locid="WinJS.PromiseStateMachine.then_p:onError">
                /// The function to be called if the promise is fulfilled with an error. The error
                /// is passed as the single argument. If it is null, the error is forwarded.
                /// The value returned from the function becomes the fulfilled value of the promise returned by then().
                /// </param>
                /// <param name='onProgress' type='Function' optional='true' locid="WinJS.PromiseStateMachine.then_p:onProgress">
                /// The function to be called if the promise reports progress. Data about the progress
                /// is passed as the single argument. Promises are not required to support
                /// progress.
                /// </param>
                /// <returns type="WinJS.Promise" locid="WinJS.PromiseStateMachine.then_returnValue">
                /// The promise whose value is the result of executing the complete or
                /// error function.
                /// </returns>
                /// </signature>

                // If the promise is already in a error state and no error handler is provided
                // we optimize by simply returning the promise instead of creating a new one.
                //
                if (!onError) { return this; }
                var result;
                var value = this._value;
                try {
                    if (!onError.handlesOnError) {
                        callonerror(null, value, detailsForHandledError, this, onError);
                    }
                    result = new CompletePromise(onError(value));
                } catch (ex) {
                    // If the value throw from the error handler is the same as the value
                    // provided to the error handler then there is no need for a new promise.
                    //
                    if (ex === value) {
                        result = this;
                    } else {
                        result = new ExceptionPromise(ex);
                    }
                }
                return result;
            }
        }, {
            supportedForProcessing: false
        }
    );

    var ExceptionPromise = _Base.Class.derive(ErrorPromise,
        function ExceptionPromise_ctor(value) {

            if (tagWithStack && (tagWithStack === true || (tagWithStack & tag.exceptionPromise))) {
                this._stack = Promise._getStack();
            }

            this._value = value;
            callonerror(this, value, detailsForException);
        }, {
            /* empty */
        }, {
            supportedForProcessing: false
        }
    );

    var CompletePromise = _Base.Class.define(
        function CompletePromise_ctor(value) {

            if (tagWithStack && (tagWithStack === true || (tagWithStack & tag.completePromise))) {
                this._stack = Promise._getStack();
            }

            if (value && typeof value === "object" && typeof value.then === "function") {
                var result = new ThenPromise(null);
                result._setCompleteValue(value);
                return result;
            }
            this._value = value;
        }, {
            cancel: function () {
                /// <signature helpKeyword="WinJS.PromiseStateMachine.cancel">
                /// <summary locid="WinJS.PromiseStateMachine.cancel">
                /// Attempts to cancel the fulfillment of a promised value. If the promise hasn't
                /// already been fulfilled and cancellation is supported, the promise enters
                /// the error state with a value of Error("Canceled").
                /// </summary>
                /// </signature>
            },
            done: function CompletePromise_done(onComplete) {
                /// <signature helpKeyword="WinJS.PromiseStateMachine.done">
                /// <summary locid="WinJS.PromiseStateMachine.done">
                /// Allows you to specify the work to be done on the fulfillment of the promised value,
                /// the error handling to be performed if the promise fails to fulfill
                /// a value, and the handling of progress notifications along the way.
                ///
                /// After the handlers have finished executing, this function throws any error that would have been returned
                /// from then() as a promise in the error state.
                /// </summary>
                /// <param name='onComplete' type='Function' locid="WinJS.PromiseStateMachine.done_p:onComplete">
                /// The function to be called if the promise is fulfilled successfully with a value.
                /// The fulfilled value is passed as the single argument. If the value is null,
                /// the fulfilled value is returned. The value returned
                /// from the function becomes the fulfilled value of the promise returned by
                /// then(). If an exception is thrown while executing the function, the promise returned
                /// by then() moves into the error state.
                /// </param>
                /// <param name='onError' type='Function' optional='true' locid="WinJS.PromiseStateMachine.done_p:onError">
                /// The function to be called if the promise is fulfilled with an error. The error
                /// is passed as the single argument. If it is null, the error is forwarded.
                /// The value returned from the function is the fulfilled value of the promise returned by then().
                /// </param>
                /// <param name='onProgress' type='Function' optional='true' locid="WinJS.PromiseStateMachine.done_p:onProgress">
                /// the function to be called if the promise reports progress. Data about the progress
                /// is passed as the single argument. Promises are not required to support
                /// progress.
                /// </param>
                /// </signature>
                if (!onComplete) { return; }
                try {
                    var result = onComplete(this._value);
                    if (result && typeof result === "object" && typeof result.done === "function") {
                        result.done();
                    }
                } catch (ex) {
                    // force the exception to be thrown asynchronously to avoid any try/catch blocks
                    Promise._doneHandler(ex);
                }
            },
            then: function CompletePromise_then(onComplete) {
                /// <signature helpKeyword="WinJS.PromiseStateMachine.then">
                /// <summary locid="WinJS.PromiseStateMachine.then">
                /// Allows you to specify the work to be done on the fulfillment of the promised value,
                /// the error handling to be performed if the promise fails to fulfill
                /// a value, and the handling of progress notifications along the way.
                /// </summary>
                /// <param name='onComplete' type='Function' locid="WinJS.PromiseStateMachine.then_p:onComplete">
                /// The function to be called if the promise is fulfilled successfully with a value.
                /// The value is passed as the single argument. If the value is null, the value is returned.
                /// The value returned from the function becomes the fulfilled value of the promise returned by
                /// then(). If an exception is thrown while this function is being executed, the promise returned
                /// by then() moves into the error state.
                /// </param>
                /// <param name='onError' type='Function' optional='true' locid="WinJS.PromiseStateMachine.then_p:onError">
                /// The function to be called if the promise is fulfilled with an error. The error
                /// is passed as the single argument. If it is null, the error is forwarded.
                /// The value returned from the function becomes the fulfilled value of the promise returned by then().
                /// </param>
                /// <param name='onProgress' type='Function' optional='true' locid="WinJS.PromiseStateMachine.then_p:onProgress">
                /// The function to be called if the promise reports progress. Data about the progress
                /// is passed as the single argument. Promises are not required to support
                /// progress.
                /// </param>
                /// <returns type="WinJS.Promise" locid="WinJS.PromiseStateMachine.then_returnValue">
                /// The promise whose value is the result of executing the complete or
                /// error function.
                /// </returns>
                /// </signature>
                try {
                    // If the value returned from the completion handler is the same as the value
                    // provided to the completion handler then there is no need for a new promise.
                    //
                    var newValue = onComplete ? onComplete(this._value) : this._value;
                    return newValue === this._value ? this : new CompletePromise(newValue);
                } catch (ex) {
                    return new ExceptionPromise(ex);
                }
            }
        }, {
            supportedForProcessing: false
        }
    );

    //
    // Promise is the user-creatable WinJS.Promise object.
    //

    function timeout(timeoutMS) {
        var id;
        return new Promise(
            function (c) {
                if (timeoutMS) {
                    id = _Global.setTimeout(c, timeoutMS);
                } else {
                    _BaseCoreUtils._setImmediate(c);
                }
            },
            function () {
                if (id) {
                    _Global.clearTimeout(id);
                }
            }
        );
    }

    function timeoutWithPromise(timeout, promise) {
        var cancelPromise = function () { promise.cancel(); };
        var cancelTimeout = function () { timeout.cancel(); };
        timeout.then(cancelPromise);
        promise.then(cancelTimeout, cancelTimeout);
        return promise;
    }

    var staticCanceledPromise;

    var Promise = _Base.Class.derive(PromiseStateMachine,
        function Promise_ctor(init, oncancel) {
            /// <signature helpKeyword="WinJS.Promise">
            /// <summary locid="WinJS.Promise">
            /// A promise provides a mechanism to schedule work to be done on a value that
            /// has not yet been computed. It is a convenient abstraction for managing
            /// interactions with asynchronous APIs.
            /// </summary>
            /// <param name="init" type="Function" locid="WinJS.Promise_p:init">
            /// The function that is called during construction of the  promise. The function
            /// is given three arguments (complete, error, progress). Inside this function
            /// you should add event listeners for the notifications supported by this value.
            /// </param>
            /// <param name="oncancel" optional="true" locid="WinJS.Promise_p:oncancel">
            /// The function to call if a consumer of this promise wants
            /// to cancel its undone work. Promises are not required to
            /// support cancellation.
            /// </param>
            /// </signature>

            if (tagWithStack && (tagWithStack === true || (tagWithStack & tag.promise))) {
                this._stack = Promise._getStack();
            }

            this._oncancel = oncancel;
            this._setState(state_created);
            this._run();

            try {
                var complete = this._completed.bind(this);
                var error = this._error.bind(this);
                var progress = this._progress.bind(this);
                init(complete, error, progress);
            } catch (ex) {
                this._setExceptionValue(ex);
            }
        }, {
            _oncancel: null,

            _cancelAction: function () {
                // BEGIN monaco change
                try {
                    if (this._oncancel) {
                        this._oncancel();
                    } else {
                        throw new Error('Promise did not implement oncancel');
                    }
                } catch (ex) {
                    // Access fields to get them created
                    var msg = ex.message;
                    var stack = ex.stack;
                    promiseEventListeners.dispatchEvent('error', ex);
                }
                // END monaco change
            },
            _cleanupAction: function () { this._oncancel = null; }
        }, {

            addEventListener: function Promise_addEventListener(eventType, listener, capture) {
                /// <signature helpKeyword="WinJS.Promise.addEventListener">
                /// <summary locid="WinJS.Promise.addEventListener">
                /// Adds an event listener to the control.
                /// </summary>
                /// <param name="eventType" locid="WinJS.Promise.addEventListener_p:eventType">
                /// The type (name) of the event.
                /// </param>
                /// <param name="listener" locid="WinJS.Promise.addEventListener_p:listener">
                /// The listener to invoke when the event is raised.
                /// </param>
                /// <param name="capture" locid="WinJS.Promise.addEventListener_p:capture">
                /// Specifies whether or not to initiate capture.
                /// </param>
                /// </signature>
                promiseEventListeners.addEventListener(eventType, listener, capture);
            },
            any: function Promise_any(values) {
                /// <signature helpKeyword="WinJS.Promise.any">
                /// <summary locid="WinJS.Promise.any">
                /// Returns a promise that is fulfilled when one of the input promises
                /// has been fulfilled.
                /// </summary>
                /// <param name="values" type="Array" locid="WinJS.Promise.any_p:values">
                /// An array that contains promise objects or objects whose property
                /// values include promise objects.
                /// </param>
                /// <returns type="WinJS.Promise" locid="WinJS.Promise.any_returnValue">
                /// A promise that on fulfillment yields the value of the input (complete or error).
                /// </returns>
                /// </signature>
                return new Promise(
                    function (complete, error) {
                        var keys = Object.keys(values);
                        if (keys.length === 0) {
                            complete();
                        }
                        var canceled = 0;
                        keys.forEach(function (key) {
                            Promise.as(values[key]).then(
                                function () { complete({ key: key, value: values[key] }); },
                                function (e) {
                                    if (e instanceof Error && e.name === canceledName) {
                                        if ((++canceled) === keys.length) {
                                            complete(Promise.cancel);
                                        }
                                        return;
                                    }
                                    error({ key: key, value: values[key] });
                                }
                            );
                        });
                    },
                    function () {
                        var keys = Object.keys(values);
                        keys.forEach(function (key) {
                            var promise = Promise.as(values[key]);
                            if (typeof promise.cancel === "function") {
                                promise.cancel();
                            }
                        });
                    }
                );
            },
            as: function Promise_as(value) {
                /// <signature helpKeyword="WinJS.Promise.as">
                /// <summary locid="WinJS.Promise.as">
                /// Returns a promise. If the object is already a promise it is returned;
                /// otherwise the object is wrapped in a promise.
                /// </summary>
                /// <param name="value" locid="WinJS.Promise.as_p:value">
                /// The value to be treated as a promise.
                /// </param>
                /// <returns type="WinJS.Promise" locid="WinJS.Promise.as_returnValue">
                /// A promise.
                /// </returns>
                /// </signature>
                if (value && typeof value === "object" && typeof value.then === "function") {
                    return value;
                }
                return new CompletePromise(value);
            },
            /// <field type="WinJS.Promise" helpKeyword="WinJS.Promise.cancel" locid="WinJS.Promise.cancel">
            /// Canceled promise value, can be returned from a promise completion handler
            /// to indicate cancelation of the promise chain.
            /// </field>
            cancel: {
                get: function () {
                    return (staticCanceledPromise = staticCanceledPromise || new ErrorPromise(new _ErrorFromName(canceledName)));
                }
            },
            dispatchEvent: function Promise_dispatchEvent(eventType, details) {
                /// <signature helpKeyword="WinJS.Promise.dispatchEvent">
                /// <summary locid="WinJS.Promise.dispatchEvent">
                /// Raises an event of the specified type and properties.
                /// </summary>
                /// <param name="eventType" locid="WinJS.Promise.dispatchEvent_p:eventType">
                /// The type (name) of the event.
                /// </param>
                /// <param name="details" locid="WinJS.Promise.dispatchEvent_p:details">
                /// The set of additional properties to be attached to the event object.
                /// </param>
                /// <returns type="Boolean" locid="WinJS.Promise.dispatchEvent_returnValue">
                /// Specifies whether preventDefault was called on the event.
                /// </returns>
                /// </signature>
                return promiseEventListeners.dispatchEvent(eventType, details);
            },
            is: function Promise_is(value) {
                /// <signature helpKeyword="WinJS.Promise.is">
                /// <summary locid="WinJS.Promise.is">
                /// Determines whether a value fulfills the promise contract.
                /// </summary>
                /// <param name="value" locid="WinJS.Promise.is_p:value">
                /// A value that may be a promise.
                /// </param>
                /// <returns type="Boolean" locid="WinJS.Promise.is_returnValue">
                /// true if the specified value is a promise, otherwise false.
                /// </returns>
                /// </signature>
                return value && typeof value === "object" && typeof value.then === "function";
            },
            join: function Promise_join(values) {
                /// <signature helpKeyword="WinJS.Promise.join">
                /// <summary locid="WinJS.Promise.join">
                /// Creates a promise that is fulfilled when all the values are fulfilled.
                /// </summary>
                /// <param name="values" type="Object" locid="WinJS.Promise.join_p:values">
                /// An object whose fields contain values, some of which may be promises.
                /// </param>
                /// <returns type="WinJS.Promise" locid="WinJS.Promise.join_returnValue">
                /// A promise whose value is an object with the same field names as those of the object in the values parameter, where
                /// each field value is the fulfilled value of a promise.
                /// </returns>
                /// </signature>
                return new Promise(
                    function (complete, error, progress) {
                        var keys = Object.keys(values);
                        var errors = Array.isArray(values) ? [] : {};
                        var results = Array.isArray(values) ? [] : {};
                        var undefineds = 0;
                        var pending = keys.length;
                        var argDone = function (key) {
                            if ((--pending) === 0) {
                                var errorCount = Object.keys(errors).length;
                                if (errorCount === 0) {
                                    complete(results);
                                } else {
                                    var canceledCount = 0;
                                    keys.forEach(function (key) {
                                        var e = errors[key];
                                        if (e instanceof Error && e.name === canceledName) {
                                            canceledCount++;
                                        }
                                    });
                                    if (canceledCount === errorCount) {
                                        complete(Promise.cancel);
                                    } else {
                                        error(errors);
                                    }
                                }
                            } else {
                                progress({ Key: key, Done: true });
                            }
                        };
                        keys.forEach(function (key) {
                            var value = values[key];
                            if (value === undefined) {
                                undefineds++;
                            } else {
                                Promise.then(value,
                                    function (value) { results[key] = value; argDone(key); },
                                    function (value) { errors[key] = value; argDone(key); }
                                );
                            }
                        });
                        pending -= undefineds;
                        if (pending === 0) {
                            complete(results);
                            return;
                        }
                    },
                    function () {
                        Object.keys(values).forEach(function (key) {
                            var promise = Promise.as(values[key]);
                            if (typeof promise.cancel === "function") {
                                promise.cancel();
                            }
                        });
                    }
                );
            },
            removeEventListener: function Promise_removeEventListener(eventType, listener, capture) {
                /// <signature helpKeyword="WinJS.Promise.removeEventListener">
                /// <summary locid="WinJS.Promise.removeEventListener">
                /// Removes an event listener from the control.
                /// </summary>
                /// <param name='eventType' locid="WinJS.Promise.removeEventListener_eventType">
                /// The type (name) of the event.
                /// </param>
                /// <param name='listener' locid="WinJS.Promise.removeEventListener_listener">
                /// The listener to remove.
                /// </param>
                /// <param name='capture' locid="WinJS.Promise.removeEventListener_capture">
                /// Specifies whether or not to initiate capture.
                /// </param>
                /// </signature>
                promiseEventListeners.removeEventListener(eventType, listener, capture);
            },
            supportedForProcessing: false,
            then: function Promise_then(value, onComplete, onError, onProgress) {
                /// <signature helpKeyword="WinJS.Promise.then">
                /// <summary locid="WinJS.Promise.then">
                /// A static version of the promise instance method then().
                /// </summary>
                /// <param name="value" locid="WinJS.Promise.then_p:value">
                /// the value to be treated as a promise.
                /// </param>
                /// <param name="onComplete" type="Function" locid="WinJS.Promise.then_p:complete">
                /// The function to be called if the promise is fulfilled with a value.
                /// If it is null, the promise simply
                /// returns the value. The value is passed as the single argument.
                /// </param>
                /// <param name="onError" type="Function" optional="true" locid="WinJS.Promise.then_p:error">
                /// The function to be called if the promise is fulfilled with an error. The error
                /// is passed as the single argument.
                /// </param>
                /// <param name="onProgress" type="Function" optional="true" locid="WinJS.Promise.then_p:progress">
                /// The function to be called if the promise reports progress. Data about the progress
                /// is passed as the single argument. Promises are not required to support
                /// progress.
                /// </param>
                /// <returns type="WinJS.Promise" locid="WinJS.Promise.then_returnValue">
                /// A promise whose value is the result of executing the provided complete function.
                /// </returns>
                /// </signature>
                return Promise.as(value).then(onComplete, onError, onProgress);
            },
            thenEach: function Promise_thenEach(values, onComplete, onError, onProgress) {
                /// <signature helpKeyword="WinJS.Promise.thenEach">
                /// <summary locid="WinJS.Promise.thenEach">
                /// Performs an operation on all the input promises and returns a promise
                /// that has the shape of the input and contains the result of the operation
                /// that has been performed on each input.
                /// </summary>
                /// <param name="values" locid="WinJS.Promise.thenEach_p:values">
                /// A set of values (which could be either an array or an object) of which some or all are promises.
                /// </param>
                /// <param name="onComplete" type="Function" locid="WinJS.Promise.thenEach_p:complete">
                /// The function to be called if the promise is fulfilled with a value.
                /// If the value is null, the promise returns the value.
                /// The value is passed as the single argument.
                /// </param>
                /// <param name="onError" type="Function" optional="true" locid="WinJS.Promise.thenEach_p:error">
                /// The function to be called if the promise is fulfilled with an error. The error
                /// is passed as the single argument.
                /// </param>
                /// <param name="onProgress" type="Function" optional="true" locid="WinJS.Promise.thenEach_p:progress">
                /// The function to be called if the promise reports progress. Data about the progress
                /// is passed as the single argument. Promises are not required to support
                /// progress.
                /// </param>
                /// <returns type="WinJS.Promise" locid="WinJS.Promise.thenEach_returnValue">
                /// A promise that is the result of calling Promise.join on the values parameter.
                /// </returns>
                /// </signature>
                var result = Array.isArray(values) ? [] : {};
                Object.keys(values).forEach(function (key) {
                    result[key] = Promise.as(values[key]).then(onComplete, onError, onProgress);
                });
                return Promise.join(result);
            },
            timeout: function Promise_timeout(time, promise) {
                /// <signature helpKeyword="WinJS.Promise.timeout">
                /// <summary locid="WinJS.Promise.timeout">
                /// Creates a promise that is fulfilled after a timeout.
                /// </summary>
                /// <param name="timeout" type="Number" optional="true" locid="WinJS.Promise.timeout_p:timeout">
                /// The timeout period in milliseconds. If this value is zero or not specified
                /// setImmediate is called, otherwise setTimeout is called.
                /// </param>
                /// <param name="promise" type="Promise" optional="true" locid="WinJS.Promise.timeout_p:promise">
                /// A promise that will be canceled if it doesn't complete before the
                /// timeout has expired.
                /// </param>
                /// <returns type="WinJS.Promise" locid="WinJS.Promise.timeout_returnValue">
                /// A promise that is completed asynchronously after the specified timeout.
                /// </returns>
                /// </signature>
                var to = timeout(time);
                return promise ? timeoutWithPromise(to, promise) : to;
            },
            wrap: function Promise_wrap(value) {
                /// <signature helpKeyword="WinJS.Promise.wrap">
                /// <summary locid="WinJS.Promise.wrap">
                /// Wraps a non-promise value in a promise. You can use this function if you need
                /// to pass a value to a function that requires a promise.
                /// </summary>
                /// <param name="value" locid="WinJS.Promise.wrap_p:value">
                /// Some non-promise value to be wrapped in a promise.
                /// </param>
                /// <returns type="WinJS.Promise" locid="WinJS.Promise.wrap_returnValue">
                /// A promise that is successfully fulfilled with the specified value
                /// </returns>
                /// </signature>
                return new CompletePromise(value);
            },
            wrapError: function Promise_wrapError(error) {
                /// <signature helpKeyword="WinJS.Promise.wrapError">
                /// <summary locid="WinJS.Promise.wrapError">
                /// Wraps a non-promise error value in a promise. You can use this function if you need
                /// to pass an error to a function that requires a promise.
                /// </summary>
                /// <param name="error" locid="WinJS.Promise.wrapError_p:error">
                /// A non-promise error value to be wrapped in a promise.
                /// </param>
                /// <returns type="WinJS.Promise" locid="WinJS.Promise.wrapError_returnValue">
                /// A promise that is in an error state with the specified value.
                /// </returns>
                /// </signature>
                return new ErrorPromise(error);
            },

            _veryExpensiveTagWithStack: {
                get: function () { return tagWithStack; },
                set: function (value) { tagWithStack = value; }
            },
            _veryExpensiveTagWithStack_tag: tag,
            _getStack: function () {
                if (_Global.Debug && _Global.Debug.debuggerEnabled) {
                    try { throw new Error(); } catch (e) { return e.stack; }
                }
            },

            _cancelBlocker: function Promise__cancelBlocker(input, oncancel) {
                //
                // Returns a promise which on cancelation will still result in downstream cancelation while
                //  protecting the promise 'input' from being  canceled which has the effect of allowing
                //  'input' to be shared amoung various consumers.
                //
                if (!Promise.is(input)) {
                    return Promise.wrap(input);
                }
                var complete;
                var error;
                var output = new Promise(
                    function (c, e) {
                        complete = c;
                        error = e;
                    },
                    function () {
                        complete = null;
                        error = null;
                        oncancel && oncancel();
                    }
                );
                input.then(
                    function (v) { complete && complete(v); },
                    function (e) { error && error(e); }
                );
                return output;
            },

        }
    );
    Object.defineProperties(Promise, _Events.createEventProperties(errorET));

    Promise._doneHandler = function (value) {
        _BaseCoreUtils._setImmediate(function Promise_done_rethrow() {
            throw value;
        });
    };

    return {
        PromiseStateMachine: PromiseStateMachine,
        Promise: Promise,
        state_created: state_created
    };
});

_winjs("WinJS/Promise", ["WinJS/Core/_Base","WinJS/Promise/_StateMachine"], function promiseInit( _Base, _StateMachine) {
    "use strict";

    _Base.Namespace.define("WinJS", {
        Promise: _StateMachine.Promise
    });

    return _StateMachine.Promise;
});

__winjs_exports = _modules["WinJS/Core/_WinJS"];
__winjs_exports.TPromise = __winjs_exports.Promise;
__winjs_exports.PPromise = __winjs_exports.Promise;

// ESM-comment-begin
// if (typeof exports === 'undefined' && typeof define === 'function' && define.amd) {
//     define([], __winjs_exports);
// } else {
//     module.exports = __winjs_exports;
// }
// ESM-comment-end

})();

// ESM-uncomment-begin
var Promise = __winjs_exports.Promise;
var TPromise = __winjs_exports.TPromise;
var PPromise = __winjs_exports.PPromise;
// ESM-uncomment-end

/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__("yLpj"), __webpack_require__("8oxB")))

/***/ }),

/***/ "xiP/":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(process) {/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

function _encode(ch) {
    return '%' + ch.charCodeAt(0).toString(16).toUpperCase();
}
// see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent
function encodeURIComponent2(str) {
    return encodeURIComponent(str).replace(/[!'()*]/g, _encode);
}
function encodeNoop(str) {
    return str.replace(/[#?]/, _encode);
}
/**
 * Uniform Resource Identifier (URI) http://tools.ietf.org/html/rfc3986.
 * This class is a simple parser which creates the basic component paths
 * (http://tools.ietf.org/html/rfc3986#section-3) with minimal validation
 * and encoding.
 *
 *       foo://example.com:8042/over/there?name=ferret#nose
 *       \_/   \______________/\_________/ \_________/ \__/
 *        |           |            |            |        |
 *     scheme     authority       path        query   fragment
 *        |   _____________________|__
 *       / \ /                        \
 *       urn:example:animal:ferret:nose
 *
 *
 */
var URI = (function () {
    function URI() {
        this._scheme = URI._empty;
        this._authority = URI._empty;
        this._path = URI._empty;
        this._query = URI._empty;
        this._fragment = URI._empty;
        this._formatted = null;
        this._fsPath = null;
    }
    URI.isUri = function (thing) {
        if (thing instanceof URI) {
            return true;
        }
        if (!thing) {
            return false;
        }
        return typeof thing.authority === 'string'
            && typeof thing.fragment === 'string'
            && typeof thing.path === 'string'
            && typeof thing.query === 'string'
            && typeof thing.scheme === 'string';
    };
    Object.defineProperty(URI.prototype, "scheme", {
        /**
         * scheme is the 'http' part of 'http://www.msft.com/some/path?query#fragment'.
         * The part before the first colon.
         */
        get: function () {
            return this._scheme;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(URI.prototype, "authority", {
        /**
         * authority is the 'www.msft.com' part of 'http://www.msft.com/some/path?query#fragment'.
         * The part between the first double slashes and the next slash.
         */
        get: function () {
            return this._authority;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(URI.prototype, "path", {
        /**
         * path is the '/some/path' part of 'http://www.msft.com/some/path?query#fragment'.
         */
        get: function () {
            return this._path;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(URI.prototype, "query", {
        /**
         * query is the 'query' part of 'http://www.msft.com/some/path?query#fragment'.
         */
        get: function () {
            return this._query;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(URI.prototype, "fragment", {
        /**
         * fragment is the 'fragment' part of 'http://www.msft.com/some/path?query#fragment'.
         */
        get: function () {
            return this._fragment;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(URI.prototype, "fsPath", {
        // ---- filesystem path -----------------------
        /**
         * Returns a string representing the corresponding file system path of this URI.
         * Will handle UNC paths and normalize windows drive letters to lower-case. Also
         * uses the platform specific path separator. Will *not* validate the path for
         * invalid characters and semantics. Will *not* look at the scheme of this URI.
         */
        get: function () {
            if (!this._fsPath) {
                var value;
                if (this._authority && this._path && this.scheme === 'file') {
                    // unc path: file://shares/c$/far/boo
                    value = "//" + this._authority + this._path;
                }
                else if (URI._driveLetterPath.test(this._path)) {
                    // windows drive letter: file:///c:/far/boo
                    value = this._path[1].toLowerCase() + this._path.substr(2);
                }
                else {
                    // other path
                    value = this._path;
                }
                if (isWindows) {
                    value = value.replace(/\//g, '\\');
                }
                this._fsPath = value;
            }
            return this._fsPath;
        },
        enumerable: true,
        configurable: true
    });
    // ---- modify to new -------------------------
    URI.prototype.with = function (change) {
        if (!change) {
            return this;
        }
        var scheme = change.scheme, authority = change.authority, path = change.path, query = change.query, fragment = change.fragment;
        if (scheme === void 0) {
            scheme = this.scheme;
        }
        else if (scheme === null) {
            scheme = '';
        }
        if (authority === void 0) {
            authority = this.authority;
        }
        else if (authority === null) {
            authority = '';
        }
        if (path === void 0) {
            path = this.path;
        }
        else if (path === null) {
            path = '';
        }
        if (query === void 0) {
            query = this.query;
        }
        else if (query === null) {
            query = '';
        }
        if (fragment === void 0) {
            fragment = this.fragment;
        }
        else if (fragment === null) {
            fragment = '';
        }
        if (scheme === this.scheme
            && authority === this.authority
            && path === this.path
            && query === this.query
            && fragment === this.fragment) {
            return this;
        }
        var ret = new URI();
        ret._scheme = scheme;
        ret._authority = authority;
        ret._path = path;
        ret._query = query;
        ret._fragment = fragment;
        URI._validate(ret);
        return ret;
    };
    // ---- parse & validate ------------------------
    URI.parse = function (value) {
        var ret = new URI();
        var data = URI._parseComponents(value);
        ret._scheme = data.scheme;
        ret._authority = decodeURIComponent(data.authority);
        ret._path = decodeURIComponent(data.path);
        ret._query = decodeURIComponent(data.query);
        ret._fragment = decodeURIComponent(data.fragment);
        URI._validate(ret);
        return ret;
    };
    URI.file = function (path) {
        var ret = new URI();
        ret._scheme = 'file';
        // normalize to fwd-slashes on windows,
        // on other systems bwd-slaches are valid
        // filename character, eg /f\oo/ba\r.txt
        if (isWindows) {
            path = path.replace(/\\/g, URI._slash);
        }
        // check for authority as used in UNC shares
        // or use the path as given
        if (path[0] === URI._slash && path[0] === path[1]) {
            var idx = path.indexOf(URI._slash, 2);
            if (idx === -1) {
                ret._authority = path.substring(2);
            }
            else {
                ret._authority = path.substring(2, idx);
                ret._path = path.substring(idx);
            }
        }
        else {
            ret._path = path;
        }
        // Ensure that path starts with a slash
        // or that it is at least a slash
        if (ret._path[0] !== URI._slash) {
            ret._path = URI._slash + ret._path;
        }
        URI._validate(ret);
        return ret;
    };
    URI._parseComponents = function (value) {
        var ret = {
            scheme: URI._empty,
            authority: URI._empty,
            path: URI._empty,
            query: URI._empty,
            fragment: URI._empty,
        };
        var match = URI._regexp.exec(value);
        if (match) {
            ret.scheme = match[2] || ret.scheme;
            ret.authority = match[4] || ret.authority;
            ret.path = match[5] || ret.path;
            ret.query = match[7] || ret.query;
            ret.fragment = match[9] || ret.fragment;
        }
        return ret;
    };
    URI.from = function (components) {
        return new URI().with(components);
    };
    URI._validate = function (ret) {
        // scheme, https://tools.ietf.org/html/rfc3986#section-3.1
        // ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
        if (ret.scheme && !URI._schemePattern.test(ret.scheme)) {
            throw new Error('[UriError]: Scheme contains illegal characters.');
        }
        // path, http://tools.ietf.org/html/rfc3986#section-3.3
        // If a URI contains an authority component, then the path component
        // must either be empty or begin with a slash ("/") character.  If a URI
        // does not contain an authority component, then the path cannot begin
        // with two slash characters ("//").
        if (ret.path) {
            if (ret.authority) {
                if (!URI._singleSlashStart.test(ret.path)) {
                    throw new Error('[UriError]: If a URI contains an authority component, then the path component must either be empty or begin with a slash ("/") character');
                }
            }
            else {
                if (URI._doubleSlashStart.test(ret.path)) {
                    throw new Error('[UriError]: If a URI does not contain an authority component, then the path cannot begin with two slash characters ("//")');
                }
            }
        }
    };
    // ---- printing/externalize ---------------------------
    /**
     *
     * @param skipEncoding Do not encode the result, default is `false`
     */
    URI.prototype.toString = function (skipEncoding) {
        if (skipEncoding === void 0) { skipEncoding = false; }
        if (!skipEncoding) {
            if (!this._formatted) {
                this._formatted = URI._asFormatted(this, false);
            }
            return this._formatted;
        }
        else {
            // we don't cache that
            return URI._asFormatted(this, true);
        }
    };
    URI._asFormatted = function (uri, skipEncoding) {
        var encoder = !skipEncoding
            ? encodeURIComponent2
            : encodeNoop;
        var parts = [];
        var scheme = uri.scheme, authority = uri.authority, path = uri.path, query = uri.query, fragment = uri.fragment;
        if (scheme) {
            parts.push(scheme, ':');
        }
        if (authority || scheme === 'file') {
            parts.push('//');
        }
        if (authority) {
            authority = authority.toLowerCase();
            var idx = authority.indexOf(':');
            if (idx === -1) {
                parts.push(encoder(authority));
            }
            else {
                parts.push(encoder(authority.substr(0, idx)), authority.substr(idx));
            }
        }
        if (path) {
            // lower-case windows drive letters in /C:/fff or C:/fff
            var m = URI._upperCaseDrive.exec(path);
            if (m) {
                if (m[1]) {
                    path = '/' + m[2].toLowerCase() + path.substr(3); // "/c:".length === 3
                }
                else {
                    path = m[2].toLowerCase() + path.substr(2); // // "c:".length === 2
                }
            }
            // encode every segement but not slashes
            // make sure that # and ? are always encoded
            // when occurring in paths - otherwise the result
            // cannot be parsed back again
            var lastIdx = 0;
            while (true) {
                var idx = path.indexOf(URI._slash, lastIdx);
                if (idx === -1) {
                    parts.push(encoder(path.substring(lastIdx)));
                    break;
                }
                parts.push(encoder(path.substring(lastIdx, idx)), URI._slash);
                lastIdx = idx + 1;
            }
            ;
        }
        if (query) {
            parts.push('?', encoder(query));
        }
        if (fragment) {
            parts.push('#', encoder(fragment));
        }
        return parts.join(URI._empty);
    };
    URI.prototype.toJSON = function () {
        var res = {
            fsPath: this.fsPath,
            external: this.toString(),
            $mid: 1
        };
        if (this.path) {
            res.path = this.path;
        }
        if (this.scheme) {
            res.scheme = this.scheme;
        }
        if (this.authority) {
            res.authority = this.authority;
        }
        if (this.query) {
            res.query = this.query;
        }
        if (this.fragment) {
            res.fragment = this.fragment;
        }
        return res;
    };
    URI.revive = function (data) {
        var result = new URI();
        result._scheme = data.scheme || URI._empty;
        result._authority = data.authority || URI._empty;
        result._path = data.path || URI._empty;
        result._query = data.query || URI._empty;
        result._fragment = data.fragment || URI._empty;
        result._fsPath = data.fsPath;
        result._formatted = data.external;
        URI._validate(result);
        return result;
    };
    return URI;
}());
/* harmony default export */ __webpack_exports__["a"] = (URI);
URI._empty = '';
URI._slash = '/';
URI._regexp = /^(([^:/?#]+?):)?(\/\/([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?/;
URI._driveLetterPath = /^\/[a-zA-z]:/;
URI._upperCaseDrive = /^(\/)?([A-Z]:)/;
URI._schemePattern = /^\w[\w\d+.-]*$/;
URI._singleSlashStart = /^\//;
URI._doubleSlashStart = /^\/\//;
var isWindows;
if (typeof process === 'object') {
    isWindows = process.platform === 'win32';
}
else if (typeof navigator === 'object') {
    var userAgent = navigator.userAgent;
    isWindows = userAgent.indexOf('Windows') >= 0;
}

/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__("8oxB")))

/***/ }),

/***/ "yLpj":
/***/ (function(module, exports) {

var g;

// This works in non-strict mode
g = (function() {
	return this;
})();

try {
	// This works if eval is allowed (see CSP)
	g = g || new Function("return this")();
} catch (e) {
	// This works if the window reference is available
	if (typeof window === "object") g = window;
}

// g can still be undefined, but nothing to do about it...
// We return undefined, instead of nothing here, so it's
// easier to handle this case. if(!global) { ...}

module.exports = g;


/***/ })

/******/ });