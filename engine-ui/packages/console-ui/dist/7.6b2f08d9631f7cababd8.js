(window.webpackJsonp=window.webpackJsonp||[]).push([[7],{1851:function(e,t,n){"use strict";n.r(t);var r=monaco.Promise,o=function(){function e(e){var t=this;this._defaults=e,this._worker=null,this._idleCheckInterval=setInterval(function(){return t._checkIfIdle()},3e4),this._lastUsedTime=0,this._configChangeListener=this._defaults.onDidChange(function(){return t._stopWorker()})}return e.prototype._stopWorker=function(){this._worker&&(this._worker.dispose(),this._worker=null),this._client=null},e.prototype.dispose=function(){clearInterval(this._idleCheckInterval),this._configChangeListener.dispose(),this._stopWorker()},e.prototype._checkIfIdle=function(){this._worker&&(Date.now()-this._lastUsedTime>12e4&&this._stopWorker())},e.prototype._getClient=function(){return this._lastUsedTime=Date.now(),this._client||(this._worker=monaco.editor.createWebWorker({moduleId:"vs/language/json/jsonWorker",label:this._defaults.languageId,createData:{languageSettings:this._defaults.diagnosticsOptions,languageId:this._defaults.languageId}}),this._client=this._worker.getProxy()),this._client},e.prototype.getLanguageServiceWorker=function(){for(var e,t,n,o,i,a=this,s=[],u=0;u<arguments.length;u++)s[u]=arguments[u];return t=this._getClient().then(function(t){e=t}).then(function(e){return a._worker.withSyncedResources(s)}).then(function(t){return e}),i=new r(function(e,t){n=e,o=t},function(){}),t.then(n,o),i},e}();var i=n(1891),a=monaco.Uri,s=monaco.Range,u=function(){function e(e,t,n){var r=this;this._languageId=e,this._worker=t,this._disposables=[],this._listener=Object.create(null);var o=function(e){var t,n=e.getModeId();n===r._languageId&&(r._listener[e.uri.toString()]=e.onDidChangeContent(function(){clearTimeout(t),t=setTimeout(function(){return r._doValidate(e.uri,n)},500)}),r._doValidate(e.uri,n))},i=function(e){monaco.editor.setModelMarkers(e,r._languageId,[]);var t=e.uri.toString(),n=r._listener[t];n&&(n.dispose(),delete r._listener[t])};this._disposables.push(monaco.editor.onDidCreateModel(o)),this._disposables.push(monaco.editor.onWillDisposeModel(function(e){i(e),r._resetSchema(e.uri)})),this._disposables.push(monaco.editor.onDidChangeModelLanguage(function(e){i(e.model),o(e.model),r._resetSchema(e.model.uri)})),this._disposables.push(n.onDidChange(function(e){monaco.editor.getModels().forEach(function(e){e.getModeId()===r._languageId&&(i(e),o(e))})})),this._disposables.push({dispose:function(){for(var e in monaco.editor.getModels().forEach(i),r._listener)r._listener[e].dispose()}}),monaco.editor.getModels().forEach(o)}return e.prototype.dispose=function(){this._disposables.forEach(function(e){return e&&e.dispose()}),this._disposables=[]},e.prototype._resetSchema=function(e){this._worker().then(function(t){t.resetSchema(e.toString())})},e.prototype._doValidate=function(e,t){this._worker(e).then(function(n){return n.doValidation(e.toString()).then(function(n){var r=n.map(function(e){return n="number"==typeof(t=e).code?String(t.code):t.code,{severity:function(e){switch(e){case i.c.Error:return monaco.MarkerSeverity.Error;case i.c.Warning:return monaco.MarkerSeverity.Warning;case i.c.Information:return monaco.MarkerSeverity.Info;case i.c.Hint:return monaco.MarkerSeverity.Hint;default:return monaco.MarkerSeverity.Info}}(t.severity),startLineNumber:t.range.start.line+1,startColumn:t.range.start.character+1,endLineNumber:t.range.end.line+1,endColumn:t.range.end.character+1,message:t.message,code:n,source:t.source};var t,n}),o=monaco.editor.getModel(e);o.getModeId()===t&&monaco.editor.setModelMarkers(o,t,r)})}).then(void 0,function(e){})},e}();function c(e){if(e)return{character:e.column-1,line:e.lineNumber-1}}function l(e){if(e)return{start:{line:e.startLineNumber-1,character:e.startColumn-1},end:{line:e.endLineNumber-1,character:e.endColumn-1}}}function d(e){if(e)return new s(e.start.line+1,e.start.character+1,e.end.line+1,e.end.character+1)}function h(e){var t=monaco.languages.CompletionItemKind;switch(e){case i.b.Text:return t.Text;case i.b.Method:return t.Method;case i.b.Function:return t.Function;case i.b.Constructor:return t.Constructor;case i.b.Field:return t.Field;case i.b.Variable:return t.Variable;case i.b.Class:return t.Class;case i.b.Interface:return t.Interface;case i.b.Module:return t.Module;case i.b.Property:return t.Property;case i.b.Unit:return t.Unit;case i.b.Value:return t.Value;case i.b.Enum:return t.Enum;case i.b.Keyword:return t.Keyword;case i.b.Snippet:return t.Snippet;case i.b.Color:return t.Color;case i.b.File:return t.File;case i.b.Reference:return t.Reference}return t.Property}function f(e){if(e)return{range:d(e.range),text:e.newText}}var g=function(){function e(e){this._worker=e}return Object.defineProperty(e.prototype,"triggerCharacters",{get:function(){return[" ",":"]},enumerable:!0,configurable:!0}),e.prototype.provideCompletionItems=function(e,t,n){e.getWordUntilPosition(t);var r=e.uri;return y(n,this._worker(r).then(function(e){return e.doComplete(r.toString(),c(t))}).then(function(e){if(e){var t=e.items.map(function(e){var t={label:e.label,insertText:e.insertText,sortText:e.sortText,filterText:e.filterText,documentation:e.documentation,detail:e.detail,kind:h(e.kind)};return e.textEdit&&(t.range=d(e.textEdit.range),t.insertText=e.textEdit.newText),e.insertTextFormat===i.d.Snippet&&(t.insertText={value:t.insertText}),t});return{isIncomplete:e.isIncomplete,items:t}}}))},e}();function p(e){return"string"==typeof e?{value:e}:(t=e)&&"object"==typeof t&&"string"==typeof t.kind?"plaintext"===e.kind?{value:e.value.replace(/[\\`*_{}[\]()#+\-.!]/g,"\\$&")}:{value:e.value}:{value:"```"+e.language+"\n"+e.value+"\n```\n"};var t}var m=function(){function e(e){this._worker=e}return e.prototype.provideHover=function(e,t,n){var r=e.uri;return y(n,this._worker(r).then(function(e){return e.doHover(r.toString(),c(t))}).then(function(e){if(e)return{range:d(e.range),contents:function(e){if(e)return Array.isArray(e)?e.map(p):[p(e)]}(e.contents)}}))},e}();var v=function(){function e(e){this._worker=e}return e.prototype.provideDocumentSymbols=function(e,t){var n=e.uri;return y(t,this._worker(n).then(function(e){return e.findDocumentSymbols(n.toString())}).then(function(e){if(e)return e.map(function(e){return{name:e.name,containerName:e.containerName,kind:function(e){var t=monaco.languages.SymbolKind;switch(e){case i.h.File:return t.Array;case i.h.Module:return t.Module;case i.h.Namespace:return t.Namespace;case i.h.Package:return t.Package;case i.h.Class:return t.Class;case i.h.Method:return t.Method;case i.h.Property:return t.Property;case i.h.Field:return t.Field;case i.h.Constructor:return t.Constructor;case i.h.Enum:return t.Enum;case i.h.Interface:return t.Interface;case i.h.Function:return t.Function;case i.h.Variable:return t.Variable;case i.h.Constant:return t.Constant;case i.h.String:return t.String;case i.h.Number:return t.Number;case i.h.Boolean:return t.Boolean;case i.h.Array:return t.Array}return t.Function}(e.kind),location:(t=e.location,{uri:a.parse(t.uri),range:d(t.range)})};var t})}))},e}();function _(e){return{tabSize:e.tabSize,insertSpaces:e.insertSpaces}}var k=function(){function e(e){this._worker=e}return e.prototype.provideDocumentFormattingEdits=function(e,t,n){var r=e.uri;return y(n,this._worker(r).then(function(e){return e.format(r.toString(),null,_(t)).then(function(e){if(e&&0!==e.length)return e.map(f)})}))},e}(),b=function(){function e(e){this._worker=e}return e.prototype.provideDocumentRangeFormattingEdits=function(e,t,n,r){var o=e.uri;return y(r,this._worker(o).then(function(e){return e.format(o.toString(),l(t),_(n)).then(function(e){if(e&&0!==e.length)return e.map(f)})}))},e}(),w=function(){function e(e){this._worker=e}return e.prototype.provideDocumentColors=function(e,t){var n=e.uri;return y(t,this._worker(n).then(function(e){return e.findDocumentColors(n.toString())}).then(function(e){if(e)return e.map(function(e){return{color:e.color,range:d(e.range)}})}))},e.prototype.provideColorPresentations=function(e,t,n){var r=e.uri;return y(n,this._worker(r).then(function(e){return e.getColorPresentations(r.toString(),t.color,l(t.range))}).then(function(e){if(e)return e.map(function(e){var t={label:e.label};return e.textEdit&&(t.textEdit=f(e.textEdit)),e.additionalTextEdits&&(t.additionalTextEdits=e.additionalTextEdits.map(f)),t})}))},e}();function y(e,t){return t.cancel&&e.onCancellationRequested(function(){return t.cancel()}),t}var C=n(1893);function S(e){return{getInitialState:function(){return new L(null,null,!1)},tokenize:function(t,n,r,o){return function(e,t,n,r,o){void 0===r&&(r=0);var i=0,a=!1;switch(n.scanError){case 2:t='"'+t,i=1;break;case 1:t="/*"+t,i=2}var s,u,c=C.a(t),l=n.lastWasColon;u={tokens:[],endState:n.clone()};for(;;){var d=r+c.getPosition(),h="";if(17===(s=c.scan()))break;if(d===r+c.getPosition())throw new Error("Scanner did not advance, next 3 characters are: "+t.substr(c.getPosition(),3));switch(a&&(d-=i),a=i>0,s){case 1:case 2:h=I,l=!1;break;case 3:case 4:h=E,l=!1;break;case 6:h=x,l=!0;break;case 5:h=M,l=!1;break;case 8:case 9:h=P,l=!1;break;case 7:h=T,l=!1;break;case 10:h=l?D:F,l=!1;break;case 11:h=W,l=!1}if(e)switch(s){case 12:h=N;break;case 13:h=j}u.endState=new L(n.getStateData(),c.getTokenError(),l),u.tokens.push({startIndex:d,scopes:h})}return u}(e,t,n,r)}}}var I="delimiter.bracket.json",E="delimiter.array.json",x="delimiter.colon.json",M="delimiter.comma.json",P="keyword.json",T="keyword.json",D="string.value.json",W="number.json",F="string.key.json",j="comment.block.json",N="comment.line.json",L=function(){function e(e,t,n){this._state=e,this.scanError=t,this.lastWasColon=n}return e.prototype.clone=function(){return new e(this._state,this.scanError,this.lastWasColon)},e.prototype.equals=function(t){return t===this||!!(t&&t instanceof e)&&(this.scanError===t.scanError&&this.lastWasColon===t.lastWasColon)},e.prototype.getStateData=function(){return this._state},e.prototype.setStateData=function(e){this._state=e},e}();function V(e){var t=[],n=new o(e);t.push(n);var r=function(){for(var e=[],t=0;t<arguments.length;t++)e[t]=arguments[t];return n.getLanguageServiceWorker.apply(n,e)},i=e.languageId;t.push(monaco.languages.registerCompletionItemProvider(i,new g(r))),t.push(monaco.languages.registerHoverProvider(i,new m(r))),t.push(monaco.languages.registerDocumentSymbolProvider(i,new v(r))),t.push(monaco.languages.registerDocumentFormattingEditProvider(i,new k(r))),t.push(monaco.languages.registerDocumentRangeFormattingEditProvider(i,new b(r))),t.push(new u(i,r,e)),t.push(monaco.languages.setTokensProvider(i,S(!0))),t.push(monaco.languages.setLanguageConfiguration(i,R)),t.push(monaco.languages.registerColorProvider(i,new w(r)))}n.d(t,"setupMode",function(){return V});var R={wordPattern:/(-?\d*\.\d\w*)|([^\[\{\]\}\:\"\,\s]+)/g,comments:{lineComment:"//",blockComment:["/*","*/"]},brackets:[["{","}"],["[","]"]],autoClosingPairs:[{open:"{",close:"}",notIn:["string"]},{open:"[",close:"]",notIn:["string"]},{open:'"',close:'"',notIn:["string"]}]}}}]);