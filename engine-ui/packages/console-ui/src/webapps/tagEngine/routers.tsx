import asyncComponent from 'utils/asyncLoad';
import NotFund from 'widgets/notFund';

// 继承主应用的的公共View组件
import MsgCenter from 'main/views/message';
import MsgList from 'main/views/message/list';
import MsgDetail from 'main/views/message/detail';

import SysAdmin from 'main/views/admin';
import AdminUser from 'main/views/admin/user';
import AdminRole from 'main/views/admin/role';
import GRoleAdd from 'main/views/admin/role/add';
import GRoleEdit from 'main/views/admin/role/edit';
import SideLayout from './views/layout/sideLayout';
// ======= 实体管理 =======
import EMEntityEdit from './views/entityManagement/entityManage/entityEdit'
import EMEntityDetail from './views/entityManagement/entityManage/entityDetail'

// ======= 关系管理 =======
import CreateRelation from './views/entityManagement/relationManage/update/create'
import EditRelation from './views/entityManagement/relationManage/update/edit'
import RelationDetail from './views/entityManagement/relationManage/relationDetail'

// ======= 字典管理 =======
import EMDictonaryEdit from './views/entityManagement/dictionaryManage/edit'
import EMDictonaryDetail from './views/entityManagement/dictionaryManage/detail'

// ======= 群组管理 =======
import GroupManagement from './views/groupAnalyse/management';
import GroupUpload from './views/groupAnalyse/management/upload/create';
import GroupUploadEdit from './views/groupAnalyse/management/upload/edit';
import GroupDetail from './views/groupAnalyse/management/detail';

// ======= 实体管理 =======
const EntityManage = asyncComponent(
    () =>
        import('./views/entityManagement/entityManage').then(
            (module: any) => module.default
        ),
    { name: 'entityManage' }
);
//  ======= 关系管理 =======
const RelationManage = asyncComponent(
    () =>
        import('./views/entityManagement/relationManage').then(
            (module: any) => module.default
        ),
    { name: 'relationManage' }
);
//  ======= 字典管理 =======
const DictionaryManage = asyncComponent(
    () =>
        import('./views/entityManagement/dictionaryManage').then(
            (module: any) => module.default
        ),
    { name: 'dictionaryManage' }
);
// ======= 标签中心 =======
const LabelManage = asyncComponent(
    () =>
        import('./views/labelCenter/labelManage').then(
            (module: any) => module.default
        ),
    { name: 'labelManage' }
);
const LabelDirectory = asyncComponent(
    () =>
        import('./views/labelCenter/labelDirectory').then(
            (module: any) => module.default
        ),
    { name: 'labelDirectory' }
);
const CreateLabel = asyncComponent(
    () =>
        import('./views/labelCenter/createLabel').then(
            (module: any) => module.default
        ),
    { name: 'createLabel' }
);
const LabelDetails = asyncComponent(
    () =>
        import('./views/labelCenter/labelDetails').then(
            (module: any) => module.default
        ),
    { name: 'labelDetails' }
);
const EditAtomicLabel = asyncComponent(
    () =>
        import('./views/labelCenter/editAtomicLabel').then(
            (module: any) => module.default
        ),
    { name: 'editAtomicLabel' }
);
// ======= 数据源管理 =======
const DataSourceStream = asyncComponent(
    () =>
        import('./views/dataSource').then(
            (module: any) => module.default
        ),
    { name: 'dataSourceStream' }
);

const routeConfig = [
    {
        path: '/',
        component: null,
        indexRoute: { component: SideLayout },
        childRoutes: [
            {
                path: '/message',
                component: MsgCenter,
                indexRoute: MsgList,
                childRoutes: [
                    {
                        path: 'list',
                        component: MsgList
                    },
                    {
                        path: 'detail/:msgId',
                        component: MsgDetail
                    }
                ]
            },
            {
                path: '/admin',
                component: SysAdmin,
                indexRoute: AdminUser,
                childRoutes: [
                    { path: 'user', component: AdminUser },
                    { path: 'role', component: AdminRole },
                    { path: 'role/add', component: GRoleAdd },
                    { path: 'role/edit/:roleId', component: GRoleEdit }
                ]
            }
        ]
    },
    {
        path: '/tag',
        component: SideLayout,
        indexRoute: EntityManage,
        childRoutes: [
            {
                path: '/entityManage',
                component: EntityManage
            },
            {
                path: '/entityManage/detail',
                component: EMEntityDetail
            },
            {
                path: '/entityManage/edit',
                component: EMEntityEdit
            },
            {
                path: '/relationManage',
                component: RelationManage
            },
            {
                path: '/relationManage/create',
                component: CreateRelation
            },
            {
                path: '/relationManage/edit',
                component: EditRelation
            },
            {
                path: '/relationManage/detail',
                component: RelationDetail
            },
            {
                path: '/dictionaryManage',
                component: DictionaryManage
            },
            {
                path: '/dictionaryManage/detail',
                component: EMDictonaryDetail
            },
            {
                path: '/dictionaryManage/edit',
                component: EMDictonaryEdit
            },
            {
                path: '/labelCenter',
                component: LabelManage
            },
            {
                path: '/labelDirectory',
                component: LabelDirectory
            },
            {
                path: '/labelDetails',
                component: LabelDetails
            },
            {
                path: '/editAtomicLabel',
                component: EditAtomicLabel
            },
            {
                path: '/createLabel',
                component: CreateLabel
            }, {
                path: '/groupAnalyse',
                component: GroupManagement
            },
            {
                path: '/groupAnalyse/upload',
                component: GroupUpload
            },
            {
                path: '/groupAnalyse/upload/edit',
                component: GroupUploadEdit
            },
            {
                path: '/groupAnalyse/detail',
                component: GroupDetail
            },
            {
                path: '/database',
                component: DataSourceStream
            }
        ]
    },
    {
        path: '/*',
        component: NotFund
    }
];
export default routeConfig;
