package com.cq.hhxk.voice.voiceoperationsystem.ui;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.cq.hhxk.voice.voiceoperationsystem.event.VoiceServiceEvent;
import com.em.baseframe.util.AppManger;
import com.em.baseframe.util.DateTool;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.startSpongeActivity;

/**
 * @title  工作人员海绵报表
 * @date   2019/08/15
 * @author enmaoFu
 */
public class SpongeActivity extends BaseAty {

    @BindView(R.id.map_personnel)
    MapView mMapView;
    @BindView(R.id.controller_traffic_situation_pie)
    PieChart controllerTrafficSituationPie;
    @BindView(R.id.time)
    TextView timeText;
    @BindView(R.id.data)
    TextView dataText;
    @BindView(R.id.left_lin)
    LinearLayout leftLin;
    @BindView(R.id.right_lin)
    LinearLayout rightLin;
    @BindView(R.id.lin_top)
    LinearLayout linTop;

    /**
     * 地图对象
     */
    private AMap aMap;

    /**
     * 水环境在线监测站经度
     */
    private double[] runoffLongitude = {29.705833,29.70546,29.706653,29.704118,29.706206,29.703895,29.70546,29.708815,29.703447,29.699347};

    /**
     * 水环境在线监测站纬度
     */
    private double[] runoffLatitude = {106.520777,106.521807,106.521034,106.521807,106.52009,106.524467,106.52524,106.523437,106.51906,106.523609};

    /**
     * 区域径流水文监测站经度
     */
    private double[] rainLongitude = {29.716722,29.716396,29.716508,29.713452,29.715949,29.717365,29.713377,29.711625,29.716769,29.711774};

    /**
     * 区域径流水文监测站纬度
     */
    private double[] rainLatitude = {106.548425,106.547738,106.547953,106.545035,106.549412,106.547438,106.544176,106.548253,106.550571,106.544906};

    /**
     * 地块径流水文监测站经度
     */
    private double[] waterloggingLongitude = {29.709091,29.708047,29.70417,29.701337,29.699995,29.698318,29.703425,29.705438,29.705885,29.708121};

    /**
     * 地块径流水文监测站纬度
     */
    private double[] waterloggingLatitude = {106.537374,106.539949,106.537761,106.536559,106.545013,106.540207,106.534327,106.53304,106.54806,106.538018};

    /**
     * 水生态在线监测站经度
     */
    private double[] waterEnvironmentLongitude = {29.700853,29.700107,29.696156,29.694217,29.691533,29.691906,29.688923,29.685195,29.693546,29.687507};

    /**
     * 水生态在线监测站纬度
     */
    private double[] waterEnvironmentLatitude = {106.530765,106.53274,106.529135,106.540207,106.53231,106.544327,106.536001,106.535915,106.529478,106.542438};

    /**
     * 水位水量在线监测站经度
     */
    private double[] waterLevelLongitude = {29.720979,29.720606,29.727132,29.72579,29.726247,29.725017,29.725138,29.725032,29.724301,29.726952};

    /**
     * 水位水量在线监测站维度
     */
    private double[] waterLevelLatitude = {106.548403,106.549347,106.551724,106.552421,106.549878,106.556777,106.552432,106.55129,106.554268,106.55497};

    /**
     * 左边布局动画
     */
    private Animation leftReAnimation;

    /**
     * 右边布局动画
     */
    private Animation rightAnimation;

