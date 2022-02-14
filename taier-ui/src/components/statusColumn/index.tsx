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

import { Row, Col } from 'antd';

export default () => {
	return (
		<div
			className="box-title graph-info"
			style={{
				bottom: 35,
				height: '40px',
				background: 'transparent',
				border: 0,
				boxShadow: 'none',
			}}
		>
			<Row justify="start">
				<Col span={4} style={{ minWidth: 200 }}>
					<div className="mxYellow" />
					等待提交/提交中/等待运行
				</Col>
				<Col span={3}>
					<div className="mxBlue" />
					运行中
				</Col>
				<Col span={3}>
					<div className="mxGreen" />
					成功
				</Col>
				<Col span={3}>
					<div className="mxRed" />
					失败
				</Col>
				<Col span={4}>
					<div className="mxGray" />
					冻结/取消
				</Col>
			</Row>
		</div>
	);
};
