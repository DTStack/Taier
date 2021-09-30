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
import { Icon, Tooltip } from 'antd';
import * as Doc from './docs';

export const relativeStyle: any = {
	position: 'initial',
	right: 0,
	top: 0,
};

export default class HelpDoc extends React.Component<any, any> {
	render() {
		const { doc, style } = this.props;
		return doc ? (
			<Tooltip title={(Doc as any)[doc]}>
				<Icon className="help-doc" style={style} type="question-circle-o" />
			</Tooltip>
		) : (
			''
		);
	}
}
