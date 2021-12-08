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

import React, { useEffect, useState } from 'react';
import { Checkbox, Row, Col, message } from 'antd';
import { API } from '@/services';
interface IProps {
  record: {
    dataInfoId: number;
  };
  oncheck(checkedValues: string): void;
}

export default function AuthSelect(props: IProps) {
  let { record, oncheck } = props;
  const [authList, setAuthList] = useState([]);
  const [checkedList, setCheckedList] = useState([]);

  //获取产品授权列表
  const getauthProductList = async () => {
    let { data, success } = await API.authProductList({
      dataInfoId: record.dataInfoId,
    });
    if (success) {
      if (data.length > 0) {
        data.forEach((item) => {
          if (item.isAuth === 1 || item.isImport === 1) {
            let newList: any = checkedList;
            newList.push(item.appType);
            setCheckedList(newList);
            oncheck(newList);
          }
        });

        setAuthList(data);
      }
    } else {
      message.error('获取产品授权列表失败！');
    }
  };

  useEffect(() => {
    getauthProductList();
  }, []);

  const onChange = (checkedValues) => {
    oncheck(checkedValues);
    setCheckedList(checkedValues);
  };

  return (
    <div>
      <p className="auth-header">
        点击进行授权/取消，若已在产品中应用，不能取消授权
      </p>
      <Checkbox.Group
        style={{ width: '100%' }}
        onChange={onChange}
        value={checkedList}>
        <Row style={{ padding: '22px 44px 0 44px' }}>
          {authList.length > 0 &&
            authList.map((item, index) => (
              <Col
                key={index}
                span={8}
                onClick={() => {
                  if (item.isImport === 1) {
                    message.error('已在产品中应用，不能取消授权。');
                  }
                }}
                style={{ marginBottom: 24 }}>
                <Checkbox value={item.appType} disabled={item.isImport === 1}>
                  {item.appName}
                </Checkbox>
              </Col>
            ))}
        </Row>
      </Checkbox.Group>
    </div>
  );
}
