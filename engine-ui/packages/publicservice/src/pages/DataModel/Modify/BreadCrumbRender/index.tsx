import React from 'react';
import { Breadcrumb } from 'antd';
import { withRouter } from 'react-router-dom'

interface IBreadcrumbLink {
  label: string;
  href?: string;
  onClick?: () => void;
}

interface IPropsBreadcrumbRender {
  links: IBreadcrumbLink[];
  history?: any;
}

const BreadcrumbRender = (props: IPropsBreadcrumbRender) => {
  const { links, history } = props;
  return (
    <Breadcrumb>
      {
        links.map(item => {
          const { onClick, href } = item;
          let callback = () => history.push(href);
          if (typeof onClick === 'function') callback = onClick;
          return (
            <Breadcrumb.Item>
              <a onClick={callback}>
                {item.label}
              </a>
            </Breadcrumb.Item>
          )
        })
      }
    </Breadcrumb>
  )
}

export default withRouter(BreadcrumbRender);
