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

import { TableJoinInfo } from 'pages/DataModel/types';
interface ITableItem {
	schema: string;
	tableName: string;
}

export const isTableSame = (
	table1: { schema: string; tableName: string },
	table2: { schema: string; tableName: string },
) => {
	return table1.schema === table2.schema && table1.tableName === table2.tableName;
};

// export const deleteLinkChain = (list, id, masterTable) => {
//   const ids = [];
//   ids.push(id);
//   while (ids.length > 0) {
//     const id = ids.pop();
//     const target = list.find((item) => item.id === id);
//     if (!target) return list;
//     const filterIds = list
//       .filter((item) => {
//         return (
//           isTableSame(
//             {
//               schema: item.leftSchema,
//               tableName: item.leftTable,
//             },
//             {
//               schema: target.schema,
//               tableName: target.table,
//             }
//           ) &&
//           !isTableSame(
//             {
//               schema: item.leftSchema,
//               tableName: item.leftTable,
//             },
//             masterTable
//           )
//         );
//       })
//       .map((item) => item.id);

//     list = list.filter((item) => item.id !== id);
//     ids.push(...filterIds);
//   }
//   return list;
// };

// 关联表删除逻辑：
// TODO: 依赖后端，关联关系列表需要返回左表别名
export const deleteLinkChain = (list, id, masterTable) => {
	const ids = [];
	ids.push(id);
	while (ids.length > 0) {
		const id = ids.pop();
		// 找到待删除的关联关系
		const target = list.find((item) => item.id === id);
		if (!target) {
			// 若未找到，表示关联关系已经被删除了，直接返回
			return list;
		} else {
			// 选取target的右表，遍历所有关联关系列表；
			// 筛选所有已target右表作为左表关联的关联关系，作为下一次删除的id，依次循环删除关联关系
			const filterIds = list
				.filter((item) => {
					return target.tableAlias === item.leftTableAlias;
				})
				.map((table) => table.id);
			list = list.filter((item) => item.id !== id);
			ids.push(...filterIds);
		}
	}
	return list;
};

export const relationListRemove = (
	relationList: TableJoinInfo[],
	id: number | string,
	masterTable: ITableItem,
) => {
	const list = deleteLinkChain(relationList, id, masterTable);
	return list;
};
