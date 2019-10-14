package com.cq.hhxk.voice.voiceoperationsystem.util.wakeup;

import android.content.Context;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.cq.hhxk.voice.voiceoperationsystem.constant.MainHandlerConstant;
import com.cq.hhxk.voice.voiceoperationsystem.listener.WakeupListner;

import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

/**
 * @title  语音唤醒封装
 * @date   2019/07/12
 * @author enmaoFu
 */
public class WakeupUtil implements EventListener, MainHandlerConstant {

    /**
     * 唤醒的事件管理器
     */
    private EventManager wakeupEventManager;

    /**
     * 获得唤醒结果自定义回调接口
     */
    private WakeupListner wakeupListner;

    public void initWakeup(Context context){
        wakeupEventManager = EventManagerFactory.create(context, "wp");
        wakeupEventManager.registerListener(this);
    }

    /**
     * 回调唤醒结果，供外部
     * @param name
     * @param params
     * @param data
     * @param offset
     * @param length
     */
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        wakeupListner.OnListener(name, params, data, offset, length);
    }

    /**
     * 开始唤醒
     */
    public void startWakeup() {
        Map<String, Object> params = new TreeMap<String, Object>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        params.put(SpeechConstant.APP_ID, APPID);
        params.put(SpeechConstant.APP_KEY, APPKEY);
        params.put(SpeechConstant.SECRET, SECRETKEY);
        String json = null; // 这里可以替换成你需要测试的json
        json = new JSONObject(params).toString();
        wakeupEventManager.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
    }

    /**
     * 停止唤醒
     */
    public void stopWakeup() {
        wakeupEventManager.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
    }

    /**
     * 退出事件管理器
     */
    public void unregisterWakeupListener(){
        wakeupEventManager.unregisterListener(this);
    }

    /**
     * 回调方法，通过此方法回调唤醒结果
     * @param wakeupListner
     */
    public void setWakeupListner(WakeupListner wakeupListner) {
        this.wakeupListner = wakeupListner;
    }

}
