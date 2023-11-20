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

import classNames from 'classnames';

import './index.scss';

enum CIRCLE_TYPES_ENUM {
    running = 'running',
    finished = 'finished',
    stopped = 'stopped',
    frozen = 'frozen',
    fail = 'fail',
    submitting = 'submitting',
    restarting = 'restarting',
    waitSubmit = 'waitSubmit',
}

type CircleType = keyof typeof CIRCLE_TYPES_ENUM;

interface CircleProps {
    type?: CircleType;
    className?: string;
    style?: React.CSSProperties;
    onClick?: () => void;
    children?: React.ReactNode;
}

export default function Circle({ className, type, children, ...other }: CircleProps) {
    const prefixCls = 'dtc-circle';
    const classes = classNames({
        className,
        [`${prefixCls}-default`]: true,
        [`${prefixCls}-${type}`]: type,
    });

    return (
        <div {...other} className={classes}>
            {children || ''}
        </div>
    );
}
