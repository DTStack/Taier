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

import { QuestionCircleOutlined } from '@ant-design/icons';
import { Tooltip } from 'antd';

import * as Doc from './docs';

export const relativeStyle: any = {
    position: 'initial',
    right: 0,
    top: 0,
};

interface IHelpDocProps {
    doc?: string;
    style?: React.CSSProperties;
    /**
     * When doc is a function, it would need a param
     */
    param?: any;
}

export default function HelpDoc({ doc, style, param }: IHelpDocProps) {
    return doc ? (
        <Tooltip title={param ? (Doc as any)[doc](param) : (Doc as any)[doc]}>
            <QuestionCircleOutlined className="help-doc" style={style} />
        </Tooltip>
    ) : null;
}
