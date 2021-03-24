import React, { useEffect, useRef } from 'react';
import hljs from 'highlight.js';
import sql from 'highlight.js/lib/languages/sql_more';
import 'highlight.js/styles/a11y-light.css';
import './style';

hljs.registerLanguage('sql', sql);

interface IPropsCodeBlock {
  code: string;
}

const CodeBlock = (props: IPropsCodeBlock) => {
  const { code } = props;
  const dom = useRef(null);

  useEffect(() => {
    if(!dom.current) return;
    hljs.highlightBlock(dom.current);
  }, [])
  return (
    <div className="code-block">
      <div className="code-container">
        <pre className="pre">
          <code ref={dom}
            className="code"
            dangerouslySetInnerHTML={{
              __html: code
            }}
          />
        </pre>
      </div>
    </div>
  )
}

export default CodeBlock;
