import * as React from "react"
import routerConf from './router-conf';
import {  Router, Switch, Route,Redirect } from 'react-router-dom';
import {history}from '@/utils/index'

function renderRouteConf(container, router, contextPath) {
  const routeChildren = [];
  const renderRoute = (routeContainer, routeItem, routeContextPath) => {
    let routePath;
    if (!routeItem.path) {
    } else if (routeItem.path === '/' || routeItem.path === '*') {
      routePath = routeItem.path;
    } else {
      routePath = `/${routeContextPath}/${routeItem.path}`.replace(/\/+/g, '/');
    }
    if (routeItem.layout && routeItem.component) {
      routeChildren.push(
        <Route
          key={routePath}
          exact
          path={routePath}
          render={(props) => {
            return React.createElement(
              routeItem.layout,
              props,
              React.createElement(routeItem.component, props)
            );
          }}
        />
      );
    }else if(routeItem.redirect){
      routeChildren.push(<Redirect key={routeItem.path}  exact from={routeItem.path} to={routeItem.redirect}/>);
    }else if (routeContainer && routeItem.component) {
      routeChildren.push(
        <Route
          key={routePath}
          exact
          path={routePath}
          render={(props) => {
            return React.createElement(
              routeContainer,
              props,
              React.createElement(routeItem.component, props)
            );
          }}
        />
      );
    } else {
      routeChildren.push(
        <Route
          key={routePath}
          exact
          path={routePath}
          component={routeItem.component}
        />
      );
    }
    if (Array.isArray(routeItem.children)) {
      routeItem.children.forEach((r) => {
        renderRoute(routeItem.component, r, routePath);
      });
    }
  };

  router.forEach((r) => {
    renderRoute(container, r, contextPath);
  });

  return <Switch>{routeChildren}</Switch>;
}
const routeChildren = renderRouteConf(null, routerConf, '/');
export default class Routers extends React.Component {
  render() {
    return (
      <Router history={history}>
        {routeChildren}
      </Router>
    );
  }
}
