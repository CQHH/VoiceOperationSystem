package com.cq.hhxk.voice.voiceoperationsystem.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.base.BaseAty;
import com.cq.hhxk.voice.voiceoperationsystem.dao.News;
import com.cq.hhxk.voice.voiceoperationsystem.http.HttpInterface;
import com.cq.hhxk.voice.voiceoperationsystem.util.command.CommandUtil;
import com.cq.hhxk.voice.voiceoperationsystem.util.navi.Navigation;
import com.cq.hhxk.voice.voiceoperationsystem.view.DialogNaviEndView;
import com.cq.hhxk.voice.voiceoperationsystem.view.DialogPatrolEndView;
import com.em.baseframe.util.AppJsonUtil;
import com.em.baseframe.util.AppManger;
import com.em.baseframe.util.RetrofitUtils;
import com.orhanobut.logger.Logger;
import com.superluo.textbannerlibrary.TextBannerView;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.startNaviType;

/**
 * @title  导航页面（访客直接导航，工作人员巡检路线导航）
 * @date   2019/07/31
 * @author enmaoFu
 */
public class NavigationAvtivity extends BaseAty implements AMapNaviListener,AMapNaviViewListener{

    @BindView(R.id.navi_view)
    AMapNaviView mAMapNaviView;
    @BindView(R.id.navi_bitmap)
    ImageView naviBitmap;
    @BindView(R.id.retain_distance)
    TextView retainDistance;
    @BindView(R.id.road_name)
    TextView roadName;
    @BindView(R.id.surplus_tiem)
    TextView surplusTiem;
    @BindView(R.id.surplus_distance)
    TextView surplusDistance;
    @BindView(R.id.tv_banner)
    TextBannerView textBannerView;
    @BindView(R.id.top_msg)
    RelativeLayout topMsg;
    @BindView(R.id.bom_re)
    RelativeLayout bomRe;
    @BindView(R.id.path_one_one)
    TextView pathOneOne;
    @BindView(R.id.path_one_two)
    TextView pathOneTwo;
    @BindView(R.id.path_one_three)
    TextView pathOneThree;
    @BindView(R.id.path_one_four)
    TextView pathOneFour;
    @BindView(R.id.path_two_one)
    TextView pathTwoOne;
    @BindView(R.id.path_two_two)
    TextView pathTwoTwo;
    @BindView(R.id.path_two_three)
    TextView pathTwoThree;
    @BindView(R.id.path_two_four)
    TextView pathTwoFour;
    @BindView(R.id.path_three_one)
    TextView pathThreeOne;
    @BindView(R.id.path_three_two)
    TextView pathThreeTwo;
    @BindView(R.id.path_three_three)
    TextView pathThreeThree;
    @BindView(R.id.path_three_four)
    TextView pathThreeFour;

    /**
     * 导航结束弹出框
     */
    private DialogNaviEndView dialogNaviEndView;

    /**
     * 巡检结束弹出框
     */
    private DialogPatrolEndView dialogPatrolEndView;

    /**
     * 地图控制类
     */
    private AMap aMap;

    /**
     * 导航视图参数类
     */
    private AMapNaviViewOptions aMapNaviViewOptions;

    /**
     * 线路
     */
    private int pathNumber = 1;

    //private int type = 2;

