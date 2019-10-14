package com.cq.hhxk.voice.voiceoperationsystem.constant;

/**
 * @title  语音服务相关常量
 * @date   2019/07/12
 * @author enmaoFu
 */
public interface VoiceServiceConstant {

    /**
     * 点击按钮语音识别时的总线
     */
    String CLICK_VOICE = "click_voice";

    /**
     * startRecognitionAnimation方法里，开始语音识别打开动画的总线
     */
    String START_VOICE = "start_voice";

    /**
     * getRecogData方法里，结束语音识别和停止动画的总线
     */
    String STOP_RECOG_ANIMATION = "stop_recog_animation";

    /**
     * getRecogData方法里，语音识别错误的总线
     */
    String VOICE_RECOG_ERROR = "voice_recog_error";

    /**
     * onBackPressed方法里，停止语音识别的总线
     */
    String RECOG_STOP = "recog_stop";

    /**
     * speak方法里，打开动画的总线
     */
    String START_SPEAK = "start_speak";

    /**
     * speak方法里，播报错误时的总线
     */
    String SPEAK_ERROR = "speak_error";

    /**
     * speak方法里,播报完毕type为1时，像activity发送的总线
     */
    String SPEAK_TYPE_1_TO_ACTIVITY = "speak_type_1_to_activity";

    /**
     * speak方法里,播报完毕type为2时，像activity发送的总线
     */
    String SPEAK_TYPE_2_TO_ACTIVITY = "speak_type_2_to_activity";

    /**
     * onBackPressed方法里，停止语音合成播报的总线
     */
    String SPEAK_STOP = "speak_stop";

    /**
     * 管理者打开综合报表-总线
     */
    String START_COMPREHENSIVE_CONTROLLER_ACTIVITY = "start_comprehensive_controller_activity";

    /**
     * 管理者查看预警详情-总线
     */
    String CONTROLLER_QUERY_WARNING = "controller_query_warning";

    /**
     * 管理者在预警详情页里查看设备详细信息-总线
     */
    String SHOW_WARING_DETAILS = "show_waring_details";

    /**
     * 工作人员打开综合报表-总线
     */
    String START_COMPREHENSIVE_PERSONNEL_ACTIVITY = "start_comprehensive_personnel_activity";

    /**
     * 工作人员在综合报表里发起巡检-总线
     */
    String START_NAVI_PERSONNEL = "start_navi_personnel";

    /**
     * 工作人员打开海绵报表-总线
     */
    String START_SPONGE_ACTIVITY = "start_sponge_activity";

    /**
     * 工作人员打开会展报表-总线
     */
    String START_EXHIBITION_ACTIVITY = "start_exhibition_activity";

    /**
     * 访客直接导航-总线
     */
    String START_NAVI_VISITOR = "start_navi_visitor";

    /**
     * 居民查看公交信息-总线
     */
    String START_RESIDENT_ACTIVITY = "start_resident_activity";

    /**
     * 打开开发者页面
     */
    String START_CONFIGURE_ACTIVITY = "start_configure_activity";

    /**
     * 从后台打开APP没有APP-总线
     */
    String NULL_ACT_NO_APP = "null_act_no_app";
}
