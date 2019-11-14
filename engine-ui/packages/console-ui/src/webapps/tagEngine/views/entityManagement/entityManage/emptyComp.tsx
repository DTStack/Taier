import * as React from 'react';
import './style.scss';

function emptyComp () {
    return (
        <div className="entity-empty-comp">
            <div className="one-item">
                <img src="public/tagEngine/img/1.png" />
                <span>链接业务的分析对象</span>
            </div>
            <div className="str-line"></div>
            <svg width={6} height={7}>
                <path d="M0 0 L6 4 L0 7 Z" style={{ fill: '#2491F7' }} />
            </svg>
            <div className="one-item">
                <img src="public/tagEngine/img/2.png" />
                <span>选择你关系的数据维度</span>
            </div>
            <div className="str-line"></div>
            <svg width={6} height={7}>
                <path d="M0 0 L6 4 L0 7 Z" style={{ fill: '#2491F7' }} />
            </svg>
            <div className="one-item">
                <img src="public/tagEngine/img/3.png" />
                <span>完成标签体系初始化</span>
            </div>
        </div>
    )
}

export default emptyComp;
