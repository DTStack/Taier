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

import React, { lazy, Suspense } from 'react';

//custom components
import Exception from '@/components/Exception';
import Loading from '@/components/loading';

const PageNotFound = () => <Exception status={404} subTitle="对不起，你访问的页面不存在" />;

const createLazyRoute = (RouteComponent: any) => {
	return function (props: any) {
		return (
			<Suspense fallback={<Loading />}>
				<RouteComponent {...props} />
			</Suspense>
		);
	};
};

const DataSourceList = createLazyRoute(lazy(() => import('pages/DataSource/List')));

const AddSource = createLazyRoute(lazy(() => import('pages/DataSource/AddSource')));

const EditSource = createLazyRoute(lazy(() => import('pages/DataSource/EditSource')));

export function getRoutes() {
	return [
		{
			path: '/data-source',
			childRoutes: [
				{
					path: 'list',
					component: DataSourceList,
				},
				{
					path: 'add',
					component: AddSource,
				},
				{
					path: 'edit',
					component: EditSource,
				},
			],
		},
		{
			path: '/*',
			component: PageNotFound,
		},
	];
}
