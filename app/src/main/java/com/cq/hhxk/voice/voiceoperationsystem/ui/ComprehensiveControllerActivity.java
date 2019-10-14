package com.cq.hhxk.voice.voiceoperationsystem.ui;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.adapter.AutoPollControllerAdapter;
import com.cq.hhxk.voice.voiceoperationsystem.base.BaseAty;
import com.cq.hhxk.voice.voiceoperationsystem.constant.VoiceServiceConstant;
import com.cq.hhxk.voice.voiceoperationsystem.dao.Flowrate;
import com.cq.hhxk.voice.voiceoperationsystem.dao.PersonNum;
import com.cq.hhxk.voice.voiceoperationsystem.event.VoiceServiceEvent;
import com.cq.hhxk.voice.voiceoperationsystem.http.HttpInterface;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.AutoPollControllerPojo;
import com.cq.hhxk.voice.voiceoperationsystem.view.AutoPollRecyclerView;
import com.cq.hhxk.voice.voiceoperationsystem.view.scrolnumber.MultiScrollNumber;
import com.em.baseframe.util.AppJsonUtil;
import com.em.baseframe.util.AppManger;
import com.em.baseframe.util.RetrofitUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.isWaring;
import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.startComprehensiveControllerActivity;

/**
 * @title  管理者综合报表
 * @date   2019/07/31
 * @author enmaoFu
 */
public class ComprehensiveControllerActivity extends BaseAty implements VoiceServiceConstant {

    @BindView(R.id.controller_traffic_situation_pie)
    PieChart controllerTrafficSituationPie;
    @BindView(R.id.left_lin)
    LinearLayout leftLin;
    @BindView(R.id.right_lin)
    LinearLayout rightLin;
    @BindView(R.id.re_top)
    RelativeLayout reTop;
    @BindView(R.id.warning_number)
    TextView warningNumber;
    @BindView(R.id.scroll_number)
    MultiScrollNumber scrollNumber;
    @BindView(R.id.scroll_numberl)
    MultiScrollNumber scrollNumberl1;
    @BindView(R.id.scroll_number2)
    MultiScrollNumber scrollNumberl2;
    @BindView(R.id.recyvlerview)
    AutoPollRecyclerView recyclerView;

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
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * 数据源
     */
    private List<AutoPollControllerPojo> autoPollPojos;

    private List<PersonNum> personNums;

    private List<Flowrate> flowrates;

