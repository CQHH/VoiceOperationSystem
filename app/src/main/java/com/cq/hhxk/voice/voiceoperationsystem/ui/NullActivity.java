package com.cq.hhxk.voice.voiceoperationsystem.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.cq.hhxk.voice.voiceoperationsystem.constant.VoiceServiceConstant;
import com.cq.hhxk.voice.voiceoperationsystem.event.VoiceServiceEvent;
import com.cq.hhxk.voice.voiceoperationsystem.util.command.CommandUtil;
import org.greenrobot.eventbus.EventBus;

public class NullActivity extends Activity implements VoiceServiceConstant {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String str = bundle.getString("str");
        String bufferStr = bundle.getString("bufferStr");

        switch (str){
            case "open":
                String getAppNameStr = CommandUtil.getInstance().openAppFilter(this,bufferStr);
                if(null != getAppNameStr){
                    if(!getAppNameStr.equals("")){
                        String getAppPackageStr = CommandUtil.getInstance().getAppPackage(getApplication(),getAppNameStr);
                        if(null != getAppPackageStr){
                            if(!getAppPackageStr.equals("")){
                                CommandUtil.getInstance().openApp(getApplication(),getAppPackageStr);
                                finish();
                            }else{
                                VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                                voiceServiceEvent.setIdStr(NULL_ACT_NO_APP);
                                EventBus.getDefault().post(voiceServiceEvent);
                                close();
                            }
                        }else{
                            VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                            voiceServiceEvent.setIdStr(NULL_ACT_NO_APP);
                            EventBus.getDefault().post(voiceServiceEvent);
                            close();
                        }
                    }
                }else{
                    VoiceServiceEvent voiceServiceEvent = new VoiceServiceEvent();
                    voiceServiceEvent.setIdStr(NULL_ACT_NO_APP);
                    EventBus.getDefault().post(voiceServiceEvent);
                    close();
                }
                break;
        }
    }

    /**
     * 关闭当前activity并且回到桌面
     */
    public void close(){
        finish();
        Intent intent = new Intent();
        // 为Intent设置Action、Category属性
        intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        startActivity(intent);
    }

}
