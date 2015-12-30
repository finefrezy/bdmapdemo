package com.gaobo.gem;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnGeoFenceListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;

public class TraceActivity extends Activity {

	private Trace mTrace;
	LBSTraceClient mClient;
	private Button mTraceStartBtn;
	private Button mTraceStopBtn;
	private Button mQueryTrack;
	private Button mFenceInit;
	private Button mFenceCreate;
	private Button mQueryHistoryTrack;
	private TextView mTextView;

	StringBuilder mStringBuilder;

	// // 鹰眼服务ID
	long mServiceId = 107302;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace);

		mStringBuilder = new StringBuilder();

		mTraceStartBtn = (Button) findViewById(R.id.trace_start);
		mTraceStopBtn = (Button) findViewById(R.id.trace_stop);
		mQueryTrack = (Button) findViewById(R.id.query_track);
		mFenceInit = (Button) findViewById(R.id.fence_init);
		mFenceCreate = (Button) findViewById(R.id.fence_create);
		mQueryHistoryTrack = (Button) findViewById(R.id.query_history_track);
		mTextView = (TextView) findViewById(R.id.msg_content);

		mTraceStartBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startTrace();
			}
		});
		mTraceStopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null == mClient) {
					Toast.makeText(getApplicationContext(),
							"please init first", Toast.LENGTH_LONG).show();
					return;
				}

				stopTrace();
			}
		});
		mQueryTrack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				queryTrack();
			}
		});
		mQueryHistoryTrack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				queryHistoryTrack();
			}
		});
		mFenceInit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFence();
			}
		});
		mFenceCreate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createCircleFence();
			}
		});

	}

	public void startTrace() {
		// 实例化轨迹服务客户端
		mClient = new LBSTraceClient(getApplicationContext());

		// entity标识
		String entityName = "mycar";
		// 轨迹服务类型（0 : 不上传位置数据，也不接收报警信息； 1 : 不上传位置数据，但接收报警信息；2 : 上传位置数据，且接收报警信息）
		// 若需实现轨迹采集，traceType必须设置为2，否则将无法实现轨迹采集。
		int traceType = 2;
		// 实例化轨迹服务
		mTrace = new Trace(getApplicationContext(), mServiceId, entityName,
				traceType);

		// 位置采集周期 单位：s
		int gatherInterval = 10;
		// 打包周期,打包上传服务器
		int packInterval = 60;
		// 设置位置采集和打包周期
		mClient.setInterval(gatherInterval, packInterval);

		// 实例化开启轨迹服务回调接口
		OnStartTraceListener startTraceListener = new OnStartTraceListener() {
			// 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onTraceCallback(int msgCode, String msgContent) {
				setText("StartTrace->" + "callback->" + msgCode + " : "
						+ msgContent);
			}

			// 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onTracePushCallback(byte msgType, String msg) {
				setText("StartTrace->" + "push->" + msgType + " : " + msg);
			}
		};

		// 开启轨迹服务
		mClient.startTrace(mTrace, startTraceListener);
	}

	public void stopTrace() {
		// 实例化停止轨迹服务回调接口
		OnStopTraceListener stopTraceListener = new OnStopTraceListener() {
			// 轨迹服务停止成功
			@Override
			public void onStopTraceSuccess() {
				setText("StopTrace-> success");

			}

			// 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onStopTraceFailed(int code, String msg) {
				setText("StopTrace-> failed->" + code + "," + msg);
			}
		};

		// 停止轨迹服务
		mClient.stopTrace(mTrace, stopTraceListener);
	}

	public void queryTrack() {

		// entity标识列表（多个entityName，以英文逗号"," 分割）
		String entityNames = "mycar";
		// 检索条件（格式为 : "key1=value1,key2=value2,....."）
		String columnKey = "car_team=1";
		// 返回结果的类型（0 : 返回全部结果，1 : 只返回entityName的列表）
		int returnType = 0;
		// 活跃时间，UNIX时间戳（指定该字段时，返回从该时间点之后仍有位置变动的entity的实时点集合）
		int activeTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
		// 分页大小
		int pageSize = 50;
		// 分页索引
		int pageIndex = 1;
		// Entity监听器
		OnEntityListener entityListener = new OnEntityListener() {
			// 查询失败回调接口
			@Override
			public void onRequestFailedCallback(String failure) {
				setText("Entity-> failure->" + failure);
			}

			// 查询entity回调接口，返回查询结果列表
			@Override
			public void onQueryEntityListCallback(String msg) {
				setText("Entity-> query->" + msg);
			}
		};

		// 查询实时轨迹
		mClient.queryEntityList(mServiceId, entityNames, columnKey, returnType,
				activeTime, pageSize, pageIndex, entityListener);
	}

	public void queryHistoryTrack() {
		// entity标识
		String entityName = "mycar";
		// 是否返回精简的结果（0 : 将只返回经纬度，1 : 将返回经纬度及其他属性信息）
		int simpleReturn = 0;
		// 开始时间（Unix时间戳）
		int startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
		// 结束时间（Unix时间戳）
		int endTime = (int) (System.currentTimeMillis() / 1000);
		// 分页大小
		int pageSize = 50;
		// 分页索引
		int pageIndex = 1;
		// 轨迹查询监听器
		OnTrackListener trackListener = new OnTrackListener() {
			// 请求失败回调接口
			@Override
			public void onRequestFailedCallback(String msg) {
				setText("TrackListener->failure->" + msg);
			}

			/**
			 * onTrackAttrCallback()接口，在回传轨迹点时回传属性数据。
			 * 注：SDK根据位置采集周期回调该接口，获取轨迹属性数据。 如汽车的油量、发动机转速等
			 */

			@Override
			public Map onTrackAttrCallback() {
				return super.onTrackAttrCallback();
			}

			// 查询历史轨迹回调接口
			@Override
			public void onQueryHistoryTrackCallback(String msg) {
				setText("TrackListener->history->" + msg);
			}

		};

		// 查询历史轨迹
		mClient.queryHistoryTrack(mServiceId, entityName, simpleReturn,
				startTime, endTime, pageSize, pageIndex, trackListener);
	}

	OnGeoFenceListener geoFenceListener = null;
	public void initFence() {
		// 实例化地理围栏监听器
		OnGeoFenceListener geoFenceListener = new OnGeoFenceListener() {
			// 请求失败回调接口
			@Override
			public void onRequestFailedCallback(String arg0) {
				setText("geoFence请求失败 : " + arg0);
			}

			// 创建圆形围栏回调接口
			@Override
			public void onCreateCircularFenceCallback(String arg0) {
				setText("创建圆形围栏回调接口消息 : " + arg0);
			}

			// 更新圆形围栏回调接口
			@Override
			public void onUpdateCircularFenceCallback(String arg0) {
				setText("更新圆形围栏回调接口消息 : " + arg0);
			}

			// 延迟报警回调接口
			@Override
			public void onDelayAlarmCallback(String arg0) {
				setText("延迟报警回调接口消息 : " + arg0);
			}

			// 删除围栏回调接口
			@Override
			public void onDeleteFenceCallback(String arg0) {
				setText(" 删除围栏回调接口消息 : " + arg0);
			}

			// 查询围栏列表回调接口
			@Override
			public void onQueryFenceListCallback(String arg0) {
				setText("查询围栏列表回调接口消息 : " + arg0);
			}

			// 查询历史报警回调接口
			@Override
			public void onQueryHistoryAlarmCallback(String arg0) {
				setText(" 查询历史报警回调接口消息 : " + arg0);
			}

			// 查询监控对象状态回调接口
			@Override
			public void onQueryMonitoredStatusCallback(String arg0) {
				setText(" 查询监控对象状态回调接口消息 : " + arg0);
			}

		};
	}

	public void createCircleFence() {
		// 创建者（entity标识）
		String creator = "mom";
		// 围栏名称
		String fenceName = "school";
		// 围栏描述
		String fenceDesc = "学校";
		// 监控对象列表（多个entityName，以英文逗号"," 分割）
		String monitoredPersons = "daughter";
		// 观察者列表（多个entityName，以英文逗号"," 分割）
		String observers = "mom,dad";
		// 生效时间列表
		String validTimes = "0800,1630";
		// 生效周期
		int validCycle = 5;
		// 围栏生效日期
		String validDate = "";
		// 生效日期列表
		String validDays = "";
		// 坐标类型 （1：GPS经纬度，2：国测局经纬度，3：百度经纬度）
		int coordType = 3;
		// 围栏圆心（圆心位置, 格式 : "经度,纬度"）
		String center = "116.838463,40.263548";
		// 围栏半径（单位 : 米）
		double radius = 500;
		// 报警条件（1：进入时触发提醒，2：离开时触发提醒，3：进入离开均触发提醒）
		int alarmCondition = 3;
		// 创建圆形地理围栏
		mClient.createCircularFence(mServiceId, creator, fenceName, fenceDesc,
				monitoredPersons, observers, validTimes, validCycle, validDate,
				validDays, coordType, center, radius, alarmCondition,
				geoFenceListener);

	}

	/**
	 * 查询实时状态
	 * 
	 * @param content
	 */
	public void queryStatus() {
		// 围栏ID
		int fenceId = 10;// <要查询的围栏ID>;
		// 监控对象列表（多个entityName，以英文逗号"," 分割）
		String monitoredPersons = "daughter,son";
		// 查询实时状态
		// client.queryMonitoredStatus(serviceId, fenceId, monitoredPersons,
		// geoFenceListener);
	}

	/**
	 * 查询历史报警信息 若需接收报警信息，traceType必须为1或2。
	 * 
	 * 报警信息在OnStartTraceListener监听器的OnTracePushCallBack()接口中获取
	 * 
	 * @param content
	 */
	public void queryHistoryWaring() {
		// 围栏ID
		// int fenceId = <要查询的围栏ID>;
		// //监控对象列表（多个entityName，以英文逗号"," 分割）
		// String monitoredPersons = "daughter,son";
		// //开始时间（unix时间戳）
		// int beginTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 *
		// 60);
		// //结束时间（unix时间戳）
		// int endTime = (int) (System.currentTimeMillis() / 1000);
		//
		// //查询历史报警信息
		// client.queryFenceHistoryAlarmInfo(serviceId, fenceId,
		// monitoredPersons, beginTime, endTime,
		// geoFenceListener);
	}

	/**
	 * 延时报警信息
	 */
	public void delayWaring() {
		// //围栏ID
		// int fenceId = <要延迟的围栏ID>;
		// //监控者列表（多个entityName，以英文逗号"," 分割）
		// String observer = "mom";
		// //延迟时间（unix时间戳）
		// int delayTime = (int) (System.currentTimeMillis() / 1000 + 60 * 60);
		//
		// //延迟报警
		// client.delayFenceAlarm(serviceId, fenceId, observer, delayTime,
		// geoFenceListener);
	}

	public void setText(String content) {
		mStringBuilder.append(content);
		mStringBuilder.append("\n");
		mTextView.setText(mStringBuilder.toString());
	}
}
