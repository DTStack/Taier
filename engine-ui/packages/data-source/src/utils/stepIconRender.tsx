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
import classnames from 'classnames';

// 自定义step icon render
/**
 * @param step 渲染下表index
 * @param current 当前active的步骤
 * @returns
 */
const stepIconRender = (step: number, current: number) => {
  const wrapper = (child: React.ReactChild) => {
    return (
      <div
        className={classnames({
          'step-icon-wrapper': true,
          filled: step === current,
          'border-gray': step > current,
        })}>
        {child}
      </div>
    );
  };

  if (step < current) {
    return wrapper(
      <i className="step-icon iconfont2 iconOutlinedxianxing_buzhou_wancheng" />
    );
  } else if (step === current) {
    return wrapper(<span className="font">{step + 1}</span>);
  } else {
    return wrapper(<span className="font">{step + 1}</span>);
  }
};

export default stepIconRender;
