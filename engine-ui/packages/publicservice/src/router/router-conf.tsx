
import BasicLayout  from '@/layouts/BasicLayout';
import AdvanceLayout  from '@/layouts/AdvanceLayout';
import Page404 from 'pages/exception/404';
import Login from '@/pages/user/Login';
import Register from '@/pages/user/Register';
import Home from 'pages/Home'


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
  {
   path:'/login',
   layout: null,
   component: Login,
  },
  {
    path:'/register',
    layout: null,
    component: Register,
   },
		{
		path: '*',
    layout: BasicLayout,
    component: Page404,
  }
];

export default routerConf;
