import * as React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Layout } from 'antd';
import * as global from '@/pages/global/redux/action';
import ErrorBoundary from '@/components/ErrorBoundary';
import HeaderBar from './HeaderBar';
import SiderBar from './SiderBar';
import './style.scss';

const { Content } = Layout;

const createLayout = (isHeaderHide: boolean, isSiderHide: boolean) => {
  return connect(
    (state) => ({ ...state.global }),
    (dispatch) => bindActionCreators({ ...global }, dispatch)
  )((props) => {
    return (
      <ErrorBoundary>
        <Layout>
          {isHeaderHide && <HeaderBar location={location} />}
          <Layout>
            {isSiderHide && <SiderBar location={location} />}
            <Layout>
              <Content>{props.children}</Content>
            </Layout>
          </Layout>
        </Layout>
      </ErrorBoundary>
    );
  });
};

export default createLayout;
