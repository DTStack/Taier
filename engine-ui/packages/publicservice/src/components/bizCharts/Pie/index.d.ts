import * as React from 'react';
export interface IPieProps {
  animate?: boolean;
  color?: string;
  height: number;
  hasLegend?: boolean;
  padding?: [number, number, number, number];
  percent?: number;
  data?: Array<{
    x: string | string;
    y: number;
  }>;
  total?: string | function;
  title?: React.ReactNode;
  tooltip?: boolean;
  valueFormat?: (value: string) => string | React.ReactNode;
  subTitle?: React.ReactNode;
}

export default class Pie extends React.Component<IPieProps, any> {}
