package com.gaobo.gem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Gradient;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends Activity {

	MapView mMapView = null;
	BaiduMap mBaiduMap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main_my);
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		
		
		// 改变地图默认中心位置
		LatLng cenpt =  new LatLng(31.275094,120.743712);  
		//定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder()
		.target(cenpt)
		.zoom(12)
		.build();
		//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		//改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate); 
		
		
		
		// 普通地图
		// mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// 卫星地图
		// mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		// 开启交通图
		// mBaiduMap.setTrafficEnabled(true);

		// 开启热力图 百度地图热力图 是用不同颜色的区块叠加在地图上实时描述人群分布、密度和变化趋势的一个产品，是基于百度大数据的一个便民出行服务
		// mBaiduMap.setBaiduHeatMapEnabled(true);

		//定义Maker坐标点  
		LatLng point = new LatLng(39.963175, 116.400244);  
		//构建Marker图标  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
		    .fromResource(R.drawable.location_icon);  
		//构建MarkerOption，用于在地图上添加Marker  
		OverlayOptions option = new MarkerOptions()  
		    .position(point)  
		    .icon(bitmap);  
		//在地图上添加Marker，并显示  
		Marker marker = (Marker) mBaiduMap.addOverlay(option);
		marker.remove();// 调用Marker对象的remove方法实现指定marker的删除
		
		
		
		
		
		OverlayOptions options = new MarkerOptions()
	    .position(point)  //设置marker的位置
	    .icon(bitmap)  //设置marker图标
	    .zIndex(9)  //设置marker所在层级
	    .draggable(true);  //设置手势拖拽
		//将marker添加到地图上
		Marker marker1 = (Marker) (mBaiduMap.addOverlay(options));
		
		//调用BaiduMap对象的setOnMarkerDragListener方法设置marker拖拽的监听
		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
		    public void onMarkerDrag(Marker marker) {
		        //拖拽中
		    }
		    public void onMarkerDragEnd(Marker marker) {
		        //拖拽结束
		    	Log.d("bdmapdemo", marker.getPosition().latitude + "");
		    	Log.d("bdmapdemo", marker.getPosition().longitude + "");
		    	
		    }
		    public void onMarkerDragStart(Marker marker) {
		        //开始拖拽
		    }
		});
		marker1.remove();
		
		
		
		BitmapDescriptor bitmap2 = BitmapDescriptorFactory  
			    .fromResource(R.drawable.location_icon2);
		BitmapDescriptor bitmap3 = BitmapDescriptorFactory  
				.fromResource(R.drawable.location_icon3);
		BitmapDescriptor bitmap4 = BitmapDescriptorFactory  
				.fromResource(R.drawable.location_icon4);
		// 通过marker的icons设置一组图片，再通过period设置多少帧刷新一次图片资源
		ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
		giflist.add(bitmap);
		giflist.add(bitmap2);
		giflist.add(bitmap3);
		giflist.add(bitmap4);
		OverlayOptions ooD = new MarkerOptions().position(point).icons(giflist)
						.zIndex(0).period(10);	
		Marker mMarkerD = (Marker) (mBaiduMap.addOverlay(ooD));
		mMarkerD.remove();
		
		
		//弹出覆盖物 定义文字所显示的坐标点  
		LatLng llText = new LatLng(39.86923, 116.397428);  
		//构建文字Option对象，用于在地图上添加文字  
		OverlayOptions textOption = new TextOptions()  
		    .bgColor(0xAAFFFF00)  
		    .fontSize(24)
		    .fontColor(0xFFFF00FF)  
		    .text("百度地图SDK")  
		    .rotate(-30)  
		    .position(llText);  
		//在地图上添加该文字对象并显示  
		mBaiduMap.addOverlay(textOption);
		
		
		
		//点击Marker弹出InfoWindow的示例图，开发者只需将InfoWindow的显示方法写在Maker的点击事件处理中即可实现该效果
		//弹出覆盖物 创建InfoWindow展示的view  
		Button button = new Button(getApplicationContext());  
		button.setBackgroundResource(R.drawable.location_icon4);  
		//定义用于显示该InfoWindow的坐标点  
		LatLng pt = new LatLng(39.86923, 116.397428);  
		//创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量 
		InfoWindow mInfoWindow = new InfoWindow(button, pt, -47);  
		//显示InfoWindow  
		mBaiduMap.showInfoWindow(mInfoWindow);
		
		
		
		//设置渐变颜色值
		int[] DEFAULT_GRADIENT_COLORS = {Color.rgb(102, 225,  0), Color.rgb(255, 0, 0) };
		//设置渐变颜色起始值
		float[] DEFAULT_GRADIENT_START_POINTS = { 0.2f, 1f };
		//构造颜色渐变对象
		Gradient gradient = new Gradient(DEFAULT_GRADIENT_COLORS, DEFAULT_GRADIENT_START_POINTS);
		//以下数据为随机生成地理位置点，开发者根据自己的实际业务，传入自有位置数据即可
		List<LatLng> randomList = new ArrayList<LatLng>();
		Random r = new Random();
		for (int i = 0; i < 500; i++) {
		    // 116.220000,39.780000 116.570000,40.150000
		    int rlat = r.nextInt(370000);
		    int rlng = r.nextInt(370000);
		    int lat = 39780000 + rlat;
		    int lng = 116220000 + rlng;
		    LatLng ll = new LatLng(lat / 1E6, lng / 1E6);
		    randomList.add(ll);
		}
		//在大量热力图数据情况下，build过程相对较慢，建议放在新建线程实现
		HeatMap heatmap = new HeatMap.Builder()
		    .data(randomList)
		    .gradient(gradient)
		    .build();
		//在地图上添加热力图
		mBaiduMap.addHeatMap(heatmap);
//		heatmap.removeHeatMap();
		
		
		
		
		
		
		
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

}
