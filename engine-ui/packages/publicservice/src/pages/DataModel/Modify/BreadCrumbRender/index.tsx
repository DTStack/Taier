import React from 'react';
import { Breadcrumb } from 'antd';
import { withRouter } from 'react-router';

interface IBreadcrumbLink {
  label: string;
  href?: string;
  onClick?: () => void;
}

interface IPropsBreadcrumbRender {
  links: IBreadcrumbLink[];
  router?: any;
}

const BreadcrumbRender = (props: IPropsBreadcrumbRender) => {
  const { links, router } = props;
  return (
    <Breadcrumb>
      {links.map((item, index) => {
        const { onClick, href } = item;
        let callback = () => router.push(href);
        if (typeof onClick === 'function') callback = onClick;
        return (
          <Breadcrumb.Item key={index}>
            <a onClick={callback}>{item.label}</a>
          </Breadcrumb.Item>
        );
      })}
    </Breadcrumb>
  );
};

export default withRouter(BreadcrumbRender);
