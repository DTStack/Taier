# dt-common

数栈前端公共模块, 主要包含以下公共组件：

- [用户](http://gitlab.prod.dtstack.cn/dt-insight-front/dt-common/tree/master/src/views/admin/user)
- [角色模块](http://gitlab.prod.dtstack.cn/dt-insight-front/dt-common/tree/master/src/views/admin/role)
- [消息](http://gitlab.prod.dtstack.cn/dt-insight-front/dt-common/tree/master/src/views/message)
- [全局公共常量](http://gitlab.prod.dtstack.cn/dt-insight-front/dt-common/blob/master/src/consts/index.ts)
- [公共静态资源](http://gitlab.prod.dtstack.cn/dt-insight-front/dt-common/tree/master/src/public)

## 安装

### npm

```bash
npm install dt-common
```

### yarn

```bash
yarn add dt-common
```

## 基本使用

### 公共业务组件

```typescript

import Container from 'dt-common/src/views'
import MsgCenter from 'dt-common/src/views/message'
import MsgList from 'dt-common/src/views/message/list'
import MsgDetail from 'dt-common/src/views/message/detail'

import SysAdmin from 'dt-common/src/views/admin'
import AdminUser from 'dt-common/src/views/admin/user'
import AdminRole from 'dt-common/src/views/admin/role'
import RoleAdd from 'dt-common/src/views/admin/role/add'
import RoleEdit from 'dt-common/src/views/admin/role/edit'
import Audit from 'dt-common/src/views/admin/audit'

<Route path="/" component={Container}>
    // 消息
    <Route path="/message" component={MsgCenter}>
        <IndexRoute component={MsgList} />
        <Route path="list" component={MsgList} />
        <Route path="detail/:msgId" component={MsgDetail} />
    </Route>
    // 用户管理
    <Route path="/admin" component={SysAdmin}>
        <IndexRoute component={AdminUser} />
        <Route path="user" component={AdminUser} />
        <Route path="role" component={AdminRole} />
        <Route path="role/add" component={RoleAdd} />
        <Route path="role/edit/:roleId" component={RoleEdit} />
        <Route path="audit" component={Audit} />
    </Route>
</Route>
```

### 导航组件

#### 基本使用

```typescript
import Navigator from 'dt-common/src/components/nav';

render () {
    return <Navigator />
}
```

#### 自定义导航

```typescript
import { Title, MyIcon, Logo, MenuRight, MenuLeft } from 'dt-common/src/components/nav';

 render () {
  
    return (
        <header className={`header default`}>
            <div className="logo left txt-left">
                <Logo />
            </div>
            {
                <MenuLeft
                    selectProjectsubMenu={this.props.selectProjectsubMenu}
                    user={user}
                    activeKey={current}
                    customItems={customItems}
                    menuItems={menuItems}
                    subMenuItems={subMenuItems}
                    licenseApps={licenseApps}
                    onClick={this.handleClick}
                />
            }
            {
                <MenuRight
                    activeKey={current}
                    user={user}
                    app={app}
                    apps={apps}
                    licenseApps={licenseApps}
                    onClick={this.clickUserMenu}
                    settingMenus={settingMenus}
                    showHelpSite={showHelpSite}
                    helpUrl={helpUrl}
                />
            }
        </header>
    )
}
```

更多应用，请参考以下项目

- [dt-portal-front](http://gitlab.prod.dtstack.cn/dt-insight-front/dt-portal-front)
- [dt-batch-works](http://gitlab.prod.dtstack.cn/dt-insight-front/dt-batch-works)
- [更多...](http://gitlab.prod.dtstack.cn/dt-insight-front)

## 更新日志

[CHANGELOG](http://gitlab.prod.dtstack.cn/dt-insight-front/dt-common/blob/master/CHANGELOG.md)

## Contributing

[CONTRIBUTING](http://gitlab.prod.dtstack.cn/dt-insight-front/dt-common/blob/master/CONTRIBUTING.md)
