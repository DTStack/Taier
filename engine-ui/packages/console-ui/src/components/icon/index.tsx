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
import './style.scss';

const imgBase = 'public/img/icon';
const imgDarkBase = 'public/img/theme-dark';

export default class Icon extends React.Component<any, any> {
	render() {
		const props = this.props;
		const base = !props.themeDark ? imgBase : imgDarkBase;
		return (
			<img
				{...props}
				className={`rdos-icon ${props.className || ''}`}
				alt={props.alt}
				src={`${base}/${props.type}.svg`}
			/>
		);
	}
}
