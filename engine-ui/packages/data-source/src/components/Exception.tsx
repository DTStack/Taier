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
import { Link } from 'react-router';
import { Result, Button } from 'antd';
import { ResultStatusType } from 'antd/lib/result';

interface IProps {
	status: ResultStatusType;
	subTitle: React.ReactNode;
}

const Exception: React.FC<IProps> = ({ status, subTitle }) => (
	<Result
		status={status}
		title={status}
		style={{
			background: 'none',
		}}
		subTitle={subTitle}
		extra={
			<Link to="/">
				<Button type="primary">返回首页</Button>
			</Link>
		}
	/>
);

export default Exception;
