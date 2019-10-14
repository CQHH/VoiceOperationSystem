package com.cq.hhxk.voice.voiceoperationsystem.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.cq.hhxk.voice.voiceoperationsystem.application.MyApplication;
import com.cq.hhxk.voice.voiceoperationsystem.constant.VoiceServiceConstant;
import com.cq.hhxk.voice.voiceoperationsystem.event.VoiceServiceEvent;
import com.cq.hhxk.voice.voiceoperationsystem.listener.VoiceRecogListner;
import com.cq.hhxk.voice.voiceoperationsystem.listener.WakeupListner;
import com.cq.hhxk.voice.voiceoperationsystem.service.constant.WordTypeConstant;
import com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar;
import com.cq.hhxk.voice.voiceoperationsystem.service.word.WordsQuery;
import com.cq.hhxk.voice.voiceoperationsystem.ui.MainActivity;
import com.cq.hhxk.voice.voiceoperationsystem.ui.MapActivity;
import com.cq.hhxk.voice.voiceoperationsystem.ui.NullActivity;
import com.cq.hhxk.voice.voiceoperationsystem.util.FeedbackUtil;
import com.cq.hhxk.voice.voiceoperationsystem.util.command.CommandUtil;
import com.cq.hhxk.voice.voiceoperationsystem.util.navi.Navigation;
import com.cq.hhxk.voice.voiceoperationsystem.util.recog.VoiceRecogUtil;
import com.cq.hhxk.voice.voiceoperationsystem.util.synthesizer.VoiceSyntherizerUtil;
import com.cq.hhxk.voice.voiceoperationsystem.util.wakeup.WakeupUtil;
import com.em.baseframe.util.AppJsonUtil;
import com.em.baseframe.util.AppManger;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.*;
import static com.cq.hhxk.voice.voiceoperationsystem.service.var.VoiceServiceStaticVar.bufferStr;

/**
 * @title  语音识别后台service
 * @date   2019/07/31
 * @author enmaoFu
 */
public class VoiceService extends Service implements VoiceServiceConstant, WordTypeConstant {

    /**
     * 播报完成，type为1时的what值
     */
    private static final int SPEECH_FINISH_TYPE_1 = 1;

    /**
     * 唤醒
     */
    private WakeupUtil wakeupUtil;

    /**
     * 语音识别
     */
    private VoiceRecogUtil voiceRecogUtil;

    /**
     * 语音合成
     */
    private VoiceSyntherizerUtil voiceSyntherizerUtil;

    /**
     * ActivityManager
     */
    private ActivityManager mAm;

