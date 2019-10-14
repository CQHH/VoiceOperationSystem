package com.cq.hhxk.voice.voiceoperationsystem.application;

import android.app.Activity;
import android.os.Bundle;

import com.cq.hhxk.voice.voiceoperationsystem.util.ForegroundCallbacks;
import com.em.baseframe.application.BaseApplication;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;

/**
 * @title  全局类
 * @date   2017/07/19
 * @author enmaoFu
 */
public class MyApplication extends BaseApplication {

    public static int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        MMKV.initialize(this);
        ForegroundCallbacks.init(this);
        ForegroundCallbacks.get().addListener(new ForegroundCallbacks.Listener() {
            @Override
            public void onBecameForeground() {
                count = 1;
                Logger.v(">>>>>>>>>>>>>>>>>>>切到前台  lifecycle");
            }

            @Override
            public void onBecameBackground() {
                count = 0;
                Logger.v(">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");
            }
        });
    }
}
