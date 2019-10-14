package com.cq.hhxk.voice.voiceoperationsystem.ui;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.cq.hhxk.voice.voiceoperationsystem.adapter.AutoPollPersonAdapter;
import com.cq.hhxk.voice.voiceoperationsystem.base.BaseAty;
import com.cq.hhxk.voice.voiceoperationsystem.constant.VoiceServiceConstant;
import com.cq.hhxk.voice.voiceoperationsystem.dao.Patrol;
import com.cq.hhxk.voice.voiceoperationsystem.event.VoiceServiceEvent;
import com.cq.hhxk.voice.voiceoperationsystem.http.HttpInterface;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.AutoPollPersonPojo;
import com.cq.hhxk.voice.voiceoperationsystem.view.AutoPollRecyclerView;
import com.em.baseframe.util.AppJsonUtil;
import com.em.baseframe.util.AppManger;
import com.em.baseframe.util.DateTool;
import com.em.baseframe.util.RetrofitUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.startComprehensivePersonnelActivity;
import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.startNaviType;

/**
 * @title  工作人员综合报表（城管）
 * @date   2019/07/31
 * @author enmaoFu
 */
public class ComprehensivePersonnelActivity extends BaseAty implements VoiceServiceConstant {

    @BindView(R.id.map_personnel)
    MapView mMapView;
    @BindView(R.id.image1)
    ImageView image1;
    @BindView(R.id.lin1)
    LinearLayout lin1;
    @BindView(R.id.recyvlerview)
    AutoPollRecyclerView recyclerView;
    @BindView(R.id.left_lin)
    LinearLayout leftLin;
    @BindView(R.id.right_lin)
    LinearLayout rightLin;
    @BindView(R.id.lin_top)
    LinearLayout linTop;
    @BindView(R.id.time)
    TextView timeText;
    @BindView(R.id.data)
    TextView dataText;

    /**
     * recyclerview布局管理器
     */
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * 数据源
     */
    private List<AutoPollPersonPojo> autoPollPojos;

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

