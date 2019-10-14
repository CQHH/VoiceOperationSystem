package com.cq.hhxk.voice.voiceoperationsystem.util.synthesizer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.cq.hhxk.voice.voiceoperationsystem.constant.MainHandlerConstant;
import com.cq.hhxk.voice.voiceoperationsystem.control.InitConfig;
import com.cq.hhxk.voice.voiceoperationsystem.control.MySyntherizer;
import com.cq.hhxk.voice.voiceoperationsystem.control.NonBlockSyntherizer;
import com.cq.hhxk.voice.voiceoperationsystem.listener.UiMessageListener;
import com.cq.hhxk.voice.voiceoperationsystem.util.AutoCheckSyntherizer;
import com.cq.hhxk.voice.voiceoperationsystem.util.OfflineResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @title  语音合成封装
 * @date   2019/07/12
 * @author enmaoFu
 */
public class VoiceSyntherizerUtil implements MainHandlerConstant {

    /**
     * 主控制类，所有合成控制方法从这个类开始
     */
    private MySyntherizer synthesizer;

    /**
     * TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
     */
    private TtsMode ttsMode = TtsMode.MIX;

    /**
     * 离线发音选择，VOICE_FEMALE即为离线女声发音。
     * assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型
     * assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
     */
    private String offlineVoice = OfflineResource.VOICE_MALE;

    /**
     * 语音合成消息通知机制
     */
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PRINT:
                    Log.v("print",msg + "...");
                    break;
                case UI_CHANGE_SYNTHES_TEXT_SELECTION:
                    //TODO 暂无用
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化语音合成
     */
    public void initialTts(Context context) {
        //日志打印在logcat中
        LoggerProxy.printable(true);
        //设置初始化参数
        //此处可以改为 含有自己业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);
        //获取参数
        Map<String, String> params = getParams(context);
        //设置appId appKey secretKey等
        InitConfig initConfig = new InitConfig(APPID, APPKEY, SECRETKEY, ttsMode, params, listener);
        //错误检测，上线时请删除AutoCheck的调用
        AutoCheckSyntherizer.getInstance(context).check(initConfig, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheckSyntherizer autoCheck = (AutoCheckSyntherizer) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainDebugMessage();
                        //可以用下面一行替代，在logcat中查看代码
                        toPrint(message);
                        // Log.w("AutoCheckMessage", message);
                    }
                }
            }
        });
        synthesizer = new NonBlockSyntherizer(context, initConfig, mainHandler);
    }

    /**
     * 打印错误
     * @param str
     */
    private void toPrint(String str) {
        Message msg = Message.obtain();
        msg.obj = str;
        mainHandler.sendMessage(msg);
    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     * @param context
     * @return
     */
    private Map<String, String> getParams(Context context) {
        Map<String, String> params = new HashMap<String, String>();
        //以下参数均为选填
        //设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        //设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        //设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        //设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");
        //该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        //MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        //MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        //MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        //MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(context,offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;
    }

    /**
     * 创建离线资源
     * @param context
     * @param voiceType
     * @return
     */
    protected OfflineResource createOfflineResource(Context context, String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(context, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
            toPrint("【error】:copy files from assets failed." + e.getMessage());
        }
        return offlineResource;
    }

    /**
     * 开始合成播报
     * @param text
     */
    public void startSpeak(String text){
        int result = synthesizer.speak(text);
        checkResult(result, "speak");
    }

    /**
     * 停止合成播报
     */
    public void stopSpeak(){
        int result = synthesizer.stop();
        checkResult(result, "speak");
    }

    /**
     * 检测结果返回
     * @param result
     * @param method
     */
    private void checkResult(int result, String method) {
        if (result != 0) {
            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

    /**
     * 释放语音合成资源
     */
    public void release(){
        synthesizer.release();
    }

    /**
     * 得到语音合成播放主控制类
     * @return
     */
    public MySyntherizer getSynthesizer(){
        return synthesizer;
    }
}
