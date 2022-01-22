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
import { Divider } from 'antd';
import { EnumModalActionType } from './types';

export const columnsGenerator = ({
  handleModalDetailAction,
  handleModelRecover,
}) => {
  return [
    {
      title: '版本号',
      dataIndex: 'version',
      key: 'version',
      render: (version) => `V${version}`,
    },
    {
      title: '操作人',
      dataIndex: 'operator',
      key: 'operator',
    },
    {
      title: '操作时间',
      dataIndex: 'operateTime',
      key: 'operateTime',
    },
    {
      title: '操作',
      key: 'operation',
      width: 200,
      fixed: 'right',
      render: (text, record) => {
        return (
          <span>
            <a
              className="btn-link"
              onClick={() => {
                handleModalDetailAction({
                  type: EnumModalActionType.OPEN,
                  payload: {
                    modelId: record.modelId,
                    version: record.version,
                  },
                });
              }}>
              查看
            </a>
            <Divider type="vertical" />
            <a
              className="btn-link"
              onClick={() => {
                handleModelRecover(record.modelId, record.version);
              }}>
              恢复
            </a>
          </span>
        );
      },
    },
  ];
};