    private List<Patrol> patrols;

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
        return R.layout.activity_comprehensive_personnel;
    }

    @Override
    protected void initData() {
        AppManger.getInstance().killActivity(ComprehensiveControllerActivity.class);
        AppManger.getInstance().killActivity(MapActivity.class);
        AppManger.getInstance().killActivity(NavigationAvtivity.class);
        AppManger.getInstance().killActivity(ResidentActivity.class);
        AppManger.getInstance().killActivity(ExhibitionActivity.class);
        AppManger.getInstance().killActivity(SpongeActivity.class);
        AppManger.getInstance().killActivity(ConfigureActivity.class);

        startComprehensivePersonnelActivity = 1;

        doHttp(RetrofitUtils.createApi(HttpInterface.class).patrol(),1);

        initMap();
        initLightsMarker();
        initDustbinMarker();
        initCoverMarker();
        initLampMarker();
        changeBearing(180.0f,3500);
        slideOut();
    }

    @Override
    protected void requestData() {

    }

    public void initRv(List<AutoPollPersonPojo> autoPollPersonPojos){
        AutoPollPersonAdapter adapter = new AutoPollPersonAdapter(autoPollPersonPojos);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.parseColor("#00eeeeee"))
                        .sizeResId(R.dimen.recyclerview_item_hr1)
                        .build());
        recyclerView.setAdapter(adapter);
        recyclerView.start();

        /*//实例化布局管理器
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //实例化适配器
        autoPollAdapter = new AutoPollAdapter(R.layout.item_comprehensive_personnel, setData());
        //设置布局管理器
        recyclerView.setLayoutManager(mLayoutManager);
        //大小不受适配器影响
        recyclerView.setHasFixedSize(true);
        //设置间隔样式
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.parseColor("#00eeeeee"))
                        .sizeResId(R.dimen.recyclerview_item_hr1)
                        .build());
        //设置adapter
        recyclerView.setAdapter(autoPollAdapter);
        recyclerView.start();*/
    }

    public List<AutoPollPersonPojo> setData(){
        autoPollPojos = new ArrayList<>();
        AutoPollPersonPojo autoPollPersonPojo1 = new AutoPollPersonPojo();
        autoPollPersonPojo1.setColor(0);
        autoPollPersonPojo1.setName("赵辉");
        autoPollPersonPojo1.setInfo("今日巡检完毕");

        AutoPollPersonPojo autoPollPersonPojo2 = new AutoPollPersonPojo();
        autoPollPersonPojo2.setColor(1);
        autoPollPersonPojo2.setName("张海");
        autoPollPersonPojo2.setInfo("今日巡检完毕");

        AutoPollPersonPojo autoPollPersonPojo3 = new AutoPollPersonPojo();
        autoPollPersonPojo3.setColor(2);
        autoPollPersonPojo3.setName("刘志勇");
        autoPollPersonPojo3.setInfo("正在巡查路线1");

        AutoPollPersonPojo autoPollPersonPojo4 = new AutoPollPersonPojo();
        autoPollPersonPojo4.setColor(3);
        autoPollPersonPojo4.setName("王红霞");
        autoPollPersonPojo4.setInfo("检修路灯中");

        AutoPollPersonPojo autoPollPersonPojo5 = new AutoPollPersonPojo();
        autoPollPersonPojo5.setColor(4);
        autoPollPersonPojo5.setName("康华");
        autoPollPersonPojo5.setInfo("正在巡查路线2");

        AutoPollPersonPojo autoPollPersonPojo6 = new AutoPollPersonPojo();
        autoPollPersonPojo6.setColor(5);
        autoPollPersonPojo6.setName("刘宝华");
        autoPollPersonPojo6.setInfo("正在巡查路线3");

        AutoPollPersonPojo autoPollPersonPojo7 = new AutoPollPersonPojo();
        autoPollPersonPojo7.setColor(6);
        autoPollPersonPojo7.setName("张晓红");
        autoPollPersonPojo7.setInfo("检修垃圾箱中");

        AutoPollPersonPojo autoPollPersonPojo8 = new AutoPollPersonPojo();
        autoPollPersonPojo8.setColor(7);
        autoPollPersonPojo8.setName("韩旭");
        autoPollPersonPojo8.setInfo("正在巡查路线4");

        autoPollPojos.add(autoPollPersonPojo1);
        autoPollPojos.add(autoPollPersonPojo2);
        autoPollPojos.add(autoPollPersonPojo3);
        autoPollPojos.add(autoPollPersonPojo4);
        autoPollPojos.add(autoPollPersonPojo5);
        autoPollPojos.add(autoPollPersonPojo6);
        autoPollPojos.add(autoPollPersonPojo7);
        autoPollPojos.add(autoPollPersonPojo8);

        return autoPollPojos;
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
                leftReAnimation = AnimationUtils.loadAnimation(ComprehensivePersonnelActivity.this, R.anim.out_to_left);
                leftReAnimation.setFillAfter(true);//android动画结束后停在结束位置
                leftLin.startAnimation(leftReAnimation);

                rightLin.setVisibility(View.VISIBLE);
                rightAnimation = AnimationUtils.loadAnimation(ComprehensivePersonnelActivity.this, R.anim.out_to_right);
                rightAnimation.setFillAfter(true);//android动画结束后停在结束位置
                rightLin.startAnimation(rightAnimation);

                linTop.setVisibility(View.VISIBLE);
                topAnimation = AnimationUtils.loadAnimation(ComprehensivePersonnelActivity.this, R.anim.out_to_top);
                topAnimation.setFillAfter(true);//android动画结束后停在结束位置
                linTop.startAnimation(topAnimation);

                timeThread = new TimeThread();
                timeThread.start();
                dataText.setText(DateTool.getformatDate("MM月dd日 ") + getWeek());
            }
        }, 4000);

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
    public void onSuccess(String result, Call<ResponseBody> call, Response<ResponseBody> response, int what) {
        super.onSuccess(result, call, response, what);
        switch (what){
            case 1:
                patrols = AppJsonUtil.getArrayList(result,Patrol.class);
                autoPollPojos = new ArrayList<>();
                AutoPollPersonPojo autoPollPersonPojo = null;
                for(int i = 0; i < patrols.size(); i ++){
                    autoPollPersonPojo = new AutoPollPersonPojo();
                    autoPollPersonPojo.setColor(i);
                    autoPollPersonPojo.setName(patrols.get(i).getName());
                    autoPollPersonPojo.setInfo(patrols.get(i).getInfo());
                    autoPollPojos.add(autoPollPersonPojo);
                }
                initRv(autoPollPojos);
                break;
        }
    }

    @Override
    public void onFailure(String result, Call<ResponseBody> call, Response<ResponseBody> response, int what) {
        super.onFailure(result, call, response, what);
        initRv(setData());
    }

    @Override
    public void onError(Call<ResponseBody> call, Throwable t, int what) {
        super.onError(call, t, what);
        initRv(setData());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(VoiceServiceEvent voiceServiceEvent){
        switch (voiceServiceEvent.getIdStr()){
            case START_NAVI_PERSONNEL:
                startNaviType = 1;
                startActivity(NavigationAvtivity.class,null);
                break;
        }
    }

    /*@OnClick({R.id.back_re,R.id.location_re})
    @Override
    public void btnClick(View view) {
        switch (view.getId()){
            case R.id.back_re:
                finish();
                break;
            case R.id.location_re:
                CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(29.718435,106.542036),13,0,0));
                aMap.moveCamera(mCameraUpdate);
                break;
        }
    }*/

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
        startComprehensivePersonnelActivity = 0;
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if(null != recyclerView){
            recyclerView.stop();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
