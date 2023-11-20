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

import { useState } from 'react';
import { CheckCircleFilled, CloseCircleFilled } from '@ant-design/icons';
import { Modal,Tooltip } from 'antd';
import classNames from 'classnames';
import { isArray } from 'lodash';

import type { COMPONENT_TYPE_VALUE } from '@/constant';
import './index.scss';

const TEST_STATUS = {
    SUCCESS: true,
    FAIL: false,
};

interface ITestStatusProps {
    clusterResourceDescription?: null | string;
    componentTypeCode: COMPONENT_TYPE_VALUE;
    componentVersion?: string | null;
    multiVersion?: ITestStatusProps[];
    errorMsg:
        | string
        | null
        | {
              componentVersion?: string | null;
              errorMsg: string | null;
          }[];
    result: null | boolean;
}

interface ITestRestIconProps {
    testStatus: ITestStatusProps;
}

export default function TestRestIcon({ testStatus }: ITestRestIconProps) {
    const [showMsg, setShowMsg] = useState(false);

    const showDetailErrMessage = (msgContent: JSX.Element | JSX.Element[]) => {
        setShowMsg(false);
        Modal.error({
            title: `错误信息`,
            content: <div style={{ maxHeight: 'calc(100vh - 300px)', overflow: 'auto' }}>{msgContent}</div>,
            zIndex: 1061,
        });
    };

    const matchCompTest = (testResult: ITestStatusProps) => {
        switch (testResult?.result) {
            case TEST_STATUS.SUCCESS: {
                return <CheckCircleFilled className="success-icon" />;
            }
            case TEST_STATUS.FAIL: {
                const msgContent = isArray(testResult?.errorMsg) ? (
                    testResult?.errorMsg?.map((msg) => (
                        <p key={msg.componentVersion}>
                            {msg.componentVersion ? `${msg.componentVersion} : ` : ''}
                            {msg.errorMsg}
                        </p>
                    ))
                ) : (
                    <span>{testResult?.errorMsg}</span>
                );
                return (
                    <Tooltip
                        visible={showMsg}
                        title={
                            <a
                                className={classNames('text-white', 'overflow-scroll')}
                                onClick={() => showDetailErrMessage(msgContent)}
                            >
                                {msgContent}
                            </a>
                        }
                        placement="right"
                        onVisibleChange={(v) => setShowMsg(v)}
                        overlayInnerStyle={{ maxHeight: 300, overflow: 'auto' }}
                    >
                        <CloseCircleFilled className="err-icon" />
                    </Tooltip>
                );
            }
            default: {
                return null;
            }
        }
    };
    return matchCompTest(testStatus);
}
