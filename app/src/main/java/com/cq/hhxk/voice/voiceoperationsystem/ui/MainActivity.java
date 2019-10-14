package com.cq.hhxk.voice.voiceoperationsystem.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.base.BaseAty;
import com.cq.hhxk.voice.voiceoperationsystem.constant.VoiceServiceConstant;
import com.cq.hhxk.voice.voiceoperationsystem.event.VoiceServiceEvent;
import com.cq.hhxk.voice.voiceoperationsystem.service.VoiceService;
import com.cq.hhxk.voice.voiceoperationsystem.view.WaveView;
import com.cq.hhxk.voice.voiceoperationsystem.view.WaveViewBySinCos;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import butterknife.BindView;
import butterknife.OnClick;
import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.*;

public class MainActivity extends BaseAty implements VoiceServiceConstant {

    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.wave)
    WaveView waveView;
    @BindView(R.id.speak)
    SimpleDraweeView speakImage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        Intent intent = new Intent(this, VoiceService.class);
        startService(intent);

        initView();
        startGif();


    }

    @Override
    protected void requestData() {

    }

    @OnClick({R.id.speak})
    @Override
    public void btnClick(View view) {
        switch (view.getId()){
            case R.id.speak:
                VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                voiceServiceEvent.setIdStr(CLICK_VOICE);
                EventBus.getDefault().post(voiceServiceEvent);

                speakImage.setVisibility(View.GONE);
                waveView.setVisibility(View.VISIBLE);
                waveView.startAnim();
                break;
            /*case R.id.text3:
                startActivity(ConfigureActivity.class,null);
                break;
            case R.id.text1:
                startActivity(ComprehensiveControllerActivity.class,null);
                break;
            case R.id.text2:
                startActivity(ComprehensivePersonnelActivity.class,null);
                break;

            case R.id.text4:
                startActivity(SpongeActivity.class,null);
                break;
            case R.id.text5:
                startActivity(ExhibitionActivity.class,null);
                break;*/
        }
    }

    public void initView(){

        logo.setAlpha(0f);
        speakImage.setAlpha(0f);

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logo.animate()
                                .alpha(1f)
                                .setDuration(3500)
                                .setListener(null);

                        speakImage.animate()
                                .alpha(1f)
                                .setDuration(3500)
                                .setListener(null);

                    }
                });
            }
        }.start();

    }

    public void startGif(){
        // 图片地址
        Uri uri = Uri.parse("res://com.cq.hhxk.voice.interaction/" + R.drawable.gif1);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        speakImage.setController(controller);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(VoiceServiceEvent voiceServiceEvent){
        Bundle bundle = new Bundle();
        switch (voiceServiceEvent.getIdStr()){
            case START_VOICE:
                speakImage.setVisibility(View.GONE);
                waveView.setVisibility(View.VISIBLE);
                waveView.startAnim();
                break;
            case STOP_RECOG_ANIMATION:
                if(waveView.getIsOpen() == 1){
                    waveView.stopAnim();
                }
                waveView.setVisibility(View.GONE);
                speakImage.setVisibility(View.VISIBLE);
                startGif();
                break;
            case VOICE_RECOG_ERROR:
                if(waveView.getIsOpen() == 1){
                    waveView.stopAnim();
                }
                waveView.setVisibility(View.GONE);
                speakImage.setVisibility(View.VISIBLE);
                startGif();
                break;
            case START_SPEAK:
                speakImage.setVisibility(View.GONE);
                waveView.setVisibility(View.VISIBLE);
                waveView.startAnim();
                break;
            case SPEAK_ERROR:
                showErrorToast("网络发生错误，播报失败，请重新进入。");
                if(waveView.getIsOpen() == 1){
                    waveView.stopAnim();
                }
                waveView.setVisibility(View.GONE);
                speakImage.setVisibility(View.VISIBLE);
                startGif();
                break;
            case SPEAK_TYPE_1_TO_ACTIVITY:
                if(waveView.getIsOpen() == 1){
                    waveView.stopAnim();
                }
                waveView.setVisibility(View.GONE);
                speakImage.setVisibility(View.VISIBLE);
                startGif();
                break;
            case SPEAK_TYPE_2_TO_ACTIVITY:
                if(waveView.getIsOpen() == 1){
                    waveView.stopAnim();
                }
                waveView.setVisibility(View.GONE);
                speakImage.setVisibility(View.VISIBLE);
                startGif();
                break;
            case START_COMPREHENSIVE_CONTROLLER_ACTIVITY:
                startActivity(ComprehensiveControllerActivity.class,null);
                break;
            case START_COMPREHENSIVE_PERSONNEL_ACTIVITY:
                startActivity(ComprehensivePersonnelActivity.class,null);
                break;
            case START_SPONGE_ACTIVITY:
                startActivity(SpongeActivity.class,null);
                break;
            case START_EXHIBITION_ACTIVITY:
                startActivity(ExhibitionActivity.class,null);
                break;
            case START_NAVI_VISITOR:
                startNaviType = 0;
                startActivity(NavigationAvtivity.class,null);
                break;
            case START_RESIDENT_ACTIVITY:
                startActivity(ResidentActivity.class,null);
                break;
            case START_CONFIGURE_ACTIVITY:
                startActivity(ConfigureActivity.class,null);
                break;
        }
    }

    /**
     * 监听后退键，不让退出APP，而是回到默认提示列表页面
     */
    @Override
    public void onBackPressed() {

        if(isSpeakInt == 1){
            VoiceServiceEvent voiceServiceEventStop = new VoiceServiceEvent();
            voiceServiceEventStop.setIdStr(SPEAK_STOP);
            EventBus.getDefault().post(voiceServiceEventStop);
            if(waveView.getIsOpen() == 1){
                waveView.stopAnim();
            }
            waveView.setVisibility(View.GONE);
            speakImage.setVisibility(View.VISIBLE);
            startGif();
        }

        if(isRecogInt == 1){
            VoiceServiceEvent voiceServiceEventStop = new VoiceServiceEvent();
            voiceServiceEventStop.setIdStr(RECOG_STOP);
            EventBus.getDefault().post(voiceServiceEventStop);
            if(waveView.getIsOpen() == 1){
                waveView.stopAnim();
            }
            waveView.setVisibility(View.GONE);
            speakImage.setVisibility(View.VISIBLE);
            startGif();
        }

        isSpeakInt = 0;
        isRecogInt = 0;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Intent intent = new Intent(this, VoiceService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isStartComprehensiveControllerActivity == 1){
            startActivity(ComprehensiveControllerActivity.class,null);
        }

        if(isStartComprehensivePersonnelActivity == 1){
            startActivity(ComprehensivePersonnelActivity.class,null);
        }

        if(isStartNaviVisitor == 1){
            startNaviType = 0;
            startActivity(NavigationAvtivity.class,null);
        }

        if(isStartResident == 1){
            startActivity(ResidentActivity.class,null);
        }

        if(isStartSpongeActivity == 1){
            startActivity(SpongeActivity.class,null);
        }

        if(isStartExhibitionActivity == 1){
            startActivity(ExhibitionActivity.class,null);
        }

        if(isStartConfigure == 1){
            startActivity(ConfigureActivity.class,null);
        }

        isStartComprehensiveControllerActivity = 0;
        isStartComprehensivePersonnelActivity = 0;
        isStartNaviVisitor = 0;
        isStartResident = 0;
        isStartSpongeActivity = 0;
        isStartExhibitionActivity = 0;
        isStartConfigure = 0;
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
