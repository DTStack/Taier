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

export interface TableCellProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
    value?: string;
    style?: React.CSSProperties;
    onChange?: any;
}
export default function TableCell(props: TableCellProps) {
    const originStyle: React.CSSProperties = {
        textIndent: '5px',
        backgroundColor: 'transparent',
        backgroundImage: 'none',
        width: '100%',
        border: 'none',
    };

    const { style } = props;
    const newStyle: any = { ...originStyle, ...style };

    return <textarea {...props} style={newStyle} />;
}
