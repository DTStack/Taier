import React, { useEffect, useRef } from 'react';
import { Empty } from 'antd';
import classnames from 'classnames';
import hljs from 'highlight.js';
import sql from 'highlight.js/lib/languages/sql_more';
import 'highlight.js/styles/a11y-light.css';
import './style';

hljs.registerLanguage('sql', sql);

interface IPropsCodeBlock {
  code: string;
  overflowEnable?: boolean;
}

const CodeBlock = (props: IPropsCodeBlock) => {
  const { code, overflowEnable } = props;
  const dom = useRef(null);
  console.log('====================================');
  console.log(overflowEnable);
  console.log('====================================');

  useEffect(() => {
    if (!dom.current) return;
    hljs.highlightBlock(dom.current);
  }, [code]);
  return (
    <div data-testid="code-block" className="code-block">
      <div
        className={classnames({
          'code-container': true,
          'max-height': overflowEnable,
        })}>
        {code ? (
          <pre className="pre">
            <code
              ref={dom}
              className="code"
              dangerouslySetInnerHTML={{
                __html: code,
              }}
            />
          </pre>
        ) : (
          <Empty description="暂无SQL信息" />
        )}
      </div>
    </div>
  );
};

export default CodeBlock;
