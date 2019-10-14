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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.col.sln3.ex;
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
import com.cq.hhxk.voice.voiceoperationsystem.adapter.AutoPollAdapter;
import com.cq.hhxk.voice.voiceoperationsystem.adapter.AutoPollExhibitionSchAdapter;
import com.cq.hhxk.voice.voiceoperationsystem.adapter.AutoPollExhibitionVenAdapter;
import com.cq.hhxk.voice.voiceoperationsystem.adapter.AutoPollPersonAdapter;
import com.cq.hhxk.voice.voiceoperationsystem.adapter.ExhibitionCSHAdapter;
import com.cq.hhxk.voice.voiceoperationsystem.adapter.ExhibitionVENAdapter;
import com.cq.hhxk.voice.voiceoperationsystem.base.BaseAty;
import com.cq.hhxk.voice.voiceoperationsystem.dao.Flowrate;
import com.cq.hhxk.voice.voiceoperationsystem.dao.Schedule;
import com.cq.hhxk.voice.voiceoperationsystem.http.HttpInterface;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.ExhibitionCSHPojo;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.ExhibitionVENPojo;
import com.cq.hhxk.voice.voiceoperationsystem.view.AutoPollRecyclerView;
import com.em.baseframe.util.AppJsonUtil;
import com.em.baseframe.util.AppManger;
import com.em.baseframe.util.DateTool;
import com.em.baseframe.util.RetrofitUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.startExhibitionActivity;

/**
 * @title  工作人员会展报表
 * @date   2019/08/15
 * @author enmaoFu
 */
public class ExhibitionActivity extends BaseAty {

    @BindView(R.id.map_controller)
    MapView mMapView;
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
    @BindView(R.id.recyvlerview_sch)
    AutoPollRecyclerView recyclerViewCSH;
    @BindView(R.id.recyvlerview_ven)
    AutoPollRecyclerView recyvlerviewVEN;

    /**
     * 地图对象
     */
    private AMap aMap;

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

    /**
     * recyclerview布局管理器
     */
    private RecyclerView.LayoutManager mLayoutManagerCSH;

    /**
     * 数据源
     */
    private List<ExhibitionCSHPojo> exhibitionCSHPojos;

    /**
     * recyclerview布局管理器
     */
    private RecyclerView.LayoutManager mLayoutManagerVEN;

    /**
     * 适配器
     */
    private ExhibitionVENAdapter exhibitionVENAdapter;

    /**
     * 数据源
     */
    private List<ExhibitionVENPojo> exhibitionVENPojos;

    private List<Schedule> schedules;

