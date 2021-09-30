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

import _ from 'lodash';

export function mergeDeep(object1: any, object2: any) {
	if (object1 == null || object2 == null) {
		return object2;
	} else if (!_.isPlainObject(object1) || !_.isPlainObject(object2)) {
		return object2;
	} else if (object1 === object2) {
		return object2;
	} else {
		if ('_isMergeAtom' in object2) {
			const isMergeAtom = object2._isMergeAtom;
			delete object2._isMergeAtom;

			if (isMergeAtom) {
				return object2;
			}
		}
		const obj = {
			...object1,
		};
		_.forEach(object2, (value, key) => {
			if (key in object1) {
				obj[key] = mergeDeep(object1[key], value);
			} else {
				obj[key] = value;
			}
		});

		return obj;
	}
}

export default {
	mergeDeep,
};
