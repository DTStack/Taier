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
import { DoubleRightOutlined } from '@ant-design/icons';
import classNames from 'classnames';

import './index.scss';

interface SlidePaneProps {
    children: React.ReactNode;
    visible: boolean;
    left?: string | number;
    width?: string | number;
    className?: string;
    style?: React.CSSProperties;
    onClose?: React.MouseEventHandler<HTMLSpanElement>;
    [propName: string]: any;
}

const slidePrefixCls = 'dtc-slide-pane';

export default function SlidePane({ className, visible, children, onClose, style = {} }: SlidePaneProps) {
    const slide = useRef<HTMLDivElement>(null);
    const myStyle: React.CSSProperties = {
        top: 0,
        transform: visible ? undefined : 'translate3d(150%, 0, 0)',
    };

    if (!visible) {
        myStyle.pointerEvents = 'none';
    }

    const handleKeyDown = (e: React.KeyboardEvent) => {
        if (e.key === 'Escape') {
            e.stopPropagation();
            if (onClose) {
                onClose(e as any);
            }
        }
    };

    useEffect(() => {
        if (visible) {
            slide.current?.focus();
        }
    }, [visible]);

    return (
        <div
            ref={slide}
            className={classNames(slidePrefixCls, className)}
            tabIndex={-1}
            style={{ ...myStyle, ...style }}
            onKeyDown={handleKeyDown}
        >
            <div
                className={`${slidePrefixCls}-conent`}
                data-testid="slidepane_container"
                style={{
                    display: visible ? 'block' : 'none',
                    height: '100%',
                }}
            >
                {children}
            </div>
            <span className={`${slidePrefixCls}-toggle`} data-testid="slidepane_action" onClick={onClose}>
                <DoubleRightOutlined />
            </span>
        </div>
    );
}