    /**
     * 模拟预警显示隐藏判断依据
     */
    private int warningNumberInt = 0;
    private CountDownTimer countDownTimer = new CountDownTimer(6000, 500) {
        @Override
        public void onTick(long millisUntilFinished) {
            if(warningNumberInt == 0){
                warningNumber.setVisibility(View.GONE);
                warningNumberInt = 1;
            }else{
                warningNumber.setVisibility(View.VISIBLE);
                warningNumberInt = 0;
            }
        }

        @Override
        public void onFinish() {
            warningNumber.setVisibility(View.VISIBLE);
            warningNumberInt = 0;
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comprehensive_controller;
    }

    @Override
    protected void initData() {
        AppManger.getInstance().killActivity(ComprehensivePersonnelActivity.class);
        AppManger.getInstance().killActivity(NavigationAvtivity.class);
        AppManger.getInstance().killActivity(MapActivity.class);
        AppManger.getInstance().killActivity(ResidentActivity.class);
        AppManger.getInstance().killActivity(ExhibitionActivity.class);
        AppManger.getInstance().killActivity(SpongeActivity.class);
        AppManger.getInstance().killActivity(ConfigureActivity.class);

        startComprehensiveControllerActivity = 1;

        doHttp(RetrofitUtils.createApi(HttpInterface.class).personNum(),1);
        doHttp(RetrofitUtils.createApi(HttpInterface.class).flowrate(),2);

        initPieChart();
        SimulatedWarning();
        slideOut();

    }

    @Override
    protected void requestData() {

    }

    public void initRv(List<AutoPollControllerPojo> autoPollControllerPojos){
        AutoPollControllerAdapter adapter = new AutoPollControllerAdapter(autoPollControllerPojos);
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
    }

    public List<AutoPollControllerPojo> setData(){
        autoPollPojos = new ArrayList<>();
        AutoPollControllerPojo autoPollPojo1 = new AutoPollControllerPojo();
        autoPollPojo1.setColor(0);
        autoPollPojo1.setName("N1馆");
        autoPollPojo1.setInfo("15202人");

        AutoPollControllerPojo autoPollPojo2 = new AutoPollControllerPojo();
        autoPollPojo2.setColor(1);
        autoPollPojo2.setName("N2馆");
        autoPollPojo2.setInfo("14201人");

        AutoPollControllerPojo autoPollPojo3 = new AutoPollControllerPojo();
        autoPollPojo3.setColor(2);
        autoPollPojo3.setName("N3馆");
        autoPollPojo3.setInfo("5236人");

        AutoPollControllerPojo autoPollPojo4 = new AutoPollControllerPojo();
        autoPollPojo4.setColor(3);
        autoPollPojo4.setName("N4馆");
        autoPollPojo4.setInfo("12369人");

        AutoPollControllerPojo autoPollPojo5 = new AutoPollControllerPojo();
        autoPollPojo5.setColor(4);
        autoPollPojo5.setName("N5馆");
        autoPollPojo5.setInfo("9536人");

        AutoPollControllerPojo autoPollPojo6 = new AutoPollControllerPojo();
        autoPollPojo6.setColor(5);
        autoPollPojo6.setName("N6馆");
        autoPollPojo6.setInfo("14203人");

        AutoPollControllerPojo autoPollPojo7 = new AutoPollControllerPojo();
        autoPollPojo7.setColor(6);
        autoPollPojo7.setName("N7馆");
        autoPollPojo7.setInfo("10385人");

        AutoPollControllerPojo autoPollPojo8 = new AutoPollControllerPojo();
        autoPollPojo8.setColor(7);
        autoPollPojo8.setName("N8馆");
        autoPollPojo8.setInfo("8236人");

        AutoPollControllerPojo autoPollPojo9 = new AutoPollControllerPojo();
        autoPollPojo9.setColor(8);
        autoPollPojo9.setName("S1馆");
        autoPollPojo9.setInfo("24162人");

        AutoPollControllerPojo autoPollPojo10 = new AutoPollControllerPojo();
        autoPollPojo10.setColor(9);
        autoPollPojo10.setName("S2馆");
        autoPollPojo10.setInfo("11056人");

        AutoPollControllerPojo autoPollPojo11 = new AutoPollControllerPojo();
        autoPollPojo11.setColor(10);
        autoPollPojo11.setName("S3馆");
        autoPollPojo11.setInfo("15202人");

        AutoPollControllerPojo autoPollPojo12 = new AutoPollControllerPojo();
        autoPollPojo12.setColor(11);
        autoPollPojo12.setName("S4馆");
        autoPollPojo12.setInfo("9520人");

        AutoPollControllerPojo autoPollPojo13 = new AutoPollControllerPojo();
        autoPollPojo13.setColor(12);
        autoPollPojo13.setName("S5馆");
        autoPollPojo13.setInfo("10236人");

        AutoPollControllerPojo autoPollPojo14 = new AutoPollControllerPojo();
        autoPollPojo14.setColor(13);
        autoPollPojo14.setName("S6馆");
        autoPollPojo14.setInfo("13058人");

        AutoPollControllerPojo autoPollPojo15 = new AutoPollControllerPojo();
        autoPollPojo15.setColor(14);
        autoPollPojo15.setName("S7馆");
        autoPollPojo15.setInfo("18302人");

        AutoPollControllerPojo autoPollPojo16 = new AutoPollControllerPojo();
        autoPollPojo16.setColor(15);
        autoPollPojo16.setName("S8馆");
        autoPollPojo16.setInfo("9503人");

        autoPollPojos.add(autoPollPojo1);
        autoPollPojos.add(autoPollPojo2);
        autoPollPojos.add(autoPollPojo3);
        autoPollPojos.add(autoPollPojo4);
        autoPollPojos.add(autoPollPojo5);
        autoPollPojos.add(autoPollPojo6);
        autoPollPojos.add(autoPollPojo7);
        autoPollPojos.add(autoPollPojo8);
        autoPollPojos.add(autoPollPojo9);
        autoPollPojos.add(autoPollPojo10);
        autoPollPojos.add(autoPollPojo11);
        autoPollPojos.add(autoPollPojo12);
        autoPollPojos.add(autoPollPojo13);
        autoPollPojos.add(autoPollPojo14);
        autoPollPojos.add(autoPollPojo15);
        autoPollPojos.add(autoPollPojo16);

        return autoPollPojos;
    }

    public void slideOut(){
        leftLin.setVisibility(View.VISIBLE);
        leftReAnimation = AnimationUtils.loadAnimation(ComprehensiveControllerActivity.this, R.anim.out_to_left);
        leftReAnimation.setFillAfter(true);//android动画结束后停在结束位置
        leftLin.startAnimation(leftReAnimation);

        rightLin.setVisibility(View.VISIBLE);
        rightAnimation = AnimationUtils.loadAnimation(ComprehensiveControllerActivity.this, R.anim.out_to_right);
        rightAnimation.setFillAfter(true);//android动画结束后停在结束位置
        rightLin.startAnimation(rightAnimation);

        reTop.setVisibility(View.VISIBLE);
        topAnimation = AnimationUtils.loadAnimation(ComprehensiveControllerActivity.this, R.anim.out_to_top);
        topAnimation.setFillAfter(true);//android动画结束后停在结束位置
        reTop.startAnimation(topAnimation);
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

    /**
     * 模拟预警
     */
    public void SimulatedWarning(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownTimer.start();
                ComprehensiveControllerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isWaring = 1;
                        warningNumber.setText("1");
                    }
                });
            }
        }.start();
    }

    @Override
    public void onSuccess(String result, Call<ResponseBody> call, Response<ResponseBody> response, int what) {
        super.onSuccess(result, call, response, what);
        switch (what){
            case 1:
                personNums = AppJsonUtil.getArrayList(result,PersonNum.class);
                for(int i = 0; i < personNums.size(); i++){
                    if(personNums.get(i).getName().equals("参观总人数")){
                        scrollNumberl1.setNumber(Integer.parseInt(personNums.get(i).getNumber()));
                    }
                    if(personNums.get(i).getName().equals("参展商")){
                        scrollNumberl2.setNumber(Integer.parseInt(personNums.get(i).getNumber()));
                    }
                    if(personNums.get(i).getName().equals("今日参观人数")){
                        scrollNumber.setNumber(Integer.parseInt(personNums.get(i).getNumber()));
                    }
                }
                break;
            case 2:
                flowrates = AppJsonUtil.getArrayList(result,Flowrate.class);
                autoPollPojos = new ArrayList<>();
                AutoPollControllerPojo autoPollPojo = null;
                for(int i = 0; i < flowrates.size(); i++){
                    autoPollPojo = new AutoPollControllerPojo();
                    autoPollPojo.setColor(i);
                    autoPollPojo.setName(flowrates.get(i).getTitle());
                    autoPollPojo.setInfo(flowrates.get(i).getInfo());
                    autoPollPojos.add(autoPollPojo);
                }
                initRv(autoPollPojos);
                break;
        }
    }

    @Override
    public void onFailure(String result, Call<ResponseBody> call, Response<ResponseBody> response, int what) {
        super.onFailure(result, call, response, what);
        scrollNumber.setNumber(21359);
        scrollNumberl1.setNumber(125930);
        scrollNumberl2.setNumber(4526);
        initRv(setData());
    }

    @Override
    public void onError(Call<ResponseBody> call, Throwable t, int what) {
        super.onError(call, t, what);
        scrollNumber.setNumber(21359);
        scrollNumberl1.setNumber(125930);
        scrollNumberl2.setNumber(4526);
        initRv(setData());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(VoiceServiceEvent voiceServiceEvent){
        switch (voiceServiceEvent.getIdStr()){
            case CONTROLLER_QUERY_WARNING:
                startActivity(MapActivity.class,null);
                break;
        }
    }

    /*@OnClick({R.id.back_re,R.id.location_re,R.id.warning_re})
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
            case R.id.warning_re:
                break;
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startComprehensiveControllerActivity = 0;
    }

}
