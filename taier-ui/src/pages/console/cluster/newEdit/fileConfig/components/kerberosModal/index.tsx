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

import * as React from 'react';
import { message, Modal } from 'antd';
import Editor from '@/components/editor';
import { debounce } from 'lodash';
import Api from '@/api';
import './index.scss';

const editorStyle: any = { height: '100%' };

interface IProps {
	visible: boolean;
	krbconfig: string;
	onCancel: (krb5Content: string) => void;
}

interface IState {
	krb5Content: string;
}

export default class KerberosModal extends React.Component<IProps, IState> {
	state: IState = {
		krb5Content: '',
	};

	componentDidMount() {
		const { krbconfig } = this.props;
		this.setState({
			krb5Content: krbconfig,
		});
	}

	editorParamsChange = (nextValue: string) => {
		this.setState({
			krb5Content: nextValue,
		});
	};

	debounceEditorChange = debounce(this.editorParamsChange, 300, {
		maxWait: 2000,
	});

	onOK = async () => {
		const { onCancel } = this.props;
		const { krb5Content } = this.state;
		const res = await Api.updateKrb5Conf({ krb5Content });
		if (res.code === 1) {
			onCancel(krb5Content);
			message.success('更新成功');
		}
	};

	render() {
		const { visible, onCancel, krbconfig } = this.props;
		return (
			<Modal
				title="合并后的krb5.conf"
				visible={visible}
				onCancel={() => onCancel(krbconfig)}
				onOk={this.onOK}
				okText="保存"
			>
				<div style={editorStyle}>
					<Editor
						sync
						value={krbconfig || ''}
						className="c-kerberosModal__edior"
						language="ini"
						options={{
							readOnly: false,
							minimap: {
								enabled: false,
							},
						}}
						onChange={this.debounceEditorChange.bind(this)}
					/>
				</div>
			</Modal>
		);
	}
}
