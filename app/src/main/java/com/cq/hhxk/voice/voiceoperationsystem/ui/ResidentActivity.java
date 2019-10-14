package com.cq.hhxk.voice.voiceoperationsystem.ui;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.adapter.ResidentAdapter;
import com.cq.hhxk.voice.voiceoperationsystem.adapter.ResidentTwoAdapter;
import com.cq.hhxk.voice.voiceoperationsystem.base.BaseAty;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.ResidentPojo;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.ResidentTwoPojo;
import com.em.baseframe.adapter.recyclerview.BaseQuickAdapter;
import com.em.baseframe.adapter.recyclerview.BaseViewHolder;
import com.em.baseframe.util.AppManger;
import com.em.baseframe.view.refresh.PtrInitHelper;
import com.em.refresh.PtrDefaultHandler;
import com.em.refresh.PtrFrameLayout;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.startResident;

/**
 * @title  居民查看交通状况页面（出行，公交车，联动打车）
 * @date   2019/07/31
 * @author enmaoFu
 */
public class ResidentActivity extends BaseAty{

    @BindView(R.id.map_transit)
    MapView mMapView;
    @BindView(R.id.refresh)
    PtrFrameLayout ptrFrameLayout;
    @BindView(R.id.recyvlerview)
    RecyclerView recyclerView;
    @BindView(R.id.recyvlerview_two)
    RecyclerView recyvlerviewTwo;
    @BindView(R.id.lin_site)
    LinearLayout linSite;
    @BindView(R.id.line_text_two)
    TextView lineTextTwo;

    /**
     * 地图对象
     */
    private AMap aMap;

    /**
     * recyclerview布局管理器
     */
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * 适配器
     */
    private ResidentAdapter residentAdapter;

    /**
     * 数据源
     */
    private List<ResidentPojo> residentPojos;

    /**
     * recyclerview布局管理器
     */
    private RecyclerView.LayoutManager mLayoutManagerTwo;

    /**
     * 适配器
     */
    private ResidentTwoAdapter residentTwoAdapter;

    /**
     * 数据源
     */
    private List<ResidentTwoPojo> residentTwoPojos;

