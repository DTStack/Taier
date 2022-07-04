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

import classNames from 'classnames';
import { connect } from '@dtinsight/molecule/esm/react';
import molecule from '@dtinsight/molecule';
import { rightBarService } from '@/services';
import type { IRightbarState } from '@/services/rightBarService';
import type { RightBarKind } from '@/interface';
import './index.scss';
import taskRenderService from '@/services/taskRenderService';

interface IProps {
	editor: molecule.model.IEditor;
	rightBar: IRightbarState;
}

export default connect(
	{ editor: molecule.editor, rightBar: rightBarService },
	({ editor, rightBar }: IProps) => {
		const { current: propsCurrent } = editor;
		const { width, current } = rightBar;

		const handleClickTab = (key: RightBarKind) => {
			const nextCurrent = current === key ? null : key;
			rightBarService.setCurrent(nextCurrent);
		};

		return (
			<div className="dt-right-bar" style={{ width }}>
				<div className="dt-right-bar-content" key={propsCurrent?.activeTab}>
					{current && rightBarService.createContent(current)}
				</div>
				<div className="dt-right-bar-title">
					{taskRenderService
						.renderRightBar(propsCurrent?.tab?.data?.taskType, propsCurrent?.tab?.data)
						.map((key) => (
							<div
								className={classNames(
									'dt-right-bar-title-item',
									current === key && 'active',
								)}
								role="tab"
								key={key}
								onClick={() => handleClickTab(key)}
							>
								{rightBarService.getTextByKind(key)}
							</div>
						))}
				</div>
			</div>
		);
	},
);
