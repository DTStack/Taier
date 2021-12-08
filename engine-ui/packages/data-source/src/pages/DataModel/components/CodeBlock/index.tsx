/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
