import * as React from 'react';

import showdown from 'showdown';
import hljs from 'highlight.js';
import sql from 'highlight.js/lib/languages/sql';

import './style.scss';
import './theme/vs.scss';
import './theme/vs-dark.scss';

hljs.registerLanguage('sql', sql);
showdown.extension('highlight', function () {
    return [{
        type: 'output',
        filter: function (text: any, converter: any, options: any) {
            const left = '<pre><code\\b[^>]*>';
            const right = '</code></pre>';
            const flags = 'g';
            const replacement = function (wholeMatch: any, match: any, left: any, right: any) {
                // eslint-disable-next-line
                let lang = (left.match(/class=\"([^ \"]+)/) || [])[1];
                left = left.slice(0, 18) + 'hljs ' + left.slice(18);
                if (lang && hljs.getLanguage(lang)) {
                    return left + hljs.highlight(lang, match).value + right;
                } else {
                    return left + hljs.highlightAuto(match).value + right;
                }
            };
            return showdown.helper.replaceRecursiveRegExp(text, replacement, left, right, flags);
        }
    }];
});

export default function MarkdownRender(props: any) {
    const { text, className, dark } = props;

    const cls = `markdown-render-body ${dark ? 'vs-dark' : 'vs'} ${className}`;
    const converter = new showdown.Converter({ extensions: ['highlight'] });
    const result = converter.makeHtml(text);
    return (
        <div className={cls} dangerouslySetInnerHTML={{ __html: result }} />
    )
}
