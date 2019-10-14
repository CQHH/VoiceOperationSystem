package com.cq.hhxk.voice.voiceoperationsystem.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.base.BaseAty;
import com.cq.hhxk.voice.voiceoperationsystem.constant.VoiceServiceConstant;
import com.cq.hhxk.voice.voiceoperationsystem.event.VoiceServiceEvent;
import com.cq.hhxk.voice.voiceoperationsystem.http.HttpInterface;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.TrafficSituationPojo;
import com.cq.hhxk.voice.voiceoperationsystem.view.PieChartView;
import com.em.baseframe.util.AppJsonUtil;
import com.em.baseframe.util.RetrofitUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.isWaring;
import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.startMapActivity;

/**
 * @title  管理者场景二级页面（交通态势，预警详情）
 * @date   2019/07/31
 * @author enmaoFu
 */
public class MapActivity extends BaseAty implements VoiceServiceConstant {

    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.map_switch)
    TextView mapSwitch;
    @BindView(R.id.pie_one)
    PieChart pieOne;
    @BindView(R.id.pie_two)
    PieChart pieTwo;
    @BindView(R.id.pie_three)
    PieChart pieThree;
    @BindView(R.id.chart_re)
    RelativeLayout chartRe;

    /**
     * 地图对象
     */
    private AMap aMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map;
    }

    @Override
    protected void initData() {

        isWaring = 0;
        startMapActivity = 1;

        initMap();
        initMarker();
        initCircle();

        showWaringMarker();

        setPieOne();
        setPieTwo();
        setPieThree();
    }


    @Override
    protected void requestData() {

    }

    public void initMap(){
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        MarkerOptions markerOption = new MarkerOptions();
        LatLng latLng = new LatLng(29.718435,106.542036);
        markerOption.position(latLng);
        markerOption.draggable(false);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.dot)));
        aMap.addMarker(markerOption);
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(29.718435,106.542036),14,0,0));
        aMap.moveCamera(mCameraUpdate);
        //设置默认缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        //实时路况
        aMap.setTrafficEnabled(true);
        CustomMapStyleOptions customMapStyleOptions = new CustomMapStyleOptions();
        customMapStyleOptions.setStyleDataPath(Environment.getExternalStorageDirectory() + "/data/mapStyle/style.data");
        aMap.setCustomMapStyle(customMapStyleOptions);
        aMap.setMapCustomEnable(true);
        aMap.setTrafficEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
    }

    /**
     * 绘制点标记
     */
    public void initMarker(){
        //沙井湾立交
        MarkerOptions markerOption1 = new MarkerOptions();
        LatLng latLng1 = new LatLng(29.707117,106.553869);
        markerOption1.position(latLng1);
        markerOption1.draggable(false);
        markerOption1.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.marker)));

        //赵家溪立交
        MarkerOptions markerOption2 = new MarkerOptions();
        LatLng latLng2 = new LatLng(29.693202,106.547515);
        markerOption2.position(latLng2);
        markerOption2.draggable(false);
        markerOption2.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.marker)));

        //悦来立交
        MarkerOptions markerOption3 = new MarkerOptions();
        LatLng latLng3 = new LatLng(29.704054,106.52964);
        markerOption3.position(latLng3);
        markerOption3.draggable(false);
        markerOption3.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.marker)));

        aMap.addMarker(markerOption1);
        aMap.addMarker(markerOption2);
        aMap.addMarker(markerOption3);
    }


    public void showWaringMarkerInfo(){
        //初始化marker内容
        MarkerOptions markerOptions = new MarkerOptions();
        TextView textView = new TextView(getApplicationContext());
        textView.setText("设备：红绿灯\n故障原因：红灯不亮\n责任人：张三\n联系电话：12159654632");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(Color.parseColor("#49FDEA"));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(0,30,0,12);
        textView.setBackgroundResource(R.drawable.waring_bg_1);
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(textView);
        markerOptions.position(new LatLng(29.708339,106.533619)).icon(markerIcon).draggable(false);
        //添加到地图上
        aMap.addMarker(markerOptions);
    }

    /**
     * 绘制异常标记
     */
    public void showWaringMarker(){
        MarkerOptions markerOptionWaring = new MarkerOptions();
        LatLng latLng1 = new LatLng(29.708339,106.533619);
        markerOptionWaring.position(latLng1);
        markerOptionWaring.draggable(false);
        markerOptionWaring.visible(true);
        markerOptionWaring.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.waring)));
        aMap.addMarker(markerOptionWaring);
    }

    /**
     * 绘制圆
     */
    public void initCircle(){
        //沙井湾立交
        LatLng latLng1 = new LatLng(29.707117,106.553869);
        //赵家溪立交
        LatLng latLng2 = new LatLng(29.693202,106.547515);
        //悦来立交
        LatLng latLng3 = new LatLng(29.704054,106.52964);

        CircleOptions circleOptions1 = new CircleOptions();
        circleOptions1.center(latLng1);
        circleOptions1.radius(600);
        circleOptions1.fillColor(Color.parseColor("#50D81E06"));
        circleOptions1.strokeColor(Color.parseColor("#D81E06"));
        circleOptions1.strokeWidth(1);

        CircleOptions circleOptions2 = new CircleOptions();
        circleOptions2.center(latLng2);
        circleOptions2.radius(600);
        circleOptions2.fillColor(Color.parseColor("#50D81E06"));
        circleOptions2.strokeColor(Color.parseColor("#D81E06"));
        circleOptions2.strokeWidth(1);

        CircleOptions circleOptions3 = new CircleOptions();
        circleOptions3.center(latLng3);
        circleOptions3.radius(600);
        circleOptions3.fillColor(Color.parseColor("#50D81E06"));
        circleOptions3.strokeColor(Color.parseColor("#D81E06"));
        circleOptions3.strokeWidth(1);

        aMap.addCircle(circleOptions1);
        aMap.addCircle(circleOptions2);
        aMap.addCircle(circleOptions3);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(VoiceServiceEvent voiceServiceEvent){
        switch (voiceServiceEvent.getIdStr()){
            case SHOW_WARING_DETAILS:
                showWaringMarkerInfo();
                break;
        }
    }

    public void setPieOne(){
        List<PieEntry> pieEntryList = new ArrayList<PieEntry>();
        List<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#0ADDA6"));
        colors.add(Color.parseColor("#03BCBF"));
        colors.add(Color.parseColor("#0C5CA3"));
        colors.add(Color.parseColor("#4D9DFC"));
        //饼图实体 PieEntry
        PieEntry unimpeded = new PieEntry(40, "畅通率");
        PieEntry slow = new PieEntry(20, "缓行率");
        PieEntry congestion = new PieEntry(15, "拥堵率");
        PieEntry unknown = new PieEntry(15, "未知率");
        pieEntryList.add(unimpeded);
        pieEntryList.add(slow);
        pieEntryList.add(congestion);
        pieEntryList.add(unknown);

        PieChartView pieChartView1 = new PieChartView();
        pieChartView1.init(pieOne,true,true,true,false,pieEntryList,colors,"沙井湾立交");
    }

    public void setPieTwo(){
        List<PieEntry> pieEntryList = new ArrayList<PieEntry>();
        List<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#0ADDA6"));
        colors.add(Color.parseColor("#03BCBF"));
        colors.add(Color.parseColor("#0C5CA3"));
        colors.add(Color.parseColor("#4D9DFC"));
        //饼图实体 PieEntry
        PieEntry unimpeded = new PieEntry(55, "畅通率");
        PieEntry slow = new PieEntry(15, "缓行率");
        PieEntry congestion = new PieEntry(10, "拥堵率");
        PieEntry unknown = new PieEntry(20, "未知率");
        pieEntryList.add(unimpeded);
        pieEntryList.add(slow);
        pieEntryList.add(congestion);
        pieEntryList.add(unknown);

        PieChartView pieChartView1 = new PieChartView();
        pieChartView1.init(pieTwo,true,true,true,false,pieEntryList,colors,"赵家溪立交");
    }

    public void setPieThree(){
        List<PieEntry> pieEntryList = new ArrayList<PieEntry>();
        List<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#0ADDA6"));
        colors.add(Color.parseColor("#03BCBF"));
        colors.add(Color.parseColor("#0C5CA3"));
        colors.add(Color.parseColor("#4D9DFC"));
        //饼图实体 PieEntry
        PieEntry unimpeded = new PieEntry(50, "畅通率");
        PieEntry slow = new PieEntry(15, "缓行率");
        PieEntry congestion = new PieEntry(30, "拥堵率");
        PieEntry unknown = new PieEntry(15, "未知率");
        pieEntryList.add(unimpeded);
        pieEntryList.add(slow);
        pieEntryList.add(congestion);
        pieEntryList.add(unknown);

        PieChartView pieChartView1 = new PieChartView();
        pieChartView1.init(pieThree,true,true,true,false,pieEntryList,colors,"悦来立交");
    }

    @OnClick({R.id.back_re,R.id.location_re,R.id.msg_re})
    @Override
    public void btnClick(View view) {
        switch (view.getId()){
            case R.id.back_re:
                finish();
                break;
            case R.id.location_re:
                CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(29.718435,106.542036),14,0,0));
                aMap.moveCamera(mCameraUpdate);
                break;
            case R.id.msg_re:
                String mapSwitchStr = mapSwitch.getText().toString().trim();
                switch (mapSwitchStr){
                    case "打开数据":
                        //getTrafficSituation();
                        mapSwitch.setText("关闭数据");
                        chartRe.setVisibility(View.VISIBLE);
                        break;
                    case "关闭数据":
                        mapSwitch.setText("打开数据");
                        chartRe.setVisibility(View.GONE);
                        break;
                }
                break;
        }
    }

    /**
     * 关闭当前activity并且回到桌面
     */
    public void close(){
        finish();
        Intent intent = new Intent();
        // 为Intent设置Action、Category属性
        intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startMapActivity = 0;
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

}
