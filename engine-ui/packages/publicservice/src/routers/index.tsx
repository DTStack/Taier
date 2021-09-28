import React, { lazy, Suspense } from 'react';

//custom components
import Exception from '@/components/Exception';
import Loading from '@/components/loading';

const PageNotFound = () => (
  <Exception status={404} subTitle="对不起，你访问的页面不存在" />
);

const createLazyRoute = (RouteComponent: any) => {
  return function (props: any) {
    return (
      <Suspense fallback={<Loading />}>
        <RouteComponent {...props} />
      </Suspense>
    );
  };
};

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