    /**
     * ActivityManager得到的当前app id
     */
    private int rtiId = -1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SPEECH_FINISH_TYPE_1:
                    startRecognitionAnimation(true);
                    break;
            }
        }
    };

    /**
     * 用于和activity绑定通信
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 首次创建
     */
    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        //唤醒
        if(wakeupUtil == null){
            wakeupUtil = new WakeupUtil();
            wakeupUtil.initWakeup(this);
            getWaleupData();
        }

        //识别
        if(voiceRecogUtil == null){
            voiceRecogUtil = new VoiceRecogUtil();
            voiceRecogUtil.initRecog(this);
            getRecogData();
        }

        //合成
        if(voiceSyntherizerUtil == null){
            voiceSyntherizerUtil = new VoiceSyntherizerUtil();
            voiceSyntherizerUtil.initialTts(this);
        }

    }

    /**
     * 创建后每次启动
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //开始唤醒监听
        wakeupUtil.startWakeup();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 销毁
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消语音识别
        voiceRecogUtil.getAsr().send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        if (voiceRecogUtil.getEnableOffline()) {
            voiceRecogUtil.unloadOfflineEngine();
        }
        voiceRecogUtil.unregisterListener();
        //停止唤醒监听
        wakeupUtil.stopWakeup();
        wakeupUtil.unregisterWakeupListener();
        //释放语音合成资源
        voiceSyntherizerUtil.release();
        bufferStr = "";
        EventBus.getDefault().unregister(this);
    }

    /**
     * 唤醒回调
     */
    private void getWaleupData(){
        wakeupUtil.setWakeupListner(new WakeupListner() {
            @Override
            public void OnListener(String name, String params, byte[] data, int offset, int length) {
                Logger.v("000000");
                if (SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS.equals(name)) {
                    //识别唤醒词成功
                    Logger.v("111111");
                    type = 1;
                    speak(FeedbackUtil.getFeedbackText(4),true,true);
                } else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_ERROR.equals(name)) {
                    //识别唤醒词报错
                    Logger.v("222222");
                } else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_STOPED.equals(name)) {
                    //关闭唤醒词
                    Logger.v("333333");
                }
            }
        });
    }

    /**
     * 语音识别回调
     */
    public void getRecogData(){
        voiceRecogUtil.setVoiceRecogListner(new VoiceRecogListner() {
            @Override
            public void OnListener(String name, String params, byte[] data, int offset, int length) {

                String logTxt = "";

                if (params != null && !params.isEmpty()) {
                    logTxt += params;
                }

                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                    if (params != null && params.contains("\"nlu_result\"")) {
                        if (length > 0 && data.length > 0) {
                            logTxt += ", 语义解析结果：" + new String(data, offset, length);
                        }
                    }
                } else if (data != null) {
                    logTxt += " ;data length=" + data.length;
                }

                getData(logTxt);

                if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)){
                    voiceRecogUtil.stop();
                    isRecogInt = 0;
                    VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                    voiceServiceEvent.setIdStr(STOP_RECOG_ANIMATION);
                    EventBus.getDefault().post(voiceServiceEvent);
                    Log.v("print","识别结束...");
                    if(!bufferStr.equals("") && bufferStr != null && !bufferStr.equals("null")){
                        switch (MyApplication.count){
                            case 0:
                                Log.v("print","识别结束0000000000");
                                Logger.v(">>>>>>>>>>>>>>>>>>>后台 service");
                                invisible(bufferStr);
                                break;
                            case 1:
                                Log.v("print","识别结束1111111111");
                                Logger.v(">>>>>>>>>>>>>>>>>>>前台 service");
                                visible(bufferStr);
                                break;
                        }
                        bufferStr = "";
                    }else{
                        Logger.v("语音识别错误...");
                        isRecogInt = 0;
                        VoiceServiceEvent voiceServiceEventError = new VoiceServiceEvent();
                        voiceServiceEventError.setIdStr(VOICE_RECOG_ERROR);
                        EventBus.getDefault().post(voiceServiceEventError);
                    }
                }

            }
        });
    }

    /**
     * App处于后台时关键词判别
     */
    public void invisible(String text){
        if(WordsQuery.query(bufferStr,WORD_LEADER_LIST)){
            jurisdiction = 1;
            type = 1;
            speak("身份识别成功，需要我做什么？",true,true);
        }else if(WordsQuery.query(bufferStr,WORD_OPERATOR_LIST)){
            jurisdiction = 2;
            type = 1;
            speak("身份识别成功，需要我做什么？",true,true);
        }else if(WordsQuery.query(bufferStr,WORD_SPONGE_LIST)){
            jurisdiction = 3;
            type = 1;
            speak("身份识别成功，需要我做什么？",true,true);
        }else if(WordsQuery.query(bufferStr,WORD_EXHIBITION_LIST)){
            jurisdiction = 4;
            type = 1;
            speak("身份识别成功，需要我做什么？",true,true);
        }else if(WordsQuery.query(bufferStr,WORD_CONFIGURE_LIST)){
            //TODO 打开开发者页面
            if(returnMain() == true && rtiId != -1){
                mAm.moveTaskToFront(rtiId, 0);
                isStartConfigure = 1;
            }else{
                type = 3;
                speak("已打开开发者页面。",false,true);
                isStartConfigure = 1;
                Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(resultIntent);
            }
        }else if(WordsQuery.query(text,WORD_CONTROLLER_FORM_LIST)){
            //TODO 管理者打开综合报表
            if(jurisdiction == 1){
                if(returnMain() == true && rtiId != -1){
                    if(startComprehensiveControllerActivity == 1){
                        mAm.moveTaskToFront(rtiId, 0);
                    }else{
                        isStartComprehensiveControllerActivity = 1;
                        //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
                        Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(resultIntent);
                    }
                }else{
                    isStartComprehensiveControllerActivity = 1;
                    Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(resultIntent);
                }
            }else{
                type = 1;
                speak("您没有这个权限，请说出身份识别口令。",true,true);
            }
        }else if(WordsQuery.query(text,WORD_PERSONNEL_FORM_LIST)){
            //TODO 工作人员打开综合报表
            if(jurisdiction == 2){
                if(returnMain() == true && rtiId != -1){
                    if(startComprehensivePersonnelActivity == 1){
                        mAm.moveTaskToFront(rtiId, 0);
                    }else{
                        isStartComprehensivePersonnelActivity = 1;
                        //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
                        Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(resultIntent);
                    }
                }else{
                    isStartComprehensivePersonnelActivity = 1;
                    Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(resultIntent);
                }
            }else{
                type = 1;
                speak("您没有这个权限，请说出身份识别口令。",true,true);
            }
        }else if(WordsQuery.query(text,WORD_SPONGE_FORM_LIST)){
            //TODO 工作人员打开海绵报表
            if(jurisdiction == 3){
                if(returnMain() == true && rtiId != -1){
                    if(startSpongeActivity == 1){
                        mAm.moveTaskToFront(rtiId, 0);
                    }else{
                        isStartSpongeActivity = 1;
                        //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
                        Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(resultIntent);
                    }
                }else{
                    isStartSpongeActivity = 1;
                    Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(resultIntent);
                }
            }else{
                type = 1;
                speak("您没有这个权限，请说出身份识别口令。",true,true);
            }
        }else if(WordsQuery.query(text,WORD_EXHIBITION_FORM_LIST)){
            //TODO 工作人员打开会展报表
            if(jurisdiction == 4){
                if(returnMain() == true && rtiId != -1){
                    if(startExhibitionActivity == 1){
                        mAm.moveTaskToFront(rtiId, 0);
                    }else{
                        isStartExhibitionActivity = 1;
                        //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
                        Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(resultIntent);
                    }
                }else{
                    isStartExhibitionActivity = 1;
                    Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(resultIntent);
                }
            }else{
                type = 1;
                speak("您没有这个权限，请说出身份识别口令。",true,true);
            }
        }else if(WordsQuery.query(text,WORD_VISITOR_NAVI_LIST)){
            //TODO 访客直接导航
            if(returnMain() == true && rtiId != -1){
                mAm.moveTaskToFront(rtiId, 0);
                isStartNaviVisitor = 1;
            }else{
                type = 2;
                speak("导航启动中。",true,true);
                isStartNaviVisitor = 1;
                Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(resultIntent);
            }
        }else if(WordsQuery.query(text,WORD_RESIDENT_TRIP_LIST)){
            //TODO 居民进入公交出行页面
            if(returnMain() == true && rtiId != -1){
                mAm.moveTaskToFront(rtiId, 0);
                isStartResident = 1;
            }else{
                type = 3;
                speak("已为您查询到当前位置公交信息。",false,true);
                isStartResident = 1;
                Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(resultIntent);
            }
        }else if(WordsQuery.query(text,WORD_VISITOR_OPEN_LIST)){
            //TODO 打开APP
            String getAppNameStr = CommandUtil.getInstance().openAppFilter(this,text);
            type = 3;
            speak("正在打开" + getAppNameStr + "。",false,true);
            Bundle bundle = new Bundle();
            bundle.putString("str","open");
            bundle.putString("bufferStr",text);
            Intent intent = new Intent(VoiceService.this, NullActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            intent.putExtra("bundle",bundle);
            startActivity(intent);
        }else if(WordsQuery.query(text,WORD_RETURN_LIST)){
            //TODO 返回系统
            AppManger.getInstance().killAllActivity();
            Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(resultIntent);
            type = 3;
            speak("正在返回系统。",false,true);

            /**
             * 以下为场景二级串联
             */
        }else if (WordsQuery.query(text,WORD_CONTROLLER_WARNING_LIST)){
            //TODO 管理者查看预警详情
            switch (startComprehensiveControllerActivity){
                case 0:
                    type = 2;
                    speak("您没有这个权限",true,true);
                    break;
                case 1:
                    switch (isWaring){
                        case 0:
                            speak("现在没有预警信息",false,true);
                            break;
                        case 1:
                            VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                            voiceServiceEvent.setIdStr(CONTROLLER_QUERY_WARNING);
                            EventBus.getDefault().post(voiceServiceEvent);
                            break;
                    }
                    break;
            }
        }else if(WordsQuery.query(text,WORD_CONTROLLER_WARNING_DETAILS_LIST)){
            //TODO 管理者通过预警详情查看设备情况
            switch (startMapActivity){
                case 0:
                    type = 2;
                    speak("您没有这个权限",true,true);
                    break;
                case 1:
                    VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                    voiceServiceEvent.setIdStr(SHOW_WARING_DETAILS);
                    EventBus.getDefault().post(voiceServiceEvent);
                    break;
            }
        }else if(WordsQuery.query(text,WORD_PERSONNEL_PATROL_LIST)){
            //TODO 工作人员在综合报表页面发起巡检导航
            switch (startComprehensivePersonnelActivity){
                case 0:
                    type = 2;
                    speak("您没有这个权限",true,true);
                    break;
                case 1:
                    type = 3;
                    speak("已为您规划三条巡查路线。",false,true);
                    VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                    voiceServiceEvent.setIdStr(START_NAVI_PERSONNEL);
                    EventBus.getDefault().post(voiceServiceEvent);
                    break;
            }
        }else if(WordsQuery.query(text,WORD_RESIDENT_TAXI_LIST)){
            //TODO 居民在在公交出行页面联动打车
            type = 2;
            speak("正在启动曹操专车",false,true);
            Bundle bundle = new Bundle();
            bundle.putString("str","open");
            bundle.putString("bufferStr","打开曹操出行");
            Intent intent = new Intent(VoiceService.this, NullActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            intent.putExtra("bundle",bundle);
            startActivity(intent);
        }else{
            type = 2;
            speak(FeedbackUtil.getFeedbackText(1),false,true);
        }
        rtiId = -1;
    }

    /**
     * App处于前台时关键词判别
     */
    public void visible(String text){
        if(WordsQuery.query(bufferStr,WORD_LEADER_LIST)){
            jurisdiction = 1;
            type = 1;
            speak("身份识别成功，需要我做什么？",true,true);
        }else if(WordsQuery.query(bufferStr,WORD_OPERATOR_LIST)){
            jurisdiction = 2;
            type = 1;
            speak("身份识别成功，需要我做什么？",true,true);
        }else if(WordsQuery.query(bufferStr,WORD_SPONGE_LIST)){
            jurisdiction = 3;
            type = 1;
            speak("身份识别成功，需要我做什么？",true,true);
        }else if(WordsQuery.query(bufferStr,WORD_EXHIBITION_LIST)){
            jurisdiction = 4;
            type = 1;
            speak("身份识别成功，需要我做什么？",true,true);
        }else if(WordsQuery.query(bufferStr,WORD_CONFIGURE_LIST)){
            //TODO 打开开发者页面
            switch (startConfigure){
                case 0:
                    VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                    voiceServiceEvent.setIdStr(START_CONFIGURE_ACTIVITY);
                    EventBus.getDefault().post(voiceServiceEvent);
                    type = 3;
                    speak("已打开开发者页面。",false,true);
                    break;
                case 1:
                    speak("已在当前页面。",false,true);
                    break;
            }
        }else if(WordsQuery.query(text,WORD_CONTROLLER_FORM_LIST)){
            //TODO 管理者打开综合报表
            if(jurisdiction == 1){
                switch (startComprehensiveControllerActivity){
                    case 0:
                        VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                        voiceServiceEvent.setIdStr(START_COMPREHENSIVE_CONTROLLER_ACTIVITY);
                        EventBus.getDefault().post(voiceServiceEvent);
                        type = 3;
                        speak("报表查询调度中",false,true);
                        break;
                    case 1:
                        speak("已在当前页面",false,true);
                        break;
                }
            }else{
                type = 1;
                speak("您没有这个权限，请说出身份识别口令。",true,true);
            }
        }else if(WordsQuery.query(text,WORD_PERSONNEL_FORM_LIST)){
            //TODO 工作人员打开城管报表
            if(jurisdiction == 2){
                switch (startComprehensivePersonnelActivity){
                    case 0:
                        VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                        voiceServiceEvent.setIdStr(START_COMPREHENSIVE_PERSONNEL_ACTIVITY);
                        EventBus.getDefault().post(voiceServiceEvent);
                        type = 3;
                        speak("报表查询调度中",false,true);
                        break;
                    case 1:
                        speak("已在当前页面",false,true);
                        break;
                }
            }else{
                type = 1;
                speak("您没有这个权限，请说出身份识别口令。",true,true);
            }
        }else if(WordsQuery.query(text,WORD_SPONGE_FORM_LIST)){
            //TODO 工作人员打开海绵报表
            if(jurisdiction == 3){
                switch (startSpongeActivity){
                    case 0:
                        VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                        voiceServiceEvent.setIdStr(START_SPONGE_ACTIVITY);
                        EventBus.getDefault().post(voiceServiceEvent);
                        type = 3;
                        speak("报表查询调度中",false,true);
                        break;
                    case 1:
                        speak("已在当前页面",false,true);
                        break;
                }
            }else{
                type = 1;
                speak("您没有这个权限，请说出身份识别口令。",true,true);
            }
        }else if(WordsQuery.query(text,WORD_EXHIBITION_FORM_LIST)){
            //TODO 工作人员打开会展报表
            if(jurisdiction == 4){
                switch (startExhibitionActivity){
                    case 0:
                        VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                        voiceServiceEvent.setIdStr(START_EXHIBITION_ACTIVITY);
                        EventBus.getDefault().post(voiceServiceEvent);
                        type = 3;
                        speak("报表查询调度中",false,true);
                        break;
                    case 1:
                        speak("已在当前页面",false,true);
                        break;
                }
            }else{
                type = 1;
                speak("您没有这个权限，请说出身份识别口令。",true,true);
            }
        }else if(WordsQuery.query(text,WORD_VISITOR_NAVI_LIST)){
            //TODO 访客直接导航
            type = 2;
            speak("导航启动中。",true,true);
            VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
            voiceServiceEvent.setIdStr(START_NAVI_VISITOR);
            EventBus.getDefault().post(voiceServiceEvent);
        }else if(WordsQuery.query(text,WORD_RESIDENT_TRIP_LIST)){
            //TODO 居民进入公交出行页面
            switch (startResident){
                case 0:
                    VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                    voiceServiceEvent.setIdStr(START_RESIDENT_ACTIVITY);
                    EventBus.getDefault().post(voiceServiceEvent);
                    type = 3;
                    speak("已为您查询到当前位置公交信息。",false,true);
                    break;
                case 1:
                    speak("已在当前页面",false,true);
                    break;
            }
        }else if(WordsQuery.query(text,WORD_VISITOR_OPEN_LIST)){
            //TODO 打开APP
            String getAppNameStr = CommandUtil.getInstance().openAppFilter(this,text);
            type = 3;
            speak("正在打开" + getAppNameStr + "。",false,true);
            openApp(text);
        }else if(WordsQuery.query(text,WORD_RETURN_LIST)){
            //TODO 返回系统
            AppManger.getInstance().killAllActivity();
            Intent resultIntent = new Intent(VoiceService.this, MainActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(resultIntent);
            type = 3;
            speak("正在返回系统。",false,true);

            /**
             * 以下为场景二级串联
             */
        }else if (WordsQuery.query(text,WORD_CONTROLLER_WARNING_LIST)){
            //TODO 管理者查看预警详情
            switch (startComprehensiveControllerActivity){
                case 0:
                    type = 2;
                    speak("您没有这个权限",true,true);
                    break;
                case 1:
                    switch (isWaring){
                        case 0:
                            speak("现在没有预警信息",false,true);
                            break;
                        case 1:
                            VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                            voiceServiceEvent.setIdStr(CONTROLLER_QUERY_WARNING);
                            EventBus.getDefault().post(voiceServiceEvent);
                            break;
                    }
                    break;
            }
        }else if(WordsQuery.query(text,WORD_CONTROLLER_WARNING_DETAILS_LIST)){
            //TODO 管理者通过预警详情查看设备情况
            switch (startMapActivity){
                case 0:
                    type = 2;
                    speak("您没有这个权限",true,true);
                    break;
                case 1:
                    VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                    voiceServiceEvent.setIdStr(SHOW_WARING_DETAILS);
                    EventBus.getDefault().post(voiceServiceEvent);
                    break;
            }
        }else if(WordsQuery.query(text,WORD_PERSONNEL_PATROL_LIST)){
            //TODO 工作人员在综合报表页面发起巡检导航
            switch (startComprehensivePersonnelActivity){
                case 0:
                    type = 2;
                    speak("您没有这个权限",true,true);
                    break;
                case 1:
                    type = 2;
                    speak("已为您规划三条巡查路线，请选择。",true,true);
                    VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                    voiceServiceEvent.setIdStr(START_NAVI_PERSONNEL);
                    EventBus.getDefault().post(voiceServiceEvent);
                    break;
            }
        }else if(WordsQuery.query(text,WORD_RESIDENT_TAXI_LIST)){
            //TODO 居民在在公交出行页面联动打车
            String getAppPackageStr;
            switch (startResident){
                case 0:
                    type = 3;
                    speak("正在启动曹操专车。",false,true);
                    getAppPackageStr = CommandUtil.getInstance().getAppPackage(getApplication(),"曹操出行");
                    CommandUtil.getInstance().openApp(getApplication(),getAppPackageStr);
                    break;
                case 1:
                    type = 3;
                    speak("正在启动曹操专车。",false,true);
                    getAppPackageStr = CommandUtil.getInstance().getAppPackage(getApplication(),"曹操出行");
                    CommandUtil.getInstance().openApp(getApplication(),getAppPackageStr);
                    break;
            }
        }else{
            type = 2;
            speak(FeedbackUtil.getFeedbackText(1),true,true);
        }
    }

    /**
     * 得到数据
     * @param text
     * @return
     */
    private void getData(String text) {
        text += "\n";
        Log.v("print","------" + text);
        String getString = AppJsonUtil.getString(text,"best_result");
        if(!"".equals(getString) && !"null".equals(getString) && null != getString){
            bufferStr = getString;
        }
    }

    /**
     * 开始语音识别
     * @param isAnimation 是否开启动画
     */
    public void startRecognitionAnimation(boolean isAnimation){
        isRecogInt = 1;
        if(isAnimation == true){
            VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
            voiceServiceEvent.setIdStr(START_VOICE);
            EventBus.getDefault().post(voiceServiceEvent);
        }
        voiceRecogUtil.start();
    }

    /**
     * 开始合成播报，合成播报回调
     * @param text 需要播报的文本信息
     * @param isAnimation 是否开起动画
     * @param isSpeak 是否开始合成播报
     */
    public void speak(String text, boolean isAnimation, boolean isSpeak){
        if(isAnimation == true){
            VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
            voiceServiceEvent.setIdStr(START_SPEAK);
            EventBus.getDefault().post(voiceServiceEvent);
        }
        if(isSpeak == true){
            voiceSyntherizerUtil.startSpeak(text);
        }
        //合成播报回调
        voiceSyntherizerUtil.getSynthesizer().setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String s) {
                //Logger.v("开始合成...");
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
                //Logger.v("合成中，数据到达时...");
            }

            @Override
            public void onSynthesizeFinish(String s) {
                //Logger.v("合成完毕...");
            }

            @Override
            public void onSpeechStart(String s) {
                Logger.v("开始播报...");
                isSpeakInt = 1;
            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {
                //Logger.v("播报中...");
            }

            @Override
            public void onSpeechFinish(String s) {
                Logger.v("播报完毕...");
                switch (type){
                    case 0:
                        break;
                    case 1:
                        VoiceServiceEvent voiceServiceEvent1 = new VoiceServiceEvent();
                        voiceServiceEvent1.setIdStr(SPEAK_TYPE_1_TO_ACTIVITY);
                        EventBus.getDefault().post(voiceServiceEvent1);
                        Message message = new Message();
                        message.what = SPEECH_FINISH_TYPE_1;
                        handler.sendMessage(message);
                        break;
                    case 2:
                        VoiceServiceEvent voiceServiceEvent2 = new VoiceServiceEvent();
                        voiceServiceEvent2.setIdStr(SPEAK_TYPE_2_TO_ACTIVITY);
                        EventBus.getDefault().post(voiceServiceEvent2);
                        break;
                    case 3:
                        break;
                }
                type = 0;
                isSpeakInt = 0;
            }

            @Override
            public void onError(String s, SpeechError speechError) {
                VoiceServiceEvent voiceServiceEventError = new VoiceServiceEvent();
                voiceServiceEventError.setIdStr(SPEAK_ERROR);
                EventBus.getDefault().post(voiceServiceEventError);
                Logger.v("播报错误...");
            }
        });
    }

    /**
     * 打开APP
     */
    public void openApp(String instructionsStr){
        String getAppNameStr = CommandUtil.getInstance().openAppFilter(getApplication(),instructionsStr);
        if(null != getAppNameStr){
            if(!getAppNameStr.equals("")){
                String getAppPackageStr = CommandUtil.getInstance().getAppPackage(getApplication(),getAppNameStr);
                if(null != getAppPackageStr){
                    if(!getAppPackageStr.equals("")){
                        CommandUtil.getInstance().openApp(getApplication(),getAppPackageStr);
                    }else{
                        type = 2;
                        speak(FeedbackUtil.getFeedbackText(2),true,true);
                    }
                }else{
                    type = 2;
                    speak(FeedbackUtil.getFeedbackText(2),true,true);
                }
            }
        }else{
            type = 2;
            speak(FeedbackUtil.getFeedbackText(3),true,true);
        }
    }

    /**
     * 返回操作系统本身
     */
    public boolean returnMain(){
        boolean isExistence = false;
        //获取ActivityManager
        mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (rti.topActivity.getPackageName().equals(getPackageName())) {
                rtiId = rti.id;
                isExistence = true;
            }
        }
        return isExistence;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(VoiceServiceEvent voiceServiceEvent){
        switch (voiceServiceEvent.getIdStr()){
            case CLICK_VOICE:
                startRecognitionAnimation(false);
                break;
            case RECOG_STOP:
                voiceRecogUtil.getAsr().send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
                break;
            case SPEAK_STOP:
                voiceSyntherizerUtil.stopSpeak();
                break;
            case NULL_ACT_NO_APP:
                type = 3;
                speak(FeedbackUtil.getFeedbackText(2),true,true);
                break;
        }
    }
}
