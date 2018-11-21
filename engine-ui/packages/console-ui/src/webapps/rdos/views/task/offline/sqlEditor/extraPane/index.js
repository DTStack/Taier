import React from "react";

class TableTipExtraPane extends React.Component {
    renderTableItem(tableName,columns){
        return <section>
            <div>{tableName}</div>
            {columns.map((column)=>{
                return <div>{column.columnName}/{column.columnType}</div>
            })}
        </section>
    }
    renderTables(){
        const {data} = this.props;
        const tableAndColumns=Object.entries(data);

        return <div>
            {tableAndColumns.map(([table,columns])=>{
                return this.renderTableItem(table,columns)
            })}
        </div>
    }
    render() {
        
        return (
           <div>
               {this.renderTables()}
            </div>
        )
    }
}
export default TableTipExtraPane;