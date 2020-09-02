import React, { Component } from 'react'
import PropTypes from 'prop-types';
import { Input } from 'antd';
import { isEmpty } from 'lodash';

const Search = Input.Search;
export default class MapDrap extends Component {
	static defaultProps = {
		MapCityName: '杭州',
		MapKey: '3d1346e434c73f8501b2af04d0d12a88',
		onDrag: () => { },
		lat: undefined,
		lng: undefined,
		onSearch: () => { },
		width: 200,
		mapWidth: '100%',
		mapHeight: 200,
	}
	static propTypes = {
		MapCityName: PropTypes.string,/* 默认城市名称 */
		MapKey: PropTypes.string,/* 百度地图api */
		onDrag: PropTypes.func,/* 拖拽地图触发的函数 */
		lat: PropTypes.string,/* 经度 */
		lng: PropTypes.string,/* 纬度 */
		onSearch: PropTypes.func,/* 搜索选中结果的函数 */
		width: PropTypes.number,/* 搜索框的宽度 */
		mapWidth: PropTypes.oneOfType([
			PropTypes.string,
			PropTypes.number,
		]),/* 地图组件的宽度 */
		mapHeight: PropTypes.oneOfType([
			PropTypes.string,
			PropTypes.number,
		]),/* 地图组件的高度 */
	}
	constructor(props) {
		super(props);
		this.state = {
			AMapUI: null,
			AMap: null,
		};
	}
	componentDidMount() {
		const { MapKey } = this.props;
		// 已载入高德地图API，则直接初始化地图
		if (window.AMap && window.AMapUI) {
			this.initMap()
			// 未载入高德地图API，则先载入API再初始化
		} else {
			this.createScript(`http://webapi.amap.com/maps?v=1.3&key=${MapKey}`).then(() => {

				this.createScript('http://webapi.amap.com/ui/1.0/main.js').then(() => {

					this.initMap()
				})
			})
		}
	}
	componentWillReceiveProps(nextProps) {
	}
	shouldComponentUpdate(nextProps, nextState) {
		return this.props != nextProps || this.state != nextState;
	}
	/**
	 * init地图组件
	 */
	initMap = () => {
		// 加载PositionPicker，loadUI的路径参数为模块名中 'ui/' 之后的部分
		const { MapCityName, lat, lng } = this.props;
		let AMapUI = this.state.AMapUI = window.AMapUI
		let AMap = this.state.AMap = window.AMap
		AMapUI.loadUI(['misc/PositionPicker'], PositionPicker => {
			let mapConfig = {
				zoom: 16,
				cityName: MapCityName
			}
			if (lat && lng) {
				mapConfig.center = [lng, lat]
			}
			let map = new AMap.Map('js-container', mapConfig)
			// 加载地图搜索插件
			AMap.plugin(['AMap.Autocomplete', 'AMap.PlaceSearch'], function () {
				var autoOptions = {
					city: MapCityName,
					input: "inputSearch"
				}
				var autocomplete = new AMap.Autocomplete(autoOptions)

				var placeSearch = new AMap.PlaceSearch({
					city: MapCityName,
					map: map
				})
				AMap.event.addListener(autocomplete, 'select', function (e) {
					//TODO 针对选中的poi实现自己的功能
					placeSearch.search(e.poi.name)
					this.props.onSearch(e.poi);
				})
			})
			// 启用工具条
			AMap.plugin(['AMap.ToolBar'], function () {
				map.addControl(new AMap.ToolBar({
					position: 'RB'
				}))
			})
			// 创建地图拖拽
			let positionPicker = new PositionPicker({
				mode: 'dragMap', // 设定为拖拽地图模式，可选'dragMap'、'dragMarker'，默认为'dragMap'
				map: map // 依赖地图对象
			})
			// 拖拽完成发送自定义 drag 事件
			positionPicker.on('success', positionResult => {
				// 过滤掉初始化地图后的第一次默认拖放
				this.props.onDrag(positionResult);
			})
			// 启动拖放
			positionPicker.start()
		})
	}
	/**
     * 创建script
     * @param url
     * @returns {Promise}
     */
	createScript = (url, hasCallback) => {
		var scriptElement = document.createElement('script')
		document.body.appendChild(scriptElement)
		var promise = new Promise((resolve, reject) => {
			scriptElement.addEventListener('load', e => {
				removeScript(scriptElement)
				if (!hasCallback) {
					resolve(e)
				}
			}, false)

			scriptElement.addEventListener('error', e => {
				removeScript(scriptElement)
				reject(e)
			}, false)

			if (hasCallback) {
				window.____callback____ = function () {
					resolve()
					window.____callback____ = null
				}
			}
		})

		if (hasCallback) {
			url += '&callback=____callback____'
		}

		scriptElement.src = url
		/**
		 * 移除script标签
		 * @param scriptElement script dom
		 */
		function removeScript(scriptElement) {
			document.body.removeChild(scriptElement)
		}
		return promise
	}
	render() {
		const { width, mapHeight, mapWidth } = this.props;
		return (
			<div style={{ position: 'relative', height: mapHeight, width: mapWidth }}>
				<Search
					id="inputSearch"
					placeholder="请输入查询关键字"
					onSearch={value => console.log(value)}
					style={{ position: 'absolute', top: 10, left: 10, width: width, zIndex: 10 }}
				/>
				<div id="js-container" style={{ height: '100%' }}></div>
			</div>
		)
	}
}
