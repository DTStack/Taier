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
import './index.scss';

interface IProps {
	comp: any;
	isMetadata: undefined | boolean;
}

export default class MetaIcon extends React.PureComponent<IProps, any> {
	render() {
		const { comp, isMetadata } = this.props;

		if (isMetadata ?? comp.multiVersion[0]?.isMetadata) {
			return <span className="c-metaIcon__title">Meta</span>;
		}
		return null;
	}
}
