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

import { IRelationTree, LoopCallback } from './types';

export const loop = (tree: IRelationTree, cb?: LoopCallback) => {
  const stack = [];
  stack.push(tree);
  while (stack.length > 0) {
    const parent = stack.pop();
    if (typeof cb === 'function') {
      cb(parent);
    }
    if (parent.children && Array.isArray(parent.children)) {
      parent.children.forEach((item) => {
        stack.push(item);
      });
    }
  }
};

export const styleStringGenerator = (split: string) => {
  return (styleConfig: Object) => {
    return Object.keys(styleConfig).reduce(
      (temp, key) => `${temp}${key}${split}${styleConfig[key]};`,
      ''
    );
  };
};
