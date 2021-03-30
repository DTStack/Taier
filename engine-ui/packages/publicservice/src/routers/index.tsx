import React, { lazy, Suspense } from 'react';
//import from dt-common
import Main from 'lib/dt-common/src/views';
import MsgCenter from 'lib/dt-common/src/views/message';
import MsgList from 'lib/dt-common/src/views/message/list';
import MsgDetail from 'lib/dt-common/src/views/message/detail';
//custom components
import Layout from '../layouts';
import Exception from '@/components/Exception';

const PageNotFound = () => (
  <Exception status={404} subTitle="对不起，你访问的页面不存在" />
);

const createLazyRoute = (RouteComponent: any) => {
  return function (props: any) {
    return (
      <Suspense fallback={<PageNotFound />}>
        <RouteComponent {...props} />
      </Suspense>
    );
  };
};

const ModelList = createLazyRoute(lazy(() => import('pages/DataModel/List')));
const ModelModify = createLazyRoute(
  lazy(() => import('pages/DataModel/Modify'))
);

const DataSourceList = createLazyRoute(
  lazy(() => import('pages/DataSource/List'))
);
const AddSource = createLazyRoute(
  lazy(() => import('pages/DataSource/AddSource'))
);
const EditSource = createLazyRoute(
  lazy(() => import('pages/DataSource/EditSource'))
);

export function getRoutes() {
  return [
    {
      path: '/',
      component: Main,
      childRoutes: [
        {
          path: '/message',
          component: MsgCenter,
          indexRoute: { component: MsgList },
          childRoutes: [
            {
              path: 'list',
              component: MsgList,
            },
            {
              path: 'detail/:msgId',
              component: MsgDetail,
            },
          ],
        },
        {
          path: '/data-model',
          component: Layout,
          childRoutes: [
            {
              path: 'list',
              component: ModelList,
            },
            {
              path: 'add',
              component: ModelModify,
            },
            {
              path: 'edit/:id',
              component: ModelModify,
            },
          ],
        },
        {
          path: '/data-source',
          component: Layout,
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
      ],
    },
    {
      path: '/*',
      component: PageNotFound,
    },
  ];
}
