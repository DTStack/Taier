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

import React from 'react';
import CodeBlock from '@/pages/DataModel/components/CodeBlock';

const overflowContainer = (child) => {
  return (
    <div className="card-container">
      <div className="inner-container">{child}</div>
    </div>
  );
};

const noneOverflowScrollContainer = (child) => {
  return <div>{child}</div>;
};

const SqlPreview = (props) => {
  const { code, overflowEnable = true } = props;
  const container = overflowEnable
    ? overflowContainer
    : noneOverflowScrollContainer;
  return container(<CodeBlock code={code} overflowEnable={overflowEnable} />);
};

export default SqlPreview;