    private List<Flowrate> flowrates;

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
        return R.layout.activity_exhibition;
    }

    @Override
    protected void initData() {
        AppManger.getInstance().killActivity(ComprehensiveControllerActivity.class);
        AppManger.getInstance().killActivity(ComprehensivePersonnelActivity.class);
        AppManger.getInstance().killActivity(NavigationAvtivity.class);
        AppManger.getInstance().killActivity(MapActivity.class);
        AppManger.getInstance().killActivity(ResidentActivity.class);
        AppManger.getInstance().killActivity(SpongeActivity.class);
        AppManger.getInstance().killActivity(ConfigureActivity.class);

        startExhibitionActivity = 1;

        doHttp(RetrofitUtils.createApi(HttpInterface.class).schedule(),1);
        doHttp(RetrofitUtils.createApi(HttpInterface.class).flowrate(),2);

        initMap();
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
                leftReAnimation = AnimationUtils.loadAnimation(ExhibitionActivity.this, R.anim.out_to_left);
                leftReAnimation.setFillAfter(true);//android动画结束后停在结束位置
                leftLin.startAnimation(leftReAnimation);

                rightLin.setVisibility(View.VISIBLE);
                rightAnimation = AnimationUtils.loadAnimation(ExhibitionActivity.this, R.anim.out_to_right);
                rightAnimation.setFillAfter(true);//android动画结束后停在结束位置
                rightLin.startAnimation(rightAnimation);

                linTop.setVisibility(View.VISIBLE);
                topAnimation = AnimationUtils.loadAnimation(ExhibitionActivity.this, R.anim.out_to_top);
                topAnimation.setFillAfter(true);//android动画结束后停在结束位置
                linTop.startAnimation(topAnimation);

                timeThread = new TimeThread();
                timeThread.start();
                dataText.setText(DateTool.getformatDate("MM月dd日 ") + getWeek());
            }
        }, 4000);

    }

    public void initRvCSH(List<ExhibitionCSHPojo> exhibitionCSHPojos){
        AutoPollExhibitionSchAdapter adapter = new AutoPollExhibitionSchAdapter(exhibitionCSHPojos);
        mLayoutManagerCSH = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewCSH.setLayoutManager(mLayoutManagerCSH);
        recyclerViewCSH.setHasFixedSize(true);
        recyclerViewCSH.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.parseColor("#00eeeeee"))
                        .sizeResId(R.dimen.recyclerview_item_hr1)
                        .build());
        recyclerViewCSH.setAdapter(adapter);
        recyclerViewCSH.start();
    }

    public void initRvVEN(List<ExhibitionVENPojo> exhibitionVENPojos){
        AutoPollExhibitionVenAdapter adapter = new AutoPollExhibitionVenAdapter(exhibitionVENPojos);
        mLayoutManagerVEN = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyvlerviewVEN.setLayoutManager(mLayoutManagerVEN);
        recyvlerviewVEN.setHasFixedSize(true);
        recyvlerviewVEN.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.parseColor("#00eeeeee"))
                        .sizeResId(R.dimen.recyclerview_item_hr1)
                        .build());
        recyvlerviewVEN.setAdapter(adapter);
        recyvlerviewVEN.start();
    }

    public List<ExhibitionCSHPojo> setDataCSH(){
        exhibitionCSHPojos = new ArrayList<>();
        ExhibitionCSHPojo exhibitionCSHPojo1 = new ExhibitionCSHPojo();
        exhibitionCSHPojo1.setColor(0);
        exhibitionCSHPojo1.setData("09:00-10:00");
        exhibitionCSHPojo1.setInfo("2019中国国际智能产业博览会开幕式");

        ExhibitionCSHPojo exhibitionCSHPojo2 = new ExhibitionCSHPojo();
        exhibitionCSHPojo2.setColor(1);
        exhibitionCSHPojo2.setData("10:00-12:00");
        exhibitionCSHPojo2.setInfo("大数据智能化高峰会");

        ExhibitionCSHPojo exhibitionCSHPojo3 = new ExhibitionCSHPojo();
        exhibitionCSHPojo3.setColor(2);
        exhibitionCSHPojo3.setData("09:00-17:00");
        exhibitionCSHPojo3.setInfo("中新工业APP创新应用大赛");

        ExhibitionCSHPojo exhibitionCSHPojo4 = new ExhibitionCSHPojo();
        exhibitionCSHPojo4.setColor(3);
        exhibitionCSHPojo4.setData("09:00-17:30");
        exhibitionCSHPojo4.setInfo("“智博杯”工业设计大赛获奖作品展示");

        ExhibitionCSHPojo exhibitionCSHPojo5 = new ExhibitionCSHPojo();
        exhibitionCSHPojo5.setColor(4);
        exhibitionCSHPojo5.setData("09:00-17:30");
        exhibitionCSHPojo5.setInfo("“智博杯”青年大数据智能化创新创业大赛作品展示\t\n");

        ExhibitionCSHPojo exhibitionCSHPojo6 = new ExhibitionCSHPojo();
        exhibitionCSHPojo6.setColor(5);
        exhibitionCSHPojo6.setData("13:30-18:00");
        exhibitionCSHPojo6.setInfo("i-VISTA自动驾驶汽车挑战赛（创新应用挑战赛）");

        ExhibitionCSHPojo exhibitionCSHPojo7 = new ExhibitionCSHPojo();
        exhibitionCSHPojo7.setColor(6);
        exhibitionCSHPojo7.setData("14:00-18:00");
        exhibitionCSHPojo7.setInfo("FPGA智能创新国际大赛总决赛");

        ExhibitionCSHPojo exhibitionCSHPojo8 = new ExhibitionCSHPojo();
        exhibitionCSHPojo8.setColor(7);
        exhibitionCSHPojo8.setData("14:00-14:30");
        exhibitionCSHPojo8.setInfo("2019中国国际智能产业博览会重大项目集中签约仪式");

        ExhibitionCSHPojo exhibitionCSHPojo9 = new ExhibitionCSHPojo();
        exhibitionCSHPojo9.setColor(8);
        exhibitionCSHPojo9.setData("14:00-18:00");
        exhibitionCSHPojo9.setInfo("重庆市大数据智能化发展战略专家咨询委员会首次年会");

        ExhibitionCSHPojo exhibitionCSHPojo10 = new ExhibitionCSHPojo();
        exhibitionCSHPojo10.setColor(9);
        exhibitionCSHPojo10.setData("15:00-18:00");
        exhibitionCSHPojo10.setInfo("第二届中国·重庆国际友好城市市长圆桌会");

        ExhibitionCSHPojo exhibitionCSHPojo11 = new ExhibitionCSHPojo();
        exhibitionCSHPojo11.setColor(10);
        exhibitionCSHPojo11.setData("14:30-18:00");
        exhibitionCSHPojo11.setInfo("数字经济百人会");

        ExhibitionCSHPojo exhibitionCSHPojo12 = new ExhibitionCSHPojo();
        exhibitionCSHPojo12.setColor(11);
        exhibitionCSHPojo12.setData("14:30-18:00");
        exhibitionCSHPojo12.setInfo("数字丝绸之路国际合作会议");

        ExhibitionCSHPojo exhibitionCSHPojo13 = new ExhibitionCSHPojo();
        exhibitionCSHPojo13.setColor(12);
        exhibitionCSHPojo13.setData("14:00-18:30");
        exhibitionCSHPojo13.setInfo("智能化应用与高品质生活高峰论坛");

        ExhibitionCSHPojo exhibitionCSHPojo14 = new ExhibitionCSHPojo();
        exhibitionCSHPojo14.setColor(13);
        exhibitionCSHPojo14.setData("14:00-18:00");
        exhibitionCSHPojo14.setInfo("微信公开课·重庆站 让创新改变生活");

        ExhibitionCSHPojo exhibitionCSHPojo15 = new ExhibitionCSHPojo();
        exhibitionCSHPojo15.setColor(14);
        exhibitionCSHPojo15.setData("13:00-15:40");
        exhibitionCSHPojo15.setInfo("新加坡-重庆服务4.0高峰论坛");

        ExhibitionCSHPojo exhibitionCSHPojo16 = new ExhibitionCSHPojo();
        exhibitionCSHPojo16.setColor(15);
        exhibitionCSHPojo16.setData("14:00-18:00");
        exhibitionCSHPojo16.setInfo("首届中英“智在未来”智能产业合作高峰论坛");

        exhibitionCSHPojos.add(exhibitionCSHPojo1);
        exhibitionCSHPojos.add(exhibitionCSHPojo2);
        exhibitionCSHPojos.add(exhibitionCSHPojo3);
        exhibitionCSHPojos.add(exhibitionCSHPojo4);
        exhibitionCSHPojos.add(exhibitionCSHPojo5);
        exhibitionCSHPojos.add(exhibitionCSHPojo6);
        exhibitionCSHPojos.add(exhibitionCSHPojo7);
        exhibitionCSHPojos.add(exhibitionCSHPojo8);
        exhibitionCSHPojos.add(exhibitionCSHPojo9);
        exhibitionCSHPojos.add(exhibitionCSHPojo10);
        exhibitionCSHPojos.add(exhibitionCSHPojo11);
        exhibitionCSHPojos.add(exhibitionCSHPojo12);
        exhibitionCSHPojos.add(exhibitionCSHPojo13);
        exhibitionCSHPojos.add(exhibitionCSHPojo14);
        exhibitionCSHPojos.add(exhibitionCSHPojo15);
        exhibitionCSHPojos.add(exhibitionCSHPojo16);
        return exhibitionCSHPojos;
    }

    public List<ExhibitionVENPojo> setDataVEN() {
        exhibitionVENPojos = new ArrayList<>();
        ExhibitionVENPojo exhibitionVENPojo1 = new ExhibitionVENPojo();
        exhibitionVENPojo1.setColor(0);
        exhibitionVENPojo1.setName("N1馆");
        exhibitionVENPojo1.setInfo("15202人");

        ExhibitionVENPojo exhibitionVENPojo2 = new ExhibitionVENPojo();
        exhibitionVENPojo2.setColor(1);
        exhibitionVENPojo2.setName("N2馆");
        exhibitionVENPojo2.setInfo("14201人");

        ExhibitionVENPojo exhibitionVENPojo3 = new ExhibitionVENPojo();
        exhibitionVENPojo3.setColor(2);
        exhibitionVENPojo3.setName("N3馆");
        exhibitionVENPojo3.setInfo("5236人");

        ExhibitionVENPojo exhibitionVENPojo4 = new ExhibitionVENPojo();
        exhibitionVENPojo4.setColor(3);
        exhibitionVENPojo4.setName("N4馆");
        exhibitionVENPojo4.setInfo("12369人");

        ExhibitionVENPojo exhibitionVENPojo5 = new ExhibitionVENPojo();
        exhibitionVENPojo5.setColor(4);
        exhibitionVENPojo5.setName("N5馆");
        exhibitionVENPojo5.setInfo("9536人");

        ExhibitionVENPojo exhibitionVENPojo6 = new ExhibitionVENPojo();
        exhibitionVENPojo6.setColor(5);
        exhibitionVENPojo6.setName("N6馆");
        exhibitionVENPojo6.setInfo("14203人");

        ExhibitionVENPojo exhibitionVENPojo7 = new ExhibitionVENPojo();
        exhibitionVENPojo7.setColor(6);
        exhibitionVENPojo7.setName("N7馆");
        exhibitionVENPojo7.setInfo("10385人");

        ExhibitionVENPojo exhibitionVENPojo8 = new ExhibitionVENPojo();
        exhibitionVENPojo8.setColor(7);
        exhibitionVENPojo8.setName("N8馆");
        exhibitionVENPojo8.setInfo("8236人");

        ExhibitionVENPojo exhibitionVENPojo9 = new ExhibitionVENPojo();
        exhibitionVENPojo9.setColor(8);
        exhibitionVENPojo9.setName("S1馆");
        exhibitionVENPojo9.setInfo("24162人");

        ExhibitionVENPojo exhibitionVENPojo10 = new ExhibitionVENPojo();
        exhibitionVENPojo10.setColor(9);
        exhibitionVENPojo10.setName("S2馆");
        exhibitionVENPojo10.setInfo("11056人");

        ExhibitionVENPojo exhibitionVENPojo11 = new ExhibitionVENPojo();
        exhibitionVENPojo11.setColor(10);
        exhibitionVENPojo11.setName("S3馆");
        exhibitionVENPojo11.setInfo("15202人");

        ExhibitionVENPojo exhibitionVENPojo12 = new ExhibitionVENPojo();
        exhibitionVENPojo12.setColor(11);
        exhibitionVENPojo12.setName("S4馆");
        exhibitionVENPojo12.setInfo("9520人");

        ExhibitionVENPojo exhibitionVENPojo13 = new ExhibitionVENPojo();
        exhibitionVENPojo13.setColor(12);
        exhibitionVENPojo13.setName("S5馆");
        exhibitionVENPojo13.setInfo("10236人");

        ExhibitionVENPojo exhibitionVENPojo14 = new ExhibitionVENPojo();
        exhibitionVENPojo14.setColor(13);
        exhibitionVENPojo14.setName("S6馆");
        exhibitionVENPojo14.setInfo("13058人");

        ExhibitionVENPojo exhibitionVENPojo15 = new ExhibitionVENPojo();
        exhibitionVENPojo15.setColor(14);
        exhibitionVENPojo15.setName("S7馆");
        exhibitionVENPojo15.setInfo("18302人");

        ExhibitionVENPojo exhibitionVENPojo16 = new ExhibitionVENPojo();
        exhibitionVENPojo16.setColor(15);
        exhibitionVENPojo16.setName("S8馆");
        exhibitionVENPojo16.setInfo("9503人");

        exhibitionVENPojos.add(exhibitionVENPojo1);
        exhibitionVENPojos.add(exhibitionVENPojo2);
        exhibitionVENPojos.add(exhibitionVENPojo3);
        exhibitionVENPojos.add(exhibitionVENPojo4);
        exhibitionVENPojos.add(exhibitionVENPojo5);
        exhibitionVENPojos.add(exhibitionVENPojo6);
        exhibitionVENPojos.add(exhibitionVENPojo7);
        exhibitionVENPojos.add(exhibitionVENPojo8);
        exhibitionVENPojos.add(exhibitionVENPojo9);
        exhibitionVENPojos.add(exhibitionVENPojo10);
        exhibitionVENPojos.add(exhibitionVENPojo11);
        exhibitionVENPojos.add(exhibitionVENPojo12);
        exhibitionVENPojos.add(exhibitionVENPojo13);
        exhibitionVENPojos.add(exhibitionVENPojo14);
        exhibitionVENPojos.add(exhibitionVENPojo15);
        exhibitionVENPojos.add(exhibitionVENPojo16);
        return exhibitionVENPojos;
    }

    @Override
    public void onSuccess(String result, Call<ResponseBody> call, Response<ResponseBody> response, int what) {
        super.onSuccess(result, call, response, what);
        switch (what){
            case 1:
                schedules = AppJsonUtil.getArrayList(result,Schedule.class);
                exhibitionCSHPojos = new ArrayList<>();
                ExhibitionCSHPojo exhibitionCSHPojo = null;
                for(int i = 0; i < schedules.size(); i++){
                    exhibitionCSHPojo = new ExhibitionCSHPojo();
                    exhibitionCSHPojo.setColor(i);
                    exhibitionCSHPojo.setData(schedules.get(i).getDate());
                    exhibitionCSHPojo.setInfo(schedules.get(i).getInfo());
                    exhibitionCSHPojos.add(exhibitionCSHPojo);
                }
                initRvCSH(exhibitionCSHPojos);
                break;
            case 2:
                flowrates = AppJsonUtil.getArrayList(result,Flowrate.class);
                exhibitionVENPojos = new ArrayList<>();
                ExhibitionVENPojo exhibitionVENPojo = null;
                for(int i = 0; i < flowrates.size(); i++){
                    exhibitionVENPojo = new ExhibitionVENPojo();
                    exhibitionVENPojo.setColor(i);
                    exhibitionVENPojo.setName(flowrates.get(i).getTitle());
                    exhibitionVENPojo.setInfo(flowrates.get(i).getInfo());
                    exhibitionVENPojos.add(exhibitionVENPojo);
                }
                initRvVEN(exhibitionVENPojos);
                break;
        }
    }

    @Override
    public void onFailure(String result, Call<ResponseBody> call, Response<ResponseBody> response, int what) {
        super.onFailure(result, call, response, what);
        initRvCSH(setDataCSH());
        initRvVEN(setDataVEN());
    }

    @Override
    public void onError(Call<ResponseBody> call, Throwable t, int what) {
        super.onError(call, t, what);
        initRvCSH(setDataCSH());
        initRvVEN(setDataVEN());
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
        startExhibitionActivity = 0;
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
