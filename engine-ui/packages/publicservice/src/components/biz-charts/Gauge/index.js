import React from 'react';
import { Chart, Geom, Axis, Coord, Guide, Shape } from 'bizcharts';
import autoHeight from '../autoHeight';

const { Arc, Html, Line } = Guide;

const defaultFormatter = val => {
  switch (val) {
    case '2':
      return '差';
    case '4':
      return '中';
    case '6':
      return '良';
    case '8':
      return '优';
    default:
      return '';
  }
};

Shape.registerShape('point', 'pointer', {
  drawShape(cfg, group) {
    let point = cfg.points[0];
    point = this.parsePoint(point);
    const center = this.parsePoint({
      x: 0,
      y: 0,
    });
    group.addShape('line', {
      attrs: {
        x1: center.x,
        y1: center.y,
        x2: point.x,
        y2: point.y,
        stroke: cfg.color,
        lineWidth: 2,
        lineCap: 'round',
      },
    });
    return group.addShape('circle', {
      attrs: {
        x: center.x,
        y: center.y,
        r: 6,
        stroke: cfg.color,
        lineWidth: 3,
        fill: '#fff',
      },
    });
  },
});

@autoHeight()
export default class Gauge extends React.Component {
  render() {
    const {
      title,
      height,
      percent,
      forceFit = true,
      formatter = defaultFormatter,
      color = '#2F9CFF',
      bgColor = '#F0F2F5',
    } = this.props;
    const cols = {
      value: {
        type: 'linear',
        min: 0,
        max: 10,
        tickCount: 6,
        nice: true,
      },
    };
    const data = [{ value: percent / 10 }];
    return (
      <Chart height={height} data={data} scale={cols} padding={[-16, 0, 16, 0]} forceFit={forceFit}>
        <Coord type="polar" startAngle={-1.25 * Math.PI} endAngle={0.25 * Math.PI} radius={0.8} />
        <Axis name="1" line={null} />
        <Axis
          line={null}
          tickLine={null}
          subTickLine={null}
          name="value"
          zIndex={2}
          gird={null}
          label={{
            offset: -12,
            formatter,
            textStyle: {
              fontSize: 12,
              fill: 'rgba(0, 0, 0, 0.65)',
              textAlign: 'center',
            },
          }}
        />
        <Guide>
          <Line
            start={[3, 0.905]}
            end={[3, 0.85]}
            lineStyle={{
              stroke: color,
              lineDash: null,
              lineWidth: 2,
            }}
          />
          <Line
            start={[5, 0.905]}
            end={[5, 0.85]}
            lineStyle={{
              stroke: color,
              lineDash: null,
              lineWidth: 3,
            }}
          />
          <Line
            start={[7, 0.905]}
            end={[7, 0.85]}
            lineStyle={{
              stroke: color,
              lineDash: null,
              lineWidth: 3,
            }}
          />
          <Arc
            zIndex={0}
            start={[0, 0.965]}
            end={[10, 0.965]}
            style={{
              stroke: bgColor,
              lineWidth: 10,
            }}
          />
          <Arc
            zIndex={1}
            start={[0, 0.965]}
            end={[data[0].value, 0.965]}
            style={{
              stroke: color,
              lineWidth: 10,
            }}
          />
          <Html
            position={['50%', '95%']}
            html={() => {
              return `
                <div style="width: 300px;text-align: center;font-size: 12px!important;">
                  <p style="font-size: 14px; color: rgba(0,0,0,0.43);margin: 0;">${title}</p>
                  <p style="font-size: 24px;color: rgba(0,0,0,0.85);margin: 0;">
                    ${data[0].value * 10}%
                  </p>
                </div>`;
            }}
          />
        </Guide>
        <Geom
          line={false}
          type="point"
          position="value*1"
          shape="pointer"
          color={color}
          active={false}
        />
      </Chart>
    );
  }
}
