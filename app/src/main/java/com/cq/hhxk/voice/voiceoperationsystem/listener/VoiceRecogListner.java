package com.cq.hhxk.voice.voiceoperationsystem.listener;

public interface VoiceRecogListner {

    void OnListener(String name, String params, byte[] data, int offset, int length);

}