    private List<News> news;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAMapNaviView.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_navigation;
    }

    @Override
    protected void initData() {

        AppManger.getInstance().killActivity(ComprehensiveControllerActivity.class);
        AppManger.getInstance().killActivity(ComprehensivePersonnelActivity.class);
        AppManger.getInstance().killActivity(MapActivity.class);
        AppManger.getInstance().killActivity(ResidentActivity.class);
        AppManger.getInstance().killActivity(ExhibitionActivity.class);
        AppManger.getInstance().killActivity(SpongeActivity.class);
        AppManger.getInstance().killActivity(ConfigureActivity.class);

        aMap = mAMapNaviView.getMap();
        Navigation.initNavi(getApplicationContext(),this);
        mAMapNaviView.setAMapNaviViewListener(this);

        switch (startNaviType){
            case 0:
                break;
            case 1:
                bomRe.setVisibility(View.VISIBLE);
                pathOneOne.setTextColor(Color.parseColor("#297EE0"));
                pathOneTwo.setTextColor(Color.parseColor("#297EE0"));
                pathOneThree.setTextColor(Color.parseColor("#297EE0"));
                pathOneFour.setTextColor(Color.parseColor("#297EE0"));

                pathTwoOne.setTextColor(Color.parseColor("#ffffff"));
                pathTwoTwo.setTextColor(Color.parseColor("#ffffff"));
                pathTwoThree.setTextColor(Color.parseColor("#ffffff"));
                pathTwoFour.setTextColor(Color.parseColor("#ffffff"));

                pathThreeOne.setTextColor(Color.parseColor("#ffffff"));
                pathThreeTwo.setTextColor(Color.parseColor("#ffffff"));
                pathThreeThree.setTextColor(Color.parseColor("#ffffff"));
                pathThreeFour.setTextColor(Color.parseColor("#ffffff"));
                break;
        }

    }

    @Override
    protected void requestData() {

    }

    public void initNaiv(boolean isAutoLockCar,boolean isLayoutVisible,boolean isModeCrossDisplayShow){
        //获取导航视图参数类
        aMapNaviViewOptions = mAMapNaviView.getViewOptions();
        //设置6秒后自动锁车
        aMapNaviViewOptions.setAutoLockCar(isAutoLockCar);
        //隐藏高德默认的布局
        aMapNaviViewOptions.setLayoutVisible(isLayoutVisible);
        //是否显示路口放大图
        aMapNaviViewOptions.setModeCrossDisplayShow(isModeCrossDisplayShow);
        //是否显示光柱图
        aMapNaviViewOptions.setTrafficBarEnabled(false);
        //路线是否自动置灰
        aMapNaviViewOptions.setAfterRouteAutoGray(true);
        //设置是否黑夜模式
        aMapNaviViewOptions.setNaviNight(true);
        //设置是否显示实时交通信息
        aMapNaviViewOptions.setTrafficLine(false);
        //设置导航视图参数类
        mAMapNaviView.setViewOptions(aMapNaviViewOptions);
    }

    @OnClick({R.id.path_one,R.id.path_two,R.id.path_three,R.id.start_text})
    @Override
    public void btnClick(View view) {
        List<NaviLatLng> mWayPointList = null;
        NaviLatLng naviLatLngVisitor1 = null;
        NaviLatLng naviLatLngVisitor2 = null;
        NaviLatLng naviLatLngVisitor3 = null;
        switch (view.getId()){
            case R.id.path_one:
                //途径点列表（用于到达终点标识）
                pathNumber = 1;

                pathOneOne.setTextColor(Color.parseColor("#297EE0"));
                pathOneTwo.setTextColor(Color.parseColor("#297EE0"));
                pathOneThree.setTextColor(Color.parseColor("#297EE0"));
                pathOneFour.setTextColor(Color.parseColor("#297EE0"));

                pathTwoOne.setTextColor(Color.parseColor("#ffffff"));
                pathTwoTwo.setTextColor(Color.parseColor("#ffffff"));
                pathTwoThree.setTextColor(Color.parseColor("#ffffff"));
                pathTwoFour.setTextColor(Color.parseColor("#ffffff"));

                pathThreeOne.setTextColor(Color.parseColor("#ffffff"));
                pathThreeTwo.setTextColor(Color.parseColor("#ffffff"));
                pathThreeThree.setTextColor(Color.parseColor("#ffffff"));
                pathThreeFour.setTextColor(Color.parseColor("#ffffff"));

                mWayPointList = new ArrayList<>();
                naviLatLngVisitor1 = new NaviLatLng(29.710552, 106.543725);
                naviLatLngVisitor2 = new NaviLatLng(29.694207, 106.544918);
                naviLatLngVisitor3 = new NaviLatLng(29.674622, 106.563784);
                mWayPointList.add(naviLatLngVisitor1);
                mWayPointList.add(naviLatLngVisitor2);
                mWayPointList.add(naviLatLngVisitor3);
                personnelNavi(mWayPointList);
                break;
            case R.id.path_two:
                //途径点列表（用于到达终点标识）
                pathNumber = 2;

                pathOneOne.setTextColor(Color.parseColor("#ffffff"));
                pathOneTwo.setTextColor(Color.parseColor("#ffffff"));
                pathOneThree.setTextColor(Color.parseColor("#ffffff"));
                pathOneFour.setTextColor(Color.parseColor("#ffffff"));

                pathTwoOne.setTextColor(Color.parseColor("#297EE0"));
                pathTwoTwo.setTextColor(Color.parseColor("#297EE0"));
                pathTwoThree.setTextColor(Color.parseColor("#297EE0"));
                pathTwoFour.setTextColor(Color.parseColor("#297EE0"));

                pathThreeOne.setTextColor(Color.parseColor("#ffffff"));
                pathThreeTwo.setTextColor(Color.parseColor("#ffffff"));
                pathThreeThree.setTextColor(Color.parseColor("#ffffff"));
                pathThreeFour.setTextColor(Color.parseColor("#ffffff"));

                mWayPointList = new ArrayList<NaviLatLng>();
                naviLatLngVisitor1 = new NaviLatLng(29.706629, 106.552128);
                naviLatLngVisitor2 = new NaviLatLng(29.701309, 106.552374);
                naviLatLngVisitor3 = new NaviLatLng(29.674622, 106.563784);
                mWayPointList.add(naviLatLngVisitor1);
                mWayPointList.add(naviLatLngVisitor2);
                mWayPointList.add(naviLatLngVisitor3);
                personnelNavi(mWayPointList);
                break;
            case R.id.path_three:
                //途径点列表（用于到达终点标识）
                pathNumber = 3;

                pathOneOne.setTextColor(Color.parseColor("#ffffff"));
                pathOneTwo.setTextColor(Color.parseColor("#ffffff"));
                pathOneThree.setTextColor(Color.parseColor("#ffffff"));
                pathOneFour.setTextColor(Color.parseColor("#ffffff"));

                pathTwoOne.setTextColor(Color.parseColor("#ffffff"));
                pathTwoTwo.setTextColor(Color.parseColor("#ffffff"));
                pathTwoThree.setTextColor(Color.parseColor("#ffffff"));
                pathTwoFour.setTextColor(Color.parseColor("#ffffff"));

                pathThreeOne.setTextColor(Color.parseColor("#297EE0"));
                pathThreeTwo.setTextColor(Color.parseColor("#297EE0"));
                pathThreeThree.setTextColor(Color.parseColor("#297EE0"));
                pathThreeFour.setTextColor(Color.parseColor("#297EE0"));

                mWayPointList = new ArrayList<NaviLatLng>();
                naviLatLngVisitor1 = new NaviLatLng(29.7176, 106.551703);
                naviLatLngVisitor2 = new NaviLatLng(29.700698, 106.534747);
                naviLatLngVisitor3 = new NaviLatLng(29.674622, 106.563784);
                mWayPointList.add(naviLatLngVisitor1);
                mWayPointList.add(naviLatLngVisitor2);
                mWayPointList.add(naviLatLngVisitor3);
                personnelNavi(mWayPointList);
                break;
            case R.id.start_text:
                aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                initNaiv(true,false,false);
                Navigation.mAMapNavi.startNavi(NaviType.EMULATOR);
                bomRe.setVisibility(View.GONE);
                topMsg.setVisibility(View.VISIBLE);
                List<String> list = new ArrayList<>();
                list.add("本次巡检路线：悦来集团（起点）-轨道园博园站（终点）");
                textBannerView.setDatas(list);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        Navigation.mAMapNavi.stopNavi();
        Navigation.mAMapNavi.destroy();
    }

    /**
     * 导航初始化失败时的回调函数。
     */
    @Override
    public void onInitNaviFailure() {

    }

    /**
     * 导航初始化成功时的回调函数。
     */
    @Override
    public void onInitNaviSuccess() {
        setPathPoint();
        switch (startNaviType){
            case 0:
                initNaiv(true,false,false);
                visitorNavi();
                break;
            case 1:
                initNaiv(false,false,false);
                //途径点列表（用于到达终点标识）
                List<NaviLatLng> mWayPointList = new ArrayList<NaviLatLng>();
                NaviLatLng naviLatLngVisitor1 = new NaviLatLng(29.710552, 106.543725);
                NaviLatLng naviLatLngVisitor2 = new NaviLatLng(29.694207, 106.544918);
                NaviLatLng naviLatLngVisitor3 = new NaviLatLng(29.674622, 106.563784);
                mWayPointList.add(naviLatLngVisitor1);
                mWayPointList.add(naviLatLngVisitor2);
                mWayPointList.add(naviLatLngVisitor3);
                personnelNavi(mWayPointList);
                break;
        }
    }

    /**
     * 启动导航后的回调函数。
     * @param i
     */
    @Override
    public void onStartNavi(int i) {

    }

    /**
     * 当前方路况光柱信息有更新时回调函数。
     */
    @Override
    public void onTrafficStatusUpdate() {

    }

    /**
     * 当GPS位置有更新时的回调函数。
     * @param aMapNaviLocation
     */
    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    /**
     * 导航播报信息回调函数。
     * @param i
     * @param s
     */
    @Override
    public void onGetNavigationText(int i, String s) {

    }

    /**
     * 导航播报信息回调函数。
     * @param s
     */
    @Override
    public void onGetNavigationText(String s) {

    }

    /**
     * 模拟导航停止后回调函数。
     */
    @Override
    public void onEndEmulatorNavi() {

    }

    /**
     * 到达目的地后回调函数。
     */
    @Override
    public void onArriveDestination() {
        Logger.v("到达目的地....");
    }

    /**
     * 步行或者驾车路径规划失败后的回调函数。
     * @param i
     */
    @Override
    public void onCalculateRouteFailure(int i) {

    }

    /**
     * 偏航后准备重新规划的通知回调。
     * 此方法只是通知准备重算事件，不需要在方法中触发算路逻辑。
     */
    @Override
    public void onReCalculateRouteForYaw() {

    }

    /**
     * 驾车导航时，如果前方遇到拥堵时需要重新计算路径的回调。
     */
    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    /**
     * 驾车路径导航到达某个途经点的回调函数。
     * @param i
     */
    @Override
    public void onArrivedWayPoint(int i) {
        List<String> list = new ArrayList<>();
        switch (startNaviType){
            case 0:
                switch (i){
                    case 0:
                        list.add("国博中心A停车场：50%空闲\n国博中心B停车场：40%空闲\n国博中心C停车场：16%空闲");
                        textBannerView.setDatas(list);
                        break;
                    case 1:
                        dialogNaviEndView = new DialogNaviEndView(this);
                        dialogNaviEndView.show();
                        dialogNaviEndView.setDialogAnimation();
                        dialogNaviEndView.setCancelable(false);
                        dialogNaviEndView.setCanceledOnTouchOutside(false);
                        dialogNaviEndView.setBtnOnClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        dialogNaviEndView.setOnClickOpenApp(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String getAppPackageStr = CommandUtil.getInstance().getAppPackage(getApplication(),"饿了么");
                                CommandUtil.getInstance().openApp(getApplication(),getAppPackageStr);
                                finish();
                            }
                        });
                        break;
                }
                break;
            case 1:
                switch (pathNumber){
                    case 1:
                        switch (i){
                            case 0:
                                Logger.v("线路1途径点1");
                                list.add("本次巡检路线：悦来集团（起点）-轨道园博园站（终点）\n经过途径点悦融路中段：\n当前范围水生态监测站情况：正常\n当前范围水环境监测站情况：正常");
                                textBannerView.setDatas(list);
                                drawRange(1,i);
                                break;
                            case 1:
                                Logger.v("线路1途径点2");
                                list.add("本次巡检路线：悦来集团（起点）-轨道园博园站（终点）\n经过途径点金兴大道：\n当前范围路灯情况：正常\n当前范围水生态监测站情况：正常");
                                textBannerView.setDatas(list);
                                drawRange(1,i);
                                break;
                            case 2:
                                Logger.v("线路1途径点3");
                                showPatrolEnd();
                                break;
                        }
                        break;
                    case 2:
                        switch (i){
                            case 0:
                                Logger.v("线路2途径点1");
                                list.add("本次巡检路线：悦来集团（起点）-轨道园博园站（终点）\n经过途径点沙井湾立交：\n当前范围路灯情况：正常\n当前范围报事报修：无");
                                textBannerView.setDatas(list);
                                drawRange(2,i);
                                break;
                            case 1:
                                Logger.v("线路2途径点2");
                                list.add("本次巡检路线：悦来集团（起点）-轨道园博园站（终点）\n经过途径点金山大道：\n当前范围径流监测站情况：正常\n当前范围报事报修：无");
                                textBannerView.setDatas(list);
                                drawRange(2,i);
                                break;
                            case 2:
                                Logger.v("线路2途径点3");
                                showPatrolEnd();
                                break;
                        }
                        break;
                    case 3:
                        switch (i){
                            case 0:
                                Logger.v("线路3途径点1");
                                list.add("本次巡检路线：悦来集团（起点）-轨道园博园站（终点）\n经过途径点悦城路第三个红绿灯路口：\n当前范围红绿灯情况：正常\n当前范围路灯情况：正常");
                                textBannerView.setDatas(list);
                                drawRange(3,i);
                                break;
                            case 1:
                                Logger.v("线路3途径点2");
                                list.add("本次巡检路线：悦来集团（起点）-轨道园博园站（终点）\n经过途径点张家溪大桥中段：\n当前范围路灯情况：正常\n当前范围报事报修：无");
                                textBannerView.setDatas(list);
                                drawRange(3,i);
                                break;
                            case 2:
                                Logger.v("线路3途径点3");
                                showPatrolEnd();
                                break;
                        }
                        break;
                }
                break;
        }
    }

    /**
     * 巡检结束弹出框
     */
    public void showPatrolEnd(){
        dialogPatrolEndView = new DialogPatrolEndView(this);
        dialogPatrolEndView.show();
        dialogPatrolEndView.setDialogAnimation();
        dialogPatrolEndView.setCancelable(false);
        dialogPatrolEndView.setCanceledOnTouchOutside(false);
        dialogPatrolEndView.setBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 用户手机GPS设置是否开启的回调函数。
     * @param b
     */
    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    /**
     * 导航引导信息回调 naviinfo 是导航信息类。
     * @param naviInfo
     */
    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        if(null != naviInfo.getIconBitmap()){
            Bitmap bitmap = naviInfo.getIconBitmap();
            naviBitmap.setImageBitmap(bitmap);
        }

        retainDistance.setText(naviInfo.getCurStepRetainDistance() + "米");

        if(null != naviInfo.getNextRoadName() && !"".equals(naviInfo.getNextRoadName()) && !"null".equals(naviInfo.getNextRoadName())){
            roadName.setText(naviInfo.getNextRoadName());
        }

        long minutes = (naviInfo.getPathRetainTime() % ( 60 * 60)) /60;
        long hours = (naviInfo.getPathRetainTime() % ( 60 * 60 * 24)) / (60 * 60);
        if(hours >= 1){
            surplusTiem.setText(hours + "小时" + minutes + "分钟");
        }else{
            surplusTiem.setText(minutes + "分钟");
        }

        if(naviInfo.getPathRetainDistance() > 1000){
            surplusDistance.setText(div(naviInfo.getPathRetainDistance(),1000,2)+ "公里");
        }else{
            surplusDistance.setText(naviInfo.getPathRetainDistance() + "米");
        }
    }

    /**
     * 已过时，请使用AMapNaviListener.onNaviInfoUpdate(NaviInfo)。
     * @param aMapNaviInfo
     */
    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    /**
     * 导航过程中的摄像头信息回调函数。
     * @param aMapNaviCameraInfos
     */
    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    /**
     * 导航过程中的区间测速信息回调函数。
     * @param aMapNaviCameraInfo
     * @param aMapNaviCameraInfo1
     * @param i
     */
    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    /**
     * 服务区信息回调函数。
     * @param aMapServiceAreaInfos
     */
    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    /**
     * 显示路口放大图回调(实景图)。
     * @param aMapNaviCross
     */
    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    /**
     * 关闭路口放大图回调(实景图)。
     */
    @Override
    public void hideCross() {

    }

    /**
     * 显示路口放大图回调(模型图)。
     * @param aMapModelCross
     */
    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    /**
     * 关闭路口放大图回调(模型图)。
     */
    @Override
    public void hideModeCross() {

    }

    /**
     * 已过时。 建议使用AMapNaviListener.showLaneInfo(AMapLaneInfo) 方法替换
     * 显示道路信息回调。
     * @param aMapLaneInfos
     * @param bytes
     * @param bytes1
     */
    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    /**
     * 显示道路信息回调。
     * @param aMapLaneInfo
     */
    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    /**
     * 关闭道路信息回调。
     */
    @Override
    public void hideLaneInfo() {

    }

    /**
     * 已过时。 该方法在6.1.0版本废弃，但是还会正常回调，建议使用AMapNaviListener.onCalculateRouteSuccess(AMapCalcRouteResult) 方法替换。
     * 算路成功回调
     * @param ints
     */
    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        if(startNaviType == 0){
            topMsg.setVisibility(View.VISIBLE);
            Navigation.mAMapNavi.startNavi(NaviType.EMULATOR);
            doHttp(RetrofitUtils.createApi(HttpInterface.class).news(),1);

        }
    }

    /**
     * 已过时。
     * 请使用ParallelRoadListener.notifyParallelRoad(AMapNaviParallelRoadStatus) 通知当前是否显示平行路切换。
     * @param i
     */
    @Override
    public void notifyParallelRoad(int i) {

    }

    /**
     * 已过时。
     * 请使用AMapNaviListener.OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[])。
     * @param aMapNaviTrafficFacilityInfo
     */
    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    /**
     * 巡航模式（无路线规划）下，道路设施信息更新回调。
     * @param aMapNaviTrafficFacilityInfos
     */
    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    /**
     * 已过时。
     * 请使用AMapNaviListener.OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[])。
     * @param trafficFacilityInfo
     */
    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    /**
     * 航模式（无路线规划）下，统计信息更新回调 连续5个点大于15km/h后开始回调。
     * @param aimLessModeStat
     */
    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    /**
     * 巡航模式（无路线规划）下，统计信息更新回调 当拥堵长度大于500米且拥堵时间大于5分钟时回调. 当前方无拥堵信息时，回调的AimLessModeCongestionInfo对象属性值都为空。
     * @param aimLessModeCongestionInfo
     */
    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    /**
     * 回调各种类型的提示音，类似高德导航"叮".。
     * @param i
     */
    @Override
    public void onPlayRing(int i) {

    }

    /**
     * 路线规划成功回调，包括算路、导航中偏航、用户改变算路策略、行程点等触发的重算，
     * 具体算路结果可以通过AMapCalcRouteResult获取 可以通过CalcRouteResult获取算路错误码、算路类型以及路线id。
     * @param aMapCalcRouteResult
     */
    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
    }

    /**
     * 路线规划失败回调，包括算路、导航中偏航、用户改变算路策略、行程点等触发的重算，
     * 具体算路结果可以通过AMapCalcRouteResult获取 可以通过CalcRouteResult获取算路错误码、算路类型以及路线id
     * @param aMapCalcRouteResult
     */
    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    /**
     * 导航过程中道路信息通知
     * 导航过程中针对拥堵区域、限行区域、禁行区域、道路封闭等情况的躲避通知。
     * 通知和避让信息结果可以通过AMapNaviRouteNotifyData获取
     * @param aMapNaviRouteNotifyData
     */
    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }

    /**
     * 界面右下角功能设置按钮的回调接口。
     */
    @Override
    public void onNaviSetting() {

    }

    /**
     * 导航页面左下角返回按钮点击后弹出的『退出导航对话框』中选择『确定』后的回调接口。
     */
    @Override
    public void onNaviCancel() {

    }

    /**
     * 导航页面左下角返回按钮的回调接口 false-由SDK主动弹出『退出导航』对话框，true-SDK不主动弹出『退出导航对话框』，由用户自定义
     * @return
     */
    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    /**
     * 导航界面地图状态的回调。
     * @param i
     */
    @Override
    public void onNaviMapMode(int i) {

    }

    /**
     * 界面左上角转向操作的点击回调。
     */
    @Override
    public void onNaviTurnClick() {

    }

    /**
     * 界面下一道路名称的点击回调。
     */
    @Override
    public void onNextRoadClick() {

    }

    /**
     * 界面全览按钮的点击回调。
     */
    @Override
    public void onScanViewButtonClick() {

    }

    /**
     * 是否锁定地图的回调。
     * @param b
     */
    @Override
    public void onLockMap(boolean b) {

    }

    /**
     * 导航view加载完成回调。
     */
    @Override
    public void onNaviViewLoaded() {

    }

    /**
     * AMapNaviView地图白天黑夜模式切换回调。
     * @param i
     */
    @Override
    public void onMapTypeChanged(int i) {

    }

    /**
     * NaviView展示模式变化回调,具体类型可参考AMapNaviViewShowMode。
     * @param i
     */
    @Override
    public void onNaviViewShowMode(int i) {

    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    private double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 访客
     */
    public void visitorNavi(){
        /**
         * 设置途径点图标
         */
        //获取导航视图参数类
        AMapNaviViewOptions aMapNaviViewOptions = mAMapNaviView.getViewOptions();
        //获取图片资源
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.after);
        //设置途径点图标
        aMapNaviViewOptions.setWayPointBitmap(bitmap);
        //设置导航视图参数类
        mAMapNaviView.setViewOptions(aMapNaviViewOptions);

        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = Navigation.mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //存储算路起点的列表
        List<NaviLatLng> sList = new ArrayList<NaviLatLng>();

        //存储算路终点的列表
        List<NaviLatLng> eList = new ArrayList<NaviLatLng>();

        //途径点列表（用于到达终点标识）
        List<NaviLatLng> mWayPointList = new ArrayList<NaviLatLng>();

        // 驾车算路
        NaviLatLng mStartLatlngVisitor = new NaviLatLng(29.714736, 106.547938);
        NaviLatLng mEndLatlngVisitor = new NaviLatLng(29.716516, 106.545523);
        sList.add(mStartLatlngVisitor);
        eList.add(mEndLatlngVisitor);
        NaviLatLng naviLatLngVisitor1 = new NaviLatLng(29.712268, 106.54125);
        NaviLatLng naviLatLngVisitor2 = new NaviLatLng(29.716516, 106.545523);
        mWayPointList.add(naviLatLngVisitor1);
        mWayPointList.add(naviLatLngVisitor2);
        Navigation.mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);

        Logger.v("导航初始化成功");
    }

    /**
     * 工作人员
     * @param mWayPointList 途径点
     */
    public void personnelNavi(List<NaviLatLng> mWayPointList){

        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));

        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = Navigation.mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //存储算路起点的列表
        List<NaviLatLng> sList = new ArrayList<NaviLatLng>();

        //存储算路终点的列表
        List<NaviLatLng> eList = new ArrayList<NaviLatLng>();

        // 驾车算路
        NaviLatLng mStartLatlngVisitor = new NaviLatLng(29.714736, 106.547938);
        NaviLatLng mEndLatlngVisitor = new NaviLatLng(29.674622, 106.563784);
        sList.add(mStartLatlngVisitor);
        eList.add(mEndLatlngVisitor);
        Navigation.mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
    }

    /**
     * 设置途径点图标
     */
    public void setPathPoint(){
        //获取导航视图参数类
        AMapNaviViewOptions aMapNaviViewOptions = mAMapNaviView.getViewOptions();
        //获取图片资源
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.after);
        //设置途径点图标
        aMapNaviViewOptions.setWayPointBitmap(bitmap);
        //设置导航视图参数类
        mAMapNaviView.setViewOptions(aMapNaviViewOptions);
    }

    /**
     * 绘制途径点及时显示一定范围内的信息
     * @param pathNumber 线路
     * @param dot 第几个途径点
     */
    public void drawRange(int pathNumber,int dot){
        switch (pathNumber){
            case 1:
                switch (dot){
                    case 0:
                        CircleOptions circleOptions10 = new CircleOptions();
                        circleOptions10.center(new LatLng(29.710552, 106.543725));
                        circleOptions10.radius(100);
                        circleOptions10.fillColor(Color.parseColor("#50D81E06"));
                        circleOptions10.strokeColor(Color.parseColor("#D81E06"));
                        circleOptions10.strokeWidth(1);

                        MarkerOptions markerOption10 = new MarkerOptions();
                        markerOption10.position(new LatLng(29.710552, 106.543725));
                        markerOption10.draggable(false);
                        markerOption10.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.marker)));

                        MarkerOptions markerOption101 = new MarkerOptions();
                        markerOption101.position(new LatLng(29.710747, 106.543532));
                        markerOption101.draggable(false);
                        markerOption101.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shuishengtai)));

                        MarkerOptions markerOption102 = new MarkerOptions();
                        markerOption102.position(new LatLng(29.710384, 106.543559));
                        markerOption102.draggable(false);
                        markerOption102.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shuihuanj)));

                        MarkerOptions markerOption103 = new MarkerOptions();
                        markerOption103.position(new LatLng(29.710007, 106.5438));
                        markerOption103.draggable(false);
                        markerOption103.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shuishengtai)));

                        MarkerOptions markerOption104= new MarkerOptions();
                        markerOption104.position(new LatLng(29.710855, 106.544245));
                        markerOption104.draggable(false);
                        markerOption104.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ludeng)));

                        aMap.addCircle(circleOptions10);
                        aMap.addMarker(markerOption10);
                        aMap.addMarker(markerOption101);
                        aMap.addMarker(markerOption102);
                        aMap.addMarker(markerOption103);
                        aMap.addMarker(markerOption104);
                        break;
                    case 1:
                        CircleOptions circleOptions11 = new CircleOptions();
                        circleOptions11.center(new LatLng(29.694207, 106.544918));
                        circleOptions11.radius(100);
                        circleOptions11.fillColor(Color.parseColor("#50D81E06"));
                        circleOptions11.strokeColor(Color.parseColor("#D81E06"));
                        circleOptions11.strokeWidth(1);

                        MarkerOptions markerOption11 = new MarkerOptions();
                        markerOption11.position(new LatLng(29.694207, 106.544918));
                        markerOption11.draggable(false);
                        markerOption11.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.marker)));

                        MarkerOptions markerOption111 = new MarkerOptions();
                        markerOption111.position(new LatLng(29.694499, 106.545041));
                        markerOption111.draggable(false);
                        markerOption111.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ludeng)));

                        MarkerOptions markerOption112 = new MarkerOptions();
                        markerOption112.position(new LatLng(29.694447, 106.544087));
                        markerOption112.draggable(false);
                        markerOption112.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shuishengtai)));

                        MarkerOptions markerOption113 = new MarkerOptions();
                        markerOption113.position(new LatLng(29.693971, 106.545926));
                        markerOption113.draggable(false);
                        markerOption113.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shuihuanj)));

                        MarkerOptions markerOption114 = new MarkerOptions();
                        markerOption114.position(new LatLng(29.694596, 106.545186));
                        markerOption114.draggable(false);
                        markerOption114.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shuiwei)));

                        aMap.addCircle(circleOptions11);
                        aMap.addMarker(markerOption11);
                        aMap.addMarker(markerOption111);
                        aMap.addMarker(markerOption112);
                        aMap.addMarker(markerOption113);
                        aMap.addMarker(markerOption114);
                        break;
                }
                break;
            case 2:
                switch (dot){
                    case 0:
                        CircleOptions circleOptions20 = new CircleOptions();
                        circleOptions20.center(new LatLng(29.706629, 106.552128));
                        circleOptions20.radius(100);
                        circleOptions20.fillColor(Color.parseColor("#50D81E06"));
                        circleOptions20.strokeColor(Color.parseColor("#D81E06"));
                        circleOptions20.strokeWidth(1);

                        MarkerOptions markerOption20 = new MarkerOptions();
                        markerOption20.position(new LatLng(29.706629, 106.552128));
                        markerOption20.draggable(false);
                        markerOption20.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.marker)));

                        MarkerOptions markerOption201 = new MarkerOptions();
                        markerOption201.position(new LatLng(29.706854, 106.551614));
                        markerOption201.draggable(false);
                        markerOption201.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ludeng)));

                        MarkerOptions markerOption202 = new MarkerOptions();
                        markerOption202.position(new LatLng(29.706271, 106.552456));
                        markerOption202.draggable(false);
                        markerOption202.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ludeng)));

                        MarkerOptions markerOption203 = new MarkerOptions();
                        markerOption203.position(new LatLng(29.70633, 106.551943));
                        markerOption203.draggable(false);
                        markerOption203.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shuiwei)));

                        MarkerOptions markerOption204 = new MarkerOptions();
                        markerOption204.position(new LatLng(29.706271, 106.552456));
                        markerOption204.draggable(false);
                        markerOption204.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shuishengtai)));

                        aMap.addCircle(circleOptions20);
                        aMap.addMarker(markerOption20);
                        aMap.addMarker(markerOption201);
                        aMap.addMarker(markerOption202);
                        aMap.addMarker(markerOption203);
                        aMap.addMarker(markerOption204);
                        break;
                    case 1:
                        CircleOptions circleOptions21 = new CircleOptions();
                        circleOptions21.center(new LatLng(29.701309, 106.552374));
                        circleOptions21.radius(100);
                        circleOptions21.fillColor(Color.parseColor("#50D81E06"));
                        circleOptions21.strokeColor(Color.parseColor("#D81E06"));
                        circleOptions21.strokeWidth(1);

                        MarkerOptions markerOption21 = new MarkerOptions();
                        markerOption21.position(new LatLng(29.701309, 106.552374));
                        markerOption21.draggable(false);
                        markerOption21.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.marker)));

                        MarkerOptions markerOption211 = new MarkerOptions();
                        markerOption211.position(new LatLng(29.701691, 106.552519));
                        markerOption211.draggable(false);
                        markerOption211.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.jingliu)));

                        MarkerOptions markerOption212 = new MarkerOptions();
                        markerOption212.position(new LatLng(29.700857, 106.552218));
                        markerOption212.draggable(false);
                        markerOption212.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.jingliu)));

                        MarkerOptions markerOption213 = new MarkerOptions();
                        markerOption213.position(new LatLng(29.701537, 106.552648));
                        markerOption213.draggable(false);
                        markerOption213.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ludeng)));

                        MarkerOptions markerOption214 = new MarkerOptions();
                        markerOption214.position(new LatLng(29.700792, 106.552203));
                        markerOption214.draggable(false);
                        markerOption214.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ludeng)));

                        aMap.addCircle(circleOptions21);
                        aMap.addMarker(markerOption21);
                        aMap.addMarker(markerOption211);
                        aMap.addMarker(markerOption212);
                        aMap.addMarker(markerOption213);
                        aMap.addMarker(markerOption214);
                        break;
                }
                break;
            case 3:
                switch (dot){
                    case 0:
                        CircleOptions circleOptions30 = new CircleOptions();
                        circleOptions30.center(new LatLng(29.7176, 106.551703));
                        circleOptions30.radius(100);
                        circleOptions30.fillColor(Color.parseColor("#50D81E06"));
                        circleOptions30.strokeColor(Color.parseColor("#D81E06"));
                        circleOptions30.strokeWidth(1);

                        MarkerOptions markerOption30 = new MarkerOptions();
                        markerOption30.position(new LatLng(29.7176, 106.551703));
                        markerOption30.draggable(false);
                        markerOption30.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.marker)));

                        MarkerOptions markerOption301 = new MarkerOptions();
                        markerOption301.position(new LatLng(29.717702, 106.551617));
                        markerOption301.draggable(false);
                        markerOption301.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.hld)));

                        MarkerOptions markerOption302 = new MarkerOptions();
                        markerOption302.position(new LatLng(29.717973, 106.551692));
                        markerOption302.draggable(false);
                        markerOption302.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ludeng)));

                        MarkerOptions markerOption303 = new MarkerOptions();
                        markerOption303.position(new LatLng(29.717532, 106.552168));
                        markerOption303.draggable(false);
                        markerOption303.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shuishengtai)));

                        MarkerOptions markerOption304 = new MarkerOptions();
                        markerOption304.position(new LatLng(29.718091, 106.551954));
                        markerOption304.draggable(false);
                        markerOption304.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.jingliu)));

                        aMap.addCircle(circleOptions30);
                        aMap.addMarker(markerOption30);
                        aMap.addMarker(markerOption301);
                        aMap.addMarker(markerOption302);
                        aMap.addMarker(markerOption303);
                        aMap.addMarker(markerOption304);
                        break;
                    case 1:
                        CircleOptions circleOptions31 = new CircleOptions();
                        circleOptions31.center(new LatLng(29.700698, 106.534747));
                        circleOptions31.radius(100);
                        circleOptions31.fillColor(Color.parseColor("#50D81E06"));
                        circleOptions31.strokeColor(Color.parseColor("#D81E06"));
                        circleOptions31.strokeWidth(1);

                        MarkerOptions markerOption31 = new MarkerOptions();
                        markerOption31.position(new LatLng(29.700698, 106.534747));
                        markerOption31.draggable(false);
                        markerOption31.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.marker)));

                        MarkerOptions markerOption311 = new MarkerOptions();
                        markerOption311.position(new LatLng(29.700526, 106.534988));
                        markerOption311.draggable(false);
                        markerOption311.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ludeng)));

                        MarkerOptions markerOption312 = new MarkerOptions();
                        markerOption312.position(new LatLng(29.701015, 106.534231));
                        markerOption312.draggable(false);
                        markerOption312.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ludeng)));

                        MarkerOptions markerOption313 = new MarkerOptions();
                        markerOption313.position(new LatLng(29.70126, 106.534738));
                        markerOption313.draggable(false);
                        markerOption313.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shuishengtai)));

                        MarkerOptions markerOption314 = new MarkerOptions();
                        markerOption314.position(new LatLng(29.700649, 106.535377));
                        markerOption314.draggable(false);
                        markerOption314.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.jingliu)));

                        aMap.addCircle(circleOptions31);
                        aMap.addMarker(markerOption31);
                        aMap.addMarker(markerOption311);
                        aMap.addMarker(markerOption312);
                        aMap.addMarker(markerOption313);
                        aMap.addMarker(markerOption314);
                        break;
                }
                break;
        }

    }

    @Override
    public void onSuccess(String result, Call<ResponseBody> call, Response<ResponseBody> response, int what) {
        super.onSuccess(result, call, response, what);
        switch (what){
            case 1:
                news = AppJsonUtil.getArrayList(result,News.class);
                List<String> list = new ArrayList<>();
                list.add(news.get(0).getText() + "\n"+ news.get(1).getText() + "\n" + news.get(2).getText() + "\n" + news.get(3).getText());
                textBannerView.setDatas(list);
                break;
        }
    }

    @Override
    public void onFailure(String result, Call<ResponseBody> call, Response<ResponseBody> response, int what) {
        super.onFailure(result, call, response, what);
        List<String> list = new ArrayList<>();
        list.add("智博会新增7个临时停车场 4000余停车位免费向市民开放\n智能制造高峰论坛27日举行\n市经济信息委青年学生志愿者助力智博会\n科技感十足！云从科技将携20余款人工智能产品亮相");
        textBannerView.setDatas(list);
    }

    @Override
    public void onError(Call<ResponseBody> call, Throwable t, int what) {
        super.onError(call, t, what);
        List<String> list = new ArrayList<>();
        list.add("智博会新增7个临时停车场 4000余停车位免费向市民开放\n智能制造高峰论坛27日举行\n市经济信息委青年学生志愿者助力智博会\n科技感十足！云从科技将携20余款人工智能产品亮相");
        textBannerView.setDatas(list);
    }
}
