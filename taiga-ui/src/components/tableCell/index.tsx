import React from 'react';

export interface TableCellProps {
  value?: string;
  style?: React.CSSProperties;
  onChange?: any;
}
export default function TableCell(props: TableCellProps) {
  const originStyle: React.CSSProperties = {
    textIndent: '5px',
    backgroundColor: 'transparent',
    backgroundImage: 'none',
    width: '100%',
    border: 'none',
  };

  const { style } = props;
  let newStyle: any = { ...originStyle, ...style };

  return <textarea {...props} style={newStyle} />;
}
