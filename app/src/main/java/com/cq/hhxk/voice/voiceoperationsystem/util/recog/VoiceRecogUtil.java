package com.cq.hhxk.voice.voiceoperationsystem.util.recog;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.cq.hhxk.voice.voiceoperationsystem.constant.MainHandlerConstant;
import com.cq.hhxk.voice.voiceoperationsystem.listener.VoiceRecogListner;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @title  语音识别封装
 * @date   2019/06/20
 * @author enmaoFu
 */
public class VoiceRecogUtil implements EventListener, MainHandlerConstant {

    /**
     * 语音识别的事件管理器
     */
    private EventManager asr;

    /**
     * 测试离线命令词，需要改成true
     */
    protected boolean enableOffline = false;

    /**
     * 获得语音识别结果自定义回调接口
     */
    private VoiceRecogListner voiceRecogListner;

    public void initRecog(Context context){
        //基于sdk集成1.1 初始化EventManager对象
        asr = EventManagerFactory.create(context, "asr");
        //基于sdk集成1.3 注册自己的输出事件类 EventListener 中 onEvent方法
        asr.registerListener(this);
        if (enableOffline) {
            loadOfflineEngine(); // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }

    /**
     * 开始语音识别
     */
    public void start() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START;
        //设置识别参数
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.APP_ID, APPID);
        params.put(SpeechConstant.APP_KEY, APPKEY);
        params.put(SpeechConstant.SECRET, SECRETKEY);
        //params.put(SpeechConstant.NLU, "enable");
        //params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        //params.put(SpeechConstant.IN_FILE, "res:///com/baidu/android/voicedemo/16k_test.pcm");
        //params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        //params.put(SpeechConstant.PID, 1537); // 中文输入法模型，有逗号
        //请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);
        //自动检测错误
        /*(new AutoCheckRecog(Context, new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheckRecog autoCheck = (AutoCheckRecog) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage(); // autoCheck.obtainAllMessage();
                        Log.v("print","错误信息：" + message + "\n");
                    }
                }
            }
        },enableOffline)).checkAsr(params);*/
        //组装识别参数，初始化字符串
        String json = null;
        //组装识别参数，将参数转为json
        json = new JSONObject(params).toString();
        //开始识别
        asr.send(event, json, null, 0, 0);
        Log.v("print","输入参数：" + json);
    }

    /**
     * 停止语音识别
     */
    public void stop() {
        Log.v("print","停止识别...");
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }

    /**
     * enableOffline设为true时，在onCreate中调用
     * 加载离线资源(离线时使用)
     */
    public void loadOfflineEngine() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.DECODER, 2);
        params.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets://baidu_speech_grammar.bsg");
        asr.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, new JSONObject(params).toString(), null, 0, 0);
    }

    /**
     * enableOffline为true时，在onDestory中调用，与loadOfflineEngine对应
     * 卸载离线资源步骤(离线时使用)
     */
    public void unloadOfflineEngine() {
        asr.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0);
    }

    /**
     * 回调语音识别结果，供外部
     * @param name
     * @param params
     * @param data
     * @param offset
     * @param length
     */
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        voiceRecogListner.OnListener(name, params, data, offset, length);
    }

    /**
     * 得到语音识别的事件管理器
     * @return
     */
    public EventManager getAsr(){
        return asr;
    }

    /**
     * 退出事件管理器
     */
    public void unregisterListener(){
        asr.unregisterListener(this);
    }

    /**
     * 得到离线命令词启用状态
     * @return
     */
    public Boolean getEnableOffline(){
        return enableOffline;
    }

    /**
     * 回调方法，通过此方法回调语音识别结果
     * @param voiceRecogListner
     */
    public void setVoiceRecogListner(VoiceRecogListner voiceRecogListner) {
        this.voiceRecogListner = voiceRecogListner;
    }

}
