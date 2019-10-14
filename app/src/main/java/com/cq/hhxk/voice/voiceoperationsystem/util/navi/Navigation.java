package com.cq.hhxk.voice.voiceoperationsystem.util.navi;

import android.content.Context;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;

public class Navigation {

    /**
     * 导航对外控制类
     */
    public static AMapNavi mAMapNavi;

    public static void initNavi(Context context, AMapNaviListener aMapNaviListener){
        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(context);
        //设置模拟导航的行车速度
        mAMapNavi.setEmulatorNaviSpeed(100);
        //添加监听
        mAMapNavi.addAMapNaviListener(aMapNaviListener);
        //设置使用内部语音
        mAMapNavi.setUseInnerVoice(true);
        //设置模拟导航的行车速度
        mAMapNavi.setEmulatorNaviSpeed(75);
    }

}
