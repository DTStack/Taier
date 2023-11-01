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

/*
 * @Author: 12574
 * @Date:   2018-09-18 16:58:30
 * @Last Modified by:   12574
 * @Last Modified time: 2018-09-28 11:43:51
 */
import * as React from 'react';
import { Modal } from 'antd';

import CodeEditor from '@/components/editor';

class ViewDetail extends React.Component<any, any> {
    state: any = {
        editor: {
            sql: '',
            cursor: undefined,
            sync: true,
        },
    };
    render() {
        const { title } = this.props;
        return (
            <Modal
                title={title || '任务详情'}
                width={650}
                onCancel={this.props.onCancel}
                onOk={this.props.onCancel}
                visible={this.props.visible}
                destroyOnClose
            >
                <CodeEditor
                    style={{ height: '400px', marginTop: '1px' }}
                    value={this.props.resource}
                    language="json"
                    options={{
                        readOnly: true,
                        minimap: {
                            enabled: false,
                        },
                    }}
                />
            </Modal>
        );
    }
}
export default ViewDetail;
