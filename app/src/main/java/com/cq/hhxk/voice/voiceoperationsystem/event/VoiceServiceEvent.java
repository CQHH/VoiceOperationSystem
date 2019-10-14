package com.cq.hhxk.voice.voiceoperationsystem.event;

/**
 * @title  语音服务事件总线实体类
 * @date   2019/07/19
 * @author enmaoFu
 */
public class VoiceServiceEvent {

    private String idStr;

    private String title;

    private String text;

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
