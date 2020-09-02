import {lz_theme} from '@/constants/walden'
import echarts from 'echarts/lib/echarts'; 
import React from 'react'
import 'echarts/lib/component/grid';
import 'echarts/lib/component/markLine'
import 'echarts/lib/component/dataZoom'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/title'
import 'echarts/lib/component/legend'
import 'zrender/lib/svg/svg';

echarts.registerTheme('lz_theme',lz_theme);
console.log(echarts,'echarts',lz_theme)
export default echarts;