    /**
     * 上边布局动画
     */
    private Animation topAnimation;
    private TimeThread timeThread;
    private boolean stopThread = false;
    private static final int MSG_ONE = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ONE:
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    timeText.setText(simpleDateFormat.format(date));
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comprehensive_spongel;
    }

    @Override
    protected void initData() {
        AppManger.getInstance().killActivity(ComprehensiveControllerActivity.class);
        AppManger.getInstance().killActivity(ComprehensivePersonnelActivity.class);
        AppManger.getInstance().killActivity(NavigationAvtivity.class);
        AppManger.getInstance().killActivity(MapActivity.class);
        AppManger.getInstance().killActivity(ResidentActivity.class);
        AppManger.getInstance().killActivity(ExhibitionActivity.class);
        AppManger.getInstance().killActivity(ConfigureActivity.class);

        startSpongeActivity = 1;

        initMap();
        initPieChart();
        initRegionRunoffMarker();
        initMassifRunoffMarker();
        initAquaticEcologyMarker();
        initWaterEnvironmentMarker();
        initWaterLevelMarker();
        changeBearing(180.0f,3500);
        slideOut();
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
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(29.718435,106.542036),13,0,0));
        aMap.moveCamera(mCameraUpdate);
        CustomMapStyleOptions customMapStyleOptions = new CustomMapStyleOptions();
        customMapStyleOptions.setStyleDataPath(Environment.getExternalStorageDirectory() + "/data/mapStyle/style.data");
        aMap.setCustomMapStyle(customMapStyleOptions);
        aMap.setMapCustomEnable(true);
        //设置默认缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        //实时路况
        aMap.setTrafficEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
    }

    /**
     * 更改角度（动画）
     */
    public void changeBearing(final float angle,final int millisecond){
        new Handler().postDelayed(new Runnable(){
            public void run() {
                aMap.animateCamera(CameraUpdateFactory.changeBearing(angle), millisecond, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onCancel() {

                    }
                });//地图旋转角度
            }
        }, 1500);

    }

    public void slideOut(){
        new Handler().postDelayed(new Runnable(){
            public void run() {
                leftLin.setVisibility(View.VISIBLE);
                leftReAnimation = AnimationUtils.loadAnimation(SpongeActivity.this, R.anim.out_to_left);
                leftReAnimation.setFillAfter(true);//android动画结束后停在结束位置
                leftLin.startAnimation(leftReAnimation);

                rightLin.setVisibility(View.VISIBLE);
                rightAnimation = AnimationUtils.loadAnimation(SpongeActivity.this, R.anim.out_to_right);
                rightAnimation.setFillAfter(true);//android动画结束后停在结束位置
                rightLin.startAnimation(rightAnimation);

                linTop.setVisibility(View.VISIBLE);
                topAnimation = AnimationUtils.loadAnimation(SpongeActivity.this, R.anim.out_to_top);
                topAnimation.setFillAfter(true);//android动画结束后停在结束位置
                linTop.startAnimation(topAnimation);

                timeThread = new TimeThread();
                timeThread.start();
                dataText.setText(DateTool.getformatDate("MM月dd日 ") + getWeek());
            }
        }, 4000);

    }

    public void initPieChart(){
        List<PieEntry> pieEntryList = new ArrayList<PieEntry>();
        List<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#03BCBF"));
        colors.add(Color.parseColor("#0C5CA3"));
        colors.add(Color.parseColor("#4D9DFC"));
        colors.add(Color.parseColor("#0ADDA6"));
        colors.add(Color.parseColor("#6485ef"));
        colors.add(Color.parseColor("#03BCBF"));
        colors.add(Color.parseColor("#0C5CA3"));
        colors.add(Color.parseColor("#4D9DFC"));
        //饼图实体 PieEntry
        PieEntry region = new PieEntry(30, "区域径流");
        PieEntry massif = new PieEntry(20, "地块径流");
        PieEntry science = new PieEntry(15, "水环境");
        PieEntry ecology = new PieEntry(15, "水生态");
        PieEntry level = new PieEntry(10, "水位水量");
        pieEntryList.add(region);
        pieEntryList.add(massif);
        pieEntryList.add(science);
        pieEntryList.add(ecology);
        pieEntryList.add(level);

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
        controllerTrafficSituationPie.setCenterText("监测数据来源");
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
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "监测数据来源");
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

    /**
     * 绘制点标记(区域径流水文监测站)
     */
    public void initRegionRunoffMarker(){
        MarkerOptions markerOption = null;
        LatLng latLng = null;
        for(int i = 0; i < rainLongitude.length; i++){
            markerOption = new MarkerOptions();
            latLng = new LatLng(rainLongitude[i],rainLatitude[i]);
            markerOption.position(latLng);
            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.quyujingl)));
            aMap.addMarker(markerOption);
        }
    }

    /**
     * 绘制点标记(地块径流水文监测站)
     */
    public void initMassifRunoffMarker(){
        MarkerOptions markerOption = null;
        LatLng latLng = null;
        for(int i = 0; i < waterloggingLongitude.length; i++){
            markerOption = new MarkerOptions();
            latLng = new LatLng(waterloggingLongitude[i],waterloggingLatitude[i]);
            markerOption.position(latLng);
            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.dikuaijinl)));
            aMap.addMarker(markerOption);
        }
    }

    /**
     * 绘制点标记(水生态在线监测站)
     */
    public void initAquaticEcologyMarker(){
        MarkerOptions markerOption = null;
        LatLng latLng = null;
        for(int i = 0; i < waterEnvironmentLongitude.length; i++){
            markerOption = new MarkerOptions();
            latLng = new LatLng(waterEnvironmentLongitude[i],waterEnvironmentLatitude[i]);
            markerOption.position(latLng);
            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.shuishengtai)));
            aMap.addMarker(markerOption);
        }
    }

    /**
     * 绘制点标记(水环境在线监测站)
     */
    public void initWaterEnvironmentMarker(){
        MarkerOptions markerOption = null;
        LatLng latLng = null;
        for(int i = 0; i < runoffLongitude.length; i++){
            markerOption = new MarkerOptions();
            latLng = new LatLng(runoffLongitude[i],runoffLatitude[i]);
            markerOption.position(latLng);
            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.shuihuanj)));
            aMap.addMarker(markerOption);
        }
    }

    /**
     * 绘制点标记(水位水量在线监测站)
     */
    public void initWaterLevelMarker(){
        MarkerOptions markerOption = null;
        LatLng latLng = null;
        for(int i = 0; i < waterLevelLongitude.length; i++){
            markerOption = new MarkerOptions();
            latLng = new LatLng(waterLevelLongitude[i],waterLevelLatitude[i]);
            markerOption.position(latLng);
            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.shuiwei)));
            aMap.addMarker(markerOption);
        }
    }

    /**
     * 通知Handler每秒获取一次时间
     */
    class TimeThread extends Thread {
        //重写run方法
        @Override
        public void run() {
            super.run();
            while (!stopThread) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = MSG_ONE;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startSpongeActivity = 0;
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
