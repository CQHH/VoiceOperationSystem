package com.cq.hhxk.voice.voiceoperationsystem.ui;


import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.base.BaseAty;
import com.cq.hhxk.voice.voiceoperationsystem.view.channel.Channel;
import com.cq.hhxk.voice.voiceoperationsystem.view.channel.ChannelView;
import com.em.baseframe.util.AppManger;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.startConfigure;

public class ConfigureActivity extends BaseAty implements View.OnTouchListener,ChannelView.OnChannelListener{

    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.lin1)
    LinearLayout lin1;
    @BindView(R.id.lin2)
    LinearLayout lin2;
    @BindView(R.id.lin3)
    LinearLayout lin3;
    @BindView(R.id.lin4)
    LinearLayout lin4;
    @BindView(R.id.lin5)
    LinearLayout lin5;
    @BindView(R.id.controller_traffic_situation_pie)
    PieChart controllerTrafficSituationPie;
    @BindView(R.id.channelView)
    ChannelView channelView;

    /**
     * 地图对象
     */
    private AMap aMap;

    /**
     * 路灯经度
     */
    private double[] lampLongitude = {29.721281,29.713566,29.711106,29.709876,29.720275,29.722138,29.719976,29.726126,29.705216,29.717703};

    /**
     * 路灯纬度
     */
    private double[] lampLatitude = {106.542136,106.536128,106.54529,106.54484,106.555654,106.546899,106.553809,106.540891,106.537201,106.555826};

    /**
     * 红绿灯经度
     */
    private double[] lightsLongitude = {29.72907,29.715206,29.706633,29.703949,29.730076,29.729089,29.72074,29.698823,29.727523,29.710006};

    /**
     * 红绿灯纬度
     */
    private double[] lightsLatitude = {106.540162,106.533639,106.553723,106.529261,106.558744,106.547264,106.539711,106.545004,106.558222,106.527494};

    /**
     * 垃圾箱经度
     */
    private double[] dustbinLongitude = {29.710068,29.711596,29.709443,29.711503,29.712034,29.712277,29.715529,29.71743,29.716615,29.713305};

    /**
     * 垃圾箱纬度
     */
    private double[] dustbinLatitude = {106.544419,106.546093,106.544859,106.545385,106.547455,106.540664,106.548367,106.541415,106.545546,106.55044};

    /**
     * 井盖经度
     */
    private double[] coverLongitude = {29.71992,29.716417,29.716342,29.711143,29.720778,29.720405,29.71556,29.711031,29.719361,29.716063};

    /**
     * 井盖纬度
     */
    private double[] coverLatitude = {106.549217,106.550075,106.548359,106.551148,106.553101,106.548123,106.55368,106.541063,106.55501,106.552951};

    private int sx;
    private int sy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_configure;
    }

    @Override
    protected void initData() {
        AppManger.getInstance().killActivity(ComprehensiveControllerActivity.class);
        AppManger.getInstance().killActivity(ComprehensivePersonnelActivity.class);
        AppManger.getInstance().killActivity(NavigationAvtivity.class);
        AppManger.getInstance().killActivity(MapActivity.class);
        AppManger.getInstance().killActivity(ResidentActivity.class);
        AppManger.getInstance().killActivity(ExhibitionActivity.class);
        AppManger.getInstance().killActivity(SpongeActivity.class);

        startConfigure = 1;

        initPieChart();
        lin1.setOnTouchListener(this);
        lin2.setOnTouchListener(this);
        lin3.setOnTouchListener(this);
        lin4.setOnTouchListener(this);
        lin5.setOnTouchListener(this);
        initMap();
        init();
    }

    @Override
    protected void requestData() {

    }

    private void init() {
        String[] myChannel = {"警告","悦来新城2019"};
        String[] recommendChannel1 = {"交通态势监控信息","设备信息","会展监控信息","设备点标记"};
        String[] recommendChannel2 = {"男声播报", "女声播报", "自动检测更新", "启动安全监控", "关闭语音唤醒", "启动数据统计", "启动异常监控", "启动语义加速"};
        String[] recommendChannel3 = {"配置A", "配置B", "配置C", "配置D", "配置E", "配置F", "配置G",
                "配置H", "配置I"};

        List<Channel> myChannelList = new ArrayList<>();
        List<Channel> recommendChannelList1 = new ArrayList<>();
        List<Channel> recommendChannelList2 = new ArrayList<>();
        List<Channel> recommendChannelList3 = new ArrayList<>();

        for (int i = 0; i < myChannel.length; i++) {
            String aMyChannel = myChannel[i];
            Channel channel;
            if (i > 2 && i < 6) {
                //可设置频道归属板块（channelBelong），当前设置此频道归属于第二板块，当删除该频道时该频道将回到第二板块
                channel = new Channel(aMyChannel, 2, i);
            } else if (i > 7 && i < 10) {
                //可设置频道归属板块（channelBelong），当前设置此频道归属于第三板块，当删除该频道时该频道将回到第三板块中
                channel = new Channel(aMyChannel, 3, i);
            } else {
                channel = new Channel(aMyChannel, (Object) i);
            }
            myChannelList.add(channel);
        }

        for (String aMyChannel : recommendChannel1) {
            Channel channel = new Channel(aMyChannel);
            recommendChannelList1.add(channel);
        }

        for (String aMyChannel : recommendChannel2) {
            Channel channel = new Channel(aMyChannel);
            recommendChannelList2.add(channel);
        }

        for (String aMyChannel : recommendChannel3) {
            Channel channel = new Channel(aMyChannel);
            recommendChannelList3.add(channel);
        }

        channelView.setChannelFixedCount(2);
        channelView.addPlate("已选配置", myChannelList);
        channelView.addPlate("推荐配置", recommendChannelList1);
        channelView.addPlate("系统配置", recommendChannelList2);
        channelView.addPlate("其他配置", recommendChannelList3);
        channelView.inflateData();
        channelView.setOnChannelItemClickListener(this);
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
        CustomMapStyleOptions customMapStyleOptions = new CustomMapStyleOptions();
        customMapStyleOptions.setStyleDataPath(Environment.getExternalStorageDirectory() + "/data/mapStyle/style.data");
        aMap.setCustomMapStyle(customMapStyleOptions);
        aMap.setMapCustomEnable(true);
        //设置默认缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        //实时路况
        aMap.setTrafficEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
    }

    public void initPieChart(){
        List<PieEntry> pieEntryList = new ArrayList<PieEntry>();
        List<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#03BCBF"));
        colors.add(Color.parseColor("#0C5CA3"));
        colors.add(Color.parseColor("#4D9DFC"));
        colors.add(Color.parseColor("#0ADDA6"));
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

        /**
         * 设置pieChart图表基本属性
         */
        //设置是否使用百分比
        controllerTrafficSituationPie.setUsePercentValues(true);
        //设置pieChart图表的描述
        controllerTrafficSituationPie.getDescription().setEnabled(false);
        //设置pieChart图表上下左右的偏移，类似于外边距
        controllerTrafficSituationPie.setExtraOffsets(0, 0, 0, 0);
        //设置pieChart图表转动阻力摩擦系数[0,1]
        controllerTrafficSituationPie.setDragDecelerationFrictionCoef(0.95f);
        //设置pieChart图表起始角度
        controllerTrafficSituationPie.setRotationAngle(0);
        //设置pieChart图表是否可以手动旋转
        controllerTrafficSituationPie.setRotationEnabled(true);
        //设置piecahrt图表点击Item高亮是否可用
        controllerTrafficSituationPie.setHighlightPerTapEnabled(false);

        /**
         * 设置pieChart图表Item文本属性
         */
        //设置pieChart是否只显示饼图上百分比不显示文字（true：下面属性才有效果）
        controllerTrafficSituationPie.setDrawEntryLabels(true);
        //设置pieChart图表文本字体颜色
        controllerTrafficSituationPie.setEntryLabelColor(Color.WHITE);
        //设置pieChart图表文本字体大小
        controllerTrafficSituationPie.setEntryLabelTextSize(8f);

        /**
         * 设置pieChart内部圆环属性
         */
        //是否显示PieChart内部圆环(true:下面属性才有意义)
        controllerTrafficSituationPie.setDrawHoleEnabled(true);
        //设置PieChart内部圆的半径(这里设置28.0f)
        controllerTrafficSituationPie.setHoleRadius(58.0f);
        //设置PieChart内部透明圆的半径(这里设置31.0f)
        controllerTrafficSituationPie.setTransparentCircleRadius(38.0f);
        //设置PieChart内部透明圆与内部圆间距(31f-28f)填充颜色
        controllerTrafficSituationPie.setTransparentCircleColor(Color.parseColor("#0030A0A3"));
        //设置PieChart内部透明圆与内部圆间距(31f-28f)透明度[0~255]数值越小越透明
        controllerTrafficSituationPie.setTransparentCircleAlpha(255);
        //设置PieChart内部圆的颜色
        controllerTrafficSituationPie.setHoleColor(Color.parseColor("#141F4C"));
        //是否绘制PieChart内部中心文本（true：下面属性才有意义）
        controllerTrafficSituationPie.setDrawCenterText(true);
        //设置PieChart内部圆文字的内容
        controllerTrafficSituationPie.setCenterText("交通态势");
        //设置PieChart内部圆文字的大小
        controllerTrafficSituationPie.setCenterTextSize(10f);
        //设置PieChart内部圆文字的颜色
        controllerTrafficSituationPie.setCenterTextColor(Color.parseColor("#ffffff"));

        /**
         * 设置pieCahrt图例
         */
        //获取图例
        Legend l = controllerTrafficSituationPie.getLegend();
        //是否启用图列（true：下面属性才有意义）
        l.setEnabled(false);
        //设置垂直位置
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        //设置水平位置
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //设置整体位置
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //设置图例的形状
        l.setForm(Legend.LegendForm.DEFAULT);
        //设置图例的大小
        l.setFormSize(4);
        //设置每个图例实体中标签和形状之间的间距
        l.setFormToTextSpace(10f);
        //设置图列换行(注意使用影响性能,仅适用legend位于图表下面)
        l.setWordWrapEnabled(true);
        //设置图例实体之间延X轴的间距（setOrientation = HORIZONTAL有效）
        l.setXEntrySpace(10f);
        //设置图例实体之间延Y轴的间距（setOrientation = VERTICAL 有效）
        l.setYEntrySpace(8f);
        //设置比例块Y轴偏移量
        l.setYOffset(0f);
        //设置图例标签文本的大小
        l.setTextSize(8f);
        //设置图例标签文本的颜色
        l.setTextColor(Color.parseColor("#000000"));

        //pieChart添加数据
        //饼状图数据集 PieDataSet
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "交通态势one");
        //设置饼状Item之间的间隙
        pieDataSet.setSliceSpace(3f);
        //设置饼状Item被选中时变化的距离
        pieDataSet.setSelectionShift(10f);
        //为DataSet中的数据匹配上颜色集(饼图Item颜色)
        pieDataSet.setColors(colors);
        //最终数据 PieData
        PieData pieData = new PieData(pieDataSet);
        //设置是否显示数据实体(百分比，true:以下属性才有意义)
        pieData.setDrawValues(true);
        //设置所有DataSet内数据实体（百分比）的文本颜色
        pieData.setValueTextColor(Color.WHITE);
        //设置所有DataSet内数据实体（百分比）的文本字体大小
        pieData.setValueTextSize(12f);
        //设置所有DataSet内数据实体（百分比）的文本字体格式
        pieData.setValueFormatter(new PercentFormatter());
        controllerTrafficSituationPie.setData(pieData);
        controllerTrafficSituationPie.highlightValues(null);
        //将图表重绘以显示设置的属性和数据
        controllerTrafficSituationPie.invalidate();
    }

    @Override
    public void channelItemClick(int position, Channel channel) {
        //showErrorToast("频道点击");
    }

    @Override
    public void channelItemDeleteClick(int position, Channel channel) {
        switch (channel.getChannelName()){
            case "交通态势监控信息":
                lin5.setVisibility(View.GONE);
                break;
            case "设备信息":
                lin3.setVisibility(View.GONE);
                break;
            case "会展监控信息":
                lin4.setVisibility(View.GONE);
                break;
            case "设备点标记":
                aMap.clear();
                MarkerOptions markerOption = new MarkerOptions();
                LatLng latLng = new LatLng(29.718435,106.542036);
                markerOption.position(latLng);
                markerOption.draggable(false);
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),R.drawable.dot)));
                aMap.addMarker(markerOption);
                CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(29.718435,106.542036),14,0,0));
                aMap.moveCamera(mCameraUpdate);
                break;
        }
    }

    @Override
    public void channelEditFinish(List<Channel> channelList) {
        //showErrorToast("频道编辑完成");
    }

    @Override
    public void channelEditStart() {
        //showErrorToast("开始编辑频道");
    }

    @Override
    public void channelItemOtherClick(int position, Channel channel) {
        switch (channel.getChannelName()){
            case "交通态势监控信息":
                lin5.setVisibility(View.VISIBLE);
                break;
            case "设备信息":
                lin3.setVisibility(View.VISIBLE);
                break;
            case "会展监控信息":
                lin4.setVisibility(View.VISIBLE);
                break;
            case "设备点标记":
                initLightsMarker();
                initDustbinMarker();
                initCoverMarker();
                initLampMarker();
                break;
        }
    }

    /**
     * 绘制点标记(红绿灯)
     */
    public void initLightsMarker(){
        MarkerOptions markerOption = null;
        LatLng latLng = null;
        for(int i = 0; i < lightsLongitude.length; i++){
            markerOption = new MarkerOptions();
            latLng = new LatLng(lightsLongitude[i],lightsLatitude[i]);
            markerOption.position(latLng);
            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.hld)));
            aMap.addMarker(markerOption);
        }
    }

    /**
     * 绘制点标记(垃圾箱)
     */
    public void initDustbinMarker(){
        MarkerOptions markerOption = null;
        LatLng latLng = null;
        for(int i = 0; i < dustbinLongitude.length; i++){
            markerOption = new MarkerOptions();
            latLng = new LatLng(dustbinLongitude[i],dustbinLatitude[i]);
            markerOption.position(latLng);
            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.laji)));
            aMap.addMarker(markerOption);
        }
    }

    /**
     * 绘制点标记(井盖)
     */
    public void initCoverMarker(){
        MarkerOptions markerOption = null;
        LatLng latLng = null;
        for(int i = 0; i < coverLongitude.length; i++){
            markerOption = new MarkerOptions();
            latLng = new LatLng(coverLongitude[i],coverLatitude[i]);
            markerOption.position(latLng);
            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.jinggai)));
            aMap.addMarker(markerOption);
        }
    }

    /**
     * 绘制点标记(路灯)
     */
    public void initLampMarker(){
        MarkerOptions markerOption = null;
        LatLng latLng = null;
        for(int i = 0; i < lampLongitude.length; i++){
            markerOption = new MarkerOptions();
            latLng = new LatLng(lampLongitude[i],lampLatitude[i]);
            markerOption.position(latLng);
            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.ludeng)));
            aMap.addMarker(markerOption);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        startConfigure = 0;
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.lin1:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 获取手指第一次接触屏幕
                        sx = (int) event.getRawX();
                        sy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动对应的事件
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        // 获取手指移动的距离
                        int dx = x - sx;
                        int dy = y - sy;
                        // 得到view最开始的各顶点的坐标
                        int l = lin1.getLeft();
                        int r = lin1.getRight();
                        int t = lin1.getTop();
                        int b = lin1.getBottom();
                        // 更改view在窗体的位置
                        lin1.layout(l + dx, t + dy, r + dx, b + dy);
                        // 获取移动后的位置
                        sx = (int) event.getRawX();
                        sy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:// 手指离开屏幕对应事件
                        // 记录最后view在窗体的位置
                        int lasty = lin1.getTop();
                        int lastx = lin1.getLeft();
                        break;
                }
                break;
            case R.id.lin2:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 获取手指第一次接触屏幕
                        sx = (int) event.getRawX();
                        sy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动对应的事件
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        // 获取手指移动的距离
                        int dx = x - sx;
                        int dy = y - sy;
                        // 得到view最开始的各顶点的坐标
                        int l = lin2.getLeft();
                        int r = lin2.getRight();
                        int t = lin2.getTop();
                        int b = lin2.getBottom();
                        // 更改view在窗体的位置
                        lin2.layout(l + dx, t + dy, r + dx, b + dy);
                        // 获取移动后的位置
                        sx = (int) event.getRawX();
                        sy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:// 手指离开屏幕对应事件
                        // 记录最后view在窗体的位置
                        int lasty = lin2.getTop();
                        int lastx = lin2.getLeft();
                        break;
                }
                break;
            case R.id.lin3:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 获取手指第一次接触屏幕
                        sx = (int) event.getRawX();
                        sy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动对应的事件
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        // 获取手指移动的距离
                        int dx = x - sx;
                        int dy = y - sy;
                        // 得到view最开始的各顶点的坐标
                        int l = lin3.getLeft();
                        int r = lin3.getRight();
                        int t = lin3.getTop();
                        int b = lin3.getBottom();
                        // 更改view在窗体的位置
                        lin3.layout(l + dx, t + dy, r + dx, b + dy);
                        // 获取移动后的位置
                        sx = (int) event.getRawX();
                        sy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:// 手指离开屏幕对应事件
                        // 记录最后view在窗体的位置
                        int lasty = lin3.getTop();
                        int lastx = lin3.getLeft();
                        break;
                }
                break;
            case R.id.lin4:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 获取手指第一次接触屏幕
                        sx = (int) event.getRawX();
                        sy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动对应的事件
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        // 获取手指移动的距离
                        int dx = x - sx;
                        int dy = y - sy;
                        // 得到view最开始的各顶点的坐标
                        int l = lin4.getLeft();
                        int r = lin4.getRight();
                        int t = lin4.getTop();
                        int b = lin4.getBottom();
                        // 更改view在窗体的位置
                        lin4.layout(l + dx, t + dy, r + dx, b + dy);
                        // 获取移动后的位置
                        sx = (int) event.getRawX();
                        sy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:// 手指离开屏幕对应事件
                        // 记录最后view在窗体的位置
                        int lasty = lin4.getTop();
                        int lastx = lin4.getLeft();
                        break;
                }
                break;
            case R.id.lin5:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 获取手指第一次接触屏幕
                        sx = (int) event.getRawX();
                        sy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动对应的事件
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        // 获取手指移动的距离
                        int dx = x - sx;
                        int dy = y - sy;
                        // 得到view最开始的各顶点的坐标
                        int l = lin5.getLeft();
                        int r = lin5.getRight();
                        int t = lin5.getTop();
                        int b = lin5.getBottom();
                        // 更改view在窗体的位置
                        lin5.layout(l + dx, t + dy, r + dx, b + dy);
                        // 获取移动后的位置
                        sx = (int) event.getRawX();
                        sy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:// 手指离开屏幕对应事件
                        // 记录最后view在窗体的位置
                        int lasty = lin5.getTop();
                        int lastx = lin5.getLeft();
                        break;
                }
                break;
        }
        return true;
    }
}
