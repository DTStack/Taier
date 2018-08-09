(window.webpackJsonp=window.webpackJsonp||[]).push([[20],{1854:function(e,t,n){"use strict";n.r(t);var r=monaco.Promise,o=function(){function e(e){var t=this;this._defaults=e,this._worker=null,this._idleCheckInterval=setInterval(function(){return t._checkIfIdle()},3e4),this._lastUsedTime=0,this._configChangeListener=this._defaults.onDidChange(function(){return t._stopWorker()})}return e.prototype._stopWorker=function(){this._worker&&(this._worker.dispose(),this._worker=null),this._client=null},e.prototype.dispose=function(){clearInterval(this._idleCheckInterval),this._configChangeListener.dispose(),this._stopWorker()},e.prototype._checkIfIdle=function(){this._worker&&(Date.now()-this._lastUsedTime>12e4&&this._stopWorker())},e.prototype._getClient=function(){return this._lastUsedTime=Date.now(),this._client||(this._worker=monaco.editor.createWebWorker({moduleId:"vs/language/html/htmlWorker",createData:{languageSettings:this._defaults.options,languageId:this._defaults.languageId},label:this._defaults.languageId}),this._client=this._worker.getProxy()),this._client},e.prototype.getLanguageServiceWorker=function(){for(var e,t,n,o,i,a=this,u=[],s=0;s<arguments.length;s++)u[s]=arguments[s];return t=this._getClient().then(function(t){e=t}).then(function(e){return a._worker.withSyncedResources(u)}).then(function(t){return e}),i=new r(function(e,t){n=e,o=t},function(){}),t.then(n,o),i},e}();var i=n(1894),a=(monaco.Uri,monaco.Range),u=function(){function e(e,t,n){var r=this;this._languageId=e,this._worker=t,this._disposables=[],this._listener=Object.create(null);var o=function(e){var t,n=e.getModeId();n===r._languageId&&(r._listener[e.uri.toString()]=e.onDidChangeContent(function(){clearTimeout(t),t=setTimeout(function(){return r._doValidate(e.uri,n)},500)}),r._doValidate(e.uri,n))},i=function(e){monaco.editor.setModelMarkers(e,r._languageId,[]);var t=e.uri.toString(),n=r._listener[t];n&&(n.dispose(),delete r._listener[t])};this._disposables.push(monaco.editor.onDidCreateModel(o)),this._disposables.push(monaco.editor.onWillDisposeModel(function(e){i(e)})),this._disposables.push(monaco.editor.onDidChangeModelLanguage(function(e){i(e.model),o(e.model)})),this._disposables.push(n.onDidChange(function(e){monaco.editor.getModels().forEach(function(e){e.getModeId()===r._languageId&&(i(e),o(e))})})),this._disposables.push({dispose:function(){for(var e in r._listener)r._listener[e].dispose()}}),monaco.editor.getModels().forEach(o)}return e.prototype.dispose=function(){this._disposables.forEach(function(e){return e&&e.dispose()}),this._disposables=[]},e.prototype._doValidate=function(e,t){this._worker(e).then(function(n){return n.doValidation(e.toString()).then(function(n){var r=n.map(function(e){return n="number"==typeof(t=e).code?String(t.code):t.code,{severity:function(e){switch(e){case i.b.Error:return monaco.MarkerSeverity.Error;case i.b.Warning:return monaco.MarkerSeverity.Warning;case i.b.Information:return monaco.MarkerSeverity.Info;case i.b.Hint:return monaco.MarkerSeverity.Hint;default:return monaco.MarkerSeverity.Info}}(t.severity),startLineNumber:t.range.start.line+1,startColumn:t.range.start.character+1,endLineNumber:t.range.end.line+1,endColumn:t.range.end.character+1,message:t.message,code:n,source:t.source};var t,n});monaco.editor.setModelMarkers(monaco.editor.getModel(e),t,r)})}).then(void 0,function(e){})},e}();function s(e){if(e)return{character:e.column-1,line:e.lineNumber-1}}function c(e){if(e)return{start:s(e.getStartPosition()),end:s(e.getEndPosition())}}function l(e){if(e)return new a(e.start.line+1,e.start.character+1,e.end.line+1,e.end.character+1)}function d(e){var t=monaco.languages.CompletionItemKind;switch(e){case i.a.Text:return t.Text;case i.a.Method:return t.Method;case i.a.Function:return t.Function;case i.a.Constructor:return t.Constructor;case i.a.Field:return t.Field;case i.a.Variable:return t.Variable;case i.a.Class:return t.Class;case i.a.Interface:return t.Interface;case i.a.Module:return t.Module;case i.a.Property:return t.Property;case i.a.Unit:return t.Unit;case i.a.Value:return t.Value;case i.a.Enum:return t.Enum;case i.a.Keyword:return t.Keyword;case i.a.Snippet:return t.Snippet;case i.a.Color:return t.Color;case i.a.File:return t.File;case i.a.Reference:return t.Reference}return t.Property}function f(e){if(e)return{range:l(e.range),text:e.newText}}var g=function(){function e(e){this._worker=e}return Object.defineProperty(e.prototype,"triggerCharacters",{get:function(){return[".",":","<",'"',"=","/"]},enumerable:!0,configurable:!0}),e.prototype.provideCompletionItems=function(e,t,n){e.getWordUntilPosition(t);var r=e.uri;return k(n,this._worker(r).then(function(e){return e.doComplete(r.toString(),s(t))}).then(function(e){if(e){var t=e.items.map(function(e){var t={label:e.label,insertText:e.insertText,sortText:e.sortText,filterText:e.filterText,documentation:e.documentation,detail:e.detail,kind:d(e.kind)};return e.textEdit&&(t.range=l(e.textEdit.range),t.insertText=e.textEdit.newText),e.insertTextFormat===i.d.Snippet&&(t.insertText={value:t.insertText}),t});return{isIncomplete:e.isIncomplete,items:t}}}))},e}();var h=function(){function e(e){this._worker=e}return e.prototype.provideDocumentHighlights=function(e,t,n){var r=e.uri;return k(n,this._worker(r).then(function(e){return e.findDocumentHighlights(r.toString(),s(t))}).then(function(e){if(e)return e.map(function(e){return{range:l(e.range),kind:function(e){var t=monaco.languages.DocumentHighlightKind;switch(e){case i.c.Read:return t.Read;case i.c.Write:return t.Write;case i.c.Text:return t.Text}return t.Text}(e.kind)}})}))},e}(),p=function(){function e(e){this._worker=e}return e.prototype.provideLinks=function(e,t){var n=e.uri;return k(t,this._worker(n).then(function(e){return e.findDocumentLinks(n.toString())}).then(function(e){if(e)return e.map(function(e){return{range:l(e.range),url:e.target}})}))},e}();function m(e){return{tabSize:e.tabSize,insertSpaces:e.insertSpaces}}var _=function(){function e(e){this._worker=e}return e.prototype.provideDocumentFormattingEdits=function(e,t,n){var r=e.uri;return k(n,this._worker(r).then(function(e){return e.format(r.toString(),null,m(t)).then(function(e){if(e&&0!==e.length)return e.map(f)})}))},e}(),v=function(){function e(e){this._worker=e}return e.prototype.provideDocumentRangeFormattingEdits=function(e,t,n,r){var o=e.uri;return k(r,this._worker(o).then(function(e){return e.format(o.toString(),c(t),m(n)).then(function(e){if(e&&0!==e.length)return e.map(f)})}))},e}();function k(e,t){return t.cancel&&e.onCancellationRequested(function(){return t.cancel()}),t}function w(e){var t=new o(e),n=function(){for(var e=[],n=0;n<arguments.length;n++)e[n]=arguments[n];return t.getLanguageServiceWorker.apply(t,e)},r=e.languageId;monaco.languages.registerCompletionItemProvider(r,new g(n)),monaco.languages.registerDocumentHighlightProvider(r,new h(n)),monaco.languages.registerLinkProvider(r,new p(n)),"html"===r&&(monaco.languages.registerDocumentFormattingEditProvider(r,new _(n)),monaco.languages.registerDocumentRangeFormattingEditProvider(r,new v(n)),new u(r,n,e))}n.d(t,"setupMode",function(){return w})}}]);