    /**
     * 用于记录当前显示的哪个线路的所经站点
     */
    private int mPosition = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_resident;
    }

    @Override
    protected void initData() {
        AppManger.getInstance().killActivity(ComprehensiveControllerActivity.class);
        AppManger.getInstance().killActivity(ComprehensivePersonnelActivity.class);
        AppManger.getInstance().killActivity(MapActivity.class);
        AppManger.getInstance().killActivity(NavigationAvtivity.class);
        AppManger.getInstance().killActivity(ExhibitionActivity.class);
        AppManger.getInstance().killActivity(SpongeActivity.class);
        AppManger.getInstance().killActivity(ConfigureActivity.class);

        startResident = 1;
        initMap();
        initCircle();

        /**
         * 下拉刷新
         */
        PtrInitHelper.initPtr(this, ptrFrameLayout);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptrFrameLayout.refreshComplete();
            }
        });

        //实例化布局管理器
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //实例化适配器
        residentAdapter = new ResidentAdapter(R.layout.item_resident, setData());
        //设置布局管理器
        recyclerView.setLayoutManager(mLayoutManager);
        //大小不受适配器影响
        recyclerView.setHasFixedSize(true);
        //设置间隔样式
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.parseColor("#132240"))
                        .sizeResId(R.dimen.recyclerview_item_hr)
                        .build());
        //设置adapter
        recyclerView.setAdapter(residentAdapter);
        residentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener<ResidentPojo>() {
            @Override
            public void onItemClick(BaseQuickAdapter<ResidentPojo, ? extends BaseViewHolder> adapter, View view, int position) {
                if(mPosition == -1){
                    linSite.setVisibility(View.VISIBLE);
                    ResidentPojo residentPojo = residentAdapter.getItem(position);
                    residentPojo.setIsSelection(1);
                    residentAdapter.setData(position,residentPojo);
                    mPosition = position;
                    residentTwoAdapter.setNewData(setDataTwo(residentPojo.getRoadNames()));
                    lineTextTwo.setText(residentPojo.getLineName() + "所经站点");
                }else{
                    if(mPosition == position){
                        linSite.setVisibility(View.GONE);
                        ResidentPojo residentPojo = residentAdapter.getItem(position);
                        residentPojo.setIsSelection(0);
                        residentAdapter.setData(position,residentPojo);
                        mPosition = -1;
                    }else{
                        linSite.setVisibility(View.VISIBLE);
                        List<ResidentPojo> residentPojos = residentAdapter.getData();
                        for(ResidentPojo residentPojo:residentPojos){
                            residentPojo.setIsSelection(0);
                        }
                        residentPojos.get(position).setIsSelection(1);
                        residentAdapter.setNewData(residentPojos);
                        mPosition = position;
                        residentTwoAdapter.setNewData(setDataTwo(residentAdapter.getItem(position).getRoadNames()));
                        lineTextTwo.setText(residentAdapter.getItem(position).getLineName() + "所经站点");
                    }
                }

            }
        });


        //实例化布局管理器
        mLayoutManagerTwo = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //实例化适配器
        residentTwoAdapter = new ResidentTwoAdapter(R.layout.item_two_resident, new ArrayList<ResidentTwoPojo>());
        //设置布局管理器
        recyvlerviewTwo.setLayoutManager(mLayoutManagerTwo);
        //大小不受适配器影响
        recyvlerviewTwo.setHasFixedSize(true);
        //设置间隔样式
        recyvlerviewTwo.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.parseColor("#132240"))
                        .sizeResId(R.dimen.recyclerview_item_hr)
                        .build());
        //设置adapter
        recyvlerviewTwo.setAdapter(residentTwoAdapter);

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
                .decodeResource(getResources(),R.drawable.marker)));
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
     * 绘制圆
     */
    public void initCircle(){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(new LatLng(29.718435,106.542036));
        circleOptions.radius(600);
        circleOptions.fillColor(Color.parseColor("#50D81E06"));
        circleOptions.strokeColor(Color.parseColor("#D81E06"));
        circleOptions.strokeWidth(1);
        aMap.addCircle(circleOptions);
    }

    public void s(){

    }

    public List<ResidentPojo> setData(){
        residentPojos = new ArrayList<>();
        ResidentPojo residentPojo1 = new ResidentPojo();
        List<String> roadNames1 = new ArrayList<>();
        roadNames1.add("鸳鸯站");
        roadNames1.add("轨道鸳鸯站");
        roadNames1.add("园博园东站");
        roadNames1.add("园博园正门站");
        roadNames1.add("博览中心南站");
        roadNames1.add("博览中心广场站");
        roadNames1.add("博览中心北站");
        residentPojo1.setLineName("635路");
        residentPojo1.setStartName("鸳鸯站");
        residentPojo1.setEndName("博览中心北站");
        residentPojo1.setRoadNames(roadNames1);

        ResidentPojo residentPojo2 = new ResidentPojo();
        List<String> roadNames2 = new ArrayList<>();
        roadNames2.add("仙桃数据谷站");
        roadNames2.add("新悦佳苑站");
        roadNames2.add("阳光路口西站");
        roadNames2.add("博览中心北站");
        roadNames2.add("博览中心广场站");
        residentPojo2.setLineName("677路");
        residentPojo2.setStartName("仙桃数据谷站");
        residentPojo2.setEndName("博览中心广场站");
        residentPojo2.setRoadNames(roadNames2);

        ResidentPojo residentPojo3 = new ResidentPojo();
        List<String> roadNames3 = new ArrayList<>();
        roadNames3.add("两路城南站");
        roadNames3.add("翠湖路四巷站");
        roadNames3.add("轨道碧津站");
        roadNames3.add("金港国际站");
        roadNames3.add("渝航园站");
        roadNames3.add("晚晴站");
        roadNames3.add("开元站");
        roadNames3.add("渝北汉渝路站");
        roadNames3.add("石油基地站");
        roadNames3.add("渝航商场站");
        roadNames3.add("天灯堡站");
        roadNames3.add("观音岩村站");
        roadNames3.add("渝北西区站");
        roadNames3.add("同茂大道东段站");
        roadNames3.add("同茂大道天普站");
        roadNames3.add("中央公园东站");
        roadNames3.add("中央公园西站");
        roadNames3.add("轨道中央公园西站");
        roadNames3.add("博览中心北站");
        residentPojo3.setLineName("685路");
        residentPojo3.setStartName("两路城南站");
        residentPojo3.setEndName("博览中心北站");
        residentPojo3.setRoadNames(roadNames3);

        ResidentPojo residentPojo4 = new ResidentPojo();
        List<String> roadNames4 = new ArrayList<>();
        roadNames4.add("万寿福居站");
        roadNames4.add("新湾站");
        roadNames4.add("进士站");
        roadNames4.add("中林湾站");
        roadNames4.add("和源家园站");
        roadNames4.add("思源站");
        roadNames4.add("沙湾站");
        roadNames4.add("东岳站");
        roadNames4.add("太山站");
        roadNames4.add("马鞍山站");
        roadNames4.add("金科岭上站");
        roadNames4.add("颜家湾站");
        roadNames4.add("悦来小学站");
        roadNames4.add("博览中心北站");
        roadNames4.add("博览中心广场站");
        roadNames4.add("博览中心南站");
        roadNames4.add("轨道高义口站");
        roadNames4.add("园博园正门站");
        roadNames4.add("园博园东站");
        residentPojo4.setLineName("965路");
        residentPojo4.setStartName("万寿福居站");
        residentPojo4.setEndName("园博园东站");
        residentPojo4.setRoadNames(roadNames2);

        ResidentPojo residentPojo5 = new ResidentPojo();
        List<String> roadNames5 = new ArrayList<>();
        roadNames5.add("悦来站");
        roadNames5.add("博览中心北站");
        roadNames5.add("博览中心广场站");
        roadNames5.add("博览中心南站");
        roadNames5.add("重庆悦来会展公园站");
        roadNames5.add("花朝小区站");
        residentPojo5.setLineName("635路(区间)");
        residentPojo5.setStartName("悦来站");
        residentPojo5.setEndName("花朝小区站");
        residentPojo5.setRoadNames(roadNames5);

        ResidentPojo residentPojo6 = new ResidentPojo();
        List<String> roadNames6 = new ArrayList<>();
        roadNames6.add("悦来站");
        roadNames6.add("博览中心北站");
        roadNames6.add("博览中心广场站");
        roadNames6.add("博览中心南站");
        roadNames6.add("轨道高义口站");
        roadNames6.add("嘉悦大桥站");
        roadNames6.add("金科城站");
        roadNames6.add("蔡家岗立交站");
        roadNames6.add("同熙路后段站");
        roadNames6.add("同熙路中段站");
        roadNames6.add("张家桥站");
        roadNames6.add("嘉德大道1站");
        roadNames6.add("蔡家管委会站");
        roadNames6.add("同兴工业园站");
        residentPojo6.setLineName("572路(定班车)");
        residentPojo6.setStartName("博览中心北站");
        residentPojo6.setEndName("同兴工业园站");
        residentPojo6.setRoadNames(roadNames6);

        residentPojos.add(residentPojo1);
        residentPojos.add(residentPojo2);
        residentPojos.add(residentPojo3);
        residentPojos.add(residentPojo4);
        residentPojos.add(residentPojo5);
        residentPojos.add(residentPojo6);
        return residentPojos;
    }

    public List<ResidentTwoPojo> setDataTwo(List<String> strings){
        residentTwoPojos = new ArrayList<>();
        ResidentTwoPojo residentTwoPojo = null;
        for(String str:strings){
            residentTwoPojo = new ResidentTwoPojo();
            residentTwoPojo.setName(str);
            residentTwoPojos.add(residentTwoPojo);
        }
        return residentTwoPojos;
    }

    @OnClick({R.id.back_re,R.id.location_re})
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
        startResident = 0;
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
