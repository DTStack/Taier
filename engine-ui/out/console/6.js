(window["webpackJsonp_console-ui"] = window["webpackJsonp_console-ui"] || []).push([[6],{

/***/ "UXsU":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__);

// EXPORTS
__webpack_require__.d(__webpack_exports__, "JSONWorker", function() { return /* binding */ jsonWorker_JSONWorker; });
__webpack_require__.d(__webpack_exports__, "create", function() { return /* binding */ create; });

// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/vscode-languageserver-types/main.js
var main = __webpack_require__("5peO");

// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/language/json/_deps/jsonc-parser/main.js + 4 modules
var jsonc_parser_main = __webpack_require__("wvy9");

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
function format(message, args) {
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
    return format(message, args);
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
    __extends(NullASTNodeImpl, _super);
    function NullASTNodeImpl(parent, offset) {
        var _this = _super.call(this, parent, offset) || this;
        _this.type = 'null';
        return _this;
    }
    return NullASTNodeImpl;
}(ASTNodeImpl));

var BooleanASTNodeImpl = /** @class */ (function (_super) {
    __extends(BooleanASTNodeImpl, _super);
    function BooleanASTNodeImpl(parent, boolValue, offset) {
        var _this = _super.call(this, parent, offset) || this;
        _this.type = 'boolean';
        _this.value = boolValue;
        return _this;
    }
    return BooleanASTNodeImpl;
}(ASTNodeImpl));

var ArrayASTNodeImpl = /** @class */ (function (_super) {
    __extends(ArrayASTNodeImpl, _super);
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
    __extends(NumberASTNodeImpl, _super);
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
    __extends(StringASTNodeImpl, _super);
    function StringASTNodeImpl(parent, offset, length) {
        var _this = _super.call(this, parent, offset, length) || this;
        _this.type = 'string';
        _this.value = '';
        return _this;
    }
    return StringASTNodeImpl;
}(ASTNodeImpl));

var PropertyASTNodeImpl = /** @class */ (function (_super) {
    __extends(PropertyASTNodeImpl, _super);
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
    __extends(ObjectASTNodeImpl, _super);
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
        case main["c" /* DiagnosticSeverity */].Error: return ProblemSeverity.Error;
        case main["c" /* DiagnosticSeverity */].Warning: return ProblemSeverity.Warning;
        case main["c" /* DiagnosticSeverity */].Information: return ProblemSeverity.Warning;
    }
    return ProblemSeverity.Ignore;
}
function newJSONDocument(root, diagnostics) {
    if (diagnostics === void 0) { diagnostics = []; }
    return new JSONDocument(root, [], [], diagnostics);
}
function getNodeValue(node) {
    switch (node.type) {
        case 'array':
            return node.items.map(getNodeValue);
        case 'object':
            var obj = Object.create(null);
            for (var _i = 0, _a = node.properties; _i < _a.length; _i++) {
                var prop = _a[_i];
                obj[prop.keyNode.value] = getNodeValue(prop.valueNode);
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
            var val = getNodeValue(node);
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
            var val = getNodeValue(node);
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
            var values_1 = getNodeValue(node);
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
function parse(textDocument, config) {
    var problems = [];
    var text = textDocument.getText();
    var scanner = jsonc_parser_main["a" /* createScanner */](text, false);
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

function startsWith(haystack, needle) {
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
function endsWith(haystack, needle) {
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
function convertSimple2RegExpPattern(pattern) {
    return pattern.replace(/[\-\\\{\}\+\?\|\^\$\.\,\[\]\(\)\#\s]/g, '\\$&').replace(/[\*]/g, '.*');
}
function repeat(value, count) {
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
            overwriteRange = main["g" /* Range */].create(document.positionAt(node.offset), document.positionAt(node.offset + node.length));
        }
        else {
            var overwriteStart = offset - currentWord.length;
            if (overwriteStart > 0 && document.getText()[overwriteStart - 1] === '"') {
                overwriteStart--;
            }
            overwriteRange = main["g" /* Range */].create(document.positionAt(overwriteStart), position);
        }
        var proposed = {};
        var collector = {
            add: function (suggestion) {
                var existing = proposed[suggestion.label];
                if (!existing) {
                    proposed[suggestion.label] = suggestion;
                    if (overwriteRange) {
                        suggestion.textEdit = main["j" /* TextEdit */].replace(overwriteRange, suggestion.insertText);
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
                        proposed[p.keyNode.value] = main["a" /* CompletionItem */].create('__');
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
                        kind: main["b" /* CompletionItemKind */].Property,
                        label: _this.getLabelForValue(currentWord),
                        insertText: _this.getInsertTextForProperty(currentWord, null, false, separatorAfter_1),
                        insertTextFormat: main["d" /* InsertTextFormat */].Snippet, documentation: ''
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
                                kind: main["b" /* CompletionItemKind */].Property,
                                label: key,
                                insertText: _this.getInsertTextForProperty(key, propertySchema, addValue, separatorAfter),
                                insertTextFormat: main["d" /* InsertTextFormat */].Snippet,
                                filterText: _this.getFilterTextForValue(key),
                                documentation: propertySchema.description || '',
                            };
                            if (endsWith(proposal.insertText, "$1" + separatorAfter)) {
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
                    kind: main["b" /* CompletionItemKind */].Property,
                    label: key,
                    insertText: _this.getInsertTextForValue(key, ''),
                    insertTextFormat: main["d" /* InsertTextFormat */].Snippet,
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
                kind: main["b" /* CompletionItemKind */].Property,
                label: '$schema',
                insertText: this.getInsertTextForProperty('$schema', null, true, ''),
                insertTextFormat: main["d" /* InsertTextFormat */].Snippet, documentation: '',
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
                insertTextFormat: main["d" /* InsertTextFormat */].Snippet,
                documentation: ''
            });
            collector.add({
                kind: this.getSuggestionKind('array'),
                label: 'Empty array',
                insertText: this.getInsertTextForValue([], ''),
                insertTextFormat: main["d" /* InsertTextFormat */].Snippet,
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
                    insertTextFormat: main["d" /* InsertTextFormat */].Snippet, documentation: ''
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
                insertTextFormat: main["d" /* InsertTextFormat */].Snippet,
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
                    insertTextFormat: main["d" /* InsertTextFormat */].Snippet,
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
                    insertTextFormat: main["d" /* InsertTextFormat */].Snippet,
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
                insertTextFormat: main["d" /* InsertTextFormat */].Snippet,
                detail: jsonCompletion_localize('defaults.object', 'New object'),
                documentation: ''
            });
        }
        if (types['array']) {
            collector.add({
                kind: this.getSuggestionKind('array'),
                label: '[]',
                insertText: this.getInsertTextForGuessedValue([], separatorAfter),
                insertTextFormat: main["d" /* InsertTextFormat */].Snippet,
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
            insertTextFormat: main["d" /* InsertTextFormat */].Snippet,
            documentation: ''
        });
    };
    JSONCompletion.prototype.addNullValueCompletion = function (separatorAfter, collector) {
        collector.add({
            kind: this.getSuggestionKind('null'),
            label: 'null',
            insertText: 'null' + separatorAfter,
            insertTextFormat: main["d" /* InsertTextFormat */].Snippet,
            documentation: ''
        });
    };
    JSONCompletion.prototype.addDollarSchemaCompletions = function (separatorAfter, collector) {
        var _this = this;
        var schemaIds = this.schemaService.getRegisteredSchemaIds(function (schema) { return schema === 'http' || schema === 'https'; });
        schemaIds.forEach(function (schemaId) { return collector.add({
            kind: main["b" /* CompletionItemKind */].Module,
            label: _this.getLabelForValue(schemaId),
            filterText: _this.getFilterTextForValue(schemaId),
            insertText: _this.getInsertTextForValue(schemaId, separatorAfter),
            insertTextFormat: main["d" /* InsertTextFormat */].Snippet, documentation: ''
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
            return main["b" /* CompletionItemKind */].Value;
        }
        switch (type) {
            case 'string': return main["b" /* CompletionItemKind */].Value;
            case 'object': return main["b" /* CompletionItemKind */].Module;
            case 'property': return main["b" /* CompletionItemKind */].Property;
            default: return main["b" /* CompletionItemKind */].Value;
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
        var scanner = jsonc_parser_main["a" /* createScanner */](document.getText(), true);
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
        var scanner = jsonc_parser_main["a" /* createScanner */](document.getText(), true);
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
        var scanner = jsonc_parser_main["a" /* createScanner */](document.getText(), false);
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
        var hoverRange = main["g" /* Range */].create(document.positionAt(hoverRangeNode.offset), document.positionAt(hoverRangeNode.offset + hoverRangeNode.length));
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
                            var idx = s.schema.enum.indexOf(getNodeValue(node));
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
                var severity = problem.severity === ProblemSeverity.Error ? main["c" /* DiagnosticSeverity */].Error : main["c" /* DiagnosticSeverity */].Warning;
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
var f = 102;
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
    if (charCode >= a && charCode <= f) {
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
        if ((resourceString === 'vscode://defaultsettings/keybindings.json') || endsWith(resourceString.toLowerCase(), '/user/keybindings.json')) {
            if (root.type === 'array') {
                var result_1 = [];
                root.items.forEach(function (item) {
                    if (item.type === 'object') {
                        for (var _i = 0, _a = item.properties; _i < _a.length; _i++) {
                            var property = _a[_i];
                            if (property.keyNode.value === 'key') {
                                if (property.valueNode) {
                                    var location = main["e" /* Location */].create(document.uri, main["g" /* Range */].create(document.positionAt(item.offset), document.positionAt(item.offset + item.length)));
                                    result_1.push({ name: getNodeValue(property.valueNode), kind: main["h" /* SymbolKind */].Function, location: location });
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
                    var location = main["e" /* Location */].create(document.uri, main["g" /* Range */].create(document.positionAt(property.offset), document.positionAt(property.offset + property.length)));
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
                return main["h" /* SymbolKind */].Module;
            case 'string':
                return main["h" /* SymbolKind */].String;
            case 'number':
                return main["h" /* SymbolKind */].Number;
            case 'array':
                return main["h" /* SymbolKind */].Array;
            case 'boolean':
                return main["h" /* SymbolKind */].Boolean;
            default:// 'null'
                return main["h" /* SymbolKind */].Variable;
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
                            var color = colorFromHex(getNodeValue(s.node));
                            if (color) {
                                var range = main["g" /* Range */].create(document.positionAt(s.node.offset), document.positionAt(s.node.offset + s.node.length));
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
        result.push({ label: label, textEdit: main["j" /* TextEdit */].replace(range, JSON.stringify(label)) });
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
            this.patternRegExp = new RegExp(convertSimple2RegExpPattern(pattern) + '$');
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
            schemaContent = jsonc_parser_main["c" /* parse */](content, jsonErrors);
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
                var schemeId = getNodeValue(schemaProperties[0].valueNode);
                if (schemeId && startsWith(schemeId, '.') && this.contextService) {
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
        parseJSONDocument: function (document) { return parse(document, { collectComments: true }); },
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
            return Object(jsonc_parser_main["b" /* format */])(d.getText(), range, options).map(function (e) {
                return main["j" /* TextEdit */].replace(main["g" /* Range */].create(d.positionAt(e.offset), d.positionAt(e.offset + e.length)), e.content);
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
                return main["i" /* TextDocument */].create(uri, this._languageId, model.version, model.getValue());
            }
        }
        return null;
    };
    return JSONWorker;
}());

function create(ctx, createData) {
    return new jsonWorker_JSONWorker(ctx, createData);
}


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

/***/ })

}]);