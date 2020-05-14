---
order: 0
title: 图表套件组合展示
---

利用 Ant Design Pro 提供的图表套件，可以灵活组合符合设计规范的图表来满足复杂的业务需求。

````jsx
import { ChartCard, Field, MiniArea, MiniBar, MiniProgress } from 'ant-design-pro/lib/Charts';
import Trend from 'ant-design-pro/lib/Trend';
import NumberInfo from 'ant-design-pro/lib/NumberInfo';
import { Row, Col, Icon, Tooltip } from 'antd';
import numeral from 'numeral';
import moment from 'moment';

const visitData = [];
const beginDay = new Date().getTime();
for (let i = 0; i < 20; i += 1) {
  visitData.push({
    x: moment(new Date(beginDay + (1000 * 60 * 60 * 24 * i))).format('YYYY-MM-DD'),
    y: Math.floor(Math.random() * 100) + 10,
  });
}

ReactDOM.render(
  <Row>
    <Col span={24}>
      <ChartCard
        title="搜索用户数量"
        total={numeral(8846).format('0,0')}
        contentHeight={134}
      >
        <NumberInfo
          subTitle={<span>本周访问</span>}
          total={numeral(12321).format('0,0')}
          status="up"
          subTotal={17.1}
        />
        <MiniArea
          line
          height={45}
          data={visitData}
        />
      </ChartCard>
    </Col>
    <Col span={24} style={{ marginTop: 24 }}>
      <ChartCard
        title="访问量"
        action={<Tooltip title="指标说明"><Icon type="info-circle-o" /></Tooltip>}
        total={numeral(8846).format('0,0')}
        footer={<Field label="日访问量" value={numeral(1234).format('0,0')} />}
        contentHeight={46}
      >
        <MiniBar
          height={46}
          data={visitData}
        />
      </ChartCard>
    </Col>
    <Col span={24} style={{ marginTop: 24 }}>
      <ChartCard
        title="线上购物转化率"
        action={<Tooltip title="指标说明"><Icon type="info-circle-o" /></Tooltip>}
        total="78%"
        footer={
          <div>
            <span>
              周同比
              <Trend flag="up" style={{ marginLeft: 8, color: 'rgba(0,0,0,.85)' }}>12%</Trend>
            </span>
            <span style={{ marginLeft: 16 }}>
              日环比
              <Trend flag="down" style={{ marginLeft: 8, color: 'rgba(0,0,0,.85)' }}>11%</Trend>
            </span>
          </div>
        }
        contentHeight={46}
      >
        <MiniProgress percent={78} strokeWidth={8} target={80} />
      </ChartCard>
    </Col>
  </Row>
, mountNode);
````
