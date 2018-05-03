import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty, cloneDeep } from 'lodash';
import { Row, Col } from 'antd';


export default class DashBoard extends Component {

    state = {
        
    }

    componentDidMount() {
    }


    render() {

        return (
            <div style={{ margin: 20 }}>
                标签工厂
            </div>
        )
    }
}