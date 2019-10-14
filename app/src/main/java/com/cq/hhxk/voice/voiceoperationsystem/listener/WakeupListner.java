package com.cq.hhxk.voice.voiceoperationsystem.listener;

public interface WakeupListner {

    void OnListener(String name, String params, byte[] data, int offset, int length);

}
