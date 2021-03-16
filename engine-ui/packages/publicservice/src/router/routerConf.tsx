import React from 'react';
import createLayout from '@/layouts';
import Exception from '@/components/Exception';
import Entry from 'pages/entry/index';
import List from 'pages/DataModel/List';
import ModelModify from '@/pages/DataModel/ModelModify';

const Layout = createLayout(true, false);

const PageNotFound = () => (
  <Exception status={404} subTitle="对不起，你访问的页面不存在" />
);

const routerConf = [
  {
    path: '/',
    layout: Layout,
    component: Entry,
  },
  {
    path: '/data-model',
    children: [
      {
        path: 'list',
        layout: Layout,
        component: List,
      },
      {
        path: 'add',
        layout: Layout,
        component: ModelModify,
      },
      {
        path: 'edit/:id',
        layout: Layout,
        component: ModelModify,
      },
      {
        path: '*',
        layout: Layout,
        component: PageNotFound,
      }
    ]
  },
  {
    path: '/data-model',
    layout: Layout,
    component: () => <div>hello</div>
  },
  {
    path: '*',
    layout: Layout,
    component: PageNotFound,
  },
];

export default routerConf;
