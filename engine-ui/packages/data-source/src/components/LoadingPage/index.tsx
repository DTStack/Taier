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

import React, { CSSProperties } from 'react';
import { Spin } from 'antd';
import './style';

interface IPropsLoadingPage {
  style?: CSSProperties;
  className?: string;
}

const LoadingPage = (props: IPropsLoadingPage) => {
  const { style, className = '' } = props;
  const _className = 'loading-page ' + className;
  return (
    <div className={_className} style={{ ...style }}>
      <Spin className="loading-page-spin" />
    </div>
  );
};

export default LoadingPage;
