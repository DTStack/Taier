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

import { TASK_TYPE_ENUM } from '@/constant';
import molecule from '@dtinsight/molecule';
import { connect } from '@dtinsight/molecule/esm/react';

const Language = connect(molecule.editor, ({ current }: molecule.model.IEditor) => {
	if (!current) return null;

	const renderLanguage = () => {
		const dataType = current.tab?.data?.taskType;
		switch (dataType) {
			case TASK_TYPE_ENUM.SQL: {
				return 'SparkSQL';
			}
			case TASK_TYPE_ENUM.SYNC: {
				return 'DataSync';
			}
			default: {
				return null;
			}
		}
	};

	return <span>{renderLanguage()}</span>;
});

export default Language;
