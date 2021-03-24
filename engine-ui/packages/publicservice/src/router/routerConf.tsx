import React from 'react';
import createLayout from '@/layouts';
import Exception from '@/components/Exception';
import Entry from 'pages/entry/index';
import ModelList from 'pages/DataModel/List';
import ModelModify from '@/pages/DataModel/Modify';

import dataSource from "pages/DataSource/List"
import addSource from "pages/DataSource/AddSource"
import editSource from "@/pages/DataSource/EditSource"

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
  //数据源项目路径
  {
    path: '/data-source',
    // layout: Layout,
    component: dataSource,
  },
  {
    path: '/add-source',
    // layout: Layout,
    component: addSource,
  },
  {
    path: '/edit-source',
    // layout: Layout,
    component: editSource,
  },
  {
    path: '/data-model',
    children: [
      {
        path: 'list',
        layout: Layout,
        component: ModelList,
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
      },
    ],
  },
  {
    path: '*',
    layout: Layout,
    component: PageNotFound,
  },
];

export default routerConf;
