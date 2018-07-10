import React, { Component } from "react";
import { assign } from 'lodash';
import { Button } from "antd";

import API from "../../../../api";
import { setCurrentPage } from '../../../../store/modules/realtimeTask/browser';

export default class Toolbar extends Component {

    sqlFormat = () => {

        const { currentTabData, dispatch } = this.props;
        const params = {
            sql: currentTabData.sqlText || ""
        };

        API.streamSqlFormat(params).then(res => {
            if (res.data) {
                const data = {
                    merged: true,
                    sqlText: res.data,
                    id: currentTabData.id,
                };
                const currentPage = assign(currentTabData, data);
                dispatch(setCurrentPage(currentPage));
            }
        });
    };

    render() {
        return (
            <div className="ide-header bd-bottom">
                <div className="ide-toolbar toolbar clear-offset">
                    <Button
                        icon="appstore-o"
                        title="格式化"
                        onClick={this.sqlFormat}
                    >
                        格式化
                    </Button>
                </div>
            </div>
        );
    }
}
