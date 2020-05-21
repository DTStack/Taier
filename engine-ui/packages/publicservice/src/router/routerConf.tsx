
import BasicLayout  from '@/layouts/basicLayout';
import AdvanceLayout  from '@/layouts/advanceLayout';
import Page404 from 'pages/exception/404';
// import Login from '@/pages/user/login';
// import Register from '@/pages/user/register';
import Home from 'pages/home'


const routerConf = [
  {
    path: '/basic/enter-park',
    layout: BasicLayout,
    component: Home,
  },
  {
    path: '/advance/ticket',
    layout: AdvanceLayout,
    component: Home,
  },
  // {
  //  path:'/login',
  //  layout: null,
  //  component: Login,
  // },
  // {
  //   path:'/register',
  //   layout: null,
  //   component: Register,
  //  },
		{
		path: '*',
    layout: BasicLayout,
    component: Page404,
  }
];

export default routerConf;
