import React from 'react';

import './style.scss';
import { HotTable } from '@handsontable/react';
import 'handsontable/languages/zh-CN.js';

class SpreadSheet extends React.Component {
    tableRef = React.createRef()
    componentDidUpdate (prevProps, prevState) {
        if (prevProps != this.props) {
            if (this.tableRef) {
                this.removeRenderClock();
                this._renderColck = setTimeout(() => {
                    this.tableRef.current.hotInstance.render();
                })
            }
        }
    }
    removeRenderClock () {
        if (this._renderColck) {
            clearTimeout(this._renderColck)
        }
    }
    componentWillUnmount (prevProps, prevState) {
        this.removeRenderClock();
    }
    getData () {
        const { data, columns } = this.props;
        let showData = data;
        if (!showData || !showData.length) {
            const emptyArr = new Array(columns.length).fill('', 0, columns.length);
            emptyArr[0] = '暂无数据';
            showData = [emptyArr];
        }
        return showData;
    }
    getMergeCells () {
        const { data, columns } = this.props;
        if (!data || !data.length) {
            return [{ row: 0, col: 0, rowspan: 1, colspan: columns.length }]
        }
        return null;
    }
    getCell () {
        const { data } = this.props;
        if (!data || !data.length) {
            return [{ row: 0, col: 0, className: 'htCenter htMiddle' }]
        }
        return null;
    }
    afterGetRowHeaderRenderers (renderers) {
        console.log(renderers);
    }
    render () {
        const { columns } = this.props;
        const showData = this.getData();
        return (
            <HotTable
                ref={this.tableRef}
                className='o-handsontable-no-border'
                style={{ width: '100%' }}
                language='zh-CN'
                colHeaders={columns}
                data={showData}
                mergeCells={this.getMergeCells()}
                cell={this.getCell()}
                readOnly={true}
                rowHeaders={true}// 数字行号
                fillHandle={false}// 拖动复制单元格
                manualRowResize={true}// 拉伸功能
                manualColumnResize={true}// 拉伸功能
                colWidths={200}
                rowHeights={30}
                columnHeaderHeight={25}
                afterGetRowHeaderRenderers={this.afterGetRowHeaderRenderers}
                contextMenu={['copy']}
                stretchH='all' // 填充空白区域
            />
        )
    }
}
export default SpreadSheet;
