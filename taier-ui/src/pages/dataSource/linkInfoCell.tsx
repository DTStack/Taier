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

import type { IDataSourceProps } from '@/interface';
import { linkMapping } from '@/utils/enums';
import './linkInfoCell.scss';

export default function LinkInfoCell(props: { sourceData: IDataSourceProps }) {
    const { sourceData } = props;
    const arr = linkMapping(`${sourceData.dataType}${sourceData.dataVersion || ''}`);

    let data: Record<string, string> = {};
    try {
        data = JSON.parse(sourceData.linkJson!) || {};
    } catch (error) {
        // don't handle this error
    }

    if (arr) {
        return (
            <div>
                {arr.map(([key, text]: any) => {
                    return (
                        <p
                            key={key}
                            style={{
                                display: 'flex',
                                lineHeight: 1.5,
                                marginBottom: 0,
                            }}
                        >
                            <span style={{ color: '#999', flexShrink: 0 }}>{text}：</span>
                            <span className="link-json" title={data[key] || ''}>
                                {data[key] || ''}
                            </span>
                        </p>
                    );
                })}
            </div>
        );
    }

    return null;
}
