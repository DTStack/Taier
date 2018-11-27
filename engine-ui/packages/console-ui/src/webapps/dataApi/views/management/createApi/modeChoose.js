import React from 'react';

import { API_MODE } from '../../../consts';

class ModeChoose extends React.Component {
    state = {

    }

    render () {
        const { chooseMode } = this.props;

        return (
            <div className="modeChooseBox">
                <div className="modeChooseItem leftItem" onClick={chooseMode.bind(null, API_MODE.GUIDE)} >
                    <div className="modeTitle">
                        <img src="./public/dataApi/img/modeguide.png" /> 模版向导模式
                    </div>
                    <div className="modeDesc">
                        只支持单张表原生字段配置，适合单张表的AP查询生成。“step by  step,简单易上手”
                    </div>

                </div>
                <div className="modeChooseItem rightItem" onClick={chooseMode.bind(null, API_MODE.SQL)} >
                    <div className="modeTitle">
                        <img src="./public/dataApi/img/modesql.png" /> 自定义SQL模式
                    </div>
                    <div className="modeDesc">
                        支持多张表的关联输出，复杂查询条件及简单函数计算。“defined   by  yourself,灵活配置”
                    </div>
                </div>
            </div>
        )
    }
}

export default ModeChoose;
