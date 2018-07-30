(window.webpackJsonp=window.webpackJsonp||[]).push([[13],{1850:function(e,n,t){"use strict";t.r(n);var r=monaco.Promise,o=function(){function e(e){var n=this;this._defaults=e,this._worker=null,this._idleCheckInterval=setInterval(function(){return n._checkIfIdle()},3e4),this._lastUsedTime=0,this._configChangeListener=this._defaults.onDidChange(function(){return n._stopWorker()})}return e.prototype._stopWorker=function(){this._worker&&(this._worker.dispose(),this._worker=null),this._client=null},e.prototype.dispose=function(){clearInterval(this._idleCheckInterval),this._configChangeListener.dispose(),this._stopWorker()},e.prototype._checkIfIdle=function(){this._worker&&(Date.now()-this._lastUsedTime>12e4&&this._stopWorker())},e.prototype._getClient=function(){return this._lastUsedTime=Date.now(),this._client||(this._worker=monaco.editor.createWebWorker({moduleId:"vs/language/css/cssWorker",label:this._defaults.languageId,createData:{languageSettings:this._defaults.diagnosticsOptions,languageId:this._defaults.languageId}}),this._client=this._worker.getProxy()),this._client},e.prototype.getLanguageServiceWorker=function(){for(var e,n,t,o,i,a=this,u=[],s=0;s<arguments.length;s++)u[s]=arguments[s];return n=this._getClient().then(function(n){e=n}).then(function(e){return a._worker.withSyncedResources(u)}).then(function(n){return e}),i=new r(function(e,n){t=e,o=n},function(){}),n.then(t,o),i},e}();var i=t(1892),a=monaco.Uri,u=function(){function e(e,n,t){var r=this;this._languageId=e,this._worker=n,this._disposables=[],this._listener=Object.create(null);var o=function(e){var n,t=e.getModeId();t===r._languageId&&(r._listener[e.uri.toString()]=e.onDidChangeContent(function(){clearTimeout(n),n=setTimeout(function(){return r._doValidate(e.uri,t)},500)}),r._doValidate(e.uri,t))},i=function(e){monaco.editor.setModelMarkers(e,r._languageId,[]);var n=e.uri.toString(),t=r._listener[n];t&&(t.dispose(),delete r._listener[n])};this._disposables.push(monaco.editor.onDidCreateModel(o)),this._disposables.push(monaco.editor.onWillDisposeModel(i)),this._disposables.push(monaco.editor.onDidChangeModelLanguage(function(e){i(e.model),o(e.model)})),t.onDidChange(function(e){monaco.editor.getModels().forEach(function(e){e.getModeId()===r._languageId&&(i(e),o(e))})}),this._disposables.push({dispose:function(){for(var e in r._listener)r._listener[e].dispose()}}),monaco.editor.getModels().forEach(o)}return e.prototype.dispose=function(){this._disposables.forEach(function(e){return e&&e.dispose()}),this._disposables=[]},e.prototype._doValidate=function(e,n){this._worker(e).then(function(n){return n.doValidation(e.toString())}).then(function(t){var r=t.map(function(e){return t="number"==typeof(n=e).code?String(n.code):n.code,{severity:function(e){switch(e){case i.c.Error:return monaco.MarkerSeverity.Error;case i.c.Warning:return monaco.MarkerSeverity.Warning;case i.c.Information:return monaco.MarkerSeverity.Info;case i.c.Hint:return monaco.MarkerSeverity.Hint;default:return monaco.MarkerSeverity.Info}}(n.severity),startLineNumber:n.range.start.line+1,startColumn:n.range.start.character+1,endLineNumber:n.range.end.line+1,endColumn:n.range.end.character+1,message:n.message,code:t,source:n.source};var n,t}),o=monaco.editor.getModel(e);o.getModeId()===n&&monaco.editor.setModelMarkers(o,n,r)}).done(void 0,function(e){})},e}();function s(e){if(e)return{character:e.column-1,line:e.lineNumber-1}}function c(e){if(e)return new monaco.Range(e.start.line+1,e.start.character+1,e.end.line+1,e.end.character+1)}function l(e){if(e)return{range:c(e.range),text:e.newText}}var d=function(){function e(e){this._worker=e}return Object.defineProperty(e.prototype,"triggerCharacters",{get:function(){return[" ",":"]},enumerable:!0,configurable:!0}),e.prototype.provideCompletionItems=function(e,n,t){e.getWordUntilPosition(n);var r=e.uri;return b(t,this._worker(r).then(function(e){return e.doComplete(r.toString(),s(n))}).then(function(e){if(e){var n=e.items.map(function(e){var n={label:e.label,insertText:e.insertText,sortText:e.sortText,filterText:e.filterText,documentation:e.documentation,detail:e.detail,kind:function(e){var n=monaco.languages.CompletionItemKind;switch(e){case i.b.Text:return n.Text;case i.b.Method:return n.Method;case i.b.Function:return n.Function;case i.b.Constructor:return n.Constructor;case i.b.Field:return n.Field;case i.b.Variable:return n.Variable;case i.b.Class:return n.Class;case i.b.Interface:return n.Interface;case i.b.Module:return n.Module;case i.b.Property:return n.Property;case i.b.Unit:return n.Unit;case i.b.Value:return n.Value;case i.b.Enum:return n.Enum;case i.b.Keyword:return n.Keyword;case i.b.Snippet:return n.Snippet;case i.b.Color:return n.Color;case i.b.File:return n.File;case i.b.Reference:return n.Reference}return n.Property}(e.kind)};return e.textEdit&&(n.range=c(e.textEdit.range),n.insertText=e.textEdit.newText),e.additionalTextEdits&&(n.additionalTextEdits=e.additionalTextEdits.map(l)),e.insertTextFormat===i.e.Snippet&&(n.insertText={value:n.insertText}),n});return{isIncomplete:e.isIncomplete,items:n}}}))},e}();function f(e){return"string"==typeof e?{value:e}:(n=e)&&"object"==typeof n&&"string"==typeof n.kind?"plaintext"===e.kind?{value:e.value.replace(/[\\`*_{}[\]()#+\-.!]/g,"\\$&")}:{value:e.value}:{value:"```"+e.language+"\n"+e.value+"\n```\n"};var n}var g=function(){function e(e){this._worker=e}return e.prototype.provideHover=function(e,n,t){var r=e.uri;return b(t,this._worker(r).then(function(e){return e.doHover(r.toString(),s(n))}).then(function(e){if(e)return{range:c(e.range),contents:function(e){if(e)return Array.isArray(e)?e.map(f):[f(e)]}(e.contents)}}))},e}();var h=function(){function e(e){this._worker=e}return e.prototype.provideDocumentHighlights=function(e,n,t){var r=e.uri;return b(t,this._worker(r).then(function(e){return e.findDocumentHighlights(r.toString(),s(n))}).then(function(e){if(e)return e.map(function(e){return{range:c(e.range),kind:function(e){switch(e){case i.d.Read:return monaco.languages.DocumentHighlightKind.Read;case i.d.Write:return monaco.languages.DocumentHighlightKind.Write;case i.d.Text:return monaco.languages.DocumentHighlightKind.Text}return monaco.languages.DocumentHighlightKind.Text}(e.kind)}})}))},e}();function p(e){return{uri:a.parse(e.uri),range:c(e.range)}}var m=function(){function e(e){this._worker=e}return e.prototype.provideDefinition=function(e,n,t){var r=e.uri;return b(t,this._worker(r).then(function(e){return e.findDefinition(r.toString(),s(n))}).then(function(e){if(e)return[p(e)]}))},e}(),v=function(){function e(e){this._worker=e}return e.prototype.provideReferences=function(e,n,t,r){var o=e.uri;return b(r,this._worker(o).then(function(e){return e.findReferences(o.toString(),s(n))}).then(function(e){if(e)return e.map(p)}))},e}();var _=function(){function e(e){this._worker=e}return e.prototype.provideRenameEdits=function(e,n,t,r){var o=e.uri;return b(r,this._worker(o).then(function(e){return e.doRename(o.toString(),s(n),t)}).then(function(e){return function(e){if(e&&e.changes){var n=[];for(var t in e.changes){for(var r=[],o=0,i=e.changes[t];o<i.length;o++){var u=i[o];r.push({range:c(u.range),text:u.newText})}n.push({resource:a.parse(t),edits:r})}return{edits:n}}}(e)}))},e}();var k=function(){function e(e){this._worker=e}return e.prototype.provideDocumentSymbols=function(e,n){var t=e.uri;return b(n,this._worker(t).then(function(e){return e.findDocumentSymbols(t.toString())}).then(function(e){if(e)return e.map(function(e){return{name:e.name,containerName:e.containerName,kind:function(e){var n=monaco.languages.SymbolKind;switch(e){case i.j.File:return n.Array;case i.j.Module:return n.Module;case i.j.Namespace:return n.Namespace;case i.j.Package:return n.Package;case i.j.Class:return n.Class;case i.j.Method:return n.Method;case i.j.Property:return n.Property;case i.j.Field:return n.Field;case i.j.Constructor:return n.Constructor;case i.j.Enum:return n.Enum;case i.j.Interface:return n.Interface;case i.j.Function:return n.Function;case i.j.Variable:return n.Variable;case i.j.Constant:return n.Constant;case i.j.String:return n.String;case i.j.Number:return n.Number;case i.j.Boolean:return n.Boolean;case i.j.Array:return n.Array}return n.Function}(e.kind),location:p(e.location)}})}))},e}(),w=function(){function e(e){this._worker=e}return e.prototype.provideDocumentColors=function(e,n){var t=e.uri;return b(n,this._worker(t).then(function(e){return e.findDocumentColors(t.toString())}).then(function(e){if(e)return e.map(function(e){return{color:e.color,range:c(e.range)}})}))},e.prototype.provideColorPresentations=function(e,n,t){var r=e.uri;return b(t,this._worker(r).then(function(e){return e.getColorPresentations(r.toString(),n.color,function(e){if(e)return{start:{line:e.startLineNumber-1,character:e.startColumn-1},end:{line:e.endLineNumber-1,character:e.endColumn-1}}}(n.range))}).then(function(e){if(e)return e.map(function(e){var n={label:e.label};return e.textEdit&&(n.textEdit=l(e.textEdit)),e.additionalTextEdits&&(n.additionalTextEdits=e.additionalTextEdits.map(l)),n})}))},e}();function b(e,n){return e.onCancellationRequested(function(){return n.cancel()}),n}function y(e){var n=new o(e),t=function(e){for(var t=[],r=1;r<arguments.length;r++)t[r-1]=arguments[r];return n.getLanguageServiceWorker.apply(n,[e].concat(t))},r=e.languageId;monaco.languages.registerCompletionItemProvider(r,new d(t)),monaco.languages.registerHoverProvider(r,new g(t)),monaco.languages.registerDocumentHighlightProvider(r,new h(t)),monaco.languages.registerDefinitionProvider(r,new m(t)),monaco.languages.registerReferenceProvider(r,new v(t)),monaco.languages.registerDocumentSymbolProvider(r,new k(t)),monaco.languages.registerRenameProvider(r,new _(t)),monaco.languages.registerColorProvider(r,new w(t)),new u(r,t,e)}t.d(n,"setupMode",function(){return y})}}]);