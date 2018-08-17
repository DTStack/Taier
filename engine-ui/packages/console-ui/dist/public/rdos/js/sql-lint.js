/* eslint-disable */
(function (mod) {
    if (typeof exports == "object" && typeof module == "object") // CommonJS
        mod(require("codemirror"));
    else if (typeof define == "function" && define.amd) // AMD
        define(["codemirror"], mod);
    else // Plain browser env
        mod(CodeMirror);
})(function (CodeMirror) {
    "use strict";
    var db = new SQL.Database();
    CodeMirror.registerHelper("lint", "sql", function (text, obj, editor) {
        var found = [];
        try {
            if (text === "") return;
                var doc = editor.getDoc(),
                cursorPos = doc.getCursor();
                db.exec(text)
        } catch (e) {
            if (e.message.indexOf('syntax') > 1) {
                found.push({
                    from: CodeMirror.Pos(cursorPos.line , cursorPos.ch),
                    to: CodeMirror.Pos(cursorPos.line , cursorPos.ch),
                    message: e.message
                });
            }
        }
        return found;
    });
});
/* eslint-disable */